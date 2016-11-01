package cn.com.bluemoon.delivery.module.wash.returning.clothescheck;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.clothescheck.BackOrder;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.ui.NoScrollListView;

/**
 * 还衣单清单
 */
public class BackOrderListDetailActivity extends BaseActivity implements
        OnListItemClickListener {

    private static final String EXTRA_TAG_CODE = "EXTRA_TAG_CODE";
    private static final String EXTRA_LIST = "EXTRA_LIST";
    private static final int REQUEST_CODE_SCAN = 0x666;
    private static final int REQUEST_CODE_FINISH = 0x444;
    @Bind(R.id.tv_tag_code)
    TextView tvTagCode;
    @Bind(R.id.tv_count)
    TextView tvCount;
    @Bind(R.id.lv)
    NoScrollListView lv;
    @Bind(R.id.sc_main)
    ScrollView scMain;
    @Bind(R.id.btn_finish)
    Button btnFinish;

    private String tagCode;

    /**
     * 数据
     */
    private ArrayList<BackOrder> BACK_ORDERS = new ArrayList<>();

    /**
     * 数据
     */
    private ArrayList<CheckBackOrder> list = new ArrayList<>();

    private ItemAdapter adapter;

    public static void actionStart(Context context, String tagCode, ArrayList<BackOrder> list) {
        Intent intent = new Intent(context, BackOrderListDetailActivity.class);
        intent.putExtra(EXTRA_TAG_CODE, tagCode);
        intent.putExtra(EXTRA_LIST, list);
        context.startActivity(intent);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();

        if (getIntent().hasExtra(EXTRA_TAG_CODE)) {
            tagCode = getIntent().getStringExtra(EXTRA_TAG_CODE);
        }

        if (getIntent().hasExtra(EXTRA_LIST)) {
            BACK_ORDERS = (ArrayList<BackOrder>) getIntent().getSerializableExtra(EXTRA_LIST);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_back_order_list_detail;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.back_order_check_list_title);
    }

    @Override
    protected void setActionBar(CommonActionBar titleBar) {
        titleBar.getImgRightView().setImageResource(R.mipmap.ic_scan);
        titleBar.getImgRightView().setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActionBarBtnRightClick() {
        ScanBackOrderActivity.actionStart(this, null, REQUEST_CODE_SCAN, tagCode, list);
    }

    @Override
    public void initView() {
        adapter = new ItemAdapter(this, this);
        adapter.setList(list);
        lv.setAdapter(adapter);
    }

    @Override
    public void initData() {
        setData();
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        switch (requestCode) {
            // 清点完成
            case REQUEST_CODE_FINISH:
                finish();
        }
    }

    private void setData() {
        btnFinish.setVisibility(View.VISIBLE);

        tvTagCode.setText(String.format(getString(R.string.driver_tag_code), tagCode));

        // 初始化数据列表
        list.clear();
        for (BackOrder backOrder : BACK_ORDERS) {
            CheckBackOrder checkBackOrder = new CheckBackOrder(backOrder.getBackOrderCode(),
                    backOrder.isAbnormal() ? CheckBackOrder.EXCEPTION : CheckBackOrder.NONEXIST);
            list.add(checkBackOrder);
        }

        tvCount.setText(String.format(getString(R.string.clothes_check_history_back_order_num),
                list.size()));

        adapter.notifyDataSetChanged();
        scMain.post(new Runnable() {
            @Override
            public void run() {
                scMain.scrollTo(0, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 扫码返回
        if (requestCode == REQUEST_CODE_SCAN) {
            ArrayList<CheckBackOrder> l = (ArrayList<CheckBackOrder>) data.getSerializableExtra(
                    ScanClothesCodeActivity.EXTRA_LIST);

            list.clear();
            if (l != null) {
                list.addAll(l);
            }
            adapter.notifyDataSetChanged();
            return;
        }
    }

    @OnClick(R.id.btn_finish)
    public void onClick() {
        //  点击清点完成
        ReturningApi.checkComplete(list, tagCode, getToken(),
                getNewHandler(REQUEST_CODE_FINISH, ResultBase.class));

    }

    class ItemAdapter extends BaseListAdapter<CheckBackOrder> {
        public ItemAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_back_order_check;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            CheckBackOrder item = (CheckBackOrder) getItem(position);

            TextView tvCode = getViewById(R.id.tv_code);
            ImageView iv = getViewById(R.id.iv);

            tvCode.setText(item.getBackOrderCode());
            if (CheckBackOrder.EXCEPTION.equals(item.getCheckStatus())) {
                iv.setImageResource(R.mipmap.ic_error);
            } else if (CheckBackOrder.NORMAL.equals(item.getCheckStatus())) {
                iv.setImageResource(R.mipmap.scaned);
            } else {
                iv.setImageResource(R.mipmap.ic_not_scan);
            }
            setClickEvent(isNew, position, convertView);
        }
    }

    @Override
    public void onItemClick(Object item, View view, int position) {
        if (item instanceof CheckBackOrder) {
            // 还衣单详情
            BackOrderDetailActivity.actionStart(this, ((CheckBackOrder) item).getBackOrderCode());
        }
    }
}