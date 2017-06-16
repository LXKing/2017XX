package com.xiangxun.wtone;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.xiangxun.activity.R;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import wintone.idcard.android.RecogParameterMessage;
import wintone.idcard.android.RecogService;
import wintone.idcard.android.ResultMessage;

import com.xiangxun.activity.R;
import com.xiangxun.cfg.SystemCfg;
import com.xiangxun.widget.MsgToast;

//识别Activity调用类及结果显示页面
public class IdcardRunner extends Activity {
	public static final String TAG = "IdcardRunner";
	public static final String PATH = Environment.getExternalStorageDirectory()
			.toString() + "/AndroidWT";
	private Button mbutquit;
	private String selectPath;
	private int nMainID = 0;
	private Boolean cutBoolean = true;
	private String resultFileNameString = "";
	private RecogService.recogBinder recogBinder;
	private String str = "";
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏
		// 调用识别
		Intent intentget = this.getIntent();
		selectPath = intentget.getStringExtra("path");
		cutBoolean = intentget.getBooleanExtra("cut", true);
		nMainID = intentget.getIntExtra("nMainID", 0);
		if (selectPath != null && !selectPath.equals("")) {
			int index = selectPath.lastIndexOf("/");
			resultFileNameString = (String) selectPath.subSequence(index + 1,
					selectPath.length());
			// 当设备分辨率不支持以上三种分辨率时，程序会调用系统相机进行activity识别，但以下几种均要进行service识别
			if (nMainID == 1020 || nMainID == 1033 || nMainID == 1034
					|| nMainID == 1036) {
//				setContentView(R.layout.idcardrunner);
//				editResult = (EditText) this.findViewById(R.id.edit_file);
//				Button takePic = (Button) this.findViewById(R.id.takePic);
//				Button backIndex = (Button) this.findViewById(R.id.backIndex);
//				// 黄震start
//				takePic.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View arg0) {
//
//
//						if (readPreferences("", "WIDTH") != 0
//								&& readPreferences("", "HEIGHT") != 0
//								&& readPreferences("", "srcwidth") != 0
//								&& readPreferences("", "srcheight") != 0) {
//							Intent intent = new Intent();
//							if (nMainID == 1020 || nMainID == 1033
//									|| nMainID == 1034 || nMainID == 1036) {
//								intent.setClass(IdcardRunner.this,
//										CameraIDCardNoActivity.class);
//							} else {
//								intent.setClass(IdcardRunner.this,
//										CameraActivity.class);
//							}
//							intent.putExtra("WIDTH",
//									readPreferences("", "WIDTH"));
//							intent.putExtra("HEIGHT",
//									readPreferences("", "HEIGHT"));
//							intent.putExtra("srcwidth",
//									readPreferences("", "srcwidth"));
//							intent.putExtra("srcheight",
//									readPreferences("", "srcheight"));
//							intent.putExtra("nMainID", nMainID);
//							IdcardRunner.this.finish();
//							startActivity(intent);
//
//						} else {
//
//							String partpath = Environment
//									.getExternalStorageDirectory() + "/wtimage";
//							File dir = new File(partpath);
//							if (!dir.exists()) {
//								dir.mkdir();
//							}
//							Date date = new Date();
//							selectPath = partpath + "/idcard" + date.getTime()
//									+ ".jpg";
//							Intent takePictureFromCameraIntent = new Intent(
//									MediaStore.ACTION_IMAGE_CAPTURE);
//							takePictureFromCameraIntent.putExtra(
//									android.provider.MediaStore.EXTRA_OUTPUT,
//									Uri.fromFile(new File(selectPath)));
//							startActivityForResult(takePictureFromCameraIntent,
//									3);
//						}
//					}
//				});
//
//				// 黄震end
//				backIndex.setOnClickListener(new OnClickListener() {
//
//					@Override
//					public void onClick(View arg0) {
//						Intent intent = new Intent();
//						intent.setClass(IdcardRunner.this, ImageChooser.class);
//						IdcardRunner.this.finish();
//						startActivity(intent);
//					}
//				});
//				new Thread() {
//					public void run() {
//						RecogService.isRecogByPath = true;
//						Intent recogIntent = new Intent(IdcardRunner.this,
//								RecogService.class);
//						bindService(recogIntent, recogConn,
//								Service.BIND_AUTO_CREATE);
//					};
//				}.start();
			} else {
				try {
					RecogService.isRecogByPath = true;
					String logopath = "";
					// String logopath = getSDPath() + "/photo_logo.png";
					Intent intent = new Intent("wintone.idcard");
					Bundle bundle = new Bundle();
					int nSubID[] = null;// {0x0001};
					bundle.putBoolean("isGetRecogFieldPos", false);// 是否获取识别字段在原图的位置信息，默认为假，不获取
																	// 必须在核心裁好的图上裁切才行
					bundle.putString("cls", "checkauto.com.IdcardRunner");
					bundle.putInt("nTypeInitIDCard", 0); // 保留，传0即可
					bundle.putString("lpFileName", selectPath);// 指定的图像路径
					bundle.putString("cutSavePath", "");// 裁切图片的存储路径
					bundle.putInt("nTypeLoadImageToMemory", 0);// 0不确定是哪种图像，1可见光图，2红外光图，4紫外光图
					// if (nMainID == 1000) {
					// nSubID[0] = 3;
					// }
					bundle.putInt("nMainID", nMainID); // 证件的主类型。6是行驶证，2是二代证，这里只可以传一种证件主类型。每种证件都有一个唯一的ID号，可取值见证件主类型说明
					bundle.putIntArray("nSubID", nSubID); // 保存要识别的证件的子ID，每个证件下面包含的子类型见证件子类型说明。nSubID[0]=null，表示设置主类型为nMainID的所有证件。
					// bundle.putBoolean("GetSubID", true);
					// //GetSubID得到识别图像的子类型id
					// bundle.putString("lpHeadFileName",
					// "/mnt/sdcard/head.jpg");//保存路径名，后缀只能为jpg、bmp、tif
					bundle.putBoolean("GetVersionInfo", true); // 获取开发包的版本信息


					// 读设置到文件里的sn
					File file = new File(PATH);
					String snString = null;
					if (file.exists()) {
						String filePATH = PATH + "/IdCard.sn";
						File newFile = new File(filePATH);
						if (newFile.exists()) {
							BufferedReader bfReader = new BufferedReader(
									new FileReader(newFile));
							snString = bfReader.readLine().toUpperCase();
							bfReader.close();
						} else {
							bundle.putString("sn", "");
						}
						if (snString != null && !snString.equals("")) {
							bundle.putString("sn", snString);
							// String string = (String) bundle.get("sn");
							// Toast.makeText(getApplicationContext(),
							// "snString=="+string, 3000).show();
						} else {
							bundle.putString("sn", "");
						}
					} else {
						bundle.putString("sn", "");
					}
					// bundle.putString("datefile",
					// "assets");//Environment.getExternalStorageDirectory().toString()+"/wtdate.lsc"
					bundle.putString("devcode", Devcode.DEVCODE);
					// bundle.putBoolean("isCheckDevType", true); // 强制验证设备型号开关
					// bundle.putString("versionfile",
					// "assets");//Environment.getExternalStorageDirectory().toString()+"/wtversion.lsc"
					// bundle.putString("sn", "XS4XAYRWEFRY248YY4LHYY178");
					// //序列号激活方式,XS4XAYRWEFRY248YY4LHYY178已使用
					// bundle.putString("server",
					// "http://192.168.0.36:8080");//http://192.168.0.36:8888
					// bundle.putString("authfile", ""); // 文件激活方式 //
					// /mnt/sdcard/AndroidWT/357816040594713_zj.txt
					bundle.putString("logo", logopath); // logo路径，logo显示在识别等待页面右上角
					bundle.putInt("nProcessType", 7);//2－旋转，3－裁切+旋转，4－倾斜校正，5－裁切+倾斜校正，6－旋转+倾斜校正，7－裁切+旋转+倾斜校正
					bundle.putInt("nSetType", 1);// nSetType: 0－取消操作，1－设置操作
					bundle.putBoolean("isCut", cutBoolean); // 如不设置此项默认自动裁切
					bundle.putBoolean("isSaveCut", false);// 是否保存裁切图片
					bundle.putString("returntype", "withvalue");// 返回值传递方式withvalue带参数的传值方式（新传值方式）
					intent.putExtras(bundle);
					startActivityForResult(intent, 8);
				} catch (Exception e) {
					Toast.makeText(
							getApplicationContext(),
							getString(R.string.noFoundProgram)
									+ "wintone.idcard", 0).show();
				}
			}
		}


	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(TAG, "onActivityResult");
		String ReturnLPFileName = null;

