package com.newqiyi.pager;

import java.io.Serializable;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.newqiyi.activity.LoginActivity;
import com.newqiyi.activity.MainActivity;
import com.newqiyi.activity.MyActListActivity;
import com.newqiyi.activity.R;
import com.newqiyi.activity.RegistActivity;
import com.newqiyi.activity.ScorePayingAcitvity;
import com.newqiyi.activity.UpdateUserInfoActivity;
import com.newqiyi.activity.UserFansActivity;
import com.newqiyi.activity.UserFocusActivity;
import com.newqiyi.activity.UserInfoProCollectActivity;
import com.newqiyi.domain.MySelfInfo;
import com.newqiyi.domain.User;
import com.newqiyi.util.GsonUtil;
import com.newqiyi.util.SharePrefUitl;
import com.newqiyi.util.URLUtil;
import com.newqiyi.view.RoundImageView;
import com.newqiyi.widget.AlertDialog;

public class MyselfPager extends BasePager implements OnClickListener {

	private View view;
	private BitmapUtils bitmapUtils;
	/*
	 * 用户基本信息
	 */
	// 名字
	@ViewInject(R.id.user_name)
	private TextView tv_user_name;
	// 头像
	@ViewInject(R.id.user_avatar)
	private RoundImageView im_user_avatar;
	// 用户签名
	@ViewInject(R.id.user_point_sign)
	private TextView tv_sign;
	// 用户启币数
	@ViewInject(R.id.user_point)
	private TextView tv_score;
	// 登录
	@ViewInject(R.id.user_login)
	private Button user_login;
	// 注册
	@ViewInject(R.id.user_regist)
	private Button user_regist;
	/*
	 * 用户关注
	 */
	@ViewInject(R.id.user_focus_layout)
	private RelativeLayout user_focus_layout;
	// 关注数
	@ViewInject(R.id.user_focus_num)
	private TextView user_focus_num;
	/*
	 * 用户粉丝
	 */
	@ViewInject(R.id.user_fans_layout)
	private RelativeLayout user_fans_layout;
	// 粉丝数
	@ViewInject(R.id.user_fans_num)
	private TextView user_fans_num;
	/*
	 * 用户收藏
	 */
	@ViewInject(R.id.user_collect_layout)
	private RelativeLayout user_collect_layout;
	// 收藏数
	@ViewInject(R.id.user_collect_num)
	private TextView user_collect_num;
	// 我的活动
	@ViewInject(R.id.user_activity)
	private RelativeLayout user_activity;
	/*
	 * 用户启币流水
	 */
	@ViewInject(R.id.user_pay)
	private RelativeLayout user_pay;

	/*
	 * 软件设置
	 */
	@ViewInject(R.id.user_setting)
	private RelativeLayout user_setting;

	private User user;
	Intent intent;
	private String url = "/servlet/AndroidMySelfInfo";

	public MyselfPager(Context context) {
		super(context);
		bitmapUtils = new BitmapUtils(context);
		MainActivity.flag = false;
		// 判断是否有用户

	}

	@Override
	public View initView() {
		view = View.inflate(context, R.layout.user_info, null);
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	public void initData() {
		im_user_avatar.setOnClickListener(this);
		user_focus_layout.setOnClickListener(this);
		user_fans_layout.setOnClickListener(this);
		user_collect_layout.setOnClickListener(this);
		user_activity.setOnClickListener(this);
		user_login.setOnClickListener(this);
		user_regist.setOnClickListener(this);
		user_pay.setOnClickListener(this);
		user_setting.setOnClickListener(this);

		// 访问网络拿取个人中心界面的数据
		String userData = SharePrefUitl.getStringData(context, "UserData", "")
				.trim();
		if (!TextUtils.isEmpty(userData)) {
			user = GsonUtil.jsonToBean(userData, User.class);
			postData();
		} else {
			user_login.setVisibility(View.VISIBLE);
			user_regist.setVisibility(View.VISIBLE);
			tv_user_name.setVisibility(View.GONE);

		}

	}

	private void postData() {
		RequestParams params = new RequestParams();
		params.addBodyParameter("user_id", user.getUser_id());

		new HttpUtils().send(HttpMethod.POST, URLUtil.BASE_URL + url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {

					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {

						if (!TextUtils.isEmpty(responseInfo.result)) {

							MySelfInfo info = GsonUtil.jsonToBean(
									responseInfo.result, MySelfInfo.class);

							if (info != null) {
								user = info.getData();
								user_login.setVisibility(View.GONE);
								user_regist.setVisibility(View.GONE);
								tv_user_name.setText(info.getData()
										.getUser_name());
								tv_user_name.setVisibility(View.VISIBLE);

								tv_score.setText(info.getData().getUser_score());
								bitmapUtils.display(im_user_avatar,
										URLUtil.BASE_URL
												+ info.getData().getPhoto());
								tv_sign.setText(info.getData().getSignname());
								user_fans_num.setText(info.getFanSize());
								user_focus_num.setText(info.getCareSize());
								user_collect_num.setText(info.getColSize());
							}
						}

					}
				});

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.user_avatar:
			if (user != null) {
				intent = new Intent(context, UpdateUserInfoActivity.class);
				context.startActivity(intent);
			} else {
				showAlertDialog();
			}
			break;
		case R.id.user_focus_layout:
			if (user != null) {
				intent = new Intent(context, UserFocusActivity.class);

				intent.putExtra("otherusercares", (Serializable) user);
				context.startActivity(intent);
			} else {
				showAlertDialog();
			}
			break;
		case R.id.user_fans_layout:
			if (user != null) {
				intent = new Intent(context, UserFansActivity.class);
				intent.putExtra("otheruserfans", (Serializable) user);
				context.startActivity(intent);
			} else {
				showAlertDialog();
			}
			break;
		case R.id.user_collect_layout:
			if (user != null) {
				intent = new Intent(context, UserInfoProCollectActivity.class);
				intent.putExtra("user", (Serializable) user);
				context.startActivity(intent);
			} else {
				showAlertDialog();
			}
			break;
		case R.id.user_activity:
			if (user != null) {
				intent = new Intent(context, MyActListActivity.class);
				intent.putExtra("user", (Serializable) user);
				context.startActivity(intent);
			} else {
				showAlertDialog();
			}
			break;
		case R.id.user_pay:
			if (user != null) {
				intent = new Intent(context, ScorePayingAcitvity.class);
				intent.putExtra("user", (Serializable) user);
				context.startActivity(intent);
			} else {
				showAlertDialog();
			}
			break;
		case R.id.user_setting:
			Toast.makeText(context, "设置功能还在开发中~~~", Toast.LENGTH_SHORT).show();
			break;
		case R.id.user_login:

			intent = new Intent(context, LoginActivity.class);
			intent.putExtra("page", "3");
			context.startActivity(intent);
			break;
		case R.id.user_regist:

			intent = new Intent(context, RegistActivity.class);
			context.startActivity(intent);
			break;
		}

	}

	// 判断登录的dialog
	public void showAlertDialog() {

		new AlertDialog(context).builder().setTitle("提醒").setMsg("登录一下吧~")
				.setPositiveButton("登录", new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(context, LoginActivity.class);
						context.startActivity(intent);
					}
				}).setNegativeButton("取消", new OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				}).show();
	}
}
