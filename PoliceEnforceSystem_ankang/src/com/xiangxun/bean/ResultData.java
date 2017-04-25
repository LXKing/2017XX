package com.xiangxun.bean;

import java.util.List;

public class ResultData {
	public static class Number {
		public String id;
		public String userId;
		public String code;
		public String isOccupy;
		public String isUser;
	}

	public static class NumberItem {
		public List<Number> number;
	}

	public static class ImageBean {
		public String file_id;
		public String prefix;
		public String url;
	}

	public static class Login {
		public String res;
		public String sessionid;
	}

	public static class UserInfos {
		public String id;//用户ID
		public String account;//账号
		public String pwd;//密码
		public String name;//用户名
		public String deptid;//部门ID
		public String code;//部门编号
		public String puserId;//警员ID
		public String iprule;//IP
		public String puserCode;//警员编号
		public String puserName;//警员名称
		public String deptName;//部门名称
		public String mobile;//警员电话
		public String regCode;//车牌识别注册码
		public String topName; //打印票头
		public String againDiscussDepartment; //复议机关
		public String courtName; //行政法院
		public String punishAddress; //处罚地点
	}

	public static class ParentInfos {
		public String id;//用户ID
		public String code;//账号
		public String name;//用户名
	}

	public static class LoginData {
		public UserInfos userinfos;
		public ParentInfos parentDept;
		public Login login;
	}

	public static class PersionInfo {
		public String birthday;
		public String telphoe;
		public String drvierLicenseDate;
		public String dabh;
		public String ljjf;
		public String zt;
		public String cclzrq;
		public String syrq;
		public String yxqs;
		public String yxqz;
		public String xzqh;
		public String zzzm;
		public String gxsj;
		public String zxbh;

		public String id;
		public String name;
		public String sex;
		public String driverLicense;
		public String address;
		public String birthdayStr;
		public String firstGetDateStr;
		public String drvierType;
		public String usefulBeginDateStr;
		public String usefulDate;
		public String recordNo;
		public String licenseDepartment;
		public String addPoint;
	}

	public static class Persion {
		public PersionInfo persionInfo;
	}

	public static class VehicleInfo {
		public String engineNo;
		public String addressDetail;
		public String contactTel;
		public String yxqz;
		public String ccdjrq;

		public String cartype;
		public String carNum;
		public String carNumType;
		public String userName;
		public String address;
		public String useNature;
		public String carBrand;
		public String distinguishCode;
		public String carColorCode;
		public String shelfNo;
		public String certiOffice;
		public String factoryDateStr;
		public String checkEndDateStr;
		public String insureFinishDateStr;
		public String carStatus;
		public String qualifiedNo;
	}

	public static class Vehicle {
		public VehicleInfo carInfo;
	}

	public static class VioInfo {
		public String wfxw;
		public String kf_name;
		public String wfsj;
		public String wfdz;
		public String zqmj;
		public String kf;
		public String ysfkje;
		public String jkbj_value;
		public String jkbj;
	}

	public static class VioInfoList {
		public List<VioInfo> violist;
	}

	public static class ArticleType{
		public String id;
		public String uncode;
		public String name;
		public String type;
		public String features;
		public String disposeMode;
		public String lawProvision;
	}
	
	public static class ArticleList {
		public int count;
		public List<ArticleType> article;
	}
	
	public static class ArticleListX {
		public List<ArticleType> article;
	}

	public  static class GroupCross{
		public String group_name;
		public int department_total;
		public List<Group> department_list;
	}

	public  static class PicUrl{
		public String picurl;
	}
}
