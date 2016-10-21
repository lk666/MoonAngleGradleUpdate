/** 
 * Description:
 * Copyright: Copyright (c) 2016
 * Company: BLUE MOON
 * @author: Eric Liang 
 * @version 1.0
 * @date: 2016/3/2/
 */
package cn.com.bluemoon.delivery.app.api.model.other;

import java.io.Serializable;
import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

public class ResultOrderInfoPickup extends ResultBase implements Serializable{
	
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
	private String storechargeMobileno;
	private String storehouseAddress;
	private List<Product> productList;
	
	public List<Product> getProductList() {
		return productList;
	}

	public void setProductList(List<Product> productList) {
		this.productList = productList;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getOrderSource() {
		return orderSource;
	}

	public void setOrderSource(String orderSource) {
		this.orderSource = orderSource;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(String totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getOrderCreateTime() {
		return orderCreateTime;
	}

	public void setOrderCreateTime(String orderCreateTime) {
		this.orderCreateTime = orderCreateTime;
	}

	public String getPickupCode() {
		return pickupCode;
	}

	public void setPickupCode(String pickupCode) {
		this.pickupCode = pickupCode;
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

	public String getStorehouseAddress() {
		return storehouseAddress;
	}

	public void setStorehouseAddress(String storehouseAddress) {
		this.storehouseAddress = storehouseAddress;
	}
}
