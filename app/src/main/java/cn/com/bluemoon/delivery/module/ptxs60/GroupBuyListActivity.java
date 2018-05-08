package cn.com.bluemoon.delivery.module.ptxs60;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.PTXS60Api;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.ptxs60.ResultQueryOrderList;
import cn.com.bluemoon.delivery.app.api.model.ptxs60.ResultRePay;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.BasePullToRefreshListViewActivity;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.base.WithContextTextHttpResponseHandler;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib_widget.module.form.BMListPaginationView;

/**
 * 团购列表
 * Created by lk on 2018/5/2.
 */

public class GroupBuyListActivity extends BasePullToRefreshListViewActivity {

    private long timestamp = 0;
    private View footView;
    public static final int REQUEST_CODE_RE_PAY = 0x333;

    public static void actStart(Context context) {
        Intent intent = new Intent(context, GroupBuyListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void setActionBar(CommonActionBar titleBar) {
        super.setActionBar(titleBar);
        titleBar.getImgRightView().setVisibility(View.VISIBLE);
        titleBar.getImgRightView().setImageResource(R.mipmap.team_add);
    }

    @Override
    protected void onActionBarBtnRightClick() {
        CreateGroupBuyActivity.actStart(this);
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.title_group_buy_list);
    }

    @Override
    protected void initPullToRefreshListView(PullToRefreshListView ptrlv) {
        super.initPullToRefreshListView(ptrlv);
        ptrlv.getRefreshableView().setDivider(null);

        int padding = getResources().getDimensionPixelOffset(R.dimen.space_10);
        ptrlv.getRefreshableView().setDividerHeight(padding);
        ptrlv.getRefreshableView().setPadding(padding,
                getResources().getDimensionPixelOffset(R.dimen.space_2), padding, padding);
        ptrlv.getRefreshableView().setClipChildren(false);
        ptrlv.getRefreshableView().setClipToPadding(false);

        footView = new BMListPaginationView(this);
        ptrlv.getRefreshableView().addFooterView(footView);
        footView.setVisibility(View.GONE);
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        super.onSuccessResponse(requestCode, jsonString, result);
        switch (requestCode) {
            //支付查询流水号
            case REQUEST_CODE_RE_PAY:
                ResultRePay resultPay = (ResultRePay) result;
                PayActivity.actStart(this, resultPay.payInfo.paymentTransaction, resultPay
                        .payInfo.payTotal, resultPay.payInfo.paymentList);
                break;
        }
    }

    @Override
    protected BaseListAdapter getNewAdapter() {
        return new ItemAdapter(this, this);
    }

    @Override
    protected List getGetMoreList(ResultBase result) {
        return getGetData(result, false);
    }

    @Override
    protected List getGetDataList(ResultBase result) {
        return getGetData(result, true);
    }

    private List getGetData(ResultBase result, boolean isRefresh) {
        ResultQueryOrderList order = (ResultQueryOrderList) result;

        List<ResultQueryOrderList.OrderListBean> list = order.orderList;
        if (list == null) {
            return null;
        }

        timestamp = order.timestamp;

        if (list.size() < Constants.PAGE_SIZE) {
            ptrlv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            if (list.size() == 0 && isRefresh) {
                footView.setVisibility(View.GONE);
            } else {
                footView.setVisibility(View.VISIBLE);
            }
        } else {
            ptrlv.setMode(PullToRefreshBase.Mode.BOTH);
            footView.setVisibility(View.GONE);
        }

        return list;
    }

    @Override
    protected PullToRefreshBase.Mode getMode() {
        return PullToRefreshBase.Mode.BOTH;
    }

    @Override
    protected void invokeGetDataDeliveryApi(int requestCode) {
        timestamp = 0;
        PTXS60Api.queryOrderList(Constants.PAGE_SIZE, 0, getToken(),
                (WithContextTextHttpResponseHandler) getNewHandler(requestCode,
                        ResultQueryOrderList.class));
    }

    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {
        PTXS60Api.queryOrderList(Constants.PAGE_SIZE, timestamp, getToken(),
                (WithContextTextHttpResponseHandler) getNewHandler(requestCode,
                        ResultQueryOrderList.class));
    }

    @Override
    public void onItemClick(Object item, View view, int position) {
    }

    class ItemAdapter extends BaseListAdapter<ResultQueryOrderList.OrderListBean> {

        public ItemAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_group_buy;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            ResultQueryOrderList.OrderListBean item = list.get(position);
            GroupBuyItemView view = getViewById(R.id.gbiv);
            view.setData(item);
        }
    }

    @Override
    protected boolean isUseEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PayResult event) {
       initData();
    }
}