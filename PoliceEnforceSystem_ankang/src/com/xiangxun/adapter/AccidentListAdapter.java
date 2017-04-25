package com.xiangxun.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xiangxun.activity.R;
import com.xiangxun.bean.AccidentInfo;
import com.xiangxun.bean.UpLoad;
import com.xiangxun.db.DBManager;
import com.xiangxun.widget.XListView;

public class AccidentListAdapter extends BaseAdapter {
	private List<AccidentInfo> lists;
	private Context context;
	private DBManager db;
	private XListView mListView;
	private int size = 0;

	public AccidentListAdapter(Context context, XListView mXListView) {
		this.context = context;
		this.mListView = mXListView;
		this.db = DBManager.getInstance();
	}

	public void setData(List<AccidentInfo> itemList) {
		if (itemList != null) {
			this.lists = itemList;
			size = itemList.size();
		}

		mListView.removeFooterView(mListView.mFooterView);
		if (size > 9) {
			mListView.addFooterView(mListView.mFooterView);
		}
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return size;
	}

	@Override
	public Object getItem(int position) {
		if (lists != null) {
			return lists.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		if (convertView != null) {
			view = convertView;
		} else {
			view = LayoutInflater.from(context).inflate(R.layout.dutylist_item, null);
		}
		TextView name = (TextView) view.findViewById(R.id.item_title);
		name.setText(db.getAccTypes().get(lists.get(position).acctype).name + "|" + lists.get(position).roadname);
		TextView content = (TextView) view.findViewById(R.id.item_content);
		content.setText(lists.get(position).realtime + "|" + UpLoad.getUpLoad(lists.get(position).isupfile, context));
		return view;
	}

}
