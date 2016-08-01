package cn.com.bluemoon.delivery.module.inventory;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.entity.InventoryDeliveryTabState;
import cn.com.bluemoon.delivery.entity.InventoryReceiveTabState;
import cn.com.bluemoon.delivery.utils.manager.ActivityManager;

public class InventoryTabActivity extends FragmentActivity {

    public static String RECEIVE_MANAGEMENT = "RECEIVE_MANAGEMENT";
    public static String DELIVERY_MANAGEMENT = "DELIVERY_MANAGEMENT";


    private String TAG = "InventoryTabActivity";
    private static Context context;
    private LayoutInflater layoutInflater;
    private FragmentTabHost mTabHost;
    private String currentTag;
    TextView amountTv;
    TextView amountTv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.extract_tab);
        context = this;

        ActivityManager.getInstance().pushOneActivity(this);
        layoutInflater = LayoutInflater.from(this);
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        mTabHost.getTabWidget().setDividerDrawable(null);

        if (getIntent().getStringExtra("type").equals(InventoryTabActivity.RECEIVE_MANAGEMENT)) {
            final InventoryReceiveTabState[] receiveStates = InventoryReceiveTabState.values();
            currentTag = getString(receiveStates[0].getContent());

            for (int i = 0; i < receiveStates.length; i++) {
                TabSpec tabSpec = mTabHost.newTabSpec(getResources().getString(receiveStates[i].getContent()))
                        .setIndicator(getTabItemView(receiveStates[i].getImage(), getResources().getString(receiveStates[i].getContent()), i));

                Bundle bundle = new Bundle();
                bundle.putString("type", receiveStates[i].getManager());
                mTabHost.addTab(tabSpec, receiveStates[i].getClazz(), bundle);
            }

            mTabHost.setOnTabChangedListener(new OnTabChangeListener() {

                @Override
                public void onTabChanged(String tabId) {
                    currentTag = tabId;
                }
            });

        } else {
            final InventoryDeliveryTabState[] deliveryStates = InventoryDeliveryTabState.values();
            currentTag = getString(deliveryStates[0].getContent());

            for (int i = 0; i < deliveryStates.length; i++) {
                TabSpec tabSpec = mTabHost.newTabSpec(getResources().getString(deliveryStates[i].getContent()))
                        .setIndicator(getTabItemView(deliveryStates[i].getImage(), getResources().getString(deliveryStates[i].getContent()), i));
                Bundle bundle = new Bundle();
                bundle.putString("type", deliveryStates[i].getManager());
                mTabHost.addTab(tabSpec, deliveryStates[i].getClazz(), bundle);
            }

            mTabHost.setOnTabChangedListener(new OnTabChangeListener() {

                @Override
                public void onTabChanged(String tabId) {
                    currentTag = tabId;
                }
            });
        }

    }

    private View getTabItemView(int resId, String content, int index) {
        View view = layoutInflater.inflate(R.layout.tab_item_view, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        imageView.setImageResource(resId);
        TextView textView = (TextView) view.findViewById(R.id.textview);
        textView.setText(content);
        if (index == 0) {
            amountTv = (TextView) view.findViewById(R.id.txt_count);
        } else {
            amountTv2 = (TextView) view.findViewById(R.id.txt_count);
        }
        return view;
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
    }

    public void TabTo(int index) {
        mTabHost.setCurrentTab(index);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            int count = Integer.parseInt(amountTv.getText().toString());
            if (count > 1) {
                count = count - 1;
                amountTv.setText(String.valueOf(count));
            } else {
                amountTv.setVisibility(View.GONE);
            }
            TabTo(1);
        }
    }

    public static void actionStart(Context context, String type) {
        Intent intent = new Intent(context, InventoryTabActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }
}
