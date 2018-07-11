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
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.commons.lang3.StringUtils;
import cz.msebera.android.httpclient.Header;
import org.apache.http.protocol.HTTP;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.inventory.ProductPreDeliverVo;
import cn.com.bluemoon.delivery.app.api.model.inventory.ResultDetail;
import cn.com.bluemoon.delivery.app.api.model.inventory.ResultPreDeliverOrderBean;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.ui.DateTimePickDialogUtil;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.lib.utils.LibImageUtil;
import cn.com.bluemoon.lib.view.CommonAlertDialog;

public class OrderDeliverDetailActivity extends BaseActivity implements OnClickListener,
        OnListItemClickListener {
    @BindView(R.id.listview_product)
    ListView listviewProduct;
    @BindView(R.id.txt_need)
    TextView txtNeed;
    @BindView(R.id.txt_should_deliver_box)
    TextView txtShouldDeliverBox;
    @BindView(R.id.txt_actual)
    TextView txtActual;
    @BindView(R.id.txt_real_deliver_box)
    TextView txtRealDeliverBox;
    @BindView(R.id.txt_diff_nums)
    TextView txtDiffNums;
    @BindView(R.id.btn_settle_deliver)
    Button btnSettleDeliver;

    private OrderProductAdapter adapter;
    private List<ProductPreDeliverVo> lists;
    private EditText tdOutBack;

    private TextView txtCommonNameFhck;
    private TextView txtCommonNameFhAddress;
    private TextView txtCommonNameDeliverDate;
    private TextView txtCustomerNameFhBill;
    private TextView txtCommonNameDeliverShipper;
    private TextView txtCommonNameFhfAddress;

    private TextView txtOrderId;
    private TextView txtSource;
    private TextView txtOrderDeliverStoreNum;
    private TextView txtTotalMoney;
    private TextView txtFhStore;
    private TextView txtFhAddress;
    private TextView txtFhDate;
    private TextView txtFhBill;
    private TextView txtFhPhone;
    private TextView txtFhName;
    private TextView txtDistributionBusinessName;
    private TextView txtFhfAddress;
    private TextView txtListOrderDetailName;
    private RelativeLayout relDeliverAddress;
    private RelativeLayout relDistributionBusinessName;

    private OrderDeliverDetailActivity main;
    private String orderCode;
    private String storeCode;

    ResultPreDeliverOrderBean detailInfo;

    private ArrayList<String> picList;//to upload the ticket list
    private ArrayList<String> failUpload;// fail upload ticket list

    private int addressId;
    private UploadTask uploadTask;
    private int uploadTaskNum;

    private int sumCount = 0;//实收支数
    private double boxNum = 0;//箱数
    private int diffNum = 0;//差异数
    private long submitTime = 0;

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        orderCode = getIntent().getStringExtra("orderCode");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.order_receive_details;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.detail_order_deliver);
    }

    @Override
    public void initView() {
        main = this;
        lists = new ArrayList<>();
        picList = new ArrayList<>();
        failUpload = new ArrayList<>();
        btnSettleDeliver.setOnClickListener(this);
        initHeadView();
        initFoot();
        adapter = new OrderProductAdapter(this, this);
        adapter.setList(lists);
        listviewProduct.setAdapter(adapter);
    }

    @Override
    public void initData() {
        if (StringUtil.isEmpty(orderCode)) {
            return;
        }
        showWaitDialog();
        DeliveryApi.getDeliverDetail(ClientStateManager.getLoginToken(), orderCode,
                getNewHandler(0, ResultPreDeliverOrderBean.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        if (requestCode == 0) {
            detailInfo = (ResultPreDeliverOrderBean) result;
            setData(detailInfo);
        } else if (requestCode == 1) {
            toast(getString(R.string.txt_order_deliver_success_tip) +
                    "\n" + ((ResultDetail) result).getOutCode());
            setResult(RESULT_OK);
            finish();
        }
    }

    private void initHeadView() {
        View headView = LayoutInflater.from(this).inflate(R.layout.order_deliver_listview_head,
                null);
        txtCommonNameFhck = (TextView) headView.findViewById(R.id.txt_commenName_fhck);
        txtCommonNameFhAddress = (TextView) headView.findViewById(R.id.txt_commenName_fh_address);
        txtCommonNameDeliverDate = (TextView) headView.findViewById(R.id
                .txt_commonName_deliver_date);
        txtCustomerNameFhBill = (TextView) headView.findViewById(R.id.txt_customerName_fh_bill);
        txtCommonNameDeliverShipper = (TextView) headView.findViewById(R.id
                .txt_commonName_deliver_shipper);
        txtCommonNameFhfAddress = (TextView) headView.findViewById(R.id.txt_commenName_fhf_address);

        txtOrderId = (TextView) headView.findViewById(R.id.txt_orderid);
        txtSource = (TextView) headView.findViewById(R.id.txt_source);
        txtOrderDeliverStoreNum = (TextView) headView.findViewById(R.id
                .txt_order_deliver_store_nums);
        txtTotalMoney = (TextView) headView.findViewById(R.id.txt_total_money);
        txtFhStore = (TextView) headView.findViewById(R.id.txt_fh_store);
        txtFhAddress = (TextView) headView.findViewById(R.id.txt_fh_address);
        txtFhDate = (TextView) headView.findViewById(R.id.txt_fh_date);
        txtFhBill = (TextView) headView.findViewById(R.id.txt_fh_bill);
        txtFhPhone = (TextView) headView.findViewById(R.id.txt_fh_phone);
        txtFhPhone.setOnClickListener(this);
        txtFhName = (TextView) headView.findViewById(R.id.txt_fh_name);
        txtDistributionBusinessName = (TextView) headView.findViewById(R.id
                .txt_distribution_buniessName);
        relDistributionBusinessName = (RelativeLayout) headView.findViewById(R.id
                .rel_distribution_buniessName);
        txtFhfAddress = (TextView) headView.findViewById(R.id.txt_fhf_address);
        txtListOrderDetailName = (TextView) headView.findViewById(R.id.txt_list_order_detail_name);

        relDeliverAddress = (RelativeLayout) headView.findViewById(R.id.rel_deliver_address);
        RelativeLayout relDeliverDate = (RelativeLayout) headView.findViewById(R.id
                .rel_deliver_date);
        RelativeLayout relDeliverTicket = (RelativeLayout) headView.findViewById(R.id
                .rel_deliverTicket);
        RelativeLayout llDiffLayout = (RelativeLayout) headView.findViewById(R.id.ll_diff_layout);
        llDiffLayout.setVisibility(View.GONE);

        relDeliverAddress.setOnClickListener(this);
        relDeliverDate.setOnClickListener(this);
        relDeliverTicket.setOnClickListener(this);
        listviewProduct.addHeaderView(headView);
        initTipText();
    }

    private void initTipText() {
        txtListOrderDetailName.setText(getResources().getString(R.string.text_deliver_sent_detail));
        txtCommonNameFhck.setText(getResources().getString(R.string.text_deliver_store));
        txtCommonNameFhAddress.setText(getResources().getString(R.string.text_deliver_address));
        txtCommonNameDeliverDate.setText(getResources().getString(R.string.text_deliver_date));
        txtCustomerNameFhBill.setText(getResources().getString(R.string
                .text_deliver_upload_ticket));
        txtCommonNameDeliverShipper.setText(getResources().getString(R.string.text_recriver_store));
        txtCommonNameFhfAddress.setText(getResources().getString(R.string.text_recrive_address));
        txtFhDate.setText(getResources().getString(R.string.txt_order_input_deliver_date));
        txtFhDate.setTextColor(getResources().getColor(R.color.text_grep));
        Drawable drawable = main.getResources().getDrawable(R.mipmap.addresser);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        txtCommonNameDeliverShipper.setCompoundDrawables(drawable, null, null, null);
    }

    private void initFoot() {
        View footView = LayoutInflater.from(this).inflate(R.layout.order_deliver_list_bottom, null);
        LinearLayout outBackLayout = (LinearLayout) footView.findViewById(R.id.ll_outBack);
        tdOutBack = (EditText) footView.findViewById(R.id.td_outBack);

        txtNeed.setText(getString(R.string.detail_order_deliver_should));
        txtActual.setText(getString(R.string.detail_order_deliver_real));
        btnSettleDeliver.setText(getString(R.string.btn_detail_order_deliver));
        outBackLayout.setVisibility(View.VISIBLE);
        listviewProduct.addFooterView(footView);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rel_deliver_address:
                OrderSelectDeliveryAddrActivity.actionStart(main,
                        InventoryTabActivity.DELIVERY_MANAGEMENT, storeCode, 101);
                break;
            case R.id.rel_deliver_date:
                String initDateTime = "";//2016-4-2 10:00
                initDateTime = txtFhDate.getText().toString().trim();
                if (StringUtil.isEmpty(initDateTime) ||
                        getString(R.string.txt_order_input_deliver_date).equals(initDateTime)
                        || getString(R.string.txt_order_input_receive_date).equals(initDateTime)) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(new Date());
                    cal.add(Calendar.HOUR_OF_DAY, 1);
                    initDateTime = DateUtil.getTime(cal.getTimeInMillis(), "yyyy-MM-dd HH:mm");
                }
                DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
                        main, initDateTime, new DateTimePickDialogUtil.OnDetailClickLister() {
                    @Override
                    public void btnClickLister(long time, String datetime) {
                        submitTime = time;
                        if (time > ((new Date().getTime() / 1000) + 3600)) {
                            PublicUtil.showToast(main, getString(R.string
                                    .txt_select_date_tip_future));
                            txtFhDate.setTextColor(main.getResources().getColor(R.color.text_grep));
                            return;
                        }
                        if ((detailInfo.getOrderDetail().getOrderDate()) >= time) {
                            String timeTip = DateUtil.getTime(detailInfo.getOrderDetail()
                                    .getOrderDate(), "yyyy-MM-dd HH:mm");
                            timeTip = timeTip.substring(0, timeTip.indexOf(":")) + ":00";

                            String dateTip = String.format(getResources().getString(R.string
                                    .txt_select_deliver_date_tip_ago), timeTip);
                            PublicUtil.showToast(main, dateTip);
                            txtFhDate.setTextColor(main.getResources().getColor(R.color.text_grep));
                            return;
                        }

                        if ("time".equals(datetime)) {
                            txtFhDate.setText(DateUtil.getTime(time, "yyyy-MM-dd HH:mm"));
                            txtFhDate.setTextColor(getResources().getColor(R.color
                                    .text_black_light));
                        }
                    }
                });
                dateTimePicKDialog.dateTimePicKDialog();
                break;
            case R.id.rel_deliverTicket:
                OrderTicketUploadActivity.actionStart(main, InventoryTabActivity
                        .DELIVERY_MANAGEMENT,
                        storeCode, picList, 103);

                break;
            case R.id.txt_fh_phone:
                PublicUtil.showCallPhoneDialog2(this, txtFhPhone.getText().toString());
                break;

            case R.id.btn_settle_deliver:
                if (submitTime <= 0 || getString(R.string.txt_order_input_deliver_date).equals
                        (txtFhDate.getText().toString().trim()) ||
                        getString(R.string.txt_order_input_receive_date).equals(txtFhDate.getText
                                ().toString().trim())) {
                    PublicUtil.showToast(main, getString(R.string.txt_order_date_not_null_tip));
                    return;
                }

                long totalMoneySubmit = 0;
                boxNum = 0;
                diffNum = 0;
                sumCount = 0;
                int mSumCount = 0;
                for (int i = 0; i < lists.size(); i++) {
                    String productNo = lists.get(i).getProductNo();
                    String flag = productNo.substring(0, 1);

                    sumCount = sumCount + lists.get(i).getDifferNum();
                    boxNum = boxNum + lists.get(i).getDiffCase();
                    mSumCount = mSumCount + lists.get(i).getOutNum();
                    totalMoneySubmit = totalMoneySubmit + (long) lists.get(i).getDifferNum() *
                            (lists.get(i).getPriceBag());

                }

                String shouldDeliverCount = String.format(getResources().getString(R.string
                        .order_boxes_count), StringUtil.formatBoxesNum(detailInfo.getOrderDetail
                        ().getTotalCase())) +
                        String.format(getResources().getString(R.string.order_product_count),
                                detailInfo.getOrderDetail().getTotalNum());
                String realDeliverCount = String.format(getResources().getString(R.string
                        .order_boxes_count), StringUtil.formatBoxesNum(boxNum)) +
                        String.format(getResources().getString(R.string.order_product_count),
                                sumCount);
                String diffCount = String.format(getResources().getString(R.string
                        .order_diff_product_count), mSumCount - sumCount);
                String totalMoney = getResources().getString(R.string.order_money_sign) +
                        StringUtil.formatPriceByFen(totalMoneySubmit);

                DialogForSubmitOrder myDialog = new DialogForSubmitOrder(main,
                        "deliver", shouldDeliverCount, realDeliverCount, diffCount, totalMoney);
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
        String token = ClientStateManager.getLoginToken(main);
        if (detailInfo == null) {
            return;
        }
        String outBack = tdOutBack.getText().toString().trim();

        int addressId;
        if (this.addressId != 0) {
            addressId = this.addressId;
        } else {
            addressId = detailInfo.getOrderDetail().getDeliStoreAddrId();
            if (detailInfo.getOrderDetail().getDeliStoreAddrId() == 0 && detailInfo
                    .getOrderDetail().isAllowedEditAddress()) {
                PublicUtil.showToast(main, getString(R.string.txt_order_deliver_address_toast));
                return;
            }
        }

        for (int i = 0; i < lists.size(); i++) {
            lists.get(i).setOutNum(lists.get(i).getDifferNum());
        }

        showWaitDialog();
        DeliveryApi.getSubmitDeliverDetail(token, orderCode, submitTime, outBack, addressId,
                lists, getNewHandler(1, ResultDetail.class));
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
        DeliveryApi.uploadTicketPic(token, orderCode, "out", LibImageUtil.scaleBitmap(bm, 800),
                uploadHandler);
    }


    AsyncHttpResponseHandler uploadHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            LogUtils.d(getDefaultTag(), "uploadHeader result = " + responseString);
            try {
                ResultBase result = JSON.parseObject(responseString,
                        ResultBase.class);

                if (result.getResponseCode() != Constants.RESPONSE_RESULT_SUCCESS) {
                    PublicUtil.showToast(main, result.getResponseMsg());
                    if (uploadTaskNum >= 1) {
                        failUpload.add(picList.get(uploadTaskNum - 1).toString());
                    }
                }
            } catch (Exception e) {
                LogUtils.e(getDefaultTag(), e.getMessage());
                PublicUtil.showToastServerBusy();
                if (uploadTaskNum >= 1) {
                    failUpload.add(picList.get(uploadTaskNum - 1).toString());
                }
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            LogUtils.e(getDefaultTag(), throwable.getMessage());
            PublicUtil.showToastServerOvertime();
            if (uploadTaskNum >= 1) {
                failUpload.add(picList.get(uploadTaskNum - 1).toString());
            }
        }
    };

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
                case 103:
                    picList.clear();
                    failUpload.clear();
                    ArrayList<String> listPath = data.getBundleExtra("datalist")
                            .getStringArrayList("datalist");
                    picList.addAll(listPath);

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
                                .setMessage(String.format(getString(R.string
                                        .txt_ticket_upload_fail_count), failUpload.size()))
                                //.setMessage("有" + failUpload.size() + "张上传失败")
                                .setNegativeButton(getString(R.string.btn_cancel), new
                                        DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        submitDeliver();
                                    }
                                })
                                .setPositiveButton(getString(R.string
                                        .txt_ticket_upload_fail_reupload), new DialogInterface
                                        .OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        picList.clear();
                                        picList.addAll(failUpload);
                                        uploadData();
                                    }
                                }).show();
                    } else {
                        submitDeliver();
                    }
                    break;
                case 1006:

                    sumCount = 0;
                    boxNum = 0;
                    diffNum = 0;
                    for (int i = 0; i < lists.size(); i++) {
                        String productNo = lists.get(i).getProductNo();
                        String flag = productNo.substring(0, 1);

                        sumCount = sumCount + lists.get(i).getDifferNum();
                        boxNum = boxNum + lists.get(i).getDiffCase();
                        diffNum = diffNum + (lists.get(i).getOutNum() - lists.get(i).getDifferNum
                                ());

                    }

                    txtRealDeliverBox.setText((String.format("%s%s", String.format(getString(R
                            .string.order_boxes_count), StringUtil.formatBoxesNum(boxNum)), String
                            .format(getString(R.string.order_product_count), sumCount))));
                    txtDiffNums.setText(String.format(getString(R.string.txt_order_product_count)
                            , diffNum));
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }

    };

    private void setData(ResultPreDeliverOrderBean result) {
        if (result == null || result.getOrderDetail() == null) {
            PublicUtil.showToastErrorData(main);
            return;
        }
        storeCode = result.getOrderDetail().getDeliStoreCode();

        List<ProductPreDeliverVo> lis = result.getOrderDetail().getProductDetails();
        for (int i = 0; i < lis.size(); i++) {
            lis.get(i).setDifferNum(lis.get(i).getOutNum());
            // lis.get(i).setDiffCase(lis.get(i).getOutCase());
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

    private void setHeadViewData(ResultPreDeliverOrderBean result) {
        if (!result.getOrderDetail().isAllowedEditAddress()) {
            txtFhAddress.setCompoundDrawables(null, null, null, null);
            txtFhAddress.setCompoundDrawablePadding(0);
            relDeliverAddress.setEnabled(false);
        }
        txtOrderId.setText(result.getOrderDetail().getOrderCode());
        txtOrderDeliverStoreNum.setText((String.format("%s%s", String.format(getString(R.string
                .order_boxes_count),
                StringUtil.formatBoxesNum(result.getOrderDetail().getTotalCase())), String.format
                (getString(R.string.order_product_count), result.getOrderDetail()
                .getTotalNum()))));
        txtTotalMoney.setText(String.format("%s%s", getString(R.string.order_money_sign),
                StringUtil.formatPriceByFen
                (result.getOrderDetail().getTotalMoney())));
        txtSource.setText(DateUtil.getTime(result.getOrderDetail().getOrderDate(), "yyyy-MM-dd"));


        StringBuilder sb = new StringBuilder("");
        if (!StringUtil.isEmptyString(result.getOrderDetail().getDeliStoreCode())) {
            sb.append(result.getOrderDetail().getDeliStoreCode());
        }
        if (!StringUtil.isEmptyString(result.getOrderDetail().getDeliStoreType())) {
            sb.append("-").append(result.getOrderDetail().getDeliStoreType());
        }
        if (!StringUtil.isEmptyString(result.getOrderDetail().getDeliStoreChargeName())) {
            sb.append("-").append(result.getOrderDetail().getDeliStoreChargeName());
        }
        txtFhStore.setText(sb.toString());


        txtFhAddress.setText(result.getOrderDetail().getDeliStoreAddrName());
        txtFhPhone.setText(result.getOrderDetail().getReceiveTel());
        txtFhPhone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        txtFhPhone.getPaint().setAntiAlias(true);

        txtFhName.setText(result.getOrderDetail().getReceiveName());
        txtDistributionBusinessName.setText(result.getOrderDetail().getCompanyName());
        if ("".equals(result.getOrderDetail().getCompanyName()) || result.getOrderDetail()
                .getCompanyName() == null) {
            relDistributionBusinessName.setVisibility(View.GONE);
        }
        txtFhfAddress.setText(result.getOrderDetail().getReceiveAddr());

        if (result.getOrderDetail().isAllowedEditAddress()) {
            txtFhAddress.setSingleLine(true);
            txtFhAddress.setEllipsize(TextUtils.TruncateAt.END);
        } else {
            txtFhAddress.setSingleLine(false);
        }

        sumCount = 0;
        boxNum = 0;
        diffNum = 0;
        for (int i = 0; i < lists.size(); i++) {
            String productNo = lists.get(i).getProductNo();
            String flag = productNo.substring(0, 1);

            sumCount = sumCount + lists.get(i).getDifferNum();
            boxNum = boxNum + lists.get(i).getDiffCase();
            diffNum = diffNum + (lists.get(i).getOutNum() - lists.get(i).getDifferNum());
        }

        txtShouldDeliverBox.setText((String.format("%s%s", String.format(getString(R.string
                .order_boxes_count),
                StringUtil.formatBoxesNum(boxNum)), String.format(getString(R.string
                .order_product_count), sumCount))));
        txtRealDeliverBox.setText((String.format("%s%s", String.format(getString(R.string
                .order_boxes_count),
                StringUtil.formatBoxesNum(boxNum)), String.format(getString(R.string
                .order_product_count), sumCount))));
        txtDiffNums.setText(String.format(getString(R.string.txt_order_product_count), 0));

    }


    public static void actionStart(Fragment context, String orderCode) {
        Intent intent = new Intent(context.getActivity(), OrderDeliverDetailActivity.class);
        intent.putExtra("orderCode", orderCode);
        context.startActivityForResult(intent, 0);
    }

    @Override
    public void onItemClick(Object item, View view, int position) {
        ProductPreDeliverVo i = (ProductPreDeliverVo) item;
        switch (view.getId()) {
            case R.id.book_add:
                if (i.getOutNum() == i.getDifferNum()) {
                    return;
                }
                i.setDifferNum(i.getDifferNum() + 1);
                double num;
                if ("90000714".equals(i.getProductNo())) {
                    num = (double) (i.getDifferNum()) / (i.getCarton() * 500);
                } else {
                    num = (double) (i.getDifferNum()) / i.getCarton();
                }
                i.setDiffCase(num);
                adapter.notifyDataSetChanged();
                handler.obtainMessage(1006).sendToTarget();
                break;
            case R.id.book_reduce:
                if (i.getDifferNum() <= 0) {
                    return;
                }
                i.setDifferNum(i.getDifferNum() - 1);
                if ("90000714".equals(i.getProductNo())) {
                    num = (double) (i.getDifferNum()) / (i.getCarton() * 500);
                } else {
                    num = (double) (i.getDifferNum()) / i.getCarton();
                }

                i.setDiffCase(num);
                adapter.notifyDataSetChanged();
                handler.obtainMessage(1006).sendToTarget();
                break;
            case R.id.book_count:
                DialogForEditOrderCount myDialog = new DialogForEditOrderCount(main, position, i
                        .getOutNum(), i.getDifferNum(), true);
                myDialog.setDialogCallback(dialogCallback);
                myDialog.show();
                break;
        }

    }

    class OrderProductAdapter extends BaseListAdapter<ProductPreDeliverVo> {


        public OrderProductAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.order_details_deliver_item;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            ProductPreDeliverVo item = list.get(position);
            if (item == null) return;
            TextView txtOrderNumber = getViewById(R.id.txt_order_number);
            TextView txtBoxRuleNum = getViewById(R.id.txt_box_rule_num);
            TextView txtOrderProductName = getViewById(R.id.txt_order_product_name);
            TextView txtDeliveryNum = getViewById(R.id.txt_delivery_nums);
            TextView txtDeliveryBoxNum = getViewById(R.id.txt_delivery_box_nums);
            EditText editBookCount = getViewById(R.id.book_count);
            TextView txtRealBoxNum = getViewById(R.id.txt_real_box_nums);
            ImageView imgBookReduce = getViewById(R.id.book_reduce);
            ImageView imgBookAdd = getViewById(R.id.book_add);
            View lineDottedBottom = getViewById(R.id.line_dotted_bottom);
            TextView txtDiffNumProductReceive = getViewById(R.id.txt_diff_nums_product_receive);
            RelativeLayout relDiffLayout = getViewById(R.id.rel_diff_layout);
            TextView txtDiffReason = getViewById(R.id.txt_diff_reason);
            View lineSolidDeepBottom = getViewById(R.id.line_solid_deep_bottom);
            lineDottedBottom.setVisibility(View.VISIBLE);
            relDiffLayout.setVisibility(View.GONE);

            txtOrderNumber.setText(item.getProductNo());
            txtBoxRuleNum.setText(String.valueOf(item.getCarton()));
            txtOrderProductName.setText(item.getProductName());
            txtDeliveryNum.setText(String.valueOf(item.getOutNum()));
            //应发箱数
            if ("90000714".equals(item.getProductNo())) {
                txtDeliveryBoxNum.setText((String.format(getString(R.string.order_boxes),
                        StringUtil.formatBoxesNum(item.getOutCase() / 500))));
            } else {
                txtDeliveryBoxNum.setText((String.format(getString(R.string.order_boxes),
                        StringUtil.formatBoxesNum(item.getOutCase()))));
            }

            txtRealBoxNum.setText(String.format(getString(R.string.order_boxes),
                    StringUtil.formatBoxesNum(item.getDiffCase())));
            editBookCount.setText(String.valueOf(item.getDifferNum()));

            String diffNum = String.valueOf((item.getOutNum() - item.getDifferNum()));
            txtDiffNumProductReceive.setText(diffNum);

            lineDottedBottom.setVisibility(View.VISIBLE);

            if (position == list.size() - 1) {
                lineDottedBottom.setVisibility(View.GONE);
                lineSolidDeepBottom.setVisibility(View.GONE);
            } else {
                lineSolidDeepBottom.setVisibility(View.GONE);
            }

            setClickEvent(isNew, position, imgBookAdd, imgBookReduce, editBookCount);
        }
    }


    DialogForEditOrderCount.DialogCallback dialogCallback = new DialogForEditOrderCount
            .DialogCallback() {
        @Override
        public void dialogDo(int position, String nums, int numCount) {
            try {
                if (Integer.valueOf(nums) > numCount) {
                    PublicUtil.showToast(main, getString(R.string
                            .txt_order_product_count_toast_tip));
                    return;
                }
                lists.get(position).setDifferNum(Integer.valueOf(nums));
                double num;
                if ("90000714".equals(lists.get(position).getProductNo())) {
                    num = (double) (Integer.valueOf(nums)) / (lists.get(position).getCarton() *
                            500);
                } else {
                    num = (double) (Integer.valueOf(nums)) / lists.get(position).getCarton();
                }
                lists.get(position).setDiffCase(num);

                adapter.notifyDataSetChanged();
                handler.obtainMessage(1006).sendToTarget();
            } catch (Exception e) {
                lists.get(position).setDifferNum(lists.get(position).getDifferNum());
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
            for (int i = 0; i < picList.size(); i++) {
                uploadTaskNum = i + 1;
                uploadPhoto(picList.get(i));
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


