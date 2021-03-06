package cn.com.bluemoon.delivery.module.offline;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.OffLineApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.offline.CurriculumsTable;
import cn.com.bluemoon.delivery.app.api.model.offline.ResultListNum;
import cn.com.bluemoon.delivery.app.api.model.offline.ResultTeacherAndStudentList;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.offline.adapter.OfflineAdapter;
import cn.com.bluemoon.lib_widget.module.form.BmSegmentView;
import cn.com.bluemoon.delivery.ui.selectordialog.DateSelectDialog;
import cn.com.bluemoon.delivery.ui.selectordialog.OnButtonClickListener;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.utils.LibViewUtil;
import cn.com.bluemoon.lib.view.CommonEmptyView;

/**
 * 列表父类
 * Created by tangqiwei on 2017/6/2.
 */

public abstract class OfflineListBaseActivity extends BaseActivity implements OnListItemClickListener, BmSegmentView.CheckCallBack, View.OnClickListener, OnButtonClickListener {

    private static final int HTTP_REQUEST_CODE_GET_NUM = 0x1000;

    @BindView(R.id.segment_tab)
    BmSegmentView segmentTab;
    @BindView(R.id.listview_offline)
    PullToRefreshListView listviewOffline;

    private View llayoutHeadview;
    private View dafHeadView;
    private TextView ytTime;
    private TextView totalCourses;
    private TextView totalDuration;
    private RelativeLayout timeSelector;

    private DateSelectDialog dialog;

    private OfflineAdapter adapter;

    private int defPosition;//默认到哪个页签

    private long curSelectorDate;//记录当前选择的时间

    public static void actionStart(Context context, int defPosition,Class cls) {
        Intent intent = new Intent(context, cls);
        intent.putExtra("defPosition", defPosition);
        context.startActivity(intent);
    }

    private List<String> getTextList() {
        List<String> titleList = new ArrayList<>();
        if(getTeacherOrStudent().equals(OfflineAdapter.LIST_TEACHER)){
            titleList.add(getString(R.string.offline_my_courses));
            titleList.add(getString(R.string.offline_courses_record));
        }else if(getTeacherOrStudent().equals(OfflineAdapter.LIST_STUDENTS)){
            titleList.add(getString(R.string.offline_waiting_to_teach));
            titleList.add(getString(R.string.offline_waiting_evaluate));
            titleList.add(getString(R.string.offline_finish));
        }else{
            titleList.add("");
            titleList.add("");
            titleList.add("");
        }
        return titleList;
    }

    @Override
    protected void onBeforeSetContentLayout() {
        defPosition = getIntent().getIntExtra("defPosition",0);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_offline_list;
    }

