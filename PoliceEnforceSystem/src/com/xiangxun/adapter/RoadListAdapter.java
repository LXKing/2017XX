package com.xiangxun.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.xiangxun.activity.R;
import com.xiangxun.bean.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/11.
 */

public class RoadListAdapter extends BaseAdapter implements Filterable {
	private ArrayFilter mFilter;
	private List<Type> RoadGroup;
	private Context context;
	private List<Type> mUnfilteredData;

	public RoadListAdapter(List<Type> roadGroup, Context context) {
		this.RoadGroup = roadGroup;
		this.context = context;
	}

	@Override
	public int getCount() {
		return RoadGroup ==null ? 0: RoadGroup.size();
	}

	@Override
	public Object getItem(int position) {
		return RoadGroup.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		ViewHolder holder;
		if(convertView==null){
			view = View.inflate(context, R.layout.auto_list_item, null);

			holder = new ViewHolder();
			holder.tv_name = (TextView) view.findViewById(R.id.tv_name);

			view.setTag(holder);
		}else{
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}

		Type road = RoadGroup.get(position);
		holder.tv_name.setText(road.getCode() +"-" + road.getName());
		return view;
	}

	static class ViewHolder{
		public TextView tv_name;
	}

	@Override
	public Filter getFilter() {
		if (mFilter == null) {
			mFilter = new ArrayFilter();
		}
		return mFilter;
	}

	private class ArrayFilter extends Filter {
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();

			if (mUnfilteredData == null) {
				mUnfilteredData = RoadGroup;
			}

			if (constraint == null || constraint.length() == 0) {
				List<Type> list = mUnfilteredData;
				results.values = list;
				results.count = list.size();
			} else {
				String constraintString = constraint.toString().toLowerCase();

				List<Type> unfilteredValues = mUnfilteredData;
				int count = unfilteredValues.size();

				List<Type> newValues = new ArrayList<Type>();
				for (int i = 0; i < count; i++) {
					Type road = unfilteredValues.get(i);
					if (road != null) {
						if(road.getName().contains(constraintString) || road.getCode().contains(constraintString)){
							newValues.add(road);
						}
					}
				}

				results.values = newValues;
				results.count = newValues.size();
			}

			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			RoadGroup = (List<Type>) results.values;
			if (results.count > 0) {
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}
		}
	}
}
