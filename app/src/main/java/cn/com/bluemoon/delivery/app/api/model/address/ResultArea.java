package cn.com.bluemoon.delivery.app.api.model.address;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

public class ResultArea extends ResultBase {
  /**  
	 * lists.  
	 *  
	 * @return  the lists  
	 * @since   JDK 1.6  
	 */
	public List<Area> getLists() {
		return lists;
	}

	/**  
	 * lists.  
	 *  
	 * @param   lists    the lists to set  
	 * @since   JDK 1.6  
	 */
	public void setLists(List<Area> lists) {
		this.lists = lists;
	}

private List<Area> lists;
}
  
