package com.xiangxun.service;

import android.content.Context;

import com.xiangxun.app.XiangXunApplication;
import com.xiangxun.bean.LawCheckInfo;
import com.xiangxun.bean.LoginInfo;
import com.xiangxun.cfg.SystemCfg;
import com.xiangxun.db.DBManager;
import com.xiangxun.http.HttpUtil;
import com.xiangxun.request.ApiUrl;
import com.xiangxun.util.ImageUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class SendLawCheckData implements IBaseSendData {
	private Context context;

	public SendLawCheckData(Context context) {
		this.context = context;
	}

	@Override
	public boolean run(Object obj) {
		// 将drawable文件夹下的空图片写入制定路径
		ImageUtils imgU = new ImageUtils(context);
		imgU.copyToSD(context);
		LawCheckInfo li = (LawCheckInfo) obj;
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("image1", li.pic1));
		params.add(new BasicNameValuePair("image2", li.pic2));
		params.add(new BasicNameValuePair("image3", li.pic3));
		params.add(new BasicNameValuePair("image4", li.pic4));
		params.add(new BasicNameValuePair("datetime", li.datetime));
		params.add(new BasicNameValuePair("actual", li.actual));
		params.add(new BasicNameValuePair("drivername", li.drivername));
		params.add(new BasicNameValuePair("cartype", li.cartype));
		params.add(new BasicNameValuePair("roadid", li.road));
		params.add(new BasicNameValuePair("roadlocation", li.roadlocation));
		params.add(new BasicNameValuePair("roaddirect", li.roaddirect));
		params.add(new BasicNameValuePair("handler", SystemCfg.getUserId(XiangXunApplication.getInstance())));
		params.add(new BasicNameValuePair("phone", li.phone));
		params.add(new BasicNameValuePair("platenum", li.platenum));
		params.add(new BasicNameValuePair("checktype", li.checktype));
		params.add(new BasicNameValuePair("must", li.must));
		params.add(new BasicNameValuePair("memo", li.memo));
		params.add(new BasicNameValuePair("handleway", li.handleway));
		params.add(new BasicNameValuePair("owner", li.owner));
		params.add(new BasicNameValuePair("drivernum", li.drivernum));

		params.add(new BasicNameValuePair("viotype", li.viotype));

		//String url = Api.urlHost + "/mpts/mobile/om/doDataLedger/?sessionid=" + LoginInfo.sessionId;
		String url = ApiUrl.doDataLedger(context) + "?sessionid=" + LoginInfo.sessionId;
		HttpUtil hu = new HttpUtil();
		if (hu.upLoadMultiDataToServer(url, params)) {
			DBManager.getInstance().taizhangUp(li.datetime);
			return true;
		} else
			return false;

	}

}
