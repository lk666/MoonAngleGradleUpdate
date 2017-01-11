package cn.com.bluemoon.delivery.module.wash.appointment;

import android.content.Context;

import java.util.ArrayList;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.entity.TabState;
import cn.com.bluemoon.delivery.entity.WashModeType;
import cn.com.bluemoon.delivery.module.base.BaseTabActivity;


/**
 * 预约收衣界面
 * Created by lk on 2016/6/12.
 */
public class AppointmentTabActivity extends BaseTabActivity {

    public static void actionStart(Context context) {
        ArrayList<TabState> tabs = new ArrayList<>();
        // TODO: lk 2016/12/20
        tabs.add(new TabState(AppointmentFragment.class,
                R.drawable.tab_appointment_selector,
                R.string.appointment_title));
        tabs.add(new TabState(AppointmentHistoryFragment.class,
                R.drawable.tab_history,
                R.string.tab_bottom_with_order_collect_record));
        actionStart(context, tabs, AppointmentTabActivity.class);
    }

    @Override
    protected WashModeType getModeType() {
        return WashModeType.APPOINTMENT_MODEL;
    }
}
