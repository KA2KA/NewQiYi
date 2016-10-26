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
import com.newqiyi.util.URLUtil;

public class ReSetPwdActivity extends Activity implements OnClickListener {

	private String name;
	private String pass;
	private EditText et_name;
	private EditText et_pass;
	private String url = "servlet/AndroidReSetPassWordServlet";

	private EditText et_verify;
	private Button btn_ok;
	private Button btn_getVerify;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.reset_password);
		initTitleBar();
		et_name = (EditText) findViewById(R.id.xiugai_name);
		et_pass = (EditText) findViewById(R.id.xiugai_pass);
		et_verify = (EditText) findViewById(R.id.et_verify);
		btn_ok = (Button) findViewById(R.id.btn_ok);
		btn_getVerify = (Button) findViewById(R.id.btn_getVerify);
		btn_ok.setOnClickListener(this);
		btn_getVerify.setOnClickListener(this);

	}

	public void getVerify() {

		name = et_name.getText().toString().trim();
		pass = et_pass.getText().toString().trim();

		if (TextUtils.isEmpty(name)
				|| !name.matches("^(13[0-9]|14[0-9]|15[0-9]|18[0-9])\\d{8}$")) {
			Toast.makeText(getApplicationContext(), "用户名输入有误",
					Toast.LENGTH_SHORT).show();
		} else if (TextUtils.isEmpty(pass)
				|| !pass.matches("^[a-zA-Z0-9_]\\w{5,17}$")) {
			Toast.makeText(getApplicationContext(), "密码输入有误",
					Toast.LENGTH_SHORT).show();
		} else {
			SMSSDK.initSDK(this, "bf842f86471a",
					"ba5f3829db4d5234eb56c08ba7c68b94", true);

			cn.smssdk.SMSSDK.getVerificationCode("86", name);
			Toast.makeText(ReSetPwdActivity.this, name + "获取验证码",
					Toast.LENGTH_SHORT).show();
		}

	}

	public void updateOk() {
		name = et_name.getText().toString().trim();
		pass = et_pass.getText().toString().trim();
		final String verify = et_verify.getText().toString().trim();

		if (TextUtils.isEmpty(name)
				|| !name.matches("^(13[0-9]|14[0-9]|15[0-9]|18[0-9])\\d{8}$")) {
			Toast.makeText(getApplicationContext(), "用户名输入有误",
					Toast.LENGTH_LONG).show();
		} else if (TextUtils.isEmpty(pass)
				|| !pass.matches("^[a-zA-Z0-9_]\\w{5,17}$")) {
			Toast.makeText(getApplicationContext(), "密码输入有误",
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

						private void dealData(String result) {
							if (TextUtils.isEmpty(result)) {
								Toast.makeText(getApplicationContext(),
										"服务器异常", Toast.LENGTH_SHORT).show();
							} else if (result.equals("RESETPWDERROR".trim())) {
								Toast.makeText(getApplicationContext(), result,
										Toast.LENGTH_SHORT).show();
							} else if (result.equals("UNREGIST".trim())) {
								Toast.makeText(getApplicationContext(), result,
										Toast.LENGTH_SHORT).show();
							} else {

								Intent intent = new Intent(
										ReSetPwdActivity.this,
										LoginActivity.class);
								startActivity(intent);

							}
						}
					});
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.imgbtn_left) {
			finish();
		} else if (v.getId() == R.id.btn_ok) {
			updateOk();
		} else if (v.getId() == R.id.btn_getVerify) {
			getVerify();
		}

	}

	private void initTitleBar() {
		// 最左边的
		// Button button = (Button) findViewById(R.id.btn_left);
		// button.setVisibility(View.GONE);
		// 设置返回按钮
		ImageButton imageButton = (ImageButton) findViewById(R.id.imgbtn_left);
		imageButton.setVisibility(View.VISIBLE);
		imageButton.setImageResource(R.drawable.back);
		imageButton.setOnClickListener(this);
		// 设置标题栏
		TextView txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setVisibility(View.VISIBLE);
		txt_title.setText("修改密码");
		// 设置评论键
		// ImageButton imgbtn_comment = (ImageButton)
		// findViewById(R.id.imgbtn_comment);
		// imgbtn_comment.setVisibility(View.GONE);
		//
		// // 设置分享键
		// ImageButton imgbtn_right = (ImageButton)
		// findViewById(R.id.imgbtn_right);
		// imgbtn_right.setVisibility(View.GONE);
		//
		// // 最右面的按钮
		// ImageButton btn_right = (ImageButton) findViewById(R.id.btn_right);
		// btn_right.setVisibility(View.GONE);
	}
}