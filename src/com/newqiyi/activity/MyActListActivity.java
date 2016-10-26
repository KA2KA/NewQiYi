package com.newqiyi.activity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.newqiyi.domain.QiyiActivity;
import com.newqiyi.domain.User;
import com.newqiyi.util.URLUtil;

public class MyActListActivity extends Activity implements OnClickListener {
	private ArrayList<QiyiActivity> myActs;
	private PullToRefreshListView freshList;
	private ActAdapter actAdapter;
	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_act_list);
		initTitleBar();
		freshList = (PullToRefreshListView) findViewById(R.id.user_act_list);
		initData();
	}

	public void initData() {
		Intent intent = getIntent();
		user = (User) intent.getSerializableExtra("user");
		HttpUtils httpUtils = new HttpUtils();
		String url = URLUtil.BASE_URL + "servlet/UserActListServlet?user_id="
				+ user.getUser_id();
		httpUtils.send(HttpMethod.GET, url, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				Gson gson = new Gson();
				Type type = new TypeToken<List<QiyiActivity>>() {
				}.getType();
				myActs = gson.fromJson(arg0.result, type);
				System.out.println("MyActListActivity>>>>>>" + arg0.result);
				actAdapter = new ActAdapter(myActs, MyActListActivity.this);
				freshList.setMode(Mode.PULL_FROM_START);
				freshList.setAdapter(actAdapter);
			}
		});

		freshList.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				new FinishRefresh().execute();
			}
		});
	}

	private class FinishRefresh extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			try {
				Thread.sleep(1000);
				initData();
			} catch (InterruptedException e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			freshList.onRefreshComplete();
		}
	}

	class ActAdapter extends BaseAdapter {
		private List<QiyiActivity> actList;
		private Context context;
		HttpUtils httpUtils;
		BitmapUtils bitmapUtils;
		private View Activity_list_Back;
		private View activity_actlist_posterback;
		public ActAdapter(List<QiyiActivity> data, Context context) {
			this.actList = data;
			this.context = context;
			bitmapUtils = new BitmapUtils(MyActListActivity.this);
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

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			QiyiActivity activity = actList.get(position);
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
				view.setTag(hold);
			} else {
				view = convertView;
				hold = (ViewHolder) view.getTag();
			}
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
			Activity_list_Back = view
					.findViewById(R.id.activity_actlist_back);
			activity_actlist_posterback = view
					.findViewById(R.id.activity_actlist_posterback);
			activity_actlist_posterback.getBackground().setAlpha(150);
			hold.title.setText(activity.getAct_title());
			hold.actTime.setText(activity.getStart_time().substring(5) + "-"
					+ activity.getEnd_time().substring(14));
			hold.type.setText(activity.getAct_type());
			String imgUrl = URLUtil.BASE_URL + activity.getPicture();
			// 显示活动图片
			bitmapUtils.display(Activity_list_Back, imgUrl);
			return view;
		}
	}

	class ViewHolder {
		ImageView poster;
		TextView actTime;
		TextView type;
		TextView title;
		TextView address;
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
		txt_title.setText("我的活动");
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

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.imgbtn_left) {
			finish();
		}

	}

}
