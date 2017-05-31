package cn.com.bluemoon.delivery.app.api.model.offline.request;

/**
 * Created by bm on 2017/5/27.
 */

public class NumData {

    public String type;

    /**
     * 获取列表角标
     * @param type {student,teacher}
     */
    public NumData(String type){
        this.type = type;
    }
}
