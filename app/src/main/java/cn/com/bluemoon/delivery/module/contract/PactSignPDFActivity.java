package cn.com.bluemoon.delivery.module.contract;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bluemoon.signature.lib.AbstractSignatureActivity;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ContractApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.contract.ResultCheckPersonReal;
import cn.com.bluemoon.delivery.app.api.model.contract.ResultContractDetail;
import cn.com.bluemoon.delivery.app.api.model.contract.ResultPDFPosition;
import cn.com.bluemoon.delivery.common.PublicLinkManager;
import cn.com.bluemoon.delivery.module.base.WithContextTextHttpResponseHandler;
import cn.com.bluemoon.delivery.module.document.BasePDFActivity;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.DialogUtil;
import cn.com.bluemoon.delivery.utils.FileUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.lib.utils.LibImageUtil;
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
    private String phone = "";

    public static void actStart(Context context, String contractId) {
        Intent intent = new Intent(context, PactSignPDFActivity.class);
        intent.putExtra(PublicLinkManager.ID, contractId);
        context.startActivity(intent);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        contractId = getIntent().getStringExtra(PublicLinkManager.ID);
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
        showWaitDialog();
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
        // 2018/1/11 暂时用测试数据
        ContractApi.getPDFPosition(getToken(), contractId, (WithContextTextHttpResponseHandler)
                getNewHandler(2, ResultPDFPosition.class));
    }

    @Override
    public void initData() {
        super.initData();
        showWaitDialog();
        ContractApi.getContractDetailToIOS(getToken(), contractId,
                (WithContextTextHttpResponseHandler) getNewHandler(0, ResultContractDetail.class));
    }

    @Override
    protected boolean isImageType() {
        return true;
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
            //获取详情
            ResultContractDetail resultContractDetail = (ResultContractDetail) result;
//            openFile(resultContractDetail.fileUrl, null, 0);
            openFile(resultContractDetail.photoList);
            if (STATUS_HAD.equals(resultContractDetail.contractStatus)) {
                ViewUtil.setViewVisibility(btnSign, View.GONE);
            } else {
                ViewUtil.setViewVisibility(btnSign, View.VISIBLE);
            }
        } else if (requestCode == 1) {
            final ResultCheckPersonReal resultBean = (ResultCheckPersonReal) result;
            phone = resultBean.mobileNo;
            if (!resultBean.isNeedReal) {
                //跳转创建签名页
                SignatureActivity.startAct(PactSignPDFActivity.this, FileUtil.getPathTemp(), 1);
            } else {
                DialogUtil.getCommonDialog(this, null, resultBean.checkContent, getString(R
                        .string.btn_no), getString(R.string.btn_goods), null, new DialogInterface
                        .OnClickListener() {

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
            heightP = (resultPDFPosition.height - resultPDFPosition.signY) / resultPDFPosition
                    .height;
        } else if (requestCode == 3) {
            submitSign();
        } else if (requestCode == 4) {
            //合同签署
            pwdDialog.dismiss();
            back(true);
        }
    }

    @Override
    public void onErrorResponse(int requestCode, ResultBase result) {
        super.onErrorResponse(requestCode, result);
        //如果电子合同已经撤销，需要刷新列表，则返回这个
        if ((requestCode == 0 && result.getResponseCode() == 13002) || (requestCode == 4 &&
                result.getResponseCode() == 13008)) {
            back(false);
        }
    }

    /**
     * 带状态返回
     * 1 表示执行成功
     * 2 表示执行失败，电子合同已经被取消，需要刷新列表
     */
    private void back(boolean isSuccess) {
        Intent intent = new Intent();
        intent.putExtra(PublicLinkManager.PDF_CODE, isSuccess ? 1 : 2);
        setResult(RESULT_OK, intent);
        finish();
    }

    private CommonAlertDialog pwdDialog;

    /**
     * 弹出验证码输入框
     */
    private void submitSign() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_send_msg_content, null);
        TextView txtMsg = (TextView) view.findViewById(R.id.txt_msg);
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
                                    ContractApi.doContractSign(contractId, getToken(), pwd,
                                            filePath,
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
                int size = AppContext.getInstance().getDisplayWidth() / 5;
                bitmap = LibImageUtil.scaleBitmap(bitmap, size, true);
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

    //签名显示的坐标比例（默认）
    private float widthP = 0.677f;
    private float heightP = 0.657f;

    @Override
    public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {
        if (displayedPage == getPageCount() - 1 && bitmap != null) {
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
