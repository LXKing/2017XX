package com.xiangxun.service;

import android.content.Context;

import com.xiangxun.app.XiangXunApplication;
import com.xiangxun.bean.LoginInfo;
import com.xiangxun.bean.VioData;
import com.xiangxun.cfg.SystemCfg;
import com.xiangxun.db.DBManager;
import com.xiangxun.http.HttpUtil;
import com.xiangxun.request.ApiUrl;
import com.xiangxun.util.ImageUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class SendVioData implements IBaseSendData {
	private Context context;
	public SendVioData(Context context) {
		this.context = context;
	}

	@Override
	public boolean run(Object obj) {
		VioData vioData = (VioData) obj;
		ImageUtils imgU = new ImageUtils(context);
		imgU.copyToSD(context);

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("image1", vioData.picurl0));
		params.add(new BasicNameValuePair("image2", vioData.picurl1));
		params.add(new BasicNameValuePair("image3", vioData.picurl2));
		params.add(new BasicNameValuePair("image4", vioData.picurl3));
		params.add(new BasicNameValuePair("platenum", vioData.platenum));
		params.add(new BasicNameValuePair("datetime", vioData.datetime));
		params.add(new BasicNameValuePair("platetype", vioData.platetype));
		params.add(new BasicNameValuePair("roadid", vioData.roadid));
		params.add(new BasicNameValuePair("roadName", vioData.roadname));
		params.add(new BasicNameValuePair("roadlocation", vioData.roadlocation));
		params.add(new BasicNameValuePair("roaddirect", vioData.roaddirect));
		params.add(new BasicNameValuePair("userid", SystemCfg.getUserId(XiangXunApplication.getInstance())));
		params.add(new BasicNameValuePair("vioid", vioData.vioid));
		params.add(new BasicNameValuePair("viocode", vioData.viocode));
		params.add(new BasicNameValuePair("datasource", Integer.toString(vioData.datasource)));
		params.add(new BasicNameValuePair("dealstate", Integer.toString(vioData.dealstate)));
		params.add(new BasicNameValuePair("issure", Integer.toString(vioData.issure)));
		params.add(new BasicNameValuePair("code", vioData.publishCode != null ? vioData.publishCode : ""));
		if(vioData.disposetype == 1){
			params.add(new BasicNameValuePair("type", "dispose"));
		} else {
			params.add(new BasicNameValuePair("type", "park"));
		}
		//String url = Api.urlHost + "/mpts/mobile/vio/doAdd/?sessionid=" + LoginInfo.sessionId;
		String url = ApiUrl.doAdd(context) + "?sessionid=" + LoginInfo.sessionId;
		HttpUtil hu = new HttpUtil();
		if (hu.upLoadMultiDataToServer(url, params)) {
			DBManager.getInstance().vioDataup(vioData.vioid);
			if(vioData.issure == 1){
				LoginInfo.isViodataUpLoaded = true;
			}
			return true;
		}
		return false;
	}

}
