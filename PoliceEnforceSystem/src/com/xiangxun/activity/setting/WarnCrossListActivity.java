package com.xiangxun.activity.setting;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ViewFlipper;

import com.google.gson.Gson;
import com.xiangxun.activity.R;
import com.xiangxun.adapter.EListAdapter;
import com.xiangxun.app.BaseActivity;
import com.xiangxun.bean.Child;
import com.xiangxun.bean.Group;
import com.xiangxun.bean.ResultData.GroupCross;
import com.xiangxun.cfg.SystemCfg;
import com.xiangxun.request.DcNetWorkUtils;
import com.xiangxun.util.ConstantStatus;
import com.xiangxun.widget.MsgToast;
import com.xiangxun.widget.TitleView;

import java.util.ArrayList;

import static com.xiangxun.request.AppBuildConfig.DEBUGURL;


public class WarnCrossListActivity extends BaseActivity {
	private TitleView titleView;
	private ViewFlipper mVF;
	private ExpandableListView listView;
	private EListAdapter adapter;
	private ArrayList<Group> groups;

	private Button btnCancel;
	private Button btnEnter;

	private String CrossNames;
	private String crossNull;	// = "null";

	@SuppressLint("HandlerLeak")
	private Handler mUIHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case ConstantStatus.SEARCH_CROSS_SUCCESS:
					@SuppressWarnings("unchecked")
					Group groupCross = (Group) msg.obj;
					if (groupCross != null && groupCross.getCross_total() > 0) {
						String departmentName = SystemCfg.getDepartment(WarnCrossListActivity.this);
						if (departmentName.contains("中队"))
							groupCross.setDepartment_name(SystemCfg.getParentDepartmentName(WarnCrossListActivity.this));
						else
							groupCross.setDepartment_name(SystemCfg.getDepartment(WarnCrossListActivity.this));
						groups.add(groupCross);
						adapter.notifyDataSetChanged();
					}
					// 没有加载到数据
					if (groups.size() == 0) {
						mVF.setDisplayedChild(2);
					} else {
						loadWarnCrossSet();

						mVF.setDisplayedChild(0);
						listView.expandGroup(0);
					}
					break;
				case ConstantStatus.SEARCH_CROSS_FAIL:
					MsgToast.geToast().setMsg("获取预警卡口信息异常");
					break;
				case ConstantStatus.NetWorkError:
					MsgToast.geToast().setMsg("网络错误");
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sel_cross_layout);
		initView();
		initData();
		initListener();
	}

	@Override
	public void initView() {
		titleView = (TitleView) findViewById(R.id.tv_comm_title);
		titleView.setTitle(R.string.warnCrossSel);

		mVF = (ViewFlipper) findViewById(R.id.viewFlipper);

		listView = (ExpandableListView) findViewById(R.id.cross_expand_list);

		btnCancel = (Button) findViewById(R.id.btn_cancel);
		btnEnter = (Button) findViewById(R.id.btn_enter);
	}

	@Override
	public void initListener() {
		titleView.setLeftBackOneListener(R.drawable.back_normal, new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				onBackPressed();
			}
		});
		btnEnter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				CrossNames = getWarnCrossNames();
				//保存预警卡口名称
				SystemCfg.setWarnCross(WarnCrossListActivity.this, CrossNames);

				Intent intent = new Intent();
				intent.putExtra("crossnames", CrossNames);
				setResult(ConstantStatus.RESULTCODE_CROSS, intent);

				onBackPressed();
			}
		});
	}

	@Override
	public void initData() {
		if (DEBUGURL)
			crossNull = "null";
		else
			crossNull = "无";

		groups = new ArrayList<Group>();

//		InitCrossData();
		DcNetWorkUtils.searchWarnCross(WarnCrossListActivity.this, mUIHandler);
		mVF.setDisplayedChild(1);

		adapter = new EListAdapter(this, groups);
		listView.setAdapter(adapter);
		listView.setOnChildClickListener(adapter);
	}

	private void loadWarnCrossSet() {
		String crossNames = SystemCfg.getWarnCross(WarnCrossListActivity.this);
		if (crossNull.equals(crossNames))
			return;

		String[] cross = crossNames.split(",");
		int crossLen = cross.length;
		int k = 0;

		for (int i = 0, len = groups.size(); i < len; i++) {
			Group group = groups.get(i);
			group.setChecked(true);
			for (int j = 0, childLen = group.getChildrenCount(); j < childLen; j++) {
				Child child = group.getChildItem(j);
				if (cross[k].equals(child.getCrossName())) {
					child.setChecked(true);
					k ++;
					if (k >= crossLen) {
						j ++;
						if (j < childLen)
							group.setChecked(false);
						return;
					}
				} else {
					group.setChecked(false);
				}
			}
		}
	}

	private String getWarnCrossNames() {
		String crossNames = "";
		for (int i = 0, len = groups.size(); i < len; i ++) {
			Group group = groups.get(i);
			for (int j = 0, childLen = group.getChildrenCount(); j < childLen; j ++ ) {
				Child child = group.getChildItem(j);
				if (child.isChecked()) {
					if ("".equals(crossNames))
						crossNames = crossNames + child.getCrossName();
					else
						crossNames = crossNames + "," + child.getCrossName();
				}
			}
		}
		if ("".equals(crossNames))
			crossNames = crossNull;
		return crossNames;
	}

//	private void getJSONObject() {
//		String crossStr;
//		if (DEBUGURL)
//			crossStr = "{'group_name':'延安交警支队','department_total':1,'department_list':[{'department_name':'子长交警大队','cross_total':11,'cross_list':[{'crossname':'a'},{'crossname':'子长县余家坪'},{'crossname':'子长县交警队新址门口'},{'crossname':'子长县袁家沟口'},{'crossname':'子长县子安路24公里500米(寺湾处）'},{'crossname':'子长县定粉厂'},{'crossname':'子长县王李家沟'},{'crossname':'子长火车站'},{'crossname':'子长县中山街'},{'crossname':'子长县迎宾路东向西'},{'crossname':'子长县迎宾路西向东'}]}]}";
//		else
//			crossStr = "{'group_name':'延安交警支队','department_total':1,'department_list':[{'department_name':'子长交警大队','cross_total':10,'cross_list':[{'crossname':'子长县余家坪'},{'crossname':'子长县交警队新址门口'},{'crossname':'子长县袁家沟口'},{'crossname':'子长县子安路24公里500米(寺湾处）'},{'crossname':'子长县定粉厂'},{'crossname':'子长县王李家沟'},{'crossname':'子长火车站'},{'crossname':'子长县中山街'},{'crossname':'子长县迎宾路东向西'},{'crossname':'子长县迎宾路西向东'}]}]}";
//		Gson gson = new Gson();
//		GroupCross groupCross = gson.fromJson(crossStr, GroupCross.class);
//		for (int i = 0; i < groupCross.department_total; i++) {
////			Group group = new Group(String.valueOf(i), groupCross.department_list.get(i).department_name);
////			for (int j = 0; j < groupCross.department_list.get(i).cross_total; j++) {
////				Child child = groupCross.department_list.get(i).cross_list.get(j);
////				group.addChildrenItem(child);
////			}
////			groups.add(group);
//			groups.add(groupCross.department_list.get(i));
//		}
//	}

	private void InitCrossData() {
		String crossStr;
		if (DEBUGURL)
			crossStr = "{'group_name':'延安交警支队','department_total':1,'department_list':[{'department_name':'子长交警大队','cross_total':11,'cross_list':[{'crossname':'a'},{'crossname':'子长县余家坪'},{'crossname':'子长县交警队新址门口'},{'crossname':'子长县袁家沟口'},{'crossname':'子长县子安路24公里500米(寺湾处）'},{'crossname':'子长县定粉厂'},{'crossname':'子长县王李家沟'},{'crossname':'子长火车站'},{'crossname':'子长县中山街'},{'crossname':'子长县迎宾路东向西'},{'crossname':'子长县迎宾路西向东'}]}]}";
		else
			crossStr = "{'group_name':'延安交警支队','department_total':1,'department_list':[{'department_name':'子长交警大队','cross_total':10,'cross_list':[{'crossname':'子长县余家坪'},{'crossname':'子长县交警队新址门口'},{'crossname':'子长县袁家沟口'},{'crossname':'子长县子安路24公里500米(寺湾处）'},{'crossname':'子长县定粉厂'},{'crossname':'子长县王李家沟'},{'crossname':'子长火车站'},{'crossname':'子长县中山街'},{'crossname':'子长县迎宾路东向西'},{'crossname':'子长县迎宾路西向东'}]}]}";
		Gson gson = new Gson();
		GroupCross groupCross = gson.fromJson(crossStr, GroupCross.class);
		Message msg = new Message();
		msg.what = ConstantStatus.SEARCH_CROSS_SUCCESS;
		msg.obj = groupCross;
		mUIHandler.sendMessage(msg);
	}
}
