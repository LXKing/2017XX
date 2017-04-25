package com.xiangxun.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xiangxun.activity.R;
import com.xiangxun.bean.ResultData.VioInfo;

import java.util.List;

public class VioInfoAdapter extends BaseAdapter {
	private List<VioInfo> lists;
	private LayoutInflater inflater;

	public VioInfoAdapter(Context context, List<VioInfo> lists) {
		this.lists = lists;
		this.inflater = LayoutInflater.from(context);
	}

	public void setData(List<VioInfo> itemList) {
		this.lists = itemList;
		this.notifyDataSetChanged();
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
		ViewHolder holder = null;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.vio_info_item_layout, null);
			holder.textView01 = (TextView) convertView.findViewById(R.id.textView01);
			holder.textView02 = (TextView) convertView.findViewById(R.id.textView02);
			holder.textView03 = (TextView) convertView.findViewById(R.id.textView03);
			holder.textView04 = (TextView) convertView.findViewById(R.id.textView04);
			holder.textView05 = (TextView) convertView.findViewById(R.id.textView05);
			holder.textView06 = (TextView) convertView.findViewById(R.id.textView06);
			holder.textView07 = (TextView) convertView.findViewById(R.id.textView07);
			holder.textView08 = (TextView) convertView.findViewById(R.id.textView08);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		VioInfo vioInfo = lists.get(position);
		if(vioInfo.jkbj != null && "0".equals(vioInfo.jkbj)){
			holder.textView01.setText(Html.fromHtml(String.format(convertView.getContext().getString(R.string.trade02), "未缴罚款")));
		} else {
			holder.textView01.setText(Html.fromHtml(String.format(convertView.getContext().getString(R.string.trade02), "其他")));
		}
		holder.textView02.setText(Html.fromHtml(String.format(convertView.getContext().getString(R.string.trade01), vioInfo.wfxw !=null ? vioInfo.wfxw:"")));
		holder.textView03.setText(Html.fromHtml(String.format(convertView.getContext().getString(R.string.trade06), vioInfo.kf !=null ? vioInfo.kf:"")));
		holder.textView04.setText(Html.fromHtml(String.format(convertView.getContext().getString(R.string.trade05), vioInfo.zqmj !=null ? vioInfo.zqmj:"")));
		holder.textView05.setText(Html.fromHtml(String.format(convertView.getContext().getString(R.string.trade07), vioInfo.ysfkje !=null ? vioInfo.ysfkje:"")));
		holder.textView06.setText(Html.fromHtml(String.format(convertView.getContext().getString(R.string.trade03), vioInfo.wfsj !=null ? vioInfo.wfsj:"")));
		holder.textView07.setText(Html.fromHtml(String.format(convertView.getContext().getString(R.string.trade04), vioInfo.wfdz !=null ? vioInfo.wfdz:"")));
		holder.textView08.setText(Html.fromHtml(String.format(convertView.getContext().getString(R.string.trade08), vioInfo.kf_name !=null ? vioInfo.kf_name:"")));
		return convertView;
	}

	class ViewHolder {
		TextView textView01;
		TextView textView02;
		TextView textView03;
		TextView textView04;
		TextView textView05;
		TextView textView06;
		TextView textView07;
		TextView textView08;
	}
}
