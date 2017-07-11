package cn.com.bluemoon.delivery.module.wash.returning.manager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.manager.ResultBackOrder;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.lib_widget.module.form.BMEditText;
import cn.com.bluemoon.lib_widget.module.form.interf.BMEditTextListener;

/**
 * 快速查询-手动查询
 * Created by tangqiwei on 2017/7/10.
 */

public class ManualQueriesActivity extends BaseActivity implements View.OnClickListener{

    private final String TYPE = "BACK_ORDER_WAIT_SIGN";

    @Bind(R.id.et_name)
    BMEditText etName;
    @Bind(R.id.et_phone)
    BMEditText etPhone;
    @Bind(R.id.llayout_scan_code_query)
    LinearLayout llayoutScanCodeQuery;
    @Bind(R.id.btn_queries)
    Button btnQueries;

    public static void actStart(Activity activity) {
        Intent intent = new Intent(activity, ManualQueriesActivity.class);
        activity.startActivityForResult(intent, 1);
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.returning_fasttips_title);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_fasttips;
    }

    @Override
    public void initView() {
        btnQueries.setOnClickListener(this);
        llayoutScanCodeQuery.setOnClickListener(this);
        etName.setListener(new BMEditTextListener() {
            @Override
            public void afterTextChanged(Editable s) {
                btnStateChange();
            }
        });
        etPhone.setListener(new BMEditTextListener() {
            @Override
            public void afterTextChanged(Editable s) {
                btnStateChange();
            }
        });
    }

    private void btnStateChange(){
        if(etName.getText().toString().isEmpty()||etPhone.getText().toString().isEmpty()){
            btnQueries.setEnabled(false);
        }else{
            btnQueries.setEnabled(true);
        }
    }

    @Override
    public void initData() {
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        hideWaitDialog();
        if(requestCode==0){
            ResultBackOrder resultBackOrder= (ResultBackOrder) result;
            SearchResultActivity.actStart(this, (ArrayList<ResultBackOrder.BackOrderListBean>) resultBackOrder.getBackOrderList());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llayout_scan_code_query:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.btn_queries:
                showWaitDialog();
                ReturningApi.queryBackOrderList(TYPE, 0, 0, 0, null,etName.getText().toString(),etPhone.getText().toString(),getToken(), getNewHandler(0, ResultBackOrder.class));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0&&resultCode==RESULT_OK){
            setResult(RESULT_OK);
            finish();
        }
    }
}
