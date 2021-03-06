package cn.com.bluemoon.delivery.module.ptxs60;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
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
import cn.com.bluemoon.delivery.ui.dialog.AddressSelectPopWindow;
import cn.com.bluemoon.delivery.utils.DialogUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.lib.view.CommonAlertDialog;
import cn.com.bluemoon.lib_widget.module.choice.BMRadioItemView;
import cn.com.bluemoon.lib_widget.module.choice.BMRadioView;
import cn.com.bluemoon.lib_widget.module.choice.entity.RadioItem;
import cn.com.bluemoon.lib_widget.module.form.BMAngleBtn3View;
import cn.com.bluemoon.lib_widget.module.form.BMFieldArrow1View;
import cn.com.bluemoon.lib_widget.module.form.BMFieldParagraphView;
import cn.com.bluemoon.lib_widget.module.form.BMFieldText1View;
import cn.com.bluemoon.lib_widget.module.form.BmCellTextView;
import cn.com.bluemoon.lib_widget.module.form.interf.BMFieldListener;

/**
 * 新建拼团销售
 */
public class CreateGroupBuyActivity extends BaseActivity implements OnListItemClickListener,
        BMFieldArrow1View.FieldArrowListener, BMFieldListener,
        View.OnLayoutChangeListener, AddressSelectPopWindow.IAddressSelectDialog, View
                .OnClickListener, BMRadioView.ClickListener {

    private static final int REQUEST_CODE_GET_BASE_INFO = 0x777;
    private static final int REQUEST_CODE_GET_UNIT_PRICE_BY_NUM = 0x666;
    private static final int REQUEST_CODE_GET_RECOMMEND_INFO = 0x555;
    private static final int REQUEST_CODE_COMMIT_ORDER = 0x444;
    private static final int REQUEST_CODE_GET_PTR_INFO = 0x333;
    @BindView(R.id.field_mendian)
    BmCellTextView fieldMendian;
    @BindView(R.id.field_store)
    BmCellTextView fieldStore;
    @BindView(R.id.field_receiver_name)
    BMFieldText1View fieldReceiverName;
    @BindView(R.id.field_receiver_phone)
    EditText fieldReceiverPhone;
    @BindView(R.id.field_area)
    BMFieldArrow1View fieldArea;
    @BindView(R.id.field_address)
    BMFieldText1View fieldAddress;
    @BindView(R.id.field_recommend_code)
    TextView fieldRecommendCode;
    @BindView(R.id.field_recommend_name)
    BmCellTextView fieldRecommendName;
    @BindView(R.id.lv_order_detail)
    NoScrollListView lvOrderDetail;
    @BindView(R.id.field_unit_price)
    BmCellTextView fieldUnitPrice;
    @BindView(R.id.btn_submit)
    BMAngleBtn3View btnSubmit;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.tv_count)
    TextView tvCount;
    @BindView(R.id.main_click)
    View mainClick;

    @BindView(R.id.mdxx)
    BMFieldParagraphView mdxx;
    @BindView(R.id.radio)
    BMRadioView radio;
    @BindView(R.id.field_ptr_code)
    TextView fieldPtrCode;
    @BindView(R.id.field_ptr_name)
    BmCellTextView fieldPtrName;

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

    private void setUnitePriceDisable() {
        fieldUnitPrice.setVisibility(View.GONE);
        tvPrice.setText(getResources().getString(R.string.order_money,
                StringUtil.getPriceFormat(0)));
        tvCount.setText(getResources().getString(R.string.total_count_zhi, 0));
        btnSubmit.setEnabled(false);
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

        fieldRecommendCode.setOnClickListener(this);
        fieldPtrCode.setOnClickListener(this);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

        fieldUnitPrice.setVisibility(View.GONE);

        fieldArea.setListener(this);


        radio.setListener(this);
        List<RadioItem> list = new ArrayList<>();
        list.add(new RadioItem(0, "否", BMRadioItemView.TYPE_NORMAL));
        list.add(new RadioItem(1, "是", BMRadioItemView.TYPE_NORMAL));
        radio.setData(list);
        mdxx.setVisibility(View.GONE);
        mdxx.setListener(this);

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
     * 有无门店资源
     */
    @Override
    public void onSelected(int position, Object value) {
        RadioItem i = (RadioItem) value;
        if (i != null) {
            // 否
            if ((Integer)(i.value) == 0) {
                mdxx.setVisibility(View.GONE);
            }
            // 是
            else {
                mdxx.setVisibility(View.VISIBLE);
            }
            checkBtn();
        }
    }

    /**
     * 界面变化时，判断是否刷新单价和推荐人
     */
    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int
            oldTop, int oldRight, int oldBottom) {
        int keyHeight = ViewUtil.getScreenHeight(this) / 4;
        if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyHeight)) {
            // 弹起
        } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyHeight)) {
            // 关闭
            mainClick.requestFocus();
        }
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
        if (TextUtils.isEmpty(data.addressInfo.cityCode) ||
                TextUtils.isEmpty(data.addressInfo.countryCode) ||
                TextUtils.isEmpty(data.addressInfo.provinceCode)) {
            toast(getString(R.string.err_area));
            return;
        }

        final String phone = fieldReceiverPhone.getText().toString();
        if (!StringUtil.isPhone(phone)) {
            toast(getString(R.string.error_message_input_phone));
            return;
        }

        DialogUtil.getCommonDialog(this, null, getString(R.string.hint_submit), getString(R
                .string.btn_cancel), getString(R.string.btn_ok), null, new DialogInterface
                .OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                showWaitDialog();

                // 提交结算
                data.addressInfo.receiverAddress = fieldAddress.getContent();
                data.addressInfo.receiverName = fieldReceiverName.getContent();
                data.addressInfo.contactPhone = phone;

                ArrayList<RequestOrderDetail> details = new ArrayList<>();
                for (ResultGetBaseInfo.OrderDetailBean bean : data.orderDetail) {
                    details.add(new RequestOrderDetail(bean));
                }
                String mdxxStr = mdxx.getVisibility() == View.VISIBLE ? mdxx.getContent() : "";

                PTXS60Api.commitOrder(data.addressInfo, data.mendianCode,
                        details, data.recommendCode, data.recommendName, data.storeCode, getToken(),
                        (WithContextTextHttpResponseHandler) getNewHandler
                                (REQUEST_CODE_COMMIT_ORDER, ResultRePay.class), mdxxStr,
                        data.pinTuanCode, data.pinTuanName);
            }
        }).show();
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
            // 查询拼团人
            case REQUEST_CODE_GET_PTR_INFO:
                setPtrInfo((ResultGetRecommendInfo) result);
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
        fieldMendian.setContentText(TextUtils.isEmpty(name) ? getString(R.string.promote_none) :
                name);
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


        fieldPtrCode.setText(data.pinTuanCode);
        fieldPtrName.setContentText(data.pinTuanName);

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

            TextView etCount = getViewById(R.id.et_count);
            etCount.setText(item.curCount);

            setClickEvent(isNew, position, etCount);

        }
    }

    /**
     * 数量修改
     */
    private CommonAlertDialog dialogCount;

    @Override
    public void onItemClick(final Object item, final View v, int position) {
        // 输入框点击
        if (item instanceof ResultGetBaseInfo.OrderDetailBean) {
            View view = LayoutInflater.from(this).inflate(R.layout.dialog_set_count, null);
            final EditText etPsw = (EditText) view.findViewById(R.id.et_psw);

            String s = ((ResultGetBaseInfo.OrderDetailBean) item).curCount;
            etPsw.setText(s);
            etPsw.setSelection(s.length());

            dialogCount = new CommonAlertDialog.Builder(this)
                    .setCancelable(false)
                    .setView(view)
                    .setDismissable(false)
                    .setNegativeButton(R.string.btn_ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String pwd = etPsw.getText().toString();
                                    if (v instanceof TextView) {
                                        ((TextView) v).setText(pwd);
                                        ((ResultGetBaseInfo.OrderDetailBean) item).curCount = pwd;
                                        refreshPrice();
                                    }
                                    ViewUtil.hideKeyboard(etPsw);
                                    dialogCount.dismiss();
                                }
                            })
                    .setPositiveButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ViewUtil.hideKeyboard(etPsw);
                            dialogCount.dismiss();
                        }
                    }).create();
            dialogCount.show();
            etPsw.post(new Runnable() {
                @Override
                public void run() {
                    ViewUtil.showKeyboard(etPsw);
                }
            });
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
                int orderNum = 0;
                try {
                    if (!TextUtils.isEmpty(bean.curCount)) {
                        orderNum = Integer.parseInt(bean.curCount);
                    }
                } catch (Exception e) {
                    orderNum = -1;
                }

                if (orderNum > -1) {
                    num += orderNum;
                } else {
                    toast("请输入正确的商品数量");
                    setUnitePriceDisable();
                    return;
                }
            }
            if (num < 1) {
                setUnitePriceDisable();
            } else {
                if (lastNum != num) {
                    lastNum = num;
                    showWaitDialog();
                    PTXS60Api.getUnitPriceByNum(num, getToken(),
                            (WithContextTextHttpResponseHandler)
                                    getNewHandler(REQUEST_CODE_GET_UNIT_PRICE_BY_NUM,
                                            ResultGetUnitPriceByNum.class));
                }
            }

        }
    }

    private long totalMoney;

    /**
     * 查价钱返回
     */
    private void setPriceInfo(ResultGetUnitPriceByNum result) {
        fieldUnitPrice.setVisibility(View.VISIBLE);
        fieldUnitPrice.setContentText(StringUtil.getPriceFormat(result.orderUnitPrice));

        tvPrice.setText(getResources().getString(R.string.order_money,
                StringUtil.getPriceFormat(result.orderTotalMoney)));
        totalMoney = result.orderTotalMoney;
        tvCount.setText(getResources().getString(R.string.total_count_zhi, result.orderTotalNum));
        checkBtn();
    }


    /**
     * 推荐人
     */
    private CommonAlertDialog dialogRecommend;
    /**
     * 拼团人
     */
    private CommonAlertDialog dialogPtr;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 点击推荐人
            case R.id.field_recommend_code:
                setRecommend();
                break;
            // 点击拼团人
            case R.id.field_ptr_code:
                setPtr();
                break;
        }
    }


    private void setPtr() {
        // 输入框点击
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_set_ptr, null);
        final EditText etPsw = (EditText) view.findViewById(R.id.et_psw);
        String s = fieldPtrCode.getText().toString();
        etPsw.setText(s);
        etPsw.setSelection(s.length());
        dialogPtr = new CommonAlertDialog.Builder(this)
                .setCancelable(false)
                .setView(view)
                .setDismissable(false)
                .setNegativeButton(R.string.btn_ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String pwd = etPsw.getText().toString();
                                fieldPtrCode.setText(pwd);
                                handlePtrCodeChange();
                                ViewUtil.hideKeyboard(etPsw);
                                dialogPtr.dismiss();
                            }
                        })
                .setPositiveButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ViewUtil.hideKeyboard(etPsw);
                        dialogPtr.dismiss();
                    }
                }).create();
        dialogPtr.show();
        etPsw.post(new Runnable() {
            @Override
            public void run() {
                ViewUtil.showKeyboard(etPsw);
            }
        });
    }


    /**
     * 处理拼团人编码修改
     */
    private void handlePtrCodeChange() {
        // 判断是否相同
        String newCode = fieldPtrCode.getText().toString();

        if (TextUtils.isEmpty(newCode)) {
            fieldPtrName.setContentText("");
            return;
        }

        if (data == null || newCode.equals(data.pinTuanCode)) {
            return;
        }

        // 查数据
        showWaitDialog();
        PTXS60Api.getRecommendInfo(newCode, getToken(), (WithContextTextHttpResponseHandler)
                getNewHandler(REQUEST_CODE_GET_PTR_INFO, ResultGetRecommendInfo.class));
    }

    private void setPtrInfo(ResultGetRecommendInfo result) {
        fieldPtrCode.setText(result.recommendCode);
        fieldPtrName.setContentText(result.recommendName);
        data.pinTuanCode = result.recommendCode;
        data.pinTuanName = result.recommendName;
        checkBtn();
    }

    private void setRecommend() {
        // 输入框点击
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_set_recommend, null);
        final EditText etPsw = (EditText) view.findViewById(R.id.et_psw);
        String s = fieldRecommendCode.getText().toString();
        etPsw.setText(s);
        etPsw.setSelection(s.length());
        dialogRecommend = new CommonAlertDialog.Builder(this)
                .setCancelable(false)
                .setView(view)
                .setDismissable(false)
                .setNegativeButton(R.string.btn_ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String pwd = etPsw.getText().toString();
                                fieldRecommendCode.setText(pwd);
                                handleRecommendCodeChange();
                                ViewUtil.hideKeyboard(etPsw);
                                dialogRecommend.dismiss();
                            }
                        })
                .setPositiveButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ViewUtil.hideKeyboard(etPsw);
                        dialogRecommend.dismiss();
                    }
                }).create();
        dialogRecommend.show();
        etPsw.post(new Runnable() {
            @Override
            public void run() {
                ViewUtil.showKeyboard(etPsw);
            }
        });
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


    @Override
    public void onSuccessException(int requestCode, Throwable t) {
        super.onSuccessException(requestCode, t);
        switch (requestCode) {
            // 查询单价
            case REQUEST_CODE_GET_UNIT_PRICE_BY_NUM:
                setUnitePriceDisable();
                break;
            // 查询推荐人
            case REQUEST_CODE_GET_RECOMMEND_INFO:
                fieldRecommendName.setContentText("");
                data.recommendCode = fieldRecommendCode.getText().toString();
                data.recommendName = "";
                break;

            // 查询拼团人
            case REQUEST_CODE_GET_PTR_INFO:
                fieldPtrName.setContentText("");
                data.pinTuanCode = fieldPtrCode.getText().toString();
                data.pinTuanName = "";
                break;
        }
    }

    @Override
    public void onErrorResponse(int requestCode, ResultBase result) {
        super.onErrorResponse(requestCode, result);
        switch (requestCode) {
            // 查询单价
            case REQUEST_CODE_GET_UNIT_PRICE_BY_NUM:
                setUnitePriceDisable();
                break;
            // 查询推荐人
            case REQUEST_CODE_GET_RECOMMEND_INFO:
                fieldRecommendName.setContentText("");
                data.recommendCode = fieldRecommendCode.getText().toString();
                data.recommendName = "";
                break;

            // 查询拼团人
            case REQUEST_CODE_GET_PTR_INFO:
                fieldPtrName.setContentText("");
                data.pinTuanCode = fieldPtrCode.getText().toString();
                data.pinTuanName = "";
                break;
        }
    }

    /**
     * 检查结算按钮
     */
    private void checkBtn() {
        if (TextUtils.isEmpty(fieldReceiverName.getContent()) ||
                TextUtils.isEmpty(fieldReceiverPhone.getText()) ||
                TextUtils.isEmpty(fieldAddress.getContent()) ||
                TextUtils.isEmpty(fieldArea.getContent()) ||
                data == null || data.addressInfo == null ||
                TextUtils.isEmpty(data.addressInfo.cityCode) ||
                TextUtils.isEmpty(data.addressInfo.countryCode) ||
                TextUtils.isEmpty(data.addressInfo.provinceCode) ||
                TextUtils.isEmpty(fieldRecommendName.getContentText()) ||
                TextUtils.isEmpty(fieldRecommendCode.getText()) ||
                TextUtils.isEmpty(fieldPtrCode.getText()) ||
                TextUtils.isEmpty(fieldPtrName.getContentText()) ||
                fieldUnitPrice.getVisibility() != View.VISIBLE ||
                (mdxx.getVisibility() == View.VISIBLE && TextUtils.isEmpty(mdxx.getContent()))) {
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
        AddressSelectPopWindow window = AddressSelectPopWindow.newInstance(this, provinceId,
                cityId, countyId);
        window.setListener(this);
        window.show(mainClick);
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
        checkBtn();
    }
}
