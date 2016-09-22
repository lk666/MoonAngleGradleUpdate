package cn.com.bluemoon.delivery.module.wash.returning.manager;

import android.os.Bundle;
import android.widget.TextView;

import org.kymjs.kjframe.KJBitmap;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.wash.returning.manager.model.ResultRefuseDetail;
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
        return "拒签信息";
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
        gridView.setList(r.getImagePaths());
        txtCode.setText("衣物编码-"+clothesCode);
        txtTime.setText(DateUtil.getTime(r.getRefuseTagTime(), "yyyy-MM-dd HH:mm:ss"));
        txtReason.setText(r.getRefuseIssueDesc());
    }
}
