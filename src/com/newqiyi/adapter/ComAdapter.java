package com.newqiyi.adapter;

import java.util.List;

import com.lidroid.xutils.BitmapUtils;
import com.newqiyi.activity.R;
import com.newqiyi.domain.CommentBean;
import com.newqiyi.util.URLUtil;
import com.newqiyi.view.RoundImageView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 评论列表的适配器
 * 
 * @author 李晶
 * 
 */
public class ComAdapter extends BaseAdapter {
	private List<CommentBean> beans;
	private Context context;
	private BitmapUtils bitmapUtil;
	//private HttpUtils http = new HttpUtils();

	//private PraiseUtil praUtil = new PraiseUtil();

	//private int user_id;
	private CommentBean bean;

	public ComAdapter(List<CommentBean> bean, Context context) {
		this.beans = bean;
		this.context = context;

		bitmapUtil = new BitmapUtils(context);
		/*// 获取当前用户id
		String UserData = SharePrefUitl.getStringData(context, "UserData", "");
		if (!TextUtils.isEmpty(UserData)) {
			User user = GsonUtil.jsonToBean(UserData, User.class);
			user_id = Integer.parseInt(user.getUser_id());
		}*/

	}

	@Override
	public int getCount() {
		return beans.size();
	}

	@Override
	public Object getItem(int position) {
		return beans.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		bean = beans.get(position);
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.listview_com_item, null);
			holder.user_log = (RoundImageView) convertView.findViewById(R.id.image_log);
			holder.tv_level = (TextView) convertView.findViewById(R.id.tv_level);
			holder.user_name = (TextView) convertView.findViewById(R.id.tv_username);
			holder.com_content = (TextView) convertView.findViewById(R.id.tv_content);
			holder.com_time = (TextView) convertView.findViewById(R.id.tv_comtime);
			holder.com_lou = (TextView) convertView.findViewById(R.id.tv_lou);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// 获取图片的绝对路径
		String imgUrl = URLUtil.BASE_URL + bean.getUser().getPhoto();
		// 显示网络图片
		bitmapUtil.display(holder.user_log, imgUrl);

		holder.tv_level.setText("Lv:" + bean.getUser().getUser_level());
		holder.user_name.setText(bean.getUser().getUser_name());
		
		/*
		final int com_id = bean.getCp().getCom_id();
		final Praise pra = new Praise(user_id, com_id);

		*//**
		 * 点赞
		 *//*
		final ImageView iv_praise = (ImageView) convertView.findViewById(R.id.iv_praise);
		final ImageView iv_praise2 = (ImageView) convertView.findViewById(R.id.iv_praise2);
		*//**
		 * 显示赞
		 *//*
		String url = URLUtil.BASE_URL + "servlet/PraiseShowServlet?user_id=" + user_id;
		http.send(HttpMethod.GET, url, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException error, String msg) {
				Toast.makeText(context, error.getMessage() + ":" + msg, Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSuccess(ResponseInfo<String> resp) {
				String jsonpras = resp.result;
				Gson g = new Gson();
				Type classOfT = new TypeToken<List<Praise>>() {
				}.getType();
				List<Praise> pras = g.fromJson(jsonpras, classOfT);
				for (Praise praise : pras) {
					if (praise.getCom_id() == bean.getCp().getCom_id()) {
						iv_praise.setVisibility(View.GONE);
						iv_praise2.setVisibility(View.VISIBLE);
					} else {
						iv_praise2.setVisibility(View.GONE);
						iv_praise.setVisibility(View.VISIBLE);
					}

				}
			}
		});

		// 点赞

		iv_praise.setTag(R.id.iv_praise, position);
		iv_praise2.setTag(R.id.iv_praise2, position);

		// 点赞
		iv_praise.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				iv_praise.setVisibility(View.GONE);
				iv_praise2.setVisibility(View.VISIBLE);

				// 插入
				praUtil.insertPraise(http, context, pra);

			}
		});
		// 取消赞
		iv_praise2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				iv_praise2.setVisibility(View.GONE);
				iv_praise.setVisibility(View.VISIBLE);
				// 删除
				praUtil.deletePraise(http, context, user_id, com_id);
			}

		});
		*/
		holder.com_lou.setText(position +1+ "楼");
		if (bean.getCp().getReply_com_id() > 0) {
			for (int i = 0; i < beans.size(); i++) {
				if (bean.getCp().getReply_com_id() == beans.get(i).getCp().getCom_id()) {
					holder.com_content.setText("回复" + (++i) + "楼:" + bean.getCp().getCom_content());
				}
			}
		} else {
			holder.com_content.setText(bean.getCp().getCom_content());
		}

		holder.com_time.setText(bean.getCp().getCom_time().substring(5, 16));

		return convertView;
	}

	public static class ViewHolder {
		RoundImageView user_log;
		TextView tv_level;
		TextView user_name;
		TextView com_content;
		TextView com_time;
		TextView com_lou;
	}

}
