package com.xiangxun.activity.warn;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/1/6.
 */

public class WarnTable {
	public String[] DOMETHOD = {"现场开具处罚文书","移交其他部门","当事人接受处理","扣留机动车","已处罚","当事人拒绝处理","教育告知后放行"};
	public String[] NORMALREASON = {"布控信息有误","号牌识别错误","违法记录错误","原车","车辆基本信息未更新","内部车辆","车辆已年检","违法已处理","车辆品牌识别错误","识别与登记品牌未对应","接驳车辆","卡口未校","驾驶人驾驶证正常","非车主本人驾驶","检查无异常"};
	public String[] NOFIND = {"未发现车辆","车辆未停或强行闯卡","车辆掉头或绕行","其他"};
	public String[] DOCUMENT = {"","简易程序处罚决定书","","强制措施凭证","","","违法处理通知书"};


	public String WARNMETHOD = "{\"total\":\"24\",\"data\":[{\"warntype\":\"01\",\"warnname\":\"事故逃逸车辆\",\"method\":[{\"code\":\"5\",\"value\":\"扣留机动车\",\"must\":true,\"link\":\"2\",\"radio\":false,\"checked\":true,\"document\":\"3\"},{\"code\":\"2\",\"value\":\"现场开具处罚文书\",\"must\":false,\"link\":\"\",\"radio\":false,\"checked\":true,\"document\":\"0\"},{\"code\":\"3\",\"value\":\"移交其他部门\",\"must\":false,\"link\":\"\",\"radio\":false,\"checked\":false,\"document\":\"\"}]},{\"warntype\":\"02\",\"warnname\":\"套牌车辆\",\"method\":[{\"code\":\"5\",\"value\":\"扣留机动车\",\"must\":true,\"link\":\"2\",\"radio\":false,\"checked\":true,\"document\":\"3\"},{\"code\":\"2\",\"value\":\"现场开具处罚文书\",\"must\":true,\"link\":\"\",\"radio\":false,\"checked\":true,\"document\":\"0\"},{\"code\":\"3\",\"value\":\"移交其他部门\",\"must\":false,\"link\":\"\",\"radio\":false,\"checked\":false,\"document\":\"\"}]},{\"warntype\":\"03\",\"warnname\":\"假牌车辆\",\"method\":[{\"code\":\"5\",\"value\":\"扣留机动车\",\"must\":true,\"link\":\"2\",\"radio\":false,\"checked\":true,\"document\":\"3\"},{\"code\":\"2\",\"value\":\"现场开具处罚文书\",\"must\":true,\"link\":\"\",\"radio\":false,\"checked\":true,\"document\":\"0\"},{\"code\":\"3\",\"value\":\"移交其他部门\",\"must\":false,\"link\":\"\",\"radio\":false,\"checked\":false,\"document\":\"\"}]},{\"warntype\":\"04\",\"warnname\":\"逾期未年检车辆\",\"method\":[{\"code\":\"5\",\"value\":\"扣留机动车\",\"must\":false,\"link\":\"2\",\"radio\":true,\"checked\":false,\"document\":\"3\"},{\"code\":\"2\",\"value\":\"现场开具处罚文书\",\"must\":false,\"link\":\"\",\"radio\":true,\"checked\":false,\"document\":\"0\"},{\"code\":\"6\",\"value\":\"已处罚\",\"must\":false,\"link\":\"\",\"radio\":true,\"checked\":false,\"document\":\"0\"},{\"code\":\"8\",\"value\":\"教育告知后放行\",\"must\":false,\"link\":\"\",\"radio\":true,\"checked\":false,\"document\":\"\"}]},{\"warntype\":\"05\",\"warnname\":\"逾期未报废车辆\",\"method\":[{\"code\":\"5\",\"value\":\"扣留机动车\",\"must\":true,\"link\":\"2\",\"radio\":false,\"checked\":true,\"document\":\"3\"},{\"code\":\"2\",\"value\":\"现场开具处罚文书\",\"must\":true,\"link\":\"\",\"radio\":false,\"checked\":true,\"document\":\"0\"}]},{\"warntype\":\"06\",\"warnname\":\"历史违法未处理车辆\",\"method\":[{\"code\":\"2\",\"value\":\"现场开具处罚文书\",\"must\":false,\"link\":\"\",\"radio\":true,\"checked\":false,\"document\":\"0\"},{\"code\":\"4\",\"value\":\"当事人接受处理\",\"must\":false,\"link\":\"\",\"radio\":true,\"checked\":false,\"document\":\"\"},{\"code\":\"7\",\"value\":\"当事人拒绝处理\",\"must\":false,\"link\":\"\",\"radio\":true,\"checked\":false,\"document\":\"\"},{\"code\":\"8\",\"value\":\"教育告知后放行\",\"must\":false,\"link\":\"\",\"radio\":true,\"checked\":false,\"document\":\"\"}]},{\"warntype\":\"07\",\"warnname\":\"实时违法未处理车辆\",\"method\":[{\"code\":\"2\",\"value\":\"现场开具处罚文书\",\"must\":false,\"link\":\"\",\"radio\":true,\"checked\":false,\"document\":\"0\"},{\"code\":\"7\",\"value\":\"当事人拒绝处理\",\"must\":false,\"link\":\"\",\"radio\":true,\"checked\":false,\"document\":\"\"},{\"code\":\"8\",\"value\":\"教育告知后放行\",\"must\":false,\"link\":\"\",\"radio\":true,\"checked\":false,\"document\":\"\"}]},{\"warntype\":\"08\",\"warnname\":\"套牌嫌疑车辆\",\"method\":[{\"code\":\"5\",\"value\":\"扣留机动车\",\"must\":true,\"link\":\"2\",\"radio\":false,\"checked\":true,\"document\":\"3\"},{\"code\":\"2\",\"value\":\"现场开具处罚文书\",\"must\":true,\"link\":\"\",\"radio\":false,\"checked\":true,\"document\":\"0\"},{\"code\":\"3\",\"value\":\"移交其他部门\",\"must\":false,\"link\":\"\",\"radio\":false,\"checked\":false,\"document\":\"\"}]},{\"warntype\":\"09\",\"warnname\":\"未安装切断装置危化品车辆\",\"method\":[{\"code\":\"5\",\"value\":\"扣留机动车\",\"must\":false,\"link\":\"2\",\"radio\":true,\"checked\":false,\"document\":\"3\"},{\"code\":\"2\",\"value\":\"现场开具处罚文书\",\"must\":false,\"link\":\"\",\"radio\":true,\"checked\":false,\"document\":\"0\"},{\"code\":\"8\",\"value\":\"教育告知后放行\",\"must\":false,\"link\":\"\",\"radio\":true,\"checked\":false,\"document\":\"\"}]},{\"warntype\":\"21\",\"warnname\":\"刑事案件\",\"method\":[{\"code\":\"5\",\"value\":\"扣留机动车\",\"must\":true,\"link\":\"2\",\"radio\":false,\"checked\":true,\"document\":\"3\"},{\"code\":\"2\",\"value\":\"现场开具处罚文书\",\"must\":false,\"link\":\"\",\"radio\":false,\"checked\":true,\"document\":\"0\"},{\"code\":\"3\",\"value\":\"移交其他部门\",\"must\":false,\"link\":\"\",\"radio\":false,\"checked\":false,\"document\":\"\"}]},{\"warntype\":\"22\",\"warnname\":\"重大治安案件\",\"method\":[{\"code\":\"5\",\"value\":\"扣留机动车\",\"must\":true,\"link\":\"2\",\"radio\":false,\"checked\":true,\"document\":\"3\"},{\"code\":\"2\",\"value\":\"现场开具处罚文书\",\"must\":false,\"link\":\"\",\"radio\":false,\"checked\":true,\"document\":\"0\"},{\"code\":\"3\",\"value\":\"移交其他部门\",\"must\":false,\"link\":\"\",\"radio\":false,\"checked\":false,\"document\":\"\"}]},{\"warntype\":\"23\",\"warnname\":\"违法犯罪嫌疑交通工具\",\"method\":[{\"code\":\"5\",\"value\":\"扣留机动车\",\"must\":true,\"link\":\"2\",\"radio\":false,\"checked\":true,\"document\":\"3\"},{\"code\":\"2\",\"value\":\"现场开具处罚文书\",\"must\":false,\"link\":\"\",\"radio\":false,\"checked\":true,\"document\":\"0\"},{\"code\":\"3\",\"value\":\"移交其他部门\",\"must\":false,\"link\":\"\",\"radio\":false,\"checked\":false,\"document\":\"\"}]},{\"warntype\":\"24\",\"warnname\":\"被盗抢机动车辆\",\"method\":[{\"code\":\"5\",\"value\":\"扣留机动车\",\"must\":true,\"link\":\"2\",\"radio\":false,\"checked\":true,\"document\":\"3\"},{\"code\":\"2\",\"value\":\"现场开具处罚文书\",\"must\":false,\"link\":\"\",\"radio\":false,\"checked\":true,\"document\":\"0\"},{\"code\":\"3\",\"value\":\"移交其他部门\",\"must\":false,\"link\":\"\",\"radio\":false,\"checked\":false,\"document\":\"\"}]},{\"warntype\":\"25\",\"warnname\":\"治安常态管控\",\"method\":[{\"code\":\"5\",\"value\":\"扣留机动车\",\"must\":true,\"link\":\"2\",\"radio\":false,\"checked\":true,\"document\":\"3\"},{\"code\":\"2\",\"value\":\"现场开具处罚文书\",\"must\":false,\"link\":\"\",\"radio\":false,\"checked\":true,\"document\":\"0\"},{\"code\":\"3\",\"value\":\"移交其他部门\",\"must\":false,\"link\":\"\",\"radio\":false,\"checked\":false,\"document\":\"\"}]},{\"warntype\":\"31\",\"warnname\":\"无牌车辆\",\"method\":[{\"code\":\"5\",\"value\":\"扣留机动车\",\"must\":false,\"link\":\"2\",\"radio\":false,\"checked\":false,\"document\":\"3\"},{\"code\":\"2\",\"value\":\"现场开具处罚文书\",\"must\":false,\"link\":\"\",\"radio\":false,\"checked\":false,\"document\":\"0\"},{\"code\":\"3\",\"value\":\"移交其他部门\",\"must\":false,\"link\":\"\",\"radio\":false,\"checked\":false,\"document\":\"\"}]},{\"warntype\":\"32\",\"warnname\":\"特殊省份车辆\",\"method\":[{\"code\":\"5\",\"value\":\"扣留机动车\",\"must\":false,\"link\":\"2\",\"radio\":false,\"checked\":false,\"document\":\"3\"},{\"code\":\"2\",\"value\":\"现场开具处罚文书\",\"must\":false,\"link\":\"\",\"radio\":false,\"checked\":false,\"document\":\"0\"},{\"code\":\"3\",\"value\":\"移交其他部门\",\"must\":false,\"link\":\"\",\"radio\":false,\"checked\":false,\"document\":\"\"}]},{\"warntype\":\"33\",\"warnname\":\"凌晨2至5点继续上路行驶客运车辆\",\"method\":[{\"code\":\"5\",\"value\":\"扣留机动车\",\"must\":false,\"link\":\"2\",\"radio\":true,\"checked\":false,\"document\":\"3\"},{\"code\":\"2\",\"value\":\"现场开具处罚文书\",\"must\":false,\"link\":\"\",\"radio\":true,\"checked\":false,\"document\":\"0\"},{\"code\":\"8\",\"value\":\"教育告知后放行\",\"must\":false,\"link\":\"\",\"radio\":true,\"checked\":false,\"document\":\"\"}]},{\"warntype\":\"34\",\"warnname\":\"车辆所有人驾驶证异常\",\"method\":[{\"code\":\"5\",\"value\":\"扣留机动车\",\"must\":false,\"link\":\"2\",\"radio\":true,\"checked\":false,\"document\":\"3\"},{\"code\":\"2\",\"value\":\"现场开具处罚文书\",\"must\":false,\"link\":\"\",\"radio\":true,\"checked\":false,\"document\":\"0\"},{\"code\":\"8\",\"value\":\"教育告知后放行\",\"must\":false,\"link\":\"\",\"radio\":true,\"checked\":false,\"document\":\"\"}]},{\"warntype\":\"35\",\"warnname\":\"重点关注大客车\",\"method\":[{\"code\":\"5\",\"value\":\"扣留机动车\",\"must\":false,\"link\":\"2\",\"radio\":true,\"checked\":false,\"document\":\"3\"},{\"code\":\"2\",\"value\":\"现场开具处罚文书\",\"must\":false,\"link\":\"\",\"radio\":true,\"checked\":false,\"document\":\"0\"},{\"code\":\"8\",\"value\":\"教育告知后放行\",\"must\":false,\"link\":\"\",\"radio\":true,\"checked\":false,\"document\":\"\"}]},{\"warntype\":\"36\",\"warnname\":\"违法本省交通通行规定行使的客运车辆\",\"method\":[{\"code\":\"5\",\"value\":\"扣留机动车\",\"must\":false,\"link\":\"2\",\"radio\":true,\"checked\":false,\"document\":\"3\"},{\"code\":\"2\",\"value\":\"现场开具处罚文书\",\"must\":false,\"link\":\"\",\"radio\":true,\"checked\":false,\"document\":\"0\"},{\"code\":\"8\",\"value\":\"教育告知后放行\",\"must\":false,\"link\":\"\",\"radio\":true,\"checked\":false,\"document\":\"\"}]},{\"warntype\":\"37\",\"warnname\":\"未按规定办理转移登记的二手车辆\",\"method\":[{\"code\":\"5\",\"value\":\"扣留机动车\",\"must\":false,\"link\":\"2\",\"radio\":true,\"checked\":false,\"document\":\"3\"},{\"code\":\"2\",\"value\":\"现场开具处罚文书\",\"must\":false,\"link\":\"\",\"radio\":true,\"checked\":false,\"document\":\"0\"},{\"code\":\"8\",\"value\":\"教育告知后放行\",\"must\":false,\"link\":\"\",\"radio\":true,\"checked\":false,\"document\":\"\"}]},{\"warntype\":\"38\",\"warnname\":\"自学车辆\",\"method\":[{\"code\":\"5\",\"value\":\"扣留机动车\",\"must\":false,\"link\":\"2\",\"radio\":true,\"checked\":false,\"document\":\"3\"},{\"code\":\"2\",\"value\":\"现场开具处罚文书\",\"must\":false,\"link\":\"\",\"radio\":true,\"checked\":false,\"document\":\"0\"},{\"code\":\"8\",\"value\":\"教育告知后放行\",\"must\":false,\"link\":\"\",\"radio\":true,\"checked\":false,\"document\":\"\"}]},{\"warntype\":\"43\",\"warnname\":\"黄标车管控\",\"method\":[{\"code\":\"5\",\"value\":\"扣留机动车\",\"must\":false,\"link\":\"2\",\"radio\":true,\"checked\":false,\"document\":\"3\"},{\"code\":\"2\",\"value\":\"现场开具处罚文书\",\"must\":false,\"link\":\"\",\"radio\":true,\"checked\":false,\"document\":\"0\"},{\"code\":\"8\",\"value\":\"教育告知后放行\",\"must\":false,\"link\":\"\",\"radio\":true,\"checked\":false,\"document\":\"\"}]},{\"warntype\":\"99\",\"warnname\":\"其他\",\"method\":[{\"code\":\"5\",\"value\":\"扣留机动车\",\"must\":false,\"link\":\"2\",\"radio\":false,\"checked\":false,\"document\":\"3\"},{\"code\":\"2\",\"value\":\"现场开具处罚文书\",\"must\":false,\"link\":\"\",\"radio\":false,\"checked\":false,\"document\":\"0\"},{\"code\":\"3\",\"value\":\"移交其他部门\",\"must\":false,\"link\":\"\",\"radio\":false,\"checked\":false,\"document\":\"\"},{\"code\":\"8\",\"value\":\"教育告知后放行\",\"must\":false,\"link\":\"\",\"radio\":true,\"checked\":false,\"document\":\"\"}]}]}";

