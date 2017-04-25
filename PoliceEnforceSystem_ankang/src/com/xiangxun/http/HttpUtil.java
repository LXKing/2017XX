package com.xiangxun.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import com.xiangxun.bean.FormFile;
import com.xiangxun.util.ConstantStatus;
import com.xiangxun.util.Logger;
import com.xiangxun.widget.MsgToast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.xiangxun.request.AppBuildConfig.DEBUGURL;
import static com.xiangxun.util.Utils.SaveSendAck;

public class HttpUtil {

	public HttpUtil() {
	}

	public String getUrlPathFromLocal(String fileName) {
		StringBuffer sb = new StringBuffer();
		sb.append("");
		Logger.i("Environment.getExternalStorageState():" + Environment.getExternalStorageState());
		Logger.i("Environment.MEDIA_MOUNTED:" + Environment.MEDIA_MOUNTED);
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File picture = new File(Environment.getExternalStorageDirectory(), fileName);
			if (picture.exists()) {
				Logger.i("从内存卡取出文件：" + picture.getPath());
				try {
					@SuppressWarnings("resource")
					BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(picture)));
					String data = null;
					while ((data = br.readLine()) != null) {
						sb.append(data + ",");
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

	public static HttpGet getHttpGet(String url) {
		HttpGet request = new HttpGet(url);
		return request;
	}

	public static HttpPost getHttpPost(String url) {
		HttpPost request = new HttpPost(url);
		return request;
	}

	public static HttpResponse getHttpResponse(HttpGet request) throws ClientProtocolException, IOException {
		HttpResponse response = new DefaultHttpClient().execute(request);
		return response;
	}

	public static HttpResponse getHttpResponse(HttpPost request) throws ClientProtocolException, IOException {
		HttpResponse response = new DefaultHttpClient().execute(request);
		return response;
	}

	public boolean upLoadCommonDataToServer(String url, List<NameValuePair> params) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpParams httpParams = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 6000);
		HttpConnectionParams.setSoTimeout(httpParams, 10000);
		HttpPost httpPost = new HttpPost(url);

		try {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
			httpPost.setEntity(entity);
			HttpResponse response = httpClient.execute(httpPost, localContext);
			int responsecode = response.getStatusLine().getStatusCode();
			Logger.i(responsecode);
			String resStr = "";
			if (responsecode == 200) {
				try {
					HttpEntity resEntity = response.getEntity();
					resStr = EntityUtils.toString(resEntity);
					Logger.i(resStr);
					if (resStr == null || (resStr.equals(""))) {
						if (DEBUGURL)
							SaveSendAck("responsecode = " + responsecode + " resStr = " + resStr);
						return false;
					} else if (null != resStr && resStr.equals("success")) {
						return true;
					} else if (resStr.contains("login")) {
						MsgToast.geToast().setMsg("已经离线，请重新登录服务器!");
					}
				} catch (Exception e) {
					e.printStackTrace();
					if (DEBUGURL)
						SaveSendAck("Exception responsecode = " + responsecode + " e = " + e.getMessage());
					return false;
				}
			} else if (responsecode == 500){
				HttpEntity resEntity = response.getEntity();
				resStr = EntityUtils.toString(resEntity);
				if (resStr.contains("违反唯一约束条件"))
					return true;
			}

			if (DEBUGURL)
				SaveSendAck("responsecode = " + responsecode + " resStr = " + resStr);
		} catch (Exception e) {
			e.printStackTrace();
			if (DEBUGURL)
				SaveSendAck("Exception httpPost = " + httpPost + " e = " + e.getMessage());
			return false;
		}
		return false;
	}

