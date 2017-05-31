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
    BmSegmentView segmentView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_ui);
        segmentView= (BmSegmentView) findViewById(R.id.segment_view);

        List<String> textList=new ArrayList<>();
        textList.add("洗衣液");
        textList.add("洗洁精");
        textList.add("洁厕剂");
        segmentView.setTextList(textList);
        List<Integer> numberList=new ArrayList<>();
        numberList.add(123);
        numberList.add(0);
        numberList.add(34);
        segmentView.setNumberList(numberList);
    }
}
