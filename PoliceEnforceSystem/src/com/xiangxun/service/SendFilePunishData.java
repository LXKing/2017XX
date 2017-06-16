package com.xiangxun.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

import com.xiangxun.app.XiangXunApplication;
import com.xiangxun.bean.FieldPunishData;
import com.xiangxun.bean.LoginInfo;
import com.xiangxun.cfg.SystemCfg;
import com.xiangxun.db.DBManager;
import com.xiangxun.http.HttpUtil;
import com.xiangxun.request.ApiUrl;

public class SendFilePunishData implements IBaseSendData {
	Context context;
	public SendFilePunishData(Context context) {
		this.context = context;
	}

	@Override
	public boolean run(Object obj) {
		return false;
	}

	public boolean sendData(FieldPunishData data) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", data.vioid));
		params.add(new BasicNameValuePair("userid", SystemCfg.getUserId(XiangXunApplication.getInstance())));
		params.add(new BasicNameValuePair("name", data.name));
		params.add(new BasicNameValuePair("owner", data.owner));
		params.add(new BasicNameValuePair("phone", data.phone));
		params.add(new BasicNameValuePair("driverlic", data.driverlic));
		params.add(new BasicNameValuePair("recordid", data.recordid));
		params.add(new BasicNameValuePair("licenseoffice", data.licenseoffice));
		params.add(new BasicNameValuePair("licensetype", data.licensetype));
		params.add(new BasicNameValuePair("plate", data.plate));
		params.add(new BasicNameValuePair("platetype", data.platetype));
		params.add(new BasicNameValuePair("location", DBManager.getInstance().getRoadIdByName(data.location)));
		params.add(new BasicNameValuePair("roadlocation", data.roadlocation));
		params.add(new BasicNameValuePair("roaddirect", data.roaddirect));
		params.add(new BasicNameValuePair("datetime", data.datetime));
		params.add(new BasicNameValuePair("money", Integer.toString(data.money)));
		params.add(new BasicNameValuePair("pdncode", data.id));
		//String url = Api.urlHost + "/mpts/mobile/vio/doDispose/?sessionid=" + LoginInfo.sessionId;
		String url = ApiUrl.doDispose(context) + "?sessionid=" + LoginInfo.sessionId;
		HttpUtil hu = new HttpUtil();
		if (hu.upLoadCommonDataToServer(url, params)) {
			// 上传成功则更改标志位为“已上传”
			DBManager.getInstance().fieldPunishDataUp(data.vioid);
			return true;
		}
		return false;
	}

}
