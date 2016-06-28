package cn.com.bluemoon.delivery.team;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
import cn.com.bluemoon.lib.utils.LibViewUtil;
import cn.com.bluemoon.lib.view.CommonClearEditText;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

public class RelationInfoActivity extends KJActivity {

    private String TAG = "RelationShipDetailActivity";
    @BindView(id = R.id.btn_ok,click = true)
    private Button btnOk;
    @BindView(id = R.id.txt_name)
    private TextView txtName;
    @BindView(id = R.id.txt_phone)
    private TextView txtPhone;
    @BindView(id = R.id.txt_type)
    private TextView txtType;
    @BindView(id = R.id.txt_community,click = true)
    private TextView txtCommunity;
    @BindView(id = R.id.txt_service,click = true)
    private TextView txtService;
    @BindView(id = R.id.txt_full,click = true)
    private TextView txtFull;
    @BindView(id = R.id.txt_part,click = true)
    private TextView txtPart;
    @BindView(id = R.id.txt_work_lengh)
    private CommonClearEditText txtWorkLengh;
    @BindView(id = R.id.txt_startdate,click = true)
    private TextView txtStartDate;
    @BindView(id = R.id.txt_enddate,click = true)
    private TextView txtEndDate;
    @BindView(id = R.id.txt_remark)
    private CommonClearEditText txtRemark;
    @BindView(id = R.id.line1)
    private View line1;
    @BindView(id = R.id.line2)
    private View line2;
    @BindView(id = R.id.img_right1)
    private View imgRight1;
    @BindView(id = R.id.img_right2)
    private View imgRight2;
    @BindView(id = R.id.layout_group)
    private LinearLayout layoutGroup;
    @BindView(id = R.id.layout_work_lengh)
    private LinearLayout layoutWorkLengh;
    private boolean mode = true;
    private CommonProgressDialog progressDialog;
    private RelationDetail item;


    @Override
    public void setRootView() {
        setContentView(R.layout.activity_relation_info);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        if(getIntent()!=null){
            mode = getIntent().getBooleanExtra("mode",true);
        }
        initCustomActionBar();
        progressDialog = new CommonProgressDialog(aty);
        if(item==null){
            item = new RelationDetail();
        }
        item.setEmpCode(getIntent().getStringExtra("empCode"));
        if(mode){
            item.setRelType(getIntent().getStringExtra("relType"));
            txtName.setText(item.getEmpCode());
            if ("group".equals(item.getRelType())) {
                line1.setVisibility(View.VISIBLE);
                layoutGroup.setVisibility(View.VISIBLE);
                txtType.setText(getString(R.string.team_group));
            } else {
                txtType.setText(getString(R.string.team_community));
            }

        }else{
            imgRight1.setVisibility(View.GONE);
            imgRight2.setVisibility(View.GONE);
            txtCommunity.setClickable(false);
            txtService.setClickable(false);
            item.setBpCode(getIntent().getStringExtra("bpCode"));
            if (progressDialog != null) {
                progressDialog.show();
            }
            DeliveryApi.getRelationShipDetail(item.getBpCode(), item.getEmpCode(), ClientStateManager.getLoginToken(aty), getRelationShipDetailHandler);
        }
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);
        LibViewUtil.hideIM(v);
        if(v==txtCommunity){
            PublicUtil.showToast(item.getBpName1());
        }else if(v == txtService){
            PublicUtil.showToast(item.getBpName());
        }else if(v == txtFull){
            setWorkType(true);
        }else if(v == txtPart){
            setWorkType(false);
        }else if(v == txtStartDate){

        }else if(v == txtEndDate){

        }else if(v == btnOk){

        }
    }

    private void setData() {
        if(item==null) return;
        txtName.setText(PublicUtil.getString2(item.getEmpCode(), item.getEmpName()));
        if ("group".equals(item.getRelType())) {
            line1.setVisibility(View.VISIBLE);
            layoutGroup.setVisibility(View.VISIBLE);
            txtType.setText(getString(R.string.team_group));
        } else {
            txtType.setText(getString(R.string.team_community));
        }
        txtCommunity.setText(PublicUtil.getString2(item.getBpCode1(), item.getBpName1()));
        if (StringUtils.isEmpty(item.getChargeName())) {
            txtService.setText(PublicUtil.getString2(item.getBpCode(), item.getBpName()));
        } else {
            txtService.setText(PublicUtil.getString2(item.getBpCode(),
                    PublicUtil.getString2(item.getBpName(), item.getChargeName())));
        }
        if("partTime".equals(item.getWorkType())){
            setWorkType(false);
            txtWorkLengh.setHint("");
            txtWorkLengh.setText(String.valueOf(item.getWorkLength()));
            txtWorkLengh.setHint(getString(R.string.team_work_part_hint));
            txtWorkLengh.updateCleanable(0,false);
        }else{
            setWorkType(true);
        }
        txtStartDate.setText(DateUtil.getTime(item.getStartDate(), "yyyy-MM-dd"));
        txtEndDate.setText(DateUtil.getTime(item.getEndDate(), "yyyy-MM-dd"));
        txtRemark.setText(item.getRemark());
        txtRemark.updateCleanable(0, false);
    }

    private void setWorkType(boolean isFull){
        if(item==null){
            item = new RelationDetail();
        }
        if(isFull){
            txtFull.setTextColor(getResources().getColor(R.color.text_red));
            txtPart.setTextColor(getResources().getColor(R.color.text_black_light));
            txtFull.setBackgroundResource(R.drawable.btn_border_red_shape4);
            txtPart.setBackgroundResource(R.drawable.btn_border_grep_shape4);
            item.setWorkType("fullTime");
            if(layoutWorkLengh.getVisibility()==View.VISIBLE){
                line2.setVisibility(View.GONE);
                layoutWorkLengh.setVisibility(View.GONE);
            }
        }else{
            txtPart.setTextColor(getResources().getColor(R.color.text_red));
            txtFull.setTextColor(getResources().getColor(R.color.text_black_light));
            txtPart.setBackgroundResource(R.drawable.btn_border_red_shape4);
            txtFull.setBackgroundResource(R.drawable.btn_border_grep_shape4);
            item.setWorkType("partTime");
            if(layoutWorkLengh.getVisibility()==View.GONE){
                line2.setVisibility(View.VISIBLE);
                layoutWorkLengh.setVisibility(View.VISIBLE);
            }
        }
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
                    item = relationDetailResult.getRelationDetail();
                    setData();
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
                if(mode){
                    v.setText(getText(R.string.team_member_add_title));
                }else{
                    v.setText(getText(R.string.team_member_edit_title));
                }

            }

        });

    }
}
