package cn.com.bluemoon.delivery.module.wash.appointment.clothesinfo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.AppointmentApi;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.appointment.ModifyUploadAppointClothesInfo;
import cn.com.bluemoon.delivery.app.api.model.wash.appointment.OneLevel;
import cn.com.bluemoon.delivery.app.api.model.wash.appointment.ResponseClothingPic;
import cn.com.bluemoon.delivery.app.api.model.wash.appointment.ResultOneLevelTypeList;
import cn.com.bluemoon.delivery.app.api.model.wash.appointment.ResultWashGoodsList;
import cn.com.bluemoon.delivery.app.api.model.wash.appointment.TwoLevel;
import cn.com.bluemoon.delivery.app.api.model.wash.appointment.UploadAppointClothesInfo;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.wash.collect.AddPhotoAdapter;
import cn.com.bluemoon.delivery.module.wash.collect.ClothingPic;
import cn.com.bluemoon.delivery.module.wash.collect.SavedClothingPic;
import cn.com.bluemoon.delivery.module.wash.collect.withorder.ManualInputCodeActivity;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DialogUtil;
import cn.com.bluemoon.delivery.utils.FileUtil;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.utils.LibConstants;
import cn.com.bluemoon.lib.view.CommonAlertDialog;
import cn.com.bluemoon.lib.view.ScrollGridView;
import cn.com.bluemoon.lib.view.TakePhotoPopView;

/**
 * 创建收衣单编辑衣物
 */
