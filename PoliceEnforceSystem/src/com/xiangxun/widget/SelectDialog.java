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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xiangxun.activity.R;
import com.xiangxun.util.Tools;

/**
 * @package: com.xiangxun.widget
 * @ClassName: SelectLayerDialog.java
 * @Description: 监测层选择器
 * @author: HanGJ
 * @date: 2015-9-8 上午10:39:48
 */
public class SelectDialog extends Dialog {
	private Context mContext = null;
	private View mCustomView = null;
	private String[] typeItems;
	private Boolean[] selectItems;
	private Boolean isRadioBox;
	
	private String mTitle = null;
	private TextView mTvCancle = null;
	private TextView mTvEnter = null;
	private TextView mTvPublishSelectTitle = null;
	private ListView mLvPublishTypes = null;
	private AffairsTypeAdapter adapter;
	private int layer;
	
	public SelectDialog(Context context, String[] typeItems, Boolean[] selectItems, Boolean isRadioBox, String title ) {
		super(context, R.style.PublishDialog);
		this.mContext = context;
		this.typeItems = typeItems;
		this.selectItems = selectItems;
		this.isRadioBox = isRadioBox;
		mTitle = title;
		init();
	}

	private void init() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		mCustomView = inflater.inflate(R.layout.publish_multiselect_dialog, null);
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

	public void setItems(Boolean[] selectItems) {
		adapter.setCheckItems(selectItems);
	}
	
	private void initView() {
		mTvCancle = (TextView) mCustomView.findViewById(R.id.tv_publish_select_dialog_cancle);
		mTvEnter = (TextView) mCustomView.findViewById(R.id.tv_publish_select_dialog_enter);
		mTvPublishSelectTitle = (TextView) mCustomView.findViewById(R.id.tv_publish_select_dialog_title);
		mTvPublishSelectTitle.setText(mTitle);
		mLvPublishTypes = (ListView) mCustomView.findViewById(R.id.lv_publish_select_dialog);
		adapter = new AffairsTypeAdapter(mContext, typeItems, selectItems, isRadioBox);
		mLvPublishTypes.setAdapter(adapter);
		mTvCancle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				adapter.setCheckItems(selectItems);
				dismiss();
			}
		});
		mTvEnter.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (selectItemClick != null){
					Boolean[] checkItems = adapter.getCheckItems();
					int position = 0;
					for (int i = 0; i < selectItems.length; i++) {
						selectItems[i] = checkItems[i];
						if (checkItems[i] == true)
							position = i;
					}					
					selectItemClick.itemOnClick(selectItems, position);
				}
				dismiss();
			}
		});
		
		
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		adapter.setCheckItems(selectItems);
		super.onBackPressed();
	}

	public class AffairsTypeAdapter extends BaseAdapter {
		private String[] clearItems = null;
		private Boolean[] checkItems = null;
		private LayoutInflater mInflater = null;
		private Boolean isRadioBox = false;

		public AffairsTypeAdapter(Context context, String[] clearItems, Boolean[] selectItems, Boolean isRadioBox) {
			mInflater = LayoutInflater.from(context);
			this.clearItems = clearItems;
			this.checkItems = new Boolean[selectItems.length];
			for (int i = 0; i < selectItems.length; i++) {
				this.checkItems[i] = selectItems[i];
			}
			this.isRadioBox = isRadioBox;
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

		public Boolean[] getCheckItems() {
			return checkItems;
		}

		public void setCheckItems(Boolean[] selectItems) {
			for (int i = 0; i < selectItems.length; i++) {
				this.checkItems[i] = selectItems[i];
			}
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.publish_multiselect_dialog_item, null);
				holder = new ViewHolder();
				holder.text = (TextView) convertView.findViewById(R.id.tv_publish_slect_dialog_string);
				if (isRadioBox) {
					holder.radiobox = (ImageView) convertView.findViewById(R.id.tv_publish_radiobox_dialog_image);
					holder.radiobox.setVisibility(View.VISIBLE);
				} else {
					holder.checkbox = (ImageView) convertView.findViewById(R.id.tv_publish_checkbox_dialog_image);
					holder.checkbox.setVisibility(View.VISIBLE);
				}
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.text.setText(clearItems[position] + "");
			
			if (isRadioBox) {
				holder.radiobox.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (checkItems[position] == false) {
							for (int i = 0, len = checkItems.length; i < len; i++)
								checkItems[i] = false;
							checkItems[position] = true;
							
							notifyDataSetChanged();
						}
					}
				});				
				
				if (checkItems[position])
					holder.radiobox.setImageResource(R.drawable.radiobox_press);
				else
					holder.radiobox.setImageResource(R.drawable.radiobox_normal);
			} else {
				holder.checkbox.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (checkItems[position]) {
							checkItems[position] = false;
							
							notifyDataSetChanged();
						}
						else {
							checkItems[position] = true;
							notifyDataSetChanged();
						}
					}
				});
				
				if (checkItems[position])
					holder.checkbox.setImageResource(R.drawable.checkbox_press);
				else
					holder.checkbox.setImageResource(R.drawable.checkbox_normal);
			}
			
			return convertView;
		}

		private class ViewHolder {
			private TextView text = null;
			private ImageView checkbox = null;
			private ImageView radiobox = null;
		}
	}
	
	private SelectItemClick selectItemClick;
	public void setSelectItemClick(SelectItemClick selectItemClick) {
		this.selectItemClick = selectItemClick;
	}

	public interface SelectItemClick {
		public void itemOnClick(Boolean[] checks, int position);
	}

}
