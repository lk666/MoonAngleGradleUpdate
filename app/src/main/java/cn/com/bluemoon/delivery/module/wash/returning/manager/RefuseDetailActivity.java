package cn.com.bluemoon.delivery.module.wash.returning.manager;

import android.widget.TextView;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.app.api.model.wash.manager.ResultRefuseDetail;
import cn.com.bluemoon.delivery.ui.ImageGridView;
import cn.com.bluemoon.delivery.utils.DateUtil;

/**
 * Created by ljl on 2016/9/212.
 */
public class RefuseDetailActivity extends BaseActivity {

    @Bind(R.id.gridview_img)
    ImageGridView gridView;
    @Bind(R.id.txt_code)
    TextView txtCode;
    @Bind(R.id.txt_time)
    TextView txtTime;
    @Bind(R.id.txt_reason)
    TextView txtReason;
    private String clothesCode;


    @Override
    protected String getTitleString() {
        return getString(R.string.manage_refuse_info_title);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_refuse_detail;
    }

    @Override
    public void initView() {
        showWaitDialog();
        clothesCode = getIntent().getStringExtra("clothesCode");
        ReturningApi.refuseSignDetail(clothesCode, getToken(), getNewHandler(1, ResultRefuseDetail.class));
    }

    @Override
    public void initData() {

    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        hideWaitDialog();
        ResultRefuseDetail r = (ResultRefuseDetail) result;
        gridView.loadAdpater(r.getImagePaths(), false);
        txtCode.setText(getString(R.string.manage_clothes_code3, clothesCode));
        txtTime.setText(DateUtil.getTime(r.getRefuseTagTime(), "yyyy-MM-dd HH:mm:ss"));
        txtReason.setText(r.getRefuseIssueDesc());
    }
}
