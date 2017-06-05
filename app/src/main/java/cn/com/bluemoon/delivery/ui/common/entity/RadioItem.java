package cn.com.bluemoon.delivery.ui.common.entity;

/**
 * Created by bm on 2017/5/27.
 */

public class RadioItem {

    public Object value;
    public String text;
    public int type;

    public RadioItem(Object value,String text,int type){
        this.value = value;
        this.text = text;
        this.type = type;
    }
}
