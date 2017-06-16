package com.xiangxun.activity.mine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.xiangxun.activity.R;
import com.xiangxun.adapter.MessageTypeListAdapter;
import com.xiangxun.app.BaseFragment;
import com.xiangxun.app.BaseFragmentActivity;
import com.xiangxun.db.DBManager;
import com.xiangxun.widget.TitleView;

import java.util.ArrayList;
import java.util.List;

public class NewMessageCenter extends BaseFragmentActivity {
	private TitleView titleView;
	private ListView messageTypeListView;
	private MessageTypeListAdapter listAdapter;
	private ViewPager messagePage;
	private FragmentAdapter mFragmentAdapter;
	private BaseFragment[] mFragments;
	private List<MessagePackageInfo> lists;
	private int curMessageType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_message_center);
		initView();
		initData();
		initListener();
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		titleView = (TitleView) findViewById(R.id.tv_comm_title);
		messageTypeListView = (ListView) findViewById(R.id.messagetype_list);
		messagePage = (ViewPager) findViewById(R.id.message_page);
	}

	private void initMessagePackpage() {
		int messageNo = 0;
		lists = new ArrayList<MessagePackageInfo>();
		String[] mTabTitle = getResources().getStringArray(R.array.tab_names);
		for (int i = 0, len = mTabTitle.length; i < len; i ++) {
			MessagePackageInfo message = new MessagePackageInfo();
			message.msgtype = mTabTitle[i];

			if ("其他消息".equals(message.msgtype))
				messageNo = 99;
			else
				messageNo = i + 1;

			message.allCount = DBManager.getInstance().getSmsgCount(messageNo);
			message.noReadCount = DBManager.getInstance().getSmsgNoReadCount(messageNo);

			lists.add(message);
		}
	}

	private void updateMessageReadStatus(int messageType) {
		int messageNo = 0;
		MessagePackageInfo message = lists.get(messageType);

		if (3 == messageType)
			messageNo = 99;
		else
			messageNo = messageType + 1;

		message.allCount = DBManager.getInstance().getSmsgCount(messageNo);
		message.noReadCount = DBManager.getInstance().getSmsgNoReadCount(messageNo);

		lists.set(messageType, message);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		titleView.setTitle(R.string.ckxx);
		initMessagePackpage();
		
		mFragments = new BaseFragment[lists.size()];
		listAdapter = new MessageTypeListAdapter(lists, this);
		messageTypeListView.setAdapter(listAdapter);

		mFragmentAdapter = new FragmentAdapter(this.getSupportFragmentManager());
		messagePage.setAdapter(mFragmentAdapter);

		messageTypeListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				messagePage.setCurrentItem(position);
				titleView.setTitle(lists.get(position).msgtype);
				messageTypeListView.setVisibility(View.GONE);
				messagePage.setVisibility(View.VISIBLE);
				curMessageType = position;
				messagePage.invalidate();
			}
		});

		messagePage.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageScrolled(int i, float v, int i1) {
//				titleView.setTitle(mFragmentAdapter.getPageTitle(i).toString());
			}

			@Override
			public void onPageSelected(int i) {
				titleView.setTitle(mFragmentAdapter.getPageTitle(i).toString());
			}

			@Override
			public void onPageScrollStateChanged(int i) {
			}
		});
	}
	
	@Override
	public void initListener() {
		// TODO Auto-generated method stub
		titleView.setLeftBackOneListener(R.drawable.back_normal, new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (View.VISIBLE == messagePage.getVisibility()) {
					messageTypeListView.setVisibility(View.VISIBLE);
					messagePage.setVisibility(View.GONE);
					titleView.setTitle(R.string.ckxx);
					updateMessageReadStatus(curMessageType);
					listAdapter.notifyDataSetChanged();
				} else
					onBackPressed();
			}
		});
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (View.VISIBLE == messagePage.getVisibility()) {
			messageTypeListView.setVisibility(View.VISIBLE);
			messagePage.setVisibility(View.GONE);
			titleView.setTitle(R.string.ckxx);
			updateMessageReadStatus(curMessageType);
			listAdapter.notifyDataSetChanged();
		} else		
			super.onBackPressed();
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
				messagePage.setCurrentItem(index);
			}
		}
	}
	
	public class MessagePackageInfo {
		public String msgtype;
		public int allCount;
		public int noReadCount; 
	}
}
