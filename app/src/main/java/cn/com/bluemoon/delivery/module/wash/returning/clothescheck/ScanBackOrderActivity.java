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
import java.util.UUID;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.ResultUploadExceptionImage;
import cn.com.bluemoon.delivery.common.photopicker.PhotoPickerActivity;
import cn.com.bluemoon.delivery.common.photopicker.SelectModel;
import cn.com.bluemoon.delivery.module.base.BaseScanCodeActivity;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.wash.collect.AddPhotoAdapter;
import cn.com.bluemoon.delivery.module.wash.collect.ClothingPic;
import cn.com.bluemoon.delivery.utils.DialogUtil;
import cn.com.bluemoon.delivery.utils.FileUtil;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.utils.LibImageUtil;
import cn.com.bluemoon.lib.view.CommonAlertDialog;
import cn.com.bluemoon.lib.view.ScrollGridView;

/**
 * 扫描还衣单标签(还衣单清点)
 */
public class ScanBackOrderActivity extends BaseScanCodeActivity {

    private static final String EXTRA_TAG_CODE = "EXTRA_TAG_CODE";
    public static final String EXTRA_LIST = "LIST";
    private static final int REQUEST_CODE_SCAN_BACK_ORDER = 0x777;
    private static final int REQUEST_CODE_UPLOAD_IMG = 0x666;
    private static final int REQUEST_CODE_TAKE_IMG = 0x444;
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
                ClothingPic cp = new ClothingPic();
                cp.setImgPath(pic.getImgPath());
                cp.setImgId("");
                abnormalImgs.add(abnormalImgs.size() - 1, cp);
                if (abnormalImgs.size() > MAX_UPLOAD_IMG) {
                    abnormalImgs.remove(abnormalImgs.size() - 1);
                }
                imgAdapter.notifyDataSetChanged();
                PublicUtil.showToast(getString(R.string.upload_success));
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

    private static final int MAX_UPLOAD_IMG = 5;

    private void addAddImage() {
        if (abnormalImgs.size() < MAX_UPLOAD_IMG) {
            ClothingPic addPic = new ClothingPic();
            addPic.setImgId(AddPhotoAdapter.ADD_IMG_ID);
            abnormalImgs.add(addPic);
        }
    }

    private OnListItemClickListener onImgClickListener = new OnListItemClickListener() {
        @Override
        public void onItemClick(Object item, View view, int position) {
            // 上传图片
            if (item instanceof ClothingPic) {
                ClothingPic pic = (ClothingPic) item;
                // 添加相片按钮
                if (AddPhotoAdapter.ADD_IMG_ID.equals(pic.getImgId())) {
                    PhotoPickerActivity.actStart(ScanBackOrderActivity.this, SelectModel.SINGLE, 1,
                            true, REQUEST_CODE_TAKE_IMG);
                }

                // 已上传图片
                else {
                    switch (view.getId()) {
                        //  删除图片（本地删除）
                        case R.id.iv_delete:
                            abnormalImgs.remove(position);
                            if (!AddPhotoAdapter.ADD_IMG_ID.equals(
                                    abnormalImgs.get(abnormalImgs.size() - 1).getImgId())) {
                                addAddImage();
                            }
                            imgAdapter.notifyDataSetChanged();
                            break;
                        case R.id.iv_pic:
                            DialogUtil.showPictureDialog(ScanBackOrderActivity.this,
                                    pic.getImgPath());
                        default:
                            break;
                    }
                }
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }

        switch (requestCode) {
            // 拍照
            case REQUEST_CODE_TAKE_IMG:
                ArrayList<String> resultList = data.getStringArrayListExtra(PhotoPickerActivity
                        .EXTRA_RESULT);
                if (resultList != null && resultList.size() > 0) {
                    Bitmap bm = LibImageUtil.getImgScale(resultList.get(0), 300, false);
                    uploadImg(bm);
                }
                break;
        }
    }

    /**
     * 上传图片
     */
    private void uploadImg(Bitmap bm) {
        showWaitDialog();
        ReturningApi.uploadImage(FileUtil.getBytes(bm), UUID.randomUUID() + ".png",
                getToken(), getNewHandler(REQUEST_CODE_UPLOAD_IMG,
                        ResultUploadExceptionImage.class));
    }

    private List<ClothingPic> abnormalImgs;
    private AddPhotoAdapter imgAdapter;
    private EditText etAbnormal;
    private ScrollGridView sgvPhoto;

    /**
     * 显示异常记录弹窗
     */
    private void showAbnormalDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_abnormal, null);
        etAbnormal = (EditText) view.findViewById(R.id.et_abnormal);
        sgvPhoto = (ScrollGridView) view.findViewById(R.id.sgv_photo);

        imgAdapter = new AddPhotoAdapter(this, onImgClickListener,
                getString(R.string.clothing_book_in_phote_most_5));

        abnormalImgs = new ArrayList<>();
        addAddImage();
        imgAdapter.setList(abnormalImgs);
        sgvPhoto.setAdapter(imgAdapter);

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
                        if (str == null) {
                            return;
                        }
                        if (str.length() < 5) {
                            toast(getString(R.string.abnormal_hint));
                            return;
                        }

                        ArrayList<UploadImage> imgs = new ArrayList<>();
                        for (ClothingPic c : abnormalImgs) {
                            if (!c.getImgId().equals(AddPhotoAdapter.ADD_IMG_ID)) {
                                UploadImage u = new UploadImage(c.getImgPath());
                                imgs.add(u);
                            }
                        }

                        showWaitDialog();
                        ReturningApi.scanBackOrder(backOrderCode, imgs, str, getToken(),
                                getNewHandler(REQUEST_CODE_ABNORMAL, ResultBase.class));
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