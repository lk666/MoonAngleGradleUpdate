
package cn.com.bluemoon.delivery.entity;

import java.io.Serializable;
import java.util.List;



public class GetStorehousesJsonResult extends BaseAPIResult implements Serializable{
	
	public List<Storehouse> itemList;
	
	public class Storehouse implements Serializable {
		
		private String storehouseCode;
		private String storehouseName;
		private String storechargeCode;
		private String storechargeName;
		private String storechargeMobileno;
		private String address;//:String(��ַ) 
		private int isDefaultstore;
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
		public String getAddress() {
			return address;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		public int getIsDefaultstore() {
			return isDefaultstore;
		}
		public void setIsDefaultstore(int isDefaultstore) {
			this.isDefaultstore = isDefaultstore;
		}
		
	}

}
