package com.xiangxun.activity.home;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.xiangxun.activity.Daily.PublishDailyAffairsActivity;
import com.xiangxun.activity.R;
import com.xiangxun.activity.mine.MyInformationActivity;
import com.xiangxun.activity.point.PointVideoActivity;
import com.xiangxun.activity.setting.SettingActivity;
import com.xiangxun.app.BaseActivity;
import com.xiangxun.app.XiangXunApplication;
import com.xiangxun.bean.LoginInfo;
import com.xiangxun.bean.VioDic;
import com.xiangxun.bean.WeijinInfo;
import com.xiangxun.cfg.SystemCfg;
import com.xiangxun.db.DBManager;
import com.xiangxun.request.DcNetWorkUtils;
import com.xiangxun.service.GpsService;
import com.xiangxun.service.MainService;
import com.xiangxun.service.NetworkStateService;
import com.xiangxun.service.NoticeOnlineService;
import com.xiangxun.util.ConstantStatus;
import com.xiangxun.util.JsonUtil;
import com.xiangxun.util.NetUtils;
import com.xiangxun.widget.MsgToast;
import com.xiangxun.widget.TitleView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint("HandlerLeak")
public class HomeActivity extends BaseActivity implements OnClickListener {
    private TitleView titleView;
    private boolean isShutDown;
    private ImageView driver, car;
    private Intent heart;
    private Button wfxwtz, jycxcf, xzqzcs, wfcj, sgdj, rcqw;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ConstantStatus.VIEWOLDPIC:

                    break;
                case ConstantStatus.NOPIC:

                    break;

                case 25:
                    isShutDown = false;
                    break;

                case ConstantStatus.NOLAWKEY:
                    MsgToast.geToast().setMsg("连接失败");
                    break;

                case ConstantStatus.DISMISS_POPUPMENU:

                    break;
                case ConstantStatus.getVioDicData:
                    final Object infoObj = (Object) msg.obj;
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            doVioDicData(infoObj);
                        }
                    }).start();
                    break;
                case ConstantStatus.getArticleInfo:
                    final Object lobj = (Object) msg.obj;
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            doArticleInfo(lobj);
                        }
                    }).start();
                    break;
            }
        }
    };

    private void doVioDicData(Object infoObj) {
        try {
            JSONArray array = new JSONArray(infoObj.toString());
            DBManager db = DBManager.getInstance();
            if (array != null) {
                db.clearVioDicData();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject singlejson = array.getJSONObject(i);
                    VioDic tempVio = new VioDic();
                    tempVio = (VioDic) JsonUtil.toBean(singlejson.toString(), VioDic.class);
                    db.add(tempVio);
                }
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
            MsgToast.geToast().setMsg("更新违法词典数据异常");
        }
    }

    private void doArticleInfo(Object lobj) {
        try {
            JSONArray array = new JSONArray(lobj.toString());
            DBManager db = DBManager.getInstance();
            if (array != null) {
                db.clearWeijinData();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject singlejson = array.getJSONObject(i);
                    if (singlejson != null) {
                        WeijinInfo wi = new WeijinInfo();
                        wi = (WeijinInfo) JsonUtil.toBean(singlejson.toString(), WeijinInfo.class);
                        db.add(wi);
                    }
                }
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
            MsgToast.geToast().setMsg("更新违禁物品数据异常");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher_above);
        initView();
        initData();
        initListener();
    }

    @Override
    public void initView() {
        titleView = (TitleView) findViewById(R.id.tv_comm_title);
        titleView.setLeftBackOneListener(R.drawable.tab_setting, this);
        titleView.setRightViewRightOneListener(R.drawable.user_normal, this);
        titleView.setTitle(R.string.app_name);
        driver = (ImageView) findViewById(R.id.driverSearch);
        car = (ImageView) findViewById(R.id.carSearch);
        wfxwtz = (Button) findViewById(R.id.wfxwtz_r);
        jycxcf = (Button) findViewById(R.id.jycxcf_r);
        xzqzcs = (Button) findViewById(R.id.xzqzcs_r);
        wfcj = (Button) findViewById(R.id.wfcj_r);
        sgdj = (Button) findViewById(R.id.sgdj_r);
        rcqw = (Button) findViewById(R.id.rcqw_r);
    }

    @Override
    public void initData() {
        // 启动GPS服务
        Intent gpsIntent = new Intent(this, GpsService.class);
        startService(gpsIntent);
        // 启动网络监听服务
        Intent net = new Intent(this, NetworkStateService.class);
        startService(net);

        heart = new Intent(this, NoticeOnlineService.class);
        startService(heart);
        try {
            new MainService().start(ConstantStatus.UPLOAD_ALLDATA);
            startService(new Intent(this, MainService.class));
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        // 开启推送服务
        XiangXunApplication.getInstance().startPushService();
        if (XiangXunApplication.getInstance().getUserName() != null && !XiangXunApplication.getInstance().getUserName().equals("")) {
            LoginInfo.isLogined = true;
        }
        if (!LoginInfo.isOffLine) {
            if (NetUtils.isNetworkAvailable(this)) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        DcNetWorkUtils.getVipCarList(HomeActivity.this, DBManager.getInstance());
                        DcNetWorkUtils.getBlackList(HomeActivity.this, DBManager.getInstance());
                        getBlackVipCar();
                    }
                }).start();
                MsgToast.geToast().setMsg(R.string.update_success);
            }
        }
    }

    @Override
    public void initListener() {
        wfxwtz.setOnClickListener(this);
        jycxcf.setOnClickListener(this);
        xzqzcs.setOnClickListener(this);
        wfcj.setOnClickListener(this);
        sgdj.setOnClickListener(this);
        rcqw.setOnClickListener(this);
        driver.setOnClickListener(this);
        car.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        stopService(heart);
        LoginInfo.isLogined = false;
        LoginInfo.isOffLine = true;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!isShutDown) {
            MsgToast.geToast().setMsg(R.string.clickAgainExit);
            isShutDown = true;
            mHandler.sendEmptyMessageDelayed(25, 2000);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
        switch (view.getId()) {
            case R.id.wfxwtz_r:// 违法行为通知

                if (ba.isEnabled()) {
                    startActivity(new Intent(HomeActivity.this, PublishViolationsNoticeActivity.class));
                } else {
                    MsgToast.geToast().setMsg("请打开蓝牙");
                    startActivityForResult(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS), 10);
                }

                break;
            case R.id.jycxcf_r:// 简易程序处罚
                if (ba.isEnabled()) {
                    startActivity(new Intent(HomeActivity.this, PublishSummaryPunishActivity.class));
                } else {
                    MsgToast.geToast().setMsg("请打开蓝牙");
                    startActivityForResult(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS), 10);
                }
                break;
            case R.id.xzqzcs_r:// 行政强制措施
                if (ba.isEnabled()) {
                    startActivity(new Intent(HomeActivity.this, PublishEnforceMeasureActivity.class));
                } else {
                    MsgToast.geToast().setMsg("请打开蓝牙");
                    startActivityForResult(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS), 10);
                }
                break;
            case R.id.wfcj_r:// 违停抓拍
                if (ba.isEnabled()) {
                    startActivity(new Intent(HomeActivity.this, PublishViolationsParkingActivity.class));
                } else {
                    MsgToast.geToast().setMsg("请打开蓝牙");
                    startActivityForResult(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS), 10);
                }
                break;
            case R.id.sgdj_r:// 事故登记
                startActivity(new Intent(HomeActivity.this, PublishRegistrationAccidentActivity.class));
                break;
            case R.id.rcqw_r:// 日常事务
                startActivity(new Intent(HomeActivity.this, PublishDailyAffairsActivity.class));
                break;
            case R.id.title_view_back_img:
                startActivity(new Intent(this, SettingActivity.class));
                break;
            case R.id.title_view_operation_imageview_right:
                startActivity(new Intent(HomeActivity.this, MyInformationActivity.class));
                break;
            case R.id.driverSearch:
                startActivity(new Intent(HomeActivity.this, SearchDriverInfoActivity.class));
                break;
            case R.id.carSearch:
                startActivity(new Intent(HomeActivity.this, SearchVehicleInfoActivity.class));
                break;
        }
    }

    private void getBlackVipCar() {
        // 按需要更新勤务类型、检查类型、事故类型、天气类型数据
        DcNetWorkUtils.getAndSaveTypeData(this, DBManager.getInstance());
        // 按需要更新道路数据
        DcNetWorkUtils.getRoadNamesData(this, DBManager.getInstance());
        // 更新违法类型数据
        DcNetWorkUtils.getVioTypesData(this, DBManager.getInstance());
        // 更新简易程序处罚编号数据
        DcNetWorkUtils.updatePunishId(this, SystemCfg.getUserId(this), "punish", DBManager.getInstance(), mHandler);
        // 更新行政强制措施编号数据
        DcNetWorkUtils.updateForceId(this, SystemCfg.getUserId(this), "force", DBManager.getInstance(), mHandler);
        // 更新违停告知书编号数据
        DcNetWorkUtils.updatePunishId(this, SystemCfg.getUserId(this), "park", DBManager.getInstance(), mHandler);
        // 更新违法处理通知编号数据
        DcNetWorkUtils.updateForceId(this, SystemCfg.getUserId(this), "dispose", DBManager.getInstance(), mHandler);
        // 更新通讯录数据
        DcNetWorkUtils.getAddressBook(this, DBManager.getInstance());
        // 更新违法词典数据
        DcNetWorkUtils.getVioDicData(this, DBManager.getInstance(), mHandler);
        // 更新违禁物品数据
        DcNetWorkUtils.getArticleInfo(this, DBManager.getInstance(), mHandler);
    }
}
