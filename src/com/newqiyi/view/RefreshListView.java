package com.newqiyi.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.newqiyi.activity.R;
import com.newqiyi.pager.ProjectPage;
import com.newqiyi.util.CommonUtil;

/**
 * 
 * @author kaka
 * @E-mail:wuwanggao@163.com
 * @version 创建时间：2015-10-20 下午3:35:58 下拉刷新
 */

public class RefreshListView extends ListView implements OnScrollListener {
	private View viewHeader;
	private LinearLayout refresh_listview_header_root;
	private LinearLayout refresh_listview_header_view;
	private ImageView refresh_listview_header_arr;
	private ProgressBar refresh_listview_header_progressbar;
	private TextView refresh_listview_header_tv;
	private TextView refresh_listview_header_time;
	private View customHeader;
	private RotateAnimation upAnimation;
	private RotateAnimation downAnimation;
	private int downY = -1;
	private int headerHeight;

	private int CURRENTOPTION = PULL_DONW;
	private int mFfirstVisibleItem = -1;
	private onRefreshListener refreshListener;

	private static final int PULL_DONW = 0;
	private static final int RELEASE_REFRESH = 1;
	private static final int IS_REFRESH = 2;
	private static final String tag = "RefreshListView";
	private View viewFooter;
	private int footerHeight;
	private boolean isLoading = false;

	public RefreshListView(Context context) {
		super(context);
		initHeader();
		initFooter();
		setOnScrollListener(this);
	}

	private void initFooter() {
		viewFooter = View.inflate(getContext(), R.layout.londing_pull_on, null);

		viewFooter.measure(0, 0);
		footerHeight = viewFooter.getMeasuredHeight();
		Log.i(tag, "footerHeight = " + footerHeight);
		viewFooter.setPadding(0, -footerHeight, 0, 0);
		this.addFooterView(viewFooter);
	}

