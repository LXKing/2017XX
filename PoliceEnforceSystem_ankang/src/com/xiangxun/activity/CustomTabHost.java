package com.xiangxun.activity;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TabHost;

public class CustomTabHost extends TabHost {
	/*private Animation slideLeftIn;
	private Animation slideLeftOut;
	private Animation slideRightIn;
	private Animation slideRightOut;*/

	private int tabCount;

	public CustomTabHost(Context context, AttributeSet attrs) {
		super(context, attrs);
		/*slideLeftIn = AnimationUtils.loadAnimation(context,
				R.anim.slide_left_in);
		slideLeftOut = AnimationUtils.loadAnimation(context,
				R.anim.slide_left_out);
		slideRightIn = AnimationUtils.loadAnimation(context,
				R.anim.slide_right_in);
		slideRightOut = AnimationUtils.loadAnimation(context,
				R.anim.slide_right_out);*/
	}

	public int getTabCount() {
		return tabCount;
	}

	@Override
	public void addTab(TabSpec tabSpec) {
		tabCount++;
		super.addTab(tabSpec);
	}

	@Override
	public void setCurrentTab(int index) {
		int currentTabIndex = getCurrentTab();

		super.setCurrentTab(index);

	}
}
