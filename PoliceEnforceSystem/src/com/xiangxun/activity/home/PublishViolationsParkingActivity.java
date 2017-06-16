package com.xiangxun.activity.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
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
import android.widget.ToggleButton;

import com.xiangxun.activity.CameraActivity;
import com.xiangxun.activity.PhotoSelectActivity;
import com.xiangxun.activity.R;
import com.xiangxun.activity.setting.PrintSettingActivity;
import com.xiangxun.adapter.PublishPictureAdapter;
import com.xiangxun.app.BaseActivity;
import com.xiangxun.app.XiangXunApplication;
import com.xiangxun.bean.ListDataItem;
import com.xiangxun.bean.ListDataObj;
import com.xiangxun.bean.MDate;
import com.xiangxun.bean.ParkVio;
import com.xiangxun.bean.PersionInfoX;
import com.xiangxun.bean.PrintInfo;
import com.xiangxun.bean.ResultData;
import com.xiangxun.bean.RoadInfo;
import com.xiangxun.bean.Type;
import com.xiangxun.bean.UpdateVio;
import com.xiangxun.bean.VehicleInfoX;
import com.xiangxun.bean.VioData;
import com.xiangxun.bean.VioDic;
import com.xiangxun.cfg.SystemCfg;
import com.xiangxun.db.DBManager;
import com.xiangxun.request.DcNetWorkUtils;
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
import com.xiangxun.widget.PublishSelectAffairsPlateTypeDialog;
import com.xiangxun.widget.PublishSelectCarColorTypeDialog;
import com.xiangxun.widget.PublishSelectPlateColorTypeDialog;
import com.xiangxun.widget.RoadEditView;
import com.xiangxun.widget.TitleView;
import com.xiangxun.widget.VioEditView;
import com.xiangxun.wtone.wtUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.xiangxun.util.Utils.GetCarStatus;

/**
 * @package: com.xiangxun.activity
 * @ClassName: PublishViolationsParkingActivity.java
 * @Description: 发布违停抓拍页面
 */
public class PublishViolationsParkingActivity extends BaseActivity implements OnClickListener, DeletePictureInterface,VioEditView.onItemvioNameClick {
	private PublishViolationsParkingActivity mContext = null;
	private TitleView titleView;
	private KeyboardUtil keybord;
	private Button yuancheng;
	private List<String> photoPaths;
	private TextView edttime;
	private boolean isLive = false;
	private EditText mEditPlate;
	private PublishPictureAdapter mViewPagerAdapter = null;
	// 添加图片
	private ImageView mIvAddPicture = null;
	private NoViewPager mVpAddPicture = null;
	private EditText mPulishCode;

	private TextView mPublishTrafficType;
	private LinearLayout mPublishClickTrafficType;
	private PublishSelectAffairsAddressDialog affairsPlateTrafficTypeDialog;

	private TextView mTvCarColorType;
	private TextView mTvPlateColorType;

	private VioEditView mTvVioType;
	private String VioTypeString = null;

	private PublishSelectCarColorTypeDialog carColorTypeDialog;
	private PublishSelectPlateColorTypeDialog plateColorTypeDialog;

	private RoadEditView mTvAddress;
	private TextView mTvRoadDirect;	//上行、下行
	private EditText mTvRoadZhan;	//收费站、服务站
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

	private TextView mTvPlateType;
	private LinearLayout ll_publish_click_platetype;
	private PublishSelectAffairsPlateTypeDialog affairsPlateTypeDialog;
	private LinearLayout mLlPlateSearch;
	private ToggleButton mTgBtnPlateSearch;
	private ListDataObj ldo = null;
	private ArrayList<Type> carColors = new ArrayList<Type>();
	private ArrayList<Type> plateColors = new ArrayList<Type>();
	private StdPrintService mPrintService;
	private boolean isPrint = false;

	private ProgressDialog mDialog;
	private MsgDialog VehicleDialog;
	// 浮层
	private View mVSupernatant = null;
	private PopupWindow mPopupWindow = null;
	private MsgDialog printerDialog;
	private boolean isSetPrint;

	private ResultData.RoadExt roadExt;
	private VehicleInfoX vehicleInfo;
	private PersionInfoX persionInfo;

