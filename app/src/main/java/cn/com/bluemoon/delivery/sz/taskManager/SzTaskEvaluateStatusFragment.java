package cn.com.bluemoon.delivery.sz.taskManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseFragment;
import cn.com.bluemoon.delivery.sz.adapter.TaskEvaluateStatusAdapter;
import cn.com.bluemoon.delivery.sz.api.SzApi;
import cn.com.bluemoon.delivery.sz.bean.taskManager.AsignJobBean;
import cn.com.bluemoon.delivery.sz.bean.taskManager.DailyPerformanceInfoBean;
import cn.com.bluemoon.delivery.sz.bean.taskManager.ResultGetTaskEvaluateList;
import cn.com.bluemoon.delivery.sz.bean.taskManager.UserInfoBean;
import cn.com.bluemoon.delivery.sz.util.LogUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;

/**
 * Created by Wan.N
 * Date       2016/9/7
 * Desc      待评价/已评价界面（共用）
 */
public class SzTaskEvaluateStatusFragment extends BaseFragment {
    @Bind(R.id.evaluate_data_lv)
    PullToRefreshListView evaluate_data_lv;

    private static final int LOAD_DATA_SUCCESS = 0x01;
    private static final int LOAD_DATA_FAIL = 0x02;
    private static final int LOAD_DATA_ERROR = 0x03;

    public static final String ACTIVITY_TYPE = "ACTIVITY_TYPE";
    public static final int ACTIVITY_TYPE_TO_EVALUATE = 0;//待评价
    public static final int ACTIVITY_TYPE_HAVE_EVALUATED = 1;//已评价
    private int activityType = 0;//记录需要展示的类型（0;待评价  1;已评价）

    private int curPage = 1;//评价的请求页数，默认为1

    private List<DailyPerformanceInfoBean> mEvaluateDatas = new ArrayList<>();//存放已评价/未评价的数据集合
    private TaskEvaluateStatusAdapter mDataAapter;

    @Override
    protected void onBeforeCreateView() {
        super.onBeforeCreateView();
        initIntent();
    }

    protected void initIntent() {
        Bundle bundle = getArguments();
        activityType = bundle.getInt(ACTIVITY_TYPE, -1);
        LogUtil.i("evaluateType:" + activityType);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.sz_fragment_task_evaluate_status;
    }

    @Override
    public void initView() {
        mDataAapter = new TaskEvaluateStatusAdapter(getActivity(), mEvaluateDatas);
        evaluate_data_lv.setMode(PullToRefreshBase.Mode.BOTH);
        evaluate_data_lv.getLoadingLayoutProxy().setRefreshingLabel(getString(R.string.sz_listview_loding_label));
        evaluate_data_lv.setAdapter(mDataAapter);
        initListener();
    }

    @Override
    public void initData() {
        curPage = 1;
        loadData();
//        loadData2();
    }

    //TODO 模拟数据
    private void loadData2() {
        DailyPerformanceInfoBean dailyPerformanceInfoBean = new DailyPerformanceInfoBean();
        UserInfoBean userInfoBean = new UserInfoBean();
        userInfoBean.setUAvatar("https://ps.ssl.qhimg.com/dr/_110_100/t0102672bd8a6bd290e.jpg#1473665509#1473665509");
        userInfoBean.setUName("去去去");
        dailyPerformanceInfoBean.setUser(userInfoBean);
        dailyPerformanceInfoBean.setDay_score("200");
        dailyPerformanceInfoBean.setDay_valid_min("279");
        dailyPerformanceInfoBean.setWork_date("2016-09-09");
        AsignJobBean asignJobBean = new AsignJobBean();
        asignJobBean.setProduce_cont("asdf今天天气好热");
        asignJobBean.setTask_cont("今天天气好热");
        List<AsignJobBean> asignJobBeanList = new ArrayList<>();
        asignJobBeanList.add(asignJobBean);
        asignJobBeanList.add(asignJobBean);
        asignJobBeanList.add(asignJobBean);
        asignJobBeanList.add(asignJobBean);
        dailyPerformanceInfoBean.setAsignJobs(asignJobBeanList);

        mEvaluateDatas.add(dailyPerformanceInfoBean);
        mEvaluateDatas.add(dailyPerformanceInfoBean);
        mEvaluateDatas.add(dailyPerformanceInfoBean);
        updateData();

    }

