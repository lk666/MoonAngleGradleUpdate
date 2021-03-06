package cn.com.bluemoon.delivery.module.card.alarm;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.card.ResultRemind;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.BasePullToRefreshListViewFragment;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.utils.LibViewUtil;
import cn.com.bluemoon.lib.view.switchbutton.SwitchButton;

/**
 * Created by allenli on 2016/9/7.
 */
public class AlarmSettingFragment extends BasePullToRefreshListViewFragment {
    RemindAdapter adapter;
    List<Remind> list;

    BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Remind remind = null;

            if (Reminds.ALARM_ALERT_ACTION.equals(intent.getAction())) {
                try {
                    //  remind = (Remind) intent.getSerializableExtra(Reminds.ALARM_INTENT_EXTRA);
                    long remindId = intent.getLongExtra(Reminds.ALARM_INTENT_EXTRA, 0);
                    if (remindId != 0) {
                        remind = Reminds.getRemind(context.getContentResolver(), remindId);
                    }
                } catch (Exception ex) {

                }

                if (remind == null) {
                    return;
                }

                // Disable this alarm if it does not repeat.
                if (!new DaysOfWeek(remind.getRemindWeek()).isRepeatSet()) {
                    Reminds.enableAlarm(context, remind.getRemindId(), false);
                    if (list != null && list.size() > 0) {
                        for (Remind alarm : list
                                ) {
                            if (alarm.getRemindId() == remind.getRemindId()) {
                                alarm.isClose = true;
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                } else {
                    // Enable the next alert if there is one. The above call to
                    // enableAlarm will call setNextAlert so avoid calling it twice.
                    Reminds.setNextAlert(context);
                }
            }
        }

    };


    @Override
    protected void initPullToRefreshListView(PullToRefreshListView ptrlv) {
        //        ptrlv.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        //        ptrlv.setDividerDrawable(getResources().getDrawable(R.drawable
        // .div_left_padding_16));
        ptrlv.getRefreshableView().setDividerHeight(getResources().getDimensionPixelSize(R.dimen
                .div_height_none));
    }

    @Override
    protected void onBeforeCreateView() {
        super.onBeforeCreateView();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Reminds.ALARM_ALERT_ACTION);
        getActivity().registerReceiver(receiver, filter);
    }

    @Override
    protected void setActionBar(CommonActionBar actionBar) {
        super.setActionBar(actionBar);

        Drawable drawableFillter = getResources().getDrawable(R.mipmap.btn_alarm_title_add);

        drawableFillter.setBounds(0, 0, drawableFillter.getMinimumWidth(), drawableFillter
                .getMinimumHeight());
        actionBar.getTvRightView().setCompoundDrawables(drawableFillter, null, null, null);
        actionBar.getTvRightView().setVisibility(View.VISIBLE);
    }


    @Override
    protected void onActionBarBtnRightClick() {
        AlarmModifyActivity.startAction(this);
    }


    @Override
    public void initData() {
        ptrlv.setBackgroundColor(getResources().getColor(R.color.white));
        adapter = getNewAdapter();

        list = new ArrayList<>();
        Cursor mCursor = Reminds.getAlarmsCursor(getActivity().getContentResolver());

        if (mCursor != null) {
            if (mCursor.moveToFirst()) {
                do {
                    Remind a = new Remind(mCursor);
                    list.add(a);
                } while (mCursor.moveToNext());
            }
            mCursor.close();
        }
        adapter.setList(list);
        ptrlv.setAdapter(adapter);
        if (list.size() > 0) {
            LibViewUtil.setViewVisibility(ptrlv, View.VISIBLE);
        } else {
            showEmptyView();
        }
    }


    @SuppressLint("InflateParams")
    class RemindAdapter extends BaseListAdapter<Remind> {

        public RemindAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_alarm_list;
        }

