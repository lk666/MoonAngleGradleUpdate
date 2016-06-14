package cn.com.bluemoon.delivery.module.clothing.collect;

import android.os.Bundle;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.module.base.BaseActionBarActivity;
import cn.com.bluemoon.delivery.module.base.BaseActivity;

// TODO: lk 2016/6/14 界面

/**
 * 衣物登记
 * Created by luokai on 2016/6/12.
 */
public class ClothingBookInActivity extends BaseActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothing_book_in);
    }

    @Override
    protected int getActionBarTitleRes() {
        return R.string.title_clothing_book_in;
    }
}
