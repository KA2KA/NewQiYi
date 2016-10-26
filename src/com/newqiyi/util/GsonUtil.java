package com.newqiyi.util;

import com.google.gson.Gson;

/** 
 * @author KaKa
 * @E-mail wuwanggao@163.com
 * @version 创建时间：2015-10-18 下午3:22:47 用户Gson解析
 * 
 * 
 */
public class GsonUtil {
	
	public static <T> T jsonToBean(String json,Class<T> clazz){
		Gson gson = new Gson();
		return gson.fromJson(json, clazz);
	}
}
