package cn.com.bluemoon.delivery.sz.bean;

/**
 * Created by jiangyuehua on 16/8/11.
 */
public class EventMessageBean {

	String eventMsgAction;
	String eventMsgContent;
	String reMark;

	public EventMessageBean() {
		super();
	}

	public EventMessageBean(String eventMsgAction,String eventMsgContent) {
		this.eventMsgAction = eventMsgAction;
		this.eventMsgContent = eventMsgContent;
	}

	public String getReMark() {
		return reMark;
	}

	public void setReMark(String reMark) {
		this.reMark = reMark;
	}

	public String getEventMsgAction() {
		return eventMsgAction;
	}

	public void setEventMsgAction(String eventMsgAction) {
		this.eventMsgAction = eventMsgAction;
	}

	public String getEventMsgContent() {
		return eventMsgContent;
	}

	public void setEventMsgContent(String eventMsgContent) {
		this.eventMsgContent = eventMsgContent;
	}

	@Override
	public String toString() {
		return "EventMessageBean{" +
				"eventMsgAction='" + eventMsgAction + '\'' +
				", eventMsgContent='" + eventMsgContent + '\'' +
				", reMark='" + reMark + '\'' +
				'}';
	}
}
