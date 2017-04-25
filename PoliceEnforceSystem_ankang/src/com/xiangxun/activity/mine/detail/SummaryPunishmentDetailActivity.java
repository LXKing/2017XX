/**
 * 
 */
package com.xiangxun.activity.mine.detail;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
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
import com.xiangxun.bean.PrintInfo;
import com.xiangxun.bean.Type;
import com.xiangxun.bean.VioType;
import com.xiangxun.cfg.SystemCfg;
import com.xiangxun.common.LocalNetWorkView;
import com.xiangxun.db.DBManager;
import com.xiangxun.service.StdPrintService;
import com.xiangxun.util.ImageCacheLoader;
import com.xiangxun.util.ImageCacheLoader.GetLocalCallBack;
import com.xiangxun.util.Logger;
import com.xiangxun.util.Tools;
import com.xiangxun.util.Utils;
import com.xiangxun.widget.FullScreenSlidePopupWindow;
import com.xiangxun.widget.MsgToast;
import com.xiangxun.widget.TitleView;

import java.util.ArrayList;
import java.util.List;

import static com.xiangxun.request.AppBuildConfig.DEBUGURL;

/**
 * @package: com.xiangxun.activity.home
 * @ClassName: SummaryPunishmentDetailActivity.java
 * @Description: 简易程序处罚详情页面
 * @author: HanGJ
 * @date: 2015-8-14 下午3:32:47
 */
@SuppressLint("HandlerLeak")
public class SummaryPunishmentDetailActivity extends BaseActivity implements OnClickListener, OnPageChangeListener {
	private TitleView titleView;
	private Context mContext;
	private TextView mTvPublishCode;// 决定书编号

	private TextView mTvDriverName;// 驾驶员姓名
	private TextView mTvDriverPhone;// 驾驶员联系方式
	private TextView mTvDriverCode;// 驾驶证号
	private TextView mTvRecordCode;// 档案编号
	private TextView mTvLicenseOffice;// 发证机关
	private TextView mTvLicenseType;// 准驾车型

	private TextView mTvCarPlateNum;// 车牌号码
	private TextView mTvCarPlateType;// 车牌种类
	private TextView mTvCarOwner;// 车辆所有人
	private TextView mTvCarOffice;// 车辆发证机关

	private TextView mTvType;// 违法行为
	private TextView mTvAddress;// 违法地点
	private TextView mTvRoadLocation;// 违法地点
	private TextView mTvRoadDirect;// 违法地点
	private TextView mTvDate;// 违法时间
	private TextView mTvMoney;// 处罚金额
	private TextView mTvAction;// 处理方式
	private TextView mTvValue;// 处罚记分

	private List<View> mViewPagerViews = null;
	private ViewPager mVpDetailPicture = null;
	private FrameLayout mFlViewPagerShow = null;
	private TextView mTvCurrentPage = null;
	private TextView mTvTotalPage = null;
	private int mCurrentPage = 0;

