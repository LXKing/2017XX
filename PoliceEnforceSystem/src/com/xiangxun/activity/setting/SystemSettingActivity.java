package com.xiangxun.activity.setting;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.xiangxun.activity.R;
import com.xiangxun.app.BaseActivity;
import com.xiangxun.cfg.SystemCfg;
import com.xiangxun.widget.TitleView;
import com.xiangxun.widget.ViewEditTextEx;

public class SystemSettingActivity extends BaseActivity {
	private TitleView titleView;
	private Button btnset;

	private ViewEditTextEx server;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.syscfg);
		initView();
		initData();
		initListener();
	}

	@Override
	public void initView() {
		titleView = (TitleView) findViewById(R.id.tv_comm_title);
		titleView.setTitle(R.string.cssz);
		btnset = (Button) this.findViewById(R.id.syscfgset);

		server = (ViewEditTextEx) findViewById(R.id.edt_server);
	}

	@Override
	public void initListener() {
		btnset.setOnClickListener(new BtnSetListener());
		titleView.setLeftBackOneListener(R.drawable.back_normal, new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});
	}

	@Override
	public void initData() {
		server.setTextViewText("管理平台地址");
		server.setEditTextOneHint("请输入管理平台IP");
		server.setEditTextTwoHint("请输入管理平台Port");
		server.setEditTextOneText(SystemCfg.getServerIP(this));
		server.setEditTextTwoText(SystemCfg.getServerPort(this));
		server.setEditTextTwoInputMode(InputType.TYPE_CLASS_NUMBER);
	}

	private class BtnSetListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			SystemCfg.setServerIP(SystemSettingActivity.this, server.getEditTextOneText().toString());
			SystemCfg.setServerPort(SystemSettingActivity.this, server.getEditTextTwoText().toString());
			setResult(Activity.RESULT_OK);
			onBackPressed();
		}
	}
}
