package cn.com.bluemoon.delivery.app.api.model;

import cn.com.bluemoon.delivery.R;

public enum TicketType {

	TICKET("ticket",R.mipmap.icon_ticket,R.string.ticket_type_ticket),
	MOONFRIEND("moonFriend",R.mipmap.icon_moonfriend,R.string.ticket_type_moonfriend),
	MOONANGEL("moonAngel",R.mipmap.icon_moonangel,R.string.ticket_type_moonangel),
	EMP("emp",R.mipmap.icon_emp,R.string.ticket_type_emp);
	
	
	private String key;
	private int icon;
	private int value;
	private TicketType(String key,int icon,int value){
		this.key = key;
		this.icon = icon;
		this.value = value;
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return key;
	}
}
