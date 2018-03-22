package cn.com.bluemoon.delivery.module.card;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.kymjs.kjframe.utils.StringUtils;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cn.com.bluemoon.delivery.BuildConfig;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.card.PunchCard;
import cn.com.bluemoon.delivery.app.api.model.card.PunchCardType;
import cn.com.bluemoon.delivery.app.api.model.card.ResultPunchCardList;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.BasePullToRefreshListViewFragment;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.view.MonthDatePickerDialog;

/**
 * 打卡记录
 */
public class RecordCardFragment extends BasePullToRefreshListViewFragment {

    private TextView txtTime;
    private TextView txtCount;
    private String mYear;
    private String mMon;
    private MonthDatePickerDialog monthDatePickerDialog;

    @Override
    protected int getHeadLayoutId() {
        return R.layout.head_tab_card_record;
    }

    @Override
    protected BaseListAdapter getNewAdapter() {
        return new CardRecordAdapter(getActivity(), this);
    }

    @Override
    protected void initHeadViewEvent(View headView) {
        super.initHeadViewEvent(headView);
        txtTime = (TextView) headView.findViewById(R.id.txt_time);
        txtCount = (TextView) headView.findViewById(R.id.txt_count);
        setEmptyViewMsg(getString(R.string.empty_hint3,getTitleString()));
    }

    @Override
    protected List getGetMoreList(ResultBase result) {
        return null;
    }

