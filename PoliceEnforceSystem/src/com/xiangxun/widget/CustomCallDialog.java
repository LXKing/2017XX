package com.xiangxun.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.xiangxun.activity.R;

import java.util.List;

/**
 * @package: com.xiangxun.widget
 * @ClassName: CustomCallDialog.java
 * @Description: 自定义打电话弹出框
 * @author: HanGJ
 * @date: 2015-8-27 上午9:42:43
 */
public class CustomCallDialog extends Dialog {

	public interface OnSelectBack {
		void selectBack(String mSelectedPhone);
	}


	private static Context mContext = null;
	private View mCustomView = null;
	private ListView mLvContactPhone = null;
	private List<String> mContactPhones = null;
	private static CustomCallDialog mCustomCallDialog;
	private String mSelectedPhone = null;
	private boolean isAddIntention = false;
	private PhoneNumberAdapter phoneNumberAdapter;

	private OnSelectBack back;

	public CustomCallDialog(Context context, List<String> contactPhones, boolean isAddIntention, OnSelectBack back) {
		super(context, R.style.CustomCallDialog);
		CustomCallDialog.mContext = context;
		this.mContactPhones = contactPhones;
		this.isAddIntention = isAddIntention;
		this.back = back;
	}

	public static CustomCallDialog getCustomCallDialog(Context context, List<String> contactPhones, boolean isAddIntention, OnSelectBack back) {
		if (mCustomCallDialog == null || mContext != context) {
			mCustomCallDialog = new CustomCallDialog(context, contactPhones, isAddIntention, back);
		}
		return mCustomCallDialog;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = LayoutInflater.from(mContext);
		mCustomView = inflater.inflate(R.layout.custom_call_dialog, null);
		setContentView(mCustomView);
		initView();
	}

	private void initView() {
		mLvContactPhone = (ListView) mCustomView.findViewById(R.id.lv_contact_phone);
		phoneNumberAdapter = new PhoneNumberAdapter(mContext, mContactPhones);
		mLvContactPhone.setAdapter(phoneNumberAdapter);
		mLvContactPhone.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> view, View currentView, int position, long id) {
				mSelectedPhone = mContactPhones.get(position).trim();
				// 不是添加意向是详情时直接打电话


				if (!isAddIntention) {
					if (mSelectedPhone.contains("#")) {
						back.selectBack(mSelectedPhone);
					} else {
						Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mSelectedPhone));
						mContext.startActivity(intent);
					}
				}
				dismiss();
			}
		});
	}

	@Override
	public void show() {
		if (mContext == null || ((Activity) mContext).isFinishing()) {
			mCustomCallDialog = null;
			return;
		}
		if (phoneNumberAdapter != null) {
			initView();
		}
		super.show();
	}

	public String getSelectedPhone() {
		return mSelectedPhone;
	}

	public class PhoneNumberAdapter extends BaseAdapter {
		private List<String> mPhoneNumbers = null;
		private LayoutInflater mInflater = null;

		public PhoneNumberAdapter(Context context, List<String> phoneNumbers) {
			mInflater = LayoutInflater.from(context);
			this.mPhoneNumbers = phoneNumbers;
		}

		@Override
		public int getCount() {
			return mPhoneNumbers.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.custom_call_dialog_item, null);
				holder = new ViewHolder();
				holder.mTvPhoneNumber = (TextView) convertView.findViewById(R.id.tv_phone_number);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (position == getCount() - 1) {
				convertView.setBackgroundResource(R.drawable.phone_message_last_selector);
			} else {
				convertView.setBackgroundResource(R.drawable.phone_message_selector);
			}

			if (mPhoneNumbers.get(position).contains("#")) {
				String aig = mPhoneNumbers.get(position).split("#")[0] + mPhoneNumbers.get(position).split("#")[1];
				holder.mTvPhoneNumber.setText(aig);
			} else {
				holder.mTvPhoneNumber.setText(mPhoneNumbers.get(position));
			}
			return convertView;
		}

		private class ViewHolder {
			private TextView mTvPhoneNumber = null;
		}
	}

	public void setPhones(List<String> contactPhones) {
		this.mContactPhones = contactPhones;
	}

}
