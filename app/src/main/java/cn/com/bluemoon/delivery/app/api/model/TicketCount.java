package cn.com.bluemoon.delivery.app.api.model;

import java.io.Serializable;

public class TicketCount implements Serializable{

	private int id;
	private String actStartTime;
	private String actEndTime;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getActStartTime() {
		return actStartTime;
	}
	public void setActStartTime(String actStartTime) {
		this.actStartTime = actStartTime;
	}
	public String getActEndTime() {
		return actEndTime;
	}
	public void setActEndTime(String actEndTime) {
		this.actEndTime = actEndTime;
	}
	
}
