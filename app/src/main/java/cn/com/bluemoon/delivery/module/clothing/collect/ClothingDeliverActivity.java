package cn.com.bluemoon.delivery.module.clothing.collect;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.module.base.BaseActionBarActivity;

/**
 * Created by allenli on 2016/6/22.
 */
public class ClothingDeliverActivity extends BaseActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothing_deliver);
       // ButterKnife.bind(this);
    }

    @Override
    protected int getActionBarTitleRes() {
        return R.string.clothing_deliver_title;
    }

    public static void actionStart(Context context, String collectCode) {
        Intent intent = new Intent(context, ClothingDeliverActivity.class);
        intent.putExtra("collectCode", collectCode);
        context.startActivity(intent);
    }
}
