package cn.com.bluemoon.delivery.module.wash.enterprise;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.wash.enterprise.ResultEnterpriseList.EnterpriseOrderListBean;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;

class ItemAdapter extends BaseListAdapter<EnterpriseOrderListBean> {


        public ItemAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_enterprise ;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            EnterpriseOrderListBean item = (EnterpriseOrderListBean) getItem(position);

            if (item == null) {
                return;
            }

            TextView tvNumber = getViewById(R.id.tv_number);
            TextView tvTime = getViewById(R.id.tv_time);
            TextView tvState = getViewById(R.id.tv_state);
            TextView tvEmployeeCode = getViewById(R.id.tv_employee_code);
            LinearLayout layoutDetail = getViewById(R.id.layout_detail);
            TextView tvCustomerName = getViewById(R.id.tv_customer_name);
            TextView tvAddress = getViewById(R.id.tv_address);
            TextView tvAmount = getViewById(R.id.tv_amount);
            TextView tvPrice = getViewById(R.id.tv_price);
            TextView tvAddClothes = getViewById(R.id.tv_add_clothes);
            TextView tvCancelOrder = getViewById(R.id.tv_cancel_order);
            tvNumber.setText(item.outerCode);
            tvTime.setText(DateUtil.getTime(item.createTime, "yyyy/MM/dd HH:mm:ss"));
            tvState.setText(item.stateName);
            tvEmployeeCode.setText(item.collectBrcode);
            // 机构名称
            tvCustomerName.setText(item.employeeName);
            tvAddress.setText(item.branchName);
            if (Constants.OUTER_ACCEPT_CLOTHES.equals(item.state)) {
                tvAddClothes.setVisibility(View.VISIBLE);
            } else {
                tvAddClothes.setVisibility(View.GONE);
            }
            if (Constants.OUTER_WAIT_PAY.equals(item.state)
                    || Constants.OUTER_ACCEPT_CLOTHES.equals(item.state)) {
                tvCancelOrder.setVisibility(View.VISIBLE);
            } else {
                tvCancelOrder.setVisibility(View.INVISIBLE);
            }
            tvAmount.setText(context.getString(R.string.enterprise_order_amount, item.actualCount));
            tvPrice.setText(context.getString(R.string.order_money, StringUtil.formatPriceByFen(item.payTotal)));
            setClickEvent(isNew, position, layoutDetail, tvCancelOrder);
        }
    }