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
import com.xiangxun.bean.Type;
import com.xiangxun.util.Tools;

import java.util.List;
/**
 * @package: com.xiangxun.widget
 * @ClassName: PublishSelectAffairsTypeDialog.java
 * @Description: 勤务类型选择器
 * @author: HanGJ
 * @date: 2015-8-12 下午2:51:43
 */
public class PublishSelectPlateColorTypeDialog extends Dialog {
	private Context mContext = null;
	private View mCustomView = null;
	private List<Type> types;
	private TextView mTvFinalStyle = null;
	private String mTitle = null;
	private TextView mTvCancle = null;
	private TextView mTvPublishSelectTitle = null;
	private ListView mLvPublishTypes = null;
	private StringBuffer mFinalStyle = new StringBuffer();
	private Type type;
	private AffairsTypeAdapter adapter;
	private int selectedItemPosition = 0;

	public PublishSelectPlateColorTypeDialog(Context context, List<Type> types, TextView finalStyle,String mTitle) {
		super(context, R.style.PublishDialog);
		this.mContext = context;
		this.types = types;
		this.mTvFinalStyle = finalStyle;
		this.mTitle = mTitle;
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
		adapter = new AffairsTypeAdapter(mContext, types);
		mLvPublishTypes.setAdapter(adapter);
		mLvPublishTypes.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectedItemPosition = position;
				mFinalStyle.delete(0, mFinalStyle.length());
				Type type = types.get(position);
				mFinalStyle.append(type.name);
				mTvFinalStyle.setText("");
				mTvFinalStyle.setText(mFinalStyle.toString());
				mTvFinalStyle.setTag(type);
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
	
	public Type getSelectedTypeItem() {
		type = types.get(selectedItemPosition);
		return type;
	}
	
	public void setSelection(int position){
		selectedItemPosition = position;
		mTvFinalStyle.setText(types.get(position).name);
	}

	public void setSelectName(String str) {
		int len = types.size();
		int i;
		for (i = 0; i < len; i++) {
			if (types.get(i).getName().equals(str)) {
				this.setSelection(i);
				break;
			}
		}
		if (i >= len) {
			this.setSelection(0);
		}

	}
	
	public int getSelectedItemPosition(){
		return selectedItemPosition;
	}
	
	public class AffairsTypeAdapter extends BaseAdapter {
		private List<Type> types = null;
		private LayoutInflater mInflater = null;

		public AffairsTypeAdapter(Context context, List<Type> types) {
			mInflater = LayoutInflater.from(context);
			this.types = types;
		}

		@Override
		public int getCount() {
			return types.size();
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
//			if (position == getCount() - 1) {
//				convertView.setBackgroundResource(R.drawable.phone_message_last_selector);
//			} else {
//				convertView.setBackgroundResource(R.drawable.phone_message_selector);
//			}
			holder.mTvStyleName.setText(types.get(position).getName());
			return convertView;
		}

		private class ViewHolder {
			private TextView mTvStyleName = null;
		}
	}

}
