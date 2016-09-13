package cn.com.bluemoon.delivery.module.card;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseActivity;

/**
 * Created by allenli on 2016/9/12.
 */
public class AlarmModifyActivity extends BaseActivity {

    @Bind(R.id.time_picker)
    TimePicker timePicker;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_alarm_modify;
    }

    @Override
    public void initView() {

        //设置成24小时，隐藏AM/PM picker
        timePicker.setIs24HourView(true);
        //修改TimePicker字体的大小
        setNumberPickerTextSize(timePicker);
        //修改TimePicker中NumberPicker的大小
        resizeTimerPicker(timePicker);


        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                setNumberPickerTextSize(timePicker);
                resizeTimerPicker(timePicker);
            }
        });

    }

    @Override
    public void initData() {

    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {

    }

    private List<NumberPicker> findNumberPicker(ViewGroup viewGroup)
    {
        List<NumberPicker> npList = new ArrayList<>();
        View child = null;

        if (null != viewGroup)
        {
            for (int i = 0; i < viewGroup.getChildCount(); i++)
            {
                child = viewGroup.getChildAt(i);
                if (child instanceof NumberPicker)
                {
                    npList.add((NumberPicker)child);
                }
                else if (child instanceof LinearLayout)
                {
                    List<NumberPicker> result = findNumberPicker((ViewGroup)child);
                    if (result.size() > 0)
                    {
                        return result;
                    }
                }
            }
        }

        return npList;
    }

    private EditText findEditText(NumberPicker np)
    {
        if (null != np)
        {
            for (int i = 0; i < np.getChildCount(); i++)
            {
                View child = np.getChildAt(i);

                if (child instanceof EditText)
                {

                    return (EditText)child;
                }
            }
        }

        return null;
    }

    private void setNumberPickerTextSize(ViewGroup viewGroup)
    {
        List<NumberPicker> npList = findNumberPicker(viewGroup);
        if (null != npList)
        {
            for (NumberPicker np : npList)
            {
                EditText et = findEditText(np);
              //  et.setFocusable(false);
                et.setGravity(Gravity.CENTER);
                et.setTextColor(getResources().getColor(R.color.text_black));
                et.setTextSize(28);

            }
        }
    }

    private void resizeTimerPicker(TimePicker tp)
    {
        List<NumberPicker> npList = findNumberPicker(tp);

        for (NumberPicker np : npList)
        {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 0, 10, 0);
            np.setLayoutParams(params);
        }
    }

}
