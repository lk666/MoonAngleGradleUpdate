package cn.com.bluemoon.delivery.module.coupons;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import cz.msebera.android.httpclient.Header;
import org.apache.http.protocol.HTTP;
import org.kymjs.kjframe.utils.StringUtils;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.coupon.MensendLog;
import cn.com.bluemoon.delivery.app.api.model.coupon.ResultMensendLog;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.view.CommonDatePickerDialog;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

public class CouponsRecordFragment extends Fragment {
    private String TAG = "CouponsRecordFragment";
    private CouponsTabActivity mContext;
    private CommonProgressDialog progressDialog;
    private PullToRefreshListView listView;
    private TextView txtTime;
    private TextView txtCount;
    private String mYear;
    private String mMon;
    private String mDay;
    private CommonDatePickerDialog commonDatePickerDialog;

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = (CouponsTabActivity) activity;
    }

    ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initCustomActionBar();
        View v = inflater.inflate(R.layout.fragment_tab_coupons_record,
                container, false);
        progressDialog = new CommonProgressDialog(mContext);
        txtCount = (TextView) v.findViewById(R.id.txt_count);
        txtTime = (TextView) v.findViewById(R.id.txt_time);
        listView = (PullToRefreshListView) v.findViewById(R.id.listView_record);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                DeliveryApi.getMensendCouponLog(ClientStateManager.getLoginToken(mContext),
                        getDayTime(), getMensendCouponLogHandler);
            }
        });
        setCurDate();
        if (progressDialog != null) {
            progressDialog.show();
        }
        DeliveryApi.getMensendCouponLog(ClientStateManager.getLoginToken(mContext), getDayTime(),
                getMensendCouponLogHandler);
        return v;
    }


    private long getDayTime() {
        if (StringUtils.isEmpty(mYear) || StringUtils.isEmpty(mMon) || StringUtils.isEmpty(mDay)) {
            setCurDate();
        }
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.set(Integer.parseInt(mYear),Integer.parseInt(mMon),Integer.parseInt(mDay));
//        LogUtils.d(DateUtil.getTime(calendar.getTimeInMillis(),"yyyy-MM-dd HH:mm:ss"));
        return calendar.getTimeInMillis();
    }

    private void initCustomActionBar() {

        CommonActionBar actionBar = new CommonActionBar(getActivity().getActionBar(),
                new IActionBarListener() {

                    @Override
                    public void onBtnRight(View v) {
                        // TODO Auto-generated method stu
                        showMonthDatePickerDialog();
                    }

                    @Override
                    public void onBtnLeft(View v) {
                        // TODO Auto-generated method stub
                        mContext.finish();
                    }

                    @Override
                    public void setTitle(TextView v) {
                        // TODO Auto-generated method stub
                        v.setText(R.string.coupons_tab_record);
                    }
                });

        actionBar.getTvRightView().setText(R.string.btn_txt_fillter);
        actionBar.getTvRightView().setCompoundDrawablePadding(10);

        Drawable drawableFillter = getResources().getDrawable(R.mipmap.icon_filter);
        drawableFillter.setBounds(0, 0, drawableFillter.getMinimumWidth(), drawableFillter
                .getMinimumHeight());
        actionBar.getTvRightView().setCompoundDrawables(drawableFillter, null, null, null);
        actionBar.getTvRightView().setVisibility(View.VISIBLE);

    }

    public void setCurDate() {
        Calendar dateAndTime = Calendar.getInstance(Locale.CHINA);
        mYear = String.valueOf(dateAndTime.get(Calendar.YEAR));
        mMon = String.valueOf(dateAndTime.get(Calendar.MONTH));
        mDay = String.valueOf(dateAndTime.get(Calendar.DAY_OF_MONTH));
        refreshMonthTxt(dateAndTime.get(Calendar.YEAR), dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH));
    }

    private void refreshMonthTxt(int year, int month, int day) {
        String text = year + "-" + (month + 1) + "-" + day;
        if (month <= 8) {
            text = year + "-0" + (month + 1) + "-" + day;
        }
        txtTime.setText(text);
    }

    private void showMonthDatePickerDialog() {
        if (StringUtils.isEmpty(mYear) || StringUtils.isEmpty(mMon)) {
            setCurDate();
        }
        if (commonDatePickerDialog == null) {
            commonDatePickerDialog = new CommonDatePickerDialog(mContext, onDateSetListener,
                    Integer.valueOf(mYear),
                    Integer.valueOf(mMon), Integer.valueOf(mDay));
            commonDatePickerDialog.show();
        } else {
            if (!commonDatePickerDialog.isShowing()) {
                commonDatePickerDialog.updateDate(Integer.valueOf(mYear), Integer.valueOf(mMon),
                        Integer.valueOf(mDay));
                commonDatePickerDialog.show();
            }
        }
    }

    CommonDatePickerDialog.OnDateSetListener onDateSetListener = new CommonDatePickerDialog
            .OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int day) {

            mYear = String.valueOf(year);
            mMon = String.valueOf(monthOfYear);
            mDay = String.valueOf(day);
            refreshMonthTxt(year, monthOfYear, day);
            if (progressDialog != null) progressDialog.show();
            DeliveryApi.getMensendCouponLog(ClientStateManager.getLoginToken(mContext),
                    getDayTime(), getMensendCouponLogHandler);
        }
    };

    AsyncHttpResponseHandler getMensendCouponLogHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d(TAG, "getMensendCouponLogHandler result = " + responseString);
            if (progressDialog != null)
                progressDialog.dismiss();
            listView.onRefreshComplete();
            try {
                ResultMensendLog resultMemsendLog = JSON.parseObject(responseString,
                        ResultMensendLog.class);
                if (resultMemsendLog.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    txtCount.setText(String.format(getString(R.string.card_record_count),
                            resultMemsendLog.getTotal()));

                    CouponRecordAdapter couponRecordAdapter = new CouponRecordAdapter(mContext);
                    couponRecordAdapter.setList(resultMemsendLog.getMensendLogs());
                    listView.setAdapter(couponRecordAdapter);
                } else {
                    PublicUtil.showErrorMsg(mContext, resultMemsendLog);
                }
            } catch (Exception e) {
                LogUtils.e(TAG, e.getMessage());
                PublicUtil.showToastServerBusy();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString,
                              Throwable throwable) {
            LogUtils.e(TAG, throwable.getMessage());
            if (progressDialog != null)
                progressDialog.dismiss();
            listView.onRefreshComplete();
            PublicUtil.showToastServerOvertime();
        }
    };

    class CouponRecordAdapter extends BaseAdapter {

        private Context context;
        private List<MensendLog> list;

        public CouponRecordAdapter(Context context) {
            this.context = context;
        }

        public void setList(List<MensendLog> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            if (list == null || list.size() == 0) {
                return 1;
            }
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            // TODO Auto-generated method stub
            LayoutInflater inflate = LayoutInflater.from(context);
            if (list == null || list.size() == 0) {
                View viewEmpty = inflate.inflate(R.layout.layout_no_data, null);
                TextView txtContent = (TextView) viewEmpty.findViewById(R.id.txt_content);
                txtContent.setText(context.getString(R.string.coupons_record_empty));
                AbsListView.LayoutParams params = new AbsListView.LayoutParams(
                        AbsListView.LayoutParams.MATCH_PARENT, listView.getHeight());
                viewEmpty.setLayoutParams(params);
                return viewEmpty;
            }
//            if (convertView == null) {
            convertView = inflate.inflate(R.layout.item_coupon_record, null);
//            }

            int index = position % 2;
            if (index == 1) {
                convertView.setBackgroundResource(R.drawable.list_item_grep_bg);
            } else {
                convertView
                        .setBackgroundResource(R.drawable.list_item_white_bg);
            }

            TextView txtName = (TextView) convertView
                    .findViewById(R.id.txt_name);
            TextView txtCount = (TextView) convertView
                    .findViewById(R.id.txt_count);
            TextView txtUser = (TextView) convertView
                    .findViewById(R.id.txt_user);
            TextView txtPhone = (TextView) convertView
                    .findViewById(R.id.txt_phone);
            TextView txtTime = (TextView) convertView
                    .findViewById(R.id.txt_time);
            final MensendLog mensendLog = list.get(position);
            txtName.setText(mensendLog.getActivity().getActivitySName());
            txtCount.setText(String.format(getString(R.string.coupons_record_item_count),
                    mensendLog.getSendNum()));
            txtUser.setText(mensendLog.getUserBase().getNickName());
            txtPhone.setText(mensendLog.getUserBase().getMobile());
            txtTime.setText(DateUtil.getTime(mensendLog.getSendTime(), "yyyy-MM-dd HH:mm"));

            convertView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent(context, CouponsDetailActivity.class);
                    intent.putExtra("item", mensendLog);
                    startActivity(intent);
                }
            });
            return convertView;
        }

    }


    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
    }

}
