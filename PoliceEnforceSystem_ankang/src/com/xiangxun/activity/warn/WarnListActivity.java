package com.xiangxun.activity.warn;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ViewFlipper;

import com.xiangxun.activity.R;
import com.xiangxun.adapter.WarnListAdapter;
import com.xiangxun.adapter.WarnListAdapter.SignAndAckOnClick;
import com.xiangxun.app.BaseActivity;
import com.xiangxun.bean.WarnData;
import com.xiangxun.db.DBManager;
import com.xiangxun.util.ConstantStatus;
import com.xiangxun.widget.MsgDialog;
import com.xiangxun.widget.MsgToast;
import com.xiangxun.widget.TitleView;
import com.xiangxun.widget.XListView;
import com.xiangxun.widget.XListView.IXListViewListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @package: com.xiangxun.activity
 * @ClassName: DailyServiceListActivity.java
 * @Description: 预警拦截
 */
public class WarnListActivity extends BaseActivity implements IXListViewListener, SignAndAckOnClick {
	private static int listState = -1;
	private int currentPage = 1;
	private int PageSize = 10;
	private int totalSize = 0;
	private WarnListAdapter mAdapter;
	private List<WarnData> warnDatas = new ArrayList<WarnData>();
	private TitleView titleView;
	private XListView mXListView;
	private ViewFlipper mVF;
	private int SelIndex;
	private MsgDialog delDialog;

	@SuppressLint("HandlerLeak")
	private Handler mUIHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			stopXListView();
			switch (msg.what) {
			case ConstantStatus.SREACH_DETAIL_SUCCESS:
				@SuppressWarnings("unchecked")
				List<WarnData> datas = (List<WarnData>) msg.obj;
				if (datas != null) {
					totalSize = datas.size();
					warnDatas.addAll(datas);
				}
				// 没有加载到数据
				if (warnDatas.size() == 0) {
					mVF.setDisplayedChild(2);
				} else {
					mVF.setDisplayedChild(0);
				}
				mAdapter.setData(warnDatas);
				break;
			case ConstantStatus.SREACH_DETAIL_FALSE:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.enforce_historylist);
		initView();
		initData();
		initListener();
	}

	@Override
	public void initView() {
		titleView = (TitleView) findViewById(R.id.tv_comm_title);
		titleView.setTitle(R.string.warn_do);
		mXListView = (XListView) findViewById(R.id.xlistview);
		mVF = (ViewFlipper) findViewById(R.id.viewFlipper);
	}

	@Override
	public void initListener() {
		mXListView.setPullLoadEnable(true);
		mXListView.setXListViewListener(this);
		titleView.setLeftBackOneListener(R.drawable.back_normal, new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});
		mXListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {
				SelIndex = index - 1;
				WarnData data = warnDatas.get(SelIndex);
				Intent intent = new Intent();
				intent.putExtra("warndata", data);
				if (-1 == data.getIsOk()) {
					// 签收
					intent.setClass(WarnListActivity.this, WarnSignActivity.class);
					startActivityForResult(intent, ConstantStatus.REQCODE_WARNSIGN);
				} else if (-1 == data.getIsAck()) {
					// 不出警，浏览
					intent.setClass(WarnListActivity.this, WarnSignActivity.class);
					startActivity(intent);
				} else if (0 == data.getIsAck()) {
					// 反馈
					intent.setClass(WarnListActivity.this, WarnAckActivity.class);
					startActivityForResult(intent, ConstantStatus.REQCODE_WARNACK);
				} else	{
					//已签收反馈，浏览
					intent.setClass(WarnListActivity.this, WarnAckActivity.class);
					startActivity(intent);
				}
			}
		});

		mXListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
				CreateDelWarnDialog();
				delDialog.show();
				return true;
			}
		});
	}

	@Override
	public void initData() {
		listState = ConstantStatus.listStateFirst;
		mAdapter = new WarnListAdapter(this, mXListView);
		mAdapter.setSignAndAckOnClick(this);
		mXListView.setAdapter(mAdapter);
		if (DBManager.getInstance().QueryWarnDataCount() <= 0) {
			mVF.setDisplayedChild(2);
		} else {
			RequestList();
		}
	}

	private void CreateDelWarnDialog() {
		delDialog = new MsgDialog(WarnListActivity.this);
		delDialog.setTitle("删除预警");
		delDialog.setMsg("删除全部未签和不出警的预警");
		delDialog.setButLeftListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				delDialog.dismiss();
			}
		});
		delDialog.setButRightListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				stopXListView();

				DBManager.getInstance().DeleteWarn();
				for (int i = warnDatas.size() - 1; i >= 0 ; i --) {
					WarnData warnData = warnDatas.get(i);
					if (warnData.getIsOk() == -1 || warnData.getIsDo() == 0) {
						warnDatas.remove(i);
					}
				}

				mAdapter.setData(warnDatas);
				delDialog.dismiss();

				SelIndex = 0;
			}
		});
	}

	private void RequestList() {
		switch (listState) {
		case ConstantStatus.listStateFirst:
			warnDatas.clear();
			mVF.setDisplayedChild(1);
			break;
		case ConstantStatus.listStateRefresh:
			warnDatas.clear();
			mVF.setDisplayedChild(0);
			break;
		case ConstantStatus.listStateLoadMore:
			mVF.setDisplayedChild(0);
			break;
		}
		DBManager.getInstance().GetWarnInfos(currentPage, PageSize, mUIHandler);
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (ConstantStatus.REQCODE_WARNSIGN == requestCode) {
			if (ConstantStatus.RESULTCODE_SIGNED == resultCode) {
				WarnData warnData = (WarnData)data.getSerializableExtra("warndata");
				warnDatas.set(SelIndex, warnData);
				mAdapter.notifyDataSetChanged();
			}
		} else if (ConstantStatus.REQCODE_WARNACK == requestCode) {
			if (ConstantStatus.RESULTCODE_ACK == resultCode) {
				WarnData warnData = (WarnData)data.getSerializableExtra("warndata");
				warnDatas.set(SelIndex, warnData);
				mAdapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	public void SignAndAckOnClick(int position) {
		SelIndex = position;
		WarnData data = warnDatas.get(SelIndex);
		Intent intent = new Intent();
		intent.putExtra("warndata", data);
		intent.putExtra("signandack", true);
		intent.setClass(WarnListActivity.this, WarnAckActivity.class);
		startActivityForResult(intent, ConstantStatus.REQCODE_WARNACK);
	}
}
