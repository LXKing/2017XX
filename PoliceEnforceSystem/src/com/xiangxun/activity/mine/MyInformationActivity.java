package com.xiangxun.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
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
	private TextView mTvNum;
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
		mTvNum = (TextView) findViewById(R.id.policenum);
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

		mTvAccount.setText(Html.fromHtml(String.format((getResources().getString(R.string.police_account)), SystemCfg.getAccount(this))));
		mTvName.setText(Html.fromHtml(String.format((getResources().getString(R.string.police_name)), SystemCfg.getPoliceName(this))));
		mTvNum.setText(Html.fromHtml(String.format((getResources().getString(R.string.police_num)), SystemCfg.getPoliceCode(this))));
		mTvDepartment.setText(Html.fromHtml(String.format((getResources().getString(R.string.police_department)), SystemCfg.getDepartment(this))));
		mTvPhone.setText(Html.fromHtml(String.format((getResources().getString(R.string.police_phone)), SystemCfg.getMobile(this))));
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
