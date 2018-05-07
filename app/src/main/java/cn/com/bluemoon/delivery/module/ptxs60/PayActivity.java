package cn.com.bluemoon.delivery.module.ptxs60;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.PTXS60Api;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.ptxs60.PaymentBean;
import cn.com.bluemoon.delivery.app.api.model.ptxs60.ResultPay;
import cn.com.bluemoon.delivery.app.api.model.ptxs60.ResultRePay;
import cn.com.bluemoon.delivery.entity.IPayOnlineResult;
import cn.com.bluemoon.delivery.entity.WXPayInfo;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.base.WithContextTextHttpResponseHandler;
import cn.com.bluemoon.delivery.utils.DialogUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.delivery.utils.service.PayService;
import cn.com.bluemoon.delivery.utils.service.UnionPayInfo;

/**
 * 拼团销售  支付界面
 * Created by tangqiwei on 2018/5/2.
 */

public class PayActivity extends BaseActivity implements OnListItemClickListener, IPayOnlineResult {

    private static final int PAY_WX_CONFIRM = 0x666;
    private static final int PAY_ALIPAY_CONFIRM = 0x555;
    private static final int PAY_UNIONPAY_CONFIRM = 0x333;
    private static final int PAY_REPAY = 0x111;

    @Bind(R.id.txt_money)
    TextView txtMoney;
    @Bind(R.id.lv_payment)
    ListView lvPayment;
    private long totalPay;
    private String transId;
    private String orderCode;
    private boolean isSubmit;

    private PayService payService;

    private PaymentAdapter adapter;
    private List<PaymentBean> payments;

    /**
     * @param context
     * @param orderCode 订单号
     */
    public static void actStart(Context context, String orderCode, long totalPay) {
        Intent intent = new Intent(context, PayActivity.class);
        intent.putExtra("totalPay", totalPay);
        intent.putExtra("orderCode", orderCode);
        context.startActivity(intent);
    }

