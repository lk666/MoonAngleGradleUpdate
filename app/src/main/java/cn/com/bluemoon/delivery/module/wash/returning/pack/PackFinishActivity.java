package cn.com.bluemoon.delivery.module.wash.returning.pack;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.pack.ResultScanBoxCode;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.lib.utils.LibViewUtil;

/**
 * Created by allenli on 2016/10/11.
 */
public class PackFinishActivity extends BaseActivity {

    private final static String EXTRA_TAG_CODE = "EXTRA_TAG_CODE";
    private final static int REQUEST_CODE_QUERY = 0x777;

    @Bind(R.id.txt_pack_box_code)
    TextView txtPackBoxCode;
    @Bind(R.id.txt_box_code)
    TextView txtBoxCode;

    @Bind(R.id.btn_back)
    Button btnBack;
    @Bind(R.id.btn_ok)
    Button btnOk;

    private String tagCode;
    private ResultScanBoxCode item;

    public static void actionStart(Context context, String tagCode) {
        Intent intent = new Intent(context, PackFinishActivity.class);
        intent.putExtra(EXTRA_TAG_CODE, tagCode);
        context.startActivity(intent);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();

        if (getIntent() != null) {
            tagCode = getIntent().getStringExtra(EXTRA_TAG_CODE);
        }
        if (TextUtils.isEmpty(tagCode)) {
            tagCode = "";
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pack_success;
    }

    @Override
    public void initView() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                finish();
                item = new ResultScanBoxCode();
                item.setBoxCode("wwew");
                checkBtn();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(item!=null && !StringUtil.isEmptyString(item.getBoxCode())){
                    ScanPackActivity.actStart(PackFinishActivity.this, tagCode, item.getBoxCode());
                    finish();
                }
            }
        });
    }

    @Override
    public void initData() {
        showWaitDialog();
        ReturningApi.scanPackageBackOrder(tagCode, getToken(), getNewHandler
                (REQUEST_CODE_QUERY, ResultScanBoxCode.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        item = (ResultScanBoxCode) result;
        checkBtn();
    }

    private void checkBtn() {
        if (item != null && !StringUtil.isEmptyString(item.getBoxCode())) {
            LibViewUtil.setViewVisibility(txtPackBoxCode, View.VISIBLE);
            txtBoxCode.setText(item.getBoxCode());
            btnOk.setClickable(true);
            btnOk.setBackgroundResource(R.drawable.btn_red_shape4);
        } else {
            LibViewUtil.setViewVisibility(txtPackBoxCode, View.GONE);
            txtBoxCode.setText(getString(R.string.pack_finish_no_box_tag));
            btnOk.setClickable(false);
            btnOk.setBackgroundResource(R.drawable.btn_disable_shape);
        }
    }
}