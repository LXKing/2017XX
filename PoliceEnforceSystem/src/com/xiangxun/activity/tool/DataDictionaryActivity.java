/**
 * 
 */
package com.xiangxun.activity.tool;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiangxun.activity.R;
import com.xiangxun.app.BaseActivity;
import com.xiangxun.bean.VioDic;
import com.xiangxun.bean.WeijinInfo;
import com.xiangxun.db.DBManager;
import com.xiangxun.util.ConstantStatus;
import com.xiangxun.util.Utils;
import com.xiangxun.widget.AutoClearEditText;
import com.xiangxun.widget.PublishSelectLawDialog;
import com.xiangxun.widget.PublishSelectLawDialog.SelectLawDateItemClick;
import com.xiangxun.widget.SelectDialog;
import com.xiangxun.widget.SelectDialog.SelectItemClick;
import com.xiangxun.widget.TitleView;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @className DataDictionaryActivity
 * @author ChenXiangshi
 * @date 2015-5-20
 */
public class DataDictionaryActivity extends BaseActivity implements OnClickListener, SelectLawDateItemClick, SelectItemClick {
	private LinearLayout lawlay;
	private LinearLayout weijinlay;
	private TitleView titleView;
	private ImageButton btn;
	private TextView mLawType;
	private LinearLayout mSelectLawType;
	private PublishSelectLawDialog lawDialog;
	private AutoClearEditText edt;
	private TextView tvviocode;
	private TextView tvlawtext;
	private TextView tvmoney;
	private TextView tvdealvalue;
	private TextView tvdealtext;
	private TextView wsviokey;
	private TextView wscode;
	private TextView wsdealtxt;
	private TextView wslawtxt;
	private TextView wstype;
	private ProgressDialog sDialog = null;
	private AlertDialog.Builder builder = null;
	private int ssid = -1;
	private SelectDialog searchKeyDialog;
	private ArrayList<String> searchKeys;
	private ArrayList<VioDic> searchVioKeys;
	@SuppressLint("HandlerLeak")
	private Handler handle = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				sDialog.dismiss();
				Toast.makeText(DataDictionaryActivity.this, R.string.dataLoadError, Toast.LENGTH_SHORT).show();
				break;
			case 1:
				sDialog.dismiss();
				if (lawDialog.getSelectedItemPosition() == 0) {
					VioDic v = (VioDic) msg.getData().getSerializable("lawinfo");
					tvviocode.setText(v.code);
					tvdealtext.setText(v.name);
					tvlawtext.setText(v.law);
					tvmoney.setText(Long.toString(v.fineDefault));
					tvdealvalue.setText(v.punish);
				} else if (lawDialog.getSelectedItemPosition() == 1) {
					WeijinInfo w = (WeijinInfo) msg.getData().getSerializable("lawinfo");
					wsviokey.setText(w.name);
					wscode.setText(w.uncode);
					wsdealtxt.setText(w.disposemode);
					wstype.setText(w.type);
					wslawtxt.setText(w.lawProvision);
				}

				break;
			case ConstantStatus.LAWKEY:
				sDialog.dismiss();
				if (lawDialog.getSelectedItemPosition() == 0) {
					searchVioKeys = (ArrayList<VioDic>) msg.obj;
					if (searchKeys == null)
						searchKeys = new ArrayList<String>();
					else
						searchKeys.clear();

					for (VioDic data:searchVioKeys){
						searchKeys.add(data.getSimpleName());
					}
				}
				else {
					Bundle b = msg.getData();
					searchKeys = b.getStringArrayList("lawkey");
				}
				int len = searchKeys.size();
				if (len > 1) {
					Boolean[] checkItems = new Boolean[len];
					for (int i = 0; i < len; i ++) {
						checkItems[i] = false;
					}

					searchKeyDialog = new SelectDialog(DataDictionaryActivity.this, searchKeys.toArray(new String[len]), checkItems, true, "选择关键字");
					searchKeyDialog.setSelectItemClick(DataDictionaryActivity.this);
					searchKeyDialog.show();
				} else {
					itemOnClick(null, 0);
				}
				break;
			case ConstantStatus.NOLAWKEY:
				sDialog.dismiss();
				Toast.makeText(DataDictionaryActivity.this, R.string.keyWordsNotFound, Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		setContentView(R.layout.search_law_data_dic_layout);
		initView();
		initData();
		initListener();
	}

	@Override
	public void initView() {
		titleView = (TitleView) findViewById(R.id.tv_comm_title);
		titleView.setTitle(R.string.sjcd);
		btn = (ImageButton) findViewById(R.id.search_button);
		edt = (AutoClearEditText) findViewById(R.id.search_edit);
		tvviocode = (TextView) this.findViewById(R.id.lsviocode);
		tvlawtext = (TextView) this.findViewById(R.id.lslawtxt);
		tvmoney = (TextView) this.findViewById(R.id.lsdealmoney);
		tvdealvalue = (TextView) this.findViewById(R.id.lsdealvalue);
		tvdealtext = (TextView) this.findViewById(R.id.lsdealtxt);
		wsviokey = (TextView) this.findViewById(R.id.wsviokey);
		wscode = (TextView) this.findViewById(R.id.wscode);
		wsdealtxt = (TextView) this.findViewById(R.id.wsdealtxt);
		wslawtxt = (TextView) this.findViewById(R.id.wslawtxt);
		wstype = (TextView) this.findViewById(R.id.wstype);
		lawlay = (LinearLayout) findViewById(R.id.law_lay);
		weijinlay = (LinearLayout) findViewById(R.id.weijin_lay);
		mLawType = (TextView) this.findViewById(R.id.tv_select_lawtype);
		mSelectLawType = (LinearLayout) findViewById(R.id.ll_select_click_lawtype);
	}

	@Override
	public void initListener() {
		titleView.setLeftBackOneListener(R.drawable.back_normal, new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Utils.hideSoftInputFromWindow(DataDictionaryActivity.this);
				if (edt.getText().toString().equals("")) {
					Toast.makeText(DataDictionaryActivity.this, R.string.keyWordsNotNull, Toast.LENGTH_SHORT).show();
					return;
				}
				sDialog.setMessage(getResources().getString(R.string.loading_hold));
				sDialog.setTitle("");
				sDialog.setIndeterminate(true);
				sDialog.setCancelable(true);
				sDialog.show();
				Thread thread = new Thread(new GetLawKey(edt.getText().toString()));
				thread.start();
			}
		});
		mSelectLawType.setOnClickListener(this);
		lawDialog.setSelectLawDateItemClick(this);
	}

	@Override
	public void initData() {
		sDialog = new ProgressDialog(this);
		String[] lawItems = { "法律条文", "违禁物品" };
		lawDialog = new PublishSelectLawDialog(this, lawItems, mLawType, "请选择");
		lawDialog.setSelection(0);
	}

	private class GetLawKey implements Runnable {
		private String str;

		public GetLawKey(String str) {
			this.str = str;
		}

		@Override
		public void run() {
			Message msg = handle.obtainMessage();
			if (lawDialog.getSelectedItemPosition() == 0) {
				ArrayList<VioDic> strlist = new ArrayList<VioDic>();
				strlist = (ArrayList<VioDic>)DBManager.getInstance().getVioListByKeyWords(str);

				if (strlist == null || strlist.size() == 0) {
					msg.what = ConstantStatus.NOLAWKEY;
				} else {
					msg.what = ConstantStatus.LAWKEY;
					msg.obj = strlist;
				}
			} else if (lawDialog.getSelectedItemPosition() == 1) {
				ArrayList<String> strlist = new ArrayList<String>();
				strlist = (ArrayList<String>) DBManager.getInstance().getWeijinKeyListByKeyWords(str);

				if (strlist == null || strlist.size() == 0) {
					msg.what = ConstantStatus.NOLAWKEY;
				} else {
					msg.what = ConstantStatus.LAWKEY;
					Bundle bundle = new Bundle();
					bundle.putStringArrayList("lawkey", strlist);
					msg.setData(bundle);
				}
			}
			handle.sendMessage(msg);
		}
	}

	private class GetLawInfo implements Runnable {
		private String key;

		public GetLawInfo(String str) {
			key = str;
		}

		@Override
		public void run() {
			Message msg = new Message();
			if (lawDialog.getSelectedItemPosition() == 0) {
				VioDic vt = null;
				vt = DBManager.getInstance().queryVioTypeByKeyCode(key);
				if (vt == null)
					msg.what = 0;
				else {
					msg.what = 1;
					Bundle bundle = new Bundle();
					bundle.putSerializable("lawinfo", (Serializable) vt);
					msg.setData(bundle);
				}
			} else if (lawDialog.getSelectedItemPosition() == 1) {
				WeijinInfo wi = null;
				wi = DBManager.getInstance().getWeijinInfoByKeywords(key);
				if (wi == null)
					msg.what = 0;
				else {
					msg.what = 1;
					Bundle bundle = new Bundle();
					bundle.putSerializable("lawinfo", (Serializable) wi);
					msg.setData(bundle);
				}
			}
			handle.sendMessage(msg);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_select_click_lawtype:
			lawDialog.show();
			break;
		}
	}

	@Override
	public void lawDateOnClick(int position) {
		if (position == 0) {
			lawlay.setVisibility(View.VISIBLE);
			weijinlay.setVisibility(View.GONE);
		} else if (position == 1) {
			lawlay.setVisibility(View.GONE);
			weijinlay.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void itemOnClick(Boolean[] checks, int position) {
		// TODO Auto-generated method stub
		sDialog.setMessage(getResources().getString(R.string.loading_hold));
		sDialog.setTitle("");
		sDialog.setIndeterminate(true);
		sDialog.setCancelable(true);
		sDialog.show();

		if (lawDialog.getSelectedItemPosition() == 0) {
			Thread thread = new Thread(new GetLawInfo(searchVioKeys.get(position).getCode()));
			thread.start();
		} else {
			Thread thread = new Thread(new GetLawInfo(searchKeys.get(position).toString()));
			thread.start();
		}
	}
}
