package cn.com.bluemoon.delivery.app.api.model;  

import java.io.Serializable;
import java.util.List;
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
  
