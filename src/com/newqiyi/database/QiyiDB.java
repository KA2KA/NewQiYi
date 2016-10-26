package com.newqiyi.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.newqiyi.domain.Step;
import com.newqiyi.domain.StepUser;
import com.newqiyi.domain.Weather;

/**
 * �����ݿ�pedometer��ĸ����������ɾ�Ĳ�
 * 
 * @author KAKA
 * 
 */
public class QiyiDB {

	public static final String DB_NAME = "qiyistep.db";// ���ݿ�����

	public static final int VERSION = 1;// ���ݰ汾

	private static QiyiDB qydb;

	private SQLiteDatabase db;

	/**
	 * ��PedometerDB�Ĺ��췽������Ϊ˽�з������ڱ�����ﲻ��ͨ��new�������������
	 * 
	 * @param context
	 */
	private QiyiDB(Context context) {
		QiyiOpenHelper pHelper = new QiyiOpenHelper(context, DB_NAME, null,
				VERSION);
		db = pHelper.getWritableDatabase();
	}

	/**
	 * ʹ�õ���ģʽ�������ݿ�
	 */
	public synchronized static QiyiDB getInstance(Context context) {
		if (qydb == null) {
			qydb = new QiyiDB(context);
		}
		return qydb;
	}

	/**
	 * ����user���������
	 * 
	 * @param user
	 */
	public void saveUser(StepUser user) {
		if (user != null) {
			ContentValues values = new ContentValues();
			values.put("user_id", user.getUser_id());
			values.put("weight", user.getWeight());
			values.put("sensitivity", user.getSensitivity());
			values.put("step_length", user.getStep_length());
			values.put("today_step", user.getToday_step());
			db.insert("stepuser", null, values);
		}
	}

	/**
	 * ����user��idɾ��user���������
	 * 
	 * @param user
	 */
	public void deleteUser(StepUser user) {
		if (user != null) {
			db.delete("stepuser", "user_id = ?",
					new String[] { user.getUser_id() });
		}
	}

	/**
	 * ����user���������
	 * 
	 * @param user
	 */
	public void updateUser(StepUser user) {
		if (user != null) {
			ContentValues values = new ContentValues();
			values.put("user_id", user.getUser_id());
			values.put("weight", user.getWeight());
			values.put("sensitivity", user.getSensitivity());
			values.put("step_length", user.getStep_length());
			values.put("today_step", user.getToday_step());
			db.update("stepuser", values, "user_id = ?",
					new String[] { user.getUser_id() });
		}
	}

	/**
	 * ����user���������
	 * 
	 * @param user
	 */
	public void changeUserId(StepUser user) {
		if (user != null) {
			ContentValues values = new ContentValues();
			values.put("user_id", user.getUser_id());
			db.update("stepuser", values, null, null);
		}
	}

	/**
	 * ����step���������
	 * 
	 * @param step
	 */
	public void saveStep(Step step) {
		if (step != null) {
			ContentValues values = new ContentValues();
			values.put("number", step.getNumber());
			values.put("date", step.getDate());
			values.put("userId", step.getUserId());
			db.insert("step", null, values);
		}
	}

	/**
	 * ����step���������
	 * 
	 * @param step
	 */
	public void updateStep(Step step) {
		if (step != null) {
			ContentValues values = new ContentValues();
			values.put("number", step.getNumber());
			values.put("date", step.getDate());
			values.put("userId", step.getUserId());
			db.update("step", values, "userId = ? and date = ?", new String[] {
					step.getUserId(), step.getDate() });
		}
	}

	/**
	 * ����step���������
	 * 
	 * @param step
	 */
	public void changeuserId(Step step) {
		if (step != null) {
			ContentValues values = new ContentValues();
			values.put("number", step.getNumber());
			values.put("date", step.getDate());
			values.put("userId", step.getUserId());
			db.update("step", values, null, null);
		}
	}

	/**
	 * �洢����վ��ץȡ����������
	 * 
	 * @param weather
	 */
	public void saveWeather(Weather weather) {
		if (weather != null) {
			ContentValues values = new ContentValues();
			values.put("cityid", weather.getCityid());
			values.put("city", weather.getCity());
			values.put("temp1", weather.getTemp1());
			values.put("temp2", weather.getTemp2());
			values.put("weather", weather.getWeather());
			values.put("date", weather.getWeather());
			db.insert("weather", null, values);
		}
	}

