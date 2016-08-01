/**
 * Description:
 * Copyright: Copyright (c) 2016
 * Company: BLUE MOON
 *
 * @author: wangshanhai
 * @version 3.1.0
 * @date: 2016/3/23
 * @todo: 进销存发货详情
 */
package cn.com.bluemoon.delivery.module.inventory;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.inventory.ProductEndReceiveVo;
import cn.com.bluemoon.delivery.app.api.model.inventory.ResultEndReceiveOrderBean;
import cn.com.bluemoon.delivery.app.api.model.inventory.ResultEndReceiveOrderVo;
import cn.com.bluemoon.delivery.app.api.model.inventory.ResultTicketPhotoVo;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.delivery.utils.manager.ActivityManager;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

public class OrderEndReceiveDetailActivity extends Activity implements OnClickListener {
    private String TAG = "OrderEndReceiveDetailActivity";
    private ListView listView;
    private OrderProductAdapter adapter;
    private List<ProductEndReceiveVo> lists;
    private View headView;
    private View footView;
    private TextView txt_outBackup;
    private TextView txt_commenName_fhck;
    private TextView txt_commenName_fh_address;
    private TextView txt_commonName_deliver_date;
    private TextView txt_customerName_fh_bill;
    private TextView txt_commonName_deliver_shipper;
    private TextView txt_commenName_fhf_address;
    private TextView txt_orderid;
    private TextView txt_source;
    private TextView txt_order_deliver_store_nums;
    private TextView txt_head_diff_nums;
    private TextView txt_total_money;
    private TextView txt_fh_store;
    private TextView txt_fh_address;
    private TextView txt_fh_date;
    private TextView txt_fh_bill;
    private TextView txt_fh_phone;
    private TextView txt_fh_name;
    private TextView txt_distribution_buniessName;
    private TextView txt_fhf_address;
    private TextView txt_list_order_detail_name;
    private RelativeLayout rel_deliver_address, rel_deliver_date, rel_deliverTicket, rel_distribution_buniessName;
    private RelativeLayout ll_diff_layout;

    private ImageView iv_fh_address;
    private ImageView iv_fh_date;
    private ImageView iv_fh_bill;

