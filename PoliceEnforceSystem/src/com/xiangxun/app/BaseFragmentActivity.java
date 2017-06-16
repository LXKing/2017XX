package com.xiangxun.app;

import java.util.ArrayList;
import java.util.Collection;

import org.linphone.InCallActivity;
import org.linphone.IncomingCallActivity;
import org.linphone.LinphoneManager;
import org.linphone.LinphoneUtils;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCore;
import org.linphone.mediastream.Log;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.OrientationEventListener;
import android.view.Surface;

import com.xiangxun.activity.R;
import com.xiangxun.common.ActivityBusiness;
import com.xiangxun.common.LogicManager;

/**
 * @package: com.xiangxun.app
 * @ClassName: BaseFragmentActivity.java
 * @Description:
 * @author: HanGJ
 * @date: 2015-7-29 上午9:45:23
 */
public abstract class BaseFragmentActivity extends FragmentActivity {
	public String mTitleName = "TIELENAME";
	public String actLable = null;
	private static final int CALL_ACTIVITY = 19;
	public OrientationEventListener mOrientationHelper;

	// 加入activity栈
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int rotation = getWindowManager().getDefaultDisplay().getRotation();
		switch (rotation) {
		case Surface.ROTATION_0:
			rotation = 0;
			break;
		case Surface.ROTATION_90:
			rotation = 90;
			break;
		case Surface.ROTATION_180:
			rotation = 180;
			break;
		case Surface.ROTATION_270:
			rotation = 270;
			break;
		}

		try {
			LinphoneManager.getLc().setDeviceRotation(rotation);
		} catch (RuntimeException e) {
			LinphoneManager.createAndStart(this);
			LinphoneManager.getLc().setDeviceRotation(rotation);
			e.printStackTrace();
		}
		mAlwaysChangingPhoneAngle = rotation;
		ActivityBusiness.instance().addBusiness(this);
	}

	// 从栈中除去
	// activity停止时 从volley中移除当前网络任务
	protected void onDestroy() {
		super.onDestroy();
		if (mOrientationHelper != null) {
			mOrientationHelper.disable();
			mOrientationHelper = null;
		}
		LogicManager.getInstance().removeTag(this);
		ActivityBusiness.instance().removeBusiness(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		int4Right();
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
		int4Right();
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		out2Left();
	}

	public void out2Left() {
		// overridePendingTransition(R.anim.new_dync_no,
		// R.anim.new_dync_out_to_left);
		overridePendingTransition(R.anim.activity_nothing, R.anim.activity_out_to_buttom);
	}

	public void int4Right() {
		overridePendingTransition(R.anim.right_in, R.anim.activity_nothing);
	}

	public abstract void initView();

	public abstract void initListener();

	public abstract void initData();
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (LinphoneManager.getLc().getCalls().length > 0) {
			LinphoneCall calls[] = LinphoneManager.getLc().getCalls();
			if (calls.length > 0) {
				LinphoneCall call = calls[0];

				if (call != null && call.getState() != LinphoneCall.State.IncomingReceived) {
					if (call.getCurrentParamsCopy().getVideoEnabled()) {
						startVideoActivity(call);
					} else {
						startIncallActivity(call);
					}
				}
			}

			// If a call is ringing, start incomingcallactivity
			Collection<LinphoneCall.State> incoming = new ArrayList<LinphoneCall.State>();
			incoming.add(LinphoneCall.State.IncomingReceived);
			if (LinphoneUtils.getCallsInState(LinphoneManager.getLc(), incoming).size() > 0) {
				if (InCallActivity.isInstanciated()) {
					InCallActivity.instance().startIncomingCallActivity();
				} else {
					startActivity(new Intent(this, IncomingCallActivity.class));
				}
			}
		}
	}
	
	public void startVideoActivity(LinphoneCall currentCall) {
		Intent intent = new Intent(this, InCallActivity.class);
		intent.putExtra("VideoEnabled", true);
		startOrientationSensor();
		startActivityForResult(intent, CALL_ACTIVITY);
	}

	public void startIncallActivity(LinphoneCall currentCall) {
		Intent intent = new Intent(this, InCallActivity.class);
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

}