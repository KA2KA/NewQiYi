package com.newqiyi.receiver;

import com.newqiyi.service.AutoSaveService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/*
 * 
 * @author kaka 
 * @E-mail:wuwanggao@163.com
 * @version 创建时间：2015-11-3 上午11:04:34
 * 
 * 说明：当天数改变就调用这个广播（静态注册）启动读取步数，清空昨天记录
 */
public class AutoSaveReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent(context, AutoSaveService.class);
		Toast.makeText(context, "date changes", Toast.LENGTH_SHORT).show();
		context.startService(i);
	}

}
