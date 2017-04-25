package com.xiangxun.activity.mine.detail;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiangxun.activity.R;
import com.xiangxun.adapter.ViewPagerAdapter;
import com.xiangxun.app.BaseActivity;
import com.xiangxun.bean.Type;
import com.xiangxun.cfg.SystemCfg;
import com.xiangxun.common.LocalNetWorkView;
import com.xiangxun.db.DBManager;
import com.xiangxun.util.ImageCacheLoader;
import com.xiangxun.util.ImageCacheLoader.GetLocalCallBack;
import com.xiangxun.util.Logger;
import com.xiangxun.util.Tools;
import com.xiangxun.widget.FullScreenSlidePopupWindow;
import com.xiangxun.widget.TitleView;

import java.util.ArrayList;
import java.util.List;

/**
 * @package: com.xiangxun.activity
 * @ClassName: DailyAffairsActivity.java
 * @Description: 日常事务详情页面
 * @date: 2015-7-29 上午10:46:18
 */
public class DailyAffairsDetailActivity extends BaseActivity implements OnClickListener, OnPageChangeListener {
	private Context mContext;
	private TitleView titleView;
	private List<String> photoPaths;
	private ArrayList<Type> dts = new ArrayList<Type>();
	private List<View> mViewPagerViews = null;
	private ViewPager mVpDetailPicture = null;
	private FrameLayout mFlViewPagerShow = null;
	private TextView mTvCurrentPage = null;
	private TextView mTvTotalPage = null;
	private int mCurrentPage = 0;
	
	private TextView mTvType;
	private TextView mTvAddress;
	private TextView mTvRoadLocation;
	private TextView mTvRoadDirect;
	private TextView mTvDate;
	private TextView mTvDiscribe;
	private FullScreenSlidePopupWindow slidePopupWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_daily_affairs_layout);
		mContext = this;
		initView();
		initData();
		initListener();
	}

	@Override
	public void initView() {
		titleView = (TitleView) findViewById(R.id.tv_comm_title);
		titleView.setLeftBackOneListener(R.drawable.back_normal, this);
		titleView.setTitle(R.string.rcqw);
		mTvType = (TextView) findViewById(R.id.tv_affairs_type);
		mTvAddress = (TextView) findViewById(R.id.tv_affairs_address);
		mTvRoadLocation = (TextView) findViewById(R.id.tv_affairs_road_location);
		LinearLayout ll_publish_click_address = (LinearLayout) findViewById(R.id.ll_publish_click_address);
		LinearLayout ll_publish_click_RoadDirect = (LinearLayout) findViewById(R.id.ll_publish_click_road_direct);
		if (getResources().getString(R.string.workroad_city).equalsIgnoreCase(SystemCfg.getWorkRoad(this))) {
			ll_publish_click_address.setVisibility(View.GONE);
			ll_publish_click_RoadDirect.setVisibility(View.GONE);
		}		
		mTvRoadDirect = (TextView) findViewById(R.id.tv_affairs_road_direct);
		mTvDate = (TextView) findViewById(R.id.tv_affairs_date);
		mTvDiscribe = (TextView) findViewById(R.id.tv_affairs_discribe);
		mVpDetailPicture = (ViewPager) findViewById(R.id.vp_common_pictures);
		//设置ViewPager显示图片宽高比例为 4:3
		LayoutParams params = mVpDetailPicture.getLayoutParams();
		params.width = Tools.getScreenPixel(this)[0];
		params.height = (params.width * 3) / 4;
		mVpDetailPicture.setLayoutParams(params);
		mFlViewPagerShow = (FrameLayout) findViewById(R.id.fl_common_view_pager);
		mTvCurrentPage = (TextView) findViewById(R.id.tv_common_current_page);
		mTvTotalPage = (TextView) findViewById(R.id.tv_common_total_page);
	}

	@Override
	public void initListener() {
		mVpDetailPicture.setOnPageChangeListener(this);
	}

	@Override
	public void initData() {
		dts = DBManager.getInstance().getDutyTypes();
		Bundle bundle = getIntent().getExtras();
		int typeId = bundle.getInt("patorltype");
		mTvType.setText(dts.get(typeId).name);
		mTvAddress.setText(bundle.getString("roadname"));
		mTvRoadLocation.setText(bundle.getString("roadlocation"));
		mTvRoadDirect.setText(DBManager.getInstance().getRoadDirectionNameByCode(bundle.getString("roaddirect")));
		mTvDate.setText(bundle.getString("datetime"));
		String memo = bundle.getString("memo");
		if(memo != null && memo.length() > 0){
			mTvDiscribe.setText(memo);
		} else {
			mTvDiscribe.setText("暂无");
		}
		photoPaths = new ArrayList<String>();
		if (!bundle.getString("pic1").equals("")) {
			photoPaths.add(0, bundle.getString("pic1"));
		}
		if (!bundle.getString("pic2").equals("")) {
			photoPaths.add(1, bundle.getString("pic2"));
		}
		if (!bundle.getString("pic3").equals("")) {
			photoPaths.add(2, bundle.getString("pic3"));
		}
		if (!bundle.getString("pic4").equals("")) {
			photoPaths.add(3, bundle.getString("pic4"));
		}

		mViewPagerViews = new ArrayList<View>();
		for (int i = 0; i < photoPaths.size(); i++) {
			slidePopupWindow = FullScreenSlidePopupWindow.getInstance(mContext, photoPaths);
			final LocalNetWorkView localNetWorkView = new LocalNetWorkView(this);
			localNetWorkView.setScaleType(ImageView.ScaleType.FIT_XY);
			if (photoPaths.get(i) != null && !photoPaths.get(i).equals("null") && !photoPaths.get(i).equals("")) {
				localNetWorkView.setTag(photoPaths.get(i));
				ImageCacheLoader.getInstance().getLocalImage(photoPaths.get(i), localNetWorkView, new GetLocalCallBack() {
					@Override
					public void localSuccess(Object o) {
						LocalNetWorkView lv = (LocalNetWorkView) o;
						if (lv.getTag().equals(lv.filePath)) {
							lv.setImageBitmap(lv.bm);
						}
					}

					@Override
					public void localFalse(Object o) {
						localNetWorkView.setBackgroundResource(R.drawable.no_data_image);
					}
				}, false);
				mViewPagerViews.add(localNetWorkView);
			} else {
				Logger.e("详情Url过滤=" + photoPaths.get(i));
			}
			localNetWorkView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					slidePopupWindow.startFullScreenSlide(mCurrentPage);
				}
			});
		}
		
		mTvCurrentPage.setText("1");
		mTvTotalPage.setText("" + mViewPagerViews.size());
		mVpDetailPicture.setAdapter(new ViewPagerAdapter(mViewPagerViews));

		if (mViewPagerViews.size() == 0) {
			mFlViewPagerShow.setVisibility(View.GONE);
		}
	}

	@Override
	public void onBackPressed() {
		if(slidePopupWindow != null){
			slidePopupWindow.popupWindowDismiss();
		}
		super.onBackPressed();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.title_view_back_img:
			onBackPressed();
			break;
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int position) {
		mCurrentPage = position;
		mTvCurrentPage.setText(String.valueOf(position + 1));
	}

}
