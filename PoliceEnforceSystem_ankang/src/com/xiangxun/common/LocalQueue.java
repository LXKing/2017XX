/**
 * 
 */
package com.xiangxun.common;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import com.xiangxun.util.ConstantStatus;
import com.xiangxun.util.MyUtils;

/**
 * @package: com.huatek.api.common
 * @ClassName: LocalQueue
 * @Description: 从本地异步获取本地图片队列
 * @author: aaron_han
 * @data: 2015-1-13 下午4:56:56
 */
public class LocalQueue {
	@SuppressLint("NewApi")
	private BlockingDeque<LocalNetWorkView> localQueue = new LinkedBlockingDeque<LocalNetWorkView>();
	// 存放文件路径的队列
	@SuppressLint("NewApi")
	private LocalImageThread thread;
	private boolean isRun = false;// 线程标记
	private BitmapCache bCache;
	private Context con;
	public static final String LINING_BIG = "LINING_BIG";

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case ConstantStatus.GetLocalSuccess:
				LocalNetWorkView lv = (LocalNetWorkView) msg.obj;
				lv.gCallBack.localSuccess(lv);
				break;
			case ConstantStatus.GetLocalFalse:
				LocalNetWorkView lv1 = (LocalNetWorkView) msg.obj;
				lv1.gCallBack.localFalse(lv1);
				break;
			default:
				break;
			}
		};
	};

	public LocalQueue(BitmapCache cache, Context con) {
		this.bCache = cache;
		this.con = con;
	}

	/**
	 * 
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
		thread = new LocalImageThread(con);
		isRun = true;
		thread.start();
	}

	/**
	 * 
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
	 * 
	 * @Title:
	 * @Description: 放入一个任务到队列
	 * @param: @param url
	 * @return: void
	 * @throws
	 */
	@SuppressLint("NewApi")
	public void put(LocalNetWorkView lView) {
		try {
			localQueue.put(lView);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@SuppressLint("NewApi")
	public void removeAll() {
		localQueue.clear();
	}

	private class LocalImageThread extends Thread {
		Context con;

		public LocalImageThread(Context con) {
			this.con = con;
		}

		@SuppressLint("NewApi")
		@Override
		public void run() {
			while (isRun) {
				try {
					LocalNetWorkView lv = localQueue.take();
					if (lv == null)
						continue;
					Bitmap bm = null;
					if (bCache.getBitmap(lv.filePath) == null) {// 如果内存没有从磁盘取图片
						bm = MyUtils.getSmallImg(lv.filePath, con);
						if (bm == null) {
							Message msg = new Message();
							msg.what = ConstantStatus.GetLocalFalse;
							msg.obj = lv;
							handler.sendMessage(msg);
							continue;
						}
						bCache.putBitmap(lv.filePath, bm);
					} else {
						bm = bCache.getBitmap(lv.filePath);
						if (bm == null) {
							Message msg = new Message();
							msg.what = ConstantStatus.GetLocalFalse;
							msg.obj = lv;
							handler.sendMessage(msg);
							continue;
						}
					}
					lv.bm = bm;
					Message msg = new Message();
					msg.what = ConstantStatus.GetLocalSuccess;
					msg.obj = lv;
					handler.sendMessage(msg);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
