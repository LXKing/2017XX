package com.xiangxun.cfg;

import android.content.Context;
import android.content.SharedPreferences;

import com.xiangxun.activity.R;

import static com.xiangxun.request.AppBuildConfig.DEBUGURL;

public class SystemCfg {
	private static SharedPreferences mysp = null;
	private static final String PREFERENCE_NAME = "xxsyscfg";
	private static void init(Context context) {
		mysp = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
	}

	public static void setRegCode(Context context,String path) {
		init(context);
		mysp.edit().putString("RegCode", path).commit();
	}

	public static String getRegCode(Context context) {
		init(context);
		return mysp.getString("RegCode", "");
	}

	public static void isRegist(Context context,boolean reg) {
		init(context);
		mysp.edit().putBoolean("isreg", reg).commit();
	}

	public static boolean Regist(Context context) {
		init(context);
		return mysp.getBoolean("isreg", false);
	}

	public static void setPlateRec(Context context,boolean rec) {
		init(context);
		mysp.edit().putBoolean("platerec", rec).commit();
	}

	public static boolean getPlateRec(Context context) {
		init(context);
		return mysp.getBoolean("platerec", false);
	}

	public static void setVioPicWidth(Context context,int w) {
		init(context);
		mysp.edit().putInt("viopicwidth", w).commit();
	}

	public static int getVioPicWidth(Context context) {
		init(context);
		return mysp.getInt("viopicwidth", 2048);
	}

	public static void setVioPicHight(Context context,int h) {
		init(context);
		mysp.edit().putInt("viopichight", h).commit();
	}

	public static int getVioPicHight(Context context) {
		init(context);
		return mysp.getInt("viopichight", 1536);
	}

	public static int getTextSize(Context context) {
		init(context);
		return mysp.getInt("textsize", 24);
	}

	public static void setTextSize(Context context,int textsize) {
		init(context);
		mysp.edit().putInt("textsize", textsize).commit();
	}

	public static void setWhiteBalance(Context context,String wb) {
		init(context);
		mysp.edit().putString("whitebalance", wb).commit();
	}

	public static void setDutyPicWidth(Context context,int w) {
		init(context);
		mysp.edit().putInt("dutypicwidth", w).commit();
	}

	public static int getDutyPicWidth(Context context) {
		init(context);
		return mysp.getInt("dutypicwidth", 2048);
	}

	public static void setDutyPicHight(Context context,int h) {
		init(context);
		mysp.edit().putInt("dutypichight", h).commit();
	}

	public static int getDutyPicHight(Context context) {
		init(context);
		return mysp.getInt("dutypichight", 1536);
	}

	public static String getWhiteBalance(Context context) {
		init(context);
		return mysp.getString("whitebalance", "auto");
	}

	public static void setSceneModes(Context context,String sm) {
		init(context);
		mysp.edit().putString("scenemodes", sm).commit();
	}

	public static String getSceneModes(Context context) {
		init(context);
		return mysp.getString("scenemodes", "auto");
	}

	public static void setExposureCompensation(Context context,int s) {
		init(context);
		mysp.edit().putInt("exposurecompensation", s).commit();
	}

	public static int getExposureCompensation(Context context) {
		init(context);
		return mysp.getInt("exposurecompensation", 0);
	}

	public static void setFlashModes(Context context,String str) {
		init(context);
		mysp.edit().putString("flashmodes", str).commit();
	}

	public static String getFlashModes(Context context) {
		init(context);
		return mysp.getString("flashmodes", "auto");
	}

	public static void setIdCardAuth(Context context,String str) {
		init(context);
		mysp.edit().putString("idcardauth", str).commit();
	}

	public static String getIdCardAuth(Context context) {
		init(context);
		return mysp.getString("idcardauth", "");
	}

	public static void setServerIP(Context context,String str) {
		init(context);
		mysp.edit().putString("serverip", str).commit();
	}

	public static void setServerPort(Context context,String str) {
		init(context);
		mysp.edit().putString("serverport", str).commit();
	}

	public static String getServerIP(Context context) {
		init(context);
		if (DEBUGURL)
			return mysp.getString("serverip", "192.168.17.97");
		else
			return mysp.getString("serverip", "192.168.17.97");
	}

	public static String getServerPort(Context context) {
		init(context);
		if (DEBUGURL)
			return mysp.getString("serverport", "8080");
		else
			return mysp.getString("serverport", "8080");
	}

	/**
	 * 警员名称
	 */
	public static void setPoliceName(Context context,String name) {
		init(context);
		mysp.edit().putString("policeName", name).commit();
	}

	/**
	 * 警员名称
	 */
	public static String getPoliceName(Context context) {
		init(context);
		return mysp.getString("policeName", "");
	}

	/**
	 * userID
	 */
	public static void setUserId(Context context,String id) {
		init(context);
		mysp.edit().putString("userId", id).commit();
	}

	/**
	 * userID
	 */
	public static String getUserId(Context context) {
		init(context);
		return mysp.getString("userId", "");
	}

	/**
	 * 警号
	 */
	public static void setPoliceCode(Context context,String code) {
		init(context);
		mysp.edit().putString("policeCode", code).commit();
	}

	/**
	 * 警号
	 */
	public static String getPoliceCode(Context context) {
		init(context);
		return mysp.getString("policeCode", "");
	}
	
	
	/**
	 * 打印票头
	 */
	public static void setTopName(Context context,String code) {
		init(context);
		mysp.edit().putString("topName", code).commit();
	}

	/**
	 * 打印票头
	 */
	public static String getTopName(Context context) {
		init(context);
		return mysp.getString("topName", "");
	}
	
