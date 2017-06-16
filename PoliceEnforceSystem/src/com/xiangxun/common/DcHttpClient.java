package com.xiangxun.common;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.widget.ImageView;

import com.xiangxun.volley.Network;
import com.xiangxun.volley.Request;
import com.xiangxun.volley.RequestQueue;
import com.xiangxun.volley.Response;
import com.xiangxun.volley.Response.ErrorListener;
import com.xiangxun.volley.Response.Listener;
import com.xiangxun.volley.VolleyError;
import com.xiangxun.volley.toolbox.BasicNetwork;
import com.xiangxun.volley.toolbox.DiskBasedCache;
import com.xiangxun.volley.toolbox.HurlStack;
import com.xiangxun.volley.toolbox.ImageLoader;
import com.xiangxun.volley.toolbox.ImageLoader.ImageCache;
import com.xiangxun.volley.toolbox.ImageLoader.ImageContainer;
import com.xiangxun.volley.toolbox.ImageLoader.ImageListener;
import com.xiangxun.volley.toolbox.NetworkImageView;

/**
 * @package: com.huatek.api.common
 * @ClassName: DcHttpClient
 * @Description: 联网控制器 添加联网请求 以及图片缓存处理工具
 * @author: aaron_han
 * @data: 2015-1-16 下午4:46:42
 */

public class DcHttpClient {
	private static DcHttpClient dcHttpClient;
	public RequestQueue mRequestQueue;// 请求队列
	public BitmapCache mBitmapCache = null;// 网络下载图片缓存容器
	public ImageLoader mImageLoader = null;// 网络图片下载器
	public LocalQueue mLocalQueue;// 本地加载图片队列
	public UpLoadImageQueue mUpLoadQueue;// 本地上传队列
	public Context con;

	public static DcHttpClient getInstance() {
		if (dcHttpClient == null) {
			dcHttpClient = new DcHttpClient();
		}
		return dcHttpClient;
	}

	private DcHttpClient() {
		super();
	}

	/**
	 * 
	 * @Title:
	 * @Description: 初始化HttpCLient
	 * @param: @param con
	 * @return: void
	 * @throws
	 */
	public void init(Context con) {
		this.con = con;
		File sdDir = Environment.getExternalStorageDirectory();
		File file = new File(sdDir, "xiangxun/cache");
		DiskBasedCache cache = new DiskBasedCache(file, 20 * 1024 * 1024);
		Network network = new BasicNetwork(new HurlStack());
		mRequestQueue = new RequestQueue(cache, network);
		mBitmapCache = BitmapCache.getInstance();
		mLocalQueue = new LocalQueue(mBitmapCache, con);
		mUpLoadQueue = new UpLoadImageQueue();
		mImageLoader = new ImageLoader(mRequestQueue, (ImageCache) mBitmapCache);
		mLocalQueue.start();
		mRequestQueue.start();
		mUpLoadQueue.start();
	}

	/**
	 * 
	 * @Title:
	 * @Description: 关闭网络相关线程
	 * @param:
	 * @return: void
	 * @throws
	 */
	public void stop() {
		if (mLocalQueue != null) {
			mLocalQueue.stop();
		}
		if (mRequestQueue != null) {
			mRequestQueue.stop();
		}
		if (mUpLoadQueue != null) {
			mUpLoadQueue.stop();
		}
	}

	/**
	 * 
	 * @Title:
	 * @Description:清理硬盘内存
	 * @param:
	 * @return:
	 * @throws
	 */
	public void cleanDiskCache() {
		mRequestQueue.getCache().clear();
	}

	/**
	 * 
	 * @Title:
	 * @Description:清理应用内存缓存
	 * @param:
	 * @return:
	 * @throws
	 */
	public void cleanLruCache() {
		mBitmapCache.clean();
	}

	/**
	 * 
	 * @Title:
	 * @Description: 断开一条请求
	 * @param: @param tag
	 * @return: void
	 * @throws
	 */
	public void cancelRequest(Object tag) {
		mRequestQueue.cancelAll(tag);
	}

	// 添加一个request请求
	public void addRequest(Request<?> req) {
		mRequestQueue.add(req);
	}

