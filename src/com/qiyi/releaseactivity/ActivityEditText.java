package com.qiyi.releaseactivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.newqiyi.activity.R;


/**
 * 
 * @author zwq
 *
 */
public class ActivityEditText extends Activity {
	EditText editTextActivity;
	private int ActivityInfoBack=22;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_edit_text);
		editTextActivity = (EditText) findViewById(R.id.editTextActivity);
		Intent intent = getIntent();
		String usedText = intent.getStringExtra("usedText");
		editTextActivity.setText(usedText);
	}

	// 返回
	public void editTextActivityBack(View v) {
		String result = editTextActivity.getText().toString();
		Intent intent = new Intent();
		intent.putExtra("result", result);
		setResult(ActivityInfoBack, intent);
		finish();
	}

	public void editTextAffirm(View v) {
		String result = editTextActivity.getText().toString();
		Intent intent = new Intent();
		intent.putExtra("result", result);
		setResult(ActivityInfoBack, intent);
		finish();

	}
}
