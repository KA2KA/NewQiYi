package com.newqiyi.view;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.newqiyi.activity.R;

/**
 * 
 * @author kaka
 * @E-mail:wuwanggao@163.com
 * @version 创建时间：2015-10-21 下午9:45:24 轮播图的处理
 */
public class RollViewPager extends ViewPager {
	private List<String> titleList;
	private List<String> imgUrlList;
	private TextView top_news_title;
	private List<View> dotList;
	private BitmapUtils bitmapUtils;
	private MyAdapter adapter;
	private RunnableTask runnableTask;
	private int currentPosition = 0;

	class RunnableTask implements Runnable {
		@Override
		public void run() {
			// 维护让图片一直滚起来的操作
			currentPosition = (currentPosition + 1) % imgUrlList.size();
			// 发送一个空消息
			handler.obtainMessage().sendToTarget();
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			RollViewPager.this.setCurrentItem(currentPosition);
			// 一直去滚动
			startRoll();
		};
	};
	private int downX;
	private int downY;
	private OnPagerClick onPagerClick;

	protected void onDetachedFromWindow() {
		// 删除当前handler中维护的任务和消息
		handler.removeCallbacksAndMessages(null);
		super.onDetachedFromWindow();
	};

	public interface OnPagerClick {
		// 定义一个业务逻辑的方法
		public void click(String url);
	}

	// 让图片轮播图和底部的listView不关联
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		// 按下的操作
		case MotionEvent.ACTION_DOWN:
			downX = (int) ev.getX();
			downY = (int) ev.getY();
			// 不允许联动
			getParent().requestDisallowInterceptTouchEvent(true);
			break;
		case MotionEvent.ACTION_MOVE:
			int moveX = (int) ev.getX();
			int moveY = (int) ev.getY();

			if (Math.abs(moveY - downY) < Math.abs(moveX - downX)) {
				// 左右滑动轮播图
				// 左往右边滑动 diff = moveX-downX diff>0
				// 右边往左边滑动 diff = moveX-downX diff<0
				int diff = moveX - downX;
				if (diff > 0 && getCurrentItem() == 0) {
					// 将前一个侧拉栏目或者界面给拖拽出来
					getParent().requestDisallowInterceptTouchEvent(true);
				} else if (diff > 0
						&& getCurrentItem() < getAdapter().getCount() - 1) {
					getParent().requestDisallowInterceptTouchEvent(true);
				} else if (diff < 0
						&& getCurrentItem() == getAdapter().getCount() - 1) {
					getParent().requestDisallowInterceptTouchEvent(true);
				} else if (diff < 0
						&& getCurrentItem() < getAdapter().getCount() - 1) {
					getParent().requestDisallowInterceptTouchEvent(true);
				}
			} else {
				// 下拉刷新
				getParent().requestDisallowInterceptTouchEvent(false);
			}
			break;
		}
		return super.dispatchTouchEvent(ev);
	}

	public RollViewPager(Context context, final List<View> dotList,
			OnPagerClick onPagerClick) {
		super(context);
		this.dotList = dotList;
		this.onPagerClick = onPagerClick;
		// 图片三级缓存
		// (LRU) 最近最少使用算法(LinkHashMap(),内存当中图片使用最少的话，就放置在最前端，)
		// 1,下载--->本地磁盘(以图片名称为唯一性标示)---(压缩，缩放图片宽高)-->加载到内存中来((LRU)LinkHashMap<url,Bitmap>)
		// 2,url--->内存找---->磁盘------>下载

		// 图片url(唯一性的)
		bitmapUtils = new BitmapUtils(context);
		runnableTask = new RunnableTask();

		this.addOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				top_news_title.setText(titleList.get(arg0));
				for (int i = 0; i < imgUrlList.size(); i++) {
					if (i == arg0) {
						dotList.get(arg0).setBackgroundResource(
								R.drawable.dot_focus);
					} else {
						dotList.get(i).setBackgroundResource(
								R.drawable.dot_normal);
					}
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	public RollViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void initTitle(List<String> titleList, TextView top_news_title) {
		if (titleList != null && top_news_title != null && titleList.size() > 0) {
			top_news_title.setText(titleList.get(0));
		}
		this.titleList = titleList;
		this.top_news_title = top_news_title;
	}

	public void initImgUrlList(List<String> imgUrlList) {
		this.imgUrlList = imgUrlList;
	}

	// 加载图片轮播
	public void startRoll() {
		if (adapter == null) {
			adapter = new MyAdapter();
			this.setAdapter(adapter);
		} else {
			adapter.notifyDataSetChanged();
		}
		// 自动滑动
		handler.postDelayed(runnableTask, 3000);
	}

	class MyAdapter extends PagerAdapter {
		@Override
		public int getCount() {
			return imgUrlList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			View view = View.inflate(getContext(), R.layout.viewpager_item,
					null);
			ImageView imageView = (ImageView) view.findViewById(R.id.image);
			bitmapUtils.display(imageView, imgUrlList.get(position));
			container.addView(view);
			view.setOnTouchListener(new OnTouchListener() {
				private int downX;
				private long downTime;

				// viewpager 和内部view事件的一个处理规则
				// 1，ACTION_DOWN默认传递给内部view
				// 如果当前手指不移动拿起，则up事件依然停留在view上
				//
				// action_move优先作用在view上，如果当前的手指移动一定距离，或者移动过程中达到一定加速度，view就触发ACTION_CANCEL.
				// 然后后续事件(ACTION_MOVE，ACTION_UP)都作用在viewpager上
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						downX = (int) event.getX();
						downTime = System.currentTimeMillis();
						// 将任务的消息都remove
						handler.removeCallbacksAndMessages(null);
						break;
					case MotionEvent.ACTION_UP:
						startRoll();
						int moveX = (int) event.getX();
						long upTime = System.currentTimeMillis();
						if (upTime - downTime < 500 && downX == moveX) {
							// 响应点击事件，响应点击事件的业务逻辑未知
							if (null != onPagerClick) {
								onPagerClick.click(imgUrlList.get(position));
							}
						}
						break;
					// Button
					case MotionEvent.ACTION_CANCEL:
						startRoll();
						break;
					}
					return true;
				}
			});
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
	}
}
