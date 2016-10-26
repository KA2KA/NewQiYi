package com.newqiyi.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.newqiyi.database.QiyiDB;
import com.newqiyi.domain.Step;
import com.newqiyi.domain.StepUser;
import com.newqiyi.domain.User;
import com.newqiyi.util.GsonUtil;
import com.newqiyi.util.SharePrefUitl;

/*
 * 
 * @author kaka 
 * @E-mail:wuwanggao@163.com
 * @version 创建时间：2015-11-3 上午11:02:52  
 * 
 * 天数改变自动保存用户的数据
 */

public class AutoSaveService extends Service {
	private QiyiDB db;
	private Step step;
	private String date;
	private Calendar calendar;
	private SimpleDateFormat sdf;
	private User user;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		System.out.println("AutoSaveService+>>>>>>>>>>>>>>>>");
		super.onCreate();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println("AutoSaveService 开启了服务");
		// 将数据传递到服务器
		init();
		return super.onStartCommand(intent, flags, startId);

	}

	@SuppressLint("SimpleDateFormat")
	private void init() {
		Log.i("info", "你好啊");
		calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, -1);// 昨天日期
		sdf = new SimpleDateFormat("yyyy-MM-dd");
		db = QiyiDB.getInstance(this);
		date = sdf.format(calendar.getTime());
		Log.i("info", date);
		/*
		 * 取到用户信息，然后查询用户的步数
		 */
		String userData = SharePrefUitl.getStringData(getApplicationContext(),
				"UserData", "");
		if (!TextUtils.isEmpty(userData)) {
			user = GsonUtil.jsonToBean(userData, User.class);
			//
			StepUser stepUser = db.loadFirstUser();

			step = db.loadSteps(user.getUser_id(), date);
			if (step != null) {
				step.setNumber(StepDetector.CURRENT_SETP);
				db.updateStep(step);
				stepUser.setToday_step(0);
				db.updateUser(stepUser);
				Log.i("info", "你好啊1");
				step = new Step();
				step.setDate(sdf.format(new Date()));
				step.setNumber(0);
				step.setUserId(user.getUser_id());
				db.saveStep(step);
			} else {

				Step step = new Step();
				stepUser.setToday_step(0);
				db.updateUser(stepUser);
				step.setDate(sdf.format(new Date()));
				step.setNumber(0);
				step.setUserId(user.getUser_id());
				db.saveStep(step);

			}

		}

	}
}
