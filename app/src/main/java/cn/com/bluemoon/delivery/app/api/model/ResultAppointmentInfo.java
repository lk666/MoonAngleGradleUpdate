package cn.com.bluemoon.delivery.app.api.model;  

public class ResultAppointmentInfo extends ResultBase {
    private AppointmentInfo appointmentInfo;

	/**  
	 * appointmentInfo.  
	 *  
	 * @return  the appointmentInfo  
	 * @since   JDK 1.6  
	 */
	public AppointmentInfo getAppointmentInfo() {
		return appointmentInfo;
	}

	/**  
	 * appointmentInfo.  
	 *  
	 * @param   appointmentInfo    the appointmentInfo to set  
	 * @since   JDK 1.6  
	 */
	public void setAppointmentInfo(AppointmentInfo appointmentInfo) {
		this.appointmentInfo = appointmentInfo;
	}
}
  
