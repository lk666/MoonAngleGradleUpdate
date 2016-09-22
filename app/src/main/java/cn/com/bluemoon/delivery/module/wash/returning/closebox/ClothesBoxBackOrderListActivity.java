package cn.com.bluemoon.delivery.module.wash.returning.closebox;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.closebox.ResultClothesBoxBackOrderList;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;

/**
 * 清点还衣单
 */
public class ClothesBoxBackOrderListActivity extends BaseActivity implements
        OnListItemClickListener {

    private static final String EXTRA_BOX_CODE = "EXTRA_BOX_CODE";
    private static final int REQUEST_CODE_QUERY_LIST = 0x777;
    @Bind(R.id.tv_code_box)
    TextView tvCodeBox;
    @Bind(R.id.tv_back_num)
    TextView tvBackNum;
    @Bind(R.id.lv_back_order)
    ListView lvBackOrder;
    @Bind(R.id.btn_print)
    Button btnPrint;
    private String boxCode;

    /**
     * 数据
     */
    private List<BackOrderItem> list = new ArrayList<>();

    private BackOrderAdapter adapter;

    public static void actionStart(Context context, String boxCode) {
        Intent intent = new Intent(context, ClothesBoxBackOrderListActivity.class);
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
        return R.layout.activity_clothes_box_back_order_list;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.close_box_back_order_title);
    }

    @Override
    protected void setActionBar(CommonActionBar titleBar) {
        titleBar.getImgLeftView().setVisibility(View.GONE);

        titleBar.getImgRightView().setImageResource(R.mipmap.ewmtxm);
        titleBar.getImgRightView().setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActionBarBtnRightClick() {
        // TODO: lk 2016/9/20 扫码
        toast("扫码" + boxCode);
    }

    @Override
    public void initView() {
        btnPrint.setVisibility(View.GONE);
        adapter = new BackOrderAdapter(this, this);
        adapter.setList(list);
        lvBackOrder.setAdapter(adapter);
    }

    @Override
    public void initData() {
        showWaitDialog();
        ReturningApi.queryClothesBoxBackOrderList(boxCode, getToken(), getNewHandler
                (REQUEST_CODE_QUERY_LIST, ResultClothesBoxBackOrderList.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        ResultClothesBoxBackOrderList obj = (ResultClothesBoxBackOrderList) result;
        setData(obj);
    }

    private void setData(ResultClothesBoxBackOrderList result) {
        if (result == null) {
            return;
        }

        tvCodeBox.setText(String.format(getString(R.string.close_box_back_order_box_code),
                boxCode));

        int num = 0;
        List<String> steList = result.getBackOrderList();
        if (steList != null) {
            num = steList.size();
        }
        tvBackNum.setText(String.format(getString(R.string.close_box_back_detail_order_num), num));
        btnPrint.setVisibility(View.GONE);

        list.clear();
        for (int i = 0; i < num; i++) {
            BackOrderItem item = new BackOrderItem(steList.get(i));
            list.add(item);
        }
        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.btn_print)
    public void onClick() {
        // TODO: lk 2016/9/21 打印
        toast("打印");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }

        switch (requestCode) {
            case Constants.TAKE_PIC_RESULT:
// TODO: lk 2016/9/21 扫码返回 

        }
    }

    class BackOrderAdapter extends BaseListAdapter<BackOrderItem> {
        public BackOrderAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_close_box_back_order;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            BackOrderItem item = (BackOrderItem) getItem(position);

            TextView tvBackCode = getViewById(R.id.tv_back_code);
            ImageView ivScan = getViewById(R.id.iv_scan);

            tvBackCode.setText(item.code);
            if (item.state == 1) {
                ivScan.setVisibility(View.VISIBLE);
            } else {
                ivScan.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onItemClick(Object item, View view, int position) {

    }

}