/**
 * 
 */
package com.xiangxun.activity;

import wintone.idcard.android.AuthParameterMessage;
import wintone.idcard.android.AuthService;
import wintone.idcard.android.AuthService.authBinder;
import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.xiangxun.widget.MsgToast;

/**
 * @package: com.xiangxun.activity
 * @ClassName: AuthVerificationActivity.java
 * @Description: 注册-授权验证页面
 */
public class AuthVerificationActivity extends Activity {
	private authBinder authBinder;
	private String sn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sn = this.getIntent().getExtras().getString("sn");
		Intent authIntent = new Intent(AuthVerificationActivity.this, AuthService.class);
		bindService(authIntent, authConn, Service.BIND_AUTO_CREATE);
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private ServiceConnection authConn = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			authBinder = (AuthService.authBinder) service;
			try {
				int ReturnAuthority = -1;
				AuthParameterMessage apm = new AuthParameterMessage();
				apm.sn = AuthVerificationActivity.this.sn;// WU9H5VSSDVXYB6KYYI52YYICW
													// WUB7RVSN1JVYHFBYY7P9YYC37
				apm.authfile = "";// /mnt/sdcard/auth/A1000038AB08A2_zj.txt
				ReturnAuthority = authBinder.getIDCardAuth(apm);

				if (ReturnAuthority != 0) {
					MsgToast.geToast().setMsg(getResources().getString(R.string.authorizationFailedReturn) + ReturnAuthority);
				} else {
					MsgToast.geToast().setMsg(R.string.authorizationFailedReturn);
				}
			} catch (Exception e) {
				MsgToast.geToast().setMsg(R.string.authorizationFailed);
			} finally {
				if (authBinder != null) {
					unbindService(authConn);
				}
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			authBinder = null;
		}
	};
}
