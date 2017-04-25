package com.xiangxun.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * @package: com.xiangxun.adapter
 * @ClassName: ViewPagerAdapter.java
 * @Description: ViewPager适配器
 * @author: HanGJ
 * @date: 2015-8-13 上午8:29:54
 */
public class ViewPagerAdapter extends PagerAdapter {
	private List<View> viewList;

	public ViewPagerAdapter(List<View> viewList) {
		this.viewList = viewList;
	}

	@Override
	public int getCount() {
		return viewList.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(viewList.get(position), 0);
		return viewList.get(position);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(viewList.get(position));
	}

}
