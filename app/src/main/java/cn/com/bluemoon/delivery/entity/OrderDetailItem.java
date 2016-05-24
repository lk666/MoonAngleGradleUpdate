package cn.com.bluemoon.delivery.entity;

import java.io.Serializable;
import java.util.List;

public class OrderDetailItem extends BaseAPIResult implements Serializable{

	private String dispatchId;
	private String orderId;
	private String source;
	private String payOrderTime;
	private String subscribeTime;
	private String deliveryTime;
	private String signTime;
	private String mobilePhone;
	private String region;
	private String address;
	private int cateAmount;
	private int totalAmount;
	private String totalPrice;
	private List<ProductItem> productList;
	private String customerName;
	private String nickName;
	private String nickPhone;
	/**  
	 * nickName.  
	 *  
	 * @return  the nickName  
	 * @since   JDK 1.6  
	 */
	public String getNickName() {
		return nickName;
	}
	/**  
	 * nickName.  
	 *  
	 * @param   nickName    the nickName to set  
	 * @since   JDK 1.6  
	 */
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	/**  
	 * nickPhone.  
	 *  
	 * @return  the nickPhone  
	 * @since   JDK 1.6  
	 */
	public String getNickPhone() {
		return nickPhone;
	}
	/**  
	 * nickPhone.  
	 *  
	 * @param   nickPhone    the nickPhone to set  
	 * @since   JDK 1.6  
	 */
	public void setNickPhone(String nickPhone) {
		this.nickPhone = nickPhone;
	}
	private String storehouseCode;
	private String storehouseName;
	private String storechargeCode;
	private String storechargeName;
	private String storechargeMobileno;
	public String getDispatchId() {
		return dispatchId;
	}
	public void setDispatchId(String dispatchId) {
		this.dispatchId = dispatchId;
	}
	public String getOrderId() {
		return orderId;
	}
	public String getSignTime() {
		return signTime;
	}
	public void setSignTime(String signTime) {
		this.signTime = signTime;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getPayOrderTime() {
		return payOrderTime;
	}
	public void setPayOrderTime(String payOrderTime) {
		this.payOrderTime = payOrderTime;
	}
	public String getSubscribeTime() {
		return subscribeTime;
	}
	public void setSubscribeTime(String subscribeTime) {
		this.subscribeTime = subscribeTime;
	}
	public String getDeliveryTime() {
		return deliveryTime;
	}
	public void setDeliveryTime(String deliveryTime) {
		this.deliveryTime = deliveryTime;
	}
	public String getMobilePhone() {
		return mobilePhone;
	}
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public int getCateAmount() {
		return cateAmount;
	}
	public void setCateAmount(int cateAmount) {
		this.cateAmount = cateAmount;
	}
	public int getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(int totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(String totalPrice) {
		this.totalPrice = totalPrice;
	}
	public List<ProductItem> getProductList() {
		return productList;
	}
	public void setProductList(List<ProductItem> productList) {
		this.productList = productList;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getStorehouseCode() {
		return storehouseCode;
	}
	public void setStorehouseCode(String storehouseCode) {
		this.storehouseCode = storehouseCode;
	}
	public String getStorehouseName() {
		return storehouseName;
	}
	public void setStorehouseName(String storehouseName) {
		this.storehouseName = storehouseName;
	}
	public String getStorechargeCode() {
		return storechargeCode;
	}
	public void setStorechargeCode(String storechargeCode) {
		this.storechargeCode = storechargeCode;
	}
	public String getStorechargeName() {
		return storechargeName;
	}
	public void setStorechargeName(String storechargeName) {
		this.storechargeName = storechargeName;
	}
	public String getStorechargeMobileno() {
		return storechargeMobileno;
	}
	public void setStorechargeMobileno(String storechargeMobileno) {
		this.storechargeMobileno = storechargeMobileno;
	}
	
	
}
