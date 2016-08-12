/**
 * Description:
 * Copyright: Copyright (c) 2016
 * Company: BLUE MOON
 *
 * @author: wangshanhai
 * @version 3.1.0
 * @date: 2016/3/23
 * @todo: end deliver goods detail
 */
package cn.com.bluemoon.delivery.module.inventory;


import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.commons.lang3.text.StrBuilder;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.inventory.ProductEndDeliverVo;
import cn.com.bluemoon.delivery.app.api.model.inventory.ResultEndDeliverOrderBean;
import cn.com.bluemoon.delivery.app.api.model.inventory.ResultEndDeliverOrderVo;
import cn.com.bluemoon.delivery.app.api.model.inventory.ResultTicketPhotoVo;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.BasePullHeadToRefreshListViewActivity;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;

/**
 * 已发货物详情
 */
public class OrderEndDeliverDetailActivity extends BasePullHeadToRefreshListViewActivity
        implements View.OnClickListener {

    private static final int REQUEST_CODE_GET_TICKET_PHOTO = 0x1;

    private String orderCode;

    /**
     * 查询的图片
     */
    private List<String> list;

    private TextView txtOutBackup;

    //////////// 头部控件 ////////////////
    private TextView txtCommenNameFhck;
    private TextView txtCommenNameFhAddress;
    private TextView txtCommonNameDeliverDate;
    private TextView txtCustomerNameFhBill;
    private TextView txtCommonNameDeliverShipper;
    private TextView txtCommenNameFhfAddress;
    private TextView txtOrderid;
    private TextView txtSource;
    private TextView txtOrderDeliverStoreNums;
    private TextView txtHeadDiffNums;
    private TextView txtTotalMoney;
    private TextView txtFhStore;
    private TextView txtFhAddress;
    private TextView txtFhDate;
    private TextView txtFhPhone;
    private TextView txtFhName;
    private TextView txtDistributionBuniessName;
    private TextView txtFhfAddress;
    private TextView txtListOrderDetailName;
    private RelativeLayout relDistributionBuniessName;
    private TextView txtFhBill;
    private RelativeLayout llDiffLayout;


    public static void actionStart(Context context, String type, String orderCode) {
        Intent intent = new Intent(context, OrderEndDeliverDetailActivity.class);
        intent.putExtra("orderCode", orderCode);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        orderCode = getIntent().getStringExtra("orderCode");
        if (orderCode == null) {
            orderCode = "";
        }
        String type = getIntent().getStringExtra("type");
        if (type == null) {
            type = "";
        }
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase resultBase) {
        super.onSuccessResponse(requestCode, jsonString, resultBase);
        switch (requestCode) {
            case REQUEST_CODE_GET_TICKET_PHOTO:
                hideWaitDialog();
                ResultTicketPhotoVo photo = (ResultTicketPhotoVo) resultBase;
                if (photo.getPicList() != null && photo.getPicList().size() > 0) {
                    if (photo.getPicList().size() > 5) {
                        list.addAll(photo.getPicList().subList(0, 5));
                    } else {
                        list.addAll(photo.getPicList());
                    }
                    txtFhBill.setTextColor(getResources().getColor(R.color.text_black_light));
                    txtFhBill.setText(getString(R.string.text_ticket_tip));
                } else {
                    txtFhBill.setText(getString(R.string.text_deliver_ticket_tip2));
                    txtFhBill.setTextColor(getResources().getColor(R.color.text_grep));
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void initPullToRefreshListView(PullToRefreshListView ptrlv) {
        ptrlv.getRefreshableView().setDivider(null);
        ptrlv.getRefreshableView().setDividerHeight(0);

        View footView = LayoutInflater.from(this).inflate(R.layout.order_inventory_detail_bottom,
                null);
        txtOutBackup = (TextView) footView.findViewById(R.id.txt_outBackup);
        footView.setVisibility(View.VISIBLE);
        ptrlv.getRefreshableView().addFooterView(footView);
    }

    @Override
    protected int getHeadLayoutId() {
        return R.layout.order_detail_listview_head;
    }

    @Override
    protected void initHeadViewEvent(View headView) {
        list = new ArrayList<>();
        relDistributionBuniessName = (RelativeLayout) headView.findViewById(R.id
                .rel_distribution_buniessName);
        txtCommenNameFhck = (TextView) headView.findViewById(R.id.txt_commenName_fhck);
        txtCommenNameFhAddress = (TextView) headView.findViewById(R.id
                .txt_commenName_fh_address);
        txtCommonNameDeliverDate = (TextView) headView.findViewById(R.id
                .txt_commonName_deliver_date);
        txtCustomerNameFhBill = (TextView) headView.findViewById(R.id.txt_customerName_fh_bill);
        txtCommonNameDeliverShipper = (TextView) headView.findViewById(R.id
                .txt_commonName_deliver_shipper);
        txtCommenNameFhfAddress = (TextView) headView.findViewById(R.id
                .txt_commenName_fhf_address);
        txtOrderid = (TextView) headView.findViewById(R.id.txt_orderid);
        txtSource = (TextView) headView.findViewById(R.id.txt_source);
        txtOrderDeliverStoreNums = (TextView) headView.findViewById(R.id
                .txt_order_deliver_store_nums);
        txtHeadDiffNums = (TextView) headView.findViewById(R.id.txt_head_diff_nums);
        txtTotalMoney = (TextView) headView.findViewById(R.id.txt_total_money);
        txtFhStore = (TextView) headView.findViewById(R.id.txt_fh_store);
        txtFhAddress = (TextView) headView.findViewById(R.id.txt_fh_address);
        txtFhDate = (TextView) headView.findViewById(R.id.txt_fh_date);
        txtFhBill = (TextView) headView.findViewById(R.id.txt_fh_bill);
        txtFhPhone = (TextView) headView.findViewById(R.id.txt_fh_phone);
        txtFhName = (TextView) headView.findViewById(R.id.txt_fh_name);
        txtDistributionBuniessName = (TextView) headView.findViewById(R.id
                .txt_distribution_buniessName);
        txtFhfAddress = (TextView) headView.findViewById(R.id.txt_fhf_address);
        txtListOrderDetailName = (TextView) headView.findViewById(R.id
                .txt_list_order_detail_name);

        RelativeLayout relDeliverTicket = (RelativeLayout) headView.findViewById(R.id
                .rel_deliverTicket);
        llDiffLayout = (RelativeLayout) headView.findViewById(R.id.ll_diff_layout);
        llDiffLayout.setVisibility(View.VISIBLE);

        relDeliverTicket.setOnClickListener(this);
        txtFhPhone.setOnClickListener(this);

        initTipText();
    }

    private void initTipText() {
        txtFhBill.setText("");
        txtListOrderDetailName.setText(getResources().getString(R.string
                .text_deliver_sent_detail));
        txtCommenNameFhck.setText(getResources().getString(R.string.text_deliver_store));
        txtCommenNameFhAddress.setText(getResources().getString(R.string
                .text_deliver_address));
        txtCommonNameDeliverDate.setText(getResources().getString(R.string.text_deliver_date));
        txtCustomerNameFhBill.setText(getResources().getString(R.string
                .text_deliver_upload_ticket));
        txtCommonNameDeliverShipper.setText(getResources().getString(R.string
                .text_recriver_store));
        txtCommenNameFhfAddress.setText(getResources().getString(R.string
                .text_recrive_address));
        Drawable drawable = getResources().getDrawable(R.mipmap.addresser);
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        }
        txtCommonNameDeliverShipper.setCompoundDrawables(drawable, null, null, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rel_deliverTicket:
                if (list.size() > 0) {
                    Intent it = new Intent(this, OrderTicketUploadActivity.class);
                    it.putExtra("type", "endReceive");
                    Bundle b = new Bundle();
                    b.putStringArrayList("piclist", (ArrayList<String>) list);
                    it.putExtra("piclist", b);
                    this.startActivity(it);
                } else {
                    PublicUtil.showToast(this, getString(R.string.text_no_ticket_tip));
                }
                break;
            case R.id.txt_fh_phone:
                PublicUtil.showCallPhoneDialog2(this, txtFhPhone.getText().toString());
                break;
        }
    }

    @Override
    protected void showEmptyView() {
        super.showEmptyView();
        // 可在此处设置head等
        setHeadViewVisibility(View.GONE);
    }

    @Override
    protected void showNetErrorView() {
        super.showNetErrorView();
        // 可在此处设置head等
        setHeadViewVisibility(View.GONE);
    }

    @Override
    protected void showRefreshView() {
        super.showRefreshView();
        // 列表数据刷新，如可在此处设置head等
        setHeadViewVisibility(View.VISIBLE);
    }

    @Override
    protected PullToRefreshBase.Mode getMode() {
        return PullToRefreshBase.Mode.DISABLED;
    }

    @Override
    protected void invokeGetDataDeliveryApi(int requestCode) {
        DeliveryApi.getOutDeliverDetail(getToken(), orderCode, getNewHandler(requestCode,
                ResultEndDeliverOrderBean.class));
    }

    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {

    }

    @Override
    protected String getTitleString() {
        return getString(R.string.detail_order_deliver_end);
    }

    @Override
    protected BaseListAdapter getNewAdapter() {
        return new OrderProductAdapter(this, this);
    }

    @Override
    protected List getGetMoreList(ResultBase result) {
        return null;
    }

    @Override
    protected List getGetDataList(ResultBase result) {
        getTicketPic();
        ResultEndDeliverOrderBean deliverOrderDetailInfo = (ResultEndDeliverOrderBean) result;
        setOutHeadViewData(deliverOrderDetailInfo);
        return deliverOrderDetailInfo.getOutOrderDetail().getProductDetails();
    }

    private void setOutHeadViewData(ResultEndDeliverOrderBean result) {
        if (result.getOutOrderDetail() == null) {
            return;
        }
        ResultEndDeliverOrderVo info = result.getOutOrderDetail();
        txtOrderid.setText(info.getOrderCode());

        txtTotalMoney.setText(new StrBuilder(getString(R.string.order_money_sign))
                .append(StringUtil.formatPriceByFen(info.getTotalMoney())).toString());

        txtOrderDeliverStoreNums.setText(
                String.format(getString(R.string.total_boxes_num),
                        StringUtil.formatBoxesNum(info.getTotalCase()), info.getTotalNum()));

        txtHeadDiffNums.setText(String.format(getString(R.string.order_diff_product_count),
                info.getTotalDiffNum() + ""));
        llDiffLayout.setVisibility(View.GONE);

        txtSource.setText(DateUtil.getTime(info.getOrderDate(), "yyyy-MM-dd"));

        StringBuilder sb = new StringBuilder("");
        if (!StringUtil.isEmptyString(info.getDeliStoreCode())) {
            sb.append(info.getDeliStoreCode());
        }
        if (!StringUtil.isEmptyString(info.getDeliStoreType())) {
            sb.append("-").append(info.getDeliStoreType());
        }
        if (!StringUtil.isEmptyString(info.getDeliStoreChargeName())) {
            sb.append("-").append(info.getDeliStoreChargeName());
        }
        txtFhStore.setText(sb.toString());

/*        txtFhStore.setText(info.getDeliStoreCode() + "-"
                + info.getDeliStoreType() + "-" + info.getDeliStoreChargeName());*/
        txtFhAddress.setText(info.getDeliStoreAddrName());
        txtFhPhone.setText(info.getReceiveTel());
        txtFhPhone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        txtFhPhone.getPaint().setAntiAlias(true);

        txtFhName.setText(info.getReceiveName());
        txtDistributionBuniessName.setText(info.getCompanyName());
        if ("".equals(info.getCompanyName()) || info.getCompanyName() == null) {
            txtDistributionBuniessName.setVisibility(View.GONE);
            relDistributionBuniessName.setVisibility(View.GONE);
        } else {
            txtDistributionBuniessName.setVisibility(View.VISIBLE);
            relDistributionBuniessName.setVisibility(View.VISIBLE);
        }
        txtFhfAddress.setText(info.getReceiveAddr());

        String time = DateUtil.getTime(info.getOrderDate(), "yyyy-MM-dd HH:mm");
        time = time.substring(0, time.indexOf(":")) + ":00";
        txtFhDate.setText(time);

        String ba = "";
        if (!StringUtil.isEmpty(info.getOutBackup())) {
            ba = info.getOutBackup();
        }

        String message = "<font color='#333333'>" + getString(R.string.txt_order_bottom_remarks) +
                "</font>" + "<font color='#666666'>" + ba + "</font>";
        txtOutBackup.setText(Html.fromHtml(message));
    }

    /**
     * 入库单局
     */
    private void getTicketPic() {
        showWaitDialog();
        DeliveryApi.getPicDetail(getToken(), orderCode, getNewHandler(REQUEST_CODE_GET_TICKET_PHOTO,
                ResultTicketPhotoVo.class));
    }

    class OrderProductAdapter extends BaseListAdapter<ProductEndDeliverVo> {
        public OrderProductAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.order_details_product_item;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            ProductEndDeliverVo item = (ProductEndDeliverVo) getItem(position);
            if (item == null) {
                return;
            }

            View lineDottedBottom = getViewById(R.id.line_dotted_bottom);
            TextView txtOrderNumber = getViewById(R.id.txt_order_number);
            TextView txtBoxRuleNum = getViewById(R.id.txt_box_rule_num);
            TextView txtOrderProductName = getViewById(R.id.txt_order_product_name);
            TextView txtBoxesCounts = getViewById(R.id.txt_boxes_counts);
            TextView txtDiffNumsProduct = getViewById(R.id.txt_diff_nums_product);
            TextView txtDiffReason = getViewById(R.id.txt_diff_reason);
            RelativeLayout relDiffLayout = getViewById(R.id.rel_diff_layout);
            LinearLayout llNoneDiffNum = getViewById(R.id.ll_none_diff_num);
            TextView txtTitleAmount = getViewById(R.id.txt_title_amount);
            View lineSolidDeepBottom = getViewById(R.id.line_solid_deep_bottom);

            txtOrderNumber.setText(item.getProductNo());
            txtBoxRuleNum.setText(String.valueOf(item.getCarton()));
            txtOrderProductName.setText(item.getProductName());
            txtTitleAmount.setText(R.string.order_title_delive_amount);
            txtBoxesCounts.setText(new StrBuilder(String.format(getString(R.string
                    .order_boxes_count), StringUtil.formatBoxesNum(item.getOutCase())))
                    .append(String.format(getString(R.string.order_product_count),
                            item.getOutNum())).toString());

            if (position == list.size() - 1) {
                lineDottedBottom.setVisibility(View.GONE);
                lineSolidDeepBottom.setVisibility(View.GONE);
            } else {
                lineDottedBottom.setVisibility(View.VISIBLE);
                lineSolidDeepBottom.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onItemClick(Object item, View view, int position) {

    }
}


