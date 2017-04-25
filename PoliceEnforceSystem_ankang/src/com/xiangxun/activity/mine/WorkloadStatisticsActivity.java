package com.xiangxun.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiangxun.activity.R;
import com.xiangxun.app.BaseActivity;
import com.xiangxun.db.DBManager;
import com.xiangxun.widget.TitleView;

public class WorkloadStatisticsActivity extends BaseActivity implements OnClickListener {
	private TextView viocount, enforcecount, accidentcount, libcount, dutycount, zhifacount, warnackcount;
	private RelativeLayout viodata, enforcedata, accidentdata, libdata, dutydata, zhifadata, warnackdata;
	private TitleView titleView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.work);
		initView();
		initData();
		initListener();
	}
	
	@Override
	public void initView() {
		titleView = (TitleView) findViewById(R.id.tv_comm_title);
		titleView.setTitle(R.string.gzltj);
		viocount = (TextView) findViewById(R.id.viocount);
		enforcecount = (TextView) findViewById(R.id.enforcecount);
		dutycount = (TextView) findViewById(R.id.dutycount);
		libcount = (TextView) findViewById(R.id.libcount);
		accidentcount = (TextView) findViewById(R.id.accidentcount);
		zhifacount = (TextView) findViewById(R.id.zhifacount);
		viodata = (RelativeLayout) findViewById(R.id.viodata);
		enforcedata = (RelativeLayout) findViewById(R.id.enforcedata);
		accidentdata = (RelativeLayout) findViewById(R.id.accidentdata);
		libdata = (RelativeLayout) findViewById(R.id.libdata);
		dutydata = (RelativeLayout) findViewById(R.id.dutydata);
		zhifadata = (RelativeLayout) findViewById(R.id.zhifajianchadata);

		warnackcount = (TextView) findViewById(R.id.warnackcount);
		warnackdata = (RelativeLayout) findViewById(R.id.warnackdata);
	}

	@Override
	public void initListener() {
		viodata.setOnClickListener(this);
		enforcedata.setOnClickListener(this);
		accidentdata.setOnClickListener(this);
		libdata.setOnClickListener(this);
		dutydata.setOnClickListener(this);
		zhifadata.setOnClickListener(this);
		warnackdata.setOnClickListener(this);
		titleView.setLeftBackOneListener(R.drawable.back_normal, new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});
	}

	@Override
	public void initData() {
		viocount.setText(DBManager.getInstance().QueryVioDataCount() + "");
		enforcecount.setText(DBManager.getInstance().QueryEnforceDataCount() + "");
		dutycount.setText(DBManager.getInstance().QueryDutyDataCount() + "");
		libcount.setText(DBManager.getInstance().QueryFieldpunishDataCount() + "");
		accidentcount.setText(DBManager.getInstance().QueryAccidentDataCount() + "");
		zhifacount.setText(DBManager.getInstance().QueryZhifaDataCount() + "");
		warnackcount.setText(DBManager.getInstance().QueryWarnAckDataCount() + "");
	}
	
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.viodata:
			Intent vio = new Intent(WorkloadStatisticsActivity.this, IllegalCollectionActivity.class);
			startActivity(vio);
			break;
		case R.id.enforcedata:
			Intent enforce = new Intent(WorkloadStatisticsActivity.this, EnforcementMeasureListActivity.class);
			startActivity(enforce);
			break;
		case R.id.accidentdata:
			Intent accident = new Intent(WorkloadStatisticsActivity.this, RegistrationAccidentListActivity.class);
			startActivity(accident);
			break;
		case R.id.libdata:
			Intent lib = new Intent(WorkloadStatisticsActivity.this, SimplePunishNoticeActivity.class);
			startActivity(lib);
			break;
		case R.id.dutydata:
			Intent duty = new Intent(WorkloadStatisticsActivity.this, DailyServiceListActivity.class);
			startActivity(duty);
			break;
		case R.id.zhifajianchadata:
			Intent zhifa = new Intent(WorkloadStatisticsActivity.this, LawEnforcementListActivity.class);
			startActivity(zhifa);
			break;
		case R.id.warnackdata:
			Intent warnack = new Intent(WorkloadStatisticsActivity.this, WarnAckListActivity.class);
			startActivity(warnack);
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onStart() {
		initData();
		super.onStart();
	}

}
