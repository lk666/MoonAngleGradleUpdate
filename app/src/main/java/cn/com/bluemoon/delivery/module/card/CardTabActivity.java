package cn.com.bluemoon.delivery.module.card;


import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.entity.TabState;
import cn.com.bluemoon.delivery.module.base.BaseFragment;
import cn.com.bluemoon.delivery.module.base.BaseTabActivity;
import cn.com.bluemoon.delivery.module.card.alarm.AlarmSettingFragment;

public class CardTabActivity extends BaseTabActivity {

    public static void actionStart(Context context, boolean isPunchCard) {
        Intent intent = new Intent(context, CardTabActivity.class);
        ArrayList<TabState> tabs = new ArrayList<>();
        if (isPunchCard) {
            tabs.add(new TabState(PunchCardGetOffWordFragment.class, R.drawable.card_tab_punch, R.string.tab_bottom_punch_card_text));
        } else {
            tabs.add(new TabState(PunchCardFragment.class, R.drawable.card_tab_punch, R.string.tab_bottom_punch_card_text));
        }
        tabs.add(new TabState(RecordCardFragment.class, R.drawable.card_tab_record, R.string.tab_bottom_punch_record_text));
        tabs.add(new TabState(AlarmSettingFragment.class, R.drawable.card_tab_record, R.string.tab_bottom_punch_alarm_setting));
        intent.putExtra(BaseFragment.EXTRA_BUNDLE_DATA, tabs);
        context.startActivity(intent);
    }

}
