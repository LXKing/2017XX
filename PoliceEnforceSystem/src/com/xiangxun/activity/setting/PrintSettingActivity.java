package com.xiangxun.activity.setting;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiangxun.activity.R;
import com.xiangxun.app.BaseActivity;
import com.xiangxun.cfg.SystemCfg;
import com.xiangxun.util.ConstantStatus;
import com.xiangxun.widget.MsgToast;
import com.xiangxun.widget.TitleView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PrintSettingActivity extends BaseActivity {
	private TitleView titleView;

	private RelativeLayout turnPrint;
	private TextView isPrint;
	private ImageView turnImage;
	
	private TextView defPrinter;
	private ListView mPrinterList;
	private PrinterListViewAdapter mPrinterAdapter;
	private List<BluetoothDevice> printers;
	private boolean isPrintChange = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.printerset);
		initView();
		initData();
		initListener();
	}

	@Override
	public void initView() {
		titleView = (TitleView) findViewById(R.id.tv_comm_title);
		titleView.setTitle(R.string.blueprintset);

		turnPrint = (RelativeLayout) findViewById(R.id.rl_is_print);
		isPrint = (TextView) findViewById(R.id.tv_is_print);
		turnImage = (ImageView) findViewById(R.id.image_is_print);

		defPrinter = (TextView) findViewById(R.id.tv_def_printer);
		mPrinterList = (ListView) findViewById(R.id.printer_list);
	}

	@Override
	public void initListener() {
		titleView.setLeftBackOneListener(R.drawable.back_normal, new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setPrintResult();
				onBackPressed();
			}
		});

		turnPrint.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean value = !SystemCfg.getIsPrint(PrintSettingActivity.this);
				setIsPrintView(value);
				SystemCfg.setIsPrint(PrintSettingActivity.this, value);
				isPrintChange = true;
			}
		});
		
		mPrinterList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String printName = printers.get(position).getName();
				defPrinter.setText(printName);
				SystemCfg.setPrinterName(PrintSettingActivity.this, printName);
			}
		});
	}

	@Override
	public void initData() {
		setIsPrintView(SystemCfg.getIsPrint(this));
		defPrinter.setText(SystemCfg.getPrinterName(this));
		
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter.isEnabled()) {

			printers = new ArrayList<BluetoothDevice>();
			
			Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
			if (devices.size() > 0) {

				for(BluetoothDevice device: devices){
					int deviceType = device.getBluetoothClass().getMajorDeviceClass();
					if (ConstantStatus.BLUETOOTHPRINTER == deviceType)
						printers.add(device);
				}
				
				if (printers.size() > 0)
					initListAdapter(printers);
				else
					MsgToast.geToast().setMsg("请配对蓝牙打印机");
			} else {
				MsgToast.geToast().setMsg("请配对蓝牙打印机");
			}
		} else {
			MsgToast.geToast().setMsg("请打开蓝牙");
			startActivityForResult(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS), 10);
		}
	}

	private void setIsPrintView(boolean value) {
		if (value) {
			isPrint.setText(getResources().getString(R.string.print));
			turnImage.setBackgroundResource(R.drawable.turn_on);
		} else {
			isPrint.setText(getResources().getString(R.string.noprint));
			turnImage.setBackgroundResource(R.drawable.turn_off);			
		}
	}
	
	private void initListAdapter(List<BluetoothDevice> devices) {
		try {
			mPrinterAdapter = new PrinterListViewAdapter(mPrinterList, this, devices) {
			};
		} catch (IllegalArgumentException e) {
			
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			
			e.printStackTrace();
		}
		
		mPrinterList.setAdapter(mPrinterAdapter);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			setPrintResult();
		}
		return super.onKeyDown(keyCode, event);
	}

	private void setPrintResult () {
		Intent intent = new Intent();
		if (isPrintChange) {
			intent.putExtra("isSetPrint", SystemCfg.getIsPrint(PrintSettingActivity.this));
			setResult(ConstantStatus.RESULTCODE_CHANGEPRINT, intent);
		}
	}
}
