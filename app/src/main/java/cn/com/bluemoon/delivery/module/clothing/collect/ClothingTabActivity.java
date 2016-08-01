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
import cn.com.bluemoon.delivery.module.clothing.collect.withorder.WithOrderManageFragment;
import cn.com.bluemoon.delivery.module.clothing.collect.withoutorder.WithoutOrderManageFragment;
import cn.com.bluemoon.delivery.module.oldbase.BaseFragmentActivity;

// TODO: lk 2016/6/16 可干掉与当前activity重复layout的资源文件 

/**
 * 收衣公共界面
 * Created by lk on 2016/6/12.
 */
public class ClothingTabActivity extends BaseFragmentActivity {

    public static String TYPE = "";
    public static final String WITH_ORDER_COLLECT_MANAGE = "WITH_ORDER_COLLECT_MANAGE";
    public static final String WITHOUT_ORDER_COLLECT_MANAGE = "WITHOUT_ORDER_COLLECT_MANAGE";

    private final static OldTabState[] TAB_WITH_ORDER = new OldTabState[2];

    private TextView amountTv;
    private TextView amountTv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.extract_tab);

        TYPE = getIntent().getStringExtra("type");

        if (TYPE.equals(WITH_ORDER_COLLECT_MANAGE)) {

            TAB_WITH_ORDER[0] = new OldTabState(WithOrderManageFragment.class, R.drawable
                    .tab_without_order_receive_selector,
                    R.string.tab_bottom_with_order_collect_manage, WITH_ORDER_COLLECT_MANAGE);
            TAB_WITH_ORDER[1] = new OldTabState(CollectClothesRecordFragment.class, R.drawable
                    .tab_without_order_record_selector,
                    R.string.tab_bottom_with_order_collect_record, WITH_ORDER_COLLECT_MANAGE);
        } else {
            TAB_WITH_ORDER[0] = new OldTabState(WithoutOrderManageFragment.class, R.drawable
                    .tab_without_order_receive_selector,
                    R.string.tab_bottom_with_order_collect_manage, WITHOUT_ORDER_COLLECT_MANAGE);
            TAB_WITH_ORDER[1] = new OldTabState(CollectClothesRecordFragment.class, R.drawable
                    .tab_without_order_record_selector,
                    R.string.tab_bottom_with_order_collect_record, WITHOUT_ORDER_COLLECT_MANAGE);
        }

        FragmentTabHost mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        mTabHost.getTabWidget().setDividerDrawable(null);

        // if (getIntent().getStringExtra(TYPE).equals(WITH_ORDER_COLLECT_MANAGE)) {
        for (int i = 0; i < TAB_WITH_ORDER.length; i++) {
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(getResources()
                    .getString(TAB_WITH_ORDER[i].getContent()))
                    .setIndicator(getTabItemView(TAB_WITH_ORDER[i].getImage(),
                            getResources().getString(TAB_WITH_ORDER[i].getContent()), i));

            Bundle bundle = new Bundle();
            bundle.putString("manager", TAB_WITH_ORDER[i].getManager());
            mTabHost.addTab(tabSpec, TAB_WITH_ORDER[i].getClazz(), bundle);
        }
        // }
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

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            int count = Integer.parseInt(amountTv.getText().toString());
//            if (count > 1) {
//                count = count - 1;
//                amountTv.setText(String.valueOf(count));
//            } else {
//                amountTv.setVisibility(View.GONE);
//            }
//            TabTo(1);
//        }
//    }

    public void setAmountShow(String type, int amount) {
//        switch (type) {
//            // 收衣管理
//            case WITH_ORDER_COLLECT_MANAGE:
        if (amount > 0) {
            amountTv.setText(String.valueOf(amount));
            amountTv.setVisibility(View.VISIBLE);
        } else {
            amountTv.setVisibility(View.GONE);
        }
//                break;
//        }
    }

    public static void actionStart(Context context, String type) {
        Intent intent = new Intent(context, ClothingTabActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }
}
