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
 * @package: com.xiangxun.activity.mine.detail
 * @ClassName: LawEnforceDetailActivity.java
 * @Description: 执法检查车辆详情页面
 * @author: HanGJ
 * @date: 2015-8-19 上午8:49:00
 */
public class LawEnforceDetailActivity extends BaseActivity implements OnClickListener, OnPageChangeListener {
	private TitleView titleView;
	private Context mContext;
	private TextView mTvDriverName;// 驾驶员姓名
	private TextView mTvDriverPhone;// 驾驶员联系方式
	private TextView mTvDriverCode;// 驾驶证号
	private TextView mTvCarPlateNum;// 车牌号码
	private TextView mTvCarPlateType;// 车牌种类
	private TextView mTvCarOwner;// 车辆所有人
	private TextView mTvCarMust;// 核载人数
	private TextView mTvCarActual;// 实载人数
	private TextView mTvCheckType;// 检查类型
	private TextView mTvLawType;// 违法行为
	private TextView mTvCheckAddress;// 检查地点
	private TextView mTvCheckRoadLocation;// 检查地点
	private TextView mTvCheckRoadDirect;// 检查地点
	private TextView mTvCheckDate;// 检查时间
	private TextView mTvHandleWay;// 处理方式
	private TextView mTvCheckMome;// 检查备注
	private List<View> mViewPagerViews = null;
	private ViewPager mVpDetailPicture = null;
	private FrameLayout mFlViewPagerShow = null;
	private TextView mTvCurrentPage = null;
	private TextView mTvTotalPage = null;
	private int mCurrentPage = 0;
	private List<String> photoPaths;
	private ArrayList<Type> types = new ArrayList<Type>();
	private FullScreenSlidePopupWindow slidePopupWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_law_enforce_layout);
		mContext = this;
		initView();
		initData();
		initListener();
	}

	@Override
	public void initView() {
		titleView = (TitleView) findViewById(R.id.tv_comm_title);
		titleView.setLeftBackOneListener(R.drawable.back_normal, this);
		titleView.setTitle(R.string.zfjccl);
		
		mVpDetailPicture = (ViewPager) findViewById(R.id.vp_common_pictures);
		//设置ViewPager显示图片宽高比例为 4:3
		LayoutParams params = mVpDetailPicture.getLayoutParams();
		params.width = Tools.getScreenPixel(this)[0];
		params.height = (params.width * 3) / 4;
		mVpDetailPicture.setLayoutParams(params);
		mFlViewPagerShow = (FrameLayout) findViewById(R.id.fl_common_view_pager);
		mTvCurrentPage = (TextView) findViewById(R.id.tv_common_current_page);
		mTvTotalPage = (TextView) findViewById(R.id.tv_common_total_page);
		
		mTvDriverName = (TextView) findViewById(R.id.tv_driver_name);// 驾驶员姓名
		mTvDriverPhone = (TextView) findViewById(R.id.tv_call_type);// 驾驶员联系方式
		mTvDriverCode = (TextView) findViewById(R.id.tv_driver_type);// 驾驶证号
		
		mTvCarPlateNum = (TextView) findViewById(R.id.tv_car_plate);// 车牌号码
		mTvCarPlateType = (TextView) findViewById(R.id.tv_plate_type);// 车牌种类
		mTvCarOwner = (TextView) findViewById(R.id.tv_car_owner);// 车辆所有人
		mTvCarMust = (TextView) findViewById(R.id.tv_plate_must);//
		mTvCarActual = (TextView) findViewById(R.id.tv_plate_actual);//
		
		mTvCheckType = (TextView) findViewById(R.id.tv_check_type);// 检查类型
		mTvLawType = (TextView) findViewById(R.id.tv_vio_type);// 违法行为
		mTvCheckAddress = (TextView) findViewById(R.id.tv_check_address);// 检查地点
		mTvCheckRoadLocation = (TextView) findViewById(R.id.tv_check_road_location);// 检查地点
		mTvCheckRoadDirect = (TextView) findViewById(R.id.tv_check_road_direct);// 检查地点
		LinearLayout ll_publish_click_address = (LinearLayout) findViewById(R.id.ll_publish_click_address);
		LinearLayout ll_publish_click_RoadDirect = (LinearLayout) findViewById(R.id.ll_publish_click_road_direct);
		if (getResources().getString(R.string.workroad_city).equalsIgnoreCase(SystemCfg.getWorkRoad(this))) {
			ll_publish_click_address.setVisibility(View.GONE);
			ll_publish_click_RoadDirect.setVisibility(View.GONE);
		}		
		mTvCheckDate = (TextView) findViewById(R.id.tv_check_date);// 检查时间
		mTvHandleWay = (TextView) findViewById(R.id.tv_check_handleway);// 处理方式
		mTvCheckMome = (TextView) findViewById(R.id.tv_check_memo);// 检查备注
	}

	@Override
	public void initData() {
		types = DBManager.getInstance().getCheckTypes();
		Bundle bundle = getIntent().getExtras();
		mTvDriverName.setText(bundle.getString("drivername"));
		mTvDriverPhone.setText(bundle.getString("phone"));
		mTvDriverCode.setText(bundle.getString("drivernum"));
		mTvCarPlateNum.setText(bundle.getString("platenum"));
		ListDataObj ldo = new ListDataObj();
		ArrayList<Type> platetypes = DBManager.getInstance().getPlateTypes();
		for (int i = 0; i < platetypes.size(); i++) {
			ldo.add(platetypes.get(i).code, platetypes.get(i).name);
		}
		for (int i = 0; i != ldo.getlist().size(); i++) {
			if (ldo.getlist().get(i).getid().equals(bundle.getString("cartype"))) {
				mTvCarPlateType.setText(ldo.getlist().get(i).toString());
				break;
			}
		}
		String owner = bundle.getString("owner");
		if(owner != null && owner.length() > 0){
			mTvCarOwner.setText(owner);
		} else {
			mTvCarOwner.setText("暂无");
		}
		String must = bundle.getString("must");
		if(must != null && must.length() > 0){
			mTvCarMust.setText(must);
		} else {
			mTvCarMust.setText("");
		}
		String actual = bundle.getString("actual");
		if(actual != null && actual.length() > 0){
			mTvCarActual.setText(actual);
		} else {
			mTvCarActual.setText("");
		}
		mTvLawType.setText(DBManager.getInstance().getVioNameByCode(bundle.getString("viotype")));
		for (int i = 0; i < types.size(); i++) {
			if (types.get(i).getCode().equals(bundle.getString("checktype"))) {
				mTvCheckType.setText(types.get(i).name);
				break;
			}
		}
		mTvCheckAddress.setText(DBManager.getInstance().getRoadNameById(bundle.getString("road")));
		mTvCheckRoadLocation.setText(bundle.getString("roadlocation"));
		mTvCheckRoadDirect.setText(DBManager.getInstance().getRoadDirectionNameByCode(bundle.getString("roaddirect")));
		mTvCheckDate.setText(bundle.getString("datetime"));
		String handleway = bundle.getString("handleway");
		if(handleway != null && handleway.length() > 0){
			mTvHandleWay.setText(handleway);
		} else {
			mTvHandleWay.setText("暂无");
		}
		String memo = bundle.getString("memo");
		if(memo != null && memo.length() > 0){
			mTvCheckMome.setText(memo);
		} else {
			mTvCheckMome.setText("暂无");
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
	public void initListener() {
		mVpDetailPicture.setOnPageChangeListener(this);
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
