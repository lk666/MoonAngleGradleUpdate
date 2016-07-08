package cn.com.bluemoon.delivery.jobrecord;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.http.Header;
import org.kymjs.kjframe.KJBitmap;
import org.kymjs.kjframe.bitmap.BitmapCallBack;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.jobrecord.PeopleFlow;
import cn.com.bluemoon.delivery.app.api.model.jobrecord.ResultPromoteInfo;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.utils.LibViewUtil;
import cn.com.bluemoon.lib.view.CommonAlertDialog;
import cn.com.bluemoon.lib.view.CommonDatePickerDialog;
import cn.com.bluemoon.lib.view.CommonProgressDialog;
import cn.com.bluemoon.lib.view.CommonTimePickerDialog;

/**
 * Created by LIANGJIANGLI on 2016/6/28.
 */
public class PeopleFlowActivity extends Activity implements View.OnClickListener{
    private String TAG = "PeopleFlowActivity";
    private boolean showStartTime = false;
    private int type; /**1==add, 2==Eidt, 3==Info**/

    private String mYear;
    private String mMonth;
    private String mMon;
    private String mDay;
    private String mHour;
    private String mMinute;
    private String mHour2;
    private String mMinute2;
    private CommonDatePickerDialog datePicker;
    private CommonTimePickerDialog timePicker;
    private TextView txtEditDate;
    private TextView txtStartTime;
    private TextView txtEndTime;
    private String flowId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_flow);
        type = getIntent().getIntExtra("type", 0);
        initCustomActionBar();
        if (type == 1 || type == 2) {
            Button btnSave = (Button)findViewById(R.id.btn_ok);
            TextView txtStar = (TextView)findViewById(R.id.txt_star);
            TextView txtStar2 = (TextView)findViewById(R.id.txt_star2);
            TextView txtStar3 = (TextView)findViewById(R.id.txt_star3);
            TextView txtStar4 = (TextView)findViewById(R.id.txt_star4);
            final EditText etStatus = (EditText)findViewById(R.id.et_status);
            PublicUtil.setGravity(etStatus);
            txtEditDate = (TextView)findViewById(R.id.txt_edit_date);
            ImageView imgRight = (ImageView)findViewById(R.id.img_date);
            final EditText etPlace = (EditText)findViewById(R.id.et_place);
            final EditText etFlow = (EditText)findViewById(R.id.et_flow);
            LinearLayout layoutTime = (LinearLayout)findViewById(R.id.layout_time);
            txtStartTime = (TextView)findViewById(R.id.txt_start_time);
            txtEndTime = (TextView)findViewById(R.id.txt_end_time);
            LinearLayout layoutDate = (LinearLayout)findViewById(R.id.layout_date);
            layoutDate.setBackgroundResource(R.drawable.btn_white);
            layoutDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDatePickerDialog();
                }
            });
            btnSave.setVisibility(View.VISIBLE);
            txtStar.setVisibility(View.VISIBLE);
            txtStar2.setVisibility(View.VISIBLE);
            txtStar3.setVisibility(View.VISIBLE);
            txtStar4.setVisibility(View.VISIBLE);
            txtEditDate.setVisibility(View.VISIBLE);
            imgRight.setVisibility(View.VISIBLE);
            etPlace.setVisibility(View.VISIBLE);
            etFlow.setVisibility(View.VISIBLE);
            layoutTime.setVisibility(View.VISIBLE);
            txtStartTime.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            txtStartTime.getPaint().setAntiAlias(true);
            txtStartTime.setOnClickListener(this);
            txtEndTime.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            txtEndTime.getPaint().setAntiAlias(true);
            txtEndTime.setOnClickListener(this);
            if (type == 2) {
                PeopleFlow peopleFlow = (PeopleFlow)getIntent().getSerializableExtra("peopleFlow");
                flowId = peopleFlow.getFlowId();
                txtEditDate.setText(DateUtil.getTime(peopleFlow.getCreateDate(), "yyyy-MM-dd  EE"));
                txtStartTime.setText(DateUtil.getTime(peopleFlow.getStartTime(), "HH:mm"));
                txtEndTime.setText(DateUtil.getTime(peopleFlow.getEndTime(), "HH:mm"));
                etPlace.setText(peopleFlow.getAddress());
                etFlow.setText(String.valueOf(peopleFlow.getPeopleFlow()));
                etStatus.setText(peopleFlow.getPeopleStatus());
                ViewTreeObserver vto = etStatus.getViewTreeObserver();
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    private boolean isFirst = true;
                    @Override
                    public void onGlobalLayout() {
                        if (isFirst) {
                            isFirst = false;
                            int lineCount = etStatus.getLineCount();
                            if (lineCount > 1) {
                                etStatus.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
                            } else {
                                etStatus.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
                            }
                        }
                    }
                });

                mYear = DateUtil.getTime(peopleFlow.getCreateDate(), "yyyy");
                mMon = String.valueOf(Integer.valueOf(DateUtil.getTime(peopleFlow.getCreateDate(), "MM"))-1);
                mMonth = DateUtil.getTime(peopleFlow.getCreateDate(), "MM");
                mDay = DateUtil.getTime(peopleFlow.getCreateDate(), "dd");
                mHour = DateUtil.getTime(peopleFlow.getStartTime(), "HH");
                mHour2 = DateUtil.getTime(peopleFlow.getEndTime(), "HH");
                mMinute = DateUtil.getTime(peopleFlow.getStartTime(), "mm");
                mMinute2 = DateUtil.getTime(peopleFlow.getEndTime(), "mm");
            } else {
                txtEditDate.setText(DateUtil.getCurDateAndWeek());
            }

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getString(R.string.people_flow_start_time).equals(txtStartTime.getText().toString())) {
                        PublicUtil.showToast(getString(R.string.people_flow_start_time_select));
                        return;
                    }
                    if (getString(R.string.people_flow_end_time).equals(txtEndTime.getText().toString())) {
                        PublicUtil.showToast(getString(R.string.people_flow_end_time_select));
                        return;
                    }
                    if (Integer.valueOf(mHour) > Integer.valueOf(mHour2)
                            || Integer.valueOf(mHour) == Integer.valueOf(mHour2) && Integer.valueOf(mMinute) > Integer.valueOf(mMinute2)) {
                        PublicUtil.showToast(getString(R.string.people_flow_time_earlier));
                        return;
                    }
                    if (!StringUtils.isNotBlank(etPlace.getText().toString())) {
                        PublicUtil.showToast(getString(R.string.people_flow_place_input));
                        return;
                    }
                    if (!StringUtils.isNotBlank(etFlow.getText().toString())) {
                        PublicUtil.showToast(getString(R.string.people_flow_amount_input));
                        return;
                    }
                    PeopleFlow peopleFlow = new PeopleFlow();
                    peopleFlow.setFlowId(flowId);
                    peopleFlow.setPeopleStatus(etStatus.getText().toString());
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  EE");
                    SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd  EE HH:mm");
                    try {
                        Date date = format.parse(txtEditDate.getText().toString());
                        Date date2 = format2.parse(txtEditDate.getText().toString() + " " +txtStartTime.getText().toString());
                        Date date3 = format2.parse(txtEditDate.getText().toString() + " " +txtEndTime.getText().toString());
                        peopleFlow.setCreateDate(date.getTime());
                        peopleFlow.setStartTime(date2.getTime());
                        peopleFlow.setEndTime(date3.getTime());
                        peopleFlow.setAddress(etPlace.getText().toString());
                        peopleFlow.setPeopleFlow(Integer.valueOf(etFlow.getText().toString()));
                        peopleFlow.setRecordStatus("add");
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("peopleFlow", peopleFlow);
                        intent.putExtras(bundle);
                        if (type == 1) {
                            setResult(1, intent);
                        } else {
                            setResult(2, intent);
                        }
                        finish();
                    } catch (ParseException e) {

                    }

                }
            });
        }
        if (type == 3) {
            PeopleFlow peopleFlow = (PeopleFlow)getIntent().getSerializableExtra("personFlow");
            TextView txtDate = (TextView)findViewById(R.id.txt_date);
            TextView txtTime = (TextView)findViewById(R.id.txt_time);
            TextView txtPlace = (TextView)findViewById(R.id.txt_place);
            TextView txtFlow = (TextView)findViewById(R.id.txt_flow);
            TextView txtStatus = (TextView)findViewById(R.id.txt_status);
            LinearLayout layoutStatus = (LinearLayout)findViewById(R.id.layout_status);
            txtDate.setVisibility(View.VISIBLE);
            txtTime.setVisibility(View.VISIBLE);
            txtPlace.setVisibility(View.VISIBLE);
            txtFlow.setVisibility(View.VISIBLE);
            layoutStatus.setVisibility(View.VISIBLE);
            txtDate.setText(DateUtil.getTime(peopleFlow.getCreateDate(),"yyyy-MM-dd  EE"));
            txtTime.setText(DateUtil.getTime(peopleFlow.getStartTime(), "HH:mm")
                    + "-" + DateUtil.getTime(peopleFlow.getEndTime(), "HH:mm"));
            txtPlace.setText(peopleFlow.getAddress());
            txtFlow.setText(String.valueOf(peopleFlow.getPeopleFlow()));
            txtStatus.setText(String.valueOf(peopleFlow.getPeopleStatus()));
        }
    }

    public void showDatePickerDialog() {
        Calendar dateAndTime = Calendar.getInstance(Locale.CHINA);
        if (mYear == null) {
            mYear = String.valueOf(dateAndTime.get(Calendar.YEAR));
            mMon = String.valueOf(dateAndTime.get(Calendar.MONTH));
            mMonth = String.valueOf(dateAndTime.get(Calendar.MONTH) + 1);
            mDay = String.valueOf(dateAndTime.get(Calendar.DAY_OF_MONTH));
        }
        if (datePicker == null) {
            datePicker = new CommonDatePickerDialog(this, mDateSetListener,
                    Integer.valueOf(mYear), Integer.valueOf(mMon),
                    Integer.valueOf(mDay));
            datePicker.show();
        } else {
            if (!datePicker.isShowing()) {
                datePicker.updateDate(Integer.valueOf(mYear),
                        Integer.valueOf(mMonth) - 1, Integer.valueOf(mDay));
                datePicker.show();
            }
        }
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
            String date = DateUtil.getTime(view.getCalendarView().getDate(), "yyyy-MM-dd  EE");
            txtEditDate.setText(date);
        }
    };

    public void showTimePickerDialog() {
        Calendar dateAndTime = Calendar.getInstance(Locale.CHINA);
        if (mHour == null) {
            mHour = String.valueOf(dateAndTime.get(Calendar.HOUR_OF_DAY));
            mMinute = String.valueOf(dateAndTime.get(Calendar.MINUTE));
        }
        if (mHour2 == null) {
            mHour2 = String.valueOf(dateAndTime.get(Calendar.HOUR_OF_DAY));
            mMinute2 = String.valueOf(dateAndTime.get(Calendar.MINUTE));
        }
        String hour = showStartTime ? mHour : mHour2;
        String minute = showStartTime ? mMinute : mMinute2;

        if (timePicker == null) {
            timePicker = new CommonTimePickerDialog(this, mTimeSetListener,
                    Integer.valueOf(hour), Integer.valueOf(minute), true);
        } else {
            if (!timePicker.isShowing()) {
                timePicker.updateTime(Integer.valueOf(hour),
                            Integer.valueOf(minute));
            }
        }
        timePicker.show();
    }

    private CommonTimePickerDialog.OnTimeSetListener mTimeSetListener = new CommonTimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String hour = String.valueOf(hourOfDay);
            if (hourOfDay <= 9) {
                    hour = "0" + hour;
            }
            if (showStartTime) {
                mHour = hour;
            } else {
                mHour2 = hour;
            }
            String m = String.valueOf(minute);
            if (minute <= 9) {
                m = "0" + minute;
            }
            if (showStartTime) {
                mMinute = m;
            } else {
                mMinute2 = m;
            }
            if (showStartTime) {
                txtStartTime.setText(mHour + ":" + mMinute);
            } else {
                txtEndTime.setText(mHour2 + ":" + mMinute2);
            }

        }
    };


    private void initCustomActionBar() {
        new CommonActionBar(getActionBar(), new IActionBarListener() {

            @Override
            public void setTitle(TextView v) {
                if (type == 1) {
                    v.setText(getResources().getString(R.string.people_flow_add_title));
                } else if (type == 2 || type == 3) {
                    v.setText(getResources().getString(R.string.people_flow_title));
                }
            }

            @Override
            public void onBtnRight(View v) {
                new CommonAlertDialog.Builder(PeopleFlowActivity.this).setMessage(getString(R.string.people_flow_delete)).
                        setNegativeButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setResult(3);
                        finish();
                    }
                }).setPositiveButton(R.string.btn_cancel, null).show();
            }

            @Override
            public void onBtnLeft(View v) {
                finish();
            }
        });

        if (type == 2) {
            ImageView right = (ImageView)this.findViewById(R.id.img_right);
            right.setImageResource(R.mipmap.icon_del);
            right.setVisibility(View.VISIBLE);
        }

    }
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
    }

    public void onPause() {
        super.onPause();

        MobclickAgent.onPageEnd(TAG);
    }

    @Override
    public void onClick(View v) {
        if (v == txtStartTime) {
            showStartTime = true;
            showTimePickerDialog();
        } else if (v == txtEndTime) {
            showStartTime = false;
            showTimePickerDialog();
        }
    }
}
