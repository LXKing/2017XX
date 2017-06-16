package com.xiangxun.service;

import android.content.Context;

import com.xiangxun.app.XiangXunApplication;
import com.xiangxun.bean.EnforceVio;
import com.xiangxun.bean.SimpleVio;
import com.xiangxun.bean.WarnAck;
import com.xiangxun.bean.WarnData;
import com.xiangxun.bean.WarnMessage;
import com.xiangxun.bean.WarnPic;
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
public class SendWarnMessage implements IBaseSendData {
	private Context context;

	private WarnData data;
	private WarnAck sign;
	private SimpleVio simpleVio;
	private EnforceVio enforceVio;
	private WarnAck ack;
	private WarnPic pic;

	private List<NameValuePair> params = new ArrayList<NameValuePair>();

	public SendWarnMessage(Context context) {
		this.context = context;
	}

	@Override
	public boolean run(Object obj) {
		WarnMessage li = (WarnMessage) obj;

		if (li.signInfo != null && !li.signInfo.equals("")) {
			if (sendSignInfo(li) == false)
				return false;
		}

		if (li.vioInfo != null && !li.vioInfo.equals("")) {
			if (sendVioInfo(li) == false)
				return false;
		}

		if (li.ackInfo != null && !li.ackInfo.equals("")) {
			if (sendAckInfo(li) == false)
				return false;
		} else
			return false;

		if (li.picInfo != null && !li.picInfo.equals("")) {
			if (sendPicInfo(li) == false)
				return false;
		} else
			return false;

		DBManager.getInstance().WarnMessageUpdate(li.yjxh, "isupfile", "1");
		return true;
	}

	private boolean sendSignInfo(WarnMessage li) {
		sign = li.getWarnSign();
		if (sign.isupfile == 0) {
			params.clear();
			params.add(new BasicNameValuePair("xmldata", sign.toXML()));

			String url = ApiUrl.doWarnAck(context);
			HttpUtil hu = new HttpUtil();
			int Status = hu.upLoadWarnDataToServer(url, params);
			if (Status == ConstantStatus.UpStatusSuccess) {
				sign.isupfile = 1;
				DBManager.getInstance().WarnMessageUpdate(li.yjxh, "sign", XiangXunApplication.getGson().toJson(sign));
				return true;
			} else if (ConstantStatus.UpStatusException == Status|| ConstantStatus.UpStatusFail == Status) {
				return false;
			} else { //UpStatusNoAuthority UpStatusSigned
				DBManager.getInstance().WarnMessageUpdate(li.yjxh, "isupfile", Integer.toString(Status));

				sign.isupfile = Status;
				DBManager.getInstance().WarnMessageUpdate(li.yjxh, "sign", XiangXunApplication.getGson().toJson(sign));

				data = XiangXunApplication.getGson().fromJson(li.dataInfo, WarnData.class);
				data.setIsAck(Status);
				DBManager.getInstance().WarnMessageUpdate(li.yjxh, "data", XiangXunApplication.getGson().toJson(data));
				MsgToast.geToast().setMsg("预警信息签收失败，请刷新预警管理列表！");
				return false;
			}
		}

		return true;
	}

	private boolean sendVioInfo(WarnMessage li) {
		boolean status = false;

		if (li.vioInfo.contains("pzbh"))
			status = sendEnforceVioInfo(li);
		else
			status = sendSimpleVioInfo(li);

		return status;
	}

	private boolean sendSimpleVioInfo(WarnMessage li) {
		simpleVio = li.getSimpleVio();
		if (simpleVio.isupfile == 0) {
			params.clear();
			params.add(new BasicNameValuePair("xmldata", simpleVio.toXML()));

			String url = ApiUrl.doWarnSimpleVio(context);
			HttpUtil hu = new HttpUtil();
			int Status = hu.upLoadWarnDataToServer(url, params);
			if (Status == ConstantStatus.UpStatusSuccess) {
				simpleVio.isupfile = 1;
				DBManager.getInstance().WarnMessageUpdate(li.yjxh, "vio", XiangXunApplication.getGson().toJson(simpleVio));
				//标记编号已经使用
//				DBManager.getInstance().markPunishId(simpleVio.jdsbh, "punish");
			} else if (Status == ConstantStatus.UpStatusException || Status == ConstantStatus.UpStatusFail) {
				return false;
			} else if (Status == ConstantStatus.UpStatusOldCode) {
				DBManager.getInstance().WarnMessageUpdate(li.yjxh, "vio", "");
				MsgToast.geToast().setMsg("违法行为代码不在有效期内，请重新提交表单.");
				return false;
			} else if (Status == ConstantStatus.UpStatusIllegalPolice) {
				DBManager.getInstance().WarnMessageUpdate(li.yjxh, "vio", "");
				MsgToast.geToast().setMsg("无权限提交表单");
				return false;
			} else {
				return false;
			}
		}

		return true;
	}

	private boolean sendEnforceVioInfo(WarnMessage li) {
		enforceVio = li.getEnforceVio();
		if (enforceVio.isupfile == 0) {
			params.clear();
			params.add(new BasicNameValuePair("xmldata", enforceVio.toXML()));

			String url = ApiUrl.doWarnEnforceVio(context);
			HttpUtil hu = new HttpUtil();
			int Status = hu.upLoadWarnDataToServer(url, params);
			if (Status == ConstantStatus.UpStatusSuccess) {
				enforceVio.isupfile = 1;
				DBManager.getInstance().WarnMessageUpdate(li.yjxh, "vio", XiangXunApplication.getGson().toJson(enforceVio));

				//标记编号已经使用
//				if ("3".equals(enforceVio.wslb)) //强制措施
//					DBManager.getInstance().markPunishId(enforceVio.pzbh, "force");
//				else  //违法程序处罚
//					DBManager.getInstance().markPunishId(enforceVio.pzbh, "dispose");
			} else if (Status == ConstantStatus.UpStatusException || Status == ConstantStatus.UpStatusFail) {
				return false;
			} else if (Status == ConstantStatus.UpStatusOldCode) {
				DBManager.getInstance().WarnMessageUpdate(li.yjxh, "vio", "");
				MsgToast.geToast().setMsg("违法行为代码不在有效期内，请重新提交表单.");
				return false;
			} else {
				return false;
			}
		}

		return true;
	}

	private boolean sendAckInfo(WarnMessage li) {
		ack = li.getWarnAck();
		if (ack.isupfile == 0) {
			params.clear();
			params.add(new BasicNameValuePair("xmldata", ack.toXML()));

			String url = ApiUrl.doWarnAck(context);
			HttpUtil hu = new HttpUtil();
			int Status = hu.upLoadWarnDataToServer(url, params);
			if (Status == ConstantStatus.UpStatusSuccess) {
				ack.isupfile = 1;
				DBManager.getInstance().WarnMessageUpdate(li.yjxh, "ack", XiangXunApplication.getGson().toJson(ack));
				return true;
			}
			else if (ConstantStatus.UpStatusException == Status|| ConstantStatus.UpStatusFail == Status) {
				return false;
			} else {
				DBManager.getInstance().WarnMessageUpdate(li.yjxh, "isupfile", Integer.toString(Status));

				ack.isupfile = Status;
				DBManager.getInstance().WarnMessageUpdate(li.yjxh, "ack", XiangXunApplication.getGson().toJson(ack));

				data = li.getWarnData();
				data.setIsAck(Status);
				DBManager.getInstance().WarnMessageUpdate(li.yjxh, "data", XiangXunApplication.getGson().toJson(data));
				MsgToast.geToast().setMsg("预警信息反馈或签收反馈失败，请刷新预警管理列表！");
				return false;
			}
		}

		return true;
	}

	private boolean sendPicInfo(WarnMessage li) {
		pic = li.getWarnPic();
		if (pic.isupfile == 0) {
			params.clear();
			params.add(new BasicNameValuePair("xmldata", pic.toXML()));

			String url = ApiUrl.doWarnPic(context);
			HttpUtil hu = new HttpUtil();
			int Status = hu.upLoadWarnDataToServer(url, params);
			if (Status == ConstantStatus.UpStatusSuccess) {
				pic.isupfile = 1;
				DBManager.getInstance().WarnMessageUpdate(li.yjxh, "pic", XiangXunApplication.getGson().toJson(pic));
				return true;
			}
			else if (ConstantStatus.UpStatusException == Status|| ConstantStatus.UpStatusFail == Status) {
				return false;
			} else {
				pic.isupfile = Status;
				DBManager.getInstance().WarnMessageUpdate(li.yjxh, "pic", XiangXunApplication.getGson().toJson(pic));

				data = XiangXunApplication.getGson().fromJson(li.dataInfo, WarnData.class);
				data.setIsAck(Status);
				DBManager.getInstance().WarnMessageUpdate(li.yjxh, "data", XiangXunApplication.getGson().toJson(data));
				MsgToast.geToast().setMsg("预警信息处置图片失败，请刷新预警管理列表！");
				return false;
			}
		}

		return true;
	}
}
