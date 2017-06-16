/**
 * 
 */
package com.xiangxun.common;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.xiangxun.volley.toolbox.ImageLoader.ImageCache;

/**
 * 
 * @package: com.huatek.api.common
 * @ClassName: BitmapCache
 * @Description: 图片缓存类
 * @author: aaron_han
 * @data: 2015-1-10 下午4:54:50
 */
public class BitmapCache implements ImageCache {
	private LruCache<String, Bitmap> mCache;
	private static BitmapCache cache;

	private BitmapCache() {
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int maxSize = maxMemory / 3;
		mCache = new LruCache<String, Bitmap>(maxSize) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}
		};
	}

	public LruCache<String, Bitmap> getmCache() {
		return mCache;
	}

	public void setmCache(LruCache<String, Bitmap> mCache) {
		this.mCache = mCache;
	}

	public static BitmapCache getInstance() {
		cache = new BitmapCache();
		return cache;
	}

	public void clean() {
		if (mCache != null)
			mCache.evictAll();
	}

	@Override
	public Bitmap getBitmap(String url) {
		return mCache.get(url);
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		mCache.put(url, bitmap);
	}

}