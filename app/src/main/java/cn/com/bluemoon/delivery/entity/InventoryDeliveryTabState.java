package cn.com.bluemoon.delivery.entity;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.module.inventory.ProcessedFragment;
import cn.com.bluemoon.delivery.module.inventory.SuspenseFragment;

/**
 * Created by allenli on 2016/3/22.
 */
public enum InventoryDeliveryTabState {


    DELIVERY(SuspenseFragment.class, R.drawable.tab_deliver_selector, R.string.tab_bottom_delivery_text,"DELIVERY_MANAGEMENT"),

    DELIVERED(ProcessedFragment.class, R.drawable.tab_delivered_selector,R.string.tab_bottom_delivered_text,"DELIVERY_MANAGEMENT");

    private Class clazz;
    private int image;
    private int content;

    public String getManager() {
        return manager;
    }

    private String manager;


    private InventoryDeliveryTabState(Class clazz, int image, int content, String manager) {
        this.clazz = clazz;
        this.image = image;
        this.content = content;
        this.manager = manager;
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
