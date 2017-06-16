package com.xiangxun.bean;

import com.xiangxun.app.XiangXunApplication;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/4/12.
 */

public class WarnMessage implements Serializable {
	public int isupfile = 0;
	public int step = 0;
	public String yjxh;		// 预警序号
	public String datetime;	// 预警时间
	public String dataInfo;	// 预警消息
	public String signInfo;	// 预警签收
	public String vioInfo;	// 预警表单
	public String ackInfo;	// 预警反馈
	public String picInfo;	// 处置图片


	public WarnData getWarnData() {
		WarnData wranData = XiangXunApplication.getGson().fromJson(dataInfo, WarnData.class);
		return wranData;
	}

	public WarnAck getWarnSign() {
		WarnAck wranSign = XiangXunApplication.getGson().fromJson(signInfo, WarnAck.class);
		return wranSign;
	}

	public WarnAck getWarnAck() {
		WarnAck wranAck = XiangXunApplication.getGson().fromJson(ackInfo, WarnAck.class);
		return wranAck;
	}

	public WarnPic getWarnPic() {
		WarnPic wranPic = XiangXunApplication.getGson().fromJson(picInfo, WarnPic.class);
		return wranPic;
	}

	public SimpleVio getSimpleVio() {
		SimpleVio simpleVio = XiangXunApplication.getGson().fromJson(vioInfo, SimpleVio.class);
		return simpleVio;
	}

	public EnforceVio getEnforceVio() {
		EnforceVio enforceVio = XiangXunApplication.getGson().fromJson(vioInfo, EnforceVio.class);
		return enforceVio;
	}
}
