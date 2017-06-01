package cn.com.bluemoon.delivery.ui.common;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
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
        list.add(new RadioItem("0","不可选",-1));
        list.add(new RadioItem("1","未选择",0));
        list.add(new RadioItem("2","未选择未选择未选择未选择未选择未选择未选择未选择未选择未选择未选",0));
        list.add(new RadioItem("3","已选择",1));
        listRadio.setData(list);

    }

    @Override
    public void initData() {

    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {

    }


}