	public RefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initHeader();
		initFooter();
		setOnScrollListener(this);
	}

	/*
	 * 初始化头布局
	 */
	private void initHeader() {

		// 布局
		viewHeader = View.inflate(getContext(),
				R.layout.refresh_listview_header, null);
		// 整个的下拉刷新头
		refresh_listview_header_root = (LinearLayout) viewHeader
				.findViewById(R.id.refresh_listview_header_root);

		refresh_listview_header_view = (LinearLayout) viewHeader
				.findViewById(R.id.refresh_listview_header_view);
		// 图片状态
		refresh_listview_header_arr = (ImageView) viewHeader
				.findViewById(R.id.refresh_listview_header_arr);
		// 进度条
		refresh_listview_header_progressbar = (ProgressBar) viewHeader
				.findViewById(R.id.refresh_listview_header_progressbar);
		// 刷新文字
		refresh_listview_header_tv = (TextView) viewHeader
				.findViewById(R.id.refresh_listview_header_tv);
		// 时间
		refresh_listview_header_time = (TextView) viewHeader
				.findViewById(R.id.refresh_listview_header_time);

		// 测量当前控件的一个高度
		refresh_listview_header_view.measure(0, 0);
		headerHeight = refresh_listview_header_view.getMeasuredHeight();
		// 将当前对应得头去做隐藏操作
		refresh_listview_header_view.setPadding(0, -headerHeight, 0, 0);
		// 作为头添加进来(添加完成的头文件)
		this.addHeaderView(viewHeader);
		initAnimation();
	}

	public interface onRefreshListener {
		// 下拉刷新
		public void onPullDownRefresh();

		// 上拉加载
		public void onPullUpLoad();
	}

	public void setOnRefreshListener(onRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
	}

	// 收起加载更多的布局
	public void OnRefreshFinish() {
		// 如果还是正在刷新状态，隐藏指定控件，并且做状态的转变，让下一次刷新操作可以
		if (CURRENTOPTION == IS_REFRESH) {
			refresh_listview_header_view.setPadding(0, -headerHeight, 0, 0);
			refresh_listview_header_arr.setVisibility(View.VISIBLE);
			refresh_listview_header_progressbar.setVisibility(View.INVISIBLE);
			refresh_listview_header_tv.setText("下拉刷新");
			CURRENTOPTION = PULL_DONW;
		} else if (isLoading) {
			isLoading = false;
			viewFooter.setPadding(0, -footerHeight, 0, 0);
		}
	}

	private void initAnimation() {
		upAnimation = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF,
				0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		upAnimation.setDuration(500);
		upAnimation.setFillAfter(true);

		downAnimation = new RotateAnimation(-180, -360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		downAnimation.setDuration(500);
		downAnimation.setFillAfter(true);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		// 点击事件
		case MotionEvent.ACTION_DOWN:
			downY = (int) ev.getY();
			break;
		// 移动事件
		case MotionEvent.ACTION_MOVE:
			if (CURRENTOPTION == IS_REFRESH) {
				break;
			}
			if (downY == -1) {
				downY = (int) ev.getY();
			}
			int moveY = (int) ev.getY();
			int padding = -headerHeight + moveY - downY;

			int[] listViewlocation = new int[2];
			this.getLocationOnScreen(listViewlocation);

			// 获取当前listView头所在Y轴位置
			int listViewY = listViewlocation[1];

			int[] headerlocation = new int[2];
			customHeader.getLocationOnScreen(headerlocation);
			// 获取当前customHeader(轮播图)头所在Y轴位置
			int headerY = headerlocation[1];

			if (headerY < listViewY) {
				break;
			}

			if (padding > -headerHeight && mFfirstVisibleItem == 0) {
				// 判断是否下拉拖拽出头的的操作
				if (padding > 0 && CURRENTOPTION == PULL_DONW) {
					// padding>0刷新头完成出来 (准备刷新)
					CURRENTOPTION = RELEASE_REFRESH;
					setcurretOption();
					Log.i(tag, "释放刷新");
				} else if (padding < 0 && CURRENTOPTION == RELEASE_REFRESH) {
					// padding<0刷新头没有完成出来 (下拉刷新)
					Log.i(tag, "下拉刷新");
					CURRENTOPTION = PULL_DONW;
					setcurretOption();
				}
				refresh_listview_header_view.setPadding(0, padding, 0, 0);
				return true;
			}
			break;
		// 离开事件
		case MotionEvent.ACTION_UP:
			// (正在刷新)
			if (CURRENTOPTION == RELEASE_REFRESH) {
				// 刷新去
				CURRENTOPTION = IS_REFRESH;
				if (refreshListener != null) {
					refreshListener.onPullDownRefresh();
				}
				refresh_listview_header_view.setPadding(0, 0, 0, 0);
				setcurretOption();
			} else if (CURRENTOPTION == PULL_DONW) {
				// 回弹
				refresh_listview_header_view.setPadding(0, -headerHeight, 0, 0);
			}
			break;
		}
		return super.onTouchEvent(ev);
	}

	public void setcurretOption() {
		switch (CURRENTOPTION) {
		case RELEASE_REFRESH:
			// 箭头向上
			refresh_listview_header_arr.setAnimation(upAnimation);
			// 文字改变
			refresh_listview_header_tv.setText("释放刷新");
			break;
		case PULL_DONW:
			// 箭头向上
			refresh_listview_header_arr.setAnimation(downAnimation);
			// 文字改变
			refresh_listview_header_tv.setText("下拉刷新");
			break;
		case IS_REFRESH:
			refresh_listview_header_tv.setText("正在刷新");
			// 设置时间
			refresh_listview_header_time.setText(CommonUtil.getStringDate());
			// 箭头去除
			refresh_listview_header_arr.setVisibility(View.GONE);
			// 清除动画
			refresh_listview_header_arr.clearAnimation();
			// 显示进度条
			refresh_listview_header_progressbar.setVisibility(View.VISIBLE);
			break;
		}
	}

	public void addCustomHeader(View v) {

		customHeader = v;
		// 添加刷新布局到头上面来
		refresh_listview_header_root.addView(v);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// 第一个可见的头
		mFfirstVisibleItem = firstVisibleItem;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// 滚动过程中状态发生改变的方法
		/*
		 * SCROLL_STATE_FLING 飞速滚动 SCROLL_STATE_IDLE 空闲(滚动状态改变时刻)
		 * 
		 * SCROLL_STATE_TOUCH_SCROLL 拿手拖拽没有放开
		 */
		if (scrollState == OnScrollListener.SCROLL_STATE_FLING
				|| scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			// this.getLastVisiblePosition()最后一个可见的listView对应item
			// isLoading是去判断是否加载过的一个标识
			if (this.getLastVisiblePosition() == getCount() - 1 && !isLoading) {
				// 对底部没做任何处理
				viewFooter.setPadding(0, 0, 0, 0);
				// 将上拉加载操作标识设置为true
				isLoading = true;

				this.setSelection(this.getCount());
				// 加载操作
				if (refreshListener != null) {
					// 上拉加载操作
					refreshListener.onPullUpLoad();
				}
			}
		}
	}
}
