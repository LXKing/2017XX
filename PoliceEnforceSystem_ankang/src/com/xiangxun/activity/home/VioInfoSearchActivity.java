package com.xiangxun.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.ViewFlipper;

import com.xiangxun.activity.R;
import com.xiangxun.adapter.VioInfoAdapter;
import com.xiangxun.app.BaseActivity;
import com.xiangxun.bean.ResultData.VioInfo;
import com.xiangxun.request.DcNetWorkUtils;
import com.xiangxun.util.ConstantStatus;
import com.xiangxun.widget.MsgToast;
import com.xiangxun.widget.TitleView;

import java.util.ArrayList;
import java.util.List;

/**
 * @package: com.xiangxun.activity.home
 * @ClassName: VioInfoSearchActivity.java
 * @Description: 违法信息查询
 * @author: HanGJ
 * @date: 2016-6-28 上午11:27:46
 */
public class VioInfoSearchActivity extends BaseActivity {
	private TitleView titleView;
	private ListView mXListView;
	private ViewFlipper mVF;
	private String carPlateNumber;
	private String carPlateType;
	private VioInfoAdapter adapter;
	private List<VioInfo> vioInfos = new ArrayList<VioInfo>();
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ConstantStatus.VEHICLE_SEARCH_SUCCESS:
				@SuppressWarnings("unchecked")
				List<VioInfo> violist = (List<VioInfo>) msg.obj;
				if (violist != null && violist.size() > 0) {
					mVF.setDisplayedChild(0);
					setViewData(violist);
				} else {
					mVF.setDisplayedChild(2);
				}
				break;

			case ConstantStatus.VEHICLE_SEARCH_FALSE:
				mVF.setDisplayedChild(2);
				MsgToast.geToast().setMsg("机动车数据查询失败,无对应数据!");
				break;

			case ConstantStatus.NetWorkError:
				MsgToast.geToast().setMsg("网络异常");
				break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vio_info_search_layout);
		initView();
		initData();
		initListener();
	}

	protected void setViewData(List<VioInfo> violist) {
		vioInfos.clear();
		vioInfos.addAll(violist);
		adapter.setData(vioInfos);
	}

	@Override
	public void initView() {
		titleView = (TitleView) findViewById(R.id.tv_comm_title);
		mXListView = (ListView) findViewById(R.id.xlistview);
		mVF = (ViewFlipper) findViewById(R.id.viewFlipper);
	}

	@Override
	public void initData() {
		titleView.setTitle("违法信息");
		Intent intent = getIntent();
		carPlateNumber = intent.getStringExtra("carPlateNumber");
		carPlateType = intent.getStringExtra("carPlateType");
		adapter = new VioInfoAdapter(this, vioInfos);
		mXListView.setAdapter(adapter);
		mVF.setDisplayedChild(1);
		DcNetWorkUtils.searchVioInfo(this, carPlateNumber, carPlateType, handler);
	}
	
	@Override
	public void initListener() {
		titleView.setLeftBackOneListener(R.drawable.back_normal, new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});
	}

}
