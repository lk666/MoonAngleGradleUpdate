package cn.com.bluemoon.delivery.module.wash.enterprise.createorder;

import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.ui.NoScrollListView;

/**
 * 确认订单信息
 * Created by tangqiwei on 2017/5/5.
 */

public class EnterpriseConfirmOrderActivity extends BaseActivity {

    @Bind(R.id.txt_order_code) TextView txtOrderCode;
    @Bind(R.id.txt_state) TextView txtState;
    @Bind(R.id.txt_name) TextView txtName;
    @Bind(R.id.txt_name_code) TextView txtNameCode;
    @Bind(R.id.txt_phone) TextView txtPhone;
    @Bind(R.id.lv_clothes) NoScrollListView lvClothes;
    @Bind(R.id.txt_collect_bag) TextView txtCollectBag;
    @Bind(R.id.txt_collect_remark) TextView txtCollectRemark;

    private List<Map<String,String>> list;

    private String[] KEYS=new String[]{"name","number","price"};

    @Override
    protected int getLayoutId() {
        return R.layout.activity_confirm_order;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        iniListDate();
        lvClothes.setAdapter(new SimpleAdapter(this,list,R.layout.item_confirm_order,new String[]{KEYS[0],KEYS[1],KEYS[2]},new int[]{R.id.txt_commodity_name,R.id.txt_commodity_number,R.id.txt_commodity_price}));

    }

    private void iniListDate() {
        list=new ArrayList<>();

    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {

    }

    @OnClick(R.id.btn_deduction_cancel)
    public void cancel(){
        finish();
    }

    @OnClick(R.id.btn_deduction_affirm)
    public void affirm(){
        // TODO: 2017/5/5 请求成功再发 
//        EventBus.getDefault().post(new Object());
    }

}
