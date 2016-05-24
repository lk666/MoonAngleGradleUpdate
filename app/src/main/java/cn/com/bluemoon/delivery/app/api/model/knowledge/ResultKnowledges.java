package cn.com.bluemoon.delivery.app.api.model.knowledge;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by allenli on 2016/5/20.
 */
public class ResultKnowledges extends ResultBase {
    private List<Knowledge> list;

    public List<Knowledge> getList() {
        return list;
    }

    public void setList(List<Knowledge> list) {
        this.list = list;
    }
}
