package com.xiangxun.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/21.
 */

public class WarnData  implements Serializable {

	/**
	 * yjxh : 6106001916910710
	 * yjsj : 2016-10-18 10:15:43
	 * ywlb : 1
	 * bklx : A
	 * bkzlx : 1
	 * gcxh : 610600100208435602
	 * kkbh : 610600100281
	 * kkmc : monitorname
	 * fxlx : 02
	 * hpzl : 02
	 * hphm : SJF3555
	 * gcsj : 2016-10-18 10:15:02
	 */
	private String yjxh; //预警序号
	private String yjsj;// 预警时间
	private String ywlb; // 业务类别
	private String bklx; // 布控类型
	private String bkzlx; // 布控子类型
	private String gcxh; // 过车序号
	private String kkbh; // 卡口编号
	private String kkmc; // 卡口名称
	private String fxlx; // 方向类型
	private String hpzl; // 号牌种类
	private String hphm; // 号牌号码
	private String gcsj; // 过车时间
	private String imageurl; //
	private int isOk;
	private int isDo;
	private int isUpFile;
	private int isAck;

	public String getYjxh() {
		return yjxh;
	}

	public void setYjxh(String yjxh) {
		this.yjxh = yjxh;
	}

	public String getYjsj() {
		return yjsj;
	}

	public void setYjsj(String yjsj) {
		this.yjsj = yjsj;
	}

	public String getYwlb() {
		return ywlb;
	}

	public void setYwlb(String ywlb) {
		this.ywlb = ywlb;
	}

	public String getBklx() {
		return bklx;
	}

	public void setBklx(String bklx) {
		this.bklx = bklx;
	}

	public String getBkzlx() {
		return bkzlx;
	}

	public void setBkzlx(String bkzlx) {
		this.bkzlx = bkzlx;
	}

	public String getGcxh() {
		return gcxh;
	}

	public void setGcxh(String gcxh) {
		this.gcxh = gcxh;
	}

	public String getKkbh() {
		return kkbh;
	}

	public void setKkbh(String kkbh) {
		this.kkbh = kkbh;
	}

	public String getKkmc() {
		return kkmc;
	}

	public void setKkmc(String kkmc) {
		this.kkmc = kkmc;
	}

	public String getFxlx() {
		return fxlx;
	}

	public void setFxlx(String fxlx) {
		this.fxlx = fxlx;
	}

	public String getHpzl() {
		return hpzl;
	}

	public void setHpzl(String hpzl) {
		this.hpzl = hpzl;
	}

	public String getHphm() {
		return hphm;
	}

	public void setHphm(String hphm) {
		this.hphm = hphm;
	}

	public String getGcsj() {
		return gcsj;
	}

	public void setGcsj(String gcsj) {
		this.gcsj = gcsj;
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

	public int getIsAck() {
		return isAck;
	}

	public void setIsAck(int isAck) {
		this.isAck = isAck;
	}

	public String getImageurl() {
		return imageurl;
	}

	public void setImageurl(String imageurl) {
		this.imageurl = imageurl;
	}
}
