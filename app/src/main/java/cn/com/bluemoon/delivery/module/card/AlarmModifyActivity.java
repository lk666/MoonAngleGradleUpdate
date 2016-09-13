package cn.com.bluemoon.delivery.module.card;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.ui.selectordialog.OnSelectChangedListener;
import cn.com.bluemoon.delivery.ui.selectordialog.SimpleWheelView;

/**
 * Created by allenli on 2016/9/12.
 */
public class AlarmModifyActivity extends BaseActivity {

    @Bind(R.id.wheel_hour)
    SimpleWheelView wheelHour;

    @Bind(R.id.wheel_minute)
    SimpleWheelView wheelMinute;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_alarm_modify;
    }


    private ArrayList<String> getHourList() {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            if(i<10){
                list.add("0"+i);
            }else {
                list.add(String.valueOf(i));
            }
        }
        return list;
    }

    private ArrayList<String> getMinuteList() {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            if(i<10){
                list.add("0"+i);
            }else {
                list.add(String.valueOf(i));
            }
        }
        return list;
    }


    @Override
    public void initView() {

        Date date = new Date();

        wheelHour.initData(getHourList(),2);
        wheelMinute.initData(getHourList(),2);



        wheelHour.setOnSelectListener(new OnSelectChangedListener() {
            @Override
            public void onEndSelected(int index, Object text) {
                Log.e("onEndSelected", index + "," + text.toString());
            }
        });


        wheelMinute.setOnSelectListener(new OnSelectChangedListener() {
            @Override
            public void onEndSelected(int index, Object text) {
                Log.e("onEndSelected", index + "," + text.toString());
            }
        });
    }

    @Override
    public void initData() {

    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {

    }

}
