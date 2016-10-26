package com.newqiyi.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.newqiyi.util.URLUtil;
import com.newqiyi.widget.AppMsg;
import com.newqiyi.widget.AppMsg.Style;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * 
 * @author NIRVANA
 * 
 */
public class UpdateNameActivity extends Activity {
	EditText NewName;
	Button submitNewName;
	String NewNameText;
	Style style = AppMsg.STYLE_CONFIRM;
	private int UserNameOk = 55;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_name);
		NewName = (EditText) findViewById(R.id.update_name_edit);
		submitNewName = (Button) findViewById(R.id.submitNewName);
	}

	public void submitNewName(View v) {
		NewNameText = NewName.getText().toString();
		if (NewNameText == null || "".equals(NewNameText.trim())) {
			AppMsg.makeText(this, "新昵称不能为空", style).show();
		} else if (NewNameText.trim().length() < 2 || NewNameText.length() > 8) {
			AppMsg.makeText(this, "新昵称长度不符", style).show();
		} else {
			confirmName();
		}
	}
public void updateNameBack(View v){
	finish();
}
	private void confirmName() {
		RequestParams params = new RequestParams("utf-8");
		HttpUtils httpUtils = new HttpUtils();
		String url = null;
		try {
			url = URLUtil.BASE_URL
					+ "servlet/CheckUserNameServlet"
					+ "?username="
					+ URLEncoder.encode(
							URLEncoder.encode(NewNameText, "utf-8"), "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		httpUtils.send(HttpMethod.GET, url, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				AppMsg.makeText(UpdateNameActivity.this, "请尝试重试", style).show();
			}

			@Override
			public void onSuccess(ResponseInfo<String> flag) {
				// TODO Auto-generated method stub
				if ("用户名可用".equals(flag.result)) {
					Intent intent = new Intent();
					intent.putExtra("newName", NewNameText);
					setResult(UserNameOk, intent);
					finish();
				} else {
					AppMsg.makeText(UpdateNameActivity.this, "用户名已存在", style)
							.show();
				}
			}

		});

	}
}
