package com.newqiyi.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePrefUitl {
	private static String CONFIG = "config";
	private static SharedPreferences sharedPreferences;

	// 保存数据
	public static void saveStringData(Context context, String key, String value) {
		if (sharedPreferences == null) {
			sharedPreferences = context.getSharedPreferences(CONFIG,
					Context.MODE_PRIVATE);
		}

		sharedPreferences.edit().putString(key, value).commit();
	}

	// 获取数据

	public static String getStringData(Context context, String key,
			String defValue) {
		if (sharedPreferences == null) {
			sharedPreferences = context.getSharedPreferences(CONFIG,
					Context.MODE_PRIVATE);
		}
		return sharedPreferences.getString(key, defValue);
	}

	// 删除数据
	public static void clearData(Context context, String key, String value) {
		sharedPreferences = context.getSharedPreferences(CONFIG,
				Context.MODE_PRIVATE);
		sharedPreferences.edit().clear().commit();
	}
}
