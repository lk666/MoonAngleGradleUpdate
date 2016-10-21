package cn.com.bluemoon.delivery.app.api.model.other;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

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
  
