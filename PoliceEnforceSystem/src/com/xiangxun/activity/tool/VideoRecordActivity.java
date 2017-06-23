package com.xiangxun.activity.tool;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.xiangxun.activity.R;
import com.xiangxun.activity.mine.MyInformationActivity;
import com.xiangxun.video.common.ToastApp;
import com.xiangxun.video.ui.RecordFragment;
import com.xiangxun.widget.TitleView;

/**
 * Created by Zhangyuhui/Darly on 2017/6/19.
 * Copyright by [Zhangyuhui/Darly]
 * ©2017 XunXiang.Company. All rights reserved.
 *
 * @TODO: 测试视频录像类
 */
public class VideoRecordActivity extends Activity implements RecordFragment.OnResultBackListener, OnClickListener {

    private FragmentManager fragmentManager;

    private RecordFragment fragment;

    private TitleView titleView;

    private WebView wv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initView();
    }

    public void initView() {
        initFragments(RecordFragment.class, R.id.test_fragment);
        wv = (WebView) findViewById(R.id.test_webview);
        wv.setVisibility(View.GONE);
        WebSettings webSettings = wv.getSettings();
        // 设置可以访问文件
        webSettings.setAllowFileAccess(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        // 设置WebView属性，能够执行Javascript脚本
        // 广播没有加载注册完成引起崩溃
        // webSettings.setBuiltInZoomControls(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setUseWideViewPort(true);// 关键点
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAppCacheEnabled(true);

        webSettings.setAppCacheMaxSize(8 * 1024 * 1024); // 8MB
        // webSettings.setAppCachePath(Constants.WEBVIEW_CACHE_DIR );
        String appCacheDir = getApplicationContext()
                .getDir("cache", Context.MODE_PRIVATE).getPath();
        webSettings.setAppCachePath(appCacheDir);
        webSettings.setDomStorageEnabled(true);
        // 启用数据库
        webSettings.setDatabaseEnabled(true);
        // 设置定位的数据库路径
        String dir = getApplicationContext()
                .getDir("database", Context.MODE_PRIVATE).getPath();
        webSettings.setGeolocationDatabasePath(dir);
        // 启用地理定位
        webSettings.setGeolocationEnabled(true);

        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        // js调用安卓方法
        //wv.addJavascriptInterface(this, "HellenDown");

        wv.loadUrl("file:///android_asset/download.html");
        titleView = (TitleView) findViewById(R.id.tv_comm_title);
        titleView.setTitle("视频录像");
        titleView.setLeftBackOneListener(R.drawable.back_normal, new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                onBackPressed();
            }
        });
    }

    private void initFragments(Class<?> cls, int resId) {
        fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (fragment == null) {
            try {
                fragment = (RecordFragment) cls.newInstance();
                fragment.setOnResultBackListener(this);
                transaction.add(resId, fragment);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            if (fragment.isVisible())
                return;
            transaction.show(fragment);
        }
        transaction.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_view_operation_imageview_right:
                startActivity(new Intent(this, MyInformationActivity.class));
                break;
        }
    }

    @Override
    public void resultBack(String path) {
        ToastApp.showToast(path);
    }
}