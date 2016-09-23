package cn.com.bluemoon.delivery.module.card.alarm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
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
import cn.com.bluemoon.lib.utils.LibViewUtil;

/**
 * Created by allenli on 2016/9/7.
 */
public class AlarmSettingFragment extends BasePullToRefreshListViewFragment {

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

        RemindAdapter adapter = getNewAdapter();

        List<Remind> list = new ArrayList<>();
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
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            final Remind remind = (Remind) getItem(position);
            LinearLayout layoutAlarm = getViewById(R.id.layout_alarm);
            TextView txtAlarmTime = getViewById(R.id.txt_alarm_time);
            TextView txtAlamTitle = getViewById(R.id.txt_alarm_title);
            TextView txtAlert = getViewById(R.id.txt_repeat);
            cn.com.bluemoon.lib.view.switchbutton.SwitchButton sbOpen = getViewById(R.id.sb_open);

            txtAlarmTime.setText(DateUtil.getTime(Reminds.calculateAlarm(remind), "HH:mm"));
            txtAlamTitle.setText(remind.getRemindTitle());
            DaysOfWeek daysOfWeek = new DaysOfWeek(remind.getRemindWeek());
            if(daysOfWeek.getCoded()!=0 && daysOfWeek.getCoded()!=0x7f){
                txtAlert.setText( getString(R.string.week) + daysOfWeek.toString(getActivity(),true));
            }else{
                txtAlert.setText(daysOfWeek.toString(getActivity(),true));
            }
            sbOpen.setChecked(!remind.isClose);

            layoutAlarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlarmModifyActivity.startAction(AlarmSettingFragment.this, remind);
                }
            });

            sbOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    remind.isClose = !isChecked;
                    Reminds.enableAlarm(getActivity(),remind.getRemindId(),isChecked);
                    DeliveryApi.turnRemindOnOrOff(getToken(), remind.getRemindId(), isChecked, getNewHandler(999, ResultBase.class));
                }
            });
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
        }
        super.onSuccessResponse(requestCode, jsonString, result);
    }




}
