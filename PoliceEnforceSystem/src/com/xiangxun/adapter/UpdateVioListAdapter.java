package com.xiangxun.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xiangxun.activity.R;
import com.xiangxun.app.XiangXunApplication;
import com.xiangxun.bean.EnforceVio;
import com.xiangxun.bean.ParkVio;
import com.xiangxun.bean.SimpleVio;
import com.xiangxun.bean.UpLoad;
import com.xiangxun.bean.UpdateVio;
import com.xiangxun.bean.WarnMessage;
import com.xiangxun.widget.XListView;

import java.util.List;

public class UpdateVioListAdapter extends BaseAdapter {
	private List<UpdateVio> lists;
	private Context context;
	private XListView mListView;
	private int size = 0;
	private SimpleVio simpleVio;
	private EnforceVio enforceVio;
	private ParkVio parkVio;

	public UpdateVioListAdapter(Context context, XListView mXListView) {
		this.context = context;
		this.mListView = mXListView;
	}

	public void setData(List<UpdateVio> itemList) {
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

		UpdateVio data = lists.get(position);
		String type = data.type;
		if ("simple".equals(type)) {
			simpleVio = XiangXunApplication.getInstance().getGson().fromJson(data.vio, SimpleVio.class);
			simpleVio.isupfile = data.isupfile;
			setViewText(holder, simpleVio);
		} else if ("enforce".equals(type)) {
			enforceVio = XiangXunApplication.getInstance().getGson().fromJson(data.vio, EnforceVio.class);
			enforceVio.isupfile = data.isupfile;
			setViewText(holder, enforceVio);
		} else {
			parkVio = XiangXunApplication.getInstance().getGson().fromJson(data.vio, ParkVio.class);
			parkVio.isupfile = data.isupfile;
			setViewText(holder, parkVio);
		}
		return view;
	}

	static class ViewHolder{
		public TextView name;
		public TextView content;
	}

	private void setViewText(ViewHolder holder, SimpleVio simpleVio) {
		String nameStr = "";
		String contentStr = "";

		nameStr = simpleVio.hphm + "|" + simpleVio.jdsbh + "|"  + "简易程序处罚";
		contentStr =  simpleVio.wfsj + "|" + UpLoad.getUpLoad(simpleVio.isupfile, context);

		holder.name.setText(nameStr);
		holder.content.setText(contentStr);
	}

	private void setViewText(ViewHolder holder, EnforceVio enforceVio) {
		String nameStr = "";
		String contentStr = "";

		nameStr = enforceVio.hphm + "|" + enforceVio.pzbh + "|";
		if ("3".equals(enforceVio.wslb))
			nameStr += "行政强制措施";
		else
			nameStr += "违法行为通知";

		contentStr =  enforceVio.wfsj + "|" + UpLoad.getUpLoad(enforceVio.isupfile, context);

		holder.name.setText(nameStr);
		holder.content.setText(contentStr);
	}

	private void setViewText(ViewHolder holder, ParkVio parkVio) {
		String nameStr = "";
		String contentStr = "";

		nameStr = parkVio.hphm + "|" + "违停";
		contentStr =  parkVio.wfsj + "|" + UpLoad.getUpLoad(parkVio.isupfile, context);

		holder.name.setText(nameStr);
		holder.content.setText(contentStr);
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
