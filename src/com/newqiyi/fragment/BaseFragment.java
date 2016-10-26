package com.newqiyi.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment {
	public View view;

	public Context context;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = getActivity();
	}

	// 需要所有组员都在此方法中构建UI
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = initView(inflater);
		return view;
	}

	// 需要所有组员都在此方法中填充数据
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		initData(savedInstanceState);
		super.onActivityCreated(savedInstanceState);
	}

	public abstract void initData(Bundle saveInstanceState);

	public abstract View initView(LayoutInflater inflater);
}
