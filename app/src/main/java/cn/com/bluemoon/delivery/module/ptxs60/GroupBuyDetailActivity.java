package cn.com.bluemoon.delivery.module.ptxs60;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.PTXS60Api;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.ptxs60.ResultGetOrderDetail;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.base.WithContextTextHttpResponseHandler;
import cn.com.bluemoon.delivery.ui.NoScrollListView;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.lib_widget.module.form.BmCellTextView;

/**
 * 详情
 * Created by lk on 2018/5/31.
 */

public class GroupBuyDetailActivity extends BaseActivity implements OnListItemClickListener {


    private static final String EXTRA_CODE = "EXTRA_CODE";
    private static final int REQUEST_CODE_GET_INFO = 0x777;
    @Bind(R.id.field_order_code)
    BmCellTextView fieldOrderCode;
    @Bind(R.id.field_mendian)
    BmCellTextView fieldMendian;
    @Bind(R.id.field_store)
    BmCellTextView fieldStore;
    @Bind(R.id.field_receiver_name)
    BmCellTextView fieldReceiverName;
    @Bind(R.id.field_receiver_phone)
    BmCellTextView fieldReceiverPhone;
    @Bind(R.id.field_address)
    BmCellTextView fieldAddress;
    @Bind(R.id.field_ptr_code)
    BmCellTextView fieldPtrCode;
    @Bind(R.id.field_ptr_name)
    BmCellTextView fieldPtrName;
    @Bind(R.id.field_recommend_code)
    BmCellTextView fieldRecommendCode;
    @Bind(R.id.field_recommend_name)
    BmCellTextView fieldRecommendName;
    @Bind(R.id.radio)
    BmCellTextView radio;
    @Bind(R.id.mdxx)
    BmCellTextView mdxx;
    @Bind(R.id.lv_order_detail)
    NoScrollListView lvOrderDetail;
    @Bind(R.id.unit)
    BmCellTextView unit;
    @Bind(R.id.count)
    BmCellTextView count;
    @Bind(R.id.price)
    BmCellTextView price;
    @Bind(R.id.sv_main)
    ScrollView svMain;

    public static void actStart(Context context, String orderCode) {
        Intent intent = new Intent(context, GroupBuyDetailActivity.class);
        intent.putExtra(EXTRA_CODE, orderCode);
        context.startActivity(intent);
    }

    private String orderCode;

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        if (getIntent() != null) {
            orderCode = getIntent().getStringExtra(EXTRA_CODE);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_group_buy_detail;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.tab_detail);
    }

    private ItemAdapter adapter;

    @Override
    public void initView() {
        adapter = new ItemAdapter(this, this);
        lvOrderDetail.setAdapter(adapter);
    }


    @Override
    public void initData() {
        showWaitDialog();
        PTXS60Api.getOrderDetail(orderCode, getToken(),
                (WithContextTextHttpResponseHandler) getNewHandler(REQUEST_CODE_GET_INFO,
                        ResultGetOrderDetail.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        switch (requestCode) {
            // 查询数据
            case REQUEST_CODE_GET_INFO:
                setInitData((ResultGetOrderDetail) result);
                break;
        }
    }

    private void setInitData(ResultGetOrderDetail data) {
        String name = data.mendianCode + " " + data.mendianName;
        name = name.trim();

        fieldOrderCode.setTitleText(data.orderCode);
        TextView tv = (TextView) fieldOrderCode.findViewById(cn.com.bluemoon.lib_widget.R.id.txt_content);
        switch (data.payStatus){
            case "cancel":
                tv.setTextColor(getResources().getColor(R.color.txt_999));
                fieldOrderCode.setContentText(getString(R.string.cancel));
                break;
            case "success":
                tv.setTextColor(getResources().getColor(R.color.green_0dd66f));
                fieldOrderCode.setContentText(getString(R.string.success));
                break;
            default:
                tv.setTextColor(getResources().getColor(R.color.orange_ff6c47));
                fieldOrderCode.setContentText(getString(R.string.wait));
                break;
        }

        fieldMendian.setContentText(TextUtils.isEmpty(name) ? getString(R.string.promote_none) :
                name);
        fieldStore.setContentText(TextUtils.isEmpty(data.storeName) ? getString(R.string
                .promote_none) : data.storeName);

        if (data.addressInfo != null) {
            fieldReceiverName.setContentText(data.addressInfo.receiverName);
            fieldReceiverPhone.setContentText(data.addressInfo.contactPhone);
            fieldAddress.setContentText(data.addressInfo.provinceName + data.addressInfo.cityName
                    + data .addressInfo.countryName + data.addressInfo.receiverAddress);
        }

        fieldRecommendCode.setContentText(TextUtils.isEmpty(data.recommendCode) ? getString(R.string
                        .promote_none) : data.recommendCode);
        fieldRecommendName.setContentText(TextUtils.isEmpty(data.recommendName) ? getString(R.string
                .promote_none) : data.recommendName);

        fieldPtrCode.setContentText(TextUtils.isEmpty(data.pinTuanCode) ? getString(R.string
                .promote_none) : data.pinTuanCode);
        fieldPtrName.setContentText(TextUtils.isEmpty(data.pinTuanName) ? getString(R.string
                .promote_none) : data.pinTuanName);

        radio.setContentText(data.isStoreResource ? getString(R.string.promote_has) :
                getString(R.string.promote_none));
        mdxx.setContentText((data.isStoreResource && !TextUtils.isEmpty(data.storeResourceInfo)) ?
                data.storeResourceInfo : getString(R.string.promote_none));
        unit.setContentText(StringUtil.getPriceFormat(data.ordeUnitPrice));
        count.setContentText("" + data.orderTotalNum);
        price.setContentText(StringUtil.getPriceFormat(data.orderTotalMoney));

        adapter.setList(data.orderDetail);
        adapter.notifyDataSetChanged();

        svMain.post(new Runnable() {
            @Override
            public void run() {
           svMain.scrollTo(0,0);
            }
        });
    }


    class ItemAdapter extends BaseListAdapter<ResultGetOrderDetail.OrderDetailBean> {

        public ItemAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_order_detail_read;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {

            ResultGetOrderDetail.OrderDetailBean item =
                    (ResultGetOrderDetail.OrderDetailBean) getItem(position);
            if (item == null) {
                return;
            }

            BmCellTextView tv = getViewById(R.id.tv);

            tv.setTitleText(item.productDesc);
            tv.setContentText(item.orderNum+"");
        }
    }

    @Override
    public void onItemClick(Object item, View view, int position) {

    }
}
