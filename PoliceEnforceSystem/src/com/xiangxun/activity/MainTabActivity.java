package com.xiangxun.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.xiangxun.activity.Daily.DailyServiceActivity;
import com.xiangxun.activity.home.HomeActivity;
import com.xiangxun.activity.tool.ToolsActivity;
import com.xiangxun.activity.warn.WarnActivity;
import com.xiangxun.cfg.SystemCfg;
import com.xiangxun.common.InfoCache;
import com.xiangxun.request.Api;
import com.xiangxun.util.ConstantStatus;
import com.xiangxun.util.NetUtils;
import com.xiangxun.widget.MsgDialog;
import com.xiangxun.widget.MsgToast;
import com.xiangxun.widget.UpdateDialog;

import org.linphone.Contact;
import org.linphone.FragmentsAvailable;
import org.linphone.InCallActivity;
import org.linphone.IncomingCallActivity;
import org.linphone.LinphoneManager;
import org.linphone.LinphonePreferences;
import org.linphone.LinphoneService;
import org.linphone.core.LinphoneAuthInfo;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCall.State;
import org.linphone.core.LinphoneChatMessage;
import org.linphone.core.LinphoneChatRoom;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCore.RegistrationState;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneCoreFactory;
import org.linphone.core.LinphoneCoreListener.LinphoneCallStateListener;
import org.linphone.core.LinphoneCoreListener.LinphoneMessageListener;
import org.linphone.core.LinphoneCoreListener.LinphoneRegistrationStateListener;
import org.linphone.core.LinphoneFriend;
import org.linphone.core.LinphoneProxyConfig;
import org.linphone.mediastream.Log;
import org.linphone.setup.RemoteProvisioningLoginActivity;
import org.linphone.setup.SetupActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.content.Intent.ACTION_MAIN;

@SuppressWarnings("deprecation")
public class MainTabActivity extends TabActivity implements OnTabChangeListener, OnGestureListener, LinphoneCallStateListener, LinphoneMessageListener, LinphoneRegistrationStateListener {
	private GestureDetector gestureDetector;
	private CustomTabHost tabHost;
	private static final int FLEEP_DISTANCE = 120;
	private int currentTabID = 0;
	private static MainTabActivity instance;
	public static final String PREF_FIRST_LAUNCH = "pref_first_launch";
	private static final int SETTINGS_ACTIVITY = 123;
	private static final int FIRST_LOGIN_ACTIVITY = 101;
	private static final int REMOTE_PROVISIONING_LOGIN_ACTIVITY = 102;
	private static final int CALL_ACTIVITY = 19;
	private FragmentsAvailable currentFragment, nextFragment;
	private boolean preferLinphoneContacts = false, isAnimationDisabled = false, isContactPresenceDisabled = true;
	private OrientationEventListener mOrientationHelper;
	private UpdateDialog updateDialog;

	public static final boolean isInstanciated() {
		return instance != null;
	}

