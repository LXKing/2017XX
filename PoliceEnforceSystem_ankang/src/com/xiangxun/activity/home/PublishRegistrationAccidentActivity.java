package com.xiangxun.activity.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
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

import com.xiangxun.activity.CameraActivity;
import com.xiangxun.activity.PhotoSelectActivity;
import com.xiangxun.activity.R;
import com.xiangxun.adapter.PublishPictureAdapter;
import com.xiangxun.app.BaseActivity;
import com.xiangxun.app.XiangXunApplication;
import com.xiangxun.bean.AccidentCar;
import com.xiangxun.bean.AccidentInfo;
import com.xiangxun.bean.ListDataItem;
import com.xiangxun.bean.ListDataObj;
import com.xiangxun.bean.MDate;
import com.xiangxun.bean.Type;
import com.xiangxun.cfg.SystemCfg;
import com.xiangxun.db.DBManager;
import com.xiangxun.util.ConstantStatus;
import com.xiangxun.util.Tools;
import com.xiangxun.util.Utils;
import com.xiangxun.widget.DeletePictureInterface;
import com.xiangxun.widget.MsgToast;
import com.xiangxun.widget.NoViewPager;
import com.xiangxun.widget.PublishSelectAffairsAddressDialog;
import com.xiangxun.widget.PublishSelectAffairsPlateTypeDialog;
import com.xiangxun.widget.PublishSelectAffairsTypeDialog;
import com.xiangxun.widget.PublishSelectCarNumDialog;
import com.xiangxun.widget.PublishSelectCarNumDialog.selectCarNumOnClick;
import com.xiangxun.widget.TitleView;
import com.xiangxun.wtone.wtUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @package: com.xiangxun.activity.home
 * @ClassName: PublishRegistrationAccidentActivity.java
 * @Description: 发布事故登记信息页面
 * @author: HanGJ
 * @date: 2015-8-18 下午2:40:37
 */
public class PublishRegistrationAccidentActivity extends BaseActivity implements OnClickListener, DeletePictureInterface, selectCarNumOnClick {
	private TitleView titleView;
	private Context mContext;
	// 存放照片url的容器
	private List<String> photoPaths;
	// 添加图片
	private ImageView mIvAddPicture = null;
	private NoViewPager mVpAddPicture = null;
	private PublishPictureAdapter mViewPagerAdapter = null;

	private EditText mAlarmName;
	private EditText mPhoneNum;
	private TextView mTvAddress;
	private LinearLayout ll_publish_click_address;
	private PublishSelectAffairsAddressDialog affairsAddressDialog;
	private EditText mTvRoadLocation;
	private TextView mTvRoadDirect;
	private LinearLayout ll_publish_click_RoadDirect;
	private PublishSelectAffairsAddressDialog affairsRoadDirectDialog;	
	private TextView mPublishVioTime;
	private TextView mTvWeatherStatus;
	private LinearLayout ll_publish_click_weather;
	private PublishSelectAffairsTypeDialog affairsWeatherDialog;

	private TextView mTvCarNum;
	private LinearLayout ll_publish_click_carnum;
	private PublishSelectCarNumDialog carNumDialog;
	private LinearLayout addCarInfo;

	private TextView mTvAccType;
	private LinearLayout ll_publish_click_acctype;
	private PublishSelectAffairsTypeDialog affairsAccTypeDialog;
	private EditText mJoinPerson;
	private EditText mTvHurt;
	private EditText mTvDied;
	private EditText mTvDesc;
	private Button save;
	private ListDataObj ldo = null;

	private ProgressDialog sDialog;
	private int btntag = 0;
	private List<Type> weatherList;
	private List<Type> accTypeList;
	private List<TextView> mTvPlates;
	private List<EditText> mTvPlateInfo;
	private List<View> mAddViews;
	private List<PublishSelectAffairsPlateTypeDialog> typeDialogs;
	private AccidentInfo ai;
	private String infolist;
	// 浮层
	private View mVSupernatant = null;
	private PopupWindow mPopupWindow = null;

