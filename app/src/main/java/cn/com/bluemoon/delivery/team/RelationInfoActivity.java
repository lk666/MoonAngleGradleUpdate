package cn.com.bluemoon.delivery.team;

import android.content.Intent;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.team.Community;
import cn.com.bluemoon.delivery.app.api.model.team.Emp;
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
import cn.com.bluemoon.lib.view.CommonDatePickerDialog;
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
    @BindView(id = R.id.txt_service, click = true)
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
    @BindView(id = R.id.layout_group)
    private LinearLayout layoutGroup;
    @BindView(id = R.id.layout_work_lengh)
    private LinearLayout layoutWorkLengh;
    private CommonProgressDialog progressDialog;
    private RelationDetail item;
    private Emp emp;
    private String type;
    private List<Community> listCommunity;
    private List<Community> listGroup;
    private boolean isCommunity;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_relation_info);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        String relType = Constants.RELTYPE_COMMUNITY;
        if (getIntent() != null) {
            type = getIntent().getStringExtra("type");
            emp = (Emp) getIntent().getSerializableExtra("emp");
            relType = getIntent().getStringExtra("relType");
        }
        initCustomActionBar();
        if (emp == null) return;
        progressDialog = new CommonProgressDialog(aty);
        txtStartDate.setCallBack(callback);
        txtEndDate.setCallBack(callback);
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
            }
        });
        if (item == null) {
            item = new RelationDetail();
        }
        item.setBpCode(emp.getBpCode());
        item.setEmpCode(emp.getEmpCode());
        item.setEmpName(emp.getEmpName());
        item.setMobileNo(emp.getMobileNo());
        item.setRelType(relType);
        txtName.setText(PublicUtil.getString2(item.getEmpCode(), item.getEmpName()));
        txtPhone.setText(item.getMobileNo());
        if (Constants.TYPE_ADD.equals(type)) {
            if (Constants.RELTYPE_GROUP.equals(relType)) {
                line1.setVisibility(View.VISIBLE);
                layoutGroup.setVisibility(View.VISIBLE);
                txtType.setText(getString(R.string.team_group));
            } else {
                txtType.setText(getString(R.string.team_community));
            }
            getCommunity();
            item.setWorkType(Constants.WORKTYPE_FULL);
        } else if (Constants.TYPE_UPDATE.equals(type)) {
            imgRight1.setVisibility(View.GONE);
            imgRight2.setVisibility(View.GONE);
            txtCommunity.setClickable(false);
            txtService.setClickable(false);
            if (progressDialog != null) {
                progressDialog.show();
            }
            DeliveryApi.getRelationShipDetail(item.getBpCode(), item.getEmpCode(), ClientStateManager.getLoginToken(aty), getRelationShipDetailHandler);
        }
    }

    DateTextView.DateTextViewCallBack callback = new DateTextView.DateTextViewCallBack() {
        @Override
        public void onDate(View view, long date) {
            if (view == txtStartDate) {
                item.setStartDate(date);
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
                openSelectView(listCommunity, getString(R.string.team_member_detail_community_select), 1);
            }
        } else if (v == txtService) {
            if (listGroup == null) {
                getGroup();
            } else {
                openSelectView(listGroup, getString(R.string.team_member_detail_group_select), 2);
            }
        } else if (v == txtFull) {
            setWorkType(true);
        } else if (v == txtPart) {
            setWorkType(false);
        } else if (v == btnOk) {
            submit();
        }
    }

    private void openSelectView(List<Community> lists, String title, int requestCode) {
        if (lists == null) return;
        ArrayList<String> list = new ArrayList<>();
        for (Community i : lists) {
            list.add(PublicUtil.getString2(i.getBpCode(), i.getBpName()));
        }
        Intent intent = new Intent(aty, CommonSelectActivity.class);
        intent.putExtra("title", title);
        intent.putStringArrayListExtra("list", list);
        startActivityForResult(intent, requestCode);
    }

    private void setData() {
        if (item == null) return;
        txtName.setText(PublicUtil.getString2(item.getEmpCode(), item.getEmpName()));
        if (Constants.RELTYPE_GROUP.equals(item.getRelType())) {
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
        if (Constants.WORKTYPE_PART.equals(item.getWorkType())) {
            setWorkType(false);
            txtWorkLengh.setHint("");
            txtWorkLengh.setText(String.valueOf(item.getWorkLength()));
            txtWorkLengh.setHint(getString(R.string.team_work_part_hint));
            txtWorkLengh.updateCleanable(0, false);
        } else {
            setWorkType(true);
        }
        txtStartDate.setText(DateUtil.getTime(item.getStartDate(), "yyyy-MM-dd"));
        txtEndDate.setText(DateUtil.getTime(item.getEndDate(), "yyyy-MM-dd"));
        txtRemark.setText(item.getRemark());
        txtRemark.updateCleanable(0, false);
    }

    private void submit() {
        if (StringUtils.isEmpty(item.getBpCode1())) {
            PublicUtil.showToast("社区不能为空");
            return;
        }
        if (Constants.RELTYPE_GROUP.equals(item.getRelType()) && StringUtils.isEmpty(item.getBpCode())) {
            PublicUtil.showToast("小组不能为空");
            return;
        }
        if (item.getStartDate() == 0) {
            PublicUtil.showToast("开始时间不能为空");
            return;
        }
        if (StringUtils.isEmpty(item.getWorkType())) {
            PublicUtil.showToast("作业性质不能为空");
            return;
        }
        if (Constants.WORKTYPE_PART.equals(item.getWorkType()) && (txtWorkLengh.getText().toString().length() == 0)) {
            PublicUtil.showToast("兼职时长不能为空");
            return;
        } else if (Constants.WORKTYPE_FULL.equals(item.getWorkType())) {
            txtWorkLengh.setText("0");
        }
        if (item.getEndDate() == 0) {
            item.setEndDate(Constants.LARGETIME);
        }
        item.setWorkLength(Double.parseDouble(txtWorkLengh.getText().toString()));
        item.setRemark(txtRemark.getText().toString());

        if (progressDialog != null) progressDialog.show();
        DeliveryApi.addRelationShip(item.getBpCode1(), item.getBpName1(), item.getEmpCode(),
                item.getEmpName(), item.getEndDate(), item.getBpCode(), item.getBpName(), item.getRelType(),
                item.getRemark(), item.getStartDate(), ClientStateManager.getLoginToken(aty), type,
                item.getWorkLength(), item.getWorkType(), addRelationShipHandler);
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
        isCommunity = true;
        DeliveryApi.getCommunityList(ClientStateManager.getLoginToken(aty), getCommunityListHandler);
    }

    private void getGroup() {
        if (StringUtils.isEmpty(item.getBpCode1())) {
            PublicUtil.showToast(getString(R.string.team_member_detail_community_hint));
            return;
        }
        if (progressDialog != null) progressDialog.show();
        isCommunity = false;
        DeliveryApi.getGroupListByCommunity(item.getBpCode1(),
                ClientStateManager.getLoginToken(aty), getCommunityListHandler);
    }

    private void setCommunity(Community community) {
        item.setBpCode1(community.getBpCode());
        item.setBpName1(community.getBpName());
        txtCommunity.setText(PublicUtil.getString2(item.getBpCode1(), item.getBpName1()));
        getGroup();
    }

    private void setGroup(Community community) {
        item.setBpCode(community.getBpCode());
        item.setBpName(community.getBpName());
        txtService.setText(PublicUtil.getString2(item.getBpCode(), item.getBpName()));
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
                ResultCommunityList result = JSON.parseObject(responseString, ResultCommunityList.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    if (isCommunity) {
                        listCommunity = result.getItemList();
                        if (listCommunity.size() == 1) {
                            setCommunity(listCommunity.get(0));
                        }
                    } else {
                        listGroup = result.getItemList();
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
                if (Constants.TYPE_ADD.equals(type)) {
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
                case 2:
                    setGroup(listGroup.get(index));
                    break;
            }
        }
    }
}
