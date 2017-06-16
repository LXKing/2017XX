/**
 * 
 */
package com.xiangxun.bean;

import java.net.URLEncoder;

/**
 * @author 
 * 
 */
public class EnforceVio {
	public int isupfile = 0;

	public String yjxh = "";	//预警序号

	public String pzbh = "";		//凭证编号
	public String wslb = "";		//文书类别
	public String qzcslx = "";		//强制措施类型
	public String klwpcfd = "";		//扣留物品存放地
	public String sjxm = "";		//收缴项目
	public String sjwpmc = "";		//收缴物品名称
	public String sjwpcfd = "";		//收缴物品存放地
	public String ryfl = "";		//人员分类
	public String jszh = "";		//驾驶证号
	public String dabh = "";		//档案编号
	public String fzjg = "";		//发证机关
	public String zjcx = "";		//准驾车型
	public String dsr = "";			//当事人
	public String zsxzqh = "";		//住所行政区划
	public String zsxxdz = "";		//住所详细地址
	public String dh = "";			//电话
	public String lxfs = "";		//联系方式
	public String clfl = "";		//车辆分类
	public String hpzl = "";		//号牌种类
	public String hphm = "";		//号牌号码
	public String jdcsyr = "";		//机动车所有人
	public String fdjh = "";		//发动机号
	public String clsbdh = "";		//车辆识别代号
	public String syxz = "";		//使用性质
	public String jtfs = "";		//交通方式
	public String wfsj = "";		//违法时间
	public String xzqh = "";		//行政区划
	public String wfdd = "";		//违法地点
	public String lddm = "";		//路段号码
	public String ddms = "";		//地点米数
	public String wfdz = "";		//违法地址
	public String wfxw1 = "";		//违法行为
	public String scz1 = "";		//实测值
	public String bzz1 = "";		//标准值
	public String wfxw2 = "";		//违法行为
	public String scz2 = "";		//实测值
	public String bzz2 = "";		//标准值
	public String wfxw3 = "";		//违法行为
	public String scz3 = "";		//实测值
	public String bzz3 = "";		//标准值
	public String wfxw4 = "";		//违法行为
	public String scz4 = "";		//实测值
	public String bzz4 = "";		//标准值
	public String wfxw5 = "";		//违法行为
	public String scz5 = "";		//实测值
	public String bzz5 = "";		//标准值
	public String zqmj = "";		//执勤民警
	public String fxjg = "";		//发现机关
	public String jsjqbj = "";		//拒收拒签标记
	public String sgdj = "";		//事故等级
	public String mjyj = "";		//民警意见
	public String jd = "";			//经度
	public String wd = "";			//纬度
	public String Zxbh = "";		//证芯编号后六位
	public String Sfzdry = "";		//是否指导人员

	public String toXML() {
		return 	"<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
				"<root>" +
				"<vioforce>" +
				"<pzbh>" + pzbh + "</pzbh>" +
				"<wslb>" + wslb + "</wslb>" +
				"<qzcslx>" + qzcslx + "</qzcslx>" +
				"<klwpcfd>" + URLEncoder.encode(URLEncoder.encode(klwpcfd)) + "</klwpcfd>" +
				"<sjxm>" + sjxm + "</sjxm>" +
				"<sjwpmc>" + URLEncoder.encode(URLEncoder.encode(sjwpmc)) + "</sjwpmc>" +
				"<sjwpcfd>" + URLEncoder.encode(URLEncoder.encode(sjwpcfd)) + "</sjwpcfd>" +
				"<ryfl>" + ryfl + "</ryfl>" +
				"<jszh>" + jszh + "</jszh>" +
				"<dabh>" + dabh + "</dabh>" +
				"<fzjg>" + URLEncoder.encode(URLEncoder.encode(fzjg)) + "</fzjg>" +
				"<zjcx>" + zjcx + "</zjcx>" +
				"<dsr>" + URLEncoder.encode(URLEncoder.encode(dsr))  + "</dsr>" +
				"<zsxzqh>" + zsxzqh + "</zsxzqh>" +
				"<zsxxdz>" + zsxxdz + "</zsxxdz>" +
				"<dh>" + dh  + "</dh>" +
				"<lxfs>" + lxfs + "</lxfs>" +
				"<clfl>" + clfl + "</clfl>" +
				"<hpzl>" + hpzl + "</hpzl>" +
				"<hphm>" + URLEncoder.encode(URLEncoder.encode(hphm)) + "</hphm>" +
				"<jdcsyr>" + URLEncoder.encode(URLEncoder.encode(jdcsyr)) + "</jdcsyr>" +
				"<fdjh>" + fdjh + "</fdjh>" +
				"<clsbdh>" + clsbdh + "</clsbdh>" +
				"<syxz>" + syxz + "</syxz>" +
				"<jtfs>" + jtfs + "</jtfs>" +
				"<wfsj>" + wfsj + "</wfsj>" +
				"<xzqh>" + xzqh + "</xzqh>" +
				"<wfdd>" + wfdd + "</wfdd>" +
				"<lddm>" + lddm + "</lddm>" +
				"<ddms>" + ddms + "</ddms>" +
				"<wfdz>" + URLEncoder.encode(URLEncoder.encode(wfdz)) + "</wfdz>" +
				"<wfxw1>" + wfxw1 + "</wfxw1>" +
				"<scz1>" + scz1 + "</scz1>" +
				"<bzz1>" + bzz1 + "</bzz1>" +
				"<wfxw2>" + wfxw2 + "</wfxw2>" +
				"<scz2>" + scz2 + "</scz2>" +
				"<bzz2>" + bzz2 + "</bzz2>" +
				"<wfxw3>" + wfxw3 + "</wfxw3>" +
				"<scz3>" + scz3 + "</scz3>" +
				"<bzz3>" + bzz3 + "</bzz3>" +
				"<wfxw4>" + wfxw4 + "</wfxw4>" +
				"<scz4>" + scz4 + "</scz4>" +
				"<bzz4>" + bzz4 + "</bzz4>" +
				"<wfxw5>" + wfxw5 + "</wfxw5>" +
				"<scz5>" + scz5 + "</scz5>" +
				"<bzz5>" + bzz5 + "</bzz5>" +
				"<zqmj>" + zqmj + "</zqmj>" +
				"<fxjg>" + fxjg + "</fxjg>" +
				"<jsjqbj>" + jsjqbj + "</jsjqbj>" +
				"<sgdj>" + sgdj + "</sgdj>" +
				"<mjyj>" + mjyj + "</mjyj>" +
				"<jd>" + jd  + "</jd>" +
				"<wd>" + wd  + "</wd>" +
				"<Zxbh>" + Zxbh + "</Zxbh>" +
				"<Sfzdry>" + Sfzdry + "</Sfzdry>" +
				"</vioforce>" +
				"</root>";
	}
}
