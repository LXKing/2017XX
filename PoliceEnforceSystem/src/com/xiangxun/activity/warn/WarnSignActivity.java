package com.xiangxun.activity.warn;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.gson.Gson;
import com.xiangxun.activity.R;
import com.xiangxun.app.BaseActivity;
import com.xiangxun.app.XiangXunApplication;
import com.xiangxun.bean.MDate;
import com.xiangxun.bean.VehicleInfoX;
import com.xiangxun.bean.WarnAck;
import com.xiangxun.bean.WarnData;
import com.xiangxun.cfg.SystemCfg;
import com.xiangxun.common.DcHttpClient;
import com.xiangxun.db.DBManager;
import com.xiangxun.request.ApiUrl;
import com.xiangxun.request.DcNetWorkUtils;
import com.xiangxun.util.ConstantStatus;
import com.xiangxun.widget.MsgToast;
import com.xiangxun.widget.PublishSelectAffairsAddressDialog;
import com.xiangxun.widget.PublishSelectAffairsAddressDialog.selectVioTypeOnClick;
import com.xiangxun.widget.TitleView;

import java.io.Serializable;

import static com.xiangxun.util.Utils.GetCarStatus;

;

/**
 * @package: com.xiangxun.activity
 * @ClassName: WarnSignActivity.java
 * @Description: 预警签收
 * @date: 2015-7-29 上午10:46:18
 */
public class WarnSignActivity extends BaseActivity implements OnClickListener, selectVioTypeOnClick {
	private WarnSignActivity mContext = null;
	private TitleView titleView;
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
	private PublishSelectAffairsAddressDialog isOkDialog;
	private PublishSelectAffairsAddressDialog isDoDialog;
	private LinearLayout mllIsOk;
	private LinearLayout mllIsDo;
	private TextView mTvIsOk;
	private TextView mTvIsDo;
	private ImageView mIvIsOk;
	private ImageView mIvIsDo;
	private LinearLayout mllbtns;

	private WarnData warnData;
	private boolean webViewOnclick = false;

	private boolean warnSignView = false;

	private Handler mUIHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case ConstantStatus.SEARCH_WARNPIC_SUCCESS:
					imageUrl = ApiUrl.getHost() + (String) msg.obj;
					warnData.setImageurl(imageUrl);
					DBManager.getInstance().WarnImage(warnData);
					DBManager.getInstance().WarnMessageUpdate(warnData.getYjxh(), "data", XiangXunApplication.getGson().toJson(warnData));
					ShowWarnPic();
					//ShowWarnPicWeb();
					break;
				case ConstantStatus.SEARCH_WARNPIC_FAIL:
					vfDevice.setDisplayedChild(1);
					break;
				case ConstantStatus.VEHICLE_SEARCH_SUCCESS:
					VehicleInfoX vehicleInfo = (VehicleInfoX) msg.obj;
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
		setContentView(R.layout.warn_sign_layout);
		mContext = WarnSignActivity.this;

		Intent intent = getIntent();
		warnData = (WarnData)intent.getSerializableExtra("warndata");
		// 检测是否预警签收浏览
		if (-1 != warnData.getIsOk())
			warnSignView = true;

