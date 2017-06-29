package cn.com.bluemoon.delivery.module.wash.appointment.clothesinfo;

import android.app.Activity;
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

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.AppointmentApi;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
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
import cn.com.bluemoon.delivery.module.wash.collect.withorder.ManualInputCodeActivity;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DialogUtil;
import cn.com.bluemoon.delivery.utils.FileUtil;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.utils.LibConstants;
import cn.com.bluemoon.lib.view.ScrollGridView;
import cn.com.bluemoon.lib.view.TakePhotoPopView;

/**
 * 创建收衣单新增衣物
 */
public class CreateClothesInfoActivity extends BaseActivity implements
        OnListItemClickListener {
    public static final String RESULT_UPLOAD_CLOTHES_INFO = "RESULT_UPLOAD_CLOTHES_INFO";

    /**
     * 最多上传图片数量
     */
    private static final int MAX_UPLOAD_IMG = 10;

    private static final int REQUEST_CODE_MANUAL = 0x43;
    private static final int REQUEST_CODE_ONE_LEVEL = 0x777;
    private static final int REQUEST_CODE_WASH_GOODS = 0x666;
    private static final int REQUEST_CODE_UPLOAD_IMG = 0x555;
    private static final int REQUEST_CODE_VALIDATE_CLOTHES_CODE = 0x444;
    private static final int REQUEST_CODE_DELETE_PIC = 0x333;
    private static final String EXTRA_EXIST_CLOTHES_CODE = "EXTRA_EXIST_CLOTHES_CODE";
    @Bind(R.id.ll_type)
    LinearLayout llType;
    @Bind(R.id.ll_clothes_name)
    LinearLayout llClothesName;
    @Bind(R.id.tv_number)
    TextView tvNumber;
    @Bind(R.id.iv_number)
    ImageView ivNumber;
    @Bind(R.id.et_flaw)
    EditText etFlaw;
    @Bind(R.id.v_div_flaw)
    View vDivFlaw;
    @Bind(R.id.et_backup)
    EditText etBackup;
    @Bind(R.id.sgv_photo)
    ScrollGridView sgvPhoto;
    @Bind(R.id.btn_ok)
    Button btnOk;
    @Bind(R.id.rb_not_falw)
    RadioButton rbNotFalw;
    @Bind(R.id.rg_falw)
    RadioGroup rgFalw;
    @Bind(R.id.rb_not_stain)
    RadioButton rbNotStain;
    @Bind(R.id.rg_stain)
    RadioGroup rgStain;

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
     * 删除的图片位置
     */
    private int delImgPos;

    private ArrayList<String> existClothesCode;

    public static void actionStart(Activity context, int requestCode,
                                   ArrayList<String> existClothesCode) {
        Intent intent = new Intent(context, CreateClothesInfoActivity.class);
        intent.putStringArrayListExtra(EXTRA_EXIST_CLOTHES_CODE, existClothesCode);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        existClothesCode = getIntent().getStringArrayListExtra(EXTRA_EXIST_CLOTHES_CODE);
        if (existClothesCode == null) {
            existClothesCode = new ArrayList<>();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_create_appointment_clothes_info;
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
        initClothesImgList();
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

    /**
     * 衣物图片初始化
     */
    private void initClothesImgList() {
        clothesImg = new ArrayList<>();

        addAddImage();

        clothingAdapter.setList(clothesImg);
        clothingAdapter.notifyDataSetChanged();
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
                clothesImg.remove(delImgPos);
                if (!AddPhotoAdapter.ADD_IMG_ID.equals(clothesImg.get
                        (clothesImg.size() - 1)
                        .getImgId())) {
                    addAddImage();
                }
                clothingAdapter.notifyDataSetChanged();
                break;
        }
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

        int count = clothesTypeConfigs.size();
        for (int i = 0; i < count; i++) {
            TwoLevel type = clothesTypeConfigs.get(i);
            WashView v = new WashView(this, type, i);
            v.setOnClickListener(nameClick);
            llClothesName.addView(v);
        }

        setClothesNameSelected(null);
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
        for (int i = 0; i < count; i++) {
            OneLevel type = datas.get(i);
            OneLevelView v = new OneLevelView(this, type, i);
            v.setOnClickListener(typeClick);
            llType.addView(v);
        }

        setClothesTypeSelected((OneLevelView) llType.getChildAt(0), true);
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

    @OnClick({R.id.btn_ok, R.id.tv_number, R.id.iv_number})
    public void onClick(View view) {
        switch (view.getId()) {
            // 确定按钮
            case R.id.btn_ok:
                if (checkBtnOK()) {
                    UploadAppointClothesInfo tmpUploadClothesInfo = new UploadAppointClothesInfo();

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

                    Intent i = new Intent();
                    i.putExtra(RESULT_UPLOAD_CLOTHES_INFO, tmpUploadClothesInfo);
                    setResult(RESULT_OK, i);
                    finish();
                }
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
                        showWaitDialog();
                        delImgPos = position;
                        DeliveryApi.delImg(pic.getImgId(), getToken(),
                                getNewHandler(REQUEST_CODE_DELETE_PIC, ResultBase.class));
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
