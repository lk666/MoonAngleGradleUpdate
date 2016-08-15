package cn.com.bluemoon.delivery.entity;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.module.storage.StockFragment;
import cn.com.bluemoon.delivery.module.storage.WarehouseFragment;

/**
 * Created by allenli on 2016/3/22.
 */
public enum StorageTabState {

    STOCK(StockFragment.class, R.drawable.tab_stock_selector,R.string.tab_bottom_my_stock_text),

    WAREHOUSE(WarehouseFragment.class, R.drawable.tab_my_warehouse_selector, R.string.tab_bottom_my_warehouse_text);



    private Class clazz;
    private int image;
    private int content;



    private StorageTabState(Class clazz, int image, int content) {
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
