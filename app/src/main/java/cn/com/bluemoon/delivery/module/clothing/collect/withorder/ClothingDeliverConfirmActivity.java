package cn.com.bluemoon.delivery.module.clothing.collect.withorder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.clothing.ResultReceiveCollectInfo;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.ClothesInfo;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.module.base.BaseActionBarActivity;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.clothing.collect.ClothesDetailActivity;
import cn.com.bluemoon.delivery.module.clothing.collect.ClothesInfoAdapter;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.lib.utils.LibConstants;

/**
 * Created by allenli on 2016/6/23.
 */
public class ClothingDeliverConfirmActivity extends BaseActionBarActivity implements
        OnListItemClickListener {
    private static final int RESULT_CODE_MANUAL = 0x23;
    private static final int REQUEST_CODE_MANUAL = 0x33;
    @Bind(R.id.txt_deliver_name)
    TextView txtDeliverName;
    @Bind(R.id.txt_deliver_phone)
    TextView txtDeliverPhone;
    @Bind(R.id.txt_deliver_remark)
    TextView txtDeliverRemark;
    @Bind(R.id.btn_ok)
    Button btnConforim;
    @Bind(R.id.btn_cancel)
    Button btnCancel;
    @Bind(R.id.listview_info)
    ListView listViewInfo;
    @Bind(R.id.txt_collect_num)
    TextView txtCollectNum;
    @Bind(R.id.txt_actual)
    TextView txtActual;
    @Bind(R.id.txt_scan_code)
    TextView txtScanCode;
    @Bind(R.id.txt_urgent)
    TextView txtUrgent;
    ClothesInfoAdapter adapter;
    private String collectCode;
    private String scanCode;
    private List<ClothesInfo> clothesInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothing_deliver_confirm);
        collectCode = getIntent().getStringExtra("collectCode");
        scanCode = getIntent().getStringExtra("scanCode");
        ButterKnife.bind(this);
        init();
        if (!StringUtil.isEmpty(scanCode)) {
            handleScaneCodeBack(scanCode);
        }
    }

    @Override
    protected int getActionBarTitleRes() {
        return R.string.title_clothing_deliver_confirm;
    }

    @Override
    protected void initCustomActionBar() {

        CommonActionBar actionBar = new CommonActionBar(getActionBar(),
                new IActionBarListener() {

                    @Override
                    public void onBtnRight(View v) {
                        goScanCode();
                    }

                    @Override
                    public void onBtnLeft(View v) {
                        finish();
                    }

                    @Override
                    public void setTitle(TextView v) {
                        v.setText(R.string.title_clothing_deliver_confirm);
                    }
                });

        actionBar.getImgRightView().setImageResource(R.mipmap.scan_top_nav);
        actionBar.getImgRightView().setVisibility(View.VISIBLE);
    }


    private void goScanCode() {
        PublicUtil.openScan(ClothingDeliverConfirmActivity.this,
                getString(R.string.coupons_scan_code_title),
                getString(R.string.with_order_collect_manual_input_code_btn),
                Constants.REQUEST_SCAN, RESULT_CODE_MANUAL);
    }

    private void init() {
        showProgressDialog();
        DeliveryApi.receiveCollectInfo(ClientStateManager.getLoginToken
                (ClothingDeliverConfirmActivity.this), collectCode, infoHandler);
    }

    private void initView(ResultReceiveCollectInfo result) {
        txtDeliverName.setText(String.format("%s %s", result.getTransmitName(), result
                .getTransmitCode()));
        txtDeliverPhone.setText(result.getTransmitPhone());
        txtActual.setText(String.valueOf(result.getActualCount()));
        txtCollectNum.setText(result.getCollectCode());
        txtScanCode.setText(result.getCollectBrcode());
        txtUrgent.setVisibility(result.getIsUrgent() > 0 ? View.VISIBLE : View.GONE);

        adapter = new ClothesInfoAdapter(this, this);
        clothesInfos = result.getClothesInfo();
        adapter.setList(clothesInfos);
        listViewInfo.setAdapter(adapter);

    }

    public static void actionStart(Activity context, String collectCode, int requestCode) {
        actionStart(context, collectCode, "", requestCode);
    }

    public static void actionStart(Activity context, String collectCode, String scanCode, int
            requestCode) {
        Intent intent = new Intent(context, ClothingDeliverConfirmActivity.class);
        intent.putExtra("collectCode", collectCode);
        intent.putExtra("scanCode", scanCode);
        context.startActivityForResult(intent, requestCode);
    }


    AsyncHttpResponseHandler infoHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d(getDefaultTag(), "infoHandler result = " + responseString);
            dismissProgressDialog();
            try {
                ResultReceiveCollectInfo result = JSON.parseObject(responseString,
                        ResultReceiveCollectInfo.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    initView(result);
                } else {
                    PublicUtil.showErrorMsg(ClothingDeliverConfirmActivity.this, result);
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


    @Override
    public void onItemClick(Object item, View view, int position) {
        if (item instanceof ClothesInfo) {
            ClothesInfo info = (ClothesInfo) item;
            ClothesDetailActivity.actionStart(ClothingDeliverConfirmActivity.this, info
                    .getClothesCode());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }

        switch (requestCode) {
            case Constants.REQUEST_SCAN:
                // 扫码返回
                if (resultCode == Activity.RESULT_OK) {
                    String resultStr = data.getStringExtra(LibConstants.SCAN_RESULT);
                    handleScaneCodeBack(resultStr);
                }
                //   跳转到手动输入
                else if (resultCode == RESULT_CODE_MANUAL) {
                    Intent intent = new Intent(this, ManualInputCodeActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_MANUAL);
                }
                break;

            // 手动输入返回
            case REQUEST_CODE_MANUAL:
                // 数字码返回
                if (resultCode == Activity.RESULT_OK) {
                    String resultStr = data.getStringExtra(ManualInputCodeActivity
                            .RESULT_EXTRA_CODE);
                    handleScaneCodeBack(resultStr);
                }
                //  跳转到扫码输入
                else if (resultCode == ManualInputCodeActivity.RESULT_CODE_SCANE_CODE) {
                    goScanCode();
                }
                break;
        }
    }

    private void handleScaneCodeBack(String code) {
        if (null != clothesInfos && clothesInfos.size() > 0) {
            for (ClothesInfo info : clothesInfos
                    ) {
                if (info.getClothesCode().equals(code)) {
                    info.setCheck(true);
                }
                adapter.notifyDataSetChanged();
            }
        }
    }

    @OnClick(R.id.btn_ok)
    void confirm(View view) {
        if (null != clothesInfos && clothesInfos.size() > 0) {
            int checkNum = 0;
            for (ClothesInfo info : clothesInfos
                    ) {
                if (info.isCheck()) {
                    checkNum++;
                }
            }
            if (checkNum == clothesInfos.size()) {
                showProgressDialog();
                DeliveryApi.confirmOrderInfo(ClientStateManager.getLoginToken
                        (ClothingDeliverConfirmActivity.this), collectCode, baseHandler);
                return;
            }
        }
        PublicUtil.showToast(getString(R.string.alert_message_start_before_end));
    }

    @OnClick(R.id.btn_cancel)
    void cancel(View view) {
        setResult(Activity.RESULT_CANCELED);
        this.finish();
    }
}