    @Override
    public void initView() {
        dafHeadView=new View(this);
        dafHeadView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.offline_listview_padding_top)));
        dafHeadView.setBackgroundColor(getResources().getColor(R.color.page_bg_f2));
        ytTime = (TextView) findViewById(R.id.txt_yt);
        totalCourses = (TextView) findViewById(R.id.txt_total_courses);
        totalDuration = (TextView) findViewById(R.id.txt_total_duration);
        timeSelector = (RelativeLayout) findViewById(R.id.llayout_time_selector);
        llayoutHeadview=findViewById(R.id.llayout_headview);
        timeSelector.setOnClickListener(this);
        llayoutHeadview.setVisibility(View.GONE);
        listviewOffline.getRefreshableView().addHeaderView(dafHeadView);
        initEmptyMsg();
    }


    @Override
    public void initData() {
        adapter = new OfflineAdapter(this, this, getTeacherOrStudent(),defPosition);
        segmentTab.setTextList(getTextList());
        segmentTab.checkUIChange(defPosition);
        segmentTab.setCheckCallBack(this);
        changeCurSelectorDate(System.currentTimeMillis());
        setShowHeadView(defPosition==getTextList().size()-1);
        listviewOffline.setAdapter(adapter);
        listviewOffline.setMode(PullToRefreshBase.Mode.PULL_DOWN_TO_REFRESH);
        listviewOffline.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                requestData(false);
            }
        });
        requestData(true);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        requestData(false);
    }

    protected void requestData(boolean isLoading) {
        if(isLoading)
            showWaitDialog();
        requestData();
    }

    private void requestData() {
        requestListData(getCheckPosition()==getTextList().size()-1?curSelectorDate:0,getCheckPosition());
        OffLineApi.listNum(getToken(), getTeacherOrStudent(), getNewHandler(HTTP_REQUEST_CODE_GET_NUM, ResultListNum.class));
    }

    /**
     * 请求列表数据
     * @param time
     */
    protected abstract void requestListData(long time,int requestCode);

    protected abstract String getTeacherOrStudent();

    @Override
    public void onFinishResponse(int requestCode) {
        switch (requestCode) {
            case 0:
            case 1:
            case 2:
                hideWaitDialog();
                listviewOffline.onRefreshComplete();
                break;
        }
    }

    @Override
    public void onFailureResponse(int requestCode, Throwable t) {
        super.onFailureResponse(requestCode, t);
        switch (requestCode) {
            case 0:
            case 1:
            case 2:
                if(requestCode!=getCheckPosition()){
                    return;
                }
                showEmptyView();
                break;
        }
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        switch (requestCode) {
            case 0:
            case 1:
            case 2:
                if(requestCode!=getCheckPosition()){
                    return;
                }
                ResultTeacherAndStudentList list = (ResultTeacherAndStudentList) result;
                if(list.data.courses==null||list.data.courses.isEmpty()){
                    showEmptyView();
                }else{
                    LibViewUtil.setViewVisibility(emptyView, View.GONE);
                }
                adapter.setList(list.data.courses, getCheckPosition());
                adapter.notifyDataSetChanged();
                if(isShowHead()){
                    changeToal(list.data.totalCourseNum,list.data.totalDuration);
                }
                break;
            case HTTP_REQUEST_CODE_GET_NUM:
                ResultListNum listNum = (ResultListNum) result;
                List<Integer> numList = new ArrayList<>();
                if (getTeacherOrStudent().equals(OfflineAdapter.LIST_TEACHER)) {
                    numList.add(listNum.data.teacher.inClassNum);
                    numList.add(listNum.data.teacher.closeClassNum);
                } else if (getTeacherOrStudent().equals(OfflineAdapter.LIST_STUDENTS)) {
                    numList.add(listNum.data.student.waitingClassNum);
                    numList.add(listNum.data.student.inClassNum);
                    numList.add(listNum.data.student.endClassNum);
                }
                segmentTab.setNumberList(numList);
                break;
        }
    }

    /**
     * 切换回调
     *
     * @param position
     */
    @Override
    public void checkListener(int position) {
        adapter.setList(new ArrayList<CurriculumsTable>(), position);
        setShowHeadView(isShowHead());
        adapter.notifyDataSetChanged();
        if(position==getTextList().size()-1){
            changeCurSelectorDate(System.currentTimeMillis());
            changeToal(0,0);
        }
        requestData(true);
    }

    /**
     * 是否在最后一列，要显示头部
     * @return
     */
    protected abstract boolean isShowHead();

    /**
     * 获取当前所在页签
     * @return
     */
    protected int getCheckPosition(){
        return segmentTab.getCurrentPosition();
    }

    /**
     * 代码切换
     *
     * @param position
     */
    protected void checkTab(int position) {
        segmentTab.changeTab(position);
    }

    /**
     * 是否显示headview
     * @param isShow
     */
    private void setShowHeadView(boolean isShow){
        if(isShow){
            if(llayoutHeadview.getVisibility()==View.GONE){
                llayoutHeadview.setVisibility(View.VISIBLE);
            }
        }else{
            if(llayoutHeadview.getVisibility()==View.VISIBLE){
                llayoutHeadview.setVisibility(View.GONE);
            }
        }
    }


    /**
     * 网络请求改变headview数据
     */
    private void changeHeadViewState(String courses, String duration) {
        if (isShowHead()) {
            totalCourses.setText(courses);
            totalDuration.setText(duration);
        }
    }
    /**
     * 改变当前选择的时间
     * */
    private void changeCurSelectorDate(long curSelectorDate) {
        this.curSelectorDate = curSelectorDate;
        ytTime.setText(DateUtil.getTimeToYM(curSelectorDate));
    }
    /**
     * 改变总节数和总时长显示
     * */
    private void changeToal(int totalCourseNum,long totalDuration) {
        changeHeadViewState(totalCourseNum+"节",
                String.valueOf(new DecimalFormat("0.0").format(totalDuration/3600.0))+"小时");
    }

    protected abstract String positionTogState(int position);


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llayout_time_selector:
                long endTime = System.currentTimeMillis();
                if(endTime<curSelectorDate){
                    endTime = curSelectorDate;
                }
                dialog = new DateSelectDialog(this, "请选择年份月份", 0, endTime, curSelectorDate, this);
                dialog.getNwvDate().setVisibility(View.GONE);
                dialog.show();
                break;
        }
    }

    /**
     * 空数据页面View
     */
    private View emptyView;
    private String emptyMsg;
    /**
     * 显示空数据页
     */
    protected void showEmptyView() {
        if (emptyView == null) {
            int layoutId = R.layout.viewstub_wrapper;
            final View viewStub = findViewById(R.id.viewstub_empty);
            if (viewStub != null) {
                final ViewStub stub = (ViewStub) viewStub;
                stub.setLayoutResource(layoutId);
                emptyView = stub.inflate();
                initEmptyViewEvent(emptyView);
            }
        }

        if (!TextUtils.isEmpty(emptyMsg)) {
            ((CommonEmptyView) ((ViewGroup) emptyView).getChildAt(0)).setContentText(emptyMsg);
        }

        LibViewUtil.setViewVisibility(emptyView, View.VISIBLE);
    }
    /**
     * 设置空数据页面
     *
     * @param emptyView
     */
    private void initEmptyViewEvent(View emptyView) {
        CommonEmptyView empty = new CommonEmptyView(this);
        empty.setEmptyListener(new CommonEmptyView.EmptyListener() {
            @Override
            public void onRefresh() {
                requestData(true);
            }
        });
        ((ViewGroup) emptyView).addView(empty);
    }
    /**
     * 初始化空数据页文字
     */
    private void initEmptyMsg() {
        setEmptyViewMsg(getEmptyMsg());
    }
    /**
     * 设置空数据页显示信息
     */
    final protected void setEmptyViewMsg(String msg) {
        emptyMsg = msg;

        if (emptyView != null) {
            ((CommonEmptyView) ((ViewGroup) emptyView).getChildAt(0)).setContentText(emptyMsg);
        }
    }
    /**
     * 获取空数据页文案
     */
    protected String getEmptyMsg(){
        return getString(R.string.empty_hint3,getTitleString()==null?"":getTitleString());
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public void onOKButtonClick(long timeStamp) {
        changeCurSelectorDate(timeStamp);
        requestData(true);
    }

    @Override
    public void onCancleButtonClick() {

    }

    @Override
    public String getCompareTips() {
        return getString(R.string.offline_date_compare);
    }
}
