
package cn.com.bluemoon.delivery.entity;

import java.util.List;

/**
 * @author Administrator
 *
 */
public class ItemListBean extends BaseAPIResult{
	
	public List<Item> itemList;
	
	public class Item {
		private String dispatchId;
		private String code;
		private String message;
		private String msg;
		public String getDispatchId() {
			return dispatchId;
		}
		public void setDispatchId(String dispatchId) {
			this.dispatchId = dispatchId;
		}
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		public String getMsg() {
			return msg;
		}
		public void setMsg(String msg) {
			this.msg = msg;
		}
	}

}
