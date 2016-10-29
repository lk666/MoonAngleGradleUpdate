package cn.com.bluemoon.delivery.app.api.model.other;

import java.util.List;


public class OrderInfo {
	private String  dispatchId;
	private String  orderId;
	private String  source;
	private String  payOrderTime;
	private String  subscribeTime;
	private String  deliveryTime;
	private String  signTime;
	private String  customerName;
	private String  mobilePhone;
	private String  region;
	private String  address;
	private int  cateAmount;
	private int  totalAmount;
	private String  totalPrice;
	private String  storehouseCode;
	private String  storehouseName;
	private String  storechargeCode;
	private String  storechargeName;
	private String  storechargeMobileno;
	private int  returnState;
	private int  exchangeState;
	private List<Product>  productList;
	private String dispatchStatus;
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
	public String getDispatchStatus() {
		return dispatchStatus;
	}
	public void setDispatchStatus(String dispatchStatus) {
		this.dispatchStatus = dispatchStatus;
	}
	/**  
	 * dispatchId.  
	 *  
	 * @return  the dispatchId  
	 * @since   JDK 1.6  
	 */
	public String getDispatchId() {
		return dispatchId;
	}
	/**  
	 * dispatchId.  
	 *  
	 * @param   dispatchId    the dispatchId to set  
	 * @since   JDK 1.6  
	 */
	public void setDispatchId(String dispatchId) {
		this.dispatchId = dispatchId;
	}
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
	public String getSource() {
		return source;
	}
	/**  
	 * orderSource.  
	 */
	public void setSource(String source) {
		this.source = source;
	}
	/**  
	 * payOrderTime.  
	 *  
	 * @return  the payOrderTime  
	 * @since   JDK 1.6  
	 */
	public String getPayOrderTime() {
		return payOrderTime;
	}
	/**  
	 * payOrderTime.  
	 *  
	 * @param   payOrderTime    the payOrderTime to set  
	 * @since   JDK 1.6  
	 */
	public void setPayOrderTime(String payOrderTime) {
		this.payOrderTime = payOrderTime;
	}
	/**  
	 * subscribeTime.  
	 *  
	 * @return  the subscribeTime  
	 * @since   JDK 1.6  
	 */
	public String getSubscribeTime() {
		return subscribeTime;
	}
	/**  
	 * subscribeTime.  
	 *  
	 * @param   subscribeTime    the subscribeTime to set  
	 * @since   JDK 1.6  
	 */
	public void setSubscribeTime(String subscribeTime) {
		this.subscribeTime = subscribeTime;
	}
	/**  
	 * deliveryTime.  
	 *  
	 * @return  the deliveryTime  
	 * @since   JDK 1.6  
	 */
	public String getDeliveryTime() {
		return deliveryTime;
	}
	/**  
	 * deliveryTime.  
	 *  
	 * @param   deliveryTime    the deliveryTime to set  
	 * @since   JDK 1.6  
	 */
	public void setDeliveryTime(String deliveryTime) {
		this.deliveryTime = deliveryTime;
	}
	/**  
	 * signTime.  
	 *  
	 * @return  the signTime  
	 * @since   JDK 1.6  
	 */
	public String getSignTime() {
		return signTime;
	}
	/**  
	 * signTime.  
	 *  
	 * @param   signTime    the signTime to set  
	 * @since   JDK 1.6  
	 */
	public void setSignTime(String signTime) {
		this.signTime = signTime;
	}
	/**  
	 * customerName.  
	 *  
	 * @return  the customerName  
	 * @since   JDK 1.6  
	 */
	public String getCustomerName() {
		return customerName;
	}
	/**  
	 * customerName.  
	 *  
	 * @param   customerName    the customerName to set  
	 * @since   JDK 1.6  
	 */
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	/**  
	 * mobilePhone.  
	 *  
	 * @return  the mobilePhone  
	 * @since   JDK 1.6  
	 */
	public String getMobilePhone() {
		return mobilePhone;
	}
	/**  
	 * mobilePhone.  
	 *  
	 * @param   mobilePhone    the mobilePhone to set  
	 * @since   JDK 1.6  
	 */
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	/**  
	 * region.  
	 *  
	 * @return  the region  
	 * @since   JDK 1.6  
	 */
	public String getRegion() {
		return region;
	}
	/**  
	 * region.  
	 *  
	 * @param   region    the region to set  
	 * @since   JDK 1.6  
	 */
	public void setRegion(String region) {
		this.region = region;
	}
	/**  
	 * address.  
	 *  
	 * @return  the address  
	 * @since   JDK 1.6  
	 */
	public String getAddress() {
		return address;
	}
	/**  
	 * address.  
	 *  
	 * @param   address    the address to set  
	 * @since   JDK 1.6  
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	/**  
	 * cateAmount.  
	 *  
	 * @return  the cateAmount  
	 * @since   JDK 1.6  
	 */
	public int getCateAmount() {
		return cateAmount;
	}
	/**  
	 * cateAmount.  
	 *  
	 * @param   cateAmount    the cateAmount to set  
	 * @since   JDK 1.6  
	 */
	public void setCateAmount(int cateAmount) {
		this.cateAmount = cateAmount;
	}
	/**  
	 * totalAmount.  
	 *  
	 * @return  the totalAmount  
	 * @since   JDK 1.6  
	 */
	public int getTotalAmount() {
		return totalAmount;
	}
	/**  
	 * totalAmount.  
	 *  
	 * @param   totalAmount    the totalAmount to set  
	 * @since   JDK 1.6  
	 */
	public void setTotalAmount(int totalAmount) {
		this.totalAmount = totalAmount;
	}
	/**  
	 * totalPrice.  
	 *  
	 * @return  the totalPrice  
	 * @since   JDK 1.6  
	 */
	public String getTotalPrice() {
		return totalPrice;
	}
	/**  
	 * totalPrice.  
	 *  
	 * @param   totalPrice    the totalPrice to set  
	 * @since   JDK 1.6  
	 */
	public void setTotalPrice(String totalPrice) {
		this.totalPrice = totalPrice;
	}
	/**  
	 * storehouseCode.  
	 *  
	 * @return  the storehouseCode  
	 * @since   JDK 1.6  
	 */
	public String getStorehouseCode() {
		return storehouseCode;
	}
	/**  
	 * storehouseCode.  
	 *  
	 * @param   storehouseCode    the storehouseCode to set  
	 * @since   JDK 1.6  
	 */
	public void setStorehouseCode(String storehouseCode) {
		this.storehouseCode = storehouseCode;
	}
	/**  
	 * storehouseName.  
	 *  
	 * @return  the storehouseName  
	 * @since   JDK 1.6  
	 */
	public String getStorehouseName() {
		return storehouseName;
	}
	/**  
	 * storehouseName.  
	 *  
	 * @param   storehouseName    the storehouseName to set  
	 * @since   JDK 1.6  
	 */
	public void setStorehouseName(String storehouseName) {
		this.storehouseName = storehouseName;
	}
	/**  
	 * storechargeCode.  
	 *  
	 * @return  the storechargeCode  
	 * @since   JDK 1.6  
	 */
	public String getStorechargeCode() {
		return storechargeCode;
	}
	/**  
	 * storechargeCode.  
	 *  
	 * @param   storechargeCode    the storechargeCode to set  
	 * @since   JDK 1.6  
	 */
	public void setStorechargeCode(String storechargeCode) {
		this.storechargeCode = storechargeCode;
	}
	/**  
	 * storechargeName.  
	 *  
	 * @return  the storechargeName  
	 * @since   JDK 1.6  
	 */
	public String getStorechargeName() {
		return storechargeName;
	}
	/**  
	 * storechargeName.  
	 *  
	 * @param   storechargeName    the storechargeName to set  
	 * @since   JDK 1.6  
	 */
	public void setStorechargeName(String storechargeName) {
		this.storechargeName = storechargeName;
	}
	/**  
	 * storechargeMobileno.  
	 *  
	 * @return  the storechargeMobileno  
	 * @since   JDK 1.6  
	 */
	public String getStorechargeMobileno() {
		return storechargeMobileno;
	}
	/**  
	 * storechargeMobileno.  
	 *  
	 * @param   storechargeMobileno    the storechargeMobileno to set  
	 * @since   JDK 1.6  
	 */
	public void setStorechargeMobileno(String storechargeMobileno) {
		this.storechargeMobileno = storechargeMobileno;
	}
	/**  
	 * returnState.  
	 *  
	 * @return  the returnState  
	 * @since   JDK 1.6  
	 */
	public int getReturnState() {
		return returnState;
	}
	/**  
	 * returnState.  
	 *  
	 * @param   returnState    the returnState to set  
	 * @since   JDK 1.6  
	 */
	public void setReturnState(int returnState) {
		this.returnState = returnState;
	}
	/**  
	 * exchangeState.  
	 *  
	 * @return  the exchangeState  
	 * @since   JDK 1.6  
	 */
	public int getExchangeState() {
		return exchangeState;
	}
	/**  
	 * exchangeState.  
	 *  
	 * @param   exchangeState    the exchangeState to set  
	 * @since   JDK 1.6  
	 */
	public void setExchangeState(int exchangeState) {
		this.exchangeState = exchangeState;
	}
	/**  
	 * productList.  
	 *  
	 * @return  the productList  
	 * @since   JDK 1.6  
	 */
	public List<Product> getProductList() {
		return productList;
	}
	/**  
	 * productList.  
	 *  
	 * @param   productList    the productList to set  
	 * @since   JDK 1.6  
	 */
	public void setProductList(List<Product> productList) {
		this.productList = productList;
	}

}
  