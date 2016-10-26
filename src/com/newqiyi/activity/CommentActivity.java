package com.newqiyi.activity;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.newqiyi.adapter.ComAdapter;
import com.newqiyi.domain.CommentBean;
import com.newqiyi.domain.CommentProject;
import com.newqiyi.domain.User;
import com.newqiyi.util.GsonUtil;
import com.newqiyi.util.SharePrefUitl;
import com.newqiyi.util.URLUtil;
import com.newqiyi.view.RoundImageView;

/**
 * 评论与回复
 * 
 * @author 李晶
 * 
 */
public class CommentActivity extends Activity implements OnClickListener {

	private ComAdapter adapter;
	private HttpUtils http;
	private PullToRefreshListView mListView;
	private ListView rListView;
	private Context context;
	private EditText inputCom;
	private ImageView commentButton;
	private int user_id;
	private int pro_id;
	private BitmapUtils bitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment);

		String userData = SharePrefUitl.getStringData(getApplicationContext(),
				"UserData", "");
		if (!TextUtils.isEmpty(userData)) {
			User user = GsonUtil.jsonToBean(userData, User.class);
			user_id = Integer.parseInt(user.getUser_id());
		}
		Intent intent = getIntent();
		pro_id = Integer.parseInt(intent.getStringExtra("pro_id"));

		initTitleBar();
		mListView = (PullToRefreshListView) findViewById(R.id.lv_comlist);
		rListView = mListView.getRefreshableView();
		commentButton = (ImageView) findViewById(R.id.bt_submitCom);
		inputCom = (EditText) findViewById(R.id.et_inputCom);
		context = CommentActivity.this;
		bitmap = new BitmapUtils(context);
		commentButton.setOnClickListener(this);
		initData();

	}

	/**
	 * 从网络中获取评论数据
	 */
	public void initData() {
		mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				new FinishTypeRefresh(URLUtil.BASE_URL
						+ "ervlet/ComShowServlet?pro_id=" + pro_id).execute();
			}
		});

		postData();
	}

	private void postData() {
		http = new HttpUtils();
		http.configCurrentHttpCacheExpiry(1000 * 10); // 设置缓存10秒，10秒内直接返回上次成功请求的结果
		String url = URLUtil.BASE_URL + "servlet/ComShowServlet?pro_id="
				+ pro_id;
		http.send(HttpMethod.GET, url, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException error, String msg) {
				Toast.makeText(context, error.getMessage() + ":" + msg,
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSuccess(ResponseInfo<String> resp) {
				String json = resp.result;
				Gson g = new Gson();
				Type type = new TypeToken<List<CommentBean>>() {
				}.getType();
				List<CommentBean> beans = g.fromJson(json, type);
				adapter = new ComAdapter(beans, context);
				rListView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				// 自动滚动到ListView的底部
				scrollMyListViewToBottom(adapter);
				selectComItem(beans);
			}
		});
	}

	/*
	 * 选择要回复的评论
	 */
	public void selectComItem(final List<CommentBean> beans) {
		rListView.setOnItemClickListener(new OnItemClickListener() {
			private String time;

			@SuppressLint("SimpleDateFormat")
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				View v = View.inflate(context, R.layout.reply_edit, null);
				final EditText ed = (EditText) v.findViewById(R.id.et_com);
				String rep_name = beans.get(position).getUser().getUser_name();
				final int com_id = beans.get(position).getCp().getCom_id();
				final int reply_com_id = beans.get(position).getCp()
						.getCom_id();
				RoundImageView image_replog = (RoundImageView) v
						.findViewById(R.id.image_replog);
				bitmap.display(image_replog,
						URLUtil.BASE_URL
								+ beans.get(position).getUser().getPhoto());
				ed.setHint("回复" + rep_name);

				/* 封装要发送的数据 */
				Date date = new Date(System.currentTimeMillis());
				time = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
				Builder builder = new Builder(context);
				builder.setTitle("我想对你说~")
						.setView(v)
						.setPositiveButton("回复",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										if (ed.getText().toString().length() == 0) {
											Toast.makeText(context, "输入内容不能为空",
													Toast.LENGTH_SHORT).show();
										} else {
											CommentProject cp = new CommentProject(
													com_id, user_id, pro_id, ed
															.getText()
															.toString(), time,
													reply_com_id, "");
											initReply(cp);
											// 重新加载数据
											try {
												Thread.sleep(1000);
											} catch (InterruptedException e) {
												e.printStackTrace();
											}
											initData();
										}
									}

									// 选择条目发表评论
									private void initReply(
											final CommentProject comp) {
										HttpUtils http = new HttpUtils();
										Gson gson = new Gson();
										RequestParams params = new RequestParams(
												"utf-8");
										params.addQueryStringParameter("cp",
												gson.toJson(comp));

										http.send(
												HttpMethod.POST,
												URLUtil.BASE_URL
														+ "servlet/ComInsertServlet",
												params,
												new RequestCallBack<String>() {

													@Override
													public void onFailure(
															HttpException error,
															String msg) {
														Toast.makeText(
																context,
																error.getMessage()
																		+ ":"
																		+ msg,
																Toast.LENGTH_SHORT)
																.show();
													}

													@Override
													public void onSuccess(
															ResponseInfo<String> resp) {
														Toast.makeText(
																context,
																resp.result,
																Toast.LENGTH_SHORT)
																.show();
													}
												});
									}
								}).setNegativeButton("取消", null);
				AlertDialog dialog = builder.create();// 创建
				dialog.show();
			}
		});
	}

	/**
	 * 点击发表按钮，发表评论(non-Javadoc)
	 */

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.imgbtn_left:
			finish();
			break;
		case R.id.bt_submitCom:
			String userData = SharePrefUitl.getStringData(getApplication(),
					"UserData", "").trim();
			if (!TextUtils.isEmpty(userData)) {
				postComment();
			} else {
				// showAlertDialog();
				Toast.makeText(context, "您还没有登录啦~~", 0).show();
			}

			break;
		}
	}

	@SuppressLint("SimpleDateFormat")
	private void postComment() {
		// 封装要发送的数据
		String content = inputCom.getText().toString();
		CommentProject cp = new CommentProject();
		cp.setUser_id(user_id);
		cp.setPro_id(pro_id);
		cp.setCom_content(content);
		Date date = new Date(System.currentTimeMillis());

		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
		cp.setCom_time(time);

		if (content.length() == 0) {
			Toast.makeText(context, "输入内容不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if (content.length() > 100) {
			Toast.makeText(context, "输入内容不能超过100字", Toast.LENGTH_SHORT).show();
			return;
		}

		// 将对象转成Json发送到服务器
		Gson gson = new Gson();
		String json = gson.toJson(cp);
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("cp", json);
		String url = URLUtil.BASE_URL + "servlet/ComInsertServlet";
		http.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException error, String msg) {
				Toast.makeText(context, error.getMessage() + ":" + msg,
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSuccess(ResponseInfo<String> resp) {
				Toast.makeText(context, resp.result, Toast.LENGTH_SHORT).show();
			}
		});
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		initData();
		// 清空内容数据
		inputCom.setText("");
	}

	/**
	 * ListView数据更新后，自动滚动到底部
	 * 
	 * @param adapter
	 */
	private void scrollMyListViewToBottom(final ComAdapter adapter) {
		rListView.post(new Runnable() {
			@Override
			public void run() {
				rListView.setSelection(adapter.getCount() - 1);
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
		txt_title.setText("项目评论");
		// 设置评论键
		ImageButton imgbtn_comment = (ImageButton) findViewById(R.id.imgbtn_comment);
		imgbtn_comment.setVisibility(View.GONE);

		// 设置分享键
		ImageButton imgbtn_right = (ImageButton) findViewById(R.id.imgbtn_right);
		imgbtn_right.setVisibility(View.GONE);
		// 最右面的按钮
		ImageButton btn_right = (ImageButton) findViewById(R.id.btn_right);
		btn_right.setVisibility(View.GONE);
	}

	private class FinishTypeRefresh extends AsyncTask<Void, Void, Void> {
		String url;

		public FinishTypeRefresh(String url) {
			this.url = url;
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			postData();
			mListView.onRefreshComplete();
		}
	}

}
