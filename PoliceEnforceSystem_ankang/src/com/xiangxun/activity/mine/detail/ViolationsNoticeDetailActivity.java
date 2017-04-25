package com.xiangxun.activity.mine.detail;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiangxun.activity.R;
import com.xiangxun.adapter.ViewPagerAdapter;
import com.xiangxun.app.BaseActivity;
import com.xiangxun.bean.ListDataObj;
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
 * @ClassName: ViolationsNoticeDetailActivity.java
 * @Description: 违法行为通知详情页面
 */
public class ViolationsNoticeDetailActivity extends BaseActivity implements OnClickListener, OnPageChangeListener {
	private TitleView titleView;
	private Context mContext;
	private List<View> mViewPagerViews = null;
	private ViewPager mVpDetailPicture = null;
	private FrameLayout mFlViewPagerShow = null;
	private TextView mTvPublishCode;
	private TextView mTvCurrentPage = null;
	private TextView mTvTotalPage = null;
	private int mCurrentPage = 0;
	private TextView mTvNumber;
	private TextView mTvType;
	private TextView mTvViolationType;
	private TextView mTvAddress;
	private TextView mTvRoadLocation;
	private TextView mTvRoadDirect;
	private TextView mTvDate;
	private TextView mTvCarColor;
	private TextView mTvPlateColor;
	private List<String> photoPaths;
	private ListDataObj ldo = null;
	private FullScreenSlidePopupWindow slidePopupWindow;
	private LinearLayout lay_plateColor;
	private LinearLayout lay_carColor;
	private LinearLayout punishCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		setContentView(R.layout.detail_violation_notice_layout);
		mContext = this;
		initView();
		initData();
		initListener();
	}

	@Override
	public void initView() {
		titleView = (TitleView) findViewById(R.id.tv_comm_title);
		titleView.setLeftBackOneListener(R.drawable.back_normal, this);
		titleView.setTitle("违法信息查看");
		mTvPublishCode = (TextView) findViewById(R.id.tv_punish_code);
		mTvCarColor = (TextView) findViewById(R.id.tv_car_color);
		lay_carColor = (LinearLayout) findViewById(R.id.lay_carcolor);
		lay_plateColor = (LinearLayout) findViewById(R.id.lay_platecolor);
		mTvPlateColor = (TextView) findViewById(R.id.tv_plate_color);
		mTvType = (TextView) findViewById(R.id.tv_number_type);
		mTvAddress = (TextView) findViewById(R.id.tv_accspn_road);
		mTvRoadLocation = (TextView) findViewById(R.id.tv_accspn_road_location);
		mTvRoadDirect = (TextView) findViewById(R.id.tv_accspn_road_direct);
		mTvDate = (TextView) findViewById(R.id.tv_accspn_date);
		mTvNumber = (TextView) findViewById(R.id.tv_car_number);
		mTvViolationType = (TextView) findViewById(R.id.tv_spvio_type);
		mVpDetailPicture = (ViewPager) findViewById(R.id.vp_common_pictures);
		//设置ViewPager显示图片宽高比例为 4:3
		LayoutParams params = mVpDetailPicture.getLayoutParams();
		params.width = Tools.getScreenPixel(this)[0];
		params.height = (params.width * 3) / 4;
		mVpDetailPicture.setLayoutParams(params);
		mFlViewPagerShow = (FrameLayout) findViewById(R.id.fl_common_view_pager);
		mTvCurrentPage = (TextView) findViewById(R.id.tv_common_current_page);
		mTvTotalPage = (TextView) findViewById(R.id.tv_common_total_page);
		punishCode = (LinearLayout) findViewById(R.id.code);
	}

	@Override
	public void initListener() {
		mVpDetailPicture.setOnPageChangeListener(this);
	}

	@Override
	public void initData() {
		Bundle bundle = getIntent().getExtras();
		ldo = new ListDataObj();
		ArrayList<Type> platetypes = DBManager.getInstance().getPlateTypes();
		for (int i = 0; i < platetypes.size(); i++) {
			ldo.add(platetypes.get(i).code, platetypes.get(i).name);
		}
		for (int i = 0; i != ldo.getlist().size(); i++) {
			if (ldo.getlist().get(i).getid().equals(bundle.getString("platetype"))) {
				mTvType.setText(ldo.getlist().get(i).toString());
				break;
			}
		}
		mTvDate.setText(bundle.getString("datetime"));
		mTvNumber.setText(bundle.getString("platenum"));
		if(bundle.getString("pubishCode") == null){
			punishCode.setVisibility(View.GONE);
		} else if(bundle.getString("pubishCode").equals("")){
			punishCode.setVisibility(View.GONE);
		}else {
			mTvPublishCode.setText(bundle.getString("pubishCode"));
		}

		mTvViolationType.setText(bundle.getString("viotype"));
		mTvAddress.setText(bundle.getString("roadname"));
		mTvRoadLocation.setText(bundle.getString("roadlocation"));
		mTvRoadDirect.setText(DBManager.getInstance().getRoadDirectionNameByCode(bundle.getString("roaddirect")));
		LinearLayout ll_publish_click_address = (LinearLayout) findViewById(R.id.ll_publish_click_address);
		LinearLayout ll_publish_click_RoadDirect = (LinearLayout) findViewById(R.id.ll_publish_click_road_direct);
		if (getResources().getString(R.string.workroad_city).equalsIgnoreCase(SystemCfg.getWorkRoad(this))) {
			ll_publish_click_address.setVisibility(View.GONE);
			ll_publish_click_RoadDirect.setVisibility(View.GONE);
		}		
		if (bundle.getString("platecolor").equals("")) {
			lay_plateColor.setVisibility(View.GONE);
		} else {
			mTvPlateColor.setText(bundle.getString("platecolor"));
		}
		if (bundle.getString("carcolor").equals("")) {
			lay_carColor.setVisibility(View.GONE);
		} else {
			mTvCarColor.setText(bundle.getString("carcolor"));
		}
		String viocode = SystemCfg.getVioParkCode(this);
		if (bundle.getString("viocode").equals(viocode)) {
			titleView.setTitle("违法信息查看--违停");
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
		if (slidePopupWindow != null) {
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
