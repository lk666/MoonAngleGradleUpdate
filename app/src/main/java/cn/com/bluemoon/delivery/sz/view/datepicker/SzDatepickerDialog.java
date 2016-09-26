package cn.com.bluemoon.delivery.sz.view.datepicker;

import android.app.AlertDialog;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.Calendar;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.sz.util.DateUtil;
import cn.com.bluemoon.delivery.sz.view.calendar.CustomDate;
import cn.com.bluemoon.delivery.sz.view.datepicker.adapter.NumericWheelAdapter;
import cn.com.bluemoon.delivery.sz.view.datepicker.widget.OnWheelChangedListener;
import cn.com.bluemoon.delivery.sz.view.datepicker.widget.WheelView;

/**
 * Created by dujiande on 2016/8/19.
 */
public class SzDatepickerDialog {

    private Context context;
    private WheelView yearWv,monthWv,dayMv;

    private int startYear = 1990;
    private int offsetYear = 10;
    private int visiableItemNum = 5;
    private int textSize = 20;
    private DateConfirmListeren dateConfirmListeren = null;

    public interface DateConfirmListeren{
        public void getDateTime(CustomDate tempDate);
    }

    public DateConfirmListeren getDateConfirmListeren() {
        return dateConfirmListeren;
    }

    public void setDateConfirmListeren(DateConfirmListeren dateConfirmListeren) {
        this.dateConfirmListeren = dateConfirmListeren;
    }

    OnWheelChangedListener onWheelChangedListener = new OnWheelChangedListener() {
        @Override
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            if(yearWv != null && monthWv != null){
                initDay(yearWv.getCurrentItem()+startYear,monthWv.getCurrentItem()+1);
            }
        }
    };

    public SzDatepickerDialog(Context context){
        this.context = context;
    }


    /**
     * 显示日期
     */
    public void showDateDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .create();
        dialog.show();
        Window window = dialog.getWindow();
        // 设置布局
        window.setContentView(R.layout.datapick);
        // 设置宽高
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        // 设置弹出的动画效果
        window.setWindowAnimations(R.style.AnimBottom);


        Calendar c = Calendar.getInstance();
        int curYear = c.get(Calendar.YEAR);
        int curMonth = c.get(Calendar.MONTH) + 1;//通过Calendar算出的月数要+1
        int curDate = c.get(Calendar.DATE);
        yearWv = (WheelView) window.findViewById(R.id.year);
        initYear(curYear);
        monthWv = (WheelView) window.findViewById(R.id.month);
        initMonth();
        dayMv = (WheelView)window.findViewById(R.id.day);
        initDay(curYear,curMonth);
        dayMv.setCurrentItem(curDate - 1);

        yearWv.setCurrentItem(curYear - startYear);
        monthWv.setCurrentItem(curMonth - 1);

        yearWv.setVisibleItems(visiableItemNum);
        monthWv.setVisibleItems(visiableItemNum);


        // 设置监听
        Button ok = (Button) window.findViewById(R.id.set);
        Button cancel = (Button) window.findViewById(R.id.cancel);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDate tempDate = new CustomDate(yearWv.getCurrentItem()+startYear,
                        monthWv.getCurrentItem()+1,dayMv.getCurrentItem()+1);
                if(dateConfirmListeren != null){
                    dateConfirmListeren.getDateTime(tempDate);
                }
                dialog.cancel();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        LinearLayout cancelLayout = (LinearLayout) window.findViewById(R.id.view_none);
        cancelLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                dialog.cancel();
                return false;
            }
        });

    }

    /**
     * 初始化年  年份不同 另起adapter
     */
    private void initYear(int curYear) {
        NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(context,startYear, curYear+offsetYear);
        numericWheelAdapter.setLabel(" 年");
        setTextStyle(numericWheelAdapter);
        yearWv.setViewAdapter(numericWheelAdapter);
        yearWv.setCyclic(false);
        yearWv.addChangingListener(onWheelChangedListener);
    }

    /**
     * 初始化月
     */
    private void initMonth() {
        NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(context,1, 12, "%02d");
        numericWheelAdapter.setLabel(" 月");
        setTextStyle(numericWheelAdapter);
        monthWv.setViewAdapter(numericWheelAdapter);
        monthWv.setCyclic(true);
        monthWv.addChangingListener(onWheelChangedListener);
    }



    /**
     * 初始化天
     */
    private void initDay(int currentYear, int currentMonth) {
        NumericWheelAdapter numericWheelAdapter=new NumericWheelAdapter(context,1,
                DateUtil.getMonthDays(currentYear, currentMonth), "%02d");
        numericWheelAdapter.setLabel(" 日");
        setTextStyle(numericWheelAdapter);
        dayMv.setViewAdapter(numericWheelAdapter);
        dayMv.setCyclic(true);
    }

    private void setTextStyle(NumericWheelAdapter numericWheelAdapter){
        numericWheelAdapter.setTextSize(textSize);  //设置字体大小
    }

}
