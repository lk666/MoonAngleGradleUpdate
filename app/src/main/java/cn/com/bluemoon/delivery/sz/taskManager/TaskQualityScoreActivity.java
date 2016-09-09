package cn.com.bluemoon.delivery.sz.taskManager;

import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.sz.util.LogUtil;

/**
 * Created by Wan.N
 * Date       2016/9/9
 * Desc      任务质量评分界面
 */
public class TaskQualityScoreActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.rl_task_quality_score_10)
    RelativeLayout score10_Rl;
    @Bind(R.id.cb_task_quality_score_10)
    CheckBox score10_Cb;

    @Bind(R.id.rl_task_quality_score_9)
    RelativeLayout score9_Rl;
    @Bind(R.id.cb_task_quality_score_9)
    CheckBox score9_Cb;

    @Bind(R.id.rl_task_quality_score_8)
    RelativeLayout score8_Rl;
    @Bind(R.id.cb_task_quality_score_8)
    CheckBox score8_Cb;

    @Bind(R.id.rl_task_quality_score_7)
    RelativeLayout score7_Rl;
    @Bind(R.id.cb_task_quality_score_7)
    CheckBox score7_Cb;

    @Bind(R.id.rl_task_quality_score_6)
    RelativeLayout score6_Rl;
    @Bind(R.id.cb_task_quality_score_6)
    CheckBox score6_Cb;

    @Bind(R.id.rl_task_quality_score_5)
    RelativeLayout score5_Rl;
    @Bind(R.id.cb_task_quality_score_5)
    CheckBox score5_Cb;

    @Bind(R.id.rl_task_quality_score_4)
    RelativeLayout score4_Rl;
    @Bind(R.id.cb_task_quality_score_4)
    CheckBox score4_Cb;

    @Bind(R.id.rl_task_quality_score_3)
    RelativeLayout score3_Rl;
    @Bind(R.id.cb_task_quality_score_3)
    CheckBox score3_Cb;

    @Bind(R.id.rl_task_quality_score_2)
    RelativeLayout score2_Rl;
    @Bind(R.id.cb_task_quality_score_2)
    CheckBox score2_Cb;

    @Bind(R.id.rl_task_quality_score_1)
    RelativeLayout score1_Rl;
    @Bind(R.id.cb_task_quality_score_1)
    CheckBox score1_Cb;

    @Bind(R.id.rl_task_quality_score_0)
    RelativeLayout score0_Rl;
    @Bind(R.id.cb_task_quality_score_0)
    CheckBox score0_Cb;

    private List<CheckBox> socreCbs = new ArrayList<>();//存放布局中所有checkbox
    private int score = 10;

    @Override
    protected int getLayoutId() {
        return R.layout.sz_activity_task_quality_score;
    }

    @Override
    public void initView() {
        collectCheckBox();
        initListener();
    }

    private void collectCheckBox() {
        socreCbs.clear();
        socreCbs.add(score10_Cb);
        socreCbs.add(score9_Cb);
        socreCbs.add(score8_Cb);
        socreCbs.add(score7_Cb);
        socreCbs.add(score6_Cb);
        socreCbs.add(score5_Cb);
        socreCbs.add(score4_Cb);
        socreCbs.add(score3_Cb);
        socreCbs.add(score2_Cb);
        socreCbs.add(score1_Cb);
        socreCbs.add(score0_Cb);
    }

    private void initListener() {
        score10_Rl.setOnClickListener(this);
        score9_Rl.setOnClickListener(this);
        score8_Rl.setOnClickListener(this);
        score7_Rl.setOnClickListener(this);
        score6_Rl.setOnClickListener(this);
        score5_Rl.setOnClickListener(this);
        score4_Rl.setOnClickListener(this);
        score3_Rl.setOnClickListener(this);
        score2_Rl.setOnClickListener(this);
        score1_Rl.setOnClickListener(this);
        score0_Rl.setOnClickListener(this);
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.sz_task_quality_score_label);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {

    }

    @Override
    public void onClick(View v) {
        int chooseIndex = -1;
        switch (v.getId()) {
            case R.id.rl_task_quality_score_10:
                chooseIndex = 0;
                break;
            case R.id.rl_task_quality_score_9:
                chooseIndex = 1;
                break;
            case R.id.rl_task_quality_score_8:
                chooseIndex = 2;
                break;
            case R.id.rl_task_quality_score_7:
                chooseIndex = 3;
                break;
            case R.id.rl_task_quality_score_6:
                chooseIndex = 4;
                break;
            case R.id.rl_task_quality_score_5:
                chooseIndex = 5;
                break;
            case R.id.rl_task_quality_score_4:
                chooseIndex = 6;
                break;
            case R.id.rl_task_quality_score_3:
                chooseIndex = 7;
                break;
            case R.id.rl_task_quality_score_2:
                chooseIndex = 8;
                break;
            case R.id.rl_task_quality_score_1:
                chooseIndex = 9;
                break;
            case R.id.rl_task_quality_score_0:
                chooseIndex = 10;
                break;
        }
        updateCheckState(chooseIndex);
    }

    private void updateCheckState(int chooseIndex) {
        if (chooseIndex >= 0 && chooseIndex <= 10) {
            score = 10 - chooseIndex;
            CheckBox cb;
            for (int i = 0; i < socreCbs.size(); i++) {
                cb = socreCbs.get(i);
                if (chooseIndex == i) {
                    cb.setChecked(true);
                } else {
                    cb.setChecked(false);
                }
            }
        }
        LogUtil.i("score:" + score);
    }
}
