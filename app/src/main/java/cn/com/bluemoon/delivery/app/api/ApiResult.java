package cn.com.bluemoon.delivery.app.api;  

import java.io.Serializable;

public class ApiResult<T> implements Serializable {

	private int responseCode;
	private boolean isSuccess;
	private String responseMsg;
	private T data;
	
	/**
	 * responseCode.
	 * 
	 * @return the responseCode
	 * @since JDK 1.6
	 */
	public int getResponseCode() {
		return responseCode;
	}

	/**
	 * responseCode.
	 * 
	 * @param responseCode
	 *            the responseCode to set
	 * @since JDK 1.6
	 */
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	/**
	 * isSuccess.
	 * 
	 * @return the isSuccess
	 * @since JDK 1.6
	 */
	public boolean isSuccess() {
		return isSuccess;
	}

	/**
	 * isSuccess.
	 * 
	 * @param isSuccess
	 *            the isSuccess to set
	 * @since JDK 1.6
	 */
	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	/**
	 * responseMsg.
	 * 
	 * @return the responseMsg
	 * @since JDK 1.6
	 */
	public String getResponseMsg() {
		return responseMsg;
	}

	/**
	 * responseMsg.
	 * 
	 * @param responseMsg
	 *            the responseMsg to set
	 * @since JDK 1.6
	 */
	public void setResponseMsg(String responseMsg) {
		this.responseMsg = responseMsg;
	}

	/**  
	 * data.  
	 *  
	 * @return  the data  
	 * @since   JDK 1.6  
	 */
	public T getData() {
		return data;
	}

	/**  
	 * data.  
	 *  
	 * @param   data    the data to set  
	 * @since   JDK 1.6  
	 */
	public void setData(T data) {
		this.data = data;
	}

}
  
