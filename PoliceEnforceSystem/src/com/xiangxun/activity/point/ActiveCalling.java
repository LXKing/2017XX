package com.xiangxun.activity.point;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.xiangxun.activity.R;
import com.xiangxun.app.BaseActivity;

import org.linphone.LinphoneManager;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneProxyConfig;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Zhangyuhui/Darly on 2017/6/15.
 * Copyright by [Zhangyuhui/Darly]
 * ©2017 XunXiang.Company. All rights reserved.
 *
 * @TODO:
 */
public class ActiveCalling extends BaseActivity {

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String num = getIntent().getStringExtra("mark");
            String name = getIntent().getStringExtra("name");
            goCallOne(num, name);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = new View(this);
        v.setBackgroundColor(getResources().getColor(R.color.transparent));
        setContentView(v);
        initView();
        initData();
        initListener();
        handler.sendEmptyMessageDelayed(0, 1000);
    }

    @Override
    public void initView() {

    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {

    }

    public void goCallOne(String num, String name) {
        LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
        if (lc != null) {
            LinphoneProxyConfig lpc = lc.getDefaultProxyConfig();
            if (lpc != null) {
                String to = lpc.normalizePhoneNumber(num);
                LinphoneManager.getInstance().newOutgoingCall(to, name);
            } else {
                LinphoneManager.getInstance().newOutgoingCall(num, name);
            }
        }
        onBackPressed();
    }
}
