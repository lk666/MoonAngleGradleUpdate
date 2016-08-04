package cn.com.bluemoon.delivery.module.team;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.kymjs.kjframe.utils.StringUtils;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.team.RelationDetail;
import cn.com.bluemoon.delivery.app.api.model.team.ResultRelationDetail;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.utils.LibViewUtil;

public class RelationShipDetailActivity extends BaseActivity {

    @Bind(R.id.txt_name)
    TextView txtName;
    @Bind(R.id.txt_phone)
    TextView txtPhone;
    @Bind(R.id.txt_type)
    TextView txtType;
    @Bind(R.id.txt_community)
    TextView txtCommunity;
    @Bind(R.id.line2)
    View line2;
    @Bind(R.id.txt_service)
    TextView txtService;
    @Bind(R.id.txt_work_type)
    TextView txtWorkType;
    @Bind(R.id.txt_work_lengh)
    TextView txtWorkLengh;
    @Bind(R.id.layout_work_lengh)
    RelativeLayout layoutWorkLengh;
    @Bind(R.id.layout_work_type)
    RelativeLayout layoutWorkType;
    @Bind(R.id.line)
    View line;
    @Bind(R.id.txt_start_date)
    TextView txtStartDate;
    @Bind(R.id.txt_end_date)
    TextView txtEndDate;
    @Bind(R.id.txt_remark)
    TextView txtRemark;
    @Bind(R.id.layout_remark)
    RelativeLayout layoutRemark;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_relation_ship_detail;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.team_member_detail_title);
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        ResultRelationDetail relationDetailResult = JSON.parseObject(jsonString, ResultRelationDetail.class);
        setData(relationDetailResult.getRelationDetail());
    }

    @Override
    public void initView() {
        txtPhone = PublicUtil.getPhoneView(this, txtPhone);
    }

    @Override
    public void initData() {
        String bpCode = getIntent().getStringExtra("bpCode");
        String empCode = getIntent().getStringExtra("empCode");
        showWaitDialog();
        DeliveryApi.getRelationShipDetail(0,bpCode, empCode, ClientStateManager.getLoginToken(this),getNewHandler(0, ResultBase.class));
    }

    public static void actionStart(Context context,String bpCode,String empCode){
        Intent intent = new Intent();
        intent.setClass(context,RelationShipDetailActivity.class);
        intent.putExtra("bpCode", bpCode);
        intent.putExtra("empCode",empCode);
        context.startActivity(intent);
    }

    private void setData(RelationDetail item) {
        txtName.setText(PublicUtil.getStringParams(item.getEmpCode(), item.getEmpName()));
        txtPhone.setText(item.getMobileNo());
        if (Constants.RELTYPE_COMMUNITY.equals(item.getRelType())) {
            txtType.setText(getString(R.string.team_community));
        } else if (Constants.RELTYPE_GROUP.equals(item.getRelType())) {
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

        if (Constants.WORKTYPE_FULL.equals(item.getWorkType())) {
            LibViewUtil.setViewVisibility(layoutWorkType, View.VISIBLE);
            LibViewUtil.setViewVisibility(line, View.VISIBLE);
            txtWorkType.setText(getString(R.string.team_work_full));
        } else if (Constants.WORKTYPE_PART.equals(item.getWorkType())) {
            LibViewUtil.setViewVisibility(layoutWorkType, View.VISIBLE);
            LibViewUtil.setViewVisibility(line, View.VISIBLE);
            txtWorkType.setText(getString(R.string.team_work_part));
            layoutWorkLengh.setVisibility(View.VISIBLE);
            txtWorkLengh.setText(String.valueOf(item.getWorkLength()));
        }
        txtStartDate.setText(DateUtil.getTime(item.getStartDate()));
        txtEndDate.setText(DateUtil.getTime(item.getEndDate()));
        if (!StringUtils.isEmpty(item.getRemark())) {
            layoutRemark.setVisibility(View.VISIBLE);
            txtRemark.setText(item.getRemark());
        }

    }

}
