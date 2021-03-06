package cn.com.bluemoon.delivery.module.wash.returning.manager;

import android.content.Context;
import android.content.DialogInterface;
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
import cn.com.bluemoon.delivery.app.api.model.wash.manager.ResultBackOrder;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.view.CommonAlertDialog;

/**
 * Created by ljl on 2016/9/19.
 */
public class ReturnFragment extends BasePullToRefreshListViewFragment {

    private long pageFlag = 0;
    private int index;
    private final String TYPE = "BACK_ORDER_WAIT_ACCEPTORDER";

    @Override
    protected String getTitleString() {
        return getString(R.string.manger_tab_2);
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
        setAmount();
        pageFlag = 0;
        ReturningApi.queryBackOrderList(TYPE, 0, 0, 0, getToken(),
                getNewHandler(requestCode, ResultBackOrder.class));
    }

    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {
        ReturningApi.queryBackOrderList(TYPE, pageFlag, 0, 0, getToken(),
                getNewHandler(requestCode, ResultBackOrder.class));
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
        }
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
        protected void setView(final int position, View convertView, ViewGroup parent, boolean isNew) {
            TextView txtNo = getViewById(R.id.txt_no);
            LinearLayout layoutDetail = getViewById(R.id.layout_detail);
            TextView txtCustomerName = getViewById(R.id.txt_customerName);
            TextView txtMobilePhone = getViewById(R.id.txt_mobilePhone);
            TextView txtAddress = getViewById(R.id.txt_address);
            Button btnAction = getViewById(R.id.btn_action);
            TextView txtTotalAmount = getViewById(R.id.txt_totalAmount);
            LinearLayout layoutUrgent = getViewById(R.id.layout_urgent);
            TextView txtUrgent = getViewById(R.id.txt_urgent);

            btnAction.setVisibility(View.VISIBLE);
            btnAction.setText(R.string.manage_go_return);
            final ResultBackOrder.BackOrderListBean result = list.get(position);
            txtCustomerName.setText(result.getCustomerName());
            txtMobilePhone.setText(result.getCustomerPhone());
            txtMobilePhone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            txtMobilePhone.getPaint().setAntiAlias(true);
            txtAddress.setText(result.getCustomerAddress());
            txtTotalAmount.setText(context.getString(R.string.manage_clothes_amount, result.getClothesNum()));

            if (result.isIsUrgent()) {
                layoutUrgent.setVisibility(View.VISIBLE);
                txtUrgent.setText(context.getString(R.string.manage_return_time,
                        DateUtil.getTime(result.getAppointBackTime(), "yyyy-MM-dd  HH:mm")));
            } else {
                layoutUrgent.setVisibility(View.GONE);
            }

            txtNo.setText(context.getString(R.string.manage_clothes_num, result.getBackOrderCode()));
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.layout_detail:
                            BackOrderDetailActivity.actStart(getActivity(), result, false);
                            break;
                        case R.id.btn_action:
                            new CommonAlertDialog.Builder(getActivity())
                                    .setMessage(getString(R.string.manage_get_clothes_code_txt))
                                    .setPositiveButton(R.string.cancel_with_space, null)
                                    .setNegativeButton(R.string.confirm_with_space, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            showWaitDialog();
                                            index = position;
                                            ReturningApi.returnClothes(result.getBackOrderCode(), getToken(), getNewHandler(1, ResultBase.class));
                                        }
                                    }).show();
                            break;
                        case R.id.txt_mobilePhone:
                            PublicUtil.showCallPhoneDialog2(getActivity(), result.getCustomerPhone());
                            break;
                    }
                }
            };
            layoutDetail.setOnClickListener(listener);
            txtMobilePhone.setOnClickListener(listener);
            btnAction.setOnClickListener(listener);
        }
    }
}
