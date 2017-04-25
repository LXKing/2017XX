/**
 * 
 */
package com.xiangxun.service;

import android.content.Context;

import com.xiangxun.app.XiangXunApplication;
import com.xiangxun.bean.GPSInfo;
import com.xiangxun.bean.LoginInfo;
import com.xiangxun.cfg.SystemCfg;
import com.xiangxun.db.DBManager;
import com.xiangxun.http.HttpUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import static com.xiangxun.request.AppBuildConfig.DEBUGURL;
import static com.xiangxun.util.Utils.SaveGPSdata;

/**
 * @author ��ѩ��
 * 
 */
public class SendGpsData implements IBaseSendData {
	private Context context;
	
	public SendGpsData(Context context) {
		this.context = context;
	}

	@Override
	public boolean run(Object obj) {
		GPSInfo gps = (GPSInfo) obj;
		if(DBManager.getInstance().isGpsExisted(gps.provider,gps.latitude,gps.longitude) && gps.isupfile == 1){
			return true;
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("pmid", SystemCfg.getPoliceCode(XiangXunApplication.getInstance())));
		params.add(new BasicNameValuePair("devid", XiangXunApplication.getInstance().getDevId()));
		params.add(new BasicNameValuePair("datetime", gps.datetime));
		params.add(new BasicNameValuePair("altitude", gps.altitude));
		params.add(new BasicNameValuePair("latitude", gps.latitude));
		params.add(new BasicNameValuePair("longitude", gps.longitude));
		params.add(new BasicNameValuePair("accuracy", gps.bearing));
		String url = "http://" + SystemCfg.getServerIP(context) + ":" + SystemCfg.getServerPort(context) + "/mpts/mobile/gps/doAdd/?sessionid=" + LoginInfo.sessionId;

//		HttpUtil hu = new HttpUtil();
//		if (hu.upLoadCommonDataToServer(url, params)) {
//			DBManager.getInstance().gpsDataup(gps.provider);
//			if (DEBUGURL)
//				SaveGPSdata(gps, true);
//			return true;
//		}
//		if (DEBUGURL)
//			SaveGPSdata(gps, false);
//		return true;

		//修改为发送失败不重发
		HttpUtil hu = new HttpUtil();
		if (hu.upLoadCommonDataToServer(url, params)) {
			//发送成功
			if (DEBUGURL)
				SaveGPSdata(gps, true);
		} else {
			//发送失败
			if (DEBUGURL)
				SaveGPSdata(gps, false);
		}
		DBManager.getInstance().gpsDataup(gps.provider);
		return true;
	}
}
