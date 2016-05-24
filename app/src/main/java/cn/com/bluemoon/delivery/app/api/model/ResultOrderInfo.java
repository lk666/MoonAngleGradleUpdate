package cn.com.bluemoon.delivery.app.api.model;
public class ResultOrderInfo extends ResultBase {
  private OrderInfo orderInfo;

/**  
 * orderInfo.  
 *  
 * @return  the orderInfo  
 * @since   JDK 1.6  
 */
public OrderInfo getOrderInfo() {
	return orderInfo;
}

/**  
 * orderInfo.  
 *  
 * @param   orderInfo    the orderInfo to set  
 * @since   JDK 1.6  
 */
public void setOrderInfo(OrderInfo orderInfo) {
	this.orderInfo = orderInfo;
}
}
  
