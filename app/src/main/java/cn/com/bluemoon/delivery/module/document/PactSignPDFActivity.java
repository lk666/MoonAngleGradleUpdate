package cn.com.bluemoon.delivery.module.document;

import android.content.Context;
import android.content.Intent;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.lib_widget.module.form.BMAngleBtn1View;

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
        toast("签署验证");
    }

    @Override
    public void onInitiallyRendered(int nbPages, float pageWidth, float pageHeight) {
        super.onInitiallyRendered(nbPages, pageWidth, pageHeight);
        btnSign.setEnabled(true);
    }
}
