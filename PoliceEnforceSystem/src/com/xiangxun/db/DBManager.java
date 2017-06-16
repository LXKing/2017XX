package com.xiangxun.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;

import com.xiangxun.app.XiangXunApplication;
import com.xiangxun.bean.AccidentCar;
import com.xiangxun.bean.AccidentInfo;
import com.xiangxun.bean.AddressBook;
import com.xiangxun.bean.BLCompare;
import com.xiangxun.bean.BlackList;
import com.xiangxun.bean.EnforcementData;
import com.xiangxun.bean.FieldPunishData;
import com.xiangxun.bean.GPSInfo;
import com.xiangxun.bean.LawCheckInfo;
import com.xiangxun.bean.MDate;
import com.xiangxun.bean.PatorlInfo;
import com.xiangxun.bean.RoadInfo;
import com.xiangxun.bean.SystemMessage;
import com.xiangxun.bean.TextMessage;
import com.xiangxun.bean.Type;
import com.xiangxun.bean.UpdateVio;
import com.xiangxun.bean.VioData;
import com.xiangxun.bean.VioDic;
import com.xiangxun.bean.VioType;
import com.xiangxun.bean.VipCarInfo;
import com.xiangxun.bean.WarnAck;
import com.xiangxun.bean.WarnData;
import com.xiangxun.bean.WarnMessage;
import com.xiangxun.bean.WarnPic;
import com.xiangxun.bean.WeijinInfo;
import com.xiangxun.util.ConstantStatus;
import com.xiangxun.util.HanyupinyinUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class DBManager {
	private DBHelper helper;
	public static DBManager instance = null;
	private SQLiteDatabase sqliteDatabase;

	private DBManager(Context context) {
		helper = new DBHelper(context);
		sqliteDatabase = helper.getWritableDatabase();
	}

	/***
	 * 获取本类对象实例
	 * 
	 * @param context
	 *            上下文对象


	 * @return
	 */
	public static final DBManager getInstance() {
		if (instance == null) {
			instance = new DBManager(XiangXunApplication.getInstance());
		}
		return instance;
	}

	public void close() {
		if (sqliteDatabase.isOpen()) {
			sqliteDatabase.close();
			sqliteDatabase = null;
		}
		if (helper != null) {
			helper.close();
			helper = null;
		}
		if (instance != null)
			instance = null;
	}

	public int add(VioData viodata) {
		int result = -1;
		try {
			ContentValues cv = new ContentValues();
			cv.put("publishCode", viodata.getPublishCode());
			cv.put("datetime", viodata.datetime);
			cv.put("picurl0", (viodata.picurl0 != null) && (!viodata.picurl0.equals("")) ? viodata.picurl0 : "");
			cv.put("picurl1", (viodata.picurl1 != null) && (!viodata.picurl1.equals("")) ? viodata.picurl1 : "");
			cv.put("picurl2", (viodata.picurl2 != null) && (!viodata.picurl2.equals("")) ? viodata.picurl2 : "");
			cv.put("picurl3", (viodata.picurl3 != null) && (!viodata.picurl3.equals("")) ? viodata.picurl3 : "");
			cv.put("platenum", viodata.platenum);
			cv.put("roadname", viodata.roadname);
			cv.put("roadlocation", viodata.roadlocation);
			cv.put("roaddirect", viodata.roaddirect);
			cv.put("user", viodata.user);
			cv.put("issure", viodata.issure);
			cv.put("isupfile", viodata.isupfile);
			cv.put("picnum", viodata.picnum);
			cv.put("platetype", viodata.platetype);
			cv.put("roadid", viodata.roadid);
			cv.put("viotype", viodata.viotype);
			cv.put("vioid", viodata.vioid);
			cv.put("viocode", viodata.viocode);
			cv.put("carcolor", viodata.carColor);
			cv.put("platecolor", viodata.plateColor);
			cv.put("carcolorcode", viodata.carColorCode);
			cv.put("platecolorcode", viodata.plateColorCode);
			cv.put("disposetype", viodata.disposetype);
			result = (int) sqliteDatabase.insert("viodata", null, cv);
			cv.clear();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public void add(BlackList blacklist) {
		ContentValues cv = new ContentValues();
		cv.put("platenum", blacklist.platenum);
		cv.put("blacklisttype", blacklist.blacklisttype);
		cv.put("platetype", blacklist.platetype);
		cv.put("blt", blacklist.blt);
		cv.put("datetime", blacklist.datetime);
		cv.put("brand", blacklist.brand);
		cv.put("owner", blacklist.owner);
		cv.put("sid", blacklist._id);
		sqliteDatabase.insert("blacklist", null, cv);
		cv.clear();
	}

	// 添加白名单数据


	public void add(VipCarInfo vipcar) {
		ContentValues cv = new ContentValues();
		cv.put("platenum", vipcar.platenum);
		cv.put("platetype", vipcar.platetype);
		sqliteDatabase.insert("vipcar", null, cv);
		cv.clear();
	}

	public void add(GPSInfo gpsInfo) {
		ContentValues cv = new ContentValues();
		cv.put("datetime", gpsInfo.datetime);
		cv.put("provider", gpsInfo.provider);
		cv.put("latitude", gpsInfo.latitude);
		cv.put("longitude", gpsInfo.longitude);
		cv.put("altitude", gpsInfo.altitude);
		cv.put("bearing", gpsInfo.bearing);
		cv.put("isupfile", gpsInfo.isupfile);
		cv.put("jobs", gpsInfo.jobs);
		try {
			sqliteDatabase.insert("gpsinfo", null, cv);
		} catch (Exception e) {
			e.printStackTrace();
		}
		cv.clear();
	}

	// 获取最近一条消息的类型
	public GPSInfo getLastestGps() {
		GPSInfo gpsInfo = new GPSInfo();
		String selection = "select * from gpsinfo order by datetime desc";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(selection, null);
			if (c.moveToFirst()) {
				gpsInfo.datetime = c.getString(c.getColumnIndex("datetime"));
				gpsInfo.provider = c.getString(c.getColumnIndex("provider"));
				gpsInfo.latitude = c.getString(c.getColumnIndex("latitude"));
				gpsInfo.longitude = c.getString(c.getColumnIndex("longitude"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return gpsInfo;
	}

	public boolean isGpsExisted(String provider, String latitude, String longitude) {
		String selection = "select * from gpsinfo where provider = ? and latitude = ? and longitude = ?";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(selection, new String[] { provider, latitude, longitude });
			if (null != c) {
				if (c.getCount() > 0) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return false;
	}

	public void add(TextMessage textMessage) {
		sqliteDatabase.beginTransaction();
		try {
			sqliteDatabase.execSQL("INSERT INTO textmsg(" + "datetime, " + "isread, " + "text) values(?,?,?)", new Object[] { textMessage.datetime, textMessage.isread, textMessage.text });
			sqliteDatabase.setTransactionSuccessful();
		} finally {
			sqliteDatabase.endTransaction();
		}
	}

	public void add(SystemMessage sysMsg) {
		try {
			sqliteDatabase.beginTransaction();
			sqliteDatabase.execSQL("INSERT INTO sysmsg(" + "datetime, " + "isread, " + "issign, " + "isack, " + "text, " + "msgtype) values(?,?,?,?,?,?)", new Object[] { sysMsg.datetime, sysMsg.isread, sysMsg.isSigned, sysMsg.isAck, sysMsg.text, sysMsg.msgtype });
			sqliteDatabase.setTransactionSuccessful();
		} finally {
			sqliteDatabase.endTransaction();
		}
	}

	public void add(String id, String type) {
		ContentValues values = new ContentValues();
		values.put("id", id);
		values.put("type", type);
		values.put("isused", 0);
		String sql = "select * from printid where id = '" + id + "' AND type = '" + type + "'";
		Cursor rawQuery = sqliteDatabase.rawQuery(sql, null);
		if (rawQuery.moveToFirst()) {
			int isused = rawQuery.getInt(rawQuery.getColumnIndex("isused"));
			values.put("isused", isused);
			sqliteDatabase.update("printid", values, null, null);
		} else {
			sqliteDatabase.insert("printid", null, values);
		}
		values.clear();
	}

	public void add(BLCompare bcl) {
		ContentValues cv = new ContentValues();
		cv.put("datetime", bcl.datetime);
		cv.put("picurl0", bcl.picurl0);
		cv.put("picurl1", bcl.picurl1);
		cv.put("picurl2", bcl.picurl2);
		cv.put("picurl3", bcl.picurl3);
		cv.put("platenum", bcl.platenum);
		cv.put("roadname", bcl.roadname);
		cv.put("roadlocation", bcl.roadlocation);
		cv.put("roaddirect", bcl.roaddirect);
		cv.put("user", bcl.user);
		cv.put("issure", bcl.issure);
		cv.put("isupfile", bcl.isupfile);
		cv.put("picnum", bcl.picnum);
		cv.put("platetype", bcl.platetype);
		cv.put("bltype", bcl.bltype);
		cv.put("carcolor", bcl.carcolor);
		cv.put("brand", bcl.brand);
		cv.put("owner", bcl.owner);
		cv.put("blt", bcl.blt);
		sqliteDatabase.insert("blcompare", null, cv);
		cv.clear();
	}

	// ����¹ʳ���




	public void add(AccidentCar ac) {
		ContentValues cv = new ContentValues();
		cv.put("cartage", ac.getCartag());
		cv.put("memo", ac.getMemo());
		cv.put("ownerid", ac.getOwnerid());
		cv.put("platenum", ac.getPlatenum());
		cv.put("platetype", ac.getPlatetype());
		sqliteDatabase.insert("accidentcar", null, cv);
		cv.clear();
	}

	// ����¹���Ϣ


	public void add(AccidentInfo ai) {
		ContentValues cv = new ContentValues();
		cv.put("id", ai.id);
		cv.put("carnum", ai.carnum);
		cv.put("memo", ai.memo);
		cv.put("occurtime", ai.infolist);
		cv.put("realtime", ai.realtime);
		cv.put("roadname", ai.roadname);
		cv.put("roadlocation", ai.roadlocation);
		cv.put("roaddirect", ai.roaddirect);
		cv.put("death", ai.death);
		cv.put("hurt", ai.hurt);
		cv.put("progress", ai.progress);
		cv.put("isupfile", ai.isupfile);
		cv.put("isread", ai.isread);
		cv.put("acctype", ai.acctype);
		cv.put("caller", ai.caller);
		cv.put("joinlist", ai.joinlist);
		cv.put("phone", ai.phone);
		cv.put("weather", ai.weather);
		cv.put("pic1", ai.pic1);
		cv.put("pic2", ai.pic2);
		cv.put("pic3", ai.pic3);
		cv.put("pic4", ai.pic4);
		cv.put("pic5", ai.pic5);
		cv.put("pic6", ai.pic6);
		cv.put("pic7", ai.pic7);
		cv.put("pic8", ai.pic8);
		cv.put("pic9", ai.pic9);
		cv.put("pic10", ai.pic10);
		sqliteDatabase.insert("accident", null, cv);
		cv.clear();
	}

	// 添加执法检查信息


	public void add(LawCheckInfo z) {
		ContentValues cv = new ContentValues();
		cv.put("datetime", z.datetime);
		cv.put("memo", z.memo);
		cv.put("actual", z.actual);
		cv.put("platetype", z.cartype);
		cv.put("checktype", z.checktype);
		cv.put("name", z.drivername);
		cv.put("handleway", z.handleway);
		cv.put("must", z.must);
		cv.put("isupfile", z.isupfile);
		cv.put("driverlic", z.drivernum);
		cv.put("phone", z.phone);
		cv.put("pic1", z.pic1);
		cv.put("pic2", z.pic2);
		cv.put("pic3", z.pic3);
		cv.put("pic4", z.pic4);
		cv.put("plate", z.platenum);
		cv.put("owner", z.owner);
		cv.put("road", z.road);
		cv.put("roadlocation", z.roadlocation);
		cv.put("roaddirect", z.roaddirect);
		cv.put("viotype", z.viotype);
		sqliteDatabase.insert("taizhangdata", null, cv);
		cv.clear();
	}

	public void add(Type dt) {
		ContentValues cv = new ContentValues();
		cv.put("code", dt.code);
		cv.put("name", dt.name);
		cv.put("type", dt.type);
		sqliteDatabase.insert("dutytype", null, cv);
		cv.clear();
	}

	public void add(FieldPunishData data) {
		ContentValues cv = new ContentValues();
		cv.put("id", data.id);
		cv.put("name", data.name);
		cv.put("phone", data.phone);
		cv.put("driverlic", data.driverlic);
		cv.put("recordid", data.recordid);
		cv.put("licenseoffice", data.licenseoffice);
		cv.put("plate", data.plate);
		cv.put("platetype", data.platetype);
		cv.put("plateoffice", data.plateoffice);
		cv.put("location", data.location);
		cv.put("roadlocation", data.roadlocation);
		cv.put("roaddirect", data.roaddirect);
		cv.put("pic1", data.pic1);
		cv.put("pic2", data.pic2);
		cv.put("pic3", data.pic3);
		cv.put("pic4", data.pic4);
		cv.put("content", data.content);
		cv.put("datetime", data.datetime);
		cv.put("action", data.action);
		cv.put("money", data.money);
		cv.put("value", data.value);
		cv.put("picnum", data.picnum);
		cv.put("vioid", data.vioid);
		cv.put("licensetype", data.licensetype);
		cv.put("isupfile", data.isupfile);
		cv.put("isprinted", data.isprinted);
		cv.put("vioid", data.vioid);
		cv.put("owner", data.owner);
		sqliteDatabase.insert("fieldpunishdata", null, cv);
		cv.clear();
	}

	public void add(EnforcementData data) {
		ContentValues cv = new ContentValues();
		cv.put("id", data.id);
		cv.put("name", data.name);
		cv.put("phone", data.phone);
		cv.put("driverlic", data.driverlic);
		cv.put("recordid", data.recordid);
		cv.put("licenseoffice", data.licenseoffice);
		cv.put("plate", data.plate);
		cv.put("platetype", data.platetype);
		cv.put("plateoffice", data.plateoffice);
		cv.put("location", data.location);
		cv.put("roadlocation", data.roadlocation);
		cv.put("roaddirect", data.roaddirect);
		cv.put("content", data.content);
		cv.put("datetime", data.datetime);
		cv.put("action", data.action);
		cv.put("picnum", data.picnum);
		cv.put("pic1", data.pic1);
		cv.put("pic2", data.pic2);
		cv.put("pic3", data.pic3);
		cv.put("pic4", data.pic4);
		cv.put("viotype", data.viotype);
		cv.put("licensetype", data.licensetype);
		cv.put("isupfile", data.isupfile);
		cv.put("isprinted", data.isprinted);
		cv.put("code", data.code);
		cv.put("vioid", data.vioid);
		sqliteDatabase.insert("enforcementdata", null, cv);
		cv.clear();
	}

	// ���Ѳ����Ϣ


	public void add(PatorlInfo pi) {
		ContentValues cv = new ContentValues();
		cv.put("datetime", pi.datetime);
		cv.put("memo", pi.memo);
		cv.put("roadname", pi.roadname);
		cv.put("roadlocation", pi.roadlocation);
		cv.put("roaddirect", pi.roaddirect);
		cv.put("patorltype", pi.patorltype);
		cv.put("isupfile", pi.isupfile);
		cv.put("isread", pi.isread);
		cv.put("km", pi.km);
		cv.put("user", pi.user);
		cv.put("id", pi.id);
		cv.put("picsole", pi.picsole);
		cv.put("pic1", pi.pic1);
		cv.put("pic2", pi.pic2);
		cv.put("pic3", pi.pic3);
		cv.put("pic4", pi.pic4);
		sqliteDatabase.insert("patorl", null, cv);
		cv.clear();
	}

	// 添加违禁物品信息
	public void add(WeijinInfo weijin) {
		ContentValues cv = new ContentValues();
		cv.put("disposemode", weijin.disposemode);
		cv.put("features", weijin.features);
		cv.put("id", weijin.id);
		cv.put("lawprovision", weijin.lawProvision);
		cv.put("name", weijin.name);
		cv.put("type", weijin.type);
		cv.put("uncode", weijin.uncode);
		sqliteDatabase.insert("weijininfo", null, cv);
		cv.clear();
	}

	// 添加违法行为
	public void add(VioType vio) {
		ContentValues cv = new ContentValues();
		cv.put("code", vio.code);
		cv.put("name", vio.name);
		cv.put("finedefault", vio.fineDefault);
		cv.put("finemin", vio.fineMin);
		cv.put("finemax", vio.fineMax);
		cv.put("law", vio.law);
		cv.put("rule", vio.rule);
		cv.put("method", vio.method);
		cv.put("punish", vio.punish);
		cv.put("simplename", vio.simpleName);
		cv.put("isshow", vio.isshow);
		cv.put("ordernum", vio.ordernum);
		cv.put("marker", vio.marker);
		sqliteDatabase.insert("viotype", null, cv);
		cv.clear();
	}

	// 添加违法词典
	public void add(VioDic vio) {
		ContentValues cv = new ContentValues();
		cv.put("code", vio.code);
		cv.put("name", vio.name);
		cv.put("finedefault", vio.fineDefault);
		cv.put("finemin", vio.fineMin);
		cv.put("finemax", vio.fineMax);
		cv.put("law", vio.law);
		cv.put("rule", vio.rule);
		cv.put("method", vio.method);
		cv.put("punish", vio.punish);
		cv.put("simplename", vio.simpleName);
		cv.put("isshow", vio.isshow);
		cv.put("ordernum", vio.ordernum);
		cv.put("marker", vio.marker);
		sqliteDatabase.insert("viodic", null, cv);
		cv.clear();
	}

	// 添加道路数据
	public void add(RoadInfo road) {
		ContentValues cv = new ContentValues();
		cv.put("id", road.id);
		cv.put("name", road.name);
		cv.put("note", road.note);
		cv.put("groupid", road.groupId);
		cv.put("pid", road.pid);
		cv.put("levels", road.levels);
		cv.put("status", road.status);
		cv.put("uploadcode", road.uploadcode);
		cv.put("orgid", road.orgId);
		cv.put("orgname", road.orgName);
		cv.put("coderoadtype", road.coderoadtype);
		cv.put("codeRoadTp", road.codeRoadType);
		cv.put("coderoaddh", road.codeRoadDh);
		cv.put("coderoadzh", road.codeRoadZh);
		cv.put("coderoadmi", road.codeRoadMi);
		sqliteDatabase.insert("road", null, cv);
		cv.clear();
	}

	public void isReadTmsg(int id) {
		ContentValues cv = new ContentValues();
		cv.put("isread", 1);
		sqliteDatabase.update("textmsg", cv, "_id = ?", new String[] { id + "" });
		cv.clear();
	}

	public void isReadSysmsg(int id) {
		ContentValues cv = new ContentValues();
		cv.put("isread", 1);
		sqliteDatabase.update("sysmsg", cv, "_id = ?", new String[] { id + "" });
		cv.clear();
	}

	public void isSignSysmsg(int id) {
		ContentValues cv = new ContentValues();
		cv.put("issign", 1);
		sqliteDatabase.update("sysmsg", cv, "_id = ?", new String[] { id + "" });
		cv.clear();
	}

	public void isAckSysmsg(int id, int value) {
		ContentValues cv = new ContentValues();
		cv.put("isack", value);
		sqliteDatabase.update("sysmsg", cv, "_id = ?", new String[] { id + "" });
		cv.clear();
	}

	// �ϴ��¹���Ϣ
	public void accidentUp(String id) {
		ContentValues cv = new ContentValues();
		cv.put("isupfile", 1);
		sqliteDatabase.update("accident", cv, "id = ?", new String[] { id + "" });
		cv.clear();
	}

	public void taizhangUp(String datetime) {
		ContentValues cv = new ContentValues();
		cv.put("isupfile", 1);
		sqliteDatabase.update("taizhangdata", cv, "datetime = ?", new String[] { datetime + "" });
		cv.clear();
	}

	// �ϴ�Ѳ����Ϣ
	public void patorlUp(String id) {
		ContentValues cv = new ContentValues();
		cv.put("isupfile", 1);
		sqliteDatabase.update("patorl", cv, "id = ?", new String[] { id + "" });
		cv.clear();
	}

	// ����˱��λ
	public void isRebuk(String soleid, int isrebuked, String rebuksug1, String rebuksug2, String rebuksug3, String rebuksug4) {
		ContentValues cv = new ContentValues();
		cv.put("isrebuked", isrebuked);
		cv.put("rebuksug1", rebuksug1 + "");
		cv.put("rebuksug2", rebuksug2 + "");
		cv.put("rebuksug3", rebuksug3 + "");
		cv.put("rebuksug4", rebuksug4 + "");
		sqliteDatabase.update("rebuk", cv, "soleid = ?", new String[] { soleid + "" });
		cv.clear();
	}

	public List<PatorlInfo> getUnUpPator(int index, int count, Handler handler) {
		List<PatorlInfo> patorlInfos = new ArrayList<PatorlInfo>();
		if (index == 1) {
			index = 0;
		} else {
			index = (index - 1) * count;
			count = count * index;
		}
		String selection = "select * from patorl order by datetime desc limit ?,?";
		Cursor cursor = null;
		try {
			cursor = sqliteDatabase.rawQuery(selection, new String[] { String.valueOf(index), String.valueOf(count) });
			if (cursor.moveToFirst()) {
				for (int i = 0; i < cursor.getCount(); i++) {
					PatorlInfo pi = new PatorlInfo();
					pi._id = cursor.getInt(cursor.getColumnIndex("_id"));
					pi.datetime = cursor.getString(cursor.getColumnIndex("datetime"));
					pi.id = cursor.getString(cursor.getColumnIndex("id"));
					pi.isread = cursor.getInt(cursor.getColumnIndex("isread"));
					pi.isupfile = cursor.getInt(cursor.getColumnIndex("isupfile"));
					pi.km = cursor.getInt(cursor.getColumnIndex("km"));
					pi.memo = cursor.getString(cursor.getColumnIndex("memo"));
					pi.patorltype = cursor.getInt(cursor.getColumnIndex("patorltype"));
					pi.pic1 = cursor.getString(cursor.getColumnIndex("pic1"));
					pi.pic2 = cursor.getString(cursor.getColumnIndex("pic2"));
					pi.pic3 = cursor.getString(cursor.getColumnIndex("pic3"));
					pi.pic4 = cursor.getString(cursor.getColumnIndex("pic4"));
					pi.roadname = cursor.getString(cursor.getColumnIndex("roadname"));
					pi.roadlocation = cursor.getString(cursor.getColumnIndex("roadlocation"));
					pi.roaddirect = cursor.getString(cursor.getColumnIndex("roaddirect"));
					pi.user = cursor.getString(cursor.getColumnIndex("user"));
					pi.picsole = cursor.getString(cursor.getColumnIndex("picsole"));
					patorlInfos.add(pi);
					if (!cursor.moveToNext())
						break;
				}
			}
			handler.sendMessage(createMsg(ConstantStatus.SREACH_DETAIL_SUCCESS, patorlInfos));
		} catch (Exception e) {
			e.printStackTrace();
			handler.sendMessage(createMsg(ConstantStatus.SREACH_DETAIL_FALSE, null));
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return patorlInfos;
	}

	// ��ȡѲ����Ϣ
	public List<PatorlInfo> getUnUpPator(int sindex, int count) {
		List<PatorlInfo> list = new ArrayList<PatorlInfo>();
		list.clear();
		String selection = "select * from patorl where isupfile = 0 limit ?,?";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(selection, new String[] { String.valueOf(sindex), String.valueOf(count) });
			if (c.moveToFirst()) {
				for (int i = 0; i < c.getCount(); i++) {
					PatorlInfo pi = new PatorlInfo();
					pi._id = c.getInt(c.getColumnIndex("_id"));
					pi.datetime = c.getString(c.getColumnIndex("datetime"));
					pi.id = c.getString(c.getColumnIndex("id"));
					pi.isread = c.getInt(c.getColumnIndex("isread"));
					pi.isupfile = c.getInt(c.getColumnIndex("isupfile"));
					pi.km = c.getInt(c.getColumnIndex("km"));
					pi.memo = c.getString(c.getColumnIndex("memo"));
					pi.patorltype = c.getInt(c.getColumnIndex("patorltype"));
					pi.pic1 = c.getString(c.getColumnIndex("pic1"));
					pi.pic2 = c.getString(c.getColumnIndex("pic2"));
					pi.pic3 = c.getString(c.getColumnIndex("pic3"));
					pi.pic4 = c.getString(c.getColumnIndex("pic4"));
					pi.roadname = c.getString(c.getColumnIndex("roadname"));
					pi.roadlocation = c.getString(c.getColumnIndex("roadlocation"));
					pi.roaddirect = c.getString(c.getColumnIndex("roaddirect"));
					pi.user = c.getString(c.getColumnIndex("user"));
					pi.picsole = c.getString(c.getColumnIndex("picsole"));
					list.add(pi);
					if (!c.moveToNext())
						break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return list;
	}

	// ��ȡ�¹���Ϣ����
	public int getUnUpAccidentCount() {
		int res = -1;
		String selection = "select count(*) from accident where isupfile = 0";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(selection, null);
			if (c.moveToFirst())
				res = c.getInt(0);
		} catch (IllegalStateException e) {
			sqliteDatabase = helper.getWritableDatabase();
			e.printStackTrace();
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return res;
	}

	public void getAccident(int sindex, int count, Handler handler) {
		List<AccidentInfo> accidentInfos = new ArrayList<AccidentInfo>();
		if (sindex == 1) {
			sindex = 0;
		} else {
			sindex = (sindex - 1) * count;
			count = count * sindex;
		}
		String selection = "select * from accident order by realtime desc limit ?, ?";
		Cursor cursor = null;
		try {
			cursor = sqliteDatabase.rawQuery(selection, new String[] { String.valueOf(sindex), String.valueOf(count) });
			if (cursor.moveToFirst()) {
				for (int i = 0; i < cursor.getCount(); i++) {
					AccidentInfo acciInfo = new AccidentInfo();
					acciInfo._id = cursor.getInt(cursor.getColumnIndex("_id"));
					acciInfo.carnum = cursor.getInt(cursor.getColumnIndex("carnum"));
					acciInfo.death = cursor.getInt(cursor.getColumnIndex("death"));
					acciInfo.hurt = cursor.getInt(cursor.getColumnIndex("hurt"));
					acciInfo.isread = cursor.getInt(cursor.getColumnIndex("isread"));
					acciInfo.isupfile = cursor.getInt(cursor.getColumnIndex("isupfile"));
					acciInfo.memo = cursor.getString(cursor.getColumnIndex("memo"));
					acciInfo.id = cursor.getString(cursor.getColumnIndex("id"));
					acciInfo.infolist = cursor.getString(cursor.getColumnIndex("occurtime"));
					acciInfo.progress = cursor.getInt(cursor.getColumnIndex("progress"));
					acciInfo.realtime = cursor.getString(cursor.getColumnIndex("realtime"));
					acciInfo.roadname = cursor.getString(cursor.getColumnIndex("roadname"));
					acciInfo.roadlocation = cursor.getString(cursor.getColumnIndex("roadlocation"));
					acciInfo.roaddirect = cursor.getString(cursor.getColumnIndex("roaddirect"));
					acciInfo.user = cursor.getString(cursor.getColumnIndex("user"));
					acciInfo.caller = cursor.getString(cursor.getColumnIndex("caller"));
					acciInfo.acctype = cursor.getInt(cursor.getColumnIndex("acctype"));
					acciInfo.joinlist = cursor.getString(cursor.getColumnIndex("joinlist"));
					acciInfo.phone = cursor.getString(cursor.getColumnIndex("phone"));
					acciInfo.weather = cursor.getInt(cursor.getColumnIndex("weather"));

					acciInfo.pic1 = cursor.getString(cursor.getColumnIndex("pic1"));
					acciInfo.pic2 = cursor.getString(cursor.getColumnIndex("pic2"));
					acciInfo.pic3 = cursor.getString(cursor.getColumnIndex("pic3"));
					acciInfo.pic4 = cursor.getString(cursor.getColumnIndex("pic4"));
					acciInfo.pic5 = cursor.getString(cursor.getColumnIndex("pic5"));
					acciInfo.pic6 = cursor.getString(cursor.getColumnIndex("pic6"));
					acciInfo.pic7 = cursor.getString(cursor.getColumnIndex("pic7"));
					acciInfo.pic8 = cursor.getString(cursor.getColumnIndex("pic8"));
					acciInfo.pic9 = cursor.getString(cursor.getColumnIndex("pic9"));
					acciInfo.pic10 = cursor.getString(cursor.getColumnIndex("pic10"));

					acciInfo.picsole = cursor.getString(cursor.getColumnIndex("sole"));
					accidentInfos.add(acciInfo);
					if (!cursor.moveToNext())
						break;
				}
			}
			handler.sendMessage(createMsg(ConstantStatus.SREACH_DETAIL_SUCCESS, accidentInfos));
		} catch (Exception e) {
			e.printStackTrace();
			handler.sendMessage(createMsg(ConstantStatus.SREACH_DETAIL_FALSE, null));
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	// ��ȡδ�ϴ��¹����


	public List<AccidentInfo> getUnUpAccident(int sindex, int count) {
		List<AccidentInfo> list = new ArrayList<AccidentInfo>();
		list.clear();
		String selection = "select * from accident where isupfile = 0 order by _id desc limit ?, ?";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(selection, new String[] { String.valueOf(sindex), String.valueOf(count) });
			if (c.moveToFirst()) {
				for (int i = 0; i < c.getCount(); i++) {
					AccidentInfo acciInfo = new AccidentInfo();
					acciInfo._id = c.getInt(c.getColumnIndex("_id"));
					acciInfo.carnum = c.getInt(c.getColumnIndex("carnum"));
					acciInfo.death = c.getInt(c.getColumnIndex("death"));
					acciInfo.hurt = c.getInt(c.getColumnIndex("hurt"));
					acciInfo.isread = c.getInt(c.getColumnIndex("isread"));
					acciInfo.isupfile = c.getInt(c.getColumnIndex("isupfile"));
					acciInfo.memo = c.getString(c.getColumnIndex("memo"));
					acciInfo.id = c.getString(c.getColumnIndex("id"));
					acciInfo.infolist = c.getString(c.getColumnIndex("occurtime"));
					acciInfo.progress = c.getInt(c.getColumnIndex("progress"));
					acciInfo.realtime = c.getString(c.getColumnIndex("realtime"));
					acciInfo.roadname = c.getString(c.getColumnIndex("roadname"));
					acciInfo.roadlocation = c.getString(c.getColumnIndex("roadlocation"));
					acciInfo.roaddirect = c.getString(c.getColumnIndex("roaddirect"));
					acciInfo.user = c.getString(c.getColumnIndex("user"));
					acciInfo.caller = c.getString(c.getColumnIndex("caller"));
					acciInfo.acctype = c.getInt(c.getColumnIndex("acctype"));
					acciInfo.joinlist = c.getString(c.getColumnIndex("joinlist"));
					acciInfo.phone = c.getString(c.getColumnIndex("phone"));
					acciInfo.weather = c.getInt(c.getColumnIndex("weather"));

					acciInfo.pic1 = c.getString(c.getColumnIndex("pic1"));
					acciInfo.pic2 = c.getString(c.getColumnIndex("pic2"));
					acciInfo.pic3 = c.getString(c.getColumnIndex("pic3"));
					acciInfo.pic4 = c.getString(c.getColumnIndex("pic4"));
					acciInfo.pic5 = c.getString(c.getColumnIndex("pic5"));
					acciInfo.pic6 = c.getString(c.getColumnIndex("pic6"));
					acciInfo.pic7 = c.getString(c.getColumnIndex("pic7"));
					acciInfo.pic8 = c.getString(c.getColumnIndex("pic8"));
					acciInfo.pic9 = c.getString(c.getColumnIndex("pic9"));
					acciInfo.pic10 = c.getString(c.getColumnIndex("pic10"));

					acciInfo.picsole = c.getString(c.getColumnIndex("sole"));
					list.add(acciInfo);
					if (!c.moveToNext())
						break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return list;
	}

	// ��ȡ���һ��Ѳ����ϢID
	public int getLastestPatorl() {
		int res = -1;
		String selection = "select _id from patorl order by _id desc limit 0,1";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(selection, null);
			if (c.moveToFirst())
				res = c.getInt(0);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return res;
	}

	// ��ȡ������Ϣ
	public List<AccidentCar> getCarInfo(String ownerid) {
		List<AccidentCar> list = new ArrayList<AccidentCar>();
		list.clear();
		String selection = "select * from accidentcar where ownerid = ?";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(selection, new String[] { String.valueOf(ownerid) });
			if (c.moveToFirst()) {
				for (int i = 0; i < c.getCount(); i++) {
					AccidentCar ac = new AccidentCar();
					ac.set_id(c.getInt(c.getColumnIndex("_id")));
					ac.setCartag(c.getInt(c.getColumnIndex("cartage")));
					ac.setMemo(c.getString(c.getColumnIndex("memo")));
					ac.setOwnerid(c.getString(c.getColumnIndex("ownerid")));
					ac.setPlatenum(c.getString(c.getColumnIndex("platenum")));
					ac.setPlatetype(c.getInt(c.getColumnIndex("platetype")));
					list.add(ac);
					if (!c.moveToNext())
						break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}

		return list;
	}

	public void vioDataup(String id) {
		ContentValues cv = new ContentValues();
		cv.put("isupfile", 1);
		sqliteDatabase.update("viodata", cv, "vioid = ?", new String[] { id + "" });
		cv.clear();
	}

	public void gpsDataup(String id) {
		ContentValues cv = new ContentValues();
		cv.put("isupfile", 1);
		sqliteDatabase.update("gpsinfo", cv, "provider = ?", new String[] { id + "" });
		cv.clear();
	}

	public int getViodatacount() {
		int res = -1;
		Cursor c = null;
		try {
			String Selection = "select count(*) from viodata";
			c = sqliteDatabase.rawQuery(Selection, null);
			if (c.moveToFirst()) {
				res = c.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return res;
	}

	public int getunUpViodataCount() {
		int res = -1;
		String Selection = "select count(*) from viodata where isupfile <> 1";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(Selection, null);
			if (c.moveToFirst()) {
				res = c.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			c.close();
		}
		return res;
	}

	public List<VioData> getUnUpViodata(int sindex, int count) {
		ArrayList<VioData> viodatas = new ArrayList<VioData>();
		String select = "select * from viodata where isupfile <> 1 limit ?, ?";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(select, new String[] { String.valueOf(sindex), String.valueOf(count) });
			if (c.moveToFirst()) {
				for (int i = 0; i < c.getCount(); i++) {
					VioData vd = new VioData();
					vd._id = c.getInt(c.getColumnIndex("_id"));
					vd.datetime = c.getString(c.getColumnIndex("datetime"));
					vd.publishCode = c.getString(c.getColumnIndex("publishCode"));
					vd.issure = c.getInt(c.getColumnIndex("issure"));
					vd.isupfile = c.getInt(c.getColumnIndex("isupfile"));
					vd.viocode = c.getString(c.getColumnIndex("viocode"));
					vd.roadid = c.getString(c.getColumnIndex("roadid"));
					vd.picnum = c.getInt(c.getColumnIndex("picnum"));
					vd.picurl0 = c.getString(c.getColumnIndex("picurl0"));
					vd.picurl1 = c.getString(c.getColumnIndex("picurl1"));
					vd.picurl2 = c.getString(c.getColumnIndex("picurl2"));
					vd.picurl3 = c.getString(c.getColumnIndex("picurl3"));
					vd.platenum = c.getString(c.getColumnIndex("platenum"));
					vd.platetype = c.getString(c.getColumnIndex("platetype"));
					vd.roadname = c.getString(c.getColumnIndex("roadname"));
					vd.roadlocation = c.getString(c.getColumnIndex("roadlocation"));
					vd.roaddirect = c.getString(c.getColumnIndex("roaddirect"));
					vd.user = c.getString(c.getColumnIndex("user"));
					vd.viotype = c.getString(c.getColumnIndex("viotype"));
					vd.vioid = c.getString(c.getColumnIndex("vioid"));
					vd.disposetype = c.getInt(c.getColumnIndex("disposetype"));
					viodatas.add(vd);
					if (!c.moveToNext())
						break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return viodatas;
	}

	// 查询GPS总数
	public int getGpsInfoCount() {
		String selection = "select count(*) from gpsinfo";
		int res = -1;
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(selection, null);

			if (c.moveToFirst()) {
				res = c.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return res;
	}

	public int getUnUpGpsCount() {
		String selection = "select count(*) from gpsinfo where isupfile <> 1";
		int res = -1;
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(selection, null);

			if (c.moveToFirst()) {
				res = c.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return res;
	}

	// 查询GPS记录，但是不显示，用于向后台传，通过时间排序
	public List<GPSInfo> getGpsInfo(int sindex, int count) {
		ArrayList<GPSInfo> gpsdata = new ArrayList<GPSInfo>();
		String selection = "select * from gpsinfo limit ?, ? order by datetime asc";

		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(selection, new String[] { String.valueOf(sindex), String.valueOf(count) });
			if (c.moveToFirst()) {
				for (int i = 0; i < c.getCount(); i++) {
					GPSInfo vd = new GPSInfo();
					vd._id = c.getInt(c.getColumnIndex("_id"));
					vd.datetime = c.getString(c.getColumnIndex("datetime"));
					vd.isupfile = c.getInt(c.getColumnIndex("isupfile"));
					vd.altitude = c.getString(c.getColumnIndex("altitude"));
					vd.bearing = c.getString(c.getColumnIndex("bearing"));
					vd.latitude = c.getString(c.getColumnIndex("latitude"));
					vd.longitude = c.getString(c.getColumnIndex("longitude"));
					vd.provider = c.getString(c.getColumnIndex("provider"));
					vd.jobs = c.getString(c.getColumnIndex("jobs"));
					gpsdata.add(vd);
					if (!c.moveToNext())
						break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return gpsdata;
	}

	public List<GPSInfo> getUnUpGpsInfo(int sindex, int count) {
		ArrayList<GPSInfo> gpsdata = new ArrayList<GPSInfo>();
		String selection = "select * from gpsinfo where isupfile <> 1 order by datetime asc limit ?, ?";

		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(selection, new String[] { String.valueOf(sindex), String.valueOf(count) });
			if (c.moveToFirst()) {
				for (int i = 0; i < c.getCount(); i++) {
					GPSInfo vd = new GPSInfo();
					vd._id = c.getInt(c.getColumnIndex("_id"));
					vd.datetime = c.getString(c.getColumnIndex("datetime"));
					vd.isupfile = c.getInt(c.getColumnIndex("isupfile"));
					vd.altitude = c.getString(c.getColumnIndex("altitude"));
					vd.bearing = c.getString(c.getColumnIndex("bearing"));
					vd.latitude = c.getString(c.getColumnIndex("latitude"));
					vd.longitude = c.getString(c.getColumnIndex("longitude"));
					vd.provider = c.getString(c.getColumnIndex("provider"));
					vd.jobs = c.getString(c.getColumnIndex("jobs"));
					gpsdata.add(vd);
					if (!c.moveToNext())
						break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return gpsdata;
	}

	public void getTmsg(int index, int count, int type, Handler mHandler) {
		List<TextMessage> tmsgs = new ArrayList<TextMessage>();
		String select = "select * from textmsg order by datetime desc limit ?, ?";
		Cursor cursor = null;
		if (index == 1) {
			index = 0;
		} else {
			index = (index - 1) * count;
			count = count * index;
		}
		try {
			cursor = sqliteDatabase.rawQuery(select, new String[] { String.valueOf(index), String.valueOf(count) });
			if (cursor.moveToFirst()) {
				for (int i = 0; i < cursor.getCount(); i++) {
					TextMessage tmsg = new TextMessage();
					tmsg._id = cursor.getInt(cursor.getColumnIndex("_id"));
					tmsg.datetime = cursor.getString(cursor.getColumnIndex("datetime"));
					tmsg.isread = cursor.getInt(cursor.getColumnIndex("isread"));
					tmsg.text = cursor.getString(cursor.getColumnIndex("text"));
					tmsgs.add(tmsg);
					if (!cursor.moveToNext())
						break;
				}
			}
			mHandler.sendMessage(createMsg(ConstantStatus.SREACH_SYSTEM_NITICE_SUCCESS, tmsgs));
		} catch (Exception e) {
			e.printStackTrace();
			mHandler.sendMessage(createMsg(ConstantStatus.SREACH_SYSTEM_NITICE_FALSE, null));
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}
	
	// 获取最近一条消息的类型
	public int getLastestSysMessageType() {
		int type = -1;
		String selection = "select * from sysmsg order by datetime desc";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(selection, null);
			if (c.moveToFirst())
				type = c.getInt(c.getColumnIndex("msgtype"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return type;
	}
	
	// 获取最近一条消息


	public SystemMessage getLastestSysMessage() {
		SystemMessage smsg = null;
		String selection = "select * from sysmsg order by datetime desc";
		Cursor cursor = null;
		try {
			cursor = sqliteDatabase.rawQuery(selection, null);
			if (cursor.moveToFirst()){
				smsg = new SystemMessage();
				smsg._id = cursor.getInt(cursor.getColumnIndex("_id"));
				smsg.datetime = cursor.getString(cursor.getColumnIndex("datetime"));
				smsg.isread = cursor.getInt(cursor.getColumnIndex("isread"));
				smsg.msgtype = cursor.getInt(cursor.getColumnIndex("msgtype"));
				smsg.text = cursor.getString(cursor.getColumnIndex("text"));
				smsg.isSigned = cursor.getInt(cursor.getColumnIndex("issign"));
				smsg.isAck = cursor.getInt(cursor.getColumnIndex("isack"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		return smsg;
	}

	public int getSmsgCount(int type) {
		int res = 0;
		Cursor cursor = null;
		try {
			String select = "select * from sysmsg where msgtype = ? order by datetime";
			cursor = sqliteDatabase.rawQuery(select, new String[] { String.valueOf(type) });
			if (cursor.moveToFirst()) {
				res = cursor.getCount();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return res;
	}	

	public int getSmsgNoReadCount(int type) {
		int res = 0;
		Cursor cursor = null;
		try {
			String select = "select * from sysmsg where msgtype = ? and isread = 0 ";
			cursor = sqliteDatabase.rawQuery(select, new String[] { String.valueOf(type)});
			if (cursor.moveToFirst()) {
				res = cursor.getCount();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return res;
	}
	
	public void getSmsg(int index, int count, int type, Handler mHandler) {
		List<SystemMessage> smsgs = new ArrayList<SystemMessage>();
		Cursor cursor = null;
		if (index == 1) {
			index = 0;
		} else {
			index = (index - 1) * count;
			count = count * index;
		}
		try {
			String select = "select * from sysmsg where msgtype = ? order by datetime desc limit ?, ?";
			cursor = sqliteDatabase.rawQuery(select, new String[] { String.valueOf(type), String.valueOf(index), String.valueOf(count) });
			if (cursor.moveToFirst()) {
				for (int i = 0; i < cursor.getCount(); i++) {
					SystemMessage smsg = new SystemMessage();
					smsg._id = cursor.getInt(cursor.getColumnIndex("_id"));
					smsg.datetime = cursor.getString(cursor.getColumnIndex("datetime"));
					smsg.isread = cursor.getInt(cursor.getColumnIndex("isread"));
					smsg.msgtype = cursor.getInt(cursor.getColumnIndex("msgtype"));
					smsg.text = cursor.getString(cursor.getColumnIndex("text"));
					smsg.isSigned = cursor.getInt(cursor.getColumnIndex("issign"));
					smsg.isAck = cursor.getInt(cursor.getColumnIndex("isack"));
					smsgs.add(smsg);
					if (!cursor.moveToNext())
						break;
				}
			}
			mHandler.sendMessage(createMsg(ConstantStatus.SREACH_SYSTEM_MESSAGE_SUCCESS, smsgs));
		} catch (Exception e) {
			e.printStackTrace();
			mHandler.sendMessage(createMsg(ConstantStatus.SREACH_SYSTEM_MESSAGE_FALSE, null));
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public int getTmsgCount() {
		String select = "select count(*) from textmsg";
		int res = 0;
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(select, null);
			if (c.moveToFirst()) {
				res = c.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return res;
	}

	public int getSmsgCount() {
		String select = "select count(*) from sysmsg";
		int res = 0;
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(select, null);
			if (c.moveToFirst()) {
				res = c.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return res;
	}

	public void getViodate(int sindex, int count, Handler handler) {
		ArrayList<VioData> viodata = new ArrayList<VioData>();
		if (sindex == 1) {
			sindex = 0;
		} else {
			sindex = (sindex - 1) * count;
			count = count * sindex;
		}
		Cursor cursor = null;
		try {
			String selection = "select * from viodata order by datetime desc limit ?, ?";
			cursor = sqliteDatabase.rawQuery(selection, new String[] { String.valueOf(sindex), String.valueOf(count) });
			while (cursor.moveToNext()) {
				VioData vd = new VioData();
				vd._id = cursor.getInt(cursor.getColumnIndex("_id"));
				vd.datetime = cursor.getString(cursor.getColumnIndex("datetime"));
				vd.publishCode = cursor.getString(cursor.getColumnIndex("publishCode"));
				vd.issure = cursor.getInt(cursor.getColumnIndex("issure"));
				vd.isupfile = cursor.getInt(cursor.getColumnIndex("isupfile"));
				vd.picnum = cursor.getInt(cursor.getColumnIndex("picnum"));
				vd.picurl0 = cursor.getString(cursor.getColumnIndex("picurl0"));
				vd.picurl1 = cursor.getString(cursor.getColumnIndex("picurl1"));
				vd.picurl2 = cursor.getString(cursor.getColumnIndex("picurl2"));
				vd.picurl3 = cursor.getString(cursor.getColumnIndex("picurl3"));
				vd.platenum = cursor.getString(cursor.getColumnIndex("platenum"));
				vd.carColor = cursor.getString(cursor.getColumnIndex("carcolor"));
				vd.carColorCode = cursor.getString(cursor.getColumnIndex("carcolorcode"));
				vd.plateColor = cursor.getString(cursor.getColumnIndex("platecolor"));
				vd.plateColorCode = cursor.getString(cursor.getColumnIndex("platecolorcode"));
				vd.platetype = cursor.getString(cursor.getColumnIndex("platetype"));
				vd.roadname = cursor.getString(cursor.getColumnIndex("roadname"));
				vd.roadlocation = cursor.getString(cursor.getColumnIndex("roadlocation"));
				vd.roaddirect = cursor.getString(cursor.getColumnIndex("roaddirect"));
				vd.user = cursor.getString(cursor.getColumnIndex("user"));
				vd.viocode = cursor.getString(cursor.getColumnIndex("viocode"));
				vd.viotype = cursor.getString(cursor.getColumnIndex("viotype"));
				vd.disposetype = cursor.getInt(cursor.getColumnIndex("disposetype"));
				viodata.add(vd);
			}
			handler.sendMessage(createMsg(ConstantStatus.SREACH_VIODATA_SUCCESS, viodata));
		} catch (Exception e) {
			e.printStackTrace();
			handler.sendMessage(createMsg(ConstantStatus.SREACH_VIODATA_FALSE, null));
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	// 根据号牌号码和号牌种类获取黑名单信息
	public List<BlackList> getInBlackList(String plate, String platetype) {
		List<BlackList> list = new ArrayList<BlackList>();
		Cursor c = null;
		try {
			if (platetype == null) {
				// String selection =
				// "select * from blacklist where platenum = '?'";
				String selection = "select * from blacklist where platenum = '" + plate + "'";
				c = sqliteDatabase.rawQuery(selection, null);
			} else {
				String selection = "select * from blacklist where platenum = '" + plate + "' and platetype = '" + platetype + "'";
				// String selection =
				// "select * from blacklist where platenum = '?' and blacklisttype = '?'";
				c = sqliteDatabase.rawQuery(selection, null);
			}

			// String selection = "select * from blacklist";
			// c = db.rawQuery(selection, null);
			if (c.moveToFirst()) {
				list.clear();
				for (int i = 0; i < c.getCount(); i++) {
					BlackList bl = new BlackList();
					bl._id = c.getInt(c.getColumnIndex("_id"));
					bl.blacklisttype = c.getString(c.getColumnIndex("blacklisttype"));
					bl.blt = c.getString(c.getColumnIndex("blt"));
					bl.brand = c.getString(c.getColumnIndex("brand"));
					bl.carcolor = c.getString(c.getColumnIndex("carcolor"));
					bl.owner = c.getString(c.getColumnIndex("owner"));
					bl.platenum = c.getString(c.getColumnIndex("platenum"));
					bl.platetype = c.getString(c.getColumnIndex("platetype"));
					list.add(bl);
					if (!c.moveToNext())
						break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return list;
	}

	// 根据号牌号码和号牌种类获取白名单信息
	public List<VipCarInfo> getVipCarList(String plate, String platetype) {
		List<VipCarInfo> list = new ArrayList<VipCarInfo>();
		Cursor c = null;
		try {
			if (platetype == null) {
				String selection = "select * from vipcar where platenum = '" + plate + "'";
				c = sqliteDatabase.rawQuery(selection, null);
			} else {
				String selection = "select * from vipcar where platenum = '" + plate + "' and platetype = '" + platetype + "'";
				c = sqliteDatabase.rawQuery(selection, null);
			}
			if (c.moveToFirst()) {
				list.clear();
				for (int i = 0; i < c.getCount(); i++) {
					VipCarInfo vi = new VipCarInfo();
					vi.platenum = c.getString(c.getColumnIndex("platenum"));
					vi.platetype = c.getString(c.getColumnIndex("platetype"));
					list.add(vi);
					if (!c.moveToNext())
						break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return list;
	}

	public void clearGPSData() {
		sqliteDatabase.delete("gpsinfo", "datetime < ?", new String[] { MDate.getGPSLimitTime() });
	}

	public void deleteViodealdataById(String id) {
		sqliteDatabase.delete("fieldpunishdata", "id = ?", new String[] { id });
	}

	public void deleteEnforcedataById(String id) {
		sqliteDatabase.delete("enforcementdata", "id = ?", new String[] { id });
	}

	public void deleteVioDataById(int id) {
		sqliteDatabase.delete("viodata", "_id = ?", new String[] { String.valueOf(id) });
	}

	public void DeleteTmsg(int id) {
		sqliteDatabase.delete("textmsg", "_id = ?", new String[] { String.valueOf(id) });
	}

	public void DeleteSysmsg(int id) {
		sqliteDatabase.delete("sysmsg", "_id = ?", new String[] { String.valueOf(id) });
	}
	public void DeleteSysmsgByType(int type) {
//		sqliteDatabase.delete("sysmsg", "msgtype = ?", new String[] { String.valueOf(type) });
		sqliteDatabase.delete("sysmsg", "msgtype = ? and issign = ? and isack = ?", new String[] { String.valueOf(type), "1", "1"});
		sqliteDatabase.delete("sysmsg", "msgtype = ? and issign = ? and isack = ?", new String[] { String.valueOf(type), "1", "-1"});
	}

	public int getUnUpFieldPunishDataCount() {
		int res = -1;
		String select = "select * from fieldpunishdata where isupfile = 0";// 0
		Cursor cursor = null;
		try {
			cursor = sqliteDatabase.rawQuery(select, null);
			res = cursor.getCount();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		return res;
	}

	public int getZhifaDataCount() {
		int res = -1;
		String select = "select * from taizhangdata";// 0
		Cursor cursor = null;
		try {
			cursor = sqliteDatabase.rawQuery(select, null);
			res = cursor.getCount();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		return res;
	}

	public int getUnUpLiabilityCount() {
		int res = -1;
		String select = "select * from liabilitydata where isupfile <> 1";
		Cursor cursor = null;
		try {
			cursor = sqliteDatabase.rawQuery(select, null);
			res = cursor.getCount();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		return res;
	}

	public List<FieldPunishData> getUnUpFieldPunishData(int index, int count) {
		List<FieldPunishData> list = new ArrayList<FieldPunishData>();
		String select = "select * from fieldpunishdata where isupfile = 0 limit ?, ?";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(select, new String[] { index + "", count + "" });

			if (c.moveToFirst()) {
				for (int i = 0; i < c.getCount(); i++) {
					FieldPunishData data = new FieldPunishData();
					data._id = c.getInt(c.getColumnIndex("_id"));
					data.id = c.getString(c.getColumnIndex("id"));
					data.isupfile = c.getInt(c.getColumnIndex("isupfile"));
					data.name = c.getString(c.getColumnIndex("name"));
					data.phone = c.getString(c.getColumnIndex("phone"));
					data.driverlic = c.getString(c.getColumnIndex("driverlic"));
					data.recordid = c.getString(c.getColumnIndex("recordid"));
					data.licenseoffice = c.getString(c.getColumnIndex("licenseoffice"));
					data.licensetype = c.getString(c.getColumnIndex("licensetype"));
					data.plate = c.getString(c.getColumnIndex("plate"));
					data.platetype = c.getString(c.getColumnIndex("platetype"));
					data.plateoffice = c.getString(c.getColumnIndex("plateoffice"));
					data.owner = c.getString(c.getColumnIndex("owner"));
					data.location = c.getString(c.getColumnIndex("location"));
					data.roadlocation = c.getString(c.getColumnIndex("roadlocation"));
					data.roaddirect = c.getString(c.getColumnIndex("roaddirect"));
					data.content = c.getString(c.getColumnIndex("content"));
					data.datetime = c.getString(c.getColumnIndex("datetime"));
					data.action = c.getString(c.getColumnIndex("action"));
					data.money = c.getInt(c.getColumnIndex("money"));
					data.value = c.getInt(c.getColumnIndex("value"));
					data.picnum = c.getInt(c.getColumnIndex("picnum"));
					data.vioid = c.getString(c.getColumnIndex("vioid"));
					data.pic1 = c.getString(c.getColumnIndex("pic1"));
					data.pic2 = c.getString(c.getColumnIndex("pic2"));
					data.pic3 = c.getString(c.getColumnIndex("pic3"));
					data.pic4 = c.getString(c.getColumnIndex("pic4"));
					list.add(data);
					if (!c.moveToNext())
						break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return list;
	}

	public List<FieldPunishData> getFieldPunishData(int index, int count, Handler handler) {
		List<FieldPunishData> fieldPunishDatas = new ArrayList<FieldPunishData>();
		if (index == 1) {
			index = 0;
		} else {
			index = (index - 1) * count;
			count = count * index;
		}
		String select = "select * from fieldpunishdata order by datetime desc limit ?, ?";
		Cursor cursor = null;
		try {
			cursor = sqliteDatabase.rawQuery(select, new String[] { index + "", count + "" });

			if (cursor.moveToFirst()) {
				for (int i = 0; i < cursor.getCount(); i++) {
					FieldPunishData data = new FieldPunishData();
					data._id = cursor.getInt(cursor.getColumnIndex("_id"));
					data.id = cursor.getString(cursor.getColumnIndex("id"));
					data.isupfile = cursor.getInt(cursor.getColumnIndex("isupfile"));
					data.name = cursor.getString(cursor.getColumnIndex("name"));
					data.phone = cursor.getString(cursor.getColumnIndex("phone"));
					data.driverlic = cursor.getString(cursor.getColumnIndex("driverlic"));
					data.recordid = cursor.getString(cursor.getColumnIndex("recordid"));
					data.licenseoffice = cursor.getString(cursor.getColumnIndex("licenseoffice"));
					data.licensetype = cursor.getString(cursor.getColumnIndex("licensetype"));
					data.plate = cursor.getString(cursor.getColumnIndex("plate"));
					data.platetype = cursor.getString(cursor.getColumnIndex("platetype"));
					data.plateoffice = cursor.getString(cursor.getColumnIndex("plateoffice"));
					data.owner = cursor.getString(cursor.getColumnIndex("owner"));
					data.location = cursor.getString(cursor.getColumnIndex("location"));
					data.roadlocation = cursor.getString(cursor.getColumnIndex("roadlocation"));
					data.roaddirect = cursor.getString(cursor.getColumnIndex("roaddirect"));
					data.content = cursor.getString(cursor.getColumnIndex("content"));
					data.datetime = cursor.getString(cursor.getColumnIndex("datetime"));
					data.action = cursor.getString(cursor.getColumnIndex("action"));
					data.money = cursor.getInt(cursor.getColumnIndex("money"));
					data.value = cursor.getInt(cursor.getColumnIndex("value"));
					data.picnum = cursor.getInt(cursor.getColumnIndex("picnum"));
					data.vioid = cursor.getString(cursor.getColumnIndex("vioid"));
					data.pic1 = cursor.getString(cursor.getColumnIndex("pic1"));
					data.pic2 = cursor.getString(cursor.getColumnIndex("pic2"));
					data.pic3 = cursor.getString(cursor.getColumnIndex("pic3"));
					data.pic4 = cursor.getString(cursor.getColumnIndex("pic4"));
					fieldPunishDatas.add(data);
					if (!cursor.moveToNext())
						break;
				}
			}
			handler.sendMessage(createMsg(ConstantStatus.SREACH_DETAIL_SUCCESS, fieldPunishDatas));
		} catch (Exception e) {
			e.printStackTrace();
			handler.sendMessage(createMsg(ConstantStatus.SREACH_DETAIL_FALSE, null));
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return fieldPunishDatas;
	}

	public List<LawCheckInfo> getUnUpLawCheckInfo(int index, int count) {
		List<LawCheckInfo> list = new ArrayList<LawCheckInfo>();
		String select = "select * from taizhangdata where isupfile = 0 limit ?, ?";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(select, new String[] { index + "", count + "" });

			if (c.moveToFirst()) {
				for (int i = 0; i < c.getCount(); i++) {
					LawCheckInfo data = new LawCheckInfo();
					data._id = c.getInt(c.getColumnIndex("_id"));
					data.actual = c.getString(c.getColumnIndex("actual"));
					data.isupfile = c.getInt(c.getColumnIndex("isupfile"));
					data.cartype = c.getString(c.getColumnIndex("platetype"));
					data.phone = c.getString(c.getColumnIndex("phone"));
					data.checktype = c.getString(c.getColumnIndex("checktype"));
					data.drivername = c.getString(c.getColumnIndex("name"));
					data.datetime = c.getString(c.getColumnIndex("datetime"));
					data.drivernum = c.getString(c.getColumnIndex("driverlic"));
					data.handler = c.getString(c.getColumnIndex("handler"));
					data.handleway = c.getString(c.getColumnIndex("handleway"));
					data.isupfile = c.getInt(c.getColumnIndex("isupfile"));
					data.owner = c.getString(c.getColumnIndex("owner"));
					data.memo = c.getString(c.getColumnIndex("memo"));
					data.must = c.getString(c.getColumnIndex("must"));
					data.pic1 = c.getString(c.getColumnIndex("pic1"));
					data.pic2 = c.getString(c.getColumnIndex("pic2"));
					data.pic3 = c.getString(c.getColumnIndex("pic3"));
					data.pic4 = c.getString(c.getColumnIndex("pic4"));
					data.platenum = c.getString(c.getColumnIndex("plate"));
					data.road = c.getString(c.getColumnIndex("road"));
					data.roadlocation = c.getString(c.getColumnIndex("roadlocation"));
					data.roaddirect = c.getString(c.getColumnIndex("roaddirect"));
					data.viotype = c.getString(c.getColumnIndex("viotype"));
					list.add(data);
					if (!c.moveToNext())
						break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return list;
	}

	public void getLawCheckInfo(int index, int count, Handler handler) {
		List<LawCheckInfo> lawCheckInfos = new ArrayList<LawCheckInfo>();
		if (index == 1) {
			index = 0;
		} else {
			index = (index - 1) * count;
			count = count * index;
		}
		String select = "select * from taizhangdata order by datetime desc limit ?, ?";
		Cursor cursor = null;
		try {
			cursor = sqliteDatabase.rawQuery(select, new String[] { index + "", count + "" });

			if (cursor.moveToFirst()) {
				for (int i = 0; i < cursor.getCount(); i++) {
					LawCheckInfo data = new LawCheckInfo();
					data._id = cursor.getInt(cursor.getColumnIndex("_id"));
					data.actual = cursor.getString(cursor.getColumnIndex("actual"));
					data.isupfile = cursor.getInt(cursor.getColumnIndex("isupfile"));
					data.cartype = cursor.getString(cursor.getColumnIndex("platetype"));
					data.phone = cursor.getString(cursor.getColumnIndex("phone"));
					data.checktype = cursor.getString(cursor.getColumnIndex("checktype"));
					data.drivername = cursor.getString(cursor.getColumnIndex("name"));
					data.datetime = cursor.getString(cursor.getColumnIndex("datetime"));
					data.drivernum = cursor.getString(cursor.getColumnIndex("driverlic"));
					data.handler = cursor.getString(cursor.getColumnIndex("handler"));
					data.handleway = cursor.getString(cursor.getColumnIndex("handleway"));
					data.isupfile = cursor.getInt(cursor.getColumnIndex("isupfile"));
					data.owner = cursor.getString(cursor.getColumnIndex("owner"));
					data.memo = cursor.getString(cursor.getColumnIndex("memo"));
					data.must = cursor.getString(cursor.getColumnIndex("must"));
					data.pic1 = cursor.getString(cursor.getColumnIndex("pic1"));
					data.pic2 = cursor.getString(cursor.getColumnIndex("pic2"));
					data.pic3 = cursor.getString(cursor.getColumnIndex("pic3"));
					data.pic4 = cursor.getString(cursor.getColumnIndex("pic4"));
					data.platenum = cursor.getString(cursor.getColumnIndex("plate"));
					data.road = cursor.getString(cursor.getColumnIndex("road"));
					data.roadlocation = cursor.getString(cursor.getColumnIndex("roadlocation"));
					data.roaddirect = cursor.getString(cursor.getColumnIndex("roaddirect"));
					data.viotype = cursor.getString(cursor.getColumnIndex("viotype"));
					lawCheckInfos.add(data);
					if (!cursor.moveToNext())
						break;
				}
			}
			handler.sendMessage(createMsg(ConstantStatus.SREACH_DETAIL_SUCCESS, lawCheckInfos));
		} catch (Exception e) {
			e.printStackTrace();
			handler.sendMessage(createMsg(ConstantStatus.SREACH_DETAIL_FALSE, null));
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public void fieldPunishDataUp(String id) {
		ContentValues cv = new ContentValues();
		cv.put("isupfile", 1);
		sqliteDatabase.update("fieldpunishdata", cv, "vioid = ?", new String[] { id });
		cv.clear();
	}

	public void enforcementDataUp(String id) {
		ContentValues cv = new ContentValues();
		cv.put("isupfile", 1);
		sqliteDatabase.update("enforcementdata", cv, "vioid = ?", new String[] { id + "" });
		cv.clear();
	}

	public void getEnforcementData(int index, int count, Handler handler) {
		List<EnforcementData> enforcementDatas = new ArrayList<EnforcementData>();
		String select = "select * from enforcementdata order by datetime desc limit ?, ?";
		Cursor cursor = null;
		if (index == 1) {
			index = 0;
		} else {
			index = (index - 1) * count;
			count = count * index;
		}
		try {
			cursor = sqliteDatabase.rawQuery(select, new String[] { index + "", count + "" });
			if (cursor.moveToFirst()) {
				for (int i = 0; i < cursor.getCount(); i++) {
					EnforcementData data = new EnforcementData();
					data._id = cursor.getInt(cursor.getColumnIndex("_id"));
					data.id = cursor.getString(cursor.getColumnIndex("id"));
					data.isupfile = cursor.getInt(cursor.getColumnIndex("isupfile"));
					data.name = cursor.getString(cursor.getColumnIndex("name"));
					data.phone = cursor.getString(cursor.getColumnIndex("phone"));
					data.driverlic = cursor.getString(cursor.getColumnIndex("driverlic"));
					data.recordid = cursor.getString(cursor.getColumnIndex("recordid"));
					data.licenseoffice = cursor.getString(cursor.getColumnIndex("licenseoffice"));
					data.licensetype = cursor.getString(cursor.getColumnIndex("licensetype"));
					data.plate = cursor.getString(cursor.getColumnIndex("plate"));
					data.platetype = cursor.getString(cursor.getColumnIndex("platetype"));
					data.plateoffice = cursor.getString(cursor.getColumnIndex("plateoffice"));
					data.location = cursor.getString(cursor.getColumnIndex("location"));
					data.roadlocation = cursor.getString(cursor.getColumnIndex("roadlocation"));
					data.roaddirect = cursor.getString(cursor.getColumnIndex("roaddirect"));
					data.viotype = cursor.getString(cursor.getColumnIndex("viotype"));
					data.datetime = cursor.getString(cursor.getColumnIndex("datetime"));
					data.action = cursor.getString(cursor.getColumnIndex("action"));
					data.picnum = cursor.getInt(cursor.getColumnIndex("picnum"));
					data.pic1 = cursor.getString(cursor.getColumnIndex("pic1"));
					data.pic2 = cursor.getString(cursor.getColumnIndex("pic2"));
					data.pic3 = cursor.getString(cursor.getColumnIndex("pic3"));
					data.pic4 = cursor.getString(cursor.getColumnIndex("pic4"));
					data.vioid = cursor.getString(cursor.getColumnIndex("vioid"));
					enforcementDatas.add(data);
					if (!cursor.moveToNext())
						break;
				}
			}
			handler.sendMessage(createMsg(ConstantStatus.SREACH_DETAIL_SUCCESS, enforcementDatas));
		} catch (Exception e) {
			e.printStackTrace();
			handler.sendMessage(createMsg(ConstantStatus.SREACH_DETAIL_FALSE, null));
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public List<EnforcementData> getUnUpEnforcementData(int index, int count) {
		List<EnforcementData> list = new ArrayList<EnforcementData>();
		String select = "select * from enforcementdata  where isupfile = 0 limit ?, ? ";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(select, new String[] { index + "", count + "" });
			if (c.moveToFirst()) {
				for (int i = 0; i < c.getCount(); i++) {
					EnforcementData data = new EnforcementData();
					data._id = c.getInt(c.getColumnIndex("_id"));
					data.id = c.getString(c.getColumnIndex("id"));
					data.name = c.getString(c.getColumnIndex("name"));
					data.isupfile = c.getInt(c.getColumnIndex("isupfile"));
					data.phone = c.getString(c.getColumnIndex("phone"));
					data.driverlic = c.getString(c.getColumnIndex("driverlic"));
					data.recordid = c.getString(c.getColumnIndex("recordid"));
					data.licenseoffice = c.getString(c.getColumnIndex("licenseoffice"));
					data.licensetype = c.getString(c.getColumnIndex("licensetype"));
					data.plate = c.getString(c.getColumnIndex("plate"));
					data.platetype = c.getString(c.getColumnIndex("platetype"));
					data.plateoffice = c.getString(c.getColumnIndex("plateoffice"));
					data.location = c.getString(c.getColumnIndex("location"));
					data.roadlocation = c.getString(c.getColumnIndex("roadlocation"));
					data.roaddirect = c.getString(c.getColumnIndex("roaddirect"));
					data.viotype = c.getString(c.getColumnIndex("viotype"));
					data.datetime = c.getString(c.getColumnIndex("datetime"));
					data.action = c.getString(c.getColumnIndex("action"));
					data.picnum = c.getInt(c.getColumnIndex("picnum"));
					data.pic1 = c.getString(c.getColumnIndex("pic1"));
					data.pic2 = c.getString(c.getColumnIndex("pic2"));
					data.pic3 = c.getString(c.getColumnIndex("pic3"));
					data.pic4 = c.getString(c.getColumnIndex("pic4"));
					data.vioid = c.getString(c.getColumnIndex("vioid"));
					data.code = c.getString(c.getColumnIndex("code"));
					list.add(data);
					if (!c.moveToNext())
						break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return list;
	}

	public EnforcementData getEnforcementData(String id) {
		String select = "select * from enforcementdata where vioid = ?";
		Cursor c = null;
		EnforcementData data = new EnforcementData();
		try {
			c = sqliteDatabase.rawQuery(select, new String[] { id + "" });
			if (c.moveToFirst()) {
				for (int i = 0; i < c.getCount(); i++) {
					data._id = c.getInt(c.getColumnIndex("_id"));
					data.id = c.getString(c.getColumnIndex("id"));
					data.name = c.getString(c.getColumnIndex("name"));
					data.isupfile = c.getInt(c.getColumnIndex("isupfile"));
					data.phone = c.getString(c.getColumnIndex("phone"));
					data.driverlic = c.getString(c.getColumnIndex("driverlic"));
					data.recordid = c.getString(c.getColumnIndex("recordid"));
					data.licenseoffice = c.getString(c.getColumnIndex("licenseoffice"));
					data.licensetype = c.getString(c.getColumnIndex("licensetype"));
					data.plate = c.getString(c.getColumnIndex("plate"));
					data.platetype = c.getString(c.getColumnIndex("platetype"));
					data.plateoffice = c.getString(c.getColumnIndex("plateoffice"));
					data.location = c.getString(c.getColumnIndex("location"));
					data.roadlocation = c.getString(c.getColumnIndex("roadlocation"));
					data.roaddirect = c.getString(c.getColumnIndex("roaddirect"));
					data.content = c.getString(c.getColumnIndex("content"));
					data.datetime = c.getString(c.getColumnIndex("datetime"));
					data.action = c.getString(c.getColumnIndex("action"));
					data.picnum = c.getInt(c.getColumnIndex("picnum"));
					data.pic1 = c.getString(c.getColumnIndex("pic1"));
					data.pic2 = c.getString(c.getColumnIndex("pic2"));
					data.pic3 = c.getString(c.getColumnIndex("pic3"));
					data.pic4 = c.getString(c.getColumnIndex("pic4"));
					data.code = c.getString(c.getColumnIndex("code"));
					data.viotype = c.getString(c.getColumnIndex("viotype"));
					data.vioid = c.getString(c.getColumnIndex("vioid"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return data;
	}

	public LawCheckInfo getZhifajiancha(int id) {
		LawCheckInfo data = new LawCheckInfo();
		String select = "select * from taizhangdata where _id = ?";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(select, new String[] { id + "" });
			if (c.moveToFirst()) {
				for (int i = 0; i < c.getCount(); i++) {

					data._id = c.getInt(c.getColumnIndex("_id"));
					data.actual = c.getString(c.getColumnIndex("actual"));
					data.cartype = c.getString(c.getColumnIndex("platetype"));
					data.phone = c.getString(c.getColumnIndex("phone"));
					data.checktype = c.getString(c.getColumnIndex("checktype"));
					data.drivername = c.getString(c.getColumnIndex("name"));
					data.datetime = c.getString(c.getColumnIndex("datetime"));
					data.drivernum = c.getString(c.getColumnIndex("driverlic"));
					data.handler = c.getString(c.getColumnIndex("handler"));
					data.handleway = c.getString(c.getColumnIndex("handleway"));
					data.isupfile = c.getInt(c.getColumnIndex("isupfile"));
					data.owner = c.getString(c.getColumnIndex("owner"));
					data.memo = c.getString(c.getColumnIndex("memo"));
					data.must = c.getString(c.getColumnIndex("must"));
					data.pic1 = c.getString(c.getColumnIndex("pic1"));
					data.pic2 = c.getString(c.getColumnIndex("pic2"));
					data.pic3 = c.getString(c.getColumnIndex("pic3"));
					data.pic4 = c.getString(c.getColumnIndex("pic4"));
					data.platenum = c.getString(c.getColumnIndex("plate"));
					data.road = c.getString(c.getColumnIndex("road"));
					data.roadlocation = c.getString(c.getColumnIndex("roadlocation"));
					data.roaddirect = c.getString(c.getColumnIndex("roaddirect"));
					data.viotype = c.getString(c.getColumnIndex("viotype"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return data;
	}

	public AccidentInfo getAccidentInfo(int id) {
		AccidentInfo data = new AccidentInfo();
		String select = "select * from accident where _id = ?";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(select, new String[] { id + "" });
			if (c.moveToFirst()) {
				for (int i = 0; i < c.getCount(); i++) {
					data._id = c.getInt(c.getColumnIndex("_id"));
					data.id = c.getString(c.getColumnIndex("id"));
					data.carnum = c.getInt(c.getColumnIndex("carnum"));
					data.death = c.getInt(c.getColumnIndex("death"));
					data.hurt = c.getInt(c.getColumnIndex("hurt"));
					data.isread = c.getInt(c.getColumnIndex("isread"));
					data.isupfile = c.getInt(c.getColumnIndex("isupfile"));
					data.memo = c.getString(c.getColumnIndex("memo"));
					data.infolist = c.getString(c.getColumnIndex("occurtime"));
					data.progress = c.getInt(c.getColumnIndex("progress"));
					data.realtime = c.getString(c.getColumnIndex("realtime"));
					data.roadname = c.getString(c.getColumnIndex("roadname"));
					data.roadlocation = c.getString(c.getColumnIndex("roadlocation"));
					data.roaddirect = c.getString(c.getColumnIndex("roaddirect"));
					data.user = c.getString(c.getColumnIndex("user"));
					data.caller = c.getString(c.getColumnIndex("caller"));
					data.acctype = c.getInt(c.getColumnIndex("acctype"));
					data.joinlist = c.getString(c.getColumnIndex("joinlist"));
					data.phone = c.getString(c.getColumnIndex("phone"));
					data.weather = c.getInt(c.getColumnIndex("weather"));
					data.pic1 = c.getString(c.getColumnIndex("pic1"));
					data.pic2 = c.getString(c.getColumnIndex("pic2"));
					data.pic3 = c.getString(c.getColumnIndex("pic3"));
					data.pic4 = c.getString(c.getColumnIndex("pic4"));
					data.pic5 = c.getString(c.getColumnIndex("pic5"));
					data.pic6 = c.getString(c.getColumnIndex("pic6"));
					data.pic7 = c.getString(c.getColumnIndex("pic7"));
					data.pic8 = c.getString(c.getColumnIndex("pic8"));
					data.pic9 = c.getString(c.getColumnIndex("pic9"));
					data.pic10 = c.getString(c.getColumnIndex("pic10"));
					data.picsole = c.getString(c.getColumnIndex("sole"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return data;
	}

	public FieldPunishData getFieldPunishData(int id) {
		FieldPunishData data = new FieldPunishData();
		String select = "select * from fieldpunishdata where _id = ?";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(select, new String[] { id + "" });
			if (c.moveToFirst()) {
				for (int i = 0; i < c.getCount(); i++) {
					data._id = c.getInt(c.getColumnIndex("_id"));
					data.id = c.getString(c.getColumnIndex("id"));
					data.name = c.getString(c.getColumnIndex("name"));
					data.phone = c.getString(c.getColumnIndex("phone"));
					data.driverlic = c.getString(c.getColumnIndex("driverlic"));
					data.recordid = c.getString(c.getColumnIndex("recordid"));
					data.licenseoffice = c.getString(c.getColumnIndex("licenseoffice"));
					data.licensetype = c.getString(c.getColumnIndex("licensetype"));
					data.plate = c.getString(c.getColumnIndex("plate"));
					data.platetype = c.getString(c.getColumnIndex("platetype"));
					data.plateoffice = c.getString(c.getColumnIndex("plateoffice"));
					data.location = c.getString(c.getColumnIndex("location"));
					data.roadlocation = c.getString(c.getColumnIndex("roadlocation"));
					data.roaddirect = c.getString(c.getColumnIndex("roaddirect"));
					data.content = c.getString(c.getColumnIndex("content"));
					data.datetime = c.getString(c.getColumnIndex("datetime"));
					data.pic1 = c.getString(c.getColumnIndex("pic1"));
					data.pic2 = c.getString(c.getColumnIndex("pic2"));
					data.pic3 = c.getString(c.getColumnIndex("pic3"));
					data.pic4 = c.getString(c.getColumnIndex("pic4"));
					data.owner = c.getString(c.getColumnIndex("owner"));
					data.picnum = c.getInt(c.getColumnIndex("picnum"));
					data.action = c.getString(c.getColumnIndex("action"));
					data.money = c.getInt(c.getColumnIndex("money"));
					data.value = c.getInt(c.getColumnIndex("value"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return data;
	}

	public boolean roadExists() {
		String select = "select * from road order by _id";
		Cursor c = sqliteDatabase.rawQuery(select, null);
		if (c != null && c.getCount() > 0) {
			c.close();
			return true;
		} else {
			c.close();
			return false;
		}
	}

	public boolean typeExists() {
		String select = "select * from dutytype order by _id";
		Cursor c = sqliteDatabase.rawQuery(select, null);
		if (c != null && c.getCount() > 0) {
			c.close();
			return true;
		} else {
			c.close();
			return false;
		}
	}

	public boolean vioTypeExists() {
		String select = "select * from viotype order by _id";
		Cursor c = sqliteDatabase.rawQuery(select, null);
		if (c != null && c.getCount() > 0) {
			c.close();
			return true;
		} else {
			c.close();
			return false;
		}
	}

	public boolean addressBookExists() {
		String select = "select * from addressbook order by _id";
		Cursor c = sqliteDatabase.rawQuery(select, null);
		if (c != null && c.getCount() > 0) {
			c.close();
			return true;
		} else {
			c.close();
			return false;
		}
	}

	public boolean weijinExists() {
		String select = "select * from weijininfo order by _id";
		Cursor c = sqliteDatabase.rawQuery(select, null);
		if (c != null && c.getCount() > 0) {
			c.close();
			return true;
		} else {
			c.close();
			return false;
		}
	}

	public List<AddressBook> getAddressBookByKeywords(String keywords) {
		ArrayList<AddressBook> addressbooks = new ArrayList<AddressBook>();
		String selection = "select * from addressbook where id like '%' || ? || '%' or name like '%' || ? || '%' or phone like '%' || ? || '%' or deptmentid like '%' || ? || '%' or deptmentname like '%' || ? || '%' or remark like '%' || ? || '%' or post like '%' || ? || '%'";
		Cursor c = sqliteDatabase.rawQuery(selection, new String[] { keywords, keywords, keywords, keywords, keywords, keywords });
		if (c.moveToFirst()) {
			for (int i = 0; i < c.getCount(); i++) {
				AddressBook ab = new AddressBook();
				ab.id = c.getString(c.getColumnIndex("id"));
				ab.name = c.getString(c.getColumnIndex("name"));
				ab.phone = c.getString(c.getColumnIndex("phone"));
				ab.remark = c.getString(c.getColumnIndex("remark"));
				ab.post = c.getString(c.getColumnIndex("post"));
				ab.deptmentid = c.getString(c.getColumnIndex("deptmentid"));
				ab.deptmentname = c.getString(c.getColumnIndex("deptmentname"));
				addressbooks.add(ab);
				if (!c.moveToNext())
					break;
			}
		}
		c.close();
		return addressbooks;
	}

	public List<String> getWeijinKeyListByKeyWords(String keywords) {
		ArrayList<String> keys = new ArrayList<String>();
		String selection = "select name from weijininfo where name like '%' || ? || '%'";
		Cursor c = sqliteDatabase.rawQuery(selection, new String[] { keywords });
		if (c.moveToFirst()) {
			for (int i = 0; i < c.getCount(); i++) {
				String key = null;
				key = c.getString(c.getColumnIndex("name"));
				keys.add(key);
				if (!c.moveToNext())
					break;
			}
		}
		c.close();
		return keys;
	}

	public List<String> getLawKeyListByKeyWords(String keywords) {
		ArrayList<String> keys = new ArrayList<String>();
		String selection = "select key from lawinfo where key like '%' || ? || '%'";
		Cursor c = sqliteDatabase.rawQuery(selection, new String[] { keywords });
		if (c.moveToFirst()) {
			for (int i = 0; i < c.getCount(); i++) {
				String key = null;
				key = c.getString(c.getColumnIndex("key"));
				keys.add(key);
				if (!c.moveToNext())
					break;
			}
		}
		c.close();
		return keys;
	}

	public WeijinInfo getWeijinInfoByKeywords(String keywords) {
		String selection = "select * from weijininfo where name like '%' || ? || '%' or type like '%' || ? || '%' or lawprovision like '%' || ? || '%' or features like '%' || ? || '%'";
		Cursor c = sqliteDatabase.rawQuery(selection, new String[] { keywords, keywords, keywords, keywords });
		WeijinInfo result = new WeijinInfo();
		if (c.moveToFirst()) {
			for (int i = 0; i < c.getCount(); i++) {
				WeijinInfo wi = new WeijinInfo();
				wi.disposemode = c.getString(c.getColumnIndex("disposemode"));
				wi.features = c.getString(c.getColumnIndex("features"));
				wi.lawProvision = c.getString(c.getColumnIndex("lawprovision"));
				wi.name = c.getString(c.getColumnIndex("name"));
				wi.type = c.getString(c.getColumnIndex("type"));
				wi.uncode = c.getString(c.getColumnIndex("uncode"));
				wi.id = c.getString(c.getColumnIndex("id"));
				result = wi;
				if (!c.moveToNext())
					break;
			}
		}
		c.close();
		return result;
	}

	public void getAddressBooks(String keywords, int currentPage, int PageSize, Handler mHandler) {
		ArrayList<AddressBook> addressbooks = new ArrayList<AddressBook>();
		String sql = "";
		Cursor cursor = null;
		try {
			if (currentPage == 1) {
				currentPage = 0;
			} else {
				currentPage = (currentPage - 1) * PageSize;
				PageSize = PageSize * currentPage;
			}
			if (keywords != null && keywords.length() > 0) {
				sql = "select * from addressbook where name like '%' || ? || '%' or phone like '%' || ? || '%'  or deptmentname like '%' || ? || '%'   order by pinyin  Limit ?,?";
				cursor = sqliteDatabase.rawQuery(sql, new String[] {  keywords, keywords, keywords, String.valueOf(currentPage), String.valueOf(PageSize) });
			} else {
				sql = "select * from addressbook order by pinyin Limit ?,?";
				cursor = sqliteDatabase.rawQuery(sql, new String[] { String.valueOf(currentPage), String.valueOf(PageSize) });
			}
			if (cursor.moveToFirst()) {
				for (int i = 0; i < cursor.getCount(); i++) {
					AddressBook ab = new AddressBook();
					ab.id = cursor.getString(cursor.getColumnIndex("id"));
					ab.name = cursor.getString(cursor.getColumnIndex("name"));
					ab.phone = cursor.getString(cursor.getColumnIndex("phone"));
					ab.post = cursor.getString(cursor.getColumnIndex("post"));
					ab.remark = cursor.getString(cursor.getColumnIndex("remark"));
					ab.deptmentid = cursor.getString(cursor.getColumnIndex("deptmentid"));
					ab.deptmentname = cursor.getString(cursor.getColumnIndex("deptmentname"));
					addressbooks.add(ab);
					if (!cursor.moveToNext())
						break;
				}
			}
			mHandler.sendMessage(createMsg(ConstantStatus.SREACHCONTATSSUCCESS, addressbooks));
		} catch (Exception e) {
			e.printStackTrace();
			mHandler.sendMessage(createMsg(ConstantStatus.SREACHCONTATSFALSE, null));
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public void addRoadName(String name) {
		ContentValues cv = new ContentValues();
		cv.put("name", name);
		sqliteDatabase.insert("road", null, cv);
		cv.clear();
		cv = null;
	}

	public ArrayList<String> queryRoadNames() {
		String select = "select * from road order by _id";
		ArrayList<String> data = new ArrayList<String>();
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(select, null);
			if (c.moveToFirst()) {
				for (int i = 0; i < c.getCount(); i++) {
					data.add(c.getString(c.getColumnIndex("name")));
					if (!c.moveToNext())
						break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return data;
	}

	public List<String> getVioNameListByKeyWords(String keywords) {
		ArrayList<String> keys = new ArrayList<String>();
		String selection = "select simplename from viodic where simplename like '%' || ? || '%' or code like '%' || ? || '%'";
		Cursor c = sqliteDatabase.rawQuery(selection, new String[] { keywords, keywords });
		if (c.moveToFirst()) {
			for (int i = 0; i < c.getCount(); i++) {
				String key = null;
				key = c.getString(c.getColumnIndex("simplename"));
				keys.add(key);
				if (!c.moveToNext())
					break;
			}
		}
		c.close();
		return keys;
	}

	public List<Type> getVioTypeList() {
		ArrayList<Type> keys = new ArrayList<Type>();
		String selection = "select * from viodic order by _id";
		Cursor c = sqliteDatabase.rawQuery(selection, null);
		if (c.moveToFirst()) {
			for (int i = 0; i < c.getCount(); i++) {
				Type vt = new Type();
				vt.code = c.getString(c.getColumnIndex("code"));
				vt.name = c.getString(c.getColumnIndex("name"));
				vt.type = c.getString(c.getColumnIndex("simplename"));
				keys.add(vt);
				if (!c.moveToNext())
					break;
			}
		}
		c.close();
		return keys;
	}

	public List<VioDic> getVioListByKeyWords(String keywords) {
		ArrayList<VioDic> keys = new ArrayList<VioDic>();
		String selection = "select * from viodic where simplename like '%' || ? || '%' or code like '%' || ? || '%'";
		Cursor c = sqliteDatabase.rawQuery(selection, new String[] { keywords, keywords });
		if (c.moveToFirst()) {
			for (int i = 0; i < c.getCount(); i++) {
				VioDic vt = new VioDic();
				vt.code = c.getString(c.getColumnIndex("code"));
				vt.name = c.getString(c.getColumnIndex("name"));
				vt.simpleName = c.getString(c.getColumnIndex("simplename"));
				vt.fineDefault = c.getShort(c.getColumnIndex("finedefault"));
				vt.fineMax = c.getShort(c.getColumnIndex("finemax"));
				vt.fineMin = c.getShort(c.getColumnIndex("finemin"));
				vt.law = c.getString(c.getColumnIndex("law"));
				vt.isshow = c.getString(c.getColumnIndex("isshow"));
				vt.marker = c.getString(c.getColumnIndex("marker"));
				vt.method = c.getString(c.getColumnIndex("method"));
				vt.ordernum = c.getShort(c.getColumnIndex("ordernum"));
				vt.punish = c.getString(c.getColumnIndex("punish"));
				vt.rule = c.getString(c.getColumnIndex("rule"));
				keys.add(vt);
				if (!c.moveToNext())
					break;
			}
		}
		c.close();
		return keys;
	}

	public String getVioCodeByName(String VioName) {
		VioDic vioDic = queryVioTypeByKeywords(VioName);
		return vioDic.code;
	}
	
	public VioDic queryVioTypeByKeywords(String keywords) {
		String selection = "select * from viodic where simplename = ?";
		Cursor c = sqliteDatabase.rawQuery(selection, new String[] { keywords });
		VioDic vt = new VioDic();
		if (c.moveToFirst()) {
			vt.code = c.getString(c.getColumnIndex("code"));
			vt.name = c.getString(c.getColumnIndex("name"));
			vt.simpleName = c.getString(c.getColumnIndex("simplename"));
			vt.fineDefault = c.getShort(c.getColumnIndex("finedefault"));
			vt.fineMax = c.getShort(c.getColumnIndex("finemax"));
			vt.fineMin = c.getShort(c.getColumnIndex("finemin"));
			vt.law = c.getString(c.getColumnIndex("law"));
			vt.isshow = c.getString(c.getColumnIndex("isshow"));
			vt.marker = c.getString(c.getColumnIndex("marker"));
			vt.method = c.getString(c.getColumnIndex("method"));
			vt.ordernum = c.getShort(c.getColumnIndex("ordernum"));
			vt.punish = c.getString(c.getColumnIndex("punish"));
			vt.rule = c.getString(c.getColumnIndex("rule"));
		}
		c.close();
		return vt;
	}

	public VioDic queryVioTypeByKeyCode(String keywords) {
		String selection = "select * from viodic where code = ?";
		Cursor c = sqliteDatabase.rawQuery(selection, new String[] { keywords });
		VioDic vt = new VioDic();
		if (c.moveToFirst()) {
			vt.code = c.getString(c.getColumnIndex("code"));
			vt.name = c.getString(c.getColumnIndex("name"));
			vt.simpleName = c.getString(c.getColumnIndex("simplename"));
			vt.fineDefault = c.getShort(c.getColumnIndex("finedefault"));
			vt.fineMax = c.getShort(c.getColumnIndex("finemax"));
			vt.fineMin = c.getShort(c.getColumnIndex("finemin"));
			vt.law = c.getString(c.getColumnIndex("law"));
			vt.isshow = c.getString(c.getColumnIndex("isshow"));
			vt.marker = c.getString(c.getColumnIndex("marker"));
			vt.method = c.getString(c.getColumnIndex("method"));
			vt.ordernum = c.getShort(c.getColumnIndex("ordernum"));
			vt.punish = c.getString(c.getColumnIndex("punish"));
			vt.rule = c.getString(c.getColumnIndex("rule"));
		}
		c.close();
		return vt;
	}

	public String getVioNameByCode(String VioCode) {
		String selection = "select * from viodic where code = ?";
		Cursor c = sqliteDatabase.rawQuery(selection, new String[] { VioCode });
		String simpleName = null;
		if (c.moveToFirst()) {
			simpleName = c.getString(c.getColumnIndex("simplename"));
		}
		c.close();
		return simpleName;
	}

	public VioType getVioTypeByName(String viotypename) {
		String selection = "select * from viotype where simplename = ?";
		Cursor c = sqliteDatabase.rawQuery(selection, new String[] { viotypename });
		VioType vt = new VioType();
		if (c.moveToFirst()) {
			vt.code = c.getString(c.getColumnIndex("code"));
			vt.name = c.getString(c.getColumnIndex("name"));
			vt.simpleName = c.getString(c.getColumnIndex("simplename"));
			vt.fineDefault = c.getShort(c.getColumnIndex("finedefault"));
			vt.fineMax = c.getShort(c.getColumnIndex("finemax"));
			vt.fineMin = c.getShort(c.getColumnIndex("finemin"));
			vt.law = c.getString(c.getColumnIndex("law"));
			vt.isshow = c.getString(c.getColumnIndex("isshow"));
			vt.marker = c.getString(c.getColumnIndex("marker"));
			vt.method = c.getString(c.getColumnIndex("method"));
			vt.ordernum = c.getShort(c.getColumnIndex("ordernum"));
			vt.punish = c.getString(c.getColumnIndex("punish"));
			vt.rule = c.getString(c.getColumnIndex("rule"));
		}
		c.close();
		return vt;
	}

	public String[] queryVioType() {
		String select = "select * from viotype order by _id";
		Cursor c = sqliteDatabase.rawQuery(select, null);
		String[] data = new String[c.getCount()];
		if (c.moveToFirst()) {
			for (int i = 0; i < c.getCount(); i++) {
				data[i] = c.getString(c.getColumnIndex("simplename"));
				if (!c.moveToNext())
					break;
			}
		}
		c.close();
		return data;
	}

	public String getVioTypeMoneyByName(String viotypename) {
		String select = "select * from viotype where simplename = ?";
		Cursor c = sqliteDatabase.rawQuery(select, new String[] { viotypename });
		String data = "";
		if (c.moveToFirst()) {
			for (int i = 0; i < c.getCount(); i++) {
				if (viotypename.equals(c.getString(c.getColumnIndex("simplename")))) {
					data = c.getString(c.getColumnIndex("finedefault"));
				}
				if (!c.moveToNext())
					break;
			}
		}
		c.close();
		return data;
	}

	public Vector<String> queryVioCode() {
		String select = "select * from viotype order by _id";
		Cursor c = sqliteDatabase.rawQuery(select, null);
		Vector<String> data = new Vector<String>();
		if (c.moveToFirst()) {
			for (int i = 0; i < c.getCount(); i++) {
				data.add(c.getString(c.getColumnIndex("code")));
				if (!c.moveToNext())
					break;
			}
		}
		c.close();
		return data;
	}

	public String getVioTypeIdByName(String vioname) {
		Cursor c = null;
		String res = "";
		try{
			String selection = "select * from viotype where simplename = '" + vioname + "'";
			c = sqliteDatabase.rawQuery(selection, null);		
			if (c.moveToFirst()){
				res = c.getString(c.getColumnIndex("code"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}		
		return res;
	}
	
	public String getVioTypeNameByCode(String viocode) {
		Cursor c = null;
		String res = "";
		try{
			String selection = "select * from viotype where code = '" + viocode + "'";
			c = sqliteDatabase.rawQuery(selection, null);		
			if (c.moveToFirst()){
				res = c.getString(c.getColumnIndex("simplename"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}		
		return res;
	}	
	
	public String[] queryRoadname() {
		String select = "select * from road order by _id";
		Cursor c = sqliteDatabase.rawQuery(select, null);
		String[] data = new String[c.getCount()];
		if (c.moveToFirst()) {
			for (int i = 0; i < c.getCount(); i++) {
				data[i] = c.getString(c.getColumnIndex("name"));
				if (!c.moveToNext())
					break;
			}
		}
		c.close();
		return data;
	}

	public List<Type> getRoadInfoList() {
		String select = "select * from road order by _id";
		Cursor c = sqliteDatabase.rawQuery(select, null);
		List<Type> data = new ArrayList<Type>();
		if (c.moveToFirst()) {
			for (int i = 0; i < c.getCount(); i++) {
				Type road = new Type();
				road.name = c.getString(c.getColumnIndex("name"));
				road.code = c.getString(c.getColumnIndex("uploadcode")).substring(0,5);
				road.type = "road";
				data.add(road);
				if (!c.moveToNext())
					break;
			}
		}
		c.close();
		return data;
	}

	public Vector<String> queryRoadId() {
		String select = "select * from road order by _id";
		Cursor c = sqliteDatabase.rawQuery(select, null);
		Vector<String> data = new Vector<String>();
		if (c.moveToFirst()) {
			for (int i = 0; i < c.getCount(); i++) {
				data.add(c.getString(c.getColumnIndex("id")));
				if (!c.moveToNext())
					break;
			}
		}
		c.close();
		return data;
	}

	public ArrayList<Type> getDutyTypes() {
		ArrayList<Type> dts = new ArrayList<Type>();
		String selection = "select * from dutytype where type='OM_TYPE'";
		Cursor c = sqliteDatabase.rawQuery(selection, null);
		if (c.moveToFirst()) {
			for (int i = 0; i < c.getCount(); i++) {
				Type dt = new Type();
				dt.code = c.getString(c.getColumnIndex("code"));
				dt.name = c.getString(c.getColumnIndex("name"));
				dt.type = c.getString(c.getColumnIndex("type"));
				dts.add(dt);
				if (!c.moveToNext())
					break;
			}
		}
		c.close();
		return dts;
	}

	public ArrayList<Type> getCarColorTypes() {
		ArrayList<Type> ts = new ArrayList<Type>();
		String selection = "select * from dutytype where type='CAR_COLOR'";
		Cursor c = sqliteDatabase.rawQuery(selection, null);
		if (c.moveToFirst()) {
			for (int i = 0; i < c.getCount(); i++) {
				Type dt = new Type();
				dt.code = c.getString(c.getColumnIndex("code"));
				dt.name = c.getString(c.getColumnIndex("name"));
				dt.type = c.getString(c.getColumnIndex("type"));
				ts.add(dt);
				if (!c.moveToNext())
					break;
			}
		}
		c.close();
		return ts;
	}

	public ArrayList<Type> getPlateColorTypes() {
		ArrayList<Type> ts = new ArrayList<Type>();
		String selection = "select * from dutytype where type='PLATE_COLOR'";
		Cursor c = sqliteDatabase.rawQuery(selection, null);
		if (c.moveToFirst()) {
			for (int i = 0; i < c.getCount(); i++) {
				Type dt = new Type();
				dt.code = c.getString(c.getColumnIndex("code"));
				dt.name = c.getString(c.getColumnIndex("name"));
				dt.type = c.getString(c.getColumnIndex("type"));
				ts.add(dt);
				if (!c.moveToNext())
					break;
			}
		}
		c.close();
		return ts;
	}

	public ArrayList<Type> getPlateTypes() {
		ArrayList<Type> dts = new ArrayList<Type>();
		String selection = "select * from dutytype where type='PLATE_TYPE'";
		Cursor c = sqliteDatabase.rawQuery(selection, null);
		if (c.moveToFirst()) {
			for (int i = 0; i < c.getCount(); i++) {
				Type dt = new Type();
				dt.code = c.getString(c.getColumnIndex("code"));
				dt.name = c.getString(c.getColumnIndex("name"));
				dt.type = c.getString(c.getColumnIndex("type"));
				dts.add(dt);
				if (!c.moveToNext())
					break;
			}
		}
		c.close();
		return dts;
	}

	public String getPlateTypeStrByCode(String code) {
		String selection = "select * from dutytype where type='PLATE_TYPE' and code=?";
		Cursor c = sqliteDatabase.rawQuery(selection, new String[] { code });
		String name = "";
		if (c.moveToFirst()) {
			name = c.getString(c.getColumnIndex("name"));
		}
		c.close();
		return name;
	}

	public String getPlateTypeCodeByName(String name) {
		String selection = "select * from dutytype where type='PLATE_TYPE' and name=?";
		Cursor c = sqliteDatabase.rawQuery(selection, new String[] { name });
		String code = "";
		if (c.moveToFirst()) {
			code = c.getString(c.getColumnIndex("code"));
		}
		c.close();
		return code;
	}

	public String[] getRoadDirection() {
		String selection = "select * from dutytype where type='DIRECT'";
		Cursor c = sqliteDatabase.rawQuery(selection, null);
		String[] data = new String[c.getCount()];
		if (c.moveToFirst()) {
			for (int i = 0; i < c.getCount(); i++) {
				data[i] = c.getString(c.getColumnIndex("name"));
				if (!c.moveToNext())
					break;
			}
		}
		c.close();
		return data;
	}	

	public String getRoadDirectionCodeByName(String name) {
		String selection = "select * from dutytype where type='DIRECT' and name=?";
		Cursor c = sqliteDatabase.rawQuery(selection, new String[] { name });
		String directionCode = "";
		if (c.moveToFirst()) {
			directionCode = c.getString(c.getColumnIndex("code"));
		}
		c.close();
		return directionCode;
	}	
	public String getRoadDirectionNameByCode(String code) {
		String selection = "select * from dutytype where type='DIRECT' and code=?";
		Cursor c = sqliteDatabase.rawQuery(selection, new String[] { code });
		String directionName = "";
		if (c.moveToFirst()) {
			directionName = c.getString(c.getColumnIndex("name"));
		}
		c.close();
		return directionName;
	}

	public RoadInfo getRoadInfoByName(String roadName) {
		Cursor c = null;
		RoadInfo ri = new RoadInfo();
		try{
			String selection = "select * from road where name = '" + roadName + "'";
			c = sqliteDatabase.rawQuery(selection, null);
			if (c.moveToFirst()){
				ri.id = c.getString(c.getColumnIndex("id"));
				ri.name = c.getString(c.getColumnIndex("name"));
				ri.note = c.getString(c.getColumnIndex("note"));
				ri.groupId = c.getString(c.getColumnIndex("groupid"));
				ri.pid = c.getString(c.getColumnIndex("pid"));
				ri.status = c.getString(c.getColumnIndex("status"));
				ri.uploadcode = c.getString(c.getColumnIndex("uploadcode"));
				ri.orgId = c.getString(c.getColumnIndex("orgid"));
				ri.orgName = c.getString(c.getColumnIndex("orgname"));
				ri.coderoadtype = c.getString(c.getColumnIndex("coderoadtype"));
				ri.codeRoadType = c.getString(c.getColumnIndex("codeRoadTp"));
				ri.codeRoadDh = c.getString(c.getColumnIndex("coderoaddh"));
				ri.codeRoadZh = c.getString(c.getColumnIndex("coderoadzh"));
				ri.codeRoadMi = c.getString(c.getColumnIndex("coderoadmi"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return ri;
	}

	public String getRoadCodeByName(String roadName) {
		Cursor c = null;
		String res = "";
		try{
			String selection = "select * from road where name = '" + roadName + "'";
			c = sqliteDatabase.rawQuery(selection, null);		
			if (c.moveToFirst()){
				res = c.getString(c.getColumnIndex("uploadcode"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return res;
	}

	public String getRoadIdByName(String roadName) {
		Cursor c = null;
		String res = "";
		try{
			String selection = "select * from road where name = '" + roadName + "'";
			c = sqliteDatabase.rawQuery(selection, null);		
			if (c.moveToFirst()){
				res = c.getString(c.getColumnIndex("id"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}		
		return res;
	}

	public String getRoadNameById(String roadId) {
		Cursor c = null;
		String res = "";
		try{
			String selection = "select * from road where id = '" + roadId + "'";
			c = sqliteDatabase.rawQuery(selection, null);		
			if (c.moveToFirst()){
				res = c.getString(c.getColumnIndex("name"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}		
		return res;
	}
	
	public String[] queryTypeName(String Type) {
		String select = "select * from dutytype where type=?";
		Cursor c = sqliteDatabase.rawQuery(select, new String[] { Type });
		String[] data = new String[c.getCount()];
		if (c.moveToFirst()) {
			for (int i = 0; i < c.getCount(); i++) {
				data[i] = c.getString(c.getColumnIndex("name"));
				if (!c.moveToNext())
					break;
			}
		}
		c.close();
		return data;
	}
	
	public String getNameByTypeAndCodeFromDic(String Type, String Code) {
		Cursor c = null;
		String res = "";
		try{
			String selection = "select * from dutytype where type = '" + Type + "' and code = '" + Code + "'";
			c = sqliteDatabase.rawQuery(selection, null);		
			if (c.moveToFirst()){
				res = c.getString(c.getColumnIndex("name"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}		
		return res;
	}
	
	public String getCodeByTypeAndNameFromDic(String Type, String Name) {
		Cursor c = null;
		String res = "";
		try{
			String selection = "select * from dutytype where type = '" + Type + "' and name = '" + Name + "'";
			c = sqliteDatabase.rawQuery(selection, null);
			if (c.moveToFirst()){
				res = c.getString(c.getColumnIndex("code"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return res;
	}
	
	public String getBlackTypeNameByCode(String code) {
		String selection = "select * from dutytype where type='BLACKTYPE_CODE' and code=?";
		Cursor c = sqliteDatabase.rawQuery(selection, new String[] { code });
		String blacktypename = "";
		if (c.moveToFirst()) {
			blacktypename = c.getString(c.getColumnIndex("name"));
		}
		c.close();
		return blacktypename;
	}

	public ArrayList<Type> getBaseTypes(String Type) {
		ArrayList<Type> dts = new ArrayList<Type>();
		String selection = "select * from dutytype where type=?";
		Cursor c = sqliteDatabase.rawQuery(selection, new String[] {Type});
		if (c.moveToFirst()) {
			for (int i = 0; i < c.getCount(); i++) {
				Type dt = new Type();
				dt.code = c.getString(c.getColumnIndex("code"));
				dt.name = c.getString(c.getColumnIndex("name"));
				dt.type = c.getString(c.getColumnIndex("type"));
				dts.add(dt);
				if (!c.moveToNext())
					break;
			}
		}
		c.close();
		return dts;
	}

	public ArrayList<Type> getCheckTypes() {
		ArrayList<Type> dts = new ArrayList<Type>();
		String selection = "select * from dutytype where type='CHECK_TYPE'";
		Cursor c = sqliteDatabase.rawQuery(selection, null);
		if (c.moveToFirst()) {
			for (int i = 0; i < c.getCount(); i++) {
				Type dt = new Type();
				dt.code = c.getString(c.getColumnIndex("code"));
				dt.name = c.getString(c.getColumnIndex("name"));
				dt.type = c.getString(c.getColumnIndex("type"));
				dts.add(dt);
				if (!c.moveToNext())
					break;
			}
		}
		c.close();
		return dts;
	}

	public ArrayList<Type> getAccTypes() {
		ArrayList<Type> dts = new ArrayList<Type>();
		String selection = "select * from dutytype where type='ACCEVENT_TYPE'";
		Cursor c = sqliteDatabase.rawQuery(selection, null);
		if (c.moveToFirst()) {
			for (int i = 0; i < c.getCount(); i++) {
				Type dt = new Type();
				dt.code = c.getString(c.getColumnIndex("code"));
				dt.name = c.getString(c.getColumnIndex("name"));
				dt.type = c.getString(c.getColumnIndex("type"));
				dts.add(dt);
				if (!c.moveToNext())
					break;
			}
		}
		c.close();
		return dts;
	}

	public ArrayList<Type> getWeatherTypes() {
		ArrayList<Type> dts = new ArrayList<Type>();
		String selection = "select * from dutytype where type='WEATHER'";
		Cursor c = sqliteDatabase.rawQuery(selection, null);
		if (c.moveToFirst()) {
			for (int i = 0; i < c.getCount(); i++) {
				Type dt = new Type();
				dt.code = c.getString(c.getColumnIndex("code"));
				dt.name = c.getString(c.getColumnIndex("name"));
				dt.type = c.getString(c.getColumnIndex("type"));
				dts.add(dt);
				if (!c.moveToNext())
					break;
			}
		}
		c.close();
		return dts;
	}

	public void add(AddressBook addressbook) {
		ContentValues cv = new ContentValues();
		cv.put("id", addressbook.id);
		cv.put("name", addressbook.name);
		if (HanyupinyinUtil.isContainsChinese(addressbook.name)) {
			cv.put("pinyin", HanyupinyinUtil.getPinYin(addressbook.name));
		} else
			cv.put("pinyin", addressbook.name);
		cv.put("phone", addressbook.phone);
		cv.put("post", addressbook.post);
		cv.put("remark", addressbook.remark);
		cv.put("deptmentid", addressbook.deptmentid);
		cv.put("deptmentname", addressbook.deptmentname);
		sqliteDatabase.insert("addressbook", null, cv);
		cv.clear();
	}

	public void clearRoadNameData() {
		sqliteDatabase.execSQL("delete from road;");
	}

	public void clearVioTypeData() {
		sqliteDatabase.execSQL("delete from viotype;");
	}

	public void clearVioDicData() {
		sqliteDatabase.execSQL("delete from viodic;");
	}

	public void clearAddressBookData() {
		sqliteDatabase.execSQL("delete from addressbook;");
	}

	public void clearWeijinData() {
		sqliteDatabase.execSQL("delete from weijininfo;");
	}

	public void clearBlacklistData() {
		sqliteDatabase.execSQL("delete from blacklist;");
	}

	public void clearVipCarData() {
		sqliteDatabase.execSQL("delete from vipcar;");
	}

	public void clearTypeData() {
		sqliteDatabase.execSQL("delete from dutytype;");
	}

	public void clearInSomedayVioData(int day) {
		sqliteDatabase.execSQL("delete from viodata where julianday(datetime('now', 'localtime'))- julianday(datetime(datetime))>?", new Object[] { day });
	}

	public void clearInSomedayDutyData(int day) {
		sqliteDatabase.execSQL("delete from patorl where julianday(datetime('now', 'localtime'))- julianday(datetime(datetime))>?", new Object[] { day });
	}

	public void clearInSomedayAccidentData(int day) {
		sqliteDatabase.execSQL("delete from accident where julianday(datetime('now', 'localtime'))- julianday(datetime(realtime))>?", new Object[] { day });
	}

	public void clearInSomedayLawCheckData(int day) {
		sqliteDatabase.execSQL("delete from taizhangdata where julianday(datetime('now', 'localtime'))- julianday(datetime(datetime))>?", new Object[] { day });
	}

	public void clearInSomedayEnforceData(int day) {
		sqliteDatabase.execSQL("delete from enforcementdata where julianday(datetime('now', 'localtime'))- julianday(datetime(datetime))>?", new Object[] { day });
	}

	public void clearInSomedayLiabilityData(int day) {
		sqliteDatabase.execSQL("delete from liabilitydata where julianday(datetime('now', 'localtime'))- julianday(datetime(datetime))>?", new Object[] { day });
	}

	public void clearInSomedayFieldpunishData(int day) {
		sqliteDatabase.execSQL("delete from fieldpunishdata where julianday(datetime('now', 'localtime'))- julianday(datetime(datetime))>?", new Object[] { day });
	}

	public void clearInSomedayUpdateVioData(int day) {
		sqliteDatabase.execSQL("delete from updatevio where julianday(datetime('now', 'localtime'))- julianday(datetime(datetime))>?", new Object[] { day });
	}

	public void clearInSomedayWarnData(int day) {
		sqliteDatabase.execSQL("delete from warnmessage where julianday(datetime('now', 'localtime'))- julianday(datetime(datetime))>?", new Object[] { day });
	}

	public void clearVioData() {
		sqliteDatabase.execSQL("delete from viodata");
	}

	public void clearDutyData() {
		sqliteDatabase.execSQL("delete from patorl");
	}

	public void clearAccidentData() {
		sqliteDatabase.execSQL("delete from accident");
	}

	public void clearLawCheckData() {
		sqliteDatabase.execSQL("delete from taizhangdata");
	}

	public void clearEnforceData() {
		sqliteDatabase.execSQL("delete from enforcementdata");
	}

	public void clearFieldpunishData() {
		sqliteDatabase.execSQL("delete from fieldpunishdata");
	}


	public void clearUpdateVioData() {
		sqliteDatabase.execSQL("delete from updatevio");
	}

	public void clearWarnData() {
		sqliteDatabase.execSQL("delete from warnmessage");
	}
	public int QueryVioDataCount() {
		int res = -1;
		String Selection = "select count(*) from viodata";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(Selection, null);
			if (c.moveToFirst()) {
				res = c.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return res;
	}

	public int QueryEnforceDataCount() {
		int res = -1;
		String Selection = "select count(*) from enforcementdata";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(Selection, null);
			if (c.moveToFirst()) {
				res = c.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return res;

	}

	public int QueryUnUpEnforceDataCount() {
		int res = -1;
		String Selection = "select count(*) from enforcementdata where isupfile = 0";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(Selection, null);
			if (c.moveToFirst()) {
				res = c.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return res;

	}

	public int QueryFieldpunishDataCount() {
		int res = -1;
		String Selection = "select count(*) from fieldpunishdata";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(Selection, null);
			if (c.moveToFirst()) {
				res = c.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return res;
	}

	public int QueryDutyDataCount() {
		int res = -1;
		String Selection = "select count(*) from patorl";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(Selection, null);
			if (c.moveToFirst()) {
				res = c.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return res;
	}

	public int getUnUpPatorlCount() {
		int res = -1;
		String Selection = "select count(*) from patorl where isupfile = 0";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(Selection, null);
			if (c.moveToFirst()) {
				res = c.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return res;
	}

	public int QueryAccidentDataCount() {
		int res = -1;
		String Selection = "select count(*) from accident";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(Selection, null);
			if (c.moveToFirst()) {
				res = c.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return res;
	}

	public int QueryZhifaDataCount() {
		int res = -1;
		String Selection = "select count(*) from taizhangdata";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(Selection, null);
			if (c.moveToFirst()) {
				res = c.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return res;
	}

	public int QueryUnUpLawCheckDataCount() {
		int res = -1;
		String Selection = "select count(*) from taizhangdata where isupfile = 0";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(Selection, null);
			if (c.moveToFirst()) {
				res = c.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return res;
	}

	public boolean isAddressbookSaved() {
		int res = -1;
		String Selection = "select count(*) from addressbook";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(Selection, null);
			if (c.moveToFirst()) {
				res = c.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		if (res != 0) {
			return true;
		} else
			return false;
	}

	public String getPunishId(String type) {
		String result = null;
		String select = "select * from printid where type = ? and isused <> 1 order by id";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(select, new String[] { type + "" });
			if (c.moveToFirst()) {
				result = c.getString(c.getColumnIndex("id"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			c.close();
		}
		return result;
	}

	public void markPunishId(String id, String type) {
		ContentValues cv = new ContentValues();
		cv.put("isused", 1);
		sqliteDatabase.update("printid", cv, "id = ? and type = ?", new String[] { id, type });
		cv.clear();
	}

	private Message createMsg(int what, Object obj) {
		Message msg = new Message();
		msg.what = what;
		if (obj != null)
			msg.obj = obj;
		return msg;
	}

	public void add(WarnMessage warnMessage) {
		ContentValues cv = new ContentValues();
		cv.put("isupfile", 0);
		cv.put("step", 0);
		cv.put("yjxh", warnMessage.yjxh);
		cv.put("datetime", warnMessage.datetime);
		cv.put("data", warnMessage.dataInfo);
		cv.put("sign", warnMessage.signInfo);
		cv.put("vio", warnMessage.vioInfo);
		cv.put("ack", warnMessage.ackInfo);
		cv.put("pic", warnMessage.picInfo);

		try {
			sqliteDatabase.insert("warnmessage", null, cv);
		} catch (Exception e) {
			e.printStackTrace();
		}
		cv.clear();
	}

	public int QueryWarnMessageCount() {
		int res = -1;
		String Selection = "select count(*) from warnmessage";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(Selection, null);
			if (c.moveToFirst()) {
				res = c.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return res;
	}

	public int QueryWarnMessageCount(String yjxh) {
		int res = -1;
		String Selection = "select count(*) from warnmessage where yjxh = ?";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(Selection, new String[] { yjxh });
			if (c.moveToFirst()) {
				res = c.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return res;
	}

	public void GetWarnMessages(int sindex, int count, Handler handler) {
		List<WarnMessage> warnMeaasges = new ArrayList<WarnMessage>();
		if (sindex == 1) {
			sindex = 0;
		} else {
			sindex = (sindex - 1) * count;
			count = count * sindex;
		}
		String selection = "select * from warnmessage order by datetime desc limit ?, ?";
		Cursor cursor = null;
		try {
			cursor = sqliteDatabase.rawQuery(selection, new String[] { String.valueOf(sindex), String.valueOf(count) });
			if (cursor.moveToFirst()) {
				for (int i = 0; i < cursor.getCount(); i++) {
					WarnMessage dt = new WarnMessage();
					dt.isupfile = cursor.getInt(cursor.getColumnIndex("isupfile"));
					dt.step = cursor.getInt(cursor.getColumnIndex("step"));
					dt.yjxh = cursor.getString(cursor.getColumnIndex("yjxh"));
					dt.datetime = cursor.getString(cursor.getColumnIndex("datetime"));
					dt.dataInfo = cursor.getString(cursor.getColumnIndex("data"));
					dt.signInfo = cursor.getString(cursor.getColumnIndex("sign"));
					dt.vioInfo = cursor.getString(cursor.getColumnIndex("vio"));
					dt.ackInfo = cursor.getString(cursor.getColumnIndex("ack"));
					dt.picInfo = cursor.getString(cursor.getColumnIndex("pic"));

					warnMeaasges.add(dt);
					if (!cursor.moveToNext())
						break;
				}
			}
			handler.sendMessage(createMsg(ConstantStatus.SREACH_DETAIL_SUCCESS, warnMeaasges));
		} catch (Exception e) {
			e.printStackTrace();
			handler.sendMessage(createMsg(ConstantStatus.SREACH_DETAIL_FALSE, null));
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public WarnMessage GetWarnMessages(String yjxh) {
		String selection = "select * from warnmessage where yjxh = ?";
		Cursor cursor = null;
		WarnMessage dt = new WarnMessage();
		try {
			cursor = sqliteDatabase.rawQuery(selection, new String[] { yjxh });
			if (cursor.moveToFirst()) {
				dt.isupfile = cursor.getInt(cursor.getColumnIndex("isupfile"));
				dt.step = cursor.getInt(cursor.getColumnIndex("step"));
				dt.yjxh = cursor.getString(cursor.getColumnIndex("yjxh"));
				dt.datetime = cursor.getString(cursor.getColumnIndex("datetime"));
				dt.dataInfo = cursor.getString(cursor.getColumnIndex("data"));
				dt.signInfo = cursor.getString(cursor.getColumnIndex("sign"));
				dt.vioInfo = cursor.getString(cursor.getColumnIndex("vio"));
				dt.ackInfo = cursor.getString(cursor.getColumnIndex("ack"));
				dt.picInfo = cursor.getString(cursor.getColumnIndex("pic"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return dt;
	}

	public void WarnMessageUpdate(String yjxh, String type, String value) {
		ContentValues cv = new ContentValues();
		if (type.equals("isupfile")) {
			cv.put("isupfile", Integer.valueOf(value));
		} else if (type.equals("step")) {
			cv.put("step", Integer.valueOf(value));
		} else if (type.equals("data")) {
			cv.put("data", value);
		} else if (type.equals("sign")) {
			cv.put("sign", value);
		} else if (type.equals("vio")) {
			cv.put("vio", value);
		} else if (type.equals("ack")) {
			cv.put("ack", value);
		} else if (type.equals("pic")) {
			cv.put("pic", value);
		}

		sqliteDatabase.update("warnmessage", cv, "yjxh = ?", new String[] { yjxh });
		cv.clear();
	}

	public void add(WarnData warnData) {
		ContentValues cv = new ContentValues();
		cv.put("yjxh", warnData.getYjxh());
		cv.put("yjsj", warnData.getYjsj());
		cv.put("ywlb", warnData.getYwlb());
		cv.put("bklx", warnData.getBklx());
		cv.put("bkzlx", warnData.getBkzlx());
		cv.put("gcxh", warnData.getGcxh());
		cv.put("kkbh", warnData.getKkbh());
		cv.put("kkmc", warnData.getKkmc());
		cv.put("fxlx", warnData.getFxlx());
		cv.put("hpzl", warnData.getHpzl());
		cv.put("hphm", warnData.getHphm());
		cv.put("gcsj", warnData.getGcsj());
		cv.put("imageurl", warnData.getImageurl());
		cv.put("isok", -1);
		cv.put("isdo", -1);
		cv.put("isupfile", 0);
		cv.put("isack", -1);
		try {
			sqliteDatabase.insert("warndata", null, cv);
		} catch (Exception e) {
			e.printStackTrace();
		}
		cv.clear();
	}

	public void DeleteWarn() {
		sqliteDatabase.delete("warnmessage", "step = ?", new String[] { "0" });	//未签
		sqliteDatabase.delete("warnmessage", "isupfile = ?", new String[] { "3" });	//无权限

//		sqliteDatabase.delete("warndata", "isok = ?", new String[] { "-1" });	//未签
//		sqliteDatabase.delete("warndata", "isdo = ?", new String[] { "0" });	//签收,不出警

////		sqliteDatabase.delete("warndata", "isack = ?", new String[] { "1" });	//已反馈

	}

	public WarnData getWarnDataInfo(String yjxh) {
					WarnData dt = new WarnData();
		String select = "select * from warndata where yjxh = ?";
		Cursor cursor = null;
		try {
			cursor = sqliteDatabase.rawQuery(select, new String[] { yjxh });
			if (cursor.moveToFirst()) {
					dt.setYjxh(cursor.getString(cursor.getColumnIndex("yjxh")));
					dt.setYjsj(cursor.getString(cursor.getColumnIndex("yjsj")));
					dt.setYwlb(cursor.getString(cursor.getColumnIndex("ywlb")));
					dt.setBklx(cursor.getString(cursor.getColumnIndex("bklx")));
					dt.setBkzlx(cursor.getString(cursor.getColumnIndex("bkzlx")));
					dt.setGcxh(cursor.getString(cursor.getColumnIndex("gcxh")));
					dt.setKkbh(cursor.getString(cursor.getColumnIndex("kkbh")));
					dt.setKkmc(cursor.getString(cursor.getColumnIndex("kkmc")));
					dt.setFxlx(cursor.getString(cursor.getColumnIndex("fxlx")));
					dt.setHpzl(cursor.getString(cursor.getColumnIndex("hpzl")));
					dt.setHphm(cursor.getString(cursor.getColumnIndex("hphm")));
					dt.setGcsj(cursor.getString(cursor.getColumnIndex("gcsj")));
					dt.setImageurl(cursor.getString(cursor.getColumnIndex("imageurl")));
					dt.setIsOk(cursor.getInt(cursor.getColumnIndex("isok")));
					dt.setIsDo(cursor.getInt(cursor.getColumnIndex("isdo")));
					dt.setIsUpFile(cursor.getInt(cursor.getColumnIndex("isupfile")));
					dt.setIsAck(cursor.getInt(cursor.getColumnIndex("isack")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		return dt;
	}

	public void GetWarnInfos(int sindex, int count, Handler handler) {
		List<WarnData> warnInfos = new ArrayList<WarnData>();
		if (sindex == 1) {
			sindex = 0;
		} else {
			sindex = (sindex - 1) * count;
			count = count * sindex;
		}
		String selection = "select * from warndata order by yjsj desc limit ?, ?";
		Cursor cursor = null;
		try {
			cursor = sqliteDatabase.rawQuery(selection, new String[] { String.valueOf(sindex), String.valueOf(count) });
			if (cursor.moveToFirst()) {
				for (int i = 0; i < cursor.getCount(); i++) {
					WarnData dt = new WarnData();
					dt.setYjxh(cursor.getString(cursor.getColumnIndex("yjxh")));
					dt.setYjsj(cursor.getString(cursor.getColumnIndex("yjsj")));
					dt.setYwlb(cursor.getString(cursor.getColumnIndex("ywlb")));
					dt.setBklx(cursor.getString(cursor.getColumnIndex("bklx")));
					dt.setBkzlx(cursor.getString(cursor.getColumnIndex("bkzlx")));
					dt.setGcxh(cursor.getString(cursor.getColumnIndex("gcxh")));
					dt.setKkbh(cursor.getString(cursor.getColumnIndex("kkbh")));
					dt.setKkmc(cursor.getString(cursor.getColumnIndex("kkmc")));
					dt.setFxlx(cursor.getString(cursor.getColumnIndex("fxlx")));
					dt.setHpzl(cursor.getString(cursor.getColumnIndex("hpzl")));
					dt.setHphm(cursor.getString(cursor.getColumnIndex("hphm")));
					dt.setGcsj(cursor.getString(cursor.getColumnIndex("gcsj")));
					dt.setImageurl(cursor.getString(cursor.getColumnIndex("imageurl")));
					dt.setIsOk(cursor.getInt(cursor.getColumnIndex("isok")));
					dt.setIsDo(cursor.getInt(cursor.getColumnIndex("isdo")));
					dt.setIsUpFile(cursor.getInt(cursor.getColumnIndex("isupfile")));
					dt.setIsAck(cursor.getInt(cursor.getColumnIndex("isack")));
					warnInfos.add(dt);
					if (!cursor.moveToNext())
						break;
				}
			}
			handler.sendMessage(createMsg(ConstantStatus.SREACH_DETAIL_SUCCESS, warnInfos));
		} catch (Exception e) {
			e.printStackTrace();
			handler.sendMessage(createMsg(ConstantStatus.SREACH_DETAIL_FALSE, null));
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public void WarnSign(WarnData warnData) {
		ContentValues cv = new ContentValues();
		String Value = warnData.getYjxh();
		cv.put("isok", warnData.getIsOk());
		cv.put("isdo", warnData.getIsDo());
		cv.put("isack", warnData.getIsAck());
		sqliteDatabase.update("warndata", cv, "yjxh = ?", new String[] { Value });
		cv.clear();
	}

	public void WarnImage(WarnData warnData) {
		ContentValues cv = new ContentValues();
		cv.put("imageurl", warnData.getImageurl());
		sqliteDatabase.update("warndata", cv, "yjxh = ?", new String[] { warnData.getYjxh() });
		cv.clear();
	}

	public void WarnSignUp(String yjxh) {
		ContentValues cv = new ContentValues();
		cv.put("isupfile", 1);
		sqliteDatabase.update("warndata", cv, "yjxh = ?", new String[] { yjxh + "" });
		cv.clear();
	}

	public int QueryWarnDataCount() {
		int res = -1;
		String Selection = "select count(*) from warndata";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(Selection, null);
			if (c.moveToFirst()) {
				res = c.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return res;
	}

	public int getLastestWarnAck() {
		int res = -1;
		String selection = "select _id from warnack order by _id desc limit 0,1";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(selection, null);
			if (c.moveToFirst())
				res = c.getInt(0);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return res;
	}

	public void add(WarnPic warnPic) {
		ContentValues cv = new ContentValues();
		cv.put("isupfile",warnPic.isupfile);
		cv.put("yjxh",warnPic.yjxh);
		cv.put("scdw",warnPic.scdw);
		cv.put("scr",warnPic.scr);
		cv.put("tp1",warnPic.tp1);
		cv.put("tp2",warnPic.tp2);
		cv.put("tp3",warnPic.tp3);
		sqliteDatabase.insert("warnpic", null, cv);
		cv.clear();
	}

	public void WarnPicUp(String yjxh) {
		ContentValues cv = new ContentValues();
		cv.put("isupfile", 1);
		sqliteDatabase.update("warnpic", cv, "yjxh = ?", new String[] { yjxh });
		cv.clear();
	}

	public int QueryUnUpWarnPicDataCount() {
		int res = -1;
		String Selection = "select count(*) from warnpic where isupfile = 0";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(Selection, null);
			if (c.moveToFirst()) {
				res = c.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return res;
	}

	public List<WarnPic> getUnUpWarnPicInfo(int index, int count) {
		List<WarnPic> list = new ArrayList<WarnPic>();
		String select = "select * from warnpic where isupfile = 0 limit ?, ?";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(select, new String[] { index + "", count + "" });

			if (c.moveToFirst()) {
				for (int i = 0; i < c.getCount(); i++) {
					WarnPic data = new WarnPic();
					data.isupfile = c.getInt(c.getColumnIndex("isupfile"));
					data.yjxh = c.getString(c.getColumnIndex("yjxh"));
					data.scdw = c.getString(c.getColumnIndex("scdw"));
					data.scr = c.getString(c.getColumnIndex("scr"));
					data.tp1 = c.getString(c.getColumnIndex("tp1"));
					data.tp2 = c.getString(c.getColumnIndex("tp2"));
					data.tp3 = c.getString(c.getColumnIndex("tp3"));
					list.add(data);
					if (!c.moveToNext())
						break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return list;
	}

	public void add(WarnAck warnAck) {
		ContentValues cv = new ContentValues();
		cv.put("isupfile",warnAck.isupfile);
		cv.put("yjxh",warnAck.yjxh);
		cv.put("ywlx",warnAck.ywlx);
		cv.put("qsjg",warnAck.qsjg);
		cv.put("sfcjlj",warnAck.sfcjlj);
		cv.put("ljclqk",warnAck.ljclqk);
		cv.put("chjg",warnAck.chjg);
		cv.put("cljg",warnAck.cljg);
		cv.put("wsbh",warnAck.wsbh);
		cv.put("jyw",warnAck.jyw);
		cv.put("wslb",warnAck.wslb);
		cv.put("wchyy",warnAck.wchyy);
		cv.put("czqkms",warnAck.czqkms);
		cv.put("czdw",warnAck.czdw);
		cv.put("czr",warnAck.czr);
		cv.put("czsj",warnAck.czsj);
		cv.put("yjbm",warnAck.yjbm);
		cv.put("lxr",warnAck.lxr);
		cv.put("lxdh",warnAck.lxdh);
		cv.put("wljdyy",warnAck.wljdyy);
		sqliteDatabase.insert("warnack", null, cv);
		cv.clear();
	}

	public void WarnAck(WarnData warnData) {
		ContentValues cv = new ContentValues();
		String Value = warnData.getYjxh();
		cv.put("isack", warnData.getIsAck());
		sqliteDatabase.update("warndata", cv, "yjxh = ?", new String[] { Value });
		cv.clear();
	}

	public void WarnDataUp(String yjxh, int Status) {
		ContentValues cv = new ContentValues();
		cv.put("isupfile", Status);
		cv.put("isack", Status);
		sqliteDatabase.update("warndata", cv, "yjxh = ? ", new String[] { yjxh });
		cv.clear();
	}

	public void WarnAckUp(String yjxh, String ywlx, int Status) {
		ContentValues cv = new ContentValues();
		cv.put("isupfile", Status);
		sqliteDatabase.update("warnack", cv, "yjxh = ? and ywlx = ?", new String[] { yjxh, ywlx });
		cv.clear();
	}

	public void WarnAckUp(String yjxh, String ywlx) {
		ContentValues cv = new ContentValues();
		cv.put("isupfile", 1);
		sqliteDatabase.update("warnack", cv, "yjxh = ? and ywlx = ?", new String[] { yjxh, ywlx });
		cv.clear();
	}

	public int QueryWarnAckDataCount() {
		int res = -1;
		String Selection = "select count(*) from warnack where ywlx != ?";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(Selection, new String[] { "1" });
			if (c.moveToFirst()) {
				res = c.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return res;
	}

	public int QueryUnUpWarnAckDataCount() {
		int res = -1;
		String Selection = "select count(*) from warnack where isupfile = 0";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(Selection, null);
			if (c.moveToFirst()) {
				res = c.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return res;
	}

	public WarnAck getWarnAckInfo(String yjxh, String ywlx) {
		WarnAck data = new WarnAck();
		String select = "select * from warnack where yjxh = ? and ywlx != ?";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(select, new String[] { yjxh, ywlx });
			if (c.moveToFirst()) {
				data.isupfile = c.getInt(c.getColumnIndex("isupfile"));
				data.yjxh = c.getString(c.getColumnIndex("yjxh"));
				data.ywlx = c.getString(c.getColumnIndex("ywlx"));
				data.qsjg = c.getString(c.getColumnIndex("qsjg"));
				data.sfcjlj = c.getString(c.getColumnIndex("sfcjlj"));
				data.ljclqk = c.getString(c.getColumnIndex("ljclqk"));
				data.chjg = c.getString(c.getColumnIndex("chjg"));
				data.cljg = c.getString(c.getColumnIndex("cljg"));
				data.wsbh = c.getString(c.getColumnIndex("wsbh"));
				data.jyw = c.getString(c.getColumnIndex("jyw"));
				data.wslb = c.getString(c.getColumnIndex("wslb"));
				data.wchyy = c.getString(c.getColumnIndex("wchyy"));
				data.czqkms = c.getString(c.getColumnIndex("czqkms"));
				data.czdw = c.getString(c.getColumnIndex("czdw"));
				data.czr = c.getString(c.getColumnIndex("czr"));
				data.czsj = c.getString(c.getColumnIndex("czsj"));
				data.yjbm = c.getString(c.getColumnIndex("yjbm"));
				data.lxr = c.getString(c.getColumnIndex("lxr"));
				data.lxdh = c.getString(c.getColumnIndex("lxdh"));
				data.wljdyy = c.getString(c.getColumnIndex("wljdyy"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return data;
	}

	public int getWarnAckUpStatus(String yjxh, String ywlx) {
		int status = 0;
		String select = "select * from warnack where yjxh = ? and ywlx != ?";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(select, new String[] { yjxh, ywlx });
			if (c.moveToFirst()) {
				status = c.getInt(c.getColumnIndex("isupfile"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return status;
	}

	public WarnPic getWarnPicInfo(String yjxh) {
		WarnPic data = new WarnPic();
		String select = "select * from warnpic where yjxh = ?";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(select, new String[] { yjxh});
			if (c.moveToFirst()) {
				data.yjxh = c.getString(c.getColumnIndex("yjxh"));
				data.tp1 = c.getString(c.getColumnIndex("tp1"));
				data.tp2 = c.getString(c.getColumnIndex("tp2"));
				data.tp3 = c.getString(c.getColumnIndex("tp3"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return data;
	}

	//获取所有已反馈预警
	public void getWarnAckInfos(int index, int count, Handler handler) {
		List<WarnAck> warnAckInfos = new ArrayList<WarnAck>();
		if (index == 1) {
			index = 0;
		} else {
			index = (index - 1) * count;
			count = count * index;
		}
		String select = "select * from warnack where ywlx != 1 order by czsj desc limit ?, ?";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(select, new String[] { index + "", count + "" });

			if (c.moveToFirst()) {
				for (int i = 0; i < c.getCount(); i++) {
					WarnAck data = new WarnAck();
					data.isupfile = c.getInt(c.getColumnIndex("isupfile"));
					data.yjxh = c.getString(c.getColumnIndex("yjxh"));
					data.ywlx = c.getString(c.getColumnIndex("ywlx"));
					data.qsjg = c.getString(c.getColumnIndex("qsjg"));
					data.sfcjlj = c.getString(c.getColumnIndex("sfcjlj"));
					data.ljclqk = c.getString(c.getColumnIndex("ljclqk"));
					data.chjg = c.getString(c.getColumnIndex("chjg"));
					data.cljg = c.getString(c.getColumnIndex("cljg"));
					data.wsbh = c.getString(c.getColumnIndex("wsbh"));
					data.jyw = c.getString(c.getColumnIndex("jyw"));
					data.wslb = c.getString(c.getColumnIndex("wslb"));
					data.wchyy = c.getString(c.getColumnIndex("wchyy"));
					data.czqkms = c.getString(c.getColumnIndex("czqkms"));
					data.czdw = c.getString(c.getColumnIndex("czdw"));
					data.czr = c.getString(c.getColumnIndex("czr"));
					data.czsj = c.getString(c.getColumnIndex("czsj"));
					data.yjbm = c.getString(c.getColumnIndex("yjbm"));
					data.lxr = c.getString(c.getColumnIndex("lxr"));
					data.lxdh = c.getString(c.getColumnIndex("lxdh"));
					data.wljdyy = c.getString(c.getColumnIndex("wljdyy"));
					warnAckInfos.add(data);
					if (!c.moveToNext())
						break;
				}
			}
			handler.sendMessage(createMsg(ConstantStatus.SREACH_DETAIL_SUCCESS, warnAckInfos));
		} catch (Exception e) {
			e.printStackTrace();
			handler.sendMessage(createMsg(ConstantStatus.SREACH_DETAIL_FALSE, null));
		} finally {
			if (c != null) {
				c.close();
			}
		}
	}

	public List<WarnAck> getUnUpWarnAckInfo(int index, int count) {
		List<WarnAck> list = new ArrayList<WarnAck>();
		String select = "select * from warnack where isupfile = 0 limit ?, ?";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(select, new String[] { index + "", count + "" });

			if (c.moveToFirst()) {
				for (int i = 0; i < c.getCount(); i++) {
					WarnAck data = new WarnAck();
					data.isupfile = c.getInt(c.getColumnIndex("isupfile"));
					data.yjxh = c.getString(c.getColumnIndex("yjxh"));
					data.ywlx = c.getString(c.getColumnIndex("ywlx"));
					data.qsjg = c.getString(c.getColumnIndex("qsjg"));
					data.sfcjlj = c.getString(c.getColumnIndex("sfcjlj"));
					data.ljclqk = c.getString(c.getColumnIndex("ljclqk"));
					data.chjg = c.getString(c.getColumnIndex("chjg"));
					data.cljg = c.getString(c.getColumnIndex("cljg"));
					data.wsbh = c.getString(c.getColumnIndex("wsbh"));
					data.jyw = c.getString(c.getColumnIndex("jyw"));
					data.wslb = c.getString(c.getColumnIndex("wslb"));
					data.wchyy = c.getString(c.getColumnIndex("wchyy"));
					data.czqkms = c.getString(c.getColumnIndex("czqkms"));
					data.czdw = c.getString(c.getColumnIndex("czdw"));
					data.czr = c.getString(c.getColumnIndex("czr"));
					data.czsj = c.getString(c.getColumnIndex("czsj"));
					data.yjbm = c.getString(c.getColumnIndex("yjbm"));
					data.lxr = c.getString(c.getColumnIndex("lxr"));
					data.lxdh = c.getString(c.getColumnIndex("lxdh"));
					data.wljdyy = c.getString(c.getColumnIndex("wljdyy"));
					list.add(data);
					if (!c.moveToNext())
						break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return list;
	}

	public String getWarnDoId (String DoType, String hpzl, String hphm) {
		String res = null;
		String Selection;
		//简易程序处罚决定书","强制措施凭证","违法处理通知书"};
		if ("简易程序处罚决定书".equals(DoType))
			Selection = "select * from fieldpunishdata where platetype = ? and plate = ? order by datetime DESC";
		else if ("强制措施凭证".equals(DoType))
			Selection = "select * from enforcementdata where platetype = ? and plate = ? order by datetime DESC";
		else //if ("违法处理通知书".equals(DoType))
			Selection = "select * from viodata where platetype = ? and platenum = ? order by datetime DESC";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(Selection, new String[] { hpzl, hphm });
			if (c.moveToFirst()) {
				int count = c.getCount();
				for (int i = 0; i < count; i++) {
					if ("违法处理通知书".equals(DoType))
						res = c.getString(c.getColumnIndex("publishCode"));
					else
						res = c.getString(c.getColumnIndex("id"));
					if (!"".equals(res))
						break;
					if (!c.moveToNext())
						break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return res;
	}

	public int QueryUnUpWarnMessageCount() {
		int res = -1;
		String Selection = "select count(*) from warnmessage where isupfile = 0 and step != 0";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(Selection, null);
			if (c.moveToFirst()) {
				res = c.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return res;
	}

	public List<WarnMessage> getUnUpWarnMessageInfo(int sindex, int count) {
		List<WarnMessage> list = new ArrayList<WarnMessage>();
		if (sindex == 1) {
			sindex = 0;
		} else {
			sindex = (sindex - 1) * count;
			count = count * sindex;
		}
		String select = "select * from warnmessage where isupfile = 0 and step != 0 limit ?, ?";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(select, new String[] { sindex + "", count + "" });

			if (c.moveToFirst()) {
				for (int i = 0; i < c.getCount(); i++) {
					WarnMessage data = new WarnMessage();
					data.isupfile = c.getInt(c.getColumnIndex("isupfile"));
					data.step = c.getInt(c.getColumnIndex("step"));
					data.yjxh = c.getString(c.getColumnIndex("yjxh"));
					data.datetime = c.getString(c.getColumnIndex("datetime"));
					data.dataInfo = c.getString(c.getColumnIndex("data"));
					data.signInfo = c.getString(c.getColumnIndex("sign"));
					data.ackInfo = c.getString(c.getColumnIndex("ack"));
					data.picInfo = c.getString(c.getColumnIndex("pic"));
					data.vioInfo = c.getString(c.getColumnIndex("vio"));
					list.add(data);
					if (!c.moveToNext())
						break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return list;
	}

	public int QueryUpWarnMessageCount() {
		int res = -1;
		String Selection = "select count(*) from warnmessage where step != 0";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(Selection, null);
			if (c.moveToFirst()) {
				res = c.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return res;
	}

	public void getUpWarnMessageInfo(int index, int count, Handler handler) {
		List<WarnMessage> list = new ArrayList<WarnMessage>();
		if (index == 1) {
			index = 0;
		} else {
			index = (index - 1) * count;
			count = count * index;
		}

		String select = "select * from warnmessage where step != 0 order by datetime desc limit ?, ?";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(select, new String[] { index + "", count + "" });

			if (c.moveToFirst()) {
				for (int i = 0; i < c.getCount(); i++) {
					WarnMessage data = new WarnMessage();
					data.isupfile = c.getInt(c.getColumnIndex("isupfile"));
					data.step = c.getInt(c.getColumnIndex("step"));
					data.yjxh = c.getString(c.getColumnIndex("yjxh"));
					data.datetime = c.getString(c.getColumnIndex("datetime"));
					data.dataInfo = c.getString(c.getColumnIndex("data"));
					data.signInfo = c.getString(c.getColumnIndex("sign"));
					data.ackInfo = c.getString(c.getColumnIndex("ack"));
					data.picInfo = c.getString(c.getColumnIndex("pic"));
					data.vioInfo = c.getString(c.getColumnIndex("vio"));
					list.add(data);
					if (!c.moveToNext())
						break;
				}
				handler.sendMessage(createMsg(ConstantStatus.SREACH_DETAIL_SUCCESS, list));
			}
		} catch (Exception e) {
			e.printStackTrace();
			handler.sendMessage(createMsg(ConstantStatus.SREACH_DETAIL_FALSE, null));
		} finally {
			c.close();
		}
	}

	public List<WarnMessage> getUpWarnMessageInfo(int index, int count) {
		List<WarnMessage> list = new ArrayList<WarnMessage>();
		if (index == 1) {
			index = 0;
		} else {
			index = (index - 1) * count;
			count = count * index;
		}
		String select = "select * from warnmessage where step != 0 limit ?, ?";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(select, new String[] { index + "", count + "" });

			if (c.moveToFirst()) {
				for (int i = 0; i < c.getCount(); i++) {
					WarnMessage data = new WarnMessage();
					data.isupfile = c.getInt(c.getColumnIndex("isupfile"));
					data.step = c.getInt(c.getColumnIndex("step"));
					data.yjxh = c.getString(c.getColumnIndex("yjxh"));
					data.datetime = c.getString(c.getColumnIndex("datetime"));
					data.dataInfo = c.getString(c.getColumnIndex("data"));
					data.signInfo = c.getString(c.getColumnIndex("sign"));
					data.ackInfo = c.getString(c.getColumnIndex("ack"));
					data.picInfo = c.getString(c.getColumnIndex("pic"));
					data.vioInfo = c.getString(c.getColumnIndex("vio"));
					list.add(data);
					if (!c.moveToNext())
						break;
				}
				return list;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			c.close();
		}

		return null;
	}

	public int add(UpdateVio updateVio) {
		int result = -1;
		try {
			ContentValues cv = new ContentValues();
			cv.put("isupfile", updateVio.isupfile);
			cv.put("type", updateVio.type);
			cv.put("sn", updateVio.sn);
			cv.put("vio", updateVio.vio);
			cv.put("datetime", updateVio.datetime);
			result = (int) sqliteDatabase.insert("updatevio", null, cv);
			cv.clear();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public void VioUpdateUp(String type, String sn, int Status) {
		ContentValues cv = new ContentValues();
		cv.put("isupfile", Status);
		sqliteDatabase.update("updatevio", cv, "type = ? and sn = ?", new String[] { type, sn });
		cv.clear();
	}

	public int QueryUnUpUpdateVioDataCount() {
		int res = -1;
		String Selection = "select count(*) from updatevio where isupfile = 0";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(Selection, null);
			if (c.moveToFirst()) {
				res = c.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return res;
	}

	public List<UpdateVio> getUnUpUpdateVioInfo(int index, int count) {
		List<UpdateVio> list = new ArrayList<UpdateVio>();
		String select = "select * from updatevio where isupfile = 0 limit ?, ?";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(select, new String[] { index + "", count + "" });

			if (c.moveToFirst()) {
				for (int i = 0; i < c.getCount(); i++) {
					UpdateVio data = new UpdateVio();
					data.isupfile = c.getInt(c.getColumnIndex("isupfile"));
					data.type = c.getString(c.getColumnIndex("type"));
					data.sn = c.getString(c.getColumnIndex("sn"));
					data.vio = c.getString(c.getColumnIndex("vio"));
					list.add(data);
					if (!c.moveToNext())
						break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return list;
	}

	public int QueryUpUpdateVioCount() {
		int res = -1;
		String Selection = "select count(*) from updatevio";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(Selection, null);
			if (c.moveToFirst()) {
				res = c.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return res;
	}

	public void getUpUpdateVioInfo(int index, int count, Handler handler) {
		List<UpdateVio> list = new ArrayList<UpdateVio>();
		if (index == 1) {
			index = 0;
		} else {
			index = (index - 1) * count;
			count = count * index;
		}

		String select = "select * from updatevio order by datetime desc limit ?, ?";
		Cursor c = null;
		try {
			c = sqliteDatabase.rawQuery(select, new String[] { index + "", count + "" });

			if (c.moveToFirst()) {
				for (int i = 0; i < c.getCount(); i++) {
					UpdateVio data = new UpdateVio();
					data.isupfile = c.getInt(c.getColumnIndex("isupfile"));
					data.datetime = c.getString(c.getColumnIndex("datetime"));
					data.type = c.getString(c.getColumnIndex("type"));
					data.sn = c.getString(c.getColumnIndex("sn"));
					data.vio = c.getString(c.getColumnIndex("vio"));
					list.add(data);
					if (!c.moveToNext())
						break;
				}
				handler.sendMessage(createMsg(ConstantStatus.SREACH_DETAIL_SUCCESS, list));
			}
		} catch (Exception e) {
			e.printStackTrace();
			handler.sendMessage(createMsg(ConstantStatus.SREACH_DETAIL_FALSE, null));
		} finally {
			c.close();
		}
	}
}
