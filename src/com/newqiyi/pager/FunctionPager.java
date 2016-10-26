package com.newqiyi.pager;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.newqiyi.activity.R;
import com.newqiyi.database.QiyiDB;
import com.newqiyi.domain.StepUser;
import com.newqiyi.domain.User;
import com.newqiyi.domain.Weather;
import com.newqiyi.service.StepDetector;
import com.newqiyi.service.StepService;
import com.newqiyi.util.GsonUtil;
import com.newqiyi.util.SharePrefUitl;
import com.newqiyi.widget.CircleBar;

/*
 * @author kaka 
 * @E-mail:wuwanggao@163.com
 * @version 创建时间：2015-10-30 下午3:08:29 记录运动
 */
@SuppressLint("SimpleDateFormat")
public class FunctionPager extends BasePager implements OnClickListener {

	private QiyiDB db;
	private String user_id;
	private StepUser sUser;

	public static boolean STARTSTEP = false;

	public FunctionPager(Context context) {
		super(context);
	}

	private int total_step = 0;
	private Thread thread;
	private int Type = 1;// 代表动画的效果。1、步数 2、卡路里 3、天气
	private int calories = 0;
	private int step_length = 50;
	private int weight = 70;
	private Weather weather;
	private String test;
	private boolean flag = true;// 来判断第三个页面是否开启动画
	@ViewInject(R.id.pb_step)
	CircleBar pb_step;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			total_step = StepDetector.CURRENT_SETP;
			if (Type == 1) {
				pb_step.setProgress(total_step, Type);
			} else if (Type == 2) {
				// 显示公益积分（1卡路里显示1积分）
				calories = (int) (weight * total_step * step_length * 0.01 * 0.01);
				pb_step.setProgress(calories, Type);
			} else if (Type == 3) {
				if (flag) {
					pb_step.startCustomAnimation();
					flag = false;
				}
				if (test != null || weather.getWeather() == null) {
					weather.setWeather("正在更新中...");
					weather.setPtime("");
					weather.setTemp1("");
					weather.setTemp2("");
					pb_step.setWeather(weather);
				} else {
					pb_step.setWeather(weather);
				}
			}
		}
	};

	private User user;

	// 布局的加载
	@Override
	public View initView() {
		view = View.inflate(context, R.layout.function_main, null);
		ViewUtils.inject(this, view);
		return view;
	}

	// 数据的处理
	@Override
	public void initData() {
		db = QiyiDB.getInstance(context);
		// 拿到系统的管理器
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		// 访问网络
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isAvailable()) {
		} else {
			Toast.makeText(context, "没有网络", Toast.LENGTH_LONG).show();
		}

		String userData = SharePrefUitl.getStringData(context, "UserData", "");
		if (!TextUtils.isEmpty(userData)) {
			user = GsonUtil.jsonToBean(userData, User.class);
			user_id = user.getUser_id();
			sUser = db.loadUser(user_id);
			if (sUser != null) {
				STARTSTEP = true;
				StepDetector.CURRENT_SETP = sUser.getToday_step();
			}
		}

		/*
		 * 开启服务
		 */
		Intent service = new Intent(context, StepService.class);
		context.startService(service);
		weather = new Weather();
		pb_step.setMax(10000);
		// 设置当前步数进度
		pb_step.setProgress(StepDetector.CURRENT_SETP, 1);
		// 开启动画
		pb_step.startCustomAnimation();
		// 设置点击事件
		pb_step.setOnClickListener(this);

		mThread();
		//
		// // 将用户当前的步数记录转换成用户的积分保存
		// int score = StepDetector.CURRENT_SETP;

	}

	private void mThread() {
		if (thread == null) {
			thread = new Thread(new Runnable() {

				@Override
				public void run() {
					while (true) {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if (flag) {
							Message message = new Message();
							handler.sendMessage(message);
						}
					}
				}
			});
			thread.start();
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pb_step:
			if (Type == 1) {

				Type = 2;
			} else if (Type == 2) {
				String address = "http://www.weather.com.cn/data/cityinfo"
						+ "/101190401.html";
				queryFromServer(address);
				flag = true;
				Type = 3;
			} else if (Type == 3) {
				Type = 1;
			}
			Message msg = new Message();
			handler.sendMessage(msg);
			break;
		}
	}

	@SuppressLint("SimpleDateFormat")
	private void queryFromServer(final String address) {
		HttpUtils hUtils = new HttpUtils();
		hUtils.send(HttpMethod.GET, address, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				test = "更新失败";
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(response.result);
					JSONObject weatherInfo = jsonObject
							.getJSONObject("weatherinfo");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					weather.setPtime(sdf.format(new Date()));
					weather.setTemp1(weatherInfo.getString("temp1"));
					weather.setTemp2(weatherInfo.getString("temp2"));
					weather.setWeather(weatherInfo.getString("weather"));
					test = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
