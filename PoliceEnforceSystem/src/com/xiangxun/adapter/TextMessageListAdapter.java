package com.xiangxun.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xiangxun.activity.R;
import com.xiangxun.bean.MsgType;
import com.xiangxun.bean.TextMessage;

public class TextMessageListAdapter extends BaseAdapter {
	private List<TextMessage> lists;
	private Context context;

	public TextMessageListAdapter(Context context, List<TextMessage> lists) {
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
		View view = null;
		if (convertView != null) {
			view = convertView;
		} else {
			view = LayoutInflater.from(context).inflate(R.layout.dutylist_item, null);
		}
		TextView name = (TextView) view.findViewById(R.id.item_title);// 标题
		name.setText(lists.get(position).text);
		TextView content = (TextView) view.findViewById(R.id.item_content);// 标�
		content.setText(MsgType.getMsgIsRead(lists.get(position).isread));
		return view;
	}

}
