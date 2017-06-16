/**
 * 
 */
package com.xiangxun.activity.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.xiangxun.activity.R;
import com.xiangxun.app.BaseActivity;
import com.xiangxun.app.XiangXunApplication;
import com.xiangxun.bean.PersionInfoX;
import com.xiangxun.cfg.SystemCfg;
import com.xiangxun.common.DcHttpClient;
import com.xiangxun.db.DBManager;
import com.xiangxun.request.DcNetWorkUtils;
import com.xiangxun.util.ConstantStatus;
import com.xiangxun.util.KeyboardUtil;
import com.xiangxun.util.Tools;
import com.xiangxun.util.Utils;
import com.xiangxun.widget.AutoClearEditText;
import com.xiangxun.widget.MsgToast;
import com.xiangxun.widget.TitleView;
import com.xiangxun.widget.WaterMark;
import com.xiangxun.wtone.IDCameraActivity;

/**
 * @package: com.xiangxun.activity.home
 * @ClassName: SearchDriverInfoActivity.java
 * @Description: 驾驶员信息查询
 * @author: HanGJ
 * @date: 2015-8-13 下午5:00:32
 */
public class SearchDriverInfoActivity extends BaseActivity implements OnClickListener {
	private TitleView titleView;
	private KeyboardUtil keybord;
	private int inputType;

	private AutoClearEditText driverid;
	private LinearLayout mSearch;
	private ToggleButton mTgBtnSearch;
	private LinearLayout ll_driverInfo;
	private LinearLayout ll_loading;// 等待
	private LinearLayout ll_no_data;// 无数据

	private TextView mShowDriverPic;
	private ImageView mDriverImage;

	private TextView tvDriver_sfzmmc; //身份证明名称
	private TextView tvDriver_sfzmhm; //身份证明号码
	private TextView tvDriver_hmcd; //号码长度
	private TextView tvDriver_xm; //姓名
	private TextView tvDriver_xb; //性别
	private TextView tvDriver_csrq; //出生日期
	private TextView tvDriver_gj; //国籍
	private TextView tvDriver_djzsxzqh; //登记住所行政区划
	private TextView tvDriver_djzsxxdz; //登记住所详细地址
	private TextView tvDriver_lxzsxzqh; //联系住所行政区划
	private TextView tvDriver_lxzsxxdz; //联系住所详细地址
	private TextView tvDriver_lxzsyzbm; //联系住所邮政编码
	private TextView tvDriver_lxdh; //联系电话
	private TextView tvDriver_sjhm; //手机号码
	private TextView tvDriver_dzyx; //电子邮箱
	private TextView tvDriver_dabh; //档案编号
	private TextView tvDriver_zjcx; //准驾车型
	private TextView tvDriver_ljjf; //累积记分
	private TextView tvDriver_zt; //状态
	private TextView tvDriver_cclzrq; //初次领证日期
	private TextView tvDriver_jzqx; //驾证期限
	private TextView tvDriver_syrq; //下一体检日期
	private TextView tvDriver_yxqs; //有效期始
	private TextView tvDriver_yxqz; //有效期止
	private TextView tvDriver_xzqh; //行政区划
	private TextView tvDriver_zzzm; //暂住证明
	private TextView tvDriver_gxsj; //更新时间
	private TextView tvDriver_zxbh; //证芯编号
	private TextView tvDriver_fzjg; //发证机关

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mSearch.setEnabled(true);
			switch (msg.what) {
			case ConstantStatus.DRIVER_SEARCH_SUCCESS:
				PersionInfoX persionInfo = (PersionInfoX) msg.obj;
				if (persionInfo != null) {
					ll_loading.setVisibility(View.GONE);
					ll_driverInfo.setVisibility(View.VISIBLE);
					setViewData(persionInfo);

					mShowDriverPic.setVisibility(View.VISIBLE);
					showWaterMark();
				} else {
					ll_no_data.setVisibility(View.VISIBLE);
				}
				break;

			case ConstantStatus.DRIVER_SEARCH_FALSE:
				ll_loading.setVisibility(View.GONE);
				ll_driverInfo.setVisibility(View.GONE);
				ll_no_data.setVisibility(View.VISIBLE);
				MsgToast.geToast().setMsg("驾驶员数据查询失败, 无对应数据!");
				break;

			case ConstantStatus.NetWorkError:
				setVisibility();
				MsgToast.geToast().setMsg("网络异常");
				break;
			case ConstantStatus.DRIVERPIC_SEARCH_FALSE:
				MsgToast.geToast().setMsg("获取驾驶人照片异常");
				break;
			case ConstantStatus.DRIVERPIC_SEARCH_SUCCESS:
				String driveImageURL = (String) msg.obj;
				if (driveImageURL.endsWith("jpg")){
					driveImageURL = "http://" + SystemCfg.getServerIP(XiangXunApplication.getInstance()) + ":" + SystemCfg.getServerPort(XiangXunApplication.getInstance()) + driveImageURL;
					ShowDriverPic(driveImageURL);
				} else	{
					MsgToast.geToast().setMsg("无法查询到驾驶人照片");
				}
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.driver_info);
		initView();
		initData();
		initListener();
	}

	@Override
	public void initView() {
		titleView = (TitleView) findViewById(R.id.tv_comm_title);
		titleView.setTitle(R.string.jsycx);
		driverid = (AutoClearEditText) findViewById(R.id.search_edit);
		mTgBtnSearch = (ToggleButton) findViewById(R.id.tgbtn_search);
		mSearch = (LinearLayout) findViewById(R.id.search_btn);
		ll_driverInfo = (LinearLayout) findViewById(R.id.ll_driver_content);
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading_layout);
		ll_no_data = (LinearLayout) findViewById(R.id.ll_no_data_layout);

		mShowDriverPic = (TextView) findViewById(R.id.show_driver_pic);
		mDriverImage = (ImageView) findViewById(R.id.driver_image);

