package com.newqiyi.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.newqiyi.util.URLUtil;
import com.newqiyi.widget.AlertDialog;
import com.newqiyi.widget.NumberCircleProgressBar;

/**
 * @author ka2ka
 * @E-mail:wuwanggao@163.com
 * @version 创建时间：2015-10-23 上午10:47:29 捐赠信息的处理
 */
@SuppressLint({ "ShowToast", "SdCardPath" })
public class DonateActivity extends Activity implements OnClickListener {

	private String user_id;// 用户id
	private String pro_id;// 项目的id
	private String user_score;// 用户积分
	private String target_count;// 目标总启币数
	private String pre_qibi_count;// 当前的启币数
	private String url = "servlet/AndroidDonateServlet";
	private String urlTwo = "servlet/AndroidDonateQibiServlet";

	private TextView pro_qibi;
	private TextView user_qibi;
	private NumberCircleProgressBar pb_donate;
	private Button im_btn1, im_btn2, im_btn3;
	private Button btn_donate;
	private EditText et_donate;

	int count = 0;// 单次捐赠的变量
	private String TAG = "DonateActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_donate);
		pro_id = getIntent().getStringExtra("pro_id");
		user_id = getIntent().getStringExtra("user_id");
		target_count = getIntent().getStringExtra("target_count");
		pre_qibi_count = getIntent().getStringExtra("pre_qibi_count");
		initTitleBar();
		initView();
		initData();
	}

	private void initView() {
		// 当前的启币数
		user_qibi = (TextView) findViewById(R.id.tv_user_qibi);
		// 已经捐赠的启币数
		pro_qibi = (TextView) findViewById(R.id.tv_pro_qibi);
		// 进度条
		pb_donate = (NumberCircleProgressBar) findViewById(R.id.pb_donate);
		// 图片一
		im_btn1 = (Button) findViewById(R.id.iv_donate1);
		// 图片二
		im_btn2 = (Button) findViewById(R.id.iv_donate2);
		// 图片三
		im_btn3 = (Button) findViewById(R.id.iv_donate3);
		// 读取输入的捐赠启币量
		et_donate = (EditText) findViewById(R.id.et_donate);
		// // 点击捐赠按钮读取数据传入到服务器存储起来
		btn_donate = (Button) findViewById(R.id.btn_donate);

	}

	private void initData() {
		//
		// user_qibi.setText("您剩余的启币数： " + 0 + "币");
		// pro_qibi.setText("您已捐的启币数：" + 0 + "币");
		// 拿到用户当前的user_score和已经捐赠的启币数
		queryQibi();

		// 设置最大进度
		pb_donate.setMax(Integer.parseInt(target_count));
		// 设置当前的进度
		if (Integer.parseInt(target_count) < Integer.parseInt(pre_qibi_count)) {
			pb_donate.setProgress(Integer.parseInt(target_count));
		}
		pb_donate.setProgress(Integer.parseInt(pre_qibi_count));
		im_btn1.setOnClickListener(this);
		im_btn2.setOnClickListener(this);
		im_btn3.setOnClickListener(this);
		// 为EditText设置焦点
		// et_donate.setFocusable(true);
		// et_donate.setFocusableInTouchMode(true);
		// et_donate.requestFocus();

		btn_donate.setOnClickListener(this);
	}

	private void queryQibi() {
		HttpUtils hUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("user_id", user_id);
		Log.i(TAG, user_id);
		params.addBodyParameter("pro_id", pro_id);
		hUtils.send(HttpMethod.POST, URLUtil.BASE_URL + urlTwo, params,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						System.out.println(responseInfo.result);
						String[] split = responseInfo.result.split(":");

						String singlepro_score = split[0];
						user_score = split[1];

						user_qibi.setText("您剩余的启币数： " + user_score + "币");

						pro_qibi.setText("您已捐的启币数：" + singlepro_score + "币");
					}

					@Override
					public void onFailure(HttpException exception, String string) {
						Toast.makeText(getApplication(), "服务器异常", 0).show();
					}

				});

	}

	// 捐赠的启币数
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgbtn_left:
			this.finish();
			break;
		case R.id.imgbtn_right:
			showShare();
			break;
		case R.id.iv_donate1:
			et_donate.setHint(100 + "");
			count = 100;
			im_btn1.setBackgroundResource(R.drawable.doctors);
			im_btn2.setBackgroundResource(R.drawable.food);
			im_btn3.setBackgroundResource(R.drawable.book);

			break;
		case R.id.iv_donate2:
			count = 300;
			im_btn2.setBackgroundResource(R.drawable.foods);
			im_btn1.setBackgroundResource(R.drawable.doctor);
			im_btn3.setBackgroundResource(R.drawable.book);
			et_donate.setHint(300 + "");
			break;
		case R.id.iv_donate3:
			count = 500;
			im_btn1.setBackgroundResource(R.drawable.doctor);
			im_btn2.setBackgroundResource(R.drawable.food);
			im_btn3.setBackgroundResource(R.drawable.books);
			et_donate.setHint(500 + "");

			break;
		case R.id.btn_donate:
			String qibi_count = et_donate.getText().toString();

			if (!TextUtils.isEmpty(qibi_count)) {
				count = Integer.parseInt(qibi_count);
			}
			if (count == 0) {
				Toast.makeText(this, "您的启币数不足", Toast.LENGTH_SHORT).show();
			} else if (TextUtils.isEmpty(user_score)) {
				Toast.makeText(this, "访问服务器异常", Toast.LENGTH_SHORT).show();
			} else if (count > Integer.parseInt(user_score)) {
				Toast.makeText(this, "您的启币数不足", Toast.LENGTH_SHORT).show();
			} else if (count > (Integer.parseInt(target_count) - Integer
					.parseInt(pre_qibi_count))) {
				Toast.makeText(getApplicationContext(), "你太土豪了~,我们以及完成项目!",
						Toast.LENGTH_SHORT).show();
			} else {
				postData();
			}
			break;
		}
	}

	private void postData() {

		RequestParams params = new RequestParams();
		params.addBodyParameter("user_id", user_id);// 用户的id
		params.addBodyParameter("pro_id", pro_id);// 项目的id
		params.addBodyParameter("don_score", count + "");// 用户的启币数

		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.POST, URLUtil.BASE_URL + url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {

						if (!TextUtils.isEmpty(responseInfo.result)) {

							String[] split = responseInfo.result.split(":");
							String singlepro_score = split[0];
							user_score = split[1];
							pre_qibi_count = split[2];

							user_qibi.setText("您剩余的启币数：" + user_score + "币");

							pro_qibi.setText("您已捐的启币数：" + singlepro_score + "币");
							if (!TextUtils.isEmpty(pre_qibi_count)) {
								// 更新进度条
								pb_donate.setProgress(Integer
										.parseInt(pre_qibi_count));
							}
							Toast.makeText(getApplicationContext(),
									"感谢您捐赠了" + count + "启币", Toast.LENGTH_SHORT)
									.show();

						} else {

							Toast.makeText(getApplicationContext(), "捐赠失败",
									Toast.LENGTH_SHORT).show();
						}
					}
				});

	}

	private void initTitleBar() {
		// 设置返回按钮
		ImageButton imageButton = (ImageButton) findViewById(R.id.imgbtn_left);
		imageButton.setVisibility(View.VISIBLE);
		imageButton.setImageResource(R.drawable.back);
		imageButton.setOnClickListener(this);
		// 设置标题栏
		TextView txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setVisibility(View.VISIBLE);
		txt_title.setText("捐赠详情");
		// 设置分享键
		ImageButton imgbtn_right = (ImageButton) findViewById(R.id.imgbtn_right);
		imgbtn_right.setVisibility(View.VISIBLE);
		imgbtn_right.setImageResource(R.drawable.icon_share);
		imgbtn_right.setOnClickListener(this);
	}

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

		new AlertDialog(DonateActivity.this).builder().setTitle("提醒")
				.setMsg("登录一下吧~")
				.setPositiveButton("登录", new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(DonateActivity.this,
								LoginActivity.class);
						startActivity(intent);
					}
				}).setNegativeButton("取消", new OnClickListener() {
					@Override
					public void onClick(View v) {
					}
				}).show();
	}

	@Override
	protected void onDestroy() {
		ShareSDK.stopSDK(this);
		super.onDestroy();
	}
}
