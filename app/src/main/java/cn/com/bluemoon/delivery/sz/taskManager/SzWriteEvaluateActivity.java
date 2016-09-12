package cn.com.bluemoon.delivery.sz.taskManager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.sz.adapter.TaskWriteEvaluateApater;
import cn.com.bluemoon.delivery.sz.api.SzApi;
import cn.com.bluemoon.delivery.sz.bean.EventMessageBean;
import cn.com.bluemoon.delivery.sz.bean.taskManager.AsignJobBean;
import cn.com.bluemoon.delivery.sz.bean.taskManager.DailyPerformanceInfoBean;
import cn.com.bluemoon.delivery.sz.bean.taskManager.RateDataInfoBean;
import cn.com.bluemoon.delivery.sz.bean.taskManager.ResultGetTaskEvaluateList;
import cn.com.bluemoon.delivery.sz.bean.taskManager.UserInfoBean;
import cn.com.bluemoon.delivery.sz.util.DisplayUtil;
import cn.com.bluemoon.delivery.sz.util.LogUtil;
import cn.com.bluemoon.delivery.sz.util.PageJumps;
import cn.com.bluemoon.delivery.sz.view.RoundImageView;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.ui.dialog.AngelAlertDialog;
import cn.com.bluemoon.delivery.utils.ImageLoaderUtil;

/**
 * Created by Wan.N
 * Date       2016/9/8
 * Desc      写评价/修改评价页面
 */
public class SzWriteEvaluateActivity extends BaseActivity {

    @Bind(R.id.rootview)
    RelativeLayout rootview;

    @Bind(R.id.rl_user_data)
    LinearLayout rl_user_data;//用户资料区域

    @Bind(R.id.user_avatar_iv)
    RoundImageView user_avatar_iv;//用户头像

    @Bind(R.id.user_name_tv)
    TextView user_name_tv;//用户名

    @Bind(R.id.user_score_tv)
    TextView user_score_tv;//用户绩效分

    @Bind(R.id.user_score_icon)
    TextView user_score_icon;//用户绩效图标

    @Bind(R.id.user_avaliabel_time_tv)
    TextView user_avaliabel_time_tv;//用户实际工作时间

    @Bind(R.id.user_avaliabel_time_icon)
    TextView user_avaliabel_time_icon;//用户实际工作时间图标

    @Bind(R.id.user_date_tv)
    TextView user_date_tv;//用户任务时间

    @Bind(R.id.user_date_icon)
    TextView user_date_icon;//用户任务时间图标

    @Bind(R.id.user_task_lv)
    ListView user_task_lv;

    /**
     * 用于eventbus数据传送识别的actionname
     * EVENT_ACTION_TYPE_ADVICE  ：驳回原因（建议）
     * EVENT_ACTION_TYPE_EVALUATE_CONTENT  ：评价内容
     * EVENT_ACTION_TYPE_QUALITY_SCORE  ：质量评分
     */
    public static int EVENT_ACTION_TYPE_ADVICE = 0x10;
    public static int EVENT_ACTION_TYPE_EVALUATE_CONTENT = 0x12;
    public static int EVENT_ACTION_TYPE_QUALITY_SCORE = 0x13;

    private LinearLayout btnAreaLl;

    //    @Bind(R.id.btn_advice)
    LinearLayout btn_advice;//驳回按钮

    //    @Bind(R.id.btn_sure)
    LinearLayout btn_sure;//确认按钮

    private Context context;
    public static final String ACTIVITY_EXTAR_DATA = "ACTIVITY_EXTAR_DATA";//外部activity携带数据过来的key
    public static final String ACTIVITY_TYPE = "ACTIVITY_TYPE";
    public static final int ACTIVITY_TYPE_WRTTE_EVALUATE = 0;//给未评价的任务写评价
    public static final int ACTIVITY_TYPE_UPDATE_EVALUATE = 1;//给已评价过的任务修改评价
    private int activityType = -1;//记录需要展示的类型（0;给未评价的任务写评价  1;给已评价过的任务修改评价）
    private boolean isFirstLayoutBtns = true;//是否是第一次摆放按钮布局（避免重复添加布局）

    private DailyPerformanceInfoBean evaluateInfo;//记录传入的绩效数据
    private CommonActionBar titleBar;

