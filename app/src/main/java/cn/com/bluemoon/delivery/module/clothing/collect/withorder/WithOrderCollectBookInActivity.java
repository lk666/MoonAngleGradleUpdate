package cn.com.bluemoon.delivery.module.clothing.collect.withorder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.module.base.BaseActionBarActivity;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.clothing.collect.ClothingBookInActivity;
import cn.com.bluemoon.delivery.order.ReturnOrderActivity;
import cn.com.bluemoon.delivery.ui.CommonActionBar;

// TODO: lk 2016/6/14 界面

/**
 * 收衣登记
 * Created by luokai on 2016/6/12.
 */
public class WithOrderCollectBookInActivity extends BaseActionBarActivity {
    private final static int REQUEST_CODE_CLOTHING_BOOK_IN_ACTIVITY = 0x13;
    public final static String EXTRA_ORDER = "EXTRA_ORDER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_order_collect_book_in);

        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: lk 2016/6/14 衣物登记
                Intent intent = new Intent(WithOrderCollectBookInActivity.this,
                        ClothingBookInActivity.class);
                Bundle bundle = new Bundle();
                // bundle.putSerializable(WithOrderCollectBookInActivity, orderClicked);
                intent.putExtras(bundle);
                WithOrderCollectBookInActivity.this.startActivityForResult(intent,
                        REQUEST_CODE_CLOTHING_BOOK_IN_ACTIVITY);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }

        switch (requestCode) {

            // TODO: lk 2016/6/14  衣物登记返回
            case REQUEST_CODE_CLOTHING_BOOK_IN_ACTIVITY:
                if (resultCode == Activity.RESULT_OK) {

                }
                break;
        }
    }

    @Override
    protected int getActionBarTitleRes() {
        return R.string.title_with_order_collect_book_in;
    }
}
