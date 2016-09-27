package cn.com.bluemoon.delivery.sz.taskManager;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.kymjs.kjframe.utils.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.ResultToken;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.sz.api.SzApi;
import cn.com.bluemoon.delivery.sz.bean.EventMessageBean;
import cn.com.bluemoon.delivery.sz.bean.taskManager.AsignJobBean;
import cn.com.bluemoon.delivery.sz.bean.taskManager.DailyPerformanceInfoBean;
import cn.com.bluemoon.delivery.sz.bean.taskManager.UserInfoAndReViewInfoBean;
import cn.com.bluemoon.delivery.sz.bean.taskManager.UserInfoBean;
import cn.com.bluemoon.delivery.sz.bean.taskManager.UserInfoListBean;
import cn.com.bluemoon.delivery.sz.util.CacheServerResponse;
import cn.com.bluemoon.delivery.sz.util.DateUtil;
import cn.com.bluemoon.delivery.sz.util.LogUtil;
import cn.com.bluemoon.delivery.sz.util.PageJumps;
import cn.com.bluemoon.delivery.sz.view.TaskTextView;
import cn.com.bluemoon.delivery.sz.view.datepicker.adapter.NumericWheelAdapter;
import cn.com.bluemoon.delivery.sz.view.datepicker.widget.WheelView;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.view.CommonAlertDialog;

/**
 * @author jiangyh
 * 添加、修改任务页面*/
public class AddTaskActivity extends BaseActivity{

    @Bind(R.id.tv_dete) TextView tv_dete;
    @Bind(R.id.ttv_appraiser) TaskTextView ttv_appraiser;
    @Bind(R.id.ttv_totalTime) TaskTextView ttv_totalTime;
    @Bind(R.id.ll_task_item_conent) LinearLayout ll_task_item_conent;
    @Bind(R.id.tv_addTask) TextView tv_addTask;
    @Bind(R.id.ll_addTask) LinearLayout ll_addTask;
    @Bind(R.id.scrollviwe_task)
    ScrollView scrollviwe_task;
    /**用于存储在本地的实例文件*/
    public static String USERINFOLISTBEAN="UserInfoListBean";
    public static final String TASKOPERATETYPE="TASKOPERATETYPE";
    public static final int TASKOPERATETYPE_ADD=0;
    public static final int TASKOPERATETYPE_MODIFY=1;
    private int taskOperateType=1;

    public static final String DATABEAN="DATABEAN";
    private DailyPerformanceInfoBean dailyPerformanceInfoBean=null;

    public static final String CURRENTDATA="CURRENTDATA";
    private String currentDate="";

    private WheelView hour;
    private WheelView mins;

    private Context context=null;
    private final int itemSize=20;
    private int itemTag=0;

    private UserInfoBean sup=null;//上级
    private UserInfoBean user=null;//自身实体

    private static final int REQUESTUSERINFO=0;
    private static final int REQUEST_COMMIT=1;


    public static SimpleDateFormat format = new SimpleDateFormat("HH:mm");

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sz_add_task;
    }
    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        context=AddTaskActivity.this;
        currentDate=getIntent().getStringExtra(CURRENTDATA);
        taskOperateType=getIntent().getIntExtra(TASKOPERATETYPE,0);
        USERINFOLISTBEAN=USERINFOLISTBEAN+ClientStateManager.getUserName();
    }

