package com.xiangxun.activity.mine;

import com.xiangxun.app.BaseFragment;

public class FragmentFactory {
	public static final int ALARTNOTICE = 0;
	public static final int QINWUNOTICE = 1;
	public static final int ACCIDENTNOTICE = 2;
	public static final int OTHERNOTICE = 3;

	public static BaseFragment createFragment(int subId) {
		BaseFragment fragment = null;
		switch (subId) {
		case ALARTNOTICE:
			fragment = SystemMessageFragment.newInstance(1); 	//new SystemMessageFragment(1);
			break;
		case QINWUNOTICE:
			fragment = SystemMessageFragment.newInstance(2);
			break;
		case ACCIDENTNOTICE:
			fragment = SystemMessageFragment.newInstance(3);
			break;
		case OTHERNOTICE:
			fragment = SystemMessageFragment.newInstance(99);
			break;
		default:
			break;
		}
		return fragment;
	}
}
