package cn.com.bluemoon.delivery.ui.common;

import android.app.Activity;
import android.os.Bundle;

import cn.com.bluemoon.delivery.R;

/**
 * Created by tangqiwei on 2017/5/26.
 */

public class TestUiAcitivty extends Activity {
    BmMarkView markView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_ui);
        markView= (BmMarkView) findViewById(R.id.mark_view);
        markView.setMarkViewWidthAndText(555);
    }
}
