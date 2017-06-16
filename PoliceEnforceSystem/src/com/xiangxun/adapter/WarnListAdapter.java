package com.xiangxun.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiangxun.activity.R;
import com.xiangxun.bean.MsgType;
import com.xiangxun.bean.WarnData;
import com.xiangxun.db.DBManager;
import com.xiangxun.widget.XListView;

import java.util.List;

public class WarnListAdapter extends BaseAdapter {
	private List<WarnData> lists;
	private Context context;
	private XListView mListView;
	private int size = 0;

	public WarnListAdapter(Context context, XListView mXListView) {
		this.context = context;
		this.mListView = mXListView;
	}

	public void setData(List<WarnData> itemList) {
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
			holder = new ViewHolder();

			view = LayoutInflater.from(context).inflate(R.layout.warnlist_item, null);

			holder.warnitem = (RelativeLayout) view.findViewById(R.id.warn_item);
			holder.name = (TextView) view.findViewById(R.id.item_title);
			holder.content = (TextView) view.findViewById(R.id.item_content);
			holder.signandack = (TextView) view.findViewById(R.id.item_do);

			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}

		holder.warnitem.setBackgroundResource(R.drawable.list_bg_blue_selector);

		WarnData data = lists.get(position);
		holder.name.setText(data.getHphm() + "|" + DBManager.getInstance().getNameByTypeAndCodeFromDic("BLACKTYPE_CODE",data.getBklx()));

		String contentStr = data.getYjsj();
		if (data.getIsOk()== -1) {
			contentStr = contentStr + "|" + MsgType.getMsgIsSign(0);

			SpannableString ss = new SpannableString("签收并反馈");
			ss.setSpan(new UnderlineSpan(), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			holder.signandack.setText(ss);
			holder.signandack.setVisibility(View.VISIBLE);
			final int index = position;
			holder.signandack.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (signAndAckClick!= null)
						signAndAckClick.SignAndAckOnClick(index);
				}
			});
		}
		else {
			holder.signandack.setVisibility(View.GONE);

			if (data.getIsAck() > 1) {
				//签收反馈异常
				contentStr = contentStr + "|" + MsgType.getMsgIsAck(data.getIsAck());
				holder.warnitem.setBackgroundResource(R.drawable.list_blue);
			} else {
				contentStr = contentStr + "|" + MsgType.getMsgIsSign(1);
				if (data.getIsDo() == 1) {
					if (data.getIsAck() == 0 || data.getIsAck() == -1) {
						contentStr = contentStr + "|" + MsgType.getMsgIsAck(0);
						holder.warnitem.setBackgroundResource(R.drawable.list_red);
					} else
						contentStr = contentStr + "|" + MsgType.getMsgIsAck(1);
				}
			}
		}

		holder.content.setText(contentStr);
		return view;
	}

	static class ViewHolder{
		public RelativeLayout warnitem;
		public TextView name;
		public TextView content;
		public TextView signandack;
	}

	private SignAndAckOnClick signAndAckClick;
	public void setSignAndAckOnClick(SignAndAckOnClick signAndAckClick) {
		this.signAndAckClick = signAndAckClick;
	}

	public interface SignAndAckOnClick {
		public void SignAndAckOnClick(int position);
	}
}
