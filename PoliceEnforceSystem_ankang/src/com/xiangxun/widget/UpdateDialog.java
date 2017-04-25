package com.xiangxun.widget;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.xiangxun.activity.R;

/**
 * 
 * @ClassName: UpdateDialog.java
 * @Description: 版本更新弹出框
 * @author: FangJW
 * @date: 2014年10月15日 上午11:44:43
 */
public class UpdateDialog extends Dialog {
	private Button cancle;
	private Button btn_update;
	private String m_title;
	private String m_content;
	private TextView textViewTitle, textViewContent;
	private String m_url;
	private String mFileName;
	private String mDirName;
	private TextView textView;
	private ProgressBar progressbar;
	private boolean iflose = false;
	private boolean isForce = true;
	private Context mContext;

	private ViewFlipper viewFlipper;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				if (isForce)
					UpdateDialog.this.dismiss();
				installApk();
				break;
			case 0:
				MsgToast.geToast().setMsg("下载失败");
				break;
			case 2:
				int i = (Integer) msg.obj;
				textView.setText("正在下载安装包..." + i + "%");
				progressbar.setProgress(i);
				Log.d("lilin", "i===" + i);
				break;
			case 3:
				MsgToast.geToast().setMsg("终止下载");
				break;
			case 4:
				textView.setText("正在下载安装包..." + 0 + "%");
				break;
			default:
				break;
			}
		}
	};

	public UpdateDialog(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public UpdateDialog(Context context, int theme, String title, String content, String url) {
		super(context, theme);
		mContext = context;
		m_title = title;
		m_content = content;
		m_url = url;
		init();
	}

	private void init() {
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View v = inflater.inflate(R.layout.update_dialog, null);
		viewFlipper = (ViewFlipper) v.findViewById(R.id.viewFlipper);
		cancle = (Button) v.findViewById(R.id.btcancle);
		btn_update = (Button) v.findViewById(R.id.btn_update);
		textViewTitle = (TextView) v.findViewById(R.id.textView_title);
		textViewTitle.setText(m_title);
		textViewContent = (TextView) v.findViewById(R.id.textView_content);
		textViewContent.setText(m_content);
		textViewContent.setMovementMethod(ScrollingMovementMethod.getInstance());
		textView = (TextView) v.findViewById(R.id.textView);
		progressbar = (ProgressBar) v.findViewById(R.id.progressbar);
		progressbar.setMax(100);
		cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				UpdateDialog.this.dismiss();
			}
		});

		btn_update.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				viewFlipper.setDisplayedChild(1);
				new Thread(new Runnable() {
					@Override
					public void run() {
						getNewApp(m_url);
					}
				}).start();
			}
		});

		// WindowManager m = ((Activity) mContext).getWindowManager();
		// Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
		//
		// LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
		// p.height = (int) (d.getHeight() * 1.0); // 高度设置为屏幕的1.0
		// p.width = (int) (d.getWidth() * 0.5); // 宽度设置为屏幕的0.8
		// p.alpha = 1.0f; // 设置本身透明度
		// p.dimAmount = 0.0f; // 设置黑暗度
		//
		// getWindow().setAttributes(p); // 设置生效
		// getWindow().setGravity(Gravity.CENTER); // 设置靠右对齐
		this.setContentView(v);
		// WindowManager m = ((Activity) mContext).getWindowManager();
		// Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高

		DisplayMetrics dm = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
		LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
		// p.height = (int) (d.getHeight() * 0.6); // 高度设置为屏幕的0.6
		p.width = (int) (dm.widthPixels * 0.80); // 宽度设置为屏幕的0.95

		getWindow().setAttributes(p); // 设置生效
		setCanceledOnTouchOutside(false);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			viewFlipper.setDisplayedChild(0);
			iflose = false;
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}

	}

	@Override
	public void setCancelable(boolean flag) {// 强制更新屏蔽返回键
		super.setCancelable(flag);
		isForce = flag;
		if (!isForce) {
			cancle.setText("退出");
			cancle.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dismiss();
					((Activity) mContext).finish();
				}
			});
		}
	}

	private void getNewApp(String str) {
		iflose = true;
		progressbar.setProgress(0);
		handler.sendEmptyMessage(4);
		String urlDownload = str;
		mDirName = Environment.getExternalStorageDirectory() + "/xiangxun/app/";
		File f = new File(mDirName);
		if (!f.exists()) {
			f.mkdirs();
		}
		// 准备拼接新的文件名（保存在存储卡后的文件名）
		mFileName = urlDownload.substring(urlDownload.lastIndexOf("/") + 1);
		String newFilename = mFileName;
		newFilename = mDirName + mFileName;
		File file = new File(newFilename);
		// 如果目标文件已经存在，则删除。产生覆盖旧文件的效果
		if (file.exists()) {
			file.delete();
		}
		InputStream is = null;
		OutputStream os = null;
		try {
			// 构造URL
			URL url = new URL(urlDownload);
			// 打开连接
			URLConnection con = url.openConnection();
			int contentLength = con.getContentLength() / 100;
			int progress = 0;
			// 获得文件的长度
			// int contentLength = con.getContentLength();
			// System.out.println("长度 :" + contentLength);
			// 输入流
			is = con.getInputStream();
			// 1K的数据缓冲
			byte[] bs = new byte[1024];
			// 读取到的数据长度
			int len;
			// 输出的文件流
			os = new FileOutputStream(newFilename);
			// 开始读取
			while ((len = is.read(bs)) != -1 && iflose) {
				os.write(bs, 0, len);
				progress = progress + len;
				Message msg = handler.obtainMessage();
				msg.what = 2;
				int n = progress / contentLength;
				msg.obj = n;
				handler.sendMessage(msg);
			}
			// 完毕，关闭所有链接
			os.close();
			is.close();
			if (iflose) {
				Message msg = handler.obtainMessage();
				msg.what = 1;
				handler.sendMessage(msg);
			} else {
				Message msg = handler.obtainMessage();
				msg.what = 3;
				handler.sendMessage(msg);
			}
		} catch (Exception e) {
			Message msg = handler.obtainMessage();
			msg.what = 0;
			handler.sendMessage(msg);
			e.printStackTrace();
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 安装APK文件
	 */
	private void installApk() {
		File apkfile = new File(mDirName, mFileName);
		if (!apkfile.exists()) {
			return;
		}
		// 通过Intent安装APK文件
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
		getContext().startActivity(i);
	}

}
