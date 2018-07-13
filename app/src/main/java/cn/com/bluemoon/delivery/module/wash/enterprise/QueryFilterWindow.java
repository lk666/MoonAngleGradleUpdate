package cn.com.bluemoon.delivery.module.wash.enterprise;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.protocol.HTTP;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.EnterpriseApi;
import cn.com.bluemoon.delivery.app.api.model.wash.enterprise.ResultEnterpriseRecord;
import cn.com.bluemoon.delivery.app.api.model.wash.enterprise.ResultEnterpriseRecord.EnterpriseListBean;
import cn.com.bluemoon.delivery.app.api.model.wash.enterprise.ResultEnterpriseRecord.EnterpriseListBean.BranchListBean;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.ui.selectordialog.SingleOptionSelectDialog;
import cn.com.bluemoon.delivery.ui.selectordialog.TextSingleOptionSelectDialog;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.lib.view.CommonDatePickerDialog;

public class QueryFilterWindow extends PopupWindow {

    private Context mContext;
    private String mYear;
    private String mMonth;
    private String mMon;
    private String mDay;
    private CommonDatePickerDialog startDatePicker;
    private CommonDatePickerDialog endDatePicker;
    EditText startDateChoice;
    EditText endDateChoice;
    Button okBtn;
    Button cancelBtn;
    LinearLayout layoutCompany;
    LinearLayout layoutCompanySub;
    TextView txtCompanySub;
    TextView txtCompany;
    EditText etName;
    private boolean touchStartDate = true;
    private boolean isClickCompany;
    private long startTime;
    private long endTime;
    private long currentTime;
    private boolean noData;

    public QueryFilterWindow(Context context, TimerFilterListener listener, boolean noData) {
        mContext = context;
        this.noData = noData;
        Init(listener);
    }

