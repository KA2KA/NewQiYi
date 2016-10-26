package com.newqiyi.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.newqiyi.activity.MainActivity;
import com.newqiyi.activity.R;
import com.newqiyi.database.QiyiDB;
import com.newqiyi.domain.Step;
import com.newqiyi.domain.StepUser;
import com.newqiyi.domain.User;
import com.newqiyi.pager.ActivityPager;
import com.newqiyi.pager.BasePager;
import com.newqiyi.pager.FunctionPager;
import com.newqiyi.pager.MyselfPager;
import com.newqiyi.pager.ProjectPage;
import com.newqiyi.service.StepDetector;
import com.newqiyi.util.GsonUtil;
import com.newqiyi.util.SharePrefUitl;
import com.newqiyi.util.URLUtil;

public class HomeFragment extends BaseFragment {

	@ViewInject(R.id.layout_content)
	private ViewPager layout_content;

	@ViewInject(R.id.main_radio)
	private RadioGroup main_radio;

	@ViewInject(R.id.rb_function)
	private RadioButton rb_function;
	@ViewInject(R.id.rb_activity)
	private RadioButton rb_activity;
	@ViewInject(R.id.rb_project)
	private RadioButton rb_project;
	@ViewInject(R.id.rb_myself)
	private RadioButton rb_myself;

	private ArrayList<BasePager> pagerList;
	private QiyiDB db;
	private StepUser sUser;
	private String url = "servlet/AndroidSportQibiInsertServlet";

	private int user_score;

	private boolean flag;

	@SuppressLint("InflateParams")
	@Override
	public View initView(LayoutInflater inflater) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.frag_home, null);
		ViewUtils.inject(this, view);
		return view;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void initData(Bundle saveInstanceState) {
		flag = MainActivity.flag;
		pagerList = new ArrayList<BasePager>();

		pagerList.add(new ProjectPage(getActivity()));
		pagerList.add(new ActivityPager(getActivity()));
		pagerList.add(new FunctionPager(getActivity()));
		pagerList.add(new MyselfPager(getActivity()));

		layout_content.setAdapter(new MyPagerAdapter());

		layout_content.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				switch (arg0) {
				case 0:
					rb_function.setChecked(true);
					break;
				case 1:
					rb_activity.setChecked(true);
					break;
				case 2:
					rb_project.setChecked(true);
					break;
				case 3:
					rb_myself.setChecked(true);
					break;
				}
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}
		});
		main_radio.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
				case R.id.rb_function:
					layout_content.setCurrentItem(0);
					((ProjectPage) pagerList.get(0)).initData();
					break;
				case R.id.rb_activity:
					layout_content.setCurrentItem(1);
					((ActivityPager) pagerList.get(1)).initData();
					break;
				case R.id.rb_project:
					layout_content.setCurrentItem(2);
					((FunctionPager) pagerList.get(2)).initData();
					break;
				case R.id.rb_myself:
					layout_content.setCurrentItem(3);
					((MyselfPager) pagerList.get(3)).initData();
					break;
				}
			}
		});
		if (flag) {
			main_radio.check(R.id.rb_myself);
		} else {
			main_radio.check(R.id.rb_function);
		}
	}

	class MyPagerAdapter extends PagerAdapter {

		public MyPagerAdapter() {
		}

		@Override
		public int getCount() {
			return pagerList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {

			container.addView(pagerList.get(position).getRootView());
			return pagerList.get(position).getRootView();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
			if (position == 2 && FunctionPager.STARTSTEP) {
				saveData();
			}
		}
	}

	// 保存用户运动步数到本地
	@SuppressLint("SimpleDateFormat")
	private void saveData() {
		db = QiyiDB.getInstance(context);

		// 拿到用户Gson数据
		String userData = SharePrefUitl.getStringData(context, "UserData", "");
		if (!TextUtils.isEmpty(userData)) {
			// 拿到用户
			User user = GsonUtil.jsonToBean(userData, User.class);

			sUser = db.loadUser(user.getUser_id());
			if (sUser != null) {
				// 拿到今天的日期：
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String today = sdf.format(new Date());

				String stepData = SharePrefUitl.getStringData(context,
						"stepData", "").trim();

				if (!TextUtils.isEmpty(stepData)) {
					user_score = (int) ((StepDetector.CURRENT_SETP - sUser
							.getToday_step()) * 0.35)
							+ Integer.parseInt(stepData);
				} else {
					user_score = (int) ((StepDetector.CURRENT_SETP - sUser
							.getToday_step()) * 0.35);
				}
				saveDataToServcie(user.getUser_id(), user_score);
				// 设置用户今天的步数
				sUser.setToday_step(StepDetector.CURRENT_SETP);
				// 更新用户的数据
				db.updateUser(sUser);

				Step step = db.loadSteps(sUser.getUser_id(), today);
				if (step != null) {
					// 总的步数
					step.setNumber(StepDetector.CURRENT_SETP);
					FunctionPager.STARTSTEP = false;
					db.updateStep(step);
				}
			}
		}
	}

	private void saveDataToServcie(String user_id, int user_score) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("user_id", user_id);
		params.addBodyParameter("user_score", user_score + "");

		new HttpUtils().send(HttpMethod.POST, URLUtil.BASE_URL + url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {

					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {

						if (TextUtils.isEmpty(responseInfo.result)) {
							Toast.makeText(context, "保存数据失败",
									Toast.LENGTH_SHORT).show();
						}
					}
				});
	}

}
