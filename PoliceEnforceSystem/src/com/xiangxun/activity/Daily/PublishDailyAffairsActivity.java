package com.xiangxun.activity.Daily;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xiangxun.activity.CameraActivity;
import com.xiangxun.activity.PhotoSelectActivity;
import com.xiangxun.activity.R;
import com.xiangxun.adapter.PublishPictureAdapter;
import com.xiangxun.app.BaseActivity;
import com.xiangxun.app.XiangXunApplication;
import com.xiangxun.bean.MDate;
import com.xiangxun.bean.PatorlInfo;
import com.xiangxun.bean.ResultData;
import com.xiangxun.bean.RoadInfo;
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
import com.xiangxun.widget.PublishSelectAffairsTypeDialog;
import com.xiangxun.widget.RoadEditView;
import com.xiangxun.widget.TitleView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @package: com.xiangxun.activity
 * @ClassName: DailyAffairsActivity.java
 * @Description: 发布日常事务页面
 * @date: 2015-7-29 上午10:46:18
 */
public class PublishDailyAffairsActivity extends BaseActivity implements OnClickListener, DeletePictureInterface {
	private PublishDailyAffairsActivity mContext = null;
	private TitleView titleView;
	private EditText edtmemo;
	private ProgressDialog sDialog;
	private PatorlInfo pi;
	private TextView mTime;
	private Button btnPage4Save;
	private List<String> photoPaths;
	private PublishPictureAdapter mViewPagerAdapter = null;
	// 添加图片
	private ImageView mIvAddPicture = null;
	private NoViewPager mVpAddPicture = null;
	private ArrayList<Type> dts = new ArrayList<Type>();
	private String[] directions;
	private TextView mTvType;
	private LinearLayout ll_publish_click_type;
	private PublishSelectAffairsTypeDialog affairsTypeDialog;
	private RoadEditView mTvAddress;
//	private TextView mTvAddress;
//	private LinearLayout ll_publish_click_address;
//	private PublishSelectAffairsAddressDialog affairsAddressDialog;

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

	// 浮层
	private View mVSupernatant = null;
	private PopupWindow mPopupWindow = null;

