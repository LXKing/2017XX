/**
 * 
 */
package com.xiangxun.activity.home;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.xiangxun.activity.R;
import com.xiangxun.app.BaseActivity;
import com.xiangxun.bean.ListDataItem;
import com.xiangxun.bean.ListDataObj;
import com.xiangxun.bean.ResultData.VehicleInfo;
import com.xiangxun.bean.Type;
import com.xiangxun.db.DBManager;
import com.xiangxun.request.DcNetWorkUtils;
import com.xiangxun.util.ConstantStatus;
import com.xiangxun.util.Tools;
import com.xiangxun.util.Utils;
import com.xiangxun.widget.AutoClearEditText;
import com.xiangxun.widget.MsgToast;
import com.xiangxun.widget.PublishSelectAffairsPlateTypeDialog;
import com.xiangxun.widget.TitleView;
import com.xiangxun.wtone.wtUtils;

import java.util.ArrayList;

import static com.xiangxun.util.Utils.GetCarStatus;

/**
 * @package: com.xiangxun.activity.home
 * @ClassName: SearchVehicleInfoActivity.java
 * @Description: 机动车信息查询

 * @author: HanGJ
 * @date: 2015-8-13 下午5:00:44
 */
public class SearchVehicleInfoActivity extends BaseActivity implements OnClickListener {
	private TitleView titleView;
	private AutoClearEditText platenum;
	private LinearLayout mSearch;
	private TextView mPublishPlateType;
	private ToggleButton mTgBtnPlateSearch;
	private LinearLayout mPublishClickPlateType;
	private ListDataObj ldo = null;
	private PublishSelectAffairsPlateTypeDialog affairsPlateTypeDialog;
	private LinearLayout ll_vehicleInfo;
	private LinearLayout ll_loading;// 等待
	private LinearLayout ll_no_data;// 无数据

	private TextView tvVehicleType;// 车辆类型
	private TextView tvVehicleColor;// 颜色
	private TextView tvVehicleName;// 车辆所有人
	private TextView tvVehicleTel;// 车辆所有人联系电话
	private TextView tvVehicleBrand;// 品牌
	private TextView tvVehicleStatus;// 机动车状态
	private TextView tvVehicleUse;// 使用性质
	private TextView tvVehicleCode;// 发动机号
	private TextView tvVehicleRecognition;// 识别代号
	private TextView tvVehicleFirst;// 初次登记日期
	private TextView tvVehicleCheck;// 检验有效期止

