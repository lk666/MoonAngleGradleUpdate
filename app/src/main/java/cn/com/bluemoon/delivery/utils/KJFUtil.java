package cn.com.bluemoon.delivery.utils;

import org.kymjs.kjframe.KJBitmap;

/**  
 * ClassName:KJFUtil <br/>  
 * Function: TODO ADD FUNCTION. <br/>  
 * Reason:   TODO ADD REASON. <br/>  
 * Date:     2015��10��19�� ����3:40:33 <br/>  
 * @author   allenli  
 * @version    
 * @since    JDK 1.6  
 * @see        
 */
/**  
 * ClassName: KJFUtil <br/>  
 * Function: TODO ADD FUNCTION. <br/>  
 * Reason: TODO ADD REASON(��ѡ). <br/>  
 * date: 2015��10��19�� ����3:40:33 <br/>  
 *  
 * @author allenli  
 * @version   
 * @since JDK 1.6  
 */
public class KJFUtil {
   private static final KJFUtil sington= new KJFUtil();
   private final  KJBitmap kjb;
   
   private KJFUtil(){
	   kjb = new KJBitmap();
   }
   
   public static KJFUtil getUtil(){
	   return sington;
   }
   
   public KJBitmap getKJB(){
	   return kjb;
   }
   
   public void freeUrl(String url){
	   kjb.getMemoryCache(url).recycle();
	   kjb.removeCache(url);
   }
   
}
  
