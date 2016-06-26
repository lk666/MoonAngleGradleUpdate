package cn.com.bluemoon.delivery.module.clothing.collect.withorder;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.ResultScanOrderInfo;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.ResultWithOrderClothingCollectList;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.WithOrderClothingCollectOrder;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.module.base.BaseFragment;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.clothing.collect.ClothingTabActivity;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.delivery.utils.ViewHolder;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.utils.LibConstants;

// TODO: lk 2016/6/13 可写个基类专门处理这种基本逻辑一样的fragment，将公共逻辑抽取（一步步来），
// 可参照dobago的BaseRefreshListFragment

/**
 * 有订单收衣管理
 * Created by luokai on 2016/6/12.
 */
public class WithOrderManageFragment extends BaseFragment implements OnListItemClickListener {
    private final static int REQUEST_CODE_WITH_ORDER_COLLECT_BOOK_IN_ACTIVITY = 0x12;
    private static final int RESULT_CODE_MANUAL = 0x23;
    private static final int REQUEST_CODE_MANUAL = 0x43;

    private ClothingTabActivity main;
    private ResultWithOrderClothingCollectList orderList;
    private OrderAdapter adapter;

    Dialog refuseDialog;
    EditText editReason;
    Button btnOk;
    Button btnCancel;

    @Bind(R.id.listview_main)
    PullToRefreshListView listviewMain;

    private String type;

    /**
     * 扫描到/输入的数字码
     */
    private String scaneCode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        main = (ClothingTabActivity) getActivity();

        Bundle bundle = getArguments();
        type = bundle.getString(ClothingTabActivity.TYPE);

        initCustomActionBar();

        View v = inflater.inflate(R.layout.fragment_tab_with_order_clothing_collect, container,
                false);

        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listviewMain.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

        adapter = new OrderAdapter(main, this);
        listviewMain.setAdapter(adapter);

        listviewMain.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                getData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            }

        });

        View emptyView = LayoutInflater.from(main).inflate(R.layout.layout_no_data, null);
        ((TextView) emptyView.findViewById(R.id.txt_content)).setText(R.string
                .with_order_collect_no_order);
        listviewMain.setEmptyView(emptyView);

        getData();
    }

    private void getData() {
        String token = ClientStateManager.getLoginToken(main);

        if (type.equals(ClothingTabActivity.WITH_ORDER_COLLECT_MANAGE)) {
            showProgressDialog();
            DeliveryApi.getOrderInfos(token, withOrderListHandler);
        }
    }

    private void setData(ResultWithOrderClothingCollectList resultOrder) {
        orderList = resultOrder;
        adapter.setList(orderList.getOrderInfos());
        main.setAmountShow(type, orderList.getOrderInfos().size());
        adapter.notifyDataSetChanged();
    }

    AsyncHttpResponseHandler withOrderListHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            LogUtils.d(getDefaultTag(), "getOrderInfos result = " + responseString);
            dismissProgressDialog();
            listviewMain.onRefreshComplete();
            try {
                ResultWithOrderClothingCollectList result = JSON.parseObject(responseString,
                        ResultWithOrderClothingCollectList.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    setData(result);
                } else {
                    PublicUtil.showErrorMsg(main, result);
                }
            } catch (Exception e) {
                LogUtils.e(getDefaultTag(), e.getMessage());
                PublicUtil.showToastServerBusy();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            // TODO: lk 2016/6/19  LogUtils要换，tag没卵用
            LogUtils.e(getDefaultTag(), throwable.getMessage());
            dismissProgressDialog();
            listviewMain.onRefreshComplete();
            PublicUtil.showToastServerOvertime();
        }
    };

    private void initCustomActionBar() {

        CommonActionBar actionBar = new CommonActionBar(main.getActionBar(),
                new IActionBarListener() {

                    @Override
                    public void onBtnRight(View v) {
                        goScanCode();
                    }

                    @Override
                    public void onBtnLeft(View v) {
                        main.finish();
                    }

                    @Override
                    public void setTitle(TextView v) {
                        if (type.equals(ClothingTabActivity.WITH_ORDER_COLLECT_MANAGE)) {
                            v.setText(R.string.tab_title_with_order_collect_manage);
                        }
                    }
                });

        actionBar.getImgRightView().setImageResource(R.mipmap.ewmtxm);
        actionBar.getImgRightView().setVisibility(View.VISIBLE);
    }

    /**
     * 打开扫码界面
     */
    private void goScanCode() {
        PublicUtil.openScanOrder(main, WithOrderManageFragment.this,
                getString(R.string.coupons_scan_code_title),
                getString(R.string.with_order_collect_manual_input_code_btn),
                Constants.REQUEST_SCAN, RESULT_CODE_MANUAL);
    }

    /**
     * 弹出拨打电话
     *
     * @param num
     */
    private void call(String num) {
        PublicUtil.showCallPhoneDialog(main, num);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }

        switch (requestCode) {
            case Constants.REQUEST_SCAN:
                // 扫码返回
                if (resultCode == Activity.RESULT_OK) {
                    String resultStr = data.getStringExtra(LibConstants.SCAN_RESULT);
                    handleScaneCodeBack(resultStr);
                }
                //   跳转到手动输入
                else if (resultCode == RESULT_CODE_MANUAL) {
                    Intent intent = new Intent(getActivity(), ManualInputCodeActivity.class);
                    WithOrderManageFragment.this.startActivityForResult(intent,
                            REQUEST_CODE_MANUAL);
                }
                break;

            // 手动输入返回
            case REQUEST_CODE_MANUAL:
                // 数字码返回
                if (resultCode == Activity.RESULT_OK) {
                    String resultStr = data.getStringExtra(ManualInputCodeActivity
                            .RESULT_EXTRA_CODE);
                    handleScaneCodeBack(resultStr);
                }
                //   跳转到扫码输入
                else if (resultCode == ManualInputCodeActivity.RESULT_CODE_SCANE_CODE) {
                    goScanCode();
                }
                break;

            // 收衣登记返回，不管有没操作，统一刷一次数据
            case REQUEST_CODE_WITH_ORDER_COLLECT_BOOK_IN_ACTIVITY:
                getData();
                break;
            default:
                break;
        }
    }


    /**
     * 处理扫码、手动输入数字码返回
     *
     * @param code
     */
    private void handleScaneCodeBack(String code) {
        showProgressDialog();
        scaneCode = code;
        DeliveryApi.scanOrderInfo(scaneCode, ClientStateManager.getLoginToken(main),
                scanOrderInfoHandler);
    }

    /**
     * 扫描接单接口返回
     */
    AsyncHttpResponseHandler scanOrderInfoHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            LogUtils.d(getDefaultTag(), "扫描接单接口 result = " + responseString);
            dismissProgressDialog();
            try {
                ResultScanOrderInfo result = JSON.parseObject(responseString,
                        ResultScanOrderInfo.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    // TODO: lk 2016/6/26 模拟数据返回的ResponseCode为1
                    handleScaneCodeInfo(result);
                } else {
                    PublicUtil.showErrorMsg(main, result);
                }
            } catch (Exception e) {
                LogUtils.e(getDefaultTag(), e.getMessage());
                PublicUtil.showToastServerBusy();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            LogUtils.e(getDefaultTag(), throwable.getMessage());
            dismissProgressDialog();
            PublicUtil.showToastServerOvertime();
        }
    };

    private void handleScaneCodeInfo(ResultScanOrderInfo result) {
        // 扫描的是衣物编码
        if (result.getCodeType().equals(ResultScanOrderInfo.CODE_TYPE_CLOTHES_CODE)) {
// TODO: lk 2016/6/26 跳转到确认接收界面，需要新增一个参数 scaneCode
            //    confirmDeliver(result.getCollectCode(), scaneCode);
        }

        // 扫描的是订单编码
        else if (result.getCodeType().equals(ResultScanOrderInfo.CODE_TYPE_OUTER_CODE)) {
            gotoCollectBookIn(scaneCode, null);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    /**
     * 收衣订单Adapter
     */
    class OrderAdapter extends BaseListAdapter<WithOrderClothingCollectOrder> {
        public OrderAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_with_order_clothing_collect_order_list;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            final WithOrderClothingCollectOrder order = (WithOrderClothingCollectOrder) getItem
                    (position);
            if (order == null) {
                return;
            }

            TextView tvCollectNumberTitle = ViewHolder.get(convertView, R.id
                    .tv_collect_number_title);
            TextView tvNumber = ViewHolder.get(convertView, R.id.tv_number);
            View tvDetail = ViewHolder.get(convertView, R.id.tv_detail);
            View ivDetail = ViewHolder.get(convertView, R.id.iv_detail);
            Button btnRightAction = ViewHolder.get(convertView, R.id.btn_right_action);
            TextView tvRightAction = ViewHolder.get(convertView, R.id.tv_right_action);
            TextView tvCustomerName = ViewHolder.get(convertView, R.id.tv_customer_name);
            TextView tvCustomerPhone = ViewHolder.get(convertView, R.id.tv_customer_phone);
            TextView tvAddress = ViewHolder.get(convertView, R.id.tv_address);
            TextView tvPayTotal = ViewHolder.get(convertView, R.id.tv_pay_total);
            TextView tvReceivableCount = ViewHolder.get(convertView, R.id.tv_receivable_count);
            TextView tvActualCount = ViewHolder.get(convertView, R.id.tv_actual_count);

            // 订单编号开头
            tvCollectNumberTitle.setVisibility(View.GONE);
            if (order.getOuterCodeType().equals(WithOrderClothingCollectOrder
                    .OUTERCODE_TYPE_WASHORDER)) {
                tvCollectNumberTitle.setVisibility(View.VISIBLE);
                // 服务单号
                tvNumber.setText(order.getCollectCode());
            } else {
                // 收衣单号
                tvNumber.setText(order.getOuterCode());
            }

            // 名称
            tvCustomerName.setText(order.getReceiveName());

            //电话
            tvCustomerPhone.setText(order.getReceivePhone());

            // 地址
            StringBuilder address = new StringBuilder(order.getProvince()).append(order.getCity())
                    .append(order.getCounty()).append(order.getStreet()).append(order.getVillage
                            ()).append(order.getAddress());
            tvAddress.setText(address);

            switch (order.getWashStatus()) {
                // 待接单
                case WithOrderClothingCollectOrder.WASH_STATUS_WAIT_ACCEPT:
                    // 右边按钮
                    btnRightAction.setVisibility(View.VISIBLE);
                    //右边文本按钮
                    tvRightAction.setVisibility(View.VISIBLE);

                    btnRightAction.setText(getString(R.string.with_order_collect_btn_accept));
                    tvRightAction.setText(getString(R.string.with_order_collect_txt_cancle_accept));
                    break;

                // 收衣中(开始收衣)
                case WithOrderClothingCollectOrder.WASH_STATUS_ANGEL_LAUNDRYING:
                    // 开始收衣
                    if (order.getActualCount() <= 0) {
                        btnRightAction.setVisibility(View.VISIBLE);
                        tvRightAction.setVisibility(View.GONE);

                        btnRightAction.setText(getString(R.string
                                .with_order_collect_btn_start_collect));
                    }

                    // 继续收衣
                    else {
                        btnRightAction.setVisibility(View.VISIBLE);
                        tvRightAction.setVisibility(View.VISIBLE);

                        btnRightAction.setText(getString(R.string
                                .with_order_collect_btn_continue_collect));
                        tvRightAction.setText(getString(R.string.with_order_collect_txt_translate));
                    }
                    break;

                // 衣物转交
                case WithOrderClothingCollectOrder.WASH_STATUS_TRANSFER:
                    btnRightAction.setVisibility(View.INVISIBLE);
                    tvRightAction.setVisibility(View.VISIBLE);

                    tvRightAction.setText(getString(R.string.with_order_collect_txt_translate));
                    break;

                //确认接收
                case WithOrderClothingCollectOrder.WASH_STATUS_RECEIVE:
                    btnRightAction.setVisibility(View.VISIBLE);
                    tvRightAction.setVisibility(View.VISIBLE);

                    btnRightAction.setText(getString(R.string
                            .with_order_collect_btn_confirm_translate));
                    tvRightAction.setText(getString(R.string
                            .with_order_collect_txt_refuse_translate));
                    break;
                case WithOrderClothingCollectOrder.WASH_STATUS_CONTINUE_LAUNDRYING:
                case WithOrderClothingCollectOrder.WASH_STATUS_WAIT_DISPATCH:
                case WithOrderClothingCollectOrder.WASH_STATUS_ALREADY_ACCEPT:
                default:
                    btnRightAction.setVisibility(View.GONE);
                    tvRightAction.setVisibility(View.GONE);
                    break;
            }

            tvPayTotal.setText(String.format("%.2f", (order.getPayTotal() / 100.0)));
            tvReceivableCount.setText(order.getReceivableCount() + "");
            tvActualCount.setText(order.getActualCount() + "");

            if (isNew) {
                tvCustomerPhone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                tvCustomerPhone.getPaint().setAntiAlias(true);

                tvRightAction.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                tvRightAction.getPaint().setAntiAlias(true);
            }

            setClickEvent(isNew, position, tvDetail, ivDetail, btnRightAction, tvRightAction,
                    tvCustomerPhone);
        }
    }

    /**
     * 开始/继续收衣
     */
    private void gotoCollectBookIn(String outerCode, String collectCode) {
        Intent intent = new Intent(main, WithOrderCollectBookInActivity.class);
        intent.putExtra(WithOrderCollectBookInActivity.EXTRA_OUTERCODE, outerCode == null ? "" :
                outerCode);
        intent.putExtra(WithOrderCollectBookInActivity.EXTRA_COLLECTCODE, collectCode == null ?
                "" : collectCode);
        WithOrderManageFragment.this.startActivityForResult(intent,
                REQUEST_CODE_WITH_ORDER_COLLECT_BOOK_IN_ACTIVITY);
    }

    @Override
    public void onItemClick(Object item, View view, int position) {
        WithOrderClothingCollectOrder order = (WithOrderClothingCollectOrder) item;
        if (order == null) {
            return;
        }
        switch (view.getId()) {
            // 打电话
            case R.id.tv_customer_phone:
                call(order.getReceivePhone());
                break;
            // TODO: lk 2016/6/19    订单详情
            case R.id.tv_detail:
            case R.id.iv_detail:
                PublicUtil.showToast("订单详情:" + order.getOuterCodeType() + ","
                        + order.getOuterCode() + ", " + order.getCollectCode());
                break;

            case R.id.btn_right_action:
                switch (order.getWashStatus()) {
                    case WithOrderClothingCollectOrder.WASH_STATUS_WAIT_ACCEPT:
//                        PublicUtil.showToast("接单:" + order.getOuterCodeType() + ","
//                                + order.getOuterCode() + ", " + order.getCollectCode());
                        acceptOrder(ClientStateManager.getLoginToken(getActivity()), order
                                .getOuterCode());
                        break;

                    // 开始/继续收衣
                    case WithOrderClothingCollectOrder.WASH_STATUS_ANGEL_LAUNDRYING:
                        gotoCollectBookIn(order.getOuterCode(), order.getCollectCode());
                        break;

                    //确认接收
                    case WithOrderClothingCollectOrder.WASH_STATUS_RECEIVE:
                        confirmDeliver(order.getCollectCode());
                        break;

                    case WithOrderClothingCollectOrder.WASH_STATUS_TRANSFER:
                    case WithOrderClothingCollectOrder.WASH_STATUS_CONTINUE_LAUNDRYING:
                    case WithOrderClothingCollectOrder.WASH_STATUS_WAIT_DISPATCH:
                    case WithOrderClothingCollectOrder.WASH_STATUS_ALREADY_ACCEPT:
                    default:
                        break;
                }
                break;
            case R.id.tv_right_action:
                switch (order.getWashStatus()) {
                    // 取消订单
                    case WithOrderClothingCollectOrder.WASH_STATUS_WAIT_ACCEPT:
//                        PublicUtil.showToast(" 取消订单:" + order.getOuterCodeType() + ","
//                                + order.getOuterCode() + ", " + order.getCollectCode());
                        cancelOrder(ClientStateManager.getLoginToken(getActivity()), order
                                .getOuterCode());
                        break;

                    // 衣服转交
                    case WithOrderClothingCollectOrder.WASH_STATUS_ANGEL_LAUNDRYING:
                        if (order.getActualCount() > 0 &&
                                order.getReceivableCount() > order.getActualCount()) {
                            deliver(order.getCollectCode());
                        }
                        break;

                    // 衣物转交
                    case WithOrderClothingCollectOrder.WASH_STATUS_TRANSFER:
                        deliver(order.getCollectCode());
                        break;

                    // 拒绝接收
                    case WithOrderClothingCollectOrder.WASH_STATUS_RECEIVE:
                        refuseDialogInit(order.getOuterCode());
                        break;
                    case WithOrderClothingCollectOrder.WASH_STATUS_CONTINUE_LAUNDRYING:
                    case WithOrderClothingCollectOrder.WASH_STATUS_WAIT_DISPATCH:
                    case WithOrderClothingCollectOrder.WASH_STATUS_ALREADY_ACCEPT:
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }


    private void refuseDialogInit(String collectCode) {
        final String code = collectCode;
        if (null == refuseDialog) {
            refuseDialog = new Dialog(getActivity(), R.style.dialog);
            refuseDialog.setContentView(R.layout.dialog_refuse_reason_view);
            editReason = (EditText) refuseDialog.findViewById(R.id.txt_reason);
            btnOk = (Button) refuseDialog.findViewById(R.id.positiveButton);
            btnCancel = (Button) refuseDialog.findViewById(R.id.negativeButton);
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!StringUtil.isEmpty(editReason.getText().toString())) {
                        refuseDeliver(ClientStateManager.getLoginToken(getActivity()), code,
                                editReason.getText().toString());
                        refuseDialog.dismiss();
                    }
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refuseDialog.dismiss();
                }
            });
        }
        editReason.setText("");
        refuseDialog.show();
    }


    private void cancelOrder(String token, String outerCode) {
        showProgressDialog();
        DeliveryApi.signOrderInfo(token, outerCode, Constants.STATUS_CANCEL_ORDER, baseHandler);
    }

    private void acceptOrder(String token, String outerCode) {
        showProgressDialog();
        DeliveryApi.signOrderInfo(token, outerCode, Constants.STATUS_ACCEPTL_ORDER, baseHandler);
    }


    private void refuseDeliver(String token, String collectCode, String remark) {
        showProgressDialog();
        DeliveryApi.refuseOrderInfo(token, collectCode, remark, baseHandler);
    }


    AsyncHttpResponseHandler baseHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d(getDefaultTag(), "baseHandler result = " + responseString);
            dismissProgressDialog();
            try {
                ResultBase result = JSON.parseObject(responseString,
                        ResultBase.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    PublicUtil.showToast(result.getResponseMsg());
                    getData();
                } else {
                    PublicUtil.showErrorMsg(getActivity(), result);
                }
            } catch (Exception e) {
                LogUtils.e(getDefaultTag(), e.getMessage());
                PublicUtil.showToastServerBusy();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            LogUtils.e(getDefaultTag(), throwable.getMessage());
            dismissProgressDialog();
            PublicUtil.showToastServerOvertime();
        }
    };

    private void deliver(String collectCode) {
        ClothingDeliverActivity.actionStart(getActivity(), collectCode);
    }

    private void confirmDeliver(String collectCode) {
        ClothingDeliverConfirmActivity.actionStart(getActivity(), collectCode);
    }

}
