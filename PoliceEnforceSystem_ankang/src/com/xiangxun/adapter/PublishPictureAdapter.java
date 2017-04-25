package com.xiangxun.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.xiangxun.activity.R;
import com.xiangxun.common.DcHttpClient;
import com.xiangxun.common.LocalNetWorkView;
import com.xiangxun.util.ImageCacheLoader;
import com.xiangxun.util.ImageCacheLoader.GetLocalCallBack;
import com.xiangxun.widget.DeletePictureInterface;
import com.xiangxun.widget.NoViewPager;
import com.xiangxun.widget.PublishLookAndDeletePicturePw;

/**
 * @package: com.xiangxun.adapter
 * @ClassName: PublishPictureAdapter.java
 * @Description: 图片展示适配器
 * @author: HanGJ
 * @date: 2015-8-14 上午10:19:10
 */
public class PublishPictureAdapter extends PagerAdapter {
	private int mChildCount = 0;
	private List<View> mViews = new ArrayList<View>();
	private NoViewPager mNoViewPager;
	private List<String> mPhotos = null;
	private PublishLookAndDeletePicturePw mPublishLookAndDeletePicturePw = null;
	private Context mContext;

	public PublishPictureAdapter(Context context, NoViewPager noViewPager, List<String> photos) {
		super();
		this.mNoViewPager = noViewPager;
		this.mPhotos = photos;
		this.mContext = context;
	}

	@Override
	public float getPageWidth(int position) {
		return (float) 0.33;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		mChildCount = getCount();
		mNoViewPager.setPagingEnabled(mChildCount > 3);
	}

	@Override
	public int getItemPosition(Object object) {
		if (mChildCount > 0) {
			mChildCount--;
			return POSITION_NONE;
		}
		return super.getItemPosition(object);
	}

	@Override
	public void destroyItem(View container, int item, Object object) {
		View view = (View) object;
		((ViewPager) container).removeView(view);
		mViews.add(view);
	}

	@Override
	public int getCount() {
		return mPhotos == null ? 0 : mPhotos.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public Object instantiateItem(final ViewGroup container, final int position) {
		View view = getView(mPhotos.get(position), mContext);
		container.addView(view);
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPublishLookAndDeletePicturePw = new PublishLookAndDeletePicturePw(mContext, mPhotos);
				mPublishLookAndDeletePicturePw.startFullScreenSlide(position);
				mPublishLookAndDeletePicturePw.setLocationListener((DeletePictureInterface) mContext);
			}
		});
		return view;
	}

	private View getView(String photoPath, Context con) {
		LayoutInflater inflater = LayoutInflater.from(con);
		LinearLayout linearLayout = new LinearLayout(mContext);
		View viewLocal = inflater.inflate(R.layout.publish_image, null);
		final RelativeLayout rlShowPictureLocal = (RelativeLayout) viewLocal.findViewById(R.id.rl_publish_picture_show_item_local);
		final LocalNetWorkView ivPicture = (LocalNetWorkView) viewLocal.findViewById(R.id.iv_publish);
		if (!new File(photoPath).exists()) {
			ivPicture.setImageResource(R.drawable.view_pager_default);
		} else {
			ImageCacheLoader.getInstance().getLocalImage(photoPath, ivPicture, new GetLocalCallBack() {
				@Override
				public void localSuccess(Object o) {
					LocalNetWorkView lv = (LocalNetWorkView) o;
					ivPicture.setImageBitmap(DcHttpClient.getInstance().mBitmapCache.getBitmap(lv.filePath));
				}

				@Override
				public void localFalse(Object o) {
					ivPicture.setImageResource(R.drawable.view_pager_default);
				}
			}, false);
		}
		ivPicture.setScaleType(ScaleType.CENTER_CROP);
		linearLayout.addView(rlShowPictureLocal);
		return linearLayout;
	}

}
