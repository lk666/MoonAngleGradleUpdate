package cn.com.bluemoon.delivery.ui.selectordialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;

/**
 * 简单选择弹窗示例
 */
public class SampleActivity extends Activity {
    @Bind(R.id.btn1)
    Button btn1;
    @Bind(R.id.btn2)
    Button btn2;
    @Bind(R.id.btn3)
    Button btn3;
    @Bind(R.id.btn4)
    Button btn4;
    @Bind(R.id.btn5)
    Button btn5;
    @Bind(R.id.btn6)
    Button btn6;
    @Bind(R.id.wheel)
    SimpleWheelView wheel;

    public static void actionStart(Context context) {
        Intent i = new Intent(context, SampleActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        ButterKnife.bind(this);

        wheel.initData(getData(20), 3);
        wheel.setOnSelectListener(new OnSelectChangedListener() {
            @Override
            public void onEndSelected(int index, Object text) {
                Log.e("onEndSelected", index + "," + text.toString());
            }
        });
    }

    private ArrayList<String> getData(int size) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(String.valueOf(i) + "WHEEL");
        }
        return list;
    }

    @OnClick({R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4})
    public void onClick(View view) {
        switch (view.getId()) {
            // 滚到默认
            case R.id.btn1:
                wheel.reSetToDefault();
                break;
            // 滚到位置0
            case R.id.btn2:
                wheel.setDefaultTo(0);
                break;
            // 重新设置数据，并滚到位置0
            case R.id.btn3:
                wheel.resetData(getData(5), 0);
                break;
            // 获取选中
            case R.id.btn4:
                Toast.makeText(SampleActivity.this, wheel.getSelectedIndex() + "," +
                        wheel.getSelectedText(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @OnClick({R.id.btn5, R.id.btn6})
    public void onClickSelect(View view) {
        switch (view.getId()) {
            // 日期选择
            case R.id.btn5:
                long year = 3600000L * 24L * 365L;
                long cur = System.currentTimeMillis();
                long start = cur - year * 6;
                long end = cur + year * 12;
                DateSelectDialog d = new DateSelectDialog(this, "DATE TITLE",
                        start, end, cur, new OnButtonClickListener() {
                    @Override
                    public void onOKButtonClick(long timeStamp) {
                        Calendar c = Calendar.getInstance();
                        c.setTimeInMillis(timeStamp);
                        Toast.makeText(SampleActivity.this, c.get(Calendar.YEAR) + "/"
                                + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar
                                .DAY_OF_MONTH), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancleButtonClick() {
                        Toast.makeText(SampleActivity.this, "Date Cancle", Toast
                                .LENGTH_SHORT).show();
                    }

                    @Override
                    public String getCompareTips() {
                        return null;
                    }
                });
                d.show();
                break;
            // 时间选择
            case R.id.btn6:
                long c = System.currentTimeMillis();
                TimeSelectDialog t = new TimeSelectDialog(this, "TIME TITLE",
                        c, new OnButtonClickListener() {
                    @Override
                    public void onOKButtonClick(long timeStamp) {
                        Calendar c = Calendar.getInstance();
                        c.setTimeInMillis(timeStamp);
                        Toast.makeText(SampleActivity.this, c.get(Calendar.HOUR_OF_DAY) + ":"
                                + (c.get(Calendar.MINUTE)), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancleButtonClick() {
                        Toast.makeText(SampleActivity.this, "Time Cancle", Toast
                                .LENGTH_SHORT).show();
                    }

                    @Override
                    public String getCompareTips() {
                        return null;
                    }
                });
                t.show();
                break;
        }
    }
}
