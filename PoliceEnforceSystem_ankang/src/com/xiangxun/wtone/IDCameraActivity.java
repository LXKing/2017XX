package com.xiangxun.wtone;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.MediaColumns;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiangxun.activity.R;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

//示例拍照并调用识别将结果显示在这个程序结果界面
public class IDCameraActivity extends Activity implements SurfaceHolder.Callback ,Camera.PreviewCallback, OnClickListener {
	private static final String TAG = "CameraActivity";
	public static final String PATH = Environment.getExternalStorageDirectory()
			.toString() + "/wtimage/";
	private String strCaptureFilePath = PATH + "/camera_snap.jpg";
	public static final Uri IMAGE_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	// 预览尺寸 默认设置
	public int WIDTH = 320;// 640;//1024;
	public int HEIGHT = 240;// 480;//768;
	// 拍摄尺寸 默认设置
	public int srcwidth = 2048;// 1600;//2048;final
	public int srcheight = 1536;// 1200;//1536;final
	// 裁切尺寸
	private int cutwidth = 1300;// 1845;//1100;
	private int cutheight = 200;// 1155;//750;
	// 证件类型
	int nMainID = 0;

	private boolean isCatchPreview = false;
	private boolean isCatchPicture = false;

	String imagename = "";
	private ImageButton backbtn, confirmbtn, resetbtn, takepicbtn, lighton,
			lightoff, cuton, cutoff;
	private TextView back_reset_text, take_recog_text, light_text, cut_text;
	private Camera camera;
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	private ToneGenerator tone;
	private ImageView imageView;
	private boolean isTaking = false;
	private Bitmap bitmap;
	public Map<Object, Object> imagemap = new HashMap<Object, Object>();
	private byte[] imagedata;
	private RelativeLayout rlyaout;
	private Boolean cut = true;
	private List<String> focusModes;
	private String path = "";
	public long fastClick = 0;
	public int recogType = -1;// 自动识别、划线识别
	public boolean isVinRecog;
	private int width, height;
	private ImageView top_left, top_right, bottom_left, bottom_right, left_cut,
			right_cut;
	private SensorManager sManager = null;
	private SensorEventListener myListener = null;
	private float x, y, z;
	private final int UPTATE_Difference_TIME = 200;
	private int count = 0, afterCount = 0;
	private final float MoveDifference = 0.08f;
	private final float MoveDifferencemin = 0.001f;
	private long last_Time;
	private Boolean noShakeBoolean = false;
	private List<float[]> mlist;
	private double move_Difference;
	private int layout_width;
	private final int ListMaxLen = 3;
	public static final int KEYCODE_T = 27;
	public static final int KEYCODE_F1 = 131;
	public static final int KEYCODE_F2 = 132;
	public static final int KEYCODE_F3 = 133;
	private boolean isTakePic = false;
	private boolean isAutoTakePic = false;// 是否设置自动拍照识别功能
	Handler handler = new Handler();
	private TimerTask timer;
	private Handler handler01 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			takepicbtn.setEnabled(true);
			findview();

		}
	};
	private Timer time = new Timer();

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		checkCameraParameters();
		writePreferences("", "WIDTH", WIDTH);
		writePreferences("", "HEIGHT", HEIGHT);
		writePreferences("", "srcwidth", srcwidth);
		writePreferences("", "srcheight", srcheight);
		writePreferences("", "isAutoTakePic", 1);
		// 设置拍摄尺寸
		Intent intentget = this.getIntent();
