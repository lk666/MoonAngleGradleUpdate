package cn.com.bluemoon.delivery.team;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;
import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.utils.StringUtils;

import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.team.RelationDetail;
import cn.com.bluemoon.delivery.app.api.model.team.ResultRelationDetail;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

public class RelationShipDetailActivity extends KJActivity {

    private String TAG = "RelationShipDetailActivity";
    @BindView(id = R.id.txt_name)
    private TextView txtName;
    @BindView(id = R.id.txt_phone)
    private TextView txtPhone;
    @BindView(id = R.id.txt_type)
    private TextView txtType;
    @BindView(id = R.id.txt_community)
    private TextView txtCommunity;
    @BindView(id = R.id.txt_service)
    private TextView txtService;
    @BindView(id = R.id.txt_work_type)
    private TextView txtWorkType;
    @BindView(id = R.id.txt_work_lengh)
    private TextView txtWorkLengh;
    @BindView(id = R.id.txt_startdate)
    private TextView txtStartDate;
    @BindView(id = R.id.txt_enddate)
    private TextView txtEndDate;
    @BindView(id = R.id.txt_remark)
    private TextView txtRemark;
    @BindView(id = R.id.line2)
    private View line2;
    @BindView(id = R.id.layout_work_lengh)
    private RelativeLayout layoutWorkLengh;
    private CommonProgressDialog progressDialog;

    @Override
    public void setRootView() {
        initCustomActionBar();
        setContentView(R.layout.activity_relation_ship_detail);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        progressDialog = new CommonProgressDialog(aty);
        txtPhone = PublicUtil.getPhoneView(aty, txtPhone);

        String  bpCode = getIntent().getStringExtra("bpCode");
        String  empCode = getIntent().getStringExtra("empCode");
        if (progressDialog != null) {
            progressDialog.show();
        }
        DeliveryApi.getRelationShipDetail(bpCode,empCode, ClientStateManager.getLoginToken(aty),getRelationShipDetailHandler);

    }

    private void setData(RelationDetail item) {
        txtName.setText(PublicUtil.getStringParams(item.getEmpCode(), item.getEmpName()));
        txtPhone.setText(item.getMobileNo());
        if (Constants.RELTYPE_COMMUNITY.equals(item.getRelType())) {
            txtType.setText(getString(R.string.team_community));
        } else if(Constants.RELTYPE_GROUP.equals(item.getRelType())){
            txtType.setText(getString(R.string.team_group));
            line2.setVisibility(View.VISIBLE);
            txtService.setVisibility(View.VISIBLE);
            if (StringUtils.isEmpty(item.getChargeName())) {
                txtService.setText(PublicUtil.getStringParams(item.getBpCode(), item.getBpName()));
            } else {
                txtService.setText(PublicUtil.getStringParams(item.getBpCode(), item.getBpName(), item.getChargeName()));
            }
        }
        txtCommunity.setText(PublicUtil.getStringParams(item.getBpCode1(), item.getBpName1()));

        if(Constants.WORKTYPE_FULL.equals(item.getWorkType())){
            txtWorkType.setText(getString(R.string.team_work_full));
        }else if(Constants.WORKTYPE_PART.equals(item.getWorkType())){
            txtWorkType.setText(getString(R.string.team_work_part));
            layoutWorkLengh.setVisibility(View.VISIBLE);
            txtWorkLengh.setText(String.valueOf(item.getWorkLength()));
        }
        txtStartDate.setText(DateUtil.getTime(item.getStartDate(),"yyyy-MM-dd"));
        txtEndDate.setText(DateUtil.getTime(item.getEndDate(),"yyyy-MM-dd"));
        txtRemark.setText(item.getRemark());
    }

    AsyncHttpResponseHandler getRelationShipDetailHandler = new TextHttpResponseHandler(HTTP.UTF_8) {
        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d(TAG, "getRelationShipDetailHandler result = " + responseString);
            if (progressDialog != null)
                progressDialog.dismiss();
            try {
                ResultRelationDetail relationDetailResult = JSON.parseObject(responseString, ResultRelationDetail.class);
                if (relationDetailResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    setData(relationDetailResult.getRelationDetail());
                } else {
                    PublicUtil.showErrorMsg(aty, relationDetailResult);
                }
            } catch (Exception e) {
                LogUtils.e(TAG, e.getMessage());
                PublicUtil.showToastServerBusy();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString,
                              Throwable throwable) {
            LogUtils.e(TAG, throwable.getMessage());
            if (progressDialog != null)
                progressDialog.dismiss();
            PublicUtil.showToastServerOvertime();
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
    }

    private void initCustomActionBar() {
        new CommonActionBar(aty.getActionBar(), new IActionBarListener() {

            @Override
            public void onBtnRight(View v) {
            }

            @Override
            public void onBtnLeft(View v) {
                finish();
            }

            @Override
            public void setTitle(TextView v) {
                v.setText(getText(R.string.team_member_detail_title));
            }

        });

    }

}
