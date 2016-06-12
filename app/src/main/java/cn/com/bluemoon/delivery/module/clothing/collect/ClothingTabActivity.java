package cn.com.bluemoon.delivery.module.clothing.collect;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.module.base.BaseFragmentActivity;

/**
 * 收衣公共界面
 * Created by lk on 2016/6/12.
 */
public class ClothingTabActivity extends BaseFragmentActivity {

    public static final String TYPE = "type";
    public static final String WITH_ORDER = "WITH_ORDER";

    private String TAG = "ClothingTabActivity";

    private FragmentTabHost mTabHost;
    TextView amountTv;
    TextView amountTv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setContentView(R.layout.extract_tab);

//        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
//        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
//        mTabHost.getTabWidget().setDividerDrawable(null);

//        if (getIntent().getStringExtra(TYPE).equals(WITH_ORDER)) {
////            final InventoryReceiveTabState[] receiveStates = InventoryReceiveTabState.values();
////
////            for (int i = 0; i < receiveStates.length; i++) {
////                TabHost.TabSpec tabSpec = mTabHost.newTabSpec(getResources().getString(receiveStates[i].getContent()))
////                        .setIndicator(getTabItemView(receiveStates[i].getImage(), getResources().getString(receiveStates[i].getContent()), i));
////
////                Bundle bundle = new Bundle();
////                bundle.putString("type", receiveStates[i].getManager());
////                mTabHost.addTab(tabSpec, receiveStates[i].getClazz(), bundle);
////            }
////
////            mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
////
////                @Override
////                public void onTabChanged(String tabId) {
////                    currentTag = tabId;
////                }
////            });
//
//        }
    }

//    private View getTabItemView(int resId, String content, int index) {
//        View view = layoutInflater.inflate(R.layout.tab_item_view, null);
//        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
//        imageView.setImageResource(resId);
//        TextView textView = (TextView) view.findViewById(R.id.textview);
//        textView.setText(content);
//        if (index == 0) {
//            amountTv = (TextView) view.findViewById(R.id.txt_count);
//        } else {
//            amountTv2 = (TextView) view.findViewById(R.id.txt_count);
//        }
//        return view;
//    }

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
        Intent intent = new Intent(context, ClothingTabActivity.class);
        intent.putExtra(TYPE, type);
        context.startActivity(intent);
    }
}
