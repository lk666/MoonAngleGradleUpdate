package cn.com.bluemoon.delivery.app.api.model.other;

import java.io.Serializable;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;


public class ResultOrderCount extends ResultBase implements Serializable {

	private int waitAccept;
	private int waitAppointment;
	private int waitDelivery;
	private int waitSign;
	public int getWaitAccept() {
		return waitAccept;
	}
	public void setWaitAccept(int waitAccept) {
		this.waitAccept = waitAccept;
	}
	public int getWaitAppointment() {
		return waitAppointment;
	}
	public void setWaitAppointment(int waitAppointment) {
		this.waitAppointment = waitAppointment;
	}
	public int getWaitDelivery() {
		return waitDelivery;
	}
	public void setWaitDelivery(int waitDelivery) {
		this.waitDelivery = waitDelivery;
	}
	public int getWaitSign() {
		return waitSign;
	}
	public void setWaitSign(int waitSign) {
		this.waitSign = waitSign;
	}
	
	
}
  
