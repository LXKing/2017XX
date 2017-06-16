package com.xiangxun.common;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.xiangxun.bean.UpdateData;
import com.xiangxun.util.Logger;

/**
 * @package: com.huatek.api.common
 * @ClassName: InfoCache
 * @Description: 非图片数据缓存类
 * @author: aaron_han
 * @data: 2015-1-16 下午4:56:11
 */
public class InfoCache {
	private static InfoCache infoCache;
	public int fragmentPosition;// 默认显示的第几个fragment
	public ArrayList<Integer> publishIds = new ArrayList<Integer>();
	private String locaiton_addres;
	private UpdateData mData;// 更新数据
	public String curVersionName = "";// 当前版本号
	public String curVersionCode = "";// 当前版本号
	private boolean isNewVer = false;// 是否有新版本

	private InfoCache() {

	}

	public static InfoCache getInstance() {
		if (infoCache == null) {
			infoCache = new InfoCache();
		}
		return infoCache;
	}

	/**
	 * 获取fragment停留标记
	 * @return the fragmentPosition
	 */
	public int getFragmentPosition() {
		return fragmentPosition;
	}

	/**
	 * 设置fragment停留标记
	 * 
	 * @param fragmentPosition
	 *            the fragmentPosition to set
	 */
	public void setFragmentPosition(int fragmentPosition) {
		this.fragmentPosition = fragmentPosition;
	}

	/**
	 * 返回当前程序版本名
	 */
	public String getAppVersionName(Context context) {
		if (TextUtils.isEmpty(curVersionName)) {
			try {
				PackageManager pm = context.getPackageManager();
				PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
				curVersionName = pi.versionName;
			} catch (Exception e) {
				Logger.e("VersionInfo" + "Exception", e);
			}
		}
		return curVersionName;
	}

	public String getAppVersionCode(Context context) {
		if (TextUtils.isEmpty(curVersionCode)) {
			try {
				PackageManager pm = context.getPackageManager();
				PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
				curVersionCode = String.valueOf(pi.versionCode);
			} catch (Exception e) {
				Logger.e("VersionInfo" + "Exception", e);
			}
		}
		return curVersionCode;
	}

	/**
	 * 返回当前程序屏幕分辨率信息
	 */
	public DisplayMetrics getDisplayMetrics(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm;
	}

	public UpdateData getmData() {
		return mData;
	}

	public void setmData(UpdateData mData) {
		this.mData = mData;
	}

	// 获取定位地址
	public String getmAddr() {
		return locaiton_addres;
	}

	public void setmAddr(String mAddr) {
		this.locaiton_addres = mAddr;
	}

	public boolean isNewVer() {
		return isNewVer;
	}

	public void setNewVer(boolean isNewVer) {
		this.isNewVer = isNewVer;
	}

}