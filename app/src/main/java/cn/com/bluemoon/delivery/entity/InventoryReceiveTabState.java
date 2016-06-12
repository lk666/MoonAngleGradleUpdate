package cn.com.bluemoon.delivery.entity;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.inventory.ProcessedFragment;
import cn.com.bluemoon.delivery.inventory.SuspenseFragment;

/**
 * Created by allenli on 2016/3/22.
 */
public enum InventoryReceiveTabState {


    RECEIVE(SuspenseFragment.class, R.drawable.tab_receive_selector, R.string.tab_bottom_receive_text,"RECEIVE_MANAGEMENT"),

    RECEIVED(ProcessedFragment.class, R.drawable.tab_received_selector,R.string.tab_bottom_received_text,"RECEIVE_MANAGEMENT");

    private Class clazz;
    private int image;
    private int content;

    public String getManager() {
        return manager;
    }

    private String manager;


    private InventoryReceiveTabState(Class clazz, int image, int content,String manager) {
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
