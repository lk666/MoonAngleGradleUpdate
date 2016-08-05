/**
 * Description:
 * Copyright: Copyright (c) 2016
 * Company: BLUE MOON
 *
 * @author: wangshanhai
 * @version 3.1.0
 * @date: 2016/3/23
 */
package cn.com.bluemoon.delivery.module.inventory;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.inventory.ProductPreReceiveVo;
import cn.com.bluemoon.delivery.app.api.model.inventory.ResultDetail;
import cn.com.bluemoon.delivery.app.api.model.inventory.ResultPreReceiveOrderBean;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.ui.DateTimePickDialogUtil;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.lib.utils.LibImageUtil;
import cn.com.bluemoon.lib.view.CommonAlertDialog;

public class OrderReceiveDetailActivity extends BaseActivity implements OnClickListener {
    @Bind(R.id.listview_product)
    ListView listView;
    @Bind(R.id.txt_need)
    TextView txtNeed;
    @Bind(R.id.txt_should_deliver_box)
    TextView txtShouldDeliverBox;
    @Bind(R.id.txt_actual)
    TextView txtActual;
    @Bind(R.id.txt_real_deliver_box)
    TextView txtRealDeliverBox;
    @Bind(R.id.txt_diff_nums)
    TextView txtDiffNums;
    @Bind(R.id.btn_settle_deliver)
    Button btnSettleDeliver;
    private OrderProductAdapter adapter;
    private List<ProductPreReceiveVo> lists;
    private View headView;
    private View footView;
    private LinearLayout llOutBack;
    private EditText tdOutBack;

    private TextView txtCommenNameFhck;
    private TextView txtCommenNameFhAddress;
    private TextView txtCommonNameDeliverDate;
    private TextView txtCustomerNameFhBill;
    private TextView txtCommonNameDeliverShipper;
    private TextView txtCommenNameFhfAddress;

    private TextView txtOrderid;
    private TextView txtSource;
    private TextView txtOrderDeliverStoreNums;
    private TextView txtTotalMoney;
    private TextView txtFhStore;
    private TextView txtFhAddress;
    private TextView txtFhDate;
    private TextView txtFhBill;
    private TextView txtFhPhone;
    private TextView txtFhName;
    private TextView txtDistributionBuniessName;
    private TextView txtFhfAddress;
    private TextView txtListOrderDetailName;
    private RelativeLayout relDeliverAddress, relDeliverDate, relDeliverTicket;
    private RelativeLayout relDistributionBuniessName;
    private RelativeLayout llDiffLayout;

    private OrderReceiveDetailActivity main;
    private String orderCode;
    private String type;
    private String storeCode;
    private int storehouseCode;

    ResultPreReceiveOrderBean detailInfo;

    private String dictName;
    private String dictId;
    private String diffReasonDetail;

    private ArrayList<String> piclist;//上传图片
    private ArrayList<String> failUpload;

    private int addressId;
    private UploadTask uploadTask;
    private int uploadTaskNums;
    private int sumCount = 0;//实收支数
    private double xianshu = 0;//箱数
    private int diffNums = 0;//差异数
    private long TotalMoney_submit;
    private long submitTime = 0;


