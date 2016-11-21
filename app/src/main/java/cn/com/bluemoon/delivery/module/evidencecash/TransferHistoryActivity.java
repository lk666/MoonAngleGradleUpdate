package cn.com.bluemoon.delivery.module.evidencecash;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.EvidenceCashApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.evidencecash.CashListBean;
import cn.com.bluemoon.delivery.app.api.model.evidencecash.CashListDataset;
import cn.com.bluemoon.delivery.app.api.model.evidencecash.ResultCashList;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.ui.PinnedSectionListView;
import cn.com.bluemoon.delivery.utils.DateUtil;

/**
 * Created by ljl on 2016/11/18.
 */
public class TransferHistoryActivity extends BaseActivity {
    @Bind(R.id.listview)
    PinnedSectionListView listview;
    private List<CashListDataset> dataSet;
    private List<String> dates;

    @Override
    protected String getTitleString() {
        return getString(R.string.transfer_history_title);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_transfer_history;
    }

    @Override
    public void initView() {
        dataSet = new ArrayList<>();
        dates = new ArrayList<>();
        ;
        showWaitDialog();
        EvidenceCashApi.cashList(0, getToken(), getNewHandler(1, ResultCashList.class));
    }

    @Override
    public void initData() {

    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        hideWaitDialog();
        if (requestCode == 1) {
            ResultCashList resultCashList = (ResultCashList) result;
            List<CashListBean> cashList = resultCashList.getCashList();
            generateDataset(cashList);
            listview = (PinnedSectionListView) findViewById(R.id.listview);
            RechargeListAdapter adapter = new RechargeListAdapter(this);
            adapter.setList(dataSet);
            listview.setAdapter(adapter);
        }
    }

    public void generateDataset(List<CashListBean> cashList) {
        for (CashListBean bean : cashList) {
            String date = DateUtil.getTime(bean.getCashTime(), "yyyy年MM月dd日");
            if (dates.isEmpty() || !dates.get(dates.size() - 1).equals(date)) {
                CashListDataset item = new CashListDataset(CashListDataset.SECTION, bean, date);
                dates.add(date);
                if (!dataSet.isEmpty()) {
                    dataSet.get(dataSet.size() -1).setLast(true);
                }
                dataSet.add(item);
            }
            CashListDataset item = new CashListDataset(CashListDataset.ITEM, bean, date);
            dataSet.add(item);
        }
    }

    class RechargeListAdapter extends BaseListAdapter<CashListDataset> implements PinnedSectionListView.PinnedSectionListAdapter {

        public RechargeListAdapter(Context context) {
            super(context, null);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.list_item_transfer_history;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            TextView txtDate = getViewById(R.id.txt_date);
            View line1 = getViewById(R.id.line_1);
            View line2 = getViewById(R.id.line_2);
            LinearLayout layoutDate = getViewById(R.id.layout_date);
            TextView txtDay = getViewById(R.id.txt_day);
            TextView txtTime = getViewById(R.id.txt_time);
            TextView txtTansferMoney = getViewById(R.id.txt_tansfer_money);
            TextView txtDisplay = getViewById(R.id.txt_display);
            TextView txtStatus = getViewById(R.id.txt_status);
            LinearLayout layoutDetail = getViewById(R.id.layout_detail);

            CashListDataset item = list.get(position);
            if (item.type == CashListDataset.SECTION) {
                layoutDate.setVisibility(View.VISIBLE);
                txtDate.setText(item.date);
            } else {
                final CashListBean bean = item.bean;
                layoutDetail.setVisibility(View.VISIBLE);
                long dateLong = bean.getCashTime();
                txtDay.setText(DateUtil.getTime(dateLong, "MM月dd日"));
                txtTime.setText(DateUtil.getTime(dateLong, "HH:mm"));
                txtTansferMoney.setText(bean.getSymbol()+bean.getTradeMoney());
                txtDisplay.setText(bean.getTradePayDisplay()+" - "+bean.getCashTypeDisplay());
                txtStatus.setText(bean.getTradeStatusDisplay());
                if ("wait".equals(bean.getTradeStatusCode())) {
                    txtStatus.setTextColor(getResources().getColor(R.color.text_orange));
                } else if ("failure".equals(bean.getTradeStatusCode())) {
                    txtStatus.setTextColor(getResources().getColor(R.color.btn_red));
                } else {
                    txtStatus.setTextColor(getResources().getColor(R.color.text_black_light));
                }
                if (item.isLast || position == list.size()-1) {
                    line1.setVisibility(View.VISIBLE);
                    line2.setVisibility(View.GONE);
                } else {
                    line2.setVisibility(View.VISIBLE);
                    line1.setVisibility(View.GONE);
                }
                layoutDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(TransferHistoryActivity.this, TransferDetailActivity.class);
                        intent.putExtra("manageId", bean.getManageId());
                        startActivity(intent);
                    }
                });
            }
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return list.get(position).type;
        }

        @Override
        public boolean isItemViewTypePinned(int viewType) {
            return viewType == CashListDataset.SECTION;
        }

    }
}