	private Handler mHander = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			mDialog.dismiss();
			switch (msg.what) {
			case ConstantStatus.SUCCESS:
				String pulishCode = DBManager.getInstance().getPunishId("park");
				if (pulishCode != null && pulishCode.length() > 0) {
					mPulishCode.setText(pulishCode);
				} else {
					//MsgToast.geToast().setMsg(R.string.getNumFailed);
				}
				break;
			case ConstantStatus.OTHER_PLATENUM:
				Utils.showPlateInfo(PublishViolationsParkingActivity.this, msg);
				break;
			case ConstantStatus.DRIVER_SEARCH_SUCCESS:
				persionInfo = (PersionInfoX) msg.obj;
				break;
			case ConstantStatus.VEHICLE_SEARCH_SUCCESS:
				vehicleInfo = (VehicleInfoX) msg.obj;
				if (vehicleInfo != null && vehicleInfo.hphm != null) {
					VehicleDialog = new MsgDialog(PublishViolationsParkingActivity.this);
					VehicleDialog.setTiele("机动车信息");
					String info = String.format(getString(R.string.wran_info),
							getIsNull(vehicleInfo.syr),
							getIsNull(vehicleInfo.clpp1),
							getIsNull(vehicleInfo.lxdz),
							getIsNull(DBManager.getInstance().getNameByTypeAndCodeFromDic("CAR_COLOR", vehicleInfo.csys)),
							getIsNull(vehicleInfo.hphm),
							getIsNull(vehicleInfo.fdjxh),
							getIsNull(vehicleInfo.hphm.substring(0,2)),
							getIsNull(DBManager.getInstance().getNameByTypeAndCodeFromDic("PLATE_TYPE", vehicleInfo.hpzl)),
							getIsNull(DBManager.getInstance().getNameByTypeAndCodeFromDic("JTFS", vehicleInfo.cllx)),
							getIsNull(DBManager.getInstance().getNameByTypeAndCodeFromDic("CAR_RUN_TYPE", vehicleInfo.syxz)),
							getIsNull(vehicleInfo.clsbdh),
							getIsNull(GetCarStatus(vehicleInfo.zt)),
							getIsNull(vehicleInfo.yxqz),
							getIsNull(vehicleInfo.bxzzrq),
							getIsNull(vehicleInfo.hdzk),
							getIsNull(vehicleInfo.hdzzl)
					);

					VehicleDialog.setMsg2(info);
					VehicleDialog.setOnlyOneBut();
					VehicleDialog.show();
					VehicleDialog.setButLeftListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							VehicleDialog.dismiss();
						}
					});
					VehicleDialog.setButRightListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							VehicleDialog.dismiss();
						}
					});

					carColorTypeDialog.setSelectCode(vehicleInfo.csys);
					mPublishTrafficType.setText(DBManager.getInstance().getNameByTypeAndCodeFromDic("JTFS", vehicleInfo.cllx));

					if (vehicleInfo.sfzmhm != null && vehicleInfo.sfzmhm.length() > 1)
						DcNetWorkUtils.searchDriver(PublishViolationsParkingActivity.this, vehicleInfo.sfzmhm, mHander);
				} else {
					MsgToast.geToast().setMsg("无此机动车信息！");
				}
				break;

			case ConstantStatus.VEHICLE_SEARCH_FALSE:
				MsgToast.geToast().setMsg("机动车数据查询失败, 稍后请重试!");
				break;
			case ConstantStatus.SHOW_KEYBOARD:
				keybord.showKeyboard();
				break;
			}
		}
	};

	private String getIsNull(String content) {
		return TextUtils.isEmpty(content) ? "暂无" : content;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.publish_violation_parking_layout);
		mContext = PublishViolationsParkingActivity.this;
		isSetPrint = false; //SystemCfg.getIsPrint(this);
		initView();
		initData();
		initListener();

		String RoadHistory = SystemCfg.getRoadHistory(this);
		if (RoadHistory != null)
			mTvAddress.setText(RoadHistory);

		String VioHistory = SystemCfg.getVioHistory(this);
		if (VioHistory != null && !VioHistory.equals("")){
			int index = VioHistory.indexOf("-");
			mTvVioType.setText(VioHistory.substring(0,index));
			VioTypeString = VioHistory.substring(index + 1);
		}

