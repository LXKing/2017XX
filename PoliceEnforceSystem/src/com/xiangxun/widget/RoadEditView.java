package com.xiangxun.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.xiangxun.adapter.RoadListAdapter;
import com.xiangxun.app.XiangXunApplication;
import com.xiangxun.bean.Type;
import com.xiangxun.db.DBManager;

import java.util.List;

/**
 * Created by Administrator on 2017/3/9.
 */

public class RoadEditView extends AutoCompleteTextView implements AdapterView.OnItemClickListener {

	private List<Type> roadGroup;
	private RoadListAdapter roadListAdapter;

	public RoadEditView(Context context) {
		super(context);
		initData();
	}

	public RoadEditView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initData();
	}

	public RoadEditView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initData();
	}

	private void initData() {
		roadGroup = DBManager.getInstance().getRoadInfoList();

		roadListAdapter = new RoadListAdapter(roadGroup, XiangXunApplication.getInstance());
		setAdapter(roadListAdapter);
		setThreshold(1);
		setDropDownWidth(-1);

		this.setOnItemClickListener(this);
	}

	public String getRoadCode() {
		String road = getText().toString();
		if (road.contains("-")){
			int index = road.indexOf("-");
			return DBManager.getInstance().getRoadCodeByName(road.substring(index + 1));
		} else
			return DBManager.getInstance().getRoadCodeByName(road);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		Type road = (Type) roadListAdapter.getItem(position);
		setText(road.getCode() + "-" + road.getName());

		if(roadNameClick != null){
			roadNameClick.roadCode(road.getCode());
		}
	}

	private onItemRoadNameClick roadNameClick;

	public void setRoadNameClick(onItemRoadNameClick roadNameClick){
		this.roadNameClick = roadNameClick;
	}

	public interface onItemRoadNameClick {
		public void roadCode(String code);
	}
}
