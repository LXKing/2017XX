package com.xiangxun.request;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.xiangxun.bean.AddressBook;
import com.xiangxun.bean.BlackList;
import com.xiangxun.bean.Group;
import com.xiangxun.bean.PersionInfoX;
import com.xiangxun.bean.ResultData;
import com.xiangxun.bean.ResultData.LoginData;
import com.xiangxun.bean.ResultData.Number;
import com.xiangxun.bean.RoadInfo;
import com.xiangxun.bean.Type;
import com.xiangxun.bean.UpdateResult;
import com.xiangxun.bean.VehicleInfoX;
import com.xiangxun.bean.VioType;
import com.xiangxun.bean.VipCarInfo;
import com.xiangxun.cfg.SystemCfg;
import com.xiangxun.common.DcHttpClient;
import com.xiangxun.common.InfoCache;
import com.xiangxun.db.DBManager;
import com.xiangxun.util.ConstantStatus;
import com.xiangxun.util.JsonUtil;
import com.xiangxun.util.Utils;
import com.xiangxun.volley.Response;
import com.xiangxun.volley.VolleyError;
import com.xiangxun.widget.MsgToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @package: com.huatek.api.request
 * @ClassName: DcNetWorkUtils
 * @author: aaron_han
 * @Description: 网络请求统一类

 * @date: 2015年01月14日 上午10:50:06
 */

