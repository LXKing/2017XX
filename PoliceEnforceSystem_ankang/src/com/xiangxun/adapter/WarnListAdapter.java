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
		if (convertView != null) {
			view = convertView;
		} else {
			view = LayoutInflater.from(context).inflate(R.layout.warnlist_item, null);
		}

		RelativeLayout warnitem = (RelativeLayout) view.findViewById(R.id.warn_item);
		TextView name = (TextView) view.findViewById(R.id.item_title);
		TextView content = (TextView) view.findViewById(R.id.item_content);
		TextView signandack = (TextView) view.findViewById(R.id.item_do);

		warnitem.setBackgroundResource(R.drawable.list_bg_blue_selector);

		WarnData data = lists.get(position);
		name.setText(data.getHphm() + "|" + DBManager.getInstance().getNameByTypeAndCodeFromDic("BLACKTYPE_CODE",data.getBklx()));

		String contentStr = data.getYjsj();
		if (data.getIsOk()== -1) {
			contentStr = contentStr + "|" + MsgType.getMsgIsSign(0);

			SpannableString ss = new SpannableString("签收并反馈");
			ss.setSpan(new UnderlineSpan(), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			signandack.setText(ss);
			signandack.setVisibility(View.VISIBLE);
			final int index = position;
			signandack.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (signAndAckClick!= null)
						signAndAckClick.SignAndAckOnClick(index);
				}
			});
		}
		else {
			signandack.setVisibility(View.GONE);
			contentStr = contentStr + "|" + MsgType.getMsgIsSign(1);
			if (data.getIsDo() == 1) {
				if ( data.getIsAck() == 0 || data.getIsAck() == -1) {
					contentStr = contentStr + "|" + MsgType.getMsgIsAck(0);
					warnitem.setBackgroundResource(R.drawable.list_red);
				}
				else
					contentStr = contentStr + "|" + MsgType.getMsgIsAck(1);
			}
		}

		content.setText(contentStr);
		return view;
	}

	private SignAndAckOnClick signAndAckClick;
	public void setSignAndAckOnClick(SignAndAckOnClick signAndAckClick) {
		this.signAndAckClick = signAndAckClick;
	}

	public interface SignAndAckOnClick {
		public void SignAndAckOnClick(int position);
	}
}
