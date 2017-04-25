/**
 * 
 */
package com.xiangxun.bean;

import com.xiangxun.app.XiangXunApplication;
import com.xiangxun.cfg.SystemCfg;

import java.io.IOException;
import java.net.URLEncoder;

import static com.xiangxun.util.ImageTools.Base64Image;

/**
 * @author 
 * 
 */
public class WarnPic {
	public int isupfile = 0;
	public String yjxh = "";	//预警序号
	public String tp1 = "";		//图片1
	public String tp2 = "";		//图片2
	public String tp3 = "";		//图片3
	public String scdw = "";	//上传单位			中文
	public String scr = "";		//上传人			中文

	@Override
	public String toString() {
		String image1 = "";
		String image2 = "";
		String image3 = "";

		String warnPic = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
				"<root>" +
				"<feedbackpic>" +
				"<yjxh>" + yjxh + "</yjxh>" +
				"<scdw>" + SystemCfg.getDepartmentCode(XiangXunApplication.getInstance()) + "</scdw>" +
				"<scr>" + URLEncoder.encode(URLEncoder.encode(scr)) + "</scr>";

		if (tp1.equals(""))
			warnPic = warnPic + "<tp1>"+ "" + "</tp1>";
		else {
			try {
				image1 = Base64Image(tp1);
				warnPic = warnPic + "<tp1>"+ URLEncoder.encode(URLEncoder.encode(image1)) + "</tp1>";
			} catch (IOException e) {
				warnPic = warnPic + "<tp1>"+ "" + "</tp1>";
			}
		}

		if (tp2.equals(""))
			warnPic = warnPic + "<tp2>"+ "" + "</tp2>";
		else {
			try {
				image2 = Base64Image(tp2);
				warnPic = warnPic + "<tp2>"+ URLEncoder.encode(URLEncoder.encode(image2)) + "</tp2>";
			} catch (IOException e) {
				warnPic = warnPic + "<tp2>"+ "" + "</tp2>";
			}
		}

		if (tp3.equals(""))
			warnPic = warnPic + "<tp3>"+ "" + "</tp3>";
		else {
			try {
				image3 = Base64Image(tp3);
				warnPic = warnPic + "<tp3>"+ URLEncoder.encode(URLEncoder.encode(image3)) + "</tp3>";
			} catch (IOException e) {
				warnPic = warnPic + "<tp3>"+ "" + "</tp3>";
			}
		}

		warnPic = warnPic +	"</feedbackpic>" +	"</root>";

		return warnPic;
	}
}
