package cn.com.bluemoon.delivery.module.offline;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.OffLineApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.offline.CurriculumsTable;
import cn.com.bluemoon.delivery.app.api.model.offline.ResultListNum;
import cn.com.bluemoon.delivery.app.api.model.offline.ResultTeacherAndStudentList;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.offline.adapter.OfflineAdapter;
import cn.com.bluemoon.delivery.ui.common.BmSegmentView;
import cn.com.bluemoon.delivery.ui.selectordialog.DateSelectDialog;
import cn.com.bluemoon.delivery.ui.selectordialog.OnButtonClickListener;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;

/**
 * 列表父类
 * Created by tangqiwei on 2017/6/2.
 */

public abstract class OfflineListBaseActivity extends BaseActivity implements OnListItemClickListener, BmSegmentView.CheckCallBack, View.OnClickListener, OnButtonClickListener {

    private static final int HTTP_REQUEST_CODE_GET_NUM = 0x1000;

    @Bind(R.id.segment_tab)
    BmSegmentView segmentTab;
    @Bind(R.id.listview_offline)
    PullToRefreshListView listviewOffline;

    private View headView;
    private TextView ytTime;
    private TextView totalCourses;
    private TextView totalDuration;
    private RelativeLayout timeSelector;

    private DateSelectDialog dialog;

    private OfflineAdapter adapter;

    private String status;//默认到哪个页签

    private long curSelectorDate;//记录当前选择的时间

    public static void actionStart(Context context, String status,Class cls) {
        Intent intent = new Intent(context, cls);
        intent.putExtra("status", status);
        context.startActivity(intent);
    }

    public String getStatus() {
        return status;
    }

    private List<String> getTextList() {
        List<String> titleList = new ArrayList<>();
        if(getTeacherOrStudent().equals(OfflineAdapter.LIST_TEACHER)){
            titleList.add(getString(R.string.offline_waiting_courses));
            titleList.add(getString(R.string.offline_courses_ing));
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
        status = getIntent().getStringExtra("status");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_offline_list;
    }

    @Override
    public void initView() {
        headView = LayoutInflater.from(this).inflate(R.layout.headview_offline_list, null);
        ytTime = (TextView) headView.findViewById(R.id.txt_yt);
        totalCourses = (TextView) headView.findViewById(R.id.txt_total_courses);
        totalDuration = (TextView) headView.findViewById(R.id.txt_total_duration);
        timeSelector = (RelativeLayout) headView.findViewById(R.id.llayout_time_selector);
        timeSelector.setOnClickListener(this);
        headView.setVisibility(View.GONE);
    }


    @Override
    public void initData() {
        adapter = new OfflineAdapter(this, this, getTeacherOrStudent(),stateTogPosition(status));
        segmentTab.setTextList(getTextList());
        segmentTab.checkUIChange(stateTogPosition(status));
        segmentTab.setCheckCallBack(this);
        changeCurSelectorDate(System.currentTimeMillis());
        setShowHeadView(status.equals(Constants.OFFLINE_STATUS_END_CLASS));
        listviewOffline.setAdapter(adapter);
        listviewOffline.setMode(PullToRefreshBase.Mode.PULL_DOWN_TO_REFRESH);
        listviewOffline.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                requestData();
            }
        });
        requestData();
    }


    private void requestData() {
        requestData(0);
    }

    private void requestData(long time) {
        requestListData(time);
        OffLineApi.listNum(getToken(), getTeacherOrStudent(), getNewHandler(HTTP_REQUEST_CODE_GET_NUM, ResultListNum.class));
    }

    /**
     * 请求列表数据
     * @param time
     */
    protected abstract void requestListData(long time);

    protected abstract String getTeacherOrStudent();


    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        switch (requestCode) {
            case 0:
            case 1:
            case 2:
                if(requestCode!=segmentTab.getCurrentPosition()){
                    return;
                }
                hideWaitDialog();
                ResultTeacherAndStudentList list = (ResultTeacherAndStudentList) result;
                listviewOffline.onRefreshComplete();
                adapter.setList(list.data.courses, stateTogPosition(status));
                adapter.notifyDataSetChanged();
                if(status.equals(Constants.OFFLINE_STATUS_END_CLASS)){
                    changeHeadViewState(list.data.totalCourseNum+"节",
                            String.valueOf(new DecimalFormat("0.0").format(list.data.totalDuration/3600.0))+"小时");
                }
                break;
            case HTTP_REQUEST_CODE_GET_NUM:
                ResultListNum listNum = (ResultListNum) result;
                List<Integer> numList = new ArrayList<>();
                if (getTeacherOrStudent().equals(OfflineAdapter.LIST_TEACHER)) {
                    numList.add(listNum.data.teacher.waitingClassNum);
                    numList.add(listNum.data.teacher.inClassNum);
                    numList.add(listNum.data.teacher.endClassNum);

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
        status = positionTogState(position);
        adapter.setList(new ArrayList<CurriculumsTable>(), position);
        setShowHeadView(status.equals(Constants.OFFLINE_STATUS_END_CLASS));
        adapter.notifyDataSetChanged();
        requestData();
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
            if(headView.getVisibility()==View.GONE){
                headView.setVisibility(View.VISIBLE);
                listviewOffline.getRefreshableView().addHeaderView(headView);
            }
        }else{
            if(headView.getVisibility()==View.VISIBLE){
                headView.setVisibility(View.GONE);
                listviewOffline.getRefreshableView().removeHeaderView(headView);
            }
        }
    }


    /**
     * 网络请求改变headview数据
     */
    private void changeHeadViewState(String courses, String duration) {
        if (status.equals(Constants.OFFLINE_STATUS_END_CLASS)) {
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


    private int stateTogPosition(String status) {
        switch (status) {
            case Constants.OFFLINE_STATUS_WAITING_CLASS:
                return OfflineAdapter.LIST_NOSTART;
            case Constants.OFFLINE_STATUS_IN_CLASS:
                return OfflineAdapter.LIST_ING;
            case Constants.OFFLINE_STATUS_END_CLASS:
            case Constants.OFFLINE_STATUS_CLOSE_CLASS:
                return OfflineAdapter.LIST_END;
            default:
                return 0;
        }
    }

    private String positionTogState(int position) {
        switch (position) {
            case OfflineAdapter.LIST_NOSTART:
                return Constants.OFFLINE_STATUS_WAITING_CLASS;
            case OfflineAdapter.LIST_ING:
                return Constants.OFFLINE_STATUS_IN_CLASS;
            case OfflineAdapter.LIST_END:
                return Constants.OFFLINE_STATUS_END_CLASS;
            default:
                return Constants.OFFLINE_STATUS_WAITING_CLASS;
        }
    }

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
        requestData(timeStamp);
    }

    @Override
    public void onCancleButtonClick() {

    }
}
