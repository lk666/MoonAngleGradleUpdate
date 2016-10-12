package cn.com.bluemoon.delivery.module.order;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.other.HistoryOrderType;
import cn.com.bluemoon.delivery.app.api.model.other.ResultOrderCount;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.entity.OrderState;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.manager.ActivityManager;

public class OrdersTabActivity extends FragmentActivity {
    private String TAG = "OrdersTabActivity";
    private LayoutInflater layoutInflater;
    private TextView amountTv;
    private TextView amountTv2;
    private TextView amountTv3;
    private TextView amountTv4;
    private AmountChangeReceiver amountChangeReceiver;
    private String currentTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orders_tab);
        Context context = this;
        ActivityManager.getInstance().pushOneActivity(this);
        layoutInflater = LayoutInflater.from(this);
        FragmentTabHost mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        mTabHost.getTabWidget().setDividerDrawable(null);
        //mTabHost.getTabWidget().setStripEnabled(false);

        final OrderState[] states = OrderState.values();
        currentTag = getString(states[0].getContent());

        HistoryFragment.ordertype = HistoryOrderType.dispatch;
        for (int i = 0; i < states.length; i++) {
            TabSpec tabSpec = mTabHost.newTabSpec(getResources().getString(states[i].getContent()))
                    .setIndicator(getTabItemView(states[i].getImage(), getResources().getString
                            (states[i].getContent()), i));
            mTabHost.addTab(tabSpec, states[i].getClazz(), null);
        }
        mTabHost.setOnTabChangedListener(new OnTabChangeListener() {

            @Override
            public void onTabChanged(String tabId) {
                currentTag = tabId;
            }
        });
        mTabHost.getTabWidget().setStripEnabled(false);
        amountChangeReceiver = new AmountChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.INTENTFILTER_ACTION);
        this.registerReceiver(amountChangeReceiver, intentFilter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(amountChangeReceiver);
    }

    private View getTabItemView(int resId, String content, int index) {
        View view = layoutInflater.inflate(R.layout.tab_item_view, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        imageView.setImageResource(resId);
        TextView textView = (TextView) view.findViewById(R.id.textview);
        textView.setText(content);
        TextView curTvAmount = (TextView) view.findViewById(R.id.txt_count);

        switch (index) {
            case 0:
                amountTv = curTvAmount;
                break;
            case 1:
                amountTv2 = curTvAmount;
                break;
            case 2:
                amountTv3 = curTvAmount;
                break;
            case 3:
                amountTv4 = curTvAmount;
                break;

            default:
                break;
        }
        return view;
    }

    public class AmountChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.INTENTFILTER_ACTION)) {
                setAmountShow();
            }
        }
    }

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
