package cn.com.bluemoon.delivery.module.team;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.kymjs.kjframe.utils.StringUtils;

import butterknife.BindView;
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
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.lib.utils.LibViewUtil;

public class RelationShipDetailActivity extends BaseActivity {

    @BindView(R.id.txt_name)
    TextView txtName;
    @BindView(R.id.txt_phone)
    TextView txtPhone;
    @BindView(R.id.txt_type)
    TextView txtType;
    @BindView(R.id.txt_community)
    TextView txtCommunity;
    @BindView(R.id.line2)
    View line2;
    @BindView(R.id.txt_service)
    TextView txtService;
    @BindView(R.id.txt_work_type)
    TextView txtWorkType;
    @BindView(R.id.txt_work_lengh)
    TextView txtWorkLengh;
    @BindView(R.id.layout_work_lengh)
    RelativeLayout layoutWorkLengh;
    @BindView(R.id.layout_work_type)
    RelativeLayout layoutWorkType;
    @BindView(R.id.line)
    View line;
    @BindView(R.id.txt_start_date)
    TextView txtStartDate;
    @BindView(R.id.txt_end_date)
    TextView txtEndDate;
    @BindView(R.id.txt_remark)
    TextView txtRemark;
    @BindView(R.id.layout_remark)
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
        setData(((ResultRelationDetail) result).getRelationDetail());
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
        DeliveryApi.getRelationShipDetail(bpCode, empCode, ClientStateManager.getLoginToken(), getNewHandler(0, ResultRelationDetail.class));
    }

    public static void actionStart(Context context, String bpCode, String empCode) {
        Intent intent = new Intent();
        intent.setClass(context, RelationShipDetailActivity.class);
        intent.putExtra("bpCode", bpCode);
        intent.putExtra("empCode", empCode);
        context.startActivity(intent);
    }

    private void setData(RelationDetail item) {
        txtName.setText(StringUtil.getStringParams(item.getEmpCode(), item.getEmpName()));
        txtPhone.setText(item.getMobileNo());
        if (Constants.RELTYPE_COMMUNITY.equals(item.getRelType())) {
            txtType.setText(getString(R.string.team_community));
        } else if (Constants.RELTYPE_GROUP.equals(item.getRelType())) {
            txtType.setText(getString(R.string.team_group));
            line2.setVisibility(View.VISIBLE);
            txtService.setVisibility(View.VISIBLE);
            if (StringUtils.isEmpty(item.getChargeName())) {
                txtService.setText(StringUtil.getStringParams(item.getBpCode(), item.getBpName()));
            } else {
                txtService.setText(StringUtil.getStringParams(item.getBpCode(), item.getBpName(), item.getChargeName()));
            }
        }
        txtCommunity.setText(StringUtil.getStringParams(item.getBpCode1(), item.getBpName1()));

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
