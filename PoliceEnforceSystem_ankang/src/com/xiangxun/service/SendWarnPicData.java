package com.xiangxun.service;

import android.content.Context;

import com.xiangxun.bean.WarnPic;
import com.xiangxun.db.DBManager;
import com.xiangxun.http.HttpUtil;
import com.xiangxun.request.ApiUrl;
import com.xiangxun.util.ConstantStatus;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class SendWarnPicData implements IBaseSendData {
	private Context context;
	public SendWarnPicData(Context context) {
		this.context = context;
	}

	@Override
	public boolean run(Object obj) {
		WarnPic li = (WarnPic) obj;

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("xmldata", li.toString()));	//业务数据写入XML格式文档

		String url = ApiUrl.doWarnPic(context);
		HttpUtil hu = new HttpUtil();
		int Status = hu.upLoadWarnDataToServer(url, params);
		if (Status == ConstantStatus.UpStatusSuccess) {
			DBManager.getInstance().WarnPicUp(li.yjxh);
			DBManager.getInstance().WarnAckUp(li.yjxh, "2", ConstantStatus.UpStatusSuccess);
			DBManager.getInstance().WarnAckUp(li.yjxh, "3", ConstantStatus.UpStatusSuccess);
			return true;
		} else if (Status == ConstantStatus.UpStatusNoAuthority
				|| Status == ConstantStatus.UpStatusSigned
				|| Status == ConstantStatus.UpStatusAcked
				|| Status == ConstantStatus.UpStatusNoinfo) {
//				DBManager.getInstance().WarnPicUp(li.yjxh);
			return true;
		} else
			return false;
	}
}
