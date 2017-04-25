package com.xiangxun.activity.Daily;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
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
	private String[] roads;
	private String[] directions;
	private TextView mTvType;
	private LinearLayout ll_publish_click_type;
	private PublishSelectAffairsTypeDialog affairsTypeDialog;
	private TextView mTvAddress;
	private LinearLayout ll_publish_click_address;
	private PublishSelectAffairsAddressDialog affairsAddressDialog;
	
	private EditText mTvRoadLocation;
	private TextView mTvRoadDirect;
	private LinearLayout ll_publish_click_RoadDirect;
	private PublishSelectAffairsAddressDialog affairsRoadDirectDialog;
	// 浮层
	private View mVSupernatant = null;
	private PopupWindow mPopupWindow = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.publish_daily_affairs_layout);
		mContext = PublishDailyAffairsActivity.this;
		initView();
		initData();
		initListener();
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
		mTvAddress = (TextView) findViewById(R.id.tv_publish_address);
		mTvRoadLocation = (EditText) findViewById(R.id.tv_publish_road_location);
		mTvRoadDirect = (TextView) findViewById(R.id.tv_publish_road_direct);
		ll_publish_click_address = (LinearLayout) findViewById(R.id.ll_publish_click_address);
		ll_publish_click_RoadDirect = (LinearLayout) findViewById(R.id.ll_publish_click_road_direct);

		LinearLayout layout_roadlocation = (LinearLayout) findViewById(R.id.layout_roadlocation);
		LinearLayout layout_roaddirect = (LinearLayout) findViewById(R.id.layout_roaddirect);
		if (getResources().getString(R.string.workroad_city).equalsIgnoreCase(SystemCfg.getWorkRoad(this))) {
			layout_roadlocation.setVisibility(View.GONE);
			layout_roaddirect.setVisibility(View.GONE);
		}		
		mVSupernatant = findViewById(R.id.v_supernatant_background);
	}

	@Override
	public void initListener() {
		mTime.setOnClickListener(this);
		btnPage4Save.setOnClickListener(this);
		ll_publish_click_type.setOnClickListener(this);
		ll_publish_click_address.setOnClickListener(this);
		ll_publish_click_RoadDirect.setOnClickListener(this);
		mIvAddPicture.setOnClickListener(this);
		mVpAddPicture.setOnClickListener(this);
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
		roads = DBManager.getInstance().queryRoadname();
		directions = DBManager.getInstance().getRoadDirection();
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
		pi.roadname = mTvAddress.getTag().toString();
		pi.roadlocation = mTvRoadLocation.getText().toString();
		pi.roaddirect =  DBManager.getInstance().getRoadDirectionCodeByName(mTvRoadDirect.getText().toString());
		
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
		case R.id.ll_publish_click_address:
			if (affairsAddressDialog == null) {
				affairsAddressDialog = new PublishSelectAffairsAddressDialog(this, roads, mTvAddress, "选择执勤地点");
			}
			affairsAddressDialog.show();
			break;
		case R.id.ll_publish_click_road_direct:
			if (affairsRoadDirectDialog == null) {
				affairsRoadDirectDialog = new PublishSelectAffairsAddressDialog(this, directions, mTvRoadDirect, getResources().getString(R.string.roaddirect_input));
			}
			affairsRoadDirectDialog.show();
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
}
