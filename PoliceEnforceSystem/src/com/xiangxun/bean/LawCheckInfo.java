package com.xiangxun.bean;

public class LawCheckInfo {

	public int _id;
	public int isupfile;
	public String datetime;
	public String drivername;
	public String cartype;
	public String phone;
	public String owner;
	public String platenum;
	public String drivernum;
	public String must;
	public String road;
	public String roadlocation;
	public String roaddirect;
	public String actual;
	public String viotype;
	public String handleway;
	public String handler;
	public String memo;
	public String checktype;
	public String pic1;
	public String pic2;
	public String pic3;
	public String pic4;

	public LawCheckInfo() {
		super();
	}

	public LawCheckInfo(int _id, String datetime, String drivername, String cartype, String phone, String owner, String platenum, String drivernum, String must, String actual, String road, String roadlocation, String roaddirect, String viotype, String handleway, String handler, String memo, String pic1, String pic2, String pic3, String pic4, String checktype, int isupfile) {
		super();
		this._id = _id;
		this.isupfile = isupfile;
		this.datetime = datetime;
		this.drivername = drivername;
		this.cartype = cartype;
		this.phone = phone;
		this.checktype = checktype;
		this.owner = owner;
		this.road = road;
		this.roadlocation = roadlocation;
		this.roaddirect = roaddirect;
		this.platenum = platenum;
		this.drivernum = drivernum;
		this.must = must;
		this.actual = actual;
		this.viotype = viotype;
		this.handleway = handleway;
		this.handler = handler;
		this.memo = memo;
		this.pic1 = pic1;
		this.pic2 = pic2;
		this.pic3 = pic3;
		this.pic4 = pic4;
	}

}
