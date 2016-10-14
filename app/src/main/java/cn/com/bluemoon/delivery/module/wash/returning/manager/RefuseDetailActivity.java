package cn.com.bluemoon.delivery.module.wash.returning.manager;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.common.photopicker.PhotoPickerActivity;
import cn.com.bluemoon.delivery.common.photopicker.PhotoPreviewActivity;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.app.api.model.wash.manager.ResultRefuseDetail;
import cn.com.bluemoon.delivery.ui.ImageGridView;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.lib.view.CommonAlertDialog;

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
    @Bind(R.id.et_reason)
    TextView etReason;
    @Bind(R.id.btn_save)
    Button btnSave;
    @Bind(R.id.txt_upload_hint)
    TextView txtUploadHint;
    @Bind(R.id.star)
    TextView star;
    @Bind(R.id.star1)
    TextView star1;
    private String clothesCode;
    private List<String> imagePaths = new ArrayList<>();


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
        boolean isSave = getIntent().getBooleanExtra("isSave", true);
        clothesCode = getIntent().getStringExtra("clothesCode");
        if (isSave) {
            etReason.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.VISIBLE);
            gridView.loadAdpater(imagePaths, true);
            txtTime.setText(DateUtil.getTime(new Date().getTime(), "yyyy-MM-dd HH:mm:ss"));
        } else {
            txtUploadHint.setVisibility(View.GONE);
            star1.setVisibility(View.GONE);
            star.setVisibility(View.GONE);
            txtReason.setVisibility(View.VISIBLE);
            showWaitDialog();
            ReturningApi.refuseSignDetail(clothesCode, getToken(), getNewHandler(1, ResultRefuseDetail.class));
        }
        txtCode.setText(getString(R.string.manage_clothes_code3, clothesCode));
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CommonAlertDialog.Builder(RefuseDetailActivity.this)
                        .setMessage(getString(R.string.manage_refuse_this_clothes_txt))
                        .setPositiveButton(R.string.btn_cancel, null)
                        .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).show();
            }
        });

    }

    @Override
    public void initData() {

    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        hideWaitDialog();
        ResultRefuseDetail r = (ResultRefuseDetail) result;
        gridView.loadAdpater(r.getImagePaths(), false);
        txtReason.setText(r.getRefuseIssueDesc());
        txtTime.setText(DateUtil.getTime(r.getRefuseTagTime(), "yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && data!= null) {
            List<String> list = data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT);
            imagePaths.addAll(list);
            gridView.loadAdpater(imagePaths, true);
        } else if (requestCode == 20 && data!= null) {
            imagePaths = data.getStringArrayListExtra(PhotoPreviewActivity.EXTRA_RESULT);
            gridView.loadAdpater(imagePaths, true);
        }
    }
}
