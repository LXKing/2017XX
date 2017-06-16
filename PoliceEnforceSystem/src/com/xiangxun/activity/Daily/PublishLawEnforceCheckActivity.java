package com.xiangxun.activity.Daily;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
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
import com.xiangxun.adapter.PublishPictureAdapter;
import com.xiangxun.app.BaseActivity;
import com.xiangxun.app.XiangXunApplication;
import com.xiangxun.bean.CarInfoList;
import com.xiangxun.bean.LawCheckInfo;
import com.xiangxun.bean.ListDataItem;
import com.xiangxun.bean.ListDataObj;
import com.xiangxun.bean.MDate;
import com.xiangxun.bean.ResultData;
import com.xiangxun.bean.PersionInfoX;
import com.xiangxun.bean.RoadInfo;
import com.xiangxun.bean.Type;
import com.xiangxun.bean.VehicleInfoX;
import com.xiangxun.bean.VioDic;
import com.xiangxun.cfg.SystemCfg;
import com.xiangxun.db.DBManager;
import com.xiangxun.request.DcNetWorkUtils;
import com.xiangxun.util.ConstantStatus;
import com.xiangxun.util.KeyboardUtil;
import com.xiangxun.util.Tools;
import com.xiangxun.util.Utils;
import com.xiangxun.widget.DeletePictureInterface;
import com.xiangxun.widget.MsgToast;
import com.xiangxun.widget.NoViewPager;
import com.xiangxun.widget.PublishSelectAffairsAddressDialog;
import com.xiangxun.widget.PublishSelectAffairsPlateTypeDialog;
import com.xiangxun.widget.PublishSelectAffairsTypeDialog;
import com.xiangxun.widget.RoadEditView;
import com.xiangxun.widget.SelectDialog;
import com.xiangxun.widget.SelectDialog.SelectItemClick;
import com.xiangxun.widget.TitleView;
import com.xiangxun.widget.VioEditView;
import com.xiangxun.wtone.IDCameraActivity;
import com.xiangxun.wtone.wtUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @package: com.xiangxun.activity
 * @ClassName: PublishLawEnforceCheckActivity.java
 * @Description: 发布执法检查车辆信息页面
 * @author: HanGJ
 * @date: 2015-8-18 下午2:16:32
 */
public class PublishLawEnforceCheckActivity extends BaseActivity implements OnClickListener, DeletePictureInterface, SelectItemClick, VioEditView.onItemvioNameClick {
	private PublishLawEnforceCheckActivity mContext = null;
	private TitleView titleView;
	private KeyboardUtil keybord;
	// 存放照片url的容器
	private List<String> photoPaths;
	// 添加图片
	private ImageView mIvAddPicture = null;
	private NoViewPager mVpAddPicture = null;
	private PublishPictureAdapter mViewPagerAdapter = null;

	private EditText mDriverName;
	private EditText mPhoneNum;
	private EditText mDriverNum;
	private LinearLayout mPublishClickCarnum;

	private EditText mPlateNum;
	private LinearLayout mPublishClickPlatenum;
	private TextView mPublishPlateType;
	private LinearLayout mPublishClickPlateType;
	private ListDataObj ldo = null;
	private PublishSelectAffairsPlateTypeDialog affairsPlateTypeDialog;
	private EditText mOwner;
	private TextView mPlateMust;
	private TextView mPlateActual;

	private TextView mTvCheckType;
	private LinearLayout ll_publish_click_checktype;
	private PublishSelectAffairsTypeDialog affairsCheckTypeDialog;
	private VioEditView mTvVioType;
	private String VioTypeString;
//	private LinearLayout ll_publish_click_viotype;
//	private PublishSelectAffairsAddressDialog affairsVioTypeDialog;
	private RoadEditView mTvAddress;
//	private LinearLayout ll_publish_click_address;
//	private PublishSelectAffairsAddressDialog affairsAddressDialog;
//	private PublishSelectAffairsAddressDialog affairsRoadDirectionDialog;
	private TextView mPublishVioTime;
	private EditText mTvHandleWay;
	private EditText mTvMemo;
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


	private ToggleButton mTgBtnDriverSearch;
	private LinearLayout mLlDriverSearch;
	private ToggleButton mTgBtnPlateSearch;
	private LinearLayout mLlPlateSearch;
	private Button upload;

	private ProgressDialog mDialog;
	private Receiver receiver;
	private ArrayList<Type> dts = new ArrayList<Type>();
	// 浮层
	private View mVSupernatant = null;
	private PopupWindow mPopupWindow = null;

	private SelectDialog searchKeyDialog;
	private ArrayList<VioDic> vioTypelist = new ArrayList<VioDic>();
	private String[] vioTypeName;
	private Boolean[] vioTypeSel;
	private boolean isVioSelected = false;

