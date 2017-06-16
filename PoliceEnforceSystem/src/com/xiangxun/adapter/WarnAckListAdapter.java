package com.xiangxun.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xiangxun.activity.R;
import com.xiangxun.app.XiangXunApplication;
import com.xiangxun.bean.UpLoad;
import com.xiangxun.bean.WarnData;
import com.xiangxun.bean.WarnMessage;
import com.xiangxun.db.DBManager;
import com.xiangxun.widget.XListView;

import java.util.List;

public class WarnAckListAdapter extends BaseAdapter {
	private List<WarnMessage> lists;
	private Context context;
	private XListView mListView;
	private int size = 0;
	private WarnData warndata;

	public WarnAckListAdapter(Context context, XListView mXListView) {
		this.context = context;
		this.mListView = mXListView;
	}

	public void setData(List<WarnMessage> itemList) {
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
		ViewHolder holder;
		if (convertView == null) {
			view = LayoutInflater.from(context).inflate(R.layout.warnlist_item, null);

			holder = new ViewHolder();
			holder.name = (TextView) view.findViewById(R.id.item_title);
			holder.content = (TextView) view.findViewById(R.id.item_content);

			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}

		WarnMessage data = lists.get(position);
		warndata = XiangXunApplication.getInstance().getGson().fromJson(data.dataInfo, WarnData.class);
		holder.name.setText(warndata.getHphm() + "|" + DBManager.getInstance().getNameByTypeAndCodeFromDic("BLACKTYPE_CODE",warndata.getBklx()));

		String contentStr = warndata.getGcsj() + getUpStatus(data);
		holder.content.setText(contentStr);
		return view;
	}

	static class ViewHolder{
		public TextView name;
		public TextView content;
	}

	private String getUpStatus(WarnMessage data) {
		String status = "|";
		String isUpfileStr;
		int isUpfileValue;
		int index;

		if (data.signInfo != null && !data.signInfo.equals("")) {
			index = data.signInfo.indexOf("isupfile");
			isUpfileStr = data.signInfo.substring(index + 10, index + 11);
			isUpfileValue = Integer.valueOf(isUpfileStr);
			status = "|签收" + UpLoad.getUpLoad(isUpfileValue, context);

			if (isUpfileValue != 1)
				return status;
		}

		if (data.vioInfo != null && !data.vioInfo.equals("")) {
			index = data.vioInfo.indexOf("isupfile");
			isUpfileStr = data.vioInfo.substring(index + 10, index + 11);
			isUpfileValue = Integer.valueOf(isUpfileStr);
			status = "|表单" + UpLoad.getUpLoad(isUpfileValue, context);

			if (isUpfileValue != 1)
				return status;
		}

		if (data.ackInfo != null && !data.ackInfo.equals("")) {
			index = data.ackInfo.indexOf("isupfile");
			isUpfileStr = data.ackInfo.substring(index + 10, index + 11);
			isUpfileValue = Integer.valueOf(isUpfileStr);
			status = "|反馈" + UpLoad.getUpLoad(isUpfileValue, context);

			if (isUpfileValue != 1)
				return status;
		}

		if (data.picInfo != null && !data.picInfo.equals("")) {
			index = data.picInfo.indexOf("isupfile");
			isUpfileStr = data.picInfo.substring(index + 10, index + 11);
			isUpfileValue = Integer.valueOf(isUpfileStr);
			status = "|图片" + UpLoad.getUpLoad(isUpfileValue, context);

			if (isUpfileValue != 1)
				return status;
		}

		return status;
	}
}
