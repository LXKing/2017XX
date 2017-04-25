package com.xiangxun.activity.mine;

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
import com.xiangxun.adapter.SysMessageListAdapter;
import com.xiangxun.app.BaseFragment;
import com.xiangxun.bean.MsgType;
import com.xiangxun.bean.SystemMessage;
import com.xiangxun.db.DBManager;
import com.xiangxun.util.ConstantStatus;
import com.xiangxun.widget.MsgToast;
import com.xiangxun.widget.XListView;
import com.xiangxun.widget.XListView.IXListViewListener;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class SystemMessageFragment extends BaseFragment implements IXListViewListener {
	private static int listState = -1;
	private int currentPage = 1;
	private int PageSize = 10;
	private int totalSize = 0;
	private List<SystemMessage> messages = new ArrayList<SystemMessage>();
	private SysMessageListAdapter mAdapter;
	private XListView mXListView;
	private ViewFlipper mVF;
	private int messageType = 1;
	private SystemMessage message;
	private int cur_id;

	@SuppressLint("HandlerLeak")
	private Handler mUIHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			stopXListView();
			switch (msg.what) {
			case ConstantStatus.SREACH_SYSTEM_MESSAGE_SUCCESS:
				@SuppressWarnings("unchecked")
				List<SystemMessage> smsgs = (List<SystemMessage>) msg.obj;
				if (smsgs != null) {
					totalSize = smsgs.size();
					messages.addAll(smsgs);
				}
				// 没有加载到数据

				if (messages.size() == 0) {
					mVF.setDisplayedChild(2);
				} else {
					mVF.setDisplayedChild(0);
				}
				mAdapter.notifyDataSetChanged();
				break;
			case ConstantStatus.SREACH_SYSTEM_MESSAGE_FALSE:
				break;
			}
		}
	};

	public static SystemMessageFragment newInstance(int messageType) {
		SystemMessageFragment newFragment = new SystemMessageFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("messageType", messageType);
		newFragment.setArguments(bundle);
		return newFragment;
	}

//	@SuppressLint("ValidFragment")
//	public SystemMessageFragment(int messageType) {
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
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("删除全部该类型消息");
				if (6 == messages.get(arg2 - 1).msgtype)
					builder.setMessage("是否删除全部该类型消息（除未签和未反馈的）");
				else
					builder.setMessage("是否删除全部该类型消息");
				builder.setPositiveButton(R.string.deleteAll, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						final int num = DBManager.getInstance().getSmsgCount();
						if (num == 0)
							return;
						DBManager.getInstance().DeleteSysmsgByType(messages.get(arg2 - 1).msgtype);
						//messages.clear();
						for (int i = messages.size() - 1; i >= 0 ; i --) {
							if (messages.get(i).isSigned == 1 && messages.get(i).isAck != 0) {
								messages.remove(i);
							}
						}
						mAdapter.notifyDataSetChanged();
						/*
						 * for (int i = 0; i <= num; i++) {
						 * DBManager.getInstance().DeleteSysmsg(i); }
						 */
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
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				message = messages.get(position - 1);
				final int id = message._id;
				DBManager.getInstance().isReadSysmsg(id);
				message.isread = 1;
				final int pos = position - 1;
				mAdapter.notifyDataSetChanged();
				cur_id = message._id;

				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle(message.datetime + "    " + MsgType.getMsgIsRead(message.isread));
				builder.setMessage(message.text);

				builder.setNeutralButton(R.string.delete, new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
						messages.remove(pos);
						DBManager.getInstance().DeleteSysmsg(id);
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
		mAdapter = new SysMessageListAdapter(getActivity(), messages);
		mXListView.setAdapter(mAdapter);

		if (DBManager.getInstance().getSmsgCount() <= 0) {
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
		DBManager.getInstance().getSmsg(currentPage, PageSize, messageType, mUIHandler);
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
