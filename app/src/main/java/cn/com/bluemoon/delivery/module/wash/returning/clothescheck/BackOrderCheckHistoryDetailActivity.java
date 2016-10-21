package cn.com.bluemoon.delivery.module.wash.returning.clothescheck;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.clothescheck.BackOrder;
import cn.com.bluemoon.delivery.app.api.model.wash.clothescheck.ResultCloseBoxHistoryDetail;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.ui.NoScrollListView;
import cn.com.bluemoon.delivery.utils.DateUtil;

/**
 * 还衣单清点详情
 */
public class BackOrderCheckHistoryDetailActivity extends BaseActivity implements
        OnListItemClickListener {
    private static final String EXTRA_CHECK_LOG_ID = "EXTRA_CHECK_LOG_ID";
    private static final int REQUEST_CODE = 0x777;
    @Bind(R.id.tv_tag_code)
    TextView tvTagCode;
    @Bind(R.id.tv_count)
    TextView tvCount;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.lv)
    NoScrollListView lv;
    @Bind(R.id.sc_main)
    ScrollView scMain;


    private String checkLogId;
    private ItemAdapter adapter;

    public static void actionStart(Activity activity, Fragment fragment, String checkLogId) {
        Intent intent = new Intent(activity, BackOrderCheckHistoryDetailActivity.class);
        intent.putExtra(EXTRA_CHECK_LOG_ID, checkLogId);
        if (fragment != null) {
            fragment.startActivity(intent);
        } else {
            activity.startActivity(intent);
        }
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();

        if (getIntent().hasExtra(EXTRA_CHECK_LOG_ID)) {
            checkLogId = getIntent().getStringExtra(EXTRA_CHECK_LOG_ID);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_back_order_history_detail;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.back_order_check_detail);
    }

    @Override
    public void initView() {
        adapter = new ItemAdapter(this, this);
        lv.setAdapter(adapter);
    }

    @Override
    public void initData() {
        showWaitDialog();
        ReturningApi.querycloseBoxHistoryDetail(checkLogId, getToken(),
                getNewHandler(REQUEST_CODE, ResultCloseBoxHistoryDetail.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        ResultCloseBoxHistoryDetail obj = (ResultCloseBoxHistoryDetail) result;
        setData(obj);
    }

    private void setData(ResultCloseBoxHistoryDetail result) {
        if (result == null) {
            return;
        }

        tvTagCode.setText(String.format(getString(R.string.driver_tag_code), result.getTagCode()));
        tvCount.setText(String.format(getString(R.string.clothes_check_history_back_order_num),
                String.valueOf(result.getBackOrderNum())));
        tvTime.setText(DateUtil.getTime(result.getOpTime(), "yyyy-MM-dd HH:mm"));

        adapter.setList(result.getBackOrderList());
        adapter.notifyDataSetChanged();
        scMain.post(new Runnable() {
            @Override
            public void run() {
                scMain.scrollTo(0, 0);
            }
        });
    }

    class ItemAdapter extends BaseListAdapter<BackOrder> {
        public ItemAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_back_order;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            BackOrder item = (BackOrder) getItem(position);

            TextView tvCode = getViewById(R.id.tv_code);
            ImageView iv = getViewById(R.id.iv);

            tvCode.setText(item.getBackOrderCode());
            if (item.isAbnormal()) {
                iv.setImageResource(R.mipmap.ic_abnormal);
            } else {
                iv.setImageResource(R.mipmap.ic_normal);
            }
//            setClickEvent(isNew, position, convertView);
        }
    }

    @Override
    public void onItemClick(Object item, View view, int position) {
        /*if (item instanceof BackOrder) {
            ClothesCheckHistoryDetailActivity.actionStart(this, null, checkLogId, ((BackOrder)
                    item).getBackOrderCode());
        }*/
    }
}