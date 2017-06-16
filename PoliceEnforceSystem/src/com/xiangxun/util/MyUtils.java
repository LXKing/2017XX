/**
 * 
 */
package com.xiangxun.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xiangxun.common.LocalQueue;
import com.xiangxun.request.Api;

/**
 * @ClassName: MyUtils
 * @Description: 工具集合
 * @date: 2015年01月20日 下午2:47:43
 */
@SuppressLint("SimpleDateFormat")
public class MyUtils {
	/**
	 * 
	 * @Title:
	 * @Description: 持久化存储对象
	 * @param: @param dir
	 * @param: @param fileName
	 * @param: @param ob
	 * @param: @param con
	 * @return: void
	 * @throws
	 */
	public static void saveToDisk(File dir, String fileName, Object ob) {
		FileOutputStream fo = null;
		ObjectOutputStream out = null;
		try {
			if (!dir.exists()) {
				dir.mkdirs();
			}
			fo = new FileOutputStream(new File(dir, fileName));
			out = new ObjectOutputStream(fo);
			out.writeObject(ob);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (fo != null) {
					fo.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 
	 * @Title:
	 * @Description: 指定路径获取序列化文件可能返回null
	 * @param: @param dir
	 * @param: @param fileName
	 * @param: @return
	 * @return: Object
	 * @throws
	 */
	public static Object getFromDisk(File dir, String fileName) {
		FileInputStream fi = null;
		ObjectInputStream in = null;
		try {
			fi = new FileInputStream(new File(dir, fileName));
			in = new ObjectInputStream(fi);
			Object ob = in.readObject();
			return ob;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (fi != null) {
					fi.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取指定Bitmap的字节数组
	 * 
	 * @param bm
	 *            源图
	 * @return 返回图像的字节数组
	 */
	public static byte[] Bitmap2Bytes(Bitmap bm) {
		byte[] bytes = null;
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			// 将Bitmap压缩成PNG编码，质量为100%存储
			bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
			bytes = baos.toByteArray();
		} catch (Exception e) {

		} finally {
			try {
				if (baos != null) {
					baos.close();
					baos = null;
				}
			} catch (Exception e) {
			}
		}
		return bytes;
	}

	/**
	 * 
	 * @Title:
	 * @Description: 字节转位图
	 * @param: @param b
	 * @param: @return
	 * @return: Bitmap
	 * @throws
	 */
	public static Bitmap Bytes2Bimap(byte[] b, int width, int height) {
		if (b != null && b.length != 0) {
			try {
				final BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeByteArray(b, 0, b.length, options);
				// Calculate inSampleSize
				options.inSampleSize = calculateInSampleSize(options, width, height);
				// Decode bitmap with inSampleSize set
				options.inJustDecodeBounds = false;
				Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length, options);
				return bitmap;
			} catch (Exception e) {
				return null;
			}
		} else {
			return null;
		}
	}

	// 获取本地相册的action
	public static Intent getPhotoIntent() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("image/*");
		return intent;
	}

	// 获取拍照功能的action
	public static Intent getCameraIntent(String dir, String imageName) {
		File file = new File(dir);
		if (!file.exists()) {
			file.mkdirs();
		}
		File tempFile = new File(dir, imageName);
		Uri imageUri = Uri.fromFile(tempFile);
		Intent intent = new Intent();
		intent.setAction("android.media.action.IMAGE_CAPTURE");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		return intent;

	}

	/*
	 * 获取图片路径 有一种情况 图片与google同步 相册可以看到这张图片 但实际是不存在这张图片的 判断提示用户
	 */
	public static String selectImage(Context context, Intent data) {
		Uri selectedImage = data.getData();
		if (selectedImage != null) {
			String uriStr = selectedImage.toString();
			String path = uriStr.substring(10, uriStr.length());
			if (path.startsWith("com.sec.android.gallery3d")) {
				Toast.makeText(context, "该图片不可用", Toast.LENGTH_SHORT).show();
				return null;
			}
		}
		String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
		cursor.moveToFirst();
		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		String picturePath = cursor.getString(columnIndex);
		cursor.close();
		return picturePath;
	}

	// 刷新资源库文件
	public static void fileScan(String file, Context con) {
		Uri data = Uri.parse("file://" + file);
		con.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, data));
	}

	/**
	 * 
	 * @Title:
	 * @Description: 从指定文件获取压缩图片
	 * @param: @param dir
	 * @param: @param fileName
	 * @param: @param con
	 * @param: @return
	 * @return: Bitmap
	 * @throws
	 */
	public static Bitmap getBigFitSizeImg(String filePath, Context con) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, 500, 500);
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
		// saveMyBitmap(filePath, bitmap, con);// 等比例缩放后后再质量压缩保存压缩图覆盖sd卡
		return bitmap;
	}

	public static Bitmap getSmallImg(String filePath, Context con) {
		boolean isBig = filePath.contains(LocalQueue.LINING_BIG);
		filePath = filePath.replaceAll(LocalQueue.LINING_BIG, "");
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		// Calculate inSampleSize
		if (isBig) {
			options.inSampleSize = calculateInSampleSize(options, 300, 300);
		} else {
			options.inSampleSize = calculateInSampleSize(options, 150, 150);
		}
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
		return bitmap;

	}

	// 计算图片的缩放值
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

	// 将bitmap保存到sd卡
	public static void saveMyBitmap(String filePath, Bitmap mBitmap, Context con) {

		if (mBitmap == null) {
			return;
		}
		String dir = filePath.substring(0, filePath.lastIndexOf("/") + 1);
		String fileName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
		File file = new File(dir);
		if (!file.exists())
			file.mkdir();
		File f = new File(dir, fileName);
		try {
			f.createNewFile();
		} catch (IOException e) {
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int options = 80;// 个人喜欢从80开始,
		mBitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
		while (baos.toByteArray().length / 1024 > 100) {
			baos.reset();
			options -= 10;
			mBitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
		}
		try {
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(baos.toByteArray());
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 将字节流图片保存到sd卡
	@SuppressLint("SimpleDateFormat")
	public static String saveToSDCard(byte[] data, int direction) throws IOException {
		Date date = new Date();
		data = rotataData(data, direction);
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); // 格式化时间
		String filename = format.format(date) + ".jpg";
		File fileFolder = new File(Api.xXPublishPictureDir);
		if (!fileFolder.exists()) {// 如果目录不存在，则创建一个名为"finger"的目录
			fileFolder.mkdir();
		}
		File jpgFile = new File(fileFolder, filename);
		FileOutputStream outputStream = new FileOutputStream(jpgFile); // 文件输出流
		outputStream.write(data); // 写入sd卡中
		outputStream.close(); // 关闭输出流
		return filename;
	}

	/**
	 * @Title:
	 * @Description: 将字节数组图片旋转后返回字节数据
	 * @param:
	 * @return: void
	 * @throws
	 */

	public static byte[] rotataData(byte[] data, int direction) {
		Bitmap bitmap = Bytes2Bimap(data, 480, 640);
		Matrix matrix = new Matrix();
		matrix.setRotate(getPreviewDegree(direction));
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return Bitmap2Bytes(bitmap);

	}

	public static Bitmap rotataBitmap(Bitmap bitmap, int i) {
		Matrix matrix = new Matrix();
		matrix.setRotate(i);
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return bitmap;
	}

	public static int getPreviewDegree(int direction) {
		int degree = 0;
		// 根据手机的方向计算相机预览画面应该选择的角度
		switch (direction) {
		case 0:
			degree = 90;
			break;
		case 1:
			degree = 180;
			break;
		case 2:
			degree = 270;
			break;
		case 3:
			degree = 0;
			break;
		}
		return degree;

	}

	/*** 关闭虚拟键盘 */
	public static void closeInputMethodWindow(Context context, View view) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null && view != null) {
			view.setFocusable(true);
			view.setFocusableInTouchMode(true);
			view.requestFocus();
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	public static String getTel(Context con) {
		TelephonyManager tm = (TelephonyManager) con.getSystemService(Context.TELEPHONY_SERVICE);
		String tel = tm.getLine1Number();
		if (tel == null || tel.equals("")) {
			tel = "";
		} else if (tel.length() > 11) {
			tel = tel.substring(tel.length() - 11, tel.length());
		}
		return tel;
	}

	/**
	 * 获取应用版本号
	 * 
	 * @param context
	 * @return
	 */
	public static String getVersionName(Context context) {
		String versionName = "1.0";
		try {
			PackageInfo mPackageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			versionName = mPackageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;
	}

	/**
	 * 用第三方工具打开文件，支持doc、pdf、png、jpg/jpeg、txt
	 * 
	 * @param path
	 * @param ct
	 */
	@SuppressLint("DefaultLocale")
	public static void openFile(String path, Context ct) {
		String str = path.substring(path.lastIndexOf(".") + 1);
		str = str.toLowerCase();
		if (str.equals("doc")) {
			openWordFile(path, ct);
		} else if (str.equals("pdf")) {
			openPdfFile(path, ct);
		} else if (str.equals("png")) {
			openPngFile(path, ct);
		} else if (str.equals("jpeg") || str.equals("jpg")) {
			openJpegFile(path, ct);
		} else if (str.equals("txt")) {
			openTextFile(path, false, ct);
		} else {
			new AlertDialog.Builder(ct).setTitle("提示").setMessage("请安装阅读软件后查看附件信息.").setPositiveButton("确定", new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			}).show();
		}
	}

	// 用于打开Word文件
	public static void openWordFile(String path, Context context) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(path));
		intent.setDataAndType(uri, "application/msword");
		context.startActivity(intent);
	}

	// 用于打开JPEG文件
	public static void openJpegFile(String path, Context context) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(path));
		intent.setDataAndType(uri, "image/jpeg");
		context.startActivity(intent);
	}

	// 用于打开PNG文件
	public static void openPngFile(String path, Context context) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(path));
		intent.setDataAndType(uri, "image/png");
		context.startActivity(intent);
	}

	// 用于打开TXT文件
	public static void openTextFile(String path, boolean paramBoolean, Context context) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (paramBoolean) {
			Uri uri1 = Uri.parse(path);
			intent.setDataAndType(uri1, "text/plain");
		} else {
			Uri uri2 = Uri.fromFile(new File(path));
			intent.setDataAndType(uri2, "text/plain");
		}
		context.startActivity(intent);
	}

	// android获取一个用于打开PDF文件的intent
	public static void openPdfFile(String path, Context context) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(path));
		intent.setDataAndType(uri, "application/pdf");
		context.startActivity(intent);
	}

	/**
	 * 能进入此方法的前提是sd卡已经加载 注：调用前需判断SD卡是否存在
	 * 
	 * @return sd卡剩余空间
	 */
	@SuppressWarnings("deprecation")
	public static long getSdRemainRoom() {
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;

	}

	/**
	 * 获取手机SIM卡序列号
	 */
	public static String getSimSerialNumber(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getSimSerialNumber();
	}

	/**
	 * 创建一个保存sim卡序列的share
	 */
	public static void ShareSimSerialNumber(Context context, String num) {
		SharedPreferences SimSerialNum = context.getSharedPreferences("SimSerialNum", 0);
		SimSerialNum.edit().putString("num", num).commit();
	}

	/**
	 * 判断手机是否联网
	 */
	public static Boolean isNetWork(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager == null || connectivityManager.getActiveNetworkInfo() == null) {
			return false;
		}
		return connectivityManager.getActiveNetworkInfo().isAvailable();
	}

	// 获取手机网关
	public static String getTelRoute(Context con) {
		TelephonyManager telManager = (TelephonyManager) con.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = telManager.getSubscriberId();
		if (imsi != null) {
			if (imsi.startsWith("46000") || imsi.startsWith("46002")) {// 移动
				return "106575587799";
			} else if (imsi.startsWith("46001")) { // 联通
				return "106550200197";
			} else if (imsi.startsWith("46003")) { // 电信
				return "";
			}

		}
		return "";

	}

	// 讲日期字符串转成日期对象 如“2014-12-13”转成正常的日期
	public static Date String2Date(String num) {
		DateFormat df = DateFormat.getDateInstance();
		df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date = df.parse(num);
			return date;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date();
	}

	// 和今天对比
	public static String compareWithToday(String str) {
		Date date = String2Date(str);
		Calendar d1 = Calendar.getInstance();
		d1.setTime(date);
		Calendar d2 = Calendar.getInstance();
		d2.setTime(new Date());
		int days = d1.get(Calendar.DAY_OF_YEAR) - d2.get(Calendar.DAY_OF_YEAR);
		int y2 = d2.get(Calendar.YEAR);
		if (d1.get(Calendar.YEAR) != y2) {
			d1 = (Calendar) d1.clone();
			do {
				days += d1.getActualMaximum(Calendar.DAY_OF_YEAR);// 得到当年的实际天数
				d1.add(Calendar.YEAR, 1);
			} while (d1.get(Calendar.YEAR) != y2);
		}
		if (days == 0) {
			return "今天";
		} else if (days > 0) {// 以后
			if (days == 1) {
				return "明天";
			} else if (days == 2) {
				return "后天";
			} else if (days == 3) {
				return "大后天";
			} else {
				return days + "天后";
			}
		} else if (days < 0) {
			if (days == -1) {
				return "昨天";
			} else if (days == -2) {
				return "前天";
			} else if (days == -3) {
				return "大前天";
			} else {
				return Math.abs(days) + "天前";
			}
		}
		return "今天";
	}

	// 选择今天明天转成日期字符串如“2014-12-13”
	public static String getDate(String s) {
		DateFormat df = DateFormat.getDateInstance();
		df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();

		if (s.equals("今天")) {
			String str = df.format(date);
			return str;
		} else if (s.equals("明天")) {
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(date);
			calendar.add(Calendar.DATE, 1);
			date = calendar.getTime();
			String dateString = df.format(date);
			return dateString;

		} else if (s.equals("后天")) {
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(date);
			calendar.add(Calendar.DATE, 2);
			date = calendar.getTime();
			String dateString = df.format(date);
			return dateString;

		} else if (s.equals("大后天")) {
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(date);
			calendar.add(Calendar.DATE, 3);
			date = calendar.getTime();
			String dateString = df.format(date);
			return dateString;

		}
		return df.format(date);

	}

	// string 转 int 因为有的价钱是类似100.0格式的 直接转int会报错 所以先转double 再转Int
	public static int string2Int(String str) {
		if (!str.equals("") || !str.equals("null")) {
			double num = Double.parseDouble(str);
			return (int) num;
		} else {
			return 0;
		}

	}

	/*
	 * date 转换成String
	 */
	public static String Date2String(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(date);
	}

	/**
	 * 
	 * @Title:
	 * @Description: 得到当前版本号
	 * @param: @return
	 * @return: String
	 * @throws
	 */
	public static String getCurrentVersion(Context mContext) {
		PackageManager pm = mContext.getPackageManager();
		try {
			PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), 0);
			return pi.versionName;

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return "xxx";
	}

	/**
	 * 
	 * @Title:
	 * @Description: 控制文本输入长度事件
	 * @param: @param editText
	 * @param: @param textView
	 * @param: @param max_length
	 * @return: void
	 * @throws
	 */
	public static void lengthFilter(final EditText editText, final TextView textView, final int max_length, final String lastStr) {
		editText.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void afterTextChanged(Editable s) {
				String str = s.toString();
				if (str.length() != 0 && str.trim().length() == 0) {
					editText.getText().clear();
					return;
				}
				int len = str.length();
				StringBuilder sb = new StringBuilder();
				if (len <= max_length) {
					sb.append(max_length - len);
					sb.append(lastStr);
					textView.setText(sb.toString());
				} else {
					int space = s.length() - len;
					s.delete(max_length + space, s.length());
					editText.setText(s);
					editText.setSelection(max_length + space);// 设置光标在最后
				}
			}

		});
	}

	/**
	 * 
	 * @Title:
	 * @Description: 控制文本输入长度事件
	 * @param: @param editText
	 * @param: @param textView
	 * @param: @param max_length
	 * @return: void
	 * @throws
	 */
	public static void lengthFilterDesc(final EditText editText, final TextView textView, final int max_length, final String lastStr) {
		textView.setText(String.valueOf(editText.length()).concat(lastStr));
		editText.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void afterTextChanged(Editable s) {
				String str = s.toString().replaceAll(" +", " ");
				int len = str.length();
				StringBuilder sb = new StringBuilder();
				if (len <= max_length) {
					sb.append(len);
					sb.append(lastStr);
					textView.setText(sb.toString());
				} else {
					int space = s.length() - len;
					s.delete(max_length + space, s.length());
					editText.setText(s);
					editText.setSelection(max_length + space);// 设置光标在最后
				}
			}

		});
	}
	
    /**
     * 递归删除文件和文件夹
     * 
     * @param file
     *            要删除的根目录
     */ 
    public static void deleteFile(File file) { 
        if (file.exists() == false) { 
            return; 
        } else { 
            if (file.isFile()) { 
                file.delete(); 
                return; 
            } 
            if (file.isDirectory()) { 
                File[] childFile = file.listFiles(); 
                if (childFile == null || childFile.length == 0) { 
                    file.delete(); 
                    return; 
                } 
                for (File f : childFile) { 
                    deleteFile(f); 
                } 
                file.delete(); 
            } 
        } 
    } 

}
