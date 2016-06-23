package cn.com.bluemoon.delivery.module.clothing.collect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.clothing.ClothesType;
import cn.com.bluemoon.delivery.app.api.model.clothing.ResultClothesTypeList;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.ResultRegisterClothesCode;
import cn.com.bluemoon.delivery.module.base.BaseActionBarActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.clothing.collect.withorder.ManualInputCodeActivity;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.ImageLoaderUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.ViewHolder;
import cn.com.bluemoon.lib.utils.LibConstants;
import cn.com.bluemoon.lib.view.ScrollGridView;
import cn.com.bluemoon.lib.view.switchbutton.SwitchButton;

/**
 * 衣物登记
 * Created by luokai on 2016/6/12.
 */
public class ClothingBookInActivity extends BaseActionBarActivity implements
        OnListItemClickListener {
    private static final int RESULT_CODE_MANUAL = 0x23;
    private static final int REQUEST_CODE_MANUAL = 0x43;

    /**
     * 衣物类型编号
     */
    public static final String EXTRA_TYPE_CODE = "EXTRA_TYPE_CODE";
    /**
     * 衣物类型编号(如：洗衣服务A类)，不为空时表示是新增衣物
     */
    private String typeCode;

    /**
     * 衣物类型名称(如：洗衣服务A类)
     */
    public static final String EXTRA_TYPE_NAME = "EXTRA_TYPE_NAME";
    /**
     * 衣物类型编号(如：洗衣服务A类)，不为空时表示是新增衣物
     */
    private String typeName;

    /**
     * 收衣单号（可空）
     */
    public static final String EXTRA_COLLECT_CODE = "EXTRA_COLLECT_CODE";

    /**
     * 收衣单号
     */
    private String collectCode;

    /**
     * 洗衣服务订单号
     */
    public static final String EXTRA_OUTER_CODE = "EXTRA_OUTER_CODE";

    /**
     * 洗衣服务订单号
     */
    private String outerCode;

    /**
     * 衣物编码（修改时必填）
     */
    public static final String EXTRA_CLOTHES_CODE = "EXTRA_CLOTHES_CODE";

    /**
     * 衣物编码
     */
    private String clothesCode;

    /**
     * 打开方式
     */
    public static final String EXTRA_MODE = "EXTRA_MODE";
    /**
     * 打开方式：有订单收衣添加
     */
    public static final String MODE_ADD = "MODE_ADD";
    /**
     * 打开方式：有订单收衣修改、删除
     */
    public static final String MODE_MODIFY = "MODE_MODIFY";
    /**
     * 打开方式
     */
    private String extraMode;

    /**
     * 衣物名称列表adapter
     */
    NameAdapter nameAdapter;

    /**
     * 已上传衣物图片列表adapter
     */
    AddPhotoAdapter clothingAdapter;

    /**
     * 保存衣物信息成功
     */
    public final static int RESULT_CODE_SAVE_CLOTHES_SUCCESS = 0x44;
    /**
     * 删除衣物信息成功
     */
    public final static int RESULT_CODE_DELETE_CLOTHES_SUCCESS = 0x45;

    /**
     * 修改时需要等获取衣物配置项和获取衣物信息返回后才结束初始化
     */
    private int modifyInitLatch = 0;

    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.gv_clothing_name)
    GridView gvClothingName;
    @Bind(R.id.tv_number)
    TextView tvNumber;
    @Bind(R.id.sb_falw)
    SwitchButton sbFalw;
    @Bind(R.id.et_flaw)
    EditText etFlaw;
    @Bind(R.id.v_div_flaw)
    View vDivFlaw;
    @Bind(R.id.sb_stain)
    SwitchButton sbStain;
    @Bind(R.id.et_backup)
    EditText etBackup;
    @Bind(R.id.sgv_photo)
    ScrollGridView sgvPhoto;

    @Bind(R.id.v_div_btn_left)
    View vDivLeft;
    @Bind(R.id.btn_delete)
    Button btnDelete;
    @Bind(R.id.v_div_btn)
    View vDivBtn;
    @Bind(R.id.btn_ok)
    Button btnOk;
    @Bind(R.id.v_div_btn_right)
    View vDivRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothing_book_in);
        ButterKnife.bind(this);
        setIntentData();

        initView();
        if (extraMode.equals(MODE_ADD)) {
            getAddClothingData();
        } else if (extraMode.equals(MODE_MODIFY)) {
            modifyInitLatch = 2;
            getModifyClothingData();
        }
    }

    private void setIntentData() {
        typeCode = getIntent().getStringExtra(EXTRA_TYPE_CODE);
        if (typeCode == null) {
            typeCode = "";
        }

        typeName = getIntent().getStringExtra(EXTRA_TYPE_NAME);
        if (typeName == null) {
            typeName = "";
        }

        collectCode = getIntent().getStringExtra(EXTRA_COLLECT_CODE);
        if (collectCode == null) {
            collectCode = "";
        }

        outerCode = getIntent().getStringExtra(EXTRA_OUTER_CODE);
        if (outerCode == null) {
            outerCode = "";
        }

        extraMode = getIntent().getStringExtra(EXTRA_MODE);
        if (extraMode == null || !extraMode.equals(MODE_MODIFY)) {
            extraMode = MODE_ADD;
        }

        clothesCode = getIntent().getStringExtra(EXTRA_CLOTHES_CODE);
        if (clothesCode == null) {
            clothesCode = "";
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
        tvTitle.setText(getString(R.string.clothing_book_in_base_info) + "-" + typeName);

        nameAdapter = new NameAdapter(this, this);
        gvClothingName.setAdapter(nameAdapter);

        btnOk.setEnabled(false);
        clothingAdapter = new AddPhotoAdapter(this, this);
        sgvPhoto.setAdapter(clothingAdapter);

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

        if (extraMode.equals(MODE_ADD)) {
            btnDelete.setVisibility(View.GONE);
            vDivBtn.setVisibility(View.GONE);
        } else if (extraMode.equals(MODE_MODIFY)) {
            btnDelete.setVisibility(View.VISIBLE);
            vDivBtn.setVisibility(View.VISIBLE);

            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) vDivLeft.getLayoutParams();
            lp.setMargins(getResources().getDimensionPixelSize(R.dimen.div_btn_clothes_book_in),
                    0, 0, 0);
            vDivBtn.setLayoutParams(lp);
            vDivLeft.setLayoutParams(lp);
            vDivRight.setLayoutParams(lp);
        }
    }

    private void getAddClothingData() {
        String token = ClientStateManager.getLoginToken(this);
        showProgressDialog();
        DeliveryApi.getClothesTypeConfigs(token, typeCode, clothesTypesHandler);
    }

    private void getModifyClothingData() {
        String token = ClientStateManager.getLoginToken(this);
        showProgressDialog();
        DeliveryApi.getClothesTypeConfigs(token, typeCode, clothesTypesHandler);
        DeliveryApi.registerClothesCode(token, clothesCode, registerClothesCodeHandler);
    }

    /**
     * 获取衣物信息返回
     */
    AsyncHttpResponseHandler registerClothesCodeHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            // TODO: lk 2016/6/20 待测试
            LogUtils.d(getDefaultTag(), "registerClothesCode result = " + responseString);
            --modifyInitLatch;
            try {
                ResultRegisterClothesCode result = JSON.parseObject(responseString,
                        ResultRegisterClothesCode.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    setClothesInfo(result);
                    return;
                } else {
                    PublicUtil.showErrorMsg(ClothingBookInActivity.this, result);
                }
            } catch (Exception e) {
                LogUtils.e(getDefaultTag(), e.getMessage());
                PublicUtil.showToastServerBusy();
            }
            checkModifyInitFinish(0);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            --modifyInitLatch;
            LogUtils.e(getDefaultTag(), throwable.getMessage());
            PublicUtil.showToastServerOvertime();
            checkModifyInitFinish(0);
        }
    };

    /**
     * 修改时衣物信息、衣物名称都获取完后刷新界面
     *
     * @param selectPos 衣物名称选中项
     */
    private void checkModifyInitFinish(int selectPos) {
        if (modifyInitLatch == 0) {
            dismissProgressDialog();
            checkBtnOKEnable();
            if (selectPos > nameAdapter.getSelectedPos()) {
                nameAdapter.setSelectedPos(selectPos);
                nameAdapter.notifyDataSetInvalidated();
            }
        }
    }

    /**
     * 设置衣物信息
     */
    private void setClothesInfo(ResultRegisterClothesCode result) {
        tvNumber.setText(result.getClothesCode());
        tvNumber.setEnabled(false);

        sbFalw.setChecked(result.getHasFlaw() == 1);
        etFlaw.setText(result.getFlawDesc());

        sbStain.setChecked(result.getHasStain() == 1);

        etBackup.setText(result.getRemark());

        clothingAdapter.setList(result.getClothesImg());
        clothingAdapter.notifyDataSetChanged();
        checkModifyInitFinish(nameAdapter.getSelectedIndex(result.getClothesnameCode()));
    }


    /**
     * 获取衣物配置项（衣物名称）返回
     */
    AsyncHttpResponseHandler clothesTypesHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            // TODO: lk 2016/6/20 待测试
            LogUtils.d(getDefaultTag(), "getClothesTypeConfigs result = " + responseString);
            dismissProgressDialog();
            try {
                ResultClothesTypeList result = JSON.parseObject(responseString,
                        ResultClothesTypeList.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    setClothesTypeData(result);
                } else {
                    PublicUtil.showErrorMsg(ClothingBookInActivity.this, result);
                }
            } catch (Exception e) {
                LogUtils.e(getDefaultTag(), e.getMessage());
                PublicUtil.showToastServerBusy();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            LogUtils.e(getDefaultTag(), throwable.getMessage());
            dismissProgressDialog();
            PublicUtil.showToastServerOvertime();
        }
    };

    /**
     * 设置衣物名称列表
     *
     * @param result
     */
    private void setClothesTypeData(ResultClothesTypeList result) {
        List<ClothesType> clothesTypeConfigs = result.getClothesTypeConfigs();
        Collections.sort(clothesTypeConfigs);

        nameAdapter.setList(clothesTypeConfigs);
        if (extraMode.equals(MODE_ADD)) {
            nameAdapter.notifyDataSetChanged();
        } else if (extraMode.equals(MODE_MODIFY)) {
            checkModifyInitFinish(0);
        }
    }

    /**
     * 设置确定按钮的可点击性
     */
    private void checkBtnOKEnable() {
        if (nameAdapter == null || nameAdapter.getSelectedPos() < 0 ||
                TextUtils.isEmpty(tvNumber.getText().toString()) ||
                clothingAdapter == null || clothingAdapter.getCount() < 1) {
            btnOk.setEnabled(false);
        } else {
            btnOk.setEnabled(true);
        }
    }

    @OnClick({R.id.btn_ok, R.id.btn_delete, R.id.tv_number})
    public void onClick(View view) {
        switch (view.getId()) {
            // 确定
            case R.id.btn_ok:
                showProgressDialog();
                String clothesnameCode = nameAdapter.getSelectedNameCode();
                String clothesCode = tvNumber.getText().toString();
                int hasFlaw = sbFalw.isChecked() ? 1 : 0;
                String flawDesc = etFlaw.getText().toString();
                int hasStain = sbStain.isChecked() ? 1 : 0;
                String remark = etBackup.getText().toString();
                String clothesImgIds = clothingAdapter.getAllIdsString();

                DeliveryApi.registerCollectInfo(ClientStateManager.getLoginToken(this),
                        collectCode, typeCode, clothesnameCode, clothesCode, hasFlaw, flawDesc,
                        hasStain, remark, clothesImgIds, outerCode, registerCollectInfoHandler);

                break;

            // 删除
            case R.id.btn_delete:
                showProgressDialog();
                String code = tvNumber.getText().toString();
                DeliveryApi.delCollectInfo(ClientStateManager.getLoginToken(this), code,
                        delCollectInfoHandler);
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
     * 点击删除响应
     */
    AsyncHttpResponseHandler delCollectInfoHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            // TODO: lk 2016/6/20 待测试
            LogUtils.d(getDefaultTag(), "delCollectInfo result = " + responseString);
            dismissProgressDialog();
            try {
                ResultBase result = JSON.parseObject(responseString, ResultBase.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    setResult(RESULT_CODE_DELETE_CLOTHES_SUCCESS);
                    finish();
                } else {
                    PublicUtil.showErrorMsg(ClothingBookInActivity.this, result);
                }
            } catch (Exception e) {
                LogUtils.e(getDefaultTag(), e.getMessage());
                PublicUtil.showToastServerBusy();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            LogUtils.e(getDefaultTag(), throwable.getMessage());
            dismissProgressDialog();
            PublicUtil.showToastServerOvertime();
        }
    };

    /**
     * 打开扫码界面
     */
    private void goScanCode() {
        PublicUtil.openScanOrder(this, null, getString(R.string.coupons_scan_code_title),
                getString(R.string.with_order_collect_manual_input_code_btn),
                Constants.REQUEST_SCAN, RESULT_CODE_MANUAL);
    }

    /**
     * 点击确定响应
     */
    AsyncHttpResponseHandler registerCollectInfoHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            // TODO: lk 2016/6/20 待测试
            LogUtils.d(getDefaultTag(), "getClothesTypeConfigs result = " + responseString);
            dismissProgressDialog();
            try {
                ResultBase result = JSON.parseObject(responseString, ResultBase.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    setResult(RESULT_CODE_SAVE_CLOTHES_SUCCESS);
                    finish();
                } else {
                    PublicUtil.showErrorMsg(ClothingBookInActivity.this, result);
                }
            } catch (Exception e) {
                LogUtils.e(getDefaultTag(), e.getMessage());
                PublicUtil.showToastServerBusy();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            LogUtils.e(getDefaultTag(), throwable.getMessage());
            dismissProgressDialog();
            PublicUtil.showToastServerOvertime();
        }
    };

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
                else if (resultCode == RESULT_CODE_MANUAL) {
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

            default:
                break;
        }
    }

    /**
     * 处理扫码、手动输入数字码返回
     *
     * @param code
     */
    private void handleScaneCodeBack(String code) {
        // TODO: lk  2016/6/20 处理扫码、手动输入数字码返回
        PublicUtil.showToast("处理扫码、手动输入数字码返回" + code);
        checkBtnOKEnable();
    }

    /**
     * 衣物名称Adapter
     */
    class NameAdapter extends BaseListAdapter<ClothesType> {

        /**
         * 当前选中的位置
         */
        private int selectedPos = -1;

        public void setSelectedPos(int pos) {
            this.selectedPos = pos;
        }

        public int getSelectedPos() {
            return selectedPos;
        }

        public NameAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_clothes_type;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            final ClothesType type = (ClothesType) getItem(position);
            if (type == null) {
                return;
            }

            ImageView ivType = ViewHolder.get(convertView, R.id.iv_type);
            ImageView ivChecked = ViewHolder.get(convertView, R.id.iv_checked);
            TextView tvType = ViewHolder.get(convertView, R.id.tv_type);

            ImageLoaderUtil.displayImage(context, type.getImgPath(), ivType);

            tvType.setText(type.getClothesName());
            if (position == selectedPos) {
                ivChecked.setVisibility(View.VISIBLE);
                tvType.setTextColor(getResources().getColor(R.color.btn_blue));
            } else {
                ivChecked.setVisibility(View.GONE);
                tvType.setTextColor(getResources().getColor(R.color.text_black_light));
            }

            setClickEvent(isNew, position, convertView);
        }

        public String getSelectedNameCode() {
            ClothesType type = (ClothesType) getItem(selectedPos);
            if (type != null) {
                return type.getClothesnameCode();
            }
            return null;
        }

        /**
         * 获取选中的衣物名称序号
         *
         * @param clothesnameCode
         * @return
         */
        public int getSelectedIndex(String clothesnameCode) {
            int num = getCount();
            for (int i = 0; i < num; i++) {
                ClothesType type = (ClothesType) getItem(i);
                if (type.getClothesnameCode().equals(clothesnameCode)) {
                    return i;
                }
            }
            return -1;
        }
    }

    @Override
    public void onItemClick(Object item, View view, int position) {
        // 点击衣物名称
        if (item instanceof ClothesType) {
            nameAdapter.setSelectedPos(position);
        }
        // 衣物照片
        else if (item instanceof ClothingPic) {
            ClothingPic pic = (ClothingPic) item;
            // 添加相片按钮
            if (TextUtils.isEmpty(pic.getImgId())) {
                // TODO: lk 2016/6/20 添加图片
            }

            // 已上传图片
            else {
                switch (view.getId()) {
                    case R.id.iv_delete:
                        // TODO: lk 2016/6/20 删除图片
                        break;
                    case R.id.iv_pic:
                        // TODO: lk 2016/6/20 浏览图片
                        break;
                }
            }
        }

    }
}
