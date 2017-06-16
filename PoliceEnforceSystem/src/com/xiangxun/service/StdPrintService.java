package com.xiangxun.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;

import com.xiangxun.activity.R;
import com.xiangxun.app.XiangXunApplication;
import com.xiangxun.bean.MDate;
import com.xiangxun.bean.PrintInfo;
import com.xiangxun.cfg.SystemCfg;
import com.xiangxun.driver.BluetoothPrintDriver;
import com.xiangxun.util.ConstantStatus;
import com.xiangxun.widget.MsgToast;

import java.util.Set;

import static com.xiangxun.driver.BluetoothPrintDriver.Begin;
import static com.xiangxun.driver.BluetoothPrintDriver.PrintAndFeedLines;
import static com.xiangxun.driver.BluetoothPrintDriver.SelChineseCodepage;
import static com.xiangxun.request.AppBuildConfig.DEBUGURL;

public class StdPrintService extends Service {

	private BluetoothPrintDriver mPrint;
	private BluetoothDevice printer = null;

	private volatile int iObjectCode = 0;
	private boolean isConnected;
	private MyBinder mBinder = new MyBinder();

	private static int PageLine = 40;

	private static int ALIGN_LEFT = 0;
	private static int ALIGN_CENTER = 1;
	private static int ALIGN_RIGHT = 2;

