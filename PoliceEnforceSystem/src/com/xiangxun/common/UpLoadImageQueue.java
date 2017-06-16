package com.xiangxun.common;

import java.util.HashMap;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import com.xiangxun.bean.PhotoInfo;
import com.xiangxun.bean.ResultData.ImageBean;
import com.xiangxun.util.ConstantStatus;
import com.xiangxun.util.Logger;

/**
 * @package: com.huatek.api.common
 * @ClassName: UpLoadImageQueue
 * @Description: 上传队列图片发布时使用
 * @author: aaron_han
 * @data: 2015-1-09 下午4:57:22
 */
public class UpLoadImageQueue {
	// 存放文件路径的队列
	@SuppressLint("NewApi")
	private BlockingDeque<PhotoInfo> queue = new LinkedBlockingDeque<PhotoInfo>();
	private UpLoadThread thread;
	private boolean isRun = false;
	LoadUpLoadMachine machine;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ConstantStatus.UpLoadSuccess:
				PhotoInfo info = (PhotoInfo) msg.obj;
				info.uCallBack.upSuccess(info);
				break;
			case ConstantStatus.UpLoadFalse:
				PhotoInfo info1 = (PhotoInfo) msg.obj;
				info1.uCallBack.upFalse(info1);
				break;
			default:
				break;
			}
		};
	};

	public UpLoadImageQueue() {
		// ToDo
	}

	/**
	 * @Title:
	 * @Description: 开启队列
	 * @param:
	 * @return: void
	 * @throws
	 */
	public void start() {
		if (thread != null && thread.isAlive()) {
			isRun = false;
			thread.interrupt();
			thread = null;
		}
		thread = new UpLoadThread();
		isRun = true;
		thread.start();
	}

	/**
	 * @Title:
	 * @Description: 停止队列
	 * @param:
	 * @return: void
	 * @throws
	 */
	public void stop() {
		isRun = false;
		if (thread != null) {
			thread.interrupt();
		}
		thread = null;
	}

	/**
	 * @Title:
	 * @Description: 放入一个任务到队列
	 * @param: @param url
	 * @return: void
	 * @throws
	 */
	@SuppressLint("NewApi")
	public void put(PhotoInfo lv) {
		try {
			queue.put(lv);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@SuppressLint("NewApi")
	public void removeAll() {
		queue.clear();
		if (machine != null) {
			// 强制断开联网请求
			machine.close();
		}
	}

	private class UpLoadThread extends Thread {
		@SuppressLint("NewApi")
		@Override
		public void run() {
			while (isRun) {
				try {
					PhotoInfo pi = queue.take();
					machine = new LoadUpLoadMachine();
					String result = null;
					try {
						HashMap<String, String> hs = new HashMap<String, String>();
						hs.put("json", "true");
						String fileType = "IMG_UPDATE_FILE";

						result = machine.requestPost(hs, pi.url, pi.filePath, fileType);

						JSONObject jsonObject = new JSONObject(result);
						Logger.e("result=" + result);
						ImageBean iBean = new ImageBean();
						if (jsonObject.getInt("result") == 0) {
							if (jsonObject.getJSONObject("data").getJSONArray("message").length() > 0) {
								JSONObject jo_i = jsonObject.getJSONObject("data").getJSONArray("message").getJSONObject(0);
								iBean.file_id = (jo_i.getString("file_id"));
								iBean.prefix = (jo_i.getString("prefix"));
								iBean.url = (jo_i.getString("url"));
							}
						}
						Logger.e("返回图片Url=" + iBean.prefix + iBean.url);
						// pi.result = jsonObject.get("picUrl").toString();
						pi.result = iBean.prefix + iBean.url;
						Message msg = new Message();
						msg.what = ConstantStatus.UpLoadSuccess;
						msg.obj = pi;
						handler.sendMessage(msg);
					} catch (Exception e) {
						e.printStackTrace();
						Logger.e("upload fail");
						pi.result = result;
						Message msg = new Message();
						msg.what = ConstantStatus.UpLoadFalse;
						msg.obj = pi;
						handler.sendMessage(msg);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}

}
