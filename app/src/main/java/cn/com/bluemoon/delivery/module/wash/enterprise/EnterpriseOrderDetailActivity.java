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

import butterknife.BindView;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.EnterpriseApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.enterprise.ClothesInfo;
import cn.com.bluemoon.delivery.app.api.model.wash.enterprise.Employee;
import cn.com.bluemoon.delivery.app.api.model.wash.enterprise.ResultEnterpriseDetail;
import cn.com.bluemoon.delivery.app.api.model.wash.enterprise.ResultEnterpriseDetail.EnterpriseOrderInfoBean;
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

    @BindView(R.id.txt_total_price2)
    TextView txtTotalPrice2;
    @BindView(R.id.txt_preferential)
    TextView txtPreferential;
    @BindView(R.id.layout_preferential)
    LinearLayout layoutPreferential;
    @BindView(R.id.txt_actual_price)
    TextView txtActualPrice;
    @BindView(R.id.layout_total_price2)
    LinearLayout layoutTotalPrice2;
    @BindView(R.id.txt_order_time)
    TextView txtOrderTime;
    @BindView(R.id.txt_cancel_time)
    TextView txtCancelTime;
    @BindView(R.id.layout_cancel_time)
    LinearLayout layoutCancelTime;
    @BindView(R.id.txt_pay_time)
    TextView txtPayTime;
    @BindView(R.id.layout_pay_time)
    LinearLayout layoutPayTime;
    private String type;
    @BindView(R.id.txt_order_code)
    TextView txtOrderCode;
    @BindView(R.id.txt_state)
    TextView txtState;
    @BindView(R.id.txt_name)
    TextView txtName;
    @BindView(R.id.txt_name_code)
    TextView txtNameCode;
    @BindView(R.id.txt_phone)
    TextView txtPhone;
    @BindView(R.id.txt_address)
    TextView txtAddress;
    @BindView(R.id.txt_collect_bag)
    TextView txtCollectBag;
    @BindView(R.id.txt_collect_remark)
    TextView txtCollectRemark;
    @BindView(R.id.txt_collect_num)
    TextView txtCollectNum;
    @BindView(R.id.lv_clothes)
    ListView lvClothes;
    @BindView(R.id.txt_total_price)
    TextView txtTotalPrice;
    @BindView(R.id.layout_total_price)
    LinearLayout layoutTotalPrice;
    @BindView(R.id.line_clothes)
    View lineClothes;
    @BindView(R.id.line_price)
    View linePrice;
    private final String FORMAT_TIME = "yyyy/MM/dd HH:mm:ss";
    private boolean isHistory;

    public static void startAct(Context context, String outerCode, String type, boolean isHistory) {
        Intent intent = new Intent(context, EnterpriseOrderDetailActivity.class);
        intent.putExtra("outerCode", outerCode);
        intent.putExtra("type", type);
        intent.putExtra("isHistory", isHistory);
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
        isHistory = getIntent().getBooleanExtra("isHistory", false);
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
                    if (enterpriseOrderInfo.clothesDetails.size() > 0) {
                        lineClothes.setVisibility(View.VISIBLE);
                        ItemAdapter adapter = new ItemAdapter(this);
                        adapter.setList(enterpriseOrderInfo.clothesDetails);
                        lvClothes.setAdapter(adapter);
                    } else {
                        lineClothes.setVisibility(View.GONE);
                    }

                }
                txtOrderTime.setText(DateUtil.getTime(enterpriseOrderInfo.createTime, FORMAT_TIME));
                if (Constants.OUTER_WAIT_PAY.equals(type)) {
                    //总价
                    linePrice.setVisibility(View.VISIBLE);
                    layoutTotalPrice.setVisibility(View.VISIBLE);
                    txtTotalPrice.setText(getString(R.string.order_money,
                            StringUtil.formatPriceByFen(enterpriseOrderInfo.sumAmount)));
                } else if (Constants.OUTER_TO_BE_WASHED.equals(type)) {
                    //总价、点值、实付金额
                    linePrice.setVisibility(View.VISIBLE);
                    layoutTotalPrice2.setVisibility(View.VISIBLE);
                    txtTotalPrice2.setText(getString(R.string.order_money,
                            StringUtil.formatPriceByFen(enterpriseOrderInfo.sumAmount)));
                    if (enterpriseOrderInfo.integralAmount == 0) {
                        layoutPreferential.setVisibility(View.GONE);
                    } else {
                        txtPreferential.setText("- " + getString(R.string.order_money,
                                StringUtil.formatPriceByFen(enterpriseOrderInfo.integralAmount)));
                    }
                    txtActualPrice.setText(getString(R.string.order_money,
                            StringUtil.formatPriceByFen(enterpriseOrderInfo.sumAmount - enterpriseOrderInfo.integralAmount)));

                } else if (Constants.OUTER_CANCEL.equals(type)) {
                    layoutCancelTime.setVisibility(View.VISIBLE);
                    txtCancelTime.setText(DateUtil.getTime(enterpriseOrderInfo.cancelTime, FORMAT_TIME));
                }
                if (enterpriseOrderInfo.payTime > 0) {
                    layoutPayTime.setVisibility(View.VISIBLE);
                    txtPayTime.setText(DateUtil.getTime(enterpriseOrderInfo.payTime, FORMAT_TIME));
                }
            }
            final Employee employeeInfo = resultEnterpriseDetail.employeeInfo;
            if (employeeInfo != null) {
                txtName.setText(employeeInfo.employeeName);
                txtNameCode.setText(employeeInfo.employeeExtension);
                txtPhone.setText(employeeInfo.employeePhone);
                txtAddress.setText(employeeInfo.branchName);
                if (isHistory) {
                    txtPhone.setTextColor(getResources().getColor(R.color.text_black_light));
                } else {
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

    }

    class ItemAdapter extends BaseListAdapter<ClothesInfo> {

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
