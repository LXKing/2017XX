package com.xiangxun.util;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.util.LruCache;
import android.text.format.Formatter;
import android.widget.ImageView;

import com.xiangxun.app.XiangXunApplication;
import com.xiangxun.common.BitmapCache;

public class ImageLoadInNative {

	private static LruCache<String, Bitmap> mCache = BitmapCache.getInstance().getmCache();

	public static String getAvailMemory() {// 获取android当前可用内存大小

		ActivityManager am = (ActivityManager) XiangXunApplication.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);
		// mi.availMem; 当前系统的可用内存
		return Formatter.formatFileSize(XiangXunApplication.getInstance(), mi.availMem);// 将获取的内存大小规格化
	}

	public static Bitmap get(Resources res, int resId, int reqWidth, int reqHeight) {
		Bitmap bitmap = mCache.get(getCacheKey(resId));
		Logger.i(getAvailMemory());
		if (bitmap != null) {
			Logger.i("Memory");
			return bitmap;
		} else {
			Logger.i("new");
			bitmap = decodeSampledBitmapFromResource(res, resId, reqWidth, reqHeight);
			mCache.put(getCacheKey(resId), bitmap);
			return bitmap;
		}
	}

	public static void clear(ImageView iv) {
		BitmapDrawable bitmapDrawable = (BitmapDrawable) iv.getBackground();
		if (bitmapDrawable != null) {
			Bitmap hisBitmap = bitmapDrawable.getBitmap();
			if (hisBitmap.isRecycled() == false) {
				hisBitmap.recycle();
			}
		}
	}

	private static String getCacheKey(int resId) {
		return new StringBuilder(String.valueOf(resId)).append("#W").toString();
	}

	private static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);
		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}
}
