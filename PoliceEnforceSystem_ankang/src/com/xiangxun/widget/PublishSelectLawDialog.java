package com.xiangxun.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.xiangxun.activity.R;
import com.xiangxun.util.Tools;

/**
 * @package: com.xiangxun.widget
 * @ClassName: PublishSelectLawDialog.java
 * @Description: 法律条文选择器
 * @author: HanGJ
 * @date: 2015-9-8 上午10:39:48
 */
public class PublishSelectLawDialog extends Dialog {
	private Context mContext = null;
	private View mCustomView = null;
	private String[] lawItems;
	private String mTitle = null;
	private TextView mTvCancle = null;
	private TextView mTvFinalStyle = null;
	private TextView mTvPublishSelectTitle = null;
	private ListView mLvPublishTypes = null;
	private AffairsTypeAdapter adapter;
	private StringBuffer mFinalStyle = new StringBuffer();
	private int selectedItemPosition = 0;

	public PublishSelectLawDialog(Context context, String[] lawItems, TextView finalStyle, String title) {
		super(context, R.style.PublishDialog);
		this.mContext = context;
		this.lawItems = lawItems;
		this.mTvFinalStyle = finalStyle;
		mTitle = title;
		init();
	}

	private void init() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		mCustomView = inflater.inflate(R.layout.publish_select_dialog, null);
		setContentView(mCustomView);
		Window window = this.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
		lp.width = dm.widthPixels - Tools.dip2px(mContext, 5.0f);
		lp.height = LayoutParams.WRAP_CONTENT;
		initView();
	}

	@Override
	public void show() {
		super.show();
	}

	private void initView() {
		mTvCancle = (TextView) mCustomView.findViewById(R.id.tv_publish_select_dialog_cancle);
		mTvPublishSelectTitle = (TextView) mCustomView.findViewById(R.id.tv_publish_select_dialog_title);
		mTvPublishSelectTitle.setText(mTitle);
		mLvPublishTypes = (ListView) mCustomView.findViewById(R.id.lv_publish_select_dialog);
		adapter = new AffairsTypeAdapter(mContext, lawItems);
		mLvPublishTypes.setAdapter(adapter);
		mLvPublishTypes.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectedItemPosition = position;
				mFinalStyle.delete(0, mFinalStyle.length());
				mFinalStyle.append(lawItems[position]);
				mTvFinalStyle.setText("");
				mTvFinalStyle.setText(mFinalStyle.toString());
				if(selectLawDateItemClick != null){
					selectLawDateItemClick.lawDateOnClick(position);
				}
				dismiss();
			}
		});

		mTvCancle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}
	
	public void setSelection(int position){
		selectedItemPosition = position;
		mTvFinalStyle.setText(lawItems[position]);
	}
	
	public int getSelectedItemPosition(){
		return selectedItemPosition;
	}

	public class AffairsTypeAdapter extends BaseAdapter {
		private String[] lawItems = null;
		private LayoutInflater mInflater = null;

		public AffairsTypeAdapter(Context context, String[] lawItems) {
			mInflater = LayoutInflater.from(context);
			this.lawItems = lawItems;
		}

		@Override
		public int getCount() {
			return lawItems.length;
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
				convertView = mInflater.inflate(R.layout.publish_select_dialog_item, null);
				holder = new ViewHolder();
				holder.mTvStyleName = (TextView) convertView.findViewById(R.id.tv_publish_slect_dialog_string);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.mTvStyleName.setText(lawItems[position] + "");
			return convertView;
		}

		private class ViewHolder {
			private TextView mTvStyleName = null;
		}
	}
	
	private SelectLawDateItemClick selectLawDateItemClick;
	public void setSelectLawDateItemClick(SelectLawDateItemClick selectLawDateItemClick) {
		this.selectLawDateItemClick = selectLawDateItemClick;
	}

	public interface SelectLawDateItemClick {
		public void lawDateOnClick(int position);
	}

}
