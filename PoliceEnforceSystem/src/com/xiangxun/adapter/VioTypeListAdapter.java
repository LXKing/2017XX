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
import com.xiangxun.db.DBManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/11.
 */

public class VioTypeListAdapter extends BaseAdapter implements Filterable {
	private ArrayFilter mFilter;
	private List<Type> VioGroup;
	private Context context;
	private List<Type> mUnfilteredData;

	public VioTypeListAdapter(List<Type> VioGroup, Context context) {
		this.VioGroup = VioGroup;
		this.context = context;
	}

	@Override
	public int getCount() {
		return VioGroup ==null ? 0: VioGroup.size();
	}

	@Override
	public Object getItem(int position) {
		return VioGroup.get(position);
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

		Type Vio = VioGroup.get(position);
		holder.tv_name.setText(Vio.code + "-" + Vio.getName());
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
				mUnfilteredData = VioGroup;
				if (mUnfilteredData.size() < 500) {
					mUnfilteredData = DBManager.getInstance().getVioTypeList();
				}
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
					Type Vio = unfilteredValues.get(i);
					if (Vio != null) {
						if(Vio.getName().contains(constraintString) || Vio.getCode().contains(constraintString)){
							newValues.add(Vio);
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
			VioGroup = (List<Type>) results.values;
			if (results.count > 0) {
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}
		}
	}
}
