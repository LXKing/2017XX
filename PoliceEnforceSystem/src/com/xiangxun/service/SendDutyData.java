/**
 * 
 */
package com.xiangxun.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

import com.xiangxun.app.XiangXunApplication;
import com.xiangxun.bean.LoginInfo;
import com.xiangxun.bean.PatorlInfo;
import com.xiangxun.cfg.SystemCfg;
import com.xiangxun.db.DBManager;
import com.xiangxun.http.HttpUtil;
import com.xiangxun.request.ApiUrl;
import com.xiangxun.util.ImageUtils;

/**
 * @author ��ѩ��
 * 
 */
public class SendDutyData implements IBaseSendData {
	private Context context;

	public SendDutyData(Context context) {
		this.context = context;
	}

	@Override
	public boolean run(Object obj) {
		// 将drawable文件夹下的空图片写入制定路径
		ImageUtils imgU = new ImageUtils(context);
		imgU.copyToSD(context);

		PatorlInfo pi = (PatorlInfo) obj;

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("image1", pi.pic1));
		params.add(new BasicNameValuePair("image2", pi.pic2));
		params.add(new BasicNameValuePair("image3", pi.pic3));
		params.add(new BasicNameValuePair("image4", pi.pic4));
		params.add(new BasicNameValuePair("datetime", pi.datetime));
		params.add(new BasicNameValuePair("memo", pi.memo));
		params.add(new BasicNameValuePair("patorltype", DBManager.getInstance().getDutyTypes().get(pi.patorltype).code));
		params.add(new BasicNameValuePair("roadid", DBManager.getInstance().getRoadIdByName(pi.roadname)));
		params.add(new BasicNameValuePair("roadlocation", pi.roadlocation));
		params.add(new BasicNameValuePair("roaddirect", pi.roaddirect));
		params.add(new BasicNameValuePair("userid", SystemCfg.getUserId(XiangXunApplication.getInstance())));
		//String url = Api.urlHost + "/mpts/mobile/om/doDailyWorking/?sessionid=" + LoginInfo.sessionId;
		String url = ApiUrl.doDailyWorking(context) + "?sessionid=" + LoginInfo.sessionId;
		HttpUtil hu = new HttpUtil();
		if (hu.upLoadMultiDataToServer(url, params)) {
			DBManager.getInstance().patorlUp(pi.id);
			return true;
		} else
			return false;
	}

}
