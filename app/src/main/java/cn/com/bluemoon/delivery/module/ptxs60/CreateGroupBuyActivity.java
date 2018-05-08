package cn.com.bluemoon.delivery.module.ptxs60;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.PTXS60Api;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.address.Area;
import cn.com.bluemoon.delivery.app.api.model.personalinfo.RequestOrderDetail;
import cn.com.bluemoon.delivery.app.api.model.ptxs60.ResultGetBaseInfo;
import cn.com.bluemoon.delivery.app.api.model.ptxs60.ResultGetRecommendInfo;
import cn.com.bluemoon.delivery.app.api.model.ptxs60.ResultGetUnitPriceByNum;
import cn.com.bluemoon.delivery.app.api.model.ptxs60.ResultRePay;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.base.WithContextTextHttpResponseHandler;
import cn.com.bluemoon.delivery.ui.NoScrollListView;
import cn.com.bluemoon.delivery.ui.dialog.AddressSelectDialog;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.lib_widget.module.form.BMAngleBtn3View;
import cn.com.bluemoon.lib_widget.module.form.BMFieldArrow1View;
import cn.com.bluemoon.lib_widget.module.form.BMFieldText1View;
import cn.com.bluemoon.lib_widget.module.form.BmCellTextView;
import cn.com.bluemoon.lib_widget.module.form.interf.BMFieldListener;

/**
 * 新建拼团销售
 */
