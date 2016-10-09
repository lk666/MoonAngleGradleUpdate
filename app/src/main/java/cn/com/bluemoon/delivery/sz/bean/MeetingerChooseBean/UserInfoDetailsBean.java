package cn.com.bluemoon.delivery.sz.bean.MeetingerChooseBean;

/**
 * Created by jiangyuehua on 16/7/13.
 */
public class UserInfoDetailsBean {


	/**
	 * originPlace : Hjtwh Mbbfkjb Gpnmvezyc Hjggbi Dlp Xzssnbg
	 * postState : 1
	 * staffID : 测试内容p3c7
	 * department : 技术部
	 * departmentID : 53268616
	 * email : e.williams@jackson.co.uk
	 * emergencyTel : 18610471585
	 * sex : 女
	 * sexNum : 0
	 * staffName : 李四
	 * station : 设计师
	 * stationID : 231770499
	 * superiorStaffID : 732535097
	 * tel : 18618557854
	 * wechat : 852154
	 *
	 *
	 */

	private String originPlace;
	private int postState;
	private String staffID;
	private String department;
	private String departmentID;
	private String email;
	private String emergencyTel;
	private String sex;
	private String sexNum;
	private String staffName;
	private String station;
	private String stationID;
	private String superiorStaffID;
	private String tel;
	private String wechat;

	private boolean isChecked=false;//选用后的标识
//	private int position=0;//在当前listView中的位置

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean checked) {
		isChecked = checked;
	}

	public String getOriginPlace() {
		return originPlace;
	}

	public void setOriginPlace(String originPlace) {
		this.originPlace = originPlace;
	}

	public int getPostState() {
		return postState;
	}

	public void setPostState(int postState) {
		this.postState = postState;
	}

	public String getStaffID() {
		return staffID;
	}

	public void setStaffID(String staffID) {
		this.staffID = staffID;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getDepartmentID() {
		return departmentID;
	}

	public void setDepartmentID(String departmentID) {
		this.departmentID = departmentID;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmergencyTel() {
		return emergencyTel;
	}

	public void setEmergencyTel(String emergencyTel) {
		this.emergencyTel = emergencyTel;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getSexNum() {
		return sexNum;
	}

	public void setSexNum(String sexNum) {
		this.sexNum = sexNum;
	}

	public String getStaffName() {
		return staffName;
	}

	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}

	public String getStation() {
		return station;
	}

	public void setStation(String station) {
		this.station = station;
	}

	public String getStationID() {
		return stationID;
	}

	public void setStationID(String stationID) {
		this.stationID = stationID;
	}

	public String getSuperiorStaffID() {
		return superiorStaffID;
	}

	public void setSuperiorStaffID(String superiorStaffID) {
		this.superiorStaffID = superiorStaffID;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getWechat() {
		return wechat;
	}

	public void setWechat(String wechat) {
		this.wechat = wechat;
	}

	@Override
	public String toString() {
		return "UserInfoDetailsBean{" +
				"originPlace='" + originPlace + '\'' +
				", postState=" + postState +
				", staffID='" + staffID + '\'' +
				", department='" + department + '\'' +
				", departmentID='" + departmentID + '\'' +
				", email='" + email + '\'' +
				", emergencyTel='" + emergencyTel + '\'' +
				", sex='" + sex + '\'' +
				", sexNum='" + sexNum + '\'' +
				", staffName='" + staffName + '\'' +
				", station='" + station + '\'' +
				", stationID='" + stationID + '\'' +
				", superiorStaffID='" + superiorStaffID + '\'' +
				", tel='" + tel + '\'' +
				", wechat='" + wechat + '\'' +
				", isChecked=" + isChecked +
				'}';
	}
}
