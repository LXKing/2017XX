package com.xiangxun.widget;

import com.xiangxun.activity.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


/**
 * @package: com.xiangxun.widget
 * @ClassName: LoadDialog.java
 * @Description: 加载等待框
 * @author: HanGJ
 * @date: 2015-9-8 上午11:14:43
 */
public class LoadDialog extends Dialog {
	private Context mContext;
	private TextView mTitle;

	public LoadDialog(Context context) {
		super(context, R.style.msg_dalog);
		mContext = context;
		init();
	}

	public LoadDialog(Context context, int theme) {
		super(context, theme);
		mContext = context;
		init();
	}

	private void init() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.load_dialog, null);
		mTitle = (TextView) v.findViewById(R.id.tv_loading_title);
		this.setContentView(v);
	}
	
	public void setTitle(String title){
		mTitle.setText(title);
	}

}
