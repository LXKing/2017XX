package com.xiangxun.service;

import android.content.Context;

import com.xiangxun.bean.WarnAck;
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
public class SendWarnAckData implements IBaseSendData {
	private Context context;
	public SendWarnAckData(Context context) {
		this.context = context;
	}

	@Override
	public boolean run(Object obj) {
		WarnAck li = (WarnAck) obj;

		List<NameValuePair> params = new ArrayList<NameValuePair>();
//		params.add(new BasicNameValuePair("yjxh", li.yjxh));
//		params.add(new BasicNameValuePair("ywlx", li.ywlx));
//		params.add(new BasicNameValuePair("qsjg", li.qsjg));
//		params.add(new BasicNameValuePair("sfcjlj", li.sfcjlj));
//		params.add(new BasicNameValuePair("ljclqk", li.ljclqk));
//		params.add(new BasicNameValuePair("chjg", li.chjg));
//		params.add(new BasicNameValuePair("cljg", li.cljg));
//		params.add(new BasicNameValuePair("wsbh", li.wsbh));
//		params.add(new BasicNameValuePair("jyw", li.jyw));
//		params.add(new BasicNameValuePair("wslb", li.wslb));
//		params.add(new BasicNameValuePair("wchyy", li.wchyy));
//		params.add(new BasicNameValuePair("czqkms", li.czqkms));
//		params.add(new BasicNameValuePair("czdw", li.czdw));
//		params.add(new BasicNameValuePair("czr", li.czr));
//		params.add(new BasicNameValuePair("czsj", li.czsj));
//		params.add(new BasicNameValuePair("yjbm", li.yjbm));
//		params.add(new BasicNameValuePair("lxr", li.lxr));
//		params.add(new BasicNameValuePair("lxdh", li.lxdh));

//		params.add(new BasicNameValuePair("xtlb", "64"));					//系统类别		详见具体接口定义
//		params.add(new BasicNameValuePair("jkxlh", ""));					//接口序列号 	申请接口后将自动下发接口序列号
//		params.add(new BasicNameValuePair("jkid", "64W02"));				//接口ID		详见具体接口定义
		params.add(new BasicNameValuePair("xmldata", li.toString()));	//业务数据写入XML格式文档

		String url = ApiUrl.doWarnAck(context);
		HttpUtil hu = new HttpUtil();
		int Status = hu.upLoadWarnDataToServer(url, params);
		if (Status == ConstantStatus.UpStatusSuccess
		 		|| Status == ConstantStatus.UpStatusNoAuthority
				|| Status == ConstantStatus.UpStatusSigned
				|| Status == ConstantStatus.UpStatusAcked
				|| Status == ConstantStatus.UpStatusNoinfo) {
			if (li.ywlx.equals("2") || li.ywlx.equals("3")) {
				int CurStatus = DBManager.getInstance().getWarnAckUpStatus(li.yjxh, "1");
				if (CurStatus == ConstantStatus.UpStatusSuccess)
					return true;
			}
			DBManager.getInstance().WarnAckUp(li.yjxh, li.ywlx, Status);
			return true;
		} else
			return false;
	}
}
