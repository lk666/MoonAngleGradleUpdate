package cn.com.bluemoon.delivery.module.wash.enterprise;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.apache.commons.lang3.StringUtils;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.EnterpriseApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.enterprise.Employee;
import cn.com.bluemoon.delivery.app.api.model.wash.enterprise.ResultEnterpriseDetail;
import cn.com.bluemoon.delivery.app.api.model.wash.enterprise.ResultEnterpriseDetail.EnterpriseOrderInfoBean;
import cn.com.bluemoon.delivery.app.api.model.wash.enterprise.ResultEnterpriseDetail.EnterpriseOrderInfoBean.ClothesDetailsBean;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;

/**
 * Created by liangjiangli on 2017/5/2.
 * 订单详情
 */

public class EnterpriseOrderDetailActivity extends BaseActivity {

    @Bind(R.id.txt_total_price2)
    TextView txtTotalPrice2;
    @Bind(R.id.txt_preferential)
    TextView txtPreferential;
    @Bind(R.id.txt_actual_price)
    TextView txtActualPrice;
    @Bind(R.id.layout_total_price2)
    LinearLayout layoutTotalPrice2;
    @Bind(R.id.txt_order_time)
    TextView txtOrderTime;
    @Bind(R.id.txt_cancel_time)
    TextView txtCancelTime;
    @Bind(R.id.layout_cancel_time)
    LinearLayout layoutCancelTime;
    @Bind(R.id.txt_pay_time)
    TextView txtPayTime;
    @Bind(R.id.layout_pay_time)
    LinearLayout layoutPayTime;
    private String type;
    @Bind(R.id.txt_order_code)
    TextView txtOrderCode;
    @Bind(R.id.txt_state)
    TextView txtState;
    @Bind(R.id.txt_name)
    TextView txtName;
    @Bind(R.id.txt_name_code)
    TextView txtNameCode;
    @Bind(R.id.txt_phone)
    TextView txtPhone;
    @Bind(R.id.txt_address)
    TextView txtAddress;
    @Bind(R.id.txt_collect_bag)
    TextView txtCollectBag;
    @Bind(R.id.txt_collect_remark)
    TextView txtCollectRemark;
    @Bind(R.id.txt_collect_num)
    TextView txtCollectNum;
    @Bind(R.id.lv_clothes)
    ListView lvClothes;
    @Bind(R.id.txt_total_price)
    TextView txtTotalPrice;
    @Bind(R.id.layout_total_price)
    LinearLayout layoutTotalPrice;
    private final String FORMAT_TIME = "yyyy/MM/dd HH:mm:ss";

    public static void startAct(Context context, String outerCode, String type) {
        Intent intent = new Intent(context, EnterpriseOrderDetailActivity.class);
        intent.putExtra("outerCode", outerCode);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_enterprise_order_detail;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.tab_detail);
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        String outerCode = getIntent().getStringExtra("outerCode");
        type = getIntent().getStringExtra("type");
        EnterpriseApi.getWashEnterpriseDetail(outerCode, getToken(), getNewHandler(1, ResultEnterpriseDetail.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        ResultEnterpriseDetail resultEnterpriseDetail = (ResultEnterpriseDetail) result;
        if (resultEnterpriseDetail != null) {
            EnterpriseOrderInfoBean enterpriseOrderInfo = resultEnterpriseDetail.enterpriseOrderInfo;
            if (enterpriseOrderInfo != null) {
                txtOrderCode.setText(enterpriseOrderInfo.outerCode);
                txtState.setText(enterpriseOrderInfo.stateName);
                txtCollectBag.setText(enterpriseOrderInfo.collectBrcode);
                if (StringUtils.isEmpty(enterpriseOrderInfo.remark)) {
                    txtCollectRemark.setText(getString(R.string.promote_none));
                } else {
                    txtCollectRemark.setText(enterpriseOrderInfo.remark);
                }
                if (enterpriseOrderInfo.clothesDetails != null) {
                    txtCollectNum.setText(getString(R.string.clothes_total_amount, enterpriseOrderInfo.clothesDetails.size()));
                    ItemAdapter adapter = new ItemAdapter(this);
                    adapter.setList(enterpriseOrderInfo.clothesDetails);
                    lvClothes.setAdapter(adapter);
                }
                txtOrderTime.setText(DateUtil.getTime(enterpriseOrderInfo.createTime, FORMAT_TIME));
                if (Constants.OUTER_WAIT_PAY.equals(type)) {
                    layoutTotalPrice.setVisibility(View.VISIBLE);
                    txtTotalPrice.setText(getString(R.string.order_money,
                            StringUtil.formatPriceByFen(enterpriseOrderInfo.sumAmount)));
                } else if (Constants.OUTER_TO_BE_WASHED.equals(type)) {
                    layoutTotalPrice2.setVisibility(View.VISIBLE);
                    txtTotalPrice2.setText(getString(R.string.order_money,
                            StringUtil.formatPriceByFen(enterpriseOrderInfo.sumAmount)));
                    txtPreferential.setText("- " + getString(R.string.order_money,
                            StringUtil.formatPriceByFen(enterpriseOrderInfo.integralAmount)));
                    txtActualPrice.setText(getString(R.string.order_money,
                            StringUtil.formatPriceByFen(enterpriseOrderInfo.payTotal)));

                } else if (Constants.OUTER_CANCEL.equals(type)) {
                    layoutCancelTime.setVisibility(View.VISIBLE);
                    txtCancelTime.setText(DateUtil.getTime(enterpriseOrderInfo.cancelTime, FORMAT_TIME));
                } else {
                    if (enterpriseOrderInfo.payTime > 0) {
                        layoutPayTime.setVisibility(View.VISIBLE);
                        txtPayTime.setText(DateUtil.getTime(enterpriseOrderInfo.payTime, FORMAT_TIME));
                    }

                }
            }
            final Employee employeeInfo = resultEnterpriseDetail.employeeInfo;
            if (employeeInfo != null) {
                txtName.setText(employeeInfo.employeeName);
                txtNameCode.setText(employeeInfo.employeeCode);
                txtPhone.setText(employeeInfo.employeePhone);
                txtAddress.setText(employeeInfo.branchName);
                txtPhone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                txtPhone.getPaint().setAntiAlias(true);
                txtPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PublicUtil.showCallPhoneDialog2(EnterpriseOrderDetailActivity.this, employeeInfo.employeePhone);
                    }
                });
            }
        }

    }

    class ItemAdapter extends BaseListAdapter<ClothesDetailsBean> {

        public ItemAdapter(Context context) {
            super(context, null);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_enterprise_clothes;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            ImageView img = getViewById(R.id.img_cloth);
            TextView clothName = getViewById(R.id.txt_cloth_name);
            TextView txtPrice = getViewById(R.id.txt_price);
            Glide.with(context).load(list.get(position).imgPath).error(R.mipmap.place_holder).into(img);
            clothName.setText(list.get(position).washName);
            if (Constants.OUTER_CANCEL.equals(type)) {
                txtPrice.setVisibility(View.GONE);
            } else {
                txtPrice.setText(getString(R.string.order_money, StringUtil.formatPriceByFen(list.get(position).memberPrice)));
            }
        }
    }
}
