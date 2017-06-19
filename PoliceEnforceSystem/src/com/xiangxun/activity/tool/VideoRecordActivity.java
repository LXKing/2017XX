package com.xiangxun.activity.tool;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.xiangxun.activity.R;
import com.xiangxun.activity.mine.MyInformationActivity;
import com.xiangxun.activity.setting.SettingActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initView();
    }

    private void initView() {
        initFragments(RecordFragment.class, R.id.test_fragment);
        titleView = (TitleView) findViewById(R.id.tv_comm_title);
        titleView.setRightViewRightOneListener(R.drawable.user_normal, this);
        titleView.setLeftBackOneListener(R.drawable.tab_setting, this);
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
            case R.id.title_view_back_img:
                startActivity(new Intent(this, SettingActivity.class));
                break;
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