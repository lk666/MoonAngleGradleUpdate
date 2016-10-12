package cn.com.bluemoon.delivery.app.api.model.other;

import java.io.Serializable;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

public class SubRecordJsonResult extends ResultBase implements Serializable{

	private SubRecord subRecord;

	public SubRecord getSubRecord() {
		return subRecord;
	}

	public void setSubRecord(SubRecord subRecord) {
		this.subRecord = subRecord;
	}
	
}
