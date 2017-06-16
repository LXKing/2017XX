package com.xiangxun.activity.mine;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ViewFlipper;

import com.xiangxun.activity.R;
import com.xiangxun.activity.mine.detail.ViolationsNoticeDetailActivity;
import com.xiangxun.adapter.VioListAdapter;
import com.xiangxun.app.BaseActivity;
import com.xiangxun.bean.VioData;
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
 * @ClassName: IllegalCollectionActivity.java
 * @Description: 工作量---违法采集
 */
public class IllegalCollectionActivity extends BaseActivity implements OnItemClickListener, IXListViewListener {
	private static int listState = -1;
	private int currentPage = 1;
	private int PageSize = 10;
	private int totalSize = 0;
	private VioListAdapter mAdapter;
	private List<VioData> vioDatas = new ArrayList<VioData>();// data for the
	private TitleView titleView;
	private XListView mXListView;
	private ViewFlipper mVF;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			stopXListView();
			switch (msg.what) {
			case ConstantStatus.SREACH_VIODATA_SUCCESS:
				@SuppressWarnings("unchecked")
				List<VioData> datas = (List<VioData>) msg.obj;
				if (datas != null) {
					totalSize = datas.size();
					vioDatas.addAll(datas);
				}
				// 没有加载到数据
				if (vioDatas.size() == 0) {
					mVF.setDisplayedChild(2);
				} else {
					mVF.setDisplayedChild(0);
				}
				mAdapter.setData(vioDatas);
				break;
			case ConstantStatus.SREACH_VIODATA_FALSE:

				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vio_dataview);
		initView();
		initData();
		initListener();
	}

	@Override
	public void initView() {
		titleView = (TitleView) findViewById(R.id.tv_comm_title);
		titleView.setTitle(R.string.wfjljl);
		mXListView = (XListView) findViewById(R.id.xlistview);
		mVF = (ViewFlipper) findViewById(R.id.viewFlipper);
	}

	@Override
	public void initListener() {
		mXListView.setPullLoadEnable(true);
		mXListView.setXListViewListener(this);
		mXListView.setOnItemClickListener(this);
		titleView.setLeftBackOneListener(R.drawable.back_normal, new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});
	}

	@Override
	public void initData() {
		listState = ConstantStatus.listStateFirst;
		mAdapter = new VioListAdapter(this, vioDatas, mXListView);
		mXListView.setAdapter(mAdapter);
		if (DBManager.getInstance().getViodatacount() <= 0) {
			mVF.setDisplayedChild(2);
		} else {
			RequestList();
		}
	}

	private void RequestList() {
		switch (listState) {
		case ConstantStatus.listStateFirst:
			vioDatas.clear();
			mVF.setDisplayedChild(1);
			break;
		case ConstantStatus.listStateRefresh:
			vioDatas.clear();
			mVF.setDisplayedChild(0);
			break;
		case ConstantStatus.listStateLoadMore:
			mVF.setDisplayedChild(0);
			break;
		}
		DBManager.getInstance().getViodate(currentPage, PageSize, handler);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent intent = new Intent(IllegalCollectionActivity.this, ViolationsNoticeDetailActivity.class);
		Bundle bundle = new Bundle();
		intent.setAction("view");
		try {
			bundle.putString("pic1", vioDatas.get(arg2 - 1).picurl0);
			bundle.putString("pic2", vioDatas.get(arg2 - 1).picurl1);
			bundle.putString("pic3", vioDatas.get(arg2 - 1).picurl2);
			bundle.putString("pic4", vioDatas.get(arg2 - 1).picurl3);
			bundle.putInt("_id", vioDatas.get(arg2 - 1)._id);
			bundle.putInt("isupfile", vioDatas.get(arg2 - 1).isupfile);
			bundle.putInt("picnum", vioDatas.get(arg2 - 1).picnum);
			bundle.putString("platetype", vioDatas.get(arg2 - 1).platetype);
			bundle.putString("viotype", vioDatas.get(arg2 - 1).viotype);
			bundle.putString("datetime", vioDatas.get(arg2 - 1).datetime);
			bundle.putString("platenum", vioDatas.get(arg2 - 1).platenum);
			bundle.putString("roadname", vioDatas.get(arg2 - 1).roadname);
			bundle.putString("roadlocation", vioDatas.get(arg2 - 1).roadlocation);
			bundle.putString("roaddirect", vioDatas.get(arg2 - 1).roaddirect);
			bundle.putString("user", vioDatas.get(arg2 - 1).user);
			bundle.putString("viocode", vioDatas.get(arg2 - 1).viocode);
			bundle.putString("pubishCode", vioDatas.get(arg2 - 1).getPublishCode());
			
			bundle.putString("carcolor", vioDatas.get(arg2 - 1).carColor);
			bundle.putString("carcolorcode", vioDatas.get(arg2 - 1).carColorCode);
			bundle.putString("platecolor", vioDatas.get(arg2 - 1).plateColor);
			bundle.putString("platecolorcode	", vioDatas.get(arg2 - 1).plateColorCode);
			
			intent.putExtras(bundle);
			startActivityForResult(intent, arg2 - 1);
		} catch (IndexOutOfBoundsException e) {
			Log.d("Layout_dataview", "IndexOutOfBoundsException listVio size is " + vioDatas.size() + " position is " + arg2);
		}
	}

	@Override
	protected void onDestroy() {
		if (vioDatas != null) {
			vioDatas.clear();
			vioDatas = null;
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
