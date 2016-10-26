package com.newqiyi.activity;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.BitmapUtils;
import com.newqiyi.domain.MemberActivity;
import com.newqiyi.domain.User;
import com.newqiyi.util.URLUtil;
import com.newqiyi.view.RoundImageView;

public class MembersActivity extends Activity implements OnItemClickListener{
	private String url = URLUtil.BASE_URL + "servlet/ActInsertServlet";
	private PullToRefreshListView freshList;
	private List<User> list;
	private List<MemberActivity> ActMemberList;
	private MyAdapter myAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_members);
		freshList = (PullToRefreshListView) findViewById(R.id.Activity_member_freshList);
		LoadMembersIntro();
		freshList.setMode(Mode.PULL_FROM_START);
		freshList.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
			}
		});
	}

	@SuppressWarnings("unchecked")
	void LoadMembersIntro() {
		Intent intent = getIntent();
		list = (ArrayList<User>) intent.getSerializableExtra("membersList");
		if(list!=null){
			myAdapter = new MyAdapter(list, MembersActivity.this);
			freshList.setAdapter(myAdapter);
		}
		
		ActMemberList = (ArrayList<MemberActivity>) intent
				.getSerializableExtra("ActMemberList");
	}

	// 设置返回键动作：返回上一层界面
	public void ActivityMembersBack(View v) {
		Runtime runtime = Runtime.getRuntime();
		try {
			runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class FinishRefresh extends AsyncTask<Void, Void, Void> {
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
			freshList.onRefreshComplete();
		}
	}

	class MyAdapter extends BaseAdapter {
		private List<User> list;
		private LayoutInflater mInflater;
		private ViewHolder holder;
		private Context context;
		private BitmapUtils bitmapUtils;

		public MyAdapter(List<User> list, Context context) {
			super();
			this.list = list;
			this.context = context;
			bitmapUtils = new BitmapUtils(context);
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.activity_members_list,
						null);
				holder = new ViewHolder();
				holder.avatar = (RoundImageView) convertView
						.findViewById(R.id.member_avatar);
				holder.name = (TextView) convertView
						.findViewById(R.id.member_name);
				holder.sex = (ImageView) convertView
						.findViewById(R.id.member_sex);
				holder.age = (TextView) convertView
						.findViewById(R.id.member_age);
				holder.intro = (TextView) convertView
						.findViewById(R.id.member_intro);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			bitmapUtils.display(holder.avatar,
					URLUtil.BASE_URL + list.get(position).getPhoto());
			holder.name.setText(list.get(position).getUser_name());
			// 生日换算成年龄
			String Birth = list.get(position).getBirthday().substring(0, 4);
			String NowTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
					.format(new Date()).substring(0, 4);
			int age = Integer.parseInt(NowTime) - Integer.parseInt(Birth);
			holder.age.setText(age + "岁");
			holder.intro.setText(ActMemberList.get(position).getJoin_intro());
			if (list.get(position).getSex().equals("男")) {
				holder.sex.setBackgroundResource(R.drawable.male_icon);
			} else {
				holder.sex.setBackgroundResource(R.drawable.female_icon);
			}
			return convertView;
		}

		class ViewHolder {
			RoundImageView avatar;
			ImageView sex;
			TextView name;
			TextView age;
			TextView intro;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(MembersActivity.this,
				OtherUserActivity.class);
		intent.putExtra("otherUserInfo", (Serializable) list.get(position-1));
		startActivity(intent);
	}
}
