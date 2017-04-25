package com.xiangxun.common;

import java.util.HashMap;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.xiangxun.util.ConstantStatus;

/**
 * @package: com.huatek.api.common
 * @ClassName: LoadUpLoadUtil
 * @Description: 上传工具类
 * @author: aaron_han
 * @data: 2015-1-16 下午4:56:31
 */
public class LoadUpLoadUtil {
	private static LoadUpLoadUtil util;

	private LoadUpLoadUtil() {
	}

	public static LoadUpLoadUtil getInstance() {
		if (util == null) {
			util = new LoadUpLoadUtil();
		}
		return util;
	}

	// 上传图片
	public void upLoadFile(final HashMap<String, String> map, final String filePath, final String url, final String fileType, final Handler handler, final Context con) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = new Message();
				try {
					String s = new LoadUpLoadMachine().requestPost(map, url, filePath, fileType);

					if (!s.equals(null)) { // 拿到的数据不为空
						msg.obj = s;
						Bundle bundle = new Bundle();
						bundle.putString(fileType, filePath);
						msg.what = ConstantStatus.UpLoadSuccess;
						msg.setData(bundle);
						handler.sendMessage(msg);
					} else {
						msg.what = ConstantStatus.UpLoadFalse;
						handler.sendMessage(msg);
					}
				} catch (Exception e) {
					msg.what = ConstantStatus.NetWorkError;
					handler.sendMessage(new Message());

				}

			}
		}).start();

	}

	public static void apkLoad(final String url, final String direct, final String fileName, final Handler handler, final Context con) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					LoadUpLoadMachine machine = new LoadUpLoadMachine();
					machine.setHandler(handler);
					int result = machine.getRemoteFileInputStream(url, direct, fileName);
					Message msg = new Message();
					msg.what = result;
					handler.sendMessage(msg);

				} catch (Exception e) {

					e.printStackTrace();
				}

			}
		}).start();

	}

}
