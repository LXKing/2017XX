package com.xiangxun.service;

import android.content.Context;

import com.xiangxun.app.XiangXunApplication;
import com.xiangxun.bean.EnforceVio;
import com.xiangxun.bean.ParkVio;
import com.xiangxun.bean.SimpleVio;
import com.xiangxun.bean.UpdateVio;
import com.xiangxun.cfg.SystemCfg;
import com.xiangxun.db.DBManager;
import com.xiangxun.http.HttpUtil;
import com.xiangxun.request.ApiUrl;
import com.xiangxun.util.ConstantStatus;
import com.xiangxun.widget.MsgToast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class SendUpdateVio implements IBaseSendData {
	private Context context;

	private UpdateVio updateVio;
	private SimpleVio simpleVio;
	private EnforceVio enforceVio;
	private ParkVio parkVio;

	private List<NameValuePair> params = new ArrayList<NameValuePair>();

	public SendUpdateVio(Context context) {
		this.context = context;
	}

	@Override
	public boolean run(Object obj) {
		String UpdateURL = "";

		updateVio = (UpdateVio) obj;
		if (updateVio.isupfile == 0) {
			params.clear();
			if ("simple".equals(updateVio.type)) {
				simpleVio = (SimpleVio) updateVio.getVioData();

				params.add(new BasicNameValuePair("xmldata", simpleVio.toXML()));
				UpdateURL = ApiUrl.doWarnSimpleVio(context);
			} else if ("enforce".equals(updateVio.type)) {
				enforceVio = (EnforceVio) updateVio.getVioData();

				params.add(new BasicNameValuePair("yhbz", SystemCfg.getPoliceCode(XiangXunApplication.getInstance())));
				params.add(new BasicNameValuePair("xmldata", enforceVio.toXML()));
				UpdateURL = ApiUrl.doWarnEnforceVio(context);
			} else {
				parkVio = (ParkVio) updateVio.getVioData();

				params.add(new BasicNameValuePair("yhbz", SystemCfg.getPoliceCode(XiangXunApplication.getInstance())));
				params.add(new BasicNameValuePair("dwjgdm", SystemCfg.getDepartmentCode(XiangXunApplication.getInstance())));
				params.add(new BasicNameValuePair("xmldata", parkVio.toXML()));
				UpdateURL = ApiUrl.doWarnParkVio(context);
			}

			HttpUtil hu = new HttpUtil();
			int Status = hu.upLoadWarnDataToServer(UpdateURL, params);
			if (Status == ConstantStatus.UpStatusSuccess) {
				DBManager.getInstance().VioUpdateUp(updateVio.type, updateVio.sn, Status);

				//标记编号已经使用
//				if ("simple".equals(updateVio.type))
//					DBManager.getInstance().markPunishId(updateVio.sn, "punish");
//				else {
//					if ("3".equals(enforceVio.wslb)) //强制措施
//						DBManager.getInstance().markPunishId(updateVio.sn, "force");
//					else
//						DBManager.getInstance().markPunishId(updateVio.sn, "dispose");
//				}
			} else if (Status == ConstantStatus.UpStatusException || Status == ConstantStatus.UpStatusFail) {
				return false;
			} else if (Status == ConstantStatus.UpStatusOldCode) {
				MsgToast.geToast().setMsg("违法行为代码不在有效期内，请重新提交表单.");
				return false;
			} else {
				DBManager.getInstance().VioUpdateUp(updateVio.type, updateVio.sn, Status);
				return false;
			}
		}

		return true;
	}
}
