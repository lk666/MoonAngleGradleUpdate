package cn.com.bluemoon.delivery.module.clothing.collect;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.entity.TabState;
import cn.com.bluemoon.delivery.module.base.BaseFragmentActivity;
import cn.com.bluemoon.delivery.module.clothing.collect.withorder.WithOrderManageFragment;
import cn.com.bluemoon.delivery.module.clothing.collect.withorder.WithOrderRecordFragment;

/**
 * 收衣公共界面
 * Created by lk on 2016/6/12.
 */
public class ClothingTabActivity extends BaseFragmentActivity {

    public static final String TYPE = "type";
    public static final String WITH_ORDER_COLLECT_MANAGE = "WITH_ORDER_COLLECT_MANAGE";
    public static final String WITH_ORDER_COLLECT_RECORD = "WITH_ORDER_COLLECT_RECORD";

    // TODO: lk 2016/6/12 图片未确定
    private final static TabState[] TAB_WITH_ORDER = {
            new TabState(WithOrderManageFragment.class, R.drawable.tab_receive_selector,
                    R.string.tab_bottom_with_order_collect_manage, WITH_ORDER_COLLECT_MANAGE),
            new TabState(WithOrderRecordFragment.class, R.drawable.tab_received_selector,
                    R.string.tab_bottom_with_order_collect_record, WITH_ORDER_COLLECT_RECORD)
    };

    private FragmentTabHost mTabHost;
    private TextView amountTv;
    private TextView amountTv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.extract_tab);

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        mTabHost.getTabWidget().setDividerDrawable(null);

        if (getIntent().getStringExtra(TYPE).equals(WITH_ORDER_COLLECT_MANAGE)) {
            for (int i = 0; i < TAB_WITH_ORDER.length; i++) {
                TabHost.TabSpec tabSpec = mTabHost.newTabSpec(getResources()
                        .getString(TAB_WITH_ORDER[i].getContent()))
                        .setIndicator(getTabItemView(TAB_WITH_ORDER[i].getImage(),
                                getResources().getString(TAB_WITH_ORDER[i].getContent()), i));

                Bundle bundle = new Bundle();
                bundle.putString(TYPE, TAB_WITH_ORDER[i].getManager());
                mTabHost.addTab(tabSpec, TAB_WITH_ORDER[i].getClazz(), bundle);
            }
        }
    }

    private View getTabItemView(int resId, String content, int index) {
        View view = LayoutInflater.from(this).inflate(R.layout.tab_item_view, null);
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

//    public void TabTo(int index) {
//        mTabHost.setCurrentTab(index);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
//            int count = Integer.parseInt(amountTv.getText().toString());
//            if (count > 1) {
//                count = count - 1;
//                amountTv.setText(String.valueOf(count));
//            } else {
//                amountTv.setVisibility(View.GONE);
//            }
//            TabTo(1);
        }
    }

    public static void actionStart(Context context, String type) {
        Intent intent = new Intent(context, ClothingTabActivity.class);
        intent.putExtra(TYPE, type);
        context.startActivity(intent);
    }
}
