package cn.com.bluemoon.delivery.app.api.model;

import java.io.Serializable;
import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.UserRight;

/**
 * Created by summer on 2016/5/13.
 */
public class MenuBean implements Serializable {
    private int group;
    private List<UserRight> item;

    public MenuBean(){}
    public MenuBean(int group, List<UserRight> item){
        this.group = group;
        this.item = item;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public List<UserRight> getItem() {
        return item;
    }

    public void setItem(List<UserRight> item) {
        this.item = item;
    }


}
