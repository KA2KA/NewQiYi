package com.newqiyi.domain;

public class JoinActivity {
	private int act_id;
	private int user_id;
	private String join_intro;
	private String join_time;
	private String user_tel;
	public JoinActivity(int act_id, int user_id, String join_intro,
			String join_time, String user_tel) {
		super();
		this.act_id = act_id;
		this.user_id = user_id;
		this.join_intro = join_intro;
		this.join_time = join_time;
		this.user_tel = user_tel;
	}
	
	public JoinActivity() {
		super();
	}

	public int getAct_id() {
		return act_id;
	}
	public void setAct_id(int act_id) {
		this.act_id = act_id;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public String getJoin_intro() {
		return join_intro;
	}
	public void setJoin_intro(String join_intro) {
		this.join_intro = join_intro;
	}
	public String getJoin_time() {
		return join_time;
	}
	public void setJoin_time(String join_time) {
		this.join_time = join_time;
	}
	public String getUser_tel() {
		return user_tel;
	}
	public void setUser_tel(String user_tel) {
		this.user_tel = user_tel;
	}

}
