package com.newqiyi.domain;

import java.io.Serializable;

/**
 * @author KaKa
 * @E-mail:wuwanggao@163.com
 * @version ����ʱ�䣺2015-11-3 ����11:32:32 �����û�(����Ʋ�)
 */
public class StepUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String user_id;

	private int weight;// ����
	private int sensitivity;// ������
	private int step_length;// ����
	private int today_step;// ���ղ���

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
