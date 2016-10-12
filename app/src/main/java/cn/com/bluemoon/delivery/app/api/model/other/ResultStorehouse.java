package cn.com.bluemoon.delivery.app.api.model.other;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

public class ResultStorehouse extends ResultBase {
 private List<Storehouse> itemList;

/**  
 * itemList.  
 *  
 * @return  the itemList  
 * @since   JDK 1.6  
 */
public List<Storehouse> getItemList() {
	return itemList;
}

/**  
 * itemList.  
 *  
 * @param   itemList    the itemList to set  
 * @since   JDK 1.6  
 */
public void setItemList(List<Storehouse> itemList) {
	this.itemList = itemList;
}
}
  
