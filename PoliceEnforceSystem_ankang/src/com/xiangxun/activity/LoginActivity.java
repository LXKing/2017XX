package com.xiangxun.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.URLSpan;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiangxun.activity.contact.ContactActivity;
import com.xiangxun.activity.setting.SetGuide;
import com.xiangxun.activity.setting.SettingActivity;
import com.xiangxun.app.XiangXunApplication;
import com.xiangxun.bean.LoginInfo;
import com.xiangxun.bean.ResultData.Login;
import com.xiangxun.bean.ResultData.LoginData;
import com.xiangxun.bean.ResultData.UserInfos;
import com.xiangxun.bean.ResultData.ParentInfos;
import com.xiangxun.cfg.SystemCfg;
import com.xiangxun.request.DcNetWorkUtils;
import com.xiangxun.service.GpsService;
import com.xiangxun.service.MainService;
import com.xiangxun.util.ConstantStatus;
import com.xiangxun.util.Logger;
import com.xiangxun.util.NetUtils;
import com.xiangxun.widget.AutoClearEditText;
import com.xiangxun.widget.LoadDialog;
import com.xiangxun.widget.MsgToast;
import com.xiangxun.widget.XSubButton;

import org.linphone.LinphoneService;

import static android.content.Intent.ACTION_MAIN;

