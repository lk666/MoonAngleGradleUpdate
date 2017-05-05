package cn.com.bluemoon.delivery.module.wash.enterprise;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.card.ResultWorkPlaceList;
import cn.com.bluemoon.delivery.app.api.model.card.Workplace;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.card.CardUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.view.CommonEmptyView;
import cn.com.bluemoon.lib.view.CommonSearchView;

public class EmployOrderQueryActivity extends BaseActivity implements OnListItemClickListener {

    public static void startAct(Context context) {
        Intent intent = new Intent(context, EmployOrderQueryActivity.class);
        context.startActivity(intent);
    }

    @Bind(R.id.search_view)
    CommonSearchView searchView;
    @Bind(R.id.listView_history)
    PullToRefreshListView listViewHistory;
    @Bind(R.id.listview_workplace)
    PullToRefreshListView listviewWorkplace;
    private GetWordPlaceAdapter getWordPlaceAdapter;
    private GetHistoryAdapter getHistoryAdapter;
    private long timestamp = 0;
    private boolean isPullUp;
    private boolean isPullDown;
    private List<Workplace> items;
    private List<Workplace> listHistory;
    private final static int HISTORY_SIZE = 5;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_query_employ_order;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.hand_query);
    }

    @Override
    public void initView() {
        searchView.setSearchViewListener(new CommonSearchView.SearchViewListener() {
            @Override
            public void onSearch(CommonSearchView view, String str) {
                getList();
            }

            @Override
            public void onCancel(CommonSearchView view) {

            }
        });
        PublicUtil.setEmptyView(listViewHistory, getString(R.string.card_search_history),null);
        PublicUtil.setEmptyView(listviewWorkplace, null, new CommonEmptyView.EmptyListener() {
            @Override
            public void onRefresh() {
                getList();
            }
        });
        listviewWorkplace.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                isPullDown = true;
                isPullUp = false;
                getList();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                isPullDown = false;
                isPullUp = true;
                getList();
            }
        });
    }

    @Override
    public void initData() {
        showHistory();
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        ResultWorkPlaceList resultWorkPlaceList = (ResultWorkPlaceList) result;
        listviewWorkplace.onRefreshComplete();
        if (resultWorkPlaceList.getTimestamp() != 0) {
            timestamp = resultWorkPlaceList.getTimestamp();
        }
        ViewUtil.setViewVisibility(listViewHistory, View.GONE);
        ViewUtil.setViewVisibility(listviewWorkplace, View.VISIBLE);
        setData(resultWorkPlaceList.getWorkplaceList());

    }

    @Override
    public void onErrorResponse(int requestCode, ResultBase result) {
        super.onErrorResponse(requestCode, result);
        listviewWorkplace.onRefreshComplete();
    }

    @Override
    public void onSuccessException(int requestCode, Throwable t) {
        super.onSuccessException(requestCode, t);
        listviewWorkplace.onRefreshComplete();
    }

    @Override
    public void onFailureResponse(int requestCode, Throwable t) {
        super.onFailureResponse(requestCode, t);
        listviewWorkplace.onRefreshComplete();
    }

    private void showHistory() {
        if (getHistoryAdapter == null) {
            getHistoryAdapter = new GetHistoryAdapter(this, null);
        }
        if (listHistory == null) {
            listHistory = ClientStateManager.getCardSearchHistory();
        }
        getHistoryAdapter.setList(listHistory);
        listViewHistory.setAdapter(getHistoryAdapter);
    }

    private void getList() {
        if (!isPullUp) {
            timestamp = 0;
        }
        if (!isPullUp && !isPullDown) {
            showWaitDialog();
        }
        DeliveryApi.getWorkplaceList(getToken(), searchView.getText(),
                AppContext.PAGE_SIZE * 2, timestamp, getNewHandler(0, ResultWorkPlaceList.class));
    }

    private void setData(List<Workplace> workplaces) {
        if (items == null) {
            items = new ArrayList<>();
        }
        if (workplaces == null || workplaces.size() == 0) {
            if (isPullUp) {
                PublicUtil.showToast(R.string.card_no_list_data);
                return;
            } else {
                items.clear();
            }
        } else {
            List<Workplace> list = new ArrayList<>();
            for (int i = 0; i < workplaces.size(); i++) {
                list.add(workplaces.get(i));
            }
            if (isPullUp) {
                items.addAll(list);
            } else {
                items = list;
            }
        }
        if (getWordPlaceAdapter == null) {
            getWordPlaceAdapter = new GetWordPlaceAdapter(this, this);
        }
        getWordPlaceAdapter.setList(items);
        if (isPullUp) {
            getWordPlaceAdapter.notifyDataSetChanged();
        } else {
            listviewWorkplace.setAdapter(getWordPlaceAdapter);
        }
    }

    private void refreshHistoryItem(Workplace workplace, boolean isAdd) {
        if (workplace == null || TextUtils.isEmpty(workplace.getWorkplaceCode())) {
            return;
        }
        if (listHistory == null) {
            listHistory = ClientStateManager.getCardSearchHistory();
        }
        if (isAdd) {
            for (int i = 0; i < listHistory.size(); i++) {
                if (i >= HISTORY_SIZE - 1) {
                    listHistory.remove(i);
                    i--;
                } else {
                    if (listHistory.get(i).getWorkplaceCode().equals(workplace.getWorkplaceCode())) {
                        listHistory.remove(i);
                        i--;
                    }
                }
            }
            listHistory.add(0, workplace);
        } else {
            listHistory.remove(workplace);
        }
        ClientStateManager.setCardSearhHistory(listHistory);
    }

    private void returnOK(Workplace workplace) {
        Intent intent = new Intent();
        intent.putExtra("code", workplace.getWorkplaceCode());
        setResult(RESULT_OK, intent);
        finish();
    }

    class GetHistoryAdapter extends BaseListAdapter<Workplace> {


        public GetHistoryAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_workplace_history_list;
        }

        @Override
        protected void setView(final int position, final View convertView, ViewGroup parent, boolean isNew) {
            final Workplace workplace = list.get(position);
            TextView txtCode = getViewById(R.id.txt_code);
            ImageView imgDelete = getViewById(R.id.img_delete);
            TextView txtName = getViewById(R.id.txt_name);
            txtCode.setText(workplace.getWorkplaceCode());
            txtName.setText(workplace.getWorkplaceName());
            if (isNew) {
                imgDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        refreshHistoryItem(list.get(position), false);
                        ViewUtil.hideIM(v);
                        int x = AppContext.getInstance().getDisplayWidth();
                        ObjectAnimator animator = ObjectAnimator.ofFloat(convertView, "translationX", 0.0f,-x);
                        animator.start();
                        animator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                ObjectAnimator animator = ObjectAnimator.ofFloat(convertView, "translationX", 0.0f);
                                animator.setDuration(10);
                                animator.start();
                                notifyDataSetChanged();
                            }
                        });
                    }
                });
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        returnOK(workplace);
                    }
                });
            }

        }
    }

    class GetWordPlaceAdapter extends BaseListAdapter<Workplace> {


        public GetWordPlaceAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_workplace_list;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            final Workplace workplace = list.get(position);
            TextView txtCode = getViewById(R.id.txt_code);
            TextView txtName = getViewById(R.id.txt_name);
            TextView txtAddress = getViewById(R.id.txt_address);
            txtCode.setText(workplace.getWorkplaceCode());
            txtName.setText(workplace.getWorkplaceName());
            txtAddress.setText(CardUtils.getWorkPlaceAddress(workplace));
            setClickEvent(isNew, position, convertView);
        }
    }

    @Override
    public void onItemClick(Object item, View view, int position) {
        Workplace workplace = (Workplace) item;
        refreshHistoryItem(workplace, true);
        returnOK(workplace);
    }
}
