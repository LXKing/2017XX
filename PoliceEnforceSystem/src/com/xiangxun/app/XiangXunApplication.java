package com.xiangxun.app;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.Camera;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.StrictMode;
import android.telephony.TelephonyManager;

import com.google.gson.Gson;
import com.xiangxun.cfg.SystemCfg;
import com.xiangxun.common.DcHttpClient;
import com.xiangxun.driver.BluetoothPrintDriver;
import com.xiangxun.request.Api;
import com.xiangxun.service.MQTTService;
import com.xiangxun.service.MainService;
import com.xiangxun.util.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class XiangXunApplication extends Application {
	private MainService mService;
	private BluetoothPrintDriver mStdPrint;
	private Camera mCamera;
	private static final String lOCALE_CHANGED = Intent.ACTION_LOCALE_CHANGED;

	private String VideoServerIp;
	private int VideoServerPort;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}

	};
	private static XiangXunApplication sApplication;
	private String mDevId;
	private ExecutorService mThreadPool;
	private Logger mLogger;
	private static Gson gson;

	@Override
	public void onCreate() {
		sApplication = this;
		super.onCreate();

		Intent sintent = new Intent(this, MainService.class);
		bindService(sintent, conn, Service.BIND_AUTO_CREATE);
		registerReceiver(mReceiver, new IntentFilter(lOCALE_CHANGED));
		// 网络对象初始化
		DcHttpClient.getInstance().init(getBaseContext());
		// 日志记录
		mLogger = Logger.getLogger("xiangxun");
		Logger.tag = "XIANGXUN";
		mLogger.initLogWriter(Api.xXDir.concat("logs/outDebug.log"));
		mLogger.initExceptionHandler(this);
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()//
				.detectCustomSlowCalls()//
				.detectDiskReads()//
				.detectDiskWrites()//
				.detectNetwork()//
				.penaltyLog()//
				.penaltyFlashScreen()//
				.build());
		try {
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()//
					.detectLeakedClosableObjects()//
					.detectLeakedSqlLiteObjects()//
					.setClassInstanceLimit(Class.forName("com.apress.proandroid.SomeClass"), 100)//
					.penaltyLog()//
					.build());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static XiangXunApplication getInstance() {
		return sApplication;
	}

	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = ((MainService.mService) service).getService();
		}
	};

	public MainService getMainService() {
		if(mService != null){
			return mService;
		}
		throw new RuntimeException("MainService should be created before accessed");
	}

	public BluetoothPrintDriver getStdPrint(Handler handler) {
		if (mStdPrint == null) {
			mStdPrint = new BluetoothPrintDriver(this, handler);
		}
		return mStdPrint;
	}

	public Camera getCamera() {
		return mCamera;
	}

	public void setCamera(Camera camera) {
		mCamera = camera;
	}

	public void setVideoServer(String serverIp, int serverPort) {
		VideoServerIp = serverIp;
		VideoServerPort = serverPort;
	}

	public String getVideoServerIp() {
		return VideoServerIp;
	}

	public int getVideoServerPort() {
		return VideoServerPort;
	}

	public String getUserName() {
		return SystemCfg.getPoliceName(getInstance());
	}

	public String getUserId() {
		return SystemCfg.getUserId(getInstance());
	}


	public String getDevId() {
		if (mDevId == null) {
			TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			mDevId = tm.getDeviceId();
		}
		return mDevId;
	}

	public ExecutorService getThreadPool() {

		if (mThreadPool == null) {
			mThreadPool = Executors.newSingleThreadExecutor();
		}
		return mThreadPool;
	}
	
	public void startPushService() {
		MQTTService.actionStart(getInstance());
	}

	public void stopPushService() {
		MQTTService.actionStop(getInstance());
	}


	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(lOCALE_CHANGED)) {
				// Permissions.reInitPermissions(getInstance());
			}
		}
	};

	public static Gson getGson() {
		if (gson == null) {
			gson = new Gson();
		}
		return gson;
	};
}