public class ModifyClothesInfoActivity extends BaseActivity implements
        OnListItemClickListener {
    private static final String EXTRA_EXIST_CLOTHES_CODE = "EXTRA_EXIST_CLOTHES_CODE";

    /**
     * 修改过的衣物数据
     */
    public static final String RESULT_UPLOAD_CLOTHES_INFO = "RESULT_UPLOAD_CLOTHES_INFO";
    /**
     * 删除的衣物数据编号
     */
    public static final String RESULT_DELETE_CLOTHES_CODE = "RESULT_DELETE_CLOTHES_CODE";
    /**
     * 删除衣物信息成功
     */
    public final static int RESULT_CODE_DELETE_CLOTHES_SUCCESS = 0x45;

    /**
     * 最多上传图片数量
     */
    private static final int MAX_UPLOAD_IMG = 10;

    /**
     * 本地保存的衣物数据
     */
    private final static String EXTRA_UPLOAD_CLOTHES_INFO = "EXTRA_UPLOAD_CLOTHES_INFO";

    /**
     * 本地保存的衣物数据
     */
    private UploadAppointClothesInfo extraUploadClothesInfo;

    private static final int REQUEST_CODE_MANUAL = 0x43;
    private static final int REQUEST_CODE_ONE_LEVEL = 0x777;
    private static final int REQUEST_CODE_WASH_GOODS = 0x666;
    private static final int REQUEST_CODE_UPLOAD_IMG = 0x555;
    private static final int REQUEST_CODE_VALIDATE_CLOTHES_CODE = 0x444;
    private static final int REQUEST_CODE_DELETE_PIC = 0x333;
    @BindView(R.id.ll_type)
    LinearLayout llType;
    @BindView(R.id.ll_clothes_name)
    LinearLayout llClothesName;
    @BindView(R.id.tv_number)
    TextView tvNumber;
    @BindView(R.id.iv_number)
    ImageView ivNumber;
    @BindView(R.id.et_flaw)
    EditText etFlaw;
    @BindView(R.id.v_div_flaw)
    View vDivFlaw;
    @BindView(R.id.et_backup)
    EditText etBackup;
    @BindView(R.id.sgv_photo)
    ScrollGridView sgvPhoto;
    @BindView(R.id.btn_ok)
    Button btnOk;
    @BindView(R.id.rb_not_falw)
    RadioButton rbNotFalw;
    @BindView(R.id.rb_falw)
    RadioButton rbFalw;
    @BindView(R.id.rg_falw)
    RadioGroup rgFalw;
    @BindView(R.id.rb_not_stain)
    RadioButton rbNotStain;
    @BindView(R.id.rb_stain)
    RadioButton rbStain;
    @BindView(R.id.rg_stain)
    RadioGroup rgStain;

    @BindView(R.id.btn_delete)
    Button btnDelete;

    /**
     * 是否初始化，只有初始化时才执行导入修改前的界面数据
     */
    private boolean isInited;

    private TakePhotoPopView takePhotoPop;

    /**
     * 已上传衣物图片列表adapter
     */
    private AddPhotoAdapter clothingAdapter;
    /**
     * 一级分类选中的item
     */
    private OneLevelView selectedTypeView;

    /**
     * 商品选中的item
     */
    private WashView selectedNameView;

    /**
     * 已上传的图片列表
     */
    private List<ClothingPic> clothesImg;

    /**
     * 本地删除的图片列表，在点击保存时提交
     */
    private List<DeleteClothingPic> deleteClothesImg;

    private static class DeleteClothingPic extends ClothingPic {
        /**
         * 是否已成功在服务器删除
         */
        boolean isDelete = false;

        DeleteClothingPic(ClothingPic pic) {
            if (pic == null) {
                return;
            }
            setImgId(pic.getImgId());
            setImgPath(pic.getImgPath());
            isDelete = false;
        }
    }

    /**
     * 修改衣物信息时，已保存的图片
     */
    private int savedImg = 0;

    private ArrayList<String> existClothesCode;
    public static void actionStart(Activity context, UploadAppointClothesInfo
            uploadClothesInfo, ArrayList<String> existClothesCode, int requestCode) {
        Intent intent = new Intent(context, ModifyClothesInfoActivity.class);
        intent.putStringArrayListExtra(EXTRA_EXIST_CLOTHES_CODE, existClothesCode);
        intent.putExtra(EXTRA_UPLOAD_CLOTHES_INFO, uploadClothesInfo);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        extraUploadClothesInfo = (UploadAppointClothesInfo) getIntent().getSerializableExtra
                (EXTRA_UPLOAD_CLOTHES_INFO);

        existClothesCode = getIntent().getStringArrayListExtra(EXTRA_EXIST_CLOTHES_CODE);
        if (existClothesCode == null) {
            existClothesCode = new ArrayList<>();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_modify_appointment_clothes_info;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.title_clothing_book_in);
    }

    @Override
    public void initView() {
        selectedTypeView = null;
        setClothesNameSelected(null);
        llType.removeAllViews();
        llClothesName.removeAllViews();

        etBackup.setText("");

        rgFalw.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_falw) {
                    vDivFlaw.setVisibility(View.VISIBLE);
                    etFlaw.setVisibility(View.VISIBLE);
                } else {
                    vDivFlaw.setVisibility(View.GONE);
                    etFlaw.setVisibility(View.GONE);
                }
            }
        });

        clothingAdapter = new AddPhotoAdapter(this, this);
        sgvPhoto.setAdapter(clothingAdapter);
    }

    private void setClothesNameSelected(WashView nameView) {
        if (nameView == null) {
            selectedNameView = null;
        } else if (nameView != selectedNameView) {
            nameView.setChecked(true);
            if (selectedNameView != null) {
                selectedNameView.setChecked(false);
            }
            selectedNameView = nameView;
        }
    }

    private void addAddImage() {
        if (clothesImg.size() < MAX_UPLOAD_IMG) {
            ClothingPic addPic = new ClothingPic();
            addPic.setImgId(AddPhotoAdapter.ADD_IMG_ID);
            clothesImg.add(addPic);
        }
    }

    @Override
    public void initData() {
        showWaitDialog();
        AppointmentApi.oneLevelTypeList(getToken(),
                getNewHandler(REQUEST_CODE_ONE_LEVEL, ResultOneLevelTypeList.class, false)
        );
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        switch (requestCode) {
            // 获取一级分类
            case REQUEST_CODE_ONE_LEVEL:
                ResultOneLevelTypeList type = (ResultOneLevelTypeList) result;
                setData(type);
                break;
            // 获取至尊洗衣商品列表
            case REQUEST_CODE_WASH_GOODS:
                ResultWashGoodsList clothesType = (ResultWashGoodsList) result;
                setClothesTypeData(clothesType);
                break;
            // 上传图片
            case REQUEST_CODE_UPLOAD_IMG:
                ResponseClothingPic responsePic = (ResponseClothingPic) result;

                ClothingPic pic = new ClothingPic();
                pic.setImgId(responsePic.getImgId());
                pic.setImgPath(responsePic.getImgPath());

                clothesImg.add(clothesImg.size() - 1, pic);
                if (clothesImg.size() > MAX_UPLOAD_IMG) {
                    clothesImg.remove(clothesImg.size() - 1);
                }
                clothingAdapter.notifyDataSetChanged();
                PublicUtil.showToast(getString(R.string.upload_success));
                break;
            // 验证衣物编码
            case REQUEST_CODE_VALIDATE_CLOTHES_CODE:
                tvNumber.setText(scanCode);
                break;
            // 删除照片
            case REQUEST_CODE_DELETE_PIC:
                deleteClothesImg.get(delImgPos).isDelete = true;
                delImgPos++;
                // 循环删除，完了就发送保存
                sendDeletePic(delImgPos);
                break;
        }
    }

    /**
     * 删除本地图片
     */
    private void deletePic(int delImgPos) {
        ClothingPic deletePic = clothesImg.get(delImgPos);
        if (deletePic instanceof SavedClothingPic) {
            savedImg--;
        }

        deleteClothesImg.add(new DeleteClothingPic(deletePic));
        clothesImg.remove(delImgPos);
        if (!AddPhotoAdapter.ADD_IMG_ID.equals(clothesImg.get
                (clothesImg.size() - 1)
                .getImgId())) {
            addAddImage();
        }
        clothingAdapter.notifyDataSetChanged();
    }

    /**
     * 设置商品列表
     */
    private void setClothesTypeData(ResultWashGoodsList result) {
        List<TwoLevel> clothesTypeConfigs = result.getTwoLevelList();
        //        Collections.sort(clothesTypeConfigs);

        if (clothesTypeConfigs == null || clothesTypeConfigs.isEmpty()) {
            return;
        }

        // 选中的衣物名称类型
        String nameCode = "";
        if (!isInited && extraUploadClothesInfo != null) {
            nameCode = extraUploadClothesInfo.getWashCode();
        }
        WashView selectedView = null;

        int count = clothesTypeConfigs.size();
        for (int i = 0; i < count; i++) {
            TwoLevel type = clothesTypeConfigs.get(i);
            WashView v = new WashView(this, type, i);
            v.setOnClickListener(nameClick);
            llClothesName.addView(v);
            if (nameCode.equals(type.getWashCode())) {
                selectedView = v;
            }
        }
        if (!isInited && selectedView != null) {
            setClothesNameSelected(selectedView);
        } else {
            setClothesNameSelected(null);
        }

        if (!isInited && extraUploadClothesInfo != null) {
            setInitClothesInfoData(extraUploadClothesInfo);
        }
    }

    /**
     * 使用原始数据初始化界面
     */
    private void setInitClothesInfoData(UploadAppointClothesInfo extraUploadClothesInfo) {
        tvNumber.setText(extraUploadClothesInfo.getClothesCode());

        if (extraUploadClothesInfo.getHasFlaw() == 0) {
            rbNotFalw.setChecked(true);
        } else {
            rbFalw.setChecked(true);
        }

        etFlaw.setText(extraUploadClothesInfo.getFlawDesc());

        if (extraUploadClothesInfo.getHasStain() == 0) {
            rbNotStain.setChecked(true);
        } else {
            rbStain.setChecked(true);
        }

        etBackup.setText(extraUploadClothesInfo.getRemark());

        clothesImg = new ArrayList<>();
        deleteClothesImg = new ArrayList<>();
        List<ClothingPic> pics = extraUploadClothesInfo.getClothingPics();
        if (pics != null) {
            int count = pics.size();
            for (int i = 0; i < count; i++) {
                SavedClothingPic scp = new SavedClothingPic(pics.get(i));
                clothesImg.add(scp);
            }
        }

        savedImg = clothesImg.size();

        addAddImage();

        clothingAdapter.setList(clothesImg);
        clothingAdapter.notifyDataSetChanged();
        isInited = true;
    }

    /**
     * 点击商品
     */
    private View.OnClickListener nameClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            WashView nameView = (WashView) view;
            setClothesNameSelected(nameView);
        }
    };

    private void setData(ResultOneLevelTypeList type) {
        setClothesTypeInfo(type.getOneLevelList());
    }

    /**
     * 设置一级分类数据
     */
    public void setClothesTypeInfo(List<OneLevel> datas) {
        if (datas == null || datas.isEmpty()) {
            return;
        }
        llType.removeAllViews();
        int count = datas.size();


        // 选中的服务类型
        String typeCode = "";
        if (!isInited && extraUploadClothesInfo != null) {
            typeCode = extraUploadClothesInfo.getOneLevelCode();
        }

        OneLevelView selectedView = null;

        for (int i = 0; i < count; i++) {
            OneLevel type = datas.get(i);
            OneLevelView v = new OneLevelView(this, type, i);
            v.setOnClickListener(typeClick);
            llType.addView(v);
            if (typeCode.equals(type.getOneLevelCode())) {
                selectedView = v;
            }
        }

        if (!isInited && selectedView != null) {
            setClothesTypeSelected(selectedView, true);
        } else {
            setClothesTypeSelected((OneLevelView) llType.getChildAt(0), true);
        }
    }

    /**
     * 点击一级分类
     */
    private View.OnClickListener typeClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            OneLevelView typeView = (OneLevelView) view;
            setClothesTypeSelected(typeView, false);
        }
    };

    private void setClothesTypeSelected(OneLevelView typeView, boolean isInit) {
        if (typeView != selectedTypeView) {
            typeView.setChecked(true);
            if (selectedTypeView != null) {
                selectedTypeView.setChecked(false);
            }
            selectedTypeView = typeView;
            setClothesNameSelected(null);
            // 改变一级分类时自动去获取对应的衣物名称
            llClothesName.removeAllViews();
            getClothingData(selectedTypeView.getOneLevel().getOneLevelCode(), isInit);
        }
    }

    private void getClothingData(String typeCode, boolean isInit) {
        if (!isInit) {
            showWaitDialog();
        }
        AppointmentApi.washGoodsList(typeCode, getToken(),
                getNewHandler(REQUEST_CODE_WASH_GOODS, ResultWashGoodsList.class));
    }

    /**
     * 判断输入数据完整性
     */
    private boolean checkBtnOK() {
        String errStr = null;
        if (TextUtils.isEmpty(tvNumber.getText().toString())) {
            errStr = getString(R.string.btn_check_err_clothes_code_empty);
        } else if (clothingAdapter == null || clothingAdapter.getCount() < 2) {
            errStr = getString(R.string.btn_check_err_clothes_photo_empty);
        } else if (selectedNameView == null) {
            errStr = getString(R.string.two_level_empty);
        } else if (selectedTypeView == null) {
            errStr = getString(R.string.one_level_empty);
        }

        if (!rbNotFalw.isChecked()) {
            if (TextUtils.isEmpty(etFlaw.getText().toString())) {
                errStr = getString(R.string.clothing_book_in_falw_empty);
            }
        }

        if (TextUtils.isEmpty(errStr)) {
            return true;
        } else {
            toast(errStr);
            return false;
        }
    }

    /**
     * 保存修改
     */
    private void save() {
        ModifyUploadAppointClothesInfo tmpUploadClothesInfo
                = new ModifyUploadAppointClothesInfo();

        tmpUploadClothesInfo.setWashName(selectedNameView.getTwoLevel().getWashName());
        tmpUploadClothesInfo.setOneLevelName(
                selectedTypeView.getOneLevel().getOneLevelName());
        tmpUploadClothesInfo.setClothesCode(tvNumber.getText().toString());
        tmpUploadClothesInfo.setClothesImgIds(clothingAdapter.getAllIdsString());
        tmpUploadClothesInfo.setFlawDesc(etFlaw.getText().toString());
        tmpUploadClothesInfo.setHasFlaw(rbNotFalw.isChecked() ? 0 : 1);
        tmpUploadClothesInfo.setHasStain(rbNotStain.isChecked() ? 0 : 1);
        tmpUploadClothesInfo.setRemark(etBackup.getText().toString());
        tmpUploadClothesInfo.setOneLevelCode(selectedTypeView.getOneLevel()
                .getOneLevelCode());
        tmpUploadClothesInfo.setWashCode(selectedNameView.getTwoLevel().getWashCode());
        tmpUploadClothesInfo.setClothingPics(getActualClothesImg(clothesImg));
        tmpUploadClothesInfo.setImgPath(clothesImg.get(0).getImgPath());

        tmpUploadClothesInfo.setInitClothesCode(extraUploadClothesInfo.getClothesCode
                ());
        Intent i = new Intent();
        i.putExtra(RESULT_UPLOAD_CLOTHES_INFO, tmpUploadClothesInfo);
        setResult(RESULT_OK, i);

        hideWaitDialog();
        finish();
    }

    int delImgPos;

    /**
     * 发送删除图片请求
     */
    private void sendDeletePic(int pos) {
        if (pos + 1 >= deleteClothesImg.size()) {
            save();
        }
        // 删除图片
        else {
            showWaitDialog();
            DeliveryApi.delImg(deleteClothesImg.get(pos).getImgId(), getToken(),
                    getNewHandler(REQUEST_CODE_DELETE_PIC, ResultBase.class, false));
        }
    }

    @OnClick({R.id.btn_ok, R.id.tv_number, R.id.btn_delete, R.id.iv_number})
    public void onClick(View view) {
        switch (view.getId()) {
            // 确定按钮
            case R.id.btn_ok:
                if (checkBtnOK()) {
                    delImgPos = 0;
                    for (DeleteClothingPic dp : deleteClothesImg) {
                        if (dp.isDelete) {
                            delImgPos++;
                        } else {
                            break;
                        }
                    }

                    showWaitDialog();
                    sendDeletePic(delImgPos);
                }
                break;
            // 删除
            case R.id.btn_delete:
                CommonAlertDialog.Builder dialog = new CommonAlertDialog.Builder(
                        ModifyClothesInfoActivity.this);
                dialog.setMessage(getString(R.string.clothing_book_delete_hint));
                dialog.setPositiveButton(R.string.btn_ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showWaitDialog();
                                Intent i = new Intent();
                                i.putExtra(RESULT_DELETE_CLOTHES_CODE,
                                        extraUploadClothesInfo.getClothesCode());
                                setResult(RESULT_CODE_DELETE_CLOTHES_SUCCESS, i);
                                finish();
                            }

                        });
                dialog.setNegativeButton(R.string.btn_cancel, null);
                dialog.show();

                break;

            //  输入衣物编码
            case R.id.tv_number:
            case R.id.iv_number:
                goScanCode();
                break;
            default:
                break;
        }
    }

    /**
     * 返回去除掉添加图片按钮的图片列表
     */
    private List<ClothingPic> getActualClothesImg(List<ClothingPic> oriList) {
        if (oriList == null || oriList.isEmpty()) {
            return new ArrayList<>();
        } else {
            ClothingPic last = oriList.get(oriList.size() - 1);
            if (AddPhotoAdapter.ADD_IMG_ID.equals(last.getImgId())) {
                oriList.remove(last);
            }
            return oriList;
        }
    }

    /**
     * 打开扫码界面
     */
    private void goScanCode() {
        PublicUtil.openClothScan(this, getString(R.string.coupons_scan_code_title),
                getString(R.string.with_order_collect_manual_input_code_btn),
                Constants.REQUEST_SCAN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }

        switch (requestCode) {
            case Constants.REQUEST_SCAN:
                // 扫码返回
                if (resultCode == Activity.RESULT_OK) {
                    String resultStr = data.getStringExtra(LibConstants.SCAN_RESULT);
                    handleScanCodeBack(resultStr);
                }
                //   跳转到手动输入
                else if (resultCode == Constants.RESULT_SCAN) {
                    Intent intent = new Intent(this, ManualInputCodeActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_MANUAL);
                }
                break;

            // 手动输入返回
            case REQUEST_CODE_MANUAL:
                // 数字码返回
                if (resultCode == Activity.RESULT_OK) {
                    String resultStr = data.getStringExtra(ManualInputCodeActivity
                            .RESULT_EXTRA_CODE);
                    handleScanCodeBack(resultStr);
                }
                //  跳转到扫码输入
                else if (resultCode == ManualInputCodeActivity.RESULT_CODE_SCANE_CODE) {
                    goScanCode();
                }
                break;

            // 拍照
            case Constants.TAKE_PIC_RESULT:
                if (takePhotoPop != null) {
                    Bitmap bm = takePhotoPop.getTakeImageBitmap();
                    uploadImg(bm);
                }
                break;

            // 选择照片
            case Constants.CHOSE_PIC_RESULT:
                if (takePhotoPop != null) {
                    Bitmap bm = takePhotoPop.getPickImageBitmap(data);
                    uploadImg(bm);
                }
                break;
            default:
                break;
        }
    }

    private void uploadImg(Bitmap bm) {
        showWaitDialog();
        DeliveryApi.uploadClothesImg(getToken(), FileUtil.getBytes(bm),
                getNewHandler(REQUEST_CODE_UPLOAD_IMG, ResponseClothingPic.class));
    }

    /**
     * 扫描到/输入的数字码
     */
    private String scanCode;

    /**
     * 新增模式下处理扫码、手动输入数字码返回
     */
    private void handleScanCodeBack(String code) {
        if (existClothesCode.contains(code)) {
            toast(getString(R.string.err_exist_clothes_code, code));
            return;
        }

        scanCode = code;
        showWaitDialog();
        DeliveryApi.validateClothesCode(scanCode, getToken(),
                getNewHandler(REQUEST_CODE_VALIDATE_CLOTHES_CODE, ResultBase.class));
    }

    @Override
    public void onItemClick(Object item, View view, int position) {
        // 衣物照片
        if (item instanceof ClothingPic) {
            ClothingPic pic = (ClothingPic) item;
            // 添加相片按钮
            if (AddPhotoAdapter.ADD_IMG_ID.equals(pic.getImgId())) {
                if (takePhotoPop == null) {
                    takePhotoPop = new TakePhotoPopView(this, Constants
                            .TAKE_PIC_RESULT, Constants.CHOSE_PIC_RESULT);
                }
                takePhotoPop.getPic(view);
            }

            // 已上传图片
            else {
                switch (view.getId()) {
                    //  删除图片
                    case R.id.iv_delete:
                        if (clothesImg.size() < 3) {
                            PublicUtil.showToast(getString(R.string.create_collect_can_not_delete));
                            return;
                        }

                        // 修改时，至少保留一张已保存的图片
                        if (savedImg < 2 && pic instanceof SavedClothingPic) {
                            PublicUtil.showToast(getString(R.string.modify_collect_can_not_delete));
                            return;
                        }
                        deletePic(position);
                        break;
                    case R.id.iv_pic:
                        // TODO: lk 2016/6/25 实现毛玻璃效果 http://blog.csdn
                        // .net/lvshaorong/article/details/50392057
                        DialogUtil.showPictureDialog(this, pic.getImgPath());
                    default:
                        break;
                }
            }
        }
    }
}
