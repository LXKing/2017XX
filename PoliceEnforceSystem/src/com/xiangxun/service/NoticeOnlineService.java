package com.xiangxun.service;

import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.xiangxun.app.XiangXunApplication;
import com.xiangxun.bean.LoginInfo;
import com.xiangxun.cfg.SystemCfg;
import com.xiangxun.http.HttpUtil;
import com.xiangxun.util.NetUtils;

public class NoticeOnlineService extends Service {
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				synchronized (this) {
					while (!Thread.currentThread().isInterrupted()) {
						try {
							// 判断应用是否在运行
							ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
							List<RunningTaskInfo> list = am.getRunningTasks(3);
							for (RunningTaskInfo info : list) {
								if (info.topActivity.getPackageName().equals("com.xiangxun.activity")) {
									break;
								}
							}
							if(!LoginInfo.isOffLine && LoginInfo.isLogined){
								System.out.println("tip");
								HttpUtil hu = new HttpUtil();
								String resStr = hu.queryStringForGet("http://" + SystemCfg.getServerIP(XiangXunApplication.getInstance()) + ":" + SystemCfg.getServerPort(XiangXunApplication.getInstance()) + "/mpts/mobile/gps/updateByCode/" + XiangXunApplication.getInstance().getDevId() + "/?sessionid=" + LoginInfo.sessionId);
							}
							Thread.sleep(1000 * 30);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}).start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

}
