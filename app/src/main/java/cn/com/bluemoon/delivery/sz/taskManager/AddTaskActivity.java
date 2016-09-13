package cn.com.bluemoon.delivery.sz.taskManager;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.kymjs.kjframe.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.ResultToken;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.sz.bean.EventMessageBean;
import cn.com.bluemoon.delivery.sz.bean.ReviewerBean;
import cn.com.bluemoon.delivery.sz.bean.taskManager.AsignJobBean;
import cn.com.bluemoon.delivery.sz.bean.taskManager.DailyPerformanceInfoBean;
import cn.com.bluemoon.delivery.sz.util.LogUtil;
import cn.com.bluemoon.delivery.sz.util.PageJumps;
import cn.com.bluemoon.delivery.sz.view.TaskTextView;
import cn.com.bluemoon.delivery.sz.view.datepicker.adapter.NumericWheelAdapter;
import cn.com.bluemoon.delivery.sz.view.datepicker.widget.WheelView;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.utils.LibViewUtil;

/**添加任务页面*/
public class AddTaskActivity extends BaseActivity{

    @Bind(R.id.tv_dete) TextView tv_dete;
    @Bind(R.id.ttv_appraiser) TaskTextView ttv_appraiser;
    @Bind(R.id.ttv_totalTime) TaskTextView ttv_totalTime;
    @Bind(R.id.ll_task_item_conent) LinearLayout ll_task_item_conent;
    @Bind(R.id.tv_addTask) TextView tv_addTask;
    @Bind(R.id.scrollviwe_task)
    ScrollView scrollviwe_task;


    public static final String TASKOPERATETYPE="TASKOPERATETYPE";
    public static final int TASKOPERATETYPE_ADD=0;
    public static final int TASKOPERATETYPE_MODIFY=1;
    private   int taskOperateType=1;

    public static final String DATABEAN="DATABEAN";
    private DailyPerformanceInfoBean dailyPerformanceInfoBean=null;

    public static final String CURRENTDATA="CURRENTDATA";
    private String currentDate="";

    private WheelView hour;
    private WheelView mins;

    private Context context=null;
    private final int itemSize=10;
    private int itemTag=0;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_sz_add_task;
    }
    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        context=AddTaskActivity.this;
        currentDate=getIntent().getStringExtra("currentDate");
        taskOperateType=getIntent().getIntExtra("TASKOPERATETYPE",0);


    }
    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
