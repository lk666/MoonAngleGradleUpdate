package cn.com.bluemoon.delivery.app.api.model.evidencecash;

/**
 * Created by ljl on 2016/11/15.
 */
public class CashListDataset {

    public static final int ITEM = 0;
    public static final int SECTION = 1;

    public final int type;
    public final CashListBean bean;
    public final String date;
    public boolean isLast;

    public CashListDataset(int type, CashListBean bean, String date) {
        this.type = type;
        this.bean = bean;
        this.date = date;
    }

    public void setLast(boolean isLast) {
        this.isLast = isLast;
    }

}
