package com.xiangxun.activity.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
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
import com.xiangxun.bean.FieldPunishData;
import com.xiangxun.bean.ListDataItem;
import com.xiangxun.bean.ListDataObj;
import com.xiangxun.bean.MDate;
import com.xiangxun.bean.PrintInfo;
import com.xiangxun.bean.ResultData;
import com.xiangxun.bean.PersionInfoX;
import com.xiangxun.bean.ResultData.RoadExt;
import com.xiangxun.bean.VehicleInfoX;
import com.xiangxun.bean.RoadInfo;
import com.xiangxun.bean.SimpleVio;
import com.xiangxun.bean.Type;
import com.xiangxun.bean.UpdateVio;
import com.xiangxun.bean.VioData;
import com.xiangxun.bean.VioDic;
import com.xiangxun.cfg.SystemCfg;
import com.xiangxun.db.DBManager;
import com.xiangxun.request.DcNetWorkUtils;
import com.xiangxun.service.MainService;
import com.xiangxun.service.StdPrintService;
import com.xiangxun.util.ConstantStatus;
import com.xiangxun.util.ImageUtils;
import com.xiangxun.util.KeyboardUtil;
import com.xiangxun.util.Md5;
import com.xiangxun.util.Tools;
import com.xiangxun.util.Utils;
import com.xiangxun.widget.DeletePictureInterface;
import com.xiangxun.widget.MsgDialog;
import com.xiangxun.widget.MsgToast;
import com.xiangxun.widget.NoViewPager;
import com.xiangxun.widget.PublishSelectAffairsAddressDialog;
import com.xiangxun.widget.PublishSelectAffairsAddressDialog.selectVioTypeOnClick;
import com.xiangxun.widget.PublishSelectAffairsPlateTypeDialog;
import com.xiangxun.widget.RoadEditView;
import com.xiangxun.widget.TitleView;
import com.xiangxun.widget.VioEditView;
import com.xiangxun.widget.VioEditView.onItemvioNameClick;
import com.xiangxun.wtone.IDCameraActivity;
import com.xiangxun.wtone.wtUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @package: com.xiangxun.activity.home
 * @ClassName: PublishSummaryPunishActivity.java
 * @Description: 发布简易程序处罚页面


 * @author: HanGJ
 * @date: 2015-8-18 上午8:42:04
 */
@SuppressLint({ "HandlerLeak", "DefaultLocale" })
public class PublishSummaryPunishActivity extends BaseActivity implements OnClickListener, DeletePictureInterface, selectVioTypeOnClick, onItemvioNameClick {
	private PublishSummaryPunishActivity mContext = null;
	private TitleView titleView;
	private KeyboardUtil keybord;
	private int inputType;
	// 添加图片
	private LinearLayout mLlAddPicture;
	private ImageView mIvAddPicture = null;
	private NoViewPager mVpAddPicture = null;
	private PublishPictureAdapter mViewPagerAdapter = null;
	// 存放照片url的容器


	private List<String> photoPaths;
	private TextView mPulishCode;

	private TextView mPublishPersonType;
	private LinearLayout mPublishClickPersonType;
	private PublishSelectAffairsAddressDialog affairsPlatePersonTypeDialog;
	private TextView mPublishCarType;
	private LinearLayout mPublishClickCarType;
	private PublishSelectAffairsAddressDialog affairsPlateCarTypeDialog;
	private TextView mPublishTrafficType;
	private LinearLayout mPublishClickTrafficType;
	private PublishSelectAffairsAddressDialog affairsPlateTrafficTypeDialog;
	private TextView mPublishPayType;
	private LinearLayout mPublishClickPayType;
	private LinearLayout mLxPublishClickPayType;
	private PublishSelectAffairsAddressDialog affairsPlatePayTypeDialog;
	private TextView mPublishPayStatus;
	private LinearLayout mPublishClickPayStatus;
	private LinearLayout mLxPublishClickPayStatus;
	private PublishSelectAffairsAddressDialog affairsPlatePayStatuscDialog;


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
	private EditText mOwner;
	private TextView mPlateOffice;

	private VioEditView mTvVioType;
	private String VioTypeString = null;
//	private LinearLayout ll_publish_click_viotype;
//	private PublishSelectAffairsAddressDialog affairsVioTypeDialog;

	private RoadEditView mTvAddress;
//	private LinearLayout ll_publish_click_address;
//	private PublishSelectAffairsAddressDialog affairsAddressDialog;
	private TextView mTvRoadDirect;	//上行、下行
	private EditText mTvRoadZhan;
	private TextView mTvRoadStep;	//路口路段
	private EditText mTvRoadKm;
	private EditText mTvRoadMi;

	private LinearLayout lx_publish_click_RoadDirect;
	private LinearLayout ll_publish_click_RoadDirect;
	private PublishSelectAffairsAddressDialog affairsRoadDirectDialog;
	private LinearLayout lx_publish_click_RoadZhan;
	private LinearLayout lx_publish_click_RoadStep;
	private LinearLayout ll_publish_click_RoadStep;
	private PublishSelectAffairsAddressDialog affairsRoadStepDialog;


	private TextView mPublishVioTime;

	private TextView mTvAction;
	private LinearLayout mllAction;
	private PublishSelectAffairsAddressDialog affairsActionDialog;

	private LinearLayout mLlmoney;
	private EditText mMoney;
	private LinearLayout mLlValue;
	private EditText mValue;

	private ListDataObj ldo = null;
	private ToggleButton mTgBtnDriverSearch;
	private LinearLayout mLlDriverSearch;
	private ToggleButton mTgBtnPlateSearch;
	private LinearLayout mLlPlateSearch;
	private Button submitData;

	private String vioid;
	private Receiver receiver;
	private StdPrintService mPrintService;
	private XiangXunApplication mApplication;
	private ProgressDialog mDialog;
	private boolean isSaved;
	private String viotype;
	private FieldPunishData fpd;
	private MainService mService = null;
	private boolean isPrint = false;
	// 浮层
	private View mVSupernatant = null;
	private PopupWindow mPopupWindow = null;
	String pic0 = "";
	String pic1 = "";
	String pic2 = "";
	String pic3 = "";

	private MsgDialog printerDialog;
	private boolean isSetPrint;
	private String yjxh = "";

	private RoadExt roadExt;
	private PersionInfoX persionInfo;
	private VehicleInfoX vehicleInfo;


