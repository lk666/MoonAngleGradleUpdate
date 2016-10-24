package cn.com.bluemoon.delivery.module.order;


import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.ResultOrderCount;
import cn.com.bluemoon.delivery.entity.DrawableTabState;
import cn.com.bluemoon.delivery.entity.TabState;
import cn.com.bluemoon.delivery.module.base.BaseTabActivity;

/**
 * Created by ljl on 2016/10/24.
 */
public class OrdersTabActivity extends BaseTabActivity {

    public static void actionStart(Context context) {
        ArrayList<TabState> tabs = new ArrayList<>();
        tabs.add(new DrawableTabState(PendingOrdersFragment.class, R.mipmap.tab_orders_normal, R.mipmap.tab_orders_disable,
                R.string.tab_orders));
        tabs.add(new DrawableTabState(PendingAppointmentFragment.class, R.mipmap.tab_appointment_normal, R.mipmap.tab_appointment_disable,
                R.string.tab_appointment));
        tabs.add(new DrawableTabState(PendingDeliveryFragment.class, R.mipmap.tab_delivery_normal, R.mipmap.tab_delivery_disable,
                R.string.tab_delivery));
        tabs.add(new DrawableTabState(PendingReceiptFragment.class, R.mipmap.tab_receipt_normal, R.mipmap.tab_receipt_disable,
                R.string.tab_receipt));
        tabs.add(new DrawableTabState(HistoryFragment.class, R.mipmap.tab_history_normal, R.mipmap.tab_history_disable,
                R.string.tab_history));
        actionStart(context, tabs, OrdersTabActivity.class);
    }

    @Override
    protected void getAmountList() {
        DeliveryApi.getOrderCount(getToken(), getNewHandler(1, ResultOrderCount.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase resultBase) {
        if (requestCode == 1) {
            ResultOrderCount result = (ResultOrderCount) resultBase;
            List<Integer> amountList = new ArrayList<>();
            amountList.add(result.getWaitAccept());
            amountList.add(result.getWaitAppointment());
            amountList.add(result.getWaitDelivery());
            amountList.add(result.getWaitSign());
            setAmountList(amountList);
        }
    }
}
