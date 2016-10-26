package com.newqiyi.domain;

import java.io.Serializable;

public class QiyiActivity implements Serializable {

	private static final long serialVersionUID = 1L;
	private int act_id;// 活动id
	private String act_type;// 活动类型
	private String act_title;// 活动标题
	private String act_content;// 活动介绍（内容）
	private String hold_time;// 发起活动时间
	private String start_time;// 活动开始时间
	private String picture;// 活动图片
	private int act_status;// 活动状态
	private String end_time;// 活动结束时间
	private int join_count;// 参加活动人数
	private int praise;// 活动收到的赞
	private String tel;// 发起人手机号码
	private String addr;// 活动地点
	private int hold_user_id;// 发起人id
	private String coordinate;// 活动坐标

	public QiyiActivity() {

	}

	public QiyiActivity(int act_id, String act_type, String act_title,
			String act_content, String hold_time, String start_time,
			String picture, int act_status, String end_time, int join_count,
			String tel, String addr, int hold_user_id, String coordinate) {
		super();
		this.act_id = act_id;
		this.act_type = act_type;
		this.act_title = act_title;
		this.act_content = act_content;
		this.hold_time = hold_time;
		this.start_time = start_time;
		this.picture = picture;
		this.act_status = act_status;
		this.end_time = end_time;
		this.join_count = join_count;
		this.tel = tel;
		this.addr = addr;
		this.hold_user_id = hold_user_id;
		this.coordinate = coordinate;
	}

	public String getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(String coordinate) {
		this.coordinate = coordinate;
	}

	public int getAct_id() {
		return act_id;
	}

	public void setAct_id(int act_id) {
		this.act_id = act_id;
	}

	public String getAct_type() {
		return act_type;
	}

	public void setAct_type(String act_type) {
		this.act_type = act_type;
	}

	public String getAct_title() {
		return act_title;
	}

	public void setAct_title(String act_title) {
		this.act_title = act_title;
	}

	public String getAct_content() {
		return act_content;
	}

	public void setAct_content(String act_content) {
		this.act_content = act_content;
	}

	public String getHold_time() {
		return hold_time;
	}

	public void setHold_time(String hold_time) {
		this.hold_time = hold_time;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public int getAct_status() {
		return act_status;
	}

	public void setAct_status(int act_status) {
		this.act_status = act_status;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	public int getJoin_count() {
		return join_count;
	}

	public void setJoin_count(int join_count) {
		this.join_count = join_count;
	}

	public int getPraise() {
		return praise;
	}

	public void setPraise(int praise) {
		this.praise = praise;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public int getHold_user_id() {
		return hold_user_id;
	}

	public void setHold_user_id(int hold_user_id) {
		this.hold_user_id = hold_user_id;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public QiyiActivity(String act_type, String act_title, String act_content,
			String hold_time, String start_time, String picture,
			int act_status, String end_time, int join_count, int praise,
			String tel, String addr, int hold_user_id) {
		super();
		this.act_type = act_type;
		this.act_title = act_title;
		this.act_content = act_content;
		this.hold_time = hold_time;
		this.start_time = start_time;
		this.picture = picture;
		this.act_status = act_status;
		this.end_time = end_time;
		this.join_count = join_count;
		this.praise = praise;
		this.tel = tel;
		this.addr = addr;
		this.hold_user_id = hold_user_id;
	}

}
