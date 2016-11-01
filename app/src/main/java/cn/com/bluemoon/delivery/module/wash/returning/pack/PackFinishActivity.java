package cn.com.bluemoon.delivery.module.wash.returning.pack;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.lib.utils.LibViewUtil;

/**
 * Created by allenli on 2016/10/11.
 */
public class PackFinishActivity extends BaseActivity {

    @Bind(R.id.txt_pack_box_code)
    TextView txtPackBoxCode;
    @Bind(R.id.txt_box_code)
    TextView txtBoxCode;
    @Bind(R.id.btn_back)
    Button btnBack;
    @Bind(R.id.btn_ok)
    Button btnOk;
    private String backOrderCode;
    private String boxCode;

    public static void actStart(Context context, String backOrderCode, String boxCode) {
        Intent intent = new Intent(context, PackFinishActivity.class);
        intent.putExtra("backOrderCode", backOrderCode);
        intent.putExtra("boxCode", boxCode);
        context.startActivity(intent);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        if (getIntent() != null) {
            backOrderCode = getIntent().getStringExtra("backOrderCode");
            boxCode = getIntent().getStringExtra("boxCode");
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pack_success;
    }

    @Override
    public void initView() {
        if (!TextUtils.isEmpty(backOrderCode) && !TextUtils.isEmpty(boxCode)) {
            LibViewUtil.setViewVisibility(txtPackBoxCode, View.VISIBLE);
            txtBoxCode.setText(boxCode);
            btnOk.setClickable(true);
            btnOk.setBackgroundResource(R.drawable.btn_red_shape4);
        }
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(backOrderCode) && !TextUtils.isEmpty(boxCode)) {
                    ScanPackActivity.actStart(PackFinishActivity.this, backOrderCode, boxCode);
                    finish();
                }
            }
        });
    }

    @Override
    public void initData() {

    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {

    }

}