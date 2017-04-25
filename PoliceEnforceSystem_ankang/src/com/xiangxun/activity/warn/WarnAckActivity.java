package com.xiangxun.activity.warn;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.xiangxun.activity.CameraActivity;
import com.xiangxun.activity.PhotoSelectActivity;
import com.xiangxun.activity.R;
import com.xiangxun.activity.home.PublishEnforceMeasureActivity;
import com.xiangxun.activity.home.PublishSummaryPunishActivity;
import com.xiangxun.activity.home.PublishViolationsNoticeActivity;
import com.xiangxun.activity.setting.PrintSettingActivity;
import com.xiangxun.adapter.PublishPictureAdapter;
import com.xiangxun.adapter.ViewPagerAdapter;
import com.xiangxun.app.BaseActivity;
import com.xiangxun.app.XiangXunApplication;
import com.xiangxun.bean.MDate;
import com.xiangxun.bean.ResultData;
import com.xiangxun.bean.WarnAck;
import com.xiangxun.bean.WarnData;
import com.xiangxun.bean.WarnPic;
import com.xiangxun.cfg.SystemCfg;
import com.xiangxun.common.DcHttpClient;
import com.xiangxun.common.LocalNetWorkView;
import com.xiangxun.db.DBManager;
import com.xiangxun.request.DcNetWorkUtils;
import com.xiangxun.util.ConstantStatus;
import com.xiangxun.util.ImageCacheLoader;
import com.xiangxun.util.ImageUtils;
import com.xiangxun.util.Logger;
import com.xiangxun.util.Tools;
import com.xiangxun.util.Utils;
import com.xiangxun.widget.DeletePictureInterface;
import com.xiangxun.widget.FullScreenSlidePopupWindow;
import com.xiangxun.widget.MsgToast;
import com.xiangxun.widget.NoViewPager;
import com.xiangxun.widget.TitleView;
import com.xiangxun.widget.WarnSelectDialog;
import com.xiangxun.widget.WarnSelectDialog.selectVioTypeOnClick;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.xiangxun.activity.R.id.ll_doType;
import static com.xiangxun.util.Utils.GetCarStatus;

/**
 * @package: com.xiangxun.activity
 * @ClassName: OrderAckActivity.java
 * @Description: 调度反馈
 * @date: 2015-7-29 上午10:46:18
 */
public class WarnAckActivity extends BaseActivity implements OnClickListener, selectVioTypeOnClick, DeletePictureInterface, OnPageChangeListener {
	private WarnAckActivity mContext = null;
	private TitleView titleView;

	// 添加图片
	private LinearLayout mLlAddPicture = null;
	private ImageView mIvAddPicture = null;
	private NoViewPager mVpAddPicture = null;
	private PublishPictureAdapter mViewPagerAdapter = null;
	// 存放照片url的容器
	private List<String> photoPaths;
	// 浮层
	private View mVSupernatant = null;
	private PopupWindow mPopupWindow = null;

	private LinearLayout mxWarnInfo;
	private TextView mWarnContent;
	private LinearLayout ll_search_device;
	private ViewFlipper vfDevice;
	private TextView devicePicView;
	private TextView deviceInfoView;
	private ImageView mWarnImage;
	private WebView mWarnWeb;
	private TextView mWarnDevice;
	private Button btnCancel;
	private Button btnSign;

	private String imageUrl;

	private TextView mTv_hphm;
	private EditText edtPolice;
	private TextView mTv_Time;
	private LinearLayout mxlIsCapture;
	private LinearLayout mxlIsSuspect;
	private LinearLayout mxlNoSupectReason;
	private LinearLayout mxlNoCaptureReason;
	private LinearLayout mxlDoResult;
	private LinearLayout mxlDoType;
	private LinearLayout mxlDoTypeId;
	private LinearLayout mxlMoveepartment;
	private LinearLayout mxlMovePerson;
	private LinearLayout mxlMovePhone;
	private LinearLayout mllIsCapture;
	private LinearLayout mllIsSuspect;
	private LinearLayout mllNoSupectReason;
	private LinearLayout mllNoCaptureReason;
	private LinearLayout mllDoResult;
	private LinearLayout mllDoType;
	private TextView mTv_IsCapture;
	private TextView mTv_NoCaptureReason;
	private TextView mTv_IsSuspect;
	private TextView mTv_NoSupectReason;
	private TextView mTv_DoType;
	private TextView mTv_DoTable;
	private ImageView mIv_IsCapture;
	private ImageView mIv_IsSuspect;
	private ImageView mIv_NoSupectReason;
	private ImageView mIv_DoType;
	private EditText edtTypeId;
	private EditText edtNote;
	private EditText edtDepartment;
	private EditText edtPerson;
	private EditText edtPhone;
	private Button UpLoad;

	private boolean SignAndAck = false;
	private WarnData warnData;
	private WarnAck warnAck;
	private WarnPic warnPic;

	private WarnSelectDialog IsCaptureDialog;
	private WarnSelectDialog NoCaptureReasonDialog;
	private WarnSelectDialog IsSupectDialog;
	private WarnSelectDialog NoSupectDialog;
	private WarnSelectDialog DoTypeDialog;

	private boolean warnAckView = false;

	private List<WarnTable.Method> Methods;
	private List<View> Views = new ArrayList<View>();

	//详情图片
	private List<View> mViewPagerViews = null;
	private ViewPager mVpDetailPicture = null;
	private FrameLayout mFlViewPagerShow = null;
	private TextView mTvCurrentPage = null;
	private TextView mTvTotalPage = null;
	private int mCurrentPage = 0;
	private FullScreenSlidePopupWindow slidePopupWindow;

