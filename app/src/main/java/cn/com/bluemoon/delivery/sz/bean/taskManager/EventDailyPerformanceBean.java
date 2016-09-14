package cn.com.bluemoon.delivery.sz.bean.taskManager;

import java.io.Serializable;

/**
 * Created by Wan.N
 * Date       2016/9/14
 * Desc      用于eventbus传送DailyPerformanceInfoBean的扩展bean
 */
public class EventDailyPerformanceBean implements Serializable {
//    public static final int ACTIVITY_TYPE_TO_EVALUATE = 0;//待评价
//    public static final int ACTIVITY_TYPE_HAVE_EVALUATED = 1;//已评价
    /**
     * 表示此DailyPerformanceInfoBean的评价属性，
     * 用于区分此绩效任务是应从未评价区移至已评价区或者相反
     */
    private int Type = -1;
    private DailyPerformanceInfoBean dailyPerformanceInfoBean;

    public EventDailyPerformanceBean() {
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public DailyPerformanceInfoBean getDailyPerformanceInfoBean() {
        return dailyPerformanceInfoBean;
    }

    public void setDailyPerformanceInfoBean(DailyPerformanceInfoBean dailyPerformanceInfoBean) {
        this.dailyPerformanceInfoBean = dailyPerformanceInfoBean;
    }

    @Override
    public String toString() {
        return "EventDailyPerformanceBean{" +
                "Type=" + Type +
                ", dailyPerformanceInfoBean=" + dailyPerformanceInfoBean +
                '}';
    }
}
