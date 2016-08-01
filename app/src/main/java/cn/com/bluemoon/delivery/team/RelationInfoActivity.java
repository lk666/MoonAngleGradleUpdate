package cn.com.bluemoon.delivery.team;

import android.content.Intent;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.team.Community;
import cn.com.bluemoon.delivery.app.api.model.team.EmpEdit;
import cn.com.bluemoon.delivery.app.api.model.team.RelationDetail;
import cn.com.bluemoon.delivery.app.api.model.team.ResultCommunityList;
import cn.com.bluemoon.delivery.app.api.model.team.ResultRelationDetail;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.ui.DateTextView;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.callback.CommonEditTextCallBack;
import cn.com.bluemoon.lib.utils.LibViewUtil;
import cn.com.bluemoon.lib.view.CommonClearEditText;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

public class RelationInfoActivity extends KJActivity {

    private String TAG = "RelationShipDetailActivity";
    @BindView(id = R.id.btn_ok, click = true)
    private Button btnOk;
    @BindView(id = R.id.txt_name)
    private TextView txtName;
    @BindView(id = R.id.txt_phone)
    private TextView txtPhone;
    @BindView(id = R.id.txt_type)
    private TextView txtType;
    @BindView(id = R.id.txt_community, click = true)
    private TextView txtCommunity;
    @BindView(id = R.id.txt_service)
    private TextView txtService;
    @BindView(id = R.id.txt_full, click = true)
    private TextView txtFull;
    @BindView(id = R.id.txt_part, click = true)
    private TextView txtPart;
    @BindView(id = R.id.txt_work_lengh)
    private CommonClearEditText txtWorkLengh;
    @BindView(id = R.id.txt_startdate)
    private DateTextView txtStartDate;
    @BindView(id = R.id.txt_enddate)
    private DateTextView txtEndDate;
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
    @BindView(id = R.id.img_right3)
    private View imgRight3;
    @BindView(id = R.id.layout_group)
    private LinearLayout layoutGroup;
    @BindView(id = R.id.layout_work_lengh)
    private LinearLayout layoutWorkLengh;
    private CommonProgressDialog progressDialog;
    private RelationDetail item;
    private EmpEdit empEdit;
    private List<Community> listCommunity;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_relation_info);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        if (getIntent() != null) {
            empEdit = (EmpEdit) getIntent().getSerializableExtra("empEdit");
        }
        if (empEdit == null) {
            PublicUtil.showToastErrorData();
            finish();
            return;
        }
        initCustomActionBar();
        progressDialog = new CommonProgressDialog(aty);
        txtStartDate.setCallBack(callback);
        txtEndDate.setCallBack(callback);
        txtStartDate.updateMinDate(DateUtil.getTimeOffsetMonth());
        txtPhone = PublicUtil.getPhoneView(aty, txtPhone);
        txtWorkLengh.setCallBack(new CommonEditTextCallBack() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                int index = s.toString().lastIndexOf(".");
                if (index != -1 && s.toString().length() > index + 2) {
                    txtWorkLengh.setText(s.toString().substring(0, index + 2));
                    txtWorkLengh.setSelection(txtWorkLengh.length());
                }
                if (!StringUtils.isEmpty(s.toString()) && Double.parseDouble(s.toString()) > 24) {
                    txtWorkLengh.setText("24.0");
                    txtWorkLengh.setSelection(txtWorkLengh.length());
                }
            }
        });
        if (item == null) {
            item = new RelationDetail();
        }
        item.setBpCode(empEdit.getGroupCode());
        item.setBpName(empEdit.getGroupName());
        item.setBpCode1(empEdit.getCommunityCode());
        item.setBpName1(empEdit.getCommunityName());
        item.setEmpCode(empEdit.getEmpCode());
        item.setEmpName(empEdit.getEmpName());
        item.setMobileNo(empEdit.getMobileNo());
        item.setRelType(empEdit.getRelType());
        txtName.setText(PublicUtil.getStringParams(item.getEmpCode(), item.getEmpName()));
        txtPhone.setText(item.getMobileNo());
        if (Constants.TYPE_ADD.equals(empEdit.getType())) {
            if (Constants.RELTYPE_GROUP.equals(item.getRelType())) {
                line1.setVisibility(View.VISIBLE);
                layoutGroup.setVisibility(View.VISIBLE);
                imgRight1.setVisibility(View.GONE);
                imgRight2.setVisibility(View.GONE);
                txtCommunity.setClickable(false);
                txtService.setClickable(false);
                txtType.setText(getString(R.string.team_group));
                txtCommunity.setText(PublicUtil.getStringParams(item.getBpCode1(), item
                        .getBpName1()));
                txtService.setText(PublicUtil.getStringParams(item.getBpCode(), item.getBpName()));
            } else if (Constants.RELTYPE_COMMUNITY.equals(item.getRelType())) {
                txtType.setText(getString(R.string.team_community));
                getCommunity();
            }
        } else if (Constants.TYPE_UPDATE.equals(empEdit.getType())) {
            imgRight1.setVisibility(View.GONE);
            imgRight2.setVisibility(View.GONE);
            txtCommunity.setClickable(false);
            txtService.setClickable(false);
            String bpCode = item.getBpCode();
            if (Constants.RELTYPE_COMMUNITY.equals(item.getRelType())) {
                bpCode = item.getBpCode1();
            }
            if (progressDialog != null) {
                progressDialog.show();
            }
            DeliveryApi.getRelationShipDetail(aty, bpCode, item.getEmpCode(),
                    ClientStateManager.getLoginToken(aty), 0, getRelationShipDetailHandler);
        }
    }

    DateTextView.DateTextViewCallBack callback = new DateTextView.DateTextViewCallBack() {
        @Override
        public void onDate(View view, long date) {
            if (view == txtStartDate) {
                item.setStartDate(date);
                txtEndDate.updateMinDate(date);
            } else if (view == txtEndDate) {
                item.setEndDate(date);
            }

        }
    };

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);
        LibViewUtil.hideIM(v);
        if (v == txtCommunity) {
            if (listCommunity == null) {
                getCommunity();
            } else {
                openSelectView(listCommunity);
            }
        } else if (v == txtFull) {
            setWorkType(true);
        } else if (v == txtPart) {
            setWorkType(false);
        } else if (v == btnOk) {
            submit();
        }
    }

    private void openSelectView(List<Community> lists) {
        if (lists == null) return;
        ArrayList<String> list = new ArrayList<>();
        for (Community i : lists) {
            list.add(PublicUtil.getStringParams(i.getBpCode(), i.getBpName()));
        }
        Intent intent = new Intent(aty, CommonSelectActivity.class);
        intent.putExtra("title", getString(R.string.team_member_detail_community_select));
        intent.putStringArrayListExtra("list", list);
        startActivityForResult(intent, 1);
    }

    private void setData() {
        if (item == null) return;
        txtName.setText(PublicUtil.getStringParams(item.getEmpCode(), item.getEmpName()));
        if (Constants.RELTYPE_GROUP.equals(item.getRelType())) {
            line1.setVisibility(View.VISIBLE);
            layoutGroup.setVisibility(View.VISIBLE);
            txtType.setText(getString(R.string.team_group));
        } else {
            txtType.setText(getString(R.string.team_community));
        }
        if (!item.isEdit) {
            txtStartDate.setClickable(false);
            imgRight3.setVisibility(View.INVISIBLE);
        }
        txtCommunity.setText(PublicUtil.getStringParams(item.getBpCode1(), item.getBpName1()));
        if (StringUtils.isEmpty(item.getChargeName())) {
            txtService.setText(PublicUtil.getStringParams(item.getBpCode(), item.getBpName()));
        } else {
            txtService.setText(PublicUtil.getStringParams(item.getBpCode(), item.getBpName(),
                    item.getChargeName()));
        }
        if (Constants.WORKTYPE_PART.equals(item.getWorkType())) {
            setWorkType(false);
            txtWorkLengh.setHint("");
            txtWorkLengh.setText(String.valueOf(item.getWorkLength()));
            txtWorkLengh.setHint(getString(R.string.team_work_part_hint));
            txtWorkLengh.updateCleanable(0, false);
        } else {
            setWorkType(true);
        }
        txtStartDate.setText(DateUtil.getTime(item.getStartDate()));
        txtEndDate.setText(DateUtil.getTime(item.getEndDate()));
        txtEndDate.updateMinDate(item.getStartDate());
        txtRemark.setText(item.getRemark());
        txtRemark.updateCleanable(0, false);
    }


    private void submit() {
        if (item.getStartDate() == 0) {
            PublicUtil.showToast(getString(R.string.team_edit_limit_startdate));
            return;
        }
        if (item.isEdit && item.getStartDate() < DateUtil.getTimeOffsetMonth()) {
            PublicUtil.showToast(getString(R.string.team_edit_limit_startdate_month));
            return;
        }
        if (item.getEndDate() > 0 && item.getStartDate() > item.getEndDate()) {
            PublicUtil.showToast(getString(R.string.team_edit_limit_enddate));
            return;
        }
        if (StringUtils.isEmpty(item.getWorkType())) {
            PublicUtil.showToast(getString(R.string.team_edit_limit_worktype));
            return;
        }
        if (Constants.WORKTYPE_PART.equals(item.getWorkType())
                && (StringUtils.isEmpty(txtWorkLengh.getText().toString())
                || "0".equals(txtWorkLengh.getText().toString()))) {
            PublicUtil.showToast(getString(R.string.team_edit_limit_parttime));
            return;
        }
        if (Constants.WORKTYPE_FULL.equals(item.getWorkType())) {
            txtWorkLengh.setText("0");
        }
        if (item.getEndDate() == 0) {
            item.setEndDate(Constants.LARGETIME);
        }
        item.setWorkLength(Double.parseDouble(txtWorkLengh.getText().toString()));
        item.setRemark(txtRemark.getText().toString());

        if (progressDialog != null) progressDialog.show();
        DeliveryApi.addRelationShip(item, ClientStateManager.getLoginToken(aty),
                empEdit.getType(), addRelationShipHandler);
    }

    private void setWorkType(boolean isFull) {
        if (item == null) {
            item = new RelationDetail();
        }
        if (isFull) {
            txtFull.setTextColor(getResources().getColor(R.color.text_red));
            txtPart.setTextColor(getResources().getColor(R.color.text_black_light));
            txtFull.setBackgroundResource(R.drawable.btn_border_red_shape4);
            txtPart.setBackgroundResource(R.drawable.btn_border_grep_shape4);
            item.setWorkType(Constants.WORKTYPE_FULL);
            if (layoutWorkLengh.getVisibility() == View.VISIBLE) {
                line2.setVisibility(View.GONE);
                layoutWorkLengh.setVisibility(View.GONE);
            }
        } else {
            txtPart.setTextColor(getResources().getColor(R.color.text_red));
            txtFull.setTextColor(getResources().getColor(R.color.text_black_light));
            txtPart.setBackgroundResource(R.drawable.btn_border_red_shape4);
            txtFull.setBackgroundResource(R.drawable.btn_border_grep_shape4);
            item.setWorkType(Constants.WORKTYPE_PART);
            if (layoutWorkLengh.getVisibility() == View.GONE) {
                line2.setVisibility(View.VISIBLE);
                layoutWorkLengh.setVisibility(View.VISIBLE);
            }
        }
    }

    private void getCommunity() {
        if (progressDialog != null) {
            progressDialog.show();
        }
        DeliveryApi.getCommunityList(ClientStateManager.getLoginToken(aty),
                getCommunityListHandler);
    }

    private void setCommunity(Community community) {
        item.setBpCode1(community.getBpCode());
        item.setBpName1(community.getBpName());
        txtCommunity.setText(PublicUtil.getStringParams(item.getBpCode1(), item.getBpName1()));
    }

    AsyncHttpResponseHandler getRelationShipDetailHandler = new TextHttpResponseHandler(HTTP
            .UTF_8) {
        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d(TAG, "getRelationShipDetailHandler result = " + responseString);
            if (progressDialog != null)
                progressDialog.dismiss();
            try {
                ResultRelationDetail result = JSON.parseObject(responseString,
                        ResultRelationDetail.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    item = result.getRelationDetail();
                    setData();
                } else {
                    PublicUtil.showErrorMsg(aty, result);
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

    AsyncHttpResponseHandler addRelationShipHandler = new TextHttpResponseHandler(HTTP.UTF_8) {
        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d(TAG, "addRelationShipHandler result = " + responseString);
            if (progressDialog != null)
                progressDialog.dismiss();
            try {
                ResultBase baseResult = JSON.parseObject(responseString, ResultBase.class);
                if (baseResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    PublicUtil.showToast(baseResult.getResponseMsg());
                    setResult(RESULT_OK);
                    finish();
                } else {
                    PublicUtil.showErrorMsg(aty, baseResult);
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

    AsyncHttpResponseHandler getCommunityListHandler = new TextHttpResponseHandler(HTTP.UTF_8) {
        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d(TAG, "getCommunityListHandler result = " + responseString);
            if (progressDialog != null)
                progressDialog.dismiss();
            try {
                ResultCommunityList result = JSON.parseObject(responseString, ResultCommunityList
                        .class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    listCommunity = result.getItemList();
                    if (listCommunity.size() == 1) {
                        setCommunity(listCommunity.get(0));
                    }
                } else {
                    PublicUtil.showErrorMsg(aty, result);
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
                if (Constants.TYPE_ADD.equals(empEdit.getType())) {
                    v.setText(getText(R.string.team_member_add_title));
                } else {
                    v.setText(getText(R.string.team_member_edit_title));
                }

            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.hasExtra("index")) {
            int index = data.getIntExtra("index", 0);
            switch (requestCode) {
                case 1:
                    setCommunity(listCommunity.get(index));
                    break;
            }
        }
    }
}