//		mPulishCode.setText("610202190000051");
//		mEditPlate.setText("陕B31999");
	}

	@Override
	protected void onRestart() {
		if (isLive && photoPaths.size() > 0 && !mEditPlate.getText().toString().equals("") && !mTvPlateType.getText().toString().equals("")) {
			PublishViolationsParkingActivity.this.finish();
		}
		super.onRestart();
	}

	@Override
	protected void onPause() {
		isLive = false;
		super.onPause();
	}

	@Override
	public void initView() {
		yuancheng = (Button) findViewById(R.id.yuancheng);
		titleView = (TitleView) findViewById(R.id.tv_comm_title);
		titleView.setLeftBackOneListener(R.drawable.back_normal, this);
		if (isSetPrint) {
			titleView.setRightViewLeftOneListener(R.drawable.icon_print, this);
		}
		titleView.setTitle("违停抓拍");

		mIvAddPicture = (ImageView) findViewById(R.id.iv_publish_add_picture);
		mVpAddPicture = (NoViewPager) findViewById(R.id.vp_publish_add_pictures);
		mPulishCode = (EditText) findViewById(R.id.publish_code);
		mTvCarColorType = (TextView) findViewById(R.id.tv_publish_carcolor);
		mTvPlateColorType = (TextView) findViewById(R.id.tv_publish_platecolor);
		mTvVioType = (VioEditView) findViewById(R.id.tv_publish_viotype);
		mTvAddress = (RoadEditView) findViewById(R.id.tv_publish_address);

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

		mTvPlateType = (TextView) findViewById(R.id.tv_publish_platetype);
		ll_publish_click_platetype = (LinearLayout) findViewById(R.id.ll_publish_click_platetype);
		mLlPlateSearch = (LinearLayout) findViewById(R.id.ll_driver_plate_num);
		mTgBtnPlateSearch = (ToggleButton)  findViewById(R.id.tgbtn_search_plate);

		mPublishTrafficType = (TextView) findViewById(R.id.tv_publish_traffictype);
		mPublishClickTrafficType = (LinearLayout) findViewById(R.id.ll_publish_click_traffictype);

		String[] directs = DBManager.getInstance().getRoadDirection();
		affairsRoadDirectDialog = new PublishSelectAffairsAddressDialog(this, directs, mTvRoadDirect, getResources().getString(R.string.roaddirect_input));
		// 获取本地数据库中的违法数据词典和道路数据		String[] viotypes = DBManager.getInstance().queryVioType();
		carColors = DBManager.getInstance().getCarColorTypes();
		plateColors = DBManager.getInstance().getPlateColorTypes();
		carColorTypeDialog = new PublishSelectCarColorTypeDialog(this, carColors, mTvCarColorType, "选择车身颜色");
		plateColorTypeDialog = new PublishSelectPlateColorTypeDialog(this, plateColors, mTvPlateColorType, "选择号牌颜色");
		plateColorTypeDialog.setSelectName(getResources().getString(R.string.def_platecolor));
		ldo = new ListDataObj();
		ArrayList<Type> platetypes = DBManager.getInstance().getPlateTypes();

		for (int i = 0; i < platetypes.size(); i++) {
			ldo.add(platetypes.get(i).code, platetypes.get(i).name);
		}

		affairsPlateTypeDialog = new PublishSelectAffairsPlateTypeDialog(this, ldo.getlist(), mTvPlateType);
		affairsPlateTypeDialog.setSelectCapture(getResources().getString(R.string.def_platetype));
		edttime = (TextView) findViewById(R.id.accedtdate);
		edttime.setText(MDate.getDate());
		mEditPlate = (EditText) findViewById(R.id.plate);

		mVSupernatant = (View) findViewById(R.id.v_supernatant_background);
		keybord = new KeyboardUtil(PublishViolationsParkingActivity.this, mEditPlate);
	}

	@Override
	public void initListener() {
		mPublishClickTrafficType.setOnClickListener(this);
		ll_publish_click_RoadDirect.setOnClickListener(this);

		ll_publish_click_RoadStep.setOnClickListener(this);
		ll_publish_click_platetype.setOnClickListener(this);
		mTvCarColorType.setOnClickListener(this);
		mTvPlateColorType.setOnClickListener(this);
		yuancheng.setOnClickListener(this);
		edttime.setOnClickListener(this);
		mTvVioType.setvioNameClick(this);
		mIvAddPicture.setOnClickListener(this);
		mVpAddPicture.setOnClickListener(this);
		mLlPlateSearch.setOnClickListener(this);
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
						SystemCfg.setRoadHistory(PublishViolationsParkingActivity.this, roadname);
						showRoadStep();
					} else
						HideRoadStep();
				} else
					HideRoadStep();

			}
		});
		mEditPlate.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Utils.hideSoftInputFromWindow(PublishViolationsParkingActivity.this);
				if(event.getAction()==MotionEvent.ACTION_UP){
					if (mEditPlate.getText().toString().length() == 0)
						keybord.changeKeyboard(false);
					else
						keybord.changeKeyboard(true);
					mHander.removeMessages(ConstantStatus.SHOW_KEYBOARD);
					mHander.sendEmptyMessageDelayed(ConstantStatus.SHOW_KEYBOARD, 200);
				}
				return false;
			}
		});

		mEditPlate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus == false) {
					mHander.removeMessages(ConstantStatus.SHOW_KEYBOARD);
					keybord.hideKeyboard();
					keybord.hideSoftInputMethod();
				}
				else {
					keybord.hideSoftInputMethod();
				}
			}
		});

		mEditPlate.addTextChangedListener(new TextWatcher() {
			int index = 0;

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String searchWord = s.toString().toUpperCase().trim();
				boolean isClear = s.toString().trim().length() == 0;
				mTgBtnPlateSearch.setChecked(!isClear);
				mEditPlate.removeTextChangedListener(this);// 解除文字改变事件
				index = mEditPlate.getSelectionStart();// 获取光标位置
				mEditPlate.setText(s.toString().toUpperCase());
				mEditPlate.setSelection(index);// 重新设置光标位置
				mEditPlate.addTextChangedListener(this);// 重新绑定事件
				Utils.chkOtherPlate(mContext, mHander, mEditPlate, searchWord);
			}
		});

	}

	@Override
	public void initData() {
		if (isSetPrint) {
			Intent in = new Intent(this, StdPrintService.class);
			bindService(in, conn, Service.BIND_AUTO_CREATE);
		}
		mDialog = new ProgressDialog(this);
		photoPaths = new ArrayList<String>();
//		String publishCode = DBManager.getInstance().getPunishId("park");
//		if (publishCode != null && publishCode.length() > 0) {
//			mPulishCode.setText(publishCode);
//		} else {
//			// 更新违停告知书编号数据
//
//			DcNetWorkUtils.updatePunishId(this, SystemCfg.getUserId(this), "park", DBManager.getInstance(), mHander);
////			MsgToast.geToast().setMsg(R.string.getNumFailed);
//		}
		// 显示图片位
		mViewPagerAdapter = new PublishPictureAdapter(this, mVpAddPicture, photoPaths);
		mVpAddPicture.setAdapter(mViewPagerAdapter);
		if (photoPaths.size() > 0) {
			mVpAddPicture.setVisibility(View.VISIBLE);
		}

		String[] trafficTypes = DBManager.getInstance().queryTypeName("JTFS");
		affairsPlateTrafficTypeDialog = new PublishSelectAffairsAddressDialog(this, trafficTypes, mPublishTrafficType, "选择交通方式");
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

	@Override
	public void onBackPressed() {
		if(mPopupWindow != null){
			if(mPopupWindow.isShowing()){
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
		

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (isSetPrint) {
			unbindService(conn);
			mPrintService.onDestroy();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 10:
			Intent i = new Intent(this, StdPrintService.class);
			bindService(i, conn, Service.BIND_AUTO_CREATE);
			break;
		case 0:
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
					edttime.setText(picTimeFormat);
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
					edttime.setText(picTimeFormat);
				}

			}
			break;
		case 1:
			if (resultCode == Activity.RESULT_OK) {
				if (null != data) {
					if (data.getExtras().getString("platetype").equals("27")) {
						MsgToast.geToast().setMsg(R.string.plateNotifyError);
					} else if (data.getExtras().getString("platenum") != null) {
						MsgToast.geToast().setMsg(R.string.plateNotifyCompleted);
						mEditPlate.setText(data.getExtras().getString("platenum"));
						affairsPlateTypeDialog.setSelectId(data.getExtras().getString("platetype"));
					} else if (data.getExtras().getString("platenum") == null) {
						MsgToast.geToast().setMsg(R.string.plateNotifyError);
					}
				} else {
					MsgToast.geToast().setMsg(R.string.plateNotifyError);
				}
			}
			break;
		case 3:// plate
			if (resultCode == Activity.RESULT_OK) {
				if (null != data) {
					if (data.getExtras().getString("platenum") != null) {
						MsgToast.geToast().setMsg(R.string.plateNotifyCompleted);
						mEditPlate.setText(data.getExtras().getString("platenum"));
					} else if (data.getExtras().getString("platenum") == null) {
						MsgToast.geToast().setMsg(R.string.plateNotifyError);
					}
				} else {
					MsgToast.geToast().setMsg(R.string.plateNotifyError);
				}
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
	}

	private void saveDb(int type) {
		int picNum = photoPaths.size();

		String date = edttime.getText().toString();
		String roadname = mTvAddress.getText().toString().substring(6);
		String roadid = DBManager.getInstance().getRoadIdByName(roadname);

		String roadlocation = "";
		String roaddirect = "";
		if (mTvRoadDirect.getText().toString().length() > 0 )
			roadlocation += mTvRoadDirect.getText().toString();
		if (mTvRoadStep.getText().toString().length() > 0)
			roadlocation += mTvRoadStep.getText().toString();
		if (mTvRoadKm.getText().toString().length() > 0)
			roadlocation += mTvRoadKm.getText().toString() + "公里";
		if (mTvRoadMi.getText().toString().length() > 0)
			roadlocation += mTvRoadMi.getText().toString() + "米";
		if (mTvRoadZhan.getText().toString().length() > 0 )
			roadlocation += mTvRoadZhan.getText().toString();
		roaddirect = mTvRoadDirect.getText().toString();
		String roadLddm = "";

		if (mTvRoadKm.getText().toString().length() > 0)
			roadLddm = mTvRoadKm.getText().toString();
		else
			roadLddm = "0000";

		if (!roadname.contains("高速")) {
			String roadStepName = mTvRoadStep.getText().toString();
			if (roadExt != null && roadExt.ldlist != null && roadExt.ldlist.size() > 0 && roadStepName.length() > 0) {
				int index = 0;
				for (int i = 0, len = roadExt.ldlist.size(); i < len; i++) {
					ResultData.RoadStep roadStep = roadExt.ldlist.get(i);
					if (roadStepName.equals(roadStep.ldmc)) {
						index = i;
						break;
					}
				}
				roadLddm = roadExt.ldlist.get(index).lddm;
			}
		}

		roaddirect = roadLddm + "-" + roaddirect;

		String plateNum = mEditPlate.getText().toString();
		if (plateNum != null && plateNum.contains(",")) {
			plateNum.replace(",", "，");
		}
		ListDataItem tag = (ListDataItem) mTvPlateType.getTag();
		String platetype = tag.getid() + "-" + DBManager.getInstance().getCodeByTypeAndNameFromDic("JTFS", mPublishTrafficType.getText().toString());
		String viocode = mTvVioType.getText().toString();	//SystemCfg.getVioParkCode(this);
		String username = SystemCfg.getPoliceName(this);
		String vioid = XiangXunApplication.getInstance().getDevId() + System.currentTimeMillis();
		final VioData vioData = new VioData();
		vioData.setVioid(vioid);
		vioData.setPublishCode(mPulishCode.getText().toString());
		vioData.setUser(username);
		vioData.setViocode(viocode);
		vioData.setPlatetype(platetype);
		vioData.setPlatenum(plateNum);
		vioData.setPicnum(picNum);
		vioData.setDatetime(date);
		vioData.setRoadid(roadid);
		vioData.setRoadname(roadname);
		vioData.setRoadlocation(roadlocation);
		vioData.setRoaddirect(roaddirect);
		vioData.setIsupfile(0);
		vioData.setIssure(1);
		vioData.setDatasource(0);
		vioData.setDealstate(0);
		vioData.setDisposetype(type);
		vioData.setViotype(DBManager.getInstance().getVioNameByCode(viocode));

		if (mTvCarColorType.getText().toString().length() > 0) {
			vioData.setCarColor(mTvCarColorType.getText().toString());
			Type carColortype = (Type) mTvCarColorType.getTag();
			vioData.setCarColorCode(carColortype.code);
		}

		if (mTvPlateColorType.getText().toString().length() > 0) {
			vioData.setPlateColor(mTvPlateColorType.getText().toString());
			Type plateColorCode = plateColorTypeDialog.getSelectedTypeItem();
			vioData.setPlateColorCode(plateColorCode.code);
		}

		new Thread(new Runnable() {
			String pic0 = "";
			String pic1 = "";
			String pic2 = "";
			String pic3 = "";

			@Override
			public void run() {
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
				ImageUtils iu = new ImageUtils(PublishViolationsParkingActivity.this);

				File f = new File(pic0);
				if (f != null && f.exists() && f.isFile()) {
					iu.pressText(pic0, "", getResources().getString(R.string.textviewaddress) //
							+ ":" + vioData.getRoadname(), getResources().getString(R.string.fangweima) //
							+ ":" + Md5.MD5("" + "" + vioData.getRoadname() + vioData.getDatetime() + "0" //
									+ "" + "" + "").substring(0, 9));
				}
				f = new File(pic1);
				if (f != null && f.exists() && f.isFile()) {
					iu.pressText(pic1, "", getResources().getString(R.string.textviewaddress) //
							+ ":" + vioData.getRoadname(), getResources().getString(R.string.fangweima) //
							+ ":" + Md5.MD5("" + "" + vioData.getRoadname() + vioData.getDatetime() + "1" //
									+ "" + "" + "").substring(0, 9));
				}
				f = new File(pic2);
				if (f != null && f.exists() && f.isFile()) {
					iu.pressText(pic2, "", getResources().getString(R.string.textviewaddress) //
							+ ":" + vioData.getRoadname(), getResources().getString(R.string.fangweima) //
							+ ":" + Md5.MD5("" + "" + vioData.getRoadname() + vioData.getDatetime() + "2" //
									+ "" + "" + "").substring(0, 9));
				}
				f = new File(pic3);
				if (f != null && f.exists() && f.isFile()) {
					iu.pressText(pic3, "", getResources().getString(R.string.textviewaddress) //
							+ ":" + vioData.getRoadname(), getResources().getString(R.string.fangweima) //
							+ ":" + Md5.MD5("" + "" + vioData.getRoadname() + vioData.getDatetime() + "3" //
									+ "" + "" + "").substring(0, 9));
				}
				vioData.setPicurl0(pic0);
				vioData.setPicurl1(pic1);
				vioData.setPicurl2(pic2);
				vioData.setPicurl3(pic3);

				DBManager.getInstance().add(vioData);
				DBManager.getInstance().markPunishId(mPulishCode.getText().toString(), "park");

				saveWarnVioData(pic0, pic1, pic2);

				XiangXunApplication.getInstance().getMainService().start(ConstantStatus.UPLOAD_VIODATA);
				MsgToast.geToast().setMsg("上传成功！");
				PublishViolationsParkingActivity.this.finish();
			}
		}).start();
	}

	@Override
	public void onClick(View arg0) {
		if (keybord != null && keybord.isShow())
			keybord.hideKeyboard();

		switch (arg0.getId()) {
		case R.id.iv_publish_add_picture:
			//必要时隐藏输入法键盘
//			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//			imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			Utils.hideSoftInputFromWindow(PublishViolationsParkingActivity.this);
			if (mPopupWindow == null || !mPopupWindow.isShowing()) {
				showSelectAddPath(); // 选择添加图片方式
				mVSupernatant.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.tv_publish_carcolor:
			carColorTypeDialog.show();
			break;
		case R.id.tv_publish_platecolor:
			plateColorTypeDialog.show();
			break;
		case R.id.tv_add_picture_from_camera:
			Intent intentCamera = new Intent(PublishViolationsParkingActivity.this, CameraActivity.class);
			intentCamera.putExtra("size", photoPaths.size());
			intentCamera.setAction("publishFourPhotos");
			startActivityForResult(intentCamera, 0);
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
		case R.id.yuancheng:
			Utils.hideSoftInputFromWindow(PublishViolationsParkingActivity.this);
			if (CheckInputText()) {
				if (isSetPrint) {
					if (!isPrint) {
						MsgToast.geToast().setMsg("请打印凭证，并拍照凭证！");
						return;
					}
				}
				saveDb(0);
				onBackPressed();
			}
			break;
		case R.id.accedtdate:
			Tools.showDateTimePicker(this, edttime);
			break;
		case R.id.title_view_back_img:
			onBackPressed();
			break;
		case R.id.title_view_operation_imageview:
			if (isSetPrint && CheckInputData()) {
				printData();
			}
			break;
		case R.id.ll_driver_plate_num:
			if (!mTgBtnPlateSearch.isChecked()) {
				Intent intentPlate = wtUtils.StartPlateRecog(PublishViolationsParkingActivity.this);
				startActivityForResult(intentPlate, 3);
			} else {
				if (mEditPlate.getText().toString().length() == 0) {
					MsgToast.geToast().setMsg(R.string.plateNotNull);
					return;
				}
				if (mTvPlateType.getText().toString() == null || mTvPlateType.getText().toString().length() <= 0) {
					MsgToast.geToast().setMsg(R.string.checkPlateType);
					return;
				}
				mDialog.setMessage(getResources().getString(R.string.checking_hold));
				mDialog.setTitle("");
				mDialog.setIndeterminate(true);
				mDialog.setCancelable(true);
				mDialog.show();
				ListDataItem platetype = (ListDataItem) mTvPlateType.getTag();
				DcNetWorkUtils.searchVehicleInfo(this, mEditPlate.getText().toString(), platetype.getid(), mHander);
			}
			break;
		case R.id.ll_publish_click_traffictype:
			affairsPlateTrafficTypeDialog.show();
			break;
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
		}
	}

	private boolean CheckInputText() {
		if (null != photoPaths) {
			if (isSetPrint) {
				if (photoPaths.size() <= 1) {
					MsgToast.geToast().setMsg(R.string.noprintpicturemessage);
					return false;
				}
			} else {
				if (photoPaths.size() <= 1) {
					MsgToast.geToast().setMsg(R.string.nopicturemessage2);
					return false;
				}
			}
		} else {
			MsgToast.geToast().setMsg(R.string.nopicture);
			return false;
		}
		return  CheckInputData();
	}

	private boolean CheckInputData() {
		if (mTvPlateType.getText().toString() == null || mTvPlateType.getText().toString().length() <= 0) {
			MsgToast.geToast().setMsg("请选择号牌种类");
			return false;
		} else if (!Tools.isCarnumberNO(mEditPlate.getText().toString(), mTvPlateType.getText().toString())) {
			MsgToast.geToast().setMsg("号牌号码格式不正确，请确认！");
			return false;
		} else if (mPublishTrafficType.getText().toString() == null || mPublishTrafficType.getText().toString().length() <= 0) {
			MsgToast.geToast().setMsg("请选择交通方式");
			return false;
		} else if (mTvVioType.getText().toString() == null || mTvVioType.getText().toString().length() <= 0) {
			MsgToast.geToast().setMsg("请输入违法行为");
			return false;
		}else if (mTvAddress.getText().toString() == null || mTvAddress.getText().toString().length() <= 0) {
			MsgToast.geToast().setMsg("请选择违法地点");
			return false;
		}else if (mTvAddress.getRoadCode() == null) {
			MsgToast.geToast().setMsg("道路名称错误!");
			return false;
		}else if (!Utils.CheckRoadExt(mTvAddress.getText().toString(), roadExt)) {
			MsgToast.geToast().setLongMsg("道路名称错误!");
			return false;
		} else if (mTvCarColorType.getText().toString() == null || mTvCarColorType.getText().toString().length() <= 0) {
			MsgToast.geToast().setMsg("请选择车身颜色");
			return false;
		} else if (mTvPlateColorType.getText().toString() == null || mTvPlateColorType.getText().toString().length() <= 0) {
			MsgToast.geToast().setMsg("请选择号牌颜色");
			return false;
		}
		return true;
	}

	private void printData() {
		if (!mPrintService.isPrintConnected()) {
			printerDialog = Utils.showNoPrinter(PublishViolationsParkingActivity.this);
			printerDialog.setButLeftListener(PublishViolationsParkingActivity.this);
			printerDialog.setButRightListener(PublishViolationsParkingActivity.this);
		} else {
//			if (!isPrint) {
				if (mPrintService.isPrintConnected()) {
					connectPrintDevice();
				} else {
					MsgToast.geToast().setMsg(R.string.iniBlueToothPrinter);
				}
//			} else {
//				MsgToast.geToast().setMsg("编辑模式不支持重复打印！");
//				return;
//			}
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
			mPrintService.print(mPrintService.formatContent(generateContent(), 9), PrintInfo.TITLE_TYPE_PARKING);
			isPrint = true;
			return true;
		} else {
			return false;
		}
	}

	@SuppressLint("DefaultLocale")
	private String generateContent() {
		StringBuffer content = new StringBuffer();
		Resources resources = this.getResources();
		content.append(getResources().getString(R.string.bianhao) + mPulishCode.getText().toString() + "%end%");
		content.append(resources.getString(R.string.platenum) + ":" + mEditPlate.getText().toString().toUpperCase() + "%end%");
		content.append("车身颜色:" + mTvCarColorType.getText().toString() + "%end%");
		content.append(resources.getString(R.string.platetype) + ":" + mTvPlateType.getText().toString().toUpperCase() + "%end%");
		content.append("号牌颜色:" + mTvPlateColorType.getText().toString() + "%end%");
		content.append("违法时间:" + Utils.ChangeTimeTochs(edttime.getText().toString()) + "%end%");
		content.append("违法地点:" + mTvAddress.getText().toString().substring(6));
		content.append(mTvRoadDirect.getText().toString());
		if (mTvRoadStep.getText().toString().length() > 0 )
			content.append(mTvRoadStep.getText().toString());
		if (mTvRoadKm.getText().toString().length() > 0)
			content.append(mTvRoadKm.getText().toString() + "公里");
		if (mTvRoadMi.getText().toString().length() > 0)
			content.append(mTvRoadMi.getText().toString() + "米");
		if (mTvRoadZhan.getText().toString().length() > 0 )
			content.append(mTvRoadZhan.getText().toString());

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
		content.append(resources.getString(R.string.shishi) + vt.simpleName + "(");
		content.append(getResources().getString(R.string.code) + vt.getCode() + ")");
//			content.append(String.format(resources.getString(R.string.weifa), vt.getLaw()));
		content.append("根据" + vt.getRule() + "罚款" + vt.getFineDefault().toString() + "元" + "记" + vt.getPunish().toString() +"分");
		content.append( resources.getString(R.string.parkingspeed) + SystemCfg.getPunishAddress(mContext) + "接受处理" + "%end%");
		return content.toString();
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
	public void vioCode(Type VioType) {
		VioTypeString = VioType.type;	//vioname

		SystemCfg.setVioHistory(this, VioType.code + "-" + VioTypeString);
	}

	private void saveWarnVioData(String Pic0, String Pic1, String Pic2) {
		// 保存简易处罚六合一数据格式
		ParkVio parkVio = new ParkVio();
		parkVio.xh = "";
		parkVio.cjjg = SystemCfg.getDepartmentCode(this);;	//采集机关
		parkVio.clfl = "3";	//车辆分类
		parkVio.hpzl = DBManager.getInstance().getCodeByTypeAndNameFromDic("PLATE_TYPE", mTvPlateType.getText().toString());	//号牌种类
		parkVio.hphm = mEditPlate.getText().toString();	//号牌号码
		parkVio.fzjg = mEditPlate.getText().toString().substring(0,2);	//发证机关
		parkVio.zsxzqh = "";	//住所行政区划
		parkVio.zsxxdz = "";	//住所详细地址
		parkVio.dh = "";		//电话
		parkVio.lxfs = "";	//联系方式
		parkVio.tzsh = "";	//通知书号
		parkVio.tzrq = edttime.getText().toString().substring(0, 10);	//通知日期
		parkVio.cjfs = "6";	//采集方式
		parkVio.wfsj = edttime.getText().toString();	//违法时间

		parkVio.xzqh = "";	//行政区划
		parkVio.wfdd = "";	//违法地点
		parkVio.lddm = "";	//路段号码
		parkVio.ddms = "";	//地点米数
		parkVio.wfsj1 = "";	//违法时间1
		parkVio.wfdd1 = "";	//违法地点1
		parkVio.lddm1 = "";	//路段号码1
		parkVio.ddms1 = "";	//地点米数1
		parkVio.wfdz = "";	//违法地址
		parkVio.wfxw = "";	//违法行为
		parkVio.scz = "";		//实测值
		parkVio.bzz = "";		//标准值
		parkVio.zqmj = "";	//执勤民警
		parkVio.spdz = "";	//视频地址
		parkVio.sbbh = "";	//设备编号
		parkVio.zpstr1 = Pic0;	//照片1
		parkVio.zpstr2 = Pic1;	//照片2
		parkVio.zpstr3 = Pic2;	//照片3

		RoadInfo roadInfo = DBManager.getInstance().getRoadInfoByName(mTvAddress.getText().toString().substring(6));
		if (roadInfo.uploadcode != null) {
			parkVio.wfdd = roadInfo.uploadcode.substring(0, 5);
		}

		if (roadExt != null && roadExt.xzqh != null && roadExt.xzqh.length() == 6) {
			parkVio.xzqh = roadExt.xzqh;
		} else
			parkVio.xzqh = SystemCfg.getDepartmentCode(this).substring(0,6);

		parkVio.wfdz = mTvAddress.getText().toString().substring(6);
		if (mTvRoadKm.getText().toString().length() > 0
				&& (parkVio.wfdd.startsWith("0") || parkVio.wfdd.startsWith("1") || parkVio.wfdd.startsWith("2")))	//高速\国道\省道
			parkVio.lddm = mTvRoadKm.getText().toString();
		else
			parkVio.lddm = "0000";
		if (!parkVio.wfdz.contains("高速")) {
			String roadStepName = mTvRoadStep.getText().toString();
			if (roadExt != null && roadExt.ldlist != null && roadExt.ldlist.size() > 0 && roadStepName.length() > 0) {
				for (int i = 0, len = roadExt.ldlist.size(); i < len; i++) {
					ResultData.RoadStep roadStep = roadExt.ldlist.get(i);
					if (roadStepName.equals(roadStep.ldmc)) {
						parkVio.lddm = roadExt.ldlist.get(i).lddm;
						break;
					}
				}
			}
		}

		if (mTvRoadMi.getText().toString().length() > 0)
			parkVio.ddms = mTvRoadMi.getText().toString();
		else
			parkVio.ddms = "000";

		if (mTvRoadKm.getText().toString().length() > 0)
			parkVio.wfdz +=  mTvRoadKm.getText().toString() + "公里";
		if (mTvRoadMi.getText().toString().length() > 0)
			parkVio.wfdz += mTvRoadMi.getText().toString() + "米";
		if (mTvRoadDirect.getText().toString().length() > 0 )
			parkVio.wfdz += mTvRoadDirect.getText().toString();
		if (mTvRoadStep.getText().toString().length() > 0)
			parkVio.wfdz += mTvRoadStep.getText().toString();
		if (mTvRoadZhan.getText().toString().length() > 0 )
			parkVio.wfdz += mTvRoadZhan.getText().toString();

		parkVio.wfxw = mTvVioType.getText().toString();
		parkVio.zqmj = SystemCfg.getUpdateCode(this); //"047986";	//"047987";	//SystemCfg.getPoliceCode(this);

		parkVio.jtfs = DBManager.getInstance().getCodeByTypeAndNameFromDic("JTFS", mPublishTrafficType.getText().toString());
		parkVio.csys = mTvCarColorType.getText().toString();
		Type carColortype = (Type) mTvCarColorType.getTag();
		parkVio.csys = carColortype.getCode();
		parkVio.syxz = "A";
		if (vehicleInfo != null) {
			parkVio.jdcsyr = vehicleInfo.syr;	//机动车所有人
			parkVio.syxz = vehicleInfo.syxz;	//使用性质
			parkVio.fdjh = vehicleInfo.fdjxh;	//发动机号
			parkVio.clsbdh = vehicleInfo.clsbdh;	//车辆识别代号
			parkVio.csys = vehicleInfo.csys;	//车身颜色
			parkVio.clpp = vehicleInfo.clpp1;	//车辆品牌
			parkVio.jtfs = vehicleInfo.cllx;	//交通方式
		}

		if (persionInfo != null) {
			parkVio.zsxzqh = persionInfo.lxzsxzqh;
			parkVio.zsxxdz = persionInfo.lxzsxxdz;
			parkVio.lxfs = persionInfo.lxdh;
			parkVio.dh = persionInfo.sjhm;
		}

		UpdateVio updateVio = new UpdateVio();
		updateVio.isupfile = 0;
		updateVio.type = "park";
		updateVio.sn = "" + System.currentTimeMillis();
		updateVio.datetime = parkVio.wfsj;
		updateVio.vio = XiangXunApplication.getGson().toJson(parkVio);

		DBManager.getInstance().add(updateVio);
	}

	private void showRoadStep() {
		HideRoadStep();

		RoadInfo roadInfo = DBManager.getInstance().getRoadInfoByName(mTvAddress.getText().toString().substring(6));
		if (roadInfo.note != null)	{
			if (roadInfo.note.contains("ldmc"))
				roadExt = XiangXunApplication.getGson().fromJson(roadInfo.note, ResultData.RoadExt.class);
			else {
				roadExt = new ResultData.RoadExt();
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