    private void loadData() {
        //请求后台数据
        LogUtil.i("loadData--curPage:" + curPage + "--activityType:" + activityType);
        SzApi.getRateJobsList(curPage + "", 20 + "", activityType + "", getNewHandler(0, ResultGetTaskEvaluateList.class));
    }

    private void initListener() {
        evaluate_data_lv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                mHandle.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadData();
                    }
                }, 800);
            }
        });

        evaluate_data_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = null;
                DailyPerformanceInfoBean itemInfo = mDataAapter.getItem(position);
                if (activityType == ACTIVITY_TYPE_TO_EVALUATE) {
                    intent = new Intent(getActivity(), SzWriteEvaluateActivity.class);
                    intent.putExtra(SzWriteEvaluateActivity.ACTIVITY_TYPE, SzWriteEvaluateActivity.ACTIVITY_TYPE_WRTTE_EVALUATE);
                } else if (activityType == ACTIVITY_TYPE_HAVE_EVALUATED) {
                    intent = new Intent(getActivity(), SzTaskOrEvaluateDetailActivity.class);
                    intent.putExtra(SzTaskOrEvaluateDetailActivity.ACTIVITY_TYPE, SzTaskOrEvaluateDetailActivity.ACTIVITY_TYPE_EVALUATE_DETAIL);
                } else {
                }
                intent.putExtra(SzWriteEvaluateActivity.ACTIVITY_EXTAR_DATA, itemInfo);
                if (intent != null) {
                    getActivity().startActivity(intent);
                }
            }
        });
    }

    private Handler mHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_DATA_SUCCESS:
                    evaluate_data_lv.onRefreshComplete();
                    LogUtil.i("LOAD_DATA_SUCCESS");
                    updateData();
                    curPage++;
                    break;
                case LOAD_DATA_FAIL:
                    evaluate_data_lv.onRefreshComplete();
                    LogUtil.i("LOAD_DATA_FAIL");
                    break;
                case LOAD_DATA_ERROR:
                    evaluate_data_lv.onRefreshComplete();
                    LogUtil.i("LOAD_DATA_ERROR");
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
            ResultGetTaskEvaluateList ResultGetTaskEvaluateList = (ResultGetTaskEvaluateList) result;
            mEvaluateDatas.clear();
            mEvaluateDatas = ResultGetTaskEvaluateList.getData();
            mHandle.sendEmptyMessage(LOAD_DATA_SUCCESS);
        } else {
            mHandle.sendEmptyMessage(LOAD_DATA_FAIL);
        }
    }


    @Override
    public void onErrorResponse(int requestCode, ResultBase result) {
        super.onErrorResponse(requestCode, result);
        LogUtil.i("onErrorResponse--ResponseCode:" + result.getResponseCode() + "--ResponseMsg:" + result.getResponseMsg() + "--isSuccess:" + result.isSuccess + "--result:" + result.toString());
        mHandle.sendEmptyMessage(LOAD_DATA_ERROR);
    }

    @Override
    public void onFailureResponse(int requestCode, Throwable t) {
        super.onFailureResponse(requestCode, t);
        LogUtil.i("onFailureResponse--result--" + t.getMessage());
        mHandle.sendEmptyMessage(LOAD_DATA_FAIL);
    }

    /**
     * 刷新数据
     */
    private void updateData() {
        if (mDataAapter == null) {
            mDataAapter = new TaskEvaluateStatusAdapter(getActivity(), mEvaluateDatas);
            evaluate_data_lv.setAdapter(mDataAapter);
        } else {
            mDataAapter.updateAdapter(mEvaluateDatas);
        }
    }

}
