package cn.com.bluemoon.delivery.module.contract;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bluemoon.signature.lib.AbstractSignatureActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.MenuFragment;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ContractApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.contract.ResultCheckPersonReal;
import cn.com.bluemoon.delivery.app.api.model.contract.ResultContractDetail;
import cn.com.bluemoon.delivery.app.api.model.contract.ResultContractDetailIOS;
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
    @Bind(R.id.pb)
    ProgressBar pb;
    private String contractId;
    private Paint paint;
    private String phone = "";

    /**
     * 0--未签署
     * 1--已签署，未提交
     * 2--已签署，未下载
     * 3--已签署，下载中
     * 4--已签署，已下载
     */
    private int status = 0;
    private final static int REQUEST_CODE_GET_CONTRACT_DETAIL = 0x777;

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
    protected void onActionBarBtnRightClick() {
        super.onActionBarBtnRightClick();

        switch (status) {
            // 已签署，未提交
            case 1:
                // 提交签名
                showWaitDialog();
                ContractApi.sendSmsBySign(getToken(), (WithContextTextHttpResponseHandler)
                        getNewHandler
                                (3, ResultBase.class));
                break;
            // 已签署，未下载
            case 2:
                DialogUtil.getCommonDialog(this, getString(R.string.title_tips),
                        getString(R.string.download_contract),
                        getString(R.string.btn_cancel),
                        getString(R.string.btn_ok), null, new DialogInterface
                                .OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                downloadPdf();
                            }
                        }).show();
                break;
            // 已签署，已下载
            case 4:
                DialogUtil.getCommonDialog(this, getString(R.string.title_tips),
                        getString(R.string.download_contract_again),
                        getString(R.string.btn_cancel),
                        getString(R.string.btn_ok), null, new DialogInterface
                                .OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                downloadPdf();
                            }
                        }).show();
                break;
            default:
                break;
        }
    }

    /**
     * 文件名（不含.pdf）
     */
    private String getPdfFileName() {
        StringBuilder sb = new StringBuilder(contractId).append("-")
                .append(resultContractDetail.contractType);
        if (MenuFragment.user != null) {
            sb.append("-")
                    .append(MenuFragment.user.getRealName()).append("-")
                    .append(MenuFragment.user.getAccount());
        }
        return sb.toString();
    }

    private void downloadPdf() {
        setPdfView(3);
        Intent i = new Intent(this, DownLoadPdfService.class);
        i.putExtra("url", resultContractDetail.fileUrl);
        i.putExtra("name", getPdfFileName());
        startService(i);
    }

    @Override
    protected boolean isUseEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DownLoadPdfService.DownloadEvent event) {
        if (event == null) {
            return;
        }

        switch (event.progress) {
            // 开始下载，用于记录downloadId
            case -1:
                //                event.downloadId
                break;
            // 下载完成
            case -2:
                setPdfView(4);
                break;
            // 下载失败
            case -3:
                setPdfView(2);
                break;
            // 进度报告
            default:
                pb.setProgress(event.progress);
                break;
        }
    }

    @Override
    protected String getTitleString() {
        return "详情";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pact_sign_pdf;
    }

    @Override
    public void initView() {
        super.initView();
        initPaint();
        ContractApi.getPDFPosition(getToken(), contractId, (WithContextTextHttpResponseHandler)
                getNewHandler(2, ResultPDFPosition.class));
    }

    @Override
    public void initData() {
        super.initData();
        setPdfView(0);
        showWaitDialog();
        ContractApi.getContractDetailToIOS(getToken(), contractId,
                (WithContextTextHttpResponseHandler) getNewHandler(0, ResultContractDetailIOS
                        .class, false));
    }

    @Override
    public void onSuccessException(int requestCode, Throwable t) {
        super.onSuccessException(requestCode, t);
        if (requestCode == 0) {
            hideWaitDialog();
        }
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
        switch (status) {
            // 未签署
            case 0:
                showWaitDialog();
                ContractApi.checkPersonReal(getToken(),
                        (WithContextTextHttpResponseHandler) getNewHandler(1,
                                ResultCheckPersonReal.class));
                break;
            // 已签署，已下载，打开文件夹
            case 4:
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_GET_CONTENT);
                intent.setDataAndType(Uri.fromFile(new File(FileUtil.getPathDown())),
                        "application/pdf");
                startActivity(intent);
                break;
            default:
                break;
        }
    }


    /**
     * 0--未签署
     * 1--已签署，未提交
     * 2--已签署，未下载
     * 3--已签署，下载中
     * 4--已签署，已下载
     */
    private void setPdfView(int status) {
        this.status = status;
        switch (status) {
            // 未签署
            case 0:
                // 设置右上角按钮
                mActionBar.getTvRightView().setVisibility(View.GONE);
                // 设置进度条
                pb.setVisibility(View.GONE);
                // 设置下方按钮
                ViewUtil.setViewVisibility(btnSign, View.VISIBLE);
                btnSign.setText(getString(R.string.btn_doc_sign));
                break;
            // 已签署，未提交
            case 1:
                // 设置右上角按钮
                mActionBar.getTvRightView().setVisibility(View.VISIBLE);
                mActionBar.getTvRightView().setText(R.string.btn_submit);
                // 设置进度条
                pb.setVisibility(View.GONE);
                // 设置下方按钮
                ViewUtil.setViewVisibility(btnSign, View.GONE);
                break;
            // 已签署，未下载
            case 2:
                // 设置右上角按钮
                mActionBar.getTvRightView().setVisibility(View.VISIBLE);
                mActionBar.getTvRightView().setText(R.string.download);
                // 设置进度条
                pb.setVisibility(View.GONE);
                // 设置下方按钮
                ViewUtil.setViewVisibility(btnSign, View.GONE);
                break;
            // 已签署，下载中
            case 3:
                // 设置右上角按钮
                mActionBar.getTvRightView().setVisibility(View.GONE);
                // 设置进度条
                pb.setVisibility(View.VISIBLE);
                // 设置下方按钮
                ViewUtil.setViewVisibility(btnSign, View.GONE);
                break;
            // 已签署，已下载
            case 4:
                // 设置右上角按钮
                mActionBar.getTvRightView().setVisibility(View.VISIBLE);
                mActionBar.getTvRightView().setText(R.string.download);
                // 设置进度条
                pb.setVisibility(View.GONE);
                // 设置下方按钮
                ViewUtil.setViewVisibility(btnSign, View.VISIBLE);
                btnSign.setText(getString(R.string.open_file));
                break;
            default:
                break;
        }
    }

    private void handleSign() {
        // 接口判断，已签署,已提交，判断是否已下载
        if (isDownload()) {
            setPdfView(4);
        } else {
            setPdfView(2);
        }
    }

    private String getPdfFilePath() {
        StringBuilder sb = new StringBuilder(FileUtil.getPathDown())
                .append(File.separator).append(contractId).append("-")
                .append(resultContractDetail.contractType);
        if (MenuFragment.user != null) {
            sb.append("-")
                    .append(MenuFragment.user.getRealName()).append("-")
                    .append(MenuFragment.user.getAccount());
        }
        sb.append(".pdf");
        return sb.toString();
    }

    private boolean isDownload() {
        return (new File(getPdfFilePath())).exists();
    }

    private ResultContractDetailIOS resultContractDetailIOS;
    private ResultContractDetail resultContractDetail;
    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        super.onSuccessResponse(requestCode, jsonString, result);
        //  已签署,已提交后，获取合同详情
        if (requestCode == REQUEST_CODE_GET_CONTRACT_DETAIL) {
            resultContractDetail = (ResultContractDetail) result;
            // 判断是否已下载
            handleSign();
        } else if (requestCode == 0) {
            // 获取详情
            resultContractDetailIOS = (ResultContractDetailIOS) result;
            //            openFile(resultContractDetailIOS.fileUrl, null, 0);
            updateTitle(getString(R.string.doc_pdf_detail, resultContractDetailIOS.contractType));
            openFile(resultContractDetailIOS.photoList);
            if (STATUS_HAD.equals(resultContractDetailIOS.contractStatus)) {
                // 已签署,已提交，获取合同详情
                ContractApi.getContractDetail(getToken(), contractId,
                        (WithContextTextHttpResponseHandler) getNewHandler
                                (REQUEST_CODE_GET_CONTRACT_DETAIL, ResultContractDetail.class));
            } else {
                setPdfView(0);
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
                        AuthUserInfoActivity.startAct(PactSignPDFActivity.this, contractId,
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
        if (requestCode == 0) {
            hideWaitDialog();
        }
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
                                } else if (pwd.length() != 6) {
                                    toast(R.string.err_valid_code_empty_6);
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
                setPdfView(1);
            }
        }

    }

    @Override
    protected void onDestroy() {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        bitmap = null;
        if (pwdDialog != null) {
            pwdDialog.dismiss();
        }
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

