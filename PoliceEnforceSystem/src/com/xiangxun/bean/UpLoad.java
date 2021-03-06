package com.xiangxun.bean;

import android.content.Context;

import com.xiangxun.activity.R;
import com.xiangxun.util.ConstantStatus;

public class UpLoad {
	public static final int noup = 0;
	public static final int uped = 1;
	public static String picpath = "";

	public static String getUpLoad(int id, Context mContext) {
		switch (id) {
		case ConstantStatus.UpStatusFail:
			return mContext.getResources().getString(R.string.notUploaded);
		case ConstantStatus.UpStatusSuccess:
			return mContext.getResources().getString(R.string.uploaded);
		case ConstantStatus.UpStatusNoAuthority:
			return mContext.getResources().getString(R.string.NoAuthority);
		case ConstantStatus.UpStatusSigned:
			return mContext.getResources().getString(R.string.Signed);
		case ConstantStatus.UpStatusAcked:
			return mContext.getResources().getString(R.string.Acked);
		case ConstantStatus.UpStatusNoinfo:
			return mContext.getResources().getString(R.string.Noinfo);
		case ConstantStatus.UpStatusNoCar:
			return mContext.getResources().getString(R.string.NoCar);
		case ConstantStatus.UpStatusNoStep:
			return mContext.getResources().getString(R.string.NoStep);
		case ConstantStatus.UpStatusExistNumber:
			return "已存在此编号记录";
		case ConstantStatus.UpStatusErrorNumber:
			return "编号错误";
		case ConstantStatus.UpStatusNoRoad:
			return "该行政区划下不存在该道路代码";
		case ConstantStatus.UpStatusErrorKM:
			return "里程数有误";
		case ConstantStatus.UpStatusOther:
			return "未录入此失败信息";
		default:
			return "未录入此失败信息";
		}
	}
}
