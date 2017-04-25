package com.xiangxun.activity.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.xiangxun.activity.CameraActivity;
import com.xiangxun.activity.PhotoSelectActivity;
import com.xiangxun.activity.R;
import com.xiangxun.activity.setting.PrintSettingActivity;
import com.xiangxun.adapter.PublishPictureAdapter;
import com.xiangxun.app.BaseActivity;
import com.xiangxun.app.XiangXunApplication;
import com.xiangxun.bean.CarInfoList;
import com.xiangxun.bean.EnforcementData;
import com.xiangxun.bean.ListDataItem;
import com.xiangxun.bean.ListDataObj;
import com.xiangxun.bean.MDate;
import com.xiangxun.bean.PrintInfo;
import com.xiangxun.bean.ResultData.PersionInfo;
import com.xiangxun.bean.ResultData.VehicleInfo;
import com.xiangxun.bean.Type;
import com.xiangxun.bean.VioDic;
import com.xiangxun.cfg.SystemCfg;
import com.xiangxun.db.DBManager;
import com.xiangxun.request.DcNetWorkUtils;
import com.xiangxun.service.MainService;
import com.xiangxun.service.StdPrintService;
import com.xiangxun.util.ConstantStatus;
import com.xiangxun.util.ImageUtils;
import com.xiangxun.util.Md5;
import com.xiangxun.util.NumberXUtil;
import com.xiangxun.util.Tools;
import com.xiangxun.util.Utils;
import com.xiangxun.widget.DeletePictureInterface;
import com.xiangxun.widget.MsgDialog;
import com.xiangxun.widget.MsgToast;
import com.xiangxun.widget.NoViewPager;
import com.xiangxun.widget.PublishSelectAffairsAddressDialog;
import com.xiangxun.widget.PublishSelectAffairsPlateTypeDialog;
import com.xiangxun.widget.TitleView;
import com.xiangxun.wtone.IDCameraActivity;
import com.xiangxun.wtone.wtUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

//import com.xiangxun.service.PrintService;

/**
 * @package: com.xiangxun.activity.home
 * @ClassName: PublishEnforceMeasureActivity.java
 * @Description: 行政強制措施业务处理页面
 * @author: HanGJ
 * @date: 2015-8-17 上午11:10:25
 */
@SuppressLint("DefaultLocale")
public class PublishEnforceMeasureActivity extends BaseActivity implements OnClickListener, DeletePictureInterface {
	private PublishEnforceMeasureActivity mContext = null;
	private TitleView titleView;
	private NumberXUtil keybord;
	// 添加图片
	private ImageView mIvAddPicture = null;
	private NoViewPager mVpAddPicture = null;
	private PublishPictureAdapter mViewPagerAdapter = null;
	// 存放照片url的容器

	private List<String> photoPaths;
	private TextView mPulishCode;
	private EditText mDriverName;
	private EditText mPhoneNum;
	private EditText mDriverNum;
	private LinearLayout mPublishClickCarnum;
	private EditText mDriverRecordCode;
	private EditText mLicenseOffice;
	private EditText mLicenseType;

	private EditText mPlateNum;
	private LinearLayout mPublishClickPlatenum;
	private TextView mPublishPlateType;
	private LinearLayout mPublishClickPlateType;
	private PublishSelectAffairsPlateTypeDialog affairsPlateTypeDialog;
	private TextView mPlateOffice;

	private EditText mTvVioType;
	private String VioTypeString = null;
//	private LinearLayout ll_publish_click_viotype;
//	private PublishSelectAffairsAddressDialog affairsVioTypeDialog;

	private TextView mTvAction;
	private LinearLayout ll_publish_click_action;
	private PublishSelectAffairsAddressDialog affairsActionDialog;

	private TextView mTvAddress;
	private LinearLayout ll_publish_click_address;
	private PublishSelectAffairsAddressDialog affairsAddressDialog;
	
	private EditText mTvRoadLocation;
	private TextView mTvRoadDirect;
	private LinearLayout ll_publish_click_RoadDirect;
	private PublishSelectAffairsAddressDialog affairsRoadDirectDialog;
	
	private TextView mPublishVioTime;

	private ListDataObj ldo = null;
	private ToggleButton mTgBtnDriverSearch;
	private LinearLayout mLlDriverSearch;
	private ToggleButton mTgBtnPlateSearch;
	private LinearLayout mLlPlateSearch;

	// private EditText mContent;
	// private Button mRecogDriverLic;
	// private Button mRecogPlate;
	// private Button mQueryDriverLic;
	// private Button mQueryPlate;
	private ProgressDialog mDialog;
	private MainService mService;
	private StdPrintService mPrintService;
	private boolean isSaved;
	private Receiver receiver;

	private Button submitData;
	private XiangXunApplication mApplication;
	private EnforcementData enforcedata;

	// 浮层
	private View mVSupernatant = null;
	private PopupWindow mPopupWindow = null;

	private MsgDialog printerDialog;
	private boolean isSetPrint;
	
