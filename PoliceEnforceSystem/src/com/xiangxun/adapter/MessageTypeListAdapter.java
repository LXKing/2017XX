package com.xiangxun.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiangxun.activity.R;
import com.xiangxun.activity.mine.FragmentFactory;
import com.xiangxun.activity.mine.NewMessageCenter.MessagePackageInfo;
import com.xiangxun.app.BaseActivity;
import com.xiangxun.app.BaseFragment;
import com.xiangxun.bean.MsgType;
import com.xiangxun.bean.SystemMessage;

public class MessageTypeListAdapter extends BaseAdapter{
	private List<MessagePackageInfo> lists;
	private Context mContext;

	public MessageTypeListAdapter(List<MessagePackageInfo> lists, Context mContext) {
		this.lists = lists;
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return lists.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return lists.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = null;
		if (convertView != null) {
			view = convertView;
		} else {
			view = LayoutInflater.from(mContext).inflate(R.layout.messagetypelist_item, null);
		}
		
		TextView name = (TextView) view.findViewById(R.id.messagetype_name);
		TextView allCount = (TextView) view.findViewById(R.id.readed_count);
		TextView noReadcount = (TextView) view.findViewById(R.id.noread_count);
		
		MessagePackageInfo info = lists.get(position);
		name.setText(info.msgtype);
		allCount.setText("全部  "+ info.allCount);
		noReadcount.setText("未读  "+ info.noReadCount);
		return view;
	}
}
