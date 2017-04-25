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
import com.xiangxun.activity.mine.detail.DailyAffairsDetailActivity;
import com.xiangxun.adapter.DutyListAdapter;
import com.xiangxun.app.BaseActivity;
import com.xiangxun.bean.PatorlInfo;
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
 * @ClassName: DailyServiceListActivity.java
 * @Description: 工作量---日常勤务
 */
public class DailyServiceListActivity extends BaseActivity implements IXListViewListener {
	private static int listState = -1;
	private int currentPage = 1;
	private int PageSize = 10;
	private int totalSize = 0;
	private DutyListAdapter mAdapter;
	private List<PatorlInfo> patorlInfos = new ArrayList<PatorlInfo>();
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
				List<PatorlInfo> datas = (List<PatorlInfo>) msg.obj;
				if (datas != null) {
					totalSize = datas.size();
					patorlInfos.addAll(datas);
				}
				// 没有加载到数据
				if (patorlInfos.size() == 0) {
					mVF.setDisplayedChild(2);
				} else {
					mVF.setDisplayedChild(0);
				}
				mAdapter.setData(patorlInfos);
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
		titleView.setTitle(R.string.rcqwjl);
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
				Intent intent = new Intent(DailyServiceListActivity.this, DailyAffairsDetailActivity.class);
				intent.setAction("view");
				PatorlInfo data = patorlInfos.get(index - 1);
				Bundle bundle = new Bundle();
				bundle.putString("datetime", data.datetime);
				bundle.putString("memo", data.memo);
				bundle.putString("roadname", data.roadname);
				bundle.putString("roadlocation", data.roadlocation);
				bundle.putString("roaddirect", data.roaddirect);
				bundle.putInt("patorltype", data.patorltype);
				bundle.putString("pic1", data.pic1);
				bundle.putString("pic2", data.pic2);
				bundle.putString("pic3", data.pic3);
				bundle.putString("pic4", data.pic4);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	@Override
	public void initData() {
		listState = ConstantStatus.listStateFirst;
		mAdapter = new DutyListAdapter(this, mXListView);
		mXListView.setAdapter(mAdapter);
		if (DBManager.getInstance().QueryDutyDataCount() <= 0) {
			mVF.setDisplayedChild(2);
		} else {
			RequestList();
		}
	}

	private void RequestList() {
		switch (listState) {
		case ConstantStatus.listStateFirst:
			patorlInfos.clear();
			mVF.setDisplayedChild(1);
			break;
		case ConstantStatus.listStateRefresh:
			patorlInfos.clear();
			mVF.setDisplayedChild(0);
			break;
		case ConstantStatus.listStateLoadMore:
			mVF.setDisplayedChild(0);
			break;
		}
		DBManager.getInstance().getUnUpPator(currentPage, PageSize, mUIHandler);
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
