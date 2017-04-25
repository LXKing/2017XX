package com.xiangxun.activity.setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.xiangxun.activity.LoginActivity;
import com.xiangxun.activity.R;
import com.xiangxun.cfg.SystemCfg;
import com.xiangxun.util.Utils;
import com.xiangxun.widget.SelectItemDialog;
import com.xiangxun.widget.SelectItemDialog.SelectResultItemClick;
import com.xiangxun.widget.TitleView;
import com.xiangxun.widget.ViewEditTextEx;

public class SetGuide  extends Activity implements OnClickListener, SelectResultItemClick {
	private TitleView titleView;
	private TextView stepLab,stepNote;
	private ViewFlipper viewPage;
	private ViewEditTextEx server;
	
	private LinearLayout wordRoad;
	private TextView wordRoadText;
	private SelectItemDialog workRoadDialog;

	private Button btnPre, btnNext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_guide);
		initView();
		initData();
		initListener();		
	}

	public void initView() {
		// TODO Auto-generated method stub
		titleView = (TitleView) findViewById(R.id.tv_comm_title);
		titleView.setTitle("设置向导");
		titleView.setLeftBackOneListener(R.drawable.tab_setting);

		stepLab = (TextView) findViewById(R.id.step_lab);
		stepNote = (TextView) findViewById(R.id.step_note);
		
		viewPage = (ViewFlipper) findViewById(R.id.viewFlipper);

		server = (ViewEditTextEx) findViewById(R.id.edt_server);
		server.setTextViewText(getResources().getString(R.string.serveraddress));
		server.setEditTextOneHint("请输入服务器IP");
		server.setEditTextTwoHint("请输入端口");
		server.setEditTextOneText(SystemCfg.getServerIP(this));
		server.setEditTextTwoText(SystemCfg.getServerPort(this));
		server.setEditTextTwoInputMode(InputType.TYPE_CLASS_NUMBER);
		
		wordRoad = (LinearLayout) findViewById(R.id.workroad);
		wordRoadText = (TextView) findViewById(R.id.workroad_text);
		wordRoadText.setText(SystemCfg.getWorkRoad(this));
		
		String workRoads[] = { getResources().getString(R.string.workroad_city), getResources().getString(R.string.workroad_speed)};
		workRoadDialog = new SelectItemDialog(this, workRoads, getResources().getString(R.string.workroad));

		btnPre = (Button) findViewById(R.id.pre);
		btnNext = (Button) findViewById(R.id.next);
		
//		viewPage.setDisplayedChild(0);
	}

	public void initListener() {
		// TODO Auto-generated method stub
		btnPre.setOnClickListener(this);
		btnNext.setOnClickListener(this);
		
		wordRoad.setOnClickListener(this);
		workRoadDialog.setSelectResultItemClick(this);
	}

	public void initData() {
		// TODO Auto-generated method stub
		stepNote.setText(R.string.step_servernote);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.pre:
			btnNext.setText(R.string.btn_next);
			btnPre.setVisibility(View.INVISIBLE);
			stepLab.setText(R.string.serveraddress);
			stepNote.setText(R.string.step_servernote);
			viewPage.setDisplayedChild(0);
			break;
		case R.id.next:
			String text = btnNext.getText().toString();
			if(getResources().getString(R.string.btn_end).equalsIgnoreCase(text)) {
				//save workroad
				SystemCfg.setWorkRoad(this, wordRoadText.getText().toString());
				
				Intent intent = new Intent();
				intent.setClass(this, LoginActivity.class);
				startActivity(intent);

				SystemCfg.setIsFirstLogion(this,false);
				SetGuide.this.finish();
			} else {
				Utils.hideSoftInputFromWindow(this);
				//save server address
				SystemCfg.setServerIP(this, server.getEditTextOneText().toString());
				SystemCfg.setServerPort(this, server.getEditTextTwoText().toString());
				
				btnNext.setText(R.string.btn_end);
				btnPre.setVisibility(View.VISIBLE);
				stepLab.setText(R.string.workroad);
				stepNote.setText(R.string.step_roadnote);
				viewPage.setDisplayedChild(1);
			}
			break;
		case R.id.workroad:
			workRoadDialog.show();
			break;
		default:
			break;
		}
	}

	@Override
	public void resultOnClick(String result) {
		// TODO Auto-generated method stub
		wordRoadText.setText(result);
	}
}
