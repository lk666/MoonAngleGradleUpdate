package cn.com.bluemoon.delivery.app.api.model.other;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

public class ResultOrder extends ResultBase {
	private int totalCount;
	/**  
	 * totalCount.  
	 *  
	 * @return  the totalCount  
	 * @since   JDK 1.6  
	 */
	public int getTotalCount() {
		return totalCount;
	}
	/**  
	 * totalCount.  
	 *  
	 * @param   totalCount    the totalCount to set  
	 * @since   JDK 1.6  
	 */
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	/**  
	 * totalAmount.  
	 *  
	 * @return  the totalAmount  
	 * @since   JDK 1.6  
	 */
	public String getTotalAmount() {
		return totalAmount;
	}
	/**  
	 * totalAmount.  
	 *  
	 * @param   totalAmount    the totalAmount to set  
	 * @since   JDK 1.6  
	 */
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	/**  
	 * itemList.  
	 *  
	 * @return  the itemList  
	 * @since   JDK 1.6  
	 */
	public List<Order> getItemList() {
		return itemList;
	}
	/**  
	 * itemList.  
	 *  
	 * @param   itemList    the itemList to set  
	 * @since   JDK 1.6  
	 */
	public void setItemList(List<Order> itemList) {
		this.itemList = itemList;
	}
	private String totalAmount;
	private List<Order> itemList;
	private long timestamp;
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	

}
  