	private Handler mHander = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mDialog.dismiss();
			switch (msg.what) {
			case ConstantStatus.UpLoadSuccess:
				if (!getIntent().getAction().equals("view")) {
//					if (!isPrint) {
						if (mPrintService.isPrintConnected()) {
							connectPrintDevice();
						} else {
							MsgToast.geToast().setMsg(R.string.iniBlueToothPrinter);
						}
//					} else {
//						MsgToast.geToast().setMsg("编辑模式不支持重复打印！");
//						return;
//					}
				} else {
					if (mPrintService.isPrintConnected()) {
						connectPrintDevice();
					} else {
						MsgToast.geToast().setMsg(R.string.iniBlueToothPrinter);
					}
				}
				break;
			case ConstantStatus.UpLoadFalse:
				break;
			case ConstantStatus.SUCCESS:
				String pulishCode = DBManager.getInstance().getPunishId("punish");
				if (pulishCode != null && pulishCode.length() > 0) {
					mPulishCode.setText(pulishCode);
				} else {
					MsgToast.geToast().setMsg(R.string.getNumFailed);
				}
				break;
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

			case ConstantStatus.PlateQuerySuccess:
				if (msg.obj != null) {
					mPlateOffice.setText((String) msg.obj);
				}
				break;
			case ConstantStatus.CarInfoSuccess:
				Bundle bundle = msg.getData();
				CarInfoList carinfolist = (CarInfoList) bundle.getSerializable("carlist");
				if (carinfolist == null || carinfolist.list == null || carinfolist.list.get(0) == null || carinfolist.list.get(0).platetype == null) {
					MsgToast.geToast().setMsg(R.string.nodata);
				}
				mPlateOffice.setText(carinfolist.list.get(0).issueoffice);
				MsgToast.geToast().setMsg(R.string.plateNotifyCompleted);
				break;
			case ConstantStatus.DRIVER_SEARCH_SUCCESS:
				persionInfo = (PersionInfoX) msg.obj;
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
				MsgToast.geToast().setMsg("获取信息异常");
				break;
			case ConstantStatus.VEHICLE_SEARCH_SUCCESS:
				vehicleInfo = (VehicleInfoX) msg.obj;
				if (vehicleInfo != null && vehicleInfo.hphm != null) {
					mPlateOffice.setText(vehicleInfo.hphm.substring(0,2));
					mOwner.setText(vehicleInfo.syr);
					mPublishTrafficType.setText(DBManager.getInstance().getNameByTypeAndCodeFromDic("JTFS", vehicleInfo.cllx));
				} else {
					MsgToast.geToast().setMsg("无此机动车信息！");
				}
				break;

			case ConstantStatus.VEHICLE_SEARCH_FALSE:
				MsgToast.geToast().setMsg("机动车数据查询失败, 稍后请重试!");
				break;
			case ConstantStatus.OTHER_PLATENUM:
				Utils.showPlateInfo(PublishSummaryPunishActivity.this, msg);
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
		setContentView(R.layout.publish_summary_punishment_layout);
		mContext = PublishSummaryPunishActivity.this;
		isSetPrint = SystemCfg.getIsPrint(this);

		Intent intent = getIntent();
		yjxh = intent.getStringExtra("yjxh");

		initView();

		String hphm = intent.getStringExtra("hphm");
		if (hphm != null && !hphm.equals("")) {
			mPlateNum.setText(hphm);
			if (hphm.length() > 2)
				mPlateOffice.setText(hphm.substring(0,2));
		}
		initData();
		initListener();

		String RoadHistory = SystemCfg.getRoadHistory(this);
		mTvAddress.setText(RoadHistory);
//		DBManager.getInstance().markPunishId("610623300037311", "force");
//		DBManager.getInstance().markPunishId("610623300037312", "force");
//		DBManager.getInstance().markPunishId("610623300037313", "force");
//		DBManager.getInstance().markPunishId("610623300037314", "force");
//		DBManager.getInstance().markPunishId("610623300037315", "force");
//		DBManager.getInstance().markPunishId("610623300037316", "force");
//		DBManager.getInstance().markPunishId("610623300037317", "force");
//		mDriverNum.setText("610202197811021227");
//		mPlateNum.setText("陕B31999");
	}

	protected void setViewData(PersionInfoX persionInfo) {
		mDriverName.setText(persionInfo.xm);
		mPhoneNum.setText(persionInfo.sjhm);
		mDriverRecordCode.setText(persionInfo.dabh);
		mLicenseOffice.setText(persionInfo.fzjg);
		mLicenseType.setText(persionInfo.zjcx);
	}

	@Override
	public void initView() {
		titleView = (TitleView) findViewById(R.id.tv_comm_title);
		titleView.setLeftBackOneListener(R.drawable.back_normal, this);
		if (isSetPrint) {
			titleView.setRightViewLeftOneListener(R.drawable.icon_print, this);
		}
		titleView.setTitle(R.string.jycfjds);

		mLlAddPicture = (LinearLayout) findViewById(R.id.woyaozhuandian_add_picture_show);
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
		mTvVioType = (VioEditView) findViewById(R.id.tv_publish_viotype);
//		ll_publish_click_viotype = (LinearLayout) findViewById(ll_publish_click_viotype);
		mTvAddress = (RoadEditView) findViewById(R.id.tv_vio_address);

		mTvRoadDirect = (TextView) findViewById(R.id.tv_vio_road_direct);
		mTvRoadZhan = (EditText) findViewById(R.id.tv_vio_road_zhan);
		mTvRoadKm = (EditText) findViewById(R.id.tv_vio_road_km);
		mTvRoadMi = (EditText) findViewById(R.id.tv_vio_road_metre);
		mTvRoadStep = (TextView) findViewById(R.id.tv_vio_road_step);

		lx_publish_click_RoadDirect = (LinearLayout) findViewById(R.id.layout_roaddirect);
		ll_publish_click_RoadDirect = (LinearLayout) findViewById(R.id.ll_publish_click_road_direct);
		lx_publish_click_RoadZhan = (LinearLayout) findViewById(R.id.layout_roadzhan);
		lx_publish_click_RoadStep = (LinearLayout) findViewById(R.id.layout_roadstep);
		ll_publish_click_RoadStep = (LinearLayout) findViewById(R.id.ll_publish_click_road_step);

		mPublishVioTime = (TextView) findViewById(R.id.accedtdate);
		mPublishVioTime.setText(MDate.getDate());

		mTvAction = (TextView) findViewById(R.id.tv_publish_action);
		mllAction = (LinearLayout) findViewById(R.id.ll_publish_click_action);

		mLlmoney = (LinearLayout) findViewById(R.id.ll_publish_click_money);
		mMoney = (EditText) findViewById(R.id.tv_publish_money);
		mLlValue = (LinearLayout) findViewById(R.id.ll_publish_click_value);
		mValue = (EditText) findViewById(R.id.tv_publish_value);

		submitData = (Button) findViewById(R.id.options);
		mLlDriverSearch = (LinearLayout) findViewById(R.id.ll_search_driver);
		mTgBtnDriverSearch = (ToggleButton) findViewById(R.id.tgbtn_search_driver);
		mLlPlateSearch = (LinearLayout) findViewById(R.id.ll_driver_plate_num);
		mTgBtnPlateSearch = (ToggleButton) findViewById(R.id.tgbtn_search_plate);
		mOwner = (EditText) findViewById(R.id.plate_car_owner);
		mVSupernatant = (View) findViewById(R.id.v_supernatant_background);

		keybord = new KeyboardUtil(PublishSummaryPunishActivity.this, mDriverNum);

		mPublishPersonType = (TextView) findViewById(R.id.tv_publish_persontype);
		mPublishClickPersonType = (LinearLayout) findViewById(R.id.ll_publish_click_persontype);
		mPublishCarType = (TextView) findViewById(R.id.tv_publish_cartype);
		mPublishClickCarType = (LinearLayout) findViewById(R.id.ll_publish_click_cartype);
		mPublishTrafficType = (TextView) findViewById(R.id.tv_publish_traffictype);
		mPublishClickTrafficType = (LinearLayout) findViewById(R.id.ll_publish_click_traffictype);
		mPublishPayType = (TextView) findViewById(R.id.tv_publish_paytype);
		mPublishClickPayType = (LinearLayout) findViewById(R.id.ll_publish_click_paytype);
		mLxPublishClickPayType = (LinearLayout) findViewById(R.id.lx_publish_click_paytype);
		mPublishPayStatus = (TextView) findViewById(R.id.tv_publish_paystatus);
		mPublishClickPayStatus = (LinearLayout) findViewById(R.id.ll_publish_click_paystatus);
		mLxPublishClickPayStatus = (LinearLayout) findViewById(R.id.lx_publish_click_paystatus);

		if (yjxh != null && yjxh.length() > 1) {
			mLlAddPicture.setVisibility(View.GONE);
//			submitData.setVisibility(View.GONE);
		}
	}

