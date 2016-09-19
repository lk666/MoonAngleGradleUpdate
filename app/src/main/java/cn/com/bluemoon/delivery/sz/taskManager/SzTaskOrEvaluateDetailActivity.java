package cn.com.bluemoon.delivery.sz.taskManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.sz.adapter.TaskOrEvaluateDetailAdapter;
import cn.com.bluemoon.delivery.sz.bean.EventMessageBean;
import cn.com.bluemoon.delivery.sz.bean.taskManager.DailyPerformanceInfoBean;
import cn.com.bluemoon.delivery.sz.bean.taskManager.EventDailyPerformanceBean;
import cn.com.bluemoon.delivery.sz.bean.taskManager.UserInfoBean;
import cn.com.bluemoon.delivery.sz.util.DisplayUtil;
import cn.com.bluemoon.delivery.sz.util.LogUtil;
import cn.com.bluemoon.delivery.sz.util.PageJumps;
import cn.com.bluemoon.delivery.sz.util.UIUtil;
import cn.com.bluemoon.delivery.sz.view.RoundImageView;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.ImageLoaderUtil;

/**
 * Created by Wan.N
 * Date       2016/9/7
 * Desc       评价详情/任务详情页面（二者共用）
 */
public class SzTaskOrEvaluateDetailActivity extends BaseActivity {

    private Context context;

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

    LinearLayout sz_rewrite_btn_layout;

    LinearLayout ll_bottom_btn_area;//底部按钮区域

    TextView btn_bottom;//底部按钮

    public static final String ACTIVITY_EXTAR_DATA = "ACTIVITY_EXTAR_DATA";//外部activity携带数据过来的key

    public static final String ACTIVITY_TYPE = "ACTIVITY_TYPE";
    public static final int ACTIVITY_TYPE_TASK_DETAIL = 0;//任务详情
    public static final int ACTIVITY_TYPE_EVALUATE_DETAIL = 1;//任务评价详情
    private int activityType = -1;//记录需要展示的类型（0;任务详情  1;评价详情）

    private boolean isFirstLayoutBtns = true;//是否是第一次摆放按钮布局（避免重复添加布局）

