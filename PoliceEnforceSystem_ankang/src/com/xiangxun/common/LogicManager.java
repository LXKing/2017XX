package com.xiangxun.common;

/**
 * @ClassName: LogicManager.java
 * @Description: 网络工具类
 * @author: aaron_han
 * @date: 2015年01月16日 下午2:15:42
 */
public class LogicManager {
	private static LogicManager lMgr;

	public static LogicManager getInstance() {
		if (lMgr == null) {
			lMgr = new LogicManager();
		}
		return lMgr;
	}

	public void removeTag(Object tag) {
		DcHttpClient.getInstance().cancelRequest(tag);
	}

}