	@Override
	public void initListener() {
		mIvAddPicture.setOnClickListener(this);
		mVpAddPicture.setOnClickListener(this);
		mTvVioType.setvioNameClick(this);
		ll_publish_click_RoadDirect.setOnClickListener(this);

		ll_publish_click_RoadStep.setOnClickListener(this);
		mPublishClickPlateType.setOnClickListener(this);
		mPublishClickCarnum.setOnClickListener(this);
		mPublishClickPlatenum.setOnClickListener(this);
		mLlDriverSearch.setOnClickListener(this);
		mLlPlateSearch.setOnClickListener(this);
		mllAction.setOnClickListener(this);
		affairsActionDialog.setVioTypeOnClick(this);
		affairsPlatePayTypeDialog.setVioTypeOnClick(this);
		mTvAddress.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void afterTextChanged(Editable editable) {
				String roadname = editable.toString().trim();
				if (roadname.length() > 6 && roadname.contains("-")){
					String roadCode = mTvAddress.getRoadCode();
					if (roadCode != null && !roadCode.equals("")) {
						SystemCfg.setRoadHistory(PublishSummaryPunishActivity.this, roadname);
						showRoadStep();
					} else
						HideRoadStep();
				} else
					HideRoadStep();
			}
		});
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
				Utils.hideSoftInputFromWindow(PublishSummaryPunishActivity.this);
				if(event.getAction()==MotionEvent.ACTION_UP){
					keybord.changeKeyboardNumberX();
					keybord.SetKeyboardEdit(mDriverNum);
					mHander.removeMessages(ConstantStatus.SHOW_KEYBOARD);
					mHander.sendEmptyMessageDelayed(ConstantStatus.SHOW_KEYBOARD, 200);
				}
				return false;
			}
		});

		mDriverNum.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus == false) {
					if (!mPlateNum.hasFocus()) {
						mHander.removeMessages(ConstantStatus.SHOW_KEYBOARD);
						keybord.hideKeyboard();
					}
				}
				else {
					keybord.hideSoftInputMethod();
				}
			}
		});

		mPlateNum.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Utils.hideSoftInputFromWindow(PublishSummaryPunishActivity.this);
				if(event.getAction()==MotionEvent.ACTION_UP){
					if (mPlateNum.getText().toString().length() == 0)
						keybord.changeKeyboard(false);
					else
						keybord.changeKeyboard(true);
					keybord.SetKeyboardEdit(mPlateNum);
					mHander.removeMessages(ConstantStatus.SHOW_KEYBOARD);
					mHander.sendEmptyMessageDelayed(ConstantStatus.SHOW_KEYBOARD, 200);
				}
				return false;
			}
		});
		mPlateNum.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus == false) {
					if (!mDriverNum.hasFocus()) {
						mHander.removeMessages(ConstantStatus.SHOW_KEYBOARD);
						keybord.hideKeyboard();
					}
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
				Utils.hideSoftInputFromWindow(PublishSummaryPunishActivity.this);
				if (checkInputText()) {
					if (isSetPrint) {
						if (!isPrint) {
							MsgToast.geToast().setMsg("请打印凭证，并拍照凭证！");
							return;
						}
					}
//					if (yjxh != null && yjxh.length() > 1)

					saveWarnVioData();
					saveData();
					onBackPressed();
				}
			}
		});

		mLicenseOffice.addTextChangedListener(new TextWatcher() {
			int index = 0;

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				mLicenseOffice.removeTextChangedListener(this);// 解除文字改变事件
				index = mLicenseOffice.getSelectionStart();// 获取光标位置
				mLicenseOffice.setText(s.toString().toUpperCase());
				mLicenseOffice.setSelection(index);// 重新设置光标位置
				mLicenseOffice.addTextChangedListener(this);// 重新绑定事件
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

		mPublishClickPersonType.setOnClickListener(this);
		mPublishClickCarType.setOnClickListener(this);
		mPublishClickTrafficType.setOnClickListener(this);
		mPublishClickPayType.setOnClickListener(this);
		mPublishClickPayStatus.setOnClickListener(this);
	}

	@Override
	public void initData() {
		if (isSetPrint) {
			Intent in = new Intent(this, StdPrintService.class);
			bindService(in, conn, Service.BIND_AUTO_CREATE);
		}

		photoPaths = new ArrayList<String>();
		mApplication = XiangXunApplication.getInstance();
		mService = mApplication.getMainService();
		receiver = new Receiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("621carlist");
		registerReceiver(receiver, filter);
		mDialog = new ProgressDialog(this);
		if (null != getIntent().getAction()) {
			if (getIntent().getAction().equals("new")) {
				vioid = getIntent().getExtras().getString("vioid");
			}
		}

		// 显示图片位


		mViewPagerAdapter = new PublishPictureAdapter(this, mVpAddPicture, photoPaths);
		mVpAddPicture.setAdapter(mViewPagerAdapter);
		if (photoPaths.size() > 0) {
			mVpAddPicture.setVisibility(View.VISIBLE);
		}

		String[] directs = DBManager.getInstance().getRoadDirection();

		affairsRoadDirectDialog = new PublishSelectAffairsAddressDialog(this, directs, mTvRoadDirect, getResources().getString(R.string.roaddirect_input));
		// 获取本地数据库中的违法数据词典和道路数据
//		String[] viotypes = DBManager.getInstance().queryVioType();
//		affairsVioTypeDialog = new PublishSelectAffairsAddressDialog(this, viotypes, mTvVioType, "选择违法行为");

		ldo = new ListDataObj();
		ArrayList<Type> platetypes = DBManager.getInstance().getPlateTypes();

		for (int i = 0; i < platetypes.size(); i++) {
			ldo.add(platetypes.get(i).code, platetypes.get(i).name);
		}
		affairsPlateTypeDialog = new PublishSelectAffairsPlateTypeDialog(this, ldo.getlist(), mPublishPlateType);
		affairsPlateTypeDialog.setSelectCapture(getResources().getString(R.string.def_platetype));
//		if (getIntent().getExtras() != null) {
//			Bundle bundle = getIntent().getExtras();
//			mPlateNum.setText(bundle.getString("plate"));
//			if (getIntent().getAction().equals("new")) {
//				LinearLayout addpic = (LinearLayout) findViewById(R.id.woyaozhuandian_add_picture_show);
//				addpic.setVisibility(View.GONE);
//				String location = bundle.getString("location");
//				mPublishVioTime.setText(bundle.getString("datetime"));
//				affairsAddressDialog.setSelectId(location);
//				viotype = bundle.getString("viotype");
////				affairsVioTypeDialog.setSelectId(viotype);
////				VioType vt = DBManager.getInstance().getVioTypeByName(mTvVioType.getText().toString());
////				VioType vt = DBManager.getInstance().getVioTypeByName(VioTypeString);
//				VioDic vt = DBManager.getInstance().queryVioTypeByKeyCode(mTvVioType.getText().toString());
//				if (null != vt.getFineDefault() && !vt.getFineDefault().equals("")) {
//					mMoney.setText(vt.getFineDefault().toString());
//				}
//				if (null != vt.getPunish() && !vt.getPunish().equals("")) {
//					mValue.setText(vt.getPunish());
//				}
//				affairsPlateTypeDialog.setSelection(bundle.getInt("platetype"));
//				mTgBtnPlateSearch.setChecked(true);
//				@SuppressWarnings("unchecked")
//				List<String> photos = (List<String>) bundle.getSerializable("photoPaths");
//				photoPaths = photos;
//				// if (photoPaths.size() > 0) {
//				// mVpAddPicture.setVisibility(View.VISIBLE);
//				// }
//				// mViewPagerAdapter.notifyDataSetChanged();
//			}
//		}

		String pulishCode = DBManager.getInstance().getPunishId("punish");
		if (pulishCode != null && pulishCode.length() > 0) {
			mPulishCode.setText(pulishCode);
		} else {
			// 更新简易程序处罚编号数据


			DcNetWorkUtils.updatePunishId(this, SystemCfg.getUserId(this), "punish", DBManager.getInstance(), mHander);
			MsgToast.geToast().setMsg(R.string.getNumFailed);
		}

		String[] ActionTypes = {"警告", "罚款"};
//		String[] ActionTypes = DBManager.getInstance().queryTypeName("CFZL");
		affairsActionDialog = new PublishSelectAffairsAddressDialog(this, ActionTypes, mTvAction, "选择处理方式");

		String[] personTypes = DBManager.getInstance().queryTypeName("RYFL");
		affairsPlatePersonTypeDialog = new PublishSelectAffairsAddressDialog(this, personTypes, mPublishPersonType, "选择人员种类");
		String[] carTypes = DBManager.getInstance().queryTypeName("CLFL");
		affairsPlateCarTypeDialog = new PublishSelectAffairsAddressDialog(this, carTypes, mPublishCarType, "选择车辆种类");
		String[] trafficTypes = DBManager.getInstance().queryTypeName("JTFS");
		affairsPlateTrafficTypeDialog = new PublishSelectAffairsAddressDialog(this, trafficTypes, mPublishTrafficType, "选择交通方式");
		//String[] payTypes = DBManager.getInstance().queryTypeName("JKFS");
		String[] payTypes = {"当场收缴","银行缴款"};
		affairsPlatePayTypeDialog = new PublishSelectAffairsAddressDialog(this, payTypes, mPublishPayType, "选择交款方式");
		//String[] payStatus = DBManager.getInstance().queryTypeName("JKBJ");
		String[] payStatus = {"未交款","已交款"};
		affairsPlatePayStatuscDialog = new PublishSelectAffairsAddressDialog(this, payStatus, mPublishPayStatus, "选择交款状态");
	}

	private void saveDB() {

		if (isSaved)
			return;

		fpd = new FieldPunishData();
		fpd.owner = mOwner.getText().toString();
		fpd.vioid = vioid;
		fpd.picnum = photoPaths.size();
		fpd.id = mPulishCode.getText().toString();
		fpd.name = mDriverName.getText().toString();
		fpd.phone = mPhoneNum.getText().toString();
		fpd.driverlic = mDriverNum.getText().toString();
		fpd.recordid = mDriverRecordCode.getText().toString();
		fpd.licenseoffice = mLicenseOffice.getText().toString();
		fpd.licensetype = mLicenseType.getText().toString();
		fpd.plate = mPlateNum.getText().toString();
		ListDataItem listDataItem = (ListDataItem) mPublishPlateType.getTag();
		fpd.platetype = listDataItem.getid();
		fpd.plateoffice = mPlateOffice.getText().toString();
		fpd.location = mTvAddress.getText().toString().substring(6);
		if (mTvRoadDirect.getText().toString().length() > 0 )
			fpd.location += mTvRoadDirect.getText().toString();
		if (mTvRoadStep.getText().toString().length() > 0)
			fpd.location += mTvRoadStep.getText().toString();
		if (mTvRoadKm.getText().toString().length() > 0)
			fpd.location += mTvRoadKm.getText().toString() + "公里";
		if (mTvRoadMi.getText().toString().length() > 0)
			fpd.location += mTvRoadMi.getText().toString() + "米";
		if (mTvRoadZhan.getText().toString().length() > 0 )
			fpd.location += mTvRoadZhan.getText().toString();

		fpd.roaddirect = mTvRoadDirect.getText().toString();
		fpd.datetime = mPublishVioTime.getText().toString();
		fpd.content = VioTypeString;	//mTvVioType.getText().toString();
		fpd.action = mTvAction.getText().toString();
		fpd.money = Integer.parseInt(mMoney.getText().toString());
		fpd.value = Integer.parseInt(mValue.getText().toString());

		if (photoPaths != null && photoPaths.size() > 0) {
			fpd.pic1 = pic0;
			fpd.pic2 = pic1;
			fpd.pic3 = pic2;
			fpd.pic4 = pic3;
		}

		fpd.isupfile = 0;
		fpd.isprinted = 0;
		DBManager.getInstance().add(fpd);

		isSaved = true;
		DBManager.getInstance().markPunishId(mPulishCode.getText().toString(), "punish");
		XiangXunApplication.getInstance().getMainService().start(ConstantStatus.UPLOAD_ALLDATA);
		MsgToast.geToast().setMsg("上传成功！");
	}
	private boolean checkInputText() {
		if (yjxh != null && yjxh.length() > 1) {
		return  checkInputData();
	}

//		if (null != photoPaths) {
//			if (isSetPrint) {
//				if (photoPaths.size() <= 1) {
//					MsgToast.geToast().setMsg(R.string.noprintpicturemessage);
//					return false;
//				}
//			} else {
//				if (photoPaths.size() <= 0) {
//					MsgToast.geToast().setMsg(R.string.nopicturemessage);
//					return false;
//				}
//			}
//		} else {
//			MsgToast.geToast().setMsg(R.string.nopicture);
//			return false;
//		}

		return  checkInputData();
	}
	private boolean checkInputData() {
		if (mPulishCode.getText().length() < 1) {
			MsgToast.geToast().setMsg(R.string.numError);
			return false;
		}
		if (mPublishPersonType.getText().length() < 1) {
			MsgToast.geToast().setMsg("请选择人员分类");
			return false;
		}

		if (mDriverName.getText().length() < 1 || Tools.isSpecialCharacter(mDriverName.getText().toString())) {
			MsgToast.geToast().setMsg(R.string.checkVioName);
			return false;
		}

		if (!Tools.isPhoneNumber(mPhoneNum.getText().toString())) {
			MsgToast.geToast().setMsg("联系方式格式不正确！");
			return false;
		}

		if (mLicenseOffice.getText().length() < 1 || mLicenseOffice.getText().length() > 32 || Tools.isSpecialCharacter(mLicenseOffice.getText().toString())) {
			MsgToast.geToast().setMsg(R.string.checkDriveLicenorganization);
			return false;
		}

		if (mLicenseType.getText().length() < 1) {
			MsgToast.geToast().setMsg("准驾车型格式不正确，请确认！");
			return false;
		}

		ListDataItem listDataItem = (ListDataItem) mPublishPlateType.getTag();
		if (!Tools.isCarnumberNO(mPlateNum.getText().toString(), listDataItem.getCapture())) {
			Toast.makeText(PublishSummaryPunishActivity.this, "号牌号码格式不正确,请重新输入", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (!Tools.isIDCardValidate(mDriverNum.getText().toString())) {
			return false;
		}
		if (mDriverRecordCode.getText().length() < 1) {
			MsgToast.geToast().setMsg(R.string.checkRecordId);
			return false;
		}

		if (mLicenseType.getText().length() < 1) {
			MsgToast.geToast().setMsg(R.string.checkDriveType);
			return false;
		}

		if (mPlateNum.getText().length() < 1) {
			MsgToast.geToast().setMsg(R.string.checkPlateNum);
			return false;
		}

		if (mPublishCarType.getText().length() < 1) {
			MsgToast.geToast().setMsg("请选择车辆分类");
			return false;
		}

		if (mPublishPlateType.getText().toString() == null || mPublishPlateType.getText().toString().length() <= 0) {
			MsgToast.geToast().setMsg(R.string.checkPlateType);
			return false;
		}

		if (mPlateOffice.getText().length() < 1) {
			MsgToast.geToast().setMsg(R.string.checkPlateorganization);
			return false;
		}

		if (mPublishTrafficType.getText().length() < 1) {
			MsgToast.geToast().setMsg("请选择交通方式");
			return false;
		}

		if (mTvAddress.getText() != null && mTvAddress.getText().toString().length() < 1) {
			MsgToast.geToast().setMsg("请选择违法地点");
			return false;
		}

		if (mTvAddress.getRoadCode() == null) {
			MsgToast.geToast().setMsg("道路名称错误!");
			return false;
		}
		if (!Utils.CheckRoadExt(mTvAddress.getText().toString(), roadExt)) {
			MsgToast.geToast().setLongMsg("道路名称错误!");
			return false;
		}

		if (mTvVioType.getText() != null && mTvVioType.getText().toString().length() < 1) {
			MsgToast.geToast().setMsg(R.string.checkVioContent);
			return false;
		}

		if (VioTypeString == null || VioTypeString.equals("")) {
			MsgToast.geToast().setMsg("请输入正确的违法代码");
			return false;
		}

		VioDic vt = DBManager.getInstance().queryVioTypeByKeyCode(mTvVioType.getText().toString());
		int moneyNum = Integer.valueOf(vt.getFineDefault());
		if (moneyNum > 200) {
			MsgToast.geToast().setMsg("不合适的违法代码，处罚默认金额大于200");
			return false;
		}

		if (mTvAction.getText().toString().equals("罚款")) {

			if (mPublishPayType.getText().length() < 1) {
				MsgToast.geToast().setMsg("请选择交款方式");
				return false;
			}

			if (mPublishPayStatus.getText().length() < 1) {
				MsgToast.geToast().setMsg("请选择交款标记");
				return false;
			}

			if (mMoney.getText().length() < 1) {
				MsgToast.geToast().setMsg(R.string.checkVioMoney);
				return false;
			}

			if (mValue.getText().length() < 1) {
				MsgToast.geToast().setMsg(R.string.checkVioValue);
				return false;
			}
		}

		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 1:
			if (resultCode == Activity.RESULT_OK) {
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
				if (null == data.getExtras().getString("code") || data.getExtras().getString("code").equals(""))
					MsgToast.geToast().setMsg("未能识别出驾驶证信息，请重新识别！");
				else
					mDriverNum.setText(data.getExtras().getString("code"));
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
		case 7:// driving licence
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
			bindService(i, conn, Service.BIND_AUTO_CREATE);
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
		default:
			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		isSaved = false;
		super.onDestroy();
		if (isSetPrint) {
			unbindService(conn);
			mPrintService.onDestroy();
		}
		unregisterReceiver(receiver);
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

	private void saveWarnVioData() {
		// 保存简易处罚六合一数据格式
		SimpleVio simpleVio = new SimpleVio();
		simpleVio.jdsbh = mPulishCode.getText().toString();
		simpleVio.ryfl = DBManager.getInstance().getCodeByTypeAndNameFromDic("RYFL", mPublishPersonType.getText().toString());
		simpleVio.jszh = mDriverNum.getText().toString();
		simpleVio.dabh = mDriverRecordCode.getText().toString();
		simpleVio.fzjg = mLicenseOffice.getText().toString();
		simpleVio.zjcx = mLicenseType.getText().toString();
		simpleVio.dsr = mDriverName.getText().toString();
		simpleVio.clfl = DBManager.getInstance().getCodeByTypeAndNameFromDic("CLFL", mPublishCarType.getText().toString());
		simpleVio.hpzl = DBManager.getInstance().getCodeByTypeAndNameFromDic("PLATE_TYPE", mPublishPlateType.getText().toString());
		simpleVio.hphm = mPlateNum.getText().toString();
		simpleVio.jtfs = DBManager.getInstance().getCodeByTypeAndNameFromDic("JTFS", mPublishTrafficType.getText().toString());
		simpleVio.wfsj = mPublishVioTime.getText().toString();

		RoadInfo roadInfo = DBManager.getInstance().getRoadInfoByName(mTvAddress.getText().toString().substring(6));
		if (roadInfo.uploadcode != null) {
			simpleVio.wfdd = roadInfo.uploadcode.substring(0, 5);
		}

		if (roadExt != null && roadExt.xzqh != null && roadExt.xzqh.length() == 6) {
			simpleVio.xzqh = roadExt.xzqh;
		} else
			simpleVio.xzqh = SystemCfg.getDepartmentCode(this).substring(0,6);

		simpleVio.wfdz = mTvAddress.getText().toString().substring(6);
		if (mTvRoadKm.getText().toString().length() > 0
				&& (simpleVio.wfdd.startsWith("0") || simpleVio.wfdd.startsWith("1") || simpleVio.wfdd.startsWith("2")))	//高速\国道\省道
			simpleVio.lddm = mTvRoadKm.getText().toString();
		else
			simpleVio.lddm = "0000";
		if (!simpleVio.wfdz.contains("高速")) {
			String roadStepName = mTvRoadStep.getText().toString();
			if (roadExt != null && roadExt.ldlist != null && roadExt.ldlist.size() > 0 && roadStepName.length() > 0) {
				for (int i = 0, len = roadExt.ldlist.size(); i < len; i++) {
					ResultData.RoadStep roadStep = roadExt.ldlist.get(i);
					if (roadStepName.equals(roadStep.ldmc)) {
						simpleVio.lddm = roadExt.ldlist.get(i).lddm;
						break;
					}
				}
			}
		}

		if (mTvRoadMi.getText().toString().length() > 0)
			simpleVio.ddms = mTvRoadMi.getText().toString();
		else
			simpleVio.ddms = "000";

		if (mTvRoadKm.getText().toString().length() > 0)
			simpleVio.wfdz +=  mTvRoadKm.getText().toString() + "公里";
		if (mTvRoadMi.getText().toString().length() > 0)
			simpleVio.wfdz += mTvRoadMi.getText().toString() + "米";
		if (mTvRoadDirect.getText().toString().length() > 0 )
			simpleVio.wfdz += mTvRoadDirect.getText().toString();
		if (mTvRoadStep.getText().toString().length() > 0)
			simpleVio.wfdz += mTvRoadStep.getText().toString();
		if (mTvRoadZhan.getText().toString().length() > 0 )
			simpleVio.wfdz += mTvRoadZhan.getText().toString();

		simpleVio.wfxw = mTvVioType.getText().toString();
		simpleVio.fxjg = SystemCfg.getDepartmentCode(this);	//"610623005000";	//"610623000000";
		simpleVio.zqmj = SystemCfg.getUpdateCode(this); //"047986";	//"047987";	//SystemCfg.getPoliceCode(this);
		simpleVio.cfzl = DBManager.getInstance().getCodeByTypeAndNameFromDic("CFZL", mTvAction.getText().toString());
		if ("警告".equals(mTvAction.getText().toString())) {
			simpleVio.fkje = "0";
			simpleVio.jkfs =DBManager.getInstance().getCodeByTypeAndNameFromDic("JKFS", "不罚款");
			simpleVio.jkbj = DBManager.getInstance().getCodeByTypeAndNameFromDic("JKBJ", "无需交款");
		} else {
			simpleVio.fkje = mMoney.getText().toString();
			simpleVio.jkfs = DBManager.getInstance().getCodeByTypeAndNameFromDic("JKFS", mPublishPayType.getText().toString());
			simpleVio.jkbj = DBManager.getInstance().getCodeByTypeAndNameFromDic("JKBJ", mPublishPayStatus.getText().toString());
		}
		simpleVio.jsjqbj = "00";			//没说明
		simpleVio.sgdj = "0";

		if (persionInfo != null){
			simpleVio.zsxzqh = persionInfo.lxzsxzqh;
			simpleVio.zsxxdz = persionInfo.lxzsxxdz;
			simpleVio.lxfs = persionInfo.lxdh;
			simpleVio.dh = persionInfo.sjhm;
		}

		if (vehicleInfo != null) {
			simpleVio.jdcsyr = vehicleInfo.syr;
			simpleVio.syxz = vehicleInfo.syxz;

		}

		if (yjxh != null && yjxh.length() > 1) {
			DBManager.getInstance().WarnMessageUpdate(yjxh, "isupfile", "0");
			DBManager.getInstance().WarnMessageUpdate(yjxh, "step", "2");
			DBManager.getInstance().WarnMessageUpdate(yjxh, "vio", XiangXunApplication.getGson().toJson(simpleVio));
		} else {
			UpdateVio updateVio = new UpdateVio();
			updateVio.isupfile = 0;
			updateVio.type = "simple";
			updateVio.sn = simpleVio.jdsbh;
			updateVio.datetime = simpleVio.wfsj;
			updateVio.vio = XiangXunApplication.getGson().toJson(simpleVio);

			DBManager.getInstance().add(updateVio);
		}
		DBManager.getInstance().markPunishId(simpleVio.jdsbh, "punish");
	}

	private void saveData() {
		// 单独进行违法处理之后的违法数据


		String roadid, viocode;
		roadid = DBManager.getInstance().getRoadIdByName(mTvAddress.getText().toString().substring(6));
		viocode = mTvVioType.getText().toString();

		int imgCount = 0;
		if (photoPaths != null) {
			TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			vioid = tm.getDeviceId() + System.currentTimeMillis();
			imgCount = photoPaths.size();
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

		ImageUtils iu = new ImageUtils(PublishSummaryPunishActivity.this);

		File f = new File(pic0);
		if (f != null && f.exists() && f.isFile()) {
			iu.pressText(pic0, "", getResources().getString(R.string.textviewaddress) + ":" + mTvAddress.getText().toString(), getResources().getString(R.string.fangweima) + ":" + Md5.MD5("" + "" + mTvAddress.getText().toString() + mPublishVioTime.getText().toString() + "0" + "" + "" + "").substring(0, 9));
		}
		f = new File(pic1);
		if (f != null && f.exists() && f.isFile()) {
			iu.pressText(pic1, "", getResources().getString(R.string.textviewaddress) + ":" + mTvAddress.getText().toString(), getResources().getString(R.string.fangweima) + ":" + Md5.MD5("" + "" + mTvAddress.getText().toString().substring(6) + mPublishVioTime.getText().toString() + "1" + "" + "" + "").substring(0, 9));
		}
		f = new File(pic2);
		if (f != null && f.exists() && f.isFile()) {
			iu.pressText(pic2, "", getResources().getString(R.string.textviewaddress) + ":" + mTvAddress.getText().toString(), getResources().getString(R.string.fangweima) + ":" + Md5.MD5("" + "" + mTvAddress.getText().toString().substring(6) + mPublishVioTime.getText().toString() + "2" + "" + "" + "").substring(0, 9));
		}
		f = new File(pic3);
		if (f != null && f.exists() && f.isFile()) {
			iu.pressText(pic3, "", getResources().getString(R.string.textviewaddress) + ":" + mTvAddress.getText().toString(), getResources().getString(R.string.fangweima) + ":" + Md5.MD5("" + "" + mTvAddress.getText().toString().substring(6) + mPublishVioTime.getText().toString() + "3" + "" + "" + "").substring(0, 9));
		}
		ListDataItem listDataItem = (ListDataItem) mPublishPlateType.getTag();


		String roadcontext = "";
		String roaddirect = "";
		roaddirect = mTvRoadDirect.getText().toString();
		if (mTvRoadDirect.getText().toString().length() > 0 )
			roadcontext += mTvRoadDirect.getText().toString();
		if (mTvRoadStep.getText().toString().length() > 0)
			roadcontext += mTvRoadStep.getText().toString();
		if (mTvRoadKm.getText().toString().length() > 0)
			roadcontext += mTvRoadKm.getText().toString() + "公里";
		if (mTvRoadMi.getText().toString().length() > 0)
			roadcontext += mTvRoadMi.getText().toString() + "米";
		if (mTvRoadZhan.getText().toString().length() > 0 )
			roadcontext += mTvRoadZhan.getText().toString();
//		final VioData vd = new VioData("", mTvVioType.getText().toString(), mPublishVioTime.getText().toString(), mTvAddress.getText().toString(), mTvRoadZh.getText().toString(), DBManager.getInstance().getRoadDirectionCodeByName(mTvRoadDirect.getText().toString()), roadid, mPlateNum.getText().toString(), listDataItem.getid(), viocode, SystemCfg.getPoliceName(XiangXunApplication.getInstance()), 0, vioid, 1, imgCount, pic0, pic1, pic2, pic3, 1, 0, 1);
		final VioData vd = new VioData("", VioTypeString, mPublishVioTime.getText().toString(), mTvAddress.getText().toString().substring(6), roadcontext, roaddirect, roadid, mPlateNum.getText().toString(), listDataItem.getid(), viocode, SystemCfg.getPoliceName(XiangXunApplication.getInstance()), 0, vioid, 1, imgCount, pic0, pic1, pic2, pic3, 1, 0, 1);

		DBManager.getInstance().add(vd);
		// 保存处理数据
		saveDB();
	}

	private boolean print() {
		if (mPrintService != null) {
			mPrintService.print(mPrintService.formatContent(generateContent(), 10), PrintInfo.TITLE_TYPE_PUNISHMENT);
			isPrint = true;
			return true;
		} else {
			return false;
		}
	}

	@SuppressLint("DefaultLocale")
	private String generateContent() {
		StringBuffer content = new StringBuffer();
		Resources resources = PublishSummaryPunishActivity.this.getResources();
		content.append(resources.getString(R.string.bianhao) + mPulishCode.getText().toString().toUpperCase() + "%end%");
		content.append(resources.getString(R.string.beichufaren) + mDriverName.getText().toString().toUpperCase() + "%end%");
		content.append(resources.getString(R.string.phone) + "：" + mPhoneNum.getText().toString().toUpperCase() + "%end%");
		content.append(resources.getString(R.string.driverId) + "%end%");
		content.append(mDriverNum.getText().toString() + "%end%");
		content.append(resources.getString(R.string.recordId) + mDriverRecordCode.getText().toString().toUpperCase() + "%end%");
		content.append(resources.getString(R.string.organization) + mLicenseOffice.getText().toString().toUpperCase() + "  ");
		content.append(resources.getString(R.string.driveType) + mLicenseType.getText().toString().toUpperCase() + "%end%");
		content.append(resources.getString(R.string.plateNum) + mPlateNum.getText().toString().toUpperCase() + "%end%");
		content.append(resources.getString(R.string.plateType) + mPublishPlateType.getText().toString() + "%end%");
//		content.append(resources.getString(R.string.organization) + mPlateOffice.getText().toString().toUpperCase() + "%end%");
		content.append(resources.getString(R.string.dangshiren));
		content.append(resources.getString(R.string.yu) + Utils.ChangeTimeTochs(mPublishVioTime.getText().toString().toUpperCase()));
		content.append(resources.getString(R.string.zai)+ mTvAddress.getText().toString().toUpperCase().substring(6));
		content.append(mTvRoadDirect.getText().toString());
		if (mTvRoadStep.getText().toString().length() > 0 )
			content.append(mTvRoadStep.getText().toString());
		if (mTvRoadKm.getText().toString().length() > 0)
			content.append(mTvRoadKm.getText().toString() + "公里");
		if (mTvRoadMi.getText().toString().length() > 0)
			content.append(mTvRoadMi.getText().toString() + "米");
		if (mTvRoadZhan.getText().toString().length() > 0 )
			content.append(mTvRoadZhan.getText().toString());
//		content.append(resources.getString(R.string.shishi) + mTvVioType.getText().toString().toUpperCase());
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
		content.append(resources.getString(R.string.wfnr) + getResources().getString(R.string.code) + vt.getCode());
		content.append(String.format(resources.getString(R.string.weifaguiding), vt.getLaw(), vt.getRule()));

		if (mTvAction.getText().toString().equals("罚款")) {
			content.append(mMoney.getText().toString().toUpperCase() + "元" + mTvAction.getText().toString() + ",");	//200罚款
			content.append(resources.getString(R.string.jifen) + mValue.getText().toString().toUpperCase() + "分。");
			content.append(resources.getString(R.string.chufa_tip) + SystemCfg.getPunishAddress(mContext) + resources.getString(R.string.chufa_tipx));
		} else {
			content.append(mTvAction.getText().toString());	//警告
		}
		content.append("%start%" + String.format(resources.getString(R.string.chufa_desc), SystemCfg.getAgainDiscussDepartment(mContext), SystemCfg.getCourtName(mContext)));
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
		if (mPopupWindow != null) {
			if (mPopupWindow.isShowing()) {
				mPopupWindow.dismiss();
				mVSupernatant.setVisibility(View.GONE);
			} else {
				super.onBackPressed();
			}
		} else if (yjxh != null && yjxh.length() > 1){
			if (isPrint == true) {
				Intent intent = new Intent();
				intent.putExtra("WarnTableNo", mPulishCode.getText().toString());
				setResult(ConstantStatus.RESULTCODE_WARNTABLE, intent);
			}
			finish();
		} else {
			super.onBackPressed();
		}
	}

	private class Receiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			CarInfoList carinfolist = null;

			carinfolist = (CarInfoList) intent.getExtras().getSerializable("mcarlist");

			Message msg = new Message();
			if (carinfolist == null) {
				msg.what = ConstantStatus.PlateQueryFalse;
			} else {
				msg.what = ConstantStatus.CarInfoSuccess;
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
		if (keybord != null && keybord.isShow())
			keybord.hideKeyboard();

		switch (arg0.getId()) {
		case R.id.iv_publish_add_picture:
			// 必要时隐藏输入法键盘
			// InputMethodManager imm = (InputMethodManager)
			// getSystemService(Context.INPUT_METHOD_SERVICE);
			// imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
			// InputMethodManager.HIDE_NOT_ALWAYS);
			Utils.hideSoftInputFromWindow(PublishSummaryPunishActivity.this);
			if (mPopupWindow == null || !mPopupWindow.isShowing()) {
				showSelectAddPath(); // 选择添加图片方式
				mVSupernatant.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.tv_add_picture_from_camera:
			Intent intentCamera = new Intent(PublishSummaryPunishActivity.this, CameraActivity.class);
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
		case R.id.ll_publish_click_road_direct:
			affairsRoadDirectDialog.show();
			break;
		case R.id.ll_publish_click_road_step:
			if (affairsRoadStepDialog != null)
				affairsRoadStepDialog.show();
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
				DcNetWorkUtils.searchDriver(this, mDriverNum.getText().toString(), mHander);
			}
			break;
		case R.id.ll_driver_plate_num:
			if (!mTgBtnPlateSearch.isChecked()) {
				Intent intentPlate = wtUtils.StartPlateRecog(PublishSummaryPunishActivity.this);
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
				DcNetWorkUtils.searchVehicleInfo(this, mPlateNum.getText().toString(), platetype.getid(), mHander);
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
			bindService(in, conn, Service.BIND_AUTO_CREATE);
			break;
		case R.id.ll_publish_click_persontype:
			affairsPlatePersonTypeDialog.show();
			break;
		case R.id.ll_publish_click_cartype:
			affairsPlateCarTypeDialog.show();
			break;
		case R.id.ll_publish_click_traffictype:
			affairsPlateTrafficTypeDialog.show();
			break;
		case R.id.ll_publish_click_paytype:
			affairsPlatePayTypeDialog.show();
			break;
		case R.id.ll_publish_click_paystatus:
			affairsPlatePayStatuscDialog.show();
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
	public void vioTypeOnClick(String vioType) {
		if (vioType.equals("警告")) {
			mLlmoney.setVisibility(View.GONE);
			mLlValue.setVisibility(View.GONE);
			mLxPublishClickPayType.setVisibility(View.GONE);
			mLxPublishClickPayStatus.setVisibility(View.GONE);
		} else if (vioType.equals("罚款")) {
			mLlmoney.setVisibility(View.VISIBLE);
			mLlValue.setVisibility(View.VISIBLE);
			mLxPublishClickPayType.setVisibility(View.VISIBLE);
			mLxPublishClickPayStatus.setVisibility(View.GONE);
		} else if (vioType.equals("当场收缴")){
			mPublishPayStatus.setText("已交款");
		} else if (vioType.equals("银行缴款")){
			mPublishPayStatus.setText("未交款");
		}
	}

	private void printData() {
		if (mService != null && mPrintService != null && !mPrintService.isPrintConnected()) {
			printerDialog = Utils.showNoPrinter(PublishSummaryPunishActivity.this);
			printerDialog.setButLeftListener(PublishSummaryPunishActivity.this);
			printerDialog.setButRightListener(PublishSummaryPunishActivity.this);
			return;
		} else {
//			if (!isPrint)
			{
				if (mPrintService.isPrintConnected()) {
					connectPrintDevice();
				} else {
					MsgToast.geToast().setMsg(R.string.iniBlueToothPrinter);
				}
			}
//			else {
//				MsgToast.geToast().setMsg("编辑模式不支持重复打印！");
//				return;
//			}
		}
	}

	@Override
	public void vioCode(Type VioType) {
		VioTypeString = VioType.type;	//vioname
		VioDic vt = DBManager.getInstance().queryVioTypeByKeyCode(VioType.code);

		if (null != vt.getFineDefault() && !vt.getFineDefault().equals("")) {
			mMoney.setText(vt.getFineDefault().toString());
		}
		if (null != vt.getPunish() && !vt.getPunish().equals("")) {
			mValue.setText(vt.getPunish());
		}
	}

	private void showRoadStep() {
		HideRoadStep();

		RoadInfo roadInfo = DBManager.getInstance().getRoadInfoByName(mTvAddress.getText().toString().substring(6));
		if (roadInfo.note != null)	{
			if (roadInfo.note.contains("ldmc"))
				roadExt = XiangXunApplication.getGson().fromJson(roadInfo.note, ResultData.RoadExt.class);
			else {
				roadExt = new RoadExt();
				roadExt.xzqh = roadInfo.note;
				roadExt.ldlist = null;
			}
			if (roadInfo.getName().contains("高速")) {
				lx_publish_click_RoadDirect.setVisibility(View.VISIBLE);
				lx_publish_click_RoadZhan.setVisibility(View.VISIBLE);

				lx_publish_click_RoadStep.setVisibility(View.GONE);
			} else {
				lx_publish_click_RoadDirect.setVisibility(View.GONE);
				lx_publish_click_RoadZhan.setVisibility(View.GONE);

				if (roadExt.ldlist != null) {
					int len = roadExt.ldlist.size();
					if (len > 0) {
						String[] steps = new String[len];

						for (int i = 0; i < len; i++) {
							steps[i] = roadExt.ldlist.get(i).ldmc;
						}

						if (affairsRoadStepDialog != null)
							affairsRoadStepDialog = null;
						affairsRoadStepDialog = new PublishSelectAffairsAddressDialog(this, steps, mTvRoadStep, getResources().getString(R.string.roadstep_input));

						lx_publish_click_RoadStep.setVisibility(View.VISIBLE);
					} else {
						lx_publish_click_RoadStep.setVisibility(View.GONE);
					}
				}
			}
		}
	}

	private void HideRoadStep(){
		if (affairsRoadStepDialog != null)
			affairsRoadStepDialog = null;

		lx_publish_click_RoadDirect.setVisibility(View.GONE);
		lx_publish_click_RoadZhan.setVisibility(View.GONE);
		lx_publish_click_RoadStep.setVisibility(View.GONE);

		mTvRoadDirect.setText("");
		mTvRoadStep.setText("");
		mTvRoadKm.setText("");
		mTvRoadMi.setText("");
		mTvRoadZhan.setText("");

		roadExt = null;
	}
}
