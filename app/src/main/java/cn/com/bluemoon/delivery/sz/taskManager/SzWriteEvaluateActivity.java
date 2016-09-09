package cn.com.bluemoon.delivery.sz.taskManager;

import android.content.Intent;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import cn.com.bluemoon.delivery.sz.adapter.TaskWriteEvaluateApater;
import cn.com.bluemoon.delivery.sz.util.DisplayUtil;
import cn.com.bluemoon.delivery.sz.util.LogUtil;
import cn.com.bluemoon.delivery.sz.view.RoundImageView;
import cn.com.bluemoon.delivery.ui.CommonActionBar;

/**
 * Created by Wan.N
 * Date       2016/9/8
 * Desc      写评价页面
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

    private LinearLayout btnAreaLl;

    //    @Bind(R.id.btn_advice)
    LinearLayout btn_advice;//驳回按钮

    //    @Bind(R.id.btn_sure)
    LinearLayout btn_sure;//确认按钮

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.sz_activity_write_evaluate;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.sz_write_task_evaluate_label);
    }

    @Override
    protected void setActionBar(CommonActionBar titleBar) {
        super.setActionBar(titleBar);
        if (titleBar != null) {

        }
    }

    @Override
    public void initView() {
        btnAreaLl = (LinearLayout) inflateView(R.layout.sz_write_evaluate_bottom_btn_layout);
        btn_advice = (LinearLayout) btnAreaLl.findViewById(R.id.btn_advice);
        btn_sure = (LinearLayout) btnAreaLl.findViewById(R.id.btn_sure);
        //TODO 模拟数据
        List<Object> list = new ArrayList<>();
        list.add(new Object());
        list.add(new Object());
        list.add(new Object());
        list.add(new Object());
        list.add(new Object());
        list.add(new Object());
        list.add(new Object());
        TaskWriteEvaluateApater adapter = new TaskWriteEvaluateApater(this, list);
        user_task_lv.setAdapter(adapter);

        initListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        layoutBottomBtnArea();
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

    private void initListener() {
        btn_advice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast("btn_advice");
                //TODO 测试用  需删除
//                Intent intent = new Intent(SzWriteEvaluateActivity.this, NotificationDetailActivity.class);
//                startActivity(intent);

            }
        });
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast("btn_sure");
            }
        });
    }

    @Override
    public void initData() {

    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {

    }
}
