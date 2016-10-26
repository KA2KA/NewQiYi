package com.newqiyi.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import cn.smssdk.SMSSDK;

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

/**
 * @author 巩文婷
 * @E-mail:1763623356@qq.com
 * @version 创建时间：${date} ${time} 注册界面
 */
public class RegistActivity extends Activity implements OnClickListener {

	private String url = "servlet/AndroidRegisterServlet";// 注册的url；（还没有赋值）

	private Button btn_getVerify;
	private EditText et_name;
	private EditText et_pass;
	private String name;

	private String pass;
	private EditText et_pass2;

	private EditText et_verify;

	private String pass2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_regist);
		initTitleBar();
		et_name = (EditText) findViewById(R.id.zhuce_name);
		et_pass = (EditText) findViewById(R.id.zhuce_pass);
		et_pass2 = (EditText) findViewById(R.id.zhuce_passtest);
		et_verify = (EditText) findViewById(R.id.yanzhengma);
		btn_getVerify = (Button) findViewById(R.id.btn_getVerification);
		Button btn_regist = (Button) findViewById(R.id.btn_regist);
		btn_getVerify.setOnClickListener(this);
		btn_regist.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_getVerification:
			getVerify();
			break;
		case R.id.btn_regist:

			regist();

		case R.id.imgbtn_left:
			finish();
			break;
		}

	}

	public void getVerify() {
		name = et_name.getText().toString().trim();
		pass = et_pass.getText().toString().trim();
		pass2 = et_pass2.getText().toString().trim();
		if (TextUtils.isEmpty(name)
				|| !name.matches("^(13[0-9]|14[0-9]|15[0-9]|18[0-9])\\d{8}$")) {

			Toast.makeText(getApplicationContext(), "用户名输入有误",
					Toast.LENGTH_SHORT).show();

		} else if (TextUtils.isEmpty(pass)
				|| !pass.matches("^[a-zA-Z0-9_]\\w{5,17}$")) {

			Toast.makeText(getApplicationContext(), "密码输入有误",
					Toast.LENGTH_SHORT).show();

		} else if (!pass.equals(pass2)) {
			Toast.makeText(getApplicationContext(), "两次输入的不一致",
					Toast.LENGTH_SHORT).show();
		} else {

			SMSSDK.initSDK(this, "bf842f86471a",
					"ba5f3829db4d5234eb56c08ba7c68b94", true);
			cn.smssdk.SMSSDK.getVerificationCode("86", name);
			Toast.makeText(RegistActivity.this, name + "获取验证码",
					Toast.LENGTH_LONG).show();
		}
	}

	public void regist() {
		name = et_name.getText().toString().trim();
		pass = et_pass.getText().toString().trim();
		final String verify = et_verify.getText().toString().trim();
		if (TextUtils.isEmpty(name)
				|| !name.matches("^(13[0-9]|14[0-9]|15[0-9]|18[0-9])\\d{8}$")) {
			Toast.makeText(getApplicationContext(), "用户名输入有误",
					Toast.LENGTH_SHORT).show();
		} else if (TextUtils.isEmpty(pass)
				|| !pass.matches("^[a-zA-Z0-9_]\\w{5,17}$")) {
			Toast.makeText(getApplicationContext(), "密码输入有误",
					Toast.LENGTH_SHORT).show();
		} else if (!pass.equals(pass2)) {
			Toast.makeText(getApplicationContext(), "两次输入的不一致",
					Toast.LENGTH_SHORT).show();
		} else {

			RequestParams params = new RequestParams();
			params.addBodyParameter("username", name);
			params.addBodyParameter("password", pass);
			params.addBodyParameter("verify", verify);
			new HttpUtils().send(HttpMethod.POST, URLUtil.BASE_URL + url,
					params, new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String arg1) {
						}

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {

							dealData(responseInfo.result);
						}
					});
		}
	}

	private void dealData(String result) {
		if (TextUtils.isEmpty(result)) {
			Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_SHORT)
					.show();
		} else if (result.equals("VERIFYERROR".trim())) {
			Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT)
					.show();
		} else if (result.equals("REGISTED".trim())) {
			Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT)
					.show();
		} else {

			// /*
			// * 调用环信sdk
			// */
			// new Thread(new Runnable() {
			// @Override
			// public void run() {
			// try {
			// // 调用sdk注册方法
			// EMChatManager.getInstance().createAccountOnServer(name,
			// pass);
			// } catch (final EaseMobException e) {
			// // 注册失败
			// int errorCode = e.getErrorCode();
			// if (errorCode == EMError.NONETWORK_ERROR) {
			// Toast.makeText(getApplicationContext(),
			// "网络异常，请检查网络！", Toast.LENGTH_SHORT).show();
			// } else if (errorCode == EMError.USER_ALREADY_EXISTS) {
			// Toast.makeText(getApplicationContext(), "用户已存在！",
			// Toast.LENGTH_SHORT).show();
			// } else if (errorCode == EMError.UNAUTHORIZED) {
			// Toast.makeText(getApplicationContext(),
			// "注册失败，无权限！", Toast.LENGTH_SHORT).show();
			// } else {
			// Toast.makeText(getApplicationContext(),
			// "注册失败: " + e.getMessage(),
			// Toast.LENGTH_SHORT).show();
			// }
			// }
			// }
			// }).start();

			Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT)
					.show();
			SharePrefUitl.saveStringData(getApplicationContext(), "UserData",
					result);

			User user = GsonUtil.jsonToBean(result, User.class);

			StepUser sUser = new StepUser();
			sUser.setUser_id(user.getUser_id());
			QiyiDB db = QiyiDB.getInstance(getApplication());
			db.saveUser(sUser);

			Intent intent = new Intent(RegistActivity.this, LoginActivity.class);
			startActivity(intent);

		}
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
		txt_title.setText("注    册");
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

}
