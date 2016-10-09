package cn.com.bluemoon.delivery.app.api.model.wash.clothescheck;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * 8.4衣物清点/还衣单清点-还衣单详情服务器返回json
 * Created by lk on 2016/9/14.
 */
public class ResultBackOrderDetail extends ResultBase {

    /**
     * 衣物明细
     */
    private List<Clothes> clothesList;

    public List<Clothes> getClothesList() {
        return clothesList;
    }

    public void setClothesList(List<Clothes> clothesList) {
        this.clothesList = clothesList;
    }

}
