package com.qiyi.releaseactivity;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.newqiyi.activity.ActLocationActivity;
import com.newqiyi.activity.MyApplication;
import com.newqiyi.activity.R;
import com.newqiyi.datepicker.DatePickerPopWindow;
import com.newqiyi.domain.QiyiActivity;
import com.newqiyi.domain.User;
import com.newqiyi.util.ActMapUtils;
import com.newqiyi.util.URLUtil;
import com.newqiyi.widget.ActionSheetDialog;
import com.newqiyi.widget.ActionSheetDialog.OnSheetItemClickListener;
import com.newqiyi.widget.ActionSheetDialog.SheetItemColor;
import com.newqiyi.widget.AlertDialog;
import com.newqiyi.widget.AppMsg;

/**
 * 
 * @author 赵文强
 * 
 */
@SuppressLint({ "SdCardPath", "SimpleDateFormat" })
public class ReleaseActivity extends Activity {
	public AppMsg.Style style = AppMsg.STYLE_CONFIRM;
	private String coordinate;// 活动坐标经纬度
	// public static final String IMAGE_UNSPECIFIED = "image/*";
	// public static final int ALBUM_REQUEST_CODE = 1;
	// public static final int CROP_REQUEST_CODE = 4;
	private static final int ActivityInfoSign = 11;// 用于活动介绍返回读取
	private static final int LocationInfoSign = 33;// 用于地址的返回读取
	public int LocationInfoBack = 44;
	private static final int PHOTO_REQUEST_TAKEPHOTO = 55;// 拍照
	private static final int PHOTO_REQUEST_GALLERY = 66;// 从相册中选择
	private static final int PHOTO_REQUEST_CUT = 77;// 结果
	public static int RESULT_LOAD_POSTER = 55;//
	// public String picturePath;
	// public File CropPoster;// 裁剪海报保存
	private String url = URLUtil.BASE_URL + "/servlet/ActInsertServlet";
	@ViewInject(R.id.release_activity_starttime)
	public TextView release_activity_starttime;// 显示开始时间
	@ViewInject(R.id.release_activity_endtime)
	public TextView release_activity_endtime;// 显示结束时间
	@ViewInject(R.id.edit_activity_text)
	public TextView edit_activity_text;// 活动介绍
	@ViewInject(R.id.activity_kind_text)
	public TextView activity_kind_text;// 活动类型
	@ViewInject(R.id.activity_person_num_text)
	public TextView activity_person_num_text;// 活动人数
	@ViewInject(R.id.locationInfo)
	public TextView locationInfo;// 地点信息
	@ViewInject(R.id.Release_activity_btn)
	public TextView Release_activity_btn;
	@ViewInject(R.id.poster_activity)
	public ImageView poster;// 海报
	@ViewInject(R.id.Release_activity_EditText)
	public EditText Release_activity_EditText;// edittext活动标题
	@ViewInject(R.id.Release_activity_initiatorTel)
	public EditText Release_activity_initiatorTel;// 发起人的手机号码
	public DatePickerPopWindow popWindow;
	public String activityTitle;// 活动标题
	public String ReleaseActivityStartTime;// 活动开始时间
	public String ReleaseActivityEndTime;// 活动结束时间
	public String EditActivityText;// 活动介绍
	public String ActivityKindText;// 活动类型
	public int ActivityPersonNum;// 活动人数
	public String LocationInfo;// 活动地点
	public String InitiatorTel;// 发起人手机号
	public String actInfo;
	public String IMAGE_FILE_LOCATION;
	// public Uri imageUri;
	private String posterName;
	LatLng ptCenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_release);
		ViewUtils.inject(this);
	}

	// 设置返回键动作：返回上一层界面
	public void releaseactivityBack(View v) {
		new AlertDialog(ReleaseActivity.this).builder().setTitle("提醒")
				.setMsg("确定取消发布活动吗")
				.setPositiveButton("确认", new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				}).setNegativeButton("取消", new OnClickListener() {
					@Override
					public void onClick(View v) {
					}
				}).show();
	}

	// 设置活动开始时间
	public void setStartTime(View v) {
		Date date = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		popWindow = new DatePickerPopWindow(ReleaseActivity.this,
				df.format(date));
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = 0.5f;
		getWindow().setAttributes(lp);
		popWindow.showAtLocation(findViewById(R.id.ReleaseActivityLayout),
				Gravity.BOTTOM, 0, 0);
		popWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				release_activity_starttime.setText(popWindow.actStartTime);
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1f;
				getWindow().setAttributes(lp);
			}
		});
	}

	// 设置活动结束时间
	public void setEndTime(View v) {
		Date date = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		popWindow = new DatePickerPopWindow(ReleaseActivity.this,
				df.format(date));
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = 0.5f;
		getWindow().setAttributes(lp);
		popWindow.showAtLocation(findViewById(R.id.ReleaseActivityLayout),
				Gravity.BOTTOM, 0, 0);

		popWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				release_activity_endtime.setText(popWindow.actStartTime);
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1f;
				getWindow().setAttributes(lp);
			}
		});
	}

	// 设置活动地点(百度地图定位)
	public void setLocation(View v) {
		Intent intent = new Intent(this, ActLocationActivity.class);
		startActivityForResult(intent, LocationInfoSign);
	}

	// 设置活动类型
	public void setActivityKind(View v) {
		new ActionSheetDialog(ReleaseActivity.this)
				.builder()
				.setTitle("请选择活动类型")
				.setCancelable(false)
				.setCanceledOnTouchOutside(false)
				.addSheetItem("体育运动", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								activity_kind_text.setText("体育运动");
							}
						})
				.addSheetItem("交友聚会", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								activity_kind_text.setText("交友聚会");
							}
						})
				.addSheetItem("学习交流", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								activity_kind_text.setText("学习交流");
							}
						})
				.addSheetItem("音乐戏剧", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								activity_kind_text.setText("音乐戏剧");
							}
						})
				.addSheetItem("户外旅行", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								activity_kind_text.setText("户外旅行");
							}
						})
				.addSheetItem("讲座公益", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								activity_kind_text.setText("讲座公益");
							}
						})
				.addSheetItem("其他", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								activity_kind_text.setText("其他");
							}
						}).setCanceledOnTouchOutside(true).show();
	}

	// 设置活动人数
	public void setPersonNum(View v) {
		new ActionSheetDialog(ReleaseActivity.this)
				.builder()
				.setTitle("请设置活动人数")
				.setCancelable(false)
				.setCanceledOnTouchOutside(false)
				.addSheetItem("2", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								activity_person_num_text.setText("2");
							}
						})
				.addSheetItem("4", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								activity_person_num_text.setText("4");
							}
						})
				.addSheetItem("6", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								activity_person_num_text.setText("6");
							}
						})
				.addSheetItem("8", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								activity_person_num_text.setText("8");
							}
						})
				.addSheetItem("10", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								activity_person_num_text.setText("10");
							}
						})
				.addSheetItem("16", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								activity_person_num_text.setText("16");
							}
						}).setCanceledOnTouchOutside(true).show();
	}

	// 跳转编辑活动界面
	public void editTextActivity(View v) {
		Intent intent = new Intent(this, ActivityEditText.class);
		String usedText = (String) edit_activity_text.getText();
		intent.putExtra("usedText", usedText);
		startActivityForResult(intent, ActivityInfoSign);

	}

	// 设置活动海报
	public void setActivityPoster(View v) {
		// startCrop();
		new ActionSheetDialog(ReleaseActivity.this)
				.builder()
				.setCancelable(true)
				.setCanceledOnTouchOutside(true)
				.addSheetItem("拍照选择海报", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								File qiyi = new File("/sdcard/qiyi");
								if (!qiyi.exists()) {
									qiyi.mkdir();
								}
								posterName = Calendar.getInstance()
										.getTimeInMillis() + ".jpg";
								Intent intent = new Intent(
										MediaStore.ACTION_IMAGE_CAPTURE);
								// 指定调用相机拍照后照片的储存路径
								intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
										.fromFile(new File("/sdcard/qiyi/",
												posterName)));
								startActivityForResult(intent,
										PHOTO_REQUEST_TAKEPHOTO);

							}
						})
				.addSheetItem("去相册选择海报", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								File qiyi = new File("/sdcard/qiyi");
								if (!qiyi.exists()) {
									qiyi.mkdir();
								}
								posterName = Calendar.getInstance()
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

	private void startPhotoZoom(Uri uri1) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri1, "image/*");
		// crop为true是设置在开启的intent中设置显示的view可以剪裁
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 2);
		intent.putExtra("aspectY", 1);
		// outputX,outputY 是剪裁图片的宽高
		intent.putExtra("scale", true);
		intent.putExtra("outputX", 480);
		intent.putExtra("outputY", 240);
		intent.putExtra("return-data", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File("/sdcard/qiyi/", posterName)));
		intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}

	// 获取编辑活动所返回的内容
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		// 返回活动介绍编辑信息
		switch (requestCode) {
		case PHOTO_REQUEST_TAKEPHOTO:
			startPhotoZoom(Uri.fromFile(new File("/sdcard/qiyi/", posterName)));
			break;
		case PHOTO_REQUEST_GALLERY:
			if (data != null)
				startPhotoZoom(data.getData());
			break;
		case PHOTO_REQUEST_CUT:
			Bitmap bitmap = BitmapFactory.decodeFile("/sdcard/qiyi/"
					+ posterName);
			poster.setImageBitmap(bitmap);
			break;
		case ActivityInfoSign:
			edit_activity_text.setText(data.getStringExtra("result"));
			break;
		case LocationInfoSign:
			if (data == null) {
				return;
			}
			locationInfo.setText(data.getStringExtra("address"));
			coordinate = data.getStringExtra("coordinate");
			String lat = coordinate.substring(0, coordinate.indexOf(","));
			String log = coordinate.substring(coordinate.indexOf(",") + 1);
			ptCenter = new LatLng(Float.valueOf(lat), Float.valueOf(log));
			break;
		}
	}

	// 发布活动
	public void releaseActivity(View v) {
		activityTitle = Release_activity_EditText.getText().toString();
		ReleaseActivityStartTime = release_activity_starttime.getText()
				.toString();
		ReleaseActivityEndTime = release_activity_endtime.getText().toString();
		EditActivityText = edit_activity_text.getText().toString();
		ActivityKindText = activity_kind_text.getText().toString();
		ActivityPersonNum = Integer.parseInt(activity_person_num_text.getText()
				.toString());
		LocationInfo = locationInfo.getText().toString();
		InitiatorTel = Release_activity_initiatorTel.getText().toString();
		if (activityTitle == null || "".equals(activityTitle)) {
			AppMsg appMsg = new AppMsg(ReleaseActivity.this);
			appMsg.setLayoutGravity(500);
			AppMsg.makeText(this, "标题不能为空", style).show();
		} else if (activityTitle.trim().length() < 5) {
			AppMsg.makeText(this, "标题字数不得少于5字", style).show();
		} else if (!InitiatorTel
				.matches("^(13[0-9]|14[0-9]|15[0-9]|18[0-9])\\d{8}$")) {
			AppMsg.makeText(this, "请输入正确的手机号码", style).show();
		} else if (ReleaseActivityStartTime.equals("点击设置")) {
			AppMsg.makeText(this, "请设置活动开始时间", style).show();
		} else if (extractNum(ReleaseActivityStartTime).before(new Date())) {
			AppMsg.makeText(this, "活动开始时间不符合", style).show();
		} else if (ReleaseActivityEndTime.equals("点击设置")) {
			AppMsg.makeText(this, "请设置活动结束时间", style).show();
		} else if (extractNum(ReleaseActivityEndTime).before(
				extractNum(ReleaseActivityStartTime))) {
			AppMsg.makeText(this, "活动结束时间必须晚于活动开始时间", style).show();
		} else if (LocationInfo.equals("点击设置")
				|| LocationInfo.equals("长按地图处设置活动地址")) {
			AppMsg.makeText(this, "请设置活动地点", style).show();
		} else if (ActivityKindText.equals("点击选择")) {
			AppMsg.makeText(this, "请选择活动类型", style).show();
		} else if (ActivityPersonNum == 0) {
			AppMsg.makeText(this, "请设置活动人数", style).show();
		} else if (edit_activity_text == null
				|| "".equals(EditActivityText.trim())) {
			AppMsg.makeText(this, "请编写活动介绍", style).show();
		} else if (EditActivityText.trim().length() < 10) {
			AppMsg.makeText(this, "活动介绍不能少于20字", style).show();
		} else if (poster.getDrawable() == null) {
			AppMsg.makeText(this, "请设置活动海报", style).show();
		} else {
			new Thread() {
				@Override
				public void run() {
					RequestParams params = new RequestParams("utf-8");
					params.addBodyParameter("poster", new File("//sdcard/qiyi/"
							+ posterName));
					actInfo = ToJson();
					try {
						actInfo = URLEncoder.encode(actInfo, "utf-8");// URL编码
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					params.addBodyParameter("actInfo", actInfo);
					HttpUtils httpUtil = new HttpUtils();
					httpUtil.configCurrentHttpCacheExpiry(10000); // 设置缓存时间 毫秒
					httpUtil.send(HttpRequest.HttpMethod.POST, url, params,
							new RequestCallBack<String>() {
								@Override
								public void onFailure(HttpException arg0,
										String arg1) {
									// TODO Auto-generated method stub
									AppMsg.makeText(ReleaseActivity.this,
											"发布失败,请重试", style).show();
								}

								@Override
								public void onSuccess(ResponseInfo<String> arg0) {
									// TODO Auto-generated method stub
									style = AppMsg.STYLE_INFO;
									AppMsg.makeText(ReleaseActivity.this,
											"发布成功", style).show();
									String act_id = arg0.result;
									ActMapUtils actMapUtils = new ActMapUtils(
											"上传活动", act_id, ptCenter);
									actMapUtils
											.startLoaction(ReleaseActivity.this);
									finish();
								}
							});
				};
			}.start();

		}

	}

	// 推送消息
	public void jpushMessage(HttpUtils http, String act_id) {

		String url = URLUtil.BASE_URL + "servlet/JPushServlet?act_id=" + act_id;
		http.send(HttpRequest.HttpMethod.GET, url,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException error, String msg) {

						Toast.makeText(ReleaseActivity.this,
								error.getMessage() + ":" + msg,
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> reps) {

						Toast.makeText(ReleaseActivity.this, reps.result,
								Toast.LENGTH_SHORT).show();
					}
				});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			new AlertDialog(ReleaseActivity.this).builder().setTitle("提醒")
					.setMsg("确定取消发布活动吗")
					.setPositiveButton("确认", new OnClickListener() {
						@Override
						public void onClick(View v) {
							finish();
						}
					}).setNegativeButton("取消", new OnClickListener() {
						@Override
						public void onClick(View v) {
						}
					}).show();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private String ToJson() {
		Gson gson = new Gson();
		User holduser = MyApplication.getInstance().getUser();
		QiyiActivity activity = new QiyiActivity();
		activity.setAct_title(activityTitle);
		activity.setTel(InitiatorTel);
		activity.setStart_time(ReleaseActivityStartTime);
		activity.setEnd_time(ReleaseActivityEndTime);
		activity.setAddr(LocationInfo);
		activity.setAct_type(ActivityKindText);
		activity.setJoin_count(ActivityPersonNum);
		activity.setAct_content(EditActivityText);
		activity.setHold_user_id(Integer.parseInt(holduser.getUser_id()));
		activity.setCoordinate(coordinate);
		String actInfo = gson.toJson(activity);
		return actInfo;
	}

	// 把活动时间字符串转为Date型
	private Date extractNum(String time) {
		String regEx = "[^0-9]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(time);
		String timeNums = m.replaceAll("").trim();
		Date date = null;
		try {

			date = new SimpleDateFormat("yyyyMMddhhmm").parse(timeNums);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}

}
