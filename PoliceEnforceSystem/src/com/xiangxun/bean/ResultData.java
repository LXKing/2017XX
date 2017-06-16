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
		public String code;			//部门编码
		public String memo;			//用户备注信息 组长警号+名字
	}

	public static class ParentInfos {
		public String id;//用户ID
		public String code;//账号
		public String name;//用户名
	}

	public static class JiaoDaoYuan {
		public String code;	//教导员警号
		public String name;	//教导员姓名
	}
//	"jiaodaoyuan": {
//		"id": "1701031149048270a7aeea808f60ae7c",
//				"code": "068001",
//				"types": "001",
//				"name": "杨忠生",
//				"orgId": "00",
//				"postCode": "005",
//				"telPhone": "15609118168"
//	}

	public static class LoginData {
		public UserInfos userinfos;
		public ParentInfos parentDept;
		public JiaoDaoYuan jiaodaoyuan;
		public Login login;
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

	public static class RoadStep{
		public String lddm;
		public String ldmc;
	}

	public  static class RoadExt{
		public String xzqh;
		public List<RoadStep> ldlist;
	}

	public  static class DriverPic{
		public String jiashirenzhaopian;
	}
}
