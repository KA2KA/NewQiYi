package com.newqiyi.util;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.newqiyi.domain.Praise;

/**
 * 点赞功能
 * 
 * @author 李晶
 * 
 */
public class PraiseUtil {
	/**
	 * 点赞
	 * 
	 * @param http
	 *            HttpUtils
	 * @param context
	 *            上下文
	 * @param pra
	 *            Praise对象
	 */
	public void insertPraise(HttpUtils http, final Context context, Praise pra) {
		String url = URLUtil.BASE_URL + "servlet/PraiseInsertServlet";
		Gson gson = new Gson();
		String json = gson.toJson(pra);
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("pra", json);
		http.send(HttpMethod.GET, url, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException error, String msg) {
				Toast.makeText(context, error.getMessage() + ":" + msg,
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSuccess(ResponseInfo<String> resp) {
				String result = resp.result;
				Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * 取消赞
	 * 
	 * @param http
	 *            HttpUtils
	 * @param context
	 *            上下文
	 * @param me_id
	 *            用户id
	 * @param other_id
	 *            被赞用户id
	 */
	public void deletePraise(HttpUtils http, final Context context, int me_id,
			int com_id) {
		String url = URLUtil.BASE_URL + "servlet/PraiseDeleteServlet?me_id="
				+ me_id + "&com_id=" + com_id;
		http.send(HttpMethod.GET, url, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException error, String msg) {
				Toast.makeText(context, error.getMessage() + ":" + msg,
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSuccess(ResponseInfo<String> resp) {
				String result = resp.result;
				Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
			}
		});
	}
}