	/**
	 * ����user���userid��date��ȡ����
	 * 
	 * @param userId
	 * @param date
	 * @return
	 */
	public Step loadSteps(String userId, String date) {
		Step step = null;
		Cursor cursor = db.query("step", null, "userId = ? and date = ?",
				new String[] { userId, date }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				step = new Step();
				step.setNumber(cursor.getInt(cursor.getColumnIndex("number")));
				step.setDate(cursor.getString(cursor.getColumnIndex("date")));
				step.setUserId(userId);
			} while (cursor.moveToNext());

		} else {
			Log.i("tag", "step is null!");
		}
		return step;
	}

	/**
	 * ȡ��user�����е����ݣ����ղ����Ľ���ȡ��
	 * 
	 * @return
	 */
	public List<StepUser> lodListUsers() {
		List<StepUser> list = null;
		Cursor cursor = db.rawQuery(
				"select * from stepuser  order by today_step desc", null);
		if (cursor.moveToFirst()) {
			list = new ArrayList<StepUser>();
			do {
				StepUser user = new StepUser();
				user.setUser_id(cursor.getString(cursor
						.getColumnIndex("user_id")));
				user.setSensitivity(cursor.getInt(cursor
						.getColumnIndex("sensitivity")));
				user.setStep_length(cursor.getInt(cursor
						.getColumnIndex("step_length")));
				user.setWeight(cursor.getInt(cursor.getColumnIndex("weight")));
				user.setToday_step(cursor.getInt(cursor
						.getColumnIndex("today_step")));
				list.add(user);
			} while (cursor.moveToNext());
		}
		return list;

	}

	/**
	 * ����dateȡ�����е�step����
	 * 
	 * @param date
	 * @return
	 */
	public List<Step> loadListSteps() {
		List<Step> list = new ArrayList<Step>();

		Cursor cursor = db.rawQuery("select * from step order by number desc",
				null);
		if (cursor.moveToFirst()) {
			do {
				Step step = new Step();
				step.setId(cursor.getInt(cursor.getColumnIndex("id")));
				step.setNumber(cursor.getInt(cursor.getColumnIndex("number")));
				step.setDate(cursor.getString(cursor.getColumnIndex("date")));
				step.setUserId(cursor.getString(cursor.getColumnIndex("userId")));
				list.add(step);
			} while (cursor.moveToNext());

		}

		return list;
	}

	/**
	 * ����idȡ��user����
	 * 
	 * @param id
	 * @return
	 */
	public StepUser loadUser(String user_id) {
		System.out.println("user_id:" + user_id);
		StepUser user = null;
		if (user_id != null && !user_id.trim().equals("")) {
			Cursor cursor = db.query("stepuser", null, "user_id = ?",
					new String[] { user_id }, null, null, null);
			if (cursor.moveToFirst()) {
				do {
					user = new StepUser();
					user.setUser_id(user_id);
					user.setSensitivity(cursor.getInt(cursor
							.getColumnIndex("sensitivity")));
					user.setStep_length(cursor.getInt(cursor
							.getColumnIndex("step_length")));
					user.setWeight(cursor.getInt(cursor
							.getColumnIndex("weight")));
					user.setToday_step(cursor.getInt(cursor
							.getColumnIndex("today_step")));
				} while (cursor.moveToNext());
			} else {
				Log.i("tag", "User is null!");
			}
		}
		return user;
	}

	/**
	 * ȡ����һ���û���Ҳ�����ô�app���û�
	 * 
	 * @param id
	 * @return
	 */
	public StepUser loadFirstUser() {
		StepUser user = null;
		Cursor cursor = db
				.query("stepuser", null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			user = new StepUser();
			user.setUser_id(cursor.getString(cursor.getColumnIndex("user_id")));
			user.setSensitivity(cursor.getInt(cursor.getColumnIndex("sensitivity")));
			user.setStep_length(cursor.getInt(cursor.getColumnIndex("step_length")));
			user.setWeight(cursor.getInt(cursor.getColumnIndex("weight")));
			user.setToday_step(cursor.getInt(cursor.getColumnIndex("today_step")));
		} else {
			Log.i("tag", "User is null!");
		}
		return user;
	}

	/**
	 * ��������ȡ����������
	 * 
	 * @param date
	 * @return
	 */
	public Weather loadWeather(String date) {
		Weather weather = new Weather();
		Cursor cursor = db.query("weather", null, "date = ?",
				new String[] { date }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				weather.setCity(cursor.getString(cursor.getColumnIndex("city")));
				weather.setTemp1(cursor.getString(cursor
						.getColumnIndex("temp1")));
				weather.setTemp2(cursor.getString(cursor
						.getColumnIndex("temp2")));
				weather.setWeather(cursor.getString(cursor
						.getColumnIndex("weather")));
			} while (cursor.moveToNext());

		} else {

		}

		return weather;

	}
}