	private ResultData.RoadExt roadExt;

	@SuppressLint("HandlerLeak")
	private Handler mHander = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mDialog.dismiss();
			switch (msg.what) {
			case ConstantStatus.PlateQueryFalse:
				Toast.makeText(PublishLawEnforceCheckActivity.this, R.string.nodata, Toast.LENGTH_SHORT).show();
				break;
			case ConstantStatus.DriverInfoSuccess:
				mDriverName.setText(msg.getData().getString("name"));
				Toast.makeText(PublishLawEnforceCheckActivity.this, R.string.plateNotifyCompleted, Toast.LENGTH_LONG).show();
				break;

			case ConstantStatus.CarInfoSuccess:
				Bundle bundle = msg.getData();
				if (bundle != null) {
					mOwner.setText(bundle.getString("owner"));
				}
				break;

			case ConstantStatus.PlateQuerySuccess:
				Bundle bundle2 = msg.getData();
				CarInfoList carinfolist = (CarInfoList) bundle2.getSerializable("carlist");
				if (carinfolist == null || carinfolist.list == null || carinfolist.list.get(0) == null || carinfolist.list.get(0).platetype == null) {
					Toast.makeText(PublishLawEnforceCheckActivity.this, R.string.nodata, Toast.LENGTH_SHORT).show();
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
				mOwner.setText(carinfolist.list.get(0).owner);
				Toast.makeText(PublishLawEnforceCheckActivity.this, R.string.plateNotifyCompleted, Toast.LENGTH_LONG).show();
				break;
			case ConstantStatus.NetWorkError:
				MsgToast.geToast().setMsg("网络异常");
				break;
			case ConstantStatus.DRIVER_SEARCH_SUCCESS:
				PersionInfoX persionInfo = (PersionInfoX) msg.obj;
				if (persionInfo != null) {
					mDriverName.setText(persionInfo.xm);
					mPhoneNum.setText(persionInfo.sjhm);
				} else {
					MsgToast.geToast().setMsg("无此驾驶员信息！");
				}
				break;

			case ConstantStatus.DRIVER_SEARCH_FALSE:
				MsgToast.geToast().setMsg("驾驶员数据查询失败, 稍后请重试!");
				break;
			case ConstantStatus.VEHICLE_SEARCH_SUCCESS:
				VehicleInfoX vehicleInfo = (VehicleInfoX) msg.obj;
				if (vehicleInfo != null && vehicleInfo.hphm != null) {
					mOwner.setText(vehicleInfo.syr);
					mPlateMust.setText(vehicleInfo.hdzk);
				} else {
					MsgToast.geToast().setMsg("无此机动车信息！");
				}
				break;
			case ConstantStatus.VEHICLE_SEARCH_FALSE:
				MsgToast.geToast().setMsg("机动车数据查询失败, 稍后请重试!");
				break;
			case ConstantStatus.OTHER_PLATENUM:
				Utils.showPlateInfo(PublishLawEnforceCheckActivity.this, msg);
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
		setContentView(R.layout.publish_lawenforce_check_layout);
		mContext = PublishLawEnforceCheckActivity.this;
		initView();
		initData();
		initListener();

		String RoadHistory = SystemCfg.getRoadHistory(this);
		mTvAddress.setText(RoadHistory);
	}

	@Override
	public void initView() {
		titleView = (TitleView) findViewById(R.id.tv_comm_title);
		titleView.setLeftBackOneListener(R.drawable.back_normal, this);
		titleView.setTitle(R.string.zfjccl);
		mIvAddPicture = (ImageView) findViewById(R.id.iv_publish_add_picture);
		mVpAddPicture = (NoViewPager) findViewById(R.id.vp_publish_add_pictures);
		mDriverName = (EditText) findViewById(R.id.driver_name);
		mPhoneNum = (EditText) findViewById(R.id.call_phone_num);
		mDriverNum = (EditText) findViewById(R.id.driver_num);
		mPublishClickCarnum = (LinearLayout) findViewById(R.id.ll_publish_click_carnum);

		mPlateNum = (EditText) findViewById(R.id.driver_plate_num);
		mPublishClickPlatenum = (LinearLayout) findViewById(R.id.ll_publish_click_platenum);
		mPublishPlateType = (TextView) findViewById(R.id.tv_publish_platetype);
		mPublishClickPlateType = (LinearLayout) findViewById(R.id.ll_publish_click_platetype);
		mOwner = (EditText) findViewById(R.id.plate_car_owner);
		mPlateMust = (TextView) findViewById(R.id.plate_must);
		mPlateActual = (TextView) findViewById(R.id.plate_actual);

		mTvVioType = (VioEditView) findViewById(R.id.tv_publish_viotype);
//		ll_publish_click_viotype = (LinearLayout) findViewById(ll_publish_click_viotype);
		mTvCheckType = (TextView) findViewById(R.id.tv_publish_checktype);
		ll_publish_click_checktype = (LinearLayout) findViewById(R.id.ll_publish_click_checktype);
		mTvAddress = (RoadEditView) findViewById(R.id.tv_check_address);

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
		mTvHandleWay = (EditText) findViewById(R.id.driver_handleway);
		mTvMemo = (EditText) findViewById(R.id.driver_memo);

		mLlDriverSearch = (LinearLayout) findViewById(R.id.ll_search_driver);
		mTgBtnDriverSearch = (ToggleButton) findViewById(R.id.tgbtn_search_driver);
		mLlPlateSearch = (LinearLayout) findViewById(R.id.ll_driver_plate_num);
		mTgBtnPlateSearch = (ToggleButton) findViewById(R.id.tgbtn_search_plate);
		upload = (Button) findViewById(R.id.upload);
		mVSupernatant = (View) findViewById(R.id.v_supernatant_background);

		keybord = new KeyboardUtil(PublishLawEnforceCheckActivity.this, mDriverNum);
	}

	@Override
	public void initData() {
		dts = DBManager.getInstance().getCheckTypes();
		photoPaths = new ArrayList<String>();
		mDialog = new ProgressDialog(this);
		receiver = new Receiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("621carlist");
		registerReceiver(receiver, filter);
		affairsCheckTypeDialog = new PublishSelectAffairsTypeDialog(this, dts, mTvCheckType, "选择检查类型");
		// 显示图片位
		mViewPagerAdapter = new PublishPictureAdapter(this, mVpAddPicture, photoPaths);
		mVpAddPicture.setAdapter(mViewPagerAdapter);
		if (photoPaths.size() > 0) {
			mVpAddPicture.setVisibility(View.VISIBLE);
		}

		String[] directions = DBManager.getInstance().getRoadDirection();
		affairsRoadDirectDialog = new PublishSelectAffairsAddressDialog(this, directions, mTvRoadDirect, getResources().getString(R.string.roaddirect_input));
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
	}

	@Override
	public void initListener() {
		mIvAddPicture.setOnClickListener(this);
		mVpAddPicture.setOnClickListener(this);
		mTvVioType.setvioNameClick(this);
		ll_publish_click_RoadDirect.setOnClickListener(this);

		ll_publish_click_RoadStep.setOnClickListener(this);
		mPublishClickPlateType.setOnClickListener(this);
		ll_publish_click_checktype.setOnClickListener(this);
		mPublishClickCarnum.setOnClickListener(this);
		mPublishClickPlatenum.setOnClickListener(this);
		mLlDriverSearch.setOnClickListener(this);
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
						SystemCfg.setRoadHistory(PublishLawEnforceCheckActivity.this, roadname);
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
				Utils.hideSoftInputFromWindow(PublishLawEnforceCheckActivity.this);
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
				Utils.hideSoftInputFromWindow(PublishLawEnforceCheckActivity.this);
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
				mPlateNum.setText(s.toString().toUpperCase());
				mPlateNum.setSelection(index);// 重新设置光标位置
				mPlateNum.addTextChangedListener(this);// 重新绑定事件
				
				Utils.chkOtherPlate(mContext, mHander, mPlateNum, searchWord);
			}
		});
		mPublishVioTime.setOnClickListener(this);

		upload.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Utils.hideSoftInputFromWindow(PublishLawEnforceCheckActivity.this);
				
				if (checkInputText()) {
					String pic0 = "";
					String pic1 = "";
					String pic2 = "";
					String pic3 = "";
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

					String roadcontext = "";
					String roaddirect = "";

					roadcontext = mTvRoadStep.getText().toString();
					if (mTvRoadKm.getText().toString().length() > 0)
						roadcontext = roadcontext + mTvRoadStep.getText().toString() + "公里";
					if (mTvRoadMi.getText().toString().length() > 0)
						roadcontext = roadcontext + mTvRoadMi.getText().toString() + "米";
					if (mTvRoadZhan.getText().toString().length() > 0)
						roadcontext = mTvRoadZhan.getText().toString();

					roaddirect = mTvRoadDirect.getText().toString();

					ListDataItem listDataItem = (ListDataItem) mPublishPlateType.getTag();
					LawCheckInfo z = new LawCheckInfo(0, mPublishVioTime.getText().toString(), mDriverName.getText().toString(), //
							listDataItem.getid(), mPhoneNum.getText().toString(), mOwner.getText().toString(), //
							mPlateNum.getText().toString(), mDriverNum.getText().toString(), mPlateMust.getText().toString(), //
							mPlateActual.getText().toString(),
							//ToUploadViodata.roadIds.get(ToUploadViodata.roadnames.indexOf(mTvAddress.getText().toString())),
							DBManager.getInstance().getRoadIdByName(mTvAddress.getText().toString().substring(6)),
							roadcontext,
							roaddirect,
							//VioTypeString,
							mTvVioType.getText().toString(),
							mTvHandleWay.getText().toString(), XiangXunApplication.getInstance().getUserName(), mTvMemo.getText().toString(),//
							pic0, pic1, pic2, pic3, dts.get(affairsCheckTypeDialog.getSelectedItemPosition()).code, 0);
					DBManager.getInstance().add(z);
					MsgToast.geToast().setMsg("上传成功！");
					XiangXunApplication.getInstance().getMainService().start(ConstantStatus.UPLOAD_LAWCHECKDATA);
					onBackPressed();
				}
			}
		});
	}

	@SuppressWarnings({ "deprecation", "unused" })
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
				super.onBackPressed();
			}
		} else {
			super.onBackPressed();
		}
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
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private boolean checkInputText() {
		if (photoPaths == null || photoPaths.size() <= 0) {
			MsgToast.geToast().setMsg("请拍摄照片");
			return false;
		}

		if (mDriverName.getText().length() < 1) {
			MsgToast.geToast().setMsg(R.string.checkName);
			return false;
		}

		if (mPhoneNum.getText().length() < 1) {
			MsgToast.geToast().setMsg(R.string.checkPhoneNum);
			return false;
		}
		if (!Tools.isPhoneNumber(mPhoneNum.getText().toString())) {
			MsgToast.geToast().setMsg("联系方式格式不正确！");
			return false;
		}

		if (!Tools.isIDCardValidate(mDriverNum.getText().toString())) {
			return false;
		}

		if (mPlateNum.getText().length() < 1) {
			MsgToast.geToast().setMsg(R.string.checkPlateNum);
			return false;
		}

		if (!Tools.isCarnumberNO(mPlateNum.getText().toString(), mPublishPlateType.getText().toString())) {
			Toast.makeText(PublishLawEnforceCheckActivity.this, "号牌号码格式不正确,请重新输入", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (mPublishPlateType.getText().toString() == null || mPublishPlateType.getText().toString().length() <= 0) {
			MsgToast.geToast().setMsg(R.string.checkPlateType);
			return false;
		}

		if (mPlateActual.getText().toString() == null || mPlateActual.getText().toString().length() <= 0 || Integer.parseInt(mPlateActual.getText().toString()) >= 100) {
			MsgToast.geToast().setMsg("实载人数必须为100以内数字");
			return false;
		}

		if (mPlateMust.getText().toString() == null || mPlateMust.getText().toString().length() <= 0 || Integer.parseInt(mPlateMust.getText().toString()) >= 100) {
			MsgToast.geToast().setMsg("核载人数必须为100以内数字");
			return false;
		}

		if (mTvCheckType.getText().toString().length() < 1) {
			MsgToast.geToast().setMsg("请选择检查类型");
			return false;
		}
		if (mTvAddress.getText().toString().length() < 1) {
			MsgToast.geToast().setMsg("请选择检查地点");
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
		if (mTvVioType.getText().toString().length() < 1) {
			MsgToast.geToast().setMsg("请输入违法代码");
			return false;
		}

		if (VioTypeString == null || VioTypeString.equals("")) {
			MsgToast.geToast().setMsg("请输入正确的违法代码");
			return false;
		}

		return true;
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
		if (keybord != null && keybord.isShow())
			keybord.hideKeyboard();

		switch (arg0.getId()) {
		case R.id.iv_publish_add_picture:
			//必要时隐藏输入法键盘
//			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//			imm.hideSoftInputFromWindow(PublishLawEnforceCheckActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			Utils.hideSoftInputFromWindow(PublishLawEnforceCheckActivity.this);
			if (mPopupWindow == null || !mPopupWindow.isShowing()) {
				showSelectAddPath(); // 选择添加图片方式
				mVSupernatant.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.tv_add_picture_from_camera:
			Intent intentCamera = new Intent(PublishLawEnforceCheckActivity.this, CameraActivity.class);
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
		case R.id.ll_publish_click_checktype:
			affairsCheckTypeDialog.show();
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
				Intent intentPlate = wtUtils.StartPlateRecog(PublishLawEnforceCheckActivity.this);
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
	public void itemOnClick(Boolean[] checks, int position) {
		isVioSelected = true;

		VioDic vioDic = vioTypelist.get(position);
		VioTypeString = vioDic.getSimpleName();
		mTvVioType.setText(vioDic.getCode());
	}

	@Override
	public void vioCode(Type VioType) {
		VioTypeString = VioType.type;	//vioname
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
