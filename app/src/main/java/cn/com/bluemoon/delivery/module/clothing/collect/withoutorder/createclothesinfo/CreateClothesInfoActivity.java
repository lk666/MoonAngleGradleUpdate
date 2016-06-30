package cn.com.bluemoon.delivery.module.clothing.collect.withoutorder.createclothesinfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.clothing.ClothesTypeInfo;
import cn.com.bluemoon.delivery.app.api.model.clothing.ResultClothesTypeInfos;
import cn.com.bluemoon.delivery.module.base.BaseActionBarActivity;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.lib.view.ScrollGridView;

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
    @Bind(R.id.gv_clothing_name)
    ScrollGridView gvClothingName;
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
    @Bind(R.id.v_div_btn_left)
    View vDivBtnLeft;
    @Bind(R.id.btn_ok)
    Button btnOk;
    @Bind(R.id.v_div_btn_right)
    View vDivBtnRight;

    /**
     * 活动编号
     */
    private String activityCode;

    /**
     * 服务类型选中的item
     */
    ClothesTypeInfoView selectedTypeView;

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
    }


    /**
     * 设置服务类型数据
     */
    public void setClothesTypeInfo(List<ClothesTypeInfo> datas) {
        if (datas == null || datas.size() < 1) {
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

        selectedTypeView = (ClothesTypeInfoView) llType.getChildAt(0);
        selectedTypeView.setChecked(true);
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
        }
    }

    @Override
    public void onItemClick(Object item, View view, int position) {

    }

    private void getData() {
        showProgressDialog();
        DeliveryApi.getClothesTypeInfos(activityCode, ClientStateManager.getLoginToken(this),
                baseHandler);
    }

    @Override
    protected void onResponseSuccess(String responseString) {
        // 获取服务类型
        ResultClothesTypeInfos type = JSON.parseObject(responseString,
                ResultClothesTypeInfos.class);
        if (type != null) {
            setData(type);
            return;
        }
    }

    private void setData(ResultClothesTypeInfos type) {
        setClothesTypeInfo(type.getClothesTypeInfos());
    }

    public static void actionStart(Activity context, String activityCode, int requestCode) {
        Intent intent = new Intent(context, CreateClothesInfoActivity.class);
        intent.putExtra(EXTRA_ACTIVITY_CODE, activityCode);
        context.startActivityForResult(intent, requestCode);
    }
}
