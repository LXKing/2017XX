package com.xiangxun.adapter;

import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.xiangxun.activity.R;
import com.xiangxun.widget.photoview.PhotoView;

/**
 * @package: com.xiangxun.adapter
 * @ClassName: PhoneViewAdapter.java
 * @Description: PhoneView适配器
 * @author: HanGJ
 * @date: 2015-8-13 上午9:45:28
 */
public class PhoneViewAdapter extends PagerAdapter {
	private Context mContext = null;
	private List<String> mPictures = null;

	public PhoneViewAdapter(Context context, List<String> pictures) {
		super();
		this.mContext = context;
		this.mPictures = pictures;
	}

	@Override
	public int getCount() {
		return mPictures.size();
	}

	@Override
	public View instantiateItem(final ViewGroup container, int position) {
		View phoneViewLayout = View.inflate(mContext, R.layout.phone_view_layout, null);
		PhotoView photoView = (PhotoView) phoneViewLayout.findViewById(R.id.pv_deitail_picture);
		if (!TextUtils.isEmpty(mPictures.get(position))) {
			photoView.setImageURI(Uri.parse(mPictures.get(position)));
		}
		container.addView(phoneViewLayout, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		return phoneViewLayout;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}
}