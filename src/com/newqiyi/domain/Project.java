package com.newqiyi.domain;

/**
 * 
 * @author kaka
 * @E-mail:wuwanggao@163.com
 * @version 创建时间：2015-10-10 下午3:14:32
 */
public class Project {
	private Integer pro_id;// 项目号
	private String pro_title;// 项目标题
	private String pro_pic;// 项目图片
	private String content;// 项目内容
	private String start_time;// 项目开始时间
	private String end_time;// 结束时间
	private Integer target_count;// 项目总启币数
	private Integer pre_qibi_count;// 当前启币数
	private Integer join_count;// 参与人数
	private Integer pro_status;// 项目状态
	private String pro_goods;// 捐赠物资
	private String pro_rec;// 接收方
	private String pro_donors;// 捐赠方
	private String pro_url;// 项目内容的URL
	private Integer hold_id;// 发起方id（管理员1）
	// private Admin admin;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getPro_id() {
		return pro_id;
	}
	public Project(){
		
	}

	public Project(Integer pro_id, String pro_title, String pro_pic,
			String start_time, String end_time, Integer target_count,
			Integer pre_qibi_count, Integer join_count, Integer pro_status,
			String pro_goods, String pro_rec, String pro_donors,
			String pro_uri, Integer hold_id) {
		super();
		this.pro_id = pro_id;
		this.pro_title = pro_title;
		this.pro_pic = pro_pic;
		this.start_time = start_time;
		this.end_time = end_time;
		this.target_count = target_count;
		this.pre_qibi_count = pre_qibi_count;
		this.join_count = join_count;
		this.pro_status = pro_status;
		this.pro_goods = pro_goods;
		this.pro_rec = pro_rec;
		this.pro_donors = pro_donors;
		this.pro_url = pro_uri;
		this.hold_id = hold_id;
	}

	public Project(Integer pro_id, String pro_title, String pro_pic,
			String start_time, String end_time, Integer target_count,
			String pro_goods, String pro_rec, String pro_donors) {
		super();
		this.pro_id = pro_id;
		this.pro_title = pro_title;
		this.pro_pic = pro_pic;
		this.start_time = start_time;
		this.end_time = end_time;
		this.target_count = target_count;
		this.pro_goods = pro_goods;
		this.pro_rec = pro_rec;
		this.pro_donors = pro_donors;
	}

	public Project(String pro_title, String pro_pic, String content,
			String start_time, String end_time, Integer target_count,
			String pro_goods, String pro_rec, String pro_donors) {
		super();
		this.pro_title = pro_title;
		this.pro_pic = pro_pic;
		this.content = content;
		this.start_time = start_time;
		this.end_time = end_time;
		this.target_count = target_count;
		this.pro_goods = pro_goods;
		this.pro_rec = pro_rec;
		this.pro_donors = pro_donors;
	}

	public Project(Integer pro_id) {
		this.pro_id = pro_id;
	}

	public Project(String pro_title, String pro_pic, String content,
			String start_time, String end_time, Integer target_count,
			String pro_goods, String pro_rec, String pro_donors, String pro_url) {
		super();
		this.pro_title = pro_title;
		this.pro_pic = pro_pic;
		this.content = content;
		this.start_time = start_time;
		this.end_time = end_time;
		this.target_count = target_count;
		this.pro_goods = pro_goods;
		this.pro_rec = pro_rec;
		this.pro_donors = pro_donors;
		this.pro_url = pro_url;

	}

	public Project(String pro_title, String pro_pic, String content,
			String end_time, Integer target_count, String pro_goods,
			String pro_rec, String pro_donors, String pro_url) {
		super();
		this.pro_title = pro_title;
		this.pro_pic = pro_pic;
		this.content = content;
		this.end_time = end_time;
		this.target_count = target_count;
		this.pro_goods = pro_goods;
		this.pro_rec = pro_rec;
		this.pro_donors = pro_donors;
		this.pro_url = pro_url;
	}

	public Project(Integer pro_id, String pro_title, String pro_pic,
			String end_time, Integer target_count, Integer pre_qibi_count,
			Integer join_count) {
		super();
		this.pro_id = pro_id;
		this.pro_title = pro_title;
		this.pro_pic = pro_pic;
		this.end_time = end_time;
		this.target_count = target_count;
		this.pre_qibi_count = pre_qibi_count;
		this.join_count = join_count;
	}


	public Project(Integer pro_id, String pro_title, String pro_pic,
			String content, String end_time, Integer target_count,
			Integer pre_qibi_count, Integer join_count, String pro_goods,
			String pro_rec, String pro_donors) {
		super();
		this.pro_id = pro_id;
		this.pro_title = pro_title;
		this.pro_pic = pro_pic;
		this.content = content;
		this.end_time = end_time;
		this.target_count = target_count;
		this.pre_qibi_count = pre_qibi_count;
		this.join_count = join_count;
		this.pro_goods = pro_goods;
		this.pro_rec = pro_rec;
		this.pro_donors = pro_donors;
	}


	public Project(String pro_title, String content,String pro_pic, 
			String end_time, Integer target_count, String pro_goods,
			String pro_rec, String pro_donors) {
		super();
		this.pro_title = pro_title;
		this.pro_pic = pro_pic;
		this.content = content;
		this.end_time = end_time;
		this.target_count = target_count;
		this.pro_goods = pro_goods;
		this.pro_rec = pro_rec;
		this.pro_donors = pro_donors;
	}

	public void setPro_id(Integer pro_id) {
		this.pro_id = pro_id;
	}

	public String getPro_title() {
		return pro_title;
	}

	public void setPro_title(String pro_title) {
		this.pro_title = pro_title;
	}

	public String getPro_pic() {
		return pro_pic;
	}

	public void setPro_pic(String pro_pic) {
		this.pro_pic = pro_pic;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	public Integer getTarget_count() {
		return target_count;
	}

	public void setTarget_count(Integer target_count) {
		this.target_count = target_count;
	}

	public Integer getPre_qibi_count() {
		return pre_qibi_count;
	}

	public void setPre_qibi_count(Integer pre_qibi_count) {
		this.pre_qibi_count = pre_qibi_count;
	}

	public Integer getJoin_count() {
		return join_count;
	}

	public void setJoin_count(Integer join_count) {
		this.join_count = join_count;
	}

	public Integer getPro_status() {
		return pro_status;
	}

	public void setPro_status(Integer pro_status) {
		this.pro_status = pro_status;
	}

	public String getPro_goods() {
		return pro_goods;
	}

	public void setPro_goods(String pro_goods) {
		this.pro_goods = pro_goods;
	}

	public String getPro_rec() {
		return pro_rec;
	}

	public void setPro_rec(String pro_rec) {
		this.pro_rec = pro_rec;
	}

	public String getPro_donors() {
		return pro_donors;
	}

	public void setPro_donors(String pro_donors) {
		this.pro_donors = pro_donors;
	}

	public String getPro_url() {
		return pro_url;
	}

	public void setPro_url(String pro_url) {
		this.pro_url = pro_url;
	}

	public Integer getHold_id() {
		return hold_id;
	}

	public void setHold_id(Integer hold_id) {
		this.hold_id = hold_id;
	}

}
