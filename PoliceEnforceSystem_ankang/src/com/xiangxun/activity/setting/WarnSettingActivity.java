package com.xiangxun.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiangxun.activity.R;
import com.xiangxun.app.BaseActivity;
import com.xiangxun.cfg.SystemCfg;
import com.xiangxun.util.ConstantStatus;
import com.xiangxun.widget.TitleView;


public class WarnSettingActivity extends BaseActivity {
	private TitleView titleView;
	
	private RelativeLayout turnWarn;
	private TextView isWarn;
	private ImageView turnImage;

	private RelativeLayout mRlCross;
	private TextView selCross;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.warnset);
		initView();
		initData();
		initListener();
	}

	@Override
	public void initView() {
		titleView = (TitleView) findViewById(R.id.tv_comm_title);
		titleView.setTitle(R.string.warnset);

		turnWarn = (RelativeLayout) findViewById(R.id.rl_is_warn);
		isWarn = (TextView) findViewById(R.id.tv_is_warn);
		turnImage = (ImageView) findViewById(R.id.image_is_warn);

		mRlCross = (RelativeLayout) findViewById(R.id.rl_sel_cross);
		selCross = (TextView) findViewById(R.id.tv_sel_cross);
	}

	@Override
	public void initListener() {
		titleView.setLeftBackOneListener(R.drawable.back_normal, new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});

		turnWarn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean value = !SystemCfg.getIsWarn(WarnSettingActivity.this);
				setIsWarnView(value);
				SystemCfg.setIsWarn(WarnSettingActivity.this, value);
			}
		});

		mRlCross.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent();
				intent.setClass(WarnSettingActivity.this, CrossListActivity.class);
				startActivityForResult(intent, ConstantStatus.REQCODE_CROSS);
			}
		});
	}

	@Override
	public void initData() {
		setIsWarnView(SystemCfg.getIsWarn(this));
		selCross.setText(SystemCfg.getWarnCross(this));
	}

	private void setIsWarnView(boolean value) {
		if (value) {
			isWarn.setText(getResources().getString(R.string.warn));
			turnImage.setBackgroundResource(R.drawable.turn_on);
		} else {
			isWarn.setText(getResources().getString(R.string.nowarn));
			turnImage.setBackgroundResource(R.drawable.turn_off);			
		}
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (ConstantStatus.REQCODE_CROSS == requestCode) {
			if (ConstantStatus.RESULTCODE_CROSS == resultCode) {
				selCross.setText(data.getStringExtra("crossnames"));
			}
		}
	}
}
