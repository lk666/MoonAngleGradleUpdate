package cn.com.bluemoon.delivery.module.wash.collect;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.clothing.ResultClothingRecord;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.ClothesInfo;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.CollectOrderDetail;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.LaundryLog;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.oldbase.BaseActionBarActivity;
import cn.com.bluemoon.delivery.ui.NoScrollListView;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;

/**
 * 收衣订单页面
 * Created by allenli on 2016/6/28.
 */
public class ClothingRecordDetailActivity extends BaseActionBarActivity implements
        OnListItemClickListener {

    private String manager;
    private String collectCode;
    private DeliverLogAdapter deliveryAdapter;
    private ClothesInfoAdapter clothingInfoAdapter;
    @Bind(R.id.txt_log)
    TextView txtLog;
    @Bind(R.id.line_logs)
    View lineLog;
    @Bind(R.id.txt_log_open)
    TextView txtLogOpen;
    @Bind(R.id.list_view_log)
    NoScrollListView listViewLog;

    @Bind(R.id.txt_outer_code)
    TextView txtOutCode;
    @Bind(R.id.txt_type_open)
    TextView txtTypeOpen;
    @Bind(R.id.layout_type)
    LinearLayout layoutType;

    @Bind(R.id.txt_username)
    TextView txtUserName;
    @Bind(R.id.txt_user_phone)
    TextView txtUserPhone;
    @Bind(R.id.txt_address)
    TextView txtAddress;
    @Bind(R.id.txt_total_money)
    TextView txtTotalMoney;


    @Bind(R.id.txt_need_lab)
    TextView txtNeedLab;
    @Bind(R.id.txt_need)
    TextView txtNeed;
    @Bind(R.id.list_view_type)
    NoScrollListView listViewType;

    @Bind(R.id.txt_collect_num)
    TextView txtCollectNum;
    @Bind(R.id.txt_actual)
    TextView txtActual;
    @Bind(R.id.txt_scan_code)
    TextView txtScanCode;
    @Bind(R.id.txt_urgent)
    TextView txtUrgent;
    @Bind(R.id.listview_info)
    NoScrollListView listViewInfo;

    @Bind(R.id.txt_scan_code_lab)
    TextView txtScanLab;

    @Bind(R.id.sc_main)
    ScrollView scMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothing_record_details);
        ButterKnife.bind(this);
        collectCode = getIntent().getStringExtra("collectCode");
        manager = getIntent().getStringExtra("manager");

        clothingInfoAdapter = new ClothesInfoAdapter(ClothingRecordDetailActivity.this, this);
        listViewInfo.setAdapter(clothingInfoAdapter);
        init();
    }

    private void init() {

        showProgressDialog();
        if (manager.equals(ClothingTabActivity.WITH_ORDER_COLLECT_MANAGE)) {
            DeliveryApi.collectInfoDetails(ClientStateManager.getLoginToken(this), collectCode,
                    infoHandler);
        } else if (manager.equals(ClothingTabActivity.WITHOUT_ORDER_COLLECT_MANAGE)) {
            DeliveryApi.collectInfoDetails2(ClientStateManager.getLoginToken(this), collectCode,
                    infoHandler);
        }
    }


    AsyncHttpResponseHandler infoHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d(getDefaultTag(), "infoHandler result = " + responseString);
            dismissProgressDialog();
            try {
                ResultClothingRecord result = JSON.parseObject(responseString,
                        ResultClothingRecord.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    InitView(result);
                } else {
                    PublicUtil.showErrorMsg(ClothingRecordDetailActivity.this, result);
                }
            } catch (Exception e) {
                LogUtils.e(getDefaultTag(), e.getMessage());
                PublicUtil.showToastServerBusy();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            LogUtils.e(getDefaultTag(), throwable.getMessage());
            dismissProgressDialog();
            PublicUtil.showToastServerOvertime();
        }
    };


    private void InitView(ResultClothingRecord record) {


        deliveryAdapter = new DeliverLogAdapter(ClothingRecordDetailActivity.this, this);
        if (null != record.getLaundryLog()
                ) {
            if (record.getLaundryLog().size() > 0) {

                txtLog.setText(String.format("%s,%s,%s\n%s", record.getLaundryLog().get(0)
                                .getNodeName(), record.getLaundryLog().get(0).getPhone()
                        , record.getLaundryLog().get(0).getAction(), DateUtil.getTime(record
                                .getLaundryLog().get(0).getOpTime(), "yyyy-MM-dd HH:mm:ss"))
                );

            }
            if (record.getLaundryLog().size() > 1) {
                List<LaundryLog> logList = record.getLaundryLog();
                logList.remove(0);
                deliveryAdapter.setList(logList);
                listViewLog.setAdapter(deliveryAdapter);
            }
        }


        txtAddress.setText(String.format("%s%s%s%s%s%s", record.getProvince(),
                record.getCity(),
                record.getCounty(),
                record.getVillage(),
                record.getStreet(),
                record.getAddress()));
        txtTotalMoney.setText(StringUtil.formatPriceByFen(record.getPayTotal()));
        if (manager.equals(ClothingTabActivity.WITH_ORDER_COLLECT_MANAGE)) {

            String brcode = record.getCollectBrcode();
            if (TextUtils.isEmpty(brcode)) {
                txtScanCode.setText(getString(R.string.text_empty));
            } else {
                txtScanCode.setText(brcode);
            }
            txtOutCode.setText(record.getOuterCode());
            txtNeed.setText(String.valueOf(record.getReceivableTotal()));
            if (null != record.getOrderDetail() && record.getOrderDetail().size() > 0) {
                ClothingTypeAdapter clothingTypeAdapter = new ClothingTypeAdapter(ClothingRecordDetailActivity.this,
                        this);
                clothingTypeAdapter.setList(record.getOrderDetail());
                listViewType.setAdapter(clothingTypeAdapter);
            }
            txtActual.setText(String.valueOf(record.getCollectOrderDetail().size()));
        } else {
            txtScanLab.setVisibility(View.GONE);
            txtNeedLab.setVisibility(View.GONE);

            txtOutCode.setText(record.getActivityName());
            txtActual.setText(String.valueOf(record.getActualCount()));
        }
        txtUserName.setText(record.getCustomerName());
        txtUserPhone.setText(record.getCustomerPhone());
        txtCollectNum.setText(record.getCollectCode());
        txtUrgent.setVisibility(record.getIsUrgent() > 0 ? View.VISIBLE : View.GONE);

        if (null != record.getCollectOrderDetail() && record.getCollectOrderDetail().size() > 0) {

            List<ClothesInfo> list = new ArrayList<>();
            for (CollectOrderDetail detail : record.getCollectOrderDetail()
                    ) {
                ClothesInfo info = new ClothesInfo();
                info.setClothesCode(detail.getClothesCode());
                info.setClothesName(detail.getClothesName());
                info.setClothesnameCode(detail.getClothesnameCode());
                info.setImgPath(detail.getImgPath());
                info.setTypeCode(detail.getTypeCode());
                info.setTypeName(detail.getTypeName());
                list.add(info);
            }

            clothingInfoAdapter.setList(list);
            clothingInfoAdapter.notifyDataSetChanged();
        }

        scMain.postDelayed(new Runnable() {
            @Override
            public void run() {
                scMain.scrollTo(0, 0);
            }
        }, 100);

    }


    @Override
    protected int getActionBarTitleRes() {
        return R.string.clothing_record_detail;
    }

    @Override
    public void onItemClick(Object item, View view, int position) {
        if (item instanceof ClothesInfo) {
            ClothesInfo info = (ClothesInfo) item;
            ClothesDetailActivity.actionStart(ClothingRecordDetailActivity.this, info
                    .getClothesCode());
        }
    }

    @OnClick({R.id.txt_log_open, R.id.layout_logs, R.id.txt_type_open, R.id.layout_activities})
    public void openView(View view) {
        switch (view.getId()) {
            case R.id.layout_logs:
            case R.id.txt_log_open:
                if (lineLog.getVisibility() == View.GONE) {
                    lineLog.setVisibility(View.VISIBLE);
                    if (deliveryAdapter.getCount() < 1) {
                        listViewLog.setVisibility(View.GONE);
                    } else {
                        listViewLog.setVisibility(View.VISIBLE);
                    }
                    txtLogOpen.setText(getString(R.string.txt_close));
                    Drawable drawable = getResources().getDrawable(R.mipmap.ic_up);
                    assert drawable != null;
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable
                            .getMinimumHeight());
                    txtLogOpen.setCompoundDrawables(null, null, drawable, null);

                } else {
                    lineLog.setVisibility(View.GONE);
                    listViewLog.setVisibility(View.GONE);
                    txtLogOpen.setText(getString(R.string.txt_open));
                    Drawable drawable = getResources().getDrawable(R.mipmap.ic_down);
                    assert drawable != null;
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable
                            .getMinimumHeight());
                    txtLogOpen.setCompoundDrawables(null, null, drawable, null);
                }
                break;
            case R.id.layout_activities:
            case R.id.txt_type_open:

                if (layoutType.getVisibility() == View.GONE) {
                    layoutType.setVisibility(View.VISIBLE);
                    txtTypeOpen.setText(getString(R.string.txt_close));
                    Drawable drawable = getResources().getDrawable(R.mipmap.ic_up);
                    assert drawable != null;
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable
                            .getMinimumHeight());
                    txtTypeOpen.setCompoundDrawables(null, null, drawable, null);

                } else {
                    layoutType.setVisibility(View.GONE);
                    txtTypeOpen.setText(getString(R.string.txt_open));
                    Drawable drawable = getResources().getDrawable(R.mipmap.ic_down);
                    assert drawable != null;
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable
                            .getMinimumHeight());
                    txtTypeOpen.setCompoundDrawables(null, null, drawable, null);
                }
                break;
        }

    }

    public static void actionStart(Context context, String collectCode, String manager) {
        Intent intent = new Intent(context, ClothingRecordDetailActivity.class);
        intent.putExtra("collectCode", collectCode);
        intent.putExtra("manager", manager);
        context.startActivity(intent);
    }
}