		if (requestCode == 8 && resultCode == RESULT_OK) {
			// 读识别返回值
			int ReturnAuthority = data.getIntExtra("ReturnAuthority", -100000);// 取激活状态
			System.out.println("ReturnAuthority:" + ReturnAuthority + "---"
					+ "nMainID:" + nMainID);
			int ReturnInitIDCard = data
					.getIntExtra("ReturnInitIDCard", -100000);// 取初始化返回值
			int ReturnLoadImageToMemory = data.getIntExtra(
					"ReturnLoadImageToMemory", -100000);// 取读图像的返回值
			int ReturnRecogIDCard = data.getIntExtra("ReturnRecogIDCard",
					-100000);// 取识别的返回值
			System.out.println("识别核心版本号:"
					+ data.getStringExtra("ReturnGetVersionInfo"));
			// //取版本信息，如果定义了GetVersionInfo为true才能取版本信息
			// System.out.println("ReturnGetSubID" +
			// intentget.getIntExtra("ReturnGetSubID",-1));
			// //取识别图像的子类型id，如果定义了GetSubID 为true才能取识别图像的子类型id
			// System.out.println("" +
			// intentget.getIntExtra("ReturnSaveHeadImage",-1));
			// //取SaveHeadImage返回值，如果定义了lpHeadFileName的路径才能得到头像
			// System.out.println("ReturnUserData:" +
			// intentget.getStringExtra("ReturnUserData"));
			Log.i(TAG,
					"ReturnLPFileName:"
							+ data.getStringExtra("ReturnLPFileName"));

			if (ReturnAuthority == 0 && ReturnInitIDCard == 0
					&& ReturnLoadImageToMemory == 0 && ReturnRecogIDCard > 0) {
				// System.out.println("接收结果");
				String result = "";
				String[] fieldname = (String[]) data
						.getSerializableExtra("GetFieldName");
				String[] fieldvalue = (String[]) data
						.getSerializableExtra("GetRecogResult");
//				ResultMessage resultMessage = (ResultMessage) data
//						.getSerializableExtra("textNamePosition");
//				List<int[]> textNamePosition = resultMessage.textNamePosition;
//				String time = data.getStringExtra("ReturnTime");
//				ReturnLPFileName = data.getStringExtra("ReturnLPFileName");
//				if (null != fieldname) {
//					int count = fieldname.length;
//					for (int i = 0; i < count; i++) {
//						if (fieldname[i] != null) {
//							result += fieldname[i] + ":" + fieldvalue[i]
//									+ ";\n";
//						}
//
//					}
//				}
//				str = "\n" + getString(R.string.recogResult1) + " \n"
//						+ getString(R.string.idcardType) + ReturnRecogIDCard
//						+ "\n" + result;
//
//				MsgToast.geToast().setMsg("1 " + str);
				Intent intent = new Intent();
				intent.putExtra("ReturnAuthority", 0);
				intent.putExtra("ReturnInitIDCard", 0);
				intent.putExtra("ReturnLoadImageToMemory", 0);
				intent.putExtra("ReturnRecogIDCard", 1);
				intent.putExtra("GetFieldName",fieldname );
				intent.putExtra("GetRecogResult",fieldvalue );
				setResult(Activity.RESULT_OK, intent);
				IdcardRunner.this.finish();
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
				MsgToast.geToast().setMsg("2 " + getString(R.string.recogResult1) + " \n"
						+ str + "\n");
			}

		}
	}

	public String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
		}
		return sdDir.toString();

	}

	// 跳转后结束本Activity
	@Override
	protected void onStop() {
		super.onStop();
		// finish();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// land
		} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			// port
		}
	}

	protected int readPreferences(String perferencesName, String key) {
		SharedPreferences preferences = getSharedPreferences(perferencesName,
				MODE_PRIVATE);

		int result = preferences.getInt(key, 0);
		return result;
	}

	// 识别验证
	public ServiceConnection recogConn = new ServiceConnection() {

		public void onServiceDisconnected(ComponentName name) {
			recogBinder = null;
		}

		public void onServiceConnected(ComponentName name, IBinder service) {

			recogBinder = (RecogService.recogBinder) service;
			RecogParameterMessage rpm = new RecogParameterMessage();
			rpm.nTypeInitIDCard = 0;
			rpm.nTypeLoadImageToMemory = 0;
			rpm.triggertype = 0;
			rpm.nMainID = nMainID;
			rpm.nSubID = null;
			rpm.GetSubID = true;
			rpm.lpHeadFileName = "";
			rpm.GetVersionInfo = true;
			rpm.logo = "";
			rpm.userdata = "";
			rpm.lpFileName = selectPath;
			rpm.sn = "";
			rpm.authfile = "";
			rpm.isCut = true;// 是否裁切
			rpm.isSaveCut = false;// 是否打开存储裁切图片的操作
			rpm.cutSavePath = "";// 裁切图片的存储路径
			rpm.isGetRecogFieldPos = false;// 是否打开获取字段在图片上的坐标位置
			if (nMainID == 1020) {
				// 护照机读码识别
				rpm.array = new int[4];
				rpm.array[0] = 0;
				rpm.array[1] = 0;
				rpm.array[2] = 0;
				rpm.array[3] = 0;
				// 手动拍照时将rpm.ncheckmrz赋值为1
				rpm.ncheckmrz = 1;
			}


			try {
				ResultMessage resultMessage;
				resultMessage = recogBinder.getRecogResult(rpm);
				if (resultMessage.ReturnAuthority == 0
						&& resultMessage.ReturnInitIDCard == 0
						&& resultMessage.ReturnLoadImageToMemory == 0
						&& resultMessage.ReturnRecogIDCard > 0) {
					String iDResultString = "";
					String[] GetFieldName = resultMessage.GetFieldName;
					String[] GetRecogResult = resultMessage.GetRecogResult;
					List<int[]> textNamePosition = resultMessage.textNamePosition;
					// 将图片按照坐标值截取操作，以第一个数组为例start
					// Bitmap
					// bitmap=BitmapFactory.decodeFile(selectPath+".jpg");
					// bitmap=Bitmap.createBitmap(bitmap,
					// textNamePosition.get(1)[0], textNamePosition.get(1)[1],
					// textNamePosition.get(1)[2]-textNamePosition.get(1)[0],
					// textNamePosition.get(1)[3]-textNamePosition.get(1)[1]);
					// File myCaptureFile = new File(PATH+"2.jpg");
					//
					// BufferedOutputStream bos = new BufferedOutputStream(
					// new FileOutputStream(myCaptureFile));
					// /* 采用压缩转档方法 */
					// bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
					// /* 调用flush()方法，更新BufferStream */
					// bos.flush();
					// /* 结束OutputStream */
					// bos.close();
					// if(bitmap!=null)
					// {
					// if(!bitmap.isRecycled()){
					// bitmap.recycle();
					// }
					// bitmap=null;
					//
					// }
					// 将图片按照坐标值截取操作，以第一个数组为例end
					/* switch (nMainID) { */
					/* case 1102: */
					// 身份证号码识别
					for (int i = 1; i < GetRecogResult.length; i++) {// 只显示身份证号码
						if (GetFieldName[i] != null
								&& !GetFieldName[i].equals("")
								&& !GetFieldName.equals("null")) {
							iDResultString += GetFieldName[i] + ":"
									+ GetRecogResult[i] + "\n";
						}
					}
					MsgToast.geToast().setMsg("3 " + iDResultString);

				} else {
					String str = "";
					if (resultMessage.ReturnAuthority == -100000) {
						str = getString(R.string.exception)
								+ resultMessage.ReturnAuthority;
					} else if (resultMessage.ReturnAuthority != 0) {
						str = getString(R.string.exception1)
								+ resultMessage.ReturnAuthority;
					} else if (resultMessage.ReturnInitIDCard != 0) {
						str = getString(R.string.exception2)
								+ resultMessage.ReturnInitIDCard;
					} else if (resultMessage.ReturnLoadImageToMemory != 0) {
						if (resultMessage.ReturnLoadImageToMemory == 3) {
							str = getString(R.string.exception3)
									+ resultMessage.ReturnLoadImageToMemory;
						} else if (resultMessage.ReturnLoadImageToMemory == 1) {
							str = getString(R.string.exception4)
									+ resultMessage.ReturnLoadImageToMemory;
						} else {
							str = getString(R.string.exception5)
									+ resultMessage.ReturnLoadImageToMemory;
						}
					} else if (resultMessage.ReturnRecogIDCard != 0) {
						str = getString(R.string.exception6)
								+ resultMessage.ReturnRecogIDCard;
					}
					MsgToast.geToast().setMsg("4 " + str);
				}
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(getApplicationContext(),
						getString(R.string.recognized_failed),
						Toast.LENGTH_SHORT).show();

			} finally {
				if (recogBinder != null) {
					unbindService(recogConn);
				}
			}

		}
	};

}
