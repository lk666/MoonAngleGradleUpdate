package cn.com.bluemoon.delivery.module.wash.returning.manager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
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
import cn.com.bluemoon.delivery.module.wash.returning.manager.model.ResultBackOrder;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;

/**
 * Created by ljl on 2016/9/19.
 */
public class ReturnHistoryFragment extends BasePullToRefreshListViewFragment {

    private long pageFlag = 0;

    private final String TYPE = "BACK_ORDER_ALREADY_SIGN";

    @Override
    protected String getTitleString() {
        return getString(R.string.manger_tab_4);
    }


    @Override
    protected BaseListAdapter getNewAdapter() {
        return new SignAdapter(getActivity(), null);
    }

    @Override
    protected List getGetMoreList(ResultBase result) {
        ResultBackOrder r = (ResultBackOrder) result;
        pageFlag = r.getPageFlag();
        return r.getBackOrderList();
    }

    @Override
    protected List getGetDataList(ResultBase result) {
        ResultBackOrder r = (ResultBackOrder) result;
        pageFlag = r.getPageFlag();
        return r.getBackOrderList();
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
        pageFlag = 0;
        ReturningApi.queryBackOrderList(TYPE, 0, 0, 0, getToken(), getNewHandler(requestCode, ResultBackOrder.class));
    }

    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {
        ReturningApi.queryBackOrderList(TYPE, pageFlag, 0, 0, getToken(), getNewHandler(requestCode, ResultBackOrder.class));
    }

    @Override
    public void onItemClick(Object item, View view, int position) {

    }

    class SignAdapter extends BaseListAdapter<ResultBackOrder.BackOrderListBean> {


        public SignAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.list_item_return_and_sign;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            final ResultBackOrder.BackOrderListBean result = list.get(position);
            TextView txtNo = getViewById(R.id.txt_no);
            LinearLayout layoutDetail = getViewById(R.id.layout_detail);
            LinearLayout layoutHistoryDetail = getViewById(R.id.layout_history_detail);
            TextView txtCustomerName = getViewById(R.id.txt_customerName);
            TextView txtMobilePhone = getViewById(R.id.txt_mobilePhone);
            TextView txtAddress = getViewById(R.id.txt_address);
            TextView txtTotalAmount = getViewById(R.id.txt_totalAmount);
            TextView txtUrgent = getViewById(R.id.txt_urgent2);
            TextView txtSignTime = getViewById(R.id.txt_sign_time);
            TextView txtStatus = getViewById(R.id.txt_status);
            ImageView img1 = getViewById(R.id.img_right1);
            ImageView img = getViewById(R.id.img_right);
            img.setVisibility(View.VISIBLE);
            img1.setVisibility(View.GONE);
            if (result.getSignTime() > 0) {
                txtStatus.setVisibility(View.VISIBLE);
                txtStatus.setText("已完成");
                txtSignTime.setText("签收时间："+ DateUtil.getTime(result.getSignTime(),"yyyy-MM-dd HH:mm:ss"));
            } else {
                txtStatus.setVisibility(View.GONE);
            }


            txtCustomerName.setText(result.getCustomerName());
            txtMobilePhone.setText(result.getCustomerPhone());
            txtMobilePhone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            txtMobilePhone.getPaint().setAntiAlias(true);
            txtAddress.setText(result.getAddress());
            txtTotalAmount.setText("衣物数量：" + result.getClothesNum());

            if (result.isIsUrgent()) {
                txtUrgent.setVisibility(View.VISIBLE);
            } else {
                txtUrgent.setVisibility(View.GONE);
            }
            txtSignTime.setVisibility(View.VISIBLE);
            txtNo.setText("还衣单号：" + result.getBackOrderCode());
            //txtNumber.setText("还衣单数量：" + result.getBackOrderNum());
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.txt_mobilePhone:
                            PublicUtil.showCallPhoneDialog2(getActivity(), result.getCustomerPhone());
                            break;
                        case R.id.layout_history_detail:
                            BackOrderDetailActivity.actStart(getActivity(), result, true);
                            break;
                    }
                }
            };
            layoutDetail.setOnClickListener(listener);
            txtMobilePhone.setOnClickListener(listener);
            layoutHistoryDetail.setOnClickListener(listener);
        }
    }
}
