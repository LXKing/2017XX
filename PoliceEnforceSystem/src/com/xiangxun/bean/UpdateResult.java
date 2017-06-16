package com.xiangxun.bean;

/**
 * @package: com.xiangxun.bean
 * @ClassName: UpdateResult.java
 * @Description: 版本更新
 * @author: HanGJ
 * @date: 2015-9-17 上午8:38:49
 */
public class UpdateResult {
	int status;
	String message;
	UpdateData data;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public UpdateData getData() {
		return data;
	}

	public void setData(UpdateData data) {
		this.data = data;
	}

}
