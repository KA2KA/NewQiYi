package com.newqiyi.domain;

public class CommentProject {
	private int user_id;
	private int pro_id;
	private int com_id;// 主键
	private String com_content;
	private String com_time;
	private int reply_com_id;
	private String com_type;

	public CommentProject() {

	}

	public CommentProject(int com_id, int user_id, int pro_id,
			String com_content, String com_time, int reply_com_id,
			String com_type) {
		super();
		this.user_id = user_id;
		this.pro_id = pro_id;
		this.com_id = com_id;
		this.com_content = com_content;
		this.com_time = com_time;
		this.reply_com_id = reply_com_id;
		this.com_type = com_type;
	}

	public CommentProject(int user_id, int pro_id, String com_content,
			String com_time, int reply_com_id, String com_type) {
		super();
		this.user_id = user_id;
		this.pro_id = pro_id;
		this.com_content = com_content;
		this.com_time = com_time;
		this.reply_com_id = reply_com_id;
		this.com_type = com_type;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public int getPro_id() {
		return pro_id;
	}

	public void setPro_id(int pro_id) {
		this.pro_id = pro_id;
	}

	public int getCom_id() {
		return com_id;
	}

	public void setCom_id(int com_id) {
		this.com_id = com_id;
	}

	public String getCom_content() {
		return com_content;
	}

	public void setCom_content(String com_content) {
		this.com_content = com_content;
	}

	public String getCom_time() {
		return com_time;
	}

	public void setCom_time(String com_time) {
		this.com_time = com_time;
	}

	public int getReply_com_id() {
		return reply_com_id;
	}

	public void setReply_com_id(int reply_com_id) {
		this.reply_com_id = reply_com_id;
	}

	public String getCom_type() {
		return com_type;
	}

	public void setCom_type(String com_type) {
		this.com_type = com_type;
	}

}
