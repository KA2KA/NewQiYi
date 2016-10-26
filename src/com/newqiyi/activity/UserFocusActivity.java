package com.newqiyi.activity;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.newqiyi.domain.User;
import com.newqiyi.util.URLUtil;
import com.newqiyi.view.RoundImageView;

/**
 * 
 * @author 赵文强
 * 
 */
public class UserFocusActivity extends Activity implements OnItemClickListener {
	private List<User> list;
	private MyAdapter myAdapter;
	private PullToRefreshListView refreshListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_focus);
		refreshListView = (PullToRefreshListView) findViewById(R.id.User_cares_freshList);
		initData();
		refreshListView.setOnItemClickListener(this);
		refreshListView.setMode(Mode.PULL_FROM_START);

		refreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				initData();
			}
		});
	}

	public void initData() {
		Intent intent = getIntent();
		User user = (User) intent.getSerializableExtra("otherusercares");
		HttpUtils httpUtils = new HttpUtils();
		String url = URLUtil.BASE_URL + "servlet/CaresByIdServlet?user_id="
				+ user.getUser_id();
		httpUtils.send(HttpMethod.GET, url, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(UserFocusActivity.this, "加载失败,请重试",
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				Gson gson = new Gson();
				Type type = new TypeToken<List<User>>() {
				}.getType();
				System.out.println("fance" + arg0.result);
				list = gson.fromJson(arg0.result, type);
				myAdapter = new MyAdapter(list, getBaseContext());
				refreshListView.setAdapter(myAdapter);
			}
		});
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
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.activity_members_list,
						null);
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
			for (User user : list) {
				System.out.println(">>>>>>>>>>>>>>>>" + user);
			}
			if (list.get(position).getPhoto() != null) {
				bitmapUtils.display(holder.avatar,
						URLUtil.BASE_URL + list.get(position).getPhoto());
			} else {
				holder.avatar.setImageResource(R.drawable.defaultavatar);
			}
			holder.name.setText(list.get(position).getUser_name());
			// 生日换算成年龄
			String Birth = list.get(position).getBirthday().substring(0, 4);
			String NowTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
					.format(new Date()).substring(0, 4);
			int age = Integer.parseInt(NowTime) - Integer.parseInt(Birth);
			holder.age.setText(age + "岁");
			holder.intro.setText(list.get(position).getSignname());
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

	public void UserCaresBack(View v) {
		finish();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(UserFocusActivity.this,
				OtherUserActivity.class);
		intent.putExtra("otherUserInfo", (Serializable) list.get(position - 1));
		startActivity(intent);
	}
}
