package com.xiangxun.bean;

/**
 * Created by Administrator on 2016/10/24.
 */

public class Child {
	private String name;
	private boolean isChecked;

	public Child(String crossName) {
		this.name = crossName;
		this.isChecked = false;
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
