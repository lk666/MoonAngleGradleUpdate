package cn.com.bluemoon.delivery.module.clothing.collect.withorder;

import android.content.Context;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.clothing.ResultClothesDeliverInfos;
import cn.com.bluemoon.delivery.app.api.model.clothing.ResultReceiveCollectInfo;
import cn.com.bluemoon.delivery.app.api.model.clothing.ResultUserInfo;
import cn.com.bluemoon.delivery.module.base.BaseActionBarActivity;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.clothing.collect.ClothesInfoAdapter;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;

/**
 * Created by allenli on 2016/6/23.
 */
public class ClothingDeliverConfirmActivity extends BaseActionBarActivity  implements OnListItemClickListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothing_deliver_confirm);
        collectCode = getIntent().getStringExtra("collectCode");
        ButterKnife.bind(this);
        init();
    }

    @Override
    protected int getActionBarTitleRes() {
        return R.string.title_clothing_deliver_confirm;
    }

    private void init() {
        showProgressDialog();
        DeliveryApi.receiveCollectInfo(ClientStateManager.getLoginToken(ClothingDeliverConfirmActivity.this), collectCode, infoHandler);
    }

    private void initView(ResultReceiveCollectInfo result){
        txtDeliverName.setText(String.format("%s %s",result.getTransmitName(),result.getTransmitCode()));
        txtDeliverPhone.setText(result.getTransmitPhone());
        txtActual.setText(String.valueOf(result.getActualCount()));
        txtCollectNum.setText(result.getCollectCode());
        txtScanCode.setText(result.getCollectBrcode());
        txtUrgent.setVisibility(result.getIsUrgent()>0? View.VISIBLE:View.GONE);

       adapter = new ClothesInfoAdapter(this,this);
       adapter.setList(result.getClothesInfo());
        listViewInfo.setAdapter(adapter);

    }

    public static void actionStart(Context context, String collectCode) {
        Intent intent = new Intent(context, ClothingDeliverConfirmActivity.class);
        intent.putExtra("collectCode", collectCode);
        context.startActivity(intent);
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
//        CollectInfo order = (CollectInfo) item;
//        if (order == null) {
//            return;
//        }
        switch (view.getId()) {
            case R.id.layout_detail:

                break;
        }
    }
}
