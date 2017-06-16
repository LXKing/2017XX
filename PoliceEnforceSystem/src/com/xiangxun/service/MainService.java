package com.xiangxun.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;

import com.xiangxun.db.DBManager;
import com.xiangxun.util.ConstantStatus;

public class MainService extends Service {
	private Thread tSendData;
	private int isFlag = ConstantStatus.UPLOAD_ALLDATA;
	private recver mrecver;

	private class recver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			MainService.this.startActivity(arg1);
		}

	}

	private mService mBinder = new mService();

	public class SendThread implements Runnable {
		DataManager dm = new DataManager(MainService.this);;
		public void run() {
			try {
				if (true) {
					switch (isFlag) {
					case ConstantStatus.UPLOAD_GPSDATA:
					case ConstantStatus.UPLOAD_VIODATA:
					case ConstantStatus.UPLOAD_FIELDPUNISHDATA:
					case ConstantStatus.UPLOAD_ENFORCEMENTDATA:
					case ConstantStatus.UPLOAD_DUTYDATA:
					case ConstantStatus.UPLOAD_ACCIDENTDATA:
					case ConstantStatus.UPLOAD_LAWCHECKDATA:
					case ConstantStatus.UPLOAD_WARNACKDATA:
					case ConstantStatus.UPLOAD_ALLDATA:
						// 上传GPS信息
						int countGPSAll = DBManager.getInstance().getUnUpGpsCount();
						if (countGPSAll > 0) {
							dm.upGpsData();
						}
						// 上传违法信息
						int countVioAll = DBManager.getInstance().getunUpViodataCount();
						if (countVioAll > 0) {
							dm.upVioData();
						}
						// 上传违法处理信息
						int countPunishAll = DBManager.getInstance().getUnUpFieldPunishDataCount();
						if (countPunishAll > 0) {
							dm.upFieldPunishData();
						}
						// 上传行政强制措施
						int countEnforceAll = DBManager.getInstance().QueryUnUpEnforceDataCount();
						if (countEnforceAll > 0) {
							dm.upEnforcementData();
						}
						// 上传日常勤务
						int countDutyAll = DBManager.getInstance().getUnUpPatorlCount();
						if (countDutyAll > 0) {
							dm.upDutyData();
						}
						// 上传事故信息
						int countAccidentAll = DBManager.getInstance().getUnUpAccidentCount();
						if (countAccidentAll > 0) {
							dm.upAccidentData();
						}
						// 上传执法检查信息
						int countLawCheckAll = DBManager.getInstance().QueryUnUpLawCheckDataCount();
						if (countLawCheckAll > 0) {
							dm.upLawCheckData();
						}
						// 上传六合一
						int countUpdateVioAll = DBManager.getInstance().QueryUnUpUpdateVioDataCount();
						if (countUpdateVioAll > 0) {
							dm.upUpdateVioData();
						}

						// 上传预警反馈
						int countWarnMessage = DBManager.getInstance().QueryUnUpWarnMessageCount();
						if (countWarnMessage > 0) {
							dm.upWarnMessage();
						}
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_REDELIVER_INTENT; // ��kill����������������
	}

	public class mService extends Binder {
		public MainService getService() {
			return MainService.this;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mrecver = new recver();
		this.registerReceiver(mrecver, new IntentFilter("rebukview"));
		tSendData = new Thread(new SendThread());
		tSendData.setName("SendThread");
		tSendData.start(); // 联网发送数据
	}

	public void start(int isFlag) {
		this.isFlag = isFlag;
		if (tSendData != null && tSendData.isAlive()) {
			tSendData.interrupt();
			tSendData = null;
		}
		tSendData = new Thread(new SendThread());
		tSendData.setName("SendThread");
		tSendData.start();
	}

	@Override
	public void onDestroy() {
		this.unregisterReceiver(mrecver);
		super.onDestroy();
		DBManager.getInstance().close();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public String getPlateRecLisn() {
		String res = null;
		return res;
	}

	public String getMacLisn(String mCode) {
		String res = null;
		return res;
	}

	public boolean isPrintDeviceConnectted() {
		return false;
	}

	public void clearGPSLimitData() {
		DataManager dm = new DataManager(MainService.this);
		dm.clearGPSLimitData();
	}

}
