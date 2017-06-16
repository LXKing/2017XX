package com.xiangxun.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.xiangxun.activity.R;
import com.xiangxun.widget.photoview.HackyViewPager;

import java.util.List;

/**
 * @package: com.xiangxun.widget
 * @ClassName: PublishLookAndDeletePicturePw.java
 * @Description: 全屏图片
 * @author: HanGJ
 * @date: 2015-8-14 上午10:02:30
 */
public class PublishLookAndDeletePicturePw implements OnClickListener, OnPageChangeListener {
	private Context mContext = null;
	private PopupWindow mPopupWindow = null;
	private View mPopupWindowView = null;
	private LayoutInflater mLayoutInflater = null;

	private TitleView mTitleView = null;
	private ViewPager mViewPager = null;
	private LinearLayout mLlPlacePointLayout = null;

	private List<String> mPictures = null;
	private ImageView[] mPoints = null;

	private int mCurrentPage = 0;
	private PhoneViewDeleteAdapter phoneViewDeleteAdapter = null;
	public DeletePictureInterface mDeletePictureInterface;
	private View mContentView;

	public void setLocationListener(DeletePictureInterface deletePictureInterface) {
		this.mDeletePictureInterface = deletePictureInterface;
	}

	public PublishLookAndDeletePicturePw(Context context, List<String> pictures) {
		this.mContext = context;
		this.mPictures = pictures;
	}

	@SuppressWarnings("static-access")
	private void initView() {
		mLayoutInflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
		mPopupWindowView = mLayoutInflater.inflate(R.layout.full_screen_slide_delete_popup_window, null);
		mTitleView = (TitleView) mPopupWindowView.findViewById(R.id.tv_picture_detail_title);
		mContentView = mPopupWindowView.findViewById(R.id.ll_popu_contnet);
		mTitleView.setRightViewRightOneListener(R.drawable.publish_delete, this);
		mTitleView.setLeftBackOneListener(R.drawable.back_normal, this);
		mViewPager = (HackyViewPager) mPopupWindowView.findViewById(R.id.vp_phone_view);
		mLlPlacePointLayout = (LinearLayout) mPopupWindowView.findViewById(R.id.ll_place_point);
	}

	private void setListener() {
		mTitleView.setOnClickListener(this);
		mViewPager.setOnPageChangeListener(this);
		mContentView.setFocusable(true);
		mContentView.setFocusableInTouchMode(true);
		mContentView.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					mDeletePictureInterface.getPictures(mPictures);
					mPopupWindow.dismiss();
				}
				return false;
			}
		});
	}

	private void setWindowAttribute() {
		mPopupWindow = new PopupWindow(mPopupWindowView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		mPopupWindow.setContentView(mPopupWindowView);
		// 这个即为默认的返回键处理
		// mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopupWindow.setAnimationStyle(R.style.AnimationFade);
		mPopupWindow.setFocusable(true);
		// mPopupWindow.setOutsideTouchable(true);
		// mPopupWindow.setTouchable(true);
		mPopupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
		mPopupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
		mPopupWindow.showAtLocation(mPopupWindowView, Gravity.CENTER, 0, 0);
	}

	public void startFullScreenSlide(int position) {
		mCurrentPage = position;
		initView();
		setPlacePoint();
		setViewData();
		setWindowAttribute();
		setListener();
	}

	private void setPlacePoint() {
		mPoints = new ImageView[mPictures.size()];
		for (int i = 0, size = mPictures.size(); i < size; i++) {
			mPoints[i] = new ImageView(mContext);
			mPoints[i].setImageResource(R.drawable.circle_white);
			mPoints[i].setLayoutParams(new LayoutParams(20, 20));
			mPoints[i].setPadding(5, 5, 5, 5);
			mLlPlacePointLayout.addView(mPoints[i]);
		}
		mPoints[mCurrentPage].setImageResource(R.drawable.circle_orange);

		if (mPictures.size() <= 1) {
			mLlPlacePointLayout.setVisibility(View.INVISIBLE);
		} else {
			mLlPlacePointLayout.setVisibility(View.VISIBLE);
		}
	}

	private void setViewData() {
		mTitleView.setTitle(R.string.picture_detail_title);
		phoneViewDeleteAdapter = new PhoneViewDeleteAdapter(mContext, mPictures);
		mViewPager.setAdapter(phoneViewDeleteAdapter);
		mViewPager.setCurrentItem(mCurrentPage);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.title_view_back_img:
			mDeletePictureInterface.getPictures(mPictures);
			mPopupWindow.dismiss();
			break;
		case R.id.title_view_operation_imageview_right:
			mPictures.remove(mCurrentPage);
			if (mPictures.size() == 0) {
				mDeletePictureInterface.getPictures(mPictures);
				mPopupWindow.dismiss();
			} else {
				mLlPlacePointLayout.removeAllViews();
				mPoints = new ImageView[mPictures.size()];
				for (int i = 0, size = mPictures.size(); i < size; i++) {
					mPoints[i] = new ImageView(mContext);
					mPoints[i].setImageResource(R.drawable.circle_white);
					mPoints[i].setLayoutParams(new LayoutParams(20, 20));
					mPoints[i].setPadding(5, 5, 5, 5);
					mLlPlacePointLayout.addView(mPoints[i]);
				}

				phoneViewDeleteAdapter = new PhoneViewDeleteAdapter(mContext, mPictures);
				mCurrentPage = 0;
				mViewPager.setAdapter(phoneViewDeleteAdapter);
				mViewPager.setCurrentItem(mCurrentPage);
				mPoints[mCurrentPage].setImageResource(R.drawable.circle_orange);

				if (mPictures.size() <= 1) {
					mLlPlacePointLayout.setVisibility(View.INVISIBLE);
				} else {
					mLlPlacePointLayout.setVisibility(View.VISIBLE);
				}
			}
			break;
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		// ToDo
	}

	@Override
	public void onPageScrolled(int position, float percent, int pixel) {
		// ToDo
	}

	@Override
	public void onPageSelected(int position) {
		mCurrentPage = position;
		for (int i = 0, size = mPictures.size(); i < size; i++) {
			if (position == i) {
				mPoints[i].setImageResource(R.drawable.circle_orange);
			} else {
				mPoints[i].setImageResource(R.drawable.circle_white);
			}
		}
	}

}