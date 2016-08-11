package cn.com.bluemoon.delivery.module.coupons;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.entity.CouponsTabState;
import cn.com.bluemoon.delivery.utils.manager.ActivityManager;

public class CouponsTabActivity extends FragmentActivity {
    private String TAG = "CouponsTabActivity";
    private LayoutInflater layoutInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_tab);

        ActivityManager.getInstance().pushOneActivity(this);
        layoutInflater = LayoutInflater.from(this);
        FragmentTabHost mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        mTabHost.getTabWidget().setDividerDrawable(null);
        final CouponsTabState[] states = CouponsTabState.values();
        for (int i = 0; i < states.length; i++) {
            TabSpec tabSpec = mTabHost.newTabSpec(getResources().getString(states[i].getContent()))
                    .setIndicator(getTabItemView(states[i].getImage(), getResources().getString
                            (states[i].getContent()), i));

            mTabHost.addTab(tabSpec, states[i].getClazz(), null);
        }
    }


    private View getTabItemView(int resId, String content, int index) {
        View view = layoutInflater.inflate(R.layout.tab_item_view, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        imageView.setImageResource(resId);
        TextView textView = (TextView) view.findViewById(R.id.textview);
        textView.setText(content);
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
}
