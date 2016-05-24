package cn.com.bluemoon.delivery.app.api.model;  

import java.util.List;
public class ResultOrderVo extends ResultBase {
  private List<OrderVo> itemList;

/**  
 * itemList.  
 *  
 * @return  the itemList  
 * @since   JDK 1.6  
 */
public List<OrderVo> getItemList() {
	return itemList;
}

/**  
 * itemList.  
 *  
 * @param   itemList    the itemList to set  
 * @since   JDK 1.6  
 */
public void setItemList(List<OrderVo> itemList) {
	this.itemList = itemList;
}
}
  
