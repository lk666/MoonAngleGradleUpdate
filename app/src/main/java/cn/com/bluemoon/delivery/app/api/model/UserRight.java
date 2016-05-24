package cn.com.bluemoon.delivery.app.api.model;  

public class UserRight {
	private String menuId;
	private String menuCode;
	private String menuName;
	private String iconImg;
	private String url;
	private int iconResId;
	private int amount;
	private int groupNum;

	public int getGroupNum() {
		return groupNum;
	}

	public void setGroupNum(int groupNum) {
		this.groupNum = groupNum;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getIconResId() {
		return iconResId;
	}
	public void setIconResId(int iconResId) {
		this.iconResId = iconResId;
	}
	/**  
	 * menuId.  
	 *  
	 * @return  the menuId  
	 * @since   JDK 1.6  
	 */
	public String getMenuId() {
		return menuId;
	}
	/**  
	 * menuId.  
	 *  
	 * @param   menuId    the menuId to set  
	 * @since   JDK 1.6  
	 */
	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}
	/**  
	 * menuCode.  
	 *  
	 * @return  the menuCode  
	 * @since   JDK 1.6  
	 */
	public String getMenuCode() {
		return menuCode;
	}
	/**  
	 * menuCode.  
	 *  
	 * @param   menuCode    the menuCode to set  
	 * @since   JDK 1.6  
	 */
	public void setMenuCode(String menuCode) {
		this.menuCode = menuCode;
	}
	/**  
	 * menuName.  
	 *  
	 * @return  the menuName  
	 * @since   JDK 1.6  
	 */
	public String getMenuName() {
		return menuName;
	}
	/**  
	 * menuName.  
	 *  
	 * @param   menuName    the menuName to set  
	 * @since   JDK 1.6  
	 */
	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}
	/**  
	 * iconImg.  
	 *  
	 * @return  the iconImg  
	 * @since   JDK 1.6  
	 */
	public String getIconImg() {
		return iconImg;
	}
	/**  
	 * iconImg.  
	 *  
	 * @param   iconImg    the iconImg to set  
	 * @since   JDK 1.6  
	 */
	public void setIconImg(String iconImg) {
		this.iconImg = iconImg;
	}
	/**  
	 * url.  
	 *  
	 * @return  the url  
	 * @since   JDK 1.6  
	 */
	public String getUrl() {
		return url;
	}
	/**  
	 * url.  
	 *  
	 * @param   url    the url to set  
	 * @since   JDK 1.6  
	 */
	public void setUrl(String url) {
		this.url = url;
	}

}
  
