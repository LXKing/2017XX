package com.xiangxun.request;

import android.os.Environment;

import com.xiangxun.app.XiangXunApplication;
import com.xiangxun.cfg.SystemCfg;

/**
 * @ClassName: Api.java
 * @Description: 所有链接头部公共部分
 * @author: aaron_han
 * @date: 2015-1-14 下午01:08:27
 */
public class Api {
	/** true为测试机 */
	private final boolean DEBUGURL = AppBuildConfig.DEBUGURL;
	public String urlHost = getUrlHead();
	/** m模块下 */
	public String urlHeadlogin = urlHost + "/mpts/m/";
	/** mobile模块下 */
	public String urlHeadMobile = urlHost + "/mpts/mobile/";
	public String urlHeadMpts = urlHost + "/mpts/";
	// path
	public static String xXDir = Environment.getExternalStorageDirectory() + "/xiangxun/";
	// 发布拍照path
	public static String xXPublishPictureDir = xXDir.concat("publishPicture/");

	public String getUrlHead() {
		if (DEBUGURL) {
			return "http://" + SystemCfg.getServerIP(XiangXunApplication.getInstance()) + ":" + SystemCfg.getServerPort(XiangXunApplication.getInstance());
		}
		return "http://" + SystemCfg.getServerIP(XiangXunApplication.getInstance()) + ":" + SystemCfg.getServerPort(XiangXunApplication.getInstance());
	}

	/** SIP */
	public static String password = "123456yng";
	public static String getSipIp() {
		return SystemCfg.getServerIP(XiangXunApplication.getInstance());
	}	
}
