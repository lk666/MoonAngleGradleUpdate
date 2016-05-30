/**
 * Description:
 * Copyright: Copyright (c) 2016
 * Company: BLUE MOON
 *
 * @author: wangshanhai
 * @version 3.1.0
 * @date: 2016/3/23
 * @todo: prepare receive goods detail
 */
package cn.com.bluemoon.delivery.inventory;


import android.app.Activity;
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
import android.widget.BaseAdapter;
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
import com.umeng.analytics.MobclickAgent;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.inventory.ProductPreReceiveVo;
import cn.com.bluemoon.delivery.app.api.model.inventory.ResultDetail;
import cn.com.bluemoon.delivery.app.api.model.inventory.ResultPreReceiveOrderBean;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.manager.ActivityManager;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.ui.DateTimePickDialogUtil;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.ui.DialogForSubmitOrder;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.ui.DialogForEditOrderCount;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.lib.utils.LibImageUtil;
import cn.com.bluemoon.lib.view.CommonAlertDialog;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

public class OrderReceiveDetailActivity extends Activity implements OnClickListener {
    private String TAG = "OrderReceiveDetailActivity";
    private ListView listView;
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
    private TextView txtNeed;
    private TextView txtActual;

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

    private Button btnSettleDeliver;
    private TextView txtShouldDeliverBox;
    private TextView txtRealDeliverBox;
    private TextView txtDiffNums;

    private OrderReceiveDetailActivity main;
    private String orderCode;
    private String type;
    private String storeCode;
    private int storehouseCode;

