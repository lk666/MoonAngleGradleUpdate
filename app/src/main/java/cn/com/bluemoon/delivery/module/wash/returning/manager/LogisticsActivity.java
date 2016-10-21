package cn.com.bluemoon.delivery.module.wash.returning.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.app.api.model.wash.manager.ResultExpressDetail;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.lib.utils.LibViewUtil;

/**
 * Created by ljl on 2016/9/20.
 */
public class LogisticsActivity extends BaseActivity {
    @Bind(R.id.txt_status)
    TextView txtStatus;
    @Bind(R.id.txt_source)
    TextView txtSource;
    @Bind(R.id.txt_no)
    TextView txtNo;
    @Bind(R.id.listview_express)
    ListView listviewExpress;
    private String companyCode;
    private String expressCode;

    public static void actStart(Activity context, String companyCode, String expressCode) {
        Intent intent = new Intent(context, LogisticsActivity.class);
        intent.putExtra("companyCode", companyCode);
        intent.putExtra("expressCode", expressCode);
        context.startActivity(intent);
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.manage_show_logistics);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_wash_logistics;
    }

    @Override
    public void initView() {
        companyCode = getIntent().getStringExtra("companyCode");
        expressCode = getIntent().getStringExtra("expressCode");
        showWaitDialog();
        ReturningApi.seeExpress(companyCode, expressCode, getToken(), getNewHandler(1, ResultExpressDetail.class));
    }

    @Override
    public void initData() {
        txtNo.setText(expressCode);
        txtSource.setText(companyCode);
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        hideWaitDialog();
        ResultExpressDetail r = (ResultExpressDetail) result;
        txtStatus.setText(r.getExpressStatus());
        LogisticsAdapter adapter = new LogisticsAdapter(this, null);
        adapter.setList(r.getExpressInfoList());
        listviewExpress.setAdapter(adapter);
        LibViewUtil.setListViewHeight2(listviewExpress);
    }


    class LogisticsAdapter extends BaseListAdapter<ResultExpressDetail.ExpressInfoListBean> {

        public LogisticsAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.list_item_logistics;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            TextView txtDetails = getViewById(R.id.txt_details);
            TextView txtTime = getViewById(R.id.txt_time);
            View viewLine = getViewById(R.id.view_line);
            View viewLine2 = getViewById(R.id.view_line2);
            View viewLine3 = getViewById(R.id.view_line3);
            ImageView imgLogistics = getViewById(R.id.img_logistics);
            ResultExpressDetail.ExpressInfoListBean r = list.get(position);
            txtDetails.setText(r.getOpDetails());
            txtTime.setText(DateUtil.getTime(r.getOpTime(), "yyyy-MM-dd HH:mm:ss"));
            if (position == 0) {
                viewLine.setVisibility(View.INVISIBLE);
                imgLogistics.setImageResource(R.mipmap.check_logistics_on);
            } else {
                viewLine.setVisibility(View.VISIBLE);
                imgLogistics.setImageResource(R.mipmap.check_logistics_off);
            }
            if (position == list.size()-1) {
                viewLine2.setVisibility(View.INVISIBLE);
                viewLine3.setVisibility(View.GONE);
            } else {
                viewLine2.setVisibility(View.VISIBLE);
                viewLine3.setVisibility(View.VISIBLE);
            }
        }
    }
}