public class CreateGroupBuyActivity extends BaseActivity implements View.OnFocusChangeListener,
        OnListItemClickListener,
        TextView.OnEditorActionListener, BMFieldArrow1View.FieldArrowListener,
        AddressSelectDialog.IAddressSelectDialog, BMFieldListener, View.OnLayoutChangeListener {

    private static final int REQUEST_CODE_GET_BASE_INFO = 0x777;
    private static final int REQUEST_CODE_GET_UNIT_PRICE_BY_NUM = 0x666;
    private static final int REQUEST_CODE_GET_RECOMMEND_INFO = 0x555;
    private static final int REQUEST_CODE_COMMIT_ORDER = 0x444;
    @Bind(R.id.field_mendian)
    BmCellTextView fieldMendian;
    @Bind(R.id.field_store)
    BmCellTextView fieldStore;
    @Bind(R.id.field_receiver_name)
    BMFieldText1View fieldReceiverName;
    @Bind(R.id.field_receiver_phone)
    EditText fieldReceiverPhone;
    @Bind(R.id.field_area)
    BMFieldArrow1View fieldArea;
    @Bind(R.id.field_address)
    BMFieldText1View fieldAddress;
    @Bind(R.id.field_recommend_code)
    EditText fieldRecommendCode;
    @Bind(R.id.field_recommend_name)
    BmCellTextView fieldRecommendName;
    @Bind(R.id.lv_order_detail)
    NoScrollListView lvOrderDetail;
    @Bind(R.id.field_unit_price)
    BmCellTextView fieldUnitPrice;
    @Bind(R.id.btn_submit)
    BMAngleBtn3View btnSubmit;
    @Bind(R.id.tv_price)
    TextView tvPrice;
    @Bind(R.id.tv_count)
    TextView tvCount;
    @Bind(R.id.main_click)
    View mainClick;

    public static void actStart(Context context) {
        Intent intent = new Intent(context, CreateGroupBuyActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_create_group_buy;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.title_create_group_buy);
    }

    private ItemAdapter adapter;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                mainClick.requestFocus();
                ViewUtil.hideKeyboard(v);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     */
    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY()
                    < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    @Override
    public void initView() {
        btnSubmit.setEnabled(false);
        fieldUnitPrice.setVisibility(View.GONE);

        adapter = new ItemAdapter(this, this);
        lvOrderDetail.setAdapter(adapter);

        fieldRecommendCode.setOnFocusChangeListener(this);
        fieldRecommendCode.setOnEditorActionListener(this);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

        fieldUnitPrice.setVisibility(View.GONE);

        fieldArea.setListener(this);

        // 空输入判断
        fieldReceiverName.setListener(this);
        fieldAddress.setListener(this);
        fieldReceiverPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                CreateGroupBuyActivity.this.afterTextChanged(fieldReceiverPhone, s.toString());
            }
        });

        mainClick.addOnLayoutChangeListener(this);
    }

    /**
     * 界面变化时，判断是否刷新单价和推荐人
     */
    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int
            oldTop, int oldRight, int oldBottom) {
        //        int keyHeight = ViewUtil.getStatusHeight(this) / 3;
        //        if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyHeight)) {
        //            // 弹起
        //            mainClick.requestFocus();
        //        } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyHeight)) {
        //            // 关闭
        handleRecommendCodeChange();
        refreshPrice();
        //        }
    }


    /**
     * 空输入判断
     */
    @Override
    public void afterTextChanged(View view, String text) {
        checkBtn();
    }

    /**
     * 提交数据结算
     */
    private void submit() {
        String phone = fieldReceiverPhone.getText().toString();
        if (!StringUtil.isPhone(phone)) {
            toast(getString(R.string.error_message_input_phone));
            return;
        }
        showWaitDialog();

        // 提交结算
        data.addressInfo.receiverAddress = fieldAddress.getContent();
        data.addressInfo.receiverName = fieldReceiverName.getContent();

        ArrayList<RequestOrderDetail> details = new ArrayList<>();
        for (ResultGetBaseInfo.OrderDetailBean bean : data.orderDetail) {
            details.add(new RequestOrderDetail(bean));
        }

        PTXS60Api.commitOrder(data.addressInfo, data.mendianCode,
                details, data.recommendCode, data.recommendName, data.storeCode, getToken(),
                (WithContextTextHttpResponseHandler) getNewHandler
                        (REQUEST_CODE_COMMIT_ORDER, ResultRePay.class));
    }

    @Override
    public void initData() {
        showWaitDialog();
        PTXS60Api.getBaseInfo(getToken(), (WithContextTextHttpResponseHandler) getNewHandler
                (REQUEST_CODE_GET_BASE_INFO, ResultGetBaseInfo.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        switch (requestCode) {
            // 查询数据
            case REQUEST_CODE_GET_BASE_INFO:
                setInitData((ResultGetBaseInfo) result);
                break;

            // 查询单价
            case REQUEST_CODE_GET_UNIT_PRICE_BY_NUM:
                setPriceInfo((ResultGetUnitPriceByNum) result);
                break;

            // 查询推荐人
            case REQUEST_CODE_GET_RECOMMEND_INFO:
                setRecommendInfo((ResultGetRecommendInfo) result);
                break;

            // 结算
            case REQUEST_CODE_COMMIT_ORDER:
                ResultRePay resultPay = (ResultRePay) result;
                PayActivity.actStart(this, resultPay.payInfo.paymentTransaction, totalMoney,
                        resultPay.payInfo.paymentList);
                finish();
                break;
        }
    }

    private ResultGetBaseInfo data;

    private void setInitData(ResultGetBaseInfo result) {
        data = result;

        String name = data.mendianCode + " " + data.mendianName;
        name = name.trim();
        fieldMendian.setContentText(TextUtils.isEmpty(name) ? getString(R.string.promote_none) : name);
        fieldStore.setContentText(TextUtils.isEmpty(data.storeName) ? getString(R.string
                .promote_none) : data.storeName);

        if (data.addressInfo != null) {
            fieldReceiverName.setContent(data.addressInfo.receiverName);
            fieldReceiverPhone.setText(data.addressInfo.contactPhone);
            fieldArea.setContent(data.addressInfo.provinceName + data.addressInfo.cityName + data
                    .addressInfo.countryName);
            fieldAddress.setContent(data.addressInfo.receiverAddress);
        }
        fieldRecommendCode.setText(data.recommendCode);
        fieldRecommendName.setContentText(data.recommendName);
        btnSubmit.setEnabled(false);
        adapter.setList(data.orderDetail);
        adapter.notifyDataSetChanged();

        tvPrice.setText(getResources().getString(R.string.order_money,
                StringUtil.getPriceFormat(0)));
        tvCount.setText(getResources().getString(R.string.total_count_zhi, 0));


        // 推荐人姓名以及价钱相关的在后续更新
        handleRecommendCodeChange();
        refreshPrice();
    }

    @Override
    public void onItemClick(Object item, View view, int position) {

    }

    class ItemAdapter extends BaseListAdapter<ResultGetBaseInfo.OrderDetailBean> {

        public ItemAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_order_detail_input;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean
                isNew) {

            ResultGetBaseInfo.OrderDetailBean item = (ResultGetBaseInfo.OrderDetailBean) getItem
                    (position);
            if (item == null) {
                return;
            }

            TextView tvTitle = getViewById(R.id.tv_title);
            tvTitle.setText(item.productDesc);

            EditText etCount = getViewById(R.id.et_count);

            if (item.curCount < 0) {
                etCount.setText("");
            } else {
                etCount.setText("" + item.curCount);
            }
            etCount.setTag(R.id.tag_obj, item);
            if (isNew) {
                etCount.setOnFocusChangeListener(CreateGroupBuyActivity.this);
                etCount.setOnEditorActionListener(CreateGroupBuyActivity.this);
            }
        }
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            if (fieldRecommendCode == v) {
                handleRecommendCodeChange();
                return;
            }

            Object obj = v.getTag(R.id.tag_obj);
            if (obj instanceof ResultGetBaseInfo.OrderDetailBean) {
                handleCountChange((EditText) v,
                        (ResultGetBaseInfo.OrderDetailBean) obj);
            }
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        // 点击键盘的完成
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            if (fieldRecommendCode == v) {
                handleRecommendCodeChange();
                return false;
            }

            Object obj = v.getTag(R.id.tag_obj);
            if (obj instanceof ResultGetBaseInfo.OrderDetailBean) {
                handleCountChange((EditText) v,
                        (ResultGetBaseInfo.OrderDetailBean) obj);
            }
        }

        // 返回false表示点击后，隐藏软键盘。返回true表示保留软键盘。
        return false;
    }


    /**
     * 处理推荐人编码修改
     */
    private void handleRecommendCodeChange() {
        // 判断是否相同
        String newCode = fieldRecommendCode.getText().toString();

        if (TextUtils.isEmpty(newCode)) {
            fieldRecommendName.setContentText("");
            return;
        }

        if (data == null || newCode.equals(data.recommendCode)) {
            return;
        }

        // 查数据
        showWaitDialog();
        PTXS60Api.getRecommendInfo(newCode, getToken(), (WithContextTextHttpResponseHandler)
                getNewHandler(REQUEST_CODE_GET_RECOMMEND_INFO, ResultGetRecommendInfo.class));
    }

    private void setRecommendInfo(ResultGetRecommendInfo result) {
        fieldRecommendCode.setText(result.recommendCode);
        fieldRecommendName.setContentText(result.recommendName);
        data.recommendCode = result.recommendCode;
        data.recommendName = result.recommendName;
        checkBtn();
    }

    /**
     * 处理拼团商品数量修改
     */
    private void handleCountChange(EditText curNumTxt, ResultGetBaseInfo.OrderDetailBean item) {
        int num = 0;
        String numStr = curNumTxt.getText().toString();
        try {
            num = Integer.parseInt(numStr);
        } catch (Exception e) {
            LogUtils.e("拼团商品数量输入错误" + curNumTxt);
            num = item.curCount;
        }
        num = num > -1 ? num : 0;

        // 乱填数字的
        if (!numStr.equals(num + "") && item.curCount == num) {
            curNumTxt.setText(num + "");
        }

        // 拼团商品数量变化
        if (item.curCount != num) {
            item.curCount = num;
            refreshPrice();
        }
    }

    private long lastNum = 0;

    /**
     * 查价钱
     */
    private void refreshPrice() {
        if (data != null && data.orderDetail != null && !data.orderDetail.isEmpty()) {
            int num = 0;
            for (ResultGetBaseInfo.OrderDetailBean bean : data.orderDetail) {
                num += bean.curCount;
            }
            if (num < 1) {
                fieldUnitPrice.setVisibility(View.GONE);
                checkBtn();
            } else {
                if (lastNum != num) {
                    showWaitDialog();
                    PTXS60Api.getUnitPriceByNum(num, getToken(),
                            (WithContextTextHttpResponseHandler)

                                    getNewHandler(REQUEST_CODE_GET_UNIT_PRICE_BY_NUM,
                                            ResultGetUnitPriceByNum
                                                    .class));
                }
            }

            lastNum = num;
        }
    }

    private long totalMoney;

    /**
     * 查价钱返回
     */
    private void setPriceInfo(ResultGetUnitPriceByNum result) {
        if (result.orderUnitPrice < 1) {
            fieldUnitPrice.setVisibility(View.GONE);
        } else {
            fieldUnitPrice.setVisibility(View.VISIBLE);
            fieldUnitPrice.setContentText(StringUtil.getPriceFormat(result.orderUnitPrice));
        }

        tvPrice.setText(getResources().getString(R.string.order_money,
                StringUtil.getPriceFormat(result.orderTotalMoney)));
        totalMoney = result.orderTotalMoney;
        tvCount.setText(getResources().getString(R.string.total_count_zhi, result.orderTotalNum));
        adapter.notifyDataSetChanged();
        checkBtn();
    }

    /**
     * 检查结算按钮
     */
    private void checkBtn() {
        if (TextUtils.isEmpty(fieldReceiverName.getContent()) ||
                TextUtils.isEmpty(fieldReceiverPhone.getText()) ||
                TextUtils.isEmpty(fieldAddress.getContent()) ||
                TextUtils.isEmpty(fieldArea.getContent()) ||
                TextUtils.isEmpty(fieldRecommendName.getContentText()) ||
                TextUtils.isEmpty(fieldRecommendCode.getText()) ||
                fieldUnitPrice.getVisibility() != View.VISIBLE) {
            btnSubmit.setEnabled(false);
        } else {
            btnSubmit.setEnabled(true);
        }
    }

    //////// 地址选择

    @Override
    public void onClickRight(View view) {

    }

    private String provinceId, cityId, countyId;
    private String provinceName, cityName, countyName;

    @Override
    public void onClickLayout(View view) {
        AddressSelectDialog dialog = AddressSelectDialog.newInstance(provinceId, cityId, countyId);
        dialog.setListener(this);
        dialog.show(getSupportFragmentManager(), "");
    }

    @Override
    public void onSelect(Area province, Area city, Area country) {
        StringBuffer strBuf = new StringBuffer();
        if (province != null) {
            provinceId = province.getDcode();
            provinceName = province.getDname();
            strBuf.append(provinceName + " ");
        }
        if (city != null) {
            cityId = city.getDcode();
            cityName = city.getDname();
            strBuf.append(cityName + " ");
        }
        if (country != null) {
            countyId = country.getDcode();
            countyName = country.getDname();
            strBuf.append(countyName);
        } else {
            countyId = null;
            countyName = null;
        }
        if (data != null) {
            if (data.addressInfo == null) {
                data.addressInfo = new ResultGetBaseInfo.AddressInfoBean();
            }

            data.addressInfo.countryName = countyName;
            data.addressInfo.countryCode = countyId;
            data.addressInfo.cityName = cityName;
            data.addressInfo.cityCode = cityId;
            data.addressInfo.provinceCode = provinceId;
            data.addressInfo.provinceName = provinceName;
        }
        fieldArea.setContent(strBuf.toString());
    }
}
