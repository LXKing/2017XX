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
 * @ClassName: PublishSelectClearDialog.java
 * @Description: 清除数据选择器
 * @author: HanGJ
 * @date: 2015-9-8 上午10:39:48
 */
public class PublishSelectClearDialog extends Dialog {
	private Context mContext = null;
	private View mCustomView = null;
	private String[] clearItems;
	private String mTitle = null;
	private TextView mTvCancle = null;
	private TextView mTvPublishSelectTitle = null;
	private ListView mLvPublishTypes = null;
	private AffairsTypeAdapter adapter;
	private int deleteDay = 0;

	public PublishSelectClearDialog(Context context, String[] clearItems, String title) {
		super(context, R.style.PublishDialog);
		this.mContext = context;
		this.clearItems = clearItems;
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
		adapter = new AffairsTypeAdapter(mContext, clearItems);
		mLvPublishTypes.setAdapter(adapter);
		mLvPublishTypes.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				deleteDay = 7;
				if(selectclearDateItemClick != null){
					switch (position) {
					case 0:
						deleteDay = 7;
						break;
					case 1:
						deleteDay = 15;
						break;
					case 2:
						deleteDay = 30;
						break;
					case 3:
						deleteDay = 0;
						break;
					}
					selectclearDateItemClick.clearDateOnClick(deleteDay);
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

	public class AffairsTypeAdapter extends BaseAdapter {
		private String[] clearItems = null;
		private LayoutInflater mInflater = null;

		public AffairsTypeAdapter(Context context, String[] clearItems) {
			mInflater = LayoutInflater.from(context);
			this.clearItems = clearItems;
		}

		@Override
		public int getCount() {
			return clearItems.length;
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
			holder.mTvStyleName.setText(clearItems[position] + "");
			return convertView;
		}

		private class ViewHolder {
			private TextView mTvStyleName = null;
		}
	}
	
	private SelectclearDateItemClick selectclearDateItemClick;
	public void setSelectclearDateItemClick(SelectclearDateItemClick selectclearDateItemClick) {
		this.selectclearDateItemClick = selectclearDateItemClick;
	}

	public interface SelectclearDateItemClick {
		public void clearDateOnClick(int deleteDay);
	}

}
