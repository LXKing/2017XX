package com.xiangxun.bean;

import java.io.Serializable;

/***
 * 违法行为
 * 
 * @author zhouhaij
 * @since 1.0
 * @version
 */
public class VioDic implements Serializable {
	private static final long serialVersionUID = 2059609765193362021L;

	public String code;
	public String name;
	public Short fineDefault;
	public Short fineMin;
	public Short fineMax;
	public String law;
	public String rule;
	public String method;
	public String punish;
	public String simpleName;
	public String isshow;
	public Short ordernum;
	public String marker;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code == null ? null : code.trim();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

	public Short getFineDefault() {
		return fineDefault;
	}

	public void setFineDefault(Short fineDefault) {
		this.fineDefault = fineDefault;
	}

	public Short getFineMin() {
		return fineMin;
	}

	public void setFineMin(Short fineMin) {
		this.fineMin = fineMin;
	}

	public Short getFineMax() {
		return fineMax;
	}

	public void setFineMax(Short fineMax) {
		this.fineMax = fineMax;
	}

	public String getLaw() {
		return law;
	}

	public void setLaw(String law) {
		this.law = law == null ? null : law.trim();
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule == null ? null : rule.trim();
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method == null ? null : method.trim();
	}

	public String getPunish() {
		return punish;
	}

	public void setPunish(String punish) {
		this.punish = punish == null ? null : punish.trim();
	}

	public String getSimpleName() {
		return simpleName;
	}

	public void setSimpleName(String simpleName) {
		this.simpleName = simpleName == null ? null : simpleName.trim();
	}

	public String getIsshow() {
		return isshow;
	}

	public void setIsshow(String isshow) {
		this.isshow = isshow == null ? null : isshow.trim();
	}

	public Short getOrdernum() {
		return ordernum;
	}

	public void setOrdernum(Short ordernum) {
		this.ordernum = ordernum;
	}

	/**
	 * @return the marker
	 */
	public String getMarker() {
		return marker;
	}

	/**
	 * @param marker
	 *            the marker to set
	 */
	public void setMarker(String marker) {
		this.marker = marker;
	}

}