	private List<String> photoPaths;
	private StdPrintService mPrintService;
	private FullScreenSlidePopupWindow slidePopupWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		setContentView(R.layout.detail_summary_notice_layou);
		mContext = this;
		initView();
		initData();
		initListener();
	}

	@Override
	public void initView() {
		titleView = (TitleView) findViewById(R.id.tv_comm_title);
		titleView.setLeftBackOneListener(R.drawable.back_normal, this);
		if (DEBUGURL) {
			if (SystemCfg.getIsPrint(this)) {
				titleView.setRightViewLeftOneListener(R.drawable.icon_print, this);
			}
		}
		titleView.setTitle(R.string.jycfjds);
		mVpDetailPicture = (ViewPager) findViewById(R.id.vp_common_pictures);
		//设置ViewPager显示图片宽高比例为 4:3
		LayoutParams params = mVpDetailPicture.getLayoutParams();
		params.width = Tools.getScreenPixel(this)[0];
		params.height = (params.width * 3) / 4;
		mVpDetailPicture.setLayoutParams(params);
		mFlViewPagerShow = (FrameLayout) findViewById(R.id.fl_common_view_pager);
		mTvCurrentPage = (TextView) findViewById(R.id.tv_common_current_page);
		mTvTotalPage = (TextView) findViewById(R.id.tv_common_total_page);

		mTvPublishCode = (TextView) findViewById(R.id.tv_punish_code);// 决定书编号

		mTvDriverName = (TextView) findViewById(R.id.tv_driver_name);// 驾驶员姓名
		mTvDriverPhone = (TextView) findViewById(R.id.tv_call_type);// 驾驶员联系方式
		mTvDriverCode = (TextView) findViewById(R.id.tv_driver_type);// 驾驶证号
		mTvRecordCode = (TextView) findViewById(R.id.tv_record_code);// 档案编号
		mTvLicenseOffice = (TextView) findViewById(R.id.tv_license_office);// 发证机关
		mTvLicenseType = (TextView) findViewById(R.id.tv_license_type);// 准驾车型

		mTvCarPlateNum = (TextView) findViewById(R.id.tv_car_plate);// 车牌号码
		mTvCarPlateType = (TextView) findViewById(R.id.tv_plate_type);// 车牌种类
		mTvCarOwner = (TextView) findViewById(R.id.tv_car_owner);// 车辆所有人
		mTvCarOffice = (TextView) findViewById(R.id.tv_plate_office);// 车辆发证机关

		mTvType = (TextView) findViewById(R.id.tv_vio_type);// 违法行为
		mTvAddress = (TextView) findViewById(R.id.tv_vio_address);// 违法地点
		mTvRoadLocation = (TextView) findViewById(R.id.tv_vio_road_location);// 违法地点
		mTvRoadDirect = (TextView) findViewById(R.id.tv_vio_road_direct);// 违法地点
		LinearLayout ll_publish_click_address = (LinearLayout) findViewById(R.id.ll_publish_click_address);
		LinearLayout ll_publish_click_RoadDirect = (LinearLayout) findViewById(R.id.ll_publish_click_road_direct);
		if (getResources().getString(R.string.workroad_city).equalsIgnoreCase(SystemCfg.getWorkRoad(this))) {
			ll_publish_click_address.setVisibility(View.GONE);
			ll_publish_click_RoadDirect.setVisibility(View.GONE);
		}		
		mTvDate = (TextView) findViewById(R.id.tv_vio_date);// 违法时间
		mTvAction = (TextView) findViewById(R.id.tv_vio_action);// 处罚金额
		mTvMoney = (TextView) findViewById(R.id.tv_vio_money);// 处罚金额
		mTvValue = (TextView) findViewById(R.id.tv_vio_value);// 处罚金额
	}

	@Override
	public void initListener() {
		mVpDetailPicture.setOnPageChangeListener(this);
	}

	@Override
	public void initData() {
		Bundle bundle = getIntent().getExtras();
		mTvPublishCode.setText(bundle.getString("id"));
		mTvDriverName.setText(bundle.getString("name"));
		mTvDriverPhone.setText(bundle.getString("phone"));
		mTvDriverCode.setText(bundle.getString("driverlic"));
		mTvRecordCode.setText(bundle.getString("recordid"));
		mTvLicenseOffice.setText(bundle.getString("licenseoffice"));
		mTvLicenseType.setText(bundle.getString("licensetype"));
		mTvCarPlateNum.setText(bundle.getString("plate"));
		ListDataObj ldo = new ListDataObj();
		ArrayList<Type> platetypes = DBManager.getInstance().getPlateTypes();
		for (int i = 0; i < platetypes.size(); i++) {
			ldo.add(platetypes.get(i).code, platetypes.get(i).name);
		}
		for (int i = 0; i != ldo.getlist().size(); i++) {
			if (ldo.getlist().get(i).getid().equals(bundle.getString("platetype"))) {
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
		
		mTvCarOffice.setText(bundle.getString("plateoffice"));
//		int indexOf = ToUploadViodata.vioTypes.indexOf(bundle.getString("content"));
//		mTvType.setText(ToUploadViodata.vioTypes.get(indexOf));
		mTvType.setText(bundle.getString("content"));
		mTvAddress.setText(bundle.getString("location"));
		mTvRoadLocation.setText(bundle.getString("roadlocation"));
		mTvRoadDirect.setText(DBManager.getInstance().getRoadDirectionNameByCode(bundle.getString("roaddirect")));
		mTvDate.setText(bundle.getString("datetime"));
		mTvAction.setText(String.valueOf(bundle.getString("action")));
		if ("警告".equals(bundle.getString("action"))) {
			mTvMoney.setVisibility(View.GONE);
			mTvValue.setVisibility(View.GONE);
		}
		mTvMoney.setText(String.valueOf(bundle.getInt("money")));
		mTvValue.setText(String.valueOf(bundle.getInt("value")));

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

		if (SystemCfg.getIsPrint(this)) {
			Intent i = new Intent(this, StdPrintService.class);
			bindService(i, conn, Context.BIND_AUTO_CREATE);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (SystemCfg.getIsPrint(this)) {
			unbindService(conn);
			mPrintService.onDestroy();
		}
	}

	private void connectPrintDevice() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(R.string.connectPrinter).setCancelable(true);
		builder.create();
		builder.setPositiveButton(R.string.tvcamok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				if (!print()) {
					MsgToast.geToast().setMsg(R.string.printError);
				}
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {

			}
		});
		builder.show();
	}

	private boolean print() {
		if (mPrintService != null) {
			mPrintService.print(mPrintService.formatContent(generateContent(), 10), PrintInfo.TITLE_TYPE_PUNISHMENT);
		}
		return true;
	}
	@SuppressLint("DefaultLocale")
	private String generateContent() {
		StringBuffer content = new StringBuffer();
		Resources resources = getResources();
		content.append(resources.getString(R.string.bianhao) + mTvPublishCode.getText().toString().toUpperCase() + "%end%");
		content.append(resources.getString(R.string.beichufaren) + mTvDriverName.getText().toString().toUpperCase() + "%end%");
		content.append(resources.getString(R.string.phone) + "：" + mTvDriverPhone.getText().toString().toUpperCase() + "%end%");
		content.append(resources.getString(R.string.driverId) + "%end%");
		content.append(mTvDriverCode.getText().toString() + "%end%");
		content.append(resources.getString(R.string.recordId) + mTvRecordCode.getText().toString().toUpperCase() + "%end%");
		content.append(resources.getString(R.string.organization) + mTvLicenseOffice.getText().toString().toUpperCase() + "  ");
		content.append(resources.getString(R.string.driveType) + mTvLicenseType.getText().toString().toUpperCase() + "%end%");
		content.append(resources.getString(R.string.plateNum) + mTvCarPlateNum.getText().toString().toUpperCase() + "%end%");
		content.append(resources.getString(R.string.plateType) + mTvCarPlateType.getText().toString() + "%end%");
//		content.append(resources.getString(R.string.organization) + mTvCarOffice.getText().toString().toUpperCase() + "%end%");
		content.append(getResources().getString(R.string.dangshiren));
		content.append(resources.getString(R.string.yu) + Utils.ChangeTimeTochs(mTvDate.getText().toString().toUpperCase()));
		content.append(resources.getString(R.string.zai));
		content.append(mTvAddress.getText().toString().toUpperCase());
		if ("高速公路".equals(SystemCfg.getWorkRoad(this))) {
			content.append("(" + getResources().getString(R.string.roadlocation_flag) + mTvRoadLocation.getText().toString());
			content.append(getResources().getString(R.string.roaddirect_flag) + mTvRoadDirect.getText().toString() + ")");
		}
		content.append(resources.getString(R.string.shishi) + mTvType.getText().toString().toUpperCase());
		VioType vt = DBManager.getInstance().getVioTypeByName(mTvType.getText().toString());
		String law = vt.getLaw();
		law = law.replaceAll("《法》","《道路交通安全法》");
		law = law.replaceAll("《国条》","《道路交通安全法实施条例》");
		law = law.replaceAll("《省条》","《陕西省道路交通安全条例》");
		law = law.replaceAll("《省高条》","《陕西省高速公路条例》");
		law = law.replaceAll("《强制险条例》","《机动车交通事故责任强制保险条例》");
		law = law.replaceAll("《办法》","《剧毒化学品购买和公路运输许可证件管理办法》");
		law = law.replaceAll("《危化品条例》","《危险化学品安全管理条例》");
		vt.setLaw(law);
		String rule = vt.getRule();
		rule = rule.replaceAll("《法》","《道路交通安全法》");
		rule = rule.replaceAll("《国条》","《道路交通安全法实施条例》");
		rule = rule.replaceAll("《省条》","《陕西省道路交通安全条例》");
		rule = rule.replaceAll("《省高条》","《陕西省高速公路条例》");
		rule = rule.replaceAll("《强制险条例》","《机动车交通事故责任强制保险条例》");
		rule = rule.replaceAll("《办法》","《剧毒化学品购买和公路运输许可证件管理办法》");
		rule = rule.replaceAll("《危化品条例》","《危险化学品安全管理条例》");
		vt.setRule(rule);
//最长测试 -------------------------
//		content.append(resources.getString(R.string.shishi) + "驾驶中型以上载客载货汽车、校车、危险物品运输车辆以外的机动车行驶超过规定时速50%以上未达70%的");
//		VioType vt = DBManager.getInstance().getVioTypeByName(mTvType.getText().toString());
//		vt.setCode("17211");
//		vt.setLaw("《道路交通安全法》第四十二条第一款、《道路交通安全法实施条例》第四十五条、第四十六条");
//		vt.setRule("《道路交通安全法》第九十九条第一款第（四）项、第二款、《陕西省道路交通安全条例》第六十四条第（三）项");

		content.append(resources.getString(R.string.wfnr) + "，" + getResources().getString(R.string.code) + vt.getCode());
		content.append(String.format(resources.getString(R.string.weifaguiding), vt.getLaw(), vt.getRule()));

		if (mTvAction.getText().toString().equals("罚款")) {
			content.append(mTvMoney.getText().toString().toUpperCase() + "元" + mTvAction.getText().toString() + ",");	//200罚款
			content.append(resources.getString(R.string.jifen) + mTvValue.getText().toString().toUpperCase() + "分。");
			content.append(resources.getString(R.string.chufa_tip) + "子长县公安局交通管理大队警务大厅" + resources.getString(R.string.chufa_tipx));
		} else {
			content.append(mTvAction.getText().toString());	//警告
		}
		content.append("%start%" + String.format(resources.getString(R.string.chufa_desc), "子长县人民政府或子长县公安局", "子长县"));
		return content.toString();
	}

	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mPrintService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mPrintService = ((StdPrintService.MyBinder) service).getPrintService();
		}
	};

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
		case R.id.title_view_operation_imageview:
			if (SystemCfg.getIsPrint(this)) {
				if (!mPrintService.isPrintConnected()) {
					MsgToast.geToast().setMsg(R.string.sureConnectBlueTooth);
				} else {
					connectPrintDevice();
				}
			}
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
