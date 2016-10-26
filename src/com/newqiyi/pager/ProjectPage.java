package com.newqiyi.pager;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.newqiyi.activity.ProDetailActivity;
import com.newqiyi.activity.R;
import com.newqiyi.adapter.QIYIAdapter;
import com.newqiyi.domain.ProsToAD;
import com.newqiyi.domain.ProsToAD.Project;
import com.newqiyi.util.CommonUtil;
import com.newqiyi.util.GsonUtil;
import com.newqiyi.util.SharePrefUitl;
import com.newqiyi.util.URLUtil;
import com.newqiyi.view.RefreshListView;
import com.newqiyi.view.RefreshListView.onRefreshListener;
import com.newqiyi.view.RollViewPager;
import com.newqiyi.view.RollViewPager.OnPagerClick;

public class ProjectPage extends BasePager {
	private View view;
	private String url = "servlet/AndroidProListAndAD";// 用于加载最初的数据
	private String moreURL = "servlet/AndroidProsOnPullUpLoad";// 加载更多的数据
	private boolean flag = true;// 判断数据库是否还有数据
	private View layout_roll_view;
	private MyAdpter adpter;

	private BitmapUtils bitmapUtils;

	// 放置顶部轮播图的线性布局
	@ViewInject(R.id.top_news_viewpager)
	private LinearLayout top_news_viewpager;
	// 放置轮播图文字的线性布局
	@ViewInject(R.id.top_news_title)
	private TextView top_news_title;
	// 放置轮播点的线性布局
	@ViewInject(R.id.dots_ll)
	private LinearLayout dots_ll;
	// 设置listView的布局
	@ViewInject(R.id.lv_item_pros)
	private RefreshListView lView;
	// 轮播图对应图片链接地址集合
	private List<String> imgUrlList = new ArrayList<String>();
	// 轮播图对应文字集合
	private List<String> titleList = new ArrayList<String>();
	// 维护轮播点的集合
	private List<View> viewList = new ArrayList<View>();
	// 项目信息的集合
	private List<Project> prosList = new ArrayList<ProsToAD.Project>();
	private int pageNo = 1;

	public static boolean status = true;// 刷新状态的

	public ProjectPage(Context context) {
		super(context);
		bitmapUtils = new BitmapUtils(context);
	}