	private TextView tv_vio_search;//
	private RelativeLayout ll_vio_search;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mSearch.setEnabled(true);
			switch (msg.what) {
			case ConstantStatus.VEHICLE_SEARCH_SUCCESS:
				VehicleInfo vehicleInfo = (VehicleInfo) msg.obj;
				if (vehicleInfo != null) {
					ll_loading.setVisibility(View.GONE);
					ll_vehicleInfo.setVisibility(View.VISIBLE);
					setViewData(vehicleInfo);
				} else {
					ll_no_data.setVisibility(View.VISIBLE);
				}
				break;

			case ConstantStatus.VEHICLE_SEARCH_FALSE:
				ll_loading.setVisibility(View.GONE);
				ll_vehicleInfo.setVisibility(View.GONE);
				ll_no_data.setVisibility(View.VISIBLE);
				MsgToast.geToast().setMsg("机动车数据查询失败,无对应数据!");
				break;

			case ConstantStatus.NetWorkError:
				setVisibility();
				MsgToast.geToast().setMsg("网络异常");
				break;
			case ConstantStatus.OTHER_PLATENUM:
				Utils.showPlateInfo(SearchVehicleInfoActivity.this, msg);
				break;				
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vehicle_info);
		initView();
		initData();
		initListener();
	}

	@Override
	public void initView() {
		titleView = (TitleView) findViewById(R.id.tv_comm_title);
		mPublishPlateType = (TextView) findViewById(R.id.tv_publish_platetype);
		mPublishClickPlateType = (LinearLayout) findViewById(R.id.ll_publish_click_platetype);
		mTgBtnPlateSearch = (ToggleButton) findViewById(R.id.tgbtn_search_plate);
		platenum = (AutoClearEditText) findViewById(R.id.search_edit);
		mSearch = (LinearLayout) findViewById(R.id.ll_driver_plate_num);
		ll_vehicleInfo = (LinearLayout) findViewById(R.id.ll_vehicle_content);
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading_layout);
		ll_no_data = (LinearLayout) findViewById(R.id.ll_no_data_layout);
		ll_vio_search = (RelativeLayout) findViewById(R.id.ll_vio_search);

		tvVehicleType = (TextView) findViewById(R.id.tv_vehicle_type);// 车辆类型
		tvVehicleColor = (TextView) findViewById(R.id.tv_vehicle_color);// 颜色
		tvVehicleName = (TextView) findViewById(R.id.tv_vehicle_name);// 车辆所有人
		tvVehicleTel = (TextView) findViewById(R.id.tv_vehicle_tel);// 联系人电话
		tvVehicleBrand = (TextView) findViewById(R.id.tv_vehicle_brand);// 品牌
		tvVehicleStatus = (TextView) findViewById(R.id.tv_vehicle_status);// 车辆状态
		tvVehicleUse = (TextView) findViewById(R.id.tv_vehicle_use);// 使用性质
		tvVehicleCode = (TextView) findViewById(R.id.tv_vehicle_code);// 车架号
		tvVehicleRecognition = (TextView) findViewById(R.id.tv_vehicle_recognition);// 识别代号
		tvVehicleFirst = (TextView) findViewById(R.id.tv_vehicle_first);// 初次登记日期
		tvVehicleCheck = (TextView) findViewById(R.id.tv_vehicle_end_date);// 检验有效期止

		tv_vio_search = (TextView) findViewById(R.id.tv_vio_search);//
	}

	@Override
	public void initData() {
		titleView.setTitle(R.string.jdccx);
		int index = platenum.getText().length();
		platenum.setSelection(index);

		ldo = new ListDataObj();
		ArrayList<Type> platetypes = DBManager.getInstance().getPlateTypes();
		for (int i = 0; i < platetypes.size(); i++) {
			ldo.add(platetypes.get(i).code, platetypes.get(i).name);
		}
		affairsPlateTypeDialog = new PublishSelectAffairsPlateTypeDialog(this, ldo.getlist(), mPublishPlateType);
		affairsPlateTypeDialog.setSelectCapture(getResources().getString(R.string.def_platetype));
		tv_vio_search.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
	}

	@Override
	public void initListener() {
		mSearch.setOnClickListener(this);
		tv_vio_search.setOnClickListener(this);
		mPublishClickPlateType.setOnClickListener(this);
		titleView.setLeftBackOneListener(R.drawable.back_normal, this);
		platenum.addTextChangedListener(new TextWatcher() {
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
				platenum.removeTextChangedListener(this);// 解除文字改变事件
				index = platenum.getSelectionStart();// 获取光标位置
				platenum.setText(s.toString().toUpperCase());
				platenum.setSelection(index);// 重新设置光标位置
				platenum.addTextChangedListener(this);// 重新绑定事件

				if (isClear) {
					ll_vio_search.setVisibility(View.GONE);
				} else {
					ll_vio_search.setVisibility(View.VISIBLE);
				}

				Utils.chkOtherPlate(SearchVehicleInfoActivity.this, handler, platenum, searchWord);
			}
		});
	}

	private void setViewData(VehicleInfo vehicleInfo) {
//		tvVehicleType.setText(getIsNull(DBManager.getInstance().getNameByTypeAndCodeFromDic("PLATE_TYPE", vehicleInfo.cartype)));	// 车辆类型	//转换K33
		tvVehicleType.setText(mPublishPlateType.getText().toString());
		tvVehicleColor.setText(getIsNull(DBManager.getInstance().getNameByTypeAndCodeFromDic("CAR_COLOR", vehicleInfo.carColorCode)));// 颜色
		tvVehicleName.setText(getIsNull(vehicleInfo.userName));// 车辆所有人
		tvVehicleTel.setText(getIsNull(vehicleInfo.contactTel));// 电话
		tvVehicleBrand.setText(getIsNull(vehicleInfo.carBrand));// 品牌
		tvVehicleStatus.setText(getIsNull(GetCarStatus(vehicleInfo.carStatus)));// 机动车状态
		tvVehicleUse.setText(getIsNull(DBManager.getInstance().getNameByTypeAndCodeFromDic("CAR_RUN_TYPE", vehicleInfo.useNature)));// 使用性质
		tvVehicleCode.setText(getIsNull(vehicleInfo.shelfNo));// 车架号
		tvVehicleRecognition.setText(getIsNull(vehicleInfo.distinguishCode));// 识别代号
		tvVehicleFirst.setText(getIsNull(vehicleInfo.ccdjrq));// 初次登记日期
		tvVehicleCheck.setText(getIsNull(vehicleInfo.yxqz));// 检验有效期止
	}

	private String getIsNull(String content) {
		return TextUtils.isEmpty(content) ? "暂无" : content;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 3:
			if (resultCode == Activity.RESULT_OK) {
				ll_vio_search.setVisibility(View.VISIBLE);
				platenum.setText(data.getExtras().getString("platenum"));
				MsgToast.geToast().setMsg(R.string.plateNotifyCompleted);
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
		case R.id.ll_driver_plate_num:
			if (!mTgBtnPlateSearch.isChecked()) {
				Intent intentPlate = wtUtils.StartPlateRecog(SearchVehicleInfoActivity.this);
				startActivityForResult(intentPlate, 3);
			} else {
				Utils.hideSoftInputFromWindow(this, platenum);
				String carPlateType = mPublishPlateType.getText().toString();
				String carPlateNumber = platenum.getText().toString().trim();
				if (carPlateNumber.length() <= 0) {
					MsgToast.geToast().setMsg(R.string.vehicleInfo_search_hint);
					return;
				}
				if (!Tools.isCarnumberNO(carPlateNumber)) {
					MsgToast.geToast().setMsg("号牌号码格式不准确，请确认");
					return;
				}

				if (carPlateType == null || carPlateType.length() <= 0) {
					MsgToast.geToast().setMsg("请选择号牌种类");
					return;
				}
				setVisibility();
				mSearch.setEnabled(false);
				ll_loading.setVisibility(View.VISIBLE);
				ListDataItem platetype = (ListDataItem) mPublishPlateType.getTag();
				DcNetWorkUtils.searchVehicleInfo(this, carPlateNumber, platetype.getid(), handler);
			}
			break;
		case R.id.ll_publish_click_platetype:
			affairsPlateTypeDialog.show();
			break;
		case R.id.tv_vio_search:
			String carPlateType = mPublishPlateType.getText().toString();
			String carPlateNumber = platenum.getText().toString().trim();
			if (carPlateNumber.length() <= 0) {
				MsgToast.geToast().setMsg(R.string.vehicleInfo_search_hint);
				return;
			}
			if (!Tools.isCarnumberNO(carPlateNumber)) {
				MsgToast.geToast().setMsg("号牌号码格式不准确，请确认");
				return;
			}

			if (carPlateType == null || carPlateType.length() <= 0) {
				MsgToast.geToast().setMsg("请选择号牌种类");
				return;
			}
			ListDataItem platetype = (ListDataItem) mPublishPlateType.getTag();
			Intent intent = new Intent(this, VioInfoSearchActivity.class);
			intent.putExtra("carPlateNumber", carPlateNumber);
			intent.putExtra("carPlateType", platetype.getid());
			startActivity(intent);
			break;
		}
	}

	private void setVisibility() {
		ll_loading.setVisibility(View.GONE);
		ll_vehicleInfo.setVisibility(View.GONE);
		ll_no_data.setVisibility(View.GONE);
	}

}
