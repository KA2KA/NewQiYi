package com.newqiyi.activity;

import java.util.Iterator;
import java.util.List;

import android.app.ActivityManager;
import android.app.Application;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import cn.jpush.android.api.JPushInterface;

import com.baidu.mapapi.SDKInitializer;
import com.newqiyi.domain.User;
import com.newqiyi.util.GsonUtil;
import com.newqiyi.util.SharePrefUitl;

/**
 * @author KaKa
 * @E-mail:wuwanggao@163.com
 * @version 创建时间：2015-10-29 下午3:19:56
 */
public class MyApplication extends Application {

	private User user;
	private static MyApplication myApplication = null;

	// 创建单例
	public static MyApplication getInstance() {
		return myApplication;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		//极光推送
//		JPushInterface.setDebugMode(true); // 设置开启日志,发布时请关闭日志
//		JPushInterface.init(this); // 初始化 JPush
		/*CustomPushNotificationBuilder builder = new CustomPushNotificationBuilder(getApplicationContext(),
				R.layout.customer_notitfication_layout, R.id.icon, R.id.title, R.id.text);
		// 指定定制的 Notification Layout
		builder.statusBarDrawable = R.drawable.ic_richpush_actionbar_back;
		// 指定最顶层状态栏小图标
		builder.layoutIconDrawable = R.drawable.message;
		// 指定下拉状态栏时显示的通知图标
		JPushInterface.setPushNotificationBuilder(0, builder);*/
		
		
		myApplication = this;
		String UserData = SharePrefUitl.getStringData(getApplicationContext(),
				"UserData", "");
		if (!TextUtils.isEmpty(UserData)) {
			User user = GsonUtil.jsonToBean(UserData, User.class);
			MyApplication.getInstance().setUser(user);

		}
		
		// 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
		SDKInitializer.initialize(this);

		// int pid = android.os.Process.myPid();
		// String processAppName = getAppName(pid);
		// // 如果app启用了远程的service，此application:onCreate会被调用2次
		// // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
		// // 默认的app会在以包名为默认的process name下运行，如果查到的process name不是app的process
		// // name就立即返回
		//
		// if (processAppName == null
		// || !processAppName.equalsIgnoreCase("com.newqiyi.activity")) {
		//
		// return;
		// }
		//
		// EMChat.getInstance().init(myApplication);

	}

	private String getAppName(int pID) {
		String processName = null;
		ActivityManager am = (ActivityManager) this
				.getSystemService(ACTIVITY_SERVICE);
		List l = am.getRunningAppProcesses();
		Iterator i = l.iterator();
		PackageManager pm = this.getPackageManager();
		while (i.hasNext()) {
			ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i
					.next());
			try {
				if (info.pid == pID) {
					CharSequence c = pm.getApplicationLabel(pm
							.getApplicationInfo(info.processName,
									PackageManager.GET_META_DATA));
					processName = info.processName;
					return processName;
				}
			} catch (Exception e) {
			}
		}
		return processName;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