	private static WarnTable instance;
	private Map<String, String> dataMap = new HashMap<String, String>();
	private WarnMethods warnMethods;
	private List<Method> method = new ArrayList<Method>();

	public WarnTable() {
		instance = this;

		//
		LoadMapData();

		//解析预警对应的处理方法
		Gson gson = new Gson();
		warnMethods = gson.fromJson(WARNMETHOD, WarnMethods.class);
	}

	public static final boolean isInstanciated() {
		return instance != null;
	}

	public static final WarnTable instance() {
		if (instance != null)
			return instance;
		else
			return new WarnTable();
	}

	public String[] GetNoCaptureReason(){
		return NOFIND;
	}

	public String[] GetWarnLinkReason(String type, String WarnType){
		int warntype = Integer.valueOf(WarnType);
		if (type.equals("4"))
			return GetWarnLinkReasonEx(warntype);
		else
			return GetWarnLinkReason(warntype);
	}

	//获取核心版预警类型对应的非嫌疑车辆原因
	private String[] GetWarnLinkReason(int warntype) {
		String reasons = "号牌识别错误";

		if (warntype != 31 && warntype != 32 && warntype != 35)
			reasons += " 布控信息有误";			//1
		if (warntype == 7)
			reasons += " 违法记录错误";			//3
		if (warntype == 2 || warntype == 8)
			reasons += " 原车";					//4
		if (warntype == 35 || warntype == 38)
			reasons += " 车辆基本信息未更新";	//5
		if (warntype == 4)
			reasons += " 车辆已年检";			//7
		if (warntype == 6 || warntype == 7)
			reasons += " 违法已处理";			//8
		if (warntype == 33 || warntype == 36)
			reasons += " 接驳车辆";				//11
		if (warntype == 33 || warntype == 36)
			reasons += " 卡口未校时";			//12
		if (warntype == 34)
			reasons += " 驾驶人驾驶证正常";		//13
		if (warntype == 34)
			reasons += " 非车主本人驾驶";		//14
		if (warntype == 9 || warntype == 31 || warntype == 32 || warntype == 33 || warntype == 35  || warntype == 36 || warntype == 37 || warntype == 38 || warntype == 43 || warntype == 99)
			reasons += " 检查无异常";			//15
		return reasons.split(" ");
	}

	//获取二次识别预警类型对应的非嫌疑车辆原因
	private String[] GetWarnLinkReasonEx(int warntype) {
		String reasons = "号牌识别错误";

		if (warntype != 3 && warntype != 4 && warntype != 5 && warntype != 8 && warntype != 31 && warntype != 32 && warntype != 35)
			reasons += " 布控信息有误";			//1
		if (warntype == 7)
			reasons += " 违法记录错误";			//3
		if (warntype == 2)
			reasons += " 原车";					//4
		if (warntype == 3 || warntype == 5 ||  warntype == 35 || warntype == 38)
			reasons += " 车辆基本信息未更新";	//5
		if (warntype == 3 || warntype == 8)
			reasons += " 内部车辆";				//6
		if (warntype == 4)
			reasons += " 车辆已年检";			//7
		if (warntype == 6 || warntype == 7)
			reasons += " 违法已处理";			//8
		if (warntype == 8)
			reasons += " 车辆品牌识别错误";		//9
		if (warntype == 8)
			reasons += " 识别与登记品牌未对应";	//10
		if (warntype == 33 || warntype == 36)
			reasons += " 接驳车辆";				//11
		if (warntype == 33 || warntype == 36)
			reasons += " 卡口未校时";			//12
		if (warntype == 34)
			reasons += " 驾驶人驾驶证正常";		//13
		if (warntype == 34)
			reasons += " 非车主本人驾驶";		//14
		if (warntype == 9 || warntype == 31 || warntype == 32 || warntype == 33 || warntype == 35  || warntype == 36 || warntype == 37 || warntype == 38 || warntype == 43 || warntype == 99)
			reasons += " 检查无异常";			//15
		return reasons.split(" ");
	}

	public List<Method> GetWarnLinkMethods (String WarnType, String bkzlx) {
		method.clear();

		for (int i = 0, len = warnMethods.total; i < len; i++) {
			MethodItem methodItem = warnMethods.data.get(i);
			if (methodItem.warntype.equals(WarnType)) {
				if (WarnType.equals("34")
						&& bkzlx != null && (bkzlx.equals("E") || bkzlx.equals("F") || bkzlx.equals("G") || bkzlx.equals("H"))){

					//取消 "教育告知后放行"
					CopyValue(methodItem.method, methodItem.method.size() - 1);
				} else {
					CopyValue(methodItem.method, methodItem.method.size());
				}
			}
		}

		if (method.size() == 0)
			CopyValue(warnMethods.data.get(0).method, warnMethods.data.get(0).method.size());
		return method;
	}

	private void CopyValue(List<Method> methods, int length) {
		for (int j = 0; j < length; j++) {
			method.add((Method) methods.get(j).clone());
		}
	}


	private void LoadMapData() {
		dataMap.clear();
		//装载费嫌疑车辆key，code
		for (int i = 0, len = NORMALREASON.length; i < len; i++){
			if (i < 9)
				dataMap.put(NORMALREASON[i], "0" + String.valueOf(i+1));
			else
				dataMap.put(NORMALREASON[i], String.valueOf(i+1));
		}

		//装载处置方法key，code
		for (int i = 0, len = DOMETHOD.length; i < len; i++){
			dataMap.put(DOMETHOD[i], String.valueOf(i + 2));
		}

		//装载未拦截到原因key，code
//		for (int i = 0, len = NOFIND.length; i < len; i++){
//			dataMap.put(NOFIND[i], String.valueOf(i));
//		}
		dataMap.put(NOFIND[0], "01");
		dataMap.put(NOFIND[1], "02");
		dataMap.put(NOFIND[2], "03");
		dataMap.put(NOFIND[3], "09");

		dataMap.put("已拦截到", "1");
		dataMap.put("未拦截到", "0");
		dataMap.put("是", "1");
		dataMap.put("否", "0");
		dataMap.put("简易程序处罚决定书", "1");
		dataMap.put("强制措施凭证", "3");
		dataMap.put("违法处理通知书", "6");
	}

	public String GetCode(String key) {
		return dataMap.get(key);
	}

	public static class WarnMethods {
		public int total;
		public List<MethodItem> data;
	}

	public static class MethodItem {
		public String warntype;
		public String warnname;
		public List<Method> method;
	}

	//需要值拷贝，否则会出现相同预警类型出现历史值问题
	public static class Method implements Cloneable{
		public String code = "";			//处置方式编号
		public String value = "";			//处置方式名称
		public boolean must = false;		//是否为必选项（true、false）
		public String link = "";			//粘连选项编号
		public boolean radio = false;		//是否单选（true、false）
		public boolean checked = false;		//是否已选择（true、false）
		public String document = "";		//处置方式对应的文书类型（暂时未使用）
		public boolean linked = false;		//是否粘连选中

		@Override
		public Object clone(){
			Method sc = null;
			try
			{
				sc = (Method) super.clone();
			} catch (CloneNotSupportedException e){
				e.printStackTrace();
			}
			return sc;
		}
	}
}
