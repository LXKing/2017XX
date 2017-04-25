package com.xiangxun.widget;

import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiangxun.activity.R;
import com.xiangxun.bean.ListPlateType;

/**
 * @package: com.xiangxun.widget
 * @ClassName: AlarmMsgDialog.java
 * @Description: 黑名单警告自定义dialog
 * @author: HanGJ
 * @date: 2015-8-21 上午10:16:52
 */
public class AlarmMsgDialog extends Dialog {

	private Context mContext;
	private TextView mTvPlateNum;
	private TextView mTvPlateType;
	private TextView mTvDiaBut1;
	private TextView mTvDiaBut2;
	private LinearLayout addAlarmInfo;

	public AlarmMsgDialog(Context context, View.OnClickListener listener) {
		super(context, R.style.msg_dalog);
		mContext = context;
		init();
		initLinster(listener);
	}

	/**
	 * 
	 * @Title:
	 * @Description:
	 * @param:
	 * @return: void
	 * @throws
	 */
	private void initLinster(View.OnClickListener listener) {
		View.OnClickListener listenerCancle = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlarmMsgDialog.this.dismiss();
			}
		};
		mTvDiaBut1.setOnClickListener(listenerCancle);
		mTvDiaBut2.setOnClickListener(listener);
	}

	/**
	 * 
	 * @Title:
	 * @Description:
	 * @param:
	 * @return: void
	 * @throws
	 */
	@SuppressLint("InflateParams")
	private void init() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.alarm_msg_dialog, null);
		mTvPlateNum = (TextView) v.findViewById(R.id.dia_tv_plate_numm);
		mTvPlateType = (TextView) v.findViewById(R.id.dia_tv_plate_type);
		mTvDiaBut1 = (TextView) v.findViewById(R.id.dia_tv_but1);
		mTvDiaBut2 = (TextView) v.findViewById(R.id.dia_tv_but2);
		addAlarmInfo = (LinearLayout) v.findViewById(R.id.ll_add_alarm_info);
		this.setContentView(v);
		this.setCanceledOnTouchOutside(false);
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
		LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
		p.width = (int) (dm.widthPixels * 0.80); // 宽度设置为屏幕的0.95
		getWindow().setAttributes(p); // 设置生效
	}

	/**
	 * @Title:
	 * @Description: 按钮事件
	 * @param: @param listener
	 * @return: void
	 * @throws
	 */
	public void setButLeftListener(View.OnClickListener listener) {
		mTvDiaBut1.setOnClickListener(listener);
	}

	/**
	 * 
	 * @Title:
	 * @Description: 按钮事件
	 * @param: @param listener
	 * @return: void
	 * @throws
	 */
	public void setButRightListener(View.OnClickListener listener) {
		mTvDiaBut2.setOnClickListener(listener);
	}

	public void setAlarmData(Bundle bundle) {
		try {
			addAlarmInfo.removeAllViews();
			addAlarmInfo.removeAllViewsInLayout();
			ListPlateType listPlateType = new ListPlateType();
			mTvPlateNum.setText(bundle.getString("platenum"));
			mTvPlateType.setText(listPlateType.getText(bundle.getString("platetype")));
			JSONArray array = new JSONArray(bundle.getString("viostr"));
			for (int i = 0; i < array.length(); i++) {
				if (array.getJSONObject(i).getString("carPlateNum").equals(bundle.getString("platenum")) && array.getJSONObject(i).getString("carPlateTypeCode").equals(bundle.getString("platetype"))) {
					String[] split = array.getJSONObject(i).getString("infos").split(",");
					View view = ((Activity) mContext).getLayoutInflater().inflate(R.layout.add_alarm_view_layout, null);
					TextView mTvBlackType = (TextView) view.findViewById(R.id.dia_tv_black_type);
					TextView mTvBlackDate = (TextView) view.findViewById(R.id.dia_tv_black_date);
					if (split.length >= 2) {
						mTvBlackType.setText(split[1]);
						mTvBlackDate.setText(split[0]);
						addAlarmInfo.addView(view);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void show() {
		try {
			if (mContext == null || ((Activity) mContext).isFinishing()) {
				return;
			}
			super.show();
		} catch (Exception e) {
		}
	}
}
