package com.xiangxun.activity.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiangxun.activity.R;
import com.xiangxun.app.BaseActivity;
import com.xiangxun.cfg.SystemCfg;
import com.xiangxun.widget.MsgToast;
import com.xiangxun.widget.SelectItemDialog;
import com.xiangxun.widget.SelectItemDialog.SelectResultItemClick;
import com.xiangxun.widget.TitleView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ResolutionFontSetActivity extends BaseActivity implements OnClickListener, SelectResultItemClick {
	private TitleView titleView;
	private Camera camera;
	private Camera.Parameters par;
	private SharedPreferences mysp = null;
	public static final String PREFERENCE_NAME = "xxsyscfg";
	
	private TextView textPicSize, textFontSize;
	private LinearLayout setPicSize, setFontSize;

	private SelectItemDialog picSizeDialog;
	private SelectItemDialog fontSizeDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_picsize);
		initView();
		initData();
		initListener();
	}

	@Override
	public void initView() {
		titleView = (TitleView) findViewById(R.id.tv_comm_title);
		titleView.setTitle(R.string.zpyzt);
		textPicSize = (TextView) findViewById(R.id.tv_pic_size);
		textFontSize = (TextView) findViewById(R.id.tv_font_size);
		setPicSize = (LinearLayout) findViewById(R.id.rl_pic_size);
		setFontSize = (LinearLayout) findViewById(R.id.rl_font_size);		
	}

	@Override
	public void initListener() {
		setPicSize.setOnClickListener(this);
		setFontSize.setOnClickListener(this);
		picSizeDialog.setSelectResultItemClick(this);
		fontSizeDialog.setSelectResultItemClick(this);
		titleView.setLeftBackOneListener(R.drawable.back_normal, new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});
	}

	@Override
	public void initData() {
		mysp = getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		camera = Camera.open();
		par = camera.getParameters();
		List<Camera.Size> piclist = par.getSupportedPictureSizes();
		if (!mysp.contains("viopicwidth")) {
			for (Size l : piclist) {
				if (l.height <= 2048 && l.width <= 1536) {
					if (l.height > 640 && l.width > 480){
						SystemCfg.setVioPicHight(ResolutionFontSetActivity.this,l.height);
						SystemCfg.setVioPicWidth(ResolutionFontSetActivity.this,l.width);
					}
					break;
				}
			}
		}
		
		final List<String> strs = new ArrayList<String>();
		Iterator<Camera.Size> itor = piclist.iterator();
		while (itor.hasNext()) {
			Camera.Size cur = itor.next();
			if (cur.width <= 2048 && cur.height <= 1536) {
				if (cur.height > 640 && cur.width > 480){
					strs.add(cur.width + "*" + cur.height);
				}
			}
		}
		int l = strs.size();
		String[] str = new String[l];
		for (int c = 0; c < l; c++) {
			str[c] = strs.get(c).toString();
		}

		camera.release();
		picSizeDialog = new SelectItemDialog(this, str, getResources().getString(R.string.picSize));
		String textsizes[] = { "16", "24", "32" };
		fontSizeDialog = new SelectItemDialog(this, textsizes, getResources().getString(R.string.txtSize));
		
		textPicSize.setText(SystemCfg.getVioPicWidth(this) + "*" + SystemCfg.getVioPicHight(this));
		textFontSize.setText(String.valueOf(SystemCfg.getTextSize(this)));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_pic_size:
			picSizeDialog.show();
			break;
		case R.id.rl_font_size:
			fontSizeDialog.show();
			break;
		}
	}

	@Override
	public void resultOnClick(String result) {
		if (result.contains("*")) {
			int index = result.indexOf("*");
			String pw = result.substring(0, index);
			String ph = result.substring(index+1);

			textPicSize.setText(result);
			
			SystemCfg.setVioPicWidth(ResolutionFontSetActivity.this, Integer.valueOf(pw));
			SystemCfg.setVioPicHight(ResolutionFontSetActivity.this, Integer.valueOf(ph));
			SystemCfg.setDutyPicWidth(ResolutionFontSetActivity.this, Integer.valueOf(pw));
			SystemCfg.setDutyPicHight(ResolutionFontSetActivity.this, Integer.valueOf(ph));
		} else {
			SystemCfg.setTextSize(ResolutionFontSetActivity.this, Integer.parseInt(result));
			textFontSize.setText(result);
		}
		MsgToast.geToast().setMsg(R.string.settingsuccess);			
	}
}
