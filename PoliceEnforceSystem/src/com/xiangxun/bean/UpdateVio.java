package com.xiangxun.bean;

import com.xiangxun.app.XiangXunApplication;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/4/12.
 */

public class UpdateVio implements Serializable {
	public int isupfile = 0;
	public String type;		// 违法类型  "simple","enforce","park"
	public String datetime;	// 违法时间
	public String sn;		// 文书编号
	public String vio;		// 违法数据Josn串

	public Object getVioData() {
		if ("simple".equals(type))
			return XiangXunApplication.getGson().fromJson(vio, SimpleVio.class);
		else if ("enforce".equals(type))
			return XiangXunApplication.getGson().fromJson(vio, EnforceVio.class);
		else if ("park".equals(type))
			return XiangXunApplication.getGson().fromJson(vio, ParkVio.class);
		return null;
	}
}
