package cn.com.bluemoon.delivery.module.wash.returning.manager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
public class SignFragment extends BasePullToRefreshListViewFragment {

    private long pageFlag = 0;
    private final String TYPE = "BACK_ORDER_WAIT_SIGN";

    @Override
    protected String getTitleString() {
        return getString(R.string.manger_tab_3);
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
            TextView txtNo = getViewById(R.id.txt_no);
            LinearLayout layoutDetail = getViewById(R.id.layout_detail);
            TextView txtCustomerName = getViewById(R.id.txt_customerName);
            TextView txtMobilePhone = getViewById(R.id.txt_mobilePhone);
            TextView txtAddress = getViewById(R.id.txt_address);
            Button btnAction = getViewById(R.id.btn_action);
            Button btnRefuseSign = getViewById(R.id.btn_refuse_sign);
            TextView txtTotalAmount = getViewById(R.id.txt_totalAmount);
            LinearLayout layoutUrgent = getViewById(R.id.layout_urgent);
            TextView txtUrgent = getViewById(R.id.txt_urgent);

            btnAction.setVisibility(View.VISIBLE);
            btnAction.setText("签收");
            btnRefuseSign.setVisibility(View.VISIBLE);
            final ResultBackOrder.BackOrderListBean result = list.get(position);
            if (result.isIsRefuse()) {
                btnRefuseSign.setText("查看拒签");
            } else {
                btnRefuseSign.setText("消费者拒签");
            }
            txtCustomerName.setText(result.getCustomerName());
            txtMobilePhone.setText(result.getCustomerPhone());
            txtMobilePhone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            txtMobilePhone.getPaint().setAntiAlias(true);
            txtAddress.setText(result.getAddress());
            txtTotalAmount.setText("衣物数量：" + result.getClothesNum());

            if (result.isIsUrgent()) {
                layoutUrgent.setVisibility(View.VISIBLE);
                txtUrgent.setText("预约还衣时间：" + DateUtil.getTime(result.getAppointBackTime(), "yyyy-MM-dd  HH:mm"));
            } else {
                layoutUrgent.setVisibility(View.GONE);
            }

            txtNo.setText("还衣单号：" + result.getBackOrderCode());
            //txtNumber.setText("还衣单数量：" + result.getBackOrderNum());
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.layout_detail:
                            longToast("detail");
                            break;
                        case R.id.btn_action:
                            Intent intent = new Intent(getActivity(), ManualSignActivity.class);
                            SignFragment.this.startActivityForResult(intent, 1);
                            break;
                        case R.id.txt_mobilePhone:
                            PublicUtil.showCallPhoneDialog2(getActivity(), result.getCustomerPhone());
                            break;
                        case R.id.btn_refuse_sign:
                            intent = new Intent(getActivity(), CustomRefuseActivity.class);
                            intent.putExtra("backOrderCode", result.getBackOrderCode());
                            SignFragment.this.startActivityForResult(intent, 2);
                            break;
                    }
                }
            };
            layoutDetail.setOnClickListener(listener);
            txtMobilePhone.setOnClickListener(listener);
            btnAction.setOnClickListener(listener);
            btnRefuseSign.setOnClickListener(listener);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data != null) {
            longToast(data.getStringExtra("fileName"));
        }
    }
}
