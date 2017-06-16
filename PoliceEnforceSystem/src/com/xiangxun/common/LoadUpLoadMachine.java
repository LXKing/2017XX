package com.xiangxun.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.net.ssl.SSLHandshakeException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

import android.os.Handler;
import android.os.Message;

import com.xiangxun.util.ConstantStatus;
import com.xiangxun.util.Logger;
import com.xiangxun.util.MyUtils;

/**
 * @package: com.huatek.api.common
 * @ClassName: LoadUpLoadMachine
 * @Description: 文件上传工具类
 * @author: aaron_han
 * @data: 2015-1-16 下午4:45:58
 */
public class LoadUpLoadMachine {
	public boolean isCancel = false;// 是否被强制关闭
	private boolean reconnect = false;// 是否重连
	private boolean showProgress = false;
	public int j = 0;// 显示进度信息
	private Handler handler;
	HttpPost postRequest;
	HttpGet getRequest;

	/**
	 * 普通Http的post请求
	 * 
	 * @param list
	 * @param url
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	public String requestPost(Map<String, String> map, String url, String filePath, String fileType) throws Exception {
		HttpClient client = new DefaultHttpClient();
		postRequest = null;
		String resStr = null;
		InputStream in = null;
		ByteArrayOutputStream out = null;
		try {
			MultipartEntity mpEntity = new MultipartEntity();
			if (map != null && !map.isEmpty()) {
				for (Map.Entry<String, String> entry : map.entrySet()) {
					StringBody par = new StringBody(entry.getValue());
					mpEntity.addPart(entry.getKey(), par);
				}
			}
			// 文件
			if (!filePath.equals("")) {
				FileBody file = new FileBody(new File(filePath));
				mpEntity.addPart(fileType, file);
			}
			// 使用HttpPost对象设置发送的URL路径
			postRequest = new HttpPost(url);
			// 发送请求体
			postRequest.setEntity(mpEntity);
			HttpResponse response = client.execute(postRequest);
			Logger.e((response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) + "==" + url);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					in = entity.getContent();
					out = new ByteArrayOutputStream();
					byte[] arr = new byte[1024 * 8];
					int len = 0;
					while ((len = in.read(arr)) > 0) {
						if (isCancel) {
							throw new ColseRequestException();
						}
						out.write(arr, 0, len);
						out.flush();
					}
					resStr = out.toString("UTF-8");
				}
			}
			return resStr;
		}

		finally {
			closeStream(in, out);
			if (postRequest != null) {
				postRequest.abort();
			}
		}
	}

	/**
	 * 大文件下载默认不显示进度条 调用setHandler方法可以回传进度值到handler
	 * 
	 * @param url
	 *            连接地址
	 * @param direct
	 *            目录名
	 * @return -2表示空间不足-1表示失败 0 表示成功 1表示文件已经存在
	 * @throws
	 * 
	 */
	@SuppressWarnings("resource")
	public int getRemoteFileInputStream(String url, String direct, String fileName) throws Exception {
		HttpClient client = new DefaultHttpClient();
		InputStream in = null;
		OutputStream out = null;
		j = 0;
		File dirfile = new File(direct);
		if (!dirfile.exists()) {
			dirfile.mkdir();
		}
		File file = new File(direct, fileName);
		try {
			getRequest = new HttpGet(url);
			if (true == reconnect) {
				// get.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,new
				// DefaultHttpMethodRetryHandler());
			}
			if (isCancel) {
				throw new ColseRequestException();
			}
			HttpResponse response = client.execute(getRequest);
			if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
				HttpEntity entity = response.getEntity();
				long curBytes = 0, totalBytes = 0;
				totalBytes = entity.getContentLength();
				if (entity != null) {
					long lenth = entity.getContentLength();
					if (lenth > MyUtils.getSdRemainRoom()) {
						return ConstantStatus.SD_NOSPACE;// sd卡空间不足 返回码
					} else {
						in = entity.getContent();

						out = new FileOutputStream(file);
						byte[] arr = new byte[1024 * 8];
						int len = 0;
						while ((len = in.read(arr)) > 0) {
							if (isCancel) {
								throw new ColseRequestException();
							}
							out.write(arr, 0, len);
							out.flush();
							if (showProgress) {
								curBytes += len;
								j = ((int) (curBytes / (float) totalBytes * 100));
								Message msg = new Message();
								msg.what = j;
								handler.sendMessage(msg);
							}

						}
					}
				}
			}
			return ConstantStatus.SUCCESS;// 获取成功返回码
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取小文件
	 * 
	 * @param url
	 * @param appBean
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	public byte[] getBytes(String url) throws Exception {
		HttpClient client = new DefaultHttpClient();
		byte[] is = null;
		InputStream in = null;
		ByteArrayOutputStream out = null;
		try {
			postRequest = new HttpPost(url);
			if (true == reconnect) {
				// get.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,new
				// DefaultHttpMethodRetryHandler());
			}
			if (isCancel) {
				throw new ColseRequestException();
			}
			HttpResponse response = client.execute(postRequest);
			if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
				HttpEntity entity = response.getEntity();

				if (entity != null) {
					in = entity.getContent();
					out = new ByteArrayOutputStream();
					byte[] arr = new byte[1024 * 8];
					int len = 0;
					while ((len = in.read(arr)) > 0) {
						if (isCancel) {
							throw new ColseRequestException();
						}
						out.write(arr, 0, len);
						out.flush();
					}
					is = out.toByteArray();
				}
			}
			return is;
		} finally {
			closeStream(in, out);
		}
	}

	/**
	 * 
	 * @param handler
	 *            传递显示进度的handler
	 */
	public void setHandler(Handler handler) {
		showProgress = true;// 置显示进度true
		this.handler = handler;

	}

	/**
	 * 关闭流
	 * 
	 * @param in
	 * @param out
	 */
	private void closeStream(InputStream in, ByteArrayOutputStream out) {
		try {
			if (in != null) {
				in.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (out != null) {
				out.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static HttpRequestRetryHandler requestRetryHandler = new HttpRequestRetryHandler() {
		// 自定义的恢复策略
		public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
			// 设置恢复策略，在发生异常时候将自动重试N次
			if (executionCount >= 3) {
				// 如果超过最大重试次数，那么就不要继续了
				return false;
			}
			if (exception instanceof NoHttpResponseException) {
				// 如果服务器丢掉了连接，那么就重试
				return true;
			}
			if (exception instanceof SSLHandshakeException) {
				// 不要重试SSL握手异常
				return false;
			}
			HttpRequest request = (HttpRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
			boolean idempotent = (request instanceof HttpEntityEnclosingRequest);
			if (!idempotent) {
				// 如果请求被认为是幂等的，那么就重试
				return true;
			}
			return false;
		}
	};

	/**
	 * 
	 * @Title:
	 * @Description: 断开一个任务
	 * @param: @return
	 * @return: boolean
	 * @throws
	 */
	public boolean close() {
		if (postRequest != null) {
			postRequest.abort();
		}
		if (getRequest != null) {
			getRequest.abort();
		}
		isCancel = true;
		return true;
	}

}