    private TaskWriteEvaluateApater evaluateadapter;
    private List<AsignJobBean> asignJobs = new ArrayList<>();

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        context = SzWriteEvaluateActivity.this;
        Intent intent = getIntent();
        if (intent.hasExtra(ACTIVITY_TYPE)) {
            activityType = intent.getIntExtra(ACTIVITY_TYPE, -1);
        }
        if (intent.hasExtra(ACTIVITY_EXTAR_DATA)) {
            evaluateInfo = (DailyPerformanceInfoBean) intent.getSerializableExtra(ACTIVITY_EXTAR_DATA);
        }
        LogUtil.i("activityType:" + activityType + "--evaluateInfo：" + evaluateInfo.toString());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.sz_activity_write_evaluate;
    }

    @Override
    protected String getTitleString() {
        if (activityType == ACTIVITY_TYPE_WRTTE_EVALUATE) {
            return getString(R.string.sz_write_task_evaluate_label);
        } else if (activityType == ACTIVITY_TYPE_UPDATE_EVALUATE) {
            return getString(R.string.sz_update_task_evaluate_label);
        } else {
            return "";
        }
    }

    @Override
    protected void setActionBar(CommonActionBar titleBar) {
        super.setActionBar(titleBar);
        if (titleBar != null) {
            this.titleBar = titleBar;
            if (activityType == ACTIVITY_TYPE_WRTTE_EVALUATE) {
                titleBar.getTvRightView().setVisibility(View.GONE);
            } else if (activityType == ACTIVITY_TYPE_UPDATE_EVALUATE) {
                titleBar.getTvRightView().setText(R.string.sz_task_sure);
                titleBar.getTvRightView().setVisibility(View.VISIBLE);
            } else {

            }
        }
    }

    @Override
    protected void onActionBarBtnRightClick() {
        super.onActionBarBtnRightClick();
        AngelAlertDialog.Builder build = new AngelAlertDialog.Builder(this);
        build.setMessage(R.string.sz_update_evaluate_dialog_title);
        build.setCancelable(true);
        build.setDismissable(true);
        build.setNegativeButton(R.string.dialog_cancel, null);
        build.setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 提交更新后的评价信息
//                toast("提交修改后的评价信息");
                submitEvaluate();
            }
        });
        build.show();

    }

    @Override
    public void initView() {
        EventBus.getDefault().register(this);
        //需要填充的按钮布局
        btnAreaLl = (LinearLayout) inflateView(R.layout.sz_write_evaluate_bottom_btn_layout);
        btn_advice = (LinearLayout) btnAreaLl.findViewById(R.id.btn_advice);
        btn_sure = (LinearLayout) btnAreaLl.findViewById(R.id.btn_sure);

        evaluateadapter = new TaskWriteEvaluateApater(this, asignJobs);
        user_task_lv.setAdapter(evaluateadapter);

        initListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateView();
    }

    private void updateView() {
        if (activityType == ACTIVITY_TYPE_WRTTE_EVALUATE && isFirstLayoutBtns) {
            layoutBottomBtnArea();
            isFirstLayoutBtns = false;
        }
        //
        if (evaluateInfo != null) {
            UserInfoBean evaluateInfoUser = evaluateInfo.getUser();
            if (evaluateInfo != null) {
                ImageLoaderUtil.displayImage(evaluateInfoUser.getUAvatar(), user_avatar_iv, R.mipmap.sz_default_user_icon,
                        R.mipmap.sz_default_user_icon);
                user_name_tv.setText(evaluateInfoUser.getUName());
            }
            asignJobs = evaluateInfo.getAsignJobs();
            if (evaluateadapter == null) {
                evaluateadapter = new TaskWriteEvaluateApater(this, asignJobs);
                user_task_lv.setAdapter(evaluateadapter);
            } else {
                evaluateadapter.updateAdapter(asignJobs);
            }
        }
    }

    private void layoutBottomBtnArea() {
        rootview.removeView(btnAreaLl);
        int totalHeight = getLayoutHeight();
        WindowManager wm = this.getWindowManager();
        int screenwidth = wm.getDefaultDisplay().getWidth();
        int screenheight = wm.getDefaultDisplay().getHeight();
        LogUtil.i("width:" + screenwidth + "--height:" + screenheight + "--totalHeight:" + totalHeight);
        if (totalHeight < screenheight) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            btnAreaLl.setLayoutParams(lp);
            rootview.addView(btnAreaLl);
        } else {
            user_task_lv.addFooterView(btnAreaLl);
        }
    }

    public int getLayoutHeight() {
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        int titleBar_height = measureViewHeight(getActionBar().getCustomView());
        int rl_user_data_height = measureViewHeight(rl_user_data);
        int user_task_lv_height = getListviewTotalHeight(user_task_lv);
        int ll_bottom_btn_area_height = measureViewHeight(btnAreaLl);
        int totalHeight = statusBarHeight + titleBar_height + rl_user_data_height + user_task_lv_height + ll_bottom_btn_area_height * 2 + DisplayUtil.dip2px(this, 10);
        LogUtil.i("statusBarHeight:" + statusBarHeight + "--titleBar_height:" + titleBar_height + "--rl_user_data_height:" + rl_user_data_height + "--user_task_lv_height:" + user_task_lv_height + "--ll_bottom_btn_area_height:" + ll_bottom_btn_area_height);
        return totalHeight;
    }

    private int measureViewHeight(View view) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        return view.getMeasuredHeight();
    }

    public int getListviewTotalHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return 0;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        return totalHeight;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventAdviceMsg(EventMessageBean messageBean) {
        if (messageBean != null) {
            LogUtil.i("eventbus返会数据--messageBean" + messageBean.toString());
            if (String.valueOf(EVENT_ACTION_TYPE_ADVICE).equalsIgnoreCase(messageBean.getEventMsgAction())) {
                asignJobs = evaluateadapter.getDatas();
                //TODO 需要将驳回的建议内容一并提交服务器
                LogUtil.i("提交的数据集合asignJobs：" + asignJobs.toString());

            } else if (String.valueOf(EVENT_ACTION_TYPE_EVALUATE_CONTENT).equalsIgnoreCase(messageBean.getEventMsgAction())) {
                //将填写的评价内容刷新显示到页面
                String position = messageBean.getReMark();//填写评价内容的itemview的position
                String reviewContent = messageBean.getEventMsgContent();//填写的评价内容
                LogUtil.i("填写了" + position + "的评价内容" + "--" + reviewContent);
                asignJobs = evaluateadapter.getDatas();
                asignJobs.get(Integer.valueOf(position)).setReview_cont(reviewContent);
                evaluateadapter.updateAdapter(asignJobs);
            } else if (String.valueOf(EVENT_ACTION_TYPE_QUALITY_SCORE).equalsIgnoreCase(messageBean.getEventMsgAction())) {
                // 将评分结果刷新显示到页面
                String score = messageBean.getEventMsgContent();
                String position = messageBean.getReMark();//评分的itemview的position
                LogUtil.i("评分结果：position:" + position + "--score:" + score);
                asignJobs = evaluateadapter.getDatas();
                asignJobs.get(Integer.valueOf(position)).setScore(score);
                evaluateadapter.updateAdapter(asignJobs);
            } else {

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initListener() {
        btn_advice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //填写建议信息
                Bundle bundle = new Bundle();
                bundle.putInt("intentNum", EVENT_ACTION_TYPE_ADVICE);
                bundle.putInt("maxTextLenght", 500);
                PageJumps.PageJumps(context, InputToolsActivity.class, bundle);
            }
        });
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AngelAlertDialog.Builder(SzWriteEvaluateActivity.this).
                        setMessage(R.string.sz_update_evaluate_dialog_title).
                        setCancelable(true).
                        setDismissable(true).
                        setNegativeButton(R.string.dialog_cancel, null).
                        setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 提交评价信息
                                submitEvaluate();
                            }
                        }).show();
            }
        });
    }

    private void submitEvaluate() {
        List<AsignJobBean> asignJobBeanList = evaluateadapter.getDatas();
        List<RateDataInfoBean> rateDataInfoBeanList = new ArrayList<>();
        RateDataInfoBean rateDataInfoBean;
        for (AsignJobBean asignJobBean : asignJobBeanList) {
            rateDataInfoBean = new RateDataInfoBean();
            rateDataInfoBean.setReview_cont(asignJobBean.getReview_cont());
            rateDataInfoBean.setValid_min(asignJobBean.getValid_min());
            rateDataInfoBean.setIs_valid(asignJobBean.getIs_valid());
            rateDataInfoBean.setState(asignJobBean.getState());
            rateDataInfoBean.setQuality_score(asignJobBean.getQuality_score());
            rateDataInfoBean.setUsage_time(asignJobBean.getUsage_time());
            rateDataInfoBean.setWork_task_id(asignJobBean.getWork_task_id());
            rateDataInfoBeanList.add(rateDataInfoBean);
        }

        SzApi.submitDayJobsRating(rateDataInfoBeanList, evaluateInfo.getWork_day_id(), getNewHandler(0, ResultBase.class));
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                v.clearFocus();
//                v.setFocusable(false);
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public void initData() {

    }

    private static final int SUBMIT_EVALUATE_SUCCESS = 0x01;
    private static final int SUBMIT_EVALUATE_FAIL = 0x02;
    private static final int SUBMIT_EVALUATE_ERROR = 0x03;

    private Handler mHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUBMIT_EVALUATE_SUCCESS:
                    LogUtil.i("");
                    finish();
                    break;
                case SUBMIT_EVALUATE_FAIL:
                    LogUtil.i("");
                    break;
                case SUBMIT_EVALUATE_ERROR:
                    LogUtil.i("");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        LogUtil.i("onSuccessResponse--jsonString--" + jsonString);
        if (result != null && result.isSuccess && result.getResponseCode() == 100) {
            mHandle.sendEmptyMessage(SUBMIT_EVALUATE_SUCCESS);
        } else {
            mHandle.sendEmptyMessage(SUBMIT_EVALUATE_FAIL);
        }
    }


    @Override
    public void onErrorResponse(int requestCode, ResultBase result) {
        super.onErrorResponse(requestCode, result);
        LogUtil.i("onErrorResponse--result--" + result.getResponseMsg());
        mHandle.sendEmptyMessage(SUBMIT_EVALUATE_ERROR);
    }

    @Override
    public void onFailureResponse(int requestCode, Throwable t) {
        super.onFailureResponse(requestCode, t);
        LogUtil.i("onFailureResponse--result--" + t.getMessage());
        mHandle.sendEmptyMessage(SUBMIT_EVALUATE_FAIL);
    }


}
