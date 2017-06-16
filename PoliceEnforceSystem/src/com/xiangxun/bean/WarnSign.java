package com.xiangxun.bean;

/**
 * Created by Administrator on 2016/10/21.
 */

public class WarnSign {

	/**
	 * yjxh : 6106001916910710
	 * isOk : 1
	 * isDo : 1
	 * policeid : 6106001916910710
	 */

	private String yjxh;
	private int isOk;
	private int isDo;
	private String policeid;
	public int isUpFile = 0;

	public String getYjxh() {
		return yjxh;
	}

	public void setYjxh(String yjxh) {
		this.yjxh = yjxh;
	}

	public int getIsOk() {
		return isOk;
	}

	public void setIsOk(int isOk) {
		this.isOk = isOk;
	}

	public int getIsDo() {
		return isDo;
	}

	public void setIsDo(int isDo) {
		this.isDo = isDo;
	}

	public int getIsUpFile() {
		return isUpFile;
	}

	public void setIsUpFile(int isUpFile) {
		this.isUpFile = isUpFile;
	}

	public String getPoliceid() {
		return policeid;
	}

	public void setPoliceid(String policeid) {
		this.policeid = policeid;
	}
}