	/**
	 * Converts <code>params</code> into an application/x-www-form-urlencoded
	 * encoded string.
	 */
	private String encodeParameters(Map<String, String> params, String paramsEncoding) {
		StringBuilder encodedParams = new StringBuilder();
		try {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
				encodedParams.append('=');
				encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
				encodedParams.append('&');
			}
			return encodedParams.toString();
		} catch (UnsupportedEncodingException uee) {
			throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
		}
	}

	/**
	 * @Description: get请求
	 * @param url
	 * @param params
	 * @param listener
	 * @param errorListener
	 * @return: void
	 */
	public void getWithURL(Context context, String url, Map<String, String> params, Listener<JSONObject> listener, ErrorListener errorListener) {
		if (params != null && params.size() > 0) {
			if (url.contains("?")) {
				url = url + "&" + encodeParameters(params, "UTF-8");
			} else {
				url = url + "?" + encodeParameters(params, "UTF-8");
			}
		}
		DcJsonRequest jsObjRequest = new DcJsonRequest(context, Request.Method.GET, url, params, listener, errorListener);
		jsObjRequest.setShouldCache(false);
		this.addRequest(jsObjRequest);
	}

	/**
	 * @Description: 缓存
	 * @param url
	 * @param params
	 * @param listener
	 * @param errorListener
	 * @param isCahe
	 * @return: void
	 */
	public void getWithURLOrCache(Context context, String url, Map<String, String> params, Listener<JSONObject> listener, ErrorListener errorListener, boolean isCahe) {
		if (params != null && params.size() > 0) {
			if (url.contains("?")) {
				url = url + "&" + encodeParameters(params, "UTF-8");
			} else {
				url = url + "?" + encodeParameters(params, "UTF-8");
			}
		}
		DcJsonRequest jsObjRequest = new DcJsonRequest(context, Request.Method.GET, url, params, listener, errorListener);
		jsObjRequest.setShouldCache(isCahe);
		this.addRequest(jsObjRequest);
	}

	/**
	 * @Description: post请求
	 * @param url
	 * @param params
	 * @param listener
	 * @param errorListener
	 * @return: void
	 */
	public void postWithURL(Context context, String url, Map<String, String> params, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
		DcJsonRequest jsObjRequest = new DcJsonRequest(context, Request.Method.POST, url, params, listener, errorListener);
		jsObjRequest.setShouldCache(false);
		this.addRequest(jsObjRequest);
	}

	/**
	 * @Description: 图片联网请求
	 * @param url
	 * @param imageView
	 * @param id
	 * @return: void
	 */
	public void getImageForNIView(String url, NetworkImageView imageView, int id) {
		imageView.setDefaultImageResId(id);
		imageView.setErrorImageResId(id);
		imageView.setImageUrl(url, mImageLoader);
	}

	/**
	 * @Description: 请求图片 设置事件监听
	 * @param niv
	 * @param imgUrl
	 * @param resId
	 * @param responseImageListener
	 * @return: void
	 */
	public void requestImage(final ImageView niv, final String imgUrl, final int resId, final ResponseImageListener responseImageListener) {
		// if (TextUtils.isEmpty(imgUrl)) {
		// niv.setImageResource(resId);
		// return;
		// }
		if (responseImageListener != null)
			responseImageListener.onLoadingStarted();
		niv.setImageResource(resId);
		mImageLoader.get(imgUrl, new ImageListener() {
			public void onResponse(ImageContainer response, boolean isImmediate) {
				niv.setImageBitmap(response.getBitmap());
				if (responseImageListener != null)
					responseImageListener.onLoadingComplete(response, isImmediate);
			}

			public void onErrorResponse(VolleyError error) {
				error.printStackTrace();
				niv.setImageResource(resId);
				if (responseImageListener != null)
					responseImageListener.onLoadingFailed(error);
			}
		});
	}

	/**
	 * @Description: 请求图片 设置事件监听
	 * @param niv
	 * @param imgUrl
	 * @param resId
	 * @param responseImageListener
	 * @return: void
	 */
	public void requestImage(final ImageView niv, final String imgUrl, final int resId) {
		niv.setImageResource(resId);
		mImageLoader.get(imgUrl, new ImageListener() {
			public void onResponse(ImageContainer response, boolean isImmediate) {
				niv.setImageBitmap(response.getBitmap());
			}

			public void onErrorResponse(VolleyError error) {
				error.printStackTrace();
				niv.setImageResource(resId);
			}
		});
	}

	public void requestImageScaled(final ImageView niv, final String imgUrl, final int resId) {
		niv.setImageResource(resId);
		mImageLoader.get(imgUrl, new ImageListener() {
			public void onResponse(ImageContainer response, boolean isImmediate) {
				Bitmap bmp = response.getBitmap();
				if (bmp != null) {
					int width = bmp.getWidth();	//300
					int height = bmp.getHeight();	//420
					if (width > 640 || height > 480) {
						width = 640;
						height = 480;
					}
					Bitmap output = Bitmap.createScaledBitmap(bmp, width, height, true);
					niv.setImageBitmap(output);
				} else {
					niv.setImageResource(resId);
				}
			}

			public void onErrorResponse(VolleyError error) {
				error.printStackTrace();
				niv.setImageResource(resId);
			}
		});
	}

	/**
	 * @package: com.huatek.api.common
	 * @ClassName: ResponseImageListener
	 * @Description: 图片下载监听接口
	 * @author: aaron_han
	 * @data: 2015-1-29 上午11:28:08
	 */
	public interface ResponseImageListener {
		public void onLoadingStarted();

		public void onLoadingFailed(VolleyError error);

		public void onLoadingComplete(ImageContainer response, boolean isImmediate);
	}

}