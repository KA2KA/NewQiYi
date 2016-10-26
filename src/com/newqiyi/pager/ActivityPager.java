package com.newqiyi.pager;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.newqiyi.activity.ActivityActivity;
import com.newqiyi.activity.LoginActivity;
import com.newqiyi.activity.MyApplication;
import com.newqiyi.activity.R;
import com.newqiyi.domain.QiyiActivity;
import com.newqiyi.util.ActMapUtils;
import com.newqiyi.util.SharePrefUitl;
import com.newqiyi.util.URLUtil;
import com.newqiyi.widget.AlertDialog;
import com.qiyi.releaseactivity.ReleaseActivity;

/**
 * 
 * @author 赵文强
 * 
 */
public class ActivityPager extends BasePager implements OnClickListener,
		OnItemClickListener {
	// 活动分类
	private TextView tv_sport;
	private TextView tv_friends;
	private TextView tv_study;
	private TextView tv_music;
	private TextView tv_trip;
	private TextView tv_lecture;
	private TextView tv_others;
	private TextView tv_all;
	private TextView create_event_text;
	private String url = URLUtil.BASE_URL + "servlet/ActShowServlet?page=1";
	private HttpUtils http;
	private PullToRefreshListView refreshListView;
	private PullToRefreshListView tyPerefreshListView;
	private ListView lvShow;
	private ActAdapter adapter;
	private Intent intent;
	private BitmapUtils bitmapUtil;
	private QiyiActivity activity;
	public List<QiyiActivity> acts = new ArrayList<QiyiActivity>();
	private int result = 0;
	private int typeresult = 0;
	private String actType;
	private String typeUrl;
	private int pageNum = 2;
	private int pageMAx;
	private int size;
	private int typepageNum = 2;
	private int typepageMAx;
	private int typesize;
	ArrayList<Integer> act_ids;
	ArrayList<Integer> distances;

	public ActivityPager(Context context) {
		super(context);
	}

	@Override
	public View initView() {
		View v = View.inflate(context, R.layout.act_list, null);
		tv_sport = (TextView) v.findViewById(R.id.tv_sport);
		tv_friends = (TextView) v.findViewById(R.id.tv_friends);
		tv_study = (TextView) v.findViewById(R.id.tv_study);
		tv_music = (TextView) v.findViewById(R.id.tv_music);
		tv_trip = (TextView) v.findViewById(R.id.tv_trip);
		tv_lecture = (TextView) v.findViewById(R.id.tv_lecture);
		tv_others = (TextView) v.findViewById(R.id.tv_others);
		tv_all = (TextView) v.findViewById(R.id.tv_all);
		create_event_text = (TextView) v.findViewById(R.id.create_event_text);
		// 注册监听
		create_event_text.setOnClickListener(this);
		tv_sport.setOnClickListener(this);
		tv_friends.setOnClickListener(this);
		tv_study.setOnClickListener(this);
		tv_music.setOnClickListener(this);
		tv_trip.setOnClickListener(this);
		tv_lecture.setOnClickListener(this);
		tv_others.setOnClickListener(this);
		tv_all.setOnClickListener(this);
		bitmapUtil = new BitmapUtils(context);
		http = new HttpUtils();
		tyPerefreshListView = (PullToRefreshListView) v
				.findViewById(R.id.listview_act);
		refreshListView = (PullToRefreshListView) v
				.findViewById(R.id.listview_act);
		changeRefreshListView(tyPerefreshListView);
		changeRefreshListView(refreshListView);
		return v;
	}

	void changeRefreshListView(PullToRefreshListView RefreshListView) {
		RefreshListView.setOnItemClickListener(this);
		RefreshListView.setMode(Mode.BOTH);
		RefreshListView.getLoadingLayoutProxy(false, true).setPullLabel(
				"上拉加载更多");
		RefreshListView.getLoadingLayoutProxy(false, true).setRefreshingLabel(
				"正在加载");
		RefreshListView.getLoadingLayoutProxy(false, true).setReleaseLabel(
				"放开加载更多");
	}

	@Override
	public void initData() {
		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if (msg.what == 11) {
					Bundle bundle = msg.getData();
					ArrayList<Integer> activities_id = bundle
							.getIntegerArrayList("activities_id");
					ArrayList<Integer> distance = bundle
							.getIntegerArrayList("distance");
				}
			}
		};
		ActMapUtils actMapUtils = new ActMapUtils("查看活动", MyApplication
				.getInstance().getUser() + "", handler);
		actMapUtils.startLoaction(context);
		changeRefreshListView(refreshListView);
		sendRequest(url);
		initRefresh();
	}

	void sendRequest(String url) {
		if (result == 0) {
			pageNum = 2;
			acts.clear();
		}
		http.send(HttpRequest.HttpMethod.GET, url, null,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> resp) {
						Gson g = new Gson();
						Type type = new TypeToken<List<QiyiActivity>>() {
						}.getType();
						if (resp.result.length() > 20) {
							List<QiyiActivity> acts2 = g.fromJson(resp.result,
									type);
							pageMAx = pageNum;
							size = acts.size();
							acts.addAll(result, acts2);
							adapter = new ActAdapter(acts, context);
							if (lvShow != null) {
								lvShow = null;
								changeRefreshListView(refreshListView);

							}
							lvShow = refreshListView.getRefreshableView();
							lvShow.setAdapter(adapter);
							refreshListView.setSelected(true);
							adapter.notifyDataSetChanged();
							if (size > 7) {
								lvShow.setSelection(size - 1);
							} else {
								lvShow.setSelection(size);
							}
						} else {
							pageNum = pageMAx;
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {

					}
				});
	}

	public void initRefresh() {
		refreshListView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {
					// 下拉刷新
					@Override
					public void onPullDownToRefresh(

					PullToRefreshBase<ListView> refreshView) {
						pageNum = 2;
						result = 0;
						new FinishRefresh(URLUtil.BASE_URL
								+ "servlet/ActShowServlet?page=1").execute();

					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						result = acts.size();
						url = url.substring(0, url.length() - 1) + pageNum;
						new FinishRefresh(url).execute();
						pageNum++;
					}
				});
	}

	public void initTypeRefresh() {

		typeUrl = URLUtil.BASE_URL + "servlet/ActShowTypeServlet?type="
				+ actType + "&page=1";
		tyPerefreshListView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {
					// 下拉刷新
					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						typeresult = 0;
						typepageNum = 2;
						new FinishTypeRefresh(URLUtil.BASE_URL
								+ "servlet/ActShowTypeServlet?type=" + actType
								+ "&page=1").execute();
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						typeresult = acts.size();
						typeUrl = typeUrl.substring(0, typeUrl.length() - 1)
								+ typepageNum;
						new FinishTypeRefresh(typeUrl).execute();
						typepageNum++;
					}
				});
	}

	// ActAdapter
	class ActAdapter extends BaseAdapter {
		private List<QiyiActivity> actList;
		private Context context;
		HttpUtils httpUtils;
		private View Activity_list_Back;
		private View activity_actlist_posterback;
		private String actCoordinate;// 活动坐标

		public ActAdapter(List<QiyiActivity> data, Context context) {
			this.actList = data;
			this.context = context;
		}

		public List<QiyiActivity> getActList() {
			return actList;
		}

		public void setActList(List<QiyiActivity> actList) {
			this.actList = actList;
		}

		@Override
		public int getCount() {
			return actList.size();
		}

		@Override
		public Object getItem(int position) {
			return actList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("ResourceAsColor")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			activity = actList.get(position);
			View view = null;
			ViewHolder hold = null;
			if (convertView == null) {
				hold = new ViewHolder();
				view = View.inflate(context, R.layout.activity_actlist, null);
				hold.title = (TextView) view
						.findViewById(R.id.activity_list_title);
				hold.actTime = (TextView) view
						.findViewById(R.id.activity_list_time);
				hold.type = (TextView) view
						.findViewById(R.id.activity_list_type);
				hold.distance = (TextView) view
						.findViewById(R.id.activity_list_distance);
				hold.activity_member_num = (TextView) view
						.findViewById(R.id.activity_list_member_num);
				view.setTag(hold);
			} else {
				view = convertView;
				hold = (ViewHolder) view.getTag();
			}
			Activity_list_Back = view.findViewById(R.id.activity_actlist_back);
			activity_actlist_posterback = view
					.findViewById(R.id.activity_actlist_posterback);
			activity_actlist_posterback.getBackground().setAlpha(150);
			String imgUrl = URLUtil.BASE_URL + activity.getPicture();
			// 显示活动图片
			bitmapUtil.display(Activity_list_Back, imgUrl);
			hold.title.setText(activity.getAct_title());
			hold.actTime.setText(activity.getStart_time().substring(0, 3)
					+ activity.getStart_time().substring(8));
			hold.activity_member_num.setText(activity.getAct_status() + 1 + "/"
					+ activity.getJoin_count() + "人");
			if (activity.getAct_type().equals("体育运动")) {
				hold.type.setTextColor(android.graphics.Color
						.parseColor("#339966"));
			} else if (activity.getAct_type().equals("交友聚会")) {
				hold.type.setTextColor(android.graphics.Color
						.parseColor("#3333ff"));
			} else if (activity.getAct_type().equals("学习交流")) {
				hold.type.setTextColor(android.graphics.Color
						.parseColor("#cc9933"));
			} else if (activity.getAct_type().equals("音乐戏剧")) {
				hold.type.setTextColor(android.graphics.Color
						.parseColor("#006699"));
			} else if (activity.getAct_type().equals("户外旅行")) {
				hold.type.setTextColor(android.graphics.Color
						.parseColor("#ffff66"));
			} else if (activity.getAct_type().equals("讲座公益")) {
				hold.type.setTextColor(android.graphics.Color
						.parseColor("#ff6666"));
			} else if (activity.getAct_type().equals("其他")) {
				hold.type.setTextColor(android.graphics.Color
						.parseColor("#006666"));
			}
			hold.type.getBackground().setAlpha(120);
			hold.type.setText(activity.getAct_type());

			return view;
		}
	}

	class ViewHolder {
		TextView actTime;
		TextView type;
		TextView title;
		TextView distance;
		TextView activity_member_num;
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

	private class FinishRefresh extends AsyncTask<Void, Void, Void> {
		String url;

		public FinishRefresh(String url) {
			this.url = url;
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			sendRequest(url);
			refreshListView.onRefreshComplete();
		}
	}

	private class FinishTypeRefresh extends AsyncTask<Void, Void, Void> {
		String url;

		public FinishTypeRefresh(String url) {
			this.url = url;
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			sendTypeToGet(url);
			tyPerefreshListView.onRefreshComplete();
		}
	}

	// sendTypeToGet分类获取服务器上的活动列表数据
	public void sendTypeToGet(String url) {
		if (typeresult == 0) {
			typepageNum = 2;
			acts.clear();
		}
		http.send(HttpRequest.HttpMethod.GET, url, null,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> resp) {
						Gson g = new Gson();
						Type type = new TypeToken<List<QiyiActivity>>() {
						}.getType();
						if (resp.result.length() > 20) {
							List<QiyiActivity> acts2 = g.fromJson(resp.result,
									type);
							typepageMAx = typepageNum;
							typesize = acts.size();
							acts.addAll(typeresult, acts2);
							adapter = new ActAdapter(acts, context);
							if (lvShow != null) {
								lvShow = null;
								changeRefreshListView(tyPerefreshListView);
							}
							lvShow = tyPerefreshListView.getRefreshableView();
							lvShow.setAdapter(adapter);
							refreshListView.setSelected(true);
							adapter.notifyDataSetChanged();
							if (typesize > 7) {
								lvShow.setSelection(typesize - 1);
							} else {
								lvShow.setSelection(typesize);
							}

						} else {
							typepageNum = typepageMAx;
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {

					}
				});
	}

	// 跳转到活动详情界面
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(context, ActivityActivity.class);
		intent.putExtra("activityInfo", (Serializable) acts.get(position - 1));
		context.startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		String typeurl = URLUtil.BASE_URL + "servlet/ActShowTypeServlet?page=1"
				+ "&type=";
		switch (v.getId()) {
		case R.id.create_event_text:

			String trim = SharePrefUitl.getStringData(context, "UserData", "")
					.trim();
			if (!TextUtils.isEmpty(trim)) {
				intent = new Intent(context, ReleaseActivity.class);
				context.startActivity(intent);
			} else {
				showAlertDialog();
			}

			break;
		// 选择分类（.....）
		case R.id.tv_sport:
			typeresult = 0;
			try {
				actType = URLEncoder.encode(URLEncoder.encode("体育运动", "utf-8"),
						"utf-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			initTypeRefresh();
			sendTypeToGet(typeurl + actType);
			break;
		case R.id.tv_friends:
			typeresult = 0;
			try {
				actType = URLEncoder.encode(URLEncoder.encode("交友聚会", "utf-8"),
						"utf-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			initTypeRefresh();
			sendTypeToGet(typeurl + actType);
			break;
		case R.id.tv_study:
			typeresult = 0;
			try {
				actType = URLEncoder.encode(URLEncoder.encode("学习交流", "utf-8"),
						"utf-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			initTypeRefresh();
			sendTypeToGet(typeurl + actType);
			break;
		case R.id.tv_music:
			typeresult = 0;
			try {
				actType = URLEncoder.encode(URLEncoder.encode("音乐戏剧", "utf-8"),
						"utf-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			initTypeRefresh();
			sendTypeToGet(typeurl + actType);
			break;
		case R.id.tv_trip:
			typeresult = 0;
			try {
				actType = URLEncoder.encode(URLEncoder.encode("户外旅行", "utf-8"),
						"utf-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			initTypeRefresh();
			sendTypeToGet(typeurl + actType);
			break;
		case R.id.tv_lecture:
			typeresult = 0;
			try {
				actType = URLEncoder.encode(URLEncoder.encode("讲座公益", "utf-8"),
						"utf-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			initTypeRefresh();
			sendTypeToGet(typeurl + actType);
			break;
		case R.id.tv_others:
			typeresult = 0;
			try {
				actType = URLEncoder.encode(URLEncoder.encode("其他", "utf-8"),
						"utf-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			initTypeRefresh();
			sendTypeToGet(typeurl + actType);
			break;
		case R.id.tv_all:
			result = 0;
			initRefresh();
			sendRequest(URLUtil.BASE_URL + "servlet/ActShowServlet?page=1");
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