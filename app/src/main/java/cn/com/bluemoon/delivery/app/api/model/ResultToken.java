package cn.com.bluemoon.delivery.app.api.model;  

public class ResultToken extends ResultBase {
    /**  
	 * token.  
	 *  
	 * @return  the token  
	 * @since   JDK 1.6  
	 */
	public String getToken() {
		return token;
	}

	/**  
	 * token.  
	 *  
	 * @param   token    the token to set  
	 * @since   JDK 1.6  
	 */
	public void setToken(String token) {
		this.token = token;
	}

	private String token;
}
  
