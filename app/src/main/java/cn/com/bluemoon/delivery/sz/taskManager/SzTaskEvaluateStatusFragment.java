package cn.com.bluemoon.delivery.sz.taskManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseFragment;
import cn.com.bluemoon.delivery.sz.adapter.TaskEvaluateStatusAdapter;
import cn.com.bluemoon.delivery.sz.api.SzApi;
import cn.com.bluemoon.delivery.sz.bean.taskManager.AsignJobBean;
import cn.com.bluemoon.delivery.sz.bean.taskManager.DailyPerformanceInfoBean;
import cn.com.bluemoon.delivery.sz.bean.taskManager.EventDailyPerformanceBean;
import cn.com.bluemoon.delivery.sz.bean.taskManager.ResultGetTaskEvaluateList;
import cn.com.bluemoon.delivery.sz.util.DateUtil;
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
        EventBus.getDefault().register(this);
        mDataAapter = new TaskEvaluateStatusAdapter(getActivity(), mEvaluateDatas, activityType);
        evaluate_data_lv.setMode(PullToRefreshBase.Mode.BOTH);
        evaluate_data_lv.getLoadingLayoutProxy().setRefreshingLabel(getString(R.string.sz_listview_loding_label));
        evaluate_data_lv.setAdapter(mDataAapter);
        initListener();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvaluateStateChanged(EventDailyPerformanceBean bean) {
        LogUtil.i("被修改的评价bean:" + bean.toString());
        if (bean != null && bean.getDailyPerformanceInfoBean() != null) {
//            if (bean.getType() == ACTIVITY_TYPE_TO_EVALUATE) {
//                //如果是未评价的被评价了，则从未评价区移至已评价区
//                if (activityType == ACTIVITY_TYPE_TO_EVALUATE) {
//                    LogUtil.i("移除前:" + mEvaluateDatas.size());
//                    mEvaluateDatas.remove(bean.getDailyPerformanceInfoBean());
//                    LogUtil.i("移除后:" + mEvaluateDatas.size());
//                } else if (activityType == ACTIVITY_TYPE_HAVE_EVALUATED) {
//                    LogUtil.i("添加前:" + mEvaluateDatas.size());
//                    mEvaluateDatas.add(bean.getDailyPerformanceInfoBean());
//                    LogUtil.i("添加后:" + mEvaluateDatas.size());
//                }
//            } else if (bean.getType() == ACTIVITY_TYPE_HAVE_EVALUATED) {
//                //如果是已评价的被修改了评价，就将此数据在已评价区更新
//                int index = mEvaluateDatas.indexOf(bean.getDailyPerformanceInfoBean());
//                mEvaluateDatas.get(index).setAsignJobs(bean.getDailyPerformanceInfoBean().getAsignJobs());
//            }
//            updateData();
            isRefresh = true;
            LogUtil.e("需要刷新数据");
        }
    }

    private boolean isRefresh = true;

    @Override
    public void initData() {
//        loadData2();
    }

    @Override
    public void onResume() {
        super.onResume();
        //每次进页面都刷新最新数据来显示
        if (isRefresh) {
            isRefresh = false;
            curPage = 1;
            mEvaluateDatas.clear();
            loadData();
        }
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
                LogUtil.i("onItemClick---position:" + position);
                Intent intent = null;
                DailyPerformanceInfoBean itemInfo = mDataAapter.getItem(position - 1);
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
        if (result != null && result.isSuccess) {
            ResultGetTaskEvaluateList ResultGetTaskEvaluateList = (ResultGetTaskEvaluateList) result;
            //将数据进行简单的是时间格式处理
            if (ResultGetTaskEvaluateList.getData() != null) {
                List<DailyPerformanceInfoBean> data = ResultGetTaskEvaluateList.getData();
                for (DailyPerformanceInfoBean dailyPerformanceInfoBean : data) {
                    dailyPerformanceInfoBean.setCreatetime(
                            DateUtil.tranTimeToDate(dailyPerformanceInfoBean.getCreatetime(), "HH:mm"));
                    dailyPerformanceInfoBean.setUpdatetime(
                            DateUtil.tranTimeToDate(dailyPerformanceInfoBean.getUpdatetime(), "yyyy-MM-dd"));
                    dailyPerformanceInfoBean.setWork_date(
                            DateUtil.tranTimeToDate(dailyPerformanceInfoBean.getWork_date(), "MM-dd"));
                    for (AsignJobBean asignJobBean : dailyPerformanceInfoBean.getAsignJobs()) {
                        asignJobBean.setCreatetime(
                                DateUtil.tranTimeToDate(asignJobBean.getCreatetime(), "HH:mm"));
                        asignJobBean.setBegin_time(DateUtil.tranTimeToDate(asignJobBean.getBegin_time()));
                        asignJobBean.setEnd_time(DateUtil.tranTimeToDate(asignJobBean.getEnd_time()));
                    }
                    mEvaluateDatas.add(dailyPerformanceInfoBean);
                    LogUtil.i("mEvaluateDatas:" + mEvaluateDatas.toString());
                }
            }
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
            mDataAapter = new TaskEvaluateStatusAdapter(getActivity(), mEvaluateDatas, activityType);
            evaluate_data_lv.setAdapter(mDataAapter);
        } else {
            mDataAapter.updateAdapter(mEvaluateDatas);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