    /**
     * @param context
     * @param transId     流水号
     * @param paymentList 支付方式
     */
    public static void actStart(Context context,  String transId, long totalPay,
                                List<ResultRePay.PayInfo.Payment> paymentList) {
        Intent intent = new Intent(context, PayActivity.class);
        intent.putExtra("transId", transId);
        intent.putExtra("totalPay", totalPay);
        intent.putExtra("payments", (Serializable) paymentList);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pay_group_booking;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.pay_title);
    }

    @Override
    public void onBackPressed() {
        DialogUtil.getCommonDialog(this, null, getString(R.string.pay_back_hint), getString(R
                .string.dialog_confirm), getString(R.string.dialog_cancel), new DialogInterface
                .OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EventBus.getDefault().post(new PayResult(false));
                PayActivity.super.onBackPressed();
            }
        }, null).show();
    }

    @Override
    public void initView() {
        orderCode = getIntent().getStringExtra("orderCode");
        transId = getIntent().getStringExtra("transId");
        totalPay = getIntent().getLongExtra("totalPay", 0);
        List<ResultRePay.PayInfo.Payment> temps;
        try {
            temps = (List<ResultRePay.PayInfo.Payment>) getIntent().getSerializableExtra
                    ("payments");
        } catch (Exception ex) {
            temps = null;
        }
        payService = new PayService(this, this);
        if (TextUtils.isEmpty(transId)) {
            showWaitDialog();
            PTXS60Api.rePay(orderCode, getToken(), (WithContextTextHttpResponseHandler)
                    getNewHandler(PAY_REPAY, ResultRePay.class));
        } else {
            setData(temps);
        }
    }

    private void setData(List<ResultRePay.PayInfo.Payment> temps) {

        payments = new ArrayList<>();
        if (null == temps || temps.size() < 1) {
            payments.add(new PaymentBean(R.mipmap.alipay, R.string.pay_alipay,
                    PAY_ALIPAY_CONFIRM, "alipay"));
            payments.add(new PaymentBean(R.mipmap.wxpay, R.string.pay_wxpay, PAY_WX_CONFIRM,
                    "wxpay"));
            payments.add(new PaymentBean(R.mipmap.payment_online, R.string.pay_unionpay,
                    PAY_UNIONPAY_CONFIRM, "unionpay"));
        } else {
            for (ResultRePay.PayInfo.Payment item : temps) {
                if (item.platform.equals("alipay")) {
                    payments.add(new PaymentBean(R.mipmap.alipay, R.string.pay_alipay,
                            PAY_ALIPAY_CONFIRM, "alipay"));
                } else if (item.platform.equals("wxpay")) {
                    payments.add(new PaymentBean(R.mipmap.wxpay, R.string.pay_wxpay,
                            PAY_WX_CONFIRM, "wxpay"));
                } else if (item.platform.equals("unionpay")) {
                    payments.add(new PaymentBean(R.mipmap.payment_online, R.string.pay_unionpay,
                            PAY_UNIONPAY_CONFIRM, "unionpay"));
                }
            }
        }
        adapter = new PaymentAdapter(this, this);
        adapter.setList(payments);
        lvPayment.setAdapter(adapter);

        String money = StringUtil.getPriceFormatAddPrefix(totalPay);
        SpannableString textSpan = new SpannableString(money);
        textSpan.setSpan(new AbsoluteSizeSpan(26, true), 1, money.indexOf("."), Spannable
                .SPAN_INCLUSIVE_INCLUSIVE);
        txtMoney.setText(textSpan);
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.btn_confirm_pay})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.btn_confirm_pay:
                actPay();
                break;
        }
    }

    //发起支付
    private void actPay() {
        if (!StringUtil.isEmpty(transId) && !isSubmit) {
            isSubmit = true;
            PaymentBean payment = null;
            for (PaymentBean bean : payments) {
                if (bean.isSelect) {
                    payment = bean;
                    break;
                }
            }
            final PaymentBean bean = payment;
            if (bean != null) {
                showWaitDialog();
                PTXS60Api.pay(transId, bean.type, getToken(),
                        (WithContextTextHttpResponseHandler) getNewHandler(bean
                                .requestCode, ResultPay.class));
            } else {
                isSubmit = false;
                longToast(R.string.pay_type_empty);
            }
        }
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        isSubmit = false;
        if (requestCode == PAY_ALIPAY_CONFIRM) {
            //支付宝
            ResultPay resultPrepayInfo = (ResultPay) result;
            if (resultPrepayInfo.getResponseCode() == 0) {
                payService.aliPay(resultPrepayInfo.payInfo);
            }

        } else if (requestCode == PAY_WX_CONFIRM) {
            //微信
            ResultPay resultPrepayInfo = (ResultPay) result;
            if (resultPrepayInfo.getResponseCode() == 0) {
                payService.wxPay(JSONObject.parseObject(resultPrepayInfo.payInfo, WXPayInfo
                        .class));
            }
        } else if (requestCode == PAY_UNIONPAY_CONFIRM) {
            //银联
            ResultPay resultUnionPayInfo = (ResultPay) result;
            if (resultUnionPayInfo.getResponseCode() == 0) {
                payService.unionPay(JSONObject.parseObject(resultUnionPayInfo.payInfo,
                        UnionPayInfo.class).tn);
            }
        } else if (requestCode == PAY_REPAY) {
            //支付查询流水号
            ResultRePay pay = (ResultRePay) result;
            transId = pay.payInfo.paymentTransaction;
            setData(pay.payInfo.paymentList);
        }
    }

    @Override
    public void onErrorResponse(int requestCode, final ResultBase result) {
        super.onErrorResponse(requestCode, result);
        isSubmit = false;
    }

    @Override
    public void onSuccessException(int requestCode, Throwable t) {
        super.onSuccessException(requestCode, t);
        isSubmit = false;
    }

    @Override
    public void onFailureResponse(int requestCode, Throwable t) {
        super.onFailureResponse(requestCode, t);
        isSubmit = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            String str = data.getExtras().getString("pay_result");
            EventBus.getDefault().post(new PayResult(str.equalsIgnoreCase("success")));
            toast(str.equalsIgnoreCase("success") ? R.string.group_booking_pay_success : R.string.group_booking_pay_failed);
            finish();
        }
    }
    @Override
    public void onItemClick(Object item, View view, int position) {
        int size = payments.size();
        for (int i = 0; i < size; i++) {
            payments.get(i).isSelect = (i == position);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void isSuccess(boolean isSuccess) {
        toast(isSuccess ? R.string.group_booking_pay_success : R.string.group_booking_pay_failed);
        EventBus.getDefault().post(new PayResult(isSuccess));
        finish();

    }

    class PaymentAdapter extends BaseListAdapter<PaymentBean> {
        public PaymentAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_payment;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            PaymentBean bean = list.get(position);
            ImageView imageIcon = getViewById(R.id.image_icon);
            TextView txtName = getViewById(R.id.txt_name);
            CheckBox cbPay = getViewById(R.id.cb_pay);
            cbPay.setChecked(bean.isSelect);
            imageIcon.setImageResource(bean.icon);
            txtName.setText(bean.name);
            setClickEvent(isNew, position, convertView, cbPay);
        }
    }
}
