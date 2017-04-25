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
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.xiangxun.activity.R;
import com.xiangxun.app.BaseActivity;
import com.xiangxun.bean.ResultData.PersionInfo;
import com.xiangxun.cfg.SystemCfg;
import com.xiangxun.request.DcNetWorkUtils;
import com.xiangxun.util.ConstantStatus;
import com.xiangxun.util.NumberXUtil;
import com.xiangxun.util.Utils;
import com.xiangxun.widget.AutoClearEditText;
import com.xiangxun.widget.MsgToast;
import com.xiangxun.widget.TitleView;
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
	private NumberXUtil keybord;
	private int inputType;

	private AutoClearEditText driverid;
	private LinearLayout mSearch;
	private ToggleButton mTgBtnSearch;
	private LinearLayout ll_driverInfo;
	private LinearLayout ll_loading;// 等待
	private LinearLayout ll_no_data;// 无数据

	private TextView tvDriverName;// 驾驶员姓名
	private TextView tvDriverSex;// 驾驶员性别
	private TextView tvDriverBrithday;// 驾驶员生日
	private TextView tvDriverTel;// 驾驶员联系电话
	private TextView tvDriverMode;// 准驾车型
	private TextView tvDriverValidity;// 驾驶证有效期
	private TextView tvIssuingAuthority;// 发证机关
	private TextView tvDriverArchivesNo;// 档案编号
	private TextView tvDriverSroce;// 累计分值
	private TextView tvDriverStatus;// 状态
	private TextView tvDriverFirst;// 初次领证日期
	private TextView tvDriverNext;// 下次体检日期
	private TextView tvEffectiveStartDate;// 有效起日期
	private TextView tvEffectiveEndDate;// 有效至日期
	private TextView tvEffectiveZipCode;// 行政区别
	private TextView tvEffectiveLiveNum;// 暂住证明
	private TextView tvEffectiveUpdate;// 更新日期
	private TextView tvEffectiveMcuNum;// 证芯编号

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mSearch.setEnabled(true);
			switch (msg.what) {
			case ConstantStatus.DRIVER_SEARCH_SUCCESS:
				PersionInfo persionInfo = (PersionInfo) msg.obj;
				if (persionInfo != null) {
					ll_loading.setVisibility(View.GONE);
					ll_driverInfo.setVisibility(View.VISIBLE);
					setViewData(persionInfo);
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

		tvDriverName = (TextView) findViewById(R.id.tv_driver_name);// 驾驶员姓名
		tvDriverSex = (TextView) findViewById(R.id.tv_driver_sex);// 驾驶员性别
		tvDriverBrithday = (TextView) findViewById(R.id.tv_driver_birthday);// 驾驶员生日
		tvDriverTel = (TextView) findViewById(R.id.tv_driver_tel);// 驾驶员联系电话
		tvDriverMode = (TextView) findViewById(R.id.tv_driver_car);// 准驾车型
		tvDriverValidity = (TextView) findViewById(R.id.tv_driver_time);// 驾驶证有效期
		tvIssuingAuthority = (TextView) findViewById(R.id.tv_driver_office);// 发证机关
		tvDriverArchivesNo = (TextView) findViewById(R.id.tv_driver_number);// 档案编号
		tvDriverSroce = (TextView) findViewById(R.id.tv_driver_score);// 累计分值
		tvDriverStatus = (TextView) findViewById(R.id.tv_driver_status);// 状态
		tvDriverFirst = (TextView) findViewById(R.id.tv_driver_first);// 初次领证日期
		tvDriverNext = (TextView) findViewById(R.id.tv_driver_next);// 下次体检日期
		tvEffectiveStartDate = (TextView) findViewById(R.id.tv_driver_start_date);// 有效起日期
		tvEffectiveEndDate = (TextView) findViewById(R.id.tv_driver_end_date);// 有效至日期
		tvEffectiveZipCode = (TextView) findViewById(R.id.tv_driver_zipcode);// 行政区别
		tvEffectiveLiveNum = (TextView) findViewById(R.id.tv_driver_livenum);// 暂住证明
		tvEffectiveUpdate = (TextView) findViewById(R.id.tv_driver_update);// 更新日期
		tvEffectiveMcuNum = (TextView) findViewById(R.id.tv_driver_mcunum);// 证芯编号
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
					if (keybord == null)
						keybord = new NumberXUtil(SearchDriverInfoActivity.this, SearchDriverInfoActivity.this, driverid);
					keybord.showKeyboard();
				}
				return false;
			}
		});

		driverid.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus == false) {
					keybord.hideKeyboard();
				}
			}
		});
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
	public void initData() {
		titleView.setLeftBackOneListener(R.drawable.back_normal, this);
	}

	private void setViewData(PersionInfo persionInfo) {
		tvDriverName.setText(getIsNull(persionInfo.name));// 驾驶员姓名
		if(persionInfo.sex.equals("1")){
			tvDriverSex.setText(getIsNull("男"));// 驾驶员性别
		}else if(persionInfo.sex.equals("2")){
			tvDriverSex.setText(getIsNull("女"));// 驾驶员性别
		}else {
			tvDriverSex.setText(getIsNull(persionInfo.sex));// 驾驶员性别
		}
		tvDriverBrithday.setText(getIsNull(persionInfo.birthdayStr));// 驾驶员生日
		tvDriverTel.setText(getIsNull(persionInfo.telphoe));// 驾驶员联系电话
		tvDriverMode.setText(getIsNull(persionInfo.drvierType));// 准驾车型
		tvDriverValidity.setText(getIsNull(persionInfo.usefulDate));// 驾驶证有效期
		tvIssuingAuthority.setText(getIsNull(persionInfo.licenseDepartment));// 发证机关
		tvDriverArchivesNo.setText(getIsNull(persionInfo.dabh));// 档案编号
		tvDriverSroce.setText(getIsNull(persionInfo.ljjf));// 累计分值
		tvDriverStatus.setText(getIsNull(persionInfo.zt));// 状态
		tvDriverFirst.setText(getIsNull(persionInfo.cclzrq));// 初次领证日期
		tvDriverNext.setText(getIsNull(persionInfo.syrq));// 下次体检日期
		tvEffectiveStartDate.setText(getIsNull(persionInfo.yxqs));// 有效起日期
		tvEffectiveEndDate.setText(getIsNull(persionInfo.yxqz));// 有效至日期
		tvEffectiveZipCode.setText(getIsNull(persionInfo.xzqh));// 行政区别
		tvEffectiveLiveNum.setText(getIsNull(persionInfo.zzzm));// 暂住证明
		tvEffectiveUpdate.setText(getIsNull(persionInfo.gxsj));// 更新日期
		tvEffectiveMcuNum.setText(getIsNull(persionInfo.zxbh));// 证芯编号
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
			if (keybord != null && keybord.getKeyboardVisible())
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
				setVisibility();
				mSearch.setEnabled(false);
				ll_loading.setVisibility(View.VISIBLE);
				DcNetWorkUtils.searchDriver(this, driverLicenseNumber, handler);
			}
			break;
		}
	}

	private void setVisibility() {
		ll_loading.setVisibility(View.GONE);
		ll_driverInfo.setVisibility(View.GONE);
		ll_no_data.setVisibility(View.GONE);
	}

}
