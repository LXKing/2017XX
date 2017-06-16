package com.xiangxun.bean;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import com.xiangxun.util.ImageCacheLoader.UpLoadCallBack;

/**
 * @package: com.xiangxun.bean
 * @ClassName: PhotoInfo.java
 * @Description: 上传照片
 * @author: HanGJ
 * @date: 2015-8-31 下午2:29:16
 */

@SuppressWarnings("serial")
public class PhotoInfo implements Serializable {
	public static final int STATUREADY = 0;
	public static final int STATUSUCCESS = 1;
	public static final int STATUFALSE = 2;
	public String filePath;// 本地路径
	public String url;
	public String result;// 返回路径
	public int statu;// 0 表示待上传 1 表示上传成功 2表示上传失败
	public UpLoadCallBack uCallBack;

	public PhotoInfo() {
	}

	public PhotoInfo(String url, String filePath, int statu, String result) {
		this.url = url;
		this.filePath = filePath;
		this.statu = statu;
		this.result = result;
	}

	public void setUCallBack(UpLoadCallBack uCallBack) {
		this.uCallBack = uCallBack;
	}

	public String getReturnPath() {
		if (result == null) {
			return null;
		} else {
			try {
				JSONObject jo = new JSONObject(result);
				String path = jo.optString("picUrl");
				return path;
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

}