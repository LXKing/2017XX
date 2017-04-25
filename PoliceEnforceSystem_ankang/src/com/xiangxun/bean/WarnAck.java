/**
 * 
 */
package com.xiangxun.bean;

import com.xiangxun.app.XiangXunApplication;
import com.xiangxun.cfg.SystemCfg;

import java.net.URLEncoder;

/**
 * @author 
 * 
 */
public class WarnAck {
	public int isupfile = 0;
	public String yjxh = "";	//预警序号
	public String ywlx = "";	//业务类型 		1-签收；	2-反馈；3-签收反馈
	public String qsjg = "";	//签收结果 		1-有效；2-无效
	public String sfcjlj = "";	//是否出警拦截	0否1是
	public String ljclqk = "";	//拦截车辆情况 	0未拦截到1已拦截到
	public String chjg = "";	//是否嫌疑车辆	0 否 1是
	public String cljg = "";	//处理结果
	public String wsbh = "";	//法律文书编号
	public String jyw = "";		//文书校验位
	public String wslb = "";	//文书类别		1 简易程序处罚决定书	3 强制措施凭证	6 违法处理通知书
	public String wchyy = "";	//非嫌疑车辆原因
	public String czqkms = "";	//处置情况描述		中文
	public String czdw = "";	//处置单位
	public String czr = "";		//处置民警			中文
	public String czsj = "";	//处置时间
	public String yjbm = "";	//移交部门			中文
	public String lxr = "";		//移交部门联系人	中文
	public String lxdh = "";	//移交部门电话
	public String wljdyy = ""; //未拦截到原因

	@Override
	public String toString() {
		return 	"<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
				"<root>" +
				"<feedback>" +
				"<yjxh>" + yjxh + "</yjxh>" +
				"<ywlx>" + ywlx + "</ywlx>" +
				"<qsjg>" + qsjg + "</qsjg>" +
				"<sfcjlj>" + sfcjlj + "</sfcjlj>" +
				"<ljclqk>" + ljclqk + "</ljclqk>" +
				"<chjg>" + chjg + "</chjg>" +
				"<cljg>" + cljg + "</cljg>" +
				"<wsbh>" + wsbh + "</wsbh>" +
				"<jyw>" + jyw + "</jyw>" +
				"<wslb>" + wslb + "</wslb>" +
				"<wchyy>" + wchyy + "</wchyy>" +
				"<czqkms>" + URLEncoder.encode(URLEncoder.encode(czqkms)) + "</czqkms>" +
				"<czdw>" + SystemCfg.getDepartmentCode(XiangXunApplication.getInstance()) + "</czdw>" +
				"<czr>"+ URLEncoder.encode(URLEncoder.encode(czr)) + "</czr>" +
				"<czsj>" +  czsj + "</czsj>" +
				"<yjbm>" +   URLEncoder.encode(URLEncoder.encode(yjbm)) + "</yjbm>" +
				"<lxr>" +   URLEncoder.encode(URLEncoder.encode(lxr)) + "</lxr>" +
				"<lxdh>" +  lxdh + "</lxdh>" +
				"<wljdyy>" +  wljdyy + "</wljdyy>" +
				"</feedback>" +
				"</root>";
	}
}
