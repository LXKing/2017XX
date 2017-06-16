package com.xiangxun.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.xiangxun.bean.BlackList;
import com.xiangxun.bean.GPSInfo;
import com.xiangxun.bean.PersionInfoX;
import com.xiangxun.bean.ResultData;
import com.xiangxun.bean.RoadInfo;
import com.xiangxun.bean.VehicleInfoX;
import com.xiangxun.bean.VipCarInfo;
import com.xiangxun.db.DBManager;
import com.xiangxun.widget.MsgDialog;
import com.xiangxun.widget.MsgToast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * @package: com.huatek.api.utils
 * @ClassName: Utils
 * @Description: 工具集合
 * @author: aaron_han
 * @data: 2015-07-26 下午6:22:13
 */
public class Utils {
	/*
	 * '100x75' ,'75x56' ,'90x67' ,'174x130' ,'135x101' ,'210x157' ,'120x90'
	 * ,'368x276' ,'68x51' ,'280x210' ,'45x45' ,'325x195' ,'800x2000'
	 */
	// 请求图片设置尺寸
	public static int[] sizes = { 174, 368 };
	public static String[] sizess = { "240x180", "800x480", "1280x720" };

	public static int getSize(Context c, int width) {
		// int size = Tools.dip2px(c, width);
		for (int i = 0; i < sizes.length; i++) {
			if (width < sizes[i]) {
				return i;
			}
		}
		return sizes.length - 1;
	}

	public static String trimImageUrl(String imgUrl, int i) {
		if (TextUtils.isEmpty(imgUrl) || !imgUrl.contains("http://")) {
			return "";
		}
		int index = imgUrl.lastIndexOf(".");
		String subEnd = imgUrl.substring(index, imgUrl.length());
		String subStart = imgUrl.substring(0, index);
		String str = subStart + "_" + sizess[i] + subEnd;
		return str;
	}

