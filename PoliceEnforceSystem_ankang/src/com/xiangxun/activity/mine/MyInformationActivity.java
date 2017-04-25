package com.xiangxun.activity.mine;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiangxun.activity.R;
import com.xiangxun.app.BaseActivity;
import com.xiangxun.cfg.SystemCfg;
import com.xiangxun.widget.TitleView;

public class MyInformationActivity extends BaseActivity implements OnClickListener {
	private TitleView titleView;
	private RelativeLayout mRlWorkload;
	private RelativeLayout mRlMessage;
	private TextView mTvAccount;
	private TextView mTvName;
	private TextView mTvDepartment;
	private TextView mTvPhone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_info_layout);
		initView();
		initData();
		initListener();
	}

	@Override
	public void initView() {
		titleView = (TitleView) findViewById(R.id.tv_comm_title);

		mTvAccount = (TextView) findViewById(R.id.account);
		mTvName = (TextView) findViewById(R.id.realname);
		mTvDepartment = (TextView) findViewById(R.id.region);
		mTvPhone = (TextView) findViewById(R.id.phone);

		mRlWorkload = (RelativeLayout) findViewById(R.id.rl_workload_statistics);
		mRlMessage = (RelativeLayout) findViewById(R.id.rl_message_center);
	}

	@Override
	public void initListener() {
		mRlWorkload.setOnClickListener(this);
		mRlMessage.setOnClickListener(this);
		titleView.setLeftBackOneListener(R.drawable.back_normal, new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});
	}

	@Override
	public void initData() {
		String str;
		SpannableString ss;
		int color = getResources().getColor(R.color.color666666);

		titleView.setTitle(R.string.wdxx);

		str = String.format(getResources().getString(R.string.police_account), SystemCfg.getAccount(this));
		ss = new SpannableString(str);
		ss.setSpan(new ForegroundColorSpan(Color.BLUE), 4, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		mTvAccount.setText(ss);

		str = String.format(getResources().getString(R.string.police_name), SystemCfg.getPoliceName(this),SystemCfg.getPoliceCode(this));
		ss = new SpannableString(str);
		int index = str.indexOf("警号");
		ss.setSpan(new ForegroundColorSpan(color), 3, index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		ss.setSpan(new ForegroundColorSpan(color), index+3, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		mTvName.setText(ss);

		str = String.format(getResources().getString(R.string.police_department), SystemCfg.getDepartment(this));
		ss = new SpannableString(str);
		ss.setSpan(new ForegroundColorSpan(color), 5, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		mTvDepartment.setText(ss);

		str = String.format(getResources().getString(R.string.police_phone), SystemCfg.getMobile(this));
		ss = new SpannableString(str);
		ss.setSpan(new ForegroundColorSpan(color), 5, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		mTvPhone.setText(ss);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.rl_workload_statistics:
				startActivity(new Intent(MyInformationActivity.this, WorkloadStatisticsActivity.class));
				break;
			case R.id.rl_message_center:
				//startActivity(new Intent(MyInformationActivity.this, MessageCenterActivity.class));
				startActivity(new Intent(MyInformationActivity.this, NewMessageCenter.class));
				break;
		}
	}
}
