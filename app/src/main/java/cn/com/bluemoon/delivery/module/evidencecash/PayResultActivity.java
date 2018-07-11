package cn.com.bluemoon.delivery.module.evidencecash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseActivity;

/**
 * Created by ljl on 2016/11/17.
 * 充值结果页面
 */
public class PayResultActivity extends BaseActivity {

    @BindView(R.id.img_result)
    ImageView imgResult;
    @BindView(R.id.txt_result)
    TextView txtResult;

    public static void actStart(Context context, boolean isSuccess) {
        Intent intent = new Intent(context, PayResultActivity.class);
        intent.putExtra("isSuccess", isSuccess);
        context.startActivity(intent);
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.pay_online_txt);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pay_result;
    }

    @Override
    public void initView() {
        boolean isSuccess = getIntent().getBooleanExtra("isSuccess", false);
        if (isSuccess) {
            imgResult.setImageResource(R.mipmap.pay_success);
            txtResult.setText(R.string.pay_success);
        } else {
            imgResult.setImageResource(R.mipmap.pay_failed);
            txtResult.setText(R.string.pay_failed);
        }
    }

    @Override
    public void initData() {

    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {

    }
}
