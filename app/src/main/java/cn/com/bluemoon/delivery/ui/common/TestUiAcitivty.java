package cn.com.bluemoon.delivery.ui.common;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.R;

/**
 * Created by tangqiwei on 2017/5/26.
 */

public class TestUiAcitivty extends Activity {
    BmRankStar1 bmRankStar1;
    BmRankStar2 bmRankStar2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_ui);
        bmRankStar1= (BmRankStar1) findViewById(R.id.bmrankstar1_view);
        bmRankStar2= (BmRankStar2) findViewById(R.id.bmrankstar2_view);
    }
}
