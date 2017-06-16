package com.xiangxun.request;

import android.content.Context;

import com.xiangxun.bean.LoginInfo;
import com.xiangxun.util.EncodeTools;
import com.xiangxun.util.Tools;

/**
 * @package: com.xiangxun.request
 * @ClassName: ApiUrl.java
 * @Description: 所有请求方法url拼接
 * @author: HanGJ
 * @date: 2015-7-30 下午4:59:58
 */
public class ApiUrl {
	private static Api api;

	private static void init(Context context) {
		api = new Api();
	}

	// 登陆
	public static String login(Context context) {
		init(context);
		return EncodeTools.Utf8URLencode(EncodeTools.getEnUrl(Tools.getSB().append(api.urlHeadlogin).append("user/login/authority/").toString()));
	}

	public static String getDriver(Context context, String driverLicenseNumber) {
		init(context);
		return EncodeTools.Utf8URLencode(EncodeTools.getEnUrl(Tools.getSB().append(api.urlHeadMobile).append("invoke/searchPersionInfo/").append(driverLicenseNumber + "/").append("?sessionid=").append(LoginInfo.sessionId).toString()));
	}

	public static String getDriverPic(Context context) {
		init(context);
		return EncodeTools.Utf8URLencode(EncodeTools.getEnUrl(Tools.getSB().append(api.urlHeadMobile).append("yujing/kakou/dic/jiashirenzhaopian/").append("?sessionid=").append(LoginInfo.sessionId).toString()));
	}

	public static String getVehicleInfo(Context context) {
		init(context);
		return EncodeTools.Utf8URLencode(EncodeTools.getEnUrl(Tools.getSB().append(api.urlHeadMobile).append("invoke/searchCarInfo/").append("?sessionid=").append(LoginInfo.sessionId).toString()));
	}

	public static String updatePunishId(Context context, String userCode, String type) {
		init(context);
		return EncodeTools.Utf8URLencode(EncodeTools.getEnUrl(Tools.getSB().append(api.urlHeadMobile).append("number/searchNumber/").append(type + "/" + userCode + "/?sessionid=").append(LoginInfo.sessionId).toString()));
	}

	public static String updateVersion(Context context) {
		init(context);
		return EncodeTools.Utf8URLencode(EncodeTools.getEnUrl(Tools.getSB().append(api.urlHeadMobile).append("versions/searchPathByUp/").append("?sessionid=").append(LoginInfo.sessionId).toString()));
	}
	
	public static String getVipCarList(Context context){
		init(context);
		return EncodeTools.Utf8URLencode(EncodeTools.getEnUrl(Tools.getSB().append(api.urlHeadMobile).append("black/searchVipInfo/").append("?sessionid=").append(LoginInfo.sessionId).toString()));
	}
	
	public static String getBlackList(Context context){
		init(context);
		return EncodeTools.Utf8URLencode(EncodeTools.getEnUrl(Tools.getSB().append(api.urlHeadMobile).append("black/searchBlackInfo/").append("?sessionid=").append(LoginInfo.sessionId).toString()));
	}
	
	public static String getAndSaveTypeData(Context context){
		init(context);
		return EncodeTools.Utf8URLencode(EncodeTools.getEnUrl(Tools.getSB().append(api.urlHeadMobile).append("tools/dic/").append("?sessionid=").append(LoginInfo.sessionId).toString()));
	}
	
	public static String getRoadNamesData(Context context){
		init(context);
		return EncodeTools.Utf8URLencode(EncodeTools.getEnUrl(Tools.getSB().append(api.urlHeadMobile).append("tools/pla/").append("?sessionid=").append(LoginInfo.sessionId).toString()));
	}
	
	public static String getVioTypesData(Context context){
		init(context);
		return EncodeTools.Utf8URLencode(EncodeTools.getEnUrl(Tools.getSB().append(api.urlHeadMobile).append("tools/vio/").append("?sessionid=").append(LoginInfo.sessionId).toString()));	
	}
	
	public static String getAddressBook(Context context){
		init(context);
		return EncodeTools.Utf8URLencode(EncodeTools.getEnUrl(Tools.getSB().append(api.urlHeadMobile).append("book/searchBookInfo/").append("?sessionid=").append(LoginInfo.sessionId).toString()));	
	}
	
	public static String getVioDicData(Context context){
		init(context);
		return EncodeTools.Utf8URLencode(EncodeTools.getEnUrl(Tools.getSB().append(api.urlHeadMobile).append("dic/searchVioTypeInfo/").append("?sessionid=").append(LoginInfo.sessionId).toString()));	
	}
	
	public static String getArticleInfo(Context context){
		init(context);
		return EncodeTools.Utf8URLencode(EncodeTools.getEnUrl(Tools.getSB().append(api.urlHeadMobile).append("dic/searchArticleInfo/").append("?sessionid=").append(LoginInfo.sessionId).toString()));	
	}

