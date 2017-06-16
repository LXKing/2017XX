package com.xiangxun.bean;

/**
 * Created by Administrator on 2016/10/24.
 */

public class Child {
	private String name;
	private String crosscode;
	private boolean isChecked;

	public Child(String crossname) {
		this.name = crossname;
		this.isChecked = false;
	}

	public Child(String crossname, String crosscode) {
		this.name = crossname;
		this.crosscode = crosscode;
		this.isChecked = false;
	}

	public String getCrossCode() {
		return crosscode;
	}

	public void setCrossCode(String crossCode) {
		this.crosscode = crossCode;
	}

	public String getCrossName() {
		return name;
	}

	public void setCrossName(String crossName) {
		this.name = crossName;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean checked) {
		isChecked = checked;
	}

	public void toggle() {
		this.isChecked = !this.isChecked;
	}
}
