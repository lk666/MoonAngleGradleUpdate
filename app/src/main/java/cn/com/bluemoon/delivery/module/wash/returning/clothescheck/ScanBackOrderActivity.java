package cn.com.bluemoon.delivery.module.wash.returning.clothescheck;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.ResultUploadExceptionImage;
import cn.com.bluemoon.delivery.common.photopicker.PhotoPickerActivity;
import cn.com.bluemoon.delivery.common.photopicker.PhotoPreviewActivity;
import cn.com.bluemoon.delivery.module.base.BaseScanCodeActivity;
import cn.com.bluemoon.delivery.ui.ImageGridView;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.FileUtil;
import cn.com.bluemoon.lib.utils.LibImageUtil;
import cn.com.bluemoon.lib.view.CommonAlertDialog;

/**
 * 扫描还衣单标签(还衣单清点)
 */
public class ScanBackOrderActivity extends BaseScanCodeActivity {

    private static final String EXTRA_TAG_CODE = "EXTRA_TAG_CODE";
    public static final String EXTRA_LIST = "LIST";
    private static final int REQUEST_CODE_SCAN_BACK_ORDER = 0x777;
    private static final int REQUEST_CODE_UPLOAD_IMG = 0x666;
    private static final int REQUEST_CODE_ABNORMAL = 0x555;

    private ArrayList<CheckBackOrder> list = new ArrayList<>();
    private String tagCode;
    private String backOrderCode;

    /**
     * 扫描界面调起方法
     */
    public static void actionStart(Activity context, Fragment fragment, int requestCode,
                                   String tagCode, ArrayList<CheckBackOrder> list) {
        Intent intent = new Intent(context, ScanBackOrderActivity.class);
        intent.putExtra("title", context.getString(R.string
                .close_box_scan_back_code_title));
        intent.putExtra("btnString", context.getString(R.string
                .with_order_collect_manual_input_code_btn));
        intent.putExtra(EXTRA_LIST, list);
        intent.putExtra(EXTRA_TAG_CODE, tagCode);
        if (fragment != null) {
            fragment.startActivityForResult(intent, requestCode);
        } else {
            context.startActivityForResult(intent, requestCode);
        }
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        if (getIntent().hasExtra(EXTRA_LIST)) {
            list = (ArrayList<CheckBackOrder>) getIntent().getSerializableExtra(EXTRA_LIST);
        }
        if (getIntent().hasExtra(EXTRA_TAG_CODE)) {
            tagCode = getIntent().getStringExtra(EXTRA_TAG_CODE);
        }
    }

    @Override
    protected void onResult(String str, String type, Bitmap barcode) {
        if (TextUtils.isEmpty(str)) {
            toast(getString(R.string.scan_fail));
            return;
        }

        if (list == null) {
            finish();
            return;
        }

        if (check(str)) {
            // 服务端校验(8.9)
            showWaitDialog();
            backOrderCode = str;
            ReturningApi.scanCheckBackOrder(backOrderCode, tagCode, getToken(), getNewHandler
                    (REQUEST_CODE_SCAN_BACK_ORDER, ResultBase.class));
        } else {
            checkFinished();
        }
    }

    // 待上传的图片列表
    private ArrayList<UploadImage> imgs = new ArrayList<>();