	public static void tvTextDeleteLineStyle(TextView tv) {
		tv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);// 并加清晰
		tv.getPaint().setAntiAlias(true);// 抗锯齿
	}

	/**
	 * 
	 * @param pic
	 *            原字符串
	 * @param str1
	 *            插入的字符串
	 * @return
	 */
	public static String getInsert(String pic, String str1) {
		StringBuffer s1 = new StringBuffer(pic); // 原字符串
		String hh = s1.substring(s1.lastIndexOf("."));
		System.out.println("ppp==" + hh);
		Pattern p = Pattern.compile(hh); // 插入位置
		Matcher m = p.matcher(s1.toString());

		if (m.find()) {
			s1.insert((m.start() + 0), str1); // 插入字符串

		}
		return s1.toString();
	}

	// 将list等分
	public static <T> List<List<T>> splitList(List<T> list, int pageSize) {
		int listSize = list.size(); // list的大小
		int page = (listSize + (pageSize - 1)) / pageSize; // 页数
		List<List<T>> listArray = new ArrayList<List<T>>(); // 创建list数组
		for (int i = 0; i < page; i++) { // 按照数组大小遍历
			List<T> subList = new ArrayList<T>(); // 数组每一位放入一个分割后的list
			for (int j = 0; j < listSize; j++) { // 遍历待分割的list
				int pageIndex = ((j + 1) + (pageSize - 1)) / pageSize; // 当前记录的页码(第几页)
				if (pageIndex == (i + 1)) { // 当前记录的页码等于要放入的页码时
					subList.add(list.get(j)); // 放入list中的元素到分割后的list(subList)
				}

				if ((j + 1) == ((j + 1) * pageSize)) { // 当放满一页时退出当前循环
					break;
				}
			}
			listArray.add(subList); // 将分割后的list放入对应的数组的位中
		}
		return listArray;
	}

	public static boolean copyApkFromAssets(Context context, String fileName, String path) {
		boolean copyIsFinish = false;
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			is = context.getAssets().open(fileName);
			File file = new File(path);
			file.createNewFile();
			fos = new FileOutputStream(file);
			byte[] temp = new byte[1024];
			int i = 0;
			while ((i = is.read(temp)) > 0) {
				fos.write(temp, 0, i);
			}
			copyIsFinish = true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return copyIsFinish;
	}

	/**
	 * 
	 * @Title:
	 * @Description: 对象转换为字节
	 * @param: @param obj
	 * @param: @return
	 * @return: byte[]
	 * @throws
	 */
	public static byte[] jserialize(Object obj) {
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			return baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (oos != null)
				try {
					oos.close();
					baos.close();
				} catch (IOException e) {
				}
		}
		return null;
	}

	/**
	 * 
	 * @Title:
	 * @Description: 字节转换为对象
	 * @param: @param bits
	 * @param: @return
	 * @return: Object
	 * @throws
	 */
	public static Object jdeserialize(byte[] bits) {
		ObjectInputStream ois = null;
		ByteArrayInputStream bais = null;
		try {
			bais = new ByteArrayInputStream(bits);
			ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ois != null)
				try {
					ois.close();
					bais.close();
				} catch (IOException e) {
				}
		}
		return null;
	}

	/**
	 * 
	 * @Title:
	 * @Description: 对象转字符串
	 * @param: @param obj
	 * @param: @return
	 * @return: String
	 * @throws
	 */
	public static String object2String(Object obj) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = null;
		String str = null;
		try {
			objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(obj);
			str = new String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (objectOutputStream != null) {
					objectOutputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return str;
	}

	/**
	 * 
	 * @Title:
	 * @Description: 字符串转对象
	 * @param: @param str
	 * @param: @return
	 * @return: Object
	 * @throws
	 */
	public static Object string2Object(String str) {
		byte[] mobileBytes = Base64.decode(str.getBytes(), Base64.DEFAULT);
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(mobileBytes);
		ObjectInputStream objectInputStream = null;
		Object obj = null;
		try {
			objectInputStream = new ObjectInputStream(byteArrayInputStream);
			obj = objectInputStream.readObject();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (objectInputStream != null) {
					objectInputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				byteArrayInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return obj;
	}

	/**
	 * 
	 * @Title:
	 * @Description: 分钟 小时倒计时
	 * @param: @param currentTimeMillis
	 * @param: @param itemtime
	 * @param: @return
	 * @return: String
	 * @throws
	 */
	@SuppressLint("SimpleDateFormat")
	public static String showTime(long currentTimeMillis, long itemtime) {
		if (currentTimeMillis > itemtime) {
			long datetime = (currentTimeMillis - itemtime) / 60000;
			if (datetime == 0) {
				return "1分钟前";
			} else if (datetime < 60) {
				return datetime + "分钟前";
			} else if (datetime < 1440) {
				return datetime / 60 + "小时前";
			} else {
				Date d = new Date(itemtime);
				GregorianCalendar g1 = new GregorianCalendar();
				GregorianCalendar g2 = new GregorianCalendar();
				g2.setTime(d);
				if (g1.get(Calendar.YEAR) == g2.get(Calendar.YEAR)) {
					SimpleDateFormat sdf2 = new SimpleDateFormat("MM-dd");
					return sdf2.format(d);
				} else {
					SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
					return sdf1.format(d);
				}
			}
		} else {
			Date d = new Date(itemtime);
			GregorianCalendar g1 = new GregorianCalendar();
			GregorianCalendar g2 = new GregorianCalendar();
			g2.setTime(d);
			if (g1.get(Calendar.YEAR) == g2.get(Calendar.YEAR)) {
				SimpleDateFormat sdf2 = new SimpleDateFormat("MM-dd");
				return sdf2.format(d);
			} else {
				SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
				return sdf1.format(d);
			}
		}
	}

	@SuppressLint("SimpleDateFormat")
	public static String showTime(long currentTimeMillis, long itemtime, long date) {
		if (currentTimeMillis > itemtime) {
			long datetime = (currentTimeMillis - itemtime) / 60000;
			if (datetime == 0) {
				return "1分钟前";
			} else if (datetime < 60) {
				return datetime + "分钟前";
			} else if (datetime < 1440) {
				return datetime / 60 + "小时前";
			} else {
				Date d = new Date(date);
				GregorianCalendar g1 = new GregorianCalendar();
				GregorianCalendar g2 = new GregorianCalendar();
				g2.setTime(d);
				if (g1.get(Calendar.YEAR) == g2.get(Calendar.YEAR)) {
					SimpleDateFormat sdf2 = new SimpleDateFormat("MM-dd");
					return sdf2.format(d);
				} else {
					SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
					return sdf1.format(d);
				}
			}
		} else {
			Date d = new Date(date);
			GregorianCalendar g1 = new GregorianCalendar();
			GregorianCalendar g2 = new GregorianCalendar();
			g2.setTime(d);
			if (g1.get(Calendar.YEAR) == g2.get(Calendar.YEAR)) {
				SimpleDateFormat sdf2 = new SimpleDateFormat("MM-dd");
				return sdf2.format(d);
			} else {
				SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
				return sdf1.format(d);
			}
		}
	}

	/**
	 * 秒转化为小时分钟
	 */
	public static String ssToHHmmss(int s) {
		int N = s / 3600;
		s = s % 3600;
		int K = s / 60;
		s = s % 60;
		int M = s;
		if (N == 0 && K != 0) {
			return K + "分钟 " + M + "秒";
		} else if (N == 0 && K == 0) {
			return M + "秒";
		} else {
			return N + "小时 " + K + "分钟 " + M + "秒";
		}
	}

	/**
	 * 
	 * @Title:
	 * @Description: 隐藏输入法
	 * @param: @param act
	 * @param: @param view
	 * @return: void
	 * @throws
	 */
	public static void hideSoftInputFromWindow(Activity act, View view) {
		InputMethodManager imm = (InputMethodManager) act.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	public static void hideSoftInputFromWindow(Activity act) {
		View view = act.getCurrentFocus();
		if (view != null)
			((InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	// 隐藏输入发
	public static void hideSoftInputFromWindow(Context context) {
		View view = ((Activity) context).getWindow().peekDecorView();
		if (view != null && view.getWindowToken() != null) {
			InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	/**
	 * 保留一位小数
	 */
	public static String decimalFormatNoe(float scale) {
		DecimalFormat fnum = new DecimalFormat("##0.0");
		return fnum.format(scale);
	}

	// 读取pcm文件
	public static byte[] getPCMData(String filePath) {
		File file = new File(filePath);
		FileInputStream inStream;
		try {
			inStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}

		byte[] data_pack = null;
		if (inStream != null) {
			long size = file.length();

			data_pack = new byte[(int) size];
			try {
				inStream.read(data_pack);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			} finally {
				try {
					inStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return data_pack;
	}

	public static short[] byteArray2ShortArray(byte[] b) {
		int len = b.length / 2;
		int index = 0;
		short[] re = new short[len];
		byte[] buf = new byte[2];
		for (int i = 0; i < b.length;) {
			buf[0] = b[i];
			buf[1] = b[i + 1];
			short st = byteToShort(buf);
			re[index] = st;
			index++;
			i += 2;
		}
		return re;
	}

	public static short byteToShort(byte[] b) {
		short s = 0;
		short s0 = (short) (b[0] & 0xff);// 最低位
		short s1 = (short) (b[1] & 0xff);
		s1 <<= 8;
		s = (short) (s0 | s1);
		return s;
	}

	/**
	 * 
	 * @Title:
	 * @Description:浏览网页
	 * @param: @param act
	 * @param: @param phone
	 * @return: void
	 * @throws
	 */
	public static void browseWebpage(Activity act, String url) {
		if (TextUtils.isEmpty(url)) {
			return;
		}
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		Uri content_url = Uri.parse(url);
		intent.setData(content_url);
		act.startActivity(intent);
	}

	/**
	 * 
	 * @Title:
	 * @Description:发送短信
	 * @param: @param act
	 * @param: @param phone
	 * @return: void
	 * @throws
	 */
	public static void sendMsg(Activity act, String phone) {
		Uri uri = Uri.parse("smsto:".concat(phone));
		Intent it = new Intent(Intent.ACTION_SENDTO, uri);
		// it.putExtra("sms_body", "102");
		act.startActivity(it);
	}

	/**
	 * 
	 * @Title:
	 * @Description: 滚动聚焦
	 * @param: @param view
	 * @return: void
	 * @throws
	 */
	public static void getFocusable(View view) {
		view.setFocusable(true);
		view.setFocusableInTouchMode(true);
		view.requestFocus();
	}

	/**
	 * 
	 * @Title:
	 * @Description: 格式化
	 * @param: @param context
	 * @param: @param res
	 * @param: @param obj
	 * @param: @return
	 * @return: String
	 * @throws
	 */
	public static String getFormatString(Context context, int res, Object... obj) {
		return String.format(context.getString(res), obj);
	}

	private static final int UNCONSTRAINED = -1;

	/**
	 * @Description:
	 * @param options
	 * @param minSideLength
	 * @param maxNumOfPixels
	 * @return 动态计算出图片的inSampleSize
	 * @return: int
	 */
	public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		// Math.ceil(Math.sqrt(w * h / maxNumOfPixels)) ：w * h /
		// maxNumOfPixels平方结果的小数部分一律向整数部分进位
		int lowerBound = (maxNumOfPixels == UNCONSTRAINED) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		// Math.floor(w / minSideLength) 将w / minSideLength结果值一律舍去小数，仅保留整数
		int upperBound = (minSideLength == UNCONSTRAINED) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			return lowerBound;
		}
		if ((maxNumOfPixels == UNCONSTRAINED) && (minSideLength == UNCONSTRAINED)) {
			return 1;
		} else if (minSideLength == UNCONSTRAINED) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	// 时间戳格式解析
	public static String getConsumerNoteTime(String dateTime) {
		return dateTime.substring(0, 4) + "-" + dateTime.substring(4, 6) + "-" + dateTime.substring(6, 8) + " " + dateTime.substring(8, 10) + ":" + dateTime.substring(10, 12) + ":" + dateTime.substring(12, 14);
	}

	public static int setRotation(int width, int height, int uiRot, int rotation) {
		if (width >= height) {
			if (uiRot == 1 || uiRot == 3) {
				switch (uiRot) {
				case 1:
					rotation = 0;

					break;
				case 3:
					rotation = 180;

					break;
				}

			} else {
				switch (uiRot) {
				case 0:
					rotation = 0;

					break;

				case 2:
					rotation = 180;

					break;

				}
			}
		} else if (height >= width) {
			if (uiRot == 0 || uiRot == 2) {

				switch (uiRot) {
				case 0:
					rotation = 90;

					break;

				case 2:
					rotation = 270;

					break;

				}
			} else {
				switch (uiRot) {
				case 1:
					rotation = 270;

					break;
				case 3:
					rotation = 90;

					break;
				}
			}
		}
		return rotation;
	}
	
	/**
	 * 输入车牌检测\如果是特殊车牌、关闭输入法
	 * @param context
	 * @param plateNum
	 * @param handle
	 * @param mPlateNum
	 */
	public static void chkOtherPlate(Context context, Handler handle, EditText mPlateNum, String plateNum) {
		if (plateNum.length() == 7) {
			if (cmpPlateNum(handle, plateNum))
				MyUtils.closeInputMethodWindow(context, mPlateNum);
		}
	}
	
	/**
	 * 检测布控车牌、VIP车牌
	 * @param plateNum
	 * @param handle
	 */
	public static Boolean cmpPlateNum (Handler handle, String plateNum) {
		List<BlackList> listBlack = DBManager.getInstance().getInBlackList(plateNum, null);
		List<VipCarInfo> listVip = DBManager.getInstance().getVipCarList(plateNum, null);

		int blackSize = listBlack.size();
		int vipSize = listVip.size();

		//判断有无布控车牌、VIP车牌
		if (blackSize > 0 || vipSize > 0) {
			Message msg = new Message();
			Bundle bundle = new Bundle();
			if (listBlack.size() > 0) {
				bundle.putString("title", "布控车牌");
				String code = listBlack.get(0).blacklisttype;
				String type = DBManager.getInstance().getBlackTypeNameByCode(code);
				bundle.putString("message", plateNum + ": " + type);
				bundle.putInt("color", Color.RED);
			} else if (listVip.size() > 0) {
				bundle.putString("title", "VIP车牌");
				bundle.putString("message", "白名单车辆不支持现场违法处理");
				bundle.putInt("color", Color.BLUE);
			}
			msg.setData(bundle);
			msg.what = ConstantStatus.OTHER_PLATENUM;
			handle.sendMessage(msg);
			
			return true;
		}
		return false;
	}
	
	public static void showPlateInfo(Context context, Message msg) {
		MsgDialog dialog = new MsgDialog(context);
		dialog.setTiele(msg.getData().getString("title"));
		dialog.setMsg(msg.getData().getString("message"));
		dialog.setMsgColor(msg.getData().getInt("color"));
		dialog.show();
	}
	
	public static MsgDialog showNoPrinter(Context context) {
		MsgDialog dialog = new MsgDialog(context);
		dialog.setTiele("未检测到打印机");
		dialog.setMsg2("请检查打印机.如果无打印机,请修改打印机设置!");
		dialog.setButLeft("检测打印机");
		dialog.setButRight("修改打印机设置");
		dialog.show();
		return dialog;
	}

	public static String ChangeTimeTochs(String timeStr) {
		String res = null;
		String[] dateStr = timeStr.split(" ");
		String[] date = dateStr[0].split("-");

		res = date[0] + "年" + date[1] + "月" + date[2] + "日";

		if (dateStr.length == 2) {
			String[] time = dateStr[1].split(":");
			res = res + time[0] + "时" + time[1] + "分";
		}
		return  res;
	}

	public static void SaveGPSdata(GPSInfo gps, boolean value) {
		try {
			File file = new File(Environment.getExternalStorageDirectory(),	"gps.log");
			FileOutputStream fos = new FileOutputStream(file, true);
			String info = "Time:" +gps.datetime + " res:" + value + " latitude:" + gps.latitude + " longitude:" + gps.longitude + "\n";
			fos.write(info.getBytes());
			fos.close();
			MsgToast.geToast().setMsg("Save GPS " + value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void SaveSendAck(String ack) {
		try {
			File file = new File(Environment.getExternalStorageDirectory(),	"gps.log");
			FileOutputStream fos = new FileOutputStream(file, true);
			String info = ack + "\n";
			fos.write(info.getBytes());
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String GetCarStatus(String value) {
		String carststus = "";
		String carValue = value;
		for (int i = 0, len = carValue.length(); i < len; i++) {
			if (i != 0)
				carststus =carststus + "-";
			carststus = carststus + DBManager.getInstance().getNameByTypeAndCodeFromDic("CAR_STATE",carValue.substring(i, i+1));
		}
		return carststus;
	}

	//检查有扩展信息，而未使用问题
	public static boolean CheckRoadExt(String roadname, ResultData.RoadExt roadExt){
		int index = 0;
		if (roadname.length() > 6 && roadname.contains("-")){
			index = roadname.indexOf("-");
			if (index == 5) {
				RoadInfo roadInfo = DBManager.getInstance().getRoadInfoByName(roadname.substring(6));
				if (roadInfo.note == null || roadInfo.note.equals(""))
					return true;
				else {
					if (roadInfo.note.contains("lddm")) {
						if (roadExt != null && roadExt.ldlist != null && roadExt.ldlist.size() > 0)
							return true;
					} else
						return true;
				}
			}
		}
		return false;
	}

	public static Map<String, String> DecodeXMLStringtoMap(String xmlStr) {
		String key;
		String value = "";
		Map<String, String> xmlMap = new HashMap<String, String>();
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(new StringReader(xmlStr));
			int event = parser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {
				switch (event) {
					case XmlPullParser.START_DOCUMENT:
						break;
					case XmlPullParser.START_TAG:
						key = parser.getName();
						int count = parser.getAttributeCount();
						if (count > 0)
							break;
						value = parser.nextText();
						xmlMap.put(key, value);
						break;
					case XmlPullParser.END_TAG:
						break;
				}
				event = parser.next();
			}
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			xmlMap = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			xmlMap = null;
		}
		return xmlMap;
	}

	public static String unescapeJava(String str) throws IOException
	{
		Writer out = new StringWriter();
		if (str != null)
		{
			int sz = str.length();
			StringBuilder unicode = new StringBuilder(4);
			boolean hadSlash = false;
			boolean inUnicode = false;

			for (int i = 0; i < sz; ++i)
			{
				char ch = str.charAt(i);
				if (inUnicode)
				{
					unicode.append(ch);
					if (unicode.length() == 4)
					{
						try
						{
							int nfe = Integer.parseInt(unicode.toString(), 16);
							out.write((char) nfe);
							unicode.setLength(0);
							inUnicode = false;
							hadSlash = false;
						}
						catch (NumberFormatException var9)
						{
						}
					}
				}
				else if (hadSlash)
				{
					hadSlash = false;
					switch (ch)
					{
						case '\"':
							out.write(34);
							break;
						case '\'':
							out.write(39);
							break;
						case '\\':
							out.write(92);
							break;
						case 'b':
							out.write(8);
							break;
						case 'f':
							out.write(12);
							break;
						case 'n':
							out.write(10);
							break;
						case 'r':
							out.write(13);
							break;
						case 't':
							out.write(9);
							break;
						case 'u':
							inUnicode = true;
							break;
						default:
							out.write(ch);
					}
				}
				else if (ch == 92)
				{
					hadSlash = true;
				}
				else
				{
					out.write(ch);
				}
			}

			if (hadSlash)
			{
				out.write(92);
			}

		}
		return out.toString();
	}

//	public static Object mapToObject(Map<String, Object> map, Class<?> beanClass) throws Exception {
//		if (map == null)
//			return null;
//
//		Object obj = beanClass.newInstance();
//
//		BeanUtils.populate(obj, map);
//
//		return obj;
//	}

	public static VehicleInfoX ConvertMapToVehicle(Map<String, String> XmlMap) {
		VehicleInfoX vehicleInfo = new VehicleInfoX();
		vehicleInfo.xh = XmlMap.get("xh");
		vehicleInfo.hpzl = XmlMap.get("hpzl");
		vehicleInfo.hphm = XmlMap.get("fzjg").substring(0, 1) + XmlMap.get("hphm");
		vehicleInfo.clpp1 = XmlMap.get("clpp1");
		vehicleInfo.clxh = XmlMap.get("clxh");
		vehicleInfo.clpp2 = XmlMap.get("clpp2");
		vehicleInfo.gcjk = XmlMap.get("gcjk");
		vehicleInfo.zzg = XmlMap.get("zzg");
		vehicleInfo.zzcmc = XmlMap.get("zzcmc");
		vehicleInfo.clsbdh = XmlMap.get("clsbdh");
		vehicleInfo.fdjh = XmlMap.get("fdjh");
		vehicleInfo.cllx = XmlMap.get("cllx");
		vehicleInfo.csys = XmlMap.get("csys");
		vehicleInfo.syxz = XmlMap.get("syxz");
		vehicleInfo.sfzmhm = XmlMap.get("sfzmhm");
		vehicleInfo.sfzmmc = XmlMap.get("sfzmmc");
		vehicleInfo.syr = XmlMap.get("syr");
		vehicleInfo.ccdjrq = XmlMap.get("ccdjrq");
		vehicleInfo.djrq = XmlMap.get("djrq");
		vehicleInfo.yxqz = XmlMap.get("yxqz");
		vehicleInfo.qzbfqz = XmlMap.get("qzbfqz");
		vehicleInfo.fzjg = XmlMap.get("fzjg");
		vehicleInfo.glbm = XmlMap.get("glbm");
		vehicleInfo.bxzzrq = XmlMap.get("bxzzrq");
		vehicleInfo.zt = XmlMap.get("zt");
		vehicleInfo.dybj = XmlMap.get("dybj");
		vehicleInfo.fdjxh = XmlMap.get("fdjxh");
		vehicleInfo.rlzl = XmlMap.get("rlzl");
		vehicleInfo.pl = XmlMap.get("pl");
		vehicleInfo.gl = XmlMap.get("gl");
		vehicleInfo.zxxs = XmlMap.get("zxxs");
		vehicleInfo.cwkc = XmlMap.get("cwkc");
		vehicleInfo.cwkk = XmlMap.get("cwkk");
		vehicleInfo.cwkg = XmlMap.get("cwkg");
		vehicleInfo.hxnbcd = XmlMap.get("hxnbcd");
		vehicleInfo.hxnbkd = XmlMap.get("hxnbkd");
		vehicleInfo.hxnbgd = XmlMap.get("hxnbgd");
		vehicleInfo.gbthps = XmlMap.get("gbthps");
		vehicleInfo.zs = XmlMap.get("zs");
		vehicleInfo.zj = XmlMap.get("zj");
		vehicleInfo.qlj = XmlMap.get("qlj");
		vehicleInfo.hlj = XmlMap.get("hlj");
		vehicleInfo.ltgg = XmlMap.get("ltgg");
		vehicleInfo.lts = XmlMap.get("lts");
		vehicleInfo.zzl = XmlMap.get("zzl");
		vehicleInfo.zbzl = XmlMap.get("zbzl");
		vehicleInfo.hdzzl = XmlMap.get("hdzzl");
		vehicleInfo.hdzk = XmlMap.get("hdzk");
		vehicleInfo.zqyzl = XmlMap.get("zqyzl");
		vehicleInfo.qpzk = XmlMap.get("qpzk");
		vehicleInfo.hpzk = XmlMap.get("hpzk");
		vehicleInfo.hbdbqk = XmlMap.get("hbdbqk");
		vehicleInfo.ccrq = XmlMap.get("ccrq");
		vehicleInfo.clyt = XmlMap.get("clyt");
		vehicleInfo.ytsx = XmlMap.get("ytsx");
		vehicleInfo.xszbh = XmlMap.get("xszbh");
		vehicleInfo.jyhgbzbh = XmlMap.get("jyhgbzbh");

		return vehicleInfo;
	}

	public static PersionInfoX ConvertMapToDriver(Map<String, String> XmlMap) {
		PersionInfoX persionInfo = new PersionInfoX();
		persionInfo.sfzmmc = XmlMap.get("sfzmmc"); //身份证明名称
		persionInfo.sfzmhm = XmlMap.get("sfzmhm"); //身份证明号码
		persionInfo.hmcd = XmlMap.get("hmcd"); //号码长度
		persionInfo.xm = XmlMap.get("xm"); //姓名
		persionInfo.xb = XmlMap.get("xb"); //性别
		persionInfo.csrq = XmlMap.get("csrq"); //出生日期
		persionInfo.gj = XmlMap.get("gj"); //国籍
		persionInfo.djzsxzqh = XmlMap.get("djzsxzqh"); //登记住所行政区划
		persionInfo.djzsxxdz = XmlMap.get("djzsxxdz"); //登记住所详细地址
		persionInfo.lxzsxzqh = XmlMap.get("lxzsxzqh"); //联系住所行政区划
		persionInfo.lxzsxxdz = XmlMap.get("lxzsxxdz"); //联系住所详细地址
		persionInfo.lxzsyzbm = XmlMap.get("lxzsyzbm"); //联系住所邮政编码
		persionInfo.lxdh = XmlMap.get("lxdh"); //联系电话
		persionInfo.sjhm = XmlMap.get("sjhm"); //手机号码
		persionInfo.dzyx = XmlMap.get("dzyx"); //电子邮箱
		persionInfo.dabh = XmlMap.get("dabh"); //档案编号
		persionInfo.zjcx = XmlMap.get("zjcx"); //准驾车型
		persionInfo.ljjf = XmlMap.get("ljjf"); //累积记分
		persionInfo.zt = XmlMap.get("zt"); //状态
		persionInfo.cclzrq = XmlMap.get("cclzrq"); //初次领证日期
		persionInfo.jzqx = XmlMap.get("jzqx"); //驾证期限
		persionInfo.syrq = XmlMap.get("syrq"); //下一体检日期
		persionInfo.yxqs = XmlMap.get("yxqs"); //有效期始
		persionInfo.yxqz = XmlMap.get("yxqz"); //有效期止
		persionInfo.xzqh = XmlMap.get("xzqh"); //行政区划
		persionInfo.zzzm = XmlMap.get("zzzm"); //暂住证明
		persionInfo.gxsj = XmlMap.get("gxsj"); //更新时间
		persionInfo.zxbh = XmlMap.get("zxbh"); //证芯编号
		persionInfo.fzjg = XmlMap.get("fzjg"); //发证机关

		return persionInfo;
	}
}
