package cn.com.bluemoon.delivery.app.api.model;  

import java.util.List;

public class ResultUserRight extends ResultBase {

	private List<UserRight> rightsList;

	private int groupCount;

	public int getGroupCount() {
		return groupCount;
	}

	public void setGroupCount(int groupCount) {
		this.groupCount = groupCount;
	}

	public List<UserRight> getRightsList() {
		return rightsList;
	}

	public void setRightsList(List<UserRight> rightsList) {
		this.rightsList = rightsList;
	}
}
  
