package cn.com.bluemoon.delivery.module.clothing.collect.withorder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.inventory.ResultOrderVo;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.module.base.BaseFragment;
import cn.com.bluemoon.delivery.order.TimerFilterWindow;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.utils.LibDateUtil;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

public class CollectClothesRecordFragment extends BaseFragment {


    private CollectClothesAdapter adapter;
    private FragmentActivity main;
    private CommonProgressDialog progressDialog;
    private PullToRefreshListView listView;
    View popStart;

    private ResultOrderVo item;
    private long startTime = 0;
    private long endTime = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        initCustomActionBar();
        main = getActivity();

        View v = inflater.inflate(R.layout.fragment_tab_clothes, container,
                false);
        popStart = (View) v.findViewById(R.id.view_pop_start);

        listView = (PullToRefreshListView) v
                .findViewById(R.id.listview_main);
        listView.setMode(PullToRefreshBase.Mode.DISABLED);
        progressDialog = new CommonProgressDialog(main);
        adapter = new CollectClothesAdapter(main);
        getItem();
        return v;
    }

    private void getItem() {
        String token = ClientStateManager.getLoginToken(main);
        if (progressDialog != null) {
            progressDialog.show();
        }

        setData();

    }

    private void setData() {

        List<String> list = new ArrayList<>();
        for(int i=0;i<10;i++){
            list.add("");
        }
        adapter.setList(list);
        listView.setAdapter(adapter);


    }


    private void initCustomActionBar() {

        CommonActionBar actionBar = new CommonActionBar(getActivity().getActionBar(),
                new IActionBarListener() {

                    @Override
                    public void onBtnRight(View v) {
                        // TODO Auto-generated method stub

                        TimerFilterWindow popupWindow = new TimerFilterWindow(getActivity(), new TimerFilterWindow.TimerFilterListener() {
                            @Override
                            public void callBack(long startDate, long endDate) {
                                if (startDate >= 0 && endDate >= startDate) {
                                    startTime = LibDateUtil.getTimeByCustTime(startDate) / 1000;
                                    endTime = LibDateUtil.getTimeByCustTime(endDate) / 1000;

                                    Date start = new Date(startTime * 1000);
                                    Date end = new Date(endTime * 1000);

                                    if (endDate >= startDate
                                            && (((end.getDate() >= start.getDate()) && ((end.getYear() * 12 + end.getMonth()) - (start.getYear() * 12 + start.getMonth()) <= 5))
                                            || ((end.getDate() < start.getDate()) && ((end.getYear() * 12 + end.getMonth()) - (start.getYear() * 12 + start.getMonth()) <= 6)))) {
                                           getItem();
                                    } else {
                                        PublicUtil.showMessage(main, getString(R.string.txt_order_fillter_date_error));
                                    }

                                }
                            }
                        });
                        popupWindow.showPopwindow(popStart);

                    }

                    @Override
                    public void onBtnLeft(View v) {
                        // TODO Auto-generated method stub
                        getActivity().finish();
                    }

                    @Override
                    public void setTitle(TextView v) {
                        // TODO Auto-generated method stub
                        v.setText("收衣记录");

                    }
                });

        actionBar.getTvRightView().setText(R.string.btn_txt_fillter);
        actionBar.getTvRightView().setCompoundDrawablePadding(10);

        Drawable drawableFillter = getResources().getDrawable(R.mipmap.icon_filter);
        drawableFillter.setBounds(0, 0, drawableFillter.getMinimumWidth(), drawableFillter.getMinimumHeight());
        actionBar.getTvRightView().setCompoundDrawables(drawableFillter, null, null, null);
        actionBar.getTvRightView().setVisibility(View.VISIBLE);

    }

    @SuppressLint("InflateParams")
    class CollectClothesAdapter extends BaseAdapter {

        private Context context;
        private List<String> lists;

        public CollectClothesAdapter(Context context) {
            this.context = context;
        }

        public void setList(List<String> list) {
            this.lists = list;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return lists.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return lists.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            // TODO Auto-generated method stub
            LayoutInflater inflate = LayoutInflater.from(context);
            if (lists.size() == 0) {
                View viewEmpty = inflate.inflate(R.layout.layout_no_data, null);
                AbsListView.LayoutParams params = new AbsListView.LayoutParams(
                        AbsListView.LayoutParams.MATCH_PARENT, listView.getHeight());
                viewEmpty.setLayoutParams(params);
                return viewEmpty;
            }

            convertView = inflate.inflate(R.layout.clothes_collect_item, null);


            convertView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                }
            });
            return convertView;
        }

    }


    public void onPause() {
        super.onPause();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}
