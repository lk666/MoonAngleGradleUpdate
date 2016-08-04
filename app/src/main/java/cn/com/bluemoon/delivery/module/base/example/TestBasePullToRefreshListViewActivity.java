package cn.com.bluemoon.delivery.module.base.example;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.ClothesInfo;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.OuterOrderReceive;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.ResultOuterOrderInfo;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.BasePullToRefreshListViewActivity;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.clothing.collect.ClothesDetailActivity;
import cn.com.bluemoon.delivery.module.clothing.collect.ClothesInfoAdapter;
import cn.com.bluemoon.delivery.ui.NoScrollListView;
import cn.com.bluemoon.delivery.utils.ViewHolder;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;

/**
 * 测试下拉刷新列表Activity
 * Created by lk on 2016/7/27.
 */
public class TestBasePullToRefreshListViewActivity extends
        BasePullToRefreshListViewActivity<TestBasePullToRefreshListViewActivity.CollectAdapter,
                OuterOrderReceive> implements OnListItemClickListener {
    @Override
    protected String getTitleString() {
        return getString(R.string.title_outer_detail);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        outerCode = getIntent().getStringExtra(EXTRA_OUTERCODE);
        if (outerCode == null) {
            outerCode = "";
        }
    }

    @Override
    protected TestBasePullToRefreshListViewActivity.CollectAdapter getNewAdapter() {
        return new CollectAdapter(this, this);
    }

    /**
     * Mode不包含上拉加载时，可这样重写此方法
     */
    @Override
    protected List<OuterOrderReceive> getGetMoreList(ResultBase result) {
        return null;
    }

    @Override
    protected List<OuterOrderReceive> getGetDataList(ResultBase result) {
        ResultOuterOrderInfo resultObj = (ResultOuterOrderInfo) result;
        return resultObj.getOrderReceive();
    }

    @Override
    protected PullToRefreshBase.Mode getMode() {
        return PullToRefreshBase.Mode.PULL_FROM_START;
    }

    @Override
    protected void invokeGetDataDeliveryApi(int requestCode) {
        DeliveryApi.getOuterOrderInfo(outerCode, ClientStateManager.getLoginToken
                (this), getNewHandler(requestCode, ResultOuterOrderInfo.class));
    }

    /**
     * Mode不包含上拉加载时，可这样重写此方法
     */
    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        super.onSuccessResponse(requestCode, jsonString, result);
        // 其他requestCode可在此处理
    }

    /**
     * 洗衣服务订单号
     */
    public final static String EXTRA_OUTERCODE = "EXTRA_OUTERCODE";

    /**
     * 洗衣服务订单号
     */
    private String outerCode;

    /**
     * 收衣单列表Adapter
     */
    public class CollectAdapter extends BaseListAdapter<OuterOrderReceive> {
        public CollectAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_with_order_outer_receive;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            final OuterOrderReceive item = (OuterOrderReceive) getItem(position);
            if (item == null) {
                return;
            }

            TextView tvCount = ViewHolder.get(convertView, R.id.tv_count);
            TextView tvCollectCode = ViewHolder.get(convertView, R.id.tv_collect_code);
            NoScrollListView lvOrderReceiveClothes = ViewHolder.get(convertView, R.id
                    .lv_order_receive);
            TextView tvUrgent = ViewHolder.get(convertView, R.id.tv_urgent);
            TextView tvBrcode = ViewHolder.get(convertView, R.id.tv_brcode);

            tvCount.setText(getString(R.string.with_order_collect_title_actual_count) + item
                    .getActualCount());
            tvCollectCode.setText(getString(R.string.with_order_collect_collect_number_text) +
                    item.getCollectCode());

            if (item.getIsUrgent() == 1) {
                tvUrgent.setVisibility(View.VISIBLE);
            } else {
                tvUrgent.setVisibility(View.GONE);
            }

            String brcode = item.getCollectBrcode();
            if (TextUtils.isEmpty(brcode)) {
                tvBrcode.setText(getString(R.string.clothing_detail_brcode) +
                        getString(R.string.text_empty));
            } else {
                tvBrcode.setText(getString(R.string.clothing_detail_brcode) + brcode);
            }

            if (isNew) {
                ClothesInfoAdapter newAdapter = new ClothesInfoAdapter(context,
                        TestBasePullToRefreshListViewActivity.this);
                lvOrderReceiveClothes.setAdapter(newAdapter);
            }

            ClothesInfoAdapter adapter = (ClothesInfoAdapter) lvOrderReceiveClothes.getAdapter();
            adapter.setList(item.getClothesInfo());
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(Object item, View view, int position) {
        // 点击衣物项
        if (item instanceof ClothesInfo) {
            Intent i = new Intent(TestBasePullToRefreshListViewActivity.this,
                    ClothesDetailActivity.class);
            i.putExtra(ClothesDetailActivity.EXTRA_CLOTHES_CODE, ((ClothesInfo) item)
                    .getClothesCode());
            startActivity(i);
        }
    }


    public static void actionStart(Activity context, String activityCode) {
        Intent intent = new Intent(context, TestBasePullToRefreshListViewActivity.class);
        intent.putExtra(EXTRA_OUTERCODE, activityCode);
        context.startActivity(intent);
    }
}
