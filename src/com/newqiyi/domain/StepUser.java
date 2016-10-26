package com.newqiyi.domain;

import java.io.Serializable;

/**
 * @author KaKa
 * @E-mail:wuwanggao@163.com
 * @version 创建时间：2015-11-3 上午11:32:32 本地用户(处理计步)
 */
public class StepUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String user_id;

	private int weight;// 体重
	private int sensitivity;// 灵敏度
	private int step_length;// 步长
	private int today_step;// 今日步长

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getSensitivity() {
		return sensitivity;
	}

	public void setSensitivity(int sensitivity) {
		this.sensitivity = sensitivity;
	}

	public int getStep_length() {
		return step_length;
	}

	public void setStep_length(int step_length) {
		this.step_length = step_length;
	}

	public int getToday_step() {
		return today_step;
	}

	public void setToday_step(int today_step) {
		this.today_step = today_step;
	}

}
