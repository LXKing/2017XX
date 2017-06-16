package com.xiangxun.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ViewFlipper;

import com.xiangxun.activity.R;
import com.xiangxun.adapter.EListAdapter;
import com.xiangxun.app.BaseActivity;
import com.xiangxun.bean.Child;
import com.xiangxun.bean.Group;
import com.xiangxun.bean.Type;
import com.xiangxun.cfg.SystemCfg;
import com.xiangxun.db.DBManager;
import com.xiangxun.util.ConstantStatus;
import com.xiangxun.widget.TitleView;

import java.util.ArrayList;


public class WarnTypeListActivity extends BaseActivity {
	private TitleView titleView;
	private ViewFlipper mVF;
	private ExpandableListView listView;
	private EListAdapter adapter;
	private ArrayList<Group> groups;

	private ArrayList<Type> BlackTypes = new ArrayList<Type>();

	private Button btnCancel;
	private Button btnEnter;

	private String TypeNames = "";
	private String TypeCodes = "";
	private String typeNull = "无";

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
		titleView.setTitle(R.string.warnTypeSel);

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

				getWarnTypes();
				//保存预警卡口名称
				SystemCfg.setWarnType(WarnTypeListActivity.this, TypeNames);
				SystemCfg.setWarnTypeCode(WarnTypeListActivity.this, TypeCodes);

				Intent intent = new Intent();
				intent.putExtra("warntypes", TypeNames);
				setResult(ConstantStatus.RESULTCODE_TYPE, intent);

				onBackPressed();
			}
		});
	}

	@Override
	public void initData() {
		groups = new ArrayList<Group>();

		BlackTypes =  DBManager.getInstance().getBaseTypes("BLACKTYPE_CODE");
		loadWarnType();

		adapter = new EListAdapter(this, groups);
		listView.setAdapter(adapter);
		listView.setOnChildClickListener(adapter);

		if (groups.size() == 0) {
			mVF.setDisplayedChild(2);
		} else {
			loadWarnTypeSet();

			mVF.setDisplayedChild(0);
			listView.expandGroup(0);
		}
	}

	private void loadWarnType() {
		Group group = new Group("0", "全部预警类型");

		for (int i = 0, len = BlackTypes.size(); i < len; i++) {
			Child child = new Child(BlackTypes.get(i).getName(), BlackTypes.get(i).getCode());
			group.addChildrenItem(child);
		}

		groups.add(group);
	}
	private void loadWarnTypeSet() {
		String warnTypes = SystemCfg.getWarnTypeCode(WarnTypeListActivity.this);
		if (typeNull.equals(warnTypes))
			return;

		String[] cross = warnTypes.split(",");
		int crossLen = cross.length;
		int k = 0;

		for (int i = 0, len = groups.size(); i < len; i++) {
			Group group = groups.get(i);
			group.setChecked(true);
			for (int j = 0, childLen = group.getChildrenCount(); j < childLen; j++) {
				Child child = group.getChildItem(j);
				if (cross[k].equals(child.getCrossCode())) {
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

	private void getWarnTypes() {
		for (int i = 0, len = groups.size(); i < len; i ++) {
			Group group = groups.get(i);
			for (int j = 0, childLen = group.getChildrenCount(); j < childLen; j ++ ) {
				Child child = group.getChildItem(j);
				if (child.isChecked()) {
					if ("".equals(TypeNames)) {
						TypeNames = TypeNames + child.getCrossName();
						TypeCodes = TypeCodes + child.getCrossCode();
					}
					else {
						TypeNames = TypeNames + "," + child.getCrossName();
						TypeCodes = TypeCodes + "," + child.getCrossCode();
					}
				}
			}
		}
		if ("".equals(TypeNames)) {
			TypeNames = typeNull;
			TypeCodes = typeNull;
		}
	}
}
