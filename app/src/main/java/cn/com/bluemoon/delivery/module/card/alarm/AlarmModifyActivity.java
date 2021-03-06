package cn.com.bluemoon.delivery.module.card.alarm;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.ui.selectordialog.OnSelectChangedListener;
import cn.com.bluemoon.delivery.ui.selectordialog.SimpleWheelView;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.lib.view.CommonAlertDialog;

/**
 * Created by allenli on 2016/9/12.
 */
public class AlarmModifyActivity extends BaseActivity {

    @BindView(R.id.wheel_hour)
    SimpleWheelView wheelHour;

    @BindView(R.id.wheel_minute)
    SimpleWheelView wheelMinute;
    @BindView(R.id.txt_repeat_content)
    TextView txtRepeat;
    @BindView(R.id.layout_repeat)
    RelativeLayout repeatLayout;

    @BindView(R.id.ed_title)
    EditText edTitle;

    @BindView(R.id.ed_content)
    EditText edContent;

    @BindView(R.id.text_error_msg)
    TextView txtError;

    @BindView(R.id.btn_save)
    Button btnSave;


    private Remind remind;

    private int hour;

    private int minute;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_alarm_modify;
    }

    @Override
    protected void setActionBar(CommonActionBar actionBar) {
        super.setActionBar(actionBar);
        if (remind.getRemindId() == -1) {
            return;
        }
        Drawable drawableFillter = getResources().getDrawable(R.mipmap.btn_title_recycle);

        drawableFillter.setBounds(0, 0, drawableFillter.getMinimumWidth(), drawableFillter
                .getMinimumHeight());
        actionBar.getTvRightView().setCompoundDrawables(drawableFillter, null, null, null);
        actionBar.getTvRightView().setVisibility(View.VISIBLE);
    }


    private ArrayList<String> getHourList() {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            if (i < 10) {
                list.add("0" + i);
            } else {
                list.add(String.valueOf(i));
            }
        }
        return list;
    }

    private ArrayList<String> getMinuteList() {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            if (i < 10) {
                list.add("0" + i);
            } else {
                list.add(String.valueOf(i));
            }
        }
        return list;
    }

    @Override
    public void onBeforeSetContentLayout() {
        Intent intent = this.getIntent();
        remind = (Remind) intent.getSerializableExtra("remind");
    }

    @Override
    public void initView() {

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        if (remind.getRemindId() == -1) {
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
        } else {
            hour = remind.getHour();
            minute = remind.getMinute();
        }

        remind.setMinute(minute);
        remind.setHour(hour);
        wheelHour.setOnSelectListener(new OnSelectChangedListener() {
            @Override
            public void onEndSelected(int index, Object text) {
                Log.e("onEndSelected", index + "," + text.toString());
                hour = Integer.parseInt(text.toString());
                remind.setHour(hour);
            }
        });


        wheelMinute.setOnSelectListener(new OnSelectChangedListener() {
            @Override
            public void onEndSelected(int index, Object text) {
                Log.e("onEndSelected", index + "," + text.toString());
                minute = Integer.parseInt(text.toString());
                remind.setMinute(minute);
            }
        });

        wheelHour.initData(getHourList(), hour);
        wheelMinute.initData(getMinuteList(), minute);

        DaysOfWeek days = new DaysOfWeek(remind.getRemindWeek());

        txtRepeat.setText(days.toString(this, true));
        edTitle.setText(remind.getRemindTitle());
        edContent.setText(remind.getRemindContent());
        edTitle.setSelection(edTitle.getText().length());
        edTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                remind.setRemindTitle(edTitle.getText().toString());
                edTitle.setSelection(edTitle.getText().length());
            }
        });

        edContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                remind.setRemindContent(edContent.getText().toString());
                edContent.setSelection(edContent.getText().length());
            }
        });

        repeatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DaysOfWeekActivity.startAction(AlarmModifyActivity.this, remind.getRemindWeek());
            }
        });

    }

    @Override
    public void initData() {

    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {

        if(requestCode==0){
            ResultRemindId resultRemindId = JSON.parseObject(jsonString,ResultRemindId.class);
            remind.setRemindId(resultRemindId.getRemindId());
            Reminds.addAlarm(this,remind);
        }else if(requestCode==1){
            Reminds.setAlarm(this,remind);
        }else if(requestCode==2){
            Reminds.deleteAlarm(this,remind.getRemindId());
        }
        setResult(RESULT_OK);

        this.finish();
    }


    @Override
    public void onErrorResponse(int requestCode, ResultBase result) {

        txtError.setText(result.getResponseMsg());
        showBtnAmin(btnSave);
        showTextAmin(txtError);
    }

    public void showError(String errorMsg){
        txtError.setText(errorMsg);
        showBtnAmin(btnSave);
        showTextAmin(txtError);
    }

    public static void showTextAmin(View view){
        ViewUtil.setViewVisibility(view, View.VISIBLE);
        ObjectAnimator.ofFloat(view, "alpha", 1.0f, 0.0f).setDuration(3000).start();
    }

    public static void showBtnAmin(View view){
        float translationX = view.getResources().getDimensionPixelOffset(R.dimen.translation_x);
        ObjectAnimator.ofFloat(view, "translationX", 0.0f, -translationX, translationX, 0.0f).setDuration(400).start();
    }

    @OnClick(R.id.btn_save)
    void saveRemind(View view) {
        txtError.setText("");

        if(StringUtil.isEmptyString(remind.getRemindTitle())){
            showError(getString(R.string.alarm_topic_error));
            return;
        }else if(StringUtil.isEmptyString(remind.getRemindContent())){
            showError(getString(R.string.alarm_content_error));
            return;
        }

        showWaitDialog();
        if (remind.getRemindId() == -1) {
            remind.setRemindTime(Reminds.calculateAlarm(remind));
            DeliveryApi.addRemind(getToken(), remind, getNewHandler(0, ResultRemindId.class));
        } else {
            DeliveryApi.updateRemind(getToken(), remind, getNewHandler(1, ResultBase.class));
        }
    }

    public static void startAction(Fragment fragment, Remind remind) {
        Intent intent = new Intent(fragment.getActivity(), AlarmModifyActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("remind", remind);
        intent.putExtras(bundle);
        fragment.startActivityForResult(intent, 0);
    }

    public static void startAction(Fragment fragment) {
        Remind remind = new Remind();
        remind.setRemindTitle(fragment.getString(R.string.alarm_topic_default));
        remind.setRemindContent(fragment.getString(R.string.alarm_content_default));
        startAction(fragment, remind);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        int days = data.getIntExtra("days", 0);
        remind.setRemindWeek(days);
        DaysOfWeek daysOfWeek = new DaysOfWeek(remind.getRemindWeek());
        txtRepeat.setText(daysOfWeek.toString(this, true));
    }

    @Override
    protected String getTitleString() {
        if (remind.getRemindId() == -1) {
            return getString(R.string.tab_title_punch_alarm_new);
        }
        return getString(R.string.tab_title_punch_alarm_modify);
    }


    @Override
    protected void onActionBarBtnRightClick() {
        super.onActionBarBtnRightClick();

        CommonAlertDialog.Builder dialog = new CommonAlertDialog.Builder(this);
            dialog.setMessage(getString(R.string.alarm_delete_alert));
        dialog.setPositiveButton(R.string.dialog_cancel, null);
        dialog.setNegativeButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (remind.getRemindId() == -1) {
                    setResult(RESULT_CANCELED);
                    finish();
                } else {
                    hideWaitDialog();
                    DeliveryApi.removeRemind(getToken(), remind.getRemindId(), getNewHandler(2, ResultBase.class));
                }
            }
        });
        dialog.show();



    }
}
