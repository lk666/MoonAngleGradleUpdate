package cn.com.bluemoon.delivery.app.api.model;  

import java.util.List;

public class ResultLogistics extends ResultBase {

	private String responseMsg;
	private String inventoryCode;
	private String inventoryName;
	private String inventoryMobilno;
	private String dispatchId;
	private List<Logistics> itemList;
	/**  
	 * responseMsg.  
	 *  
	 * @return  the responseMsg  
	 * @since   JDK 1.6  
	 */
	public String getResponseMsg() {
		return responseMsg;
	}
	/**  
	 * responseMsg.  
	 *  
	 * @param   responseMsg    the responseMsg to set  
	 * @since   JDK 1.6  
	 */
	public void setResponseMsg(String responseMsg) {
		this.responseMsg = responseMsg;
	}
	/**  
	 * inventoryCode.  
	 *  
	 * @return  the inventoryCode  
	 * @since   JDK 1.6  
	 */
	public String getInventoryCode() {
		return inventoryCode;
	}
	/**  
	 * inventoryCode.  
	 *  
	 * @param   inventoryCode    the inventoryCode to set  
	 * @since   JDK 1.6  
	 */
	public void setInventoryCode(String inventoryCode) {
		this.inventoryCode = inventoryCode;
	}
	/**  
	 * inventoryName.  
	 *  
	 * @return  the inventoryName  
	 * @since   JDK 1.6  
	 */
	public String getInventoryName() {
		return inventoryName;
	}
	/**  
	 * inventoryName.  
	 *  
	 * @param   inventoryName    the inventoryName to set  
	 * @since   JDK 1.6  
	 */
	public void setInventoryName(String inventoryName) {
		this.inventoryName = inventoryName;
	}
	/**  
	 * inventoryMobilno.  
	 *  
	 * @return  the inventoryMobilno  
	 * @since   JDK 1.6  
	 */
	public String getInventoryMobilno() {
		return inventoryMobilno;
	}
	/**  
	 * inventoryMobilno.  
	 *  
	 * @param   inventoryMobilno    the inventoryMobilno to set  
	 * @since   JDK 1.6  
	 */
	public void setInventoryMobilno(String inventoryMobilno) {
		this.inventoryMobilno = inventoryMobilno;
	}
	/**  
	 * dispatchId.  
	 *  
	 * @return  the dispatchId  
	 * @since   JDK 1.6  
	 */
	public String getDispatchId() {
		return dispatchId;
	}
	/**  
	 * dispatchId.  
	 *  
	 * @param   dispatchId    the dispatchId to set  
	 * @since   JDK 1.6  
	 */
	public void setDispatchId(String dispatchId) {
		this.dispatchId = dispatchId;
	}
	/**  
	 * itemList.  
	 *  
	 * @return  the itemList  
	 * @since   JDK 1.6  
	 */
	public List<Logistics> getItemList() {
		return itemList;
	}
	/**  
	 * itemList.  
	 *  
	 * @param   itemList    the itemList to set  
	 * @since   JDK 1.6  
	 */
	public void setItemList(List<Logistics> itemList) {
		this.itemList = itemList;
	}

}
  