        @Override
        protected void setView(int position, final View convertView, final ViewGroup parent,
                               boolean isNew) {
            Remind remind = (Remind) getItem(position);
            LinearLayout layoutAlarm = getViewById(R.id.layout_alarm);
            TextView txtAlarmTime = getViewById(R.id.txt_alarm_time);
            TextView txtAlarmTitle = getViewById(R.id.txt_alarm_title);
            TextView txtAlert = getViewById(R.id.txt_repeat_content);
            SwitchButton sbOpen = getViewById(R.id.sb_open);

            txtAlarmTime.setText(DateUtil.getTime(Reminds.calculateAlarm(remind), "HH:mm"));
            txtAlarmTitle.setText(remind.getRemindTitle());
            DaysOfWeek daysOfWeek = new DaysOfWeek(remind.getRemindWeek());
            if (daysOfWeek.getCoded() != 0 && daysOfWeek.getCoded() != 0x7f) {
                txtAlert.setText(getString(R.string.alarm_repeat) + getString(R.string.week) +
                        daysOfWeek.toString(getActivity(), true));
            } else {
                txtAlert.setText(getString(R.string.alarm_repeat) + daysOfWeek.toString
                        (getActivity(), true));
            }


            layoutAlarm.setTag(R.id.tag_position, position);
            sbOpen.setTag(R.id.tag_position, position);

            sbOpen.setChecked(!remind.isClose);
            setBg(layoutAlarm, txtAlarmTime, txtAlarmTitle, txtAlert, !remind.isClose);

            if (isNew) {
                layoutAlarm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Remind remind = (Remind) getItem((int) v.getTag(R.id.tag_position));
                        AlarmModifyActivity.startAction(AlarmSettingFragment.this, remind);
                    }
                });

                sbOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Remind remind =
                                (Remind) getItem((int) buttonView.getTag(R.id.tag_position));
                        if (remind.isClose == isChecked) {
                            remind.isClose = !isChecked;
                            View parent = (View) buttonView.getParent().getParent();
                            LinearLayout layoutAlarm = (LinearLayout) parent.findViewById(R.id
                                    .layout_alarm);
                            TextView txtAlarmTime = (TextView) parent.findViewById(R.id
                                    .txt_alarm_time);
                            TextView txtAlarmTitle = (TextView) parent.findViewById(R.id
                                    .txt_alarm_title);
                            TextView txtAlert = (TextView) parent.findViewById(R.id
                                    .txt_repeat_content);
                            setBg(layoutAlarm, txtAlarmTime, txtAlarmTitle, txtAlert, !remind
                                    .isClose);

                            Reminds.enableAlarm(getActivity(), remind.getRemindId(), !remind
                                    .isClose);
                            DeliveryApi.turnRemindOnOrOff(getToken(), remind.getRemindId(), !remind
                                    .isClose, getNewHandler(999, ResultBase.class));
                        }
                    }
                });
            }
        }

        private void setBg(LinearLayout layoutAlarm, TextView txtAlarmTime, TextView
                txtAlamTitle, TextView txtAlert, boolean isOpen) {
            if (isOpen) {
                if (Build.VERSION.SDK_INT > 15) {
                    layoutAlarm.setBackground(getResources().getDrawable(R.drawable
                            .btn_border_blue_alarm));
                } else {
                    layoutAlarm.setBackgroundDrawable(getResources().getDrawable(R.drawable
                            .btn_border_blue_alarm));
                }
                txtAlarmTime.setTextColor(getResources().getColor(R.color.btn_blue));
                txtAlamTitle.setTextColor(getResources().getColor(R.color.text_grep));
                txtAlert.setTextColor(getResources().getColor(R.color.text_black));
            } else {
                if (Build.VERSION.SDK_INT > 15) {
                    layoutAlarm.setBackground(getResources().getDrawable(R.drawable
                            .btn_border_grey_alarm));
                } else {
                    layoutAlarm.setBackgroundDrawable(getResources().getDrawable(R.drawable
                            .btn_border_grey_alarm));
                }

                txtAlarmTime.setTextColor(getResources().getColor(R.color.text_grep_a));
                txtAlamTitle.setTextColor(getResources().getColor(R.color.text_grep_a));
                txtAlert.setTextColor(getResources().getColor(R.color.text_grep_a));
            }
        }
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.tab_bottom_punch_alarm_setting);
    }

    @Override
    protected PullToRefreshBase.Mode getMode() {
        return PullToRefreshBase.Mode.DISABLED;
    }

    @Override
    protected RemindAdapter getNewAdapter() {
        return new RemindAdapter(getActivity(), this);
    }

    @Override
    protected List<Remind> getGetMoreList(ResultBase result) {
        return null;
    }

    @Override
    protected List<Remind> getGetDataList(ResultBase result) {
        ResultRemind resultObj = (ResultRemind) result;
        return resultObj.getRemindList();
    }

    @Override
    protected void invokeGetDataDeliveryApi(int requestCode) {
        // DeliveryApi.getRemindList(getToken(), getNewHandler(requestCode, ResultRemind.class));
        hideWaitDialog();
    }


    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {

    }

    @Override
    public void onItemClick(Object item, View view, int position) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            initData();
        }
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        if (requestCode == 999) {
            return;
        } else {
            super.onSuccessResponse(requestCode, jsonString, result);
        }
    }

    @Override
    public void onDestroyView() {
        getActivity().unregisterReceiver(receiver);
        super.onDestroyView();
    }
}