    private String outBackup;
    private Activity main;
    private String orderCode;
    private String type;
    private CommonProgressDialog progressDialog;
    private List<String> list;//查询的图片


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_inventory_details);
        ActivityManager.getInstance().pushOneActivity(this);
        init();
        initCustomActionBar();
        initView();
    }

    private void init() {
        lists = new ArrayList<ProductEndReceiveVo>();
        orderCode = getIntent().getStringExtra("orderCode");
        type = getIntent().getStringExtra("type");
        main = this;
        list = new ArrayList<String>();
        progressDialog = new CommonProgressDialog(main);
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.listview_product);
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
        DeliveryApi.getOutReceiveDetail(token, orderCode, deliverOrderDetailHandler);
    }

    private void initHeadView() {
        headView = LayoutInflater.from(this).inflate(R.layout.order_detail_listview_head, null);
        rel_distribution_buniessName = (RelativeLayout) headView.findViewById(R.id.rel_distribution_buniessName);
        txt_commenName_fhck = (TextView) headView.findViewById(R.id.txt_commenName_fhck);
        txt_commenName_fh_address = (TextView) headView.findViewById(R.id.txt_commenName_fh_address);
        txt_commonName_deliver_date = (TextView) headView.findViewById(R.id.txt_commonName_deliver_date);
        txt_customerName_fh_bill = (TextView) headView.findViewById(R.id.txt_customerName_fh_bill);
        txt_commonName_deliver_shipper = (TextView) headView.findViewById(R.id.txt_commonName_deliver_shipper);
        txt_commenName_fhf_address = (TextView) headView.findViewById(R.id.txt_commenName_fhf_address);
        txt_orderid = (TextView) headView.findViewById(R.id.txt_orderid);
        txt_source = (TextView) headView.findViewById(R.id.txt_source);
        txt_order_deliver_store_nums = (TextView) headView.findViewById(R.id.txt_order_deliver_store_nums);
        txt_head_diff_nums = (TextView) headView.findViewById(R.id.txt_head_diff_nums);
        txt_total_money = (TextView) headView.findViewById(R.id.txt_total_money);
        txt_fh_store = (TextView) headView.findViewById(R.id.txt_fh_store);
        txt_fh_address = (TextView) headView.findViewById(R.id.txt_fh_address);
        txt_fh_date = (TextView) headView.findViewById(R.id.txt_fh_date);
        txt_fh_bill = (TextView) headView.findViewById(R.id.txt_fh_bill);
        txt_fh_phone = (TextView) headView.findViewById(R.id.txt_fh_phone);
        txt_fh_phone.setOnClickListener(this);
        txt_fh_name = (TextView) headView.findViewById(R.id.txt_fh_name);
        txt_distribution_buniessName = (TextView) headView.findViewById(R.id.txt_distribution_buniessName);
        txt_fhf_address = (TextView) headView.findViewById(R.id.txt_fhf_address);
        txt_list_order_detail_name = (TextView) headView.findViewById(R.id.txt_list_order_detail_name);

        rel_deliver_address = (RelativeLayout) headView.findViewById(R.id.rel_deliver_address);
        rel_deliver_date = (RelativeLayout) headView.findViewById(R.id.rel_deliver_date);
        rel_deliverTicket = (RelativeLayout) headView.findViewById(R.id.rel_deliverTicket);
        ll_diff_layout = (RelativeLayout) headView.findViewById(R.id.ll_diff_layout);
        ll_diff_layout.setVisibility(View.VISIBLE);

        iv_fh_address = (ImageView) headView.findViewById(R.id.iv_fh_address);
        iv_fh_date = (ImageView) headView.findViewById(R.id.iv_fh_date);
        iv_fh_bill = (ImageView) headView.findViewById(R.id.iv_fh_bill);

        rel_deliver_address.setOnClickListener(this);
        rel_deliver_date.setOnClickListener(this);
        rel_deliverTicket.setOnClickListener(this);
        listView.addHeaderView(headView);
        initTipText();
    }

    private void initTipText() {
        txt_fh_bill.setText("");
        txt_list_order_detail_name.setText(getResources().getString(R.string.text_deliver_recvive_detail));
        txt_commenName_fhck.setText(getResources().getString(R.string.text_recrive_store));
        txt_commenName_fh_address.setText(getResources().getString(R.string.text_store_address_title));
        txt_commonName_deliver_date.setText(getResources().getString(R.string.text_recrive_date));
        txt_customerName_fh_bill.setText(getResources().getString(R.string.text_recrive_upload_ticket));
        txt_commonName_deliver_shipper.setText(getResources().getString(R.string.text_deliver_name));
        txt_commenName_fhf_address.setText(getResources().getString(R.string.text_deliver_sent_address));
        Drawable drawable = main.getResources().getDrawable(R.mipmap.addressee);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        txt_commonName_deliver_shipper.setCompoundDrawables(drawable, null, null, null);
    }

    private void initFoot() {
        footView = LayoutInflater.from(this).inflate(R.layout.order_inventory_detail_bottom, null);
        txt_outBackup = (TextView) footView.findViewById(R.id.txt_outBackup);
        footView.setVisibility(View.GONE);
    }

    private void initCustomActionBar() {
        new CommonActionBar(getActionBar(), new IActionBarListener() {
            @Override
            public void setTitle(TextView v) {
                // TODO Auto-generated method stub
                v.setText(getResources().getString(R.string.text_recvive_detail_end));
            }

            @Override
            public void onBtnRight(View v) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onBtnLeft(View v) {
                // TODO Auto-generated method stub
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
            case R.id.rel_deliverTicket:
                if (list.size() > 0) {
                    Intent it = new Intent(main, OrderTicketUploadActivity.class);
                    it.putExtra("type", "endReceive");
                    Bundle b = new Bundle();
                    b.putStringArrayList("piclist", (ArrayList<String>) list);
                    it.putExtra("piclist", b);
                    main.startActivity(it);
                } else {
                    PublicUtil.showToast(main, getString(R.string.text_no_ticket_tip));
                }
                break;
            case R.id.txt_fh_phone:
                PublicUtil.showCallPhoneDialog2(this, txt_fh_phone.getText().toString());
                break;
        }
    }

    private boolean isDeliver() {
        if (InventoryTabActivity.DELIVERY_MANAGEMENT.equals(type)) {
            return true;
        } else {
            return false;
        }
    }


    private void getTicketPic() {
        if (orderCode == null || "".equals(orderCode)) {
            return;
        }
        String token = ClientStateManager.getLoginToken(main);
        if (progressDialog != null) {
            progressDialog.show();
        }
        DeliveryApi.getPicDetail(token, orderCode, getTicketPhotoHandler);
    }

    AsyncHttpResponseHandler getTicketPhotoHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            LogUtils.d(TAG, "deliverOrderDetailHandler result = " + responseString);
            if (progressDialog != null)
                progressDialog.dismiss();
            try {
                ResultTicketPhotoVo photo = JSON.parseObject(responseString,
                        ResultTicketPhotoVo.class);
                if (photo.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    if (photo.getPicList() != null && photo.getPicList().size() > 0) {
                        if (photo.getPicList().size() > 5) {
                            list.addAll(photo.getPicList().subList(0, 5));
                        } else {
                            list.addAll(photo.getPicList());
                        }
                        txt_fh_bill.setTextColor(getResources().getColor(R.color.text_black_light));
                        txt_fh_bill.setText(getString(R.string.text_ticket_tip));
                    } else {
                        txt_fh_bill.setText(getString(R.string.text_deliver_ticket_tip2));
                        txt_fh_bill.setTextColor(getResources().getColor(R.color.text_grep));
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
            LogUtils.e(TAG, throwable.getMessage());
            if (progressDialog != null)
                progressDialog.dismiss();
            PublicUtil.showToastServerOvertime();
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
                ResultEndReceiveOrderBean deliverOrderDetailInfo = JSON.parseObject(responseString,
                        ResultEndReceiveOrderBean.class);
                if (deliverOrderDetailInfo.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    getTicketPic();
                    setData(deliverOrderDetailInfo);
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


    private void setData(ResultEndReceiveOrderBean result) {
        if (result == null || result.getReceiptOrderDetail() == null || result.getReceiptOrderDetail().getProductDetails().size() < 1) {
            PublicUtil.showToastErrorData(main);
            return;
        }
        setHeadViewData(result);
        List<ProductEndReceiveVo> lis = result.getReceiptOrderDetail().getProductDetails();
        lists.addAll(lis);
        adapter.notifyDataSetChanged();
    }

    private void setHeadViewData(ResultEndReceiveOrderBean result) {
        if (result.getReceiptOrderDetail() == null) {
            return;
        }
        ResultEndReceiveOrderVo info = result.getReceiptOrderDetail();
        txt_orderid.setText(info.getOrderCode());
        txt_order_deliver_store_nums.setText((String.format(getString(R.string.order_boxes_count),
                StringUtil.formatBoxesNum(info.getTotalCase())) +
                String.format(getString(R.string.order_product_count), info.getTotalNum() + "")));
        txt_total_money.setText(getString(R.string.order_money_sign) + StringUtil.formatPriceByFen(info.getTotalMoney()));
        txt_head_diff_nums.setText(String.format(getString(R.string.order_diff_product_count), info.getTotalDiffNum() + ""));
        txt_source.setText(DateUtil.getTime(info.getReDate(), "yyyy-MM-dd"));


        StringBuilder sb = new StringBuilder("");
        if (!StringUtil.isEmptyString(info.getReStoreCode())) {
            sb.append(info.getReStoreCode());
        }
        if (!StringUtil.isEmptyString(info.getReStoreType())) {
            sb.append("-").append(info.getReStoreType());
        }
        if(!StringUtil.isEmptyString(info.getReStoreChargeName())){
            sb.append("-").append(info.getReStoreChargeName());
        }
        txt_fh_store.setText(sb.toString());

/*        txt_fh_store.setText(info.getReStoreCode() + "-"
                + info.getReStoreType() + "-" + info.getReStoreChargeName());*/
        txt_fh_address.setText(info.getReStoreAddrName());
        txt_fh_phone.setText(info.getDeliveryTel());
        txt_fh_phone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        txt_fh_phone.getPaint().setAntiAlias(true);

        txt_fh_name.setText(info.getDeliveryName());
        txt_distribution_buniessName.setText(info.getCompanyName());
        if ("".equals(info.getCompanyName()) || info.getCompanyName() == null) {
            txt_distribution_buniessName.setVisibility(View.GONE);
            rel_distribution_buniessName.setVisibility(View.GONE);
        } else {
            txt_distribution_buniessName.setVisibility(View.VISIBLE);
            rel_distribution_buniessName.setVisibility(View.VISIBLE);
        }
        txt_fhf_address.setText(info.getDeliveryAddr());

        String time = DateUtil.getTime(info.getReDate(), "yyyy-MM-dd HH:mm");
        time = time.substring(0, time.indexOf(":")) + ":00";
        txt_fh_date.setText(time);
    }


    public static void actionStart(Context context, String type, String orderCode) {
        Intent intent = new Intent(context, OrderEndReceiveDetailActivity.class);
        intent.putExtra("orderCode", orderCode);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }


    class OrderProductAdapter extends BaseAdapter {
        LayoutInflater mInflater;
        private List<ProductEndReceiveVo> list;
        Context context;

        public OrderProductAdapter(Context context, List<ProductEndReceiveVo> data) {
            this.context = context;
            this.mInflater = LayoutInflater.from(context);
            this.list = data;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.order_details_product_item, null);
                holder.line_dotted_bottom = convertView.findViewById(R.id.line_dotted_bottom);
                holder.txt_order_number = (TextView) convertView.findViewById(R.id.txt_order_number);
                holder.txt_box_rule_num = (TextView) convertView.findViewById(R.id.txt_box_rule_num);
                holder.txt_order_product_name = (TextView) convertView.findViewById(R.id.txt_order_product_name);
                holder.txt_boxes_counts = (TextView) convertView.findViewById(R.id.txt_boxes_counts);
                holder.txt_diff_nums_product = (TextView) convertView.findViewById(R.id.txt_diff_nums_product);
                holder.txt_diff_reason = (TextView) convertView.findViewById(R.id.txt_diff_reason);
                holder.rel_diff_layout = (RelativeLayout) convertView.findViewById(R.id.rel_diff_layout);
                holder.ll_none_diff_num = (LinearLayout) convertView.findViewById(R.id.ll_none_diff_num);
                holder.txt_title_amount = (TextView) convertView.findViewById(R.id.txt_title_amount);
                holder.line_solid_deep_bottom = convertView.findViewById(R.id.line_solid_deep_bottom);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.txt_order_number.setText(list.get(position).getProductNo());
            holder.txt_box_rule_num.setText(list.get(position).getCarton() + "");
            holder.txt_order_product_name.setText(list.get(position).getProductName());
            holder.txt_title_amount.setText(R.string.order_title_receive_amount);
            holder.txt_boxes_counts.setText((String.format(getString(R.string.order_boxes_count),
                    StringUtil.formatBoxesNum(list.get(position).getReCase())) +
                    String.format(getString(R.string.order_product_count), list.get(position).getReNum())));
            holder.txt_diff_nums_product.setText(list.get(position).getReDifferNum() + "");
            holder.txt_diff_reason.setText(list.get(position).getReDifferReason());
            holder.txt_diff_reason.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            holder.txt_diff_reason.getPaint().setAntiAlias(true);

            if (list.get(position).getReDifferNum() == 0) {
                holder.rel_diff_layout.setVisibility(View.GONE);
                holder.line_dotted_bottom.setVisibility(View.VISIBLE);
                holder.ll_none_diff_num.setVisibility(View.VISIBLE);
            } else {
                holder.rel_diff_layout.setVisibility(View.VISIBLE);
                holder.line_dotted_bottom.setVisibility(View.GONE);
                holder.ll_none_diff_num.setVisibility(View.GONE);
            }

            if (isDeliver()) {
                holder.rel_diff_layout.setVisibility(View.GONE);
                holder.line_dotted_bottom.setVisibility(View.GONE);
                holder.ll_none_diff_num.setVisibility(View.GONE);
            }

            if (position == list.size() - 1) {
                holder.line_dotted_bottom.setVisibility(View.GONE);
                holder.line_solid_deep_bottom.setVisibility(View.GONE);
            } else {
                holder.line_dotted_bottom.setVisibility(View.VISIBLE);
                holder.line_solid_deep_bottom.setVisibility(View.GONE);
            }

            holder.txt_diff_reason.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogForDiffReason dialogForDiffReason = new DialogForDiffReason(main, list.get(position).getReDifferReason(),
                            list.get(position).getReDifferBackup());
                    dialogForDiffReason.setDialogCallback(dialogcallback2);
                    dialogForDiffReason.show();
                }
            });
            return convertView;
        }

        final class ViewHolder {
            View line_dotted_bottom, line_solid_deep_bottom;
            TextView txt_order_number;
            TextView txt_box_rule_num;
            TextView txt_order_product_name;
            TextView txt_boxes_counts;
            TextView txt_diff_nums_product;
            TextView txt_diff_reason;
            TextView txt_title_amount;
            RelativeLayout rel_diff_layout;
            LinearLayout ll_none_diff_num;
        }

    }


    DialogForDiffReason.Dialogcallback dialogcallback2 = new DialogForDiffReason.Dialogcallback() {
        @Override
        public void dialogdo(String string) {
            //btn2.setText(string);
    }
    };


}


