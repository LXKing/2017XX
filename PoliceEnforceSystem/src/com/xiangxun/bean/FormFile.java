/**
 * 
 */
package com.xiangxun.bean;

public class FormFile {
	private byte[] data;// �ļ��ֽ�
	private String fileName;// �ļ����
	private String formname;// �?�����
	private String contentType = "image/jpg";// ��������

	public FormFile(String fileName, byte[] data, String formname,
			String contentType) {
		this.fileName = fileName;
		this.data = data;
		this.formname = formname;
		if(contentType!=null){
			this.contentType = contentType;
		}

	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFormname() {
		return formname;
	}

	public void setFormname(String formname) {
		this.formname = formname;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

}
