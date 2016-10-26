package com.newqiyi.activity;

import java.io.Serializable;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.newqiyi.domain.User;
import com.newqiyi.util.GsonUtil;
import com.newqiyi.util.SharePrefUitl;
import com.newqiyi.util.URLUtil;
import com.newqiyi.view.RoundImageView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author NIRVANA
 * 
 */
public class OtherUserActivity extends Activity implements OnClickListener {
	@ViewInject(R.id.otherUser_avatar)
	private RoundImageView otherUser_avatar;
	@ViewInject(R.id.otherUser_name)
	private TextView otherUser_name;
	@ViewInject(R.id.otherUser_level)
	private TextView otherUser_level;
	@ViewInject(R.id.otherUser_point_sign)
	private TextView otherUser_point_sign;
	@ViewInject(R.id.otherUser_point)
	private TextView otherUser_point;
	@ViewInject(R.id.otherUser_focus_num)
	private TextView otherUser_focus_num;
	@ViewInject(R.id.otherUser_fans_num)
	private TextView otherUser_fans_num;
	@ViewInject(R.id.otherUser_collect_num)
	private TextView otherUser_collect_num;
	@ViewInject(R.id.other_user_back)
	private TextView other_user_back;
	@ViewInject(R.id.otherUser_focus_layout)
	private RelativeLayout otherUser_focus_layout;
	@ViewInject(R.id.otherUser_fans_layout)
	private RelativeLayout otherUser_fans_layout;
	@ViewInject(R.id.otherUser_collect_layout)
	private RelativeLayout otherUser_collect_layout;
	@ViewInject(R.id.tv_addfocus)
	private TextView tv_addfocus;
	Intent intent;
	User user;
	String user_id;
	String url;
	private boolean flag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_other_user);
		String userData = SharePrefUitl.getStringData(getApplicationContext(),"UserData", "");
		if (!TextUtils.isEmpty(userData)) {
			User user = GsonUtil.jsonToBean(userData, User.class);
			user_id = user.getUser_id();
		}
		
		ViewUtils.inject(this);
		Intent intent = getIntent();
		user = (User) intent.getSerializableExtra("otherUserInfo");
		otherUser_level = (TextView) findViewById(R.id.otherUser_level);
		initView();
	}

	public void initView() {
		// otherUser_avatar=(RoundImageView)
		// findViewById(R.id.otherUser_avatar);
		// otherUser_name = (TextView) findViewById(R.id.otherUser_name);
		// otherUser_level = (TextView) findViewById(R.id.otherUser_level);
		// otherUser_point_sign = (TextView)
		// findViewById(R.id.otherUser_point_sign);
		// otherUser_point = (TextView) findViewById(R.id.otherUser_point);
		// otherUser_focus_num = (TextView)
		// findViewById(R.id.otherUser_focus_num);
		// otherUser_fans_num = (TextView)
		// findViewById(R.id.otherUser_fans_num);
		// otherUser_collect_num = (TextView)
		// findViewById(R.id.otherUser_collect_num);
		// other_user_back = (TextView) findViewById(R.id.other_user_back);
		// user_focus_layout = (RelativeLayout)
		// findViewById(R.id.user_focus_layout);
		// otherUser_fans_layout = (RelativeLayout)
		// findViewById(R.id.otherUser_fans_layout);
		// user_focus_layout = (RelativeLayout)
		// findViewById(R.id.user_focus_layout);
		BitmapUtils bitUtils = new BitmapUtils(this);
		String uri = URLUtil.BASE_URL + user.getPhoto();
		bitUtils.display(otherUser_avatar, uri);
		otherUser_name.setText(user.getUser_name());
		otherUser_level.setText("LV " + user.getUser_level());
		otherUser_point_sign.setText(user.getSignname());
		otherUser_point.setText(user.getUser_score());
		other_user_back.setOnClickListener(this);
		otherUser_focus_layout.setOnClickListener(this);
		otherUser_fans_layout.setOnClickListener(this);
		otherUser_collect_layout.setOnClickListener(this);
		tv_addfocus.setOnClickListener(this);
		// 访问网络判断是否是已经关注的对象
		postData();

	}

	private void postData() {
		url="servlet/CareFansQueryServlet";
		
		RequestParams params = new RequestParams();
		params.addBodyParameter("user_id", user_id);
		params.addBodyParameter("other_id", user.getUser_id());

		new HttpUtils().send(HttpMethod.POST, URLUtil.BASE_URL + url, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				
			}

			@Override
			public void onSuccess(ResponseInfo<String> reInfo) {
				if ("好友".equals(reInfo.result.trim())) {
					flag = true;
					tv_addfocus.setText("取消关注");
				} else {
					tv_addfocus.setText("添加关注");
				}

			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.other_user_back:
			finish();
			break;
//		case R.id.user_focus_layout:
//			intent = new Intent(OtherUserActivity.this, UserFocusActivity.class);
//			intent.putExtra("user", (Serializable) user);
//			startActivity(intent);
//			break;
//		case R.id.otherUser_fans_layout:
//			intent = new Intent(OtherUserActivity.this, UserFansActivity.class);
//			intent.putExtra("user", (Serializable) user);
//			startActivity(intent);
//			break;
//		case R.id.otherUser_collect_layout:
//
//			break;
		case R.id.tv_addfocus:
			if (flag) {
				deleteFocus();
			} else {
				addFoucs();
			}
			break;

		}
	}

	private void deleteFocus() {
		url="servlet/CareFansDeleteServlet2";
		RequestParams params = new RequestParams();
		params.addBodyParameter("user_id", user_id);
		params.addBodyParameter("other_id", user.getUser_id());

		new HttpUtils().send(HttpMethod.POST, URLUtil.BASE_URL + url, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {

			}

			@Override
			public void onSuccess(ResponseInfo<String> reInfo) {
				if ("删除关注".equals(reInfo.result.trim())) {
					flag = false;
					tv_addfocus.setText("添加关注");
				} else {
					Toast.makeText(getApplication(), "quxiaoshibai", 0).show();
					tv_addfocus.setText("取消关注");
				}

			}
		});

	}

	private void addFoucs() {
		url="servlet/CareFansAddServlet";
		RequestParams params = new RequestParams();
		params.addBodyParameter("user_id", user_id);
		params.addBodyParameter("other_id", user.getUser_id());

		new HttpUtils().send(HttpMethod.POST, URLUtil.BASE_URL + url, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException error, String msg) {
				Toast.makeText(OtherUserActivity.this, error.getMessage() + ":" + msg,
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSuccess(ResponseInfo<String> reInfo) {
				if ("好友".equals(reInfo.result.trim())) {
					flag = true;
					tv_addfocus.setText("取消关注");
				} else {
					tv_addfocus.setText("添加关注");
				}

			}
		});
	}
	
}
