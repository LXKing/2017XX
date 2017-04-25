package com.xiangxun.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "data.db";
	private static final int DATABASE_VERSION = 7;

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 创建违法数据表
		db.execSQL("CREATE TABLE IF NOT EXISTS  viodata "
				+ "(_id INTEGER PRIMARY KEY, " + "datetime TEXT, " + "publishCode TEXT,"
				+ "issure NUMERIC, " + "isupfile NUMERIC, " + "vioid TEXT, "
				+ "disposetype NUMERIC, " + "picnum NUMERIC, "
				+ "picurl0 TEXT, " + "picurl1 TEXT, " + "picurl2 TEXT, "
				+ "picurl3 TEXT, "  + "carcolorcode TEXT, "  + "platecolorcode TEXT, "  + "platecolor TEXT, " + "carcolor TEXT, " + "platenum TEXT, " + "platetype TEXT, "
				+ "roadid TEXT, "+ "roadname TEXT, "+ "roadlocation TEXT, "+ "roaddirect TEXT, " + "user TEXT, " + "viocode TEXT, "
				+ "viotype TEXT)");
		// 创建黑名单数据表
		db.execSQL("CREATE TABLE IF NOT EXISTS  blacklist "
				+ "(_id INTEGER PRIMARY KEY, " + "platenum TEXT, "
				+ "blacklisttype TEXT, " + "platetype TEXT, " + "blt TEXT, "
				+ "datetime TEXT, " + "carcolor TEXT, " + "brand TEXT, "
				+ "sid NUMERIC, " + "owner TEXT)");
		// 创建白名单数据表
		db.execSQL("CREATE TABLE IF NOT EXISTS  vipcar "
				+ "(_id INTEGER PRIMARY KEY, " + "platenum TEXT, "
				+ "platetype TEXT)");
		// 创建GPS数据表
		db.execSQL("CREATE TABLE IF NOT EXISTS  gpsinfo "
				+ "(_id INTEGER PRIMARY KEY, " + "datetime TEXT, "
				+ "provider TEXT, " + "latitude TEXT, " + "longitude TEXT, "
				+ "altitude TEXT, " + "bearing TEXT, " + "isupfile TEXT, "
				+ "jobs TEXT)");
		// 创建消息数据表
		db.execSQL("CREATE TABLE IF NOT EXISTS textmsg "
				+ "(_id INTEGER PRIMARY KEY, " + "datetime TEXT, "
				+ "isread NUMERIC, " + "text TEXT)");
		// 创建系统消息数据表
		db.execSQL("CREATE TABLE IF NOT EXISTS sysmsg "
				+ "(_id INTEGER PRIMARY KEY, " + "datetime TEXT, "
				+ "isread NUMERIC, " + "issign NUMERIC, " + "isack NUMERIC, "
				+ "msgtype NUMERIC, " + "text TEXT)");
		//
		db.execSQL("CREATE TABLE IF NOT EXISTS mlog "
				+ "(_id INTEGER PRIMARY KEY, " + "classname TEXT, "
				+ "datetime TEXT, " + "isupfile NUMERIC, "
				+ "logtype NUMERIC, " + "manipulation TEXT, " + "user TEXT)");
		// 创建黑名单比对数据表
		db.execSQL("CREATE TABLE IF NOT EXISTS blcompare "
				+ "(_id INTEGER PRIMARY KEY, " + "datetime TEXT, "
				+ "issure TEXT, " + "isupfile NUMERIC, " + "picnum NUMERIC, "
				+ "picurl0 TEXT, " + "picurl1 TEXT, " + "picurl2 TEXT, "
				+ "picurl3 TEXT, " + "platenum TEXT, " + "platetype TEXT, "
				+ "roadname TEXT, "+ "roadlocation TEXT, "+ "roaddirect TEXT, " + "user TEXT, " + "bltype TEXT, "
				+ "blt TEXT, " + "carcolor TEXT, " + "brand TEXT, " + "owner)");
		// 创建巡逻勤务数据表
		db.execSQL("CREATE TABLE IF NOT EXISTS patorl "
				+ "(_id INTEGER PRIMARY KEY, " + "datetime TEXT, "
				+ "memo TEXT, " + "patorltype NUMERIC, " + "pic1 TEXT, "
				+ "pic2 TEXT, " + "pic3 TEXT, " + "pic4 TEXT, "
				+ "id TEXT, " + "roadname TEXT, "+ "roadlocation TEXT, "+ "roaddirect TEXT, " + "km NUMERIC, " + "isupfile NUMERIC, "
				+ "user TEXT, " + "isread NUMERIC, " + "picsole TEXT)");
		// db.execSQL("drop table accident");
		// 创建事故数据表
		db.execSQL("CREATE TABLE IF NOT EXISTS accident "
				+ "(_id INTEGER PRIMARY KEY, " + "carnum NUMERIC, "
				+ "id TEXT, " + "death NUMERIC, " + "hurt NUMERIC, " + "memo TEXT, "
				+ "occurtime TEXT, " + "progress NUMERIC, " + "realtime TEXT, "
				+ "roadname TEXT, "+ "roadlocation TEXT, "+ "roaddirect TEXT, " + "acctype NUMERIC, " + "caller TEXT, "
				+ "phone TEXT, " + "user TEXT, "
				+ "pic1 TEXT, " + "pic2 TEXT, " + "pic3 TEXT, " + "pic4 TEXT, "
				+ "pic5 TEXT, " + "pic6 TEXT, " + "pic7 TEXT, " + "pic8 TEXT, "
				+ "pic9 TEXT, " + "pic10 TEXT, " + "joinlist TEXT, "
				+ "weather NUMERIC, " + "isupfile NUMERIC, " + "isread NUMERIC, "
				+ "sole NUMERIC)");

		// 创建图片信息数据表
		db.execSQL("CREATE TABLE IF NOT EXISTS picinfo "
				+ "(_id INTEGER PRIMARY KEY, " + "memo TEXT, "
				+ "ownerid TEXT, " + "picindex NUMERIC, " + "pictag NUMERIC, "
				+ "pictype NUMERIC, " + "isupfile NUMERIC, " + "picurl TEXT)");
		// 创建事故车辆信息数据表
		db.execSQL("CREATE TABLE IF NOT EXISTS accidentcar "
				+ "(_id INTEGER PRIMARY KEY, " + "cartage NUMERIC, "
				+ "memo TEXT, " + "ownerid TEXT, " + "platenum TEXT, "
				+ "platetype NUMERIC)");

		// 创建违法处理数据表
		db.execSQL("CREATE TABLE IF NOT EXISTS dealviodata "
				+ "(_id INTEGER PRIMARY KEY, " + "vioid TEXT, "
				+ "dealtype TEXT, " + "penalnum NUMERIC, " + "datetime TEXT, "
				+ "isupfile NUMERIC, " + "picnum NUMERIC)");
		// 创建简易程序处罚数据表
		db.execSQL("CREATE TABLE IF NOT EXISTS fieldpunishdata "
				+ "(_id INTEGER PRIMARY KEY, " + "id TEXT, " + "name TEXT, "
				+ "phone TEXT, " + "driverlic TEXT, " + "recordid TEXT, "
				+ "licenseoffice TEXT, " + "plate TEXT, " + "platetype TEXT, "
				+ "plateoffice TEXT, " + "owner TEXT, " + "pic1 TEXT, "
				+ "pic2 TEXT, " + "pic3 TEXT, " + "pic4 TEXT, "
				+ "location TEXT, " + "content TEXT, " + "datetime TEXT, "
				+ "roadlocation TEXT, " + "roaddirect TEXT, "
				+ "isupfile NUMERIC, " + "isprinted NUMERIC, "
				+ "action TEXT, " + "money NUMERIC, " + "value NUMERIC, " + "vioid TEXT, " + "licensetype TEXT, "
				+ "picnum NUMERIC)");
		// 创建行政强制措施数据表
		db.execSQL("CREATE TABLE IF NOT EXISTS enforcementdata "
				+ "(_id INTEGER PRIMARY KEY, " + "id TEXT, " + "viotype TEXT, "
				+ "name TEXT, " + "phone TEXT, " + "driverlic TEXT, "
				+ "recordid TEXT, " + "licenseoffice TEXT, " + "plate TEXT, "
				+ "platetype TEXT, " + "plateoffice TEXT, " + "location TEXT, "
				+ "roadlocation TEXT, " + "roaddirect TEXT, "
				+ "content TEXT, " + "datetime TEXT, " + "isupfile NUMERIC, "
				+ "vioid TEXT, "+ "code TEXT, " + "action TEXT, " + "licensetype TEXT, "
				+ "pic1 TEXT, " + "pic2 TEXT, " + "pic3 TEXT, " + "pic4 TEXT, "
				+ "isprinted NUMERIC, " + "picnum NUMERIC)");

		// 创建执法检查数据表
		db.execSQL("CREATE TABLE IF NOT EXISTS taizhangdata "
				+ "(_id INTEGER PRIMARY KEY, " + "viotype TEXT, "
				+ "name TEXT, " + "phone TEXT, " + "driverlic TEXT, "
				+ "plate TEXT, " + "platetype TEXT, " + "road TEXT, "
				+ "roadlocation TEXT, " + "roaddirect TEXT, "
				+ "datetime TEXT, " + "isupfile NUMERIC, " + "memo TEXT, "
				+ "vioid TEXT, " + "owner TEXT, " + "pic1 TEXT, " + "pic2 TEXT, "
				+ "pic3 TEXT, " + "pic4 TEXT, " + "checktype TEXT, "
				+ "handleway TEXT, " + "handler TEXT, " + "actual TEXT, "
				+ "must TEXT)");

		db.execSQL("CREATE TABLE IF NOT EXISTS parkingdata "
				+ "(_id INTEGER PRIMARY KEY, " + "vioid TEXT, " + "id TEXT, "
				+ "plate TEXT, " + "platetype TEXT, " + "location TEXT, "
				+ "roadlocation TEXT, " + "roaddirect TEXT, "
				+ "content TEXT, " + "datetime TEXT, " + "isupfile NUMERIC, "
				+ "name TEXT, " + "isprinted NUMERIC, " + "picnum NUMERIC)");

		db.execSQL("CREATE TABLE IF NOT EXISTS liabilitydata "
				+ "(_id INTEGER PRIMARY KEY, " + "vioid TEXT, " + "id TEXT, "
				+ "datetime TEXT, " + "road TEXT, "+ "roadlocation TEXT, "+ "roaddirect TEXT, " + "km TEXT, "
				+ "weather TEXT, " +

				"name1 TEXT, " + "gender1 TEXT, " + "traffictype1 TEXT, "
				+ "phone1 TEXT, " + "license1 TEXT, " + "address1 TEXT, "
				+ "liability1 TEXT, " + "insurance1 TEXT, " + "plate1 TEXT, "
				+ "platetype1 TEXT, " +

				"name2 TEXT, " + "gender2 TEXT, " + "traffictype2 TEXT, "
				+ "phone2 TEXT, " + "license2 TEXT, " + "address2 TEXT, "
				+ "liability2 TEXT, " + "insurance2 TEXT, " + "plate2 TEXT, "
				+ "platetype2 TEXT, " +

				"process TEXT, " + "result TEXT, " + "pernum NUMERIC, "
				+ "isupfile NUMERIC, " + "isprinted NUMERIC, " +

				"picnum NUMERIC)");

		db.execSQL("CREATE TABLE IF NOT EXISTS person "
				+ "(_id INTEGER PRIMARY KEY, " + "id TEXT, " + "name TEXT, "
				+ "gender TEXT, " + "traffictype TEXT, " + "phone TEXT, "
				+ "license TEXT, " + "address TEXT, " + "liability TEXT, "
				+ "plate TEXT, " + "platetype TEXT, " + "ind NUMERIC, "
				+ "insurance TEXT)");

		db.execSQL("CREATE TABLE IF NOT EXISTS rebuk "
				+ "(_id INTEGER PRIMARY KEY, " + "soleid TEXT, "
				+ "vioid TEXT, " + "viotime TEXT, " + "vioaddr TEXT, "
				+ "viotype TEXT, " + "vioidplate TEXT, "
				+ "vioidplatetype TEXT, " + "rebuktype1 TEXT, "
				+ "rebuktype2 TEXT, " + "rebuktype3 TEXT, "
				+ "rebuktype4 TEXT, " + "rebuktime TEXT, " + "rebukadr TEXT, "
				+ "km TEXT, " + "rebuksug1 TEXT, " + "rebuksug2 TEXT, "
				+ "rebuksug3 TEXT, " + "rebuksug4 TEXT, "
				+ "isupfile NUMERIC, " + "picnum NUMERIC, "
				+ "isrebuked NUMERIC)");

		// viotype data
		db.execSQL("CREATE TABLE IF NOT EXISTS viotype "
				+ "(_id INTEGER PRIMARY KEY, " + "code TEXT, " + "name TEXT, "
				+ "finedefault INTEGER," + "finemax INTEGER,"
				+ "finemin INTEGER," + "law TEXT," + "rule TEXT,"
				+ "method TEXT," + "punish TEXT," + "simplename TEXT,"
				+ "isshow TEXT," + "ordernum INTEGER," + "marker TEXT)");
		
		// viodic data
		db.execSQL("CREATE TABLE IF NOT EXISTS viodic "
				+ "(_id INTEGER PRIMARY KEY, " + "code TEXT, " + "name TEXT, "
				+ "finedefault INTEGER," + "finemax INTEGER,"
				+ "finemin INTEGER," + "law TEXT," + "rule TEXT,"
				+ "method TEXT," + "punish TEXT," + "simplename TEXT,"
				+ "isshow TEXT," + "ordernum INTEGER," + "marker TEXT)");
		
		// road data
		db.execSQL("CREATE TABLE IF NOT EXISTS road "
				+ "(_id INTEGER PRIMARY KEY, " + "id TEXT, " + "name TEXT, "
				+ "note TEXT," + "groupid TEXT," + "pid TEXT,"
				+ "levels INTEGER," + "status TEXT," + "uploadcode TEXT,"
				+ "coderoadtype TEXT," + "orgid TEXT," + "orgname TEXT,"
				+ "codeRoadTp TEXT," + "coderoaddh TEXT," + "coderoadzh TEXT,"
				+ "coderoadmi TEXT)");
		// address book
		db.execSQL("CREATE TABLE IF NOT EXISTS addressbook "
				+ "(_id INTEGER PRIMARY KEY, " + "id TEXT, " + "name TEXT, "
				+ "pinyin TEXT, " + "phone TEXT," + "deptmentid TEXT,"
				+ "deptmentname TEXT," + "post TEXT," + "remark TEXT)");
		// lawinfo
		db.execSQL("CREATE TABLE IF NOT EXISTS lawinfo "
				+ "(_id INTEGER PRIMARY KEY, " + "key TEXT, " + "vioid TEXT, "
				+ "dealtext TEXT, " + "law TEXT," + "money TEXT)");
		// weijininfo
		db.execSQL("CREATE TABLE IF NOT EXISTS weijininfo "
				+ "(_id INTEGER PRIMARY KEY, " + "id TEXT, " + "uncode TEXT, "
				+ "name TEXT, " + "lawprovision TEXT, " + "disposemode TEXT, "
				+ "type TEXT," + "features TEXT)");
		// print id
		db.execSQL("CREATE TABLE IF NOT EXISTS printid "
				+ "(_id INTEGER PRIMARY KEY, " + "type NUMERIC, "
				+ "isused NUMERIC, " + "id TEXT)");
		// dutytype
		db.execSQL("CREATE TABLE IF NOT EXISTS dutytype "
				+ "(_id INTEGER PRIMARY KEY, " + "code TEXT, " + "name TEXT, "
				+ "type TEXT)");
		// warndata
		db.execSQL("CREATE TABLE IF NOT EXISTS warndata "
				+ "(_id INTEGER PRIMARY KEY, " + "yjxh TEXT, " + "yjsj TEXT, " + "ywlb TEXT, "
				+ "bklx TEXT, " + "bkzlx TEXT, " + "gcxh TEXT, " + "kkbh TEXT, " + "kkmc TEXT, "
				+ "fxlx TEXT, " + "hpzl TEXT, " + "hphm TEXT, " + "gcsj TEXT, " + "imageurl TEXT, "
				+ "isok NUMERIC, " + "isdo NUMERIC, " + "isupfile NUMERIC, " + "isack NUMERIC)");
		// 创建预警反馈数据表
		db.execSQL("CREATE TABLE IF NOT EXISTS warnack "
				+ "(_id INTEGER PRIMARY KEY, " + "isupfile NUMERIC, "
				+ "yjxh TEXT, " + "ywlx TEXT, " + "qsjg TEXT, "	+ "sfcjlj TEXT, " + "ljclqk TEXT, "
				+ "chjg TEXT, "	+ "cljg TEXT, " + "wsbh TEXT, " + "jyw TEXT, " + "wslb TEXT, "
				+ "wchyy TEXT, " + "czqkms TEXT, " + "czdw TEXT, "	+ "czr TEXT, " + "czsj TEXT, "
				+ "yjbm TEXT, " + "lxr TEXT, " + "lxdh TEXT, " + "wljdyy TEXT)");
		// 创建预警拦截图片数据表
		db.execSQL("CREATE TABLE IF NOT EXISTS warnpic "
				+ "(_id INTEGER PRIMARY KEY, " + "isupfile NUMERIC, "
				+ "yjxh TEXT, " + "scdw TEXT, " + "scr TEXT, "
				+ "tp1 TEXT, " + "tp2 TEXT, " + "tp3 TEXT)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion > oldVersion) {
			db.execSQL("DROP TABLE IF EXISTS viodata;");
			onCreate(db);
		}
	}

}