	public static String doAccEventInfo(Context context) {
		init(context);
		return EncodeTools.Utf8URLencode(EncodeTools.getEnUrl(Tools.getSB().append(api.urlHeadMobile).append("acc/doAccEventInfo/").toString()));
	}	

	public static String doDailyWorking(Context context) {
		init(context);
		return EncodeTools.Utf8URLencode(EncodeTools.getEnUrl(Tools.getSB().append(api.urlHeadMobile).append("om/doDailyWorking/").toString()));
	}	

	public static String doForceMeasure(Context context) {
		init(context);
		return EncodeTools.Utf8URLencode(EncodeTools.getEnUrl(Tools.getSB().append(api.urlHeadMobile).append("vio/doForceMeasure/").toString()));
	}	

	public static String doDispose(Context context) {
		init(context);
		return EncodeTools.Utf8URLencode(EncodeTools.getEnUrl(Tools.getSB().append(api.urlHeadMobile).append("vio/doDispose/").toString()));
	}	

	public static String doAdd(Context context) {
		init(context);
		return EncodeTools.Utf8URLencode(EncodeTools.getEnUrl(Tools.getSB().append(api.urlHeadMobile).append("vio/doAdd/").toString()));
	}	

	public static String doDataLedger(Context context) {
		init(context);
		return EncodeTools.Utf8URLencode(EncodeTools.getEnUrl(Tools.getSB().append(api.urlHeadMobile).append("om/doDataLedger/").toString()));
	}
	
	public static String changePassword(Context context) {
		init(context);
		return EncodeTools.Utf8URLencode(EncodeTools.getEnUrl(Tools.getSB().append(api.urlHeadlogin).append("user/login/updatepwd/").toString()));
	}

	public static String searchPathByModeId(Context context) {
		init(context);
		return EncodeTools.Utf8URLencode(EncodeTools.getEnUrl(Tools.getSB().append(api.urlHeadMobile).append("regcode/searchPathByModeId/").toString()));
	}

	public static String getVioInfo(Context context) {
		init(context);
		return EncodeTools.Utf8URLencode(EncodeTools.getEnUrl(Tools.getSB().append(api.urlHeadMpts).append("information/CarQuery/querycarVioInfo/").append("?sessionid=").append(LoginInfo.sessionId).toString()));
	}

	public static String getWarnCross(Context context) {
		init(context);
		return EncodeTools.Utf8URLencode(EncodeTools.getEnUrl(Tools.getSB().append(api.urlHeadMpts).append("mobile/yujing/kakou/dic/list/").append("?sessionid=").append(LoginInfo.sessionId).toString()));
	}

	public static String getWarnPic(Context context) {
		init(context);
		return EncodeTools.Utf8URLencode(EncodeTools.getEnUrl(Tools.getSB().append(api.urlHeadMpts).append("sendlog/send/queryJcbkpicBygcxh/").append("?sessionid=").append(LoginInfo.sessionId).toString()));
	}

	public static String doWarnAck(Context context) {
		init(context);
		return EncodeTools.Utf8URLencode(EncodeTools.getEnUrl(Tools.getSB().append(api.urlHeadMobile).append("yujing/kakou/dic/yujingqianshou/").append("?sessionid=").append(LoginInfo.sessionId).toString()));
	}

	public static String doWarnPic(Context context) {
		init(context);
		return EncodeTools.Utf8URLencode(EncodeTools.getEnUrl(Tools.getSB().append(api.urlHeadMobile).append("yujing/kakou/dic/yujingtupian/").append("?sessionid=").append(LoginInfo.sessionId).toString()));
	}

	public static String doWarnSimpleVio(Context context) {
		init(context);
		return EncodeTools.Utf8URLencode(EncodeTools.getEnUrl(Tools.getSB().append(api.urlHeadMobile).append("yujing/kakou/dic/jianyiweifachuli/").append("?sessionid=").append(LoginInfo.sessionId).toString()));
	}

	public static String doWarnEnforceVio(Context context) {
		init(context);
		return EncodeTools.Utf8URLencode(EncodeTools.getEnUrl(Tools.getSB().append(api.urlHeadMobile).append("yujing/kakou/dic/qiangzhicuoshichuli/").append("?sessionid=").append(LoginInfo.sessionId).toString()));
	}

	public static String doWarnParkVio(Context context) {
		init(context);
		return EncodeTools.Utf8URLencode(EncodeTools.getEnUrl(Tools.getSB().append(api.urlHeadMobile).append("/yujing/kakou/dic/weifashangchuanliuheyi/").append("?sessionid=").append(LoginInfo.sessionId).toString()));
	}

	public static String getHost() {
		return api.getUrlHead();
	}
}
