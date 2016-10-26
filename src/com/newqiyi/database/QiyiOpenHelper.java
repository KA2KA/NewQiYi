package com.newqiyi.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class QiyiOpenHelper extends SQLiteOpenHelper{
	
	/**
	 * 创建step表
	 */
	public static final String CREATE_STEP = "create table step("
			+ "id integer primary key autoincrement,"
			+ "number text,"
			+ "date integer,"
			+ "userId text)";

	/**
	 * 创建user表
	 */
	public static final String CREATE_STEPUSER = "create table stepuser("
			+ "user_id text ,"
			+ "weight integer,"
			+ "step_length integer,"
			+ "sensitivity integer,"
			+ "today_step integer)";
	
	/**
	 * 创建weather表
	 */
	public static final String CREATE_WEATHER = "create table weather("
			+ "cityid integer primary key,"
			+ "city text,"
			+ "temp1 text,"
			+ "temp2 text,"
			+ "weather text,"
			+ "ptime text)";
	
	/**
	 * 带参数的PedometerOpenHelper构造函数
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 */
	public QiyiOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	/**
	 * 创建数据库
	 */
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_STEP);
		db.execSQL(CREATE_STEPUSER);
		db.execSQL(CREATE_WEATHER);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		
	}

	

}
