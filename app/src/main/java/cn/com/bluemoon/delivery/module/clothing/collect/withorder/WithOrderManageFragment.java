package cn.com.bluemoon.delivery.module.clothing.collect.withorder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.ResultScanOrderInfo;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.ResultWithOrderClothingCollectList;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.WithOrderClothingCollectOrder;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.module.clothing.collect.ClothingTabActivity;
import cn.com.bluemoon.delivery.module.oldbase.BaseFragment;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.delivery.utils.ViewHolder;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.utils.LibConstants;
import cn.com.bluemoon.lib.view.CommonAlertDialog;

// 可参照dobago的BaseRefreshListFragment

/**
 * 有订单收衣管理
 * Created by luokai on 2016/6/12.
 */
public class WithOrderManageFragment extends BaseFragment implements OnListItemClickListener {
    private final static int REQUEST_CODE_WITH_ORDER_COLLECT_BOOK_IN_ACTIVITY = 0x12;
    private static final int RESULT_CODE_MANUAL = 0x23;
    private static final int REQUEST_CODE_MANUAL = 0x43;
    private static final int REQUEST_CODE_DELIVER = 0x44;
    private static final int REQUEST_CODE_DELIVER_CONFIRM = 0x45;
    private ClothingTabActivity main;
    private OrderAdapter adapter;
    Dialog refuseDialog;
    EditText editReason;
    Button btnOk;
    Button btnCancel;
    String currentCollectCode;
    String currentOuterCode;
    @Bind(R.id.list_view_main)
    PullToRefreshListView listViewMain;

    private String manager;


    /**
     * 扫描到/输入的数字码
     */
    private String scaneCode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        main = (ClothingTabActivity) getActivity();

        Bundle bundle = getArguments();
        manager = bundle.getString("manager");

        initCustomActionBar();

        View v = inflater.inflate(R.layout.fragment_tab_with_order_clothing_collect, container,
                false);

        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listViewMain.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

        adapter = new OrderAdapter(main, this);
        listViewMain.setAdapter(adapter);

        listViewMain.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

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
        listViewMain.setEmptyView(emptyView);

