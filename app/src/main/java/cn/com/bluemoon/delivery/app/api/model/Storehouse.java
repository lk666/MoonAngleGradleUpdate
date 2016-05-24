package cn.com.bluemoon.delivery.app.api.model;  

import java.io.Serializable;


public class Storehouse implements Serializable{
	private String storehouseCode;
	private String storehouseName;
	private String storechargeCode;
	private String storechargeName;
	private String storechargeMobileno;
	private String address;
	private int isDefaultstore;
	private boolean isSelect;
	private String content;
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public boolean isSelect() {
		return isSelect;
	}
	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}
	public int getIsDefaultstore() {
		return isDefaultstore;
	}
	public void setIsDefaultstore(int isDefaultstore) {
		this.isDefaultstore = isDefaultstore;
	}
	/**  
	 * storehouseCode.  
	 *  
	 * @return  the storehouseCode  
	 * @since   JDK 1.6  
	 */
	public String getStorehouseCode() {
		return storehouseCode;
	}
	/**  
	 * storehouseCode.  
	 *  
	 * @param   storehouseCode    the storehouseCode to set  
	 * @since   JDK 1.6  
	 */
	public void setStorehouseCode(String storehouseCode) {
		this.storehouseCode = storehouseCode;
	}
	/**  
	 * storehouseName.  
	 *  
	 * @return  the storehouseName  
	 * @since   JDK 1.6  
	 */
	public String getStorehouseName() {
		return storehouseName;
	}
	/**  
	 * storehouseName.  
	 *  
	 * @param   storehouseName    the storehouseName to set  
	 * @since   JDK 1.6  
	 */
	public void setStorehouseName(String storehouseName) {
		this.storehouseName = storehouseName;
	}
	/**  
	 * storechargeCode.  
	 *  
	 * @return  the storechargeCode  
	 * @since   JDK 1.6  
	 */
	public String getStorechargeCode() {
		return storechargeCode;
	}
	/**  
	 * storechargeCode.  
	 *  
	 * @param   storechargeCode    the storechargeCode to set  
	 * @since   JDK 1.6  
	 */
	public void setStorechargeCode(String storechargeCode) {
		this.storechargeCode = storechargeCode;
	}
	/**  
	 * storechargeName.  
	 *  
	 * @return  the storechargeName  
	 * @since   JDK 1.6  
	 */
	public String getStorechargeName() {
		return storechargeName;
	}
	/**  
	 * storechargeName.  
	 *  
	 * @param   storechargeName    the storechargeName to set  
	 * @since   JDK 1.6  
	 */
	public void setStorechargeName(String storechargeName) {
		this.storechargeName = storechargeName;
	}
	/**  
	 * storechargeMobileno.  
	 *  
	 * @return  the storechargeMobileno  
	 * @since   JDK 1.6  
	 */
	public String getStorechargeMobileno() {
		return storechargeMobileno;
	}
	/**  
	 * storechargeMobileno.  
	 *  
	 * @param   storechargeMobileno    the storechargeMobileno to set  
	 * @since   JDK 1.6  
	 */
	public void setStorechargeMobileno(String storechargeMobileno) {
		this.storechargeMobileno = storechargeMobileno;
	}
	/**  
	 * address.  
	 *  
	 * @return  the address  
	 * @since   JDK 1.6  
	 */
	public String getAddress() {
		return address;
	}
	/**  
	 * address.  
	 *  
	 * @param   address    the address to set  
	 * @since   JDK 1.6  
	 */
	public void setAddress(String address) {
		this.address = address;
	}

}
  
