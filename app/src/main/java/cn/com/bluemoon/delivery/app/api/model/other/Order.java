package cn.com.bluemoon.delivery.app.api.model.other;


public class Order {
	private String orderId;
	private String orderSource;
	private String totalPrice;
	private long signTime;
	private String address;
	/**  
	 * orderId.  
	 *  
	 * @return  the orderId  
	 * @since   JDK 1.6  
	 */
	public String getOrderId() {
		return orderId;
	}
	/**  
	 * orderId.  
	 *  
	 * @param   orderId    the orderId to set  
	 * @since   JDK 1.6  
	 */
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	/**  
	 * orderSource.  
	 *  
	 * @return  the orderSource  
	 * @since   JDK 1.6  
	 */
	public String getOrderSource() {
		return orderSource;
	}
	/**  
	 * orderSource.  
	 *  
	 * @param   orderSource    the orderSource to set  
	 * @since   JDK 1.6  
	 */
	public void setOrderSource(String orderSource) {
		this.orderSource = orderSource;
	}
	/**  
	 * totalPrice.  
	 *  
	 * @return  the totalPrice  
	 * @since   JDK 1.6  
	 */
	public String getTotalPrice() {
		return totalPrice;
	}
	/**  
	 * totalPrice.  
	 *  
	 * @param   totalPrice    the totalPrice to set  
	 * @since   JDK 1.6  
	 */
	public void setTotalPrice(String totalPrice) {
		this.totalPrice = totalPrice;
	}
	
	
	/**  
	 * signTime.  
	 *  
	 * @return  the signTime  
	 * @since   JDK 1.6  
	 */
	public long getSignTime() {
		return signTime;
	}
	/**  
	 * signTime.  
	 *  
	 * @param   signTime    the signTime to set  
	 * @since   JDK 1.6  
	 */
	public void setSignTime(long signTime) {
		this.signTime = signTime;
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
  
