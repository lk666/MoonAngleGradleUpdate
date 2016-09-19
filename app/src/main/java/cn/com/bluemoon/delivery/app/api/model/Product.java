package cn.com.bluemoon.delivery.app.api.model;  

import java.io.Serializable;
import java.util.List;

public class Product implements Serializable{

	private String orderId;
	private String orderSource;
	private String buyNum;
	private String payPrice;
	private String shopProName;
	private String productCode;
	private String img;
	private List<Package> packageDetails;
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
	 * buyNum.  
	 *  
	 * @return  the buyNum  
	 * @since   JDK 1.6  
	 */
	public String getBuyNum() {
		return buyNum;
	}
	/**  
	 * buyNum.  
	 *  
	 * @param   buyNum    the buyNum to set  
	 * @since   JDK 1.6  
	 */
	public void setBuyNum(String buyNum) {
		this.buyNum = buyNum;
	}
	/**  
	 * payPrice.  
	 *  
	 * @return  the payPrice  
	 * @since   JDK 1.6  
	 */
	public String getPayPrice() {
		return payPrice;
	}
	/**  
	 * payPrice.  
	 *  
	 * @param   payPrice    the payPrice to set  
	 * @since   JDK 1.6  
	 */
	public void setPayPrice(String payPrice) {
		this.payPrice = payPrice;
	}
	/**  
	 * shopProName.  
	 *  
	 * @return  the shopProName  
	 * @since   JDK 1.6  
	 */
	public String getShopProName() {
		return shopProName;
	}
	/**  
	 * shopProName.  
	 *  
	 * @param   shopProName    the shopProName to set  
	 * @since   JDK 1.6  
	 */
	public void setShopProName(String shopProName) {
		this.shopProName = shopProName;
	}
	/**  
	 * productCode.  
	 *  
	 * @return  the productCode  
	 * @since   JDK 1.6  
	 */
	public String getProductCode() {
		return productCode;
	}
	/**  
	 * productCode.  
	 *  
	 * @param   productCode    the productCode to set  
	 * @since   JDK 1.6  
	 */
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	/**  
	 * img.  
	 *  
	 * @return  the img  
	 * @since   JDK 1.6  
	 */
	public String getImg() {
		return img;
	}
	/**  
	 * img.  
	 *  
	 * @param   img    the img to set  
	 * @since   JDK 1.6  
	 */
	public void setImg(String img) {
		this.img = img;
	}

	public List<Package> getPackageDetails() {
		return packageDetails;
	}

	public void setPackageDetails(List<Package> packageDetails) {
		this.packageDetails = packageDetails;
	}

}
  
