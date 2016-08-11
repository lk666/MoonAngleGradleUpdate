package cn.com.bluemoon.delivery.module.order;

import cn.com.bluemoon.delivery.app.api.model.OrderVo;

/**  
 * ClassName:OrderChoiseDateInterface <br/>  
 * Function: TODO ADD FUNCTION. <br/>  
 * Reason:   TODO ADD REASON. <br/>  
 * Date:     2016��2��26�� ����10:08:03 <br/>  
 * @author   allenli  
 * @version    
 * @since    JDK 1.6  
 * @see        
 */
/**  
 * ClassName: OrderChoiseDateInterface <br/>  
 * Function: TODO ADD FUNCTION. <br/>  
 * Reason: TODO ADD REASON(��ѡ). <br/>  
 * date: 2016��2��26�� ����10:08:03 <br/>  
 *  
 * @author allenli  
 * @version   
 * @since JDK 1.6  
 */
public interface IOrderChoiceDateListener {
	public void Choise(OrderVo orderVo, long time, String formatTime);
}
  
