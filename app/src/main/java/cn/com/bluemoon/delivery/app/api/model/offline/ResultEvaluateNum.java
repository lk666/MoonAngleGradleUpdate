package cn.com.bluemoon.delivery.app.api.model.offline;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * 获取评价学员页面的评价数量
 * Created by tangqiwei on 2017/6/5.
 */

public class ResultEvaluateNum extends ResultBase {
    public Data data;

    public static class Data{
        public int evaluatedNum;//	已评价数量	number	@mock=0
        public int unEvaluatedNum;//	未评价数量	number	@mock=1
    }
}
