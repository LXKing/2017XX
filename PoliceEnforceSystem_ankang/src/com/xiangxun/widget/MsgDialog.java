package com.xiangxun.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.xiangxun.activity.R;

/**
 * @package: com.xiangxun.widget
 * @ClassName: MsgDialog.java
 * @Description: 自定义dialog
 * @author: HanGJ
 * @date: 2015-7-28 下午2:05:45
 */
public class MsgDialog extends Dialog {

	private Context mContext;
	private TextView mTvDiaTitle;
	private TextView mTvDiaMsg;
	private TextView mTvDiaMsg2;
	private TextView mTvDiaMsg3;
	private TextView mTvDiaBut1;
	private TextView mTvDiaBut2;
	private View mVerLine;

	public MsgDialog(Context context) {
		super(context, R.style.msg_dalog);
		mContext = context;
		init();
		initLinster();
	}

	/**
	 * 
	 * @Title:
	 * @Description:
	 * @param:
	 * @return: void
	 * @throws
	 */
	private void initLinster() {
		View.OnClickListener listener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MsgDialog.this.dismiss();
			}
		};
		mTvDiaBut1.setOnClickListener(listener);
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
		View v = inflater.inflate(R.layout.msg_dialog, null);
		mTvDiaTitle = (TextView) v.findViewById(R.id.dia_tv_title);
		mTvDiaMsg = (TextView) v.findViewById(R.id.dia_tv_msg);
		mTvDiaMsg2 = (TextView) v.findViewById(R.id.dia_tv_msg2);
		mTvDiaMsg3 = (TextView) v.findViewById(R.id.dia_tv_msg3);
		mTvDiaBut1 = (TextView) v.findViewById(R.id.dia_tv_but1);
		mTvDiaBut2 = (TextView) v.findViewById(R.id.dia_tv_but2);
		mVerLine = v.findViewById(R.id.view_ver_line);
		this.setContentView(v);
		this.setCanceledOnTouchOutside(false);
		// WindowManager m = ((Activity) mContext).getWindowManager();
		// Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高

		DisplayMetrics dm = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
		LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
		// p.height = (int) (d.getHeight() * 0.6); // 高度设置为屏幕的0.6
		p.width = (int) (dm.widthPixels * 0.80); // 宽度设置为屏幕的0.95

		getWindow().setAttributes(p); // 设置生效
	}

	/**
	 * 
	 * @Title:
	 * @Description:
	 * @param:
	 * @return: void
	 * @throws
	 */
	public void setMsg(CharSequence text) {
		mTvDiaMsg.setVisibility(View.VISIBLE);
		mTvDiaMsg.setText(text);
	}

	public void setMsg(int text) {
		mTvDiaMsg.setVisibility(View.VISIBLE);
		mTvDiaMsg.setText(text);
	}
	
	public void setMsgColor(int color) {
		mTvDiaMsg.setTextColor(color);
	}
	/**
	 * 
	 * @Title:
	 * @Description:
	 * @param:
	 * @return: void
	 * @throws
	 */
	public void setMsg2(CharSequence text) {
		mTvDiaMsg2.setVisibility(View.VISIBLE);
		mTvDiaMsg2.setText(text);
	}

	public void setMsg2(int text) {
		mTvDiaMsg2.setVisibility(View.VISIBLE);
		mTvDiaMsg2.setText(text);
	}

	/**
	 * 
	 * @Title:
	 * @Description:
	 * @param:
	 * @return: void
	 * @throws
	 */
	public void setMsg3(CharSequence text) {
		mTvDiaMsg3.setVisibility(View.VISIBLE);
		mTvDiaMsg3.setText(text);
	}

	/**
	 * 
	 * @Title:
	 * @Description:
	 * @param: @param text
	 * @return: void
	 * @throws
	 */
	public void setTiele(CharSequence text) {
		mTvDiaTitle.setText(text);
	}

	/**
	 * 
	 * @Title:
	 * @Description:
	 * @param: @param text
	 * @return: void
	 * @throws
	 */
	public void setButLeft(CharSequence text) {
		mTvDiaBut1.setText(text);
	}

	/**
	 * 
	 * @Title:
	 * @Description:
	 * @param: @param text
	 * @return: void
	 * @throws
	 */
	public void setButRight(CharSequence text) {
		mTvDiaBut2.setText(text);
	}

	/**
	 * 
	 * @Title:
	 * @Description:
	 * @param: @param text
	 * @return: void
	 * @throws
	 */
	public void setButRightColor(int color) {
		mTvDiaBut2.setTextColor(color);
	}

	/**
	 * 
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

	public void setOnlyOneBut() {
		mTvDiaBut1.setVisibility(View.GONE);
		mTvDiaBut2.setBackgroundResource(R.drawable.hit_selector_one);
		mVerLine.setVisibility(View.GONE);
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
