package com.xiangxun.activity.tool;

import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.xiangxun.activity.R;
import com.xiangxun.adapter.AddressBookListAdapter;
import com.xiangxun.app.BaseActivity;
import com.xiangxun.bean.AddressBook;
import com.xiangxun.db.DBManager;
import com.xiangxun.util.ConstantStatus;
import com.xiangxun.widget.CustomCallDialog;
import com.xiangxun.widget.CustomCallDialog.OnSelectBack;
import com.xiangxun.widget.MsgToast;
import com.xiangxun.widget.TitleView;
import com.xiangxun.widget.XListView;
import com.xiangxun.widget.XListView.IXListViewListener;

import org.linphone.LinphoneManager;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCoreListener.LinphoneCallStateListener;
import org.linphone.core.LinphoneProxyConfig;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends BaseActivity implements OnClickListener, IXListViewListener, OnSelectBack, LinphoneCallStateListener {
	private static int listState = -1;
	private TitleView titleView;
	private List<AddressBook> addressbooks = new ArrayList<AddressBook>();
	private int currentPage = 1;
	private int PageSize = 10;
	private int totalSize = 0;
	private AddressBookListAdapter mAdapter;
	private ImageView mIVdelete;
	private ImageView mIV_search;
	private EditText mETSearch;
	private XListView mXListView;
	private ViewFlipper mVF;
	private String keywords = "";
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			stopXListView();
			switch (msg.what) {
				case ConstantStatus.SREACHCONTATSSUCCESS:
					@SuppressWarnings("unchecked")
					List<AddressBook> books = (List<AddressBook>) msg.obj;
					if (books != null) {
						totalSize = books.size();
						addressbooks.addAll(books);
					}
					mVF.setDisplayedChild(0);
					// 没有加载到数据
					if (addressbooks.size() == 0) {
						mVF.setDisplayedChild(2);
					}
					mAdapter.setData(addressbooks);
					break;
				case ConstantStatus.SREACHCONTATSFALSE:

					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts_list_layout);
		initView();
		initData();
		initListener();
	}

	@Override
	public void initView() {
		titleView = (TitleView) findViewById(R.id.tv_comm_title);
		titleView.setTitle(R.string.tongxunlu);
		mIVdelete = (ImageView) findViewById(R.id.delete);
		mETSearch = (EditText) findViewById(R.id.editText_search);
		mIV_search = (ImageView) findViewById(R.id.mIV_search);
		mXListView = (XListView) findViewById(R.id.xlistview);
		mVF = (ViewFlipper) findViewById(R.id.viewFlipper);
	}

	@Override
	public void initListener() {
		titleView.setLeftBackOneListener(R.drawable.back_normal, new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});
		mXListView.setPullLoadEnable(true);
		mXListView.setXListViewListener(this);
		mIVdelete.setOnClickListener(this);
		mIV_search.setOnClickListener(this);
		mETSearch.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable arg0) {
			}

			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				if (arg0.toString().length() > 0) {
					mIVdelete.setVisibility(View.VISIBLE);
				} else {
					mIVdelete.setVisibility(View.INVISIBLE);
				}
			}
		});
		mXListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				List<String> mPhones = new ArrayList<String>();
				AddressBook ab = addressbooks.get(position - 1);
				if (ab.phone != null && !ab.phone.equals("")) {
					mPhones.add(ab.phone);
				}
//                不知道那个字段是Linphone的电话，先用868299021323554代替。
				mPhones.add("网络电话#" + "351662010206813" + "#" + ab.name);
				CustomCallDialog callDialog = new CustomCallDialog(ContactsActivity.this, mPhones, false, ContactsActivity.this);
				callDialog.setCanceledOnTouchOutside(true);
				callDialog.show();
			}
		});
	}

	@Override
	public void initData() {
		listState = ConstantStatus.listStateFirst;
		IntentFilter filter_dynamic = new IntentFilter();
		filter_dynamic.addAction(ConstantStatus.FILESELECTORACTIONXML);
		filter_dynamic.addAction(ConstantStatus.FILESELECTORACTIONEXCEL);

		mAdapter = new AddressBookListAdapter(this, mXListView);
		mXListView.setAdapter(mAdapter);


		if (!DBManager.getInstance().isAddressbookSaved()) {
			mVF.setDisplayedChild(2);
		} else {
			RequestList();
		}
	}

	private void RequestList() {
		switch (listState) {
			case ConstantStatus.listStateFirst:
				addressbooks.clear();
				mVF.setDisplayedChild(1);
				break;
			case ConstantStatus.listStateRefresh:
				addressbooks.clear();
				mVF.setDisplayedChild(0);
				break;
			case ConstantStatus.listStateLoadMore:
				mVF.setDisplayedChild(0);
				break;
		}
		DBManager.getInstance().getAddressBooks(keywords, currentPage, PageSize, mHandler);
	}

	@Override
	protected void onDestroy() {
		if (addressbooks != null) {
			addressbooks.clear();
			addressbooks = null;
		}
		super.onDestroy();
	}

	// xLisView 停止
	private void stopXListView() {
		mXListView.stopRefresh();
		mXListView.stopLoadMore();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.delete:
				mETSearch.setText("");
				keywords = "";
				onRefresh(null);
				break;

			case R.id.mIV_search:
				if (TextUtils.isEmpty(mETSearch.getText().toString().trim())) {
					MsgToast.geToast().setMsg("请输入关键字");
				} else {
					keywords = mETSearch.getText().toString().trim();
					listState = ConstantStatus.listStateRefresh;
					RequestList();
				}
				break;
		}
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

	@Override
	public void selectBack(String mSelectedPhone) {
		LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
		if (lc != null) {
			lc.addListener(this);
			LinphoneProxyConfig lpc = lc.getDefaultProxyConfig();
			if (lpc != null) {
				String to = lpc.normalizePhoneNumber(mSelectedPhone.split("#")[1]);
				LinphoneManager.getInstance().newOutgoingCall(to, mSelectedPhone.split("#")[2]);
			} else {
				LinphoneManager.getInstance().newOutgoingCall(mSelectedPhone.split("#")[1], mSelectedPhone.split("#")[2]);
			}
		}

	}

	@Override
	public void callState(LinphoneCore linphoneCore, LinphoneCall linphoneCall, LinphoneCall.State state, String s) {
		//这块后退功能要么执行一次要么不执行。
		if (state == LinphoneCall.State.Error) {
			MsgToast.geToast().setMsg("对方无法接通，请稍后再拨！");

		}
		if (state == LinphoneCall.State.Connected) {
			moveTaskToBack(true);
		}
	}
}