    private TaskOrEvaluateDetailAdapter adapter = null;
    private DailyPerformanceInfoBean evaluateInfo = null;//记录传入的绩效数据


    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        Intent intent = getIntent();
        if (intent.hasExtra(ACTIVITY_TYPE)) {
            activityType = intent.getIntExtra(ACTIVITY_TYPE, -1);
        }
        if (intent.hasExtra(ACTIVITY_EXTAR_DATA)) {
            evaluateInfo = (DailyPerformanceInfoBean) intent.getSerializableExtra(ACTIVITY_EXTAR_DATA);
            LogUtil.i("--evaluateInfo：" + evaluateInfo.toString());
        }
        LogUtil.i("activityType:" + activityType);
    }

    @Override
    protected String getTitleString() {
        if (activityType == ACTIVITY_TYPE_TASK_DETAIL) {
            return getString(R.string.sz_task_detail_label);
        } else if (activityType == ACTIVITY_TYPE_EVALUATE_DETAIL) {
            return getString(R.string.sz_evaluate_detail_label);
        } else {
            return super.getTitleString();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.sz_activity_task_or_evaluate_detail;
    }

    private CommonActionBar titleBar;

    @Override
    public void setActionBar(CommonActionBar titleBar) {
        super.setActionBar(titleBar);
        if (titleBar != null) {
            this.titleBar = titleBar;
        }
    }

    @Override
    public void initView() {
        EventBus.getDefault().register(this);
        context = SzTaskOrEvaluateDetailActivity.this;
        sz_rewrite_btn_layout = (LinearLayout) inflateView(R.layout.sz_task_or_evaluate_detail_btn_layout);
        ll_bottom_btn_area = (LinearLayout) sz_rewrite_btn_layout.findViewById(R.id.ll_bottom_btn_area);
        btn_bottom = (TextView) sz_rewrite_btn_layout.findViewById(R.id.btn_bottom);
        //*/ jiangyh
        if (activityType==ACTIVITY_TYPE_TASK_DETAIL){
            user_score_tv.setVisibility(View.GONE);
            user_score_icon.setVisibility(View.GONE);

        }

        adapter = new TaskOrEvaluateDetailAdapter(this, activityType, evaluateInfo.getAsignJobs());
        user_task_lv.setAdapter(adapter);
        //添加头部，用作分割线
        View header = new View(this);
        header.setBackgroundColor(getResources().getColor(R.color.page_bg_ed));
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UIUtil.dip2px(this, 10));
        header.setLayoutParams(lp);
        user_task_lv.addHeaderView(header);

        initListener();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvaluateStateChanged(EventDailyPerformanceBean bean) {
        LogUtil.i("被修改的评价bean:" + bean.toString());
        if (bean != null && bean.getDailyPerformanceInfoBean() != null) {
            if (bean.getType() == SzTaskEvaluateStatusFragment.ACTIVITY_TYPE_TO_EVALUATE) {
                //如果是未评价的被评价了，则从未评价区移至已评价区
            } else if (bean.getType() == SzTaskEvaluateStatusFragment.ACTIVITY_TYPE_HAVE_EVALUATED) {
                //如果是已评价的被修改了评价，就将此数据在已评价区更新
                finish();
            }
        }
    }

    private void initListener() {
        ll_bottom_btn_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**@author jiangyh 修改工作任务*/
                if (activityType == ACTIVITY_TYPE_TASK_DETAIL) {
                    //
                    if (evaluateInfo != null) {
                        Bundle mBundle = new Bundle();
                        mBundle.putInt(AddTaskActivity.TASKOPERATETYPE,
                                AddTaskActivity.TASKOPERATETYPE_MODIFY);
                        mBundle.putSerializable(AddTaskActivity.DATABEAN, evaluateInfo);
                        PageJumps.PageJumps(context, AddTaskActivity.class, mBundle);

                    }
                } else if (activityType == ACTIVITY_TYPE_EVALUATE_DETAIL) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(SzWriteEvaluateActivity.ACTIVITY_TYPE, SzWriteEvaluateActivity.ACTIVITY_TYPE_UPDATE_EVALUATE);
                    bundle.putSerializable(SzWriteEvaluateActivity.ACTIVITY_EXTAR_DATA, evaluateInfo);
                    PageJumps.PageJumps(SzTaskOrEvaluateDetailActivity.this, SzWriteEvaluateActivity.class, bundle);
                } else {

                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getModifyTaskSuccess(EventMessageBean messageBean){
        if (messageBean.getEventMsgAction().equals("101")){
            PageJumps.finish(context);
        }
    }


    @Override
    public void initData() {
    }


    @Override
    public void onResume() {
        super.onResume();
        updateView();
    }

    private void updateView() {
        if (activityType == ACTIVITY_TYPE_TASK_DETAIL) {
            btn_bottom.setText(R.string.sz_update_task_labe);
        } else if (activityType == ACTIVITY_TYPE_EVALUATE_DETAIL) {
            btn_bottom.setText(R.string.sz_update_evaluete_label);
        } else {
            btn_bottom.setText("");
        }
        if (evaluateInfo != null) {
            UserInfoBean evaluateInfoUser = evaluateInfo.getUser();
            if (evaluateInfo != null) {
                ImageLoaderUtil.displayImage(evaluateInfoUser.getUAvatar(), user_avatar_iv, R.mipmap.sz_default_user_icon,
                        R.mipmap.sz_default_user_icon, R.mipmap.sz_default_user_icon);
                user_name_tv.setText(evaluateInfoUser.getUName());
            }
            //工作日期
            user_date_tv.setText(evaluateInfo.getWork_date());
            //有效工作时间（单位：分钟）
            user_avaliabel_time_tv.setText(evaluateInfo.getDay_valid_min());
        }
        if (isFirstLayoutBtns) {
            layoutBottomBtnArea();
            isFirstLayoutBtns = false;
        }
    }

    private void layoutBottomBtnArea() {
        rootview.removeView(sz_rewrite_btn_layout);
        int totalHeight = getLayoutHeight();
        WindowManager wm = this.getWindowManager();
        int screenwidth = wm.getDefaultDisplay().getWidth();
        int screenheight = wm.getDefaultDisplay().getHeight();
        LogUtil.i("width:" + screenwidth + "--height:" + screenheight + "--totalHeight:" + totalHeight);
        if (totalHeight < screenheight) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            sz_rewrite_btn_layout.setLayoutParams(lp);
            rootview.addView(sz_rewrite_btn_layout);
        } else {
            user_task_lv.addFooterView(sz_rewrite_btn_layout);
        }
    }

    public int getLayoutHeight() {
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        int rl_user_data_height = measureViewHeight(rl_user_data);
        int titleBar_height = measureViewHeight(getActionBar().getCustomView());
        int user_task_lv_height = getListviewTotalHeight(user_task_lv);
        int ll_bottom_btn_area_height = measureViewHeight(ll_bottom_btn_area);
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

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}