//		tvDriver_sfzmmc = (TextView) findViewById(R.id.key_sfzmmc_value));//身份证明名称
//		tvDriver_sfzmhm = (TextView) findViewById(R.id.tv_driver_sfzmhm));//身份证明号码
//		tvDriver_hmcd = (TextView) findViewById(R.id.tv_driver_hmcd); //号码长度
		tvDriver_xm = (TextView) findViewById(R.id.key_xm_value); //姓名
		tvDriver_xb = (TextView) findViewById(R.id.key_xb_value); //性别
//		tvDriver_csrq = (TextView) findViewById(R.id.tv_driver_csrq); //出生日期
//		tvDriver_gj = (TextView) findViewById(R.id.tv_driver_gjTe; //国籍
//		tvDriver_djzsxzqh = (TextView) findViewById(R.id.key_djzsxzqh_value); //登记住所行政区划
		tvDriver_djzsxxdz = (TextView) findViewById(R.id.key_djzsxxdz_value); //登记住所详细地址
//		tvDriver_lxzsxzqh = (TextView) findViewById(R.id.key_lxzsxzqh_value); //联系住所行政区划
		tvDriver_lxzsxxdz = (TextView) findViewById(R.id.key_lxzsxxdz_value); //联系住所详细地址
//		tvDriver_lxzsyzbm = (TextView) findViewById(R.id.key_lxzsyzbm_value); //联系住所邮政编码
		tvDriver_lxdh = (TextView) findViewById(R.id.key_lxdh_value); //联系电话
		tvDriver_sjhm = (TextView) findViewById(R.id.key_sjhm_value); //手机号码
//		tvDriver_dzyx = (TextView) findViewById(R.id.key_dzyx_value); //电子邮箱
		tvDriver_dabh = (TextView) findViewById(R.id.key_dabh_value); //档案编号
		tvDriver_zjcx = (TextView) findViewById(R.id.key_zjcx_value); //准驾车型
		tvDriver_ljjf = (TextView) findViewById(R.id.key_ljjf_value); //累积记分
		tvDriver_zt = (TextView) findViewById(R.id.key_zt_value); //状态
		tvDriver_cclzrq = (TextView) findViewById(R.id.key_cclzrq_value); //初次领证日期
//		tvDriver_jzqx = (TextView) findViewById(R.id.key_jzqx_value); //驾证期限
		tvDriver_syrq = (TextView) findViewById(R.id.key_syrq_value); //下一体检日期
		tvDriver_yxqs = (TextView) findViewById(R.id.key_yxqs_value); //有效期始
		tvDriver_yxqz = (TextView) findViewById(R.id.key_yxqz_value); //有效期止
//		tvDriver_xzqh = (TextView) findViewById(R.id.key_xzqh_value); //行政区划
		tvDriver_zzzm = (TextView) findViewById(R.id.key_zzzm_value); //暂住证明
		tvDriver_gxsj = (TextView) findViewById(R.id.key_gxsj_value); //更新时间
		tvDriver_zxbh = (TextView) findViewById(R.id.key_zxbh_value); //证芯编号
		tvDriver_fzjg = (TextView) findViewById(R.id.key_fzjg_value); //发证机关
	}

	@Override
	public void initListener() {
		mSearch.setOnClickListener(this);
		driverid.addTextChangedListener(new TextWatcher() {

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
					driverid.setText(s.toString().trim());
				}
				boolean isClear = searchWord.length() == 0;
				mTgBtnSearch.setChecked(!isClear);
				
				driverid.removeTextChangedListener(this);// 解除文字改变事件
				index  = driverid.getSelectionStart();// 获取光标位置
				driverid.setText(s.toString().toUpperCase());
				driverid.setSelection(index);// 重新设置光标位置
				driverid.addTextChangedListener(this);// 重新绑定事件
			}
		});

		driverid.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN){
					if (keybord == null) {
						keybord = new KeyboardUtil(SearchDriverInfoActivity.this, driverid);
						keybord.changeKeyboardNumberX();
					}
					keybord.showKeyboard();
				}
				return false;
			}
		});

		mShowDriverPic.setOnClickListener(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (keybord != null && keybord.isShow())
				keybord.hideKeyboard();
			else
				onBackPressed();

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void initData() {
		titleView.setLeftBackOneListener(R.drawable.back_normal, this);
		SpannableString ss = new SpannableString("显示驾驶人照片");
		ss.setSpan(new UnderlineSpan(), 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		mShowDriverPic.setText(ss);
	}

	private void setViewData(PersionInfoX persionInfo) {
//		tvDriver_sfzmmc.setText(getIsNull(persionInfo.sfzmmc)); //身份证明名称
//		tvDriver_sfzmhm.setText(getIsNull(persionInfo.sfzmhm)); //身份证明号码
//		tvDriver_hmcd.setText(getIsNull(persionInfo.hmcd)); //号码长度
		tvDriver_xm.setText(getIsNull(persionInfo.xm)); //姓名
		tvDriver_xb.setText(getIsNull(persionInfo.xb.equals("1") ? "男":"女")); //性别
//		tvDriver_csrq.setText(getIsNull(persionInfo.csrq)); //出生日期
//		tvDriver_gj.setText(getIsNull(persionInfo.gj)); //国籍
//		tvDriver_djzsxzqh.setText(getIsNull(persionInfo.djzsxzqh)); //登记住所行政区划
		tvDriver_djzsxxdz.setText(getIsNull(persionInfo.djzsxxdz)); //登记住所详细地址
//		tvDriver_lxzsxzqh.setText(getIsNull(persionInfo.lxzsxzqh)); //联系住所行政区划
		tvDriver_lxzsxxdz.setText(getIsNull(persionInfo.lxzsxxdz)); //联系住所详细地址
//		tvDriver_lxzsyzbm.setText(getIsNull(persionInfo.lxzsyzbm)); //联系住所邮政编码
		tvDriver_lxdh.setText(getIsNull(persionInfo.lxdh)); //联系电话
		tvDriver_sjhm.setText(getIsNull(persionInfo.sjhm)); //手机号码
//		tvDriver_dzyx.setText(getIsNull(persionInfo.dzyx)); //电子邮箱
		tvDriver_dabh.setText(getIsNull(persionInfo.dabh)); //档案编号
		tvDriver_zjcx.setText(getIsNull(persionInfo.zjcx)); //准驾车型
		tvDriver_ljjf.setText(getIsNull(persionInfo.ljjf)); //累积记分
		tvDriver_zt.setText(getIsNull(DBManager.getInstance().getNameByTypeAndCodeFromDic("LIENCE_STATUS", persionInfo.zt))); //状态
		tvDriver_cclzrq.setText(getIsNull(persionInfo.cclzrq)); //初次领证日期
//		tvDriver_jzqx.setText(getIsNull(persionInfo.jzqx)); //驾证期限
		tvDriver_syrq.setText(getIsNull(persionInfo.syrq)); //下一体检日期
		tvDriver_yxqs.setText(getIsNull(persionInfo.yxqs)); //有效期始
		tvDriver_yxqz.setText(getIsNull(persionInfo.yxqz)); //有效期止
//		tvDriver_xzqh.setText(getIsNull(persionInfo.xzqh)); //行政区划
		tvDriver_zzzm.setText(getIsNull(persionInfo.zzzm)); //暂住证明
		tvDriver_gxsj.setText(getIsNull(persionInfo.gxsj)); //更新时间
		tvDriver_zxbh.setText(getIsNull(persionInfo.zxbh)); //证芯编号
		tvDriver_fzjg.setText(getIsNull(persionInfo.fzjg)); //发证机关
	}

	private String getIsNull(String content) {
		return TextUtils.isEmpty(content) ? "暂无" : content;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 7:
			if (resultCode == Activity.RESULT_OK) {
				if (data != null) {
					String recogResult = data.getStringExtra("recogResult");
					String exception = data.getStringExtra("exception");
					if (exception != null && !exception.equals("")) {
						MsgToast.geToast().setMsg("未识别出驾驶证信息，请重新识别！");
					} else {
						if (recogResult != null && recogResult.length() > 0) {
							driverid.setText(recogResult);
						} else {
							MsgToast.geToast().setMsg("未识别出驾驶证信息，请重新识别！");
						}
					}
				}
			}
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_view_back_img:
			onBackPressed();
			break;
		case R.id.search_btn:
			if (keybord != null && keybord.isShow())
				keybord.hideKeyboard();
			if (!mTgBtnSearch.isChecked()) {
				Intent intentDriver = new Intent(this, IDCameraActivity.class);
				intentDriver.putExtra("nMainId", 5);
				intentDriver.putExtra("devcode", SystemCfg.getRegistCode(this));
				startActivityForResult(intentDriver, 7);
			} else {
				Utils.hideSoftInputFromWindow(this, driverid);
				String driverLicenseNumber = driverid.getText().toString().trim();
				if (driverLicenseNumber.length() <= 0) {
					MsgToast.geToast().setMsg(R.string.driver_search_hint);
					return;
				}
				if (!Tools.isIDCardValidate(driverLicenseNumber)) {
					return;
				}
				setVisibility();
				mSearch.setEnabled(false);
				mDriverImage.setImageResource(R.drawable.empty_photo);
				mDriverImage.setVisibility(View.GONE);
				ll_loading.setVisibility(View.VISIBLE);
				DcNetWorkUtils.searchDriver(this, driverLicenseNumber, handler);
			}
			break;
		case R.id.show_driver_pic:
			DcNetWorkUtils.searchDriverPic(this, driverid.getText().toString().trim(), handler);
			break;
		}
	}

	private void setVisibility() {
		ll_loading.setVisibility(View.GONE);
		ll_driverInfo.setVisibility(View.GONE);
		ll_no_data.setVisibility(View.GONE);
	}

	private void showWaterMark() {
		WaterMark water_mark = (WaterMark) findViewById(R.id.water_mark);
		water_mark.setWaterStr(SystemCfg.getDepartment(this) + " " + SystemCfg.getPoliceName(this));
		water_mark.setVisibility(View.VISIBLE);
	}

	private void ShowDriverPic(String imageUrl) {
		if (imageUrl != null && (imageUrl.endsWith(".png") || imageUrl.endsWith(".gif") || imageUrl.endsWith(".jpg"))) {
			mDriverImage.setImageResource(R.drawable.empty_photo);
			DcHttpClient.getInstance().requestImageScaled(mDriverImage, imageUrl, R.drawable.empty_photo);
			mDriverImage.setVisibility(View.VISIBLE);
			MsgToast.geToast().setMsg("正在加载驾驶人照片，请稍侯...");
//			mDriverImage.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					Intent intent = new Intent();
//					intent.putExtra("msgURL", imageUrl);
//					intent.setClass(WarnSignActivity.this, WarnImageActivity.class);
//					startActivity(intent);
//				}
//			});
		}
	}
}
