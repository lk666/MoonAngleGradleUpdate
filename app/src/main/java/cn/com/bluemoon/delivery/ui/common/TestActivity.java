package cn.com.bluemoon.delivery.ui.common;

import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.ui.common.entity.RadioItem;

public class TestActivity extends BaseActivity {


    @Bind(R.id.view_bottom)
    BMListPaginationView viewBottom;
    @Bind(R.id.view_field)
    BMFieldParagraphView viewField;
    @Bind(R.id.item_radio)
    BMRadioItemView itemRadio;
    @Bind(R.id.list_radio)
    BMRadioListView listRadio;
    @Bind(R.id.btn3)
    BMAngleBtn3View btn3;
    @Bind(R.id.btn1)
    BMAngleBtn1View btn1;
    @Bind(R.id.btn2)
    BMAngleBtn1View btn2;

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test;
    }

    @Override
    protected String getTitleString() {
        return "通用组件测试";
    }

    @Override
    public void initView() {
        List<RadioItem> list = new ArrayList<>();
        list.add(new RadioItem("0", "不可选", -1));
        list.add(new RadioItem("1", "未选择", 0));
        list.add(new RadioItem("2", "未选择未选择未选择未选择未选择未选择未选择未选择未选择未选择未选", 0));
        list.add(new RadioItem("3", "已选择", 1));
        listRadio.setData(list);

    }

    @Override
    public void initData() {

    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {

    }


}
