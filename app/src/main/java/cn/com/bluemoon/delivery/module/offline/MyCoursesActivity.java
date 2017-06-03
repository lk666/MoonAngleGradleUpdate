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
import cn.com.bluemoon.delivery.app.api.model.offline.ResultTeacherList;
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
 * Created by tangqiwei on 2017/6/2.
 */

public class MyCoursesActivity extends BaseActivity implements OnListItemClickListener, BmSegmentView.CheckCallBack, View.OnClickListener, OnButtonClickListener {

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

    public static void actionStart(Context context) {
        actionStart(context, Constants.OFFLINE_STATUS_WAITING_CLASS);
    }

    public static void actionStart(Context context, String status) {
        Intent intent = new Intent(context, MyCoursesActivity.class);
        intent.putExtra("status", status);
        context.startActivity(intent);
    }

    private List<String> getTextList() {
        List<String> titleList = new ArrayList<>();
        titleList.add(getString(R.string.offline_waiting_courses));
        titleList.add(getString(R.string.offline_courses_ing));
        titleList.add(getString(R.string.offline_courses_record));
        return titleList;
    }

    @Override
    protected void onBeforeSetContentLayout() {
        status = getIntent().getStringExtra("status");
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.offline_my_courses);
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
        adapter = new OfflineAdapter(this, this, OfflineAdapter.LIST_TEACHER);
    }

    @Override
    public void initData() {
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
                requestLiatData();
            }
        });
        requestLiatData();
    }


    private void requestLiatData() {
        requestLiatData(0);
    }

    private void requestLiatData(long time) {
        OffLineApi.teacherCourseList(getToken(), time, status, getNewHandler(0, ResultTeacherList.class));
        OffLineApi.listNum(getToken(), getTeacherOrStudent(), getNewHandler(1, ResultListNum.class));
    }

    private String getTeacherOrStudent() {
        return TEACHER;
    }

    public static final String TEACHER = "teacher", STUDENT = "student";

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        switch (requestCode) {
            case 0:
                ResultTeacherList teacherList = (ResultTeacherList) result;
                listviewOffline.onRefreshComplete();
                adapter.setList(teacherList.data.courses, stateTogPosition(status));
                adapter.notifyDataSetChanged();
                if(status.equals(Constants.OFFLINE_STATUS_END_CLASS)){
                    changeHeadViewState(teacherList.data.totalCourseNum+"节",
                            String.valueOf(new DecimalFormat("0.0").format(teacherList.data.totalDuration/3600.0))+"小时");
                }
                break;
            case 1:
                ResultListNum listNum = (ResultListNum) result;
                List<Integer> list = new ArrayList<>();
                if (getTeacherOrStudent().equals(TEACHER)) {
                    list.add(listNum.data.teacher.waitingClassNum);
                    list.add(listNum.data.teacher.inClassNum);
                    list.add(listNum.data.teacher.endClassNum);

                } else if (getTeacherOrStudent().equals(STUDENT)) {
                    list.add(listNum.data.student.waitingClassNum);
                    list.add(listNum.data.student.inClassNum);
                    list.add(listNum.data.student.endClassNum);
                }
                segmentTab.setNumberList(list);
                break;
        }
    }

    @Override
    public void onItemClick(Object item, View view, int position) {

    }

    /**
     * 点击切换
     *
     * @param position
     */
    @Override
    public void checkListener(int position) {
        status = positionTogState(position);
        adapter.setList(new ArrayList<CurriculumsTable>(), position);
        setShowHeadView(status.equals(Constants.OFFLINE_STATUS_END_CLASS));
        adapter.notifyDataSetChanged();
        requestLiatData();
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
     * 代码切换
     *
     * @param position
     */
    private void checkTab(int position) {
        segmentTab.checkUIChange(position);
        checkListener(position);
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
                dialog = new DateSelectDialog(this, "请选择年份月份", 0, System.currentTimeMillis(), curSelectorDate, this);
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
        requestLiatData(timeStamp);
    }

    @Override
    public void onCancleButtonClick() {

    }
}
