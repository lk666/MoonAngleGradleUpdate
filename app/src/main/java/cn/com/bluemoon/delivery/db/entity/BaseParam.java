package cn.com.bluemoon.delivery.db.entity;

import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.db.manager.GreenDaoUtil;

/**
 * Created by bm on 2017/5/9.
 */

public class BaseParam {

    public String gps;
    public String ip;

    public BaseParam(){
        gps = ClientStateManager.getLongitude()+","+ ClientStateManager.getLatitude();
        ip = GreenDaoUtil.getLocalIpAddress();
    }

}
