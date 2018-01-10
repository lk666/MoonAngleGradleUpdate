package cn.com.bluemoon.delivery.module.contract;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.bluemoon.signature.lib.AbstractSignatureActivity;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ContractApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.contract.ResultCheckPersonReal;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.WithContextTextHttpResponseHandler;
import cn.com.bluemoon.delivery.utils.FileUtil;
import cn.com.bluemoon.lib_widget.module.form.BMAngleBtn1View;
import cn.com.bluemoon.lib_widget.module.form.BmCellTextView;

/**
 * Created by liangjiangli on 2018/1/10.
 */

public class AuthUserInfoActivity extends BaseActivity {

    @Bind(R.id.txt_name)
    BmCellTextView txtName;
    @Bind(R.id.txt_card_id)
    BmCellTextView txtCardId;
    @Bind(R.id.txt_phone)
    BmCellTextView txtPhone;
    @Bind(R.id.cb_tnc)
    CheckBox cbTnc;
    @Bind(R.id.btn_confirm)
    BMAngleBtn1View btnConfirm;
    @Bind(R.id.lv_remark)
    ListView lvRemark;
    private static final String DATA_RESULT = "dataResult";

    public static void startAct(Activity context, ResultCheckPersonReal result, int requestCode) {
        Intent intent = new Intent(context, AuthUserInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(DATA_RESULT, result);
        intent.putExtras(bundle);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_auth_user_info;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.auth_user_info_title);
    }

    @Override
    public void initView() {
        cbTnc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                btnConfirm.setEnabled(b);
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWaitDialog();
                ContractApi.doRealNameCheck(getToken(),
                        (WithContextTextHttpResponseHandler)getNewHandler(1, ResultBase.class));

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            toast(data.getStringExtra(AbstractSignatureActivity.FILE_PATH));
            //TODO 跳到劳动合同详情
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public void initData() {
        ResultCheckPersonReal bean = (ResultCheckPersonReal)getIntent().getSerializableExtra(DATA_RESULT);
        txtName.setContentText(bean.empName);
        txtCardId.setContentText(bean.idCard);
        txtPhone.setContentText(bean.mobileNo);
        RemarkAdapter adapter = new RemarkAdapter(this);
        adapter.setList(bean.remarkText);
        lvRemark.setAdapter(adapter);
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        if (requestCode == 1) {
            SignatureActivity.startAct(AuthUserInfoActivity.this, FileUtil.getPathTemp(), 1);
        }
    }

    class RemarkAdapter extends BaseListAdapter<String> {

        public RemarkAdapter(Context context) {
            super(context, null);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_auth_info_remark;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            String remark = list.get(position);
            TextView txtTips = getViewById(R.id.txt_remark);
            txtTips.setText(remark);
        }
    }
}
