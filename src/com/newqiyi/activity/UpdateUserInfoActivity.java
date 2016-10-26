package com.newqiyi.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.newqiyi.domain.User;
import com.newqiyi.fragment.HomeFragment;
import com.newqiyi.userupdateview.JudgeDate;
import com.newqiyi.userupdateview.MyAlertDialog;
import com.newqiyi.userupdateview.ScreenInfo;
import com.newqiyi.userupdateview.WheelMain;
import com.newqiyi.util.URLUtil;
import com.newqiyi.view.CircleImageView;
import com.newqiyi.view.wheelcity.AddressData;
import com.newqiyi.view.wheelcity.OnWheelChangedListener;
import com.newqiyi.view.wheelcity.WheelView;
import com.newqiyi.view.wheelcity.adapters.AbstractWheelTextAdapter;
import com.newqiyi.view.wheelcity.adapters.ArrayWheelAdapter;
import com.newqiyi.widget.ActionSheetDialog;
import com.newqiyi.widget.ActionSheetDialog.OnSheetItemClickListener;
import com.newqiyi.widget.ActionSheetDialog.SheetItemColor;
import com.newqiyi.widget.AppMsg;
import com.newqiyi.widget.AppMsg.Style;

/**
 * 
 * @author 赵文强
 * 
 */
