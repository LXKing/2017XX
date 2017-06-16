package com.xiangxun.bean;

public class AccidentCar {
	private int _id;
	private int cartag;
	private String memo;
	private String ownerid;
	private String platenum;
	private int platetype;

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public int getCartag() {
		return cartag;
	}

	public void setCartag(int cartag) {
		this.cartag = cartag;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getOwnerid() {
		return ownerid;
	}

	public void setOwnerid(String ownerid) {
		this.ownerid = ownerid;
	}

	public String getPlatenum() {
		return platenum;
	}

	public void setPlatenum(String platenum) {
		this.platenum = platenum;
	}

	public int getPlatetype() {
		return platetype;
	}

	public void setPlatetype(int platetype) {
		this.platetype = platetype;
	}

}