//        ClientStateManager.setLoginToken(((ResultToken) result).getToken());
//        ClientStateManager.setUserName(getUserName());
//        MobclickAgent.onProfileSignIn(getUserName());
//        toMainActivity();
    }

    @Override
    public void initView() {
        EventBus.getDefault().register(this);
        initCustomActionBar();

        if (taskOperateType==TASKOPERATETYPE_ADD){//默认最少添加一项
            tv_dete.setText(currentDate);
            ll_task_item_conent.addView(initTaskItemView(itemTag),itemTag);
            itemTag++;

        }else if(taskOperateType==TASKOPERATETYPE_MODIFY) {//修改
            try {
                dailyPerformanceInfoBean=
                        (DailyPerformanceInfoBean) getIntent().getSerializableExtra(DATABEAN);
                currentDate=dailyPerformanceInfoBean.getCreatetime();
                tv_dete.setText(currentDate);

                ReviewerBean reviewerBean=dailyPerformanceInfoBean.getReviewer();
                ttv_appraiser.setText_right(reviewerBean.getuName());
                ttv_totalTime.setText_right(dailyPerformanceInfoBean.getDay_valid_min());

                List<AsignJobBean>  asignJobBeanList =dailyPerformanceInfoBean.getAsignJobs();
                initSetViewContentList(asignJobBeanList);
            }catch (Exception e){
                e.printStackTrace();
            }


        }

        ttv_appraiser.setOnRightTextOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle mBundle=new Bundle();
                mBundle.putString(AppraiseChooserActivity.APPRAISE_NAME,
                        ttv_appraiser.getTv_rightContent().getText().toString());
                mBundle.putInt(AppraiseChooserActivity.APPRAISE_NAME_ACTION,
                        AppraiseChooserActivity.APPRAISE_NAME_ACTION_CONTENT);
                PageJumps.PageJumps(context,AppraiseChooserActivity.class,mBundle);
            }
        });

    }

    /**遍历所有的item 设置Item 的数据内容*/
    private void initSetViewContentList(List<AsignJobBean> asignJobBeanList) {
        for (AsignJobBean asignJobBean: asignJobBeanList) {
            ll_task_item_conent.addView(initTaskItemView(itemTag),itemTag);
            initViewForBean(asignJobBean,itemTag);
            itemTag++;
        }
    }

    /**根据实体 直充内容*/
    private void initViewForBean(AsignJobBean asignJobBean,int itemTag){
        View view =ll_task_item_conent.getChildAt(itemTag);
        TaskViewHolder taskViewHolder=new TaskViewHolder(view);
        taskViewHolder.ttv_taskName.setText_right(asignJobBean.getTask_cont());
        taskViewHolder.ttv_workOutput.setText_right(asignJobBean.getProduce_cont());
        taskViewHolder.tv_dateStart.setText(asignJobBean.getCreatetime());
        taskViewHolder.tv_dateEnd.setText(asignJobBean.getEnd_time());
        String taskStatus=asignJobBean.getState();
        switch (taskStatus){
            case "0":
                taskViewHolder.rg_status.check(taskViewHolder.rb_finish.getId());
                break;
            case "1":
                taskViewHolder.rg_status.check(taskViewHolder.rb_working.getId());
                break;
            case "2":
                taskViewHolder.rg_status.check(taskViewHolder.rb_pause.getId());
                break;
            default:
                break;
        }


    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getInputEventMessagBean(EventMessageBean messageBean){
//        任务项相应的Item的角标
        int tag=Integer.parseInt(messageBean.getEventMsgAction());
        if (tag==AppraiseChooserActivity.APPRAISE_NAME_ACTION_CONTENT){
//            为评价人的回传
            ttv_appraiser.setText_right(messageBean.getEventMsgContent());

        }else{
            inputContentItem(messageBean,tag);
        }
        LogUtil.i("传递过来的值："+messageBean.toString());

    }

    /**弹出输入页面返回的新内容*/
    public void inputContentItem(EventMessageBean messageBean,int itemTag){
        View view =ll_task_item_conent.getChildAt(itemTag);
        TaskViewHolder taskViewHolder=new TaskViewHolder(view);
        if (messageBean.getReMark().equals("ttv_taskName")){//对应的控件名称
            taskViewHolder.ttv_taskName.setText_right(messageBean.getEventMsgContent());
        }else if(messageBean.getReMark().equals("ttv_workOutput")){
            taskViewHolder.ttv_workOutput.setText_right(messageBean.getEventMsgContent());
        }

    }

    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    break;
                default:
                    break;
            }
        }
    };


    private void initCustomActionBar() {
        CommonActionBar titleBar = new CommonActionBar(
                getActionBar(), new IActionBarListener() {
            @Override
            public void onBtnRight(View v) {
                getAllTastContent();
            }
            @Override
            public void onBtnLeft(View v) {
                finish();
            }
            @Override
            public void setTitle(TextView v) {
                v.setText(R.string.sz_task_add_task);
            }

        });
        TextView tv_right=titleBar.getTvRightView();
        tv_right.setVisibility(View.VISIBLE);
        tv_right.setText("确定");


    }

    @Override
    public void initData() {

    }


    private void login(String name,String psw){
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(psw)) {
            LibViewUtil.toast(AppContext.getInstance(),
                    AppContext.getInstance().getString(R.string.register_not_empty));
            return;
        }
        showWaitDialog();
        DeliveryApi.ssoLogin(name, psw, getNewHandler(0, ResultToken.class));
    }


    @OnClick({R.id.tv_addTask, R.id.tv_dete})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_addTask:
                ll_task_item_conent.addView(initTaskItemView(itemTag),itemTag);
                itemTag++;
                setItemName();
                if (itemTag==itemSize) {
                    tv_addTask.setVisibility(View.GONE);
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollviwe_task.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
                break;
            case R.id.tv_dete:


                break;
        }
    }

    public View initTaskItemView(final int Tag){
        final View view= LayoutInflater.from(context).inflate(R.layout.activity_sz_add_item,null);
        final TaskViewHolder taskViewHolder=new TaskViewHolder(view);
        taskViewHolder.ttv_taskName.setOnRightTextOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle mbBundle=new Bundle();
//                PublicUtil.showToast("ttv_taskName");

                mbBundle.putInt("intentNum",Tag);
                mbBundle.putInt("maxTextLenght",50);
                mbBundle.putString("viewName","ttv_taskName");
                mbBundle.putString("inputContent",
                        taskViewHolder.ttv_taskName
                                .getTv_rightContent().getText().toString());

                PageJumps.PageJumps(context,InputToolsActivity.class,mbBundle);
            }
        });
        taskViewHolder.ttv_workOutput.setOnRightTextOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle mbBundle=new Bundle();