public class DcNetWorkUtils {
	/**
	 * 登录
	 * 
	 * @param context
	 * @param usetName
	 * @param passWord
	 * @param handler
	 */
	public static void login(Context context, String usetName, String passWord, String deviceId, final Handler handler) {
		String url = ApiUrl.login(context);
		Map<String, String> params = new HashMap<String, String>();
		params.put("loginName", usetName);
		params.put("password", passWord);
		params.put("deviceCode", deviceId);
		DcHttpClient.getInstance().postWithURL(context, url, params, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject object) {
				if (null != object && !object.equals("网络异常")) {
					try {
						LoginData loginData = getGson().fromJson(object.toString(), LoginData.class);
						handler.sendMessage(createMsg(ConstantStatus.loadSuccess, loginData));
					} catch (JsonSyntaxException e) {
						e.printStackTrace();
						handler.sendMessage(createMsg(ConstantStatus.FAILED, null));
					}
				} else {
					handler.sendMessage(createMsg(ConstantStatus.loadFailed, null));
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				handler.sendMessage(createMsg(ConstantStatus.NetWorkError, null));
			}
		});
	}

	/**
	 * 查询驾驶员信息

	 * 
	 * @param context
	 * @param driverLicenseNumber
	 * @param handler
	 */
	public static void searchDriver(Context context, String driverLicenseNumber, final Handler handler) {
		String url = ApiUrl.getDriver(context, driverLicenseNumber);
		Map<String, String> params = new HashMap<String, String>();
		params.put("xmlrequested", "yes");
		DcHttpClient.getInstance().getWithURL(context, url, params, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject object) {
				if (null != object && !object.equals("网络异常")) {
					String DriverStr = object.toString().trim();
					try {
						DriverStr = Utils.unescapeJava(DriverStr);
					} catch (IOException e) {

					}

					if (DriverStr.contains("<DrvPerson") && DriverStr.contains("</DrvPerson>")) {
						int start = DriverStr.indexOf("<DrvPerson");
						int end = DriverStr.indexOf("</DrvPerson>");
						String DriverXML = DriverStr.substring(start, end + 12);
						Map<String, String> xmlMap = Utils.DecodeXMLStringtoMap(DriverXML);
						PersionInfoX persionInfo = Utils.ConvertMapToDriver(xmlMap);
						if (null == persionInfo.sfzmmc || persionInfo.sfzmmc.equals("")) {
							handler.sendMessage(createMsg(ConstantStatus.DRIVER_SEARCH_FALSE, null));
						} else
							handler.sendMessage(createMsg(ConstantStatus.DRIVER_SEARCH_SUCCESS, persionInfo));
					} else
						handler.sendMessage(createMsg(ConstantStatus.DRIVER_SEARCH_FALSE, null));
				} else {
					handler.sendMessage(createMsg(ConstantStatus.DRIVER_SEARCH_FALSE, null));
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				handler.sendMessage(createMsg(ConstantStatus.NetWorkError, null));
			}
		});
	}

	public static void searchDriverPic(Context context, String driverLicenseNumber, final Handler handler) {
		String url = ApiUrl.getDriverPic(context);
		Map<String, String> params = new HashMap<String, String>();
		params.put("sfzh", driverLicenseNumber);
		params.put("xmldata", getDriverNumberXML(driverLicenseNumber));
		DcHttpClient.getInstance().getWithURL(context, url, params, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject object) {
				if (null != object && !object.equals("网络异常")) {
					ResultData.DriverPic driverPic = getGson().fromJson(object.toString(), ResultData.DriverPic.class);
					if (null == driverPic.jiashirenzhaopian || driverPic.jiashirenzhaopian.equals("")) {
						handler.sendMessage(createMsg(ConstantStatus.DRIVERPIC_SEARCH_FALSE, null));
					} else
						handler.sendMessage(createMsg(ConstantStatus.DRIVERPIC_SEARCH_SUCCESS, driverPic.jiashirenzhaopian));
				} else {
					handler.sendMessage(createMsg(ConstantStatus.DRIVERPIC_SEARCH_FALSE, null));
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				handler.sendMessage(createMsg(ConstantStatus.NetWorkError, null));
			}
		});
	}

	/**
	 * 查询机动车信息

	 * 
	 * @param context
	 * @param carPlateNumber
	 * @param carPlateType
	 * @param handler
	 */
	public static void searchVehicleInfo(Context context, String carPlateNumber, String carPlateType, final Handler handler) {
		String url = ApiUrl.getVehicleInfo(context);
		Map<String, String> params = new HashMap<String, String>();
		params.put("carPlateNumber", carPlateNumber);
		params.put("carPlateType", carPlateType);
		params.put("xmlrequested", "yes");
		DcHttpClient.getInstance().postWithURL(context, url, params, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject object) {
				if (null != object && !object.equals("网络异常")) {
					String VehicleStr = object.toString().trim();
					try {
						VehicleStr = Utils.unescapeJava(VehicleStr);
					} catch (IOException e) {

					}

					if (VehicleStr.contains("<veh") && VehicleStr.contains("</veh>")) {
						int start = VehicleStr.indexOf("<veh");
						int end = VehicleStr.indexOf("</veh>");
						String VehicleXML = VehicleStr.substring(start, end + 6);
						Map<String, String> xmlMap = Utils.DecodeXMLStringtoMap(VehicleXML);
						VehicleInfoX vehicleInfo = Utils.ConvertMapToVehicle(xmlMap);
						if (null == vehicleInfo || vehicleInfo.hphm == null || vehicleInfo.hphm.equals("")) {
							handler.sendMessage(createMsg(ConstantStatus.VEHICLE_SEARCH_FALSE, null));
						} else
							handler.sendMessage(createMsg(ConstantStatus.VEHICLE_SEARCH_SUCCESS, vehicleInfo));
					} else
						handler.sendMessage(createMsg(ConstantStatus.VEHICLE_SEARCH_FALSE, null));
				} else {
					handler.sendMessage(createMsg(ConstantStatus.VEHICLE_SEARCH_FALSE, null));
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				handler.sendMessage(createMsg(ConstantStatus.NetWorkError, null));
			}
		});
	}

	/**
	 * 申请简易程序编码ID
	 * 
	 * @param context
	 * @param userCode
	 * @param type
	 * @param handler
	 */
	public static void updatePunishId(final Context context, String userCode, final String type, final DBManager db, final Handler handler) {
		String url = ApiUrl.updatePunishId(context, userCode, type);
		Map<String, String> params = new HashMap<String, String>();
		params.put("depId", SystemCfg.getDepartmentID(context));
		DcHttpClient.getInstance().getWithURL(context, url, params, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject object) {
				if (null != object && !object.equals("网络异常")) {
					List<Number> numbers = getGson().fromJson(object.toString(), ResultData.NumberItem.class).number;
					if (numbers != null && numbers.size() > 0) {
						for (Number number : numbers) {
							db.add(number.code, type);
						}
					}
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				handler.sendMessage(createMsg(ConstantStatus.NetWorkError, null));
			}
		});
	}

	/**
	 * 申请强制措施编码ID
	 * 
	 * @param context
	 * @param userCode
	 * @param type
	 * @param handler
	 */
	public static void updateForceId(final Context context, String userCode, final String type, final DBManager db, final Handler handler) {
		String url = ApiUrl.updatePunishId(context, userCode, type);
		Map<String, String> params = new HashMap<String, String>();
		params.put("depId", SystemCfg.getDepartmentID(context));
		DcHttpClient.getInstance().getWithURL(context, url, params, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject object) {
				if (null != object && !object.equals("网络异常")) {
					List<Number> numbers = new Gson().fromJson(object.toString(), ResultData.NumberItem.class).number;
					if (numbers != null && numbers.size() > 0) {
						for (Number number : numbers) {
							db.add(number.code, type);
						}
					}
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				handler.sendMessage(createMsg(ConstantStatus.NetWorkError, null));
			}
		});
	}

	/**
	 * 得到服务端的版本
	 * 
	 * @param showFalse
	 * @param handler
	 * @param context
	 */
	public static void getVersoin(final boolean showFalse, final Handler handler, final Context context) {
		String url = ApiUrl.updateVersion(context);
		DcHttpClient.getInstance().getWithURL(context, url, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject object) {
				if (object.optInt("status") == 1) {
					InfoCache.getInstance().setmData(((UpdateResult) getGson().fromJson(object.toString(), UpdateResult.class)).getData());
					int local = Integer.parseInt(InfoCache.getInstance().getAppVersionCode(context));
					int current = Integer.parseInt(InfoCache.getInstance().getmData().getCurrentVersionsCode());
					InfoCache.getInstance().setNewVer(local - current < 0);
					if (InfoCache.getInstance().isNewVer()) {
						handler.sendMessage(createMsg(ConstantStatus.updateSuccess, InfoCache.getInstance().getmData()));
					} else {
						if (showFalse) {
							handler.sendMessage(createMsg(ConstantStatus.updateFalse, null));
						}
					}
				} else {
					handler.sendMessage(createMsg(ConstantStatus.NetWorkError, null));
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				handler.sendMessage(createMsg(ConstantStatus.NetWorkError, null));
			}
		});
	}

	/**
	 * 获取白名单数据

	 * 
	 * @param context
	 * @param db
	 */
	public static void getVipCarList(Context context, final DBManager db) {
		String url = ApiUrl.getVipCarList(context);
		DcHttpClient.getInstance().getWithURL(context, url, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject object) {
				if (null != object && !object.equals("网络异常")) {
					try {
						Object lobj = object.get("vipName");
						JSONArray array = new JSONArray(lobj.toString());
						if (array != null) {
							db.clearVipCarData();
							for (int i = 0; i < array.length(); i++) {
								VipCarInfo vi = new VipCarInfo();
								JSONObject singlejson = array.getJSONObject(i);
								vi.platenum = singlejson.getString("platenum");
								vi.platetype = singlejson.getString("platetypecode");
								db.add(vi);
							}
						}
					} catch (JSONException e1) {
						e1.printStackTrace();
						MsgToast.geToast().setMsg("白名单数据异常");
					}
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				MsgToast.geToast().setMsg("获取白名单信息异常");
			}
		});
	}

	/**
	 * 获取黑名单数据

	 * 
	 * @param context
	 * @param db
	 */
	public static void getBlackList(Context context, final DBManager db) {
		String url = ApiUrl.getBlackList(context);
		DcHttpClient.getInstance().getWithURL(context, url, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject object) {
				if (null != object && !object.equals("网络异常")) {
					try {
						Object lobj = object.get("crossBlackInfo");
						JSONArray array = new JSONArray(lobj.toString());
						Object lobj1 = object.get("alarmInfo");
						if (array != null) {
							db.clearBlacklistData();
							for (int i = 0; i < array.length(); i++) {
								BlackList bl = new BlackList();
								JSONObject singlejson = array.getJSONObject(i);
								bl.platenum = singlejson.getString("carPlateNum");
								bl.blacklisttype = singlejson.getString("blackTypeCode");
								if (singlejson.getString("blackTypeCode").equals("A")) {
									bl.blt = lobj1.toString();
								}
								bl.platetype = singlejson.getString("carPlateTypeCode");
								bl.datetime = singlejson.getString("timeVerify");
								bl.brand = singlejson.getString("id");
								db.add(bl);
							}
						}
					} catch (JSONException e1) {
						e1.printStackTrace();
						MsgToast.geToast().setMsg("黑名单数据异常");
					}
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				MsgToast.geToast().setMsg("获取黑名单信息异常");
			}
		});
	}

	/**
	 * 更新勤务类型、检查类型、事故类型、天气类型、黑名单类型、号牌颜色类型、车辆颜色类型数据

	 * 
	 * @param context
	 * @param db
	 */
	public static void getAndSaveTypeData(Context context, final DBManager dbmanager) {
		String url = ApiUrl.getAndSaveTypeData(context);
		DcHttpClient.getInstance().getWithURL(context, url, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject jsonObject) {
				if (null != jsonObject && !jsonObject.equals("网络异常")) {
					try {
						// 黑名单类型

						Object blacktype = jsonObject.get("blacktypecode");
						// 勤务类型
						Object lobj = jsonObject.get("omType");
						// 检查类型

						Object lobj1 = jsonObject.get("checkType");
						// 事故类型
						Object lobj2 = jsonObject.get("accEventType");
						// 天气类型
						Object lobj3 = jsonObject.get("weather");
						// 号牌类型
						Object lobj4 = jsonObject.get("platetype");
						// 车辆颜色类型
						Object lobj5 = jsonObject.get("carcolor");
						// 车辆号牌颜色类型
						Object lobj6 = jsonObject.get("platecolor");
						// 方向表

						Object lobj7 = jsonObject.get("direction");
						// 车辆状态

						Object lobj8 = jsonObject.get("carstate");
						// 车辆性质
						Object lobj9 = jsonObject.get("runtype");
						// 车辆分类
						Object lobj10 = jsonObject.get("clfl");
						// 交通方式

						Object lobj11 = jsonObject.get("jtfs");
						// 处罚种类
						Object lobj12 = jsonObject.get("cfzl");
						// 交款方式
						Object lobj13 = jsonObject.get("jkfs");
						// 交款标记
						Object lobj14 = jsonObject.get("jkbj");
						// 人员分类
						Object lobj15 = jsonObject.get("ryfl");
						// 强制措施类型
						Object lobj16 = jsonObject.get("qcType");
						// 驾驶证状态
						Object lobj17 = jsonObject.get("liencestatus");

						JSONArray array = new JSONArray(lobj.toString());
						JSONArray array1 = new JSONArray(lobj1.toString());
						JSONArray array2 = new JSONArray(lobj2.toString());
						JSONArray array3 = new JSONArray(lobj3.toString());
						JSONArray array4 = new JSONArray(lobj4.toString());
						JSONArray array5 = new JSONArray(lobj5.toString());
						JSONArray array6 = new JSONArray(lobj6.toString());
						JSONArray array7 = new JSONArray(lobj7.toString());
						JSONArray array8 = new JSONArray(lobj8.toString());
						JSONArray array9 = new JSONArray(lobj9.toString());						
						JSONArray array10 = new JSONArray(lobj10.toString());
						JSONArray array11 = new JSONArray(lobj11.toString());
						JSONArray array12 = new JSONArray(lobj12.toString());
						JSONArray array13 = new JSONArray(lobj13.toString());
						JSONArray array14 = new JSONArray(lobj14.toString());
						JSONArray array15 = new JSONArray(lobj15.toString());
						JSONArray array16 = new JSONArray(lobj16.toString());
						JSONArray array17 = new JSONArray(lobj17.toString());

						JSONArray blacktypearray = new JSONArray(blacktype.toString());
						if (array.length() > 0 && array1.length() > 0 && array2.length() > 0 && array3.length() > 0) {
							dbmanager.clearTypeData();
						}
						for (int i = 0; i < array.length(); i++) {
							Type dt = new Type();
							JSONObject singlejson = array.getJSONObject(i);
							dt = (Type) JsonUtil.toBean(singlejson.toString(), Type.class);
							dbmanager.add(dt);
						}
						for (int i = 0; i < array1.length(); i++) {
							Type dt = new Type();
							JSONObject singlejson = array1.getJSONObject(i);
							dt = (Type) JsonUtil.toBean(singlejson.toString(), Type.class);
							dbmanager.add(dt);
						}
						for (int i = 0; i < array2.length(); i++) {
							Type dt = new Type();
							JSONObject singlejson = array2.getJSONObject(i);
							dt = (Type) JsonUtil.toBean(singlejson.toString(), Type.class);
							dbmanager.add(dt);
						}
						for (int i = 0; i < array3.length(); i++) {
							Type dt = new Type();
							JSONObject singlejson = array3.getJSONObject(i);
							dt = (Type) JsonUtil.toBean(singlejson.toString(), Type.class);
							dbmanager.add(dt);
						}

						for (int i = 0; i < array4.length(); i++) {
							Type dt = new Type();
							JSONObject singlejson = array4.getJSONObject(i);
							dt = (Type) JsonUtil.toBean(singlejson.toString(), Type.class);
							dbmanager.add(dt);
						}

						for (int i = 0; i < blacktypearray.length(); i++) {
							Type dt = new Type();
							JSONObject singlejson = blacktypearray.getJSONObject(i);
							dt = (Type) JsonUtil.toBean(singlejson.toString(), Type.class);
							dbmanager.add(dt);
						}
						for (int i = 0; i < array5.length(); i++) {
							Type dt = new Type();
							JSONObject singlejson = array5.getJSONObject(i);
							dt = (Type) JsonUtil.toBean(singlejson.toString(), Type.class);
							dbmanager.add(dt);
						}
						for (int i = 0; i < array6.length(); i++) {
							Type dt = new Type();
							JSONObject singlejson = array6.getJSONObject(i);
							dt = (Type) JsonUtil.toBean(singlejson.toString(), Type.class);
							dbmanager.add(dt);
						}
						for (int i = 0; i < array7.length(); i++) {
							Type dt = new Type();
							JSONObject singlejson = array7.getJSONObject(i);
							dt = (Type) JsonUtil.toBean(singlejson.toString(), Type.class);
							dbmanager.add(dt);
						}
						for (int i = 0; i < array8.length(); i++) {
							Type dt = new Type();
							JSONObject singlejson = array8.getJSONObject(i);
							dt = (Type) JsonUtil.toBean(singlejson.toString(), Type.class);
							dbmanager.add(dt);
						}
						for (int i = 0; i < array9.length(); i++) {
							Type dt = new Type();
							JSONObject singlejson = array9.getJSONObject(i);
							dt = (Type) JsonUtil.toBean(singlejson.toString(), Type.class);
							dbmanager.add(dt);
						}
						for (int i = 0; i < array10.length(); i++) {
							Type dt = new Type();
							JSONObject singlejson = array10.getJSONObject(i);
							dt = (Type) JsonUtil.toBean(singlejson.toString(), Type.class);
							dbmanager.add(dt);
						}
						List<Type> jtfs = new ArrayList<Type>();
						for (int i = 0; i < array11.length(); i++) {
							Type dt = new Type();
							JSONObject singlejson = array11.getJSONObject(i);
							dt = (Type) JsonUtil.toBean(singlejson.toString(), Type.class);
							if (dt.name.equals("小型轿车") || dt.name.equals("大型轿车") || dt.name.equals("电动自行车")
									|| dt.name.equals("普通二轮摩托车") || dt.name.equals("普通正三轮摩托车") || dt.name.equals("普通低速货车"))
								jtfs.add(0, dt);
							else
								jtfs.add(dt);
						}

						for (int i = 0,len = jtfs.size(); i < len; i++) {
							dbmanager.add(jtfs.get(i));
						}

						for (int i = 0; i < array12.length(); i++) {
							Type dt = new Type();
							JSONObject singlejson = array12.getJSONObject(i);
							dt = (Type) JsonUtil.toBean(singlejson.toString(), Type.class);
							dbmanager.add(dt);
						}

						for (int i = 0; i < array13.length(); i++) {
							Type dt = new Type();
							JSONObject singlejson = array13.getJSONObject(i);
							dt = (Type) JsonUtil.toBean(singlejson.toString(), Type.class);
							dbmanager.add(dt);
						}

						for (int i = 0; i < array14.length(); i++) {
							Type dt = new Type();
							JSONObject singlejson = array14.getJSONObject(i);
							dt = (Type) JsonUtil.toBean(singlejson.toString(), Type.class);
							dbmanager.add(dt);
						}
						for (int i = 0; i < array15.length(); i++) {
							Type dt = new Type();
							JSONObject singlejson = array15.getJSONObject(i);
							dt = (Type) JsonUtil.toBean(singlejson.toString(), Type.class);
							dbmanager.add(dt);
						}
						for (int i = 0; i < array16.length(); i++) {
							Type dt = new Type();
							JSONObject singlejson = array16.getJSONObject(i);
							dt = (Type) JsonUtil.toBean(singlejson.toString(), Type.class);
							dbmanager.add(dt);
						}
						for (int i = 0; i < array17.length(); i++) {
							Type dt = new Type();
							JSONObject singlejson = array17.getJSONObject(i);
							dt = (Type) JsonUtil.toBean(singlejson.toString(), Type.class);
							dbmanager.add(dt);
						}
					} catch (JSONException e1) {
						e1.printStackTrace();
						MsgToast.geToast().setMsg("获取基础数据信息异常");
					}
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				MsgToast.geToast().setMsg("获取基础数据信息异常");
			}
		});
	}

	/**
	 * 按需要更新道路数据

	 * 
	 * @param context
	 * @param db
	 */
	public static void getRoadNamesData(Context context, final DBManager db) {
		String url = ApiUrl.getRoadNamesData(context);
		Map<String, String> params = new HashMap<String, String>();
		params.put("depId", SystemCfg.getDepartmentID(context));
		DcHttpClient.getInstance().getWithURL(context, url, params, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject jsonObject) {
				if (null != jsonObject && !jsonObject.equals("网络异常")) {
					try {
						Object infoObj = jsonObject.get("placeDic");
						JSONArray array = new JSONArray(infoObj.toString());
						if (array != null) {
							db.clearRoadNameData();
							for (int i = 0; i < array.length(); i++) {
								JSONObject singlejson = array.getJSONObject(i);
								RoadInfo ri = new RoadInfo();
								ri = (RoadInfo) JsonUtil.toBean(singlejson.toString(), RoadInfo.class);
								db.add(ri);
							}
						}
					} catch (JSONException e1) {
						e1.printStackTrace();
						MsgToast.geToast().setMsg("更新道路数据异常");
					}
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				MsgToast.geToast().setMsg("获取道路信息异常");
			}
		});
	}
	
	/**
	 * 更新违法类型数据
	 * @param context
	 * @param db
	 */
	public static void getVioTypesData(Context context, final DBManager db) {
		String url = ApiUrl.getVioTypesData(context);
		DcHttpClient.getInstance().getWithURL(context, url, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject jsonObject) {
				if (null != jsonObject && !jsonObject.equals("网络异常")) {
					try {
						Object infoObj = jsonObject.get("vioDic");
						JSONArray array = new JSONArray(infoObj.toString());
						if (array != null) {
							db.clearVioTypeData();
							for (int i = 0; i < array.length(); i++) {
								JSONObject singlejson = array.getJSONObject(i);
								VioType tempVio = new VioType();
								tempVio = (VioType) JsonUtil.toBean(singlejson.toString(), VioType.class);
								db.add(tempVio);
							}
						}
					} catch (JSONException e1) {
						e1.printStackTrace();
						MsgToast.geToast().setMsg("更新违法类型数据异常");
					}
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				MsgToast.geToast().setMsg("获取违法类型信息异常");
			}
		});
	}
	
	/**
	 * 更新通讯录数据

	 * @param context
	 * @param db
	 */
	public static void getAddressBook(Context context, final DBManager db) {
		String url = ApiUrl.getAddressBook(context);
		Map<String, String> params = new HashMap<String, String>();
		params.put("depId", SystemCfg.getDepartmentID(context));
		DcHttpClient.getInstance().getWithURL(context, url, params, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject jsonObject) {
				if (null != jsonObject && !jsonObject.equals("网络异常")) {
					try {
						Object lobj = jsonObject.get("addressBook");
						JSONArray array = new JSONArray(lobj.toString());
						if (array != null) {
							db.clearAddressBookData();
							for (int i = 0; i < array.length(); i++) {
								AddressBook ab = new AddressBook();
								JSONObject singlejson = array.getJSONObject(i);
								ab.deptmentid = singlejson.getString("deptment");
								ab.deptmentname = singlejson.getString("deptmentName");
								ab.name = singlejson.getString("name");
								ab.id = singlejson.getString("id");
								ab.phone = singlejson.getString("phone");
								ab.post = singlejson.getString("post");
								ab.remark = singlejson.getString("remark");
								db.add(ab);
							}
						}
					} catch (JSONException e1) {
						e1.printStackTrace();
						MsgToast.geToast().setMsg("更新通讯录数据异常");
					}
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				MsgToast.geToast().setMsg("获取通讯录信息异常");
			}
		});
	}
	
	/**
	 * 更新违法词典数据
	 * @param context
	 * @param db
	 */
	public static void getVioDicData(Context context, final DBManager db, final Handler handler) {
		String url = ApiUrl.getVioDicData(context);
		DcHttpClient.getInstance().getWithURL(context, url, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject jsonObject) {
				if (null != jsonObject && !jsonObject.equals("网络异常")) {
					try {
						Object infoObj = jsonObject.get("vioType");
						handler.sendMessage(createMsg(ConstantStatus.getVioDicData, infoObj));
						return;						
//						JSONArray array = new JSONArray(infoObj.toString());
//						if (array != null) {
//							db.clearVioDicData();
//							for (int i = 0; i < array.length(); i++) {
//								JSONObject singlejson = array.getJSONObject(i);
//								VioDic tempVio = new VioDic();
//								tempVio = (VioDic) JsonUtil.toBean(singlejson.toString(), VioDic.class);
//								db.add(tempVio);
//							}
//						}
					} catch (JSONException e1) {
						e1.printStackTrace();
						MsgToast.geToast().setMsg("更新违法词典数据异常");
					}
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				MsgToast.geToast().setMsg("获取违法词典信息异常");
			}
		});
	}
	
	/**
	 * 更新违禁物品数据
	 * @param context
	 * @param db
	 */
	public static void getArticleInfo(Context context, final DBManager db, final Handler handler) {
		String url = ApiUrl.getArticleInfo(context);
		DcHttpClient.getInstance().getWithURL(context, url, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject jsonObject) {
				if (null != jsonObject && !jsonObject.equals("网络异常")) {
					try {
						Object lobj = jsonObject.get("article");
						handler.sendMessage(createMsg(ConstantStatus.getArticleInfo, lobj));
						return;
//						JSONArray array = new JSONArray(lobj.toString());
//						if (array != null) {
//							db.clearWeijinData();
//							for (int i = 0; i < array.length(); i++) {
//								JSONObject singlejson = array.getJSONObject(i);
//								if (singlejson != null) {
//									WeijinInfo wi = new WeijinInfo();
//									wi = (WeijinInfo) JsonUtil.toBean(singlejson.toString(), WeijinInfo.class);
//									db.add(wi);
//								}
//							}
//						}
					} catch (JSONException e1) {
						e1.printStackTrace();
						MsgToast.geToast().setMsg("更新违禁物品数据异常");
					}
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				MsgToast.geToast().setMsg("获取违禁品信息异常");
			}
		});
	}

	/**
	 * 查询违法信息
	 *
	 * @param context
	 * @param carPlateNumber
	 * @param carPlateType
	 * @param handler
	 */
	public static void searchVioInfo(Context context, String carPlateNumber, String carPlateType, final Handler handler) {
		String url = ApiUrl.getVioInfo(context);
		Map<String, String> params = new HashMap<String, String>();
		params.put("carPlateNumber", carPlateNumber);
		params.put("carPlateType", carPlateType);
		DcHttpClient.getInstance().postWithURL(context, url, params, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject object) {
				if (null != object && !object.equals("网络异常")) {
					List<ResultData.VioInfo> violist = getGson().fromJson(object.toString(), ResultData.VioInfoList.class).violist;
					if (null == violist) {
						handler.sendMessage(createMsg(ConstantStatus.VEHICLE_SEARCH_FALSE, null));
					} else
						handler.sendMessage(createMsg(ConstantStatus.VEHICLE_SEARCH_SUCCESS, violist));
				} else {
					handler.sendMessage(createMsg(ConstantStatus.VEHICLE_SEARCH_FALSE, null));
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				handler.sendMessage(createMsg(ConstantStatus.NetWorkError, null));
			}
		});
	}

	/**
	 * 查询预警卡口列表
	 *
	 * @param context
	 * @param carPlateNumber
	 * @param carPlateType
	 * @param handler
	 */
	public static void searchWarnCross(Context context, final Handler handler) {
		String url = ApiUrl.getWarnCross(context);
		Map<String, String> params = new HashMap<String, String>();
		params.put("depId", SystemCfg.getDepartmentID(context));
		DcHttpClient.getInstance().getWithURL(context, url, params, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject object) {
				if (null != object && !object.equals("网络异常")) {
					Group groupCross = getGson().fromJson(object.toString(), Group.class);
					if (null == groupCross) {
						handler.sendMessage(createMsg(ConstantStatus.SEARCH_CROSS_FAIL, null));
					} else
						handler.sendMessage(createMsg(ConstantStatus.SEARCH_CROSS_SUCCESS, groupCross));
				} else {
					handler.sendMessage(createMsg(ConstantStatus.SEARCH_CROSS_FAIL, null));
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				handler.sendMessage(createMsg(ConstantStatus.NetWorkError, null));
			}
		});
	}

	/**
	 * 查询预警图片
	 *
	 * @param context
	 * @param carPlateNumber
	 * @param carPlateType
	 * @param handler
	 */
	public static void searchWarnPic(Context context, String gcxh, final Handler handler) {
		String url = ApiUrl.getWarnPic(context);
		Map<String, String> params = new HashMap<String, String>();
		params.put("gcxh", gcxh);
		params.put("method", "geturl");
		DcHttpClient.getInstance().postWithURL(context, url, params, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject object) {
				if (null != object && !object.equals("网络异常")) {
					ResultData.PicUrl picUrl = getGson().fromJson(object.toString(), ResultData.PicUrl.class);
					handler.sendMessage(createMsg(ConstantStatus.SEARCH_WARNPIC_SUCCESS, picUrl.picurl));
				} else {
					handler.sendMessage(createMsg(ConstantStatus.SEARCH_WARNPIC_FAIL, null));
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				handler.sendMessage(createMsg(ConstantStatus.SEARCH_WARNPIC_FAIL, null));
//				handler.sendMessage(createMsg(ConstantStatus.SEARCH_WARNPIC_SUCCESS, "/mpts/images/login_box_bg.png"));
				MsgToast.geToast().setMsg("获取信息异常");
			}
		});
	}

	private static Message createMsg(int what, Object obj) {
		Message msg = new Message();
		msg.what = what;
		if (obj != null)
			msg.obj = obj;
		return msg;
	}

	private static Gson gson;

	private static Gson getGson() {
		if (gson == null) {
			gson = new Gson();
		}
		return gson;
	}

	private static String getDriverNumberXML(String DriverNumber) {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version =\"1.0\" encoding =\"GBK\"?>");
		sb.append("<root>");sb.append("<QueryCondition>");
		sb.append("<sfzmhm>");
		sb.append(DriverNumber); // 驾驶证号
		sb.append("</sfzmhm>");
		sb.append("</QueryCondition>");
		sb.append("</root>");
		return sb.toString();
	}
}