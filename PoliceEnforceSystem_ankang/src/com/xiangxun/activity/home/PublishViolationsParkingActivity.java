package com.xiangxun.activity.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
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
import android.view.LayoutInflater;
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
import com.xiangxun.bean.ListDataItem;
import com.xiangxun.bean.ListDataObj;
import com.xiangxun.bean.MDate;
import com.xiangxun.bean.PrintInfo;
import com.xiangxun.bean.ResultData;
import com.xiangxun.bean.Type;
import com.xiangxun.bean.VioData;
import com.xiangxun.cfg.SystemCfg;
import com.xiangxun.db.DBManager;
import com.xiangxun.request.DcNetWorkUtils;
import com.xiangxun.service.StdPrintService;
import com.xiangxun.util.ConstantStatus;
import com.xiangxun.util.ImageUtils;
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
import com.xiangxun.widget.TitleView;
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
public class PublishViolationsParkingActivity extends BaseActivity implements OnClickListener, DeletePictureInterface {
	private PublishViolationsParkingActivity mContext = null;
	private TitleView titleView;
	private Button yuancheng;
	private List<String> photoPaths;
	private TextView edttime;
	private boolean isLive = false;
	private EditText mEditPlate;
	private PublishPictureAdapter mViewPagerAdapter = null;
	// 添加图片
	private ImageView mIvAddPicture = null;
	private NoViewPager mVpAddPicture = null;
	private TextView mPulishCode;
	//private TextView mTvVioType;
	private TextView mTvCarColorType;
	private TextView mTvPlateColorType;
	private LinearLayout ll_publish_click_viotype;
	private PublishSelectAffairsAddressDialog affairsVioTypeDialog;
	private PublishSelectCarColorTypeDialog carColorTypeDialog;
	private PublishSelectPlateColorTypeDialog plateColorTypeDialog;

	private TextView mTvAddress;
	private LinearLayout ll_publish_click_address;
	private PublishSelectAffairsAddressDialog affairsAddressDialog;
	private EditText mTvRoadLocation;
	private TextView mTvRoadDirect;
	private LinearLayout layout_roadlocation;
	private LinearLayout ll_publish_click_RoadDirect;
	private LinearLayout ll_publish_click_roadDirect;
	private PublishSelectAffairsAddressDialog affairsRoadDirectDialog;
	
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
					MsgToast.geToast().setMsg(R.string.getNumFailed);
				}
				break;
			case ConstantStatus.OTHER_PLATENUM:
				Utils.showPlateInfo(PublishViolationsParkingActivity.this, msg);
				break;
			case ConstantStatus.VEHICLE_SEARCH_SUCCESS:
				ResultData.VehicleInfo vehicleInfo = (ResultData.VehicleInfo) msg.obj;
				if (vehicleInfo != null && vehicleInfo.carNum != null) {
					VehicleDialog = new MsgDialog(PublishViolationsParkingActivity.this);
					VehicleDialog.setTiele("机动车信息");
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

					carColorTypeDialog.setSelectCode(vehicleInfo.carColorCode);
				} else {
					MsgToast.geToast().setMsg("无此机动车信息！");
				}
				break;

			case ConstantStatus.VEHICLE_SEARCH_FALSE:
				MsgToast.geToast().setMsg("机动车数据查询失败, 稍后请重试!");
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
		isSetPrint = SystemCfg.getIsPrint(this);
		initView();
		initData();
		initListener();
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
		mPulishCode = (TextView) findViewById(R.id.publish_code);
		mTvCarColorType = (TextView) findViewById(R.id.tv_publish_carcolor);
		mTvPlateColorType = (TextView) findViewById(R.id.tv_publish_platecolor);
		ll_publish_click_viotype = (LinearLayout) findViewById(R.id.ll_publish_click_viotype);
		mTvAddress = (TextView) findViewById(R.id.tv_publish_address);
		ll_publish_click_address = (LinearLayout) findViewById(R.id.ll_publish_click_address);
		ll_publish_click_RoadDirect = (LinearLayout) findViewById(R.id.ll_publish_click_road_direct);

		LinearLayout layout_roadlocation = (LinearLayout) findViewById(R.id.layout_roadlocation);
		LinearLayout layout_roaddirect = (LinearLayout) findViewById(R.id.layout_roaddirect);
		if (getResources().getString(R.string.workroad_city).equalsIgnoreCase(SystemCfg.getWorkRoad(this))) {
			layout_roadlocation.setVisibility(View.GONE);
			layout_roaddirect.setVisibility(View.GONE);
		}
		
		mTvRoadLocation = (EditText) findViewById(R.id.tv_publish_road_location);
		mTvRoadDirect = (TextView) findViewById(R.id.tv_publish_road_direct);
		ll_publish_click_roadDirect = (LinearLayout) findViewById(R.id.ll_publish_click_road_direct);		
		mTvPlateType = (TextView) findViewById(R.id.tv_publish_platetype);
		ll_publish_click_platetype = (LinearLayout) findViewById(R.id.ll_publish_click_platetype);
		mLlPlateSearch = (LinearLayout) findViewById(R.id.ll_driver_plate_num);
		mTgBtnPlateSearch = (ToggleButton)  findViewById(R.id.tgbtn_search_plate);

		String[] roads = DBManager.getInstance().queryRoadname();
		affairsAddressDialog = new PublishSelectAffairsAddressDialog(this, roads, mTvAddress, "选择违法地点");
		String[] directs = DBManager.getInstance().getRoadDirection();
		affairsRoadDirectDialog = new PublishSelectAffairsAddressDialog(this, directs, mTvRoadDirect, getResources().getString(R.string.roaddirect_input));		
		// 获取本地数据库中的违法数据词典和道路数据
		String[] viotypes = DBManager.getInstance().queryVioType();
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

		mVSupernatant = findViewById(R.id.v_supernatant_background);
	}

	@Override
	public void initListener() {
		ll_publish_click_viotype.setOnClickListener(this);
		ll_publish_click_address.setOnClickListener(this);
		ll_publish_click_roadDirect.setOnClickListener(this);
		ll_publish_click_platetype.setOnClickListener(this);
		mTvCarColorType.setOnClickListener(this);
		mTvPlateColorType.setOnClickListener(this);
		yuancheng.setOnClickListener(this);
		edttime.setOnClickListener(this);
		mIvAddPicture.setOnClickListener(this);
		mVpAddPicture.setOnClickListener(this);
		mLlPlateSearch.setOnClickListener(this);
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
			bindService(in, conn, Context.BIND_AUTO_CREATE);
		}
		mDialog = new ProgressDialog(this);
		photoPaths = new ArrayList<String>();
		String publishCode = DBManager.getInstance().getPunishId("park");
		if (publishCode != null && publishCode.length() > 0) {
			mPulishCode.setText(publishCode);
		} else {
			// 更新违停告知书编号数据

			DcNetWorkUtils.updatePunishId(this, SystemCfg.getUserId(this), "park", DBManager.getInstance(), mHander);
//			MsgToast.geToast().setMsg(R.string.getNumFailed);
		}
		// 显示图片位

		mViewPagerAdapter = new PublishPictureAdapter(this, mVpAddPicture, photoPaths);
		mVpAddPicture.setAdapter(mViewPagerAdapter);
		if (photoPaths.size() > 0) {
			mVpAddPicture.setVisibility(View.VISIBLE);
		}
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
			bindService(i, conn, Context.BIND_AUTO_CREATE);
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
		String roadid = DBManager.getInstance().queryRoadId().get(affairsAddressDialog.getSelectedItemPosition());
		String roadname = mTvAddress.getTag().toString();
		String roadlocation = mTvRoadLocation.getText().toString();
		String roaddirect = DBManager.getInstance().getRoadDirectionCodeByName(mTvRoadDirect.getText().toString());

		String plateNum = mEditPlate.getText().toString();
		if (plateNum != null && plateNum.contains(",")) {
			plateNum.replace(",", "，");
		}
		ListDataItem tag = (ListDataItem) mTvPlateType.getTag();
		String platetype = tag.getid();
		String viocode = SystemCfg.getVioParkCode(this);
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
		vioData.setIssure(0);
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
				XiangXunApplication.getInstance().getMainService().start(ConstantStatus.UPLOAD_VIODATA);
				MsgToast.geToast().setMsg("上传成功！");
				PublishViolationsParkingActivity.this.finish();
			}
		}).start();
	}

	@Override
	public void onClick(View arg0) {
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
		case R.id.ll_publish_click_viotype:
			affairsVioTypeDialog.show();
			break;
		case R.id.ll_publish_click_address:
			affairsAddressDialog.show();
			break;
		case R.id.ll_publish_click_road_direct:
			affairsRoadDirectDialog.show();
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
			bindService(in, conn, Context.BIND_AUTO_CREATE);
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
				if (photoPaths.size() <= 0) {
					MsgToast.geToast().setMsg(R.string.nopicturemessage);
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
		if (mEditPlate.getText().toString().length() != 7) {
			Toast.makeText(PublishViolationsParkingActivity.this, "号牌号码格式不正确，请确认！", Toast.LENGTH_SHORT).show();
			return false;
		} else if (!Tools.isCarnumberNO(mEditPlate.getText().toString())) {
			MsgToast.geToast().setMsg("号牌号码格式不正确，请确认！");
			return false;
		} else if (mTvPlateType.getText().toString() == null || mTvPlateType.getText().toString().length() <= 0) {
			MsgToast.geToast().setMsg("请选择号牌种类");
			return false;
		}else if (mTvAddress.getText().toString() == null || mTvAddress.getText().toString().length() <= 0) {
			MsgToast.geToast().setMsg("请选择违法地点");
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
		content.append("违停时间:" + Utils.ChangeTimeTochs(edttime.getText().toString()) + "%end%");
		content.append("违停地点:" + mTvAddress.getText().toString() + "%end%");
		if (getResources().getString(R.string.workroad_speed).equalsIgnoreCase(SystemCfg.getWorkRoad(this))) {
			content.append("(" + getResources().getString(R.string.roadlocation_flag) + mTvRoadLocation.getText().toString());
			content.append(getResources().getString(R.string.roaddirect_flag) + mTvRoadDirect.getText().toString() + ")");
		}
		content.append("%start%" + resources.getString(R.string.parkingTip) + "子长县公安局交通管理大队" + "接受处理（电话）0911-7116958" + "%end%");
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
	
}
