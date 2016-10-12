package cn.com.bluemoon.delivery.app.api.model.other;

import java.io.Serializable;

public class VenueInfo implements Serializable{
	private String venueCode;
	private String venueSname;
	private String timesCode;
	private String timesName;
	public boolean isDefault;
	
	public String getVenueSname() {
		return venueSname;
	}
	public void setVenueSname(String venueSname) {
		this.venueSname = venueSname;
	}
	/**  
	 * venueCode.  
	 *  
	 * @return  the venueCode  
	 * @since   JDK 1.6  
	 */
	public String getVenueCode() {
		return venueCode;
	}
	/**  
	 * venueCode.  
	 *  
	 * @param   venueCode    the venueCode to set  
	 * @since   JDK 1.6  
	 */
	public void setVenueCode(String venueCode) {
		this.venueCode = venueCode;
	}
	
	/**  
	 * timesCode.  
	 *  
	 * @return  the timesCode  
	 * @since   JDK 1.6  
	 */
	public String getTimesCode() {
		return timesCode;
	}
	/**  
	 * timesCode.  
	 *  
	 * @param   timesCode    the timesCode to set  
	 * @since   JDK 1.6  
	 */
	public void setTimesCode(String timesCode) {
		this.timesCode = timesCode;
	}
	/**  
	 * timesName.  
	 *  
	 * @return  the timesName  
	 * @since   JDK 1.6  
	 */
	public String getTimesName() {
		return timesName;
	}
	/**  
	 * timesName.  
	 *  
	 * @param   timesName    the timesName to set  
	 * @since   JDK 1.6  
	 */
	public void setTimesName(String timesName) {
		this.timesName = timesName;
	}

}
  
