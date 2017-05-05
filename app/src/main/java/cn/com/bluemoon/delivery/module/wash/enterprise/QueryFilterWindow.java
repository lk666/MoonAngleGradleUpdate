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

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

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
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.PublicUtil;
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

    public QueryFilterWindow(Context context,
                             TimerFilterListener listener) {
        mContext = context;
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
                long currentDate = getCurDate();
                long endDate = Long.valueOf(getEndDateTime());
                if (startDate == 0 && endDate > 0) {
                    PublicUtil.showToast(R.string.alert_message_input_start_time);
                } else if (startDate > currentDate) {
                    PublicUtil.showToast(R.string.alert_message_start_after_current);
                } else if (endDate > 0 && startDate > endDate) {
                    PublicUtil.showToast(R.string.alert_message_start_before_end);
                } else {
                    if (startDate > 0 && endDate == 0) {
                        endDate = currentDate + 235959;
                    }
                    listener.callBack(startDate, endDate, enterpriseCode, branchCode, etName.getText().toString());
                    dismiss();
                }
            }
        });

        setDefaultTime();
        EnterpriseApi.getEnterpriseRecordQuery(ClientStateManager.getLoginToken(), handler);
    }

    private void setDefaultTime() {
        //初始化
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Calendar c = Calendar.getInstance();
        endDateChoice.setText(sdf.format(c.getTime()));
        c.add(Calendar.DATE, -6);
        startDateChoice.setText(sdf.format(c.getTime()));
    }

    public void showPopwindow(View popStart) {
        showAsDropDown(popStart);
    }

    public void showDatePickerDialog() {
        setCurDate();
        if (touchStartDate && startDatePicker == null) {
            String[] value = startDateChoice.getText().toString().split("/");
            startDatePicker = new CommonDatePickerDialog(mContext, mDateSetListener,
                    Integer.valueOf(value[0]), Integer.valueOf(value[1]) - 1,
                    Integer.valueOf(value[2]));
            startDatePicker.getDatePicker().setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
            startDatePicker.show();
        } else if (touchStartDate) {
            if (!startDatePicker.isShowing()) {
                if (!"".equals(startDateChoice.getText().toString())) {
                    String[] value = startDateChoice.getText().toString().split("/");
                    startDatePicker.updateDate(Integer.valueOf(value[0]),
                            Integer.valueOf(value[1]) - 1, Integer.valueOf(value[2]));
                } else {
                    startDatePicker.updateDate(Integer.valueOf(mYear), Integer.valueOf(mMon), Integer.valueOf(mDay));
                }
                startDatePicker.getDatePicker().setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
                startDatePicker.show();
            }
        }

        if (!touchStartDate && endDatePicker == null) {
            endDatePicker = new CommonDatePickerDialog(mContext, mDateSetListener,
                    Integer.valueOf(mYear), Integer.valueOf(mMon),
                    Integer.valueOf(mDay));
            endDatePicker.getDatePicker().setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
            endDatePicker.show();
        } else if (!touchStartDate) {
            if (!endDatePicker.isShowing()) {
                if (!"".equals(endDateChoice.getText().toString())) {
                    String[] value = endDateChoice.getText().toString().split("/");
                    endDatePicker.updateDate(Integer.valueOf(value[0]),
                            Integer.valueOf(value[1]) - 1, Integer.valueOf(value[2]));
                } else {
                    endDatePicker.updateDate(Integer.valueOf(mYear), Integer.valueOf(mMon), Integer.valueOf(mDay));
                }


                endDatePicker.getDatePicker().setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
                endDatePicker.show();
            }
        }
    }

    public long getCurDate() {
        Calendar dateAndTime = Calendar.getInstance(Locale.CHINA);
        String year = String.valueOf(dateAndTime.get(Calendar.YEAR));
        int monthOfYear = dateAndTime.get(Calendar.MONTH);
        String month = "";
        if (monthOfYear <= 8) {
            month = "0" + (monthOfYear + 1);
        } else {
            month = String.valueOf(monthOfYear + 1);
        }
        int dayOfMonth = dateAndTime.get(Calendar.DAY_OF_MONTH);
        String day = "";
        if (dayOfMonth <= 9) {
            day = String.valueOf("0" + dayOfMonth);
        } else {
            day = String.valueOf(dayOfMonth);
        }
        StringBuffer sb = new StringBuffer();
        sb.append(year);
        sb.append(month);
        sb.append(day);
        sb.append("000000");
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
                startDateChoice.setText(getDate());
            } else {
                endDateChoice.setText(getDate());
            }
        }
    };

    private void showSelectView() {
        if (!isClickCompany && !StringUtils.isNoneBlank(enterpriseCode)) {
            Toast.makeText(mContext, R.string.input_company, Toast.LENGTH_SHORT).show();
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
        int index = itemList.size() > 2 ? 1 : 0;
        new SingleOptionSelectDialog(mContext, "",
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
        }).show();

    }
    private List<EnterpriseListBean> list;
    private String enterpriseCode;
    private String branchCode;
    TextHttpResponseHandler handler = new TextHttpResponseHandler(HTTP.UTF_8) {
        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            try {
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
