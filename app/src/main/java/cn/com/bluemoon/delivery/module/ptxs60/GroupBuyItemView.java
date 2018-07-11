package cn.com.bluemoon.delivery.module.ptxs60;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.PTXS60Api;
import cn.com.bluemoon.delivery.app.api.model.ptxs60.ResultQueryOrderList;
import cn.com.bluemoon.delivery.app.api.model.ptxs60.ResultRePay;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.base.WithContextTextHttpResponseHandler;
import cn.com.bluemoon.delivery.ui.NoScrollListView;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.lib_widget.module.form.BMAngleBtn2View;

public class GroupBuyItemView extends RelativeLayout implements View.OnClickListener, OnListItemClickListener {
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.tv_order_code)
    TextView tvOrderCode;
    @BindView(R.id.tv_order_pay_time)
    TextView tvOrderPayTime;
    @BindView(R.id.div)
    View div;
    @BindView(R.id.lv_order_detail)
    NoScrollListView lvOrderDetail;
    @BindView(R.id.btn_pay)
    BMAngleBtn2View btnPay;
    @BindView(R.id.tv_count)
    TextView tvCount;
    @BindView(R.id.tvprice)
    TextView tvprice;

    public GroupBuyItemView(Context context) {
        super(context);
        init();
    }

    public GroupBuyItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private Paint paint;

    private void init() {
        // ViewGroup默认不会调用ondraw
//        setWillNotDraw(false);
        LayoutInflater.from(getContext()).inflate(R.layout.view_group_buy_item, this, true);
        ButterKnife.bind(this);

        btnPay.setOnClickListener(this);
        adapter = new OrderDetailLogAdapter(getContext(), this);
        lvOrderDetail.setAdapter(adapter);
    }

    private OrderDetailLogAdapter adapter;

    private ResultQueryOrderList.OrderListBean data;

    public void setData(ResultQueryOrderList.OrderListBean data) {
        if (data == null) {
            return;
        }
        this.data = data;

        setStatus();

        tvOrderCode.setText(data.orderCode);

        tvCount.setText(getResources().getString(R.string.total_count, data.orderTotalNum));
        tvprice.setText(getResources().getString(R.string.order_money,
                StringUtil.getPriceFormat(data.orderTotalMoney)));

        adapter.setList(data.orderDetail);
        adapter.notifyDataSetChanged();
    }

    /**
     * 设置不同状态
     */
    private void setStatus() {

        switch (data.payStatus) {

            case ResultQueryOrderList.PAY_STATUS_FAIL:
            case ResultQueryOrderList.PAY_STATUS_WAIT:
                tvStatus.setText(AppContext.getInstance().getString(R.string.wait));
                tvStatus.setTextColor(getResources().getColor(R.color.orange_ff6c47));

                tvOrderPayTime.setVisibility(GONE);

                btnPay.setVisibility(VISIBLE);
                break;

            case ResultQueryOrderList.PAY_STATUS_SUCCESS:
                tvStatus.setText(AppContext.getInstance().getString(R.string.success));
                tvStatus.setTextColor(getResources().getColor(R.color.green_0dd66f));

                tvOrderPayTime.setVisibility(VISIBLE);
                tvOrderPayTime.setText(getResources().getString(R.string.pending_order_pay_time,
                        DateUtil.getTimeToYMDHMS(data.orderPayTime)));

                btnPay.setVisibility(GONE);
                break;


            case ResultQueryOrderList.PAY_STATUS_CANCEL:
                tvStatus.setText(AppContext.getInstance().getString(R.string.cancel));
                tvStatus.setTextColor(getResources().getColor(R.color.txt_999));

                tvOrderPayTime.setVisibility(GONE);

                btnPay.setVisibility(GONE);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (data != null && ResultQueryOrderList.PAY_STATUS_WAIT.equals(data.payStatus)) {
            // 待支付的，跳到支付页面
            BaseActivity aty = (BaseActivity) getContext();
            aty.showWaitDialog();
            PTXS60Api.rePay(data.orderCode, ClientStateManager.getLoginToken(),
                    (WithContextTextHttpResponseHandler) aty.getNewHandler
                            (GroupBuyListActivity.REQUEST_CODE_RE_PAY, ResultRePay.class));
        }
    }

    @Override
    public void onItemClick(Object item, View view, int position) {

    }

    public class OrderDetailLogAdapter extends BaseListAdapter<ResultQueryOrderList.OrderListBean
            .OrderDetailBean> {

        public OrderDetailLogAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_order_detail;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            ResultQueryOrderList.OrderListBean.OrderDetailBean deliver = (ResultQueryOrderList
                    .OrderListBean.OrderDetailBean) getItem(position);
            if (deliver == null) {
                return;
            }

            TextView tvName = getViewById(R.id.tv_detail_name);
            TextView tvCount = getViewById(R.id.tv_detail_count);
            tvName.setText(deliver.productDesc);
            tvCount.setText(getResources().getString(R.string.text_count_format, deliver.orderNum));
        }
    }

}
