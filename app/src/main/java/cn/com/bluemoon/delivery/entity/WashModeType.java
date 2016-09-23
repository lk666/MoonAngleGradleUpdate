package cn.com.bluemoon.delivery.entity;

/**
 * Created by bm on 2016/9/23.
 */
public enum WashModeType {
    BACK_ORDER_MANAGE_MODEL("EXPRESS_RECEIPT","WAIT_BACK_CLOTHES","WAIT_CLOTHES_SIGN"),//还衣单管理
    CABINET_BACK_ORDER_MODEL("WAIT_PACKED","WAIT_PACKING_BOX"),//还衣单打包
    CARRIAGE_RECEIVE_MODEL("WAIT_CARRIAGE_SIGN"),//承运签收
    CLOSE_BOX_MODEL("WAIT_SEALED_BOX"),//封箱
    DRIVER_CARRIER_MODEL("WAIT_LOADED","WAIT_ARRIVED");//司机承运


    private String[] strs;

    WashModeType(String... strs){
        this.strs = strs;
    }

    public String[] getStrs() {
        return strs;
    }

    public void setStrs(String[] strs) {
        this.strs = strs;
    }

    public String getName(){
        return name();
    }
}