    @Override
    protected List getGetDataList(ResultBase result) {
        ResultPunchCardList punchCardListResult = (ResultPunchCardList)result;
        setTxtCount(punchCardListResult.getTotalCount());
        return punchCardListResult.getPunchCardList();
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.tab_bottom_punch_record_text);
    }

    @Override
    public void initData() {
        setHeadViewVisibility(View.VISIBLE);
        setCurDate();
        showWaitDialog();
        getData();
    }

    @Override
    protected PullToRefreshBase.Mode getMode() {
        return PullToRefreshBase.Mode.PULL_FROM_START;
    }

    @Override
    protected void invokeGetDataDeliveryApi(int requestCode) {
        DeliveryApi.getPunchCardList(getToken(), getMonth(), getNewHandler(requestCode, ResultPunchCardList.class));
    }

    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {

    }

    private void setTxtCount(int count){
        txtCount.setText(getString(R.string.card_record_count, "" + count));
    }

    private long getMonth() {
        if (TextUtils.isEmpty(mYear) || TextUtils.isEmpty(mMon)) {
            setCurDate();
        }
        if (Integer.valueOf(mMon) <= 8) {
            return Long.valueOf(mYear + "0" + (Integer.valueOf(mMon) + 1));
        } else {
            return Long.valueOf(mYear + (Integer.valueOf(mMon) + 1));
        }
    }

    @Override
    protected void showEmptyView() {
        super.showEmptyView();
        setHeadViewVisibility(View.VISIBLE);
        setTxtCount(0);
    }

    @Override
    protected void showNetErrorView() {
        super.showNetErrorView();
        setHeadViewVisibility(View.GONE);
        setTxtCount(0);
    }

    @Override
    protected void setActionBar(CommonActionBar titleBar) {
        super.setActionBar(titleBar);
        titleBar.getTvRightView().setText(R.string.btn_txt_fillter);
        titleBar.getTvRightView().setCompoundDrawablePadding(10);
        Drawable drawableFillter = getResources().getDrawable(R.mipmap.icon_filter);
        drawableFillter.setBounds(0, 0, drawableFillter.getMinimumWidth(), drawableFillter.getMinimumHeight());
        titleBar.getTvRightView().setCompoundDrawables(drawableFillter, null, null, null);
        titleBar.getTvRightView().setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActionBarBtnRightClick() {
        super.onActionBarBtnRightClick();
        showMonthDatePickerDialog();
    }

    public void setCurDate() {
        Calendar dateAndTime = Calendar.getInstance(Locale.CHINA);
        mYear = String.valueOf(dateAndTime.get(Calendar.YEAR));
        mMon = String.valueOf(dateAndTime.get(Calendar.MONTH));
        refreshMonthTxt(dateAndTime.get(Calendar.YEAR), dateAndTime.get(Calendar.MONTH));
    }

    private void refreshMonthTxt(int year, int month) {
        String text = year + "-" + (month + 1);
        if (month <= 8) {
            text = mYear + "-0" + (month + 1);
        }
        txtTime.setText(text);
    }

    private void showMonthDatePickerDialog() {
        if (StringUtils.isEmpty(mYear) || StringUtils.isEmpty(mMon)) {
            setCurDate();
        }
        if (monthDatePickerDialog == null) {
            monthDatePickerDialog = new MonthDatePickerDialog(getActivity(), onDateSetListener, Integer.valueOf(mYear), Integer.valueOf(mMon), 1);
            monthDatePickerDialog.show();
        } else {
            if (!monthDatePickerDialog.isShowing()) {
                monthDatePickerDialog.updateDate(Integer.valueOf(mYear), Integer.valueOf(mMon), 1);
                monthDatePickerDialog.show();
            }
        }
    }

    MonthDatePickerDialog.OnDateSetListener onDateSetListener = new MonthDatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int day) {
            mYear = String.valueOf(year);
            mMon = String.valueOf(monthOfYear);
            refreshMonthTxt(year, monthOfYear);
            getData();
        }
    };

    @Override
    public void onItemClick(Object item, View view, int position) {

    }

    class CardRecordAdapter extends BaseListAdapter<PunchCard> {

        public CardRecordAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_card_record;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            final PunchCard punchCard = list.get(position);
            TextView txtTimeStart = getViewById(R.id.txt_time_start);
            TextView txtTimeEnd = getViewById(R.id.txt_time_end);
            LinearLayout layoutCharge = getViewById(R.id.layout_charge);
            LinearLayout layoutAddress = getViewById(R.id.layout_address);
            TextView txtCharge = getViewById(R.id.txt_charge);
            TextView txtCardAddress = getViewById(R.id.txt_card_address);
            TextView txtCardRelease = getViewById(R.id.txt_card_release);
            ImageView imgRight = getViewById(R.id.img_right);
            txtTimeStart.setText(String.format(getString(R.string.card_record_start_time),
                    DateUtil.getTime(punchCard.getPunchInTime(), "yyyy-MM-dd HH:mm")));
            txtTimeEnd.setText(String.format(getString(R.string.card_record_end_time),
                    DateUtil.getTime(punchCard.getPunchOutTime(), "yyyy-MM-dd HH:mm")));

            if (PunchCardType.rest.toString().equals(punchCard.getPunchCardType())) {
                ViewUtil.setViewVisibility(layoutCharge, View.GONE);
                ViewUtil.setViewVisibility(layoutAddress, View.GONE);
                ViewUtil.setViewVisibility(imgRight, View.GONE);
                ViewUtil.setViewVisibility(txtCardRelease, View.VISIBLE);
                convertView.setOnClickListener(null);
            } else {
                ViewUtil.setViewVisibility(layoutCharge, View.VISIBLE);
                ViewUtil.setViewVisibility(layoutAddress, View.VISIBLE);
                ViewUtil.setViewVisibility(imgRight, View.VISIBLE);
                ViewUtil.setViewVisibility(txtCardRelease, View.GONE);
                txtCharge.setText(CardUtils.getChargeNoPhone(punchCard));
                txtCardAddress.setText(CardUtils.getAddress(punchCard));
                convertView.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        //跳转网页
                        String url = String.format(BuildConfig.H5_DOMAIN, "angel/#/punchDetails?token="
                                + getToken() + "&punchCardId=" + punchCard.getPunchCardId());
                        PublicUtil.openWebView(context, url, "");
                    }
                });
            }

        }

    }


}