	private ResultData.RoadExt roadExt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.publish_daily_affairs_layout);
		mContext = PublishDailyAffairsActivity.this;
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
		titleView.setTitle(R.string.rcqw);
		mTime = (TextView) findViewById(R.id.date);
		mTime.setText(MDate.getDate());
		edtmemo = (EditText) findViewById(R.id.dutydiscribe);
		btnPage4Save = (Button) findViewById(R.id.upload);

		mIvAddPicture = (ImageView) findViewById(R.id.iv_publish_add_picture);
		mVpAddPicture = (NoViewPager) findViewById(R.id.vp_publish_add_pictures);
		mTvType = (TextView) findViewById(R.id.tv_publish_type);
		ll_publish_click_type = (LinearLayout) findViewById(R.id.ll_publish_click_type);
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

		mVSupernatant = (View) findViewById(R.id.v_supernatant_background);
	}

	@Override
	public void initListener() {
		mTime.setOnClickListener(this);
		btnPage4Save.setOnClickListener(this);
		ll_publish_click_type.setOnClickListener(this);
		ll_publish_click_RoadDirect.setOnClickListener(this);

		ll_publish_click_RoadStep.setOnClickListener(this);
		mIvAddPicture.setOnClickListener(this);
		mVpAddPicture.setOnClickListener(this);

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
						SystemCfg.setRoadHistory(PublishDailyAffairsActivity.this, roadname);
						showRoadStep();
					} else
						HideRoadStep();
				} else
					HideRoadStep();

			}
		});
	}

	@Override
	public void initData() {
		photoPaths = new ArrayList<String>();
		sDialog = new ProgressDialog(this);
		dts = DBManager.getInstance().getDutyTypes();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		String dutytypes[] = new String[dts.size()];
		for (int i = 0; i < dutytypes.length; i++) {
			dutytypes[i] = dts.get(i).name;
		}
		affairsTypeDialog = new PublishSelectAffairsTypeDialog(this, dts, mTvType, "选择勤务类型");
		directions = DBManager.getInstance().getRoadDirection();
		affairsRoadDirectDialog = new PublishSelectAffairsAddressDialog(this, directions, mTvRoadDirect, getResources().getString(R.string.roaddirect_input));

		// 显示图片位
		mViewPagerAdapter = new PublishPictureAdapter(this, mVpAddPicture, photoPaths);
		mVpAddPicture.setAdapter(mViewPagerAdapter);
		if (photoPaths.size() > 0) {
			mVpAddPicture.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onDestroy() {
		setResult(Activity.RESULT_CANCELED);
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		if(mPopupWindow != null){
			if(mPopupWindow.isShowing()){
				mPopupWindow.dismiss();
				mVSupernatant.setVisibility(View.GONE);
			} else super.onBackPressed();
		} else super.onBackPressed();
		
	}

	private boolean saveDB() {
		String pic0 = "";
		String pic1 = "";
		String pic2 = "";
		String pic3 = "";
		pi = new PatorlInfo();
		sDialog.setMessage(getResources().getString(R.string.dataSaving));
		sDialog.setTitle("");
		sDialog.setIndeterminate(true);
		sDialog.setCancelable(false);
		sDialog.show();
		pi.isupfile = 0;
		pi.datetime = mTime.getText().toString();
		String text = edtmemo.getText().toString();
		if (text.contains("'")) {
			text = text.replace("'", ",");
		}

		pi.memo = text;
		pi.patorltype = affairsTypeDialog.getSelectedItemPosition();
		pi.roadname = mTvAddress.getTag().toString().substring(6);

		pi.roaddirect = mTvRoadDirect.getText().toString();

		if (mTvRoadStep.getText().toString().length() > 0 )
			pi.roadlocation = pi.roadlocation + mTvRoadStep.getText().toString();
		if (mTvRoadKm.getText().toString().length() > 0)
			pi.roadlocation = pi.roadlocation + mTvRoadKm.getText().toString() + "公里";
		if (mTvRoadMi.getText().toString().length() > 0)
			pi.roadlocation = pi.roadlocation + mTvRoadMi.getText().toString() + "米";
		pi.roadlocation += mTvRoadZhan.getText().toString();

		pi.user = SystemCfg.getPoliceName(XiangXunApplication.getInstance());
		pi.id = XiangXunApplication.getInstance().getDevId() + System.currentTimeMillis();
		long id = DBManager.getInstance().getLastestPatorl() + System.currentTimeMillis();
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		pi.picsole = tm.getDeviceId() + id;
		if (photoPaths != null && photoPaths.size() > 0) {
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
			pi.pic1 = pic0;
			pi.pic2 = pic1;
			pi.pic3 = pic2;
			pi.pic4 = pic3;
		}
		DBManager.getInstance().add(pi);

		affairsTypeDialog.setSelection(0);
		edtmemo.setText("");
		sDialog.dismiss();
		MsgToast.geToast().setMsg("上传成功！");
		XiangXunApplication.getInstance().getMainService().start(ConstantStatus.UPLOAD_DUTYDATA);
		return true;
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK)
			switch (requestCode) {
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
					}
					mViewPagerAdapter.notifyDataSetChanged();
					String picTime = photoPaths.get(0).substring(photoPaths.get(0).lastIndexOf("/") + 4, photoPaths.get(0).lastIndexOf("/") + 18);
					String picTimeFormat = Utils.getConsumerNoteTime(picTime);
					mTime.setText(picTimeFormat);
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
					}
					mViewPagerAdapter.notifyDataSetChanged();
					String picTime = photoPaths.get(0).substring(photoPaths.get(0).lastIndexOf("/") + 4, photoPaths.get(0).lastIndexOf("/") + 18);
					String picTimeFormat = Utils.getConsumerNoteTime(picTime);
					mTime.setText(picTimeFormat);
				}
				break;
			}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.iv_publish_add_picture:
			Utils.hideSoftInputFromWindow(PublishDailyAffairsActivity.this);
			if (mPopupWindow == null || !mPopupWindow.isShowing()) {
				showSelectAddPath(); // 选择添加图片方式
				mVSupernatant.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.tv_add_picture_from_camera:
			Intent intentCamera = new Intent(PublishDailyAffairsActivity.this, CameraActivity.class);
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
		case R.id.date:
			Tools.showDateTimePicker(this, mTime);
			break;
		case R.id.upload:
			Utils.hideSoftInputFromWindow(PublishDailyAffairsActivity.this);
			if (mTvType.getText().toString() == null || mTvType.getText().toString().length() <= 0) {
				MsgToast.geToast().setLongMsg("请选择勤务类型!");
				return;
			} else if (mTvAddress.getText().toString() == null || mTvAddress.getText().toString().length() <= 0) {
				MsgToast.geToast().setLongMsg("请选择执勤地点!");
				return;
			} else if (mTvAddress.getRoadCode() == null) {
				MsgToast.geToast().setLongMsg("道路名称错误!");
				return;
			} else if (!Utils.CheckRoadExt(mTvAddress.getText().toString(), roadExt)) {
				MsgToast.geToast().setLongMsg("道路名称错误!");
				return;
			} else if (edtmemo.getText().toString().equals("")) {
				MsgToast.geToast().setLongMsg(R.string.descNotNull);
				return;
			} else if (null == photoPaths || photoPaths.size() == 0) {
				MsgToast.geToast().setLongMsg("请拍摄照片！");
				return;
			} else {
				saveDB();
				onBackPressed();
			}
			break;
		case R.id.title_view_back_img:
			onBackPressed();
			break;
		case R.id.ll_publish_click_type:
			if (affairsTypeDialog == null) {
				affairsTypeDialog = new PublishSelectAffairsTypeDialog(this, dts, mTvType, "选择勤务类型");
			}
			affairsTypeDialog.show();
			break;
		case R.id.ll_publish_click_road_direct:
			affairsRoadDirectDialog.show();
			break;
		case R.id.ll_publish_click_road_step:
			if (affairsRoadStepDialog != null)
				affairsRoadStepDialog.show();
			break;
		}
	}

	/**
	 * //必要时隐藏输入法键盘
	 */
	private void HideSoftInputFromWindow() {
		//必要时隐藏输入法键盘
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	@Override
	public void getPictures(List<String> photos) {
		photoPaths = photos;
		if (photoPaths.size() == 0) {
			mVpAddPicture.setVisibility(View.GONE);
		}
		mViewPagerAdapter.notifyDataSetChanged();
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
