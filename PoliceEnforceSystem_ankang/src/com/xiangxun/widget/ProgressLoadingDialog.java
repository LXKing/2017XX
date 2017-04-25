package com.xiangxun.widget;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.xiangxun.activity.R;

public class ProgressLoadingDialog extends Dialog {
	private ProgressImageView mFivIcon;
	private TextView mHtvText;
	private String mText;

	public ProgressLoadingDialog(Context context, String text) {
		super(context, R.style.Theme_Light_FullScreenDialogAct);
		mText = text;
		init();
	}

	private void init() {
		setContentView(R.layout.common_progress_loading_diloag);
		setCancelable(false);
		mFivIcon = (ProgressImageView) findViewById(R.id.loadingdialog_fiv_icon);
		mHtvText = (TextView) findViewById(R.id.loadingdialog_htv_text);
		mFivIcon.startAnimation();
		mHtvText.setText(mText);
	}

	public void setText(String text) {
		mText = text;
		mHtvText.setText(mText);
	}

	@Override
	public void dismiss() {
		if (isShowing()) {
			super.dismiss();
		}
	}

	@Override
	public void show() {
		super.show();
	}

}