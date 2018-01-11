package cn.com.bluemoon.delivery.module.document;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bluemoon.signature.lib.AbstractSignatureActivity;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.MenuFragment;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ContractApi;
import cn.com.bluemoon.delivery.app.api.HRApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.contract.ResultCheckPersonReal;
import cn.com.bluemoon.delivery.app.api.model.contract.ResultContractDetail;
import cn.com.bluemoon.delivery.app.api.model.contract.ResultPDFPosition;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.WithContextTextHttpResponseHandler;
import cn.com.bluemoon.delivery.module.contract.AuthUserInfoActivity;
import cn.com.bluemoon.delivery.module.contract.SignatureActivity;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.DialogUtil;
import cn.com.bluemoon.delivery.utils.FileUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.lib.view.CommonAlertDialog;
import cn.com.bluemoon.lib_widget.module.form.BMAngleBtn3View;

public class PactSignPDFActivity extends BasePDFActivity {

    private final static String STATUS_WAIT = "wait_sign";
    private final static String STATUS_HAD = "had_sign";

    @Bind(R.id.btn_sign)
    BMAngleBtn3View btnSign;
    private String contractId;
    private Paint paint;
    private CommonActionBar mActionBar;

    private String test = "http://pubfile.bluemoon.com" +
            ".cn/group1/M00/02/DB/wKgwb1pVes-EJCI4AAAAACNWajk941" +
            ".pdf?name=%E5%8A%B3%E5%8A%A8%E5%90%88%E5%90%8C-%E7%AD%BE%E8%AE%A2%E7%94%B5%E5%AD%90" +
            "%E5%90%88%E5%90%8C%E4%BD%BF%E7%94%A820171228.pdf";

    public static void actStart(Context context, String contractId) {
        Intent intent = new Intent(context, PactSignPDFActivity.class);
        intent.putExtra("contractId", contractId);
        context.startActivity(intent);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        contractId = getIntent().getStringExtra("contractId");
        contractId = "123";
    }

    @Override
    protected void setActionBar(CommonActionBar titleBar) {
        super.setActionBar(titleBar);
        titleBar.getTvRightView().setText(R.string.btn_submit);
        mActionBar = titleBar;
    }

    @Override
    protected void onActionBarBtnRightClick() {
        super.onActionBarBtnRightClick();
        ContractApi.sendSmsBySign(getToken(), (WithContextTextHttpResponseHandler) getNewHandler
                (3, ResultBase.class));
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
    public void initView() {
        super.initView();
        initPaint();
//        ContractApi.getPDFPosition(getToken(), contractId, (WithContextTextHttpResponseHandler)
//                getNewHandler(2, ResultPDFPosition.class));
    }

    @Override
    public void initData() {
        super.initData();
        showWaitDialog();
        ContractApi.getContractDetail(getToken(), contractId,
                (WithContextTextHttpResponseHandler) getNewHandler(0, ResultContractDetail.class));
    }

    private void initPaint() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setFilterBitmap(true);
        paint.setDither(true);
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
        if (requestCode == 0) {
            ResultContractDetail resultContractDetail = (ResultContractDetail) result;
            // TODO: 2018/1/10 打开文档,暂时用测试地址
            openFile(test, null, 0);
            if (STATUS_HAD.equals(resultContractDetail.contractStatus)) {
                ViewUtil.setViewVisibility(btnSign, View.GONE);
            } else {
                ViewUtil.setViewVisibility(btnSign, View.VISIBLE);
            }
        } else if (requestCode == 1) {
            final ResultCheckPersonReal resultBean = (ResultCheckPersonReal) result;
            if (!resultBean.isNeedReal) {
                //跳转创建签名页
                SignatureActivity.startAct(PactSignPDFActivity.this, FileUtil.getPathTemp(), 1);
            } else {
                DialogUtil.getCommonDialog(this, null, resultBean.getResponseMsg(), "暂不", "好的",
                        null, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //跳转实名认证页
                                AuthUserInfoActivity.startAct(PactSignPDFActivity.this,
                                        resultBean, 2);
                            }
                        }).show();

            }
        } else if (requestCode == 2) {
            ResultPDFPosition resultPDFPosition = (ResultPDFPosition) result;
            widthP = resultPDFPosition.signX / resultPDFPosition.width;
            heightP = 1 - (resultPDFPosition.signY / resultPDFPosition.height);
        } else if (requestCode == 3) {
            submitSign();
        } else if (requestCode == 4) {
            pwdDialog.dismiss();
            finish();
        }
    }

    private CommonAlertDialog pwdDialog;

    /**
     * 弹出验证码输入框
     */
    private void submitSign() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_send_msg_content, null);
        TextView txtMsg = (TextView) view.findViewById(R.id.txt_msg);
        String phone = MenuFragment.user != null ? MenuFragment.user.getMobileNo() : getString(R
                .string.your_number);
        txtMsg.setText(getString(R.string.send_message, phone));
        final EditText etCode = (EditText) view.findViewById(R.id.et_psw);

        // 输入密码提示
        pwdDialog = new CommonAlertDialog.Builder(this)
                .setTitle(getString(R.string.title_tips))
                .setCancelable(false)
                .setView(view)
                .setDismissable(false)
                .setNegativeButton(R.string.btn_ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                String pwd = etCode.getText().toString();
                                if (TextUtils.isEmpty(pwd)) {
                                    toast(R.string.err_valid_code_empty);
                                } else {
                                    showWaitDialog(false);
                                    //提交签名
                                    ContractApi.doContractSign(getToken(), pwd, filePath,
                                            (WithContextTextHttpResponseHandler) getNewHandler(4,
                                                    ResultBase.class));
                                    ViewUtil.hideKeyboard(etCode);
                                }
                            }
                        })
                .setPositiveButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ViewUtil.hideKeyboard(etCode);
                        dialog.dismiss();
                    }
                }).create();
        pwdDialog.show();
        etCode.post(new Runnable() {
            @Override
            public void run() {
                ViewUtil.showKeyboard(etCode);
            }
        });
    }

    private Bitmap bitmap;
    private String filePath;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if ((requestCode == 1 || requestCode == 2) && data != null) {
                ViewUtil.setViewVisibility(btnSign, View.GONE);
                filePath = data.getStringExtra(AbstractSignatureActivity.FILE_PATH);
                //生成可绘制的小签名bitmap
                bitmap = BitmapFactory.decodeFile(filePath);
                int width = bitmap.getWidth() / 6;
                int height = bitmap.getHeight() / 6;
                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
                mActionBar.getTvRightView().setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    protected void onDestroy() {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        bitmap = null;
        super.onDestroy();

    }

    @Override
    public void onInitiallyRendered(int nbPages, float pageWidth, float pageHeight) {
        super.onInitiallyRendered(nbPages, pageWidth, pageHeight);
        btnSign.setEnabled(true);
    }

    private float widthP = 0.63f;
    private float heightP = 0.83f;

    @Override
    public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {
        if (displayedPage == pdfView.getPageCount() - 1 && bitmap != null) {
//            canvas.drawBitmap(bitmap, pageWidth * 0.8f, pageHeight * 0.8f, null);

            Rect srcRect = new Rect(0, 0, (int) pageWidth, (int) pageHeight);
            int left = (int) (pageWidth * widthP);
            int top = (int) (pageHeight * heightP);
            int right = left + (int) (bitmap.getWidth() * pdfView.getZoom());
            int bottom = top + (int) (bitmap.getHeight() * pdfView.getZoom());
            Rect bitRect = new Rect(left, top, right, bottom);
            canvas.drawBitmap(bitmap, srcRect, bitRect, paint);
        }
    }

}
