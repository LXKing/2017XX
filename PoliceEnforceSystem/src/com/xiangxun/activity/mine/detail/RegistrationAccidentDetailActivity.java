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
 * @ClassName: RegistrationAccidentDetailActivity.java
 * @Description: 事故登记详情页面
 * @author: HanGJ
 * @date: 2015-8-17 上午9:26:43
 */
public class RegistrationAccidentDetailActivity extends BaseActivity implements OnClickListener, OnPageChangeListener {
	private TitleView titleView;
	private Context mContext;
	private TextView mTvAlarmName;// 报警人姓名
	private TextView mTvAlarmPhone;// 报警人联系方式
	private TextView mTvAccidentAddress;// 事故地点
	private TextView mTvAccidentRoadLocation;// 事故地点
	private TextView mTvAccidentRoadDirect;// 事故地点
	private TextView mTvAccidentDate;// 事故时间
	private TextView mTvWeatherStatus;// 天气状况
	private TextView mTvCarCount;// 车辆数目
	private LinearLayout addCarInfo;
	private TextView mTvAccidentType;// 事故类型
	private TextView mTvJoinPerson;// 参与人员
	private TextView mTvAccidenthurt;// 受伤人数
	private TextView mTvAccidentDied;// 死亡人数
	private TextView mTvAccidentDesc;// 事故简介
	private List<View> mViewPagerViews = null;
	private ViewPager mVpDetailPicture = null;
	private FrameLayout mFlViewPagerShow = null;
	private TextView mTvCurrentPage = null;
	private TextView mTvTotalPage = null;
	private int mCurrentPage = 0;
	// 存放照片url的容器
	private List<String> photoPaths;
	private ArrayList<Type> weatherList;
	private ArrayList<Type> accTypeList;
	private FullScreenSlidePopupWindow slidePopupWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_accident_registration_layout);
		mContext = this;
		initView();
		initData();
		initListener();
	}

	@Override
	public void initView() {
		titleView = (TitleView) findViewById(R.id.tv_comm_title);
		titleView.setTitle(R.string.sgdj);
		mVpDetailPicture = (ViewPager) findViewById(R.id.vp_common_pictures);
		//设置ViewPager显示图片宽高比例为 4:3
		LayoutParams params = mVpDetailPicture.getLayoutParams();
		params.width = Tools.getScreenPixel(this)[0];
		params.height = (params.width * 3) / 4;
		mVpDetailPicture.setLayoutParams(params);
		mFlViewPagerShow = (FrameLayout) findViewById(R.id.fl_common_view_pager);
		mTvCurrentPage = (TextView) findViewById(R.id.tv_common_current_page);
		mTvTotalPage = (TextView) findViewById(R.id.tv_common_total_page);
		
		mTvAlarmName = (TextView) findViewById(R.id.tv_alarm_name);// 报警人姓名
		mTvAlarmPhone = (TextView) findViewById(R.id.tv_call_type);// 报警人联系方式
		mTvAccidentAddress = (TextView) findViewById(R.id.tv_accident_address);// 事故地点
		mTvAccidentRoadLocation = (TextView) findViewById(R.id.tv_accident_road_location);// 事故地点
		mTvAccidentRoadDirect = (TextView) findViewById(R.id.tv_accident_road_direct);// 事故地点
		LinearLayout mll_publish_click_road_direct = (LinearLayout) findViewById(R.id.ll_publish_click_road_direct);
		mll_publish_click_road_direct.setVisibility(View.GONE);
		mTvAccidentDate = (TextView) findViewById(R.id.tv_accident_date);// 事故时间
		mTvWeatherStatus = (TextView) findViewById(R.id.tv_weather_status);// 天气状况
		
		mTvCarCount = (TextView) findViewById(R.id.tv_accspicar_num);// 车辆数目
		addCarInfo = (LinearLayout) findViewById(R.id.acclinearLayoutcarinfo);
		
		mTvAccidentType = (TextView) findViewById(R.id.tv_accident_type);// 事故类型
		mTvJoinPerson = (TextView) findViewById(R.id.tv_join_person);// 参与人员
		mTvAccidenthurt = (TextView) findViewById(R.id.tv_accident_thurt);// 受伤人数
		mTvAccidentDied = (TextView) findViewById(R.id.tv_accident_died);// 死亡人数
		mTvAccidentDesc = (TextView) findViewById(R.id.tv_accident_desc);// 事故简介
	}

	@Override
	public void initListener() {
		titleView.setLeftBackOneListener(R.drawable.back_normal, this);
		mVpDetailPicture.setOnPageChangeListener(this);
	}

	@Override
	public void initData() {
		Bundle bundle = getIntent().getExtras();
		mTvAlarmName.setText(bundle.getString("caller"));
		mTvAlarmPhone.setText(bundle.getString("phone"));
		mTvAccidentAddress.setText(bundle.getString("roadname"));
		mTvAccidentRoadLocation.setText(bundle.getString("roadlocation"));
		mTvAccidentRoadDirect.setText(DBManager.getInstance().getRoadDirectionNameByCode(bundle.getString("roaddirect")));
		mTvAccidentDate.setText(bundle.getString("datetime"));
		weatherList = DBManager.getInstance().getWeatherTypes();
		int weatherStatus = bundle.getInt("weather");
		mTvWeatherStatus.setText(weatherList.get(weatherStatus).name);
		int carnum = bundle.getInt("carnum");
		mTvCarCount.setText(String.valueOf(carnum));
		addCarInfo.removeAllViews();
		ListDataObj ldo = new ListDataObj();
		ArrayList<Type> platetypes = DBManager.getInstance().getPlateTypes();
		for (int i = 0; i < platetypes.size(); i++) {
			ldo.add(platetypes.get(i).code, platetypes.get(i).name);
		}
		// 根据车辆数动态绘制车辆信息UI显示车辆信息
		for (int i = 0; i < carnum; i++) {
			int platetype = DBManager.getInstance().getCarInfo(bundle.getString("ownerid")).get(i).getPlatetype();
			addCarInfoView(DBManager.getInstance().getCarInfo(bundle.getString("ownerid")).get(i).getPlatenum(), ldo.getlist().get(platetype).toString());
		}
		// 获取事故类型
		accTypeList = DBManager.getInstance().getAccTypes();
		int accidentType = bundle.getInt("acctype");
		mTvAccidentType.setText(accTypeList.get(accidentType).name);
		String joinlist = bundle.getString("joinlist");
		if(joinlist != null && joinlist.length() > 0){
			mTvJoinPerson.setText(joinlist);
		} else {
			mTvJoinPerson.setText("暂无");
		}
		
		int hurt = bundle.getInt("hurt");
		if(hurt > 0){
			mTvAccidenthurt.setText(String.valueOf(hurt));
		} else {
			mTvAccidenthurt.setText(String.valueOf(0));	
		}
		int death = bundle.getInt("death");
		if(death > 0){
			mTvAccidentDied.setText(String.valueOf(death));
		} else {
			mTvAccidentDied.setText(String.valueOf(0));	
		}
		String memo = bundle.getString("memo");
		if (memo.contains("'")) {
			memo = memo.replace("'", ",");
		}
		mTvAccidentDesc.setText(memo);
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
		if (!bundle.getString("pic5").equals("")) {
			photoPaths.add(4, bundle.getString("pic5"));
		}
		if (!bundle.getString("pic6").equals("")) {
			photoPaths.add(5, bundle.getString("pic6"));
		}
		if (!bundle.getString("pic7").equals("")) {
			photoPaths.add(6, bundle.getString("pic7"));
		}
		if (!bundle.getString("pic8").equals("")) {
			photoPaths.add(7, bundle.getString("pic8"));
		}
		if (!bundle.getString("pic9").equals("")) {
			photoPaths.add(8, bundle.getString("pic9"));
		}
		if (!bundle.getString("pic10").equals("")) {
			photoPaths.add(9, bundle.getString("pic10"));
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

	private void addCarInfoView(String platenum, String platetype) {
		View view = getLayoutInflater().inflate(R.layout.add_accident_carinfo_item, null);
		TextView carCode = (TextView) view.findViewById(R.id.tv_accident_code);
		TextView carCodeType = (TextView) view.findViewById(R.id.tv_plate_type);
		carCode.setText(platenum);
		carCodeType.setText(platetype);
		addCarInfo.addView(view);
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
