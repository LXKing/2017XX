package com.xiangxun.service;

import android.content.Context;

import com.xiangxun.app.XiangXunApplication;
import com.xiangxun.bean.AccidentInfo;
import com.xiangxun.bean.LoginInfo;
import com.xiangxun.bean.Type;
import com.xiangxun.cfg.SystemCfg;
import com.xiangxun.db.DBManager;
import com.xiangxun.http.HttpUtil;
import com.xiangxun.request.ApiUrl;
import com.xiangxun.util.ImageUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class SendAccData implements IBaseSendData {
	private ArrayList<Type> weatherList;
	private ArrayList<Type> accTypeList;
	private Context context;
	public SendAccData(Context context) {
		this.context = context;
	}

	@Override
	public boolean run(Object obj) {
		// 获取天气信息
		weatherList = DBManager.getInstance().getWeatherTypes();
		// 获取事故类型
		accTypeList = DBManager.getInstance().getAccTypes();
		AccidentInfo ai = (AccidentInfo) obj;
		// 将drawable文件夹下的空图片写入制定路径
		ImageUtils imgU = new ImageUtils(context);
		imgU.copyToSD(context);

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("image1", ai.pic1 != null ? ai.pic1 : ""));
		params.add(new BasicNameValuePair("image2", ai.pic2 != null ? ai.pic2 : ""));
		params.add(new BasicNameValuePair("image3", ai.pic3 != null ? ai.pic3 : ""));
		params.add(new BasicNameValuePair("image4", ai.pic4 != null ? ai.pic4 : ""));
		params.add(new BasicNameValuePair("image5", ai.pic5 != null ? ai.pic5 : ""));
		params.add(new BasicNameValuePair("image6", ai.pic6 != null ? ai.pic6 : ""));
		params.add(new BasicNameValuePair("image7", ai.pic7 != null ? ai.pic7 : ""));
		params.add(new BasicNameValuePair("image8", ai.pic8 != null ? ai.pic8 : ""));
		params.add(new BasicNameValuePair("image9", ai.pic9 != null ? ai.pic9 : ""));
		params.add(new BasicNameValuePair("image10", ai.pic10 != null ? ai.pic10 : ""));
		params.add(new BasicNameValuePair("datetime", ai.realtime));
		params.add(new BasicNameValuePair("acctype", accTypeList.get(ai.acctype).code));

		params.add(new BasicNameValuePair("caller", ai.caller));
		params.add(new BasicNameValuePair("memo", ai.memo));
		params.add(new BasicNameValuePair("phone", ai.phone));
		params.add(new BasicNameValuePair("location", DBManager.getInstance().getRoadIdByName(ai.roadname)));
		params.add(new BasicNameValuePair("roadlocation", ai.roadlocation));
		params.add(new BasicNameValuePair("roaddirect", ai.roaddirect));
		params.add(new BasicNameValuePair("carnum", Integer.toString(ai.carnum)));
		params.add(new BasicNameValuePair("weather", weatherList.get(ai.weather).code));
		params.add(new BasicNameValuePair("death", Integer.toString(ai.death)));
		params.add(new BasicNameValuePair("hurt", Integer.toString(ai.hurt)));
		params.add(new BasicNameValuePair("userid", SystemCfg.getUserId(XiangXunApplication.getInstance())));
		params.add(new BasicNameValuePair("joinlist ", ai.joinlist));
		params.add(new BasicNameValuePair("infolist ", ai.infolist));

		//String url = Api.urlHost + "/mpts/mobile/acc/doAccEventInfo/?sessionid=" + LoginInfo.sessionId;
		String url = ApiUrl.doAccEventInfo(context) + "?sessionid=" + LoginInfo.sessionId;
		HttpUtil hu = new HttpUtil();
		if (hu.upLoadMultiDataToServer(url, params)) {
			DBManager.getInstance().accidentUp(ai.id);
			return true;
		} else
			return false;

	}

}
