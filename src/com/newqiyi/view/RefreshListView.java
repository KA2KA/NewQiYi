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
 * @version ����ʱ�䣺2015-10-20 ����3:35:58 ����ˢ��
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
	 * ��ʼ��ͷ����
	 */
	private void initHeader() {

		// ����
		viewHeader = View.inflate(getContext(),
				R.layout.refresh_listview_header, null);
		// ����������ˢ��ͷ
		refresh_listview_header_root = (LinearLayout) viewHeader
				.findViewById(R.id.refresh_listview_header_root);

		refresh_listview_header_view = (LinearLayout) viewHeader
				.findViewById(R.id.refresh_listview_header_view);
		// ͼƬ״̬
		refresh_listview_header_arr = (ImageView) viewHeader
				.findViewById(R.id.refresh_listview_header_arr);
		// ������
		refresh_listview_header_progressbar = (ProgressBar) viewHeader
				.findViewById(R.id.refresh_listview_header_progressbar);
		// ˢ������
		refresh_listview_header_tv = (TextView) viewHeader
				.findViewById(R.id.refresh_listview_header_tv);
		// ʱ��
		refresh_listview_header_time = (TextView) viewHeader
				.findViewById(R.id.refresh_listview_header_time);

		// ������ǰ�ؼ���һ���߶�
		refresh_listview_header_view.measure(0, 0);
		headerHeight = refresh_listview_header_view.getMeasuredHeight();
		// ����ǰ��Ӧ��ͷȥ�����ز���
		refresh_listview_header_view.setPadding(0, -headerHeight, 0, 0);
		// ��Ϊͷ��ӽ���(�����ɵ�ͷ�ļ�)
		this.addHeaderView(viewHeader);
		initAnimation();
	}

	public interface onRefreshListener {
		// ����ˢ��
		public void onPullDownRefresh();

		// ��������
		public void onPullUpLoad();
	}

	public void setOnRefreshListener(onRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
	}

	// ������ظ���Ĳ���
	public void OnRefreshFinish() {
		// �����������ˢ��״̬������ָ���ؼ���������״̬��ת�䣬����һ��ˢ�²�������
		if (CURRENTOPTION == IS_REFRESH) {
			refresh_listview_header_view.setPadding(0, -headerHeight, 0, 0);
			refresh_listview_header_arr.setVisibility(View.VISIBLE);
			refresh_listview_header_progressbar.setVisibility(View.INVISIBLE);
			refresh_listview_header_tv.setText("����ˢ��");
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
		// ����¼�
		case MotionEvent.ACTION_DOWN:
			downY = (int) ev.getY();
			break;
		// �ƶ��¼�
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

			// ��ȡ��ǰlistViewͷ����Y��λ��
			int listViewY = listViewlocation[1];

			int[] headerlocation = new int[2];
			customHeader.getLocationOnScreen(headerlocation);
			// ��ȡ��ǰcustomHeader(�ֲ�ͼ)ͷ����Y��λ��
			int headerY = headerlocation[1];

			if (headerY < listViewY) {
				break;
			}

			if (padding > -headerHeight && mFfirstVisibleItem == 0) {
				// �ж��Ƿ�������ק��ͷ�ĵĲ���
				if (padding > 0 && CURRENTOPTION == PULL_DONW) {
					// padding>0ˢ��ͷ��ɳ��� (׼��ˢ��)
					CURRENTOPTION = RELEASE_REFRESH;
					setcurretOption();
					Log.i(tag, "�ͷ�ˢ��");
				} else if (padding < 0 && CURRENTOPTION == RELEASE_REFRESH) {
					// padding<0ˢ��ͷû����ɳ��� (����ˢ��)
					Log.i(tag, "����ˢ��");
					CURRENTOPTION = PULL_DONW;
					setcurretOption();
				}
				refresh_listview_header_view.setPadding(0, padding, 0, 0);
				return true;
			}
			break;
		// �뿪�¼�
		case MotionEvent.ACTION_UP:
			// (����ˢ��)
			if (CURRENTOPTION == RELEASE_REFRESH) {
				// ˢ��ȥ
				CURRENTOPTION = IS_REFRESH;
				if (refreshListener != null) {
					refreshListener.onPullDownRefresh();
				}
				refresh_listview_header_view.setPadding(0, 0, 0, 0);
				setcurretOption();
			} else if (CURRENTOPTION == PULL_DONW) {
				// �ص�
				refresh_listview_header_view.setPadding(0, -headerHeight, 0, 0);
			}
			break;
		}
		return super.onTouchEvent(ev);
	}

	public void setcurretOption() {
		switch (CURRENTOPTION) {
		case RELEASE_REFRESH:
			// ��ͷ����
			refresh_listview_header_arr.setAnimation(upAnimation);
			// ���ָı�
			refresh_listview_header_tv.setText("�ͷ�ˢ��");
			break;
		case PULL_DONW:
			// ��ͷ����
			refresh_listview_header_arr.setAnimation(downAnimation);
			// ���ָı�
			refresh_listview_header_tv.setText("����ˢ��");
			break;
		case IS_REFRESH:
			refresh_listview_header_tv.setText("����ˢ��");
			// ����ʱ��
			refresh_listview_header_time.setText(CommonUtil.getStringDate());
			// ��ͷȥ��
			refresh_listview_header_arr.setVisibility(View.GONE);
			// �������
			refresh_listview_header_arr.clearAnimation();
			// ��ʾ������
			refresh_listview_header_progressbar.setVisibility(View.VISIBLE);
			break;
		}
	}

	public void addCustomHeader(View v) {

		customHeader = v;
		// ���ˢ�²��ֵ�ͷ������
		refresh_listview_header_root.addView(v);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// ��һ���ɼ���ͷ
		mFfirstVisibleItem = firstVisibleItem;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// ����������״̬�����ı�ķ���
		/*
		 * SCROLL_STATE_FLING ���ٹ��� SCROLL_STATE_IDLE ����(����״̬�ı�ʱ��)
		 * 
		 * SCROLL_STATE_TOUCH_SCROLL ������קû�зſ�
		 */
		if (scrollState == OnScrollListener.SCROLL_STATE_FLING
				|| scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			// this.getLastVisiblePosition()���һ���ɼ���listView��Ӧitem
			// isLoading��ȥ�ж��Ƿ���ع���һ����ʶ
			if (this.getLastVisiblePosition() == getCount() - 1 && !isLoading) {
				// �Եײ�û���κδ���
				viewFooter.setPadding(0, 0, 0, 0);
				// ���������ز�����ʶ����Ϊtrue
				isLoading = true;

				this.setSelection(this.getCount());
				// ���ز���
				if (refreshListener != null) {
					// �������ز���
					refreshListener.onPullUpLoad();
				}
			}
		}
	}
}
