package cn.com.bluemoon.delivery.app.api.model;

import java.util.List;

/**
 * Created by allenli on 2016/4/2.
 */
public class ResultModelNum extends  ResultBase {

    List<ModelNum> modelBeans;

    public List<ModelNum> getModelBeans() {
        return modelBeans;
    }

    public void setModelBeans(List<ModelNum> modelBeans) {
        this.modelBeans = modelBeans;
    }
}
