package com.xiangxun.activity.Daily;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.xiangxun.activity.R;
import com.xiangxun.activity.mine.MyInformationActivity;
import com.xiangxun.activity.setting.SettingActivity;
import com.xiangxun.app.BaseActivity;
import com.xiangxun.widget.TitleView;
/**
 * @package: com.xiangxun.activity
 * @ClassName: DailyServiceActivity.java
 * @Description: 日常勤务
 */
public class DailyServiceActivity extends BaseActivity implements OnClickListener {
	private TitleView titleView;
	private RelativeLayout mRlPublishDaily;
	private RelativeLayout mRlPublishLaw;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.daily_service_layout);
		initView();
		initData();
		initListener();
	}

	@Override
	public void initView() {
		titleView = (TitleView) findViewById(R.id.tv_comm_title);
		mRlPublishDaily = (RelativeLayout) findViewById(R.id.rl_publish_daily);
		mRlPublishLaw = (RelativeLayout) findViewById(R.id.rl_publish_law);
	}

	@Override
	public void initData() {
		titleView.setTitle(R.string.qw);
	}

	@Override
	public void initListener() {
		mRlPublishDaily.setOnClickListener(this);
		mRlPublishLaw.setOnClickListener(this);
		titleView.setRightViewRightOneListener(R.drawable.user_normal, this);
		titleView.setLeftBackOneListener(R.drawable.tab_setting, this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_view_back_img:
			startActivity(new Intent(this, SettingActivity.class));
			break;
		case R.id.title_view_operation_imageview_right:
			startActivity(new Intent(this, MyInformationActivity.class));
			break;
		case R.id.rl_publish_daily:
			startActivity(new Intent(this, PublishDailyAffairsActivity.class));
			break;
		case R.id.rl_publish_law:
			startActivity(new Intent(this, PublishLawEnforceCheckActivity.class));
			break;
		}
	}

}