	public static final MainTabActivity instance() {
		if (instance != null)
			return instance;
		throw new RuntimeException("MainTabActivity not instantiated yet");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabmain);
		instance = this;
		tabHost = (CustomTabHost) findViewById(android.R.id.tabhost);
		tabHost.setOnTabChangedListener(this);
		createTab1();
		createTab2();
		createTab3();
		createTab4();
		createTab5();
		gestureDetector = new GestureDetector(this);
		new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (gestureDetector.onTouchEvent(event)) {
					return true;
				}
				return false;
			}
		};
		if (isTablet() && getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else if (!isTablet() && getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		boolean useFirstLoginActivity = getResources().getBoolean(R.bool.display_account_wizard_at_first_start);
		if (LinphonePreferences.instance().isProvisioningLoginViewEnabled()) {
			Intent wizard = new Intent();
			wizard.setClass(this, RemoteProvisioningLoginActivity.class);
			wizard.putExtra("Domain", LinphoneManager.getInstance().wizardLoginViewDomain);
			startActivityForResult(wizard, REMOTE_PROVISIONING_LOGIN_ACTIVITY);
		} else if (useFirstLoginActivity && LinphonePreferences.instance().isFirstLaunch()) {
			if (LinphonePreferences.instance().getAccountCount() > 0) {
				LinphonePreferences.instance().firstLaunchSuccessful();
			} else {
				startActivityForResult(new Intent().setClass(this, SetupActivity.class), FIRST_LOGIN_ACTIVITY);
			}
		}
		if (NetUtils.isNetworkAvailable(this)) {
//			DcNetWorkUtils.getVersoin(false, handler, this);
		}
	}

	private void createTab1() {
		TabHost.TabSpec localTabSpec1 = this.tabHost.newTabSpec("0");
		View localView = getLayoutInflater().inflate(R.layout.tab_indicator, null);
		ImageView localImageView = (ImageView) localView.findViewById(R.id.icon);
		TextView localTextView = (TextView) localView.findViewById(R.id.title);
		localImageView.setImageResource(R.drawable.home_tab_background_selector);
		localTextView.setText("首页");
		TabHost.TabSpec localTabSpec2 = localTabSpec1.setIndicator(localView);
		Intent localIntent = new Intent(this, HomeActivity.class);
		localTabSpec2.setContent(localIntent);
		this.tabHost.addTab(localTabSpec1);
	}

	public void createTab2() {
		TabHost.TabSpec localTabSpec1 = this.tabHost.newTabSpec("1");
		View localView = getLayoutInflater().inflate(R.layout.tab_indicator, null);
		ImageView localImageView = (ImageView) localView.findViewById(R.id.icon);
		TextView localTextView = (TextView) localView.findViewById(R.id.title);
		localImageView.setImageResource(R.drawable.zhifa_tab_background_selector);
		localTextView.setText(R.string.zf);
		TabHost.TabSpec localTabSpec2 = localTabSpec1.setIndicator(localView);
		Intent localIntent = new Intent(this, LawEnforcementActivity.class);
		localTabSpec2.setIndicator(localView).setContent(localIntent);
		this.tabHost.addTab(localTabSpec1);
	}

	private void createTab3() {
		TabHost.TabSpec localTabSpec1 = this.tabHost.newTabSpec("2");
		View localView = getLayoutInflater().inflate(R.layout.tab_indicator, null);
		ImageView localImageView = (ImageView) localView.findViewById(R.id.icon);
		TextView localTextView = (TextView) localView.findViewById(R.id.title);
		localImageView.setImageResource(R.drawable.yujing_tab_background_selector);
		localTextView.setText(R.string.yj);
		TabHost.TabSpec localTabSpec2 = localTabSpec1.setIndicator(localView);
		Intent localIntent = new Intent(this, WarnActivity.class);
		localTabSpec2.setContent(localIntent);
		this.tabHost.addTab(localTabSpec1);

	}

	private void createTab4() {
		TabHost.TabSpec localTabSpec1 = this.tabHost.newTabSpec("3");
		View localView = getLayoutInflater().inflate(R.layout.tab_indicator, null);
		ImageView localImageView = (ImageView) localView.findViewById(R.id.icon);
		TextView localTextView = (TextView) localView.findViewById(R.id.title);
		localImageView.setImageResource(R.drawable.qinwu_tab_background_selector);
		localTextView.setText(R.string.qw);
		TabHost.TabSpec localTabSpec2 = localTabSpec1.setIndicator(localView);
		Intent localIntent = new Intent(this, DailyServiceActivity.class);
		localTabSpec2.setContent(localIntent);
		this.tabHost.addTab(localTabSpec1);
	}

	private void createTab5() {
		TabHost.TabSpec localTabSpec1 = this.tabHost.newTabSpec("4");
		View localView = getLayoutInflater().inflate(R.layout.tab_indicator, null);
		ImageView localImageView = (ImageView) localView.findViewById(R.id.icon);
		TextView localTextView = (TextView) localView.findViewById(R.id.title);
		localImageView.setImageResource(R.drawable.gongju_tab_background_selector);
		localTextView.setText(R.string.gj);
		TabHost.TabSpec localTabSpec2 = localTabSpec1.setIndicator(localView);
		Intent localIntent = new Intent(this, ToolsActivity.class);
		localTabSpec2.setContent(localIntent);
		this.tabHost.addTab(localTabSpec1);
	}

	@Override
	public void onTabChanged(String tabId) {
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if (gestureDetector.onTouchEvent(event)) {
			event.setAction(MotionEvent.ACTION_CANCEL);
		}
		return super.dispatchTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		System.out.println("velocityX:" + velocityX);
		if (Math.abs(velocityX) >= 2000) {
			if (e1.getX() - e2.getX() <= (-FLEEP_DISTANCE)) {
				currentTabID = tabHost.getCurrentTab() - 1;
				if (currentTabID < 0) {
					currentTabID = tabHost.getTabCount() - 1;
				}
			} else if (e1.getX() - e2.getX() >= FLEEP_DISTANCE) {
				currentTabID = tabHost.getCurrentTab() + 1;
				if (currentTabID >= tabHost.getTabCount()) {
					currentTabID = 0;
				}
			}
			tabHost.setCurrentTab(currentTabID);
		}

		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public void onBackPressed() {
		if ((System.currentTimeMillis() - exitTime) > 2000) {
			MsgToast.geToast().setMsg("再按一次退出程序");
			exitTime = System.currentTimeMillis();
		} else {
			finish();
		}
	}

	private long exitTime = 0;

	@Override
	protected void onDestroy() {
		SystemCfg.setLinphoneStats(instance, 0);
		if (mOrientationHelper != null) {
			mOrientationHelper.disable();
			mOrientationHelper = null;
		}
		instance = null;
		unbindDrawables(findViewById(R.id.topLayout));
		//stopService(new Intent(Intent.ACTION_MAIN).setClass(this, LinphoneService.class));
		super.onDestroy();
	}

	private boolean isTablet() {
		return getResources().getBoolean(R.bool.isTablet);
	}

	@Override
	public void registrationState(LinphoneCore lc, LinphoneProxyConfig proxy, RegistrationState state, String smessage) {
		if (state.equals(RegistrationState.RegistrationCleared)) {
			if (lc != null) {
				LinphoneAuthInfo authInfo = lc.findAuthInfo(proxy.getIdentity(), proxy.getRealm(), proxy.getDomain());
				if (authInfo != null)
					lc.removeAuthInfo(authInfo);
			}
		}
	}

	@Override
	public void callState(LinphoneCore lc, LinphoneCall call, State state, String message) {
		if (state == State.IncomingReceived) {
			startActivity(new Intent(this, IncomingCallActivity.class));
		} else if (state == State.OutgoingInit) {
			if (call.getCurrentParamsCopy().getVideoEnabled()) {
				startVideoActivity(call);
			} else {
				startIncallActivity(call);
			}
		} else if (state == State.CallEnd || state == State.Error || state == State.CallReleased) {
			// Convert LinphoneCore message for internalization
			if (message != null && message.equals("Call declined.")) {
				displayCustomToast(getString(R.string.error_call_declined), Toast.LENGTH_LONG);
			} else if (message != null && message.equals("Not Found")) {
				displayCustomToast(getString(R.string.error_user_not_found), Toast.LENGTH_LONG);
			} else if (message != null && message.equals("Unsupported media type")) {
				displayCustomToast(getString(R.string.error_incompatible_media), Toast.LENGTH_LONG);
			}
			resetClassicMenuLayoutAndGoBackToCallIfStillRunning();
		}
	}

	public void resetClassicMenuLayoutAndGoBackToCallIfStillRunning() {
		if (LinphoneManager.isInstanciated() && LinphoneManager.getLc().getCallsNb() > 0) {
			LinphoneCall call = LinphoneManager.getLc().getCalls()[0];
			if (call.getState() == LinphoneCall.State.IncomingReceived) {
				startActivity(new Intent(this, IncomingCallActivity.class));
			} else if (call.getCurrentParamsCopy().getVideoEnabled()) {
				startVideoActivity(call);
			} else {
				startIncallActivity(call);
			}
		}
	}

	public void displayCustomToast(final String message, final int duration) {
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.toast, (ViewGroup) findViewById(R.id.toastRoot));

		TextView toastText = (TextView) layout.findViewById(R.id.toastMessage);
		toastText.setText(message);

		final Toast toast = new Toast(getApplicationContext());
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(duration);
		toast.setView(layout);
		toast.show();
	}

	public boolean newFriend(Contact contact, String sipUri) {
		LinphoneFriend friend = LinphoneCoreFactory.instance().createLinphoneFriend(sipUri);
		friend.enableSubscribes(true);
		friend.setIncSubscribePolicy(LinphoneFriend.SubscribePolicy.SPAccept);
		try {
			LinphoneManager.getLc().addFriend(friend);
			contact.setFriend(friend);
			return true;
		} catch (LinphoneCoreException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 前提:Activity已经启动过,处于当前应用的Activity堆栈中
	 * 1.当Activity的LaunchMode为SingleTop时，如果Activity在栈顶
	 * ,且现在要再启动Activity，这时会调用onNewIntent()方法
	 * 2.当Activity的LaunchMode为SingleInstance
	 * ,SingleTask时,如果已经Activity已经在堆栈中，那么此时会调用onNewIntent()方法
	 * 3.当Activity的LaunchMode为Standard时
	 * ，由于每次启动Activity都是启动新的实例，和原来启动的没关系，所以不会调用原来Activity的onNewIntent方法
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	public void exit() {
		finish();
//		stopService(new Intent(ACTION_MAIN).setClass(this, LinphoneService.class));
	}

	private void updateAnimationsState() {
		isAnimationDisabled = getResources().getBoolean(R.bool.disable_animations) || !LinphonePreferences.instance().areAnimationsEnabled();
		isContactPresenceDisabled = !getResources().getBoolean(R.bool.enable_linphone_friends);
	}

	public boolean isAnimationDisabled() {
		return isAnimationDisabled;
	}

	public boolean isContactPresenceDisabled() {
		return isContactPresenceDisabled;
	}

	@SuppressLint("SimpleDateFormat")
	private String secondsToDisplayableString(int secs) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		cal.set(0, 0, 0, 0, 0, secs);
		return dateFormat.format(cal.getTime());
	}

	public void applyConfigChangesIfNeeded() {
		if (nextFragment != FragmentsAvailable.SETTINGS && nextFragment != FragmentsAvailable.ACCOUNT_SETTINGS) {
			updateAnimationsState();
		}
	}

	public void setLinphoneContactsPrefered(boolean isPrefered) {
		preferLinphoneContacts = isPrefered;
	}

	public boolean isLinphoneContactsPrefered() {
		return preferLinphoneContacts;
	}

	public boolean removeFriend(Contact contact, String sipUri) {
		LinphoneFriend friend = LinphoneManager.getLc().findFriendByAddress(sipUri);
		if (friend != null) {
			friend.enableSubscribes(false);
			LinphoneManager.getLc().removeFriend(friend);
			contact.setFriend(null);
			return true;
		}
		return false;
	}

	public FragmentsAvailable getCurrentFragment() {
		return currentFragment;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_FIRST_USER && requestCode == SETTINGS_ACTIVITY) {
			if (data.getExtras().getBoolean("Exit", false)) {
				exit();
			}
		} else if (resultCode == Activity.RESULT_FIRST_USER && requestCode == CALL_ACTIVITY) {
			getIntent().putExtra("PreviousActivity", CALL_ACTIVITY);
			resetClassicMenuLayoutAndGoBackToCallIfStillRunning();
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	protected void onPause() {
		LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
		if (lc != null) {
			lc.removeListener(this);
		}

		getIntent().putExtra("PreviousActivity", 0);
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!LinphoneService.isReady()) {
			startService(new Intent(ACTION_MAIN).setClass(this, LinphoneService.class));
		}

		LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
		if (lc != null) {
			lc.addListener(this);
		}
		LinphoneManager.getInstance().changeStatusToOnline();

		if (getIntent().getIntExtra("PreviousActivity", 0) != CALL_ACTIVITY) {
			if (LinphoneManager.getLc().getCalls().length > 0) {
				LinphoneCall call = LinphoneManager.getLc().getCalls()[0];
				LinphoneCall.State callState = call.getState();
				if (callState == State.IncomingReceived) {
					startActivity(new Intent(this, IncomingCallActivity.class));
				} else {

					if (call.getCurrentParamsCopy().getVideoEnabled()) {
						startVideoActivity(call);
					} else {
						startIncallActivity(call);
					}
				}
			}
		}
	}

	private void unbindDrawables(View view) {
		if (view != null && view.getBackground() != null) {
			view.getBackground().setCallback(null);
		}
		if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				unbindDrawables(((ViewGroup) view).getChildAt(i));
			}
			((ViewGroup) view).removeAllViews();
		}
	}

	@Override
	public void messageReceived(LinphoneCore lc, LinphoneChatRoom cr, LinphoneChatMessage message) {
	}

	public void startVideoActivity(LinphoneCall currentCall) {
		Intent intent = new Intent(this, InCallActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		intent.putExtra("VideoEnabled", true);
		startOrientationSensor();
		startActivityForResult(intent, CALL_ACTIVITY);
	}

	public void startIncallActivity(LinphoneCall currentCall) {
		Intent intent = new Intent(this, InCallActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		intent.putExtra("VideoEnabled", false);
		startOrientationSensor();
		startActivityForResult(intent, CALL_ACTIVITY);
	}

	private synchronized void startOrientationSensor() {
		if (mOrientationHelper == null) {
			mOrientationHelper = new LocalOrientationEventListener(this);
		}
		mOrientationHelper.enable();
	}

	public int mAlwaysChangingPhoneAngle = -1;

	private class LocalOrientationEventListener extends OrientationEventListener {
		public LocalOrientationEventListener(Context context) {
			super(context);
		}

		@Override
		public void onOrientationChanged(final int o) {
			if (o == OrientationEventListener.ORIENTATION_UNKNOWN) {
				return;
			}

			int degrees = 270;
			if (o < 45 || o > 315)
				degrees = 0;
			else if (o < 135)
				degrees = 90;
			else if (o < 225)
				degrees = 180;

			if (mAlwaysChangingPhoneAngle == degrees) {
				return;
			}
			mAlwaysChangingPhoneAngle = degrees;

			Log.d("Phone orientation changed to ", degrees);
			int rotation = (360 - degrees) % 360;
			LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
			if (lc != null) {
				lc.setDeviceRotation(rotation);
				LinphoneCall currentCall = lc.getCurrentCall();
				if (currentCall != null && currentCall.cameraEnabled() && currentCall.getCurrentParamsCopy().getVideoEnabled()) {
					lc.updateCall(currentCall, null);
				}
			}
		}
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		private MsgDialog mUpdateDialog;

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case ConstantStatus.updateSuccess:// 获取版本更新数据成功
				MsgToast.geToast().cancel();
				String[] arrayStr = { "发现新版本,请更新!" };
				StringBuilder sb = new StringBuilder();
//				for (int i = 0, len = arrayStr.length; i < len; i++) {
//					sb.append(new StringBuilder(String.valueOf(i + 1)).append(".").toString());
//					sb.append(arrayStr[i]);
//					sb.append(i == len - 1 ? "\n" : "\n\n");
//				}
				sb.append("发现新版本,请更新!");
				String url = new Api().urlHost + InfoCache.getInstance().getmData().getFileAddress().trim();
				if (updateDialog == null) {
					updateDialog = new UpdateDialog(MainTabActivity.this, R.style.updateDialog, InfoCache.getInstance().getmData().getCurrentVersionsCode(), sb.toString(), url);
				}
				updateDialog.setCancelable(true);
				updateDialog.show();
				break;
			case ConstantStatus.updateFalse:// 版本更新
				if (mUpdateDialog == null) {
					mUpdateDialog = new MsgDialog(MainTabActivity.this);
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
	protected void onStart() {
		boolean isFlag = getIntent().getBooleanExtra("isFlag", false);
		if(!isFlag){
			SystemCfg.setLinphoneStats(instance, 119);
		}
		super.onStart();
	}

}