	public boolean upLoadMultiDataToServer(String url, List<NameValuePair> params) {
		boolean isSuccess = false;
		String emptyImgPath = Environment.getExternalStorageDirectory() + "/xiangxun/no_image.jpg";
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpParams httpParams = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
		HttpConnectionParams.setSoTimeout(httpParams, 5000);
		HttpPost httpPost = new HttpPost(url);

//		try {
//			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
		try {
			MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			for (int index = 0; index < params.size(); index++) {
				if (params.get(index).getName().equalsIgnoreCase("image1")) {
					if (!params.get(index).getValue().equals("")) {
						entity.addPart(params.get(index).getName(), new FileBody(new File(params.get(index).getValue())));
					} else {
						entity.addPart(params.get(index).getName(), new FileBody(new File(emptyImgPath)));
					}
				} else if (params.get(index).getName().equalsIgnoreCase("image2")) {

					if (!params.get(index).getValue().equals("")) {
						entity.addPart(params.get(index).getName(), new FileBody(new File(params.get(index).getValue())));
					} else {
						entity.addPart(params.get(index).getName(), new FileBody(new File(emptyImgPath)));
					}

				} else if (params.get(index).getName().equalsIgnoreCase("image3")) {

					if (!params.get(index).getValue().equals("")) {
						entity.addPart(params.get(index).getName(), new FileBody(new File(params.get(index).getValue())));
					} else {
						entity.addPart(params.get(index).getName(), new FileBody(new File(emptyImgPath)));
					}

				} else if (params.get(index).getName().equalsIgnoreCase("image4")) {

					if (!params.get(index).getValue().equals("")) {
						entity.addPart(params.get(index).getName(), new FileBody(new File(params.get(index).getValue())));
					} else {
						entity.addPart(params.get(index).getName(), new FileBody(new File(emptyImgPath)));
					}

				} else if (params.get(index).getName().equalsIgnoreCase("image5")) {

					if (!params.get(index).getValue().equals("")) {
						entity.addPart(params.get(index).getName(), new FileBody(new File(params.get(index).getValue())));
					} else {
						entity.addPart(params.get(index).getName(), new FileBody(new File(emptyImgPath)));
					}

				} else if (params.get(index).getName().equalsIgnoreCase("image6")) {

					if (!params.get(index).getValue().equals("")) {
						entity.addPart(params.get(index).getName(), new FileBody(new File(params.get(index).getValue())));
					} else {
						entity.addPart(params.get(index).getName(), new FileBody(new File(emptyImgPath)));
					}

				} else if (params.get(index).getName().equalsIgnoreCase("image7")) {

					if (!params.get(index).getValue().equals("")) {
						entity.addPart(params.get(index).getName(), new FileBody(new File(params.get(index).getValue())));
					} else {
						entity.addPart(params.get(index).getName(), new FileBody(new File(emptyImgPath)));
					}

				} else if (params.get(index).getName().equalsIgnoreCase("image8")) {

					if (!params.get(index).getValue().equals("")) {
						entity.addPart(params.get(index).getName(), new FileBody(new File(params.get(index).getValue())));
					} else {
						entity.addPart(params.get(index).getName(), new FileBody(new File(emptyImgPath)));
					}

				} else if (params.get(index).getName().equalsIgnoreCase("image9")) {

					if (!params.get(index).getValue().equals("")) {
						entity.addPart(params.get(index).getName(), new FileBody(new File(params.get(index).getValue())));
					} else {
						entity.addPart(params.get(index).getName(), new FileBody(new File(emptyImgPath)));
					}

				} else if (params.get(index).getName().equalsIgnoreCase("image10")) {

					if (!params.get(index).getValue().equals("")) {
						entity.addPart(params.get(index).getName(), new FileBody(new File(params.get(index).getValue())));
					} else {
						entity.addPart(params.get(index).getName(), new FileBody(new File(emptyImgPath)));
					}

				} else {
					entity.addPart(params.get(index).getName(), new StringBody(params.get(index).getValue(), Charset.forName("UTF-8")));
				}
			}
			Logger.i("post - done" + entity);
			httpPost.setEntity(entity);
			HttpResponse response = httpClient.execute(httpPost, localContext);
			int responsecode = response.getStatusLine().getStatusCode();
			Logger.i(responsecode);
			String resStr = "";
			if (responsecode == 200) {
				HttpEntity resEntity = response.getEntity();
				resStr = EntityUtils.toString(resEntity);
				Logger.i("vioup==" + resStr);
				if (resStr == null || (resStr.equals(""))) {
					isSuccess = false;
				} else if (null != resStr && resStr.equals("success")) {
					isSuccess = true;
				} else if (resStr.contains("login")) {
					MsgToast.geToast().setMsg("已经离线，请重新登录服务器!");
				} else if (resStr.contains("操作成功") || resStr.contains("该预警信息的签收标记为1,无法签收")
						|| resStr.contains("反馈记录不存在") || resStr.contains("只有已经反馈，并且已经查获车辆的预警信息才能上传图片")
						|| resStr.contains("无权签收反馈该预警信息") || resStr.contains("同一过车记录的其他预警信息已完成处置反馈")){
					isSuccess = true;
				}
			} else if (responsecode == 500){
				HttpEntity resEntity = response.getEntity();
				resStr = EntityUtils.toString(resEntity);
				if (resStr.contains("违反唯一约束条件"))
					isSuccess = true;
			}

		} catch (Exception e) {
			return false;
		}
		return isSuccess;
	}

