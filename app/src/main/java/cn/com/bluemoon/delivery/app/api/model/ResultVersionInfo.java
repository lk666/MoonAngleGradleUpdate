package cn.com.bluemoon.delivery.app.api.model;  


public class ResultVersionInfo extends ResultBase {
	private Version itemList;

	/**  
	 * itemList.  
	 *  
	 * @return  the itemList  
	 * @since   JDK 1.6  
	 */
	public Version getItemList() {
		return itemList;
	}

	/**  
	 * itemList.  
	 *  
	 * @param   itemList    the itemList to set  
	 * @since   JDK 1.6  
	 */
	public void setItemList(Version itemList) {
		this.itemList = itemList;
	}
}
  