    /**
     * 当前上传图片到的位置（当前位置未上传）
     */
    private int curUploadPosition;

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        switch (requestCode) {
            // 8.9衣物清点/还衣单清点-还衣单扫描校验
            case REQUEST_CODE_SCAN_BACK_ORDER:
                CommonAlertDialog.Builder dialog = new CommonAlertDialog.Builder(this);
                dialog.setMessage(R.string.scan_back_order_detail_dialog_msg);
                dialog.setTitle(R.string.scan_succeed);
                dialog.setPositiveButton(R.string.scan_back_order_detail_dialog_abnormal,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 异常记录
                                showAbnormalDialog();
                            }
                        });
                dialog.setNegativeButton(R.string.continue_scan, new DialogInterface
                        .OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        for (CheckBackOrder item : list) {
                            if (item.getBackOrderCode().equals(backOrderCode)) {
                                item.setCheckStatus(CheckBackOrder.NORMAL);
                                break;
                            }
                        }

                        checkFinished();
                    }
                });
                dialog.setPositiveButtonTextColor(getResources().getColor(R.color.text_red));
                dialog.show();
                break;

            // 上传图片
            case REQUEST_CODE_UPLOAD_IMG:
                ResultUploadExceptionImage pic = (ResultUploadExceptionImage) result;
                imgs.get(curUploadPosition).setImagePath(pic.getImgPath());
                curUploadPosition++;
                continueAbnormal();
                break;

            // 异常确认
            case REQUEST_CODE_ABNORMAL:
                for (CheckBackOrder item : list) {
                    if (item.getBackOrderCode().equals(backOrderCode)) {
                        item.setCheckStatus(CheckBackOrder.EXCEPTION);
                        break;
                    }
                }

                checkFinished();
                break;
        }
    }

    /**
     * 异常描述
     */
    private String issueDesc;

    private void continueAbnormal() {
        if (uploadImg()) {
            showWaitDialog();
            ReturningApi.scanBackOrder(backOrderCode, imgs, issueDesc, getToken(),
                    getNewHandler(REQUEST_CODE_ABNORMAL, ResultBase.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 图片控件
        if (requestCode == Constants.REQUEST_ADD_IMG && data != null) {
            List<String> list = data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT);
            paths.addAll(list);
            gridviewImg.loadAdpater(paths, true);
        } else if (requestCode == Constants.REQUEST_PREVIEW_IMG && data != null) {
            paths = data.getStringArrayListExtra(PhotoPreviewActivity.EXTRA_RESULT);
            gridviewImg.loadAdpater(paths, true);
        }
    }

    /**
     * 上传图片，若列表中的已全部上传完，返回true;
     */
    private boolean uploadImg() {
        if (curUploadPosition < imgs.size()) {
            showWaitDialog();
            UploadImage img = imgs.get(curUploadPosition);
            ReturningApi.uploadImage(FileUtil.getBytes(LibImageUtil.getImgScale(img
                            .getLocalImagePath(), 300, false)),
                    img.getFileName(), getToken(), getNewHandler(REQUEST_CODE_UPLOAD_IMG,
                            ResultUploadExceptionImage.class));
            return false;
        }
        return true;
    }

    private List<String> paths;
    private EditText etAbnormal;
    private ImageGridView gridviewImg;

    /**
     * 显示异常记录弹窗
     */
    private void showAbnormalDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_abnormal, null);
        etAbnormal = (EditText) view.findViewById(R.id.et_abnormal);
        gridviewImg = (ImageGridView) view.findViewById(R.id.gridview_img);
        imgs = null;
        paths = new ArrayList<>();
        gridviewImg.loadAdpater(paths, true);

        CommonAlertDialog.Builder dialog = new CommonAlertDialog.Builder(this);
        dialog.setView(view);
        dialog.setPositiveButton(getString(R.string.btn_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkFinished();
                    }
                });
        dialog.setNegativeButton(getString(R.string.btn_ok3),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //上传异常信息
                        String str = etAbnormal.getText().toString();
                        if (str.length() < 5) {
                            toast(getString(R.string.abnormal_hint));
                            return;
                        }
                        if (imgs == null) {
                            imgs = new ArrayList<>();
                            curUploadPosition = 0;
                            for (String c : paths) {
                                if (!ImageGridView.ICON_ADD.equals(c)) {
                                    UploadImage u = new UploadImage(c);
                                    imgs.add(u);
                                }
                            }
                        }
                        issueDesc = str;
                        continueAbnormal();
                    }
                });
        dialog.setPositiveButtonBg(R.drawable.dialog_btn_f2f2f2_left);
        dialog.setNegativeButtonBg(R.drawable.dialog_btn_f2f2f2_right);
        dialog.setMainBg(R.drawable.dialog_f2f2f2_bg);
        dialog.setCancelable(false);
        dialog.show();
    }

    /**
     * 判断是否已完成
     */
    private void checkFinished() {
        if (isScanFinished()) {
            toast(getString(R.string.back_order_check_finish));
            finish();
        } else {
            startDelay();
        }
    }

    /**
     * 本地检查
     *
     * @return 是否继续服务端校验
     */
    private boolean check(String code) {
        boolean isIn = false;
        for (CheckBackOrder item : list) {
            if (item.getBackOrderCode().equals(code)) {
                isIn = true;

                if (CheckBackOrder.EXCEPTION.equals(item.getCheckStatus())) {
                    toast(getString(R.string.duplicate_abnormal));
                    return false;
                } else if (CheckBackOrder.NORMAL.equals(item.getCheckStatus())) {
                    toast(getString(R.string.duplicate_code));
                    return false;
                }
                break;
            }
        }
        if (!isIn) {
            toast(getString(R.string.back_order_check_not_in));
            return false;
        }
        return true;
    }

    /**
     * 是否已完成扫描
     */
    private boolean isScanFinished() {
        for (CheckBackOrder item : list) {
            if (CheckBackOrder.NONEXIST.equals(item.getCheckStatus())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void finish() {
        Intent i = new Intent();
        i.putExtra(EXTRA_LIST, list);
        setResult(Activity.RESULT_OK, i);
        super.finish();
    }
}