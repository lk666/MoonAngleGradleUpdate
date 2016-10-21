package cn.com.bluemoon.delivery.app.api.model.other;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

public class ResultVailCode extends ResultBase {
   private String verifyCode;
   private int time;

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}


/**  
 * verifyCode.  
 *  
 * @return  the verifyCode  
 * @since   JDK 1.6  
 */
public String getVerifyCode() {
	return verifyCode;
}

/**  
 * verifyCode.  
 *  
 * @param   verifyCode    the verifyCode to set  
 * @since   JDK 1.6  
 */
public void setVerifyCode(String verifyCode) {
	this.verifyCode = verifyCode;
}


}
  
