package com.xiangxun.bean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/10/24.
 */

public class Group {
	private String id;
	private String department_name;

	private int cross_total;
	private ArrayList<Child> cross_list;
	private boolean isChecked;

	public Group(String id, String title) {
		this.id = id;
		this.department_name = title;
		this.isChecked = false;
		cross_list = new ArrayList<Child>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return department_name;
	}

	public void setTitle(String title) {
		this.department_name = title;
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

	public void addChildrenItem(Child child) {
		cross_list.add(child);
	}

	public int getChildrenCount() {
		return cross_list.size();
	}

	public Child getChildItem(int index) {
		return cross_list.get(index);
	}

	public String getDepartment_name() {
		return department_name;
	}

	public void setDepartment_name(String department_name) {
		this.department_name = department_name;
	}

	public int getCross_total() {
		return cross_total;
	}

	public void setCross_total(int cross_total) {
		this.cross_total = cross_total;
	}
}
