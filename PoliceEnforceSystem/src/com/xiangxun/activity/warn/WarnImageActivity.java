package com.xiangxun.activity.warn;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xiangxun.app.BaseActivity;
import com.xiangxun.common.DcHttpClient;
import com.xiangxun.activity.R;
import com.xiangxun.widget.photoview.PhotoView;

public class WarnImageActivity extends BaseActivity {
	private PhotoView planImage;
	private WebView planWeb;
	private String msgURL;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.warn_image_layout);
		initView();
		initData();
		initListener();		
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		msgURL = intent.getStringExtra("msgURL");
		if (msgURL.endsWith("/")) {
			planWeb = (WebView) findViewById(R.id.plan_web);
			planWeb.loadUrl(msgURL);
			planWeb.setWebViewClient(new WebViewClient());
			WebSettings webSet = planWeb.getSettings();
			webSet.setLoadWithOverviewMode(true);
			webSet.setLoadsImagesAutomatically(true);
//			webSet.setSupportZoom(true);
			planWeb.setVisibility(View.VISIBLE);
		} else if (msgURL.endsWith(".png") || msgURL.endsWith(".gif") || msgURL.endsWith(".jpg")) {
			planImage = (PhotoView) findViewById(R.id.plan_image);
			DcHttpClient.getInstance().requestImage(planImage, msgURL, R.drawable.empty_photo);
			planImage.setZoomable(true);
			planImage.setBackgroundColor(Color.BLACK);
			planImage.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void initListener() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		
	}

}
