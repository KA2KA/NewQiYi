package com.newqiyi.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.newqiyi.domain.MemberActivity;
import com.newqiyi.domain.User;
import com.newqiyi.util.URLUtil;
import com.newqiyi.widget.AlertDialog;
import com.newqiyi.widget.AppMsg;

public class JoinActActivity extends Activity {
	public AppMsg.Style style = AppMsg.STYLE_CONFIRM;
	private EditText memberTel;// 报名成员手机号
	private EditText memberIntro;// 报名成员介绍
	private String url = URLUtil.BASE_URL + "servlet/ActJoinServlet";
	private int act_id;
	private int ActivityCount;
	private int JOINSUCESS = 12;
	private User mySelf;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_join);
		memberTel = (EditText) findViewById(R.id.Join_activity_Tel);
		memberIntro = (EditText) findViewById(R.id.Join_activity_Intro);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		act_id = bundle.getInt("act_id");
		ActivityCount = bundle.getInt("ActivityCount");
		mySelf = MyApplication.getInstance().getUser();
	}

	// 设置返回键动作：返回上一层界面
	public void JoinEventBack(View v) {
		new AlertDialog(JoinActActivity.this).builder().setTitle("提醒")
				.setMsg("取消报名吗").setPositiveButton("确认", new OnClickListener() {
					@Override
					public void onClick(View v) {
						Runtime runtime = Runtime.getRuntime();
						try {
							runtime.exec("input keyevent "
									+ KeyEvent.KEYCODE_BACK);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).setNegativeButton("取消", new OnClickListener() {
					@Override
					public void onClick(View v) {
					}
				}).show();

	}

	public void submitInfo(View v) {
		if (memberTel == null || "".equals(memberTel.toString().trim())) {
			AppMsg.makeText(this, "请输入手机号码", style).show();
		} else if (!memberTel.getText().toString()
				.matches("^(13[0-9]|14[0-9]|15[0-9]|18[0-9])\\d{8}$")) {
			AppMsg.makeText(this, "请输入正确的手机号码", style).show();
		} else {
			new Thread() {
				@Override
				public void run() {
					RequestParams params = new RequestParams("utf-8");
					MemberActivity activity = new MemberActivity();
					activity.setAct_id(act_id);
					activity.setUser_id(Integer.parseInt(mySelf.getUser_id()));
					activity.setUser_tel(memberTel.getText().toString());
					activity.setJoin_intro(memberIntro.getText().toString());
					Gson gson = new Gson();
					String joinInfo = gson.toJson(activity);
					try {
						joinInfo = URLEncoder.encode(joinInfo, "utf-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					params.addBodyParameter("joinInfo", joinInfo);
					HttpUtils httpUtil = new HttpUtils();
					httpUtil.configCurrentHttpCacheExpiry(10000); // 设置缓存时间 毫秒
					httpUtil.send(HttpRequest.HttpMethod.POST, url, params,
							new RequestCallBack<String>() {
								@Override
								public void onFailure(HttpException arg0,
										String arg1) {
									// TODO Auto-generated method stub
								}

								@Override
								public void onSuccess(ResponseInfo<String> arg0) {
									// TODO Auto-generated method stub
									Intent intent = new Intent();
									intent.putExtra("JOINFLAG", true);
									setResult(JOINSUCESS, intent);
									style = AppMsg.STYLE_INFO;
									AppMsg.makeText(JoinActActivity.this,
											"报名成功", style).show();
									finish();
								}
							});
				}
			}.start();
		}
	}

}
