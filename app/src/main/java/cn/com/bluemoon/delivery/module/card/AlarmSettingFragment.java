package cn.com.bluemoon.delivery.module.card;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.card.Remind;
import cn.com.bluemoon.delivery.app.api.model.card.ResultRemind;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.BasePullToRefreshListViewFragment;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;

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
            Remind remind = (Remind) getItem(position);
            LinearLayout layoutAlarm = getViewById(R.id.layout_alarm);
            TextView txtAlarmTime = getViewById(R.id.txt_alarm_time);
            TextView txtAlamTitle = getViewById(R.id.txt_alarm_title);
            TextView txtAlarmContent = getViewById(R.id.txt_alarm_content);
            cn.com.bluemoon.lib.view.switchbutton.SwitchButton sbOpen = getViewById(R.id.sb_open);

            txtAlarmTime.setText(DateUtil.getTime(remind.getRemindId(),"HH:mm"));
            txtAlamTitle.setText(remind.getRemindTitle());
            txtAlarmContent.setText(remind.getRemindContent());
            sbOpen.setChecked(!remind.isClose());
            layoutAlarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(),AlarmModifyActivity.class);
                    startActivity(intent);
                }
            });


//            txtBoxesCount.setText(
//                    new StrBuilder(String.format(getString(R.string.order_boxes_count),
//                            StringUtil.formatBoxesNum(order.getTotalCase())))
//                            .append(String.format(getString(R.string.order_product_count),
//                                    order.getTotalNum())).toString()
//            );
//            int index = position % 2;
//            if (index == 1) {
//                convertView.setBackgroundResource(R.drawable.list_item_grep_bg);
//            } else {
//                convertView
//                        .setBackgroundResource(R.drawable.list_item_white_bg);
//            }
//
//            setClickEvent(isNew, position, convertView);
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


//        DeliveryApi.getOutOrders(getToken(), startTime / 1000, endTime / 1000, getNewHandler
//                (requestCode,
//                        ResultOrderVo.class));
        DeliveryApi.getRemindList(getToken(),getNewHandler(requestCode,ResultRemind.class));

    }

    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {

    }

    @Override
    public void onItemClick(Object item, View view, int position) {

    }
}
