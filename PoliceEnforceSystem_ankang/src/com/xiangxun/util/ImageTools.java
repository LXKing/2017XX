package com.xiangxun.util;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore.MediaColumns;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public final class ImageTools {

	public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels){
		int initialSize = computeInitialSampleSize(options, minSideLength,maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8 ){
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		}else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}
	
	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels){
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 150 : (int) Math.min(Math.floor(w / minSideLength),Math.floor(h / minSideLength));
		if (upperBound < lowerBound){
			return lowerBound;
		}
		if((maxNumOfPixels == -1) && (minSideLength == -1)){
			return 1;
		}else if (minSideLength == -1){
			return lowerBound;
		}else {
			return upperBound;
		}
	}
	
	/**
	 * ���URI��ȡͼƬ����·��
	 * 
	 * */
	protected static String getAbsoluteImagePath(Uri uri, Activity activity){
		String[] proj = { MediaColumns.DATA };
		Cursor cursor = activity.managedQuery(uri, proj, null, null, null);
		String result = null;
		if (cursor != null) {
			int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
			cursor.moveToFirst();
			result = cursor.getString(column_index);
			cursor.close();
		}
		return result;
	}
	
	public static String Base64Image(String path)throws IOException {
		String ImageBase64 = "";

		Bitmap bm = BitmapFactory.decodeFile(path);

		int w = bm.getWidth();
		int h = bm.getHeight();

		Bitmap newmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 70, stream);
		byte[] input = stream.toByteArray();
		ImageBase64 += new String(Base64.encode(input, Base64.DEFAULT), "UTF-8");

		return ImageBase64;
	}
}

