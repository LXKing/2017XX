package com.xiangxun.service;

import android.content.Context;

import com.xiangxun.bean.AccidentInfo;
import com.xiangxun.bean.EnforcementData;
import com.xiangxun.bean.FieldPunishData;
import com.xiangxun.bean.GPSInfo;
import com.xiangxun.bean.LawCheckInfo;
import com.xiangxun.bean.PatorlInfo;
import com.xiangxun.bean.UpdateVio;
import com.xiangxun.bean.VioData;
import com.xiangxun.bean.WarnAck;
import com.xiangxun.bean.WarnMessage;
import com.xiangxun.bean.WarnPic;
import com.xiangxun.db.DBManager;
import com.xiangxun.util.ConstantStatus;

import java.util.List;

public class DataManager {
	private Context context;

	public DataManager(Context context) {
		this.context = context;
	}
	
	public int upGpsData() {
		int count = DBManager.getInstance().getUnUpGpsCount();
		if (count == -1)
			return -1;
		List<GPSInfo> list = DBManager.getInstance().getUnUpGpsInfo(0, 10);
		SendGpsData svd = new SendGpsData(context);

		for (int i = 0; i < list.size(); i++) {
			if (!svd.run(list.get(i)))
				break;
		}
		return count;

	}

	public int upVioData() {
		int count = getVioCount();
		if (count == -1)
			return -1;
		List<VioData> list = DBManager.getInstance().getUnUpViodata(0, 10);
		SendVioData svd = new SendVioData(context);

		for (int i = 0; i < list.size(); i++) {
			if (!svd.run(list.get(i)))
				break;
		}
		return count;

	}

	public int upFieldPunishData() {
		int count = -1;
		count = DBManager.getInstance().getUnUpFieldPunishDataCount();
		if (count < 1)
			return -1;

		List<FieldPunishData> list = DBManager.getInstance().getUnUpFieldPunishData(0, 10);
		SendFilePunishData sfpd = new SendFilePunishData(context);
		for (int i = 0; i < list.size(); i++) {
			if (!sfpd.sendData(list.get(i)))
				break;
		}
		return count;
	}

	public int upEnforcementData() {
		int count = -1;
		count = DBManager.getInstance().QueryUnUpEnforceDataCount();
		if (count < 1)
			return -1;
		List<EnforcementData> list = DBManager.getInstance().getUnUpEnforcementData(0,10);
		SendEnforcementData send = new SendEnforcementData(context);
		for (int i = 0; i < list.size(); i++) {
			if (!send.sendData(list.get(i)))
				break;
		}
		return count;
	}
	
	public int upDutyData() {
		int count = DBManager.getInstance().getUnUpPatorlCount();
		if (count <= 0)
			return -1;
		List<PatorlInfo> listpi;
		listpi = DBManager.getInstance().getUnUpPator(0, 10);
		SendDutyData sdd = new SendDutyData(context);

		for (int i = 0; i < listpi.size(); i++) {
			if (!sdd.run(listpi.get(i)))
				break;
		}
		return count;
	}
	
	
	public int upAccidentData() {
		int count = DBManager.getInstance().getUnUpAccidentCount();
		if (count <= 0)
			return -1;
		List<AccidentInfo> listAI;
		listAI = DBManager.getInstance().getUnUpAccident(0, 10);
		SendAccData sad = new SendAccData(context);

		for (int i = 0; i < listAI.size(); i++) {
			if (!sad.run(listAI.get(i)))
				break;
		}
		return count;
	}
	
	public int upLawCheckData() {
		int count = DBManager.getInstance().QueryUnUpLawCheckDataCount();
		if (count <= 0)
			return -1;
		List<LawCheckInfo> listAI;
		listAI = DBManager.getInstance().getUnUpLawCheckInfo(0, 10);
		SendLawCheckData sad = new SendLawCheckData(context);

		for (int i = 0; i < listAI.size(); i++) {
			if (!sad.run(listAI.get(i)))
				break;
		}
		return count;
	}

	public void clearGPSLimitData() {
		DBManager.getInstance().clearGPSData();
	}

	private int getVioCount() {
		int res = -1;
		res = DBManager.getInstance().getunUpViodataCount();
		return res;
	}

	public int upWarnAckData() {
		int count = DBManager.getInstance().QueryUnUpWarnAckDataCount();
		if (count <= 0)
			return -1;
		List<WarnAck> listAI;
		listAI = DBManager.getInstance().getUnUpWarnAckInfo(0, 10);
		SendWarnAckData sad = new SendWarnAckData(context);

		for (int i = 0; i < listAI.size(); i++) {
			sad.run(listAI.get(i));
		}
		return count;
	}

	public int upWarnPicData() {
		int count = DBManager.getInstance().QueryUnUpWarnPicDataCount();
		if (count <= 0)
			return -1;
		List<WarnPic> listAI;
		listAI = DBManager.getInstance().getUnUpWarnPicInfo(0, 10);
		//剔除预警信息未上传成功的
		RemoveUnUpWarnAck(listAI);

		SendWarnPicData sad = new SendWarnPicData(context);

		for (int i = 0; i < listAI.size(); i++) {
			sad.run(listAI.get(i));
		}
		return count;
	}

	private void RemoveUnUpWarnAck(List<WarnPic> listPIC) {
		for (int len = listPIC.size(), i = len - 1; i >= 0; i --) {
			String yjxh = listPIC.get(i).yjxh;
			int Status = DBManager.getInstance().getWarnAckUpStatus(yjxh, "1");
			if (Status != ConstantStatus.UpStatusSuccess && Status != ConstantStatus.UpStatusSigned) {
				listPIC.remove(i);
				if (Status != ConstantStatus.UpStatusFail && Status != ConstantStatus.UpStatusException)
					DBManager.getInstance().WarnPicUp(yjxh);
			}
		}
	}

	public int upWarnMessage() {
		int count = DBManager.getInstance().QueryUnUpWarnMessageCount();
		if (count <= 0)
			return -1;
		List<WarnMessage> listAI;
		listAI = DBManager.getInstance().getUnUpWarnMessageInfo(0, 10);

		SendWarnMessage sad = new SendWarnMessage(context);

		for (int i = 0; i < listAI.size(); i++) {
			sad.run(listAI.get(i));
		}
		return count;
	}

	public int upUpdateVioData() {
		int count = DBManager.getInstance().QueryUnUpUpdateVioDataCount();
		if (count <= 0)
			return -1;
		List<UpdateVio> listAI;
		listAI = DBManager.getInstance().getUnUpUpdateVioInfo(0, 10);
		SendUpdateVio sad = new SendUpdateVio(context);

		for (int i = 0; i < listAI.size(); i++) {
			sad.run(listAI.get(i));
		}
		return count;
	}
}
