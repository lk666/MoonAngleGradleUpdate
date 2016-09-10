package cn.com.bluemoon.delivery.sz.taskManager;

import android.content.Context;
import android.content.DialogInterface;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.sz.adapter.TaskWriteEvaluateApater;
import cn.com.bluemoon.delivery.sz.bean.EventMessageBean;
import cn.com.bluemoon.delivery.sz.util.DisplayUtil;
import cn.com.bluemoon.delivery.sz.util.LogUtil;
import cn.com.bluemoon.delivery.sz.util.PageJumps;
import cn.com.bluemoon.delivery.sz.view.RoundImageView;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.ui.dialog.AngelAlertDialog;

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

    private Context context;
    public static final String ACTIVITY_TYPE = "ACTIVITY_TYPE";
    public static final int ACTIVITY_TYPE_WRTTE_EVALUATE = 0;//给未评价的任务写评价
    public static final int ACTIVITY_TYPE_UPDATE_EVALUATE = 1;//给已评价过的任务修改评价
    private int activityType = -1;//记录需要展示的类型（0;给未评价的任务写评价  1;给已评价过的任务修改评价）

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        context = SzWriteEvaluateActivity.this;
        Intent intent = getIntent();
        if (intent.hasExtra(ACTIVITY_TYPE)) {
            activityType = intent.getIntExtra(ACTIVITY_TYPE, -1);
        }
        LogUtil.i("activityType:" + activityType);
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

    private CommonActionBar titleBar;

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
                //TODO 提交更新后的评价信息
                toast("提交修改后的评价信息");
                finish();
            }
        });
        build.show();

    }

    @Override
    public void initView() {
        //
        EventBus.getDefault().register(this);
        //需要填充的按钮布局
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
        updateView();
    }

    private void updateView() {
        if (activityType == ACTIVITY_TYPE_WRTTE_EVALUATE) {
            layoutBottomBtnArea();
        }
//        if (activityType == ACTIVITY_TYPE_WRTTE_EVALUATE) {
//            titleBar.getTvRightView().setVisibility(View.GONE);
//        } else if (activityType == ACTIVITY_TYPE_UPDATE_EVALUATE) {
//            titleBar.getTvRightView().setText(R.string.sz_task_sure);
//            titleBar.getTvRightView().setVisibility(View.VISIBLE);
//        } else {
//
//        }
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
        if (messageBean != null && String.valueOf(toInputActivityNum).equalsIgnoreCase(messageBean.getEventMsgAction())) {
            LogUtil.i("eventbus返会数据--messageBean" + messageBean.toString());
            //TODO 需要将驳回的建议内容一并提交服务器
        }
    }

    private int toInputActivityNum = 0x10;

    private void initListener() {
        btn_advice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 测试用  需删除
                Bundle bundle = new Bundle();
                bundle.putInt("intentNum", toInputActivityNum);
                bundle.putInt("maxTextLenght", 666);
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
                                //TODO 提交评价信息
                                finish();
                            }
                        }).show();
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
