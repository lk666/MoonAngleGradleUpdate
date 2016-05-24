package cn.com.bluemoon.delivery.app.api.model;  


public class ResultUser extends ResultBase {
   private User user;

/**  
 * user.  
 *  
 * @return  the user  
 * @since   JDK 1.6  
 */
public User getUser() {
	return user;
}

/**  
 * user.  
 *  
 * @param   user    the user to set  
 * @since   JDK 1.6  
 */
public void setUser(User user) {
	this.user = user;
}
}
  