	private Handler mUIHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case ConstantStatus.VEHICLE_SEARCH_SUCCESS:
					ResultData.VehicleInfo vehicleInfo = (ResultData.VehicleInfo) msg.obj;
					if (vehicleInfo != null) {
						ShowDeviceInfo(vehicleInfo);
						vfDevice.setDisplayedChild(4);
					} else {
						vfDevice.setDisplayedChild(1);
					}
					break;
				case ConstantStatus.VEHICLE_SEARCH_FALSE:
					vfDevice.setDisplayedChild(1);
					break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.warn_ack_layout);
		mContext = WarnAckActivity.this;

		Intent intent = getIntent();
		warnData = (WarnData)intent.getSerializableExtra("warndata");
		SignAndAck = intent.getBooleanExtra("signandack", false);
		// 检测是否预警签收浏览
		if (1 == warnData.getIsAck()) {
			warnAckView = true;
			//获取预警反馈信息（非签收） 业务类型 1-签收；	2-反馈；3-签收反馈
			warnAck = DBManager.getInstance().getWarnAckInfo(warnData.getYjxh(), "1");
			warnPic = DBManager.getInstance().getWarnPicInfo(warnData.getYjxh());
		}

		initView();
		initData();
		initListener();
	}

	@Override
	public void initView() {
		titleView = (TitleView) findViewById(R.id.tv_comm_title);
		titleView.setLeftBackOneListener(R.drawable.back_normal, this);

		mxWarnInfo = (LinearLayout) findViewById(R.id.warn_info);
		mFlViewPagerShow = (FrameLayout) findViewById(R.id.fl_common_view_pager);
		if (warnAckView) {
			titleView.setTitle("预警反馈详情");
			mxWarnInfo.setVisibility(View.VISIBLE);

			if (warnPic.tp1 != null || warnPic.tp2 != null || warnPic.tp3 != null) {
				mVpDetailPicture = (ViewPager) findViewById(R.id.vp_common_pictures);
				//设置ViewPager显示图片宽高比例为 4:3
				ViewGroup.LayoutParams params = mVpDetailPicture.getLayoutParams();
				params.width = Tools.getScreenPixel(this)[0];
				params.height = (params.width * 3) / 4;
				mVpDetailPicture.setLayoutParams(params);
				mTvCurrentPage = (TextView) findViewById(R.id.tv_common_current_page);
				mTvTotalPage = (TextView) findViewById(R.id.tv_common_total_page);
			}
		}
		else {
			titleView.setTitle(R.string.warn_ack);
			mxWarnInfo.setVisibility(View.GONE);

			mFlViewPagerShow.setVisibility(View.GONE);
		}
		mLlAddPicture = (LinearLayout) findViewById(R.id.woyaozhuandian_add_picture_show);
		mIvAddPicture = (ImageView) findViewById(R.id.iv_publish_add_picture);
		mVpAddPicture = (NoViewPager) findViewById(R.id.vp_publish_add_pictures);
		mVSupernatant = findViewById(R.id.v_supernatant_background);

		mWarnContent = (TextView) findViewById(R.id.warn_content);
		devicePicView = (TextView) findViewById(R.id.tv_device_pic);
		deviceInfoView = (TextView) findViewById(R.id.tv_device_info);

		ll_search_device  = (LinearLayout) findViewById(R.id.ll_search_device);
		vfDevice = (ViewFlipper) findViewById(R.id.vf_search_device);

		mWarnImage = (ImageView) findViewById(R.id.wran_image);
		mWarnWeb = (WebView) findViewById(R.id.wran_web);
		mWarnDevice = (TextView) findViewById(R.id.warn_device);


		mTv_hphm = (TextView) findViewById(R.id.tv_hphm);
		edtPolice = (EditText) findViewById(R.id.edtPolice);
		mTv_Time = (TextView) findViewById(R.id.dodate);

		mxlNoCaptureReason = (LinearLayout) findViewById(R.id.xl_noCaptureReason);
		mxlIsSuspect = (LinearLayout) findViewById(R.id.xl_isSuspect);
		mxlNoSupectReason = (LinearLayout) findViewById(R.id.xl_noSupectReason);
		mxlDoResult = (LinearLayout) findViewById(R.id.xl_doResult);
		mxlDoType = (LinearLayout) findViewById(R.id.xl_doType);
		mxlDoTypeId = (LinearLayout) findViewById(R.id.xl_TypeId);

		mllIsCapture = (LinearLayout) findViewById(R.id.ll_isCapture);
		mllNoCaptureReason = (LinearLayout) findViewById(R.id.ll_noCaptureReason);
		mllIsSuspect = (LinearLayout) findViewById(R.id.ll_isSuspect);
		mllNoSupectReason = (LinearLayout) findViewById(R.id.ll_noSupectReason);
		mllDoResult = (LinearLayout) findViewById(R.id.ll_doResult);
		mllDoType = (LinearLayout) findViewById(R.id.ll_doType);

		mTv_IsCapture = (TextView) findViewById(R.id.isCapture);
		mTv_NoCaptureReason = (TextView) findViewById(R.id.noCaptureReason);
		mTv_IsSuspect = (TextView) findViewById(R.id.isSuspect);
		mTv_NoSupectReason = (TextView) findViewById(R.id.noSuspectReason);
		mTv_DoType = (TextView) findViewById(R.id.tv_doType);
		mTv_DoTable = (TextView) findViewById(R.id.tv_doTable);

		mIv_IsCapture = (ImageView) findViewById(R.id.iv_isCapture);
		mIv_IsSuspect = (ImageView) findViewById(R.id.iv_isSuspect);
		mIv_NoSupectReason = (ImageView) findViewById(R.id.iv_noSuspectReason);
		mIv_DoType = (ImageView) findViewById(R.id.iv_doType);

		edtTypeId = (EditText) findViewById(R.id.edtTypeId);
		edtNote = (EditText) findViewById(R.id.doNote);
		UpLoad = (Button) findViewById(R.id.upload);
		UpLoad.setText("上传处置图片和信息");

		//移交部门
		mxlMoveepartment = (LinearLayout) findViewById(R.id.xl_Moveepartment);
		mxlMovePerson = (LinearLayout) findViewById(R.id.xl_MovePerson);
		mxlMovePhone = (LinearLayout) findViewById(R.id.xl_MovePhone);
		edtDepartment = (EditText) findViewById(R.id.edtMoveDepartment);
		edtPerson = (EditText) findViewById(R.id.edtMovePerson);
		edtPhone = (EditText) findViewById(R.id.edtMovePhone);

		if (warnAckView) {
			mLlAddPicture.setVisibility(View.GONE);
			mIv_IsCapture.setVisibility(View.GONE);
			mIv_IsSuspect.setVisibility(View.GONE);
			mIv_NoSupectReason.setVisibility(View.GONE);
			mIv_DoType.setVisibility(View.GONE);
			UpLoad.setVisibility(View.GONE);
		}
	}

	@Override
	public void initListener() {
		if (warnAckView) {
			mWarnImage.setOnClickListener(this);
			devicePicView.setOnClickListener(this);
			deviceInfoView.setOnClickListener(this);
			mVpDetailPicture.setOnPageChangeListener(this);
		} else {
			mIvAddPicture.setOnClickListener(this);
			mVpAddPicture.setOnClickListener(this);

			mTv_Time.setOnClickListener(this);
			UpLoad.setOnClickListener(this);
			mllIsCapture.setOnClickListener(this);
			mllNoCaptureReason.setOnClickListener(this);
			mllIsSuspect.setOnClickListener(this);
			mllNoSupectReason.setOnClickListener(this);
			mllDoType.setOnClickListener(this);
			mTv_DoTable.setOnClickListener(this);

			IsCaptureDialog.setVioTypeOnClick(this);
			NoCaptureReasonDialog.setVioTypeOnClick(this);
			IsSupectDialog.setVioTypeOnClick(this);
			NoSupectDialog.setVioTypeOnClick(this);
		}
	}

	@Override
	public void initData() {
		if (warnAckView) {
			setWarnPic();
			SetWarnAckViewData();
			edtNote.setSingleLine(false);
		} else
			initDataAck();
	}

	private void initDataAck() {
		// 显示图片位
		photoPaths = new ArrayList<String>();
		mViewPagerAdapter = new PublishPictureAdapter(this, mVpAddPicture, photoPaths);
		mVpAddPicture.setAdapter(mViewPagerAdapter);
		if (photoPaths.size() > 0) {
			mVpAddPicture.setVisibility(View.VISIBLE);
		}

		mTv_hphm.setText(warnData.getHphm());
		edtPolice.setText(SystemCfg.getPoliceName(this));
		mTv_Time.setText(MDate.getDate());

		//获取预警类型对应的非嫌疑车辆原因
		String[] NoSupect = WarnTable.instance().GetWarnLinkReason(warnData.getYwlb(), warnData.getBklx());
		NoSupectDialog = new WarnSelectDialog(this, NoSupect, mTv_NoSupectReason, "选择非嫌疑车辆原因");

		String[] IsCapture = {"已拦截到","未拦截到"};
		IsCaptureDialog = new WarnSelectDialog(this, IsCapture, mTv_IsCapture, "选择拦截情况");

		String[] IsNoCapture = WarnTable.instance().GetNoCaptureReason();
		NoCaptureReasonDialog = new WarnSelectDialog(this, IsNoCapture, mTv_NoCaptureReason, "选择未拦截到原因");
		String[] IsSupect = {"是","否"};
		IsSupectDialog = new WarnSelectDialog(this, IsSupect, mTv_IsSuspect, "选择是否嫌疑车辆");

		Methods = WarnTable.instance().GetWarnLinkMethods(warnData.getBklx(),warnData.getBkzlx());
		Views.clear();
		for (int i = 0, len = Methods.size(); i < len; i ++) {
			final WarnTable.Method method = Methods.get(i);
			View view = addDoMethodView(method.value, i);
			final int index = i;
			RelativeLayout mChecked = (RelativeLayout) view.findViewById(R.id.rl_method);
			mChecked.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (method.must == false){
						if (method.checked == false) {
							method.checked = true;
							//更新互斥\粘连
							ChangeMethodsChecked(index, method);
							//刷新checked
							UpdateMethodViews();
							//添加处理方式View
							AddDoMethodViews();
						} else {
							if (method.linked != true) {
								method.checked = false;
								//更新互斥\粘连
								ChangeMethodsChecked(index, method);
								//刷新checked
								UpdateMethodViews();
								//添加处理方式View
								AddDoMethodViews();
							}
						}
					}
				}
			});

			mllDoResult.addView(view);
			Views.add(view);
		}

		UpdateMethodViews();
	}

	private void UpdateMethodViews () {
		for (int i = 0, len = Methods.size(); i < len; i++) {
			View view = Views.get(i);
			ImageView mChecked = (ImageView) view.findViewById(R.id.checked);
			WarnTable.Method method = Methods.get(i);

			if (method.checked == true){
				mChecked.setBackgroundResource(R.drawable.checkbox_press);
			} else
				mChecked.setBackgroundResource(R.drawable.checkbox_normal);
		}
	}

	private void AddDoMethodViews () {
		boolean isDetainvehicle = false;
		boolean isMove = false;
		boolean isDocument = false;

		for (int i = 0, len = Methods.size(); i < len; i++) {
			View view = Views.get(i);
			ImageView mChecked = (ImageView) view.findViewById(R.id.checked);
			WarnTable.Method method = Methods.get(i);

			if (method.value.equals("扣留机动车")) {
				if (method.checked == true)
					isDetainvehicle = true;
				else
					isDetainvehicle = false;
			} else	if (method.value.equals("现场开具处罚文书") || method.value.equals("已处罚")){
				if (method.checked == true)
					isDocument = true;
			} else if (method.value.equals("移交其他部门")){
				isMove = method.checked  == true;
			}
		}

		ShowDocument(isDocument,isDetainvehicle );
		ShowMoveto(isMove);
	}

	private void ShowDocument(boolean status, boolean isOnlyMust) {

		if (status) {
			mTv_DoType.setText("");
			edtTypeId.setText("");

			mxlDoType.setVisibility(View.VISIBLE);
			mxlDoTypeId.setVisibility(View.VISIBLE);
			mTv_DoTable.setVisibility(View.GONE);
			UpLoad.setText("确认");

			String[] DoType;

			if (isOnlyMust) {
				DoType = new String[1];
				DoType[0] = "强制措施凭证";
			} else {
				DoType = new String[3];
				DoType[0] = "简易程序处罚决定书";
				DoType[1] = "强制措施凭证";
				DoType[2] = "违法处理通知书";
			}
			DoTypeDialog = new WarnSelectDialog(this, DoType, mTv_DoType, "选择文书类型");
			DoTypeDialog.setVioTypeOnClick(this);
		} else {
			mxlDoType.setVisibility(View.GONE);
			mxlDoTypeId.setVisibility(View.GONE);
			UpLoad.setText("上传处置图片和信息");
		}
	}

	private void ShowMoveto (boolean status) {
		if (status) {
			mxlMoveepartment.setVisibility(View.VISIBLE);
			mxlMovePerson.setVisibility(View.VISIBLE);
			mxlMovePhone.setVisibility(View.VISIBLE);
		} else {
			mxlMoveepartment.setVisibility(View.GONE);
			mxlMovePerson.setVisibility(View.GONE);
			mxlMovePhone.setVisibility(View.GONE);
		}
	}

	private void ChangeMethodsChecked (int index, WarnTable.Method curMethod) {

		for (int i = 0, len = Methods.size(); i < len; i++) {
			WarnTable.Method method = Methods.get(i);
			if (i != index){
				if (method.code.equals(curMethod.link)) {
					if (curMethod.checked  == true) {
						method.checked = true;
						method.linked = true;
					}
					else {
						method.checked = false;
						method.linked = false;
					}
				} else {
					if (curMethod.checked  == true) {
						if (curMethod.radio  == true) {
							method.checked = false;
							method.linked = false;
						} else if (method.radio  == true) {
							method.checked = false;
						}
					}
				}
			}
		}
	}

	private View addDoMethodView(String method, int index) {
		final View view = getLayoutInflater().inflate(R.layout.publish_add_method_layout, null);
		final TextView mMethod = (TextView) view.findViewById(R.id.tv_method);
		final ImageView mChecked = (ImageView) view.findViewById(R.id.checked);
		mMethod.setText(method);

		view.setTag(index);
		return  view;
	}

	private void SetWarnAckViewData() {
		String devicePic = getResources().getString(R.string.devicePic);
		SpannableString ssPic = new SpannableString(devicePic);
		ssPic.setSpan(new URLSpan(devicePic), 0, devicePic.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		devicePicView.setText(ssPic);
		String deviceInfo = getResources().getString(R.string.deviceInfo);
		SpannableString ssInfo = new SpannableString(deviceInfo);
		ssInfo.setSpan(new URLSpan(deviceInfo), 0, deviceInfo.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		deviceInfoView.setText(ssInfo);

//		<string name="wran_note">预警序号: %1$s\n预警时间: %2$s\n卡口名称: %3$s\n方向类型: %4$s\n号牌种类: %5$s\n号牌号码: %6$s\n布控类型: %7$s</string>
		String note = String.format(getString(R.string.wran_note),
				warnData.getYjxh(),
				warnData.getYjsj(),
				warnData.getKkmc(),
				DBManager.getInstance().getNameByTypeAndCodeFromDic("DIRECT", "0" + warnData.getFxlx()),
				DBManager.getInstance().getNameByTypeAndCodeFromDic("PLATE_TYPE", warnData.getHpzl()),
				warnData.getHphm(),
				DBManager.getInstance().getNameByTypeAndCodeFromDic("BLACKTYPE_CODE", warnData.getBklx())
		);
		mWarnContent.setText(note);

		mTv_hphm.setText(warnData.getHphm());
		edtPolice.setText(warnAck.czr);
		edtPolice.setEnabled(false);
		mTv_Time.setText(warnAck.czsj);
		if (warnAck.ljclqk.equals("0")) {
			mTv_IsCapture.setText("未拦截到");
		} else {
			mTv_IsCapture.setText("已拦截到");

			mxlIsSuspect.setVisibility(View.VISIBLE);
			if (warnAck.chjg.equals("0")) {
				mTv_IsSuspect.setText("否");

				mTv_NoSupectReason.setText(WarnTable.instance().NORMALREASON[Integer.valueOf(warnAck.wchyy)]);
				mxlNoSupectReason.setVisibility(View.VISIBLE);
			} else {
				mTv_IsSuspect.setText("是");

				//处置方式
				if (!warnAck.cljg.equals("")) {
					Methods = WarnTable.instance().GetWarnLinkMethods(warnData.getBklx(),warnData.getBkzlx());
					for (int i = 0, len = Methods.size(); i < len; i ++) {
						WarnTable.Method method = Methods.get(i);
						if (warnAck.cljg.contains(method.code))
							method.checked = true;
						else
							method.checked = false;
						View view = addDoMethodView(method.value, i);
						mllDoResult.addView(view);
						Views.add(view);
					}
					UpdateMethodViews();
					mxlDoResult.setVisibility(View.VISIBLE);
				}

				//文书类别
				if (!warnAck.wslb.equals("")) {
					mTv_DoType.setText(WarnTable.instance().DOCUMENT[Integer.valueOf(warnAck.wslb)]);
					mxlDoType.setVisibility(View.VISIBLE);
				}
				//文书编号
				if (!warnAck.wsbh.equals("")) {
					edtTypeId.setText(warnAck.wsbh);
					edtTypeId.setEnabled(false);
					mxlDoTypeId.setVisibility(View.VISIBLE);
				}

				//移交部门
				if (!warnAck.yjbm.equals("")) {
					edtDepartment.setText(warnAck.yjbm);
					edtDepartment.setEnabled(false);
					mxlMoveepartment.setVisibility(View.VISIBLE);
				}

				//联系人
				if (!warnAck.lxr.equals("")) {
					edtPerson.setText(warnAck.lxr);
					edtPerson.setEnabled(false);
					mxlMovePerson.setVisibility(View.VISIBLE);
				}

				//联系电话
				if (!warnAck.lxdh.equals("")) {
					edtPhone.setText(warnAck.lxdh);
					edtPhone.setEnabled(false);
					mxlMovePhone.setVisibility(View.VISIBLE);
				}
			}
		}
		edtNote.setText(warnAck.czqkms);
		edtNote.setEnabled(false);
	}

	private void setWarnPic () {
		photoPaths = new ArrayList<String>();
		if (!warnPic.tp1.equals("")) {
			photoPaths.add(0, warnPic.tp1);
		}
		if (!warnPic.tp2.equals("")) {
			photoPaths.add(1, warnPic.tp1);
		}
		if (!warnPic.tp3.equals("")) {
			photoPaths.add(2, warnPic.tp3);
		}

		mViewPagerViews = new ArrayList<View>();
		for (int i = 0; i < photoPaths.size(); i++) {
			slidePopupWindow = FullScreenSlidePopupWindow.getInstance(mContext, photoPaths);
			final LocalNetWorkView localNetWorkView = new LocalNetWorkView(this);
			localNetWorkView.setScaleType(ImageView.ScaleType.FIT_XY);
			if (photoPaths.get(i) != null && !photoPaths.get(i).equals("null") && !photoPaths.get(i).equals("")) {
				localNetWorkView.setTag(photoPaths.get(i));
				ImageCacheLoader.getInstance().getLocalImage(photoPaths.get(i), localNetWorkView, new ImageCacheLoader.GetLocalCallBack() {
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
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		if (mPopupWindow != null) {
			if (mPopupWindow.isShowing()) {
				mPopupWindow.dismiss();
				mVSupernatant.setVisibility(View.GONE);
			} else {
				super.onBackPressed();
			}
		} else {
			if(slidePopupWindow != null)
				slidePopupWindow.popupWindowDismiss();
			super.onBackPressed();
		}
	}

	private boolean savePic() {
		String pic0 = "";
		String pic1 = "";
		String pic2 = "";
		WarnPic picInfo = new WarnPic();

		if (photoPaths != null) {
			String pathString0 = photoPaths.size() >= 1 ? photoPaths.get(0) : "";
			String pathString1 = photoPaths.size() >= 2 ? photoPaths.get(1) : "";
			String pathString2 = photoPaths.size() >= 3 ? photoPaths.get(2) : "";

			File dir = new File(Environment.getExternalStorageDirectory() + "/xiangxun/vioback");
			if (!dir.exists()) {
				dir.mkdir();
			}
			File file = null;
			File tempFile = null;
			String[] path = null;
			String dest = null;
			if (pathString0 != null && pathString0.length() > 1) {
				file = new File(pathString0);
				if (file.exists()) {
					tempFile = file;
					path = file.getAbsolutePath().split("vio/");
					if (path.length == 1) {
						dest = path[0];
					} else
						dest = path[0] + "vioback/" + path[1];

					file.renameTo(new File(dest));
					pic0 = dest;
					tempFile.delete();
					Log.d("SendPicData", tempFile + " has been deleted!");
				}
			}
			if (pathString1 != null && pathString1.length() > 1) {
				file = new File(pathString1);
				if (file.exists()) {
					tempFile = file;
					path = file.getAbsolutePath().split("vio/");
					if (path.length == 1) {
						dest = path[0];
					} else
						dest = path[0] + "vioback/" + path[1];
					file.renameTo(new File(dest));
					pic1 = dest;
					tempFile.delete();
					Log.d("SendPicData", file + " has been deleted!");
				}
			}
			if (pathString2 != null && pathString2.length() > 1) {
				file = new File(pathString2);
				if (file.exists()) {
					tempFile = file;
					path = file.getAbsolutePath().split("vio/");
					if (path.length == 1) {
						dest = path[0];
					} else
						dest = path[0] + "vioback/" + path[1];
					file.renameTo(new File(dest));
					pic2 = dest;
					tempFile.delete();
					Log.d("SendPicData", file + " has been deleted!");
				}
			}
		}
		// 图片加水印

		ImageUtils iu = new ImageUtils(WarnAckActivity.this);

		File f = new File(pic0);
		if (f != null && f.exists() && f.isFile()) {
			iu.pressText(pic0, "", getResources().getString(R.string.warn_no) + ":" + warnData.getYjxh(), getResources().getString(R.string.plate_number) + ":" + warnData.getHphm(), getResources().getString(R.string.controltype) + ":" + DBManager.getInstance().getNameByTypeAndCodeFromDic("BLACKTYPE_CODE", warnData.getBklx()));
		}
		f = new File(pic1);
		if (f != null && f.exists() && f.isFile()) {
			iu.pressText(pic1, "", getResources().getString(R.string.warn_no) + ":" + warnData.getYjxh(), getResources().getString(R.string.plate_number) + ":" + warnData.getHphm(), getResources().getString(R.string.controltype) + ":" + DBManager.getInstance().getNameByTypeAndCodeFromDic("BLACKTYPE_CODE", warnData.getBklx()));
		}
		f = new File(pic2);
		if (f != null && f.exists() && f.isFile()) {
			iu.pressText(pic2, "", getResources().getString(R.string.warn_no) + ":" + warnData.getYjxh(), getResources().getString(R.string.plate_number) + ":" + warnData.getHphm(), getResources().getString(R.string.controltype) + ":" + DBManager.getInstance().getNameByTypeAndCodeFromDic("BLACKTYPE_CODE", warnData.getBklx()));
		}

		picInfo.tp1 = pic0;
		picInfo.tp2 = pic1;
		picInfo.tp3 = pic2;

		picInfo.yjxh = warnData.getYjxh();
		picInfo.scdw = SystemCfg.getDepartment(this);	//处置单位
		picInfo.scr = edtPolice.getText().toString();	//处置民警

		//如果有文书，直接标记上传成功
		if (mxlDoType.getVisibility() == View.VISIBLE) {
			picInfo.isupfile = 1;
			DBManager.getInstance().add(picInfo);
		} else {
			DBManager.getInstance().add(picInfo);
			MsgToast.geToast().setMsg("上传预警图片成功！");
			XiangXunApplication.getInstance().getMainService().start(ConstantStatus.UPLOAD_WARNACKDATA);
		}

		Intent intent = new Intent();
		intent.putExtra("warndata", warnData);
		setResult(ConstantStatus.RESULTCODE_ACK, intent);
		return true;
	}
	private boolean saveDB() {
		WarnAck ackInfo = new WarnAck();
		ackInfo.isupfile = 0;
		ackInfo.yjxh = warnData.getYjxh();	//预警序号

		if (SignAndAck == false)
			ackInfo.ywlx = "2";	//业务类型 		1-签收；	2-反馈；3-签收反馈
		else
			ackInfo.ywlx = "3";	//业务类型 		1-签收；	2-反馈；3-签收反馈

		ackInfo.qsjg = "1";	//签收结果 		1-有效；2-无效
		ackInfo.sfcjlj = "1";	//是否出警拦截	0否1是
		ackInfo.ljclqk = WarnTable.instance().GetCode(mTv_IsCapture.getText().toString());	//拦截车辆情况 	0未拦截到1已拦截到
		if (ackInfo.ljclqk.equals("1")) {
			ackInfo.chjg = WarnTable.instance().GetCode(mTv_IsSuspect.getText().toString());    //是否嫌疑车辆	0 否 1是
			if (ackInfo.chjg.equals("1")) {
				//装处置方式
				for (int i = 0, len = Methods.size(); i < len; i++) {
					WarnTable.Method method = Methods.get(i);
					if (method.checked  == true) {
						ackInfo.cljg += method.code;
					}
				}
				//装法律文书信息
				if (mxlDoType.getVisibility() == View.VISIBLE) {
					ackInfo.wsbh = edtTypeId.getText().toString();    //法律文书编号
					if (ackInfo.wsbh.length() > 0)
						ackInfo.jyw = ackInfo.wsbh.substring(ackInfo.wsbh.length() - 1);    //文书校验位
					else
						ackInfo.jyw = "";
					ackInfo.wslb = WarnTable.instance().GetCode(mTv_DoType.getText().toString());    //文书类别		1 简易程序处罚决定书	3 强制措施凭证	6 违法处理通知书
				}
				//装移交部门信息
				if (mxlMoveepartment.getVisibility() == View.VISIBLE) {
					ackInfo.yjbm = edtDepartment.getText().toString();
					ackInfo.lxr = edtPerson.getText().toString();
					ackInfo.lxdh = edtPhone.getText().toString();
				}
			} else
				ackInfo.wchyy = WarnTable.instance().GetCode(mTv_NoSupectReason.getText().toString());    //非嫌疑车辆原因 01布控信息有误	02号牌号码识别错误	99其他
		} else {
			ackInfo.wljdyy = WarnTable.instance().GetCode(mTv_NoCaptureReason.getText().toString());	//未拦截到原因
		}

		String text = edtNote.getText().toString();
		if (text.contains("'")) {
			text = text.replace("'", ",");
		}
		ackInfo.czqkms = text;	//处置情况描述
		ackInfo.czdw = SystemCfg.getDepartment(this);	//处置单位
		ackInfo.czr = edtPolice.getText().toString();	//处置民警
		ackInfo.czsj = mTv_Time.getText().toString();	//处置时间

		edtNote.setText("");

		//如果有文书，直接标记上传成功
		if (mxlDoType.getVisibility() == View.VISIBLE) {
			ackInfo.isupfile = 1;
		}
		DBManager.getInstance().add(ackInfo);

		warnData.setIsOk(1);
		warnData.setIsDo(1);
		warnData.setIsAck(1);
		DBManager.getInstance().WarnSign(warnData);
//		DBManager.getInstance().WarnAck(warnData);
		if (mxlDoType.getVisibility() != View.VISIBLE) {
			MsgToast.geToast().setMsg("上传预警处置信息成功！");
			XiangXunApplication.getInstance().getMainService().start(ConstantStatus.UPLOAD_WARNACKDATA);
		}
		Intent intent = new Intent();
		intent.putExtra("warndata", warnData);
		setResult(ConstantStatus.RESULTCODE_ACK, intent);
		return true;
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.iv_publish_add_picture:
			// 必要时隐藏输入法键盘
			Utils.hideSoftInputFromWindow(WarnAckActivity.this);
			if (mPopupWindow == null || !mPopupWindow.isShowing()) {
				showSelectAddPath(); // 选择添加图片方式
				mVSupernatant.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.tv_add_picture_from_camera:
			Intent intentCamera = new Intent(WarnAckActivity.this, CameraActivity.class);
			intentCamera.putExtra("size", photoPaths.size());
			intentCamera.setAction("publishThreePhotos");
			startActivityForResult(intentCamera, 1);
			mPopupWindow.dismiss();
			mVSupernatant.setVisibility(View.GONE);
			break;
		case R.id.tv_add_picture_from_album:
			Intent intentAlbum = new Intent();
			intentAlbum.putExtra("size", photoPaths.size());
			intentAlbum.setAction("publishThreePhotos");
			intentAlbum.setClass(mContext, PhotoSelectActivity.class);
			startActivityForResult(intentAlbum, 99);
			mPopupWindow.dismiss();
			mVSupernatant.setVisibility(View.GONE);
			break;
		case R.id.tv_add_picture_cancle:
			mPopupWindow.dismiss();
			mVSupernatant.setVisibility(View.GONE);
			break;
		case R.id.tv_device_pic:
			mWarnDevice.setVisibility(View.GONE);
			imageUrl = warnData.getImageurl();
			ShowWarnPic();
			//ShowWarnPicWeb();
			ll_search_device.setVisibility(View.VISIBLE);
			break;
		case R.id.tv_device_info:
			mWarnImage.setVisibility(View.GONE);
			mWarnWeb.setVisibility(View.GONE);
			vfDevice.setDisplayedChild(0);
			DcNetWorkUtils.searchVehicleInfo(this, warnData.getHphm(), warnData.getHpzl(), mUIHandler);
			ll_search_device.setVisibility(View.VISIBLE);
			break;
		case R.id.dodate:
			Tools.showDateTimePicker(this, mTv_Time);
			break;
		case R.id.ll_isCapture:
			IsCaptureDialog.show();
			break;
		case R.id.ll_noCaptureReason:
			NoCaptureReasonDialog.show();
			break;
		case R.id.ll_isSuspect:
			IsSupectDialog.show();
			break;
		case R.id.ll_noSupectReason:
			NoSupectDialog.show();
			break;
		case ll_doType:
			DoTypeDialog.show();
			break;
		case R.id.tv_doTable:
			GoToWarnTable(mTv_DoType.getText().toString());
			break;
		case R.id.upload:
			Utils.hideSoftInputFromWindow(WarnAckActivity.this);
			if (checkInputText()) {
				//保存反馈信息
				saveDB();
				//保存处置图片
				savePic();

				onBackPressed();
			}
			break;
		case R.id.title_view_back_img:
			Utils.hideSoftInputFromWindow(WarnAckActivity.this);
			onBackPressed();
			break;
		}
	}

	private void GoToWarnTable(String doType) {
		if (false == SystemCfg.getIsPrint(this)) {
			MsgToast.geToast().setMsg("请打开打印设置");
			startActivity(new Intent(this, PrintSettingActivity.class));
		}
		else {
			Intent intent = new Intent();
			intent.putExtra("iswarn", true);
			if (doType.equals("简易程序处罚决定书"))
				intent.setClass(WarnAckActivity.this, PublishSummaryPunishActivity.class);
			else if (doType.equals("违法处理通知书"))
				intent.setClass(WarnAckActivity.this, PublishViolationsNoticeActivity.class);
			else //强制措施凭证
				intent.setClass(WarnAckActivity.this, PublishEnforceMeasureActivity.class);
			startActivityForResult(intent, ConstantStatus.REQCODE_WARNTABLE);
		}
	}

	private boolean checkInputText() {
		if (photoPaths.size() == 0) {
			MsgToast.geToast().setLongMsg("请拍摄处置图片");
			return false;
		}

		if (edtPolice.getText().toString().equals("")) {
			MsgToast.geToast().setLongMsg("请输入处理民警");
			return false;
		}

		if (mTv_Time.getText().toString().equals("")) {
			MsgToast.geToast().setLongMsg("请输入处理时间");
			return false;
		}

		if (mTv_IsCapture.getText().toString().equals("")) {
			MsgToast.geToast().setLongMsg("请输入拦截情况");
			return false;
		}

		if (mTv_IsCapture.getText().toString().equals("已拦截到")) {
			if (mTv_IsSuspect.getText().toString().equals("否")) {
				if (mTv_NoSupectReason.getText().toString().equals("")) {
					MsgToast.geToast().setLongMsg("请输入非嫌疑车辆原因");
					return false;
				}
			} else if (mTv_IsSuspect.getText().toString().equals("是")) {

				boolean isSelectMethod = false;
				for (int i = 0, len = Methods.size(); i < len; i++) {
					WarnTable.Method method = Methods.get(i);
					if (method.checked  == true) {
						String warnType = warnData.getBklx();
						if (warnType.equals("01") || warnType.equals("21") || warnType.equals("22") || warnType.equals("23") || warnType.equals("24") || warnType.equals("25"))
						{
							if (!method.code.equals("5")){
								isSelectMethod = true;
								break;
							}
						}
						else {
							isSelectMethod = true;
							break;
						}
					}
				}
				if (isSelectMethod == false) {
					MsgToast.geToast().setLongMsg("请选择处置方式");
					return false;
				}

				if (mxlDoType.getVisibility() == View.VISIBLE) {
					if (mTv_DoType.getText().toString().equals("")) {
						MsgToast.geToast().setLongMsg("请输入文书类型");
						return false;
					}
					if (edtTypeId.getText().toString().equals("")) {
						MsgToast.geToast().setLongMsg("请输入文书编号");
						return false;
					}
				}

				if (mxlMoveepartment.getVisibility() == View.VISIBLE) {
					if (edtDepartment.getText().toString().equals("")) {
						MsgToast.geToast().setLongMsg("请输入移交到的部门");
						return false;
					}
					if (edtPerson.getText().toString().equals("")) {
						MsgToast.geToast().setLongMsg("请输入移交到的部门联系人");
						return false;
					}
					if (!Tools.isPhoneNumber(edtPhone.getText().toString())) {
						MsgToast.geToast().setMsg("联系方式格式不正确！");
						return false;
					}
				}
			} else {
				MsgToast.geToast().setLongMsg("请输入是否嫌疑车辆");
				return false;
			}
//		} else {
//			if (edtNote.getText().toString().equals("")) {
//				MsgToast.geToast().setLongMsg("请输入拦截情况描述");
//				return false;
//			}
//			String note = edtNote.getText().toString();
//			note = note.replaceAll(" ", "");
//			if (note.length() == 0) {
//				MsgToast.geToast().setLongMsg("拦截情况描述不能为空");
//				return false;
//			}
		}

		return true;
	}

	@Override
	public void vioTypeOnClick(WarnSelectDialog dialog, String vioType) {
		if (IsCaptureDialog == dialog) {
			mxlIsSuspect.setVisibility(View.GONE);
			mxlNoSupectReason.setVisibility(View.GONE);
			mxlDoResult.setVisibility(View.GONE);
			mxlDoType.setVisibility(View.GONE);
			mxlDoTypeId.setVisibility(View.GONE);
			mxlMoveepartment.setVisibility(View.GONE);
			mxlMovePerson.setVisibility(View.GONE);
			mxlMovePhone.setVisibility(View.GONE);
			UpLoad.setText("上传处置图片和信息");

			if (vioType.equals("已拦截到")) {
				mTv_IsSuspect.setText("");
				mxlIsSuspect.setVisibility(View.VISIBLE);
				mxlNoCaptureReason.setVisibility(View.GONE);
			} else {
				mTv_NoCaptureReason.setText("");
				mxlNoCaptureReason.setVisibility(View.VISIBLE);
				mxlIsSuspect.setVisibility(View.GONE);
			}
		} else if (IsSupectDialog == dialog) {
			mxlNoSupectReason.setVisibility(View.GONE);
			mxlDoResult.setVisibility(View.GONE);
			mxlDoType.setVisibility(View.GONE);
			mxlDoTypeId.setVisibility(View.GONE);
			mxlMoveepartment.setVisibility(View.GONE);
			mxlMovePerson.setVisibility(View.GONE);
			mxlMovePhone.setVisibility(View.GONE);
			UpLoad.setText("上传处置图片和信息");
			if (vioType.equals("是")) {
				mxlNoSupectReason.setVisibility(View.GONE);

				mxlDoResult.setVisibility(View.VISIBLE);
				AddDoMethodViews();
			} else {
				mTv_NoSupectReason.setText("");
				mxlNoSupectReason.setVisibility(View.VISIBLE);
			}
		} else if (DoTypeDialog == dialog) {
			mTv_DoTable.setVisibility(View.VISIBLE);
			edtTypeId.setText(DBManager.getInstance().getWarnDoId(vioType, warnData.getHpzl(), warnData.getHphm()));
		}
	}

	private String getIsNull(String content) {
		return TextUtils.isEmpty(content) ? "暂无" : content;
	}

	private void ShowDeviceInfo(ResultData.VehicleInfo vehicleInfo) {
		String info = String.format(getString(R.string.wran_info),
				getIsNull(vehicleInfo.userName),
				getIsNull(vehicleInfo.carBrand),
				getIsNull(vehicleInfo.address),
				getIsNull(DBManager.getInstance().getNameByTypeAndCodeFromDic("CAR_COLOR", vehicleInfo.carColorCode)),
				getIsNull(vehicleInfo.carNum),
				getIsNull(vehicleInfo.shelfNo),
				getIsNull(vehicleInfo.carNum.substring(0,2)),
				getIsNull(vehicleInfo.carNumType),
				getIsNull(DBManager.getInstance().getNameByTypeAndCodeFromDic("CAR_RUN_TYPE", vehicleInfo.useNature)),
				getIsNull(vehicleInfo.distinguishCode),
				getIsNull(GetCarStatus(vehicleInfo.carStatus)),
				getIsNull(vehicleInfo.yxqz));
		mWarnDevice.setText(info);
	}

	private void ShowWarnPic() {
		if (imageUrl != null && (imageUrl.endsWith(".png") || imageUrl.endsWith(".gif") || imageUrl.endsWith(".jpg"))){
			DcHttpClient.getInstance().requestImageScaled(mWarnImage, imageUrl, R.drawable.empty_photo);
			mWarnImage.setVisibility(View.VISIBLE);

			mWarnImage.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.putExtra("msgURL", imageUrl);
					intent.setClass(WarnAckActivity.this, WarnImageActivity.class);
					startActivity(intent);
				}
			});

			vfDevice.setDisplayedChild(2);
		} else
			vfDevice.setDisplayedChild(1);
	}

	private void showSelectAddPath() {
		LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		View popupwindowView = layoutInflater.inflate(R.layout.add_picture_popup_window, null);
		TextView tvAddFromCamera = (TextView) popupwindowView.findViewById(R.id.tv_add_picture_from_camera);
		TextView tvAddFromAlbum = (TextView) popupwindowView.findViewById(R.id.tv_add_picture_from_album);
		TextView tvAddCancle = (TextView) popupwindowView.findViewById(R.id.tv_add_picture_cancle);
		tvAddFromCamera.setOnClickListener(mContext);
		tvAddFromAlbum.setOnClickListener(mContext);
		tvAddCancle.setOnClickListener(mContext);
		mPopupWindow = new PopupWindow(popupwindowView, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		mPopupWindow.setContentView(popupwindowView);
		mPopupWindow.setFocusable(false);
		mPopupWindow.setOutsideTouchable(false);
		mPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
		mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		mPopupWindow.setAnimationStyle(R.style.Add_Picture_AnimationFade);
		mPopupWindow.showAtLocation(popupwindowView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case 1:
				if (data != null) {
					@SuppressWarnings("unchecked")
					List<String> photos = (List<String>) data.getSerializableExtra("camera_picture");
					photoPaths.addAll(photos);
					// 只取前3张图片
					if (photoPaths.size() > 3) {
						photoPaths = photoPaths.subList(0, 3);
						MsgToast.geToast().setMsg("最多添加3照片噢");
					}
					if (photoPaths.size() > 0) {
						mVpAddPicture.setVisibility(View.VISIBLE);
						mViewPagerAdapter.notifyDataSetChanged();
						String picTime = photoPaths.get(0).substring(photoPaths.get(0).lastIndexOf("/") + 4, photoPaths.get(0).lastIndexOf("/") + 18);
						String picTimeFormat = Utils.getConsumerNoteTime(picTime);
						mTv_Time.setText(picTimeFormat);
					}

				}
				break;
			case 99:
				if (data != null) {
					@SuppressWarnings("unchecked")
					List<String> photos = (List<String>) data.getSerializableExtra("album_picture");
					photoPaths.addAll(photos);
					// 只取前4张图片

					if (photoPaths.size() > 3) {
						photoPaths = photoPaths.subList(0, 3);
						MsgToast.geToast().setMsg("最多添加3照片噢");
					}
					if (photoPaths.size() > 0) {
						mVpAddPicture.setVisibility(View.VISIBLE);
						mViewPagerAdapter.notifyDataSetChanged();
						String picTime = photoPaths.get(0).substring(photoPaths.get(0).lastIndexOf("/") + 4, photoPaths.get(0).lastIndexOf("/") + 18);
						String picTimeFormat = Utils.getConsumerNoteTime(picTime);
						mTv_Time.setText(picTimeFormat);
					}

				}
				break;
			case ConstantStatus.REQCODE_WARNTABLE:
				if (resultCode ==ConstantStatus.RESULTCODE_WARNTABLE ) {
					String tableNo = data.getStringExtra("WarnTableNo");
					if (tableNo != null)
						edtTypeId.setText(tableNo);
				}
				break;
		}
	}

	@Override
	public void getPictures(List<String> photos) {
		photoPaths = photos;
		if (photoPaths.size() == 0) {
			mVpAddPicture.setVisibility(View.GONE);
		}
		mViewPagerAdapter.notifyDataSetChanged();
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
