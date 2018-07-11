package cn.com.bluemoon.delivery.module.evidencecash;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.EvidenceCashApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.evidencecash.ResultSaveCashInfo;
import cn.com.bluemoon.delivery.app.api.model.evidencecash.ResultUploadImg;
import cn.com.bluemoon.delivery.common.photopicker.PhotoPickerActivity;
import cn.com.bluemoon.delivery.common.photopicker.PhotoPreviewActivity;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.FileUtil;
import cn.com.bluemoon.delivery.utils.TextWatcherUtils;

/**
 * Created by ljl on 2016/11/17.
 * 提交银行转账凭证
 */
public class TransferVoucherActivity extends BaseActivity {

    @BindView(R.id.et_tranfer_money)
    EditText etTranferMoney;
    @BindView(R.id.img_add)
    ImageView imgAdd;
    @BindView(R.id.btn_ok)
    Button btnOk;
    private List<String> imagePaths;
    private String money;

    @Override
    protected String getTitleString() {
        return getString(R.string.transfer_voucher_title);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_transfer_voucher;
    }

    @Override
    public void initView() {
        imagePaths = new ArrayList<>();
        TextWatcherUtils.setMaxNumberWatcher(etTranferMoney, 20, 2, null);
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.img_add, R.id.btn_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_add:
                if (imagePaths.isEmpty()) {
                    PhotoPickerActivity.actStart(TransferVoucherActivity.this, Constants.REQUEST_ADD_IMG);
                } else {
                    PhotoPreviewActivity.actStart(this, (ArrayList<String>) imagePaths, 0, Constants.REQUEST_PREVIEW_IMG);
                }
                break;
            case R.id.btn_ok:
                money = etTranferMoney.getText().toString();
                if (StringUtils.isNotBlank(money)) {
                    if (imagePaths.isEmpty()) {
                        toast(getString(R.string.has_not_select_return_receipt_img));
                    } else {
                        btnOk.setEnabled(false);
                        showWaitDialog();
                        EvidenceCashApi.uploadImg(FileUtil.getBytes(BitmapFactory.decodeFile(imagePaths.get(0))),
                                getToken(),getNewHandler(1, ResultUploadImg.class));
                    }
                } else{
                    toast(getString(R.string.not_input_tranfer_money));
                }
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_ADD_IMG && data!= null) {
            List<String> list = data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT);
            imagePaths.addAll(list);
            if (!imagePaths.isEmpty()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imagePaths.get(0));
                imgAdd.setImageBitmap(bitmap);
            }
        } else if (requestCode == Constants.REQUEST_PREVIEW_IMG && data!= null) {
            imagePaths = data.getStringArrayListExtra(PhotoPreviewActivity.EXTRA_RESULT);
            if (imagePaths.isEmpty()) {
                imgAdd.setImageResource(R.mipmap.addpic);
            }
        }
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        if (requestCode == 1) {
            ResultUploadImg resultUploadImg = (ResultUploadImg)result;
            EvidenceCashApi.saveCashInfo((long)(Double.valueOf(money)*100), resultUploadImg.getEvidencePath(), getToken(), "bank", getNewHandler(2, ResultSaveCashInfo.class));
        }else {
            hideWaitDialog();
            toast(result.getResponseMsg());
            finish();
        }
    }

    @Override
    public void onErrorResponse(int requestCode, ResultBase result) {
        super.onErrorResponse(requestCode, result);
        btnOk.setEnabled(true);
    }

    @Override
    public void onSuccessException(int requestCode, Throwable t) {
        super.onSuccessException(requestCode, t);
        btnOk.setEnabled(true);
    }

    @Override
    public void onFailureResponse(int requestCode, Throwable t) {
        super.onFailureResponse(requestCode, t);
        btnOk.setEnabled(true);
    }
}