	@SuppressLint("HandlerLeak")
	private Handler mUIHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case ConstantStatus.MESSAGE_STATE_CHANGE:
					break;
				case ConstantStatus.MESSAGE_WRITE:
					break;
				case ConstantStatus.MESSAGE_READ:
					String ErrorMsg = null;
					byte[] readBuf = (byte[]) msg.obj;
					float Voltage = 0;
					if(readBuf[2]==0)
						ErrorMsg = "NO ERROR!         ";
					else
					{
						if((readBuf[2] & 0x02) != 0)
							ErrorMsg = "错误：无打印机连接！";
						if((readBuf[2] & 0x04) != 0)
							ErrorMsg = "错误：无纸！";
						if((readBuf[2] & 0x08) != 0)
							ErrorMsg = "错误：打印机低电！";
						if((readBuf[2] & 0x40) != 0)
							ErrorMsg = "错误：打印机过热！";
					}
					Voltage = (float) ((readBuf[0]*256 + readBuf[1])/10.0);
					MsgToast.geToast().setMsg(ErrorMsg + "Battery voltage："+Voltage+" V");
					break;
				case ConstantStatus.MESSAGE_DEVICE_NAME:
					break;
				case ConstantStatus.MESSAGE_TOAST:
					break;
			}
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		mPrint = XiangXunApplication.getInstance().getStdPrint(mUIHandler);
		String printerName = SystemCfg.getPrinterName(XiangXunApplication.getInstance());

		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter.isEnabled()) {
			Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();

			if (devices.size() > 0) {
				for(BluetoothDevice device: devices){
					if (printerName.equals(device.getName())) {
						printer = device;
						mPrint.connect(device);
					}
				}
				if (printer == null)
					MsgToast.geToast().setMsg("请配对" + printerName);
			} else {
				MsgToast.geToast().setMsg("请配对" + printerName);
			}
		} else {
			MsgToast.geToast().setMsg("请打开蓝牙");
//			startActivityForResult(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS), 10);
			startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (!mPrint.IsNoConnection()) {
				mPrint.stop();
		}
	}

	public class MyBinder extends Binder {
		public StdPrintService getPrintService() {
			return StdPrintService.this;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	public void PrintInit() {
		Begin();
	}

	public boolean print(String[] content, int type) {
		int lines = content.length;
		int height = lines * (PrintInfo.FONT + 5);
		int start = 0;
		int printLines = 0;
		int feedLine = 0;
		int printCount = 1;
		boolean isSet = false;

		Begin();
		SelChineseCodepage();

		//----- Print
		while (printCount > 0) {

			PrintAndFeedLines(4);

			PrintTitle(SystemCfg.getTopName(this));
			switch (type) {
				case PrintInfo.TITLE_TYPE_PUNISHMENT:
					PrintTitle("公安交通管理简易程序处罚决定书");
					if (isSet == false) {
						printCount = 3;
						isSet = true;
					}
					break;
				case PrintInfo.TITLE_TYPE_DETAINMENT:
					PrintTitle("公安交通管理行政强制措施凭证");
					if (isSet == false) {
						printCount = 2;
						isSet = true;
					}
					break;
				case PrintInfo.TITLE_TYPE_PARKING:
					PrintTitle("违法停车告知单");
					if (isSet == false) {
						printCount = 1;
						isSet = true;
					}
					break;
				case PrintInfo.TITLE_TYPE_LIABILITY:
					PrintTitle("道路交通安全违法行为处理通知书");
					if (isSet == false) {
						printCount = 3;
						isSet = true;
					}
					break;
			}

			if (DEBUGURL)
				printCount = 1;

			PrintNormal("--------------------------------");

			SetDefStyle();
			for (int i = 0; i < lines; i++) {
				String strContent = content[i];
				int len = strContent.length();

				if (strContent != null && len > 0) {
					PrintNormal(strContent);
//					int codeLen = 0;
//					for (int k = 0; k < len; k++) {
//						char c = strContent.charAt(k);
//						if (c < 0x80)
//							codeLen++;
//						else
//							codeLen += 2;
//					}
//					printLines = printLines + codeLen / 32 + (codeLen % 32 == 0 ? 0 : 1);
				}
			}

			switch (type) {
				case PrintInfo.TITLE_TYPE_PUNISHMENT:
					PrintNormal("交通警察（签章）：");
					PrintAndFeedLines(1);
					PrintNormal("被处罚人签名：");
					PrintDate();
					PrintNormal("备注：");
//					PrintAndFeedLines(1);
//
//					printLines += 6;
					break;
				case PrintInfo.TITLE_TYPE_DETAINMENT:
					PrintNormal("交通警察（签章）：");
					PrintAndFeedLines(1);
					PrintNormal("当事人对本凭证记载内容有无异议：");
					PrintNormal("当事人签名：");
					PrintDate();
					PrintNormal("本凭证同时作为现场笔录");
					PrintNormal("备注：");
//					PrintAndFeedLines(1);
//
//					printLines += 8;
					break;
				case PrintInfo.TITLE_TYPE_PARKING:
//					PrintAndFeedLines(1);
					PrintDate();
					PrintNormal("--------------------------------");
					PrintNormal(getResources().getString(R.string.parkingNote));
//					PrintAndFeedLines(1);
//					printLines += 7;
					break;
				case PrintInfo.TITLE_TYPE_LIABILITY:

					break;
			}

//			if (PageLine > printLines)
//				feedLine = PageLine - printLines;
//			else
//				feedLine = 1;
//			PrintAndFeedLines(feedLine);

			// 打印监制信息
//			SetAlign(ALIGN_CENTER);
//			PrintBold("");	//(getResources().getString(R.string.jianzhi));
//			PrintAndFeedLines(4);

			SetDefStyle();
//			PrintAndFeedLines(3);

			if (PrintInfo.TITLE_TYPE_PARKING == type) {
				SearchBlackLabel();
				PrintAndFeedLines(2);
			} else
				SearchBlackLabel();

			//打印单张完成
			printCount --;
		}
		return true;
	}

	public boolean isPrintConnected() {
		return !mPrint.IsNoConnection();
	}

	private void PrintTitle(String title) {
		mPrint.SetBold((byte)1);
		mPrint.SetAlignMode((byte)1);

		mPrint.printString(title);
	}

	private void PrintBold(String str) {
		mPrint.SetBold((byte)1);
		mPrint.printString(str);
	}

	private void PrintNormal(String str) {
		mPrint.printString(str);
	}

	private void PrintDate() {
		PrintNormal(MDate.getChsDate());
	}

	private void SetDefStyle() {
		mPrint.SetBold((byte)0);
		mPrint.SetAlignMode((byte)0);
	}

	private void SetAlign(int align) {
		mPrint.SetAlignMode((byte)align);
	}

	private void PrintBlackLabel() {
		mPrint.SetBlackReversePrint((byte)1);
		PrintNormal("   ");
		mPrint.SetBlackReversePrint((byte)0);
	}

	private void SearchBlackLabel() {
		String printerName = SystemCfg.getPrinterName(XiangXunApplication.getInstance());
		if ("Feasycom".equals(printerName))
			mPrint.LocateBlackLabel_2Byte();
		else	//瑞工
			mPrint.LocateBlackLabel_1Byte();
	}

	public String[] formatContent(String s, int rawlines) {
		String[] content = s.split("%start%|%end%");
		return content;
	}

}
