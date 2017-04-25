package com.xiangxun.bean;

public class VioData {
	public int _id = 0;
	public String publishCode = "";
	public String datetime = "";
	public String timevalue = "";
	public String endtime = "";
	public String roadname = "";
	public String roadlocation = "";
	public String roaddirect = "";
	public String roadid = "";
	public String platenum = "";
	public String platetype = "";
	public String viotype = "";
	public String viocode = "";
	public String carColor = "";
	public String plateColor = "";
	public String carColorCode = "";
	public String plateColorCode = "";
	public String user = "";
	public int isupfile = 0;
	public int issure = 0;
	public int picnum = 0;
	public String picurl0 = "";
	public String picurl1 = "";
	public String picurl2 = "";
	public String picurl3 = "";
	public String vioid = "";
	public int viostate = 0;
	public int disposetype = 0;
	public int datasource = 0;
	public int dealstate = 0;

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getPublishCode() {
		return publishCode;
	}

	public void setPublishCode(String publishCode) {
		this.publishCode = publishCode;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public String getTimevalue() {
		return timevalue;
	}

	public void setTimevalue(String timevalue) {
		this.timevalue = timevalue;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public String getRoadname() {
		return roadname;
	}

	public void setRoadname(String roadname) {
		this.roadname = roadname;
	}

	public String getRoadlocation() {
		return roadlocation;
	}

	public void setRoadlocation(String roadlocation) {
		this.roadlocation = roadlocation;
	}

	public String getRoaddirect() {
		return roaddirect;
	}

	public void setRoaddirect(String roaddirect) {
		this.roaddirect = roaddirect;
	}

	public String getCarColor() {
		return carColor;
	}

	public void setCarColor(String carColor) {
		this.carColor = carColor;
	}

	public String getPlateColor() {
		return plateColor;
	}

	public void setPlateColor(String plateColor) {
		this.plateColor = plateColor;
	}

	public String getCarColorCode() {
		return carColorCode;
	}

	public void setCarColorCode(String carColorCode) {
		this.carColorCode = carColorCode;
	}

	public String getPlateColorCode() {
		return plateColorCode;
	}

	public void setPlateColorCode(String plateColorCode) {
		this.plateColorCode = plateColorCode;
	}

	public String getPlatenum() {
		return platenum;
	}

	public void setPlatenum(String platenum) {
		this.platenum = platenum;
	}

	public String getPlatetype() {
		return platetype;
	}

	public void setPlatetype(String platetype) {
		this.platetype = platetype;
	}

	public String getViotype() {
		return viotype;
	}

	public void setViotype(String viotype) {
		this.viotype = viotype;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public int getIsupfile() {
		return isupfile;
	}

	public void setIsupfile(int isupfile) {
		this.isupfile = isupfile;
	}

	public int getIssure() {
		return issure;
	}

	public void setIssure(int issure) {
		this.issure = issure;
	}

	public int getPicnum() {
		return picnum;
	}

	public void setPicnum(int picnum) {
		this.picnum = picnum;
	}

	public String getPicurl0() {
		return picurl0;
	}

	public void setPicurl0(String picurl0) {
		this.picurl0 = picurl0;
	}

	public String getPicurl1() {
		return picurl1;
	}

	public void setPicurl1(String picurl1) {
		this.picurl1 = picurl1;
	}

	public String getPicurl2() {
		return picurl2;
	}

	public void setPicurl2(String picurl2) {
		this.picurl2 = picurl2;
	}

	public String getPicurl3() {
		return picurl3;
	}

	public void setPicurl3(String picurl3) {
		this.picurl3 = picurl3;
	}

	public String getVioid() {
		return vioid;
	}

	public void setVioid(String vioid) {
		this.vioid = vioid;
	}

	public int getViostate() {
		return viostate;
	}

	public void setViostate(int viostate) {
		this.viostate = viostate;
	}

	public int getDisposetype() {
		return disposetype;
	}

	public void setDisposetype(int disposetype) {
		this.disposetype = disposetype;
	}

	public String getViocode() {
		return viocode;
	}

	public void setViocode(String viocode) {
		this.viocode = viocode;
	}

	public int getDatasource() {
		return datasource;
	}

	public void setDatasource(int datasource) {
		this.datasource = datasource;
	}

	public int getDealstate() {
		return dealstate;
	}

	public void setDealstate(int dealstate) {
		this.dealstate = dealstate;
	}

	public String getRoadid() {
		return roadid;
	}

	public void setRoadid(String roadid) {
		this.roadid = roadid;
	}

	public VioData() {
	}

	public VioData(String publishcode,String viotype,String datetime, String roadname, String roadlocation, String roaddirect, String roadid, String platenum, String platetype,//
			String viocode, String user, int isupfile, String vioid, int disposetype, int picnum, //
			String picurl0, String picurl1, String picurl2, String picurl3, int issure, int datasource,//
			int dealstate) {
		this.publishCode = publishcode;
		this.viotype = viotype;
		this.datetime = datetime;
		this.roadid = roadid;
		this.roadname = roadname;
		this.roadlocation = roadlocation;
		this.roaddirect = roaddirect;
		this.platenum = platenum;
		this.platetype = platetype;
		this.viocode = viocode;
		this.user = user;
		this.isupfile = isupfile;
		this.vioid = vioid;
		this.picnum = picnum;
		this.disposetype = disposetype;
		this.picurl0 = picurl0;
		this.picurl1 = picurl1;
		this.picurl2 = picurl2;
		this.picurl3 = picurl3;
		this.issure = issure;
		this.datasource = datasource;
		this.dealstate = dealstate;
	}

	public VioData(String datetime, String roadname, String platenum, String platetype, String viotype, String user, int isupfile, int picnum) {
		this.datetime = datetime;
		this.roadname = roadname;
		this.platenum = platenum;
		this.platetype = platetype;
		this.viotype = viotype;
		this.user = user;
		this.isupfile = isupfile;
		this.picnum = picnum;
	}

}
