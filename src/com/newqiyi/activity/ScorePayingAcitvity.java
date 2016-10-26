package com.newqiyi.activity;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.newqiyi.domain.RecordScore;
import com.newqiyi.domain.RecordScore.Dons;
import com.newqiyi.domain.User;
import com.newqiyi.util.GsonUtil;
import com.newqiyi.util.URLUtil;

/**
 * @author KaKa
 * @E-mail:wuwanggao@163.com
 * @version 创建时间：2015-11-17 下午8:20:02
 */
public class ScorePayingAcitvity extends Activity implements OnClickListener {
	String user_id;

	private ListView lv_sp1;

	private MyAdapterPay madapter = null;
	private User user;

	// 捐赠流水
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_score);
		lv_sp1 = (ListView) findViewById(R.id.lv_sp1);
		user = (User) getIntent().getSerializableExtra("user");
		initTitleBar();
		initData();

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
		txt_title.setText("捐赠记录");
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

	private void initData() {
		String url = "AndroidDonateRecord?user_id=" + user.getUser_id();
		new HttpUtils().send(HttpMethod.GET, URLUtil.BASE_URL + url,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {

					}

					@Override
					public void onSuccess(ResponseInfo<String> info) {
						System.out.println("拿到数据" + info.result);
						if (!TextUtils.isEmpty(info.result)) {
							dealData(info.result);
						}
					}

				});

	}

	private void dealData(String result) {
		RecordScore bean = GsonUtil.jsonToBean(result, RecordScore.class);
		if (bean.don.size() > 0) {
			System.out.println("~~~" + bean.don.get(0).don_id);
			if (madapter == null) {
				madapter = new MyAdapterPay(bean.don, bean.titles,
						ScorePayingAcitvity.this);
				lv_sp1.setAdapter(madapter);
			} else {
				madapter.notifyDataSetChanged();
			}

		}
	}

	class MyAdapterPay extends BaseAdapter {
		List<Dons> don;
		List<String> titles;
		Context context;

		public MyAdapterPay(List<Dons> don, List<String> titles, Context context) {
			this.don = don;
			this.titles = titles;
			this.context = context;
		}

		@Override
		public int getCount() {
			return don.size();
		}

		@Override
		public Object getItem(int position) {
			return don.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(context,
						R.layout.activity_record_score, null);
			}

			for (Dons dons : don) {
				System.out.println("~" + dons.don_id);
			}
			TextView title = (TextView) convertView
					.findViewById(R.id.tv_score1);
			TextView count = (TextView) convertView
					.findViewById(R.id.tv_score2);

			title.setText(titles.get(position));
			count.setText(don.get(position).don_qb_count);
			return convertView;
		}

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.imgbtn_left) {
			finish();
		}

	}

}