//		srcwidth = intentget.getIntExtra("srcwidth", 2048);
//		srcheight = intentget.getIntExtra("srcheight", 1536);
//		WIDTH = intentget.getIntExtra("WIDTH", 640);
//		HEIGHT = intentget.getIntExtra("HEIGHT", 480);
		recogType = intentget.getIntExtra("recogType", 1);
		nMainID = intentget.getIntExtra("nMainId", 5);
	
		mlist = new ArrayList<float[]>();
		sManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		// 监听传感器事件
		myListener = new SensorEventListener() {
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {

			}

			@Override
			public void onSensorChanged(SensorEvent event) {
				long now_Time = System.currentTimeMillis();// 获取当前时间
				long time_Difference = now_Time - last_Time;

				if (time_Difference >= UPTATE_Difference_TIME) {
					// System.out.println("time_Difference:" + time_Difference);
					last_Time = now_Time;
					x = event.values[SensorManager.DATA_X];
					y = event.values[SensorManager.DATA_Y];
					z = event.values[SensorManager.DATA_Z];

					double move_Difference = getStableFloat(x, y, z);
					if (count < 5) {
						count++;
					}
					// else if(count==10&&afterCount<=4)
					// {
					// afterCount++;
					// }
					// System.out.println("次数:" + count );
					if (isAutoTakePic) {
						if (move_Difference <= MoveDifference
								&& move_Difference >= MoveDifferencemin
								&& count == 5) {

							if (!noShakeBoolean) {
								noShakeBoolean = true;
								takePicture();
								// if(afterCount==4){
								// camera.takePicture(shutterCallback, null,
								// PictureCallback);
								// }
							}
						} else if (move_Difference > MoveDifference) {
							count = 0;
							afterCount = 0;
							// System.out.println("count:" + count);
							noShakeBoolean = false;
							if (timer != null) {
								timer.cancel();
								timer = null;
							}
						}
					}
				}
			}
		};
		sManager.registerListener(myListener,
				sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
		
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(IDCameraActivity.this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		noShakeBoolean = false;
		if (readPreferences("", "isAutoTakePic") == 1)
			isAutoTakePic = true;
		else if (readPreferences("", "isAutoTakePic") == 0)
			isAutoTakePic = false;
		// new Thread() {
		// public void run() {
		// try {
		// sleep(2000);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// Looper.prepare();
		// autoFocus();
		// Looper.loop();
		// };
		// }.start();
		

		if (!SharedPreferencesHelper.getBoolean(getApplicationContext(),
				"cut-mode", true)) {
			cuton.setVisibility(View.VISIBLE);
			cutoff.setVisibility(View.INVISIBLE);
			cut_text.setText(getString(R.string.OpenedCutting));
			cut = false;
		} else {
			cuton.setVisibility(View.INVISIBLE);
			cutoff.setVisibility(View.VISIBLE);
			cut_text.setText(getString(R.string.ClosedCutting));
			cut = true;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		height = dm.heightPixels;
		System.out.println("屏幕的宽和高:" + width + "--" + height);
		setContentView(R.layout.wintone_camera);
		if (nMainID == 0) {
			String cfg = "";
			try {
				cfg = readtxt();
			} catch (Exception e) {
				e.printStackTrace();
			}
			String cfgs[] = cfg.split("==##");
			if (cfgs != null && cfgs.length >= 2) {
				if (cfgs[0] != null && !cfgs[0].equals("")) {
					try {
						nMainID = Integer.parseInt(cfgs[0]);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		findview();
		System.out.println("onCreate");
	}

	private void findview() {
		back_reset_text = (TextView) findViewById(R.id.back_and_reset_text);
		back_reset_text.setTextColor(Color.BLACK);
		take_recog_text = (TextView) findViewById(R.id.take_and_confirm_text);
		take_recog_text.setTextColor(Color.BLACK);
		light_text = (TextView) findViewById(R.id.light_on_off_text);
		light_text.setTextColor(Color.BLACK);
		cut_text = (TextView) findViewById(R.id.cut_on_off_text);
		cut_text.setTextColor(Color.BLACK);

		int button_width = (int) (height * 0.125);
		int button_distance = (int) (height * 0.1);

		RelativeLayout.LayoutParams lParams = new RelativeLayout.LayoutParams(
				button_width, button_width);
		lParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		lParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
		lParams.topMargin = button_distance;
		backbtn = (ImageButton) findViewById(R.id.backbtn);
		backbtn.setLayoutParams(lParams);
		backbtn.setOnClickListener(new mClickListener());
		resetbtn = (ImageButton) findViewById(R.id.reset_btn);
		resetbtn.setLayoutParams(lParams);
		resetbtn.setOnClickListener(new mClickListener());

		lParams = new RelativeLayout.LayoutParams(button_width, button_width);
		lParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		lParams.addRule(RelativeLayout.BELOW, R.id.backbtn);
		lParams.topMargin = button_distance;
		takepicbtn = (ImageButton) findViewById(R.id.takepic_btn);
		takepicbtn.setLayoutParams(lParams);
		takepicbtn.setOnClickListener(new mClickListener());
		confirmbtn = (ImageButton) findViewById(R.id.confirm_btn);
		confirmbtn.setLayoutParams(lParams);
		confirmbtn.setOnClickListener(new mClickListener());

		lParams = new RelativeLayout.LayoutParams(button_width, button_width);
		lParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		lParams.addRule(RelativeLayout.BELOW, R.id.confirm_btn);
		lParams.topMargin = button_distance;
		lighton = (ImageButton) findViewById(R.id.lighton);
		lighton.setLayoutParams(lParams);
		lighton.setOnClickListener(new mClickListener());
		lightoff = (ImageButton) findViewById(R.id.lightoff);
		lightoff.setLayoutParams(lParams);
		lightoff.setOnClickListener(new mClickListener());

		lParams = new RelativeLayout.LayoutParams(button_width, button_width);
		lParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		lParams.addRule(RelativeLayout.BELOW, R.id.lighton);
		lParams.topMargin = button_distance;
		cuton = (ImageButton) findViewById(R.id.cuton);
		cuton.setLayoutParams(lParams);
		cuton.setOnClickListener(new mClickListener());
		cutoff = (ImageButton) findViewById(R.id.cutoff);
		cutoff.setLayoutParams(lParams);
		cutoff.setOnClickListener(new mClickListener());

		top_left = (ImageView) findViewById(R.id.topleft);
		top_right = (ImageView) findViewById(R.id.topright);
		bottom_left = (ImageView) findViewById(R.id.bottomleft);
		bottom_right = (ImageView) findViewById(R.id.bottomright);

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				(int) (height * 0.18), (int) (height * 0.18));
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
				RelativeLayout.TRUE);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
				RelativeLayout.TRUE);
		top_left.setLayoutParams(layoutParams);

		layoutParams = new RelativeLayout.LayoutParams((int) (height * 0.18),
				(int) (height * 0.18));
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
				RelativeLayout.TRUE);
		layoutParams.addRule(RelativeLayout.LEFT_OF, R.id.idcard_rightlyaout);
		top_right.setLayoutParams(layoutParams);

		layoutParams = new RelativeLayout.LayoutParams((int) (height * 0.18),
				(int) (height * 0.18));
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
				RelativeLayout.TRUE);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
				RelativeLayout.TRUE);
		bottom_left.setLayoutParams(layoutParams);

		layoutParams = new RelativeLayout.LayoutParams((int) (height * 0.18),
				(int) (height * 0.18));
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
				RelativeLayout.TRUE);
		layoutParams.addRule(RelativeLayout.LEFT_OF, R.id.idcard_rightlyaout);
		bottom_right.setLayoutParams(layoutParams);

		int margin = 0;
		int cutImageLayoutHeight = 0;
		if (srcwidth == 1280 || srcwidth == 960) {
			margin = (int) ((height * 1.333) * 0.165);
			cutImageLayoutHeight = (int) (height * 0.135);
		}
		if (srcwidth == 1600 || srcwidth == 1200) {
			margin = (int) ((height * 1.333) * 0.19);
			cutImageLayoutHeight = (int) (height * 0.108);
		}
		if (srcwidth == 2048 || srcwidth == 1536) {
			margin = (int) ((height * 1.333) * 0.22);
			cutImageLayoutHeight = (int) (height * 0.13);
		}
		left_cut = (ImageView) findViewById(R.id.leftcut);
		right_cut = (ImageView) findViewById(R.id.rightcut);
		layoutParams = new RelativeLayout.LayoutParams(
				(int) (cutImageLayoutHeight * 0.6), cutImageLayoutHeight);
		layoutParams.addRule(RelativeLayout.CENTER_VERTICAL,
				RelativeLayout.TRUE);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
				RelativeLayout.TRUE);
		layoutParams.leftMargin = margin;
		left_cut.setLayoutParams(layoutParams);

		layoutParams = new RelativeLayout.LayoutParams(
				(int) (cutImageLayoutHeight * 0.6), cutImageLayoutHeight);
		layoutParams.addRule(RelativeLayout.CENTER_VERTICAL,
				RelativeLayout.TRUE);
		layoutParams.addRule(RelativeLayout.LEFT_OF, R.id.idcard_rightlyaout);
		layoutParams.rightMargin = margin;
		right_cut.setLayoutParams(layoutParams);

		imageView = (ImageView) findViewById(R.id.backimageView);
		surfaceView = (SurfaceView) findViewById(R.id.surfaceViwe);
		rlyaout = (RelativeLayout) findViewById(R.id.idcard_rightlyaout);
		if (WIDTH == 1920 && HEIGHT == 1080) {
			layout_width = (int) (width - (height * 1.5));
		} else {
			layout_width = width - ((height * 4) / 3);
		}
		if (((float) width / height == (float) 4 / 3)
				|| ((float) width / height == (float) 3 / 4)) {
			RelativeLayout.LayoutParams lP = new RelativeLayout.LayoutParams(
					width / 4, height);
			// lP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
			// RelativeLayout.TRUE);
			lP.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
			lP.leftMargin = 3 * width / 4;
			rlyaout.setLayoutParams(lP);
			lP = new RelativeLayout.LayoutParams(width, height);
			lP.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
			lP.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
			surfaceView.setLayoutParams(lP);
		} else {
			RelativeLayout.LayoutParams lP = new RelativeLayout.LayoutParams(
					layout_width, height);
			lP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
			lP.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
			rlyaout.setLayoutParams(lP);
		}
		if (nMainID == 1100 || nMainID == 1101) {
			left_cut.setBackgroundResource(R.drawable.leftcut);
			right_cut.setBackgroundResource(R.drawable.rightcut);
			showTwoCutImageView();
		} else {
			top_left.setBackgroundResource(R.drawable.top_left);
			bottom_left.setBackgroundResource(R.drawable.bottom_left);
			top_right.setBackgroundResource(R.drawable.top_right);
			bottom_right.setBackgroundResource(R.drawable.bottom_right);
			if (((float) width / height == (float) 4 / 3)
					|| ((float) width / height == (float) 3 / 4)) {
				RelativeLayout.LayoutParams lP = new RelativeLayout.LayoutParams(
						width / 20, width / 30);
				lP.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
						RelativeLayout.TRUE);
				lP.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
				top_left.setLayoutParams(lP);
				lP = new RelativeLayout.LayoutParams(width / 20, width / 30);
				lP.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
						RelativeLayout.TRUE);
				lP.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
						RelativeLayout.TRUE);
				bottom_left.setLayoutParams(lP);
				lP = new RelativeLayout.LayoutParams(width / 20, width / 30);
				lP.leftMargin = (int) (width * 0.7);
				lP.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
				top_right.setLayoutParams(lP);
				lP = new RelativeLayout.LayoutParams(width / 20, width / 30);
				lP.leftMargin = (int) (width * 0.7);
				lP.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
						RelativeLayout.TRUE);
				bottom_right.setLayoutParams(lP);

			}
			showFourImageView();
		}
		if (SharedPreferencesHelper.getBoolean(getApplicationContext(),
				"flash-mode", false)) {

			lightoff.setVisibility(View.VISIBLE);
			lighton.setVisibility(View.INVISIBLE);
			light_text.setText(getString(R.string.light2_string));

		} else {

			lightoff.setVisibility(View.INVISIBLE);
			lighton.setVisibility(View.VISIBLE);
			light_text.setText(getString(R.string.light1_string));

		}
	}

	private void showTwoCutImageView() {
		left_cut.setVisibility(View.VISIBLE);
		right_cut.setVisibility(View.VISIBLE);
		top_left.setVisibility(View.INVISIBLE);
		top_right.setVisibility(View.INVISIBLE);
		bottom_left.setVisibility(View.INVISIBLE);
		bottom_right.setVisibility(View.INVISIBLE);
	}

	private void hideTwoCutImageView() {
		left_cut.setVisibility(View.INVISIBLE);
		right_cut.setVisibility(View.INVISIBLE);
	}

	private void hideFourImageView() {
		top_left.setVisibility(View.INVISIBLE);
		top_right.setVisibility(View.INVISIBLE);
		bottom_left.setVisibility(View.INVISIBLE);
		bottom_right.setVisibility(View.INVISIBLE);
	}

	private void showFourImageView() {
		left_cut.setVisibility(View.INVISIBLE);
		right_cut.setVisibility(View.INVISIBLE);
		top_left.setVisibility(View.VISIBLE);
		top_right.setVisibility(View.VISIBLE);
		bottom_left.setVisibility(View.VISIBLE);
		bottom_right.setVisibility(View.VISIBLE);

	}

	private class mClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.backbtn:
				closeCamera();
				if (timer != null) {
					timer.cancel();
					timer = null;
				}
				setResult(Activity.RESULT_CANCELED);
				IDCameraActivity.this.finish();
				break;
			// 拍照
			case R.id.takepic_btn:
				if (timer != null) {
					timer.cancel();
					timer = null;
				}
				takepicbtn.setEnabled(false);
				takePicture();
				break;
			case R.id.lighton:
				SharedPreferencesHelper.putBoolean(getApplicationContext(),
						"flash-mode", true);
				lightoff.setVisibility(View.VISIBLE);
				lighton.setVisibility(View.INVISIBLE);
				light_text.setText(getString(R.string.light2_string));

				Camera.Parameters parameters = camera.getParameters();
				parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
				camera.setParameters(parameters);
				break;
			case R.id.lightoff:
				SharedPreferencesHelper.putBoolean(getApplicationContext(),
						"flash-mode", false);
				lighton.setVisibility(View.VISIBLE);
				lightoff.setVisibility(View.INVISIBLE);
				light_text.setText(getString(R.string.light1_string));
				// 关闭闪光灯
				Camera.Parameters parameters2 = camera.getParameters();
				parameters2.setFlashMode(Parameters.FLASH_MODE_OFF);
				camera.setParameters(parameters2);
				break;
			case R.id.cuton:
				SharedPreferencesHelper.putBoolean(getApplicationContext(),
						"cut-mode", true);
				cuton.setVisibility(View.INVISIBLE);
				cutoff.setVisibility(View.VISIBLE);
				cut_text.setText(getString(R.string.ClosedCutting));
				cut = true;
				break;

			case R.id.cutoff:
				SharedPreferencesHelper.putBoolean(getApplicationContext(),
						"cut-mode", false);
				cuton.setVisibility(View.VISIBLE);
				cutoff.setVisibility(View.INVISIBLE);
				cut_text.setText(getString(R.string.OpenedCutting));
				cut = false;
				break;
			}

		}

	}

	protected int readPreferences(String perferencesName, String key) {
		SharedPreferences preferences = getSharedPreferences(perferencesName,
				MODE_PRIVATE);
		int result = preferences.getInt(key, 0);
		return result;
	}

	protected void writePreferences(String perferencesName, String key,
									int value) {
		SharedPreferences preferences = getSharedPreferences(perferencesName,
				MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	// 读取配置文件
	public String readtxt() throws IOException {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
		}
		String paths = sdDir.toString();
		if (paths.equals("") || paths == null) {
			return "";
		}
		String path = paths + "/AndroidWT/idcard.cfg";
		File file = new File(path);
		if (!file.exists())
			return "";
		FileReader fileReader = new FileReader(path);
		BufferedReader br = new BufferedReader(fileReader);
		String str = "";
		String r = br.readLine();
		while (r != null) {
			str += r;
			r = br.readLine();
		}
		br.close();
		fileReader.close();
		return str;
	}

	public boolean isEffectClick() {
		long lastClick = System.currentTimeMillis();
		long diffTime = lastClick - fastClick;
		if (diffTime > 5000) {
			fastClick = lastClick;
			return true;
		}
		return false;
	}

	/* 拍照对焦 */
	/* 拍照 */
	public void takePicture() {
		if (camera != null) {
			try {
				if (focusModes != null
						&& focusModes
								.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
					camera.autoFocus(new AutoFocusCallback() {
						@Override
						public void onAutoFocus(boolean success, Camera camera) {

							if (success) {
								if (count == 5 || !isAutoTakePic) {
									camera.takePicture(shutterCallback, null,
											PictureCallback);
								}
							} else {
								if (count == 5 || !isAutoTakePic) {
									camera.takePicture(shutterCallback, null,
											PictureCallback);
								}
							}

						}
					});
				} else {
					camera.takePicture(shutterCallback, null, PictureCallback);
				}

			} catch (Exception e) {
				e.printStackTrace();
				camera.stopPreview();
				camera.startPreview();
				takepicbtn.setEnabled(true);
				Toast.makeText(this, R.string.toast_autofocus_failure,
						Toast.LENGTH_SHORT).show();
				Log.i(TAG, "exception:" + e.getMessage());
				// System.out.println( "exception:" + e.getMessage());
			}
		}
	}

	// 快门按下的时候onShutter()被回调拍照声音
	private ShutterCallback shutterCallback = new ShutterCallback() {
		@Override
		public void onShutter() {
			if (tone == null)
				// 发出提示用户的声音
				tone = new ToneGenerator(1,// AudioManager.AUDIOFOCUS_REQUEST_GRANTED
						ToneGenerator.MIN_VOLUME);
			tone.startTone(ToneGenerator.TONE_PROP_BEEP);
		}
	};

	/* 拍照后回显 */
	private Camera.PictureCallback PictureCallback = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			count = 0;
			afterCount = 0;
			Log.i(TAG, "onPictureTaken");
			BitmapFactory.Options opts = new BitmapFactory.Options();
			// 设置成了true,不占用内存，只获取bitmap宽高
			opts.inJustDecodeBounds = true;
			// 根据内存大小设置采样率
			// 需要测试！
			int SampleSize = computeSampleSize(opts, -1, 2048 * 1536);
			opts.inSampleSize = SampleSize;
			opts.inJustDecodeBounds = false;
			opts.inPurgeable = true;
			opts.inInputShareable = true;
			// opts.inNativeAlloc = true;
			// //属性设置为true，可以不把使用的内存算到VM里。SDK默认不可设置这个变量，只能用反射设置。
			try {
				Field field = BitmapFactory.Options.class
						.getDeclaredField("inNativeAlloc");
				field.set(opts, true);
			} catch (Exception e) {
				Log.i(TAG, "Exception inNativeAlloc");
			}
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
			if (srcwidth == 2048 && srcheight == 1536) {
				Matrix matrix = new Matrix();
				matrix.postScale(0.625f, 0.625f);
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
						bitmap.getHeight(), matrix, true);
			}
			if (srcwidth == 1600 && srcheight == 1200) {
				Matrix matrix = new Matrix();
				matrix.postScale(0.8f, 0.8f);
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
						bitmap.getHeight(), matrix, true);
			}
			imagedata = data;
			/* 创建文件 */
			File dir = new File(PATH);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			// 保存样本start
			new FrameCapture(bitmap, "11");
			// 保存样本end
			File myCaptureFile = new File(strCaptureFilePath);
			try {
				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(myCaptureFile));
				/* 采用压缩转档方法 */
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
				/* 调用flush()方法，更新BufferStream */
				bos.flush();
				/* 结束OutputStream */
				bos.close();
				// 隐藏焦点图片和行驶证外框

				if (nMainID == 1100 || nMainID == 1101) {
					hideTwoCutImageView();
				} else {
					hideFourImageView();
				}

				/* 将拍照下来且保存完毕的图文件，显示出来 */
				imageView.setImageBitmap(bitmap);
				// takepicbtn.setVisibility(View.INVISIBLE);
				// backbtn.setVisibility(View.INVISIBLE);
				// resetbtn.setVisibility(View.VISIBLE);
				// back_reset_text.setText(getString(R.string.reset_btn_string));
				// confirmbtn.setVisibility(View.VISIBLE);
				// take_recog_text.setText(getString(R.string.confirm_btn_string));
				// confirmbtn.setEnabled(true);
				savephoto();
				if (isAutoTakePic) {

					if (timer != null) {
						timer.cancel();
						timer = null;
					}
				}
				resetCamera();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	};

	/* 保存图片并送识别 */
	private void savephoto() {

		// 系统时间
		long datetime = System.currentTimeMillis();
		// 图像名称
		Date date = new Date();
		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddhhmmss");
		String sysDatetime = fmt.format(date.getTime());
		// String name = "idcard" + date.getTime() + ".jpg";
		String name = "idcard_" + sysDatetime + ".jpg";
		// 存储图像（PATH目录）
		Uri uri = insertImage(getContentResolver(), name, datetime, PATH, name,
				bitmap, imagedata);// bm bitmap

		// 裁切
		if ((nMainID == 1100 || nMainID == 1101) && recogType == 1) {
			if (srcwidth == 1280 || srcwidth == 960) {
				cutwidth = 750;
				cutheight = 130;
			}
			if (srcwidth == 2048 || srcwidth == 1536) {
				cutwidth = 1300;
				cutheight = 200;
			}
			cutwidth = 750;
			cutheight = 130;
			CutPhoto cutPhoto = new CutPhoto(IDCameraActivity.this, cutwidth,
					cutheight);
			path = cutPhoto.getCutPhotoPath(bitmap,
					name.replace(".jpg", "_cut"), PATH);
		} else {
			path = PATH + "/" + name;
		}
		if (bitmap != null) {
			bitmap.recycle();
			bitmap = null;
		}

		if (recogType == 3 && (1100 == nMainID || 1101 == nMainID)) {
//			Intent intent = new Intent(IDCameraActivity.this,
//					RecognizeActivity.class);
//			intent.putExtra("selectPath", path);
//			// 设置识别自动裁切
//			intent.putExtra("iscut", true);
//			intent.putExtra("recogType", recogType);
//			intent.putExtra("nMainID", nMainID);
//			IDCameraActivity.this.finish();
//			startActivity(intent);
		} else {
			Intent intent = new Intent(IDCameraActivity.this, IdcardRunner.class);
			intent.putExtra("path", path);
			intent.putExtra("cut", cut);
			// 设置识别自动裁切
			intent.putExtra("iscut", true);
			intent.putExtra("nMainID", nMainID);
			startActivityForResult(intent, 10);
		}
	}

	// 存储图像并将信息添加入媒体数据库
	private Uri insertImage(ContentResolver cr, String name, long dateTaken,
							String directory, String filename, Bitmap source, byte[] jpegData) {

		OutputStream outputStream = null;
		String filePath = directory + filename;
		try {
			File dir = new File(directory);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File file = new File(directory, filename);
			if (file.createNewFile()) {
				outputStream = new FileOutputStream(file);
				if (source != null) {
					// 如果证件类型为身份证号码识别则将图片旋转90度
					if (nMainID == 1102) {
						Matrix matrix = new Matrix();
						matrix.reset();
						matrix.setRotate(90);
						source = Bitmap.createBitmap(source, 0, 0,
								source.getWidth(), source.getHeight(), matrix,
								true);
					}
					//
					source.compress(CompressFormat.JPEG, 100, outputStream);

				} else {
					outputStream.write(jpegData);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (Throwable t) {
				}
			}
		}
		ContentValues values = new ContentValues(7);
		values.put(MediaColumns.TITLE, name);
		values.put(MediaColumns.DISPLAY_NAME, filename);
		values.put(ImageColumns.DATE_TAKEN, dateTaken);
		values.put(MediaColumns.MIME_TYPE, "image/jpeg");
		values.put(MediaColumns.DATA, filePath);
		return cr.insert(IMAGE_URI, values);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
							   int height) {
		if (camera != null) {
			try {
				Camera.Parameters parameters = camera.getParameters();
				if (SharedPreferencesHelper.getBoolean(getApplicationContext(),
						"flash-mode", false)) {
					parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
				} else {
					parameters.setFlashMode(Parameters.FLASH_MODE_OFF);

				}
				parameters.setPictureFormat(PixelFormat.JPEG);
				EquipmentUtill er = new EquipmentUtill();
				if (er.CheckPLKTL01H()) {
					parameters.setPreviewSize(1920, 1080);

				} else {
					parameters.setPreviewSize(WIDTH, HEIGHT);

				}
				parameters.setPictureSize(srcwidth, srcheight);

				camera.setParameters(parameters);
				camera.setPreviewDisplay(holder);
				camera.setPreviewCallback(IDCameraActivity.this);
				camera.startPreview();
				focusModes = parameters.getSupportedFocusModes();
			} catch (IOException e) {
				camera.release();
				camera = null;
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i(TAG, "surfaceCreated");
		// 获得Camera对象

		try {
			if (null == camera) {
				camera = Camera.open();
			}
		} catch (Exception e) {
		}
		if (!isAutoTakePic)
		{

			startTimer();
		}
		Message msg = new Message();
		handler01.sendMessage(msg);
	}
/**
 * 开启计时器
 */
	private void startTimer() {
		// TODO Auto-generated method stub
		if (timer == null) {
			timer = new TimerTask() {
				@Override
				public void run() {
					if (camera != null) {
						try {
							autoFocus();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
			};

		}
		if(time==null){
			time=new Timer();
		}
		time.schedule(timer, 200, 2500);

	}

	// 在surface创建时激发
	@Override
	public void surfaceCreated(SurfaceHolder holder) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		closeCamera();
	}

	/* 相机重置 */
	private void resetCamera() {
		if (camera != null) {
			camera.stopPreview();

		}
	}

	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	public void displayPopuWindow(String message) {
		int relalayoutwidth = width - ((height * 4) / 3);
		int scWidth = this.getWindow().getWindowManager().getDefaultDisplay()
				.getWidth();
		int scHeight = this.getWindow().getWindowManager().getDefaultDisplay()
				.getHeight();
		int popuWidth = scWidth - relalayoutwidth;
		EditText et = new EditText(this);
		et.setWidth(scWidth);
		et.setHeight(popuWidth);
		PopupWindow popuWindow = new PopupWindow(et, popuWidth, 200, true);
		popuWindow.setWidth(popuWidth);
		popuWindow.setHeight(200);
		popuWindow.setContentView(et);
		ColorDrawable color = new ColorDrawable(Color.BLACK);
		color.setAlpha(60);
		popuWindow.setBackgroundDrawable(color);
		popuWindow.setOutsideTouchable(true);
		popuWindow.setFocusable(true);
		popuWindow.showAtLocation(surfaceView, Gravity.LEFT, 0, scHeight - 200);
		et.setText(message);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 10 && resultCode == RESULT_OK) {
			int ReturnAuthority = data.getIntExtra("ReturnAuthority", -100000);// 取激活状态
			int ReturnInitIDCard = data
					.getIntExtra("ReturnInitIDCard", -100000);// 取初始化返回值
			int ReturnLoadImageToMemory = data.getIntExtra(
					"ReturnLoadImageToMemory", -100000);// 取读图像的返回值
			int ReturnRecogIDCard = data.getIntExtra("ReturnRecogIDCard",
					-100000);// 取识别的返回值
			if (ReturnAuthority == 0 && ReturnInitIDCard == 0
					&& ReturnLoadImageToMemory == 0 && ReturnRecogIDCard > 0) {
				String result = "";
				String[] fieldname = (String[]) data
						.getSerializableExtra("GetFieldName");
				String[] fieldvalue = (String[]) data
						.getSerializableExtra("GetRecogResult");
//				Toast.makeText(getApplicationContext(), "fieldvalue", 0).show();
//				if (null != fieldname) {
//					int count = fieldname.length;
//					for (int i = 0; i < count; i++) {
//						if (fieldname[i] != null) {
//							result += fieldname[i] + ":" + fieldvalue[i]
//									+ ";\n";
//						}
//					}
//				}
//				displayPopuWindow(getString(R.string.recogResult)
//						+ ReturnRecogIDCard + "\n" + result);

				Intent intent = new Intent();
				intent.putExtra("recogResult",fieldvalue[1] );
				setResult(Activity.RESULT_OK, intent);
				IDCameraActivity.this.finish();

			} else {
				String str = "";
				if (ReturnAuthority == -100000) {
					str = getString(R.string.exception) + ReturnAuthority;
				} else if (ReturnAuthority != 0) {
					str = getString(R.string.exception1) + ReturnAuthority;
				} else if (ReturnInitIDCard != 0) {
					str = getString(R.string.exception2) + ReturnInitIDCard;
				} else if (ReturnLoadImageToMemory != 0) {
					if (ReturnLoadImageToMemory == 3) {
						str = getString(R.string.exception3)
								+ ReturnLoadImageToMemory;
					} else if (ReturnLoadImageToMemory == 1) {
						str = getString(R.string.exception4)
								+ ReturnLoadImageToMemory;
					} else {
						str = getString(R.string.exception5)
								+ ReturnLoadImageToMemory;
					}
				} else if (ReturnRecogIDCard != 0) {
					str = getString(R.string.exception6) + ReturnRecogIDCard;
				}
				displayPopuWindow(getString(R.string.recogResult)
						+ ReturnRecogIDCard + "\n" + str);
			}

		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		// onDestory()方法中解除注册
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		if (time != null) {
			time.cancel();
			time = null;
		}
        closeCamera();
		super.onDestroy();

	}

	@Override
	protected void onStop() {
		super.onStop();
		System.out.println("onStop");
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		if (time != null) {
			time.cancel();
			time = null;
		}

		closeCamera();
		if (myListener != null) {
			sManager.unregisterListener(myListener);
		}
		sManager = null;
		myListener = null;
	}

	//
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// switch (keyCode) {
	// case KEYCODE_F1:
	// if (back_reset_text.getText().toString()
	// .equals(getString(R.string.returnbutlog))) {
	// finish();
	// isTakePic = false;
	// } else if (back_reset_text.getText().toString()
	// .equals(getString(R.string.reset_btn_string))) {
	// isTakePic = false;
	// if (nMainID == 1100 || nMainID == 1101) {
	// showTwoCutImageView();
	// } else {
	// showFourImageView();
	// }
	//
	// takepicbtn.setVisibility(View.VISIBLE);
	// take_recog_text.setText(R.string.takepic_btn_string);
	// backbtn.setVisibility(View.VISIBLE);
	// back_reset_text.setText(R.string.backbtn_string);
	// imageView.setImageDrawable(null);
	// resetbtn.setVisibility(View.INVISIBLE);
	// confirmbtn.setVisibility(View.INVISIBLE);
	// takepicbtn.setEnabled(true);
	// if (null != bitmap) {
	// bitmap.recycle();
	// bitmap = null;
	// }
	// camera.startPreview();
	// }
	//
	// break;
	//
	// case KEYCODE_F2:
	// if (light_text.getText().toString()
	// .equals(getString(R.string.light1_string))) {
	// lightoff.setVisibility(View.VISIBLE);
	// lighton.setVisibility(View.INVISIBLE);
	// light_text.setText(R.string.light2_string);
	// // 开启闪光灯
	// Camera.Parameters parameters = camera.getParameters();
	// parameters.set("flash-mode", "on");
	// camera.setParameters(parameters);
	// } else if (light_text.getText().toString()
	// .equals(getString(R.string.light2_string))) {
	//
	// lighton.setVisibility(View.VISIBLE);
	// lightoff.setVisibility(View.INVISIBLE);
	// light_text.setText(R.string.light1_string);
	// // 关闭闪光灯
	// Camera.Parameters parameters2 = camera.getParameters();
	// parameters2.set("flash-mode", "off");
	// camera.setParameters(parameters2);
	// }
	//
	// break;
	//
	// case KEYCODE_F3:
	// if (cut_text.getText().toString()
	// .equals(getString(R.string.OpenedCutting))) {
	// cuton.setVisibility(View.INVISIBLE);
	// cutoff.setVisibility(View.VISIBLE);
	// cut_text.setText(R.string.ClosedCutting);
	// cut = true;
	// } else if (cut_text.getText().toString()
	// .equals(getString(R.string.ClosedCutting))) {
	// cuton.setVisibility(View.VISIBLE);
	// cutoff.setVisibility(View.INVISIBLE);
	// cut_text.setText(R.string.OpenedCutting);
	// cut = false;
	// }
	// break;
	//
	// case KEYCODE_T:
	// if (take_recog_text.getText().toString()
	// .equals(getString(R.string.takepic_btn_string))
	// && !isTakePic) {
	// isTakePic = true;
	// takepicbtn.setEnabled(false);
	// takePicture();
	// } else if (take_recog_text.getText().toString()
	// .equals(getString(R.string.confirm_btn_string))
	// && isEffectClick()) {
	// isTakePic = false;
	// confirmbtn.setEnabled(false);
	// if (nMainID == 1100 || nMainID == 1101) {
	// hideTwoCutImageView();
	// } else {
	// hideFourImageView();
	// }
	// takepicbtn.setVisibility(View.VISIBLE);
	// backbtn.setVisibility(View.VISIBLE);
	// resetbtn.setVisibility(View.INVISIBLE);
	// confirmbtn.setVisibility(View.INVISIBLE);
	// imageView.setImageDrawable(null);
	// savephoto();
	// }
	// break;
	// }
	//
	// return super.onKeyDown(keyCode, event);
	// }

	private float getStableFloat(float x, float y, float z) {
		float move_Difference = 0.0f;
		float[] floatdata = { x, y, z };
		if (mlist.size() < ListMaxLen) {
			mlist.add(floatdata);
		} else {
			mlist.remove(0);
			mlist.add(floatdata);
		}
		if (mlist.size() < ListMaxLen) {
			return 0.1f;
		}
		float sumx = 0;
		float sumy = 0;
		float sumz = 0;
		int len = mlist.size();
		for (int i = 0; i < len; i++) {
			float[] dd = mlist.get(i);
			sumx += dd[0];
			sumy += dd[1];
			sumz += dd[2];
		}
		float avgx = sumx / len;
		float avgy = sumy / len;
		float avgz = sumz / len;
		for (int i = 0; i < len; i++) {
			float[] dd = mlist.get(i);
			move_Difference = (dd[0] - avgx) * (dd[0] - avgx) + (dd[1] - avgy)
					* (dd[1] - avgy) + (dd[2] - avgz) * (dd[2] - avgz);
		}
		return move_Difference;
	}

	public void autoFocus() {

		if (camera != null) {
			try {
				if (camera.getParameters().getSupportedFocusModes() != null
						&& camera.getParameters().getSupportedFocusModes()
								.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
					camera.autoFocus(new AutoFocusCallback() {
						@Override
						public void onAutoFocus(boolean success, Camera camera) {
							if (success) {

							} else {

							}
						}
					});
				} else {

					Toast.makeText(getBaseContext(),
							getString(R.string.unsupport_auto_focus),
							Toast.LENGTH_LONG).show();
				}

			} catch (Exception e) {
				e.printStackTrace();
				camera.stopPreview();
				camera.startPreview();
				takepicbtn.setEnabled(true);
				Toast.makeText(this, R.string.toast_autofocus_failure,
						Toast.LENGTH_SHORT).show();

			}
		}

	}

	public void closeCamera() {
		
			try {
				if (camera != null) {
					camera.setPreviewCallback(null);
					camera.stopPreview();
					camera.release();
					camera = null;
				}
			} catch (Exception e) {
				Log.i("TAG", e.getMessage());
			}
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		// TODO Auto-generated method stub
		
	}

	public void checkCameraParameters() {
		// 读取支持的预览尺寸
		Camera camera = null;
		try {
			camera = Camera.open();
			if (camera != null) {
				// 读取支持的预览尺寸,优先选择640后320
				Camera.Parameters parameters = camera.getParameters();
				List<Integer> SupportedPreviewFormats = parameters
						.getSupportedPreviewFormats();
				for (int i = 0; i < SupportedPreviewFormats.size(); i++) {
					System.out.println("PreviewFormats="
							+ SupportedPreviewFormats.get(i));
				}
				Log.i(TAG,
						"preview-size-values:"
								+ parameters.get("preview-size-values"));
				List<Camera.Size> previewSizes = splitSize(
						parameters.get("preview-size-values"), camera);// parameters.getSupportedPreviewSizes();

				// 冒泡排序算法 实现分辨率从小到大排列
				// 该算法以分辨率的宽度为准，如果宽度相等，则判断高度
				int tempWidth = 0;
				int tempHeight = 0;
				for (int i = 0; i < previewSizes.size(); i++) {
					for (int j = i + 1; j < previewSizes.size(); j++) {
						if (previewSizes.get(i).width > previewSizes.get(j).width) {

							tempWidth = previewSizes.get(i).width;
							tempHeight = previewSizes.get(i).height;
							previewSizes.get(i).width = previewSizes.get(j).width;
							previewSizes.get(i).height = previewSizes.get(j).height;
							previewSizes.get(j).width = tempWidth;
							previewSizes.get(j).height = tempHeight;

						} else if (previewSizes.get(i).width == previewSizes
								.get(j).width
								&& previewSizes.get(i).height > previewSizes
								.get(j).height) {
							tempWidth = previewSizes.get(i).width;
							tempHeight = previewSizes.get(i).height;
							previewSizes.get(i).width = previewSizes.get(j).width;
							previewSizes.get(i).height = previewSizes.get(j).height;
							previewSizes.get(j).width = tempWidth;
							previewSizes.get(j).height = tempHeight;
						}
					}
				}
				for (int i = 0; i < previewSizes.size(); i++) {
					System.out.println("宽度:" + previewSizes.get(i).width + "--"
							+ "高度:" + previewSizes.get(i).height);
				}
				// 冒泡排序算法
				// 该段程序主要目的是为了遵循:优先选择比640*480大的并且是最接近的而且是比例为4:3的原则编写的。
				for (int i = 0; i < previewSizes.size(); i++) {
					// 当预览宽度和高度分别大于640和480并且宽和高的比为4:3时。
					if (previewSizes.get(i).width > 640
							&& previewSizes.get(i).height > 480
							&& (((float) previewSizes.get(i).width / previewSizes
							.get(i).height) == (float) 4 / 3)) {
						isCatchPreview = true;
						WIDTH = previewSizes.get(i).width;
						HEIGHT = previewSizes.get(i).height;
						break;
					}
					// 如果在640*480前没有满足的值，WIDTH和HEIGHT就都为0，然后进行如下判断，看是否有640*480，如果有则赋值，如果没有则进行下一步验证。
					if (previewSizes.get(i).width == 640
							&& previewSizes.get(i).height == 480 && WIDTH < 640
							&& HEIGHT < 480) {
						isCatchPreview = true;
						WIDTH = 640;
						HEIGHT = 480;
					}
					if (previewSizes.get(i).width == 320
							&& previewSizes.get(i).height == 240 && WIDTH < 320
							&& HEIGHT < 240) {// 640 //480
						isCatchPreview = true;
						WIDTH = 320;
						HEIGHT = 240;
					}
				}
				Log.i(TAG, "isCatchPreview=" + isCatchPreview);

				// 读取支持的相机尺寸,优先选择1280后1600后2048
				List<Integer> SupportedPictureFormats = parameters
						.getSupportedPictureFormats();
				for (int i = 0; i < SupportedPictureFormats.size(); i++) {
					System.out.println("PictureFormats="
							+ SupportedPictureFormats.get(i));
				}
				Log.i(TAG,
						"picture-size-values:"
								+ parameters.get("picture-size-values"));
				List<Camera.Size> PictureSizes = splitSize(
						parameters.get("picture-size-values"), camera);// parameters.getSupportedPictureSizes();
				for (int i = 0; i < PictureSizes.size(); i++) {
//                    if (PictureSizes.get(i).width == 3264
//                            && PictureSizes.get(i).height == 1840) {
//                        // 优先选择小的照片分辨率
//                        isCatchPicture = true;
//                        srcwidth = 3264;
//                        srcheight = 1840;
//
//                    }
					if (PictureSizes.get(i).width == 2048
							&& PictureSizes.get(i).height == 1536) {
						// 优先选择小的照片分辨率
						if ((srcwidth == 0 && srcheight == 0)
								|| (srcwidth > 2048 && srcheight > 1536)) {
							isCatchPicture = true;
							srcwidth = 2048;
							srcheight = 1536;
						}

					}
					if (PictureSizes.get(i).width == 1600
							&& PictureSizes.get(i).height == 1200) {
						if ((srcwidth == 0 && srcheight == 0)
								|| (srcwidth > 1600 && srcheight > 1200)) {
							isCatchPicture = true;
							srcwidth = 1600;
							srcheight = 1200;
						}

					}
					if (PictureSizes.get(i).width == 1280
							&& PictureSizes.get(i).height == 960) {
						if ((srcwidth == 0 && srcheight == 0)
								|| (srcwidth > 1280 && srcheight > 960)) {
							isCatchPicture = true;
							srcwidth = 1280;
							srcheight = 960;
						}
					}
					if (PictureSizes.get(i).width == 640
							&& PictureSizes.get(i).height == 480) {
						if ((srcwidth == 0 && srcheight == 0)
								|| (srcwidth > 640 && srcheight > 480)) {
							isCatchPicture = true;
							srcwidth = 640;
							srcheight = 480;
						}
					}
				}
				Log.i(TAG, "isCatchPicture=" + isCatchPicture);
			}
			camera.release();
			camera = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (camera != null) {
				try {
					camera.release();
					camera = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private ArrayList<Camera.Size> splitSize(String str, Camera camera) {
		if (str == null)
			return null;
		StringTokenizer tokenizer = new StringTokenizer(str, ",");
		ArrayList<Camera.Size> sizeList = new ArrayList<Camera.Size>();
		while (tokenizer.hasMoreElements()) {
			Camera.Size size = strToSize(tokenizer.nextToken(), camera);
			if (size != null)
				sizeList.add(size);
		}
		if (sizeList.size() == 0)
			return null;
		return sizeList;
	}

	private Camera.Size strToSize(String str, Camera camera) {
		if (str == null)
			return null;
		int pos = str.indexOf('x');
		if (pos != -1) {
			String width = str.substring(0, pos);
			String height = str.substring(pos + 1);
			return camera.new Size(Integer.parseInt(width),
					Integer.parseInt(height));
		}
		return null;
	}
}