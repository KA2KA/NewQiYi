package com.newqiyi.pager;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public abstract class BasePager {
	protected static final String tag = "BasePager";
	public Context context;
	public View view;
	public TextView txt_title;

	public BasePager(Context context) {
		this.context = context;
		view = initView();
	}

	// 子类中都有UI需要去加载，所以统一放在initView中去做
	public abstract View initView();

	// 子类中都有数据需要去加载，所以统一放在initData中去做
	public abstract void initData();

	// 返回当前界面通过xml布局构建的view
	public View getRootView() {
		return view;
	}

	//
	public void loadData(HttpMethod httpMethod, String url,
			RequestParams params, RequestCallBack<String> callBack) {
		HttpUtils utils = new HttpUtils();
		utils.send(httpMethod, url, params, callBack);
	}

}
