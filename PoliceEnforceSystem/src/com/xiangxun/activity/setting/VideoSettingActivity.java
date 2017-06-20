package com.xiangxun.activity.setting;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.xiangxun.activity.R;
import com.xiangxun.app.BaseFragmentActivity;
import com.xiangxun.widget.TitleView;

import org.linphone.AboutFragment;
import org.linphone.AccountPreferencesFragment;
import org.linphone.FragmentsAvailable;
import org.linphone.SettingsFragment;

/**
 * 网络电话设置
 *
 * @package: com.xiangxun.activity.setting
 * @ClassName: VideoSettingActivity.java
 * @Description:
 * @author: HanGJ
 * @date: 2015-09-27 下午3:23:34
 */
public class VideoSettingActivity extends BaseFragmentActivity {
    private TitleView titleView;
    private static VideoSettingActivity instance;
    private FragmentsAvailable currentFragment;

    public static final boolean isInstanciated() {
        return instance != null;
    }

    public static final VideoSettingActivity instance() {
        if (instance != null)
            return instance;
        throw new RuntimeException("VideoSettingActivity not instantiated yet");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_setting_layout);
        instance = this;
        initView();
        initData();
        initListener();
    }

    @Override
    public void initView() {
        titleView = (TitleView) findViewById(R.id.tv_comm_title);
    }

    @Override
    public void initListener() {
        titleView.setLeftBackOneListener(R.drawable.back_normal, new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
    }

    public void displayAbout() {
        currentFragment = FragmentsAvailable.ABOUT;
        changeCurrentFragment(FragmentsAvailable.ABOUT, null);
    }

    public void displayAccountSettings(int accountNumber) {
        Bundle bundle = new Bundle();
        bundle.putInt("Account", accountNumber);
        currentFragment = FragmentsAvailable.ACCOUNT_SETTINGS;
        changeCurrentFragment(FragmentsAvailable.ACCOUNT_SETTINGS, bundle);
    }

    public void displaySettings() {
        currentFragment = FragmentsAvailable.SETTINGS;
        changeCurrentFragment(FragmentsAvailable.SETTINGS, null);
    }

    @Override
    public void initData() {
        titleView.setTitle("网络电话参数设置");
        Bundle b = new Bundle();
        b.putSerializable("About", FragmentsAvailable.ABOUT_INSTEAD_OF_SETTINGS);
        currentFragment = FragmentsAvailable.SETTINGS;
        changeCurrentFragment(FragmentsAvailable.SETTINGS, b);
    }

    private void changeCurrentFragment(FragmentsAvailable newFragmentType, Bundle extras) {
        changeCurrentFragment(newFragmentType, extras, false);
    }

    @SuppressWarnings("incomplete-switch")
    private void changeCurrentFragment(FragmentsAvailable newFragmentType, Bundle extras, boolean withoutAnimation) {
        Fragment newFragment = null;
        switch (newFragmentType) {
            case SETTINGS:
                newFragment = new SettingsFragment();
                break;
            case ACCOUNT_SETTINGS:
                newFragment = new AccountPreferencesFragment();
                break;
            case ABOUT:
            case ABOUT_INSTEAD_OF_CHAT:
            case ABOUT_INSTEAD_OF_SETTINGS:
                newFragment = new AboutFragment();
                break;
        }
        if (newFragment != null) {
            newFragment.setArguments(extras);
            if (isTablet()) {
                changeFragmentForTablets(newFragment, newFragmentType, withoutAnimation);
            } else {
                changeFragment(newFragment, newFragmentType, withoutAnimation);
            }
        }
    }

    private boolean isTablet() {
        return getResources().getBoolean(R.bool.isTablet);
    }

    private void changeFragment(Fragment newFragment, FragmentsAvailable newFragmentType, boolean withoutAnimation) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        try {
            getSupportFragmentManager().popBackStackImmediate(newFragmentType.toString(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } catch (java.lang.IllegalStateException e) {

        }
        transaction.addToBackStack(newFragmentType.toString());
        transaction.replace(R.id.fragmentContainer, newFragment, newFragmentType.toString());
        transaction.commitAllowingStateLoss();
        getSupportFragmentManager().executePendingTransactions();
    }

    private void changeFragmentForTablets(Fragment newFragment, FragmentsAvailable newFragmentType, boolean withoutAnimation) {
        LinearLayout ll = (LinearLayout) findViewById(R.id.fragmentContainer);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (newFragmentType == FragmentsAvailable.DIALER || newFragmentType == FragmentsAvailable.ABOUT || newFragmentType == FragmentsAvailable.ABOUT_INSTEAD_OF_CHAT || newFragmentType == FragmentsAvailable.ABOUT_INSTEAD_OF_SETTINGS || newFragmentType == FragmentsAvailable.SETTINGS || newFragmentType == FragmentsAvailable.ACCOUNT_SETTINGS) {
            ll.setVisibility(View.GONE);
        } else {
            ll.setVisibility(View.INVISIBLE);
        }

        try {
            getSupportFragmentManager().popBackStackImmediate(newFragmentType.toString(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } catch (java.lang.IllegalStateException e) {

        }

        transaction.addToBackStack(newFragmentType.toString());
        transaction.replace(R.id.fragmentContainer, newFragment);
        transaction.commitAllowingStateLoss();
        getSupportFragmentManager().executePendingTransactions();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (currentFragment == FragmentsAvailable.SETTINGS) {
                finish();
            } else {
                currentFragment = FragmentsAvailable.SETTINGS;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
