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

	private RelativeLayout mRlType;
	private TextView selType;

	private RelativeLayout mRlDirect;
	private TextView selDirect;

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

		mRlType = (RelativeLayout) findViewById(R.id.rl_sel_type);
		selType = (TextView) findViewById(R.id.tv_sel_type);

		mRlDirect = (RelativeLayout) findViewById(R.id.rl_sel_direct);
		selDirect = (TextView) findViewById(R.id.tv_sel_direct);
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
				intent.setClass(WarnSettingActivity.this, WarnCrossListActivity.class);
				startActivityForResult(intent, ConstantStatus.REQCODE_CROSS);
			}
		});

		mRlType.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent();
				intent.setClass(WarnSettingActivity.this, WarnTypeListActivity.class);
				startActivityForResult(intent, ConstantStatus.REQCODE_TYPE);
			}
		});

		mRlDirect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent();
				intent.setClass(WarnSettingActivity.this, WarnDirectListActivity.class);
				startActivityForResult(intent, ConstantStatus.REQCODE_DIRECT);
			}
		});
	}

	@Override
	public void initData() {
		setIsWarnView(SystemCfg.getIsWarn(this));
		selCross.setText(SystemCfg.getWarnCross(this));
		selType.setText(SystemCfg.getWarnType(this));
		selDirect.setText(SystemCfg.getWarnDirect(this));
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
		} else if (ConstantStatus.REQCODE_TYPE == requestCode) {
			if (ConstantStatus.RESULTCODE_TYPE == resultCode) {
				selType.setText(data.getStringExtra("warntypes"));
			}
		} else if (ConstantStatus.REQCODE_DIRECT == requestCode) {
			if (ConstantStatus.RESULTCODE_DIRECT == resultCode) {
				selDirect.setText(data.getStringExtra("warndirects"));
			}
		}
	}
}
