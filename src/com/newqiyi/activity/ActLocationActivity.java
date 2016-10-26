package com.newqiyi.activity;

import java.util.ArrayList;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.favorite.FavoriteManager;
import com.baidu.mapapi.favorite.FavoritePoiInfo;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapLongClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.newqiyi.widget.AppMsg;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author 赵文强
 * 
 */
public class ActLocationActivity extends Activity implements
		OnMapLongClickListener, OnMarkerClickListener, OnMapClickListener,
		OnGetGeoCoderResultListener {
	// 地图相关
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	private LocationMode mCurrentMode;
	boolean isFirstLoc = true;// 是否首次定位
	// 界面控件相关
	private int LocationInfoBack = 44;
	private String act_coordinate;// 坐标
	private String nameText;// 名称
	private String lat;// 经度
	private String log;// 维度
	private TextView act_address_text;
	private LatLng actPoint;
	// 现实marker的图标
	BitmapDescriptor bdA = BitmapDescriptorFactory
			.fromResource(R.drawable.icon_marka);
	List<Marker> markers = new ArrayList<Marker>();
	MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(16.0f);// 开始的坐标距离
	GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_location);
		act_address_text = (TextView) findViewById(R.id.act_address_text);
		mCurrentMode = LocationMode.NORMAL;
		// 地图初始化
		mMapView = (MapView) findViewById(R.id.actlocationView);
		mMapView.showZoomControls(false);// 去掉距离控制
		mMapView.removeViewAt(1);// 去掉百度logo
		mBaiduMap = mMapView.getMap();
		// 设置初次进入地图的显示距离
		mBaiduMap.setMapStatus(msu);
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 设置点击事件
		mBaiduMap.setOnMapLongClickListener(this);
		mBaiduMap.setOnMarkerClickListener(this);
		mBaiduMap.setOnMapClickListener(this);
		// 初始化收藏夹
		FavoriteManager.getInstance().init();
		initLocation();// 初始化定位模式
		// 初始化搜索模块，注册事件监听
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);
	}

	// 初始化定位模式
	public void initLocation() {
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				mCurrentMode, true, null));
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
		mLocClient.setLocOption(option);
		mLocClient.start();
	}

	/**
	 * 发起搜索
	 * 
	 * @param v
	 */
	public void SearchButtonProcess() {
		// LatLng ptCenter = new LatLng(Float.valueOf(lat), Float.valueOf(log));
		// 反Geo搜索
		mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(actPoint));

	}

	// 回到定位处
	public void initializeMap(View v) {
		isFirstLoc = true;
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {

		}
	}

	@Override
	protected void onPause() {
		// MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		// MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// 释放收藏夹功能资源
		FavoriteManager.getInstance().destroy();
		bdA.recycle();
		// MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
		// 退出时销毁定位
		mLocClient.stop();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mBaiduMap = null;
		super.onDestroy();
	}

	@Override
	public void onMapClick(LatLng arg0) {
		// TODO Auto-generated method stub
		mBaiduMap.hideInfoWindow();
	}

	@Override
	public boolean onMapPoiClick(MapPoi arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		return true;
	}

	@Override
	public void onMapLongClick(LatLng point) {
		actPoint = point;
		act_coordinate = String.valueOf(point.latitude) + ","
				+ String.valueOf(point.longitude);// 经纬度信息
		FavoritePoiInfo info = new FavoritePoiInfo();
		info.poiName(nameText);
		// LatLng pt;// 经纬度
		lat = act_coordinate.substring(0, act_coordinate.indexOf(","));
		log = act_coordinate.substring(act_coordinate.indexOf(",") + 1);
		// pt = new LatLng(Double.parseDouble(lat), Double.parseDouble(log));
		info.pt(point);
		// 绘制在地图
		markers.clear();
		MarkerOptions option = new MarkerOptions().icon(bdA).position(point);
		Bundle b = new Bundle();
		b.putString("id", info.getID());
		option.extraInfo(b);
		markers.add((Marker) mBaiduMap.addOverlay(option));
		SearchButtonProcess();
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {

	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(ActLocationActivity.this, "抱歉，未能找到结果",
					Toast.LENGTH_LONG).show();
			return;
		}
		mBaiduMap.clear();
		mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.icon_marka)));
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
				.getLocation()));
		Toast.makeText(ActLocationActivity.this, result.getAddress(),
				Toast.LENGTH_LONG).show();
		act_address_text.setText(result.getAddress());
	}

	// 保存地址信息及坐标
	public void ActivitylocationBack(View v) {
		if ("长按地图处设置活动地点".equals(act_address_text.getText().toString())) {
			AppMsg.Style style = AppMsg.STYLE_CONFIRM;
			AppMsg.makeText(this, "请长按地图设置活动地点", style).show();
		} else {
			Intent intent = new Intent();
			intent.putExtra("address", act_address_text.getText().toString());
			intent.putExtra("coordinate", act_coordinate);
			setResult(LocationInfoBack, intent);
			finish();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ("长按地图处设置活动地点".equals(act_address_text.getText().toString())) {
				AppMsg.Style style = AppMsg.STYLE_CONFIRM;
				AppMsg.makeText(this, "请长按地图设置活动地点", style).show();
			} else {
				Intent intent = new Intent();
				intent.putExtra("address", act_address_text.getText()
						.toString());
				intent.putExtra("coordinate", act_coordinate);
				setResult(LocationInfoBack, intent);
				finish();
			}
		}
		return super.onKeyDown(keyCode, event);

	}


	
}
