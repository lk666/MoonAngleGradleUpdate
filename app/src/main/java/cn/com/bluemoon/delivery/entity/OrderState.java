package cn.com.bluemoon.delivery.entity;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.module.order.HistoryFragment;
import cn.com.bluemoon.delivery.module.order.PendingAppointmentFragment;
import cn.com.bluemoon.delivery.module.order.PendingDeliveryFragment;
import cn.com.bluemoon.delivery.module.order.PendingOrdersFragment;
import cn.com.bluemoon.delivery.module.order.PendingReceiptFragment;

public enum OrderState {
	/** ���ӵ� */
	PENDINGORDERS(PendingOrdersFragment.class, R.drawable.tab_orders, R.string.tab_orders),
	/** ��ԤԼ */
	PENDINGAPPOINTMENT(PendingAppointmentFragment.class, R.drawable.tab_appointment,R.string.tab_appointment),
	/** ���ջ� */
	PENDINGDELIVERY(PendingDeliveryFragment.class, R.drawable.tab_delivery_selector, R.string.tab_delivery),
	/** ��ǩ�� */
	PENDINGRECEIPT(PendingReceiptFragment.class, R.drawable.tab_receipt, R.string.tab_receipt),
	/** ��ʷ�� */
	HISTORY(HistoryFragment.class, R.drawable.tab_history, R.string.tab_history);
	
	private Class clazz;
	private int image;
	private int content;

	private OrderState(Class clazz, int image, int content) {
		this.clazz = clazz;
		this.image = image;
		this.content = content;
	}

	public Class getClazz() {
		return clazz;
	}

	public int getImage() {
		return image;
	}

	public int getContent() {
		return content;
	}

}
