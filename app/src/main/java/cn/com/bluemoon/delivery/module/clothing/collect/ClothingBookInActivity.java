package cn.com.bluemoon.delivery.module.clothing.collect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.module.base.BaseActionBarActivity;
import cn.com.bluemoon.delivery.ui.switchbutton.SwitchButton;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.utils.LibConstants;
import cn.com.bluemoon.lib.view.ScrollGridView;

// TODO: lk 2016/6/14 界面

/**
 * 衣物登记
 * Created by luokai on 2016/6/12.
 */
public class ClothingBookInActivity extends BaseActionBarActivity {


    @Bind(R.id.et_number)
    EditText etNumber;
    @Bind(R.id.iv_scane_code)
    ImageView ivScaneCode;
    @Bind(R.id.tv_service_type)
    TextView tvServiceType;
    @Bind(R.id.ll_service_type)
    LinearLayout llServiceType;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.ll_name)
    LinearLayout llName;
    @Bind(R.id.sb_falw)
    SwitchButton sbFalw;
    @Bind(R.id.v_div_falw)
    View vDivFalw;
    @Bind(R.id.et_flaw)
    EditText etFlaw;
    @Bind(R.id.sb_stain)
    SwitchButton sbStain;
    @Bind(R.id.et_backup)
    EditText etBackup;
    @Bind(R.id.sgv_photo)
    ScrollGridView sgvPhoto;
    @Bind(R.id.btn_delete)
    Button btnDelete;
    @Bind(R.id.btn_ok)
    Button btnOk;
    @Bind(R.id.v_div_btn)
    View vDivBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothing_book_in);
        ButterKnife.bind(this);
    }

    @Override
    protected int getActionBarTitleRes() {
        return R.string.title_clothing_book_in;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }

        switch (requestCode) {
            case Constants.REQUEST_SCAN:
                if (resultCode == RESULT_OK) {
                    String resultStr = data.getStringExtra(LibConstants.SCAN_RESULT);
                    etNumber.setText(resultStr);
                }
                break;
        }
    }

    @OnClick({R.id.iv_scane_code, R.id.ll_service_type, R.id.ll_name, R.id.btn_delete, R.id.btn_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_scane_code:
                PublicUtil.openScanCard(this, getString(R.string.coupons_scan_code_title),
                        Constants.REQUEST_SCAN);
                break;
            case R.id.ll_service_type:
                // TODO: lk 2016/6/15 选择服务类型
                break;
            case R.id.ll_name:
                // TODO: lk 2016/6/15 选择衣物名称
                break;
            case R.id.btn_delete:
                // TODO: lk 2016/6/15 删除
                break;
            case R.id.btn_ok:
                // TODO: lk 2016/6/15 确定
                break;
        }
    }
}
