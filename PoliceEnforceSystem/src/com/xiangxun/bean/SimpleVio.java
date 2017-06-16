/**
 * 
 */
package com.xiangxun.bean;

import java.net.URLEncoder;

/**
 * @author 
 * 
 */
public class SimpleVio {
	public int isupfile = 0;

	public String yjxh = "";	//预警序号

	public String jdsbh = "";	//决定书编号
	public String ryfl = "";	//人员分类
	public String jszh = "";	//驾驶证号
	public String dabh = "";	//档案编号
	public String fzjg = "";	//发证机关
	public String zjcx = "";	//准驾车型
	public String dsr = "";		//当事人
	public String zsxzqh = "";	//住所行政区划
	public String zsxxdz = "";	//住所详细地址
	public String dh = "";		//电话
	public String lxfs = "";	//联系方式
	public String clfl = "";	//车辆分类
	public String hpzl = "";	//号牌种类
	public String hphm = "";	//号牌号码
	public String jdcsyr = "";	//机动车所有人
	public String syxz = "";	//使用性质
	public String jtfs = "";	//交通方式
	public String wfsj = "";	//违法时间
	public String xzqh = "";	//行政区划
	public String wfdd = "";	//违法地点
	public String lddm = "";	//路段号码
	public String ddms = "";	//地点米数
	public String wfdz = "";	//违法地址
	public String wfxw = "";	//违法行为
	public String scz = "";		//实测值
	public String bzz = "";		//标准值
	public String cfzl = "";	//处罚种类
	public String fkje = "";	//罚款金额
	public String zqmj = "";	//执勤民警
	public String jkfs = "";	//交款方式
	public String jkbj = "";	//交款标记
	public String jkrq = "";	//交款日期
	public String fxjg = "";	//发现机关
	public String clsj = "";	//处理时间
	public String jsjqbj = "";	//拒收拒签标记
	public String sgdj = "";	//事故等级
	public String jd = "";		//经度
	public String wd = "";		//纬度
	public String Zxbh = "";	//证芯编号后六位
	public String Sfzdry = "";	//是否指导人员


	public String toXML() {

		return 	"<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
				"<root>" +
				"<violation>" +
				"<jdsbh>" + jdsbh + "</jdsbh>" +
				"<ryfl>" + ryfl + "</ryfl>" +
				"<jszh>" + jszh + "</jszh>" +
				"<dabh>" + dabh + "</dabh>" +
				"<fzjg>" + URLEncoder.encode(URLEncoder.encode(fzjg)) + "</fzjg>" +
				"<zjcx>" + zjcx + "</zjcx>" +
				"<dsr>" + URLEncoder.encode(URLEncoder.encode(dsr)) + "</dsr>" +
				"<zsxzqh>" + zsxzqh + "</zsxzqh>" +
				"<zsxxdz>" + zsxxdz + "</zsxxdz>" +
				"<dh>" + dh + "</dh>" +
				"<lxfs>" + lxfs + "</lxfs>" +
				"<clfl>" + clfl + "</clfl>" +
				"<hpzl>" + hpzl + "</hpzl>" +
				"<hphm>" + URLEncoder.encode(URLEncoder.encode(hphm)) + "</hphm>" +
				"<jdcsyr>" + URLEncoder.encode(URLEncoder.encode(jdcsyr)) + "</jdcsyr>" +
				"<syxz>" + syxz + "</syxz>" +
				"<jtfs>" + jtfs + "</jtfs>" +
				"<wfsj>" + wfsj + "</wfsj>" +
				"<xzqh>" + xzqh + "</xzqh>" +
				"<wfdd>" + wfdd + "</wfdd>" +
				"<lddm>" + lddm + "</lddm>" +
				"<ddms>" + ddms + "</ddms>" +
				"<wfdz>" + URLEncoder.encode(URLEncoder.encode(wfdz)) + "</wfdz>" +
				"<wfxw>" + wfxw + "</wfxw>" +
				"<scz>" + scz + "</scz>" +
				"<bzz>" + bzz + "</bzz>" +
				"<cfzl>" + cfzl + "</cfzl>" +
				"<fkje>" + fkje + "</fkje>" +
				"<zqmj>" + zqmj + "</zqmj>" +
				"<jkfs>" + jkfs + "</jkfs>" +
				"<jkbj>" + jkbj + "</jkbj>" +
				"<jkrq>" + jkrq + "</jkrq>" +
				"<fxjg>" + fxjg + "</fxjg>" +
				"<clsj>" + clsj + "</clsj>" +
				"<jsjqbj>" + jsjqbj + "</jsjqbj>" +
				"<sgdj>" + sgdj + "</sgdj>" +
				"<jd>" + jd + "</jd>" +
				"<wd>" + wd + "</wd>" +
				"<Zxbh>" + Zxbh + "</Zxbh>" +
				"<Sfzdry>" + Sfzdry + "</Sfzdry>" +
				"</violation>" +
				"</root>";
	}
}