    private void Init(final TimerFilterListener listener) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_enterprise_history_filter, null);


        LinearLayout ll_popup = (LinearLayout) view
                .findViewById(R.id.layout_history_filter);

        setWidth(LayoutParams.MATCH_PARENT);
        setHeight(LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setOutsideTouchable(true);
        setContentView(view);
        setBackgroundDrawable(mContext.getResources().getDrawable(
                R.drawable.bg_transparent));

        ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
                R.anim.push_top_in));

        startDateChoice = (EditText) view.findViewById(R.id.start_date_choice);
        endDateChoice = (EditText) view.findViewById(R.id.end_date_choice);

        okBtn = (Button) view.findViewById(R.id.btn_confirm);
        cancelBtn = (Button) view.findViewById(R.id.btn_cancel);
        layoutCompany = (LinearLayout) view.findViewById(R.id.layout_company);
        layoutCompanySub = (LinearLayout) view.findViewById(R.id.layout_company_sub);
        etName = (EditText) view.findViewById(R.id.et_name);
        txtCompany = (TextView) view.findViewById(R.id.txt_company);
        txtCompanySub = (TextView) view.findViewById(R.id.txt_company_sub);
        layoutCompany.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                isClickCompany = true;
                showSelectView();
            }
        });
        layoutCompanySub.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                isClickCompany = false;
                showSelectView();
            }
        });

        startDateChoice.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                touchStartDate = true;
                showDatePickerDialog();
                return false;
            }
        });

        endDateChoice.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                touchStartDate = false;
                showDatePickerDialog();
                return false;
            }
        });

        view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        cancelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setDefaultTime();
                txtCompanySub.setText("");
                txtCompany.setText("");
                etName.setText("");
            }
        });

        okBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                long startDate = Long.valueOf(getStartDateTime());
                long endDate = Long.valueOf(getEndDateTime());
                listener.callBack(startDate, endDate, enterpriseCode, branchCode, etName.getText().toString());
                dismiss();

            }
        });

        setDefaultTime();
        EnterpriseApi.getEnterpriseRecordQuery(ClientStateManager.getLoginToken(), handler);
    }

    private void setDefaultTime() {
        //初始化
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
        Calendar c = Calendar.getInstance();
        endDateChoice.setText(sdf.format(c.getTime()));
        endTime = Long.valueOf(sdf2.format(c.getTime()));
        currentTime = Long.valueOf(sdf2.format(c.getTime()));
        c.add(Calendar.DATE, -6);
        startDateChoice.setText(sdf.format(c.getTime()));
        startTime = Long.valueOf(sdf2.format(c.getTime()));
    }

    public void showPopwindow(View popStart) {
        showAsDropDown(popStart);
    }

    public void showDatePickerDialog() {
        setCurDate();
        final int mIYear = Integer.valueOf(mYear);
        final int mIMon = Integer.valueOf(mMon);
        final int mIDay = Integer.valueOf(mDay);
        if (touchStartDate && startDatePicker == null) {
            String[] value = startDateChoice.getText().toString().split("/");
            int year = Integer.valueOf(value[0]);
            int mon = Integer.valueOf(value[1]) - 1;
            int day = Integer.valueOf(value[2]);
            startDatePicker = new CommonDatePickerDialog(mContext, mDateSetListener, year, mon, day);
            startDatePicker.getDatePicker().setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
            //时间范围控制，结束时间之前
            startDatePicker.getDatePicker().init(year, mon, day, new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                    long time = getFormatTime(i, i1, i2);
                    if (time > endTime) {
                        String timeStr = String.valueOf(endTime);
                        int year = Integer.valueOf(timeStr.substring(0, 4));
                        int mon = Integer.valueOf(timeStr.substring(4, 6));
                        int day = Integer.valueOf(timeStr.substring(6, 8));
                        datePicker.init(year, mon - 1, day, this);
                    }
                }
            });
            startDatePicker.show();
        } else if (touchStartDate) {
            if (!startDatePicker.isShowing()) {
                if (!"".equals(startDateChoice.getText().toString())) {
                    String[] value = startDateChoice.getText().toString().split("/");
                    startDatePicker.updateDate(Integer.valueOf(value[0]),
                            Integer.valueOf(value[1]) - 1, Integer.valueOf(value[2]));
                } else {
                    startDatePicker.updateDate(mIYear, mIMon, mIDay);
                }
                startDatePicker.getDatePicker().setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
                startDatePicker.show();
            }
        }

        if (!touchStartDate && endDatePicker == null) {
            endDatePicker = new CommonDatePickerDialog(mContext, mDateSetListener, mIYear, mIMon, mIDay);
            endDatePicker.getDatePicker().setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
            //时间范围控制，开始时间和当前时间之间
            endDatePicker.getDatePicker().init(mIYear, mIMon, mIDay, new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                    long time = getFormatTime(i, i1, i2);
                    String timeStr = null;
                    if (time < startTime) {
                        timeStr = String.valueOf(startTime);
                    } else if (time > currentTime) {
                        timeStr = String.valueOf(currentTime);
                    }
                    if (!StringUtil.isEmpty(timeStr)) {
                        int year = Integer.valueOf(timeStr.substring(0, 4));
                        int mon = Integer.valueOf(timeStr.substring(4, 6));
                        int day = Integer.valueOf(timeStr.substring(6, 8));
                        datePicker.init(year, mon - 1, day, this);
                    }
                }
            });
            endDatePicker.show();
        } else if (!touchStartDate) {
            if (!endDatePicker.isShowing()) {
                if (!"".equals(endDateChoice.getText().toString())) {
                    String[] value = endDateChoice.getText().toString().split("/");
                    int year = Integer.valueOf(value[0]);
                    int mon = Integer.valueOf(value[1]) - 1;
                    int day = Integer.valueOf(value[2]);
                    endDatePicker.updateDate(year, mon, day);
                } else {
                    endDatePicker.updateDate(mIYear, mIMon, mIDay);
                }
                endDatePicker.getDatePicker().setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
                endDatePicker.show();
            }
        }
    }

    /**
     * 格式化获取时间
     *
     * @return
     */
    private long getFormatTime(int i, int i1, int i2) {
        StringBuffer sb = new StringBuffer();
        sb.append(i);
        int mon = i1 + 1;
        if (mon < 10) {
            sb.append(0);
        }
        sb.append(mon);
        if (i2 < 10) {
            sb.append(0);
        }
        sb.append(i2);
        return Long.valueOf(sb.toString());
    }

    public void setCurDate() {
        Calendar dateAndTime = Calendar.getInstance(Locale.CHINA);
        mYear = String.valueOf(dateAndTime.get(Calendar.YEAR));
        mMon = String.valueOf(dateAndTime.get(Calendar.MONTH));
        mMonth = String.valueOf(dateAndTime.get(Calendar.MONTH) + 1);
        mDay = String.valueOf(dateAndTime.get(Calendar.DAY_OF_MONTH));
    }


    public String getStartDateTime() {
        if ("".equals(startDateChoice.getText().toString())) {
            return "0";
        } else {
            return startDateChoice.getText().toString().replaceAll("/", "") + "000000";
        }
    }

    public String getEndDateTime() {
        if ("".equals(endDateChoice.getText().toString())) {
            return "0";
        } else {
            return endDateChoice.getText().toString().replaceAll("/", "") + "235959";
        }
    }

    public String getDate() {
        return mYear + "/" + mMonth + "/" + mDay;
    }

    private CommonDatePickerDialog.OnDateSetListener mDateSetListener = new CommonDatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mYear = String.valueOf(year);
            mMon = String.valueOf(monthOfYear);
            if (monthOfYear <= 8) {
                mMonth = "0" + (monthOfYear + 1);
            } else {
                mMonth = String.valueOf(monthOfYear + 1);
            }
            if (dayOfMonth <= 9) {
                mDay = String.valueOf("0" + dayOfMonth);
            } else {
                mDay = String.valueOf(dayOfMonth);
            }
            if (touchStartDate) {
                startTime = Long.valueOf(mYear + mMonth + mDay);
                startDateChoice.setText(getDate());
            } else {
                endTime = Long.valueOf(mYear + mMonth + mDay);
                endDateChoice.setText(getDate());
            }
        }
    };

    private SingleOptionSelectDialog dialog;

    private void showSelectView() {

        if (!isClickCompany && StringUtil.isEmpty(enterpriseCode)) {
            Toast.makeText(mContext, R.string.input_company, Toast.LENGTH_SHORT).show();
            return;
        } else if (noData) {
            Toast.makeText(mContext, R.string.no_company, Toast.LENGTH_SHORT).show();
            return;
        }
        List<String> itemList = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (EnterpriseListBean bean : list) {
                if (isClickCompany) {
                    itemList.add(bean.enterpriseSName);
                } else {
                    if (enterpriseCode.equals(bean.enterpriseCode)) {
                        for (BranchListBean brancBean : bean.branchList) {
                            itemList.add(brancBean.branchName);
                        }
                        break;
                    }
                }
            }
        }
        if (itemList.isEmpty()) {
            return;
        }
        int index = itemList.size() > 2 ? 1 : 0;
        dialog = new TextSingleOptionSelectDialog(mContext, "",
                itemList, index, new SingleOptionSelectDialog.OnButtonClickListener() {
            @Override
            public void onOKButtonClick(int index, String text) {
                if (isClickCompany) {
                    txtCompany.setText(text);
                    enterpriseCode = list.get(index).enterpriseCode;
                } else {
                    txtCompanySub.setText(text);
                    for (EnterpriseListBean bean : list) {
                        if (enterpriseCode.equals(bean.enterpriseCode)) {
                            branchCode = bean.branchList.get(index).branchCode;
                        }
                    }
                }
            }

            @Override
            public void onCancelButtonClick() {

            }
        });
        dialog.show();

    }

    private List<EnterpriseListBean> list;
    private String enterpriseCode;
    private String branchCode;
    TextHttpResponseHandler handler = new TextHttpResponseHandler(HTTP.UTF_8) {
        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            try {
                LogUtils.d("test", responseString);
                Object resultObj;
                resultObj = JSON.parseObject(responseString, ResultEnterpriseRecord.class);
                if (resultObj instanceof ResultEnterpriseRecord) {
                    ResultEnterpriseRecord resultBase = (ResultEnterpriseRecord) resultObj;
                    if (resultBase.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                        list = resultBase.enterpriseList;
                    } else {
                        //fail
                    }
                }
            } catch (Exception e) {
                //fail
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            //fail
        }
    };

    public interface TimerFilterListener {
        public void callBack(long startTime, long endTime, String enterpriseCode, String branchCode, String queryCode);
    }
}
