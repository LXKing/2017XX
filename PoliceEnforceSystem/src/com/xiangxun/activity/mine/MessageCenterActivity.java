package com.xiangxun.activity.mine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;

import com.xiangxun.activity.R;
import com.xiangxun.app.BaseFragment;
import com.xiangxun.app.BaseFragmentActivity;
import com.xiangxun.db.DBManager;
import com.xiangxun.widget.PagerSlidingTabStrip;
import com.xiangxun.widget.TitleView;

public class MessageCenterActivity extends BaseFragmentActivity {
	private FragmentAdapter mFragmentAdapter;
	private ViewPager mPageVp;
	private TitleView titleView;
	private PagerSlidingTabStrip mPageTabs;
	private static MessageCenterActivity instance;
	private DBManager db;

	public static final boolean isInstanciated() {
		return instance != null;
	}

	public static final MessageCenterActivity instance() {
		if (instance != null)
			return instance;
		throw new RuntimeException("MessageCenterActivity not instantiated yet");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_main);
		instance = this;
		initView();
		initData();
		initListener();
	}

	@Override
	public void initView() {
		titleView = (TitleView) findViewById(R.id.tv_comm_title);
		titleView.setTitle(R.string.ckxx);
		mPageVp = (ViewPager) this.findViewById(R.id.id_page_vp);
		mPageTabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
	}

	@Override
	public void initListener() {
		mPageTabs.setOnPageChangeListener(mFragmentAdapter);
		titleView.setLeftBackOneListener(R.drawable.back_normal, new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});
	}

	@Override
	public void initData() {
		db = DBManager.getInstance();
		mFragmentAdapter = new FragmentAdapter(this.getSupportFragmentManager());
		mPageVp.setAdapter(mFragmentAdapter);
		mPageTabs.setViewPager(mPageVp);
		int lastestMessageType = db.getLastestSysMessageType();
		if (lastestMessageType == -1) {
			setCurrentPosition(0);
		} else {
			switch (lastestMessageType) {
			case 1:
				setCurrentPosition(0);
				break;
			case 2:
				setCurrentPosition(1);
				break;
			case 3:
				setCurrentPosition(2);
				break;
			case 99:
				setCurrentPosition(3);
				break;
			default:
				break;
			}
		}

	}

	public void setCurrentPosition(int currentPosition) {
		mPageVp.setCurrentItem(currentPosition);
	}

	public class FragmentAdapter extends FragmentPagerAdapter implements OnPageChangeListener {

		String[] mTabTitle;
		BaseFragment[] mFragments;
		int mCurrentIndex;

		public FragmentAdapter(FragmentManager fm) {
			super(fm);
			mTabTitle = getResources().getStringArray(R.array.tab_names);
			mFragments = new BaseFragment[mTabTitle.length];
		}

		@Override
		public Fragment getItem(int position) {
			if (mFragments[position] == null || !(mFragments[position] instanceof BaseFragment)) {
				mFragments[position] = FragmentFactory.createFragment(position);
			}
			return mFragments[position];
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mTabTitle[position];
		}

		@Override
		public int getCount() {
			return mTabTitle.length;
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// ViewPager滑动状态改变的回调
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// ViewPager滑动时的回调
		}

		@Override
		public void onPageSelected(int index) {
			// ViewPager页面被选中的回调
			if (index < mFragments.length) {
				mCurrentIndex = index;
				mFragments[index].load();
				mPageVp.setCurrentItem(index);
			}
		}
	}

}