//    RecyclerView
    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        switch (requestCode){
            case REQUESTUSERINFO:
                LogUtil.i("上级查询＝requestCode:"+requestCode+"/"+jsonString);
                UserInfoAndReViewInfoBean userInfoAndReViewInfoBean=
                        JSON.parseObject(jsonString,UserInfoAndReViewInfoBean.class);
                sup=userInfoAndReViewInfoBean.getSup();
                user=userInfoAndReViewInfoBean.getUser();
                LogUtil.i("上级："+sup.toString());
                LogUtil.i("自身："+user.toString());

                //上级不能为自己
                if (sup!=null
                        && !TextUtils.isEmpty(sup.getUID())
                        && !TextUtils.isEmpty(sup.getUName())
                        && !user.getUID().equals(sup.getUID())){
                    ttv_appraiser.setText_right(sup.getUName()+"("+sup.getUID()+")");
                }else{
                    ttv_appraiser.setText_right_hint(getString(R.string.sz_task_ttv_appraiser));
                }
                break;
            case REQUEST_COMMIT:
                LogUtil.i("提交任务＝requestCode:"+requestCode+"/"+jsonString);
                ResultBase resultBase=JSON.parseObject(jsonString,ResultBase.class);
                if (resultBase.isSuccess){

                    PublicUtil.showToast("提交任务成功！");
                    if (sup!=null && !TextUtils.isEmpty(sup.getUID())
                            && !TextUtils.isEmpty(sup.getUName())){//保存评价人到本地
                        saveReViewData(sup);
                    }
//                    mHandler.sendEmptyMessage(0);
                    //发送通知关闭详情页，并重新请求当天的数据
                    EventMessageBean messageBean=new EventMessageBean();
                    messageBean.setEventMsgAction("101");
                    EventBus.getDefault().post(messageBean);

                    ll_task_item_conent.removeAllViews();

                    PageJumps.finish(context);
                }

                break;
        }

    }

    @Override
    public void initView() {
        EventBus.getDefault().register(this);
        initCustomActionBar();

        if (taskOperateType==TASKOPERATETYPE_ADD){//默认最少添加一项
            ll_task_item_conent.addView(initTaskItemView(itemTag),itemTag);
            itemTag++;
            setItemName();
            getAllTaskTimes();

        }else if(taskOperateType==TASKOPERATETYPE_MODIFY) {//修改
            try {
                dailyPerformanceInfoBean=
                        (DailyPerformanceInfoBean) getIntent().getSerializableExtra(DATABEAN);
                //修改时带过来的日期
                currentDate=dailyPerformanceInfoBean.getWork_date();
                sup=dailyPerformanceInfoBean.getReviewer();
                user=dailyPerformanceInfoBean.getUser();

                UserInfoBean reviewerBean=dailyPerformanceInfoBean.getReviewer();
                if(reviewerBean!=null){
                    if (!TextUtils.isEmpty(reviewerBean.getUID())
                            && !TextUtils.isEmpty(reviewerBean.getUName())){
                        ttv_appraiser.setText_right(reviewerBean.getUName()+"("+reviewerBean.getUID()+")");
                    }
                }
                ttv_totalTime.setText_right(dailyPerformanceInfoBean.getDay_valid_min());

                List<AsignJobBean>  asignJobBeanList =dailyPerformanceInfoBean.getAsignJobs();
                initSetViewContentList(asignJobBeanList);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        String[] date=currentDate.split("-");
        String year=date[0];
        String mooth=date[1];
        String day=date[2];
        tv_dete.setText(mooth+"月"+day+"日");

        ttv_appraiser.setOnRightTextOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle mBundle=new Bundle();
                mBundle.putString(AppraiseChooserActivity.APPRAISE_NAME,
                        ttv_appraiser.getTv_rightContent().getText().toString());
                mBundle.putInt(AppraiseChooserActivity.APPRAISE_NAME_ACTION,
                        AppraiseChooserActivity.APPRAISE_NAME_ACTION_CONTENT);
                if (user!=null){
                    mBundle.putSerializable(AppraiseChooserActivity.USERBEAN,user);
                }

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
            getAllTaskTimes();
            setItemName();
        }
    }

    /**根据实体 直充内容*/
    private void initViewForBean(AsignJobBean asignJobBean,int itemTag){
        View view =ll_task_item_conent.getChildAt(itemTag);
        TaskViewHolder taskViewHolder=new TaskViewHolder(view);
        taskViewHolder.ttv_taskName.setText_right(asignJobBean.getTask_cont());
        taskViewHolder.ttv_taskName.setRtGravityStyle(0);//左对齐
        taskViewHolder.ttv_workOutput.setText_right(asignJobBean.getProduce_cont());
        taskViewHolder.ttv_workOutput.setRtGravityStyle(0);//左对齐
        taskViewHolder.tv_dateStart.setText(asignJobBean.getBegin_time());
        taskViewHolder.tv_dateEnd.setText(asignJobBean.getEnd_time());
        String taskStatus=asignJobBean.getState();
        /**0未开始，1进行中，2已完成，3暂停*/
        switch (taskStatus){
            case "2":
                taskViewHolder.rg_status.check(taskViewHolder.rb_finish.getId());
                break;
            case "1":
                taskViewHolder.rg_status.check(taskViewHolder.rb_working.getId());
                break;
            case "3":
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
//            为评价人的回传实体
            sup=JSON.parseObject(messageBean.getEventMsgContent(),UserInfoBean.class);
            ttv_appraiser.setText_right(sup.getUName()+"("+sup.getUID()+")");
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
            taskViewHolder.ttv_taskName.setRtGravityStyle(0);
        }else if(messageBean.getReMark().equals("ttv_workOutput")){
            taskViewHolder.ttv_workOutput.setText_right(messageBean.getEventMsgContent());
            taskViewHolder.ttv_workOutput.setRtGravityStyle(0);
        }

    }

    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
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
                    submitDayJobsApi();
            }
            @Override
            public void onBtnLeft(View v) {
                finish();
            }
            @Override
            public void setTitle(TextView v) {
                if (taskOperateType==TASKOPERATETYPE_ADD){
                    v.setText(R.string.sz_task_add_task);
                }else if(taskOperateType==TASKOPERATETYPE_MODIFY){
                    v.setText(R.string.sz_task_modify_task);

                }
            }
        });
        TextView tv_right=titleBar.getTvRightView();
        tv_right.setVisibility(View.VISIBLE);
        tv_right.setText("确定");
    }

    private void showGetUserInfoDialog(String content){
        CommonAlertDialog.Builder dialog = new CommonAlertDialog.Builder(
                AddTaskActivity.this);
        dialog.setMessage(content);
        dialog.setPositiveButton(R.string.btn_ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showWaitDialog();
                        getuserinfo(ClientStateManager.getUserName());
                        dialog.dismiss();
                    }
                });
        dialog.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**提交工作任务*/
    public void submitDayJobsApi(){
//      时间是否有交集
        List<AsignJobBean> asignJobs=getAllTastContent();
        AsignJobBean asignJobBean=null;
        AsignJobBean asignJobBeanNext=null;

        boolean isOverlap=false;
        for (int i=0;i<asignJobs.size();i++){
            asignJobBean=asignJobs.get(i);
            String startTime1=asignJobBean.getBegin_time();
            String endTime1=asignJobBean.getEnd_time();
            /**任务项时长不可等于0*/
            if (startTime1.equals(endTime1)){
                PublicUtil.showToast("任务"+asignJobBean.getTask_idx()+"时长不可为0");
                isOverlap=true;
                return;
            }

//                提示不可为空 名称跟内容
            String taskName=asignJobBean.getTask_cont();
            String taskConent=asignJobBean.getProduce_cont();

            if (TextUtils.isEmpty(taskName)){
                PublicUtil.showToast( "任务"+asignJobBean.getTask_idx()+"内容不可为空！");
                return;
            }
            if (TextUtils.isEmpty(taskConent)){
                PublicUtil.showToast( "任务"+asignJobBean.getTask_idx()+"工作输出内容不可为空！");
                return;
            }
            for(int j=i+1;j<asignJobs.size();j++){
                asignJobBeanNext=asignJobs.get(j);
                String startTime2=asignJobBeanNext.getBegin_time();
                String endTime2=asignJobBeanNext.getEnd_time();

                isOverlap=isOverlap(startTime1,endTime1,startTime2,endTime2);

                LogUtil.w("是否有交集：------>"+isOverlap);
                if (isOverlap==true){
                    PublicUtil.showToast("任务 "+asignJobBean.getTask_idx()
                            +"时间与任务 "+asignJobBeanNext.getTask_idx()+"时间有冲突");
                    return;
                }
            }
        }
        /**网络请求 添加*/
        if (isOverlap==false){
            final DailyPerformanceInfoBean submitData=new DailyPerformanceInfoBean();
            submitData.setAsignJobs(asignJobs);

            if (user==null ||
                    TextUtils.isEmpty(user.getUID())
                    || TextUtils.isEmpty(user.getUName())){
                showGetUserInfoDialog("未获得到个人信息内容，请重新获取！");
            }else{
                submitData.setUser(user);
            }
            String appraiserName=ttv_appraiser.getTv_rightContent().getText().toString();
            if (sup==null ||
                    TextUtils.isEmpty(appraiserName)
                    || TextUtils.isEmpty(sup.getUID())
                    || TextUtils.isEmpty(sup.getUName())){
                PublicUtil.showToast("评价人不可为空，请选择评价人！");
                return;
            }else{
                if (!user.getUID().equals(sup.getUID()))
                    submitData.setReviewer(sup);
                else
                    PublicUtil.showToast("评价人不可以是自己，请重新选择评价人！");
            }


            submitData.setWork_date(DateUtil.tranDateToTime(currentDate,"yyyy-MM-dd")+"");
            LogUtil.w("任务添加实体转换："+JSON.toJSONString(submitData));

            if (taskOperateType==TASKOPERATETYPE_MODIFY){
    //            日工作id
                submitData.setWork_day_id(dailyPerformanceInfoBean.getWork_day_id());
                CommonAlertDialog.Builder dialog = new CommonAlertDialog.Builder(
                        AddTaskActivity.this);
                dialog.setMessage("确定修改工作任务？");
                dialog.setPositiveButton(R.string.btn_ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showWaitDialog();
                                SzApi.submitDayJobsApi(submitData,"1",getNewHandler(REQUEST_COMMIT, ResultToken.class));
                                dialog.dismiss();
                            }
                        });
                dialog.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }else{
                showWaitDialog();
                SzApi.submitDayJobsApi(submitData,"0",getNewHandler(REQUEST_COMMIT, ResultToken.class));
            }
        }
    }


    /**保存评价员的信息到本地缓存中 最多五十个*/
    private void saveReViewData(UserInfoBean currUserInfoBean){
        UserInfoListBean userInfoListBean=null;//本地取出的实例

        List<UserInfoBean> userInfoBeanList=new ArrayList<>();//本的的list
        List<UserInfoBean> userInfoBeanListTemp=new ArrayList<>();//用于记录存在的信息 用于删除
        List<UserInfoBean> userInfoBeanListFinal=new ArrayList<>();//中转用后最后的排序

        userInfoListBean=
                (UserInfoListBean) CacheServerResponse.readObject(context,USERINFOLISTBEAN);
        if (userInfoListBean!=null){
            userInfoBeanList=userInfoListBean.getData();
            //对比是否存在
            for (UserInfoBean userInfoBean:userInfoBeanList) {
                LogUtil.e("本地缓存的用户信息："+userInfoBean.toString());
                if (userInfoBean.getUID().equals(currUserInfoBean.getUID())){
                    userInfoBeanListTemp.add(userInfoBean);
                }
            }
            userInfoBeanList.removeAll(userInfoBeanListTemp);

            userInfoBeanListFinal.add(currUserInfoBean);
            userInfoBeanListFinal.addAll(userInfoBeanList);

//            Collections.reverse(userInfoBeanList);
            for (UserInfoBean userInfoBean:userInfoBeanListFinal) {
                LogUtil.e("中转 本地缓存的用户信息："+userInfoBean.toString());
            }
            userInfoListBean.setData(userInfoBeanListFinal);

        }else{
            userInfoListBean=new UserInfoListBean();
            userInfoBeanListFinal.add(currUserInfoBean);
            userInfoListBean.setData(userInfoBeanListFinal);
            LogUtil.e("第一次本地缓存的用户信息："+currUserInfoBean.toString());
        }
            CacheServerResponse.saveObject(context,
                    USERINFOLISTBEAN, userInfoListBean);

    }


    private  boolean isOverlap(String startdate1, String enddate1,String startdate2, String enddate2) {
        Date leftStartDate = null;
        Date leftEndDate = null;
        Date rightStartDate = null;
        Date rightEndDate = null;
        try {
            leftStartDate = format.parse(
                    String.valueOf(DateUtil.tranTimeToDate(startdate1,"HH:mm")));
            leftEndDate = format.parse(
                    String.valueOf(DateUtil.tranTimeToDate(enddate1,"HH:mm")));
            rightStartDate = format.parse(
                    String.valueOf(DateUtil.tranTimeToDate(startdate2,"HH:mm")));
            rightEndDate = format.parse(
                    String.valueOf(DateUtil.tranTimeToDate(enddate2,"HH:mm")));

            LogUtil.e("leftStartDate:"+leftStartDate+"//"
                    +"leftEndDate:"+leftEndDate+"//"+
                    "rightStartDate:"+rightStartDate+"//"+
                    "rightEndDate:"+rightEndDate+"//");

        } catch (ParseException e) {
            return false;
        }

        return
                ((leftStartDate.getTime() >= rightStartDate.getTime())
                        && leftStartDate.getTime() < rightEndDate.getTime())
                        ||
                        ((leftStartDate.getTime() > rightStartDate.getTime())
                                && leftStartDate.getTime() <= rightEndDate.getTime())
                        ||
                        ((rightStartDate.getTime() >= leftStartDate.getTime())
                                && rightStartDate.getTime() < leftEndDate.getTime())
                        ||
                        ((rightStartDate.getTime() > leftStartDate.getTime())
                                && rightStartDate.getTime() < leftEndDate.getTime());
    }

        @Override
    public void initData() {
        //添加时才自行搜索上级
        if (taskOperateType==TASKOPERATETYPE_ADD){
            getuserinfo(ClientStateManager.getUserName());
        }

    }

    /**查询上级及自己的人员信息*/
    private void getuserinfo(String account){
        if (!StringUtils.isEmpty(account)) {
            SzApi.getuserinfo(account, getNewHandler(REQUESTUSERINFO, ResultToken.class));
        }
    }

    @OnClick({R.id.tv_addTask,R.id.ll_addTask, R.id.tv_dete})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_addTask:
            case R.id.ll_addTask:
                if (itemTag<itemSize){
                    ll_task_item_conent.addView(initTaskItemView(itemTag),itemTag);
                    itemTag++;
                    setItemName();
                    getAllTaskTimes();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollviwe_task.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                    });
                }else  if(itemTag>=itemSize) {
//                    tv_addTask.setVisibility(View.GONE);
//                    ll_addTask.setVisibility(View.GONE);
                    PublicUtil.showToast("您今日工作任务数量达到20项，已不能继续添加！");
                }
                break;
            case R.id.tv_dete:

                break;
        }
    }

    public View initTaskItemView(final int Tag){
        /**获得上一个任务的时间 默认加1分钟 结束时间再加一小时*/
        String nextSTime="";
        String nextETime="";
        if (itemTag>=1){
            TaskViewHolder taskViewHolder=
                    new TaskViewHolder(ll_task_item_conent.getChildAt(itemTag-1));
            String preStartTime=taskViewHolder.tv_dateStart.getText().toString();
            String preEndTime=taskViewHolder.tv_dateEnd.getText().toString();

                long preST=DateUtil.tranDateToTime(preStartTime);
                long preET=DateUtil.tranDateToTime(preEndTime);

                long nextST=preET;//加一分钟 re 不加一分钟
                long nextET=nextST+(1000*60*60);//加一小时
                nextSTime=DateUtil.tranTimeToDate(nextST+"");
                nextETime=DateUtil.tranTimeToDate(nextET+"");

        }
        final View view= LayoutInflater.from(context).inflate(R.layout.activity_sz_add_item,null);
        final TaskViewHolder taskViewHolder=new TaskViewHolder(view);

        //出现跨天的现象导致出现负数，直接设置成23：59
        if (!TextUtils.isEmpty(nextSTime) && !TextUtils.isEmpty(nextETime)){
            int preStarth=Integer.parseInt(nextSTime.split(":")[0]);
            int preStartm=Integer.parseInt(nextSTime.split(":")[1]);

            int preEndh=Integer.parseInt(nextETime.split(":")[0]);
            int preEndm=Integer.parseInt(nextETime.split(":")[1]);

            if (Tag!=0){//
                if (preEndh>=preStarth){//正常
                    taskViewHolder.tv_dateStart.setText(nextSTime);
                    taskViewHolder.tv_dateEnd.setText(nextETime);
                }else{//23:30-00:30  结束时间设为23：59
                    taskViewHolder.tv_dateStart.setText(nextSTime);
                    taskViewHolder.tv_dateEnd.setText("23:59");
                }
            }
        }


        taskViewHolder.ttv_taskName.setOnRightTextOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle mbBundle=new Bundle();
//                PublicUtil.showToast("ttv_taskName");

                mbBundle.putInt(InputToolsActivity.INTENTITEMTAG,Tag);
                mbBundle.putInt(InputToolsActivity.MAXTEXTLENGHT,50);
                mbBundle.putString(InputToolsActivity.VIEWNAME,"ttv_taskName");
                mbBundle.putString(InputToolsActivity.INPUTTITEL,
                        taskViewHolder.ttv_taskName.getTv_leftName().getText().toString());

                mbBundle.putString(InputToolsActivity.INPUTTITELCONTENT,
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

                mbBundle.putInt(InputToolsActivity.INTENTITEMTAG,Tag);
                mbBundle.putInt(InputToolsActivity.MAXTEXTLENGHT,200);
                mbBundle.putString(InputToolsActivity.VIEWNAME,"ttv_workOutput");
                mbBundle.putString(InputToolsActivity.INPUTTITEL,
                        taskViewHolder.ttv_taskName.getTv_leftName().getText().toString()+"工作输出");
                mbBundle.putString(InputToolsActivity.INPUTTITELCONTENT,
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
                String endTimes=taskViewHolder.tv_dateEnd.getText().toString();

                showTimeDialogStart(taskViewHolder,endTimes,currHour,currMin,"开始时间");
            }
        });
        taskViewHolder.tv_dateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time=taskViewHolder.tv_dateEnd.getText().toString();
                String[] times=time.split(":");
                int currHour=Integer.parseInt(times[0]);
                int currMin=Integer.parseInt(times[1]);

                String startTimes=taskViewHolder.tv_dateStart.getText().toString();

                showTimeDialogEnd(taskViewHolder,startTimes,currHour,currMin,"结束时间");
            }
        });

        taskViewHolder.iv_taskDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemTag>1){
                    CommonAlertDialog.Builder dialog = new CommonAlertDialog.Builder(
                            AddTaskActivity.this);
                    dialog.setMessage(String.format(getString(R.string.sz_task_delete_dialog_hint),
                            taskViewHolder.ttv_taskName.getTv_leftName().getText().toString()));
                    dialog.setPositiveButton(R.string.btn_ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    showDeleteAnimator(view);
                                    mHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            //                添加动画
                                            ll_task_item_conent.removeView(view);
                                            itemTag--;
                                            setItemName();
                                            getAllTaskTimes();
                                            if (itemTag<itemSize) {
                                                tv_addTask.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    },400);
                                dialog.dismiss();

                                }

                            });
                    dialog.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();

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
     * 显示时间 start
     * 开始时间如果大于结束时间 结束时间默认跟开始时间一样
     */
    private void showTimeDialogStart(final TaskViewHolder taskViewHolder, final String endTimes,
                                     int currHour, int currMin, String timeTilte){
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

//                当结束时间小于开始时间时
                String finalStartT=finalHours+":"+finalMins;
                long start=DateUtil.tranDateToTime(finalStartT);
                long end=DateUtil.tranDateToTime(endTimes);

                if (end<=start){//默认加一分钟
                    long endTime=start+1000*60;
                    String endFinalTime=DateUtil.tranTimeToDate(String.valueOf(endTime));
                    taskViewHolder.tv_dateEnd.setText(endFinalTime);
                }

                LogUtil.i("选取后的时间："+finalStartT);
                taskViewHolder.tv_dateStart.setText(finalStartT);
                getAllTaskTimes();
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
    /**
     * 显示时间 end
     * 结束时间不可小于开始时间
     */
    private void showTimeDialogEnd(final TaskViewHolder taskViewHolder,
                                   final String startTims, int currHour, int currMin, String timeTilte){
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
                //当结束时间小于开始时间时，提示不可选
                String finalEndT=finalHours+":"+finalMins;
                long start=DateUtil.tranDateToTime(startTims);
                long end=DateUtil.tranDateToTime(finalEndT);
                if (end<=start){
                    PublicUtil.showToast("结束时间不可小于等于开始时间！");
                }else{
                    LogUtil.i("选取后的时间："+finalEndT);
                    taskViewHolder.tv_dateEnd.setText(finalEndT);
                    getAllTaskTimes();
                    dialog.cancel();

                }
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

//                时间转成时间戳
                String finalStartTimes=currentDate+" "+taskViewHolder.tv_dateStart.getText().toString()+":00";
                String finalEndTimes=currentDate+" "+taskViewHolder.tv_dateEnd.getText().toString()+":00";

                long finalStartT=DateUtil.tranDateToTime(finalStartTimes,"yyyy-MM-dd HH:mm:ss");
                long finalEndT=DateUtil.tranDateToTime(finalEndTimes,"yyyy-MM-dd HH:mm:ss");

                LogUtil.w("年月日时间---转成开始时间戳 "+finalStartT);
                LogUtil.w("年月日时间---转成结束时间戳 "+finalEndT);

//                得到时长 216000
                long totalTime=(finalEndT-finalStartT)/(1000*60);
                asignJobBean.setUsage_time(String.valueOf(totalTime));

                asignJobBean.setBegin_time(String.valueOf(finalStartT));
                asignJobBean.setEnd_time(String.valueOf(finalEndT));

                asignJobBean.setTask_idx(String.valueOf(i+1));
                /**0未开始，1进行中，2已完成，3暂停*/
                int checkedStatus=0;
                if (taskViewHolder.rg_status.getCheckedRadioButtonId()==R.id.rb_finish){
                    checkedStatus=2;
                }else if(taskViewHolder.rg_status.getCheckedRadioButtonId()==R.id.rb_working){
                    checkedStatus=1;
                }else if(taskViewHolder.rg_status.getCheckedRadioButtonId()==R.id.rb_pause){
                    checkedStatus=3;
                }
                asignJobBean.setState(checkedStatus+"");

                /**是修改的情况*/
                try {
                    if (taskOperateType==TASKOPERATETYPE_MODIFY){
                        //修改
                        List<AsignJobBean> modifyAsignJobBeanList=dailyPerformanceInfoBean.getAsignJobs();
                        AsignJobBean modifyAsignJobBean=modifyAsignJobBeanList.get(i);
                        if (modifyAsignJobBean!=null){
                            asignJobBean.setWork_day_id(modifyAsignJobBean.getWork_day_id());
                            asignJobBean.setWork_task_id(modifyAsignJobBean.getWork_task_id());
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            asignJobBeanList.add(asignJobBean);
            LogUtil.i("asignJobBean:"+asignJobBean.toString());
        }
        return asignJobBeanList;
    }

    /**遍历所有的item 得到总时长*/
    public void getAllTaskTimes(){
        long mins=0;
        for (int i = 0; i < itemTag; i++) {
            TaskViewHolder taskViewHolder=
                    new TaskViewHolder(ll_task_item_conent.getChildAt(i));

            String start=taskViewHolder.tv_dateStart.getText().toString();
            String end=taskViewHolder.tv_dateEnd.getText().toString();

            long startTime= DateUtil.tranDateToTime(start);
            long endTime=DateUtil.tranDateToTime(end);
            long totalTime=(endTime-startTime)/(1000*60);

            mins+=totalTime;

        }
            ttv_totalTime.setText_right(mins+"分钟");
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
