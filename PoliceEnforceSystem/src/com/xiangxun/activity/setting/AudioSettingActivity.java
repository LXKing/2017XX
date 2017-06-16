package com.xiangxun.activity.setting;

import org.linphone.LinphoneManager;
import org.linphone.LinphonePreferences;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCore.AdaptiveRateAlgorithm;
import org.linphone.core.LinphoneCore.EcCalibratorStatus;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneCoreListener.LinphoneEchoCalibrationListener;
import org.linphone.core.PayloadType;
import org.linphone.mediastream.Log;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiangxun.activity.R;
import com.xiangxun.app.BaseActivity;
import com.xiangxun.widget.PublishSelectLawDialog;
import com.xiangxun.widget.PublishSelectLawDialog.SelectLawDateItemClick;
import com.xiangxun.widget.TitleView;

public class AudioSettingActivity extends BaseActivity implements LinphoneEchoCalibrationListener, OnClickListener, SelectLawDateItemClick {
	private TitleView titleView;
	private LinphonePreferences mPrefs;
	private RelativeLayout mRlSilencer;
	private CheckBox mSilencer;
	private LinearLayout mSilencerCalibrate;
	private TextView mCalibrate;
	private RelativeLayout rlSpeedControl;
	private CheckBox mSpeedControl;
	private LinearLayout mSpeedAlgorithm;
	private TextView mTvSpeedAlgorithm;
	private PublishSelectLawDialog selectLawDialog;
	private String[] algorithms;
	private LinearLayout mBytesSpeed;
	private TextView mTvBytesSpeed;
	private PublishSelectLawDialog selectBytesDialog;
	private String[] byteSpeeds;
	private boolean isFlag = false;
	private LinearLayout addItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.audio_setting_layout);
		initView();
		initData();
		initListener();
	}

	@Override
	public void initView() {
		titleView = (TitleView) findViewById(R.id.comment_title);
		mRlSilencer = (RelativeLayout) findViewById(R.id.rl_silencer);
		mSilencer = (CheckBox) findViewById(R.id.silencer_check);
		mSilencerCalibrate = (LinearLayout) findViewById(R.id.ll_silencer_calibrate);
		mCalibrate = (TextView) findViewById(R.id.silencer_calibrate);
		rlSpeedControl = (RelativeLayout) findViewById(R.id.rl_speed_control);
		mSpeedControl = (CheckBox) findViewById(R.id.speed_control);
		mSpeedAlgorithm = (LinearLayout) findViewById(R.id.ll_speed_algorithm);
		mTvSpeedAlgorithm = (TextView) findViewById(R.id.speed_algorithm);
		mBytesSpeed = (LinearLayout) findViewById(R.id.ll_bytes_speed);
		mTvBytesSpeed = (TextView) findViewById(R.id.bytes_speed);
		addItem = (LinearLayout) findViewById(R.id.ll_add_item);
	}

	@Override
	public void initData() {
		mPrefs = LinphonePreferences.instance();
		titleView.setTitle("音频参数设置");
		algorithms = getResources().getStringArray(R.array.adaptive_rate_algorithm_entries);
		selectLawDialog = new PublishSelectLawDialog(this, algorithms, mTvSpeedAlgorithm, "选择自适应速率算法");
		byteSpeeds = getResources().getStringArray(R.array.limit_bitrate_entry_values);
		selectBytesDialog = new PublishSelectLawDialog(this, byteSpeeds, mTvBytesSpeed, "选择编解码器比特率限制");
		mSilencer.setChecked(mPrefs.isEchoCancellationEnabled());
		if (mPrefs.isEchoCancellationEnabled()) {
			mCalibrate.setVisibility(View.VISIBLE);
			mCalibrate.setText(String.format(getString(R.string.ec_calibrated), mPrefs.getEchoCalibration()));
		}
		mSpeedControl.setChecked(mPrefs.isAdaptiveRateControlEnabled());
		if(mPrefs.getAdaptiveRateAlgorithm() != null && String.valueOf(mPrefs.getAdaptiveRateAlgorithm()).length() > 0){
			mTvSpeedAlgorithm.setText(String.valueOf(mPrefs.getAdaptiveRateAlgorithm()));	
		} else {
			selectLawDialog.setSelection(0);
		}
		
		if(mPrefs.getCodecBitrateLimit() > 0){
			mTvBytesSpeed.setText(String.valueOf(mPrefs.getCodecBitrateLimit()));
		} else {
			selectBytesDialog.setSelection(0);	
		}
		addItemView();
	}

	@Override
	public void initListener() {
		mRlSilencer.setOnClickListener(this);
		mSilencerCalibrate.setOnClickListener(this);
		rlSpeedControl.setOnClickListener(this);
		mSpeedAlgorithm.setOnClickListener(this);
		mBytesSpeed.setOnClickListener(this);
		selectLawDialog.setSelectLawDateItemClick(this);
		selectBytesDialog.setSelectLawDateItemClick(this);
		titleView.setLeftBackOneListener(R.drawable.back_normal, new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});

	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.silencer_check:
			boolean enabled = mSilencer.isChecked();
			mPrefs.setEchoCancellation(enabled);
			if (enabled) {
				mSilencer.setChecked(!enabled);
			} else {
				mSilencer.setChecked(enabled);
			}
			break;

		case R.id.ll_silencer_calibrate:
			mCalibrate.setVisibility(View.VISIBLE);
			synchronized (this) {
				try {
					LinphoneManager.getInstance().startEcCalibration(this);
					mCalibrate.setText(R.string.ec_calibrating);
				} catch (LinphoneCoreException e) {
					Log.w(e, "Cannot calibrate EC");
				}
			}
			break;
		case R.id.rl_speed_control:
			boolean enabledControl = mSpeedControl.isChecked();
			mPrefs.enableAdaptiveRateControl(enabledControl);
			if (enabledControl) {
				mSpeedControl.setChecked(!enabledControl);
			} else {
				mSpeedControl.setChecked(enabledControl);
			}
			break;
		case R.id.ll_speed_algorithm:
			isFlag = false;
			selectLawDialog.show();
			break;
		case R.id.ll_bytes_speed:
			isFlag = true;
			selectBytesDialog.show();
			break;
		}
	}

	private void addItemView() {
		addItem.removeAllViews();
		addItem.removeAllViewsInLayout();
		final LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
		for (final PayloadType pt : lc.getAudioCodecs()) {
			View view = getLayoutInflater().inflate(R.layout.add_audio_item_layout, null);
			RelativeLayout click = (RelativeLayout) view.findViewById(R.id.add_onclick);
			TextView title = (TextView) view.findViewById(R.id.add_silencer_title);
			TextView content = (TextView) view.findViewById(R.id.add_silencer_hint);
			final CheckBox checkBox = (CheckBox) findViewById(R.id.silencer_check);
			title.setText(pt.getMime());
			/* Special case */
			if (pt.getMime().equals("mpeg4-generic")) {
				if (android.os.Build.VERSION.SDK_INT < 16) {
					/* Make sure AAC is disabled */
					try {
						lc.enablePayloadType(pt, false);
					} catch (LinphoneCoreException e) {
						e.printStackTrace();
					}
					continue;
				} else {
					title.setText("AAC-ELD");
				}
			}

			content.setText(pt.getRate() + " Hz");
			checkBox.setChecked(lc.isPayloadTypeEnabled(pt));
			click.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					boolean enable = checkBox.isChecked();
					try {
						LinphoneManager.getLcIfManagerNotDestroyedOrNull().enablePayloadType(pt, enable);
					} catch (LinphoneCoreException e) {
						e.printStackTrace();
					}
					checkBox.setChecked(lc.isPayloadTypeEnabled(pt));
				}
			});
			addItem.addView(view);
		}
	}


	@Override
	public void ecCalibrationStatus(LinphoneCore lc, EcCalibratorStatus status, int delayMs, Object data) {
		LinphoneManager.getInstance().routeAudioToReceiver();
		if (status == EcCalibratorStatus.DoneNoEcho) {
			mCalibrate.setText(R.string.no_echo);
			mSilencer.setChecked(false);
			LinphonePreferences.instance().setEchoCancellation(false);
		} else if (status == EcCalibratorStatus.Done) {
			mCalibrate.setText(String.format(getString(R.string.ec_calibrated), delayMs));
			mSilencer.setChecked(true);
			LinphonePreferences.instance().setEchoCancellation(true);
		} else if (status == EcCalibratorStatus.Failed) {
			mCalibrate.setText(R.string.failed);
			mSilencer.setChecked(true);
			LinphonePreferences.instance().setEchoCancellation(true);
		}
	}

	@Override
	public void lawDateOnClick(int position) {
		if(!isFlag){
			String algorithm = algorithms[position];
			mPrefs.setAdaptiveRateAlgorithm(AdaptiveRateAlgorithm.fromString((String) algorithm));
//			preference.setSummary(String.valueOf(mPrefs.getAdaptiveRateAlgorithm()));	
		} else {
			String byteSpeed = byteSpeeds[position];
			mPrefs.setCodecBitrateLimit(Integer.parseInt(byteSpeed.toString()));
			LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
			int bitrate = Integer.parseInt(byteSpeed.toString());

			for (final PayloadType pt : lc.getAudioCodecs()) {
				if (lc.payloadTypeIsVbr(pt)) {
					lc.setPayloadTypeBitrate(pt, bitrate);
				}
			}
//			preference.setSummary(String.valueOf(mPrefs.getCodecBitrateLimit()));
		}
	}

}
