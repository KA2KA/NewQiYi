package com.newqiyi.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.radar.RadarNearbyInfo;
import com.baidu.mapapi.radar.RadarNearbyResult;
import com.baidu.mapapi.radar.RadarNearbySearchOption;
import com.baidu.mapapi.radar.RadarSearchError;
import com.baidu.mapapi.radar.RadarSearchListener;
import com.baidu.mapapi.radar.RadarSearchManager;
import com.baidu.mapapi.radar.RadarUploadInfo;
import com.baidu.mapapi.radar.RadarUploadInfoCallback;

public class ActMapUtils implements RadarUploadInfoCallback,
		RadarSearchListener, BDLocationListener {
	Handler handler;
	private static final String TAG = "MapUtils";
	boolean isFirstLoc = true;// 是否首次定位
	// 用户坐标
	private static LatLng mLatLng = null;
	LatLng actLatLng;
	LocationClient mLocClient;
	// 附近搜索的数据
	List<RadarNearbyInfo> listNear = new ArrayList<RadarNearbyInfo>();
	// 搜索的活动信息存放
	ArrayList<Integer> activities_id = new ArrayList<Integer>();
	ArrayList<Integer> distance = new ArrayList<Integer>();
	String map_method_type;
	String id;

	public ActMapUtils(String map_method_type, String id, LatLng latLng
			) {
		super();
		this.map_method_type = map_method_type;
		this.id = id;
		this.actLatLng = latLng;
	}

	public ActMapUtils(String map_method_type, String id, Handler handler) {
		super();
		this.map_method_type = map_method_type;
		this.id = id;
		this.handler = handler;

	}

	// 定位自己的位置
	@Override
	public void onReceiveLocation(BDLocation arg0) {
		if (arg0 == null)
			return;
		mLatLng = new LatLng(arg0.getLatitude(), arg0.getLongitude());
		stopLocation();
		if (map_method_type == "查看活动") {
			if (isFirstLoc) {
				searchRequest();
			}
		}
		if (map_method_type == "上传活动") {
			if (isFirstLoc) {
				upLoadActInfo(id, actLatLng);
			}
		}
	}

	@Override
	public void onGetClearInfoState(RadarSearchError arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onGetNearbyInfoList(RadarNearbyResult result,
			RadarSearchError error) {
		System.out.println(error);
		if (error == RadarSearchError.RADAR_NO_ERROR) {
			// 获取成功，处理数据
			activities_id.clear();
			distance.clear();
			listNear = result.infoList;
			if (listNear.size() > 0) {
				for (RadarNearbyInfo info : listNear) {
					if (info.comments.equals("活动")) {
						String element_id = info.userID;
							activities_id.add(Integer.parseInt(element_id));
							distance.add(info.distance);
						}

					}
				}
				new Thread() {
					public void run() {
						Message message = Message.obtain();
						Bundle bundle = new Bundle();
						bundle.putIntegerArrayList("activities_id",
								activities_id);
						bundle.putIntegerArrayList("distance", distance);
						message.setData(bundle);
						message.what = 11;
						handler.sendMessage(message);
					}
				}.start();

			}

		}

	@Override
	public void onGetUploadState(RadarSearchError error) {

	}

	@Override
	public RadarUploadInfo OnUploadInfoCallback() {
		RadarUploadInfo info = new RadarUploadInfo();
		info.comments = "人";
		info.pt = mLatLng;
		return info;
	}

	/**
	 * 开始自动上传
	 * 
	 */
	public void uploadContinue() {
		if (mLatLng == null) {
			return;
		}
		// uploadAuto = true;
		RadarSearchManager.getInstance().startUploadAuto(this, 5000);
	}

	public void searchRequest() {
		if (mLatLng == null) {
			return;
		}
		if (mLatLng != null) {
			RadarNearbySearchOption option = new RadarNearbySearchOption()
					.centerPt(mLatLng).pageNum(0).radius(2000);
			RadarSearchManager.getInstance().nearbyInfoRequest(option);
		} else {
			Log.i(TAG, "mLatLng:" + mLatLng);
		}
	}

	// 上传信息（手动）
	public void upLoadActInfo(String id, LatLng latLng) {
		if (latLng == null) {
			return;

		}
		RadarUploadInfo info = new RadarUploadInfo();
		RadarSearchManager.getInstance().setUserID(id);
		info.comments = "活动";
		info.pt = latLng;
		RadarSearchManager.getInstance().uploadInfoRequest(info);
		activities_id.clear();
		distance.clear();
	}

	// 开启定位
	public void startLoaction(Context context) {
		RadarSearchManager.getInstance()
				.addNearbyInfoListener(ActMapUtils.this);
		RadarSearchManager.getInstance().setUserID("");
		mLocClient = new LocationClient(context);
		mLocClient.registerLocationListener(ActMapUtils.this);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
	}

	public void stopLocation() {
		mLocClient.unRegisterLocationListener(ActMapUtils.this);
		mLocClient.stop();
	}

}
