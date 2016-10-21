package cn.com.bluemoon.delivery.module.wash.returning.manager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.BasePullToRefreshListViewFragment;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.app.api.model.wash.manager.ResultExpress;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.utils.LibConstants;
import cn.com.bluemoon.lib.view.CommonAlertDialog;

/**
 * Created by ljl on 2016/9/19.
 */
public class DeliveryFragment extends BasePullToRefreshListViewFragment {

    private long pageFlag = 0;
    private int index;

    @Override
    protected String getTitleString() {
        return getString(R.string.manger_tab_1);
    }

    @Override
    protected void setActionBar(CommonActionBar actionBar) {
        super.setActionBar(actionBar);
        ImageView rightImg = actionBar.getImgRightView();
        rightImg.setImageResource(R.mipmap.scan_top_nav);
        rightImg.setVisibility(View.VISIBLE);
        rightImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PublicUtil.openNewScanView(DeliveryFragment.this, getString(R.string.scan_delivery_title), getString(R.string.input_by_hand), null, 1);
            }
        });
    }

    @Override
    protected BaseListAdapter getNewAdapter() {
        return new DeliveryAdapter(getActivity(), null);
    }

    @Override
    protected List getGetMoreList(ResultBase result) {
        ResultExpress r = (ResultExpress) result;
        pageFlag = ((ResultExpress) result).getPageFlag();
        return r.getExpressList();
    }

    @Override
    protected List getGetDataList(ResultBase result) {
        ResultExpress r = (ResultExpress) result;
        pageFlag = ((ResultExpress) result).getPageFlag();
        return r.getExpressList();
    }

    @Override
    protected PullToRefreshBase.Mode getMode() {
        return PullToRefreshBase.Mode.BOTH;
    }

    @Override
    protected void initPullToRefreshListView(PullToRefreshListView ptrlv) {
        ptrlv.getRefreshableView().setDivider(null);
        ptrlv.getRefreshableView().setDividerHeight(0);
        ptrlv.getRefreshableView().setCacheColorHint(Color.TRANSPARENT);
    }

    @Override
    protected void invokeGetDataDeliveryApi(int requestCode) {
        setAmount();
        pageFlag = 0;
        ReturningApi.queryExpressReceiveList(0, getToken(), getNewHandler(requestCode, ResultExpress.class));
    }

    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {
        ReturningApi.queryExpressReceiveList(pageFlag, getToken(), getNewHandler(requestCode, ResultExpress.class));
    }

    @Override
    public void onItemClick(Object item, View view, int position) {

    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        super.onSuccessResponse(requestCode, jsonString, result);
        if (requestCode == 1) {
            getList().remove(index);
            getAdapter().notifyDataSetChanged();
            toast(result.getResponseMsg());
            setAmount();
            if (getList().isEmpty()) {
                initData();
            }
        } else if (requestCode == 2) {
            toast(result.getResponseMsg());
            initData();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data != null) {
            String number = data.getStringExtra(LibConstants.SCAN_RESULT);
            showWaitDialog();
            ReturningApi.confirmReceive(number, getToken(), getNewHandler(2, ResultBase.class));
        }
    }

    class DeliveryAdapter extends BaseListAdapter<ResultExpress.ExpressListBean> {

        public DeliveryAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.list_item_return_express;
        }

        @Override
        protected void setView(final int position, View convertView, ViewGroup parent, boolean isNew) {
            TextView txtNo = getViewById(R.id.txt_no);
            LinearLayout layoutDetail = getViewById(R.id.layout_detail);
            TextView txtNumber = getViewById(R.id.txt_number);
            TextView txtLogistics = getViewById(R.id.txt_logistics);
            Button receivingOrdersAction = getViewById(R.id.receiving_orders_action);
            final ResultExpress.ExpressListBean result = list.get(position);
            txtNo.setText(result.getCompanyName() + "：" + result.getExpressCode());
            txtNumber.setText(String.valueOf(result.getBackOrderNum()));
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.layout_detail :
                            Intent intent = new Intent(getActivity(), DeliveryDetailActivity.class);
                            intent.putExtra("expressName", result.getCompanyName());
                            intent.putExtra("expressCode", result.getExpressCode());
                            intent.putExtra("count", list.size());
                            startActivity(intent);
                            break;
                        case R.id.txt_logistics :
                            LogisticsActivity.actStart(getActivity(),
                                    result.getCompanyCode(), result.getExpressCode());
                            break;
                        case R.id.receiving_orders_action :
                            new CommonAlertDialog.Builder(getActivity())
                                    .setMessage(getString(R.string.manage_get_produce_txt))
                                    .setPositiveButton(R.string.cancel_with_space, null)
                                    .setNegativeButton(R.string.confirm_with_space, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            showWaitDialog();
                                            index = position;
                                            ReturningApi.confirmReceive(result.getExpressCode(), getToken(), getNewHandler(1, ResultBase.class));
                                        }
                                    }).show();
                            break;
                    }
                }
            };
            layoutDetail.setOnClickListener(listener);
            txtLogistics.setOnClickListener(listener);
            receivingOrdersAction.setOnClickListener(listener);
        }
    }
}