    @Override
    protected int getLayoutId() {
        return R.layout.order_receive_details;
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        orderCode = getIntent().getStringExtra("orderCode");
        type = getIntent().getStringExtra("type");
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.text_recvive_detail);
    }

    @Override
    public void initView() {
        main = this;
        lists = new ArrayList<ProductPreReceiveVo>();
        piclist = new ArrayList<String>();
        failUpload = new ArrayList<String>();
        initHeadView();
        initFoot();
    }

    @Override
    public void initData() {

        adapter = new OrderProductAdapter(this, null);
        adapter.setList(lists);
        listView.setAdapter(adapter);
        if (TextUtils.isEmpty(orderCode)) {
            return;
        }
        DeliveryApi.getReceiveDetail(ClientStateManager.getLoginToken(),
                orderCode, getNewHandler(0, ResultPreReceiveOrderBean.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        switch (requestCode) {
            case 0:
                detailInfo = (ResultPreReceiveOrderBean) result;
                setData(detailInfo);
                break;
            case 1:
                PublicUtil.showCustomToast(main, getString(R.string.txt_order_receive_success_tip) +
                        "\n" + ((ResultDetail) result).getReceiptCode(), Gravity.CENTER_VERTICAL);
                handler.obtainMessage(1007).sendToTarget();
                break;
            case 2:
                break;
        }
    }

    @Override
    public void onErrorResponse(int requestCode, ResultBase result) {
        if (requestCode == 2) {
            PublicUtil.showToast(main, result.getResponseMsg());
            if (uploadTaskNums >= 1) {
                failUpload.add(piclist.get(uploadTaskNums - 1).toString());
            }
        } else {
            super.onErrorResponse(requestCode, result);
        }
    }

    @Override
    public void onSuccessException(int requestCode, Throwable t) {
        if (requestCode == 2) {
            PublicUtil.showToastServerBusy();
            if (uploadTaskNums >= 1) {
                failUpload.add(piclist.get(uploadTaskNums - 1).toString());
            }
        } else {
            super.onSuccessException(requestCode, t);
        }
    }

    @Override
    public void onFailureResponse(int requestCode, Throwable t) {
        if (requestCode == 2) {
            PublicUtil.showToastServerOvertime();
            if (uploadTaskNums >= 1) {
                failUpload.add(piclist.get(uploadTaskNums - 1).toString());
            }
        } else {
            super.onFailureResponse(requestCode, t);
        }
    }

    private void initHeadView() {
        headView = LayoutInflater.from(this).inflate(R.layout.order_deliver_listview_head, null);
        txtCommenNameFhck = (TextView) headView.findViewById(R.id.txt_commenName_fhck);
        txtCommenNameFhAddress = (TextView) headView.findViewById(R.id.txt_commenName_fh_address);
        txtCommonNameDeliverDate = (TextView) headView.findViewById(R.id.txt_commonName_deliver_date);
        txtCustomerNameFhBill = (TextView) headView.findViewById(R.id.txt_customerName_fh_bill);
        txtCommonNameDeliverShipper = (TextView) headView.findViewById(R.id.txt_commonName_deliver_shipper);
        txtCommenNameFhfAddress = (TextView) headView.findViewById(R.id.txt_commenName_fhf_address);

        txtOrderid = (TextView) headView.findViewById(R.id.txt_orderid);
        txtSource = (TextView) headView.findViewById(R.id.txt_source);
        txtOrderDeliverStoreNums = (TextView) headView.findViewById(R.id.txt_order_deliver_store_nums);
        txtTotalMoney = (TextView) headView.findViewById(R.id.txt_total_money);
        txtFhStore = (TextView) headView.findViewById(R.id.txt_fh_store);
        txtFhAddress = (TextView) headView.findViewById(R.id.txt_fh_address);
        txtFhDate = (TextView) headView.findViewById(R.id.txt_fh_date);
        txtFhBill = (TextView) headView.findViewById(R.id.txt_fh_bill);
        txtFhPhone = (TextView) headView.findViewById(R.id.txt_fh_phone);
        txtFhPhone.setOnClickListener(this);
        txtFhName = (TextView) headView.findViewById(R.id.txt_fh_name);
        txtDistributionBuniessName = (TextView) headView.findViewById(R.id.txt_distribution_buniessName);
        relDistributionBuniessName = (RelativeLayout) headView.findViewById(R.id.rel_distribution_buniessName);
        txtFhfAddress = (TextView) headView.findViewById(R.id.txt_fhf_address);
        txtListOrderDetailName = (TextView) headView.findViewById(R.id.txt_list_order_detail_name);

        relDeliverAddress = (RelativeLayout) headView.findViewById(R.id.rel_deliver_address);
        relDeliverDate = (RelativeLayout) headView.findViewById(R.id.rel_deliver_date);
        relDeliverTicket = (RelativeLayout) headView.findViewById(R.id.rel_deliverTicket);
        llDiffLayout = (RelativeLayout) headView.findViewById(R.id.ll_diff_layout);
        llDiffLayout.setVisibility(View.GONE);
        relDeliverAddress.setOnClickListener(this);
        relDeliverDate.setOnClickListener(this);
        relDeliverTicket.setOnClickListener(this);
        listView.addHeaderView(headView);
        initTipText();
    }

    private void initTipText() {
        txtListOrderDetailName.setText(getResources().getString(R.string.text_deliver_recvive_detail));
        txtCommenNameFhck.setText(getResources().getString(R.string.text_recrive_store));
        txtCommenNameFhAddress.setText(getResources().getString(R.string.text_recrive_address));
        txtCommonNameDeliverDate.setText(getResources().getString(R.string.text_recrive_date));
        txtCustomerNameFhBill.setText(getResources().getString(R.string.text_recrive_upload_ticket));
        txtCommonNameDeliverShipper.setText(getResources().getString(R.string.text_deliver_name));
        txtCommenNameFhfAddress.setText(getResources().getString(R.string.text_deliver_sent_address));
        txtFhDate.setText(getResources().getString(R.string.txt_order_input_receive_date));
        txtFhDate.setTextColor(getResources().getColor(R.color.text_grep));
        Drawable drawable = main.getResources().getDrawable(R.mipmap.addressee);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        txtCommonNameDeliverShipper.setCompoundDrawables(drawable, null, null, null);
    }

    private void initFoot() {
        footView = LayoutInflater.from(this).inflate(R.layout.order_deliver_list_bottom, null);
        llOutBack = (LinearLayout) footView.findViewById(R.id.ll_outBack);
        tdOutBack = (EditText) footView.findViewById(R.id.td_outBack);
        txtNeed.setText(getString(R.string.detail_order_receive_should));
        txtActual.setText(getString(R.string.detail_order_receive_real));
        btnSettleDeliver.setText(getString(R.string.btn_detail_order_receive));
        llOutBack.setVisibility(View.GONE);
        listView.addFooterView(footView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rel_deliver_address:
                OrderSelectDeliveryAddrActivity.actionStart(main,
                        InventoryTabActivity.RECEIVE_MANAGEMENT, storeCode, 101);
                break;
            case R.id.rel_deliver_date:
                String initDateTime = "";
                initDateTime = txtFhDate.getText().toString().trim();
                if (null == initDateTime || "".equals(initDateTime) ||
                        getString(R.string.txt_order_input_deliver_date).equals(initDateTime)
                        || getString(R.string.txt_order_input_receive_date).equals(initDateTime)) {

                    Date datetime = new Date();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(datetime);
                    cal.add(Calendar.HOUR_OF_DAY, 2);
                    datetime = cal.getTime();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    initDateTime = dateFormat.format(datetime);
                }
                DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
                        main, initDateTime, new DateTimePickDialogUtil.OnDetailClickLister() {
                    @Override
                    public void btnClickLister(long time, String datetime) {
                        submitTime = time;
                        if (time > ((new Date().getTime() / 1000) + 3600 * 2)) {
                            PublicUtil.showToast(main, getString(R.string.txt_select_date_tip_future));
                            txtFhDate.setTextColor(main.getResources().getColor(R.color.text_grep));
                            return;
                        }
                        if ((detailInfo.getOrderDetail().getOutDate()) >= time) {

                            String timeTip = DateUtil.getTime(detailInfo.getOrderDetail().getOutDate(), "yyyy-MM-dd HH:mm");
                            timeTip = timeTip.substring(0, timeTip.indexOf(":")) + ":00";

                            String dateTip = String.format(getResources().getString(R.string.txt_select_receive_date_tip_ago),
                                    timeTip);
                            PublicUtil.showToast(main, dateTip);
                            txtFhDate.setTextColor(main.getResources().getColor(R.color.text_grep));
                            return;
                        }

                        if ("time".equals(datetime)) {
                            txtFhDate.setText(DateUtil.getTime(time, "yyyy-MM-dd HH:mm"));
                            txtFhDate.setTextColor(getResources().getColor(R.color.text_black_light));
                        }
                    }
                });
                dateTimePicKDialog.dateTimePicKDialog();
                break;

            case R.id.rel_deliverTicket:
                Intent it = new Intent(main, OrderTicketUploadActivity.class);
                it.putExtra("type", InventoryTabActivity.RECEIVE_MANAGEMENT);
                it.putExtra("storeCode", storeCode);
                it.putExtra("storehouseCode", storehouseCode);
                if (piclist != null && piclist.size() > 0) {
                    Bundle b = new Bundle();
                    b.putStringArrayList("piclist", (ArrayList<String>) piclist);
                    it.putExtra("piclist", b);
                }
                main.startActivityForResult(it, 103);

                break;
            case R.id.txt_fh_phone:
                PublicUtil.showCallPhoneDialog2(this, txtFhPhone.getText().toString());
                break;
            case R.id.btn_settle_deliver:
                if (submitTime <= 0 || getString(R.string.txt_order_input_receive_date).equals(txtFhDate.getText().toString().trim())) {
                    PublicUtil.showToast(main, getString(R.string.txt_order_date_not_null_tip));
                    return;
                }

                boolean flag = false;
                for (int i = 0; i < lists.size(); i++) {
                    lists.get(i).setOutCase(lists.get(i).getDiffCase());
                    if ((lists.get(i).getOutNum() - lists.get(i).getDifferNum()) > 0) {
                        if (StringUtil.isEmpty(lists.get(i).getReDifferReason())) {
                            flag = true;
                        }
                    }
                }

                if (flag) {
                    PublicUtil.showToast(main, getString(R.string.txt_order_diff_reason_toast));
                    return;
                }

                TotalMoney_submit = 0;
                xianshu = 0;
                diffNums = 0;
                sumCount = 0;
                int mSumCount = 0;
                for (int i = 0; i < lists.size(); i++) {
                    String productNo = lists.get(i).getProductNo();
                    String isMetail = productNo.substring(0, 1);
                    //   if (!"9".equals(isMetail)) {
                    sumCount = sumCount + lists.get(i).getDifferNum();
                    xianshu = xianshu + lists.get(i).getDiffCase();
                    mSumCount = mSumCount + lists.get(i).getOutNum();
                    TotalMoney_submit = TotalMoney_submit + (long) lists.get(i).getDifferNum() * (lists.get(i).getPriceBag());
                    //   } else {
                    //       TotalMoney_submit = TotalMoney_submit + (long) lists.get(i).getDifferNum() * (lists.get(i).getPriceBag());
                    //   }
                }

                String shouldDeliverCount = String.format(getResources().getString(R.string.order_boxes_count), StringUtil.formatBoxesNum(detailInfo.getOrderDetail().getTotalCase())) +
                        String.format(getResources().getString(R.string.order_product_count), detailInfo.getOrderDetail().getTotalNum());
                String realDeliverCount = String.format(getResources().getString(R.string.order_boxes_count), StringUtil.formatBoxesNum(xianshu)) +
                        String.format(getResources().getString(R.string.order_product_count), sumCount);
                String diffCount = String.format(getResources().getString(R.string.order_diff_product_count), mSumCount - sumCount);
                String totalMoney = getResources().getString(R.string.order_money_sign) + StringUtil.formatPriceByFen(TotalMoney_submit);

                DialogForSubmitOrder myDialog = new DialogForSubmitOrder(main,
                        "receive", shouldDeliverCount, realDeliverCount, diffCount, totalMoney);
                myDialog.setDialogCallback(callback);
                myDialog.show();
                break;
        }
    }

    DialogForSubmitOrder.DialogCallback callback = new DialogForSubmitOrder.DialogCallback() {
        @Override
        public void dialogDo(String string) {
            uploadData();
        }
    };

    private void submitDeliver() {
        cancelReadCacheTask();
        if (orderCode == null || "".equals(orderCode)) {
            return;
        }
        if (detailInfo == null) {
            return;
        }
        int addressid = 0;
        if (addressId != 0) {
            addressid = addressId;
        } else {
            addressid = detailInfo.getOrderDetail().getReStoreAddrId();
            if (detailInfo.getOrderDetail().getReStoreAddrId() == 0 && detailInfo.getOrderDetail().isAllowedEditAddress()) {
                toast(R.string.txt_order_receive_address_toast);
                return;
            }
        }

        for (int i = 0; i < lists.size(); i++) {
            lists.get(i).setReNum(lists.get(i).getDifferNum());
        }

        showWaitDialog();
        DeliveryApi.getSubmitReceiveDetail(ClientStateManager.getLoginToken(),
                orderCode, submitTime, addressid, lists, getNewHandler(1, ResultDetail.class));
    }


    private void uploadPhoto(String path) {
        String token = ClientStateManager.getLoginToken(main);
        if (StringUtils.isEmpty(token)) {
            return;
        }
        String filePath = path;

        File newFile = new File(filePath);
        if (newFile == null || !newFile.exists()) {
            return;
        }
        Bitmap bm = ImageUtil.convertToBitmap(path);
        // PublicUtil.getBytes(bm);
        DeliveryApi.uploadTicketPic(token, orderCode, "receipt", LibImageUtil.scaleBitmap(bm, 800),
                getNewHandler(2, ResultBase.class));
    }

    private boolean isDeliver() {
        if (InventoryTabActivity.DELIVERY_MANAGEMENT.equals(type)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 101) {
            switch (requestCode) {
                case 101://选择地址返回
                    if (data == null) {
                        return;
                    }
                    String address = data.getStringExtra("address");
                    addressId = data.getIntExtra("addressId", 0);
                    txtFhAddress.setText(address);
                    break;
            }

        } else {
            switch (requestCode) {
                case 102:
                    if (data == null) {
                        return;
                    }
                    dictId = data.getStringExtra(OrderDiffReasonActivity.KEY_DICTID);
                    dictName = data.getStringExtra(OrderDiffReasonActivity.KEY_DICTNAME);
                    diffReasonDetail = data.getStringExtra(OrderDiffReasonActivity.KEY_DIFFREASONDETAIL);
                    int pos = data.getIntExtra(OrderDiffReasonActivity.KEY_POS, 0);
                    lists.get(pos).setReDifferReason(dictId);
                    lists.get(pos).setReDifferBackup(diffReasonDetail);
                    lists.get(pos).setReDifferReasonName(dictName);
                    adapter.notifyDataSetChanged();
                    break;
                case 103:
                    piclist.clear();
                    failUpload.clear();
                    ArrayList<String> listPath = data.getBundleExtra("datalist")
                            .getStringArrayList("datalist");
                    piclist.addAll(listPath);

                    if (listPath.size() > 0) {
                        txtFhBill.setTextColor(getResources().getColor(R.color.text_black_light));
                        txtFhBill.setText(getString(R.string.text_ticket_tip));
                    } else {
                        txtFhBill.setTextColor(getResources().getColor(R.color.text_grep));
                        txtFhBill.setText(getString(R.string.text_deliver_ticket_tip2));
                    }
                    handler.obtainMessage(1004).sendToTarget();
                    break;
            }
        }
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1005:
                    if (failUpload.size() > 0) {
                        new CommonAlertDialog.Builder(main)
                                .setMessage(String.format(getString(R.string.txt_ticket_upload_fail_count), failUpload.size()))
                                .setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        submitDeliver();
                                    }
                                })
                                .setPositiveButton(getString(R.string.txt_ticket_upload_fail_reupload), new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        piclist.clear();
                                        piclist.addAll(failUpload);
                                        uploadData();
                                    }
                                }).show();
                    } else {
                        submitDeliver();
                    }
                    break;
                case 1006:

                    sumCount = 0;
                    xianshu = 0;
                    diffNums = 0;
                    for (int i = 0; i < lists.size(); i++) {
                        String productNo = lists.get(i).getProductNo();
                        String flag = productNo.substring(0, 1);
                        //   if (!"9".equals(flag)) {
                        sumCount = sumCount + lists.get(i).getDifferNum();
                        xianshu = xianshu + lists.get(i).getDiffCase();
                        diffNums = diffNums + (lists.get(i).getOutNum() - lists.get(i).getDifferNum());
                        //   }
                    }
                    txtRealDeliverBox.setText((String.format(getString(R.string.order_boxes_count), StringUtil.formatBoxesNum(xianshu))
                            + String.format(getString(R.string.order_product_count), sumCount)));
                    txtDiffNums.setText(String.format(getString(R.string.txt_order_product_count), diffNums));
                    break;
                case 1007:
                    setResult(RESULT_OK);
                    finish();
                default:
                    break;
            }
            super.handleMessage(msg);
        }

    };


    private void setData(ResultPreReceiveOrderBean result) {
        if (result == null || result.getOrderDetail() == null) {
            PublicUtil.showToastErrorData(main);
            return;
        }
        storeCode = result.getOrderDetail().getReStoreCode();
        storehouseCode = result.getOrderDetail().getReStoreAddrId();
        List<ProductPreReceiveVo> lis = result.getOrderDetail().getProductDetails();
        for (int i = 0; i < lis.size(); i++) {
            lis.get(i).setDifferNum(lis.get(i).getOutNum());
            lis.get(i).setReNum(lis.get(i).getOutNum());
            //lis.get(i).setDiffCase(lis.get(i).getOutCase());

            if ("90000714".equals(lis.get(i).getProductNo())) {
                lis.get(i).setDiffCase(lis.get(i).getOutCase() / 500);
            } else {
                lis.get(i).setDiffCase(lis.get(i).getOutCase());
            }

        }
        lists.addAll(lis);
        setHeadViewData(result);
        adapter.notifyDataSetChanged();
    }

    private void setHeadViewData(ResultPreReceiveOrderBean result) {
        if (!result.getOrderDetail().isAllowedEditAddress()) {
            txtFhAddress.setCompoundDrawables(null, null, null, null);
            txtFhAddress.setCompoundDrawablePadding(0);
            relDeliverAddress.setEnabled(false);
        }
        txtOrderid.setText(result.getOrderDetail().getOrderCode());
        txtOrderDeliverStoreNums.setText((getString(R.string.order_boxes_count,
                StringUtil.formatBoxesNum(result.getOrderDetail().getTotalCase())) +
                getString(R.string.order_product_count, result.getOrderDetail().getTotalNum())));
        txtTotalMoney.setText(getString(R.string.order_money_sign) + StringUtil.formatPriceByFen(result.getOrderDetail().getTotalMoney()));

        txtSource.setText(DateUtil.getTime(result.getOrderDetail().getOutDate()));

        txtFhStore.setText(StringUtil.getStringParams(result.getOrderDetail().getReStoreCode(),
                result.getOrderDetail().getReStoreType(), result.getOrderDetail().getReStoreChargeName()));
        txtFhAddress.setText(result.getOrderDetail().getReStoreAddrName());
        txtFhPhone.setText(result.getOrderDetail().getDeliveryTel());
        txtFhPhone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        txtFhPhone.getPaint().setAntiAlias(true);

        txtFhName.setText(result.getOrderDetail().getDeliveryName());
        txtDistributionBuniessName.setText(result.getOrderDetail().getCompanyName());
        if (TextUtils.isEmpty(result.getOrderDetail().getCompanyName())) {
            relDistributionBuniessName.setVisibility(View.GONE);
        }
        txtFhfAddress.setText(result.getOrderDetail().getDeliveryAddr());

        if (result.getOrderDetail().isAllowedEditAddress()) {
            txtFhAddress.setSingleLine(true);
            txtFhAddress.setEllipsize(TextUtils.TruncateAt.END);
        } else {
            txtFhAddress.setSingleLine(false);
        }

        sumCount = 0;
        xianshu = 0;
        diffNums = 0;
        for (int i = 0; i < lists.size(); i++) {
    /*        String productNo = lists.get(i).getProductNo();
            String flag = productNo.substring(0, 1);*/
            //  if (!"9".equals(flag)) {
            sumCount = sumCount + lists.get(i).getDifferNum();
            xianshu = xianshu + lists.get(i).getDiffCase();
            diffNums = diffNums + (lists.get(i).getOutNum() - lists.get(i).getDifferNum());
            //   }


        }

        txtShouldDeliverBox.setText((getString(R.string.order_boxes_count, StringUtil.formatBoxesNum(xianshu))
                + getString(R.string.order_product_count, sumCount)));
        txtRealDeliverBox.setText((getString(R.string.order_boxes_count, StringUtil.formatBoxesNum(xianshu))
                + getString(R.string.order_product_count, sumCount)));
        txtDiffNums.setText(getString(R.string.txt_order_product_count, 0));
    }

    public static void actionStart(Fragment context, String type, String orderCode) {
        Intent intent = new Intent(context.getActivity(), OrderReceiveDetailActivity.class);
        intent.putExtra("orderCode", orderCode);
        intent.putExtra("type", type);
        context.startActivityForResult(intent, 0);
    }

    class OrderProductAdapter extends BaseListAdapter<ProductPreReceiveVo> {

        public OrderProductAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.order_details_receive_item;
        }

        @Override
        protected void setView(final int position, View convertView, ViewGroup parent, boolean isNew) {
            final ProductPreReceiveVo info = list.get(position);
            if (info == null) return;

            TextView txtOrderNumber = getViewById(R.id.txt_order_number);
            TextView txtBoxRuleNum = getViewById(R.id.txt_box_rule_num);
            TextView txtOrderProductName = getViewById(R.id.txt_order_product_name);
            TextView txtDeliveryNum = getViewById(R.id.txt_delivery_nums);
            TextView txtDeliveryBoxNum = getViewById(R.id.txt_delivery_box_nums);
            TextView editBookCount = getViewById(R.id.book_count);
            TextView txtRealBoxNum = getViewById(R.id.txt_real_box_nums);
            ImageView imgBookReduce = getViewById(R.id.book_reduce);
            ImageView imgBookAdd = getViewById(R.id.book_add);
            View lineDottedBottom = getViewById(R.id.line_dotted_bottom);
            TextView txtDiffNumProductReceive = getViewById(R.id.txt_diff_nums_product_receive);
            RelativeLayout relDiffLayout = getViewById(R.id.rel_diff_layout);
            TextView txtDiffReason = getViewById(R.id.txt_diff_reason);
            View lineSolidDeepBottom = getViewById(R.id.line_solid_deep_bottom);
            relDiffLayout.setVisibility(View.VISIBLE);
            lineDottedBottom.setVisibility(View.GONE);

            txtOrderNumber.setText(info.getProductNo());
            txtBoxRuleNum.setText(String.valueOf(info.getCarton()));
            txtOrderProductName.setText(info.getProductName());
            txtDeliveryNum.setText(String.valueOf(info.getOutNum()));
            //应发箱数
            if ("90000714".equals(info.getProductNo())) {
                txtDeliveryBoxNum.setText((String.format(getString(R.string.order_boxes),
                        StringUtil.formatBoxesNum(info.getOutCase() / 500))));
            } else {
                txtDeliveryBoxNum.setText((String.format(getString(R.string.order_boxes),
                        StringUtil.formatBoxesNum(info.getOutCase()))));
            }

            txtRealBoxNum.setText(String.format(getString(R.string.order_boxes),
                    StringUtil.formatBoxesNum(info.getDiffCase())));
            editBookCount.setText(String.valueOf(info.getDifferNum()));
            String diffNums = String.valueOf(info.getOutNum() - info.getDifferNum());
            txtDiffNumProductReceive.setText(diffNums);

            imgBookAdd.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (info.getOutNum() == info.getDifferNum()) {
                        return;
                    }
                    info.setDifferNum(info.getDifferNum() + 1);
                    double num;
                    if ("90000714".equals(info.getProductNo())) {
                        num = (double) (info.getDifferNum()) / (info.getCarton() * 500);
                    } else {
                        num = (double) (info.getDifferNum()) / info.getCarton();
                    }
                    info.setDiffCase(num);
                    notifyDataSetChanged();
                    handler.obtainMessage(1006).sendToTarget();
                }
            });

            imgBookReduce.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (info.getDifferNum() <= 0) {
                        return;
                    }
                    info.setDifferNum(info.getDifferNum() - 1);
                    double num;
                    if ("90000714".equals(info.getProductNo())) {
                        num = (double) (info.getDifferNum()) / (info.getCarton() * 500);
                    } else {
                        num = (double) (info.getDifferNum()) / info.getCarton();
                    }

                    info.setDiffCase(num);
                    notifyDataSetChanged();
                    handler.obtainMessage(1006).sendToTarget();
                }
            });

            editBookCount.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogForEditOrderCount myDialog = new DialogForEditOrderCount(main, position, info.getOutNum(), info.getDifferNum(), isDeliver());
                    myDialog.setDialogCallback(dialogCallback);
                    myDialog.show();
                }
            });

            if (info.getDifferNum() != info.getOutNum() && !isDeliver()) {
                relDiffLayout.setVisibility(View.VISIBLE);
                lineDottedBottom.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(info.getReDifferReason())) {
                    txtDiffReason.setText(info.getReDifferReasonName());
                    txtDiffReason.setTextColor(getResources().getColor(R.color.text_grep));
                } else {
                    txtDiffReason.setText(getResources().getString(R.string.text_diff_reason));
                    txtDiffReason.setTextColor(getResources().getColor(R.color.text_red));
                }
            } else {
                relDiffLayout.setVisibility(View.GONE);
                lineDottedBottom.setVisibility(View.VISIBLE);
            }

            if (position == list.size() - 1) {
                lineDottedBottom.setVisibility(View.GONE);
                lineSolidDeepBottom.setVisibility(View.GONE);
            } else {
                lineSolidDeepBottom.setVisibility(View.GONE);
            }

            relDiffLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    OrderDiffReasonActivity.actStart(main, 102, position, info.getReDifferReason(),
                            info.getReDifferReasonName(), info.getReDifferBackup());
                }
            });
        }
    }

    DialogForEditOrderCount.DialogCallback dialogCallback = new DialogForEditOrderCount.DialogCallback() {
        @Override
        public void dialogDo(int postion, String nums, int numCount) {
            try {
                if (Integer.valueOf(nums) > numCount) {
                    PublicUtil.showToast(main, getString(R.string.txt_order_product_count_toast_tip));
                    return;
                }
                lists.get(postion).setDifferNum(Integer.valueOf(nums));
                double num;
                if ("90000714".equals(lists.get(postion).getProductNo())) {
                    num = (double) (Integer.valueOf(nums)) / (lists.get(postion).getCarton() * 500);
                } else {
                    num = (double) (Integer.valueOf(nums)) / lists.get(postion).getCarton();
                }
                lists.get(postion).setDiffCase(num);

                adapter.notifyDataSetChanged();
                handler.obtainMessage(1006).sendToTarget();
            } catch (Exception e) {
                lists.get(postion).setDifferNum(lists.get(postion).getDifferNum());
            }
        }
    };


    private void uploadData() {
        showWaitDialog(false);
        cancelReadCacheTask();
        uploadTask = (UploadTask) new UploadTask().execute("");
    }

    private void cancelReadCacheTask() {
        if (uploadTask != null) {
            uploadTask.cancel(true);
            uploadTask = null;
        }
    }

    private class UploadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            for (int i = 0; i < piclist.size(); i++) {
                uploadTaskNums = i + 1;
                uploadPhoto(piclist.get(i).toString());
            }
            return "success";
        }

        @Override
        protected void onPostExecute(String info) {
            super.onPostExecute(info);

            hideWaitDialog();
            handler.obtainMessage(1005).sendToTarget();
        }
    }

}


