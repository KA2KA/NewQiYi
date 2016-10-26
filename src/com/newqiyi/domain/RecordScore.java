package com.newqiyi.domain;

import java.util.List;

/**
 * @author KaKa
 * @E-mail:wuwanggao@163.com
 * @version 创建时间：2015-11-17 下午10:51:39
 */
public class RecordScore {

	public List<Dons> don;
	public List<String> titles;

	public class Dons {
		public String don_id;
		public String don_qb_count;
		public String don_time;
		public String pro_id;
		public String user_id;
	}

}
