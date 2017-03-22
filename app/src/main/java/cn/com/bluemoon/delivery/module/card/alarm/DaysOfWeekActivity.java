package cn.com.bluemoon.delivery.module.card.alarm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.Calendar;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.delivery.utils.ViewHolder;


/**
 * Created by allenli on 2016/9/18.
 */
public class DaysOfWeekActivity extends BaseActivity implements  OnListItemClickListener{

    @Bind(R.id.list_day_of_week)
    ListView listDays;

    DaysOfWeek oldDays;

    DaysOfWeek newDays;
     boolean isInit;
    String[] weekdays = new DateFormatSymbols().getWeekdays();

    String[] values = new String[]{
            weekdays[Calendar.MONDAY],
            weekdays[Calendar.TUESDAY],
            weekdays[Calendar.WEDNESDAY],
            weekdays[Calendar.THURSDAY],
            weekdays[Calendar.FRIDAY],
            weekdays[Calendar.SATURDAY],
            weekdays[Calendar.SUNDAY],
    };


    @Override
    protected int getLayoutId() {
        return R.layout.activity_days_of_week;
    }


    @Override
    public void onBeforeSetContentLayout() {
        oldDays = new DaysOfWeek(getIntent().getIntExtra("days", 0));
        newDays = new DaysOfWeek(0);
    }


    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        isInit = true;
        DaysAdapter daysAdapter = new DaysAdapter(this, this);
        //国际化
        if (getResources().getConfiguration().locale.getCountry().equals("CN")) {
            daysAdapter.setList(Arrays.asList(values));
        } else {
            daysAdapter.setList(Arrays.asList(getString(R.string.weekdays_long_ch).split(",")));
        }

        listDays.setAdapter(daysAdapter);
        isInit = false;
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {

    }

    public static void startAction(Activity context, int days) {
        Intent intent = new Intent(context, DaysOfWeekActivity.class);
        intent.putExtra("days", days);
        context.startActivityForResult(intent, 0);
    }

    @Override
    public void onItemClick(Object item, View view, int position) {
         CheckBox cbDay =  (CheckBox)view.findViewById(R.id.ck_day);
        if(cbDay.isChecked()){
            cbDay.setChecked(false);
            newDays.set(position,false);
        }else{
            cbDay.setChecked(true);
            newDays.set(position,true);
        }
    }


    @Override
    protected String getTitleString() {
        return getString(R.string.alarm_title_repeat);
    }

    class DaysAdapter extends BaseListAdapter<String> {
        public DaysAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_day_of_week_list;
        }

        @Override
        protected void setView(final int position, View convertView, ViewGroup parent, boolean isNew) {
            final String day = (String) getItem
                    (position);
            if (StringUtil.isEmptyString(day)) {
                return;
            }
            RelativeLayout layoutDay = ViewHolder.get(convertView,R.id.layout_day);
            TextView txtDay = ViewHolder.get(convertView, R.id.txt_day);
            CheckBox ckDay = ViewHolder.get(convertView, R.id.ck_day);
            txtDay.setText(day);
            boolean isCheck = oldDays.getBooleanArray()[position];
            ckDay.setChecked(isCheck);
            newDays.set(position, isCheck);
            setClickEvent(isNew,position,layoutDay);

        }
    }


    @Override
    public void onActionBarBtnLeftClick() {
        Intent data = new Intent();
        data.putExtra("days", newDays.getmDays());
        setResult(Activity.RESULT_OK, data);
        this.finish();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onActionBarBtnLeftClick();
        }
        return super.onKeyDown(keyCode, event);
    }

}
