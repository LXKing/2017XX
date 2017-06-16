package com.xiangxun.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.xiangxun.activity.R;

/**
 * @package: com.xiangxun.app
 * @ClassName: BaseFragment.java
 * @Description: 基础Fragment
 * @author: HanGJ
 * @date: 2015-7-30 下午2:41:13
 */
public abstract class BaseFragment extends Fragment {
	public String mTitleName = "TIELENAME";

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		int4Right();
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
		int4Right();
	}

	public void startActivityForResult(Intent intent, int requestCode, int defautl) {
		super.startActivityForResult(intent, requestCode);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	public void int4Right() {
		getActivity().overridePendingTransition(R.anim.right_in, R.anim.activity_nothing);
	}

	public abstract void initView(View view);

	public abstract void initListener();

	public abstract void initData();
	public abstract void load();

}