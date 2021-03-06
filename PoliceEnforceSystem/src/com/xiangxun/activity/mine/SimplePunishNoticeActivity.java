package com.xiangxun.activity.mine;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ViewFlipper;

import com.xiangxun.activity.R;
import com.xiangxun.activity.mine.detail.SummaryPunishmentDetailActivity;
import com.xiangxun.adapter.FieldPunishmentListAdapter;
import com.xiangxun.app.BaseActivity;
import com.xiangxun.bean.FieldPunishData;
import com.xiangxun.db.DBManager;
import com.xiangxun.util.ConstantStatus;
import com.xiangxun.widget.MsgToast;
import com.xiangxun.widget.TitleView;
import com.xiangxun.widget.XListView;
import com.xiangxun.widget.XListView.IXListViewListener;

import java.util.ArrayList;
import java.util.List;
/**
 * @package: com.xiangxun.activity
 * @ClassName: SimplePunishNoticeActivity.java
 * @Description: 工作量 --- 简易处罚
 */
public class SimplePunishNoticeActivity extends BaseActivity implements IXListViewListener {
	private static int listState = -1;
	private int currentPage = 1;
	private int PageSize = 10;
	private int totalSize = 0;
	private FieldPunishmentListAdapter mAdapter;
	private List<FieldPunishData> fieldPunishDatas = new ArrayList<FieldPunishData>();
	private TitleView titleView;
	private XListView mXListView;
	private ViewFlipper mVF;
	@SuppressLint("HandlerLeak")
	private Handler mUIHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			stopXListView();
			switch (msg.what) {
			case ConstantStatus.SREACH_DETAIL_SUCCESS:
				@SuppressWarnings("unchecked")
				List<FieldPunishData> datas = (List<FieldPunishData>) msg.obj;
				if (datas != null) {
					totalSize = datas.size();
					fieldPunishDatas.addAll(datas);
				}
				// 没有加载到数据
				if (fieldPunishDatas.size() == 0) {
					mVF.setDisplayedChild(2);
				} else {
					mVF.setDisplayedChild(0);
				}
				mAdapter.setData(fieldPunishDatas);
				break;
			case ConstantStatus.SREACH_DETAIL_FALSE:
				break;
			}
		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.field_historylist);
		initView();
		initData();
		initListener();
	}
	
	@Override
	public void initView() {
		titleView = (TitleView) findViewById(R.id.tv_comm_title);
		titleView.setTitle(R.string.jycxcfjl);
		mXListView = (XListView) findViewById(R.id.xlistview);
		mVF = (ViewFlipper) findViewById(R.id.viewFlipper);
	}

	@Override
	public void initListener() {
		titleView.setLeftBackOneListener(R.drawable.back_normal, new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});
		mXListView.setPullLoadEnable(true);
		mXListView.setXListViewListener(this);
		mXListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {
				BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
				if(ba.isEnabled()){
					Intent intent = new Intent(SimplePunishNoticeActivity.this, SummaryPunishmentDetailActivity.class);
					intent.setAction("view");
					FieldPunishData data = (FieldPunishData) mAdapter.getItem(index - 1);
					Bundle bundle = new Bundle();
					bundle.putString("id", data.id);
					bundle.putString("name", data.name);
					bundle.putString("phone", data.phone);
					bundle.putString("driverlic", data.driverlic);
					bundle.putString("recordid", data.recordid);
					bundle.putString("licenseoffice", data.licenseoffice);
					bundle.putString("licensetype", data.licensetype);
					bundle.putString("plate", data.plate);
					bundle.putString("platetype", data.platetype);
					bundle.putString("plateoffice", data.plateoffice);
					bundle.putString("owner", data.owner);
					bundle.putString("location", data.location);
					bundle.putString("roadlocation", data.roadlocation);
					bundle.putString("roaddirect", data.roaddirect);
					bundle.putString("content", data.content);
					bundle.putString("datetime", data.datetime);
					bundle.putInt("isReprint", data.isprinted);
					bundle.putInt("isupfile", data.isupfile);
					bundle.putString("action", data.action);
					bundle.putInt("money", data.money);
					bundle.putInt("value", data.value);
					bundle.putString("pic1", data.pic1);
					bundle.putString("pic2", data.pic2);
					bundle.putString("pic3", data.pic3);
					bundle.putString("pic4", data.pic4);
					intent.putExtras(bundle);
					startActivity(intent);
				} else {
					MsgToast.geToast().setMsg("请打开蓝牙");
					startActivityForResult(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS), 10);
				}

			}
		});
	}

	@Override
	public void initData() {
		listState = ConstantStatus.listStateFirst;
		mAdapter = new FieldPunishmentListAdapter(this, mXListView);
		mXListView.setAdapter(mAdapter);
		if (DBManager.getInstance().QueryFieldpunishDataCount() <= 0) {
			mVF.setDisplayedChild(2);
		} else {
			RequestList();
		}
	}

	private void RequestList() {
		switch (listState) {
		case ConstantStatus.listStateFirst:
			fieldPunishDatas.clear();
			mVF.setDisplayedChild(1);
			break;
		case ConstantStatus.listStateRefresh:
			fieldPunishDatas.clear();
			mVF.setDisplayedChild(0);
			break;
		case ConstantStatus.listStateLoadMore:
			mVF.setDisplayedChild(0);
			break;
		}
		DBManager.getInstance().getFieldPunishData(currentPage, PageSize, mUIHandler);
	}

	@Override
	protected void onDestroy() {
		if (fieldPunishDatas != null) {
			fieldPunishDatas.clear();
			fieldPunishDatas = null;
		}
		super.onDestroy();
	}

	@Override
	public void onRefresh(View v) {
		currentPage = 1;
		listState = ConstantStatus.listStateRefresh;
		RequestList();
	}

	@Override
	public void onLoadMore(View v) {
		if (totalSize < PageSize) {
			MsgToast.geToast().setMsg("已经是最后一页");
			mXListView.removeFooterView(mXListView.mFooterView);
		} else {
			currentPage++;
			listState = ConstantStatus.listStateLoadMore;
			RequestList();
		}
	}

	// xLisView 停止
	private void stopXListView() {
		mXListView.stopRefresh();
		mXListView.stopLoadMore();
	}

}
