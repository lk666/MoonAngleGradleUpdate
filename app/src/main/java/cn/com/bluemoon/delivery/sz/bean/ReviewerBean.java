package cn.com.bluemoon.delivery.sz.bean;

import java.io.Serializable;

/**
 * Created by jiangyuehua on 16/9/12.
 */
public class ReviewerBean implements Serializable {

	/**
	 * uAvatar	头像Url	string
	 uID	用户ID	string
	 uName*/

	String uAvatar;//	头像Url	string
	String uID;//用户ID	string
	String uName;

	public String getuAvatar() {
		return uAvatar;
	}

	public void setuAvatar(String uAvatar) {
		this.uAvatar = uAvatar;
	}

	public String getuID() {
		return uID;
	}

	public void setuID(String uID) {
		this.uID = uID;
	}

	public String getuName() {
		return uName;
	}

	public void setuName(String uName) {
		this.uName = uName;
	}

	@Override
	public String toString() {
		return "ReviewerBean{" +
				"uAvatar='" + uAvatar + '\'' +
				", uID='" + uID + '\'' +
				", uName='" + uName + '\'' +
				'}';
	}
}
