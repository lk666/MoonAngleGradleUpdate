package cn.com.bluemoon.delivery.app.api.model.offline.request;

/**
 * 获取列表角标
 * Created by tangqiwei on 2017/6/3.
 */

public class ListNumData {
    public String type;//	类型	string	可选值{student,teacher}
    public ListNumData(String type){
        this.type=type;
    }
}
