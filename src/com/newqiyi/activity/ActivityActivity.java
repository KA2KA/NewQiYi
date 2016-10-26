package com.newqiyi.activity;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.newqiyi.domain.ActivityMemberInfo;
import com.newqiyi.domain.MemberActivity;
import com.newqiyi.domain.QiyiActivity;
import com.newqiyi.domain.User;
import com.newqiyi.util.SharePrefUitl;
import com.newqiyi.util.URLUtil;
import com.newqiyi.view.RoundImageView;
import com.newqiyi.widget.AlertDialog;
import com.newqiyi.widget.AppMsg;
import com.newqiyi.widget.AppMsg.Style;

/**
 * 
 * @author NIRVANA
 * 
 */
public class ActivityActivity extends Activity {
	@ViewInject(R.id.activity_title_text)
	private TextView activity_title_text;
	@ViewInject(R.id.activity_location_text)
	private TextView activity_location_text;
	@ViewInject(R.id.activity_time_text)
	private TextView activity_time_text;
	@ViewInject(R.id.activity_type_text)
	private TextView activity_type_text;
	@ViewInject(R.id.activity_Info)
	private TextView activity_Info;
	@ViewInject(R.id.members_num)
	private TextView members_num;
	@ViewInject(R.id.members_list_num)
	private TextView members_list_num;
	@ViewInject(R.id.activity_click_details)
	private TextView activity_click_details;
	@ViewInject(R.id.activity_Info_sign)
	private ImageView activity_Info_sign;
	@ViewInject(R.id.initiator_avatar)
	private RoundImageView initiator_avatar;
	private RoundImageView MembersAvatar;
	@ViewInject(R.id.Join_activity_btn)
	private Button Join_activity_btn;
	@ViewInject(R.id.ActivityActivity)
	private View ActivityActivity;
	@ViewInject(R.id.Activity_Click_Details)
	private View Activity_Click_Details;
	@ViewInject(R.id.Activity_UI_Back)
	private View Activity_UI_Back;
	@ViewInject(R.id.Activity_UI)
	private View Activity_UI;
	@ViewInject(R.id.Activity_Info)
	private View ActivityInfo;
	@ViewInject(R.id.Activity_menber_Scroll)
	private HorizontalScrollView MembersScrollView;
	private LinearLayout MembersAvatarlayout;
	private QiyiActivity qiyiActivity;
	private int ActivityCount;
	private int act_id;
	private BitmapUtils bitmapUtils;
	private ArrayList<User> memberList;// 成员列表
	private ArrayList<MemberActivity> ActMemberList;
	private int JOINREQUEST = 11;
	private int JOINSUCESS = 12;
	User holdUser;
	private User Myself;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setContentView(R.layout.activity_qiyi_event);
		ViewUtils.inject(this);
		Intent intent = getIntent();
		qiyiActivity = (QiyiActivity) intent
				.getSerializableExtra("activityInfo");
		bitmapUtils = new BitmapUtils(this);
		MembersAvatarlayout = new LinearLayout(this);
		Myself = MyApplication.getInstance().getUser();
		getActInfo();
		SelectInfo();
		if (Myself != null) {
			getActMembers();
		}
		initHoldUser();
		if (qiyiActivity.getAct_status() == qiyiActivity.getJoin_count()) {
			Join_activity_btn.setText("活动人数已满");
			if (extractNum(qiyiActivity.getStart_time()).before(new Date())) {
				Join_activity_btn.setText("活动已过期");
			}
		} else if (extractNum(qiyiActivity.getStart_time()).before(new Date())) {
			Join_activity_btn.setText("活动已过期");
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == JOINREQUEST) {
			if (resultCode == JOINSUCESS) {
				Boolean flag = data.getBooleanExtra("JOINFLAG", false);
				if (flag) {
					Join_activity_btn.setText("已报名");
				}
			}
		}
	}

	public void initHoldUser() {
		String url = URLUtil.BASE_URL
				+ "servlet/QueryUserIdServlet?holdUserId="
				+ qiyiActivity.getHold_user_id();
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.GET, url, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				Gson gson = new Gson();
				Type type = new TypeToken<User>() {
				}.getType();
				holdUser = gson.fromJson(arg0.result, type);
				bitmapUtils.display(initiator_avatar, URLUtil.BASE_URL
						+ holdUser.getPhoto());
				initiator_avatar.setClickable(true);
				initiator_avatar.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(ActivityActivity.this,
								OtherUserActivity.class);
						intent.putExtra("otherUserInfo",
								(Serializable) holdUser);
						startActivity(intent);
					}
				});
			}
		});
	}

	// 设置跳转到报名界面
	public void JoinActivity(View v) {
		String trim = SharePrefUitl.getStringData(getApplication(), "UserData",
				"").trim();
		if (!TextUtils.isEmpty(trim)) {
			if (Join_activity_btn.getText().equals("报名")) {
				Intent intent = new Intent(ActivityActivity.this,
						JoinActActivity.class);
				intent.putExtra("act_id", act_id);
				intent.putExtra("ActivityCount", ActivityCount);
				startActivityForResult(intent, JOINREQUEST);
			} else if (Join_activity_btn.getText().equals("已报名")) {

				Join_activity_btn.setOnClickListener(new OnClickListener() {
					String url = URLUtil.BASE_URL
							+ "servlet/ActDelMemberServlet?" + "user_id="
							+ Myself.getUser_id() + "&act_id="
							+ qiyiActivity.getAct_id();

					@Override
					public void onClick(View v) {
						new AlertDialog(ActivityActivity.this)
								.builder()
								.setTitle("提醒")
								.setMsg("确定退出该活动吗")
								.setPositiveButton("确认", new OnClickListener() {
									@Override
									public void onClick(View v) {

										RequestParams params = new RequestParams(
												"utf-8");
										HttpUtils httpUtil = new HttpUtils();
										httpUtil.configCurrentHttpCacheExpiry(10000); // 设置缓存时间
																						// 毫秒
										httpUtil.send(
												HttpRequest.HttpMethod.GET,
												url, params,
												new RequestCallBack<String>() {
													@Override
													public void onFailure(
															HttpException arg0,
															String arg1) {
														Style style = AppMsg.STYLE_CONFIRM;
														AppMsg.makeText(
																ActivityActivity.this,
																"退出失败，请重试",
																style);
													}

													@Override
													public void onSuccess(
															ResponseInfo<String> arg0) {
														Join_activity_btn
																.setText("报名");
														Style style = AppMsg.STYLE_INFO;
														AppMsg.makeText(
																ActivityActivity.this,
																"您已退出活动", style);
														onResume();
														if (qiyiActivity
																.getAct_status() == qiyiActivity
																.getJoin_count()) {
															Join_activity_btn
																	.setText("活动人数已满");
															if (extractNum(
																	qiyiActivity
																			.getStart_time())
																	.before(new Date())) {
																Join_activity_btn
																		.setText("活动已过期");
															}
														} else if (extractNum(
																qiyiActivity
																		.getStart_time())
																.before(new Date())) {
															Join_activity_btn
																	.setText("活动已过期");
														}
													}
												});
									}
								})
								.setNegativeButton("取消", new OnClickListener() {
									@Override
									public void onClick(View v) {
									}
								}).show();
					}
				});
			} else if (Join_activity_btn.getText().equals("查看活动人员")) {
				if (memberList.size() != 0) {
					Intent intent = new Intent(ActivityActivity.this,
							MembersActivity.class);
					intent.putExtra("membersList", (Serializable) memberList);
					intent.putExtra("ActMemberList",
							(Serializable) ActMemberList);
					startActivity(intent);
				} else {
					Toast.makeText(ActivityActivity.this, "暂时无人报名",
							Toast.LENGTH_SHORT).show();
				}

			}
		}
	}

	public void alertDialog() {
		new AlertDialog(ActivityActivity.this).builder().setTitle("提醒")
				.setMsg("登录一下吧~")
				.setPositiveButton("登录", new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(ActivityActivity.this,
								LoginActivity.class);
						startActivity(intent);
					}
				}).setNegativeButton("取消", new OnClickListener() {
					@Override
					public void onClick(View v) {
					}
				}).show();
	}

	// 把活动时间字符串转为Date型
	private Date extractNum(String time) {
		String regEx = "[^0-9]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(time);
		String timeNums = m.replaceAll("").trim();
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyyMMddhhmm").parse(timeNums);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	// 获取服务器上的活动信息
	public void getActInfo() {
		act_id = qiyiActivity.getAct_id();
		ActivityCount = qiyiActivity.getJoin_count();
		activity_title_text.setText(qiyiActivity.getAct_title());
		activity_location_text.setText(qiyiActivity.getAddr());
		activity_location_text.getBackground().setAlpha(120);
		activity_time_text.setText(qiyiActivity.getStart_time().substring(5)
				+ "-" + qiyiActivity.getEnd_time().substring(14));
		activity_Info.setText(qiyiActivity.getAct_content());
		activity_type_text.setText(qiyiActivity.getAct_type());
		bitmapUtils.display(Activity_UI_Back,
				URLUtil.BASE_URL + qiyiActivity.getPicture());
		Activity_UI.getBackground().setAlpha(180);
	}

	//
	public void intoActAddress(View v) {
		Intent intent = new Intent(this, ActAddressMap.class);
		intent.putExtra("coordinate", qiyiActivity.getCoordinate());
		startActivity(intent);
	}

	// 获取报名该活动的人
	public void getActMembers() {
		RequestQueue requestQueue = Volley.newRequestQueue(this);
		String url = URLUtil.BASE_URL + "servlet/ActMemberServlet" + "?act_id="
				+ act_id;
		StringRequest request = new StringRequest(url, new Listener<String>() {
			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				Gson gson = new Gson();
				Type type1 = new TypeToken<ActivityMemberInfo>() {
				}.getType();
				Type type2 = new TypeToken<List<User>>() {
				}.getType();
				Type type3 = new TypeToken<List<MemberActivity>>() {
				}.getType();
				ActivityMemberInfo bean = gson.fromJson(response, type1);
				memberList = gson.fromJson(bean.getUserInfo(), type2);
				ActMemberList = gson.fromJson(bean.getMemberInfo(), type3);
				if(memberList!=null){
					LoadMembersAvatar();// 添加用户头像
					if (CheckMyself(memberList) > 0) {
						Join_activity_btn.setText("已报名");
					}
					if (qiyiActivity.getHold_user_id() == Integer.parseInt(Myself
							.getUser_id())) {
						Join_activity_btn.setText("查看活动人员");
					}
				}

			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
			}
		});
		requestQueue.add(request);
	}

	int CheckMyself(ArrayList<User> list) {
		int i = 0;
		for (User user : list) {
			if (user.getUser_id().equals(Myself.getUser_id())) {
				i = 1;
			}

		}
		return i;

	}

	public void LoadMembersAvatar() {
		MembersAvatarlayout.setOrientation(LinearLayout.HORIZONTAL);
		members_num.setText(memberList.size() + "/" + (ActivityCount - 1)
				+ "人已报名");
		members_num.getBackground().setAlpha(150);
		members_list_num.setText(memberList.size() + "人");
		for (int i = 0; i < memberList.size(); i++) {
			MembersAvatar = new RoundImageView(ActivityActivity.this);
			LayoutParams params = new LayoutParams(110, 110);
			MembersAvatar.setClickable(true);
			MembersAvatar.setLayoutParams(params);
			final User user = memberList.get(i);
			bitmapUtils.display(MembersAvatar,
					URLUtil.BASE_URL + user.getPhoto());
			MembersAvatarlayout.addView(MembersAvatar);
			MembersAvatar.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(ActivityActivity.this,
							OtherUserActivity.class);
					intent.putExtra("otherUserInfo", (Serializable) user);
					startActivity(intent);
				}
			});
		}
		MembersScrollView.addView(MembersAvatarlayout);
	}

	public void SelectInfo() {
		ViewTreeObserver vto = ActivityInfo.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				ActivityInfo.getViewTreeObserver()
						.removeGlobalOnLayoutListener(this);
				int height = ActivityInfo.getHeight();
				if (height < 125) {
					Activity_Click_Details.setVisibility(View.GONE);
				} else {
					RelativeLayout.LayoutParams layoutparams = (RelativeLayout.LayoutParams) ActivityInfo
							.getLayoutParams();
					layoutparams.height = 125;
					ActivityInfo.setLayoutParams(layoutparams);
				}
			}
		});
	}

	// 点击查看活动详情
	public void lookOverView(View v) {
		RelativeLayout.LayoutParams layoutparams = (RelativeLayout.LayoutParams) ActivityInfo
				.getLayoutParams();
		if (activity_click_details.getText().equals("收起")) {
			activity_click_details.setText("查看详情");
			activity_Info_sign.setImageResource(R.drawable.triangle_down);
			layoutparams.height = 125;
			ActivityInfo.setLayoutParams(layoutparams);
		} else if (activity_click_details.getText().equals("查看详情")) {
			activity_click_details.setText("收起");
			activity_Info_sign.setImageResource(R.drawable.triangle_up);
			layoutparams.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
			ActivityInfo.setLayoutParams(layoutparams);
		}
	}

	// 查看用户列表
	public void activityMemberList(View v) {
		if (memberList!=null) {
			Intent intent = new Intent(ActivityActivity.this,
					MembersActivity.class);
			intent.putExtra("membersList", (Serializable) memberList);
			intent.putExtra("ActMemberList", (Serializable) ActMemberList);
			startActivity(intent);
		} else {
			Toast.makeText(ActivityActivity.this, "暂时无人报名", Toast.LENGTH_SHORT)
					.show();
		}
	}

	// 返回
	public void Activity_back(View v) {
		finish();
	}
}
