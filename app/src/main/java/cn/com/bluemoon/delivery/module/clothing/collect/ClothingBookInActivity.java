package cn.com.bluemoon.delivery.module.clothing.collect;

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
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import java.util.Collection;
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
import cn.com.bluemoon.delivery.module.base.BaseActionBarActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.ImageLoaderUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.ViewHolder;
import cn.com.bluemoon.lib.view.ScrollGridView;
import cn.com.bluemoon.lib.view.switchbutton.SwitchButton;

// TODO: lk 2016/6/14 暂时只是新增收衣界面

/**
 * 衣物登记
 * Created by luokai on 2016/6/12.
 */
public class ClothingBookInActivity extends BaseActionBarActivity implements
        OnListItemClickListener {
    /**
     * 衣物类型编号(如：洗衣服务A类)
     */
    public static final String EXTRA_TYPE_CODE = "EXTRA_TYPE_CODE";
    /**
     * 衣物类型编号(如：洗衣服务A类)，不为空时表示是新增衣物
     */
    private String typeCode;

    /**
     * 收衣单号（可空）
     */
    public static final String EXTRA_COLLECT_CODE = "EXTRA_COLLECT_CODE";

    /**
     * 收衣单号，不为空时表示是新增衣物
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

//    /**
//     * 由于初始化可能要等待多个网络数据返回，可采用此种方式
//     */
//    private AtomicInteger addInitLatch = new AtomicInteger(2);

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
    @Bind(R.id.tv_backup)
    TextView tvBackup;
    @Bind(R.id.et_backup)
    EditText etBackup;
    @Bind(R.id.sgv_photo)
    ScrollGridView sgvPhoto;
    @Bind(R.id.btn_ok)
    Button btnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothing_book_in);
        ButterKnife.bind(this);
        typeCode = getIntent().getStringExtra(EXTRA_TYPE_CODE);

        collectCode = getIntent().getStringExtra(EXTRA_COLLECT_CODE);
        if (collectCode == null) {
            collectCode = "";
        }

        outerCode = getIntent().getStringExtra(EXTRA_OUTER_CODE);
        if (outerCode == null) {
            outerCode = "";
        }

        initAddClothing();
        getAddClothingData();
    }

    @Override
    protected int getActionBarTitleRes() {
        return R.string.title_clothing_book_in;
    }

    /**
     * 初始化新增衣物界面
     */
    private void initAddClothing() {
        tvTitle.setText(getString(R.string.clothing_book_in_base_info) + "-" + typeCode);

        nameAdapter = new NameAdapter(this, this);
        gvClothingName.setAdapter(nameAdapter);

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
    }

    private void getAddClothingData() {
        String token = ClientStateManager.getLoginToken(this);
        showProgressDialog();
        DeliveryApi.getClothesTypeConfigs(token, typeCode, clothesTypesHandler);
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
        nameAdapter.notifyDataSetChanged();
    }

    @OnClick({R.id.btn_ok, R.id.tv_number})
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
            // todo 输入衣物编码，tvNumber有文本，确定按钮可点击
            case R.id.tv_number:


                break;
            default:
                break;
        }
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
        if (resultCode == RESULT_CANCELED) {
            return;
        }

        switch (requestCode) {
            default:
                break;
        }
    }


    /**
     * 衣物名称Adapter
     */
    class NameAdapter extends BaseListAdapter<ClothesType> {

        /**
         * 当前选中的位置
         */
        private int selectedPos = 0;

        public void setSelectedPos(int pos) {
            this.selectedPos = pos;
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
            if (TextUtils.isEmpty(pic.getClothesImgId())) {
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