	@SuppressLint("HandlerLeak")
	private Handler mHander = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			mDialog.dismiss();
			switch (msg.what) {
			case ConstantStatus.PlateQueryFalse:
				MsgToast.geToast().setMsg(R.string.nodata);
				break;
			case ConstantStatus.DriverInfoSuccess:
				mDriverName.setText(msg.getData().getString("name"));
				mDriverRecordCode.setText(msg.getData().getString("filesnum"));
				mLicenseOffice.setText(msg.getData().getString("issueoffice"));
				mLicenseType.setText(msg.getData().getString("cartype"));
				MsgToast.geToast().setMsg(R.string.plateNotifyCompleted);
				break;

			case ConstantStatus.CarInfoSuccess:
				Bundle bundle = msg.getData();
				if (bundle != null) {
					mPlateOffice.setText(bundle.getString("office"));

				}
				break;
			case ConstantStatus.SUCCESS:
				String pulishCode = DBManager.getInstance().getPunishId("force");
				if (pulishCode != null && pulishCode.length() > 0) {
					mPulishCode.setText(pulishCode);
				} else {
					MsgToast.geToast().setMsg(R.string.getNumFailed);
				}
				break;
			case ConstantStatus.PlateQuerySuccess:
				Bundle bundle2 = msg.getData();
				CarInfoList carinfolist = (CarInfoList) bundle2.getSerializable("carlist");
				if (carinfolist == null || carinfolist.list == null || carinfolist.list.get(0) == null || carinfolist.list.get(0).platetype == null) {
					MsgToast.geToast().setMsg(R.string.nodata);
				}

				// String type = carinfolist.list.get(0).platetype;
				// Adapter adapter = mTypeSpinner.getAdapter();
				// int count = adapter.getCount();
				// int index = -1;
				// for (int i = 0; i < count; i++) {
				// if (adapter.getItem(i).toString().equals(type)) {
				// index = i;
				// break;
				// }
				// }
				// if (index > -1) {
				// mTypeSpinner.setSelection(index);
				// }
				mPlateOffice.setText(carinfolist.list.get(0).issueoffice);
				MsgToast.geToast().setMsg(R.string.plateNotifyCompleted);
				break;
			case ConstantStatus.DRIVER_SEARCH_SUCCESS:
				PersionInfo persionInfo = (PersionInfo) msg.obj;
				if (persionInfo != null) {
					setViewData(persionInfo);
				} else {
					MsgToast.geToast().setMsg("无此驾驶员信息！");
				}
				break;

			case ConstantStatus.DRIVER_SEARCH_FALSE:
				MsgToast.geToast().setMsg("驾驶员数据查询失败, 稍后请重试!");
				break;

			case ConstantStatus.NetWorkError:
				MsgToast.geToast().setMsg("网络异常");
				break;
			case ConstantStatus.VEHICLE_SEARCH_SUCCESS:
				VehicleInfo vehicleInfo = (VehicleInfo) msg.obj;
				if (vehicleInfo != null && vehicleInfo.carNum != null) {
					mPlateOffice.setText(vehicleInfo.carNum.substring(0, 2));
				} else {
					MsgToast.geToast().setMsg("无此机动车信息！");
				}
				break;

			case ConstantStatus.VEHICLE_SEARCH_FALSE:
				MsgToast.geToast().setMsg("机动车数据查询失败, 稍后请重试!");
				break;
			case ConstantStatus.OTHER_PLATENUM:
				Utils.showPlateInfo(PublishEnforceMeasureActivity.this, msg);
				break;
			case ConstantStatus.SHOW_KEYBOARD:
				keybord.showKeyboard();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.publish_enforcement_measure_layout);
		mContext = PublishEnforceMeasureActivity.this;
		isSetPrint = SystemCfg.getIsPrint(this);
		initView();
		initData();
		initListener();
	}

	protected void setViewData(PersionInfo persionInfo) {
		mDriverName.setText(persionInfo.name);
		mDriverRecordCode.setText(persionInfo.dabh);
		mLicenseOffice.setText(persionInfo.licenseDepartment);
		mLicenseType.setText(persionInfo.drvierType);
	}

	@Override
	public void initView() {
		titleView = (TitleView) findViewById(R.id.tv_comm_title);
		titleView.setLeftBackOneListener(R.drawable.back_normal, this);
		if (isSetPrint) {
			titleView.setRightViewLeftOneListener(R.drawable.icon_print, this);
		}
		titleView.setTitle(R.string.xzqzcs);

		mIvAddPicture = (ImageView) findViewById(R.id.iv_publish_add_picture);
		mVpAddPicture = (NoViewPager) findViewById(R.id.vp_publish_add_pictures);

		mPulishCode = (TextView) findViewById(R.id.publish_code);
		mDriverName = (EditText) findViewById(R.id.driver_name);
		mPhoneNum = (EditText) findViewById(R.id.call_phone_num);
		mDriverNum = (EditText) findViewById(R.id.driver_num);
		mPublishClickCarnum = (LinearLayout) findViewById(R.id.ll_publish_click_carnum);
		mDriverRecordCode = (EditText) findViewById(R.id.driver_record_code);
		mLicenseOffice = (EditText) findViewById(R.id.license_office);
		mLicenseType = (EditText) findViewById(R.id.license_type);
		mPlateNum = (EditText) findViewById(R.id.driver_plate_num);
		mPublishClickPlatenum = (LinearLayout) findViewById(R.id.ll_publish_click_platenum);
		mPublishPlateType = (TextView) findViewById(R.id.tv_publish_platetype);
		mPublishClickPlateType = (LinearLayout) findViewById(R.id.ll_publish_click_platetype);
		mPlateOffice = (EditText) findViewById(R.id.plate_office);
		mTvVioType = (EditText) findViewById(R.id.tv_publish_viotype);
//		ll_publish_click_viotype = (LinearLayout) findViewById(ll_publish_click_viotype);
		mTvAddress = (TextView) findViewById(R.id.tv_vio_address);
		mTvRoadLocation = (EditText) findViewById(R.id.tv_vio_road_location);
		mTvRoadDirect = (TextView) findViewById(R.id.tv_vio_road_direct);
		ll_publish_click_address = (LinearLayout) findViewById(R.id.ll_publish_click_address);
		ll_publish_click_RoadDirect = (LinearLayout) findViewById(R.id.ll_publish_click_road_direct);

		LinearLayout layout_roadlocation = (LinearLayout) findViewById(R.id.layout_roadlocation);
		LinearLayout layout_roaddirect = (LinearLayout) findViewById(R.id.layout_roaddirect);
		if (getResources().getString(R.string.workroad_city).equalsIgnoreCase(SystemCfg.getWorkRoad(this))) {
			layout_roadlocation.setVisibility(View.GONE);
			layout_roaddirect.setVisibility(View.GONE);
		}
		mPublishVioTime = (TextView) findViewById(R.id.accedtdate);
		mPublishVioTime.setText(MDate.getDate());

		mTvAction = (TextView) findViewById(R.id.driver_action);
		ll_publish_click_action = (LinearLayout) findViewById(R.id.ll_publish_click_action);

		submitData = (Button) findViewById(R.id.options);
		mLlDriverSearch = (LinearLayout) findViewById(R.id.ll_search_driver);
		mTgBtnDriverSearch = (ToggleButton) findViewById(R.id.tgbtn_search_driver);
		mLlPlateSearch = (LinearLayout) findViewById(R.id.ll_driver_plate_num);
		mTgBtnPlateSearch = (ToggleButton) findViewById(R.id.tgbtn_search_plate);
		mVSupernatant = findViewById(R.id.v_supernatant_background);

		keybord = new NumberXUtil(PublishEnforceMeasureActivity.this, PublishEnforceMeasureActivity.this, mDriverNum);
	}

	@Override
	public void initData() {
		if (isSetPrint) {
			Intent in = new Intent(this, StdPrintService.class);
			bindService(in, conn, Context.BIND_AUTO_CREATE);
		}
		mDialog = new ProgressDialog(this);
		mApplication = XiangXunApplication.getInstance();
		mService = mApplication.getMainService();
		photoPaths = new ArrayList<String>();
		receiver = new Receiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("621carlist");
		registerReceiver(receiver, filter);
		String pulishCode = DBManager.getInstance().getPunishId("force");
		if (pulishCode != null && pulishCode.length() > 0) {
			mPulishCode.setText(pulishCode);
		} else {
			// 更新行政强制措施编号数据
			DcNetWorkUtils.updateForceId(this, SystemCfg.getUserId(this), "force", DBManager.getInstance(), mHander);
			// MsgToast.geToast().setMsg(R.string.getNumFailed);
		}
		// 显示图片位

		mViewPagerAdapter = new PublishPictureAdapter(this, mVpAddPicture, photoPaths);
		mVpAddPicture.setAdapter(mViewPagerAdapter);
		if (photoPaths.size() > 0) {
			mVpAddPicture.setVisibility(View.VISIBLE);
		}
		String[] roads = DBManager.getInstance().queryRoadname();
		affairsAddressDialog = new PublishSelectAffairsAddressDialog(this, roads, mTvAddress, "选择违法地点");
		String[] directs = DBManager.getInstance().getRoadDirection();
		affairsRoadDirectDialog = new PublishSelectAffairsAddressDialog(this, directs, mTvRoadDirect, getResources().getString(R.string.roaddirect_input));
		// 获取本地数据库中的违法数据词典和道路数据
//		String[] viotypes = DBManager.getInstance().queryVioType();
//		affairsVioTypeDialog = new PublishSelectAffairsAddressDialog(this, viotypes, mTvVioType, "选择违法行为");
		String[] ActionTyps = {"扣留机动车","扣留非机动车","扣留机动车驾驶证","扣留机动车行驶证","校验血液、尿样","拖移机动车"};
		affairsActionDialog  = new PublishSelectAffairsAddressDialog(this, ActionTyps, mTvAction, "选择采取的强制措施");

		ldo = new ListDataObj();
		ArrayList<Type> platetypes = DBManager.getInstance().getPlateTypes();
		for (int i = 0; i < platetypes.size(); i++) {
			ldo.add(platetypes.get(i).code, platetypes.get(i).name);
		}
		affairsPlateTypeDialog = new PublishSelectAffairsPlateTypeDialog(this, ldo.getlist(), mPublishPlateType);
		affairsPlateTypeDialog.setSelectCapture(getResources().getString(R.string.def_platetype));
	}

	@Override
	public void initListener() {
		mIvAddPicture.setOnClickListener(this);
		mVpAddPicture.setOnClickListener(this);
//		ll_publish_click_viotype.setOnClickListener(this);
		ll_publish_click_address.setOnClickListener(this);
		ll_publish_click_RoadDirect.setOnClickListener(this);
		mPublishClickPlateType.setOnClickListener(this);
		mPublishClickCarnum.setOnClickListener(this);
		mPublishClickPlatenum.setOnClickListener(this);
		mLlDriverSearch.setOnClickListener(this);
		mLlPlateSearch.setOnClickListener(this);
		ll_publish_click_action.setOnClickListener(this);
		mDriverNum.addTextChangedListener(new TextWatcher() {

			private int index = 0;

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String searchWord = s.toString().trim();
				if (s.toString().length() == 1 && searchWord.length() == 0) {
					mDriverNum.setText(s.toString().trim());
				}
				boolean isClear = searchWord.length() == 0;
				mTgBtnDriverSearch.setChecked(!isClear);

				mDriverNum.removeTextChangedListener(this);// 解除文字改变事件
				index = mDriverNum.getSelectionStart();// 获取光标位置
				mDriverNum.setText(s.toString().toUpperCase());
				mDriverNum.setSelection(index);// 重新设置光标位置
				mDriverNum.addTextChangedListener(this);// 重新绑定事件
			}
		});

		mDriverNum.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Utils.hideSoftInputFromWindow(PublishEnforceMeasureActivity.this);
				if(event.getAction()==MotionEvent.ACTION_UP){
					mHander.removeMessages(ConstantStatus.SHOW_KEYBOARD);
					mHander.sendEmptyMessageDelayed(ConstantStatus.SHOW_KEYBOARD, 100);
				}
				return false;
			}
		});

		mDriverNum.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus == false) {
					mHander.removeMessages(ConstantStatus.SHOW_KEYBOARD);
					keybord.hideKeyboard();
				}
				else {
					keybord.hideSoftInputMethod();
				}
			}
		});

		mPlateNum.addTextChangedListener(new TextWatcher() {

			private int index = 0;

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String searchWord = s.toString().toUpperCase().trim();
				if (s.toString().length() == 1 && searchWord.length() == 0) {
					mPlateNum.setText(s.toString().trim());
				}
				boolean isClear = searchWord.length() == 0;
				mTgBtnPlateSearch.setChecked(!isClear);

				mPlateNum.removeTextChangedListener(this);// 解除文字改变事件
				index = mPlateNum.getSelectionStart();// 获取光标位置
				mPlateNum.setText(searchWord);
				if (searchWord.length() > 2)
					mPlateOffice.setText(searchWord.substring(0,2));
				mPlateNum.setSelection(index);// 重新设置光标位置
				mPlateNum.addTextChangedListener(this);// 重新绑定事件
				Utils.chkOtherPlate(mContext, mHander, mPlateNum, searchWord);
			}
		});
		mPublishVioTime.setOnClickListener(this);
		submitData.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Utils.hideSoftInputFromWindow(PublishEnforceMeasureActivity.this);
				if (getIntent().getExtras() == null) {
					if (checkInputText()) {
						if (isSetPrint) {
							if (!isPrint) {
								MsgToast.geToast().setMsg("请打印凭证，并拍照凭证！");
								return;
							}
						}
						saveDB();
						onBackPressed();
					}
				}
			}
		});

		mLicenseType.addTextChangedListener(new TextWatcher() {
			int index = 0;

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				mLicenseType.removeTextChangedListener(this);// 解除文字改变事件
				index = mLicenseType.getSelectionStart();// 获取光标位置
				mLicenseType.setText(s.toString().toUpperCase());
				mLicenseType.setSelection(index);// 重新设置光标位置
				mLicenseType.addTextChangedListener(this);// 重新绑定事件
			}
		});

		mDriverRecordCode.addTextChangedListener(new TextWatcher() {
			int index = 0;

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				mDriverRecordCode.removeTextChangedListener(this);// 解除文字改变事件
				index = mDriverRecordCode.getSelectionStart();// 获取光标位置
				mDriverRecordCode.setText(s.toString().toUpperCase());
				mDriverRecordCode.setSelection(index);// 重新设置光标位置
				mDriverRecordCode.addTextChangedListener(this);// 重新绑定事件
			}
		});

		mTvVioType.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (mTvVioType.getText() != null && mTvVioType.getText().toString().length() > 1) {
					VioTypeString = DBManager.getInstance().getVioNameByCode(mTvVioType.getText().toString());
					if (VioTypeString != null) {
						Utils.hideSoftInputFromWindow(PublishEnforceMeasureActivity.this);
						MsgToast.geToast().setMsg("违法行为:" + VioTypeString);
					}
				}
			}
		});
	}

	private void printData() {
		if (!mPrintService.isPrintConnected()) {
			printerDialog = Utils.showNoPrinter(PublishEnforceMeasureActivity.this);
			printerDialog.setButLeftListener(PublishEnforceMeasureActivity.this);
			printerDialog.setButRightListener(PublishEnforceMeasureActivity.this);
		} else {
//			if (!isPrint) {
				if (mPrintService.isPrintConnected()) {
					connectPrintDevice();
				} else {
					MsgToast.geToast().setMsg(R.string.iniBlueToothPrinter);
				}
//			} else {
//				MsgToast.geToast().setMsg("编辑模式不支持重复打印！");
//			}
		}
	}

	@Override
	protected void onDestroy() {
		isSaved = false;
		super.onDestroy();
		if (isSetPrint) {
			unbindService(conn);
			mPrintService.onDestroy();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (keybord != null && keybord.getKeyboardVisible())
				keybord.hideKeyboard();
			else
				onBackPressed();

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@SuppressWarnings("deprecation")
	private void showSelectAddPath() {
		LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		View popupwindowView = layoutInflater.inflate(R.layout.add_picture_popup_window, null);
		TextView tvAddFromCamera = (TextView) popupwindowView.findViewById(R.id.tv_add_picture_from_camera);
		TextView tvAddFromAlbum = (TextView) popupwindowView.findViewById(R.id.tv_add_picture_from_album);
		TextView tvAddCancle = (TextView) popupwindowView.findViewById(R.id.tv_add_picture_cancle);
		tvAddFromCamera.setOnClickListener(mContext);
		tvAddFromAlbum.setOnClickListener(mContext);
		tvAddCancle.setOnClickListener(mContext);
		mPopupWindow = new PopupWindow(popupwindowView, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		mPopupWindow.setContentView(popupwindowView);
		mPopupWindow.setFocusable(false);
		mPopupWindow.setOutsideTouchable(false);
		mPopupWindow.setWidth(LayoutParams.MATCH_PARENT);
		mPopupWindow.setHeight(LayoutParams.WRAP_CONTENT);
		mPopupWindow.setAnimationStyle(R.style.Add_Picture_AnimationFade);
		mPopupWindow.showAtLocation(popupwindowView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
	}

	private boolean isPrint = false;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 1:
			if (data != null) {
				@SuppressWarnings("unchecked")
				List<String> photos = (List<String>) data.getSerializableExtra("camera_picture");
				photoPaths.addAll(photos);
				// 只取前4张图片

				if (photoPaths.size() > 4) {
					photoPaths = photoPaths.subList(0, 4);
					MsgToast.geToast().setMsg("最多添加4照片噢");
				}
				if (photoPaths.size() > 0) {
					mVpAddPicture.setVisibility(View.VISIBLE);
					mViewPagerAdapter.notifyDataSetChanged();
					String picTime = photoPaths.get(0).substring(photoPaths.get(0).lastIndexOf("/") + 4, photoPaths.get(0).lastIndexOf("/") + 18);
					String picTimeFormat = Utils.getConsumerNoteTime(picTime);
					mPublishVioTime.setText(picTimeFormat);
				}

			}
			break;
		case 99:
			if (data != null) {
				@SuppressWarnings("unchecked")
				List<String> photos = (List<String>) data.getSerializableExtra("album_picture");
				photoPaths.addAll(photos);
				// 只取前4张图片

				if (photoPaths.size() > 4) {
					photoPaths = photoPaths.subList(0, 4);
					MsgToast.geToast().setMsg("最多添加4照片噢");
				}
				if (photoPaths.size() > 0) {
					mVpAddPicture.setVisibility(View.VISIBLE);
					mViewPagerAdapter.notifyDataSetChanged();
					String picTime = photoPaths.get(0).substring(photoPaths.get(0).lastIndexOf("/") + 4, photoPaths.get(0).lastIndexOf("/") + 18);
					String picTimeFormat = Utils.getConsumerNoteTime(picTime);
					mPublishVioTime.setText(picTimeFormat);
				}

			}
			break;
		case 2:// driver
			if (resultCode == Activity.RESULT_OK) {
				if (data != null) {
					String recogResult = data.getStringExtra("recogResult");
					String exception = data.getStringExtra("exception");
					if (exception != null && !exception.equals("")) {
						MsgToast.geToast().setMsg("未识别出驾驶证信息，请重新识别！");
					} else {
						if (recogResult != null && recogResult.length() > 0) {
							mDriverNum.setText(recogResult);
						} else {
							MsgToast.geToast().setMsg("未识别出驾驶证信息，请重新识别！");
						}
					}
				}
			}
			break;
		case 3:// plate
			if (resultCode == Activity.RESULT_OK) {
				if (null != data) {
					if (data.getExtras().getString("platenum") != null) {
						MsgToast.geToast().setMsg(R.string.plateNotifyCompleted);
						mPlateNum.setText(data.getExtras().getString("platenum"));
					} else if (data.getExtras().getString("platenum") == null) {
						MsgToast.geToast().setMsg(R.string.plateNotifyError);
					}
				} else {
					MsgToast.geToast().setMsg(R.string.plateNotifyError);
				}
			}
			break;

		case 7:// driving license
			if (resultCode == Activity.RESULT_OK) {
				if (data != null) {
					String recogResult = data.getStringExtra("recogResult");
					String exception = data.getStringExtra("exception");
					if (exception != null && !exception.equals("")) {
						MsgToast.geToast().setMsg("未识别出驾驶证信息，请重新识别！");
					} else {
						if (recogResult != null && recogResult.length() > 0) {
							mDriverNum.setText(recogResult);
						} else {
							MsgToast.geToast().setMsg("未识别出驾驶证信息，请重新识别！");
						}
					}
				}
			}
			break;
		case 10:
			Intent i = new Intent(this, StdPrintService.class);
			bindService(i, conn, Context.BIND_AUTO_CREATE);
			break;
		case 11:
			if (resultCode == Activity.RESULT_OK) {
				mPlateNum.setText(data.getStringExtra("platenum"));
				affairsPlateTypeDialog.setSelectId(data.getStringExtra("platetype"));
			}
			break;
		case ConstantStatus.RQECOE_CHANGEPRINT:
			if (ConstantStatus.RESULTCODE_CHANGEPRINT == resultCode) {
				boolean isPrint = data.getBooleanExtra("isSetPrint", false);
				if (isPrint == false) {
					isSetPrint = false;
					titleView.setRightViewLeftVisibile();
					if (mPrintService != null) {
						unbindService(conn);
						mPrintService.onDestroy();
					}
				}
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
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

		builder.setNegativeButton(R.string.syscfgcanel, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
			}
		});
		builder.show();
	}

	/**
	 * @author sunhaifeng
	 * @return boolean 打印
	 */
	private boolean print() {
		if (mPrintService != null) {
			mPrintService.print(mPrintService.formatContent(generateContent(), 10), PrintInfo.TITLE_TYPE_DETAINMENT);
			isPrint = true;
			return true;
		} else {
			return false;
		}

	}

	/**
	 * @author sunhaifeng 检查文本输入格式

	 * @return boolean
	 */

	private boolean checkInputText() {
		if (null != photoPaths) {
			if (isSetPrint) {
				if (photoPaths.size() <= 1) {
					MsgToast.geToast().setMsg(R.string.noprintpicturemessage);
					return false;
				}
			} else {
				if (photoPaths.size() <= 0) {
					MsgToast.geToast().setMsg(R.string.nopicturemessage);
					return false;
				}
			}
		} else {
			MsgToast.geToast().setMsg(R.string.nopicture);
			return false;
		}
		return  checkInputData();
	}

	private boolean checkInputData() {
		if (mPulishCode.getText().length() < 1) {
			MsgToast.geToast().setMsg(R.string.numError);
			return false;
		}

		if (mDriverName.getText().length() < 1) {
			MsgToast.geToast().setMsg(R.string.checkName);
			return false;
		}

		if (Tools.isSpecialCharacter(mDriverName.getText().toString())) {
			MsgToast.geToast().setMsg(R.string.checkName);
			return false;
		}

		if (!Tools.isPhoneNumber(mPhoneNum.getText().toString())) {
			MsgToast.geToast().setMsg("联系方式格式不正确！");
			return false;
		}

		if (!Tools.isIDCardValidate(mDriverNum.getText().toString())) {
			return false;
		}

		if (mDriverRecordCode.getText().length() < 1) {
			MsgToast.geToast().setMsg(R.string.checkRecordId);
			return false;
		}

		if (mLicenseOffice.getText().length() < 1 || mLicenseOffice.getText().length() > 32 || Tools.isSpecialCharacter(mLicenseOffice.getText().toString())) {
			MsgToast.geToast().setMsg(R.string.checkDriveLicenorganization);
			return false;
		}

		if (mLicenseType.getText().length() < 1) {
			MsgToast.geToast().setMsg(R.string.checkDriveType);
			return false;
		}

		if (mLicenseType.getText().length() != 2) {
			MsgToast.geToast().setMsg("准驾车型格式不正确，请确认！");
			return false;
		}

		if (mPlateNum.getText().toString().length() != 7) {
			Toast.makeText(PublishEnforceMeasureActivity.this, "号牌号码格式不正确，请确认！", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (mPlateNum.getText().length() < 1 || !Tools.isCarnumberNO(mPlateNum.getText().toString())) {
			MsgToast.geToast().setMsg(R.string.checkPlateNum);
			return false;
		}

		if (mPublishPlateType.getText().toString() == null || mPublishPlateType.getText().toString().length() <= 0) {
			MsgToast.geToast().setMsg(R.string.checkPlateType);
			return false;
		}

		if (mPlateOffice.getText().length() < 1 || Tools.isSpecialCharacter(mPlateOffice.getText().toString())) {
			MsgToast.geToast().setMsg(R.string.checkPlateorganization);
			return false;
		}

		if (mTvAddress.getText() != null && mTvAddress.getText().toString().length() < 1) {
			MsgToast.geToast().setMsg("请选择违法地点");
			return false;
		}

		if (mTvVioType.getText() != null && mTvVioType.getText().toString().length() < 1) {
			MsgToast.geToast().setMsg(R.string.checkVioContent);
			return false;
		}

		if (mTvVioType.getText().toString().length() < 1) {
			MsgToast.geToast().setMsg("请输入违法代码");
			return false;
		}

		if (VioTypeString == null || VioTypeString.equals("")) {
			MsgToast.geToast().setMsg("请输入正确的违法代码");
			return false;
		}

		if (mTvAction.getText().length() < 1) {
			MsgToast.geToast().setMsg(R.string.checkPunishment);
			return false;
		}

		return true;
	}

	/**
	 * @author sunhaifeng
	 * @return String 收集填写的信息并以一定格式排列，为打印机提供打印内容
	 */
	private String generateContent() {
		StringBuffer content = new StringBuffer();
		Resources resources = getResources();
		content.append(resources.getString(R.string.bianhao) + mPulishCode.getText().toString() + "%end%");
		content.append(resources.getString(R.string.beichufaren) + mDriverName.getText().toString() + "%end%");
		content.append(resources.getString(R.string.phone) + "：" + mPhoneNum.getText().toString() + "%end%");
		content.append(resources.getString(R.string.driverId) + "%end%");
		content.append(mDriverNum.getText().toString() + "%end%");
		content.append(resources.getString(R.string.recordId) + mDriverRecordCode.getText().toString() + "%end%");
		content.append(resources.getString(R.string.organization) + mLicenseOffice.getText().toString() + "  ");
		content.append(resources.getString(R.string.driveType) + mLicenseType.getText().toString() + "%end%");
		content.append(resources.getString(R.string.plateNum) + mPlateNum.getText().toString() + "%end%");
		content.append(resources.getString(R.string.plateType) + mPublishPlateType.getText().toString() + "%end%");
//		content.append(resources.getString(R.string.organization) + mPlateOffice.getText().toString() + "%end%");
		content.append(resources.getString(R.string.dangshiren));	// + mDriverName.getText().toString());
		content.append(resources.getString(R.string.yu) + Utils.ChangeTimeTochs(mPublishVioTime.getText().toString()));
		content.append(resources.getString(R.string.zai) + mTvAddress.getText().toString());
		if (resources.getString(R.string.workroad_speed).equalsIgnoreCase(SystemCfg.getWorkRoad(this))) {
			content.append("(" + resources.getString(R.string.roadlocation_flag) + mTvRoadLocation.getText().toString());
			content.append(resources.getString(R.string.roaddirect_flag) + mTvRoadDirect.getText().toString() + ")");
		}
//		content.append(resources.getString(R.string.shishi) + mTvVioType.getText().toString());
//		VioType vt = DBManager.getInstance().getVioTypeByName(mTvVioType.getText().toString());
		content.append(resources.getString(R.string.shishi) + VioTypeString);
		VioDic vt = DBManager.getInstance().queryVioTypeByKeyCode(mTvVioType.getText().toString());
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
		content.append(resources.getString(R.string.wfnr) + resources.getString(R.string.code) + vt.getCode());
		content.append(resources.getString(R.string.according) + vt.getRule());
		content.append("%start%" + resources.getString(R.string.takePunishment) + mTvAction.getText().toString());
		content.append("%start%" + String.format(resources.getString(R.string.action_note),  "子长县公安局交通管理大队", "子长县人民政府或子长县公安局", "子长县"));
		return content.toString();
	}

	/**
	 * 自定义事件修改选择框

	 */

	// 蓝牙打印连接
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

	/**
	 * @author sunhaifeng 将录入的行政强制措施存入数据库

	 */
	private void saveDB() {
		String pic0 = "";
		String pic1 = "";
		String pic2 = "";
		String pic3 = "";

		enforcedata = new EnforcementData();
		if (isSaved)
			return;
		enforcedata.isupfile = 0;
		enforcedata.code = mTvVioType.getText().toString().trim();
		enforcedata.viotype = VioTypeString;	//mTvVioType.getText().toString().trim();
		enforcedata.id = mPulishCode.getText().toString();
		enforcedata.name = mDriverName.getText().toString();
		enforcedata.phone = mPhoneNum.getText().toString();
		enforcedata.driverlic = mDriverNum.getText().toString();
		enforcedata.recordid = mDriverRecordCode.getText().toString();
		enforcedata.licenseoffice = mLicenseOffice.getText().toString();
		enforcedata.licensetype = mLicenseType.getText().toString();
		enforcedata.plate = mPlateNum.getText().toString();
		ListDataItem listDataItem = (ListDataItem) mPublishPlateType.getTag();
		enforcedata.platetype = listDataItem.getid();
		enforcedata.plateoffice = mPlateOffice.getText().toString();
		enforcedata.location = mTvAddress.getText().toString();
		enforcedata.roadlocation = mTvRoadLocation.getText().toString();
		enforcedata.roaddirect = DBManager.getInstance().getRoadDirectionCodeByName(mTvRoadDirect.getText().toString());
		enforcedata.location = mTvAddress.getText().toString();
		enforcedata.datetime = mPublishVioTime.getText().toString();
		enforcedata.action = mTvAction.getText().toString();
		if (photoPaths != null) {
			String pathString0 = photoPaths.size() >= 1 ? photoPaths.get(0) : "";
			String pathString1 = photoPaths.size() >= 2 ? photoPaths.get(1) : "";
			String pathString2 = photoPaths.size() >= 3 ? photoPaths.get(2) : "";
			String pathString3 = photoPaths.size() >= 4 ? photoPaths.get(3) : "";

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
			if (pathString3 != null && pathString3.length() > 1) {
				file = new File(pathString3);
				if (file.exists()) {
					tempFile = file;
					path = file.getAbsolutePath().split("vio/");
					if (path.length == 1) {
						dest = path[0];
					} else
						dest = path[0] + "vioback/" + path[1];
					file.renameTo(new File(dest));
					pic3 = dest;
					tempFile.delete();
					Log.d("SendPicData", file + " has been deleted!");
				}
			}
		}
		// 图片加水印

		ImageUtils iu = new ImageUtils(PublishEnforceMeasureActivity.this);

		File f = new File(pic0);
		if (f != null && f.exists() && f.isFile()) {
			iu.pressText(pic0, "", getResources().getString(R.string.textviewaddress) + ":" + mTvAddress.getText().toString(), getResources().getString(R.string.fangweima) + ":" + Md5.MD5("" + "" + mTvAddress.getText().toString() + mPublishVioTime.getText().toString() + "0" + "" + "" + "").substring(0, 9));
		}
		f = new File(pic1);
		if (f != null && f.exists() && f.isFile()) {
			iu.pressText(pic1, "", getResources().getString(R.string.textviewaddress) + ":" + mTvAddress.getText().toString(), getResources().getString(R.string.fangweima) + ":" + Md5.MD5("" + "" + mTvAddress.getText().toString() + mPublishVioTime.getText().toString() + "1" + "" + "" + "").substring(0, 9));
		}
		f = new File(pic2);
		if (f != null && f.exists() && f.isFile()) {
			iu.pressText(pic2, "", getResources().getString(R.string.textviewaddress) + ":" + mTvAddress.getText().toString(), getResources().getString(R.string.fangweima) + ":" + Md5.MD5("" + "" + mTvAddress.getText().toString() + mPublishVioTime.getText().toString() + "2" + "" + "" + "").substring(0, 9));
		}
		f = new File(pic3);
		if (f != null && f.exists() && f.isFile()) {
			iu.pressText(pic3, "", getResources().getString(R.string.textviewaddress) + ":" + mTvAddress.getText().toString(), getResources().getString(R.string.fangweima) + ":" + Md5.MD5("" + "" + mTvAddress.getText().toString() + mPublishVioTime.getText().toString() + "3" + "" + "" + "").substring(0, 9));
		}

		enforcedata.pic1 = pic0;
		enforcedata.pic2 = pic1;
		enforcedata.pic3 = pic2;
		enforcedata.pic4 = pic3;

		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		enforcedata.vioid = tm.getDeviceId() + System.currentTimeMillis();

		DBManager.getInstance().add(enforcedata);
		isSaved = true;
		DBManager.getInstance().markPunishId(mPulishCode.getText().toString(), "force");
		XiangXunApplication.getInstance().getMainService().start(ConstantStatus.UPLOAD_ENFORCEMENTDATA);
		MsgToast.geToast().setMsg(R.string.uploaded);
	}

	@Override
	public void onBackPressed() {
		if (mPopupWindow != null) {
			if (mPopupWindow.isShowing()) {
				mPopupWindow.dismiss();
				mVSupernatant.setVisibility(View.GONE);
			} else {
				setResult(Activity.RESULT_CANCELED);
				super.onBackPressed();
			}
		} else {
			setResult(Activity.RESULT_CANCELED);
			super.onBackPressed();
		}
	}

	private class Receiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			CarInfoList carinfolist = (CarInfoList) intent.getExtras().getSerializable("mcarlist");
			Message msg = new Message();
			if (carinfolist == null) {
				msg.what = ConstantStatus.PlateQueryFalse;
			} else {
				msg.what = ConstantStatus.PlateQuerySuccess;
				Bundle bundle = new Bundle();
				bundle.putInt("count", carinfolist.count);
				bundle.putInt("sumcount", carinfolist.sumcount);
				bundle.putInt("index", carinfolist.firstindex);
				for (int i = 0; i < carinfolist.list.size(); i++) {
					bundle.putString("platenum" + i, carinfolist.list.get(i).platenum);
					bundle.putString("platetype" + i, carinfolist.list.get(i).platetype + "");
				}
				bundle.putSerializable("carlist", carinfolist);
				msg.setData(bundle);
			}
			mHander.sendMessage(msg);
		}

	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.iv_publish_add_picture:
			// 必要时隐藏输入法键盘
//			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//			imm.hideSoftInputFromWindow(PublishEnforceMeasureActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			Utils.hideSoftInputFromWindow(PublishEnforceMeasureActivity.this);
			if (mPopupWindow == null || !mPopupWindow.isShowing()) {
				showSelectAddPath(); // 选择添加图片方式
				mVSupernatant.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.tv_add_picture_from_camera:
			Intent intentCamera = new Intent(PublishEnforceMeasureActivity.this, CameraActivity.class);
			intentCamera.putExtra("size", photoPaths.size());
			intentCamera.setAction("publishFourPhotos");
			startActivityForResult(intentCamera, 1);
			mPopupWindow.dismiss();
			mVSupernatant.setVisibility(View.GONE);
			break;
		case R.id.tv_add_picture_from_album:
			Intent intentAlbum = new Intent();
			intentAlbum.putExtra("size", photoPaths.size());
			intentAlbum.setAction("publishFourPhotos");
			intentAlbum.setClass(mContext, PhotoSelectActivity.class);
			startActivityForResult(intentAlbum, 99);
			mPopupWindow.dismiss();
			mVSupernatant.setVisibility(View.GONE);
			break;
		case R.id.tv_add_picture_cancle:
			mPopupWindow.dismiss();
			mVSupernatant.setVisibility(View.GONE);
			break;
		case R.id.accedtdate:
			// 弹出时间选择器

			Tools.showDateTimePicker(this, mPublishVioTime);
			break;
		case R.id.title_view_back_img:
			onBackPressed();
			break;
		case R.id.title_view_operation_imageview:
			if (isSetPrint && checkInputData()) {
				printData();
			}
			break;
//		case ll_publish_click_viotype:
//			affairsVioTypeDialog.show();
//			break;
		case R.id.ll_publish_click_address:
			affairsAddressDialog.show();
			break;
		case R.id.ll_publish_click_road_direct:
			affairsRoadDirectDialog.show();
			break;			
		case R.id.ll_publish_click_platetype:
			affairsPlateTypeDialog.show();
			break;
		case R.id.ll_publish_click_action:
			affairsActionDialog.show();
			break;
		case R.id.ll_search_driver:
			if (!mTgBtnDriverSearch.isChecked()) {
				Intent intentDriver = new Intent(this, IDCameraActivity.class);
				intentDriver.putExtra("nMainId", 5);
				intentDriver.putExtra("devcode", SystemCfg.getRegistCode(this));
				startActivityForResult(intentDriver, 7);
			} else {
				if (mDriverNum.getText().toString().length() == 0) {
					MsgToast.geToast().setMsg(R.string.driverIdNotNull);
					return;
				}
				mDialog.setMessage(getResources().getString(R.string.checking_hold));
				mDialog.setTitle("");
				mDialog.setIndeterminate(true);
				mDialog.setCancelable(true);
				mDialog.show();
				DcNetWorkUtils.searchDriver(PublishEnforceMeasureActivity.this, mDriverNum.getText().toString(), mHander);
			}
			break;
		case R.id.ll_driver_plate_num:
			if (!mTgBtnPlateSearch.isChecked()) {
				Intent intentPlate = wtUtils.StartPlateRecog(PublishEnforceMeasureActivity.this);
				startActivityForResult(intentPlate, 3);
			} else {
				if (mPlateNum.getText().toString().length() == 0) {
					MsgToast.geToast().setMsg(R.string.plateNotNull);
					return;
				}
				if (mPublishPlateType.getText().toString() == null || mPublishPlateType.getText().toString().length() <= 0) {
					MsgToast.geToast().setMsg(R.string.checkPlateType);
					return;
				}
				mDialog.setMessage(getResources().getString(R.string.checking_hold));
				mDialog.setTitle("");
				mDialog.setIndeterminate(true);
				mDialog.setCancelable(true);
				mDialog.show();
				ListDataItem platetype = (ListDataItem) mPublishPlateType.getTag();
				DcNetWorkUtils.searchVehicleInfo(PublishEnforceMeasureActivity.this, mPlateNum.getText().toString(), platetype.getid(), mHander);
			}
			break;
		case R.id.dia_tv_but2:
			printerDialog.dismiss();
			startActivityForResult(new Intent(this, PrintSettingActivity.class), ConstantStatus.RQECOE_CHANGEPRINT);
			break;
		case R.id.dia_tv_but1:
			printerDialog.dismiss();
//			MsgToast.geToast().setMsg(R.string.sureConnectBlueTooth);
//			startActivityForResult(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS), 10);
			unbindService(conn);
			mPrintService.onDestroy();
			//重新连接打印机
			Intent in = new Intent(this, StdPrintService.class);
			bindService(in, conn, Context.BIND_AUTO_CREATE);
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
	
}
