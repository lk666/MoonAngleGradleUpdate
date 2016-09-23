package cn.com.bluemoon.delivery.module.wash.returning.closebox;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.closebox.CloseBoxTag;
import cn.com.bluemoon.delivery.app.api.model.wash.closebox.ResultCloseBoxList;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;

/**
 * 封箱条列表页面
 */
public class CloseBoxListActivity extends BaseActivity implements OnListItemClickListener {

    private static final String EXTRA_BOX_CODE = "EXTRA_BOX_CODE";
    private static final int REQUEST_CODE_QUERY_LIST = 0x777;
    private static final int REQUEST_CODE_SCANE_CODE = 0x778;
    @Bind(R.id.tv_count)
    TextView tvCount;
    @Bind(R.id.btn_print_tag)
    Button btnPrintTag;
    @Bind(R.id.lv)
    ListView lv;
    @Bind(R.id.btn_scan)
    Button btnScan;

    private String boxCode;

    /**
     * 数据
     */
    private ArrayList<CloseBoxTag> list = new ArrayList<>();

    private ItemAdapter adapter;

    public static void actionStart(Context context, String boxCode) {
        Intent intent = new Intent(context, CloseBoxListActivity.class);
        intent.putExtra(EXTRA_BOX_CODE, boxCode);
        context.startActivity(intent);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();

        if (getIntent().hasExtra(EXTRA_BOX_CODE)) {
            boxCode = getIntent().getStringExtra(EXTRA_BOX_CODE);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_close_box_list;
    }

    @Override
    public void initView() {
        btnScan.setVisibility(View.GONE);
        adapter = new ItemAdapter(this, this);
        adapter.setList(list);
        lv.setAdapter(adapter);
        btnPrintTag.setEnabled(false);
        tvCount.setText(getString(R.string.close_box_tag_count));
    }

    @Override
    public void initData() {
        showWaitDialog();
        ReturningApi.queryCloseBoxList(boxCode, getToken(), getNewHandler
                (REQUEST_CODE_QUERY_LIST, ResultCloseBoxList.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        ResultCloseBoxList obj = (ResultCloseBoxList) result;
        setData(obj);
    }

    private void setData(ResultCloseBoxList result) {
        if (result == null) {
            return;
        }

        int num = 0;
        List<CloseBoxTag> tagList = result.getTagList();
        if (tagList != null) {
            num = tagList.size();
        }
        tvCount.setText(String.format(getString(R.string.close_box_tag_count), num));

        if (num < 1) {
            return;
        }

        list.clear();
        list.addAll(tagList);
        adapter.notifyDataSetChanged();

        btnPrintTag.setEnabled(true);
        btnScan.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.btn_print_tag, R.id.btn_scan})
    public void onClick(View view) {
        switch (view.getId()) {
            // todo 打印封箱条
            case R.id.btn_print_tag:
                break;
            // todo 扫描封箱条
            case R.id.btn_scan:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // todo 扫码返回
        if (requestCode == REQUEST_CODE_SCANE_CODE) {

        }
    }

    // TODO: lk 2016/9/22 UI未定 
    class ItemAdapter extends BaseListAdapter<CloseBoxTag> {
        public ItemAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_close_box_back_order;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            CloseBoxTag item = (CloseBoxTag) getItem(position);
//
//            TextView tvBackCode = getViewById(R.id.tv_back_code);
//            ImageView ivScan = getViewById(R.id.iv_scan);
//
//            tvBackCode.setText(item.code);
//            if (item.state == 1) {
//                ivScan.setVisibility(View.VISIBLE);
//            } else {
//                ivScan.setVisibility(View.GONE);
//            }
        }
    }

    @Override
    public void onItemClick(Object item, View view, int position) {

    }
}
