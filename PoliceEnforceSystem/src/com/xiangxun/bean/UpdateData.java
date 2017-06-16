package com.xiangxun.bean;

/**
 * @package: com.xiangxun.bean
 * @ClassName: UpdateData.java
 * @Description: 版本更新
 * @author: HanGJ
 * @date: 2015-9-17 上午8:38:37
 */
public class UpdateData {
	String upVersionsCode;
	String currentVersionsCode;
	String fileAddress;
	String uploadDatetime;
	String id;

	public String getUpVersionsCode() {
		return upVersionsCode;
	}

	public void setUpVersionsCode(String upVersionsCode) {
		this.upVersionsCode = upVersionsCode;
	}

	public String getCurrentVersionsCode() {
		return currentVersionsCode;
	}

	public void setCurrentVersionsCode(String currentVersionsCode) {
		this.currentVersionsCode = currentVersionsCode;
	}

	public String getFileAddress() {
		return fileAddress;
	}

	public void setFileAddress(String fileAddress) {
		this.fileAddress = fileAddress;
	}

	public String getUploadDatetime() {
		return uploadDatetime;
	}

	public void setUploadDatetime(String uploadDatetime) {
		this.uploadDatetime = uploadDatetime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}