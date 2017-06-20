package com.xiangxun.activity.tool;

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

public class ToolsActivity extends BaseActivity implements OnClickListener {
	private TitleView titleView;
	private RelativeLayout mRlDataDictionary;
	private RelativeLayout mRlContacts;
	private RelativeLayout mVideo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tools_info_layout);
		initView();
		initData();
		initListener();
	}

	@Override
	public void initView() {
		titleView = (TitleView) findViewById(R.id.tv_comm_title);
		mRlDataDictionary = (RelativeLayout) findViewById(R.id.rl_data_dictionary);
		mRlContacts = (RelativeLayout) findViewById(R.id.rl_contacts);
		mVideo = (RelativeLayout) findViewById(R.id.rl_video);
	}

	@Override
	public void initListener() {
		mRlDataDictionary.setOnClickListener(this);
		mRlContacts.setOnClickListener(this);
		mVideo.setOnClickListener(this);
		titleView.setRightViewRightOneListener(R.drawable.user_normal, this);
		titleView.setLeftBackOneListener(R.drawable.tab_setting, this);
	}

	@Override
	public void initData() {
		titleView.setTitle(R.string.tools);
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
			case R.id.rl_data_dictionary:
				startActivity(new Intent(ToolsActivity.this, DataDictionaryActivity.class));
				break;
			case R.id.rl_contacts:
				startActivity(new Intent(ToolsActivity.this, ContactsActivity.class));
				break;
			case R.id.rl_video:
				startActivity(new Intent(ToolsActivity.this, VideoRecordActivity.class));
				break;
		}
	}

}
