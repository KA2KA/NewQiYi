package com.newqiyi.activity;

import java.io.Serializable;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.newqiyi.domain.User;
import com.newqiyi.util.GsonUtil;
import com.newqiyi.util.SharePrefUitl;
import com.newqiyi.util.URLUtil;
import com.newqiyi.widget.AlertDialog;

/**
 * @author kaka
 * @E-mail:wuwanggao@163.com
 * @version 创建时间：2015-10-17 下午12:45:29 项目详情
 */

public class ProDetailActivity extends Activity implements OnClickListener {
	private WebView mwebview;
	private Button btn_donate;
	private ProgressBar progressBar;
	private String url = "servlet/ProPostShowServlet";// servlet/ProPostShowServlet
	private String target_count = null;
	private String pre_qibi_count = null;
	private String pro_id = null;
	private Button ib_proCol;
	private User user;
	private String colData;

	private String colurl = "servlet/AndroidInsertCollectionServlet";// 用于收藏的url

	private String deleteUrl = "servlet/AndroidColDeleteServlet";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_pro_detail);
		// 初始化标题栏
		target_count = getIntent().getStringExtra("target_count");
		pre_qibi_count = getIntent().getStringExtra("pre_qibi_count");
		pro_id = getIntent().getStringExtra("pro_id");
		initTitleBar();
		initView();
		initEvent();

	}

	private void initView() {
		mwebview = (WebView) findViewById(R.id.wv_web);
		progressBar = (ProgressBar) findViewById(R.id.pb_progress);
		btn_donate = (Button) findViewById(R.id.btn_donate_detail);
		ib_proCol = (Button) findViewById(R.id.ib_proCol);

	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initEvent() {
		btn_donate.setOnClickListener(this);
		ib_proCol.setOnClickListener(this);

		colData = SharePrefUitl.getStringData(getApplicationContext(), pro_id
				+ "col", "");

		if (!colData.equals("0") && !TextUtils.isEmpty(colData)) {
			ib_proCol.setBackgroundResource(R.drawable.pro_coled);
		} else {
			ib_proCol.setBackgroundResource(R.drawable.pro_col);
		}

		WebSettings settings = mwebview.getSettings();
		settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);// 设置缓存模式
		settings.setJavaScriptEnabled(true); // 表示支持JS
		mwebview.setWebViewClient(new WebViewClient() {

			/**
			 * 网页开始加载
			 */
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				System.out.println("网页开始加载");
				progressBar.setVisibility(View.VISIBLE);
			}

			/**
			 * 网页加载结束
			 */
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				System.out.println("网页开始结束");
				progressBar.setVisibility(View.GONE);
			}

			/**
			 * 所有跳转的链接都会在此方法中回调
			 */
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				System.out.println("跳转url:" + url);
				view.loadUrl(url);
				return true;
			}
		});
		// mwebview.loadUrl("http://m172634791.github.io/learngit/xt.html");
		// http://m172634791.github.io/learngit/xt.html
		mwebview.loadUrl(URLUtil.BASE_URL + url + "?pro_id=" + pro_id);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.imgbtn_left) {
			this.finish();

		} else if (id == R.id.ib_proCol) {
			// String userData = SharePrefUitl.getStringData(getApplication(),
			// "UserData", "").trim();
			// if(TextUtils.isEmpty(userData)){
			//
			// }
			String userData = SharePrefUitl.getStringData(
					getApplicationContext(), "UserData", "");
			if (!TextUtils.isEmpty(userData)) {
				user = GsonUtil.jsonToBean(userData, User.class);

				colData = SharePrefUitl.getStringData(getApplicationContext(),
						pro_id + "col", "");

				if (colData.isEmpty() || colData.equals("0")) {
					insertCollection(); // 插入收藏(1)
				} else {
					cancelCollection();// 取消收藏(0)
				}
			} else {
				showAlertDialog();
			}
		} else if (id == R.id.imgbtn_comment) {
			// 跳转到评论界面

			Toast.makeText(getApplicationContext(), "评论", Toast.LENGTH_SHORT)
					.show();
			String sd = SharePrefUitl.getStringData(ProDetailActivity.this,
					"UserData", "").trim();
			if (!TextUtils.isEmpty(sd)) {

				Intent intent = new Intent(ProDetailActivity.this,
						CommentActivity.class);
				intent.putExtra("pro_id", pro_id);
				startActivity(intent);

			} else {
				showAlertDialog();
			}
		} else if (id == R.id.imgbtn_right) {
			showShare();
		} else if (id == R.id.btn_donate_detail) {

			String userString = SharePrefUitl.getStringData(
					getApplicationContext(), "UserData", "");

			// 跳转到捐赠的界面

			if (!TextUtils.isEmpty(userString)) {
				User user = GsonUtil.jsonToBean(userString, User.class);
				Intent intent = new Intent(ProDetailActivity.this,
						DonateActivity.class);
				intent.putExtra("pro_id", pro_id);
				intent.putExtra("pre_qibi_count", pre_qibi_count);
				intent.putExtra("target_count", target_count);
				intent.putExtra("user_id", user.getUser_id());
				startActivity(intent);
			} else {
				showAlertDialog();
			}
		}
	}

	private void cancelCollection() {

		HttpUtils hUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("user_id", user.getUser_id());
		params.addBodyParameter("pro_id", pro_id);
		hUtils.send(HttpMethod.POST, URLUtil.BASE_URL + deleteUrl, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
					}

					@Override
					public void onSuccess(ResponseInfo<String> info) {
						if (info.result != null) {
							Toast.makeText(getApplicationContext(), "不收藏了~",
									Toast.LENGTH_SHORT).show();
							ib_proCol.setBackgroundResource(R.drawable.pro_col);
							SharePrefUitl.saveStringData(
									getApplicationContext(), pro_id + "col",
									"0");

						} else {
							Toast.makeText(getApplicationContext(), "收藏失败",
									Toast.LENGTH_SHORT).show();
						}
					}
				});

	}

	// 添加收藏
	private void insertCollection() {
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();

		params.addBodyParameter("user_id", user.getUser_id());
		params.addBodyParameter("pro_id", pro_id);
		httpUtils.send(HttpMethod.POST, URLUtil.BASE_URL + colurl, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {

					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						if (responseInfo.result != null) {
							ib_proCol
									.setBackgroundResource(R.drawable.pro_coled);
							SharePrefUitl.saveStringData(
									getApplicationContext(), pro_id + "col",
									"1");
						}

					}
				});

	}

	private void initTitleBar() {
		// 最左边的
		Button button = (Button) findViewById(R.id.btn_left);
		button.setVisibility(View.GONE);
		// 设置返回按钮
		ImageButton imageButton = (ImageButton) findViewById(R.id.imgbtn_left);
		imageButton.setVisibility(View.VISIBLE);
		imageButton.setImageResource(R.drawable.back);
		imageButton.setOnClickListener(this);
		// 设置标题栏
		TextView txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setVisibility(View.VISIBLE);
		txt_title.setText("项目详情");
		// 设置评论键
		ImageButton imgbtn_comment = (ImageButton) findViewById(R.id.imgbtn_comment);
		imgbtn_comment.setVisibility(View.VISIBLE);
		imgbtn_comment.setImageResource(R.drawable.comment);
		imgbtn_comment.setOnClickListener(this);

		// 设置分享键
		ImageButton imgbtn_right = (ImageButton) findViewById(R.id.imgbtn_right);
		imgbtn_right.setVisibility(View.VISIBLE);
		imgbtn_right.setImageResource(R.drawable.icon_share);
		imgbtn_right.setOnClickListener(this);
		// 最右面的按钮
		ImageButton btn_right = (ImageButton) findViewById(R.id.btn_right);
		btn_right.setVisibility(View.GONE);
	}

	// ShareSDk实现分享功能
	public void showShare() {
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();

		oks.setTitle("新启益（标题）");

		// text是分享文本：所有平台都需要这个字段
		oks.setText("公益是你我的一份责任，小伙伴快来启益体验下全新的公益模式吧~~"); // 最多40个字符

		// imagePath是图片的本地路径：除Linked-In以外的平台都支持此参数
		// oks.setImagePath(Environment.getExternalStorageDirectory() +
		// "/meinv.jpg");//确保SDcard下面存在此张图片
		// Platform weibo = ShareSDK.getPlatform(getApplicationContext(),
		// SinaWeibo.NAME);
		// weibo.setPlatformActionListener(paListener);
		// weibo.authorize();
		// 移除授权
		// weibo.removeAccount();
		// 网络图片的url：所有平台
		// oks.setImageUrl("http://7sby7r.com1.z0.glb.clouddn.com/CYSJ_02.jpg");//
		// 网络图片rul

		// url：仅在微信（包括好友和朋友圈）中使用
		oks.setUrl(URLUtil.BASE_URL + url + "?pro_id=" + pro_id); // 网友点进链接后，可以看到分享的详情

		// Url：仅在QQ空间使用
		oks.setTitleUrl(URLUtil.BASE_URL + url + "?pro_id=" + pro_id); // 网友点进链接后，可以看到分享的详情

		// 启动分享GUI
		oks.show(this);
	}

	// 判断登录的dialog
	public void showAlertDialog() {

		new AlertDialog(ProDetailActivity.this).builder().setTitle("提醒")
				.setMsg("登录一下吧~")
				.setPositiveButton("登录", new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(ProDetailActivity.this,
								LoginActivity.class);
						startActivity(intent);
					}
				}).setNegativeButton("取消", new OnClickListener() {
					@Override
					public void onClick(View v) {
					}
				}).show();
	}
}