    private CommonProgressDialog progressDialog;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_receive_details);
        main = this;
        ActivityManager.getInstance().pushOneActivity(this);
        init();
        initCustomActionBar();
        initView();
    }

    private void init() {
        lists = new ArrayList<ProductPreReceiveVo>();
        orderCode = getIntent().getStringExtra("orderCode");
        type = getIntent().getStringExtra("type");
        piclist = new ArrayList<String>();
        failUpload = new ArrayList<String>();
        progressDialog = new CommonProgressDialog(main);
    }

    private void initView() {
        btnSettleDeliver = (Button) findViewById(R.id.btn_settle_deliver);
        btnSettleDeliver.setOnClickListener(this);
        txtShouldDeliverBox = (TextView) findViewById(R.id.txt_should_deliver_box);
        txtRealDeliverBox = (TextView) findViewById(R.id.txt_real_deliver_box);
        txtDiffNums = (TextView) findViewById(R.id.txt_diff_nums);
        listView = (ListView) findViewById(R.id.listview_product);
        txtNeed = (TextView) findViewById(R.id.txt_need);
        txtActual = (TextView) findViewById(R.id.txt_actual);
        initHeadView();
        initFoot();
        adapter = new OrderProductAdapter(this, lists);
        listView.setAdapter(adapter);
        getData();
    }

    private void getData() {
        if (orderCode == null || "".equals(orderCode)) {
            return;
        }
        String token = ClientStateManager.getLoginToken(main);
        if (progressDialog != null) {
            progressDialog.show();
        }
        DeliveryApi.getReceiveDetail(token, orderCode, deliverOrderDetailHandler);
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

    private void initCustomActionBar() {
        new CommonActionBar(getActionBar(), new IActionBarListener() {
            @Override
            public void setTitle(TextView v) {
                // TODO Auto-generated method stub
                v.setText(getResources().getString(R.string.text_recvive_detail));
            }

            @Override
            public void onBtnRight(View v) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onBtnLeft(View v) {
                // TODO Auto-generated method stub
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rel_deliver_address:
                Intent intent = new Intent(main, OrderSelectDeliveryAddrActivity.class);
                intent.putExtra("type", InventoryTabActivity.RECEIVE_MANAGEMENT);
                intent.putExtra("storeCode", storeCode);
                intent.putExtra("storehouseCode", storehouseCode);
                main.startActivityForResult(intent, 101);
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

    DialogForSubmitOrder.Dialogcallback callback = new DialogForSubmitOrder.Dialogcallback() {
        @Override
        public void dialogdo(String string) {
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
        int addressid = 0;
        if (addressId != 0) {
            addressid = addressId;
        } else {
            addressid = detailInfo.getOrderDetail().getReStoreAddrId();
            if (detailInfo.getOrderDetail().getReStoreAddrId() == 0 && detailInfo.getOrderDetail().isAllowedEditAddress()) {
                PublicUtil.showToast(main, getString(R.string.txt_order_receive_address_toast));
                return;
            }
        }

        for (int i = 0; i < lists.size(); i++) {
            lists.get(i).setReNum(lists.get(i).getDifferNum());
        }

        if (progressDialog != null) {
            progressDialog.show();
        }
        DeliveryApi.getSubmitReceiveDetail(token, orderCode, submitTime, addressid, lists, submitReceiveHandler);
    }


    AsyncHttpResponseHandler submitReceiveHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            // TODO Auto-generated method stub
            LogUtils.d(TAG, "uploadHeader result = " + responseString);
            if (progressDialog != null)
                progressDialog.dismiss();
            try {
                ResultDetail result = JSON.parseObject(responseString,
                        ResultDetail.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {

                    PublicUtil.showCustomToast(main, getString(R.string.txt_order_receive_success_tip) +
                            "\n" + result.getReceiptCode(), Gravity.CENTER_VERTICAL);
                    handler.obtainMessage(1007).sendToTarget();
                } else {
                    PublicUtil.showCustomToast(OrderReceiveDetailActivity.this, result.getResponseMsg(), Gravity.CENTER_VERTICAL);
                }
            } catch (Exception e) {
                LogUtils.e(TAG, e.getMessage());
                PublicUtil.showToastServerBusy();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            // TODO Auto-generated method stub
            if (progressDialog != null)
                progressDialog.dismiss();
            LogUtils.e(TAG, throwable.getMessage());
            PublicUtil.showToastServerOvertime();

        }
    };


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
        DeliveryApi.uploadTicketPic(token, orderCode, "receipt", LibImageUtil.scaleBitmap(bm, 800), uploadHandler);
    }

    AsyncHttpResponseHandler uploadHandler = new TextHttpResponseHandler(HTTP.UTF_8) {
        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            // TODO Auto-generated method stub
            LogUtils.d(TAG, "uploadHeader result = " + responseString);
            try {
                ResultBase result = JSON.parseObject(responseString,
                        ResultBase.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    //  Toast.makeText(OrderReceiveDetailActivity.this, "第" + uploadTaskNums + "张上传成功", Toast.LENGTH_LONG).show();
                } else {
                    PublicUtil.showToast(main, result.getResponseMsg());
                    if (uploadTaskNums >= 1) {
                        failUpload.add(piclist.get(uploadTaskNums - 1).toString());
                    }
                }
            } catch (Exception e) {
                LogUtils.e(TAG, e.getMessage());
                PublicUtil.showToastServerBusy();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            // TODO Auto-generated method stub
            LogUtils.e(TAG, throwable.getMessage());
            PublicUtil.showToastServerOvertime();
            if (uploadTaskNums >= 1) {
                failUpload.add(piclist.get(uploadTaskNums - 1).toString());
            }
        }
    };

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
                    dictId = data.getStringExtra("dictId");
                    dictName = data.getStringExtra("dictName");
                    diffReasonDetail = data.getStringExtra("diffReasonDetail");
                    int pos = data.getIntExtra("pos", 0);
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
            // TODO Auto-generated method stub
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


    AsyncHttpResponseHandler deliverOrderDetailHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            LogUtils.d(TAG, "deliverOrderDetailHandler result = " + responseString);
            if (progressDialog != null)
                progressDialog.dismiss();
            try {
                ResultPreReceiveOrderBean resultPreReceiveOrderBean = JSON.parseObject(responseString,
                        ResultPreReceiveOrderBean.class);
                if (resultPreReceiveOrderBean.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    detailInfo = resultPreReceiveOrderBean;
                    setData(resultPreReceiveOrderBean);
                }
            } catch (Exception e) {
                LogUtils.e(TAG, e.getMessage());
                PublicUtil.showToastServerBusy();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            LogUtils.e(TAG, throwable.getMessage());
            if (progressDialog != null)
                progressDialog.dismiss();
            PublicUtil.showToastServerOvertime();
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

            if("90000714".equals(lis.get(i).getProductNo())){
                lis.get(i).setDiffCase(lis.get(i).getOutCase()/500);
            }else{
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
        txtOrderDeliverStoreNums.setText((String.format(getString(R.string.order_boxes_count),
                StringUtil.formatBoxesNum(result.getOrderDetail().getTotalCase())) +
                String.format(getString(R.string.order_product_count), result.getOrderDetail().getTotalNum())));
        txtTotalMoney.setText(getString(R.string.order_money_sign) + StringUtil.formatPriceByFen(result.getOrderDetail().getTotalMoney()));

        txtSource.setText(DateUtil.getTime(result.getOrderDetail().getOutDate(), "yyyy-MM-dd"));

        StringBuilder sb = new StringBuilder("");
        if (!StringUtil.isEmptyString(result.getOrderDetail().getReStoreCode())) {
            sb.append(result.getOrderDetail().getReStoreCode());
        }
        if (!StringUtil.isEmptyString(result.getOrderDetail().getReStoreType())) {
            sb.append("-").append(result.getOrderDetail().getReStoreType());
        }
        if(!StringUtil.isEmptyString(result.getOrderDetail().getReStoreChargeName())){
            sb.append("-").append(result.getOrderDetail().getReStoreChargeName());
        }
        txtFhStore.setText(sb.toString());
      /*  txt_fh_stor.setText(result.getOrderDetail().getReStoreCode() + "-"
                + result.getOrderDetail().getReStoreType() + "-" + result.getOrderDetail().getReStoreChargeName());*/
        txtFhAddress.setText(result.getOrderDetail().getReStoreAddrName());
        txtFhPhone.setText(result.getOrderDetail().getDeliveryTel());
        txtFhPhone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        txtFhPhone.getPaint().setAntiAlias(true);

        txtFhName.setText(result.getOrderDetail().getDeliveryName());
        txtDistributionBuniessName.setText(result.getOrderDetail().getCompanyName());
        if ("".equals(result.getOrderDetail().getCompanyName()) || result.getOrderDetail().getCompanyName() == null) {
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

        txtShouldDeliverBox.setText((String.format(getString(R.string.order_boxes_count), StringUtil.formatBoxesNum(xianshu))
                + String.format(getString(R.string.order_product_count), sumCount)));
        txtRealDeliverBox.setText((String.format(getString(R.string.order_boxes_count), StringUtil.formatBoxesNum(xianshu))
                + String.format(getString(R.string.order_product_count), sumCount)));
        txtDiffNums.setText(String.format(getString(R.string.txt_order_product_count), 0));
    }

    public static void actionStart(Fragment context, String type, String orderCode) {
        Intent intent = new Intent(context.getActivity(), OrderReceiveDetailActivity.class);
        intent.putExtra("orderCode", orderCode);
        intent.putExtra("type", type);
        context.startActivityForResult(intent, 0);
    }

    class OrderProductAdapter extends BaseAdapter {
        LayoutInflater mInflater;
        private List<ProductPreReceiveVo> list;
        Context context;

        public OrderProductAdapter(Context context, List<ProductPreReceiveVo> data) {
            this.context = context;
            this.mInflater = LayoutInflater.from(context);
            this.list = data;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub、
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.order_details_receive_item, null);
                holder.txt_order_number = (TextView) convertView.findViewById(R.id.txt_order_number);
                holder.txt_box_rule_num = (TextView) convertView.findViewById(R.id.txt_box_rule_num);
                holder.txt_order_product_name = (TextView) convertView.findViewById(R.id.txt_order_product_name);
                holder.txt_delivery_nums = (TextView) convertView.findViewById(R.id.txt_delivery_nums);
                holder.txt_delivery_box_nums = (TextView) convertView.findViewById(R.id.txt_delivery_box_nums);
                holder.book_count = (EditText) convertView.findViewById(R.id.book_count);
                holder.txt_real_box_nums = (TextView) convertView.findViewById(R.id.txt_real_box_nums);
                holder.book_reduce = (ImageView) convertView.findViewById(R.id.book_reduce);
                holder.book_add = (ImageView) convertView.findViewById(R.id.book_add);
                holder.line_dotted_bottom = convertView.findViewById(R.id.line_dotted_bottom);
                holder.txt_diff_nums_product_receive = (TextView) convertView.findViewById(R.id.txt_diff_nums_product_receive);
                holder.rel_diff_layout = (RelativeLayout) convertView.findViewById(R.id.rel_diff_layout);
                holder.txt_diff_reason = (TextView) convertView.findViewById(R.id.txt_diff_reason);
                holder.line_solid_deep_bottom = convertView.findViewById(R.id.line_solid_deep_bottom);
                holder.rel_diff_layout.setVisibility(View.VISIBLE);
                holder.line_dotted_bottom.setVisibility(View.GONE);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final ProductPreReceiveVo info = list.get(position);
            holder.txt_order_number.setText(list.get(position).getProductNo());
            holder.txt_box_rule_num.setText(list.get(position).getCarton() + "");
            holder.txt_order_product_name.setText(list.get(position).getProductName());
            holder.txt_delivery_nums.setText(list.get(position).getOutNum() + "");
            //应发箱数
            if("90000714".equals(list.get(position).getProductNo())){
                holder.txt_delivery_box_nums.setText((String.format(getString(R.string.order_boxes),
                        StringUtil.formatBoxesNum(list.get(position).getOutCase() / 500))));
            }else{
                holder.txt_delivery_box_nums.setText((String.format(getString(R.string.order_boxes),
                        StringUtil.formatBoxesNum(list.get(position).getOutCase()))));
            }
          /*  holder.txt_delivery_box_nums.setText((String.format(getString(R.string.order_boxes),
                    StringUtil.formatBoxesNum(list.get(position).getOutCase()))));*/

            //实发箱数
       /*     if("90000714".equals(list.get(position).getProductNo())){
                holder.txt_real_box_nums.setText(String.format(getString(R.string.order_boxes),
                        StringUtil.formatBoxesNum(list.get(position).getDiffCase() / 500)));
            }else{
                holder.txt_real_box_nums.setText(String.format(getString(R.string.order_boxes),
                        StringUtil.formatBoxesNum(list.get(position).getDiffCase())));
            }*/

            holder.txt_real_box_nums.setText(String.format(getString(R.string.order_boxes),
                    StringUtil.formatBoxesNum(list.get(position).getDiffCase())));
            holder.book_count.setText(list.get(position).getDifferNum() + "");
            String diffNums = (list.get(position).getOutNum() - list.get(position).getDifferNum()) + "";
            holder.txt_diff_nums_product_receive.setText(diffNums);

            holder.book_add.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (list.get(position).getOutNum() == list.get(position).getDifferNum()) {
                        return;
                    }
                    list.get(position).setDifferNum(list.get(position).getDifferNum() + 1);
                  //  double num = (double) (list.get(position).getDifferNum()) / list.get(position).getCarton();
                    double num ;
                    if("90000714".equals(list.get(position).getProductNo())){
                        num = (double) (list.get(position).getDifferNum()) / (list.get(position).getCarton()*500);
                    }else{
                        num = (double) (list.get(position).getDifferNum()) / list.get(position).getCarton();
                    }
                    list.get(position).setDiffCase(num);
                    adapter.notifyDataSetChanged();
                    handler.obtainMessage(1006).sendToTarget();
                }
            });

            holder.book_reduce.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (list.get(position).getDifferNum() <= 0) {
                        return;
                    }
                    list.get(position).setDifferNum(list.get(position).getDifferNum() - 1);
               //     double num = (double) (list.get(position).getDifferNum()) / list.get(position).getCarton();
                    double num ;
                    if("90000714".equals(list.get(position).getProductNo())){
                        num = (double) (list.get(position).getDifferNum()) / (list.get(position).getCarton()*500);
                    }else{
                        num = (double) (list.get(position).getDifferNum()) / list.get(position).getCarton();
                    }

                    list.get(position).setDiffCase(num);
                    adapter.notifyDataSetChanged();
                    handler.obtainMessage(1006).sendToTarget();
                }
            });

            holder.book_count.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogForEditOrderCount myDialog = new DialogForEditOrderCount(main, position, list.get(position).getOutNum(), list.get(position).getDifferNum(), isDeliver());
                    myDialog.setDialogCallback(dialogcallback);
                    myDialog.show();
                }
            });

            if (info.getDifferNum() != info.getOutNum() && !isDeliver()) {
                holder.rel_diff_layout.setVisibility(View.VISIBLE);
                holder.line_dotted_bottom.setVisibility(View.GONE);
                if (!"".equals(info.getReDifferReason()) && info.getReDifferReason() != null) {
                    holder.txt_diff_reason.setText(info.getReDifferReasonName());
                    holder.txt_diff_reason.setTextColor(getResources().getColor(R.color.text_grep));
                } else {
                    holder.txt_diff_reason.setText(getResources().getString(R.string.text_diff_reason));
                    holder.txt_diff_reason.setTextColor(getResources().getColor(R.color.text_red));
                }
            } else {
                holder.rel_diff_layout.setVisibility(View.GONE);
                holder.line_dotted_bottom.setVisibility(View.VISIBLE);
            }

            if (position == list.size() - 1) {
                holder.line_dotted_bottom.setVisibility(View.GONE);
                holder.line_solid_deep_bottom.setVisibility(View.GONE);
            } else {
                holder.line_solid_deep_bottom.setVisibility(View.GONE);
            }

            holder.rel_diff_layout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(main, OrderDiffReasonActivity.class);
                    intent.putExtra("type", type);
                    intent.putExtra("pos", position);
                    if (!StringUtil.isEmpty(info.getReDifferReason())) {
                        intent.putExtra("dictId", info.getReDifferReason());
                        intent.putExtra("dictName", info.getReDifferReasonName());
                        intent.putExtra("dictBackUp", info.getReDifferBackup());
                    }
                    main.startActivityForResult(intent, 102);
                }
            });

            return convertView;
        }

        final class ViewHolder {
            TextView txt_order_number;
            TextView txt_box_rule_num;
            TextView txt_order_product_name;
            TextView txt_delivery_nums;
            TextView txt_delivery_box_nums;
            EditText book_count;
            TextView txt_real_box_nums;
            ImageView book_reduce;
            ImageView book_add;
            RelativeLayout rel_diff_layout;
            TextView txt_diff_nums_product_receive;
            View line_dotted_bottom;
            View line_solid_deep_bottom;
            TextView txt_diff_reason;
        }
    }

    DialogForEditOrderCount.Dialogcallback dialogcallback = new DialogForEditOrderCount.Dialogcallback() {
        @Override
        public void dialogdo(int postion, String nums, int numCount) {
            try {
                if (Integer.valueOf(nums) > numCount) {
                    PublicUtil.showToast(main, getString(R.string.txt_order_product_count_toast_tip));
                    return;
                }
                lists.get(postion).setDifferNum(Integer.valueOf(nums));
                double num;
                if("90000714".equals(lists.get(postion).getProductNo())){
                    num = (double) (Integer.valueOf(nums)) / (lists.get(postion).getCarton()*500);
                }else{
                    num = (double) (Integer.valueOf(nums)) / lists.get(postion).getCarton();
                }
              //  double num = (double) (Integer.valueOf(nums)) / lists.get(postion).getCarton();
                lists.get(postion).setDiffCase(num);

                adapter.notifyDataSetChanged();
                handler.obtainMessage(1006).sendToTarget();
            } catch (Exception e) {
                lists.get(postion).setDifferNum(lists.get(postion).getDifferNum());
            }
        }
    };


    private void uploadData() {
        progressDialog.setCanceledOnTouchOutside(false);
        if (progressDialog != null) {
            progressDialog.show();
        }
        cancelReadCacheTask();
        uploadTask = (UploadTask) new UploadTask(main).execute("");
    }

    private void cancelReadCacheTask() {
        if (uploadTask != null) {
            uploadTask.cancel(true);
            uploadTask = null;
        }
    }

    private class UploadTask extends AsyncTask<String, Void, String> {
        private UploadTask(Context context) {
        }

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

            if (progressDialog != null)
                progressDialog.dismiss();
            handler.obtainMessage(1005).sendToTarget();
        }
    }

}