	public int upLoadWarnDataToServer(String url, List<NameValuePair> params) {
		int isSuccess = ConstantStatus.UpStatusFail;
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpParams httpParams = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
		HttpConnectionParams.setSoTimeout(httpParams, 5000);
		HttpPost httpPost = new HttpPost(url);

		try {
			MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			for (int index = 0; index < params.size(); index++) {
				entity.addPart(params.get(index).getName(), new StringBody(params.get(index).getValue(), Charset.forName("UTF-8")));
			}
			Logger.i("post - done" + entity);
			httpPost.setEntity(entity);
			HttpResponse response = httpClient.execute(httpPost, localContext);
			int responsecode = response.getStatusLine().getStatusCode();
			Logger.i(responsecode);
			String resStr = "";
			if (responsecode == 200) {
				HttpEntity resEntity = response.getEntity();
				resStr = EntityUtils.toString(resEntity);
				Logger.i("vioup==" + resStr);
				if (resStr == null || (resStr.equals(""))) {
					isSuccess = ConstantStatus.UpStatusFail;
				} else if (null != resStr && resStr.equals("success")) {
					isSuccess = ConstantStatus.UpStatusSuccess;
				} else if (resStr.contains("login")) {
					MsgToast.geToast().setMsg("已经离线，请重新登录服务器!");
					isSuccess = ConstantStatus.UpStatusFail;
				} else if (resStr.contains("操作成功")) {
					isSuccess = ConstantStatus.UpStatusSuccess;
				} else if (resStr.contains("该预警信息的签收标记为1")) { //该预警信息的签收标记为1,无法签收
					isSuccess = ConstantStatus.UpStatusSigned;
				} else if (resStr.contains("同一过车记录的其他预警信息已完成处置反馈")) {
					isSuccess = ConstantStatus.UpStatusAcked;
				} else if (resStr.contains("无权签收反馈该预警信息")) {
					isSuccess = ConstantStatus.UpStatusNoAuthority;
				} else if (resStr.contains("反馈记录不存在")) {
					isSuccess = ConstantStatus.UpStatusNoinfo;
				}
				return isSuccess;
			} else if (responsecode == 500){
				HttpEntity resEntity = response.getEntity();
				resStr = EntityUtils.toString(resEntity);
				if (resStr.contains("违反唯一约束条件"))
					isSuccess = ConstantStatus.UpStatusSuccess;
				else
					return ConstantStatus.UpStatusException;
			}
		} catch (Exception e) {
			return ConstantStatus.UpStatusException;
		}
		return isSuccess;
	}

