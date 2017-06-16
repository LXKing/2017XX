package com.xiangxun.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.xiangxun.adapter.VioTypeListAdapter;
import com.xiangxun.app.XiangXunApplication;
import com.xiangxun.bean.Type;
import com.xiangxun.db.DBManager;

import java.util.List;

/**
 * Created by Administrator on 2017/3/9.
 */

public class VioEditView extends AutoCompleteTextView implements AdapterView.OnItemClickListener {

	private List<Type> vioGroup;
	private VioTypeListAdapter vioListAdapter;

	public VioEditView(Context context) {
		super(context);
		initData();
	}

	public VioEditView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initData();
	}

	public VioEditView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initData();
	}

	private void initData() {
		vioGroup = DBManager.getInstance().getVioTypeList();

		vioListAdapter = new VioTypeListAdapter(vioGroup, XiangXunApplication.getInstance());
		setAdapter(vioListAdapter);
		setThreshold(1);
		setDropDownWidth(-1);

		this.setOnItemClickListener(this);
	}

	public String getVioCode() {
		String vio = getText().toString();
		if (vio.contains("-")){
			int index = vio.indexOf("-");
			return DBManager.getInstance().getVioCodeByName(vio.substring(index + 1));
		} else
			return DBManager.getInstance().getVioCodeByName(vio);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		Type vio = (Type) vioListAdapter.getItem(position);
		setText(vio.getCode());

		if(vioNameClick != null){
			vioNameClick.vioCode(vio);
		}
	}

	private onItemvioNameClick vioNameClick;

	public void setvioNameClick(onItemvioNameClick vioNameClick){
		this.vioNameClick = vioNameClick;
	}

	public interface onItemvioNameClick {
		public void vioCode(Type VioType);
	}
}
