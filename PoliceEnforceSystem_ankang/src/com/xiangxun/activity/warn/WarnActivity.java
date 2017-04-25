package com.xiangxun.activity.warn;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.xiangxun.activity.R;
import com.xiangxun.activity.mine.MyInformationActivity;
import com.xiangxun.activity.setting.SettingActivity;
import com.xiangxun.activity.setting.WarnSettingActivity;
import com.xiangxun.app.BaseActivity;
import com.xiangxun.widget.TitleView;

/**
 * @package: com.xiangxun.activity
 * @ClassName: DailyServiceActivity.java
 * @Description: 预警
 */
public class WarnActivity extends BaseActivity implements OnClickListener {
	private TitleView titleView;
	private RelativeLayout mRlPublishWarnSet;
	private RelativeLayout mRlPublishWarn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.warn_layout);
		initView();
		initData();
		initListener();
	}

	@Override
	public void initView() {
		titleView = (TitleView) findViewById(R.id.tv_comm_title);
		mRlPublishWarnSet = (RelativeLayout) findViewById(R.id.rl_warn_setting);
		mRlPublishWarn = (RelativeLayout) findViewById(R.id.rl_publish_warn);
	}

	@Override
	public void initData() {
		titleView.setTitle(R.string.jcwarn);
	}

	@Override
	public void initListener() {
		mRlPublishWarnSet.setOnClickListener(this);
		mRlPublishWarn.setOnClickListener(this);
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
		case R.id.rl_warn_setting:
			startActivity(new Intent(this, WarnSettingActivity.class));
			break;
		case R.id.rl_publish_warn:
			startActivity(new Intent(this, WarnListActivity.class));
			break;
		}
	}

}
