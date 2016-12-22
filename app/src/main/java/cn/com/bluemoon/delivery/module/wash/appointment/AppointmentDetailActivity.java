package cn.com.bluemoon.delivery.module.wash.appointment;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.wash.collect.ClothingRecordDetailActivity;

/**
 * 收衣订单详情页面
 */
public class AppointmentDetailActivity extends BaseActivity implements OnListItemClickListener {

    private String collectCode;

    public static void actionStart(Context context, String collectCode) {
        Intent intent = new Intent(context, ClothingRecordDetailActivity.class);
        intent.putExtra("collectCode", collectCode);
        context.startActivity(intent);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        collectCode = getIntent().getStringExtra("collectCode");
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_appointment_details;
    }

    @Override
    public void onItemClick(Object item, View view, int position) {

    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {

    }
}
