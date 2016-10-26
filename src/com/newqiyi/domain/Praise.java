package com.newqiyi.domain;

public class Praise {
	private int id;
	private int me_id;
	private int com_id;
	public Praise(int me_id,int com_id){
		this.me_id=me_id;
		this.com_id=com_id;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getMe_id() {
		return me_id;
	}
	public void setMe_id(int me_id) {
		this.me_id = me_id;
	}
	public int getCom_id() {
		return com_id;
	}
	public void setCom_id(int com_id) {
		this.com_id = com_id;
	}
	
	
}
