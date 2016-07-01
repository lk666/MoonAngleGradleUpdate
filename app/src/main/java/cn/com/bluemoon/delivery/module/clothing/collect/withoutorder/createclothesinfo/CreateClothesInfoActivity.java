package cn.com.bluemoon.delivery.module.clothing.collect.withoutorder.createclothesinfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.clothing.ClothesType;
import cn.com.bluemoon.delivery.app.api.model.clothing.ClothesTypeInfo;
import cn.com.bluemoon.delivery.app.api.model.clothing.ResultClothesTypeInfos;
import cn.com.bluemoon.delivery.app.api.model.clothing.ResultClothesTypeList;
import cn.com.bluemoon.delivery.module.base.BaseActionBarActivity;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.clothing.collect.ClothesNameView;
import cn.com.bluemoon.delivery.module.clothing.collect.ClothesTypeInfoView;
import cn.com.bluemoon.lib.view.ScrollGridView;
import cn.com.bluemoon.lib.view.switchbutton.SwitchButton;

/**
 * 创建收衣订单新增衣物
 * Created by luokai on 2016/6/30.
 */
public class CreateClothesInfoActivity extends BaseActionBarActivity implements
        OnListItemClickListener {
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
    @Bind(R.id.tv_backup)
    TextView tvBackup;
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

    /**
     * 活动编号
     */
    private String activityCode;

    /**
     * 服务类型选中的item
     */
    ClothesTypeInfoView selectedTypeView;

    /**
     * 衣物名称选中的item
     */
    ClothesNameView selectedNameView;

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
        selectedNameView = null;
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
                    etBackup.setGravity(Gravity.LEFT);
                } else {
                    etBackup.setGravity(Gravity.RIGHT);
                }
            }
        });

        btnOk.setEnabled(false);

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

    private void setClothesTypeSelected(ClothesTypeInfoView typeView) {
        if (typeView != selectedTypeView) {
            typeView.setChecked(true);
            if (selectedTypeView != null) {
                selectedTypeView.setChecked(false);
            }
            selectedTypeView = typeView;
            getClothingData(selectedTypeView.getTypeInfo().getTypeCode());
        }
    }

    private void getClothingData(String typeCode) {
        String token = ClientStateManager.getLoginToken(this);
        showProgressDialog();
        DeliveryApi.getClothesTypeConfigs(token, typeCode, createResponseHandler(new IHttpResponseHandler() {
            @Override
            public void onResponseSuccess(String responseString) {
                // TODO: lk 2016/6/30 待测试
                // 获取衣物配置项
                ResultClothesTypeList clothesType = JSON.parseObject(responseString,
                        ResultClothesTypeList.class);
                setClothesTypeData(clothesType);
            }
        }));
    }

    @Override
    public void onItemClick(Object item, View view, int position) {

    }

    private void getData() {
        showProgressDialog();
        DeliveryApi.getClothesTypeInfos(activityCode, ClientStateManager.getLoginToken(this),
                createResponseHandler(new IHttpResponseHandler() {
                    @Override
                    public void onResponseSuccess(String responseString) {
                        // TODO: lk 2016/6/30 待测试
                        // 获取服务类型
                        ResultClothesTypeInfos type = JSON.parseObject(responseString,
                                ResultClothesTypeInfos.class);
                        setData(type);
                    }
                }));
    }

    /**
     * 设置衣物名称列表
     */
    private void setClothesTypeData(ResultClothesTypeList result) {
        List<ClothesType> clothesTypeConfigs = result.getClothesTypeConfigs();
        Collections.sort(clothesTypeConfigs);

        if (clothesTypeConfigs == null || clothesTypeConfigs.isEmpty()) {
            return;
        }

        llClothesName.removeAllViews();
        int count = clothesTypeConfigs.size();
        for (int i = 0; i < count; i++) {
            ClothesType type = clothesTypeConfigs.get(i);
            ClothesNameView v = new ClothesNameView(this, type, i);
            v.setOnClickListener(nameClick);
            llClothesName.addView(v);
        }

        setClothesNameSelected((ClothesNameView) llClothesName.getChildAt(0));
    }

    private void setClothesNameSelected(ClothesNameView nameView) {
        if (nameView != selectedNameView) {
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

    private void setData(ResultClothesTypeInfos type) {
        setClothesTypeInfo(type.getClothesTypeInfos());
    }

    public static void actionStart(Activity context, String activityCode, int requestCode) {
        Intent intent = new Intent(context, CreateClothesInfoActivity.class);
        intent.putExtra(EXTRA_ACTIVITY_CODE, activityCode);
        context.startActivityForResult(intent, requestCode);
    }
}
