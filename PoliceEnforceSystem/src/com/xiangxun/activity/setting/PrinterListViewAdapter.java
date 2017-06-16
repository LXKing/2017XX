package com.xiangxun.activity.setting;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.xiangxun.activity.R;

import java.util.List;

/**
 * http://blog.csdn.net/lmj623565791/article/details/40212367
 * 
 * @author zhy
 * 
 * @param <T>
 */
public class PrinterListViewAdapter extends BaseAdapter {

	protected Context mContext;
	/**
	 * 存储所有可见的Node
	 */
	protected List<BluetoothDevice> mNodes;
	protected LayoutInflater mInflater;

	/**
	 * 
	 * @param mTree
	 * @param context
	 * @param datas
	 * @param defaultExpandLevel
	 *            默认展开几级树
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public PrinterListViewAdapter(ListView mTree, Context context,
								  List<BluetoothDevice> datas) throws IllegalArgumentException,
			IllegalAccessException {
		mContext = context;
		/**
		 * 过滤出可见的Node
		 */
		mNodes = (List<BluetoothDevice>) datas;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return mNodes.size();
	}

	@Override
	public Object getItem(int position) {
		return mNodes.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		BluetoothDevice node = mNodes.get(position);
		convertView = getConvertView(node, position, convertView, parent);
		// 设置内边距
		convertView.setPadding(0, 3, 3, 3);
		return convertView;
	}

	public View getConvertView(final BluetoothDevice node, int position,
			View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.printer_list_item, parent,
					false);
			viewHolder = new ViewHolder();

			viewHolder.name = (TextView) convertView
					.findViewById(R.id.item_msg1);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.name.setText(node.getName());

		return convertView;
	}

	private final class ViewHolder {
		TextView name;
	}
}
