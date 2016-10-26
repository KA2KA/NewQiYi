package com.newqiyi.domain;

import java.util.List;

/**
 * @author KaKa
 * @E-mail:wuwanggao@163.com
 * @version 创建时间：2015-10-18 下午1:35:53
 */
public class ProsToAD {

	public All data;

	public class All {

		public List<Project> pros;// 项目的集合

		public List<Ad> ads;// 广告轮播的数据

//		 public String more;//加载更多
	}


	public class Project {
		// 项目标识、记录在本地，判断是否已读
		public String pro_id;// 项目号
		public String pro_title;// 项目标题
		public String pro_pic;// 项目图片
		public String content;// 项目内容
		public String start_time;// 项目开始时间
		public String end_time;// 结束时间
		public String target_count;// 项目总启币数
		public String pre_qibi_count;// 当前启币数
		public String join_count;// 参与人数
		public String pro_status;// 项目状态
		// url关联详情页请求地址 html------>WebView(加载HTML网页)
		public String pro_url;// 项目内容的URL

		public String pro_goods;// 捐赠物资
		public String pro_rec;// 接收方
		public String pro_donors;// 捐赠方
		public String hold_id;// 发起方id（管理员1）
		// public Admin admin;
//		public boolean isRead;

	}

	public class Ad {
		public String ad_id;
		public String ad_title;
		public String ad_img;
		public String ad_url;
	}

}
