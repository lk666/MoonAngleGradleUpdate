package cn.com.bluemoon.delivery.module.order;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.HistoryOrderType;
import cn.com.bluemoon.delivery.app.api.model.ResultOrderCount;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.entity.DrawableTabState;
import cn.com.bluemoon.delivery.entity.TabState;
import cn.com.bluemoon.delivery.module.base.BaseTabActivity;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.manager.ActivityManager;

/**
 * Created by ljl on 2016/10/24.
 */
public class OrdersTabActivity extends BaseTabActivity {

    public static void actionStart(Context context){
        ArrayList<TabState> tabs = new ArrayList<>();
        tabs.add(new DrawableTabState(PendingOrdersFragment.class,R.mipmap.tab_orders_normal, R.mipmap.tab_orders_disable,
                R.string.tab_orders));
        tabs.add(new DrawableTabState(PendingAppointmentFragment.class,R.mipmap.tab_appointment_normal, R.mipmap.tab_appointment_disable,
                R.string.tab_appointment));
        tabs.add(new DrawableTabState(PendingDeliveryFragment.class,R.mipmap.tab_delivery_normal, R.mipmap.tab_delivery_disable,
                R.string.tab_appointment));
        tabs.add(new DrawableTabState(PendingReceiptFragment.class,R.mipmap.tab_delivery_normal, R.mipmap.tab_delivery_disable,
                R.string.tab_delivery));
        tabs.add(new DrawableTabState(HistoryFragment.class,R.mipmap.tab_history_normal, R.mipmap.tab_history_disable,
                R.string.tab_history));
        actionStart(context, tabs, OrdersTabActivity.class);
    }

    private String TAG = "OrdersTabActivity";
    private LayoutInflater layoutInflater;
    private TextView amountTv;
    private TextView amountTv2;
    private TextView amountTv3;
    private TextView amountTv4;
    private String currentTag;


    public void setAmountShow() {
        DeliveryApi.getOrderCount(ClientStateManager.getLoginToken(OrdersTabActivity.this),
				getOrderCountHandler);
    }

    AsyncHttpResponseHandler getOrderCountHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d("test", "getOrderCountHandler result = " + responseString);
            try {
                ResultOrderCount result = JSON.parseObject(responseString, ResultOrderCount.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    if (result.getWaitAccept() > 0) {
                        amountTv.setText(String.valueOf(result.getWaitAccept()));
                        amountTv.setVisibility(View.VISIBLE);
                    } else {
                        amountTv.setVisibility(View.GONE);
                    }
                    if (result.getWaitAppointment() > 0) {
                        amountTv2.setText(String.valueOf(result.getWaitAppointment()));
                        amountTv2.setVisibility(View.VISIBLE);
                    } else {
                        amountTv2.setVisibility(View.GONE);
                    }
                    if (result.getWaitDelivery() > 0) {
                        amountTv3.setText(String.valueOf(result.getWaitDelivery()));
                        amountTv3.setVisibility(View.VISIBLE);
                    } else {
                        amountTv3.setVisibility(View.GONE);
                    }
                    if (result.getWaitSign() > 0) {
                        amountTv4.setText(String.valueOf(result.getWaitSign()));
                        amountTv4.setVisibility(View.VISIBLE);
                    } else {
                        amountTv4.setVisibility(View.GONE);
                    }
                } else {
                    PublicUtil.showErrorMsg(OrdersTabActivity.this, result);
                }
            } catch (Exception e) {
                PublicUtil.showToastServerBusy(OrdersTabActivity.this);
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString,
                              Throwable throwable) {
            PublicUtil.showToastServerOvertime(OrdersTabActivity.this);
        }
    };

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
    }
}
