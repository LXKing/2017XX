package com.xiangxun.util;

/**
 * @package: com.xiangxun.util
 * @ClassName: ConstantStatus.java
 * @Description: 结果码


 * @author: HanGJ
 * @date: 2015-7-24 上午10:47:02
 */
public class ConstantStatus {
	// 列表请求的状态 刷新 加载更多
	public static final int listStateRefresh = 1;
	public static final int listStateLoadMore = 2;
	public static final int listStateFirst = 3;

	// 登录
	public static final int loadSuccess = 4;
	public static final int loadFailed = 5;

	public static final int UpLoadDealSuccess = 6;
	public static final int UpLoadDealFalse = 7;

	public static final int PrinterClassSuccess = 8;

	public static final int PlateQuerySuccess = 9;
	public static final int PlateQueryFalse = 10;

	public static final int CarInfoSuccess = 11;

	public static final int DriverInfoSuccess = 12;

	// 从本地加载图片状态


	public static final int GetLocalSuccess = 13;
	public static final int GetLocalFalse = 14;

	public static final int FAILED = 15;
	public static final int FAILED_NO_NET = 16;
	public static final int FAILED_NO_USER = 17;
	public static final int VIEWOLDPIC = 18;
	public static final int NOPIC = 19;
	public static final int NOLAWKEY = 20;
	public static final int LAWKEY = 21;
	public static final int DISMISS_POPUPMENU = 22;
	public static final int WHAT_DID_LOAD_DATA = 23;
	public static final int WHAT_DID_REFRESH = 24;
	public static final int WHAT_DID_MORE = 25;
	public static final int DATA_CHANGE = 26;

	public static final int DRIVER_SEARCH_SUCCESS = 27;
	public static final int DRIVER_SEARCH_FALSE = 28;

	public static final int VEHICLE_SEARCH_SUCCESS = 29;
	public static final int VEHICLE_SEARCH_FALSE = 30;

	public static final int UPDATA_PUBLISH_ID_SUCCESS = 31;
	public static final int UPDATA_PUBLISH_ID_FALSE = 32;

	public static final int ACCOUNT_NO_MATCH = 33;
	public static final int ACCOUNT_NO_BOUND = 34;
	public static final int ACCOUNT_NO_AUTHORITY = 35;

	public static final int SREACHCONTATSSUCCESS = 36;
	public static final int SREACHCONTATSFALSE = 37;

	public static final int SREACH_SYSTEM_MESSAGE_SUCCESS = 38;
	public static final int SREACH_SYSTEM_MESSAGE_FALSE = 39;

	public static final int SREACH_SYSTEM_NITICE_SUCCESS = 40;
	public static final int SREACH_SYSTEM_NITICE_FALSE = 41;

	public static final int SREACH_VIODATA_SUCCESS = 42;
	public static final int SREACH_VIODATA_FALSE = 43;

	public static final int SREACH_DETAIL_SUCCESS = 44;
	public static final int SREACH_DETAIL_FALSE = 45;

	public static final int UPLOAD_GPSDATA = 46;
	public static final int UPLOAD_VIODATA = 47;
	public static final int UPLOAD_FIELDPUNISHDATA = 48;
	public static final int UPLOAD_ENFORCEMENTDATA = 49;
	public static final int UPLOAD_DUTYDATA = 50;
	public static final int UPLOAD_ACCIDENTDATA = 51;
	public static final int UPLOAD_LAWCHECKDATA = 52;
	public static final int UPLOAD_ALLDATA = 53;
	public static final int WARN_SIGNED = 54;
//	public static final int UPLOAD_WARNSIGNDATA = 56;
	public static final int UPLOAD_WARNACKDATA = 57;

	public static final int SEARCH_CROSS_SUCCESS = 58;
	public static final int SEARCH_CROSS_FAIL = 59;
	public static final int SEARCH_WARNPIC_SUCCESS = 60;
	public static final int SEARCH_WARNPIC_FAIL = 61;

	public static final int DRIVERPIC_SEARCH_SUCCESS = 62;
	public static final int DRIVERPIC_SEARCH_FALSE = 63;

	public static final int START_YEAR = 1990, END_YEAR = 2100;

	// 成功返回码


	public static final int SUCCESS = 200;
	// 网络错误返回码


	public static final int NetWorkError = 400;
	// SD卡无空间
	public static final int SD_NOSPACE = 31;
	// 格式
	public static final String ENCODE = "UTF-8";

	public static final int GetLawInfoSuccess = 51;
	public static final int GetLawInfoFalse = 52;
	// 上传本地照片状态码
	public static final int UpLoadSuccess = 61;
	public static final int UpLoadFalse = 62;

	public static final int UpLoadInterrupt = 63;
	public static final int auctionListSuccess = 64;
	public static final int auctionListFalse = 65;

	public static final int getVioDicData = 66;
	public static final int getArticleInfo = 67;

	public static final int OTHER_PLATENUM = 68;
	public static final int SHOW_KEYBOARD = 69;

	// 检查更新


	public static final int updateSuccess = 131;
	public static final int updateFalse = 132;
	public static final String FILESELECTORACTIONXML = "com.xiangxun.xml";
	public static final String FILESELECTORACTIONEXCEL = "com.xiangxun.excel";

	public static final String SIZE = "size";

	public static final int COMPUTER = 256;
	public static final int PHONE = 512;
	public static final int BLUETOOTHPRINTER = 1536;

	public static final int REQCODE_WARNSIGN = 2016;
	public static final int RESULTCODE_SIGNED = 2017;
	public static final int REQCODE_WARNACK = 2019;
	public static final int RESULTCODE_ACK = 2020;

	public static final int REQCODE_CROSS = 2021;
	public static final int RESULTCODE_CROSS = 2022;

	public static final int RQECOE_CHANGEPRINT = 2023;
	public static final int RESULTCODE_CHANGEPRINT = 2024;

	public static final int REQCODE_WARNTABLE = 2025;
	public static final int RESULTCODE_WARNTABLE = 2026;

	public static final int REQCODE_TYPE = 2027;
	public static final int RESULTCODE_TYPE = 2028;

	public static final int REQCODE_DIRECT = 2029;
	public static final int RESULTCODE_DIRECT = 2030;

	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	public static final int UpStatusFail = 0; //失败
	public static final int UpStatusSuccess = 1; //
	public static final int UpStatusException = 2; //
	public static final int UpStatusNoAuthority = 3; //无权
	public static final int UpStatusSigned = 4; //已签收
	public static final int UpStatusAcked = 5; //已反馈
	public static final int UpStatusNoNumber = 6; //无表单编号
	public static final int UpStatusNoinfo = 7; //无反馈信息
	public static final int UpStatusOldCode = 8; //违法行为代码[xxxx]不在有效期内
	public static final int UpStatusIllegalPolice = 9; //该发现机关下不存在该执勤民警信息
	public static final int UpStatusNoCar = 10; //无法核查到该机动车信息
	public static final int UpStatusNoStep = 11; //不存在对应的路段路口代码
	public static final int UpStatusExistNumber = 12; //已存在该记录
	public static final int UpStatusErrorNumber = 13; //凭证编号错误
	public static final int UpStatusNoRoad = 14; //该行政区划下不存在该道路代码
	public static final int UpStatusErrorKM = 15; //里程数有误

	public static final int UpStatusOther = 99; //
}