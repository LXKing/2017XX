/**
 * 
 */
package com.xiangxun.bean;

import java.io.IOException;
import java.net.URLEncoder;

import static com.xiangxun.util.ImageTools.Base64Image;

/**
 * @author 
 * 
 */
public class ParkVio {
	public int isupfile = 0;

	public String xh = "";		//序号
	public String cjjg = "";	//采集机关
	public String clfl = "";	//车辆分类
	public String hpzl = "";	//号牌种类
	public String hphm = "";	//号牌号码
	public String jdcsyr = "";	//机动车所有人
	public String syxz = "";	//使用性质
	public String fdjh = "";	//发动机号
	public String clsbdh = "";	//车辆识别代号
	public String csys = "";	//车身颜色
	public String clpp = "";	//车辆品牌
	public String jtfs = "";	//交通方式
	public String fzjg = "";	//发证机关
	public String zsxzqh = "";	//住所行政区划
	public String zsxxdz = "";	//住所详细地址
	public String dh = "";		//电话
	public String lxfs = "";	//联系方式
	public String tzsh = "";	//通知书号
	public String tzrq = "";	//通知日期
	public String cjfs = "";	//采集方式
	public String wfsj = "";	//违法时间
	public String xzqh = "";	//行政区划
	public String wfdd = "";	//违法地点
	public String lddm = "";	//路段号码
	public String ddms = "";	//地点米数
	public String wfsj1 = "";	//违法时间1
	public String wfdd1 = "";	//违法地点1
	public String lddm1 = "";	//路段号码1
	public String ddms1 = "";	//地点米数1
	public String wfdz = "";	//违法地址
	public String wfxw = "";	//违法行为
	public String scz = "";		//实测值
	public String bzz = "";		//标准值
	public String zqmj = "";	//执勤民警
	public String spdz = "";	//视频地址
	public String sbbh = "";	//设备编号
	public String zpstr1 = "";	//照片1
	public String zpstr2 = "";	//照片2
	public String zpstr3 = "";	//照片3


	public String toXML() {
		String image1 = "";
		String image2 = "";
		String image3 = "";

		String parkXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
				"<root>" +
				"<viosurveil>" +
				"<xh>" + xh + "</xh>" +
				"<cjjg>" + cjjg + "</cjjg>" +
				"<clfl>" + clfl + "</clfl>" +
				"<hpzl>" + hpzl + "</hpzl>" +
				"<hphm>" + URLEncoder.encode(URLEncoder.encode(hphm)) + "</hphm>" +
				"<jdcsyr>" + URLEncoder.encode(URLEncoder.encode(jdcsyr)) + "</jdcsyr>" +
				"<syxz>" + syxz + "</syxz>" +
				"<fdjh>" + fdjh + "</fdjh>" +
				"<clsbdh>" + clsbdh + "</clsbdh>" +
				"<csys>" + URLEncoder.encode(URLEncoder.encode(csys)) + "</csys>" +
				"<clpp>" + URLEncoder.encode(URLEncoder.encode(clpp)) + "</clpp>" +
				"<jtfs>" + jtfs + "</jtfs>" +
				"<fzjg>" + URLEncoder.encode(URLEncoder.encode(fzjg)) + "</fzjg>" +
				"<zsxzqh>" + zsxzqh + "</zsxzqh>" +
				"<zsxxdz>" + URLEncoder.encode(URLEncoder.encode(zsxxdz)) + "</zsxxdz>" +
				"<dh>" + dh + "</dh>" +
				"<lxfs>" + lxfs + "</lxfs>" +
				"<tzsh>" + tzsh + "</tzsh>" +
				"<tzrq>" + tzrq + "</tzrq>" +
				"<cjfs>" + "6" + "</cjfs>" +
				"<wfsj>" + wfsj + "</wfsj>" +
				"<xzqh>" + xzqh + "</xzqh>" +
				"<wfdd>" + wfdd + "</wfdd>" +
				"<lddm>" + lddm + "</lddm>" +
				"<ddms>" + ddms + "</ddms>" +
				"<wfsj1>" + wfsj1 + "</wfsj1>" +
				"<wfdd1>" + wfdd1 + "</wfdd1>" +
				"<lddm1>" + lddm1 + "</lddm1>" +
				"<ddms1>" + ddms1 + "</ddms1>" +
				"<wfdz>" + URLEncoder.encode(URLEncoder.encode(wfdz)) + "</wfdz>" +
				"<wfxw>" + wfxw + "</wfxw>" +
				"<scz>" + scz + "</scz>" +
				"<bzz>" + bzz + "</bzz>" +
				"<zqmj>" + zqmj + "</zqmj>" +
				"<spdz>" + spdz + "</spdz>" +
				"<sbbh>" + sbbh + "</sbbh>";

		if (zpstr1.equals(""))
			parkXML = parkXML + "<zpstr1>"+ "" + "</zpstr1>";
		else {
			try {
				image1 = Base64Image(zpstr1);
				parkXML = parkXML + "<zpstr1>"+ URLEncoder.encode(URLEncoder.encode(image1)) + "</zpstr1>";
			} catch (IOException e) {
				parkXML = parkXML + "<zpstr1>"+ "" + "</zpstr1>";
			}
		}

		if (zpstr2.equals(""))
			parkXML = parkXML + "<zpstr2>"+ "" + "</zpstr2>";
		else {
			try {
				image2 = Base64Image(zpstr2);
				parkXML = parkXML + "<zpstr2>"+ URLEncoder.encode(URLEncoder.encode(image2)) + "</zpstr2>";
			} catch (IOException e) {
				parkXML = parkXML + "<zpstr2>"+ "" + "</zpstr2>";
			}
		}

		if (zpstr3.equals(""))
			parkXML = parkXML + "<zpstr3>"+ "" + "</zpstr3>";
		else {
			try {
				image3 = Base64Image(zpstr3);
				parkXML = parkXML + "<zpstr3>"+ URLEncoder.encode(URLEncoder.encode(image3)) + "</zpstr3>";
			} catch (IOException e) {
				parkXML = parkXML + "<zpstr3>"+ "" + "</zpstr3>";
			}
		}

		parkXML = parkXML +	"</viosurveil>" +	"</root>";
		return  parkXML;
	}
}
