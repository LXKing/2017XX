package com.xiangxun.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xiangxun.activity.R;
import com.xiangxun.bean.AddressBook;
import com.xiangxun.widget.XListView;

public class AddressBookListAdapter extends BaseAdapter {
	private List<AddressBook> lists;
	private Context context;
	private XListView mListView;
	private int size = 0;

	public AddressBookListAdapter(Context context, XListView mXListView) {
		this.context = context;
		this.mListView = mXListView;
	}

	public void setData(List<AddressBook> itemList) {
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
			view = LayoutInflater.from(context).inflate(R.layout.addressbooklist_item, null);
		}
		TextView name = (TextView) view.findViewById(R.id.item_title);
		name.setText(view.getResources().getString(R.string.name) + "：" + lists.get(position).name);
		TextView content = (TextView) view.findViewById(R.id.item_content);
		content.setText("部门：" + lists.get(position).deptmentname);
		return view;
	}

}
