package com.newqiyi.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * @author KaKa
 * @E-mail:wuwanggao@163.com
 * @version 创建时间：2015-10-18 下午4:47:27
 * @param <T>
 */
public abstract class QIYIAdapter<T> extends BaseAdapter {
	public Context context;
	public List<T> list;

	public QIYIAdapter(Context context, List<T> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public abstract View getView(int position, View convertView,
			ViewGroup parent);

}
