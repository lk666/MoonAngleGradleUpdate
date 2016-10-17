package cn.com.bluemoon.delivery.module.wash.returning.clothescheck;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.clothescheck.Clothes;
import cn.com.bluemoon.delivery.app.api.model.wash.clothescheck.ResultBackOrderDetail;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.ui.NoScrollListView;
import cn.com.bluemoon.delivery.utils.DialogUtil;
import cn.com.bluemoon.lib.utils.ImageLoaderUtil;

/**
 * 还衣单详情
 */
public class BackOrderDetailActivity extends BaseActivity implements
        OnListItemClickListener {

    private static final String EXTRA_BACK_ORDER_CODE = "EXTRA_BACK_ORDER_CODE";
    private static final int REQUEST_CODE_QUERY_LIST = 0x777;
    @Bind(R.id.tv_back_order_code)
    TextView tvBackOrderCode;
    @Bind(R.id.tv_count)
    TextView tvCount;
    @Bind(R.id.lv)
    NoScrollListView lv;
    @Bind(R.id.sc_main)
    ScrollView scMain;

    private String backOrderCode;

    /**
     * 数据
     */
    private ArrayList<Clothes> list = new ArrayList<>();

    private ItemAdapter adapter;

    public static void actionStart(Context context, String backOrderCode) {
        Intent intent = new Intent(context, BackOrderDetailActivity.class);
        intent.putExtra(EXTRA_BACK_ORDER_CODE, backOrderCode);
        context.startActivity(intent);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();

        if (getIntent().hasExtra(EXTRA_BACK_ORDER_CODE)) {
            backOrderCode = getIntent().getStringExtra(EXTRA_BACK_ORDER_CODE);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_clothes_back_order_detail;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.back_order_detail_title);
    }

    @Override
    public void initView() {
        adapter = new ItemAdapter(this, this);
        adapter.setList(list);
        lv.setAdapter(adapter);
    }

    @Override
    public void initData() {
        showWaitDialog();
        ReturningApi.queryBackOrderDetail3(backOrderCode, getToken(), getNewHandler
                (REQUEST_CODE_QUERY_LIST, ResultBackOrderDetail.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        switch (requestCode) {
            // 请求列表数据
            case REQUEST_CODE_QUERY_LIST:
                ResultBackOrderDetail obj = (ResultBackOrderDetail) result;
                setData(obj);
                break;
        }
    }

    private void setData(ResultBackOrderDetail result) {
        if (result == null) {
            return;
        }

        tvBackOrderCode.setText(String.format(getString(R.string.back_order_code),
                backOrderCode));

        int num = 0;
        List<Clothes> steList = result.getClothesList();
        if (steList != null) {
            num = steList.size();
        }
        tvCount.setText(String.format(getString(R.string.clothes_check_history_clothes_num), num));

        list.clear();
        if (num > 0) {
            list.addAll(steList);
        }
        adapter.notifyDataSetChanged();
        scMain.post(new Runnable() {
            @Override
            public void run() {
                scMain.scrollTo(0, 0);
            }
        });
    }

    class ItemAdapter extends BaseListAdapter<Clothes> {
        public ItemAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_clothes;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            Clothes item = (Clothes) getItem(position);

            TextView tvCode = getViewById(R.id.tv_code);
            ImageView iv = getViewById(R.id.iv);

            tvCode.setText(String.format(getString(R.string.clothes_check_detail_code),
                    item.getClothesCode()));

            ImageLoaderUtil.displayImage(item.getImagePath(), iv);

//            setClickEvent(isNew, position, iv);
        }
    }

    @Override
    public void onItemClick(Object item, View view, int position) {

    }
}