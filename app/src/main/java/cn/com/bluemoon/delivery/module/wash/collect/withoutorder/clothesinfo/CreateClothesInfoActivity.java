package cn.com.bluemoon.delivery.module.wash.collect.withoutorder.clothesinfo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.clothing.ClothesType;
import cn.com.bluemoon.delivery.app.api.model.clothing.ClothesTypeInfo;
import cn.com.bluemoon.delivery.app.api.model.clothing.ResultClothesTypeInfos;
import cn.com.bluemoon.delivery.app.api.model.clothing.ResultClothesTypeList;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.UploadClothesInfo;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.wash.collect.AddPhotoAdapter;
import cn.com.bluemoon.delivery.module.wash.collect.ClothesNameView;
import cn.com.bluemoon.delivery.module.wash.collect.ClothesTypeInfoView;
import cn.com.bluemoon.delivery.module.wash.collect.ClothingPic;
import cn.com.bluemoon.delivery.module.wash.collect.withorder.ManualInputCodeActivity;
import cn.com.bluemoon.delivery.module.oldbase.BaseActionBarActivity;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DialogUtil;
import cn.com.bluemoon.delivery.utils.FileUtil;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.utils.LibConstants;
import cn.com.bluemoon.lib.view.ScrollGridView;
import cn.com.bluemoon.lib.view.TakePhotoPopView;
import cn.com.bluemoon.lib.view.switchbutton.SwitchButton;

/**
 * 创建收衣订单新增衣物
 * Created by luokai on 2016/6/30.
 */
public class CreateClothesInfoActivity extends BaseActionBarActivity implements
        OnListItemClickListener {

    public static final String RESULT_UPLOAD_CLOTHES_INFO = "RESULT_UPLOAD_CLOTHES_INFO";

    /**
     * 最多上传图片数量
     */
    private static final int MAX_UPLOAD_IMG = 10;

    private static final int REQUEST_CODE_MANUAL = 0x43;

    /**
     * 活动编码
     */
    private final static String EXTRA_ACTIVITY_CODE = "EXTRA_ACTIVITY_CODE";

    @Bind(R.id.ll_type)
    LinearLayout llType;
    @Bind(R.id.ll_clothes_name)
    LinearLayout llClothesName;
    @Bind(R.id.tv_number)
    TextView tvNumber;
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

    @Bind(R.id.sb_falw)
    SwitchButton sbFalw;
    @Bind(R.id.sb_stain)
    SwitchButton sbStain;


    private TakePhotoPopView takePhotoPop;

    /**
     * 活动编号
     */
    private String activityCode;

    /**
     * 服务类型选中的item
     */
    private ClothesTypeInfoView selectedTypeView;

    /**
     * 衣物名称选中的item
     */
    private ClothesNameView selectedNameView;

    /**
     * 已上传衣物图片列表adapter
     */
    private AddPhotoAdapter clothingAdapter;

    /**
     * 已上传的图片列表
     */
    private List<ClothingPic> clothesImg;

    /**
     * 扫描到/输入的数字码
     */
    private String scaneCode;

    /**
     * 删除的图片位置
     */
    private int delImgPos;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_clothes_info);
        ButterKnife.bind(this);

        setIntentData();
        initView();
        getData();
    }

    private void setIntentData() {
        activityCode = getIntent().getStringExtra(EXTRA_ACTIVITY_CODE);
        if (activityCode == null) {
            activityCode = "";
        }
    }

    @Override
    protected int getActionBarTitleRes() {
        return R.string.title_clothing_book_in;
    }

    /**
     * 初始化衣物界面
     */
    private void initView() {
        selectedTypeView = null;
        setClothesNameSelected(null);
        llType.removeAllViews();
        llClothesName.removeAllViews();

        etBackup.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etBackup.getLineCount() > 1) {
                    etBackup.setGravity(Gravity.START);
                } else {
                    etBackup.setGravity(Gravity.END);
                }
            }
        });

        sbFalw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
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

    private void getData() {
        showProgressDialog();
        DeliveryApi.getClothesTypeInfos(activityCode, ClientStateManager.getLoginToken(this),
                createResponseHandler(new IHttpResponseHandler() {
                    @Override
                    public void onResponseSuccess(String responseString) {
                        // 获取服务类型
                        ResultClothesTypeInfos type = JSON.parseObject(responseString,
                                ResultClothesTypeInfos.class);
                        setData(type);
                    }
                }));
    }

    private void setData(ResultClothesTypeInfos type) {
        setClothesTypeInfo(type.getClothesTypeInfos());
    }

    private void addAddImage() {
        if (clothesImg.size() < MAX_UPLOAD_IMG) {
            ClothingPic addPic = new ClothingPic();
            addPic.setImgId(AddPhotoAdapter.ADD_IMG_ID);
            clothesImg.add(addPic);
        }
    }

    /**
     * 设置服务类型数据
     */
    public void setClothesTypeInfo(List<ClothesTypeInfo> datas) {
        if (datas == null || datas.isEmpty()) {
            return;
        }
        llType.removeAllViews();
        int count = datas.size();
        for (int i = 0; i < count; i++) {
            ClothesTypeInfo type = datas.get(i);
            ClothesTypeInfoView v = new ClothesTypeInfoView(this, type, i);
            v.setOnClickListener(typeClick);
            llType.addView(v);
        }

        setClothesTypeSelected((ClothesTypeInfoView) llType.getChildAt(0));
    }

    private void setClothesTypeSelected(ClothesTypeInfoView typeView) {
        if (typeView != selectedTypeView) {
            typeView.setChecked(true);
            if (selectedTypeView != null) {
                selectedTypeView.setChecked(false);
            }
            selectedTypeView = typeView;

            // 改变服务类型时自动去获取对应的衣物名称
            llClothesName.removeAllViews();
            getClothingData(selectedTypeView.getTypeInfo().getTypeCode());
        }
    }

    /**
     * 点击服务类型
     */
    private View.OnClickListener typeClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ClothesTypeInfoView typeView = (ClothesTypeInfoView) view;
            setClothesTypeSelected(typeView);
        }
    };

    private void getClothingData(String typeCode) {
        String token = ClientStateManager.getLoginToken(this);
        showProgressDialog();
        DeliveryApi.getClothesTypeConfigs(token, typeCode, createResponseHandler(new IHttpResponseHandler() {
            @Override
            public void onResponseSuccess(String responseString) {
                // 获取衣物配置项
                ResultClothesTypeList clothesType = JSON.parseObject(responseString,
                        ResultClothesTypeList.class);
                setClothesTypeData(clothesType);
            }
        }));
    }

    /**
     * 设置衣物名称列表
     */
    private void setClothesTypeData(ResultClothesTypeList result) {
        List<ClothesType> clothesTypeConfigs = result.getClothesTypeConfigs();
        Collections.sort(clothesTypeConfigs);

        if (clothesTypeConfigs.isEmpty()) {
            return;
        }

        int count = clothesTypeConfigs.size();
        for (int i = 0; i < count; i++) {
            ClothesType type = clothesTypeConfigs.get(i);
            ClothesNameView v = new ClothesNameView(this, type, i);
            v.setOnClickListener(nameClick);
            llClothesName.addView(v);
        }

        setClothesNameSelected(null);
//        setClothesNameSelected((ClothesNameView) llClothesName.getChildAt(0));
    }

    private void setClothesNameSelected(ClothesNameView nameView) {
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
     * 点击衣物名称
     */
    private View.OnClickListener nameClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ClothesNameView nameView = (ClothesNameView) view;
            setClothesNameSelected(nameView);
        }
    };


    /**
     * 衣物图片初始化
     */
    private void initClothesImgList() {
        clothesImg = new ArrayList<>();

        addAddImage();

        clothingAdapter.setList(clothesImg);
        clothingAdapter.notifyDataSetChanged();
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
            errStr = getString(R.string.btn_check_err_clothes_name_empty);
        } else if (selectedTypeView == null) {
            errStr = getString(R.string.btn_check_err_clothes_type_empty);
        }

        if (sbFalw.isChecked()) {
            if (TextUtils.isEmpty(etFlaw.getText().toString())) {
                errStr = getString(R.string.clothing_book_in_falw_empty);
            }
        }

        if (TextUtils.isEmpty(errStr)) {
            return true;
        } else {
            PublicUtil.showToast(errStr);
            return false;
        }
    }

    @OnClick({R.id.btn_ok, R.id.tv_number})
    public void onClick(View view) {
        switch (view.getId()) {
            // 确定按钮
            case R.id.btn_ok:
                if (checkBtnOK()) {
                    UploadClothesInfo tmpUploadClothesInfo = new UploadClothesInfo();
                    tmpUploadClothesInfo.setClothesCode(tvNumber.getText().toString());
                    tmpUploadClothesInfo.setClothesImgIds(clothingAdapter.getAllIdsString());
                    tmpUploadClothesInfo.setClothesnameCode(selectedNameView.getType()
                            .getClothesnameCode());
                    tmpUploadClothesInfo.setFlawDesc(etFlaw.getText().toString());
                    tmpUploadClothesInfo.setHasFlaw(sbFalw.isChecked() ? 1 : 0);
                    tmpUploadClothesInfo.setHasStain(sbStain.isChecked() ? 1 : 0);
                    tmpUploadClothesInfo.setRemark(etBackup.getText().toString());
                    tmpUploadClothesInfo.setTypeCode(selectedTypeView.getTypeInfo().getTypeCode());
                    tmpUploadClothesInfo.setClothingPics(getActualClothesImg(clothesImg));
                    tmpUploadClothesInfo.setTypeName(selectedTypeView.getTypeInfo().getTypeName());
                    tmpUploadClothesInfo.setClothesName(selectedNameView.getType().getClothesName
                            ());

                    tmpUploadClothesInfo.setImgPath(clothesImg.get(0).getImgPath());

                    Intent i = new Intent();
                    i.putExtra(RESULT_UPLOAD_CLOTHES_INFO, tmpUploadClothesInfo);
                    setResult(RESULT_OK, i);
                    finish();
                }
                break;

            //  输入衣物编码
            case R.id.tv_number:
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

    // TODO: lk 2016/7/1 有、无订单，增、改操作界面可把逻辑抽出来公用，界面可使用include重用公共部分，底部按钮等分开

    // TODO: lk 2016/7/1 多处用到，可抽 

    /**
     * 打开扫码界面
     */
    private void goScanCode() {
        PublicUtil.openNewScan(this, getString(R.string.coupons_scan_code_title),
                getString(R.string.with_order_collect_manual_input_code_btn),Constants.REQUEST_SCAN);
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
                    handleScaneCodeBack(resultStr);
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
                    handleScaneCodeBack(resultStr);
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
        showProgressDialog();
        DeliveryApi.uploadClothesImg(ClientStateManager.getLoginToken(CreateClothesInfoActivity
                        .this),
                FileUtil.getBytes(bm), createResponseHandler(new IHttpResponseHandler() {
                    @Override
                    public void onResponseSuccess(String responseString) {
                        ClothingPic pic = JSON.parseObject(responseString, ClothingPic.class);

                        clothesImg.add(clothesImg.size() - 1, pic);
                        if (clothesImg.size() > MAX_UPLOAD_IMG) {
                            clothesImg.remove(clothesImg.size() - 1);
                        }
                        clothingAdapter.notifyDataSetChanged();
                        PublicUtil.showToast(getString(R.string.upload_success));
                    }
                }));
    }


    /**
     * 新增模式下处理扫码、手动输入数字码返回
     */
    private void handleScaneCodeBack(String code) {
        scaneCode = code;
        DeliveryApi.validateClothesCode(scaneCode, ClientStateManager.getLoginToken(this),
                createResponseHandler(new IHttpResponseHandler() {
                    @Override
                    public void onResponseSuccess(String responseString) {
                        tvNumber.setText(scaneCode);
                    }
                }));
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
                        showProgressDialog();
                        delImgPos = position;
                        DeliveryApi.delImg(pic.getImgId(), ClientStateManager.getLoginToken
                                (CreateClothesInfoActivity.this), createResponseHandler(
                                new IHttpResponseHandler() {
                                    @Override
                                    public void onResponseSuccess(String responseString) {
                                        clothesImg.remove(delImgPos);
                                        if (!AddPhotoAdapter.ADD_IMG_ID.equals(clothesImg.get
                                                (clothesImg.size() - 1)
                                                .getImgId())) {
                                            addAddImage();
                                        }
                                        clothingAdapter.notifyDataSetChanged();
                                    }
                                }));
                        break;
                    case R.id.iv_pic:
                        // TODO: lk 2016/6/25 实现毛玻璃效果 http://blog.csdn
                        // .net/lvshaorong/article/details/50392057
                        DialogUtil.showPictureDialog(CreateClothesInfoActivity.this, pic
                                .getImgPath());
                    default:
                        break;
                }
            }
        }
    }

    public static void actionStart(Activity context, String activityCode, int requestCode) {
        Intent intent = new Intent(context, CreateClothesInfoActivity.class);
        intent.putExtra(EXTRA_ACTIVITY_CODE, activityCode);
        context.startActivityForResult(intent, requestCode);
    }
}
