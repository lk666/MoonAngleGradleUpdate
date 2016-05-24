package cn.com.bluemoon.delivery.app.api.model;  
public class Version {
	private int id;
	/**  
	 * id.  
	 *  
	 * @return  the id  
	 * @since   JDK 1.6  
	 */
	public int getId() {
		return id;
	}
	/**  
	 * id.  
	 *  
	 * @param   id    the id to set  
	 * @since   JDK 1.6  
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**  
	 * buildVersion.  
	 *  
	 * @return  the buildVersion  
	 * @since   JDK 1.6  
	 */
	public String getBuildVersion() {
		return buildVersion;
	}
	/**  
	 * buildVersion.  
	 *  
	 * @param   buildVersion    the buildVersion to set  
	 * @since   JDK 1.6  
	 */
	public void setBuildVersion(String buildVersion) {
		this.buildVersion = buildVersion;
	}
	/**  
	 * mustUpdate.  
	 *  
	 * @return  the mustUpdate  
	 * @since   JDK 1.6  
	 */
	public int getMustUpdate() {
		return mustUpdate;
	}
	/**  
	 * mustUpdate.  
	 *  
	 * @param   mustUpdate    the mustUpdate to set  
	 * @since   JDK 1.6  
	 */
	public void setMustUpdate(int mustUpdate) {
		this.mustUpdate = mustUpdate;
	}
	/**  
	 * version.  
	 *  
	 * @return  the version  
	 * @since   JDK 1.6  
	 */
	public String getVersion() {
		return version;
	}
	/**  
	 * version.  
	 *  
	 * @param   version    the version to set  
	 * @since   JDK 1.6  
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	/**  
	 * download.  
	 *  
	 * @return  the download  
	 * @since   JDK 1.6  
	 */
	public String getDownload() {
		return download;
	}
	/**  
	 * download.  
	 *  
	 * @param   download    the download to set  
	 * @since   JDK 1.6  
	 */
	public void setDownload(String download) {
		this.download = download;
	}
	/**  
	 * platform.  
	 *  
	 * @return  the platform  
	 * @since   JDK 1.6  
	 */
	public String getPlatform() {
		return platform;
	}
	/**  
	 * platform.  
	 *  
	 * @param   platform    the platform to set  
	 * @since   JDK 1.6  
	 */
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	/**  
	 * description.  
	 *  
	 * @return  the description  
	 * @since   JDK 1.6  
	 */
	public String getDescription() {
		return description;
	}
	/**  
	 * description.  
	 *  
	 * @param   description    the description to set  
	 * @since   JDK 1.6  
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**  
	 * status.  
	 *  
	 * @return  the status  
	 * @since   JDK 1.6  
	 */
	public int getStatus() {
		return status;
	}
	/**  
	 * status.  
	 *  
	 * @param   status    the status to set  
	 * @since   JDK 1.6  
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	/**  
	 * timestamp.  
	 *  
	 * @return  the timestamp  
	 * @since   JDK 1.6  
	 */
	public long getTimestamp() {
		return timestamp;
	}
	/**  
	 * timestamp.  
	 *  
	 * @param   timestamp    the timestamp to set  
	 * @since   JDK 1.6  
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	private String buildVersion;
	private int mustUpdate;
	private String version;
	private String download;
	private String platform;
	private String description;
	private int status;
	private long timestamp;
}
  
