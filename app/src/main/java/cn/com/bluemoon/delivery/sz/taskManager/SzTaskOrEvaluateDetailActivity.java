package cn.com.bluemoon.delivery.sz.taskManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.sz.adapter.TaskOrEvaluateDetailAdapter;
import cn.com.bluemoon.delivery.sz.bean.taskManager.AsignJobBean;
import cn.com.bluemoon.delivery.sz.bean.taskManager.DailyPerformanceInfoBean;
import cn.com.bluemoon.delivery.sz.util.DisplayUtil;
import cn.com.bluemoon.delivery.sz.util.LogUtil;
import cn.com.bluemoon.delivery.sz.util.PageJumps;
import cn.com.bluemoon.delivery.sz.view.RoundImageView;
import cn.com.bluemoon.delivery.ui.CommonActionBar;

/**
 * Created by Wan.N
 * Date       2016/9/7
 * Desc       评价详情/任务详情
 */
public class SzTaskOrEvaluateDetailActivity extends BaseActivity {

    private Context context;

    @Bind(R.id.rootview)
    RelativeLayout rootview;

    @Bind(R.id.rl_user_data)
    LinearLayout rl_user_data;//用户资料区域

    // @Bind(R.id.user_avatar_iv)
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

    //    @Bind(R.id.ll_bottom_btn_area)
    LinearLayout ll_bottom_btn_area;//底部按钮区域

    //    @Bind(R.id.btn_bottom)
    TextView btn_bottom;//底部按钮

    public static final String ACTIVITY_EXTAR_DATA = "ACTIVITY_EXTAR_DATA";//外部activity携带数据过来的key

    public static final String ACTIVITY_TYPE = "ACTIVITY_TYPE";
    public static final int ACTIVITY_TYPE_TASK_DETAIL = 0;//任务详情
    public static final int ACTIVITY_TYPE_EVALUATE_DETAIL = 1;//任务评价详情
    private int activityType = -1;//记录需要展示的类型（0;任务详情  1;评价详情）

    public static final String ACTIVITY_BEAN_TAYE = "ACTIVITY_TYPE_BEAN";

    private TaskOrEvaluateDetailAdapter adapter = null;
    private DailyPerformanceInfoBean evaluateInfo;//记录传入的绩效数据

    private List<AsignJobBean> asignJobBeanList = new ArrayList<>();

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
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
        context = SzTaskOrEvaluateDetailActivity.this;
        sz_rewrite_btn_layout = (LinearLayout) inflateView(R.layout.sz_task_or_evaluate_detail_btn_layout);
        ll_bottom_btn_area = (LinearLayout) sz_rewrite_btn_layout.findViewById(R.id.ll_bottom_btn_area);
        btn_bottom = (TextView) sz_rewrite_btn_layout.findViewById(R.id.btn_bottom);
        //
        user_score_tv.setVisibility(View.GONE);
        user_score_icon.setVisibility(View.GONE);


        /**工作任务
         * @author jiangyh*/
        if (activityType == ACTIVITY_TYPE_TASK_DETAIL) {
            btn_bottom.setText(R.string.sz_update_task_labe);
            evaluateInfo = (DailyPerformanceInfoBean)
                    getIntent().getSerializableExtra(ACTIVITY_BEAN_TAYE);

            //TODO 模拟数据
            if (evaluateInfo!=null){
                List<AsignJobBean> asignJobBeanList = evaluateInfo.getAsignJobs();
                user_date_tv.setText(evaluateInfo.getCreatetime());
                user_avaliabel_time_tv.setText(evaluateInfo.getDay_valid_min());

                adapter = new TaskOrEvaluateDetailAdapter(this, activityType, asignJobBeanList);
            }

        } else if (activityType == ACTIVITY_TYPE_EVALUATE_DETAIL) {
            btn_bottom.setText(R.string.sz_update_evaluete_label);
        } else {
            btn_bottom.setText("");
        }

        user_task_lv.setAdapter(adapter);

        initListener();
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
        user_task_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SzTaskOrEvaluateDetailActivity.this.toast("test");


            }
        });
    }


    @Override
    public void initData() {

    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {

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
        layoutBottomBtnArea();
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
}