	@Override
	public View initView() {
		// 加载轮播图
		layout_roll_view = View.inflate(context, R.layout.layout_roll_view,
				null);
		ViewUtils.inject(this, layout_roll_view);
		// 加载listView布局
		view = View.inflate(context, R.layout.pro_list_page, null);
		ViewUtils.inject(this, view);
		// 添加轮播图的数据和下拉刷新的头
		lView.addCustomHeader(layout_roll_view);
		// 设置头部上拉加载、下拉刷新监听事件
		lView.setOnRefreshListener(new onRefreshListener() {

			@Override
			public void onPullUpLoad() {
				// 上拉加载
				if (flag) {
					++pageNo;
					getData(moreURL, false);
				} else {
					Toast.makeText(context, "没有更多的数据....", Toast.LENGTH_SHORT)
							.show();
					lView.OnRefreshFinish();
				}
			}

			@Override
			public void onPullDownRefresh() {
				status = false;
				// 刷新操作
				getData(url, true);

			}
		});

		// 设置listView的点击事件
		lView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (status) {
					// // 跳转到页面详情(activity)
					Intent intent = new Intent(context, ProDetailActivity.class);
					// 将需要传递的参数传递给ProDetailActivity
					// intent.putExtra("pro_url",
					// prosList.get(position - 1).pro_url);
					intent.putExtra("target_count",
							prosList.get(position - 1).target_count);
					intent.putExtra("pre_qibi_count",
							prosList.get(position - 1).pre_qibi_count);
					intent.putExtra("pro_id", prosList.get(position - 1).pro_id);
					context.startActivity(intent);
				}
			}
		});

		return view;
	}

	@Override
	public void initData() {
		// 先从内存中读取，没有从网络上获取
		String result = SharePrefUitl.getStringData(context, URLUtil.BASE_URL
				+ url, "");
		if (!TextUtils.isEmpty(result)) {
			processData(result, true);
		}
		getData(url, true);

	}

	// 从网络上获取数据
	private void getData(final String url, final boolean b) {

		RequestParams params = new RequestParams();
		params.addBodyParameter("pageNo", pageNo + "");
		loadData(HttpMethod.POST, URLUtil.BASE_URL + url, params,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						status = true;
						// System.out.println("网络接收数据："+responseInfo.result);
						// 刷新失败、加载失败
						if (responseInfo.result.equals("false")) {
							flag = false;
							// lView.OnRefreshFinish();
						} else if (responseInfo.result.equals("RefreshFail")) {
							Toast.makeText(context, "刷新失败", Toast.LENGTH_SHORT)
									.show();
						} else {
							flag = true;
							SharePrefUitl
									.saveStringData(context, URLUtil.BASE_URL
											+ url, responseInfo.result);
							processData(responseInfo.result, b);
						}
						lView.OnRefreshFinish();
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						lView.OnRefreshFinish();
					}
				});

	}

	private void processData(String result, boolean b) {

		ProsToAD prosToAD = GsonUtil.jsonToBean(result, ProsToAD.class);
		System.out.println("项目列表数据条数" + prosToAD.data.pros.size());
		// // 加载更多的数据
		// moreURL = "servlet/AndroidProListAndAD?pageNo=" + count;
		// System.out.println(prosToAD.data.pros.get(0).pro_pic);
		// 加载图片轮播的数据
		if (prosToAD.data.ads.size() > 0) {
			if (b) {
				imgUrlList.clear();
				titleList.clear();
				for (int i = 0; i < prosToAD.data.ads.size(); i++) {
					imgUrlList.add(URLUtil.BASE_URL
							+ prosToAD.data.ads.get(i).ad_img);
					titleList.add(prosToAD.data.ads.get(i).ad_title);
				}
				// 初始点操作，第一个点默认的是红色
				initDot();
				RollViewPager rollViewPager = new RollViewPager(context,
						viewList, new OnPagerClick() {

							@Override
							public void click(String url) {
								// 点击图片处理自己的业务逻辑
							}
						});
				rollViewPager.initTitle(titleList, top_news_title);
				rollViewPager.initImgUrlList(imgUrlList);
				rollViewPager.startRoll();

				// ***先移除原有的轮播图
				top_news_viewpager.removeAllViews();
				top_news_viewpager.addView(rollViewPager);
			}
		}
		if (prosToAD.data.pros.size() > 0) {
			if (b) {
				// 刷新操作
				prosList.clear();
			}
			prosList.addAll(prosToAD.data.pros);
			// // 填充底部的ListView
			if (adpter == null) {
				adpter = new MyAdpter(context, prosList);
				lView.setAdapter(adpter);
			} else {
				// 更新listView
				adpter.notifyDataSetChanged();
			}
		}

	}

	private void initDot() {
		// 清除点
		dots_ll.removeAllViews();
		viewList.clear();
		for (int i = 0; i < imgUrlList.size(); i++) {
			View view = new View(context);
			if (i == 0) {
				view.setBackgroundResource(R.drawable.dot_focus);
			} else {
				view.setBackgroundResource(R.drawable.dot_normal);
			}
			// 可以在此处做适配（将px---->dp）
			// 设置点的大小
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					CommonUtil.dip2px(context, 6),
					CommonUtil.dip2px(context, 6));
			// 设置点的间距
			layoutParams.setMargins(CommonUtil.dip2px(context, 4), 0,
					CommonUtil.dip2px(context, 4), 0);
			dots_ll.addView(view, layoutParams);
			viewList.add(view);
		}

	}

	class MyAdpter extends QIYIAdapter<Project> {

		private String day;

		public MyAdpter(Context context, List<Project> list) {
			super(context, list);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(context, R.layout.pro_list_content,
						null);
			}
			// 项目的图片
			RelativeLayout iv_img = (RelativeLayout) convertView
					.findViewById(R.id.iv_pro_img);
			// 设置项目的状态
			RelativeLayout tv_pro_status = (RelativeLayout) convertView
					.findViewById(R.id.rl_execute);
			// 项目的标题
			TextView tv_title = (TextView) convertView
					.findViewById(R.id.tv_pro_title);
			// 项目的进度条
			ProgressBar pbBar = (ProgressBar) convertView
					.findViewById(R.id.pb_pro_plan);

			// TextView tv_progress = (TextView) convertView
			// .findViewById(R.id.tv_progress);
			// 项目的参与人数
			TextView tv_join = (TextView) convertView
					.findViewById(R.id.tv_join);
			// 项目的背景
			LinearLayout line_color = (LinearLayout) convertView
					.findViewById(R.id.ll_background);
			// 项目的参与目标
			TextView tv_goal = (TextView) convertView
					.findViewById(R.id.tv_goal);
			// 项目的参与截至时间
			TextView tv_endtime = (TextView) convertView
					.findViewById(R.id.tv_endtime);
			// 图片的加载
			bitmapUtils.display(iv_img, URLUtil.BASE_URL
					+ list.get(position).pro_pic);
			// 标题的加载
			tv_title.setText(list.get(position).pro_title);
			// 设置最大进度
			pbBar.setMax(Integer.parseInt(list.get(position).target_count));
			// 设置当前的进度
			pbBar.setProgress(Integer.parseInt(list.get(position).pre_qibi_count));
			pbBar.setVisibility(View.VISIBLE);
			int a = Integer.parseInt(list.get(position).pre_qibi_count);
			int b = Integer.parseInt(list.get(position).target_count);
			// float i = (float) (Math.round((a / b) * 100) / 100);
			// tv_progress.setText(i + "%");
			// 设置进度条的不确定模式
			pbBar.setIndeterminate(false);
			// 设置参与人数
			tv_join.setText(list.get(position).join_count + "人");
			// 设置目标启币数
			tv_goal.setText(list.get(position).target_count + "币");
			// 对时间日期的操作
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");// 小写的mm表示的是分钟
			String nowData = sdf.format(new Date());
			String endtime = list.get(position).end_time;
			try {
				java.util.Date date1 = sdf.parse(nowData);
				java.util.Date date2 = sdf.parse(endtime);
				int time = (int) ((date2.getTime() - date1.getTime()) / (24 * 60 * 60 * 1000));
				if (time > 0) {
					day = time + "";
				} else {
					day = 0 + "";
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}

			// 设置状态栏
			if (Integer.parseInt(list.get(position).target_count) <= Integer
					.parseInt(list.get(position).pre_qibi_count)) {
				tv_pro_status.setVisibility(View.VISIBLE);
				line_color.setBackgroundColor(Color.GRAY);
				tv_endtime.setText(0 + "天");

			} else {
				tv_pro_status.setVisibility(View.GONE);
				line_color.setBackgroundColor(Color.WHITE);
				tv_endtime.setText(day + "天");
			}
			return convertView;
		}

	}

}