package cn.com.bluemoon.delivery.app.api.model;

import java.io.Serializable;
import java.util.List;

public class TicketCountJsonResult extends ResultBase implements Serializable{

	private List<TicketCount> list;

	public List<TicketCount> getList() {
		return list;
	}

	public void setList(List<TicketCount> list) {
		this.list = list;
	}

	
	
}
