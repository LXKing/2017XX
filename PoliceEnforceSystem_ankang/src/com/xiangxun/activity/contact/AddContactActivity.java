package com.xiangxun.activity.contact;

import org.linphone.EditContactFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.xiangxun.activity.R;
import com.xiangxun.app.BaseFragmentActivity;
import com.xiangxun.widget.TitleView;

/**
 * @package: com.xiangxun.activity.contact
 * @ClassName: AddContactActivity.java
 * @Description:新增联系人功能模块。
 * @author: ZhangYH
 * @date: 2017-4-14 下午1:42:59
 */
public class AddContactActivity extends BaseFragmentActivity implements OnClickListener {
	private TitleView titleView;

	private EditContactFragment fragment;
	
	private static AddContactActivity instance;
	
	public static final boolean isInstanciated() {
		return instance != null;
	}

	public static final AddContactActivity instance() {
		if (instance != null)
			return instance;
		throw new RuntimeException("AddContactActivity not instantiated yet");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.contact_activity);
		instance = this;
		initView();
		initData();
		initListener();
	}

	@Override
	public void initView() {
		titleView = (TitleView) findViewById(R.id.contact_title);
	}

	@Override
	public void initData() {
		titleView.setTitle("新增联系人");
		Bundle extras = getIntent().getBundleExtra("Bundle");
		if (fragment == null) {
			fragment = new EditContactFragment();
		}
		fragment.setArguments(extras);
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.add(R.id.contact_fragment, fragment);
		ft.commit();
	}

	@Override
	public void initListener() {
		titleView.setLeftBackOneListener(R.drawable.back_normal, this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.title_view_back_img:
			onBackPressed();
			break;
		}
	}
}
