package cn.com.bluemoon.delivery.entity;

import java.io.Serializable;

public class SubRegion implements Serializable{
	private String dcode;
	private String dname;
	
	public SubRegion() {
	}
	public SubRegion(String code, String name) {
		this.dcode = code;
		this.dname = name;
	}
	public String getDcode() {
		return dcode;
	}
	public void setDcode(String dcode) {
		this.dcode = dcode;
	}
	public String getDname() {
		return dname;
	}
	public void setDname(String dname) {
		this.dname = dname;
	}
	@Override
	public String toString() {
		return dname;
	}
}
