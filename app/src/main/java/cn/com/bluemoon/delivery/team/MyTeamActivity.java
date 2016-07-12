package cn.com.bluemoon.delivery.team;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.entity.TeamTabState;
import cn.com.bluemoon.delivery.manager.ActivityManager;
import cn.com.bluemoon.delivery.utils.PublicUtil;

public class MyTeamActivity extends FragmentActivity implements BackHandledInterface {

    private String TAG = "MyTeamActivity";
    private LayoutInflater layoutInflater;
    private FragmentTabHost mTabHost;
    public static String roleCode;
    private BackHandledFragment mBackHandedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.extract_tab);

        ActivityManager.getInstance().pushOneActivity(this);
        layoutInflater = LayoutInflater.from(this);
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        mTabHost.getTabWidget().setDividerDrawable(null);
        final TeamTabState[] states = TeamTabState.values();
        for (int i = 0;i < states.length; i++) {
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(getResources().getString(states[i].getContent()))
                    .setIndicator(getTabItemView(states[i].getImage(),getResources().getString(states[i].getContent()), i));
            mTabHost.addTab(tabSpec, states[i].getClazz(), null);
        }
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (!"CEO".equals(roleCode) && mTabHost.getCurrentTab() == 1) {
                    mTabHost.setCurrentTab(0);
                    PublicUtil.showMessageNoTitle(MyTeamActivity.this, getString(R.string.team_member_limit));
                }
            }
        });

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

    @Override
    public void setSelectedFragment(BackHandledFragment selectedFragment) {
        this.mBackHandedFragment = selectedFragment;
    }

    @Override
    public void onBackPressed() {
        if(mBackHandedFragment == null || !mBackHandedFragment.onBackPressed()){
            if(getSupportFragmentManager().getBackStackEntryCount() == 0){
                super.onBackPressed();
            }else{
                getSupportFragmentManager().popBackStack();
            }
        }
    }
}
