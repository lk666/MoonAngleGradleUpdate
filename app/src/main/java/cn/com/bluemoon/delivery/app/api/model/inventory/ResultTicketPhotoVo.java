/**
 * 待发货详情
 * wangshanhai
 */
package cn.com.bluemoon.delivery.app.api.model.inventory;

import java.io.Serializable;
import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;


public class ResultTicketPhotoVo extends ResultBase implements Serializable {

    private List<String> picList;

    public List<String> getPicList() {
        return picList;
    }

    public void setPicList(List<String> picList) {
        this.picList = picList;
    }
}


  
