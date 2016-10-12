package cn.com.bluemoon.delivery.app.api.model.other;

public enum OrderState {

	ACCEPT("WAIT_ACCEPT"),
	APPOINTMENT("WAIT_APPOINTMENT"),
	DELIVERY("WAIT_DELIVERY"),
	SIGN("WAIT_SIGN"),
	HISTORY("ALREADY_SIGN"),
	RETURN("ALREADY_RETURN"),
	LOGISTICS("FORWARD_LOGISTICS"),
	PICKUP("ORDER_PICKUP"),
	CHANGEAPPOINTMENT("CHANGE_TIME");
	
	private String value;
	private OrderState(String value){
		this.value = value;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return value;
	}
	
}
