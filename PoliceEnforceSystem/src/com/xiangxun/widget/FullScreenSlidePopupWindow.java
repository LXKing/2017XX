package com.xiangxun.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.xiangxun.activity.R;
import com.xiangxun.adapter.PhoneViewAdapter;
import com.xiangxun.widget.photoview.HackyViewPager;

import java.util.List;

/**
 * @package: com.xiangxun.widget
 * @ClassName: FullScreenSlidePopupWindow.java
 * @Description: 全屏图片
 * @author: HanGJ
 * @date: 2015-8-13 上午9:43:57
 */
public class FullScreenSlidePopupWindow implements OnPageChangeListener {
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
	private static FullScreenSlidePopupWindow slidePopupWindow;

	private FullScreenSlidePopupWindow(Context context, List<String> pictures) {
		this.mContext = context;
		this.mPictures = pictures;
	}
	
	public static FullScreenSlidePopupWindow getInstance(Context context, List<String> pictures) {
		if(slidePopupWindow == null){
			slidePopupWindow = new FullScreenSlidePopupWindow(context, pictures);
		}
		return slidePopupWindow;
	}

	@SuppressWarnings("static-access")
	private void initView() {
		mLayoutInflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
		mPopupWindowView = mLayoutInflater.inflate(R.layout.full_screen_slide_popup_window, null);
		mTitleView = (TitleView) mPopupWindowView.findViewById(R.id.tv_picture_detail_title);
		mViewPager = (HackyViewPager) mPopupWindowView.findViewById(R.id.vp_phone_view);
		mLlPlacePointLayout = (LinearLayout) mPopupWindowView.findViewById(R.id.ll_place_point);
	}

	private void setListener() {
		mTitleView.setLeftBackOneListener(R.drawable.back_normal, new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPopupWindow.dismiss();
			}
		});
		mViewPager.setOnPageChangeListener(this);
	}
	
	public void popupWindowDismiss(){
		slidePopupWindow = null;
		mPopupWindowView = null;
		if(mPopupWindow != null){
			mPopupWindow.dismiss();
			mPopupWindow = null;
		}
	}

	@SuppressWarnings("deprecation")
	private void setWindowAttribute() {
		mPopupWindow = new PopupWindow(mPopupWindowView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		mPopupWindow.setContentView(mPopupWindowView);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopupWindow.setAnimationStyle(R.style.AnimationFade);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setTouchable(true);
		mPopupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
		mPopupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
		mPopupWindow.showAtLocation(mPopupWindowView, Gravity.CENTER, 0, 0);
	}

	public void startFullScreenSlide(int position) {
		mCurrentPage = position;
		if (mPopupWindowView == null) {
			initView();
		}
		setListener();
		setPlacePoint();
		setViewData();
		if (mPopupWindow == null) {
			setWindowAttribute();
		} else {
			if(!mPopupWindow.isShowing()){
				mPopupWindow.showAtLocation(mPopupWindowView, Gravity.CENTER, 0, 0);
			}
		}
	}

	private void setPlacePoint() {
		mLlPlacePointLayout.removeAllViews();
		mLlPlacePointLayout.removeAllViewsInLayout();
		mPoints = new ImageView[mPictures.size()];
		for (int i = 0, size = mPictures.size(); i < size; i++) {
			mPoints[i] = new ImageView(mContext);
			mPoints[i].setImageResource(R.drawable.circle_white);
			mPoints[i].setLayoutParams(new LayoutParams(20, 20));
			mPoints[i].setPadding(5, 5, 5, 5);
			mLlPlacePointLayout.addView(mPoints[i]);
		}
		mPoints[0].setImageResource(R.drawable.circle_orange);

		if (mPictures.size() <= 1) {
			mLlPlacePointLayout.setVisibility(View.INVISIBLE);
		} else {
			mLlPlacePointLayout.setVisibility(View.VISIBLE);
		}
	}

	private void setViewData() {
		mTitleView.setTitle(R.string.picture_detail_title);
		mViewPager.setAdapter(new PhoneViewAdapter(mContext, mPictures));
		mViewPager.setCurrentItem(mCurrentPage);
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
		for (int i = 0, size = mPictures.size(); i < size; i++) {
			if (position == i) {
				mPoints[i].setImageResource(R.drawable.circle_orange);
			} else {
				mPoints[i].setImageResource(R.drawable.circle_white);
			}
		}
	}

}
