package com.newqiyi.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.newqiyi.database.QiyiDB;
import com.newqiyi.domain.StepUser;
import com.newqiyi.domain.User;
import com.newqiyi.util.GsonUtil;
import com.newqiyi.util.SharePrefUitl;
import com.newqiyi.util.URLUtil;
import com.newqiyi.view.CircleImageView;

/**
 * @author 巩文婷&& 吴旺高
 * @E-mail:1763623356@qq.com
 * @version 创建时间：${date} ${time}
 */

@SuppressLint("ShowToast")
public class LoginActivity extends Activity {
	CircleImageView civ;

	private String url = "servlet/AndroidUserServlet";
	private EditText et_pass;
	private EditText et_name;

	// activity创建时，此方法调用
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		String userData = SharePrefUitl.getStringData(getApplicationContext(),
				"UserData", "");

		if (!TextUtils.isEmpty(userData)) {
			startActivity(new Intent(LoginActivity.this, MainActivity.class));
			return;
		}

		// 设置显示内容，通过设置资源id指定该activity显示哪个布局文件
		setContentView(R.layout.activity_login);
	}

	// 点击登录
	public void login(View v) {
		/*
		 * 1、先从内存中读取数据、有就直接跳转至主页 2、若没有用户信息从网络获取数据
		 */
		et_name = (EditText) findViewById(R.id.name);
		et_pass = (EditText) findViewById(R.id.pass);

		// 如果用户名改变，清空密码
		et_name.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				et_pass.setText(null);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		String userData = SharePrefUitl.getStringData(getApplicationContext(),
				"UserData", "");
		if (!TextUtils.isEmpty(userData)) {
			User user = GsonUtil.jsonToBean(userData, User.class);
			et_name.setText(user.getUser_name());
		}

		/**
		 * 登录
		 */
		String account = et_name.getText().toString().trim();
		String pass = et_pass.getText().toString().trim();
		if (TextUtils.isEmpty(account)) {
			Toast.makeText(getApplicationContext(), "用户名为空", 0).show();
		} else if (!account
				.matches("^(13[0-9]|14[0-9]|15[0-9]|18[0-9])\\d{8}$")) {
			Toast.makeText(getApplicationContext(), "用户名输入有误", 0).show();
		} else if (TextUtils.isEmpty(pass)) {
			Toast.makeText(getApplicationContext(), "密码为空", 0).show();

		} else if (!pass.matches("^[a-zA-Z@0-9_]\\w{5,17}$")) {
			Toast.makeText(getApplicationContext(), "密码输入有误", 0).show();
		} else {
			System.out.println(account + ":" + pass);

			/*
			 * 环信登录
			 */
			// EMChatManager.getInstance().login(account, pass, new EMCallBack()
			// {// 回调
			// @Override
			// public void onSuccess() {
			//
			// }
			//
			// @Override
			// public void onProgress(int progress, String status) {
			//
			// }
			//
			// @Override
			// public void onError(int code, String message) {
			//
			// }
			// });

			HttpUtils utils = new HttpUtils();
			RequestParams params = new RequestParams();
			params.addBodyParameter("username", account);
			params.addBodyParameter("password", pass);
			System.out.println(account + "::" + pass);
			utils.send(HttpMethod.POST, URLUtil.BASE_URL + url, params,
					new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String string) {
							Toast.makeText(getApplicationContext(), "服务器异常", 1)
									.show();
						}

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							System.out.println(responseInfo.result);
							if (!(responseInfo.result).equals("false")) {

								// 保存登陆用户信息
								SharePrefUitl.saveStringData(
										getApplicationContext(), "UserData",
										responseInfo.result);
								// 拿到用户对象将用户id放到SQLite
								User user = GsonUtil.jsonToBean(
										responseInfo.result, User.class);

								StepUser sUser = new StepUser();
								sUser.setUser_id(user.getUser_id());

								QiyiDB db = QiyiDB
										.getInstance(getApplication());
								db.saveUser(sUser);

								// 拿到User对象
								MyApplication.getInstance().setUser(user);
								Toast.makeText(getApplicationContext(), "登录成功",
										0).show();

								String extra = getIntent().getStringExtra(
										"page");
								if (!TextUtils.isEmpty(extra)) {
									Intent intent = new Intent(
											LoginActivity.this,
											MainActivity.class);
									intent.putExtra("page", "3");
									startActivity(intent);
								}

								finish();
							} else {
								Toast.makeText(getApplicationContext(),
										"该用户不存在", 1).show();
							}
						}
					});
		}
	}

	/**
	 * 跳转至RegistActivity页面
	 * 
	 * @param v
	 */
	public void zhuce(View v) {
		Intent intent = new Intent();
		// cls：直接指定目标Activity的类名
		// 显示意图
		intent.setClass(this, RegistActivity.class);
		startActivity(intent);
	}

	/**
	 * 跳转至ResetPassword页面
	 * 
	 * @param v
	 */
	public void zhaohui(View v) {
		Intent intent = new Intent();
		// cls：直接指定目标Activity的类名
		// 显示意图
		intent.setClass(this, ReSetPwdActivity.class);
		startActivity(intent);
	}

}
