package com.xiangxun.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xiangxun.activity.R;
import com.xiangxun.bean.UpLoad;
import com.xiangxun.bean.WarnAck;
import com.xiangxun.bean.WarnData;
import com.xiangxun.db.DBManager;
import com.xiangxun.widget.XListView;

import java.util.List;

public class WarnAckListAdapter extends BaseAdapter {
	private List<WarnAck> lists;
	private Context context;
	private XListView mListView;
	private int size = 0;
	private WarnData warndata;

	public WarnAckListAdapter(Context context, XListView mXListView) {
		this.context = context;
		this.mListView = mXListView;
	}

	public void setData(List<WarnAck> itemList) {
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
			view = LayoutInflater.from(context).inflate(R.layout.warnlist_item, null);
		}

		TextView name = (TextView) view.findViewById(R.id.item_title);
		TextView content = (TextView) view.findViewById(R.id.item_content);

		WarnAck data = lists.get(position);
		warndata = DBManager.getInstance().getWarnDataInfo(data.yjxh);
		name.setText(warndata.getHphm() + "|" + DBManager.getInstance().getNameByTypeAndCodeFromDic("BLACKTYPE_CODE",warndata.getBklx()));

		String contentStr = data.czsj + "|" + UpLoad.getUpLoad(data.isupfile, context);
		content.setText(contentStr);
		return view;
	}
}
