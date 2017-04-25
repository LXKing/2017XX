package com.xiangxun.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.xiangxun.app.XiangXunApplication;
import com.xiangxun.bean.GPSInfo;
import com.xiangxun.db.DBManager;
import com.xiangxun.util.ConstantStatus;

import java.text.SimpleDateFormat;

public class GpsService extends Service implements TencentLocationListener{
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	public class threadBinder extends Binder {
		public GpsService getService() {
			return GpsService.this;
		}
	}

	public String addr = null;
	private threadBinder tbinder = new threadBinder();

	private TencentLocationManager mLocationManager;
	private boolean mStarted;

	@Override
	public void onCreate() {
		Log.i("GpsService", "backGroudService create");
		System.out.println("backGroudService create");
		super.onCreate();
		mLocationManager = TencentLocationManager.getInstance(this);
		// 设置坐标系为 gcj-02, 缺省坐标为 gcj-02, 所以通常不必进行如下调用
		mLocationManager.setCoordinateType(TencentLocationManager.COORDINATE_TYPE_GCJ02);
		startLocation();
	}

//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//		// 退出 activity 前一定要停止定位!
//		stopLocation();
//	}

	public void startLocation() {
		if (!mStarted) {
			mStarted = true;
			// 创建定位请求
			TencentLocationRequest request = TencentLocationRequest
					.create()
					.setRequestLevel(
							TencentLocationRequest.REQUEST_LEVEL_GEO)
					.setInterval(30 * 1000); // 设置定位周期, 建议值为 1s-20s

			// 开始定位
			mLocationManager.requestLocationUpdates(request, this);
		}
	}

	public void stopLocation() {
		if (mStarted) {
			mStarted = false;
			mLocationManager.removeUpdates(this);
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return tbinder;
	}

	@Override
	public void onLocationChanged(TencentLocation location, int error,String reason) {
		String msg = null;
		if (error == TencentLocation.ERROR_OK) {
			// 定位成功
			GPSInfo info = new GPSInfo();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			info.datetime = sdf.format(location.getTime());
			info.altitude = String.valueOf(0);
			info.longitude = String.valueOf(location.getLongitude());
			info.latitude = String.valueOf(location.getLatitude());
			info.bearing = String.valueOf(location.getAccuracy());
			info.isupfile = 0;
			info.jobs = "";
			info.provider = XiangXunApplication.getInstance().getDevId() + System.currentTimeMillis();
			info.bearing = "";

			if (info.longitude == null || info.longitude.equals("0.0") || info.longitude.equals("360.0")) {
				return;
			} else if (DBManager.getInstance().isGpsExisted(info.provider,info.latitude,info.longitude)) {
				return;
			} else /*if (NetUtils.isNetworkAvailable(GpsService.this))*/{
				GPSInfo last = DBManager.getInstance().getLastestGps();
				if (last.latitude != null && last.longitude != null) {
					if (last.latitude.equals(info.latitude) && last.longitude.equals(info.longitude))
						return;
				}
				DBManager.getInstance().add(info);
			}
			// Toast.makeText(getApplicationContext(), "λ�ã�" + locRes.Longitude
			// + "\n"+ locRes.Latitude, Toast.LENGTH_LONG).show();

			XiangXunApplication.getInstance().getMainService().start(ConstantStatus.UPLOAD_GPSDATA);
		} else {
			// 定位失败
//			msg = "定位失败: " + reason;
		}
	}

	@Override
	public void onStatusUpdate(String name, int status, String desc) {
		// ignore
	}
}
