package com.newqiyi.datepicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.newqiyi.activity.R;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.LinearLayout.LayoutParams;

public class DatePickerPopWindow extends PopupWindow {
	private Context context;
	private String startTime;
	private Date date;
	private int curYear, curMonth;
	private LayoutInflater mInflater;
	private View dateView;
	private WheelView yearView;
	private WheelView monthView;
	private WheelView dayView;
	private WheelView hourView;
	private WheelView minView;
	private WheelView secView;
	public String actStartTime;
	private int[] timeInt;

	public DatePickerPopWindow(Context context, String startTime) {
		this.context = context;
		this.startTime = startTime;
		setStartTime();
		initWindow();
	}

	private void setStartTime() {
		// TODO Auto-generated method stub
		actStartTime = startTime.substring(0, startTime.length() - 3);
		timeInt = new int[6];
		timeInt[0] = Integer.valueOf(startTime.substring(0, 4));
		timeInt[1] = Integer.valueOf(startTime.substring(5, 7));
		timeInt[2] = Integer.valueOf(startTime.substring(8, 10));
		timeInt[3] = Integer.valueOf(startTime.substring(11, 13));
		timeInt[4] = Integer.valueOf(startTime.substring(14, 16));
		timeInt[5] = Integer.valueOf(startTime.substring(17, 19));
	}

	private void initWindow() {
		// TODO Auto-generated method stub
		mInflater = LayoutInflater.from(context);
		dateView = mInflater.inflate(R.layout.wheel_date_picker, null);
		yearView = (WheelView) dateView.findViewById(R.id.actyear);
		monthView = (WheelView) dateView.findViewById(R.id.actmonth);
		dayView = (WheelView) dateView.findViewById(R.id.actday);
		hourView = (WheelView) dateView.findViewById(R.id.acttime);
		minView = (WheelView) dateView.findViewById(R.id.actmin);
		secView = (WheelView) dateView.findViewById(R.id.actsec);
		secView.setVisibility(View.GONE);
		initWheel();
	}

	private void initWheel() {
		// TODO Auto-generated method stub
		Calendar calendar = Calendar.getInstance();
		curYear = calendar.get(Calendar.YEAR);
		curMonth = calendar.get(Calendar.MONTH) + 1;

		NumericWheelAdapter numericWheelAdapter1 = new NumericWheelAdapter(
				context, curYear, curYear + 10);
		numericWheelAdapter1.setLabel("年");
		yearView.setViewAdapter(numericWheelAdapter1);
		yearView.setCyclic(true);
		yearView.addScrollingListener(scrollListener);

		NumericWheelAdapter numericWheelAdapter2 = new NumericWheelAdapter(
				context, 1, 12, "%02d");
		numericWheelAdapter2.setLabel("月");
		monthView.setViewAdapter(numericWheelAdapter2);
		monthView.setCyclic(true);
		monthView.addScrollingListener(scrollListener);

		NumericWheelAdapter numericWheelAdapter3 = new NumericWheelAdapter(
				context, 1, getDay(curYear, curMonth), "%02d");
		numericWheelAdapter3.setLabel("日");
		dayView.setViewAdapter(numericWheelAdapter3);
		dayView.setCyclic(true);
		dayView.addScrollingListener(scrollListener);

		NumericWheelAdapter numericWheelAdapter4 = new NumericWheelAdapter(
				context, 0, 23, "%02d");
		numericWheelAdapter4.setLabel("时");
		hourView.setViewAdapter(numericWheelAdapter4);
		hourView.setCyclic(true);
		hourView.addScrollingListener(scrollListener);

		NumericWheelAdapter numericWheelAdapter5 = new NumericWheelAdapter(
				context, 0, 59, "%02d");
		numericWheelAdapter5.setLabel("分");
		minView.setViewAdapter(numericWheelAdapter5);
		minView.setCyclic(true);
		minView.addScrollingListener(scrollListener);

		yearView.setCurrentItem(timeInt[0] - curYear);
		monthView.setCurrentItem(timeInt[1] - 1);
		dayView.setCurrentItem(timeInt[2] - 1);
		hourView.setCurrentItem(timeInt[3]);
		minView.setCurrentItem(timeInt[4]);
		yearView.setVisibleItems(7);// 设置显示行数
		monthView.setVisibleItems(7);
		dayView.setVisibleItems(7);
		hourView.setVisibleItems(7);
		minView.setVisibleItems(7);
		setContentView(dateView);
		setWidth(LayoutParams.FILL_PARENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		ColorDrawable dw = new ColorDrawable(0xFFFFFFFF);
		setBackgroundDrawable(dw);
		setFocusable(true);
	}

	OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
		@Override
		public void onScrollingStarted(WheelView wheel) {

		}

		@Override
		public void onScrollingFinished(WheelView wheel) {

			int n_year = yearView.getCurrentItem() + curYear;// 年
			int n_month = monthView.getCurrentItem() + 1;// 月

			initDay(n_year, n_month);

			String birthday = new StringBuilder()
					.append((yearView.getCurrentItem() + curYear))
					.append("/")
					.append((monthView.getCurrentItem() + 1) < 10 ? "0"
							+ (monthView.getCurrentItem() + 1) : (monthView
							.getCurrentItem() + 1))
					.append("/")
					.append(((dayView.getCurrentItem() + 1) < 10) ? "0"
							+ (dayView.getCurrentItem() + 1) : (dayView
							.getCurrentItem() + 1)).append("  ")
					.append(((hourView.getCurrentItem()) < 10) ? "0"
							+ (hourView.getCurrentItem()) : (hourView
							.getCurrentItem()))
					.append(":")
					.append(((minView.getCurrentItem()) < 10) ? "0"
							+ (minView.getCurrentItem()) : (minView
							.getCurrentItem())).toString();
			birthday = extractWeek(birthday) + " " + birthday;
			actStartTime = birthday;
		}
	};

	private String extractWeek(String time) {
		final String dayNames[] = { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };
		SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy/MM/dd");
		Calendar calendar = Calendar.getInstance();
		Date date1 = null;
		try {
			date1 = sdfInput.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		calendar.setTime(date1);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		if (dayOfWeek < 0)
			dayOfWeek = 0;
		return dayNames[dayOfWeek];
	}

	private void initDay(int arg1, int arg2) {
		NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(
				context, 1, getDay(arg1, arg2), "%02d");
		numericWheelAdapter.setLabel("日");
		dayView.setViewAdapter(numericWheelAdapter);
	}

	private int getDay(int year, int month) {
		int day = 30;
		boolean flag = false;
		switch (year % 4) {
		case 0:
			flag = true;
			break;
		default:
			flag = false;
			break;
		}
		switch (month) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			day = 31;
			break;
		case 2:
			day = flag ? 29 : 28;
			break;
		default:
			day = 30;
			break;
		}
		return day;
	}
}
