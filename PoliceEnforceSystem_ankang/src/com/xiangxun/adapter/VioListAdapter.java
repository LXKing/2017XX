package com.xiangxun.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xiangxun.activity.R;
import com.xiangxun.bean.UpLoad;
import com.xiangxun.bean.VioData;
import com.xiangxun.widget.XListView;

public class VioListAdapter extends BaseAdapter {
	private List<VioData> lists;
	private Context context;
	private XListView mListView;
	private int size = 0;

	public VioListAdapter(Context context, List<VioData> lists, XListView mXListView) {
		this.context = context;
		this.lists = lists;
		this.mListView = mXListView;
	}

	public void setData(List<VioData> itemList) {
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
		TextView name = (TextView) view.findViewById(R.id.item_title);// 标题
		name.setText(lists.get(position).platenum + "  " + lists.get(position).viotype);
		TextView content = (TextView) view.findViewById(R.id.item_content);// 标题
		content.setText(lists.get(position).datetime + "|" + UpLoad.getUpLoad(lists.get(position).isupfile, context));
		return view;
	}

}
