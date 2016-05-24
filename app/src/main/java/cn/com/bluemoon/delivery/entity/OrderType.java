package cn.com.bluemoon.delivery.entity;

public enum OrderType {

	PENDINGORDERS("wait_accept"),

	PENDINGAPPOINTMENT("wait_appointment"),

	PENDINGDELIVERY("wait_delivery"),

	PENDINGRECEIPT("wait_sign");
	
	private String type;

	private OrderType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}

	

}
