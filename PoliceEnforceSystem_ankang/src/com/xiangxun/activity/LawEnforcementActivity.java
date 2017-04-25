package com.xiangxun.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;

import com.xiangxun.activity.home.PublishEnforceMeasureActivity;
import com.xiangxun.activity.home.PublishSummaryPunishActivity;
import com.xiangxun.activity.home.PublishViolationsNoticeActivity;
import com.xiangxun.activity.home.PublishViolationsParkingActivity;
import com.xiangxun.activity.mine.MyInformationActivity;
import com.xiangxun.activity.setting.SettingActivity;
import com.xiangxun.app.BaseActivity;
import com.xiangxun.widget.TitleView;

/**
 * @package: com.xiangxun.activity
 * @ClassName: LawEnforcementActivity.java
 * @Description: 现场执法
 */
public class LawEnforcementActivity extends BaseActivity implements OnClickListener {
	private TitleView titleView;
	private RelativeLayout mRlPublishViolat;
	private RelativeLayout mRlPublishNitice;
	private RelativeLayout mRlPublishEnforce;
	private RelativeLayout mRlPublishSummary;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.law_info_layout);
		initView();
		initData();
		initListener();
	}

	@Override
	public void initView() {
		titleView = (TitleView) findViewById(R.id.tv_comm_title);
		mRlPublishViolat = (RelativeLayout) findViewById(R.id.rl_publish_violation);
		mRlPublishNitice = (RelativeLayout) findViewById(R.id.rl_publish_notice);
		mRlPublishEnforce = (RelativeLayout) findViewById(R.id.rl_publish_enforce);
		mRlPublishSummary = (RelativeLayout) findViewById(R.id.rl_publish_summary);
	}

	@Override
	public void initListener() {
		mRlPublishViolat.setOnClickListener(this);
		mRlPublishEnforce.setOnClickListener(this);
		mRlPublishSummary.setOnClickListener(this);
		mRlPublishNitice.setOnClickListener(this);
		titleView.setLeftBackOneListener(R.drawable.tab_setting, this);
		titleView.setRightViewRightOneListener(R.drawable.user_normal, this);
	}

	@Override
	public void initData() {
		titleView.setLeftBackOneListener(R.drawable.app_icon);
		titleView.setTitle(R.string.xczf);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_view_back_img:
			startActivity(new Intent(this, SettingActivity.class));
			break;
		case R.id.title_view_operation_imageview_right:
			Intent login = new Intent(LawEnforcementActivity.this, MyInformationActivity.class);
			startActivity(login);
			break;
		case R.id.rl_publish_violation:
			startActivity(new Intent(LawEnforcementActivity.this, PublishViolationsParkingActivity.class));
			break;
		case R.id.rl_publish_enforce:
			startActivity(new Intent(LawEnforcementActivity.this, PublishEnforceMeasureActivity.class));
			break;
		case R.id.rl_publish_summary:
			Intent intent2 = new Intent(LawEnforcementActivity.this, PublishSummaryPunishActivity.class);
			intent2.setAction("deal");
			startActivity(intent2);
			break;
		case R.id.rl_publish_notice:
			startActivity(new Intent(LawEnforcementActivity.this, PublishViolationsNoticeActivity.class));
			break;
		}
	}

}
