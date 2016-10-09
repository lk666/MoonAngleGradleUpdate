package cn.com.bluemoon.delivery.sz.bean;

import java.io.Serializable;

public class TaskBean implements Serializable{

	private String id;
	private int type;// 0 可调 ，1不可调，2空
	private String datetime;
	private String content;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
  
