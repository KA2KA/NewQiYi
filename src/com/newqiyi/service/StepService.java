package com.newqiyi.service;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
/**
 * 
* @author kaka 
* @E-mail:wuwanggao@163.com
* @version 创建时间：2015-10-24 下午2:02:17  开启记录运动的服务
 */
public class StepService extends Service {
	public static Boolean flag = false;//是否开启
	private SensorManager sensorManager;//振动器管理
	private StepDetector stepDetector;//计步器算法

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		new Thread(new Runnable() {
			public void run() {
				startStepDetector();
			}
		}).start();

	}

	private void startStepDetector() {
		flag = true;
		stepDetector = new StepDetector(this);
		sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);//获取传感器管理器的实例
		Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//获得传感器的类型，这里获得的类型是加速度传感器
		//此方法用来注册，只有注册过才会生效，参数：SensorEventListener的实例，Sensor的实例，更新速率
		sensorManager.registerListener(stepDetector, sensor,SensorManager.SENSOR_DELAY_FASTEST);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		flag = false;
		if (stepDetector != null) {
			sensorManager.unregisterListener(stepDetector);
		}

	}
}
