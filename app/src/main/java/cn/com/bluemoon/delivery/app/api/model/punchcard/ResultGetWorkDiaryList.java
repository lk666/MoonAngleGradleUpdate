package cn.com.bluemoon.delivery.app.api.model.punchcard;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by liangjiangli on 2016/4/1.
 */
public class ResultGetWorkDiaryList extends ResultBase{
    private int totalBreedSalesNum;
    private int totalSalesNum;
    private List<Product> workDailyList;

    public int getTotalBreedSalesNum() {
        return totalBreedSalesNum;
    }

    public void setTotalBreedSalesNum(int totalBreedSalesNum) {
        this.totalBreedSalesNum = totalBreedSalesNum;
    }

    public int getTotalSalesNum() {
        return totalSalesNum;
    }

    public void setTotalSalesNum(int totalSalesNum) {
        this.totalSalesNum = totalSalesNum;
    }

    public List<Product> getWorkDailyList() {
        return workDailyList;
    }

    public void setWorkDailyList(List<Product> workDailyList) {
        this.workDailyList = workDailyList;
    }


}