	/**
	 * 复议机关
	 */
	public static void setAgainDiscussDepartment(Context context,String code) {
		init(context);
		mysp.edit().putString("againDiscussDepartment", code).commit();
	}

	/**
	 * 复议机关
	 */
	public static String getAgainDiscussDepartment(Context context) {
		init(context);
		return mysp.getString("againDiscussDepartment", "");
	}
	
	/**
	 * 行政法院
	 */
	public static void setCourtName(Context context,String code) {
		init(context);
		mysp.edit().putString("courtName", code).commit();
	}

	/**
	 * 行政法院
	 */
	public static String getCourtName(Context context) {
		init(context);
		return mysp.getString("courtName", "");
	}
	
	/**
	 * 处罚地址
	 */
	public static void setPunishAddress(Context context,String code) {
		init(context);
		mysp.edit().putString("punishAddress", code).commit();
	}

	/**
	 * 处罚地址
	 */
	public static String getPunishAddress(Context context) {
		init(context);
		return mysp.getString("punishAddress", "");
	}
	
	/**
	 * 车牌识别注册码
	 */
	public static String getRegistCode(Context context) {
		init(context);
		return mysp.getString("registCode", "");
	}
	
	/**
	 * 车牌识别注册码
	 */
	public static void setRegistCode(Context context,String code) {
		init(context);
		mysp.edit().putString("registCode", code).commit();
	}

	/**
	 * 部门名称
	 */
	public static void setDepartment(Context context,String dep) {
		init(context);
		mysp.edit().putString("department", dep).commit();
	}
	public static String getDepartment(Context context) {
		init(context);
		return mysp.getString("department", "");
	}
	public static void setDepartmentID(Context context,String dep) {
		init(context);
		mysp.edit().putString("departmentID", dep).commit();
	}
	public static String getDepartmentID(Context context) {
		init(context);
		return mysp.getString("departmentID", "");
	}
	public static void setDepartmentCode(Context context,String dep) {
		init(context);
		mysp.edit().putString("departmentCode", dep).commit();
	}
	public static String getDepartmentCode(Context context) {
		init(context);
		return mysp.getString("departmentCode", "");
	}
	public static void setParentDepartmentName(Context context,String dep) {
		init(context);
		mysp.edit().putString("ParentDepartmentName", dep).commit();
	}
	public static String getParentDepartmentName(Context context) {
		init(context);
		return mysp.getString("ParentDepartmentName", "");
	}

	/**
	 * 电话
	 */
	public static void setMobile(Context context,String mob) {
		init(context);
		mysp.edit().putString("mobile", mob).commit();
	}

	/**
	 * 电话
	 */
	public static String getMobile(Context context) {
		init(context);
		return mysp.getString("mobile", "");
	}

	/**
	 * 账户
	 */
	public static void setAccount(Context context,String acc) {
		init(context);
		mysp.edit().putString("account", acc).commit();
	}

	/**
	 * 账户
	 */
	public static String getAccount(Context context) {
		init(context);
		return mysp.getString("account", "");
	}

	/**
	 * 加密密码
	 */
	public static void setPassword(Context context,String pwd) {
		init(context);
		mysp.edit().putString("password", pwd).commit();
	}

	/**
	 * 加密密码
	 */
	public static String getPassword(Context context) {
		init(context);
		return mysp.getString("password", "");
	}

	/**
	 * 未加密密码
	 */
	public static void setWhitePwd(Context context,String pwd) {
		init(context);
		mysp.edit().putString("pwd", pwd).commit();
	}

	/**
	 * 未加密密码
	 */
	public static String getWhitePwd(Context context) {
		init(context);
		return mysp.getString("pwd", "");
	}
	
	public static void setLinphoneStats(Context context,int s) {
		init(context);
		mysp.edit().putInt("LinphoneStats", s).commit();
	}

	public static int getLinphoneStats(Context context) {
		init(context);
		return mysp.getInt("LinphoneStats", 0);
	}
	/**
	 * 首次登陆
	 */
	public static void setIsFirstLogion(Context context,boolean value) {
		init(context);
		mysp.edit().putBoolean("isFirstLogion", value).commit();
	}

	public static boolean getIsFirstLogion(Context context) {
		init(context);
		return mysp.getBoolean("isFirstLogion", true);
	}
	/**
	 * 工作覆盖区域
	 */
	public static void setWorkRoad(Context context,String value) {
		init(context);
		mysp.edit().putString("WorkRoad", value).commit();
	}

	public static String getWorkRoad(Context context) {
		init(context);
		return mysp.getString("WorkRoad", context.getResources().getString(R.string.workroad_city));
	}

	public static void setPrinterName(Context context,String str) {
		init(context);
		mysp.edit().putString("printer", str).commit();
	}

	public static String getPrinterName(Context context) {
		init(context);
		String mapserver = mysp.getString("printer", "RG-MTP58B");
		return mapserver;
	}

	public static void setIsPrint(Context context,boolean value) {
		init(context);
		mysp.edit().putBoolean("isprint", value).commit();
	}

	public static boolean getIsPrint(Context context) {
		init(context);
		boolean mapserver = mysp.getBoolean("isprint", false);
		return mapserver;
	}

	public static void setIsWarn(Context context,boolean value) {
		init(context);
		mysp.edit().putBoolean("iswarn", value).commit();
	}

	public static boolean getIsWarn(Context context) {
		init(context);
		return mysp.getBoolean("iswarn", false);
	}

	public static void setWarnCross(Context context,String cross) {
		init(context);
		mysp.edit().putString("warncross", cross).commit();
	}

	public static String getWarnCross(Context context) {
		init(context);
		return mysp.getString("warncross", "无");
	}

	public static String getVioParkCode(Context context) {
		init(context);
		return mysp.getString("vioparkcode", "1039");
	}
}