	@SuppressLint("HandlerLeak")
	private Handler mHander = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ConstantStatus.OTHER_PLATENUM:
				Utils.showPlateInfo(PublishRegistrationAccidentActivity.this, msg);
				break;				
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.publish_accident_registrat_layout);
		mContext = this;
		initView();
		initData();
		initListener();
	}

	@Override
	public void initView() {
		titleView = (TitleView) findViewById(R.id.tv_comm_title);
		titleView.setTitle(R.string.sgdj);

		mIvAddPicture = (ImageView) findViewById(R.id.iv_publish_add_picture);
		mVpAddPicture = (NoViewPager) findViewById(R.id.vp_publish_add_pictures);
		mAlarmName = (EditText) findViewById(R.id.tv_alarm_name);
		mPhoneNum = (EditText) findViewById(R.id.call_phone_num);
		mTvAddress = (TextView) findViewById(R.id.tv_vio_address);
		ll_publish_click_address = (LinearLayout) findViewById(R.id.ll_publish_click_address);
		mTvRoadLocation = (EditText) findViewById(R.id.tv_vio_road_location);
		mTvRoadDirect = (TextView) findViewById(R.id.tv_vio_road_direct);
		ll_publish_click_RoadDirect = (LinearLayout) findViewById(R.id.ll_publish_click_road_direct);

		LinearLayout layout_roadlocation = (LinearLayout) findViewById(R.id.layout_roadlocation);
		LinearLayout layout_roaddirect = (LinearLayout) findViewById(R.id.layout_roaddirect);
		if (getResources().getString(R.string.workroad_city).equalsIgnoreCase(SystemCfg.getWorkRoad(this))) {
			layout_roadlocation.setVisibility(View.GONE);
			layout_roaddirect.setVisibility(View.GONE);
		}	
		mPublishVioTime = (TextView) findViewById(R.id.accedtdate);
		mTvWeatherStatus = (TextView) findViewById(R.id.tv_weather_status);
		ll_publish_click_weather = (LinearLayout) findViewById(R.id.ll_publish_click_weather);

		mTvCarNum = (TextView) findViewById(R.id.tv_car_num);
		ll_publish_click_carnum = (LinearLayout) findViewById(R.id.ll_publish_click_carnum);
		addCarInfo = (LinearLayout) findViewById(R.id.acclinearLayoutcarinfo);

		mTvAccType = (TextView) findViewById(R.id.tv_accident_type);
		ll_publish_click_acctype = (LinearLayout) findViewById(R.id.ll_publish_click_acctype);
		mJoinPerson = (EditText) findViewById(R.id.tv_join_person);
		mTvHurt = (EditText) findViewById(R.id.tv_accident_hurt);
		mTvDied = (EditText) findViewById(R.id.tv_accident_died);
		mTvDesc = (EditText) findViewById(R.id.tv_accident_info);
		save = (Button) this.findViewById(R.id.save);
		mVSupernatant = findViewById(R.id.v_supernatant_background);
	}

	@Override
	public void initData() {
		mAlarmName.setText(SystemCfg.getPoliceName(this));
		mPhoneNum.setText(SystemCfg.getMobile(this));
		sDialog = new ProgressDialog(this);
		photoPaths = new ArrayList<String>();
		ldo = new ListDataObj();
		mTvPlates = new ArrayList<TextView>();
		mTvPlateInfo = new ArrayList<EditText>();
		mAddViews = new ArrayList<View>();
		typeDialogs = new ArrayList<PublishSelectAffairsPlateTypeDialog>();
		ArrayList<Type> platetypes = DBManager.getInstance().getPlateTypes();
		for (int i = 0; i < platetypes.size(); i++) {
			ldo.add(platetypes.get(i).code, platetypes.get(i).name);
		}
		String[] roads = DBManager.getInstance().queryRoadname();
		affairsAddressDialog = new PublishSelectAffairsAddressDialog(this, roads, mTvAddress, "选择事故地点");
		String[] directs = DBManager.getInstance().getRoadDirection();
		affairsRoadDirectDialog = new PublishSelectAffairsAddressDialog(this, directs, mTvRoadDirect, getResources().getString(R.string.roaddirect_input));		
		// 获取天气信息
		weatherList = DBManager.getInstance().getWeatherTypes();
		affairsWeatherDialog = new PublishSelectAffairsTypeDialog(this, weatherList, mTvWeatherStatus, "选择天气状况");
		// 获取事故类型
		accTypeList = DBManager.getInstance().getAccTypes();
		affairsAccTypeDialog = new PublishSelectAffairsTypeDialog(this, accTypeList, mTvAccType, "选择事故类型");
		mPublishVioTime.setText(MDate.getDate());
		int[] carNum = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 };
		carNumDialog = new PublishSelectCarNumDialog(this, carNum, mTvCarNum, "选择车辆数目");
		carNumDialog.setSelection(0);
		addCarNumView(0);
		// 显示图片位
		mViewPagerAdapter = new PublishPictureAdapter(this, mVpAddPicture, photoPaths);
		mVpAddPicture.setAdapter(mViewPagerAdapter);
		if (photoPaths.size() > 0) {
			mVpAddPicture.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void initListener() {
		titleView.setLeftBackOneListener(R.drawable.back_normal, this);
		mIvAddPicture.setOnClickListener(this);
		mVpAddPicture.setOnClickListener(this);
		ll_publish_click_address.setOnClickListener(this);
		ll_publish_click_RoadDirect.setOnClickListener(this);
		ll_publish_click_weather.setOnClickListener(this);
		ll_publish_click_carnum.setOnClickListener(this);
		ll_publish_click_acctype.setOnClickListener(this);
		mPublishVioTime.setOnClickListener(this);
		carNumDialog.setCarNumOnClick(this);
		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Utils.hideSoftInputFromWindow(PublishRegistrationAccidentActivity.this);
				ai = new AccidentInfo();
				ai.carnum = Integer.valueOf(mTvCarNum.getText().toString());
				if (null != photoPaths && photoPaths.size() <= 0) {
					MsgToast.geToast().setMsg("请拍摄照片！");
					return;
				}

				if (mAlarmName.getText().toString() == null || mAlarmName.getText().toString().length() <= 0) {
					MsgToast.geToast().setMsg("请输入报警人员姓名");
					return;
				}
				if (!Tools.isPhoneNumber(mPhoneNum.getText().toString())) {
					MsgToast.geToast().setMsg("联系方式格式不正确！");
					return;
				}
				if (mTvAddress.getText().toString() == null || mTvAddress.getText().toString().length() <= 0) {
					MsgToast.geToast().setMsg("请选择事故地点");
					return;
				}

				if (mTvWeatherStatus.getText().toString() == null || mTvWeatherStatus.getText().toString().length() <= 0) {
					MsgToast.geToast().setMsg("请选择天气状况");
					return;
				}
				for (int i = 0; i < ai.carnum; i++) {
					if (mTvPlates.get(i).getText().toString() == null && mTvPlates.get(i).getText().toString().length() <= 0) {
						Toast.makeText(PublishRegistrationAccidentActivity.this, R.string.checkPlatType, Toast.LENGTH_SHORT).show();
						return;
					}
				}

				for (int i = 0; i < ai.carnum; i++) {
					View view = mAddViews.get(i);
					EditText mPlateNum = (EditText) view.findViewById(R.id.driver_plate_num);
					if (mPlateNum.getText().toString() == null || mPlateNum.getText().toString().length() <= 0) {
						MsgToast.geToast().setMsg("号牌号码不能为空,请输入或者识别号牌号码");
						return;
					}
				}
				for (int i = 0; i < ai.carnum; i++) {
					View view = mAddViews.get(i);
					EditText mPlateNum = (EditText) view.findViewById(R.id.driver_plate_num);
					String plateNum = mPlateNum.getText().toString();
					if (plateNum.length() != 7) {
						MsgToast.geToast().setMsg("号牌号码格式不正确，请确认");
						return;
					}

					if (!Tools.isCarnumberNO(plateNum)) {
						MsgToast.geToast().setMsg("号牌号码格式不正确，请确认");
						return;
					}
				}
				for (int i = 0; i < ai.carnum; i++) {
					View view = mAddViews.get(i);
					TextView mPublishPlateType = (TextView) view.findViewById(R.id.tv_publish_platetype);
					String plateType = mPublishPlateType.getText().toString();
					if (plateType == null || plateType.length() <= 0) {
						MsgToast.geToast().setMsg("请选择号牌种类");
						return;
					}
				}

				if (mTvAccType.getText().toString() == null || mTvAccType.getText().toString().length() <= 0) {
					MsgToast.geToast().setMsg("请选择事故类型");
					return;
				}
				
				if(mJoinPerson.getText().toString().length() <= 0 || mJoinPerson.getText().toString().equals("")){
					MsgToast.geToast().setMsg("请填写事故参与人员");
					return;
				}
				if (!mTvDied.getText().toString().equals("")) {
					ai.death = Integer.valueOf(mTvDied.getText().toString());
				} else {
					ai.death = 0;
				}
				if (!mTvHurt.getText().toString().equals("")) {
					ai.hurt = Integer.valueOf(mTvHurt.getText().toString());
				} else {
					ai.hurt = 0;
				}
				sDialog.setMessage(getResources().getString(R.string.dataSaving));
				sDialog.setTitle("");
				sDialog.setIndeterminate(true);
				sDialog.setCancelable(false);
				sDialog.show();
				infolist = "";

				ai.memo = mTvDesc.getText().toString();
				ai.realtime = mPublishVioTime.getText().toString();
				ai.roadname = mTvAddress.getText().toString();
				ai.roadlocation = mTvRoadLocation.getText().toString();
				ai.roaddirect = DBManager.getInstance().getRoadDirectionCodeByName(mTvRoadDirect.getText().toString());

				ai.user = SystemCfg.getPoliceName(PublishRegistrationAccidentActivity.this);
				if (photoPaths != null && photoPaths.size() > 0) {
					String pic0 = "";
					String pic1 = "";
					String pic2 = "";
					String pic3 = "";
					String pic4 = "";
					String pic5 = "";
					String pic6 = "";
					String pic7 = "";
					String pic8 = "";
					String pic9 = "";
					String pathString0 = photoPaths.size() > 0 ? photoPaths.get(0) : "";
					String pathString1 = photoPaths.size() > 1 ? photoPaths.get(1) : "";
					String pathString2 = photoPaths.size() > 2 ? photoPaths.get(2) : "";
					String pathString3 = photoPaths.size() > 3 ? photoPaths.get(3) : "";
					String pathString4 = photoPaths.size() > 4 ? photoPaths.get(4) : "";
					String pathString5 = photoPaths.size() > 5 ? photoPaths.get(5) : "";
					String pathString6 = photoPaths.size() > 6 ? photoPaths.get(6) : "";
					String pathString7 = photoPaths.size() > 7 ? photoPaths.get(7) : "";
					String pathString8 = photoPaths.size() > 8 ? photoPaths.get(8) : "";
					String pathString9 = photoPaths.size() > 9 ? photoPaths.get(9) : "";

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
					
					if (pathString4 != null && pathString4.length() > 1) {
						file = new File(pathString4);
						if (file.exists()) {
							tempFile = file;
							path = file.getAbsolutePath().split("vio/");
							if (path.length == 1) {
								dest = path[0];
							} else
								dest = path[0] + "vioback/" + path[1];

							file.renameTo(new File(dest));
							pic4 = dest;
							tempFile.delete();
							Log.d("SendPicData", tempFile + " has been deleted!");
						}
					}
					if (pathString5 != null && pathString5.length() > 1) {
						file = new File(pathString5);
						if (file.exists()) {
							tempFile = file;
							path = file.getAbsolutePath().split("vio/");
							if (path.length == 1) {
								dest = path[0];
							} else
								dest = path[0] + "vioback/" + path[1];
							file.renameTo(new File(dest));
							pic5 = dest;
							tempFile.delete();
							Log.d("SendPicData", file + " has been deleted!");
						}
					}
					if (pathString6 != null && pathString6.length() > 1) {
						file = new File(pathString6);
						if (file.exists()) {
							tempFile = file;
							path = file.getAbsolutePath().split("vio/");
							if (path.length == 1) {
								dest = path[0];
							} else
								dest = path[0] + "vioback/" + path[1];
							file.renameTo(new File(dest));
							pic6 = dest;
							tempFile.delete();
							Log.d("SendPicData", file + " has been deleted!");
						}
					}
					if (pathString7 != null && pathString7.length() > 1) {
						file = new File(pathString7);
						if (file.exists()) {
							tempFile = file;
							path = file.getAbsolutePath().split("vio/");
							if (path.length == 1) {
								dest = path[0];
							} else
								dest = path[0] + "vioback/" + path[1];
							file.renameTo(new File(dest));
							pic7 = dest;
							tempFile.delete();
							Log.d("SendPicData", file + " has been deleted!");
						}
					}
					
					if (pathString8 != null && pathString8.length() > 1) {
						file = new File(pathString8);
						if (file.exists()) {
							tempFile = file;
							path = file.getAbsolutePath().split("vio/");
							if (path.length == 1) {
								dest = path[0];
							} else
								dest = path[0] + "vioback/" + path[1];
							file.renameTo(new File(dest));
							pic8 = dest;
							tempFile.delete();
							Log.d("SendPicData", file + " has been deleted!");
						}
					}
					if (pathString9 != null && pathString9.length() > 1) {
						file = new File(pathString9);
						if (file.exists()) {
							tempFile = file;
							path = file.getAbsolutePath().split("vio/");
							if (path.length == 1) {
								dest = path[0];
							} else
								dest = path[0] + "vioback/" + path[1];
							file.renameTo(new File(dest));
							pic9 = dest;
							tempFile.delete();
							Log.d("SendPicData", file + " has been deleted!");
						}
					}
					ai.pic1 = pic0;
					ai.pic2 = pic1;
					ai.pic3 = pic2;
					ai.pic4 = pic3;
					ai.pic5 = pic4;
					ai.pic6 = pic5;
					ai.pic7 = pic6;
					ai.pic8 = pic7;
					ai.pic9 = pic8;
					ai.pic10 = pic9;
				}
				ai.isupfile = 0;
				ai.acctype = affairsAccTypeDialog.getSelectedItemPosition();
				ai.weather = affairsWeatherDialog.getSelectedItemPosition();
				ai.caller = mAlarmName.getText().toString();
				ai.phone = mPhoneNum.getText().toString();
				ai.joinlist = mJoinPerson.getText().toString();
				ai.id = XiangXunApplication.getInstance().getDevId() + System.currentTimeMillis();
				// 保存事故车辆信息
				for (int i = 0; i < ai.carnum; i++) {
					AccidentCar ac = new AccidentCar();
					ac.setOwnerid(ai.id);
					ac.setPlatenum(mTvPlateInfo.get(i).getText().toString());
					ac.setPlatetype(typeDialogs.get(i).getSelectedItemPosition());
					DBManager.getInstance().add(ac);
				}

				for (int i = 0; i < ai.carnum; i++) {
					String plateNum = mTvPlateInfo.get(i).getText().toString();
					View view = mAddViews.get(i);
					TextView mPublishPlateType = (TextView) view.findViewById(R.id.tv_publish_platetype);
					ListDataItem listDataItem = (ListDataItem) mPublishPlateType.getTag();
					String plateId = listDataItem != null ? listDataItem.getid() : "";
					infolist += plateNum + "," + plateId;
					infolist += ";";
				}

				ai.infolist = infolist;
				// 将事故数据添加至数据库
				DBManager.getInstance().add(ai);
				addCarInfo.removeAllViews();
				setResult(Activity.RESULT_OK);
				sDialog.dismiss();
				XiangXunApplication.getInstance().getMainService().start(ConstantStatus.UPLOAD_ACCIDENTDATA);
				Toast.makeText(PublishRegistrationAccidentActivity.this, "上传成功！", Toast.LENGTH_SHORT).show();
				onBackPressed();
			}
		});
	}
	
	@SuppressWarnings("deprecation")
	private void showSelectAddPath() {
		LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		View popupwindowView = layoutInflater.inflate(R.layout.add_picture_popup_window, null);
		TextView tvAddFromCamera = (TextView) popupwindowView.findViewById(R.id.tv_add_picture_from_camera);
		TextView tvAddFromAlbum = (TextView) popupwindowView.findViewById(R.id.tv_add_picture_from_album);
		TextView tvAddCancle = (TextView) popupwindowView.findViewById(R.id.tv_add_picture_cancle);
		tvAddFromCamera.setOnClickListener(PublishRegistrationAccidentActivity.this);
		tvAddFromAlbum.setOnClickListener(PublishRegistrationAccidentActivity.this);
		tvAddCancle.setOnClickListener(PublishRegistrationAccidentActivity.this);
		mPopupWindow = new PopupWindow(popupwindowView, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		mPopupWindow.setContentView(popupwindowView);
		mPopupWindow.setFocusable(false);
		mPopupWindow.setOutsideTouchable(false);
		mPopupWindow.setWidth(LayoutParams.MATCH_PARENT);
		mPopupWindow.setHeight(LayoutParams.WRAP_CONTENT);
		mPopupWindow.setAnimationStyle(R.style.Add_Picture_AnimationFade);
		mPopupWindow.showAtLocation(popupwindowView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
	}

	private void addCarNumView(int tag) {
		final View view = getLayoutInflater().inflate(R.layout.publish_add_carnum_layout, null);
		final EditText mPlateNum = (EditText) view.findViewById(R.id.driver_plate_num);
		final TextView mPublishPlateType = (TextView) view.findViewById(R.id.tv_publish_platetype);
		final TextView mTvPlate = (TextView) view.findViewById(R.id.tv_plate);
		if(Integer.parseInt(mTvCarNum.getText().toString()) > 1){
			mTvPlate.setText("号牌号码" + (tag+1));
		}
		LinearLayout mPublishClickPlateType = (LinearLayout) view.findViewById(R.id.ll_publish_click_platetype);
		LinearLayout mLlPlateSearch = (LinearLayout) view.findViewById(R.id.ll_driver_plate_num);
		if (ldo.getlist().size() > 0) {
			ldo.getlist().clear();
		}
		ArrayList<Type> platetypes = DBManager.getInstance().getPlateTypes();
		for (int i = 0; i < platetypes.size(); i++) {
			ldo.add(platetypes.get(i).code, platetypes.get(i).name);
		}
		final PublishSelectAffairsPlateTypeDialog plateTypeDialog = new PublishSelectAffairsPlateTypeDialog(this, ldo.getlist(), mPublishPlateType);
		plateTypeDialog.setSelectCapture(getResources().getString(R.string.def_platetype));
		// mPlateNum.setTag(tag);
		view.setTag(tag);
		mPublishClickPlateType.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				plateTypeDialog.show();
			}
		});
		mLlPlateSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btntag = (Integer) view.getTag();
				Intent intentPlate = wtUtils.StartPlateRecog(PublishRegistrationAccidentActivity.this);
				startActivityForResult(intentPlate, 3);
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
				
				if(s.length() == 7){
					for (int i = 0; i < Integer.parseInt(mTvCarNum.getText().toString()); i++) {
						if (i != Integer.parseInt(view.getTag().toString()) && mTvPlateInfo.get(i) != null && mTvPlates.get(i) != null) {
							if (mTvPlateInfo.get(i).getText().toString().equals(s.toString().toUpperCase())) {
								MsgToast.geToast().setMsg("当前号牌号码与第" + (i + 1) + "辆车重复，请重新输入");
								mPlateNum.setText("");
								mPublishPlateType.setText("选择号牌种类");
								return;
							}
							
						}
					}
				}
				String searchWord = s.toString().toUpperCase().trim();
				if (s.toString().length() == 1 && searchWord.length() == 0) {
					mPlateNum.setText(s.toString().trim());
				}

				mPlateNum.removeTextChangedListener(this);// 解除文字改变事件
				index = mPlateNum.getSelectionStart();// 获取光标位置
				mPlateNum.setText(s.toString().toUpperCase());
				mPlateNum.setSelection(index);// 重新设置光标位置
				mPlateNum.addTextChangedListener(this);// 重新绑定事件
				Utils.chkOtherPlate(mContext, mHander, mPlateNum, searchWord);
			}
		});
		mTvPlates.add(mPublishPlateType);
		mTvPlateInfo.add(mPlateNum);
		mAddViews.add(view);
		typeDialogs.add(plateTypeDialog);
		addCarInfo.addView(view, tag);
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
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.title_view_back_img:
			onBackPressed();
			break;
		case R.id.iv_publish_add_picture:
			//必要时隐藏输入法键盘
			Utils.hideSoftInputFromWindow(PublishRegistrationAccidentActivity.this);
			if (mPopupWindow == null || !mPopupWindow.isShowing()) {
				showSelectAddPath(); // 选择添加图片方式
				mVSupernatant.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.tv_add_picture_from_camera:
			Intent intentCamera = new Intent(PublishRegistrationAccidentActivity.this, CameraActivity.class);
			intentCamera.putExtra("size", photoPaths.size());
			intentCamera.setAction("publishFourPhotosAccident");
			startActivityForResult(intentCamera, 1);
			mPopupWindow.dismiss();
			mVSupernatant.setVisibility(View.GONE);
			break;
		case R.id.tv_add_picture_from_album:
			Intent intentAlbum = new Intent();
			intentAlbum.putExtra("size", photoPaths.size());
			intentAlbum.setAction("publishFourPhotosAccident");
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
		case R.id.ll_publish_click_address:
			affairsAddressDialog.show();
			break;
		case R.id.ll_publish_click_road_direct:
			affairsRoadDirectDialog.show();
			break;
		case R.id.ll_publish_click_weather:
			affairsWeatherDialog.show();
			break;
		case R.id.ll_publish_click_acctype:
			affairsAccTypeDialog.show();
			break;
		case R.id.ll_publish_click_carnum:
			carNumDialog.show();
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 3:
			if (resultCode == Activity.RESULT_OK) {
				if (null != data) {
					if (null != data.getExtras().getString("platenum")) {
						View view = mAddViews.get(btntag);
						EditText mPlateNum = (EditText) view.findViewById(R.id.driver_plate_num);
						mPlateNum.setText(data.getExtras().getString("platenum"));
						MsgToast.geToast().setMsg(R.string.plateNotifyCompleted);
					} else {
						MsgToast.geToast().setMsg(R.string.plateNotifyError);
					}
				}
			}
			break;
		case 1:
			if (resultCode == Activity.RESULT_OK) {
				if (data != null) {
					@SuppressWarnings("unchecked")
					List<String> photos = (List<String>) data.getSerializableExtra("camera_picture");
					photoPaths.addAll(photos);
					// 只取前10张图片
					if (photoPaths.size() > 10) {
						photoPaths = photoPaths.subList(0, 10);
						MsgToast.geToast().setMsg("最多添加10张照片噢");
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
				if (photoPaths.size() > 10) {
					photoPaths = photoPaths.subList(0, 10);
					MsgToast.geToast().setMsg("最多添加10张照片噢");
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
		case 11:
			getApplicationContext();
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
	public void carNumOnClick(int carNum) {
		/*
		 * mAddViews.clear(); mTvPlates.clear(); mTvPlateInfo.clear();
		 * typeDialogs.clear(); addCarInfo.removeAllViews();
		 * addCarInfo.removeAllViewsInLayout(); if (carNum > 0) { for (int i =
		 * 0; i < carNum; i++) { addCarNumView(i); } }
		 */

		int count = addCarInfo.getChildCount();
		if (carNum < count) {
			addCarInfo.removeViews(carNum, count - carNum);
			for (int i = count - 1; i >= carNum; i--) {
				mTvPlateInfo.remove(i);
				mTvPlates.remove(i);
				mAddViews.remove(i);
				typeDialogs.remove(i);
			}
		}

		if (carNum >= count) {
			for (int i = 0; i < carNum - count; i++)
				addCarNumView(count + i);
		}
	}

}
