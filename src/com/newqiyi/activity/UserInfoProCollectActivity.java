package com.newqiyi.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.newqiyi.domain.ProCollection;
import com.newqiyi.domain.User;
import com.newqiyi.util.GsonUtil;
import com.newqiyi.util.URLUtil;
import com.newqiyi.view.CircleImageView;
import com.newqiyi.widget.AlertDialog;

/*
 * @author 巩文婷 &吴旺高
 * @E-mail:1763623356@qq.com
 * @version 创建时间：${date} ${time} 项目收藏列表
 */
public class UserInfoProCollectActivity extends Activity implements
		OnItemClickListener, OnClickListener {

	private MyAdapter adapter;

	private String url = "servlet/AndroidCollectionShowServlet";

	private String url2 = "servlet/AndroidColDeleteServlet";

	private ImageView back;

	private PullToRefreshListView lView;

	private ProCollection prosCol;

	private User user;
	private BitmapUtils bitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userinfo_pro_collect);
		bitmap = new BitmapUtils(this);
		initView();
		initData();
	}

	private void initView() {

		lView = (PullToRefreshListView) findViewById(R.id.lv_procol);
		back = (ImageView) findViewById(R.id.back_personal_info);
		user = (User) getIntent().getSerializableExtra("user");
	}

	private void initData() {
		lView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				getData();
				new FinishRefresh().execute();
			}
		});

		back.setOnClickListener(this);
		lView.setOnItemClickListener(this);
		getData();
		// 访问网络拿到收藏信息

	}

	private void getData() {

		HttpUtils hUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("user_id", user.getUser_id());
		hUtils.send(HttpMethod.POST, URLUtil.BASE_URL + url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
					}

					@Override
					public void onSuccess(ResponseInfo<String> info) {
						System.out.println("收藏++" + info.result);

						if (!TextUtils.isEmpty(info.result)) {
							ProcessData(info.result);

							lView.onRefreshComplete();
						} else {
							Toast.makeText(getApplicationContext(), "访问服务器异常",
									Toast.LENGTH_LONG).show();
						}
					}

				});
	}

	private void ProcessData(String result) {
		prosCol = GsonUtil.jsonToBean(result, ProCollection.class);

		if (prosCol != null) {
			adapter = new MyAdapter(this, prosCol);
			lView.setAdapter(adapter);
			adapter.notifyDataSetChanged(); // 更新listView
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.back_personal_info) {
			finish();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		// // 跳转到页面详情(activity)
		Intent intent = new Intent(this, ProDetailActivity.class);
		// 将需要传递的参数传递给ProDetailActivity
		intent.putExtra("target_count",
				prosCol.data.get(position - 1).target_count);
		intent.putExtra("pre_qibi_count",
				prosCol.data.get(position - 1).pre_qibi_count);
		intent.putExtra("pro_id", prosCol.data.get(position - 1).pro_id);
		startActivity(intent);
	}

	// // 判断是否要删除收藏
	// public void showAlertDialog(final int position) {
	//
	// new AlertDialog(UserInfoProCollectActivity.this).builder()
	// .setTitle("提醒").setMsg("取消报名吗")
	// .setPositiveButton("确认", new OnClickListener() {
	// @Override
	// public void onClick(View v) {
	// HttpUtils hUtils = new HttpUtils();
	// RequestParams params = new RequestParams();
	// params.addBodyParameter("user_id", user.getUser_id());
	// params.addBodyParameter("pro_id",
	// prosCol.data.get(position).pro_id);
	// hUtils.send(HttpMethod.POST, URLUtil.BASE_URL + url2,
	// params, new RequestCallBack<String>() {
	//
	// @Override
	// public void onFailure(HttpException arg0,
	// String arg1) {
	// }
	//
	// @Override
	// public void onSuccess(
	// ResponseInfo<String> info) {
	// if (info.result != null) {
	// Toast.makeText(
	// getApplicationContext(),
	// "删除成功", 0).show();
	// } else {
	// Toast.makeText(
	// getApplicationContext(),
	// "删除失败", 0).show();
	// }
	// }
	// });
	// }
	// }).setNegativeButton("取消", new OnClickListener() {
	// @Override
	// public void onClick(View v) {
	// }
	// }).show();
	//
	// }

	// listView 的布局
	class MyAdapter extends BaseAdapter {
		Context context;
		ProCollection pCollection;
		private PullToRefreshListView lView;

		public MyAdapter(Context context, ProCollection prosCol) {
			this.context = context;
			pCollection = prosCol;

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(context, R.layout.pro_col, null);
			}

			CircleImageView im_photo = (CircleImageView) convertView
					.findViewById(R.id.civ_col);
			TextView tv_collect = (TextView) convertView
					.findViewById(R.id.tv_procol);
			TextView tv_coltime = (TextView) convertView
					.findViewById(R.id.tv_coltime);
			// 收藏表
			System.out.println("收场表" + pCollection.data.get(position));
			if (pCollection.data.get(position).pro_pic != null) {
				String Url = URLUtil.BASE_URL
						+ pCollection.data.get(position).pro_pic;
				bitmap.display(im_photo, Url);
			} else {
				im_photo.setImageResource(android.R.color.darker_gray);
			}
			tv_collect.setText(pCollection.data.get(position).pro_title);
			String time = pCollection.datetime.get(position);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String today = sdf.format(new Date());
			java.util.Date date1;
			java.util.Date date2;
			try {
				date1 = sdf.parse(today);
				date2 = sdf.parse(time);
				int day = (int) (date1.getTime() - date2.getTime())
						/ (24 * 60 * 60 * 1000);
				tv_coltime.setText(day + "天前");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return convertView;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return pCollection.data.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
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
			// adapter.notifyDataSetChanged();
			lView.onRefreshComplete();
		}
	}
}
