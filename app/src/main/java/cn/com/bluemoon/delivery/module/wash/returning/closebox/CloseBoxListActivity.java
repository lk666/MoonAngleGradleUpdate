package cn.com.bluemoon.delivery.module.wash.returning.closebox;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
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
import cn.com.bluemoon.delivery.app.api.model.wash.closebox.CloseBoxTag;
import cn.com.bluemoon.delivery.app.api.model.wash.closebox.ResultCloseBoxList;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.lib.qrcode.utils.BarcodeUtil;
import cn.com.bluemoon.lib.utils.threadhelper.ExRunable;
import cn.com.bluemoon.lib.utils.threadhelper.Feedback;
import cn.com.bluemoon.lib.utils.threadhelper.ThreadPool;

/**
 * 打印封箱条页面
 */
public class CloseBoxListActivity extends BaseActivity implements OnListItemClickListener {

    private static final String EXTRA_BOX_CODE = "EXTRA_BOX_CODE";
    private static final int REQUEST_CODE_QUERY_LIST = 0x777;
    private static final int REQUEST_CODE_SCAN_CODE = 0x778;
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

        if (getIntent() != null) {
            boxCode = getIntent().getStringExtra(EXTRA_BOX_CODE);
        }
        if (TextUtils.isEmpty(boxCode)) {
            boxCode = "";
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

    @Override
    public void onFailureResponse(int requestCode, Throwable t) {
        super.onFailureResponse(requestCode, t);
        setData(null);
    }

    @Override
    public void onSuccessException(int requestCode, Throwable t) {
        super.onSuccessException(requestCode, t);
        setData(null);
    }

    @Override
    public void onErrorResponse(int requestCode, ResultBase result) {
        super.onErrorResponse(requestCode, result);
        setData(null);
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

        list.clear();
        if (tagList != null) {
            list.addAll(tagList);
            btnPrintTag.setEnabled(true);
            btnScan.setVisibility(View.VISIBLE);
        } else {
            btnPrintTag.setEnabled(false);
            btnScan.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
    }

    @OnClick({R.id.btn_print_tag, R.id.btn_scan})
    public void onClick(View view) {
        switch (view.getId()) {
            // TODO: lk 2016/9/28 打印封箱条
            case R.id.btn_print_tag:
                break;
            // 扫描封箱条
            case R.id.btn_scan:
                ScanCloseBoxSignActivity.actionStart(this, null, REQUEST_CODE_SCAN_CODE,
                        boxCode, list);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //  扫码完成
        if (requestCode == REQUEST_CODE_SCAN_CODE && resultCode == RESULT_OK) {
            finish();
        }
    }

    class ItemAdapter extends BaseListAdapter<CloseBoxTag> {

        private final int IV_TAG = 0x123;

        public ItemAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_close_box_detail;
        }

        @Override
        protected void setView(final int position, View convertView, ViewGroup parent, boolean isNew) {
            CloseBoxTag item = (CloseBoxTag) getItem(position);

            TextView tvIndex = getViewById(R.id.tv_index);
            final ImageView ivCodeBar = getViewById(R.id.iv_code_bar);
            TextView tvTagCode = getViewById(R.id.tv_tag_code);
            TextView tvMainAddress = getViewById(R.id.tv_main_address);
            TextView tvDetailAddress = getViewById(R.id.tv_detail_address);
            TextView tvBackOrderNum = getViewById(R.id.tv_back_order_num);
            TextView tvBoxCode = getViewById(R.id.tv_box_code);
            View div = getViewById(R.id.div);

            tvIndex.setText(String.format("%s/%s", position, getCount()));

            tvTagCode.setText(item.getTagCode());
            tvMainAddress.setText(String.format("%s %s /%s", item.getProvince(), item.getCity(), item.getCounty()));
            tvDetailAddress.setText(item.getAddress());

            tvBackOrderNum.setText(String.format(getString(R.string.close_box_back_detail_order_num),
                    String.valueOf(item.getBackOrderNum())));
            tvBoxCode.setText(String.format(getString(R.string.close_box_tag_detail_box_code),
                    String.valueOf(item.getBoxCode())));

            if (position < getCount() - 1) {
                div.setVisibility(View.VISIBLE);
            } else {
                div.setVisibility(View.GONE);
            }

            ivCodeBar.setTag(IV_TAG, String.valueOf(position));

            ThreadPool.PICTURE_THREAD_POOL.execute(new ExRunable(new Feedback() {
                @Override
                public void feedback(Object obj) {
                    if (ivCodeBar != null) {
                        ImageTag imageTag = (ImageTag) obj;
                        if (imageTag != null && imageTag.tag.equals(ivCodeBar.getTag(IV_TAG))) {
                            ivCodeBar.setImageBitmap((Bitmap) obj);
                        }
                    }
                }
            }) {
                @Override
                public Object execute() {
                    String curTag = String.valueOf(position);
                    return new ImageTag(BarcodeUtil.createQRCode(boxCode), curTag);
                }
            });
        }
    }

    class ImageTag {
        Bitmap bm;
        String tag;

        public ImageTag(Bitmap bm, String tag) {
            this.bm = bm;
            this.tag = tag;
        }
    }

    @Override
    public void onItemClick(Object item, View view, int position) {

    }
}
