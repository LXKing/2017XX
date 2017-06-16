package com.xiangxun.service;

import android.content.Context;

import com.xiangxun.app.XiangXunApplication;
import com.xiangxun.bean.EnforcementData;
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

public class SendEnforcementData implements IBaseSendData {
	private Context context;
	public SendEnforcementData(Context context) {
		this.context = context;
	}

	@Override
	public boolean run(Object obj) {
		return false;
	}

	public boolean sendData(EnforcementData data) {
		// 将drawable文件夹下的空图片写入制定路径
		ImageUtils imgU = new ImageUtils(context);
		imgU.copyToSD(context);

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("image1", data.pic1));
		params.add(new BasicNameValuePair("image2", data.pic2));
		params.add(new BasicNameValuePair("image3", data.pic3));
		params.add(new BasicNameValuePair("image4", data.pic4));
		params.add(new BasicNameValuePair("datetime", data.datetime));
		params.add(new BasicNameValuePair("licenseoffice", data.licenseoffice));
		params.add(new BasicNameValuePair("licensetype", data.licensetype));
		params.add(new BasicNameValuePair("name", data.name));
		params.add(new BasicNameValuePair("roadid", DBManager.getInstance().getRoadIdByName(data.location)));
		params.add(new BasicNameValuePair("roadlocation", data.roadlocation));
		params.add(new BasicNameValuePair("roaddirect", data.roaddirect));
		params.add(new BasicNameValuePair("userid", SystemCfg.getUserId(XiangXunApplication.getInstance())));
		params.add(new BasicNameValuePair("phone", data.phone));
		params.add(new BasicNameValuePair("plate", data.plate));
		params.add(new BasicNameValuePair("recordid", data.recordid));
		params.add(new BasicNameValuePair("enforceid", data.id));
		params.add(new BasicNameValuePair("platetype", data.platetype));
		params.add(new BasicNameValuePair("driverlic", data.driverlic));
		params.add(new BasicNameValuePair("licenseoffice", data.licenseoffice));
		params.add(new BasicNameValuePair("enforce", data.action));
//		params.add(new BasicNameValuePair("viotype", ToUploadViodata.vioCodes.get(ToUploadViodata.vioTypes.indexOf(data.viotype))));
//		params.add(new BasicNameValuePair("viotype", DBManager.getInstance().getVioCodeByName(data.viotype)));
		params.add(new BasicNameValuePair("viotype", data.code));
		//String url = Api.urlHost + "/mpts/mobile/vio/doForceMeasure/?sessionid=" + LoginInfo.sessionId;
		String url = ApiUrl.doForceMeasure(context) + "?sessionid=" + LoginInfo.sessionId;
		HttpUtil hu = new HttpUtil();
		if (hu.upLoadMultiDataToServer(url, params)) {
			// 上传成功则更改标志位为“已上传”
			DBManager.getInstance().enforcementDataUp(data.vioid);
			return true;
		} else
			return false;
	}

}
