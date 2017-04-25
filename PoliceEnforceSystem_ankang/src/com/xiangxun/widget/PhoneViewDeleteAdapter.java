package com.xiangxun.widget;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ViewFlipper;

import com.xiangxun.activity.R;
import com.xiangxun.widget.photoview.PhotoView;

/**
 * @package: com.xiangxun.widget
 * @ClassName: PhoneViewDeleteAdapter.java
 * @Description: PhoneView适配器
 * @author: HanGJ
 * @date: 2015-8-14 上午10:04:58
 */
public class PhoneViewDeleteAdapter extends PagerAdapter {
	private Context mContext = null;
	private List<String> mPictures = null;

	public PhoneViewDeleteAdapter(Context context, List<String> pictures) {
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
		View phoneViewLayout = View.inflate(mContext, R.layout.publish_delete_picture_layout, null);
		ViewFlipper vfPhoneView = (ViewFlipper) phoneViewLayout.findViewById(R.id.vf_phone_view);
		vfPhoneView.setDisplayedChild(0);
		PhotoView photoView = (PhotoView) phoneViewLayout.findViewById(R.id.pv_deitail_picture);

		String filePath = mPictures.get(position);
		if (new File(filePath).exists()) {
			if (!TextUtils.isEmpty(mPictures.get(position))) {
				photoView.setImageURI(Uri.parse(mPictures.get(position)));
			}
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