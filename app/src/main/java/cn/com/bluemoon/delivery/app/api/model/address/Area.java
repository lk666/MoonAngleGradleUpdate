package cn.com.bluemoon.delivery.app.api.model.address;

import java.util.List;

public class Area {
	private String  dcode;
	/**  
	 * dcode.  
	 *  
	 * @return  the dcode  
	 * @since   JDK 1.6  
	 */
	public String getDcode() {
		return dcode;
	}
	/**  
	 * dcode.  
	 *  
	 * @param   dcode    the dcode to set  
	 * @since   JDK 1.6  
	 */
	public void setDcode(String dcode) {
		this.dcode = dcode;
	}
	/**  
	 * dname.  
	 *  
	 * @return  the dname  
	 * @since   JDK 1.6  
	 */
	public String getDname() {
		return dname;
	}
	/**  
	 * dname.  
	 *  
	 * @param   dname    the dname to set  
	 * @since   JDK 1.6  
	 */
	public void setDname(String dname) {
		this.dname = dname;
	}
	/**  
	 * pcode.  
	 *  
	 * @return  the pcode  
	 * @since   JDK 1.6  
	 */
	public String getPcode() {
		return pcode;
	}
	/**  
	 * pcode.  
	 *  
	 * @param   pcode    the pcode to set  
	 * @since   JDK 1.6  
	 */
	public void setPcode(String pcode) {
		this.pcode = pcode;
	}
	/**  
	 * type.  
	 *  
	 * @return  the type  
	 * @since   JDK 1.6  
	 */
	public String getType() {
		return type;
	}
	/**  
	 * type.  
	 *  
	 * @param   type    the type to set  
	 * @since   JDK 1.6  
	 */
	public void setType(String type) {
		this.type = type;
	}
	private String  dname;
	private String  pcode;
	private String  type;
    private List<Area> itemList;
	/**  
	 * itemList.  
	 *  
	 * @return  the itemList  
	 * @since   JDK 1.6  
	 */
	public List<Area> getItemList() {
		return itemList;
	}
	/**  
	 * itemList.  
	 *  
	 * @param   itemList    the itemList to set  
	 * @since   JDK 1.6  
	 */
	public void setItemList(List<Area> itemList) {
		this.itemList = itemList;
	}
}
  