public class LoginActivity extends Activity implements android.view.View.OnClickListener {
	private XSubButton mBtnLogin;
	private LinearLayout mll_Username;
	private LinearLayout mll_Password;
	private AutoClearEditText mUsername;
	private AutoClearEditText mPassword;
	// private ImageView mSettings;
	// private ImageView mTakePic;
	private TextView mLoginSet;
	private TextView mLoginGuide;
	private LoadDialog loadDialog;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case ConstantStatus.SUCCESS:
				Intent intent = new Intent(LoginActivity.this, MainTabActivity.class);
				intent.putExtra("isFlag", false);
				startActivity(intent);
				// XiangXunApplication.getInstance().startPushService();
				XiangXunApplication.getInstance().getMainService().clearGPSLimitData();
				LoginActivity.this.finish();
				break;
			case ConstantStatus.FAILED:
				MsgToast.geToast().setMsg(R.string.login_wrong);
				break;
			case ConstantStatus.FAILED_NO_NET:
				MsgToast.geToast().setMsg(R.string.login_no_net);
				break;
			case ConstantStatus.FAILED_NO_USER:
				MsgToast.geToast().setMsg(R.string.login_no_user);
				break;
			case ConstantStatus.loadSuccess:
				LoginData loginData = (LoginData) msg.obj;
				resultData(loginData);
				break;
			case ConstantStatus.loadFailed:
				MsgToast.geToast().setMsg(R.string.login_no_net);
				break;
			case ConstantStatus.NetWorkError:
				MsgToast.geToast().setMsg(R.string.login_no_net);
				break;
			case ConstantStatus.ACCOUNT_NO_MATCH:
				MsgToast.geToast().setLongMsg("您已绑定的IEMI不匹配,请勿更换设备或联系管理员!");
				break;
			case ConstantStatus.ACCOUNT_NO_BOUND:
				MsgToast.geToast().setLongMsg("您的设备未绑定IEMI,请联系管理员!");
				break;
			case ConstantStatus.ACCOUNT_NO_AUTHORITY:
				MsgToast.geToast().setLongMsg("您无权限使用,请联系管理员!");
				break;
			}
			mBtnLogin.setNormal();
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Logger.d("LoginActivity -----> onCreate(savedInstanceState)");
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		setContentView(R.layout.login);
		initView();
		initData();
		initListener();
	}

	public void initView() {
		mBtnLogin = (XSubButton) this.findViewById(R.id.login_btn);
		mll_Username = (LinearLayout) findViewById(R.id.ll_login_user);
		mll_Password = (LinearLayout) findViewById(R.id.ll_login_password);
		mUsername = (AutoClearEditText) this.findViewById(R.id.login_name);
		mPassword = (AutoClearEditText) this.findViewById(R.id.login_password);
		// mSettings = (ImageView) this.findViewById(R.id.settings);
		// mTakePic = (ImageView) this.findViewById(R.id.take_pic);
		mLoginSet = (TextView) this.findViewById(R.id.login_set);
		mLoginGuide = (TextView) this.findViewById(R.id.login_guide);
	}

	public void initData() {
		loadDialog = new LoadDialog(this);
		setUpLoginAnim();
		setUsernameAndPassword();
		if (!NetUtils.isNetworkAvailable(LoginActivity.this)) {
			if (SystemCfg.getUserId(this) != null && !SystemCfg.getUserId(this).equals("")) {
				MsgToast.geToast().setLongMsg(R.string.loginoffline);
			} else {
				MsgToast.geToast().setLongMsg(R.string.netnotavailable);
				mBtnLogin.setEnabled(false);
			}
		}
		mBtnLogin.setViewInit(R.string.mine_login_login, R.string.mine_login_loginning, mUsername, mPassword); // ,
																												// mSettings,
																												// mTakePic);
		if (!ActivityManager.isUserAMonkey()) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					if (!NetUtils.SUCCESS.equals(NetUtils.Ping(SystemCfg.getServerIP(LoginActivity.this)))) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								startActivity(new Intent(Settings.ACTION_APN_SETTINGS));
							}
						});
					}
				}
			}).start();

		}

		String server_set_str = getResources().getString(R.string.loginset);
		SpannableString ss = new SpannableString(server_set_str);
		ss.setSpan(new URLSpan(server_set_str), 0, server_set_str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		mLoginSet.setText(ss);
		String loginGuide_str = getResources().getString(R.string.loginguide);
		SpannableString guide = new SpannableString(loginGuide_str);
		guide.setSpan(new URLSpan(loginGuide_str), 0, loginGuide_str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		mLoginGuide.setText(guide);

		if (ActivityManager.isUserAMonkey()) {
			mUsername.setEnabled(false);
			mPassword.setEnabled(false);
		}
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			MsgToast.geToast().setMsg(R.string.openGps);
			Intent setintent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(setintent);

		} else {
			Intent gpsIntent = new Intent(this, GpsService.class);
			startService(gpsIntent);
		}
		Intent mainService = new Intent(this, MainService.class);
		startService(mainService);
		loadDialog.setTitle("正在初始化中，请稍后...");
		loadDialog.show();
		startService(new Intent(ACTION_MAIN).setClass(this, LinphoneService.class));
		mThread = new ServiceWaitThread();
		mThread.start();
	}

	private ServiceWaitThread mThread;

	private class ServiceWaitThread extends Thread {
		@Override
		public void run() {
			while (!LinphoneService.isReady()) {
				try {
					sleep(30);
				} catch (InterruptedException e) {
					throw new RuntimeException("waiting thread sleep() has been interrupted");
				}
			}
			loadDialog.dismiss();
			mThread = null;
		}
	}

	public void initListener() {
		// mSettings.setOnClickListener(this);
		// mTakePic.setOnClickListener(this);
		mBtnLogin.setOnClickListener(this);
		mLoginSet.setOnClickListener(this);
		mLoginGuide.setOnClickListener(this);
		mUsername.addTextChangedListener(new TextWatcher() {
			int index = 0;

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				mUsername.removeTextChangedListener(this);// 解除文字改变事件
				index = mUsername.getSelectionStart();// 获取光标位置
				if (s.length() == 0) {
					mPassword.setText("");
				}
				mUsername.setSelection(index);// 重新设置光标位置
				mUsername.addTextChangedListener(this);// 重新绑定事件
			}
		});

		mUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus)
					mll_Username.setPressed(true);
				else
					mll_Username.setPressed(false);
			}
		});
		mPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus)
					mll_Password.setPressed(true);
				else
					mll_Password.setPressed(false);
			}
		});
	}

	private void setUsernameAndPassword() {
		if (mUsername == null || mPassword == null)
			return;
		String username = SystemCfg.getAccount(this);
		String password = SystemCfg.getWhitePwd(this);
		if (username.length() > 1 && password.length() > 1) {
			mUsername.setText(username);
			mPassword.setText(password);
		}
	}

	private void setUpLoginAnim() {
		Animation animation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.login_up);
		animation.setAnimationListener(new Animation.AnimationListener() {

			@Override
			public void onAnimationStart(Animation arg0) {
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
			}

			@Override
			public void onAnimationEnd(Animation arg0) {
			}
		});
	}

	private boolean checkInputValidation() {
		if (mUsername == null || mUsername.getText().toString().trim().length() < 1) {
			MsgToast.geToast().setMsg(R.string.checkusername);
			mBtnLogin.setNormal();
			return false;
		}

		if (mPassword == null || mPassword.getText().toString().trim().length() < 1) {
			MsgToast.geToast().setMsg(R.string.checkpassword);
			mBtnLogin.setNormal();
			return false;
		}
		return true;
	}

	private void resultData(LoginData loginData) {
		Login login = loginData.login;
		if (login != null) {
			LoginInfo.sessionId = login.sessionid;
			if (login.res.equals("no_name")) {
				handler.sendEmptyMessage(ConstantStatus.FAILED_NO_USER);
			} else if (login.res.equals("false")) {
				handler.sendEmptyMessage(ConstantStatus.FAILED);
			} else if (login.res.equals("no_match")) {
				handler.sendEmptyMessage(ConstantStatus.ACCOUNT_NO_MATCH);
			} else if (login.res.equals("no_bound")) {
				handler.sendEmptyMessage(ConstantStatus.ACCOUNT_NO_BOUND);
			} else if (login.res.equals("no_authority")) {
				handler.sendEmptyMessage(ConstantStatus.ACCOUNT_NO_AUTHORITY);
			} else {
				UserInfos userInfos = loginData.userinfos;
				if (userInfos != null) {
					SystemCfg.setPoliceName(this, null != userInfos.name ? userInfos.name.toString() : "");
					// 用户ID
					SystemCfg.setUserId(this, null != userInfos.id ? userInfos.id.toString() : "");
					// 车牌识别注册码
					SystemCfg.setRegistCode(this, null != userInfos.regCode ? userInfos.regCode.toString() : "");
					// 警号
					SystemCfg.setPoliceCode(this, null != userInfos.puserCode ? userInfos.puserCode.toString() : "");
					// 部门
					SystemCfg.setDepartmentID(this, null != userInfos.deptid ? userInfos.deptid.toString() : "");
					SystemCfg.setDepartmentCode(this, null != userInfos.code ? userInfos.code.toString() : "");
					SystemCfg.setDepartment(this, null != userInfos.deptName ? userInfos.deptName.toString() : "");
					// 电话
					SystemCfg.setMobile(this, null != userInfos.mobile ? userInfos.mobile.toString() : "");
					// 账户
					SystemCfg.setAccount(this, null != userInfos.account ? userInfos.account.toString() : "");
					// 加密密码
					SystemCfg.setPassword(this, null != userInfos.pwd ? userInfos.pwd.toString() : "");
					// 未加密密码
					SystemCfg.setWhitePwd(this, mPassword.getText().toString());
					// 打印票头
					SystemCfg.setTopName(this, null != userInfos.topName ? userInfos.topName.toString() : "");
					// 复议机关
					SystemCfg.setAgainDiscussDepartment(this, null != userInfos.againDiscussDepartment ? userInfos.againDiscussDepartment.toString() : "");
					// 行政法院
					SystemCfg.setCourtName(this, null != userInfos.courtName ? userInfos.courtName.toString() : "");
					// 处罚地点
					SystemCfg.setPunishAddress(this, null != userInfos.punishAddress ? userInfos.punishAddress.toString() : "");
				}

				// 中队需要保存大队的id, code
				ParentInfos parentInfos = loginData.parentDept;
				if (parentInfos != null && userInfos.deptName.contains("中队")) {
					SystemCfg.setDepartmentID(this, null != parentInfos.id ? parentInfos.id.toString() : "");
					// SystemCfg.setDepartmentCode(this, null !=
					// parentInfos.code ? parentInfos.code.toString() : "");
					SystemCfg.setParentDepartmentName(this, null != parentInfos.name ? parentInfos.name.toString() : "");
				}

				handler.sendEmptyMessage(ConstantStatus.SUCCESS);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_guide:
			if (!ActivityManager.isUserAMonkey()) {
				Intent intent = new Intent(LoginActivity.this, SetGuide.class);
				startActivity(intent);
			}
			break;
		case R.id.login_set:
			// case R.id.settings:
			if (!ActivityManager.isUserAMonkey()) {
				Intent intent = new Intent(LoginActivity.this, SettingActivity.class);
				intent.putExtra("isFlag", 1);
				startActivity(intent);
			}
			break;
		// case R.id.take_pic:
		// startActivity(new Intent(LoginActivity.this, CameraActivity.class));
		// break;
		case R.id.login_btn:
			if (!NetUtils.isNetworkAvailable(LoginActivity.this) && SystemCfg.getUserId(this) != null && !SystemCfg.getUserId(this).equals("")) {
				LoginInfo.isOffLine = true;
				Intent offline = new Intent(LoginActivity.this, MainTabActivity.class);
				startActivity(offline);
				finish();
			} else {
				LoginInfo.isOffLine = false;
				if (checkInputValidation()) {
					mUsername.setClearIconVisible(false);
					mPassword.setClearIconVisible(false);
					String deviceId = XiangXunApplication.getInstance().getDevId();
					DcNetWorkUtils.login(LoginActivity.this, mUsername.getText().toString(), mPassword.getText().toString(), deviceId, handler);
				}
			}
			break;
		}
	}

	@Override
	protected void onStart() {
		Logger.d("LoginActivity -----> onStart()");
		super.onStart();
	}

	@Override
	protected void onRestart() {
		Logger.d("LoginActivity -----> onRestart()");
		super.onRestart();
	}

	@Override
	protected void onResume() {
		Logger.d("LoginActivity -----> onResume()");
		super.onResume();
	}

	@Override
	protected void onPause() {
		Logger.d("LoginActivity -----> onPause()");
		super.onPause();
	}

	@Override
	protected void onStop() {
		Logger.d("LoginActivity -----> onStop()");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Logger.d("LoginActivity -----> onDestroy()");
		super.onDestroy();
	}

}
