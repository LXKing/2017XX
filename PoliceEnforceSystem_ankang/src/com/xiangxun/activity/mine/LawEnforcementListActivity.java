package com.xiangxun.activity.mine;

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
import com.xiangxun.activity.mine.detail.LawEnforceDetailActivity;
import com.xiangxun.adapter.TaizhangListAdapter;
import com.xiangxun.app.BaseActivity;
import com.xiangxun.bean.LawCheckInfo;
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
 * @ClassName: LawEnforcementListActivity.java
 * @Description: 工作量---执法检查
 */
public class LawEnforcementListActivity extends BaseActivity implements IXListViewListener {
	private static int listState = -1;
	private int currentPage = 1;
	private int PageSize = 10;
	private int totalSize = 0;
	private TaizhangListAdapter mAdapter;
	private List<LawCheckInfo> lawCheckInfos = new ArrayList<LawCheckInfo>();
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
				List<LawCheckInfo> datas = (List<LawCheckInfo>) msg.obj;
				if (datas != null) {
					totalSize = datas.size();
					lawCheckInfos.addAll(datas);
				}
				// 没有加载到数据
				if (lawCheckInfos.size() == 0) {
					mVF.setDisplayedChild(2);
				} else {
					mVF.setDisplayedChild(0);
				}
				mAdapter.setData(lawCheckInfos);
				break;
			case ConstantStatus.SREACH_DETAIL_FALSE:
				break;
			}
		}
	};

	@Override
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
		titleView.setTitle("执法检查记录");
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
				Intent intent = new Intent(LawEnforcementListActivity.this, LawEnforceDetailActivity.class);
				intent.setAction("view");
				LawCheckInfo data = (LawCheckInfo) mAdapter.getItem(index - 1);
				Bundle bundle = new Bundle();
				bundle.putString("actual", data.actual);
				bundle.putString("cartype", data.cartype);
				bundle.putString("checktype", data.checktype);
				bundle.putString("datetime", data.datetime);
				bundle.putString("drivername", data.drivername);
				bundle.putString("drivernum", data.drivernum);
				bundle.putString("handler", data.handler);
				bundle.putString("handleway", data.handleway);
				bundle.putString("memo", data.memo);
				bundle.putString("must", data.must);
				bundle.putString("owner", data.owner);
				bundle.putString("phone", data.phone);
				bundle.putString("pic1", data.pic1);
				bundle.putString("pic2", data.pic2);
				bundle.putString("pic3", data.pic3);
				bundle.putString("pic4", data.pic4);
				bundle.putString("platenum", data.platenum);
				bundle.putInt("isupfile", data.isupfile);
				bundle.putString("road", data.road);
				bundle.putString("roadlocation", data.roadlocation);
				bundle.putString("roaddirect", data.roaddirect);
				bundle.putString("viotype", data.viotype);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	@Override
	public void initData() {
		listState = ConstantStatus.listStateFirst;
		mAdapter = new TaizhangListAdapter(this, mXListView);
		mXListView.setAdapter(mAdapter);
		if (DBManager.getInstance().QueryZhifaDataCount() <= 0) {
			mVF.setDisplayedChild(2);
		} else {
			RequestList();
		}
	}

	private void RequestList() {
		switch (listState) {
		case ConstantStatus.listStateFirst:
			lawCheckInfos.clear();
			mVF.setDisplayedChild(1);
			break;
		case ConstantStatus.listStateRefresh:
			lawCheckInfos.clear();
			mVF.setDisplayedChild(0);
			break;
		case ConstantStatus.listStateLoadMore:
			mVF.setDisplayedChild(0);
			break;
		}
		DBManager.getInstance().getLawCheckInfo(currentPage, PageSize, mUIHandler);
	}

	@Override
	protected void onDestroy() {
		if (lawCheckInfos != null) {
			lawCheckInfos.clear();
			lawCheckInfos = null;
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
