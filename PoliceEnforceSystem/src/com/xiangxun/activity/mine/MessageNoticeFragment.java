package com.xiangxun.activity.mine;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ViewFlipper;

import com.xiangxun.activity.R;
import com.xiangxun.adapter.TextMessageListAdapter;
import com.xiangxun.app.BaseFragment;
import com.xiangxun.bean.MsgType;
import com.xiangxun.bean.TextMessage;
import com.xiangxun.db.DBManager;
import com.xiangxun.util.ConstantStatus;
import com.xiangxun.widget.MsgToast;
import com.xiangxun.widget.XListView;
import com.xiangxun.widget.XListView.IXListViewListener;

@SuppressLint("ValidFragment")
public class MessageNoticeFragment extends BaseFragment implements IXListViewListener {
	private static int listState = -1;
	private int currentPage = 1;
	private int PageSize = 10;
	private int totalSize = 0;
	private List<TextMessage> messages = new ArrayList<TextMessage>();
	private TextMessageListAdapter mAdapter;
	private XListView mXListView;
	private ViewFlipper mVF;
	private int messageType = 1;
	@SuppressLint("HandlerLeak")
	private Handler mUIHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			stopXListView();
			switch (msg.what) {
			case ConstantStatus.SREACH_SYSTEM_NITICE_SUCCESS:
				@SuppressWarnings("unchecked")
				List<TextMessage> textMessages = (List<TextMessage>) msg.obj;
				if (textMessages != null) {
					totalSize = textMessages.size();
					messages.addAll(textMessages);
				}
				// 没有加载到数据
				if (messages.size() == 0) {
					mVF.setDisplayedChild(2);
				} else {
					mVF.setDisplayedChild(0);
				}
				mAdapter.notifyDataSetChanged();
				break;
			case ConstantStatus.SREACH_SYSTEM_NITICE_FALSE:

				break;
			}
		}
	};
	
	public MessageNoticeFragment newInstance(int messageType) {
		MessageNoticeFragment newFragment = new MessageNoticeFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("messageType", messageType);
		newFragment.setArguments(bundle);
		return newFragment;

	}
//	public MessageNoticeFragment(int messageType) {
//		super();
//		this.messageType = messageType;
//	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.sysmessage, container, false);
		messageType = getArguments().getInt("messageType");
		initView(view);
		initListener();
		initData();
		return view;
	}

	@Override
	public void initView(View view) {
		mXListView = (XListView) view.findViewById(R.id.xlistview);
		mVF = (ViewFlipper) view.findViewById(R.id.viewFlipper);
	}

	@Override
	public void initListener() {
		mXListView.setPullLoadEnable(true);
		mXListView.setXListViewListener(this);
		mXListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle(R.string.deleteAllMessage);
				builder.setMessage(R.string.ifDeleteAllMessage);
				builder.setPositiveButton(R.string.deleteAll, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						messages.clear();
						mAdapter.notifyDataSetChanged();
						final int num = DBManager.getInstance().getTmsgCount();
						new Thread(new Runnable() {

							@Override
							public void run() {
								if (num == 0)
									return;
								for (int i = 0; i <= num; i++) {
									DBManager.getInstance().DeleteTmsg(i);
								}
							}
						}).start();
					}
				});
				builder.setNegativeButton(R.string.returnbutlog, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				builder.create().show();
				return true;
			}
		});
		mXListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				final int id = messages.get(arg2 - 1)._id;
				DBManager.getInstance().isReadTmsg(id);
				messages.get(arg2 - 1).isread = 1;
				final int pos = arg2 - 1;
				mAdapter.notifyDataSetChanged();
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle(messages.get(arg2 - 1).datetime + "    " + MsgType.getMsgIsRead(messages.get(arg2).isread));
				builder.setMessage(messages.get(arg2 - 1).text);

				builder.setNeutralButton(R.string.delete, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						messages.remove(pos);
						DBManager.getInstance().DeleteTmsg(id);
						mAdapter.notifyDataSetChanged();
					}
				});
				builder.setNegativeButton(R.string.returnbutlog, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				builder.create().show();

			}
		});
	}

	@Override
	public void initData() {
		listState = ConstantStatus.listStateFirst;
		mAdapter = new TextMessageListAdapter(getActivity(), messages);
		mXListView.setAdapter(mAdapter);

		if (DBManager.getInstance().getTmsgCount() <= 0) {
			mVF.setDisplayedChild(2);
		} else {
			RequestList();
		}
	}

	private void RequestList() {
		switch (listState) {
		case ConstantStatus.listStateFirst:
			messages.clear();
			mVF.setDisplayedChild(1);
			break;
		case ConstantStatus.listStateRefresh:
			mVF.setDisplayedChild(0);
			messages.clear();
			break;
		case ConstantStatus.listStateLoadMore:
			mVF.setDisplayedChild(0);
			break;
		}
		//dbManager.getTmsg(currentPage, PageSize, mUIHandler);
	}

	@Override
	public void load() {
		onRefresh(null);
	}

	@Override
	public void onRefresh(View v) {
		currentPage = 1;
		listState = ConstantStatus.listStateRefresh;
		RequestList();
	}

	@Override
	public void onLoadMore(View v) {
		if (totalSize < PageSize) {
			MsgToast.geToast().setMsg("已经是最后一页");
			mXListView.removeFooterView(mXListView.mFooterView);
		} else {
			currentPage++;
			listState = ConstantStatus.listStateLoadMore;
			RequestList();
		}
	}

	// xLisView 停止
	private void stopXListView() {
		mXListView.stopRefresh();
		mXListView.stopLoadMore();
	}
}
