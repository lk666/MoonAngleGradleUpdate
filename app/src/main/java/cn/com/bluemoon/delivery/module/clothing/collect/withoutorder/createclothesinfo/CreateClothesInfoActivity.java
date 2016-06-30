package cn.com.bluemoon.delivery.module.clothing.collect.withoutorder.createclothesinfo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.clothing.ClothesTypeInfo;
import cn.com.bluemoon.delivery.module.base.BaseActionBarActivity;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.lib.view.ScrollGridView;

/**
 * 创建收衣订单新增衣物
 * Created by luokai on 2016/6/30.
 */
public class CreateClothesInfoActivity extends BaseActionBarActivity implements
        OnListItemClickListener {


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
    }

    private void getData() {
    }

    @Override
    protected int getActionBarTitleRes() {
        return R.string.title_clothing_book_in;
    }

    /**
     * 初始化衣物界面
     */
    private void initView() {

    }

    @Override
    public void onItemClick(Object item, View view, int position) {
        //  todo 选择服务类型
        if (item instanceof ClothesTypeInfo) {
        }
    }

    public static void actionStart(Activity context, int requestCode) {
        Intent intent = new Intent(context, CreateClothesInfoActivity.class);
        context.startActivityForResult(intent,requestCode);
    }

}
