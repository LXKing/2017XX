package com.xiangxun.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xiangxun.activity.R;
import com.xiangxun.bean.MsgType;
import com.xiangxun.bean.SystemMessage;

import java.util.List;

public class SysMessageListAdapter extends BaseAdapter {
	private List<SystemMessage> lists;
	private Context context;

	public SysMessageListAdapter(Context context, List<SystemMessage> lists) {
		this.context = context;
		this.lists = lists;
	}

	@Override
	public int getCount() {
		return lists.size();
	}

	@Override
	public Object getItem(int position) {
		return lists.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		String contentStr;

		View view = null;
		if (convertView != null) {
			view = convertView;
		} else {
			view = LayoutInflater.from(context).inflate(R.layout.dutylist_item, null);
		}
		TextView name = (TextView) view.findViewById(R.id.item_title);// 标题
		TextView content = (TextView) view.findViewById(R.id.item_content);//

		name.setText(lists.get(position).text);
		contentStr = lists.get(position).datetime + "|" + MsgType.getMsgIsRead(lists.get(position).isread);
		content.setText(contentStr);
		return view;
	}

}
