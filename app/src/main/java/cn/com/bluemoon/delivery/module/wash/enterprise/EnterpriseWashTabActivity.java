package cn.com.bluemoon.delivery.module.wash.enterprise;

import android.content.Context;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.entity.DrawableTabState;
import cn.com.bluemoon.delivery.entity.TabState;
import cn.com.bluemoon.delivery.module.base.BaseTabActivity;
import cn.com.bluemoon.delivery.module.wash.enterprise.event.ConfirmEvent;


/**
 * 企业洗衣界面
 * Created by ljl on 2016/6/12.
 */
public class EnterpriseWashTabActivity extends BaseTabActivity {

    public static void actionStart(Context context) {
        ArrayList<TabState> tabs = new ArrayList<>();
        tabs.add(new DrawableTabState(EnterpriseFragment.class, R.mipmap.tab_company_active,
                R.mipmap.tab_company_normal, R.string.tab_enterprise_txt));
        tabs.add(new DrawableTabState(EnterpriseHistoryFragment.class, R.mipmap.tab_history_normal,
                R.mipmap.tab_history_disable, R.string.card_search_history));
        actionStart(context, tabs, EnterpriseWashTabActivity.class);
    }

    @Override
    protected boolean isUseEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ConfirmEvent event) {
        tabhost.postDelayed(new Runnable() {
            @Override
            public void run() {
                tabhost.setCurrentTab(1);
            }
        }, 200);
    }
}
