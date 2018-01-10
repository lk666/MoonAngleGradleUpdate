package cn.com.bluemoon.delivery.module.document;

import android.content.Context;
import android.content.Intent;

import com.bluemoon.signature.lib.AbstractSignatureActivity;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ContractApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.contract.ResultCheckPersonReal;
import cn.com.bluemoon.delivery.module.base.WithContextTextHttpResponseHandler;
import cn.com.bluemoon.delivery.module.contract.AuthUserInfoActivity;
import cn.com.bluemoon.delivery.module.contract.SignatureActivity;
import cn.com.bluemoon.delivery.utils.FileUtil;
import cn.com.bluemoon.lib_widget.module.form.BMAngleBtn1View;

import static android.app.Activity.RESULT_OK;

public class PactSignPDFActivity extends BasePDFActivity {

    @Bind(R.id.btn_sign)
    BMAngleBtn1View btnSign;
    private String contractId;

    private String test = "http://pubfile.bluemoon.com" +
            ".cn/group1/M00/0A/44/wKgwB1mt-xyEd--HAAAAALwV5x8063.pdf";

    public static void actStart(Context context, String contractId) {
        Intent intent = new Intent(context, PactSignPDFActivity.class);
        intent.putExtra("contractId", contractId);
        context.startActivity(intent);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        contractId = getIntent().getStringExtra("contractId");
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.doc_pdf_detail);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pact_sign_pdf;
    }

    @Override
    public void initData() {
        super.initData();
        openFile(test, null, 0);
    }

    @OnClick(R.id.btn_sign)
    public void onClick() {
        showWaitDialog();
        ContractApi.checkPersonReal(getToken(),
                (WithContextTextHttpResponseHandler) getNewHandler(1, ResultCheckPersonReal.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        super.onSuccessResponse(requestCode, jsonString, result);
        if (requestCode == 1) {
            ResultCheckPersonReal resultBean = (ResultCheckPersonReal)result;
            if (!resultBean.isNeedReal) {
                //跳转创建签名页
                SignatureActivity.startAct(PactSignPDFActivity.this, FileUtil.getPathTemp(), 1);
            } else {
                //TODO 弹框
                //跳转实名认证页
                AuthUserInfoActivity.startAct(PactSignPDFActivity.this, resultBean, 2);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1 && data != null) {
                toast(data.getStringExtra(AbstractSignatureActivity.FILE_PATH));
                //TODO 跳到劳动合同详情
            } else if (requestCode == 2) {
                finish();//采用签名后关闭
            }
        }

    }

    @Override
    public void onInitiallyRendered(int nbPages, float pageWidth, float pageHeight) {
        super.onInitiallyRendered(nbPages, pageWidth, pageHeight);
        btnSign.setEnabled(true);
    }
}
