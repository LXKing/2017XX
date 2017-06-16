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
import android.view.KeyEvent;
import android.view.MotionEvent;
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
import com.xiangxun.bean.PersionInfoX;
import com.xiangxun.bean.Type;
import com.xiangxun.bean.VehicleInfoX;
import com.xiangxun.cfg.SystemCfg;
import com.xiangxun.db.DBManager;
import com.xiangxun.request.DcNetWorkUtils;
import com.xiangxun.util.ConstantStatus;
import com.xiangxun.util.KeyboardUtil;
import com.xiangxun.util.Tools;
import com.xiangxun.util.Utils;
import com.xiangxun.widget.AutoClearEditText;
import com.xiangxun.widget.MsgToast;
import com.xiangxun.widget.PublishSelectAffairsPlateTypeDialog;
import com.xiangxun.widget.TitleView;
import com.xiangxun.widget.WaterMark;
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
	private KeyboardUtil keybord;

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

	private TextView tvVehicle_xh;// 机动车序号
	private TextView tvVehicle_hpzl;// 号牌种类
	private TextView tvVehicle_hphm;// 号牌号码
	private TextView tvVehicle_clpp1;// 中文品牌
	private TextView tvVehicle_clxh;// 车辆型号
	private TextView tvVehicle_clpp2;// 英文品牌
	private TextView tvVehicle_gcjk;// 国产/进口
	private TextView tvVehicle_zzg;// 制造国
	private TextView tvVehicle_zzcmc;// 制造厂名称
	private TextView tvVehicle_clsbdh;// 车辆识别代号
	private TextView tvVehicle_fdjh;// 发动机号
	private TextView tvVehicle_cllx;// 车辆类型
	private TextView tvVehicle_csys;// 车身颜色
	private TextView tvVehicle_syxz;// 使用性质
	private TextView tvVehicle_sfzmhm;// 身份证明号码
	private TextView tvVehicle_sfzmmc;// 身份证明名称
	private TextView tvVehicle_syr;// 机动车所有人
	private TextView tvVehicle_ccdjrq;// 初次登记日期
	private TextView tvVehicle_djrq;// 最近定检日期
	private TextView tvVehicle_yxqz;// 检验有效期止
	private TextView tvVehicle_qzbfqz;// 强制报废期止
	private TextView tvVehicle_fzjg;// 发证机关
	private TextView tvVehicle_glbm;// 管理部门
	private TextView tvVehicle_bxzzrq;// 保险终止日期
	private TextView tvVehicle_zt;// 机动车状态
	private TextView tvVehicle_dybj;// 抵押标记
	private TextView tvVehicle_fdjxh;// 发动机型号
	private TextView tvVehicle_rlzl;// 燃料种类
	private TextView tvVehicle_pl;// 排量
	private TextView tvVehicle_gl;// 功率
	private TextView tvVehicle_zxxs;// 转向形式
	private TextView tvVehicle_cwkc;// 车外廓长
	private TextView tvVehicle_cwkk;// 车外廓宽
	private TextView tvVehicle_cwkg;// 车外廓高
	private TextView tvVehicle_hxnbcd;// 货箱内部长度
	private TextView tvVehicle_hxnbkd;// 货箱内部宽度
	private TextView tvVehicle_hxnbgd;// 货箱内部高度
	private TextView tvVehicle_gbthps;// 钢板弹簧片数
	private TextView tvVehicle_zs;// 轴数
	private TextView tvVehicle_zj;// 轴距
	private TextView tvVehicle_qlj;// 前轮距
	private TextView tvVehicle_hlj;// 后轮距
	private TextView tvVehicle_ltgg;// 轮胎规格
	private TextView tvVehicle_lts;// 轮胎数
	private TextView tvVehicle_zzl;// 总质量
	private TextView tvVehicle_zbzl;// 整备质量
	private TextView tvVehicle_hdzzl;// 核定载质量
	private TextView tvVehicle_hdzk;// 核定载客
	private TextView tvVehicle_zqyzl;// 准牵引总质量
	private TextView tvVehicle_qpzk;// 驾驶室前排载客人数
	private TextView tvVehicle_hpzk;// 驾驶室后排载客人数
	private TextView tvVehicle_hbdbqk;// 环保达标情况
	private TextView tvVehicle_ccrq;// 出厂日期
	private TextView tvVehicle_clyt;// 车辆用途
	private TextView tvVehicle_ytsx;// 用途属性
	private TextView tvVehicle_xszbh;// 行驶证证芯编号
	private TextView tvVehicle_jyhgbzbh;// 检验合格标志
	private TextView tvVehicle_lxdh;// 联系电话
	private TextView tvVehicle_lxdz;// 联系地址

	private TextView tv_vio_search;//
	private RelativeLayout ll_vio_search;

	private VehicleInfoX vehicleInfo;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mSearch.setEnabled(true);
			switch (msg.what) {
			case ConstantStatus.VEHICLE_SEARCH_SUCCESS:
				vehicleInfo = (VehicleInfoX) msg.obj;
				if (vehicleInfo != null) {
					ll_loading.setVisibility(View.GONE);
					ll_vehicleInfo.setVisibility(View.VISIBLE);
					setViewData(vehicleInfo);
					if (vehicleInfo.sfzmmc != null && vehicleInfo.sfzmmc.equals("A") && vehicleInfo.sfzmhm != null && vehicleInfo.sfzmhm.length() > 1)
						getCarPhone(vehicleInfo.sfzmhm);
					else {
						tvVehicle_lxdh.setText(getIsNull(vehicleInfo.lxdh));
						tvVehicle_lxdz.setText(getIsNull(vehicleInfo.lxdz));
					}

					showWaterMark();
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
			case ConstantStatus.DRIVER_SEARCH_SUCCESS:
				PersionInfoX persionInfo = (PersionInfoX) msg.obj;
				if (persionInfo != null) {
					if (persionInfo.sjhm != null && !persionInfo.sjhm.equals("") && persionInfo.sjhm.length() > 0)
						vehicleInfo.lxdh = persionInfo.sjhm;// 电话
					else if (persionInfo.lxdh != null && !persionInfo.lxdh.equals("") && persionInfo.lxdh.length() > 0)
						vehicleInfo.lxdh = persionInfo.lxdh;// 电话
					else
						vehicleInfo.lxdh = null;

					if (persionInfo.lxzsxxdz != null && !persionInfo.lxzsxxdz.equals("") && persionInfo.lxzsxxdz.length() > 0)
						vehicleInfo.lxdz = persionInfo.lxzsxxdz;// 电话
					else if (persionInfo.djzsxxdz != null && !persionInfo.djzsxxdz.equals("") && persionInfo.djzsxxdz.length() > 0)
						vehicleInfo.lxdz = persionInfo.djzsxxdz;// 电话
					else
						vehicleInfo.lxdz = null;

					tvVehicle_lxdh.setText(getIsNull(vehicleInfo.lxdh));
					tvVehicle_lxdz.setText(getIsNull(vehicleInfo.lxdz));
				}
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
		tv_vio_search = (TextView) findViewById(R.id.tv_vio_search);//

		tvVehicle_xh = (TextView) findViewById(R.id.key_xh_value);// 机动车序号
		tvVehicle_hpzl = (TextView) findViewById(R.id.key_hpzl_value);// 号牌种类
		tvVehicle_hphm = (TextView) findViewById(R.id.key_hphm_value);// 号牌号码
		tvVehicle_clpp1 = (TextView) findViewById(R.id.key_clpp_value);// 中文品牌
		tvVehicle_clxh = (TextView) findViewById(R.id.key_clxh_value);// 车辆型号
//		tvVehicle_clpp2 = (TextView) findViewById(R.id.key_clpp2_value);// 英文品牌
//		tvVehicle_gcjk = (TextView) findViewById(R.id.key_gcjk_value);// 国产/进口
//		tvVehicle_zzg = (TextView) findViewById(R.id.key_zzg_value);// 制造国
//		tvVehicle_zzcmc = (TextView) findViewById(R.id.key_zzcmc_value);// 制造厂名称
		tvVehicle_clsbdh = (TextView) findViewById(R.id.key_clsbdh_value);// 车辆识别代号
		tvVehicle_fdjh = (TextView) findViewById(R.id.key_fdjh_value);// 发动机号
		tvVehicle_cllx = (TextView) findViewById(R.id.key_cllx_value);// 车辆类型
		tvVehicle_csys = (TextView) findViewById(R.id.key_csys_value);// 车身颜色
		tvVehicle_syxz = (TextView) findViewById(R.id.key_syxz_value);// 使用性质
//		tvVehicle_sfzmhm = (TextView) findViewById(R.id.key_sfzmhm_value);// 身份证明号码
//		tvVehicle_sfzmmc = (TextView) findViewById(R.id.key_sfzmmc_value);// 身份证明名称
		tvVehicle_syr = (TextView) findViewById(R.id.key_syr_value);// 机动车所有人
		tvVehicle_ccdjrq = (TextView) findViewById(R.id.key_ccdjrq_value);// 初次登记日期
		tvVehicle_djrq = (TextView) findViewById(R.id.key_djrq_value);// 最近定检日期
		tvVehicle_yxqz = (TextView) findViewById(R.id.key_yxqz_value);// 检验有效期止
		tvVehicle_qzbfqz = (TextView) findViewById(R.id.key_qzbfqz_value);// 强制报废期止
//		tvVehicle_fzjg = (TextView) findViewById(R.id.key_fzjg_value);// 发证机关
//		tvVehicle_glbm = (TextView) findViewById(R.id.key_glbm_value);// 管理部门
		tvVehicle_bxzzrq = (TextView) findViewById(R.id.key_bxzzrq_value);// 保险终止日期
		tvVehicle_zt = (TextView) findViewById(R.id.key_zt_value);// 机动车状态
		tvVehicle_dybj = (TextView) findViewById(R.id.key_dybj_value);// 抵押标记
//		tvVehicle_fdjxh = (TextView) findViewById(R.id.key_fdjxh_value);// 发动机型号
//		tvVehicle_rlzl = (TextView) findViewById(R.id.key_rlzl_value);// 燃料种类
//		tvVehicle_pl = (TextView) findViewById(R.id.key_pl_value);// 排量
//		tvVehicle_gl = (TextView) findViewById(R.id.key_gl_value);// 功率
//		tvVehicle_zxxs = (TextView) findViewById(R.id.key_zxxs_value);// 转向形式
//		tvVehicle_cwkc = (TextView) findViewById(R.id.key_cwkc_value);// 车外廓长
//		tvVehicle_cwkk = (TextView) findViewById(R.id.key_cwkk_value);// 车外廓宽
//		tvVehicle_cwkg = (TextView) findViewById(R.id.key_cwkg_value);// 车外廓高
//		tvVehicle_hxnbcd = (TextView) findViewById(R.id.key_hxnbcd_value);// 货箱内部长度
//		tvVehicle_hxnbkd = (TextView) findViewById(R.id.key_hxnbkd_value);// 货箱内部宽度
//		tvVehicle_hxnbgd = (TextView) findViewById(R.id.key_hxnbgd_value);// 货箱内部高度
//		tvVehicle_gbthps = (TextView) findViewById(R.id.key_gbthps_value);// 钢板弹簧片数
//		tvVehicle_zs = (TextView) findViewById(R.id.key_zs_value);// 轴数
//		tvVehicle_zj = (TextView) findViewById(R.id.key_zj_value);// 轴距
//		tvVehicle_qlj = (TextView) findViewById(R.id.key_qlj_value);// 前轮距
//		tvVehicle_hlj = (TextView) findViewById(R.id.key_hlj_value);// 后轮距
//		tvVehicle_ltgg = (TextView) findViewById(R.id.key_ltgg_value);// 轮胎规格
//		tvVehicle_lts = (TextView) findViewById(R.id.key_lts_value);// 轮胎数
//		tvVehicle_zzl = (TextView) findViewById(R.id.key_zzl_value);// 总质量
//		tvVehicle_zbzl = (TextView) findViewById(R.id.key_zbzl_value);// 整备质量
		tvVehicle_hdzzl = (TextView) findViewById(R.id.key_hdzzl_value);// 核定载质量
		tvVehicle_hdzk = (TextView) findViewById(R.id.key_hdzk_value);// 核定载客
//		tvVehicle_zqyzl = (TextView) findViewById(R.id.key_zqyzl_value);// 准牵引总质量
//		tvVehicle_qpzk = (TextView) findViewById(R.id.key_qpzk_value);// 驾驶室前排载客人数
//		tvVehicle_hpzk = (TextView) findViewById(R.id.key_hpzk_value);// 驾驶室后排载客人数
//		tvVehicle_hbdbqk = (TextView) findViewById(R.id.key_hbdbqk_value);// 环保达标情况
//		tvVehicle_ccrq = (TextView) findViewById(R.id.key_ccrq_value);// 出厂日期
		tvVehicle_clyt = (TextView) findViewById(R.id.key_clyt_value);// 车辆用途
		tvVehicle_ytsx = (TextView) findViewById(R.id.key_ytsx_value);// 用途属性
		tvVehicle_xszbh = (TextView) findViewById(R.id.key_xszbh_value);// 行驶证证芯编号
		tvVehicle_jyhgbzbh = (TextView) findViewById(R.id.key_jyhgbzbh_value);// 检验合格标志
		tvVehicle_lxdh = (TextView) findViewById(R.id.key_lxdh_value);// 检验合格标志
		tvVehicle_lxdz = (TextView) findViewById(R.id.key_lxdz_value);// 检验合格标志
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

		platenum.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN) {
					if (keybord == null)
						keybord = new KeyboardUtil(SearchVehicleInfoActivity.this, platenum);
					keybord.showKeyboard();
				}
				return false;
			}
		});
	}

	private void setViewData(VehicleInfoX vehicleInfo) {
		tvVehicle_xh.setText(getIsNull(vehicleInfo.xh));// 机动车序号
		tvVehicle_hpzl.setText(getIsNull(DBManager.getInstance().getNameByTypeAndCodeFromDic("PLATE_TYPE", vehicleInfo.hpzl)));// 号牌种类
		tvVehicle_hphm.setText(getIsNull(vehicleInfo.hphm));// 号牌号码
		tvVehicle_clpp1.setText(getIsNull(vehicleInfo.clpp1));// 中文品牌
		tvVehicle_clxh.setText(getIsNull(vehicleInfo.clxh));// 车辆型号
//		tvVehicle_clpp2.setText(getIsNull(clpp2));// 英文品牌
//		tvVehicle_gcjk.setText(getIsNull(gcjk));// 国产/进口
//		tvVehicle_zzg.setText(getIsNull(zzg));// 制造国
//		tvVehicle_zzcmc.setText(getIsNull(zzcmc));// 制造厂名称
		tvVehicle_clsbdh.setText(getIsNull(vehicleInfo.clsbdh));// 车辆识别代号
		tvVehicle_fdjh.setText(getIsNull(vehicleInfo.fdjh));// 发动机号
		tvVehicle_cllx.setText(getIsNull(DBManager.getInstance().getNameByTypeAndCodeFromDic("JTFS", vehicleInfo.cllx)));// 车辆类型
		tvVehicle_csys.setText(getIsNull(DBManager.getInstance().getNameByTypeAndCodeFromDic("CAR_COLOR", vehicleInfo.csys)));// 车身颜色
		tvVehicle_syxz.setText(getIsNull(DBManager.getInstance().getNameByTypeAndCodeFromDic("CAR_RUN_TYPE", vehicleInfo.syxz)));// 使用性质
//		tvVehicle_sfzmhm.setText(getIsNull(sfzmhm));// 身份证明号码
//		tvVehicle_sfzmmc.setText(getIsNull(sfzmmc));// 身份证明名称
		tvVehicle_syr.setText(getIsNull(vehicleInfo.syr));// 机动车所有人
		tvVehicle_ccdjrq.setText(getIsNull(vehicleInfo.ccdjrq));// 初次登记日期
		tvVehicle_djrq.setText(getIsNull(vehicleInfo.djrq));// 最近定检日期
		tvVehicle_yxqz.setText(getIsNull(vehicleInfo.yxqz));// 检验有效期止
		tvVehicle_qzbfqz.setText(getIsNull(vehicleInfo.qzbfqz));// 强制报废期止
//		tvVehicle_fzjg.setText(getIsNull(fzjg));// 发证机关
//		tvVehicle_glbm.setText(getIsNull(glbm));// 管理部门
		tvVehicle_bxzzrq.setText(getIsNull(vehicleInfo.bxzzrq));// 保险终止日期
		tvVehicle_zt.setText(getIsNull(GetCarStatus(vehicleInfo.zt)));// 机动车状态
		tvVehicle_dybj.setText(getIsNull(vehicleInfo.dybj.equals("0") ? "未抵押":"已抵押"));// 抵押标记
//		tvVehicle_fdjxh.setText(getIsNull(fdjxh));// 发动机型号
//		tvVehicle_rlzl.setText(getIsNull(rlzl));// 燃料种类
//		tvVehicle_pl.setText(getIsNull(pl));// 排量
//		tvVehicle_gl.setText(getIsNull(gl));// 功率
//		tvVehicle_zxxs.setText(getIsNull(zxxs));// 转向形式
//		tvVehicle_cwkc.setText(getIsNull(cwkc));// 车外廓长
//		tvVehicle_cwkk.setText(getIsNull(cwkk));// 车外廓宽
//		tvVehicle_cwkg.setText(getIsNull(cwkg));// 车外廓高
//		tvVehicle_hxnbcd.setText(getIsNull(hxnbcd));// 货箱内部长度
//		tvVehicle_hxnbkd.setText(getIsNull(hxnbkd));// 货箱内部宽度
//		tvVehicle_hxnbgd.setText(getIsNull(hxnbgd));// 货箱内部高度
//		tvVehicle_gbthps.setText(getIsNull(gbthps));// 钢板弹簧片数
//		tvVehicle_zs.setText(getIsNull(zs));// 轴数
//		tvVehicle_zj.setText(getIsNull(zj));// 轴距
//		tvVehicle_qlj.setText(getIsNull(qlj));// 前轮距
//		tvVehicle_hlj.setText(getIsNull(hlj));// 后轮距
//		tvVehicle_ltgg.setText(getIsNull(ltgg));// 轮胎规格
//		tvVehicle_lts.setText(getIsNull(lts));// 轮胎数
//		tvVehicle_zzl.setText(getIsNull(zzl));// 总质量
//		tvVehicle_zbzl.setText(getIsNull(zbzl));// 整备质量
		tvVehicle_hdzzl.setText(getIsNull(vehicleInfo.hdzzl));// 核定载质量
		tvVehicle_hdzk.setText(getIsNull(vehicleInfo.hdzk));// 核定载客
//		tvVehicle_zqyzl.setText(getIsNull(zqyzl));// 准牵引总质量
//		tvVehicle_qpzk.setText(getIsNull(qpzk));// 驾驶室前排载客人数
//		tvVehicle_hpzk.setText(getIsNull(hpzk));// 驾驶室后排载客人数
//		tvVehicle_hbdbqk.setText(getIsNull(hbdbqk));// 环保达标情况
//		tvVehicle_ccrq.setText(getIsNull(ccrq));// 出厂日期
		tvVehicle_clyt.setText(getIsNull(vehicleInfo.clyt));// 车辆用途
		tvVehicle_ytsx.setText(getIsNull(vehicleInfo.ytsx));// 用途属性
		tvVehicle_xszbh.setText(getIsNull(vehicleInfo.xszbh));// 行驶证证芯编号
		tvVehicle_jyhgbzbh.setText(getIsNull(vehicleInfo.jyhgbzbh));// 检验合格标志
	}
	private void getCarPhone(String driverLicenseNumber){
		DcNetWorkUtils.searchDriver(this, driverLicenseNumber, handler);
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
	public void onClick(View v) {
		if (keybord != null && keybord.isShow())
			keybord.hideKeyboard();

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
				if (!Tools.isCarnumberNO(carPlateNumber, carPlateType)) {
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
			if (!Tools.isCarnumberNO(carPlateNumber, carPlateType )) {
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

	private void showWaterMark() {
		WaterMark water_mark = (WaterMark) findViewById(R.id.water_mark);
		water_mark.setWaterStr(SystemCfg.getDepartment(this) + " " + SystemCfg.getPoliceName(this));
		water_mark.setVisibility(View.VISIBLE);
	}
}