        getData();
    }

    private void getData() {
        String token = ClientStateManager.getLoginToken(main);

        if (manager.equals(ClothingTabActivity.WITH_ORDER_COLLECT_MANAGE)) {
            showProgressDialog();
            DeliveryApi.getOrderInfos(token, withOrderListHandler);
        }
    }

    private void setData(ResultWithOrderClothingCollectList resultOrder) {
        adapter.setList(resultOrder.getOrderInfos());
        main.setAmountShow(manager, resultOrder.getOrderInfos().size());
        adapter.notifyDataSetChanged();
    }

    AsyncHttpResponseHandler withOrderListHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            if (listViewMain == null) {
                return;
            }
            LogUtils.d(getDefaultTag(), "getOrderInfos result = " + responseString);
            dismissProgressDialog();
            listViewMain.onRefreshComplete();
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
            if (listViewMain == null) {
                return;
            }
            // TODO: lk 2016/6/19  LogUtils要换，tag没卵用
            LogUtils.e(getDefaultTag(), throwable.getMessage());
            dismissProgressDialog();
            listViewMain.onRefreshComplete();
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
                        // if (manager.equals(ClothingTabActivity.WITH_ORDER_COLLECT_MANAGE)) {
                        v.setText(R.string.tab_title_with_order_collect_manage);
                        // }
                    }
                });

        actionBar.getImgRightView().setImageResource(R.mipmap.ewmtxm);
        actionBar.getImgRightView().setVisibility(View.VISIBLE);
    }

    /**
     * 打开扫码界面
     */
    private void goScanCode() {
        PublicUtil.openNewScanOrder(main, WithOrderManageFragment.this,
                getString(R.string.coupons_scan_code_title),
                getString(R.string.with_order_collect_manual_input_code_btn),
                Constants.REQUEST_SCAN, RESULT_CODE_MANUAL);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 收衣登记返回，不管有没操作，统一刷一次数据
        if (requestCode == REQUEST_CODE_WITH_ORDER_COLLECT_BOOK_IN_ACTIVITY) {
            getData();
        }

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
            case REQUEST_CODE_DELIVER_CONFIRM:
            case REQUEST_CODE_DELIVER:
                if (resultCode == Activity.RESULT_OK) {
                    getData();
                }
                break;
            default:
                break;
        }
    }


    /**
     * 处理扫码、手动输入数字码返回
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

            confirmDeliver(result.getCollectCode(), scaneCode);
        }

        // 扫描的是订单编码，刷新界面
        else if (result.getCodeType().equals(ResultScanOrderInfo.CODE_TYPE_OUTER_CODE)) {
            getData();
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
        private int colorTxtBtnBlue;
        private int colorTxtBtnGray;

        public OrderAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
            colorTxtBtnBlue = getResources().getColor(R.color.btn_blue);
            colorTxtBtnGray = getResources().getColor(R.color.text_black_light);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_with_order_clothing_collect_order_list;
        }

        @SuppressLint("DefaultLocale")
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
            //    View ivDetail = ViewHolder.get(convertView, R.id.iv_detail);
            Button btnRightAction = ViewHolder.get(convertView, R.id.btn_right_action);
            TextView tvRightAction = ViewHolder.get(convertView, R.id.tv_right_action);
            TextView tvCustomerName = ViewHolder.get(convertView, R.id.tv_customer_name);
            TextView tvCustomerPhone = ViewHolder.get(convertView, R.id.tv_customer_phone);
            TextView tvAddress = ViewHolder.get(convertView, R.id.tv_address);
            TextView tvPayTotal = ViewHolder.get(convertView, R.id.tv_pay_total);
            TextView tvReceivableCount = ViewHolder.get(convertView, R.id.tv_receivable_count);
            TextView tvActualCount = ViewHolder.get(convertView, R.id.tv_actual_count);
            View div = ViewHolder.get(convertView, R.id.div);

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
            tvCustomerName.setText(order.getCustomerName());

            //电话
            tvCustomerPhone.setText(order.getCustomerPhone());

            // 地址
            StringBuilder address = new StringBuilder(order.getProvince()).append(order.getCity())
                    .append(order.getCounty()).append(order.getStreet()).append(order.getVillage
                            ()).append(order.getAddress());
            tvAddress.setText(address);

            // 右边文本按钮
            switch (order.getWashStatus()) {
                // 待接单
                case WithOrderClothingCollectOrder.WASH_STATUS_WAIT_ACCEPT:
                    // 右边按钮
                    btnRightAction.setVisibility(View.VISIBLE);
                    //右边文本按钮
                    tvRightAction.setVisibility(View.VISIBLE);

                    btnRightAction.setText(getString(R.string.with_order_collect_btn_accept));
                    tvRightAction.setText(getString(R.string.with_order_collect_txt_cancle_accept));
                    tvRightAction.setTextColor(colorTxtBtnGray);
                    break;

                // 开始收衣
                case WithOrderClothingCollectOrder.WASH_STATUS_ALREADY_ACCEPT:
                    btnRightAction.setVisibility(View.VISIBLE);
                    tvRightAction.setVisibility(View.GONE);
                    btnRightAction.setText(getString(R.string
                            .with_order_collect_btn_start_collect));
                    break;

                // 收衣中
                case WithOrderClothingCollectOrder.WASH_STATUS_ANGEL_LAUNDRYING:
                    // 继续收衣
                    if (order.getReceivableCount() > order.getActualCount()) {
                        btnRightAction.setVisibility(View.VISIBLE);
                        tvRightAction.setVisibility(View.VISIBLE);

                        btnRightAction.setText(getString(R.string
                                .with_order_collect_btn_continue_collect));
                        tvRightAction.setText(getString(R.string.with_order_collect_txt_translate));
                        tvRightAction.setTextColor(colorTxtBtnBlue);
                    }

                    // 衣物转交
                    else {
                        btnRightAction.setVisibility(View.INVISIBLE);
                        tvRightAction.setVisibility(View.VISIBLE);
                        tvRightAction.setText(getString(R.string.with_order_collect_txt_translate));
                        tvRightAction.setTextColor(colorTxtBtnBlue);
                    }
                    break;

                //确认接收
                case WithOrderClothingCollectOrder.WASH_STATUS_TRANSFER:
                    btnRightAction.setVisibility(View.VISIBLE);
                    tvRightAction.setVisibility(View.VISIBLE);

                    btnRightAction.setText(getString(R.string
                            .with_order_collect_btn_confirm_translate));
                    tvRightAction.setText(getString(R.string
                            .with_order_collect_txt_refuse_translate));
                    tvRightAction.setTextColor(colorTxtBtnGray);
                    break;

                default:
                    btnRightAction.setVisibility(View.GONE);
                    tvRightAction.setVisibility(View.GONE);
                    break;
            }

            tvPayTotal.setText(String.format("%.2f", (order.getPayTotal() / 100.0)));
            tvReceivableCount.setText(String.valueOf(order.getReceivableCount()));
            tvActualCount.setText(String.valueOf(order.getActualCount()));

            if (isNew) {
                tvCustomerPhone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                tvCustomerPhone.getPaint().setAntiAlias(true);

                tvRightAction.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                tvRightAction.getPaint().setAntiAlias(true);
            }

            if (position < getCount() - 1) {
                div.setVisibility(View.VISIBLE);
            } else {
                div.setVisibility(View.GONE);
            }

            setClickEvent(isNew, position, tvDetail, btnRightAction, tvRightAction
            );
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
            case R.id.tv_detail:
                if (order.getOuterCodeType().equals(WithOrderClothingCollectOrder
                        .OUTERCODE_TYPE_ORDER)) {
                    // 服务单号
                    Intent i = new Intent(main, WithOrderOuterDetailActivity.class);
                    i.putExtra(WithOrderOuterDetailActivity.EXTRA_OUTERCODE, order.getOuterCode());
                    startActivity(i);

                } else if (order.getOuterCodeType().equals(WithOrderClothingCollectOrder
                        .OUTERCODE_TYPE_WASHORDER)) {
                    // 收衣单号
                    Intent i = new Intent(main, WithOrderOuterDetailActivity.class);
                    i.putExtra(WithOrderOuterDetailActivity.EXTRA_OUTERCODE, order.getOuterCode());
                    startActivity(i);
                }
                break;

            case R.id.btn_right_action:
                switch (order.getWashStatus()) {

                    // 待接单
                    case WithOrderClothingCollectOrder.WASH_STATUS_WAIT_ACCEPT:
                        acceptOrder(ClientStateManager.getLoginToken(getActivity()), order
                                .getOuterCode());
                        break;

                    // 开始收衣
                    case WithOrderClothingCollectOrder.WASH_STATUS_ALREADY_ACCEPT:
                        gotoCollectBookIn(order.getOuterCode(), order.getCollectCode());
                        break;

                    // 收衣中，继续收衣
                    case WithOrderClothingCollectOrder.WASH_STATUS_ANGEL_LAUNDRYING:
                        if (order.getReceivableCount() > order.getActualCount()) {
                            gotoCollectBookIn(order.getOuterCode(), order.getCollectCode());
                        }
                        break;

                    //确认接收
                    case WithOrderClothingCollectOrder.WASH_STATUS_TRANSFER:
                        confirmDeliver(order.getCollectCode());
                        break;

                    default:
                        break;
                }
                break;
            case R.id.tv_right_action:
                switch (order.getWashStatus()) {
                    // 取消订单
                    case WithOrderClothingCollectOrder.WASH_STATUS_WAIT_ACCEPT:
                        currentOuterCode = order
                                .getOuterCode();
                        cancelOrder();
                        break;

                    // 收衣中，衣服转交
                    case WithOrderClothingCollectOrder.WASH_STATUS_ANGEL_LAUNDRYING:
                        deliver(order.getCollectCode());
                        break;

                    //拒绝接收
                    case WithOrderClothingCollectOrder.WASH_STATUS_TRANSFER:
                        currentCollectCode = order.getCollectCode();
                        refuseDialogInit();
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

    private void refuseDialogInit() {
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
                        refuseDeliver(ClientStateManager.getLoginToken(getActivity()),
                                currentCollectCode,
                                editReason.getText().toString());
                        refuseDialog.dismiss();
                    } else {
                        PublicUtil.showToast(getString(R.string.no_refuse_reason));
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


    private void cancelOrder() {

        new CommonAlertDialog.Builder(main)
                .setCancelable(true)
                .setMessage(main.getString(R.string.pending_order_get_or_not))
                .setPositiveButton(R.string.btn_cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                            }
                        })
                .setNegativeButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        showProgressDialog();
                        DeliveryApi.signOrderInfo(ClientStateManager.getLoginToken(main),
                                currentOuterCode, Constants.STATUS_CANCEL_ORDER, baseHandler);

                    }
                })
                .show();


    }

    private void acceptOrder(String token, String outerCode) {
        showProgressDialog();
        DeliveryApi.signOrderInfo(token, outerCode, Constants.STATUS_ACCEPTL_ORDER, acceptHandler);
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
    AsyncHttpResponseHandler acceptHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d(getDefaultTag(), "acceptHandler result = " + responseString);
            dismissProgressDialog();
            try {
                ResultBase result = JSON.parseObject(responseString,
                        ResultBase.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    PublicUtil.showToast(getResources().getString(R.string.pending_order_success));
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
        ClothingDeliverActivity.actionStart(WithOrderManageFragment.this, collectCode,
                REQUEST_CODE_DELIVER);
    }

    private void confirmDeliver(String collectCode) {
        ClothingDeliverConfirmActivity.actionStart(WithOrderManageFragment.this, collectCode,
                REQUEST_CODE_DELIVER_CONFIRM);
    }

    private void confirmDeliver(String collectCode, String scanCode) {
        ClothingDeliverConfirmActivity.actionStart(WithOrderManageFragment.this, collectCode,
                scanCode,
                REQUEST_CODE_DELIVER_CONFIRM);
    }


}
