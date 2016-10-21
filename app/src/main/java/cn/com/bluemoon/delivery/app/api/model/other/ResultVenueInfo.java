package cn.com.bluemoon.delivery.app.api.model.other;

import java.io.Serializable;
import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

public class ResultVenueInfo extends ResultBase implements Serializable{
 private List<VenueInfo> itemList;

/**  
 * itemList.  
 *  
 * @return  the itemList  
 * @since   JDK 1.6  
 */
public List<VenueInfo> getItemList() {
	return itemList;
}

/**  
 * itemList.  
 *  
 * @param   itemList    the itemList to set  
 * @since   JDK 1.6  
 */
public void setItemList(List<VenueInfo> itemList) {
	this.itemList = itemList;
}
}
  
