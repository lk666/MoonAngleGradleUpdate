package cn.com.bluemoon.delivery.entity;  

import java.util.List;


public class OrderInfoPickupResult extends BaseAPIResult {

		private String orderId;

		private String orderSource;

		private String nickName;

		private String mobilePhone;

		private String totalPrice;

		private String orderCreateTime;

		private String pickupCode;

		private String storehouseCode;

		private String storehouseName;

		private String storechargeCode;

		private String storechargeName;
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
		 * orderCreateTime.  
		 *  
		 * @return  the orderCreateTime  
		 * @since   JDK 1.6  
		 */
		public String getOrderCreateTime() {
			return orderCreateTime;
		}

		/**  
		 * orderCreateTime.  
		 *  
		 * @param   orderCreateTime    the orderCreateTime to set  
		 * @since   JDK 1.6  
		 */
		public void setOrderCreateTime(String orderCreateTime) {
			this.orderCreateTime = orderCreateTime;
		}

		/**  
		 * pickupCode.  
		 *  
		 * @return  the pickupCode  
		 * @since   JDK 1.6  
		 */
		public String getPickupCode() {
			return pickupCode;
		}

		/**  
		 * pickupCode.  
		 *  
		 * @param   pickupCode    the pickupCode to set  
		 * @since   JDK 1.6  
		 */
		public void setPickupCode(String pickupCode) {
			this.pickupCode = pickupCode;
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
		 * storehouseAddress.  
		 *  
		 * @return  the storehouseAddress  
		 * @since   JDK 1.6  
		 */
		public String getStorehouseAddress() {
			return storehouseAddress;
		}

		/**  
		 * storehouseAddress.  
		 *  
		 * @param   storehouseAddress    the storehouseAddress to set  
		 * @since   JDK 1.6  
		 */
		public void setStorehouseAddress(String storehouseAddress) {
			this.storehouseAddress = storehouseAddress;
		}

		/**  
		 * productList.  
		 *  
		 * @return  the productList  
		 * @since   JDK 1.6  
		 */
		public List<ProductItem> getProductList() {
			return productList;
		}

		/**  
		 * productList.  
		 *  
		 * @param   productList    the productList to set  
		 * @since   JDK 1.6  
		 */
		public void setProductList(List<ProductItem> productList) {
			this.productList = productList;
		}


		private String storechargeMobileno;

		private String storehouseAddress;

		private List<ProductItem> productList;
}
  
