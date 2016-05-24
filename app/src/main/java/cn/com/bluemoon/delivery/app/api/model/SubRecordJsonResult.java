package cn.com.bluemoon.delivery.app.api.model;

import java.io.Serializable;

public class SubRecordJsonResult extends ResultBase implements Serializable{

	private SubRecord subRecord;

	public SubRecord getSubRecord() {
		return subRecord;
	}

	public void setSubRecord(SubRecord subRecord) {
		this.subRecord = subRecord;
	}
	
}
