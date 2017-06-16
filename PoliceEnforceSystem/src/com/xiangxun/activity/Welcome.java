package com.xiangxun.activity;

import com.xiangxun.activity.setting.SetGuide;
import com.xiangxun.cfg.SystemCfg;
import com.xiangxun.util.ConstantStatus;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class Welcome extends Activity {

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case ConstantStatus.SUCCESS:
				switchToOther();
				break;
			};
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		if (SystemCfg.getIsFirstLogion(this)) {
			handler.sendEmptyMessageDelayed(ConstantStatus.SUCCESS, 2000);
		} else {
			handler.sendEmptyMessage(ConstantStatus.SUCCESS);
		}
		
	}

	private void switchToOther () {
		Intent intent = new Intent();
		if (SystemCfg.getIsFirstLogion(this)) {
			intent.setClass(this, SetGuide.class);
		} else {
			intent.setClass(this, LoginActivity.class);
		}
		startActivity(intent);
		Welcome.this.finish();
	}
}