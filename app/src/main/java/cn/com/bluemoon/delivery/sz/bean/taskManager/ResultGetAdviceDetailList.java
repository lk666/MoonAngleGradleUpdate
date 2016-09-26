package cn.com.bluemoon.delivery.sz.bean.taskManager;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by Wan.N
 * Date       2016/9/9
 * Desc       获得任务评价列表实体
 */
public class ResultGetAdviceDetailList extends ResultBase {

    private List<TaskAdviceDetailInfoBean> data;

    public List<TaskAdviceDetailInfoBean> getData() {
        return data;
    }

    public void setData(List<TaskAdviceDetailInfoBean> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResultGetAdviceDetailList{" +
                "data=" + data +
                '}';
    }
}
