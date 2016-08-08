package cn.com.bluemoon.delivery.app.api.model.clothing;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.CollectInfo;

/**
 * Created by allenli on 2016/6/27.
 */
public class ResultCollectInfo extends ResultBase {
    private List<CollectInfo> collectInfos;

    public List<CollectInfo> getCollectInfos() {
        return collectInfos;
    }

    public void setCollectInfos(List<CollectInfo> collectInfos) {
        this.collectInfos = collectInfos;
    }
}
