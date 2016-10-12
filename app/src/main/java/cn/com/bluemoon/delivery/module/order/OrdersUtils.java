/** 
 * Description:
 * Copyright: Copyright (c) 2016
 * Company: BLUE MOON
 * @author: Eric Liang 
 * @version 1.0
 * @date: 2016/1/13
 */
package cn.com.bluemoon.delivery.module.order;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.other.OrderVo;
import cn.com.bluemoon.delivery.app.api.model.other.ResultOrderInfoPickup;
import cn.com.bluemoon.delivery.app.api.model.other.Storehouse;

/**
 * @author Administrator
 *
 */
public class OrdersUtils {
	
	public static int dp2px(int dp, Context context) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
	}

	public static CharSequence formatLongString(String str, TextView tv) {
		if (str == null) {
			return null;
		}
		return TextUtils.ellipsize(str,  tv.getPaint(), 400,TextUtils.TruncateAt.END);
	}
	
	public static String getStorehouseString(OrderVo order, Context context) {
		if (!StringUtils.isNotBlank(order.getStorehouseCode())
				&& !StringUtils.isNotBlank(order.getStorehouseName())
				&& !StringUtils.isNotBlank(order.getStorechargeName())) {
			return context.getString(R.string.pending_order_storehouse_null);
		} else {
			StringBuffer strBuffer = new StringBuffer();
			if (StringUtils.isNotBlank(order.getStorehouseCode())) {
				strBuffer.append(order.getStorehouseCode());
			}
			if (StringUtils.isNotBlank(order.getStorehouseName())) {
				strBuffer.append("-").append(order.getStorehouseName());
			}
			if (StringUtils.isNotBlank(order.getStorechargeName())) {
				strBuffer.append("-").append(order.getStorechargeName());
			}
			return strBuffer.toString();
		}
	}

	public static String getShString(ResultOrderInfoPickup order) {
		StringBuffer strBuffer = new StringBuffer();
		if (StringUtils.isNotBlank(order.getStorehouseCode())) {
			strBuffer.append(order.getStorehouseCode());
		}
		if (StringUtils.isNotBlank(order.getStorehouseName())) {
			strBuffer.append("-").append(order.getStorehouseName());
		}
		if (StringUtils.isNotBlank(order.getStorechargeName())) {
			strBuffer.append("-").append(order.getStorechargeName());
		}
		return  strBuffer.toString();
	}
	
	public static String getShContent(Storehouse order) {

		StringBuffer strBuffer = new StringBuffer();
		if (StringUtils.isNotBlank(order.getStorehouseCode())) {
			strBuffer.append(order.getStorehouseCode());
		}
		if (StringUtils.isNotBlank(order.getStorehouseName())) {
			strBuffer.append("-").append(order.getStorehouseName());
		}
		if (StringUtils.isNotBlank(order.getStorechargeName())) {
			strBuffer.append("-").append(order.getStorechargeName());
		}

		order.setContent(strBuffer.toString());
		return order.getContent();
	}

}
