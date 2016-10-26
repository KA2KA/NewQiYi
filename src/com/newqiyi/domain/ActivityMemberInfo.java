package com.newqiyi.domain;

public class ActivityMemberInfo {
	public String UserInfo;
	public String MemberInfo;

	public String getUserInfo() {
		return UserInfo;
	}

	public void setUserInfo(String userInfo) {
		UserInfo = userInfo;
	}

	public String getMemberInfo() {
		return MemberInfo;
	}

	public void setMemberInfo(String memberInfo) {
		MemberInfo = memberInfo;
	}

	public ActivityMemberInfo(String userInfo, String memberInfo) {
		super();
		UserInfo = userInfo;
		MemberInfo = memberInfo;
	}

}