//                PublicUtil.showToast("ttv_taskName");

                mbBundle.putInt("intentNum",Tag);
                mbBundle.putInt("maxTextLenght",200);
                mbBundle.putString("viewName","ttv_workOutput");
                mbBundle.putString("inputContent",
                        taskViewHolder.ttv_workOutput
                                .getTv_rightContent().getText().toString());

                PageJumps.PageJumps(context,InputToolsActivity.class,mbBundle);
            }
        });

        taskViewHolder.tv_dateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time=taskViewHolder.tv_dateStart.getText().toString();
                String[] times=time.split(":");
                int currHour=Integer.parseInt(times[0]);
                int currMin=Integer.parseInt(times[1]);

                showTimeDialog(taskViewHolder.tv_dateStart,currHour,currMin,"开始时间");
            }
        });
        taskViewHolder.tv_dateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time=taskViewHolder.tv_dateEnd.getText().toString();
                String[] times=time.split(":");
                int currHour=Integer.parseInt(times[0]);
                int currMin=Integer.parseInt(times[1]);

                showTimeDialog(taskViewHolder.tv_dateEnd,currHour,currMin,"结束时间");
            }
        });

        taskViewHolder.iv_taskDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemTag>1){
                    showDeleteAnimator(view);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                             //                添加动画
                            ll_task_item_conent.removeView(view);
                            itemTag--;
                            setItemName();
                                if (itemTag<10) {
                                    tv_addTask.setVisibility(View.VISIBLE);
                                }
                        }
                    },400);

                }else{
                    PublicUtil.showToast("至少有一项任务！");

                }
            }
        });

        return view;
    }


    /**删除动效*/
    public void showDeleteAnimator(View view){
        //                添加动画
        ObjectAnimator animScaleX = ObjectAnimator//
                .ofFloat(view, "scaleX", 1.0F,  0.0F);//
        ObjectAnimator animScaleY = ObjectAnimator//
                .ofFloat(view, "scaleY", 1.0F,  0.0F);//
        ObjectAnimator animAlpha = ObjectAnimator//
                .ofFloat(view, "alpha", 0.6F,  0.0F);//
        AnimatorSet set=new AnimatorSet();
        set.setDuration(500);
        set.playTogether(animScaleX,animScaleY,animAlpha);
        set.start();
    }


    /**
     * 初始化时
     */
    private void initHour() {
        NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(this,0, 23, "%02d");
        numericWheelAdapter.setLabel(" 时");
        //		numericWheelAdapter.setTextSize(15);  设置字体大小
        hour.setViewAdapter(numericWheelAdapter);
        hour.setCyclic(true);
    }

    /**
     * 初始化分
     */
    private void initMins() {
        NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(this,0, 59, "%02d");
        numericWheelAdapter.setLabel(" 分");
//		numericWheelAdapter.setTextSize(15);  设置字体大小
        mins.setViewAdapter(numericWheelAdapter);
        mins.setCyclic(true);
    }

    /**
     * 显示时间
     */
    private void showTimeDialog(final TextView textView,int currHour,int currMin,String timeTilte){
        final AlertDialog dialog = new AlertDialog.Builder(AddTaskActivity.this)
                .create();
        dialog.show();
        Window window = dialog.getWindow();
        // 设置布局
        window.setContentView(R.layout.activity_task_time_datapick);
        // 设置宽高
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        // 设置弹出的动画效果
        window.setWindowAnimations(R.style.AnimBottom);

        hour = (WheelView) window.findViewById(R.id.wv_date_time_hour);
        initHour();
        mins = (WheelView) window.findViewById(R.id.wv_date_time_min);
        initMins();
        // 设置当前时间

        LogUtil.i("当前时间："+currHour+":"+currMin);
        hour.setCurrentItem(currHour);
        mins.setCurrentItem(currMin);
        hour.setVisibleItems(7);
        mins.setVisibleItems(7);

        // 设置监听
        Button btn_commit = (Button) window.findViewById(R.id.btn_commit);
        Button btn_cancel = (Button) window.findViewById(R.id.btn_cancel);
        TextView tv_timeTilte = (TextView) window.findViewById(R.id.tv_timeTilte);
        tv_timeTilte.setText(timeTilte);
        btn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                int finalHour=hour.getCurrentItem();
                int finalMin=mins.getCurrentItem();
                String finalHours=""+finalHour;
                String finalMins=""+finalMin;
                if (finalHour<10){
                    finalHours="0"+finalHours;
                }
                if (finalMin<10){
                    finalMins="0"+finalMins;
                }

                LogUtil.i("选取后的时间："+finalHours+":"+finalMins);
                textView.setText(finalHours+":"+finalMins);
                dialog.cancel();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.cancel();
            }
        });
        LinearLayout cancelLayout = (LinearLayout) window.findViewById(R.id.view_none);
        cancelLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                dialog.cancel();
                return false;
            }
        });
    }


    /**设置item 名称*/
    public void setItemName(){
        for (int i = 0; i <= itemTag; i++) {
            View view =ll_task_item_conent.getChildAt(i);
            if (view!=null) {
                TaskViewHolder taskViewHolder=new TaskViewHolder(view);
                int itemCount=(i+1);
                taskViewHolder.ttv_taskName.setText_left("任  务"+itemCount);
            }
        }
    }

    /**遍历所有的item 得到所有的数据*/
    public List<AsignJobBean> getAllTastContent(){
        List<AsignJobBean> asignJobBeanList=new ArrayList<AsignJobBean>();
        for (int i = 0; i < itemTag; i++) {
            AsignJobBean asignJobBean=new AsignJobBean();
            View view =ll_task_item_conent.getChildAt(i);
            if (view!=null) {
                TaskViewHolder taskViewHolder=new TaskViewHolder(view);
                asignJobBean.setTask_cont(
                        taskViewHolder.ttv_taskName.getTv_rightContent().getText().toString());
                asignJobBean.setProduce_cont(
                        taskViewHolder.ttv_workOutput.getTv_rightContent().getText().toString());
                asignJobBean.setBegin_time(taskViewHolder.tv_dateStart.getText().toString());
                asignJobBean.setEnd_time(taskViewHolder.tv_dateEnd.getText().toString());
                int checkedStatus=0;
                if (taskViewHolder.rg_status.getCheckedRadioButtonId()==R.id.rb_finish){
                    checkedStatus=0;
                }else if(taskViewHolder.rg_status.getCheckedRadioButtonId()==R.id.rb_working){
                    checkedStatus=1;
                }else if(taskViewHolder.rg_status.getCheckedRadioButtonId()==R.id.rb_pause){
                    checkedStatus=2;
                }
                asignJobBean.setState(checkedStatus+"");
            }
            asignJobBeanList.add(asignJobBean);
            LogUtil.i("asignJobBean:"+asignJobBean.toString());
        }
        return asignJobBeanList;

    }



    class TaskViewHolder{
        @Bind(R.id.ttv_taskName) TaskTextView ttv_taskName;
        @Bind(R.id.ttv_workOutput) TaskTextView ttv_workOutput;
        @Bind(R.id.tv_dateStart) TextView tv_dateStart;
        @Bind(R.id.tv_dateEnd) TextView tv_dateEnd;
        @Bind(R.id.rg_status) RadioGroup rg_status;
        @Bind(R.id.rb_finish) RadioButton rb_finish;
        @Bind(R.id.rb_working) RadioButton rb_working;
        @Bind(R.id.rb_pause) RadioButton rb_pause;
        @Bind(R.id.iv_taskDelete) ImageView iv_taskDelete;

        TaskViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