@SuppressLint("SdCardPath")
public class UpdateUserInfoActivity extends Activity {
	@ViewInject(R.id.user_avatar_image)
	private CircleImageView user_avatar_image;
	@ViewInject(R.id.user_name_text)
	private TextView user_name_text;
	@ViewInject(R.id.user_address_text)
	private TextView user_address_text;
	@ViewInject(R.id.user_sex_text)
	private TextView user_sex_text;
	@ViewInject(R.id.user_signature_text)
	private TextView user_signature_text;
	@ViewInject(R.id.user_birthday_text)
	private TextView user_birthday_text;
	WheelMain wheelMain;
	private String imageName;
	private String user_address_string;
	private static final int PHOTO_REQUEST_TAKEPHOTO = 11;// 拍照
	private static final int PHOTO_REQUEST_GALLERY = 22;// 从相册中选择
	private static final int PHOTO_REQUEST_CUT = 33;// 结果
	private static final int UpdateNameCode = 44;
	private static final int UserNameOk = 55;
	private static final int UpdateSignatureCode = 66;
	private static final int UserSignatureOk = 77;
	private User mySelf;
	@SuppressLint("SimpleDateFormat")
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_user_info);
		ViewUtils.inject(this);
		mySelf = MyApplication.getInstance().getUser();
		initData();
		initView();
	}

	public void initData() {
		String url = URLUtil.BASE_URL
				+ "servlet/QueryUserIdServlet?holdUserId="
				+ mySelf.getUser_id();
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.GET, url, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				Gson gson = new Gson();
				Type type = new TypeToken<User>() {
				}.getType();
				mySelf = gson.fromJson(arg0.result, type);
				if (mySelf.getUser_name() != null) {
					user_name_text.setText(mySelf.getUser_name());
				}
				if (mySelf.getUser_position() != null) {
					user_address_text.setText(mySelf.getUser_position());
				}
				if (mySelf.getBirthday() != null) {
					user_birthday_text.setText(mySelf.getBirthday());
				} else {
					Calendar calendar = Calendar.getInstance();
					user_birthday_text.setText(calendar.get(Calendar.YEAR)
							+ "-" + (calendar.get(Calendar.MONTH) + 1) + "-"
							+ calendar.get(Calendar.DAY_OF_MONTH) + "");
				}
				if (mySelf.getSex() != null) {
					user_sex_text.setText(mySelf.getSex());
				}
				if (mySelf.getSignname() != null) {
					user_signature_text.setText(mySelf.getSignname());
				}
				if (mySelf.getPhoto() != null) {
					BitmapUtils bitmapUtils = new BitmapUtils(
							UpdateUserInfoActivity.this);
					bitmapUtils.display(user_avatar_image, URLUtil.BASE_URL
							+ mySelf.getPhoto());
				}
			}
		});
	}

	public void initView() {

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case PHOTO_REQUEST_TAKEPHOTO:
			startPhotoZoom(Uri.fromFile(new File("/sdcard/qiyi/", imageName)),
					480);
			break;
		case PHOTO_REQUEST_GALLERY:
			if (data != null)
				startPhotoZoom(data.getData(), 480);
			break;
		case PHOTO_REQUEST_CUT:
			Bitmap bitmap = BitmapFactory.decodeFile("/sdcard/qiyi/"
					+ imageName);
			user_avatar_image.setImageBitmap(bitmap);
			break;
		case UpdateNameCode:
			if (resultCode == UserNameOk) {
				user_name_text.setText(data.getStringExtra("newName"));
			}
			break;
		case UpdateSignatureCode:
			if (resultCode == UserSignatureOk) {
				user_signature_text
						.setText(data.getStringExtra("newSignature"));
			}
			break;
		}
	}

	// 更改用户头像
	public void updateUserAvatar(View v) {
		new ActionSheetDialog(UpdateUserInfoActivity.this)
				.builder()
				.setCancelable(true)
				.setCanceledOnTouchOutside(true)
				.addSheetItem("拍照更换头像", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								File qiyi = new File("/sdcard/qiyi");
								if (!qiyi.exists()) {
									qiyi.mkdir();
								}
								imageName = Calendar.getInstance()
										.getTimeInMillis() + ".jpg";
								Intent intent = new Intent(
										MediaStore.ACTION_IMAGE_CAPTURE);
								// 指定调用相机拍照后照片的储存路径
								intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
										.fromFile(new File("/sdcard/qiyi/",
												imageName)));
								startActivityForResult(intent,
										PHOTO_REQUEST_TAKEPHOTO);

							}
						})
				.addSheetItem("去相册更换头像", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								File qiyi = new File("/sdcard/qiyi");
								if (!qiyi.exists()) {
									qiyi.mkdir();
								}
								imageName = Calendar.getInstance()
										.getTimeInMillis() + ".jpg";
								Intent intent = new Intent(Intent.ACTION_PICK,
										null);
								intent.setDataAndType(
										MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
										"image/*");
								startActivityForResult(intent,
										PHOTO_REQUEST_GALLERY);
							}
						}).setCanceledOnTouchOutside(true).show();
	}

	// 更改用户名
	public void updateUserName(View v) {
		Intent intent = new Intent(this, UpdateNameActivity.class);
		startActivityForResult(intent, UpdateNameCode);
	}

	// 更改生日
	public void updateUserBirthday(View v) {
		LayoutInflater inflater = LayoutInflater
				.from(UpdateUserInfoActivity.this);
		final View timepickerview = inflater.inflate(R.layout.timepicker, null);
		ScreenInfo screenInfo = new ScreenInfo(UpdateUserInfoActivity.this);
		wheelMain = new WheelMain(timepickerview);
		wheelMain.screenheight = screenInfo.getHeight();
		String time = user_birthday_text.getText().toString();
		Calendar calendar = Calendar.getInstance();
		if (JudgeDate.isDate(time, "yyyy-MM-dd")) {
			try {
				calendar.setTime(dateFormat.parse(time));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		wheelMain.initDateTimePicker(year, month, day);
		final MyAlertDialog dialog = new MyAlertDialog(
				UpdateUserInfoActivity.this).builder().setTitle("选择时间")
				.setView(timepickerview)
				.setNegativeButton("取消", new OnClickListener() {
					@Override
					public void onClick(View v) {
					}
				});
		dialog.setPositiveButton("保存", new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), wheelMain.getTime(), 1)
						.show();
				user_birthday_text.setText(wheelMain.getTime());
			}
		});
		dialog.show();
	}

	public void userInfoBack(View v) {
		finish();
	}

	// 更改用户地区
	public void updateUserAddress(View v) {
		View view = dialogm();
		final MyAlertDialog dialog1 = new MyAlertDialog(
				UpdateUserInfoActivity.this).builder().setTitle("选择地区")
				.setView(view).setNegativeButton("取消", new OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				});
		dialog1.setPositiveButton("保存", new OnClickListener() {
			@Override
			public void onClick(View v) {
				user_address_text.setText(user_address_string);
			}
		});
		dialog1.show();
	}

	// 更改用户性别
	public void updateUserSex(View v) {
		new ActionSheetDialog(UpdateUserInfoActivity.this)
				.builder()
				.setCancelable(true)
				.setCanceledOnTouchOutside(true)
				.addSheetItem("男", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								user_sex_text.setText("男");

							}
						})
				.addSheetItem("女", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								user_sex_text.setText("女");
							}
						}).setCanceledOnTouchOutside(true).show();
	}

	// 更改用户个性签名
	public void updateUserSignature(View v) {
		Intent intent = new Intent(this, UpdateSignatureActivity.class);
		startActivityForResult(intent, UpdateSignatureCode);
	}

	// 上传保存信息到服务器
	public void uploadInfo(View v) {
		if (!user_name_text.getText().toString().equals("点击修改")) {
			mySelf.setUser_name(user_name_text.getText().toString());
		}
		if (!user_address_text.getText().toString().equals("点击设置")) {
			mySelf.setUser_position(user_address_text.getText().toString());
		}
		mySelf.setBirthday(user_birthday_text.getText().toString());
		if (!user_sex_text.getText().toString().equals("点击修改")) {
			mySelf.setSex(user_sex_text.getText().toString());
		}
		if (user_signature_text.getText().toString() != null) {
			mySelf.setSignname(user_signature_text.getText().toString());
		}
		Gson gson = new Gson();
		String userInfo = gson.toJson(mySelf);
		try {
			userInfo = URLEncoder.encode(userInfo, "utf-8");
			RequestParams params = new RequestParams("utf-8");
			params.addBodyParameter("userInfo", userInfo);
			params.addBodyParameter("useravatar", new File("//sdcard/qiyi/"
					+ imageName));
			HttpUtils httpUtils = new HttpUtils();
			String url = URLUtil.BASE_URL
					+ "servlet/UserUpdateServlet?user_id="
					+ mySelf.getUser_id();
			httpUtils.send(HttpRequest.HttpMethod.POST, url, params,
					new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							// TODO Auto-generated method stub
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							// TODO Auto-generated method stub
							if ("更新用户信息成功".equals(arg0.result)) {
								Style style = AppMsg.STYLE_INFO;
								AppMsg.makeText(UpdateUserInfoActivity.this,
										"保存成功", style).show();
								Intent intent = new Intent(
										UpdateUserInfoActivity.this,
										MainActivity.class);
								intent.putExtra("page", "3");
								startActivity(intent);
							} else {
								Style style = AppMsg.STYLE_CONFIRM;
								AppMsg.makeText(UpdateUserInfoActivity.this,
										"保存失败，请重试", style).show();
							}

						}
					});
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void startPhotoZoom(Uri uri1, int size) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri1, "image/*");
		// crop为true是设置在开启的intent中设置显示的view可以剪裁
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX,outputY 是剪裁图片的宽高
		intent.putExtra("outputX", size);
		intent.putExtra("outputY", size);
		intent.putExtra("return-data", false);

		intent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File("/sdcard/qiyi/", imageName)));
		intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}

	public Bitmap decodeUriAsBitmap(Uri uri) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(getContentResolver()
					.openInputStream(uri));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}

	private View dialogm() {
		View contentView = LayoutInflater.from(this).inflate(
				R.layout.wheelcity_cities_layout, null);
		final WheelView country = (WheelView) contentView
				.findViewById(R.id.wheelcity_country);
		country.setVisibleItems(3);
		country.setViewAdapter(new CountryAdapter(this));

		final String cities[][] = AddressData.CITIES;
		final String ccities[][][] = AddressData.COUNTIES;
		final WheelView city = (WheelView) contentView
				.findViewById(R.id.wheelcity_city);
		city.setVisibleItems(0);

		// 地区选择
		final WheelView ccity = (WheelView) contentView
				.findViewById(R.id.wheelcity_ccity);
		ccity.setVisibleItems(0);// 不限城市

		country.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				updateCities(city, cities, newValue);
				user_address_string = AddressData.PROVINCES[country
						.getCurrentItem()]
						+ "   "
						+ AddressData.CITIES[country.getCurrentItem()][city
								.getCurrentItem()]
						+ "   "
						+ AddressData.COUNTIES[country.getCurrentItem()][city
								.getCurrentItem()][ccity.getCurrentItem()];
			}
		});

		city.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				updatecCities(ccity, ccities, country.getCurrentItem(),
						newValue);
				user_address_string = AddressData.PROVINCES[country
						.getCurrentItem()]
						+ "   "
						+ AddressData.CITIES[country.getCurrentItem()][city
								.getCurrentItem()]
						+ "   "
						+ AddressData.COUNTIES[country.getCurrentItem()][city
								.getCurrentItem()][ccity.getCurrentItem()];
			}
		});

		ccity.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				user_address_string = AddressData.PROVINCES[country
						.getCurrentItem()]
						+ "   "
						+ AddressData.CITIES[country.getCurrentItem()][city
								.getCurrentItem()]
						+ "   "
						+ AddressData.COUNTIES[country.getCurrentItem()][city
								.getCurrentItem()][ccity.getCurrentItem()];
			}
		});

		country.setCurrentItem(1);// 设置北京
		city.setCurrentItem(1);
		ccity.setCurrentItem(1);
		return contentView;
	}

	/**
	 * Updates the city wheel
	 */
	private void updateCities(WheelView city, String cities[][], int index) {
		ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(this,
				cities[index]);
		adapter.setTextSize(18);
		city.setViewAdapter(adapter);
		city.setCurrentItem(0);
	}

	/**
	 * Updates the ccity wheel
	 */
	private void updatecCities(WheelView city, String ccities[][][], int index,
			int index2) {
		ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(this,
				ccities[index][index2]);
		adapter.setTextSize(18);
		city.setViewAdapter(adapter);
		city.setCurrentItem(0);
	}

	/**
	 * Adapter for countries
	 */
	private class CountryAdapter extends AbstractWheelTextAdapter {
		// Countries names
		private String countries[] = AddressData.PROVINCES;

		/**
		 * Constructor
		 */
		protected CountryAdapter(Context context) {
			super(context, R.layout.wheelcity_country_layout, NO_RESOURCE);
			setItemTextResource(R.id.wheelcity_country_name);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			return view;
		}

		@Override
		public int getItemsCount() {
			return countries.length;
		}

		@Override
		protected CharSequence getItemText(int index) {
			return countries[index];
		}
	}
}