		initView();
		initData();
		initListener();
	}

	@Override
	public void initView() {
		titleView = (TitleView) findViewById(R.id.tv_comm_title);
		titleView.setLeftBackOneListener(R.drawable.back_normal, this);
		if (warnSignView)
			titleView.setTitle("预警签收详情");
		else
			titleView.setTitle(R.string.warn_sign);

		mWarnContent = (TextView) findViewById(R.id.warn_content);
		devicePicView = (TextView) findViewById(R.id.tv_device_pic);
		deviceInfoView = (TextView) findViewById(R.id.tv_device_info);

		ll_search_device  = (LinearLayout) findViewById(R.id.ll_search_device);
		vfDevice = (ViewFlipper) findViewById(R.id.vf_search_device);

		mWarnImage = (ImageView) findViewById(R.id.wran_image);
		mWarnWeb = (WebView) findViewById(R.id.wran_web);
		mWarnDevice = (TextView) findViewById(R.id.warn_device);

		mllIsOk = (LinearLayout) findViewById(R.id.ll_is_ok);
		mTvIsOk = (TextView) findViewById(R.id.tv_is_ok);
		mllIsDo = (LinearLayout) findViewById(R.id.ll_is_do);
		mTvIsDo = (TextView) findViewById(R.id.tv_is_do);
		mIvIsOk = (ImageView) findViewById(R.id.iv_is_ok);
		mIvIsDo = (ImageView) findViewById(R.id.iv_is_do);

		mllbtns =  (LinearLayout) findViewById(R.id.ll_btn);
		if (warnSignView) {
			mIvIsOk.setVisibility(View.GONE);
			mIvIsDo.setVisibility(View.GONE);
			mllbtns.setVisibility(View.GONE);
		}
		btnCancel = (Button) findViewById(R.id.btn_cancel);
		btnSign = (Button) findViewById(R.id.btn_sign);
	}

	@Override
	public void initListener() {
		mWarnImage.setOnClickListener(this);

		if (warnSignView == false) {
			mllIsOk.setOnClickListener(this);
			mllIsDo.setOnClickListener(this);
			btnCancel.setOnClickListener(this);
			btnSign.setOnClickListener(this);
		}

		devicePicView.setOnClickListener(this);
		deviceInfoView.setOnClickListener(this);
		isOkDialog.setVioTypeOnClick(this);
		isDoDialog.setVioTypeOnClick(this);
	}

	@Override
	public void initData() {
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

		String[] okStrs = {"有效", "无效"};
		String[] doStrs = {"是", "否"};

		isOkDialog = new PublishSelectAffairsAddressDialog(this, okStrs, mTvIsOk, "选择有效性");
		isDoDialog = new PublishSelectAffairsAddressDialog(this, doStrs, mTvIsDo, "选择是否出警");

		imageUrl = warnData.getImageurl();

		if (-1 != warnData.getIsOk()) {
			if (1 == warnData.getIsOk())
				isOkDialog.setSelection(0);
			else
				isOkDialog.setSelection(1);

			if (1 == warnData.getIsDo())
				isDoDialog.setSelection(0);
			else
				isDoDialog.setSelection(1);
		} else {
			isOkDialog.setSelection(0);
			isDoDialog.setSelection(1);
		}
	}

	private String getIsNull(String content) {
		return TextUtils.isEmpty(content) ? "暂无" : content;
	}

	private void ShowDeviceInfo(VehicleInfoX vehicleInfo) {
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
					intent.setClass(WarnSignActivity.this, WarnImageActivity.class);
					startActivity(intent);
				}
			});

			vfDevice.setDisplayedChild(2);
		} else
			vfDevice.setDisplayedChild(1);
	}

	private void ShowWarnPicWeb() {
		if (imageUrl != null && (imageUrl.endsWith(".png") || imageUrl.endsWith(".gif") || imageUrl.endsWith(".jpg"))){
			mWarnWeb.loadUrl(imageUrl);
			mWarnWeb.setWebViewClient(new WebViewClient());
			WebSettings webSet = mWarnWeb.getSettings();

			webSet.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
			webSet.setLoadWithOverviewMode(true);

			webSet.setSupportZoom(true);
			webSet.setUseWideViewPort(true);
			webSet.setLoadWithOverviewMode(true);
//			webSet.setBuiltInZoomControls(true);
//			webSet.setDisplayZoomControls(false);
			mWarnWeb.setVisibility(View.VISIBLE);

			mWarnWeb.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View arg0, MotionEvent event) {
					switch (event.getAction()){
						case MotionEvent.ACTION_DOWN:
							webViewOnclick = true;
							break;
						case MotionEvent.ACTION_MOVE:
							break;
						case MotionEvent.ACTION_UP:
							if (webViewOnclick) {
								webViewOnclick = false;

								Intent intent = new Intent();
								intent.putExtra("msgURL", imageUrl);
								intent.setClass(WarnSignActivity.this, WarnImageActivity.class);
								startActivity(intent);
							}
							break;
						default:
							webViewOnclick = false;
					}
					return true;
				}
			});
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		if (imageUrl != null && imageUrl != "") {
			Intent intent = new Intent();
			intent.putExtra("warndata", (Serializable) warnData);
			setResult(ConstantStatus.RESULTCODE_SIGNED, intent);
		}
		super.onBackPressed();
	}

	private boolean signWarn() {
		if (checkData()) {
			warnData.setIsUpFile(0);
			warnData.setIsOk("有效".equals(mTvIsOk.getText().toString()) ? 1 : 0);
			warnData.setIsDo("是".equals(mTvIsDo.getText().toString()) ? 1 : 0);
			if (1 == warnData.getIsDo())
				warnData.setIsAck(0);

			WarnAck ackInfo = new WarnAck();
			ackInfo.isupfile = 0;
			ackInfo.yjxh = warnData.getYjxh();	//预警序号
			ackInfo.ywlx = "1";	//业务类型 		1-签收；	2-反馈；3-签收反馈
			ackInfo.qsjg = "有效".equals(mTvIsOk.getText().toString()) ? "1" : "2";	//签收结果 		1-有效；2-无效
			ackInfo.sfcjlj ="是".equals(mTvIsDo.getText().toString()) ? "1" : "0";	//是否出警拦截	0否1是
			ackInfo.czdw = SystemCfg.getDepartmentCode(this);	//处置单位
			ackInfo.czr = SystemCfg.getUpdateName(this);	//处置民警
			ackInfo.czsj = MDate.getDate();					//处置时间

//			DBManager.getInstance().add(ackInfo);
			DBManager.getInstance().WarnMessageUpdate(warnData.getYjxh(), "data", XiangXunApplication.getGson().toJson(warnData));
			DBManager.getInstance().WarnMessageUpdate(warnData.getYjxh(), "step", "1");
			DBManager.getInstance().WarnMessageUpdate(warnData.getYjxh(), "sign", XiangXunApplication.getGson().toJson(ackInfo));

//			DBManager.getInstance().WarnSign(warnData);
//			MsgToast.geToast().setMsg("上传成功！");
			XiangXunApplication.getInstance().getMainService().start(ConstantStatus.UPLOAD_WARNACKDATA);
			Intent intent = new Intent();
			intent.putExtra("warndata", (Serializable) warnData);
			setResult(ConstantStatus.RESULTCODE_SIGNED, intent);
			return true;
		}
		return false;
	}

	private boolean checkData() {
		if (mTvIsOk.getText().length() < 1) {
			MsgToast.geToast().setMsg("有效性不能为空");
			return false;
		}
		if (mTvIsDo.getText().length() < 1) {
			MsgToast.geToast().setMsg("是否出警不能为空");
			return false;
		}
		return true;
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.title_view_back_img:
		case R.id.btn_cancel:
			onBackPressed();
			break;
		case R.id.btn_sign:
			if (signWarn())
				onBackPressed();
			break;
		case R.id.ll_is_ok:
			isOkDialog.show();
			break;
		case R.id.ll_is_do:
			isDoDialog.show();
			break;
		case R.id.tv_device_pic:
			mWarnDevice.setVisibility(View.GONE);
			if (imageUrl == null || imageUrl.equals("")) {
				vfDevice.setDisplayedChild(0);
				DcNetWorkUtils.searchWarnPic(this, warnData.getGcxh(), mUIHandler);
			} else {
				ShowWarnPic();
				//ShowWarnPicWeb();
			}
			ll_search_device.setVisibility(View.VISIBLE);
			break;
		case R.id.tv_device_info:
			mWarnImage.setVisibility(View.GONE);
			mWarnWeb.setVisibility(View.GONE);
			vfDevice.setDisplayedChild(0);
			DcNetWorkUtils.searchVehicleInfo(this, warnData.getHphm(), warnData.getHpzl(), mUIHandler);
			ll_search_device.setVisibility(View.VISIBLE);
			break;
		case R.id.wran_image:
			break;
		}
	}

	private static Gson gson;
	private static Gson getGson() {
		if (gson == null) {
			gson = new Gson();
		}
		return gson;
	}

	@Override
	public void vioTypeOnClick(String vioType) {
		if ("无效".equals(vioType)) {
			mTvIsDo.setText("否");
			mllIsDo.setEnabled(false);
		} else if ("有效".equals(vioType)) {
			mllIsDo.setEnabled(true);
		}
	}
}
