package com.newqiyi.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class UpdateSignatureActivity extends Activity {
	private EditText update_signature_edit;
	private static final int UserSignatureOk = 77;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_signature);
		update_signature_edit = (EditText) findViewById(R.id.update_signature_edit);
	}
	public void updateSignatureBack(View v){
		finish();
	}
	public void submitNewSignnature(View v) {
		String newSignature = update_signature_edit.getText().toString();
		Intent intent = new Intent();
		intent.putExtra("newSignature", newSignature);
		setResult(UserSignatureOk, intent);
		finish();
	}
}
