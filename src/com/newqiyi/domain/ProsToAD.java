package com.newqiyi.domain;

import java.util.List;

/**
 * @author KaKa
 * @E-mail:wuwanggao@163.com
 * @version ����ʱ�䣺2015-10-18 ����1:35:53
 */
public class ProsToAD {

	public All data;

	public class All {

		public List<Project> pros;// ��Ŀ�ļ���

		public List<Ad> ads;// ����ֲ�������

//		 public String more;//���ظ���
	}


	public class Project {
		// ��Ŀ��ʶ����¼�ڱ��أ��ж��Ƿ��Ѷ�
		public String pro_id;// ��Ŀ��
		public String pro_title;// ��Ŀ����
		public String pro_pic;// ��ĿͼƬ
		public String content;// ��Ŀ����
		public String start_time;// ��Ŀ��ʼʱ��
		public String end_time;// ����ʱ��
		public String target_count;// ��Ŀ��������
		public String pre_qibi_count;// ��ǰ������
		public String join_count;// ��������
		public String pro_status;// ��Ŀ״̬
		// url��������ҳ�����ַ html------>WebView(����HTML��ҳ)
		public String pro_url;// ��Ŀ���ݵ�URL

		public String pro_goods;// ��������
		public String pro_rec;// ���շ�
		public String pro_donors;// ������
		public String hold_id;// ����id������Ա1��
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
