package cn.com.bluemoon.delivery.app.api.model.other;

import java.io.Serializable;
import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

public class TicketCountJsonResult extends ResultBase implements Serializable{

	private List<TicketCount> list;

	public List<TicketCount> getList() {
		return list;
	}

	public void setList(List<TicketCount> list) {
		this.list = list;
	}

	
	
}
