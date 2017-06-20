package com.xiangxun.activity.setting;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiangxun.activity.R;
import com.xiangxun.activity.mine.MyInformationActivity;
import com.xiangxun.app.BaseFragmentActivity;
import com.xiangxun.cfg.SystemCfg;
import com.xiangxun.common.InfoCache;
import com.xiangxun.db.DBManager;
import com.xiangxun.request.Api;
import com.xiangxun.request.DcNetWorkUtils;
import com.xiangxun.util.ConstantStatus;
import com.xiangxun.util.MyUtils;
import com.xiangxun.util.NetUtils;
import com.xiangxun.widget.LoadDialog;
import com.xiangxun.widget.MsgDialog;
import com.xiangxun.widget.MsgToast;
import com.xiangxun.widget.PublishSelectClearDialog;
import com.xiangxun.widget.PublishSelectClearDialog.SelectclearDateItemClick;
import com.xiangxun.widget.PublishSelectLawDialog;
import com.xiangxun.widget.PublishSelectLawDialog.SelectLawDateItemClick;
import com.xiangxun.widget.TitleView;
import com.xiangxun.widget.UpdateDialog;

import java.io.File;

public class SettingActivity extends BaseFragmentActivity implements android.view.View.OnClickListener, SelectclearDateItemClick {
	private TitleView titleView;
	private int isFlag;
	private RelativeLayout mRlChangePaw;
	private RelativeLayout mRlRegister;
	private RelativeLayout mRlSystemSet;
	private RelativeLayout mRlClearData;
	private RelativeLayout mRlResolutionFont;
	private RelativeLayout mRlUpdata;
	private RelativeLayout mRlVedio;
	private RelativeLayout mRlWorkRoad;
	private TextView mRlWorkText;
	private RelativeLayout mRlPrint;
	private TextView mTvUpdate;
	private PublishSelectClearDialog clearDialog;
	private PublishSelectLawDialog workRoadDialog;
	private MsgDialog msgDialog;
	/** 更新版本提示框 */
	private UpdateDialog updateDialog;// 更新版本提示框
	private int deleteDay = 0;
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		private MsgDialog mUpdateDialog;
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case ConstantStatus.updateSuccess:// 获取版本更新数据成功
				MsgToast.geToast().cancel();
				String[] arrayStr = {"发现新版本,请更新!"};
				StringBuilder sb = new StringBuilder();
//				for (int i = 0, len = arrayStr.length; i < len; i++) {
//					sb.append(new StringBuilder(String.valueOf(i + 1)).append(".").toString());
//					sb.append(arrayStr[i]);
//					sb.append(i == len - 1 ? "\n" : "\n\n");
//				}
				sb.append("发现新版本,请更新!");
				String url = new Api().urlHost + InfoCache.getInstance().getmData().getFileAddress().trim();
				if (updateDialog == null) {
					updateDialog = new UpdateDialog(SettingActivity.this, R.style.updateDialog, InfoCache.getInstance().getmData().getCurrentVersionsCode(), sb.toString(), url);
				}
				updateDialog.setCancelable(true);
				updateDialog.show();
				showNew();
				break;
			case ConstantStatus.updateFalse:// 版本更新
				if (mUpdateDialog == null) {
					mUpdateDialog = new MsgDialog(SettingActivity.this);
					mUpdateDialog.setTiele(Html.fromHtml(getText(R.string.update_tips_html).toString()));
					mUpdateDialog.setMsg(getString(R.string.latest_version_please_look));
					mUpdateDialog.setOnlyOneBut();
				}
				mUpdateDialog.show();
				break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tools_setting_info_layout);
		isFlag = getIntent().getIntExtra("isFlag", 0);
		initView();
		initData();
		initListener();
	}

	@Override
	public void initView() {
		titleView = (TitleView) findViewById(R.id.tv_comm_title);
		mRlChangePaw = (RelativeLayout) findViewById(R.id.rl_change_password);
		mRlRegister = (RelativeLayout) findViewById(R.id.rl_register);
		mRlSystemSet = (RelativeLayout) findViewById(R.id.rl_system_setting);
		mRlClearData = (RelativeLayout) findViewById(R.id.rl_more_clear);
		mRlResolutionFont = (RelativeLayout) findViewById(R.id.rl_resolution_font);
		mRlUpdata = (RelativeLayout) findViewById(R.id.rl_more_update);
		mRlVedio = (RelativeLayout) findViewById(R.id.rl_video_setting);
		mTvUpdate = (TextView) this.findViewById(R.id.tv_more_version);
		mRlWorkRoad = (RelativeLayout) findViewById(R.id.rl_more_work);
		mRlWorkText = (TextView) findViewById(R.id.tv_more_work);
		mRlPrint = (RelativeLayout) findViewById(R.id.rl_print_setting);

		if (isFlag == 1) {
			mRlChangePaw.setVisibility(View.GONE);
			mRlRegister.setVisibility(View.GONE);
			mRlClearData.setVisibility(View.GONE);
			mRlUpdata.setVisibility(View.GONE);
			titleView.setLeftBackOneListener(R.drawable.back_normal, this);
		} else {
			titleView.setLeftBackOneListener(R.drawable.back_normal, this);
			titleView.setRightViewRightOneListener(R.drawable.user_normal, this);
		}
	}

	@Override
	public void initData() {
		titleView.setTitle(R.string.xtsz);
		String[] clearItems = new String[] { "7天前", "15天前", "30天前", "全部" };
		clearDialog = new PublishSelectClearDialog(this, clearItems, "清除数据");
//		String[] workRoadItems = new String[] { getResources().getString(R.string.workroad_city), getResources().getString(R.string.workroad_speed)};
//		workRoadDialog = new PublishSelectLawDialog(this, workRoadItems, mRlWorkText, getResources().getString(R.string.workroad));
//		mRlWorkText.setText(SystemCfg.getWorkRoad(this));
		showNew();
	}

	@Override
	public void initListener() {
		mRlChangePaw.setOnClickListener(this);
		mRlRegister.setOnClickListener(this);
		mRlSystemSet.setOnClickListener(this);
		mRlClearData.setOnClickListener(this);
		mRlResolutionFont.setOnClickListener(this);
		mRlUpdata.setOnClickListener(this);
		mRlVedio.setOnClickListener(this);
		clearDialog.setSelectclearDateItemClick(this);
		mRlWorkRoad.setOnClickListener(this);
		mRlPrint.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_view_operation_imageview_right:
			Intent login = new Intent(this, MyInformationActivity.class);
			startActivity(login);
			break;
		case R.id.title_view_back_img:
			onBackPressed();
			break;
		case R.id.rl_change_password:
			startActivity(new Intent(SettingActivity.this, ChangePasswordActivity.class));
			break;
		case R.id.rl_register:
			startActivity(new Intent(SettingActivity.this, RegisterActivity.class));
			break;
		case R.id.rl_system_setting:
			startActivity(new Intent(SettingActivity.this, SystemSettingActivity.class));
			break;
		case R.id.rl_more_clear:
			clearDialog.show();
			break;
		case R.id.rl_resolution_font:
			startActivity(new Intent(SettingActivity.this, ResolutionFontSetActivity.class));
			break;
		case R.id.rl_more_update:
			if (NetUtils.isNetworkAvailable(this)) {
				MsgToast.geToast().setMsg("检查更新中");
				DcNetWorkUtils.getVersoin(true, handler, this);
			} else {
				MsgToast.geToast().setMsg("无网络,请检查网络是否正常连接!");
			}
			break;
		case R.id.rl_more_work:
			workRoadDialog.show();
			break;
		case R.id.rl_print_setting:
			startActivity(new Intent(SettingActivity.this, PrintSettingActivity.class));
			break;
		case R.id.dia_tv_but1:
			msgDialog.dismiss();
			break;
		case R.id.dia_tv_but2:
			msgDialog.dismiss();
			final LoadDialog loadDialog = new LoadDialog(this);
			loadDialog.setTitle("正在处理,请稍后...");
			loadDialog.show();
			if (deleteDay == 0) {
				DBManager.getInstance().clearVioData();
				DBManager.getInstance().clearEnforceData();
				DBManager.getInstance().clearFieldpunishData();
				DBManager.getInstance().clearDutyData();
				DBManager.getInstance().clearAccidentData();
				DBManager.getInstance().clearLawCheckData();
				DBManager.getInstance().clearWarnData();
				//删除所有图片
				File pictures = new File(Environment.getExternalStorageDirectory() + "/xiangxun/vio");
				MyUtils.deleteFile(pictures);
				File picturesbackup = new File(Environment.getExternalStorageDirectory() + "/xiangxun/vioback");
				MyUtils.deleteFile(picturesbackup);
			} else {
				DBManager.getInstance().clearInSomedayVioData(deleteDay);
				DBManager.getInstance().clearInSomedayEnforceData(deleteDay);
				DBManager.getInstance().clearInSomedayFieldpunishData(deleteDay);
				DBManager.getInstance().clearInSomedayDutyData(deleteDay);
				DBManager.getInstance().clearInSomedayAccidentData(deleteDay);
				DBManager.getInstance().clearInSomedayLawCheckData(deleteDay);
				DBManager.getInstance().clearInSomedayWarnData(deleteDay);
			}

			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					loadDialog.dismiss();
					MsgToast.geToast().setMsg("删除成功");
				}
			}, 2000);
			break;
		case R.id.rl_video_setting:
			startActivity(new Intent(SettingActivity.this, VideoSettingActivity.class));
			break;
		}
	}
	
	private void showNew() {
		if (InfoCache.getInstance().isNewVer()) {
			mTvUpdate.setText(null);
			mTvUpdate.setBackgroundResource(R.drawable.more_update);
		} else {
			mTvUpdate.setText(new StringBuilder(getString(R.string.curr_ver)).append(InfoCache.getInstance().getAppVersionName(this)));
		}
	}

	@Override
	public void clearDateOnClick(int deleteDay) {
		this.deleteDay = deleteDay;
		msgDialog = new MsgDialog(this);
		msgDialog.setTitle("确定要删除历史数据？");
		if(deleteDay == 0){
			msgDialog.setMsg2("删除全部数据");
		}else{
			msgDialog.setMsg2("删除" + deleteDay + "天前的数据");
		}
		msgDialog.setButLeftListener(this);
		msgDialog.setButRightListener(this);
		msgDialog.show();
	}
}