	public String queryStringForGet(String url) {
		HttpGet request = HttpUtil.getHttpGet(url);
		String result = null;
		try {
			HttpResponse response = HttpUtil.getHttpResponse(request);
			if (response.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(response.getEntity(), "UTF-8");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			result = "网络异常";
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			result = "网络异常";
			return result;
		}
		return result;
	}

	/**
	 * post
	 * 
	 * @author hanyu
	 * @param path
	 * @param params
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("finally")
	public String queryStringForPost(String path, Map<String, String> params, String encoding) throws Exception {
		String result = null;
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();// ����������
		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
		}
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs, encoding);
		HttpPost httpPost = new HttpPost(path);
		httpPost.setEntity(entity);

		DefaultHttpClient client = new DefaultHttpClient();
		client.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 60000); // 请求超时
		client.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);// 连接超时

		HttpResponse response = client.execute(httpPost);
		try {
			if (response.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(response.getEntity(), "UTF-8");
			}
			Logger.i("result-->" + result);
		} catch (Exception e) {
			e.printStackTrace();
			result = "网络异常";
		}

		finally {
			return result;

		}

	}

	/**
	 * @author hanyu 文件上传
	 * @param actionUrl
	 * @param params
	 * @param files
	 * @return
	 * @throws Exception
	 */
	public static String uploadFile(String actionUrl, Map<String, String> params, FormFile[] files) throws Exception {
		String enterNewline = "\r\n";// 回车换行
		String fix = "--";// 分隔符前缀
		String boundary = "######";// 分隔符
		URL url = new URL(actionUrl);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setDoInput(true);// 可以输入
		con.setDoOutput(true);// 可以输出
		con.setUseCaches(false);// 禁止使用缓存
		con.setRequestMethod("POST");// 请求方法为"post"
		con.setRequestProperty("Connection", "Keep-Alive");
		con.setRequestProperty("Charset", "UTF-8");
		con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
		DataOutputStream ds = new DataOutputStream(con.getOutputStream());
		Set<String> keySet = params.keySet();
		Iterator<String> it = keySet.iterator();
		while (it.hasNext()) {
			String key = it.next();
			String value = params.get(key);
			ds.writeBytes(fix + boundary + enterNewline);
			ds.writeBytes("Content-Disposition: form-data; " + "name=\"" + key + "\"" + enterNewline);
			ds.writeBytes(enterNewline);
			ds.writeBytes(value);
			ds.writeBytes(enterNewline);
		}
		if (files != null && files.length > 0) {
			for (int i = 0; i < files.length; i++) {
				ds.writeBytes(fix + boundary + enterNewline);
				ds.writeBytes("Content-Disposition: form-data; " + "name=\"" + files[0].getFormname() + "\"" + ";filename=\"" + files[0].getFileName() + "\"" + enterNewline);
				ds.writeBytes(enterNewline);
				byte[] buffer = files[0].getData();
				ds.write(buffer);
				ds.writeBytes(enterNewline);
			}
		}
		ds.writeBytes(fix + boundary + fix + enterNewline);
		ds.flush();
		InputStream is = con.getInputStream();
		int ch;
		StringBuffer sb = new StringBuffer();
		while ((ch = is.read()) != -1) {
			sb.append((char) ch);
		}
		ds.close();
		return sb.toString().trim();
	}

	/**
	 * 判断当前网络类型
	 * 
	 * @author hanyu
	 * @param context
	 * @param urlstr
	 * @return
	 */
	public static HttpURLConnection openUrl(Context context, String urlstr) {

		URL url = null;
		HttpURLConnection httpConn = null;

		try {
			url = new URL(urlstr);
			NetworkInfo networkinfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
			if (networkinfo != null) {

				if (networkinfo.getType() == ConnectivityManager.TYPE_MOBILE) {

					String host = android.net.Proxy.getDefaultHost();

					int port = android.net.Proxy.getDefaultPort();
					InetSocketAddress inetsockeadrress = new InetSocketAddress(host, port);
					java.net.Proxy.Type porxytype = java.net.Proxy.Type.valueOf(url.getProtocol().toUpperCase());

					java.net.Proxy proxy = new java.net.Proxy(porxytype, inetsockeadrress);

					try {
						httpConn = (HttpURLConnection) url.openConnection(proxy);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {

					try {
						httpConn = (HttpURLConnection) url.openConnection();
						httpConn.setConnectTimeout(10000);
						httpConn.setDoInput(true);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return httpConn;
	}

	/**
	 * 检测当前网络状态
	 * 
	 * @param context
	 * @return boolean类型 如果网络可用 返回 true，否则返回 false
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * JSONArray 转换 arraylist
	 * 
	 * @param jsonArray
	 * @return
	 */
	public ArrayList<Map<String, Object>> getInitListData(JSONArray jsonArray) {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list.clear();
		try {
			if (jsonArray.length() > 0) {
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					Map<String, Object> map = new HashMap<String, Object>();
					Iterator<String> it = jsonObject.keys();
					while (it.hasNext()) {
						String key = it.next();
						String value = "";
						try {
							Object values = jsonObject.get(key);
							if (values != null) {
								value = values.toString();
							}
						} catch (Exception e) {
							value = "";
						}
						map.put(key, value);
					}
					list.add(map);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * obj -> hashmap
	 * 
	 * @param obj
	 * @return
	 */
	public HashMap<String, String> getHashMap(Object obj) {
		HashMap<String, String> data = new HashMap<String, String>();
		try {
			JSONObject jsonObject = new JSONObject(obj.toString());
			Iterator it = jsonObject.keys();
			while (it.hasNext()) {
				String key = String.valueOf(it.next());
				String value = jsonObject.getString(key);
				data.put(key, value);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return data;
	}
}
