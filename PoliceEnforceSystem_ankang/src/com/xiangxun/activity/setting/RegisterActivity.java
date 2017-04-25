package com.xiangxun.activity.setting;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wintone.plateid.AuthService;
import com.xiangxun.activity.R;
import com.xiangxun.app.BaseActivity;
import com.xiangxun.app.XiangXunApplication;
import com.xiangxun.bean.LoginInfo;
import com.xiangxun.bean.ZipExtractorTask;
import com.xiangxun.cfg.SystemCfg;
import com.xiangxun.http.HttpUtil;
import com.xiangxun.request.ApiUrl;
import com.xiangxun.service.MainService;
import com.xiangxun.util.Md5;
import com.xiangxun.widget.MsgToast;
import com.xiangxun.widget.TitleView;

import org.apache.http.client.ClientProtocolException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class RegisterActivity extends BaseActivity {
	private TitleView titleView;
	private TextView tvmcode;
	private TextView tvplate;
	private Button btncreg;
	private Button btnsreg;
	private Button btnidcard;
	private EditText edtidcard;
	private TextView textRegidcard;
	private boolean platereg = false;
	private boolean sysreg;
	private MainService mService;
	private AuthService.MyBinder authBinder;
	private int ReturnAuthority = -1;
	private String sn;
	private Button recovery;
	private ProgressBar pb;
	private TextView tv;
	private int fileSize;
	private int downLoadFileSize;
	private String filename;
	private String resUrl = "";
	private Intent authIntent;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {// 定义一个Handler，用于处理下载线程与UI间通讯
			if (!Thread.currentThread().isInterrupted()) {
				switch (msg.what) {
				case 0:
					pb.setMax(fileSize);
				case 1:
					pb.setProgress(downLoadFileSize);
					int result = downLoadFileSize * 100 / fileSize;
					tv.setText(result + "%");
					break;
				case 2:
					Toast.makeText(RegisterActivity.this, "文件下载完成", Toast.LENGTH_SHORT).show();
					doZipExtractorWork();
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(RegisterActivity.this, "恢复成功！", Toast.LENGTH_SHORT).show();
							onBackPressed();
						}
					}, 1000);
					break;

				case -1:
					String error = msg.getData().getString("error");
					Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_SHORT).show();
					break;
				}
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.regist);
		initView();
		initData();
		initListener();
	}

	@Override
	public void initView() {
		titleView = (TitleView) findViewById(R.id.tv_comm_title);
		titleView.setTitle(R.string.sbzc);
		recovery = (Button) findViewById(R.id.recovery);
		pb = (ProgressBar) findViewById(R.id.down_pb);
		tv = (TextView) findViewById(R.id.tv);
		tvmcode = (TextView) this.findViewById(R.id.regtvmcode);
		tvplate = (TextView) this.findViewById(R.id.regtvplate);
		btncreg = (Button) this.findViewById(R.id.regbtnok);
		btnsreg = (Button) this.findViewById(R.id.regbtnintent);
		btnidcard = (Button) this.findViewById(R.id.regbtnidcard);
		edtidcard = (EditText) this.findViewById(R.id.regedtidcard);
		textRegidcard = (TextView) findViewById(R.id.text_regidcard);
	}

	private void ViewFocusChange(boolean hasFocus, TextView textView, EditText editView) {
		if (hasFocus) {
			textView.setTextSize(12);
			editView.setBackground(getResources().getDrawable(R.drawable.login_edittext_highlight));
			editView.setTextSize(16);
		}else {
			textView.setTextSize(16);
			editView.setBackground(getResources().getDrawable(R.drawable.login_edittext_normal));
			editView.setTextSize(12);
		}
	}
	
	@Override
	public void initListener() {
		edtidcard.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				ViewFocusChange(hasFocus, textRegidcard, edtidcard);
			}
		});

		titleView.setLeftBackOneListener(R.drawable.back_normal, new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});
		btnidcard.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
/*				Bundle bundle = new Bundle();
				bundle.putString("sn", edtidcard.getText().toString());
				Intent intent = new Intent(RegisterActivity.this, AuthVerificationActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
				overridePendingTransition(R.anim.left_in, R.anim.right_out);*/
				if (platereg) {
					Toast.makeText(RegisterActivity.this, R.string.registSuccess, Toast.LENGTH_SHORT).show();
				} else {
					sn = edtidcard.getText().toString();
					bindService(authIntent, authConn, Context.BIND_AUTO_CREATE);
				}
				if (platereg) {
					tvmcode.setText(R.string.systemRegisted);
					tvplate.setText(R.string.plateNotificationRegisted);
					edtidcard.setEnabled(false);
					btncreg.setEnabled(false);
					btnsreg.setEnabled(false);
				}
			}
		});
		btnsreg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (platereg) {
					getSysreg(sysreg, true);
				} else {
					sn = mService.getPlateRecLisn();
					bindService(authIntent, authConn, Context.BIND_AUTO_CREATE);
					getSysreg(sysreg, true);
				}
				if (sysreg && platereg) {
					tvmcode.setText(R.string.systemRegisted);
					tvplate.setText(R.string.plateNotificationRegisted);
					edtidcard.setEnabled(false);
					btncreg.setEnabled(false);
					btnsreg.setEnabled(false);
				}
			}
		});
		btncreg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (platereg) {
					Toast.makeText(RegisterActivity.this, R.string.registSuccess, Toast.LENGTH_SHORT).show();
				} else {
					sn = edtidcard.getText().toString();
					bindService(authIntent, authConn, Context.BIND_AUTO_CREATE);
				}
				if (platereg) {
					tvmcode.setText(R.string.systemRegisted);
					tvplate.setText(R.string.plateNotificationRegisted);
					edtidcard.setEnabled(false);
					btncreg.setEnabled(false);
					btnsreg.setEnabled(false);
				}
			}
		});
		recovery.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new Thread() {
					@Override
					public void run() {
						try {
							HttpUtil hu = new HttpUtil();
							//resUrl = hu.queryStringForGet(Api.urlHost + "/mpts/mobile/regcode/searchPathByModeId/" + XiangXunApplication.getInstance().getDevId() + "/?sessionid=" + LoginInfo.sessionId);
							resUrl = hu.queryStringForGet(ApiUrl.searchPathByModeId(RegisterActivity.this) + XiangXunApplication.getInstance().getDevId() + "/?sessionid=" + LoginInfo.sessionId);
							if(null == resUrl||resUrl.equals("")){
								MsgToast.geToast().setMsg("此设备无备份信息！");
								return;
							}
							System.out.println(ApiUrl.searchPathByModeId(RegisterActivity.this) + XiangXunApplication.getInstance().getDevId() + "/?sessionid=" + LoginInfo.sessionId);
							down_file(resUrl, "/storage/emulated/0/");
							String downUrl = resUrl;
							System.out.println(downUrl);
							// 下载文件，参数：第一个URL，第二个存放路径
						} catch (ClientProtocolException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}.start();
			}
		});
	}

	@Override
	public void initData() {
		sysreg = SystemCfg.Regist(this);
		platereg = SystemCfg.getPlateRec(this);
		edtidcard.setText(SystemCfg.getRegistCode(this));
		if (sysreg && platereg) {
			tvmcode.setText(R.string.systemRegisted);
			tvplate.setText(R.string.plateNotificationRegisted);
			edtidcard.setEnabled(false);
			btncreg.setEnabled(false);
			btnsreg.setEnabled(false);
			return;
		}

		mService = XiangXunApplication.getInstance().getMainService();

		sn = "";
		authIntent = new Intent(RegisterActivity.this, AuthService.class);
		bindService(authIntent, authConn, Context.BIND_AUTO_CREATE);
		if (sysreg) {
			tvmcode.setText(R.string.systemRegisted);

		} else {
			tvmcode.setText(getResources().getString(R.string.jiqima) + sysCode(0) + getResources().getString(R.string.zhucema_tip));
		}

		btnsreg.setVisibility(View.GONE);

	}

	private void down_file(String url, String path) throws IOException {
		// 下载函数
		filename = url.substring(url.lastIndexOf("/") + 1);
		// 获取文件名
		URL myURL = new URL(url);
		URLConnection conn = myURL.openConnection();
		conn.connect();
		InputStream is = conn.getInputStream();
		this.fileSize = conn.getContentLength();// 根据响应获取文件大小
		if (this.fileSize <= 0)
			throw new RuntimeException("无法获知文件大小 ");
		if (is == null)
			throw new RuntimeException("stream is null");
		@SuppressWarnings("resource")
		FileOutputStream fos = new FileOutputStream(path + filename);
		// 把数据存入路径+文件名
		byte buf[] = new byte[1024];
		downLoadFileSize = 0;
		sendMsg(0);
		do {
			// 循环读取
			int numread = is.read(buf);
			if (numread == -1) {
				break;
			}
			fos.write(buf, 0, numread);
			downLoadFileSize += numread;
			sendMsg(1);// 更新进度条
		} while (true);
		sendMsg(2);// 通知下载完成
		try {
			is.close();
		} catch (Exception ex) {
			Log.e("tag", "error: " + ex.getMessage(), ex);
		}

	}

	private void sendMsg(int flag) {
		Message msg = new Message();
		msg.what = flag;
		handler.sendMessage(msg);
	}

	private void doZipExtractorWork() {
		ZipExtractorTask task = new ZipExtractorTask("/storage/emulated/0/" + resUrl.split("/")[3], "/storage/emulated/0/", this, true);
		String url = "/storage/emulated/0/" + resUrl.split("/")[3];
		System.out.println(url);
		task.execute();
	}

	private void getSysreg(boolean arg, boolean in) {
		if (sysreg) {

		} else {
			if (in)
				if (sysCode(1).equals(mService.getMacLisn(sysCode(0)))) {
					tvmcode.setText(R.string.systemRegisted);
					Toast.makeText(RegisterActivity.this, R.string.registSuccess, Toast.LENGTH_SHORT).show();
					SystemCfg.setRegCode(RegisterActivity.this, mService.getMacLisn(sysCode(0)));
					SystemCfg.isRegist(RegisterActivity.this, true);
					sysreg = true;
					btnidcard.setEnabled(false);
					edtidcard.setEnabled(false);
				} else {
					recovery.setVisibility(View.VISIBLE);
					pb.setVisibility(View.VISIBLE);
					Toast.makeText(RegisterActivity.this, R.string.zhucemaError, Toast.LENGTH_SHORT).show();
					sysreg = false;
				}
			else if (sysCode(1).equals(edtidcard.getText().toString())) {
				tvmcode.setText(R.string.systemRegisted);
				Toast.makeText(RegisterActivity.this, R.string.registSuccess, Toast.LENGTH_SHORT).show();
				SystemCfg.setRegCode(RegisterActivity.this, edtidcard.getText().toString());
				sysreg = true;
				btnidcard.setEnabled(false);
				edtidcard.setEnabled(false);
			} else {
				recovery.setVisibility(View.VISIBLE);
				pb.setVisibility(View.VISIBLE);
				Toast.makeText(RegisterActivity.this, R.string.zhucemaError, Toast.LENGTH_SHORT).show();
				sysreg = false;
			}
		}
	}

	private String sysCode(int type) {
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String str = Md5.MD5(tm.getDeviceId());
		String str9 = "";
		for (int i = 0; i < str.length();) {
			str9 += str.charAt(i);
			i += str.length() / 9;
		}
		if (type == 0) {
			return str9;
		} else {
			str = Md5.MD5(str9);
			String str8 = "";
			for (int i = str.length() - 1; i >= 0;) {
				str8 += str.charAt(i);
				i -= str.length() / 8;
			}
			return str8;
		}
	}

	// 授权验证服务绑定后的操作与start识别服务
	private ServiceConnection authConn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			authBinder = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// Log.i(TAG, "authConn onServiceConnected");
			authBinder = (AuthService.MyBinder) service;
			// Toast.makeText(getApplicationContext(), "��Ȩ��֤���� �󶨳ɹ�",
			// Toast.LENGTH_SHORT).show();
			try {
				// sn：采用序列号方式激活时设置此参数，否则写""
				// authfile：采用激活文件方式激活时设置此参数，否则写""
				// 以上俩个参数都不为""时按序列号方式激活；当sn和authfile为""时会在根目录下找激活文件xxxxxxxxxxxxxxx_cp.txt
				// sn = "";
				ReturnAuthority = authBinder.getAuth(sn.toUpperCase(), "");
				if (ReturnAuthority != 0) {
					recovery.setVisibility(View.VISIBLE);
					pb.setVisibility(View.VISIBLE);
					tvplate.setText(R.string.plateRegistFailed);
					Toast.makeText(RegisterActivity.this, R.string.zhucemaError, Toast.LENGTH_SHORT).show();
					SystemCfg.setPlateRec(RegisterActivity.this, false);
					platereg = false;
				} else {
					// 若授权成功，则启动识别服务
					tvplate.setText(R.string.plateNotificationRegisted);
					Toast.makeText(RegisterActivity.this, R.string.registSuccess, Toast.LENGTH_SHORT).show();
					SystemCfg.setPlateRec(RegisterActivity.this, true);
					platereg = true;
					btnidcard.setEnabled(false);
					edtidcard.setEnabled(false);
				}
			} catch (Exception e) {
				e.printStackTrace();
				// Log.i(TAG, "e=" + e.toString());
			} finally {
				if (authBinder != null) {
					unbindService(authConn);// �����Ȩ��֤����
				}
			}
		}
	};
}
