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
import cn.com.bluemoon.delivery.module.wash.returning.pack.PackFragment;
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
    private static final String EXTRA_LIST = "EXTRA_LIST";
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

    private ArrayList<String> backOrderCodes = new ArrayList<>();

    public static void actionStart(Context context, String boxCode, ArrayList<String>
            backOrderCodes) {
        Intent intent = new Intent(context, CloseBoxListActivity.class);
        intent.putExtra(EXTRA_BOX_CODE, boxCode);
        intent.putExtra(EXTRA_LIST, backOrderCodes);
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
        backOrderCodes = getIntent().getStringArrayListExtra(EXTRA_LIST);
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
        ReturningApi.queryCloseBoxList(backOrderCodes, boxCode, getToken(), getNewHandler
                (REQUEST_CODE_QUERY_LIST, ResultCloseBoxList.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        hideWaitDialog();
        if (requestCode == 1) {
            toast(result.getResponseMsg());
        } else {
            ResultCloseBoxList obj = (ResultCloseBoxList) result;
            setData(obj);
        }
    }

    @Override
    public void onErrorResponse(int requestCode, ResultBase result) {
        //跳转待装箱
        if ((requestCode == 1 && result.getResponseCode() == 230033)
                || (requestCode == REQUEST_CODE_QUERY_LIST && result.getResponseCode() == 230021)) {
            toast(result.getResponseMsg());
            finish();
        } else {
            super.onErrorResponse(requestCode, result);
        }
    }

    private void setData(ResultCloseBoxList result) {
        if (result == null || result.getTagList() == null) {
            return;
        }

        int num = 0;
        List<CloseBoxTag> tagList = result.getTagList();
        if (tagList != null) {
            num = tagList.size();
        }
        tvCount.setText(String.format(getString(R.string.close_box_tag_count), num));

        list.clear();
        if (num > 0) {
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
            case R.id.btn_print_tag:
                showWaitDialog();
                List<String> tags = new ArrayList<>();
                for (CloseBoxTag tag : list) {
                    tags.add(tag.getTagCode());
                }
                ReturningApi.printTags(tags, getToken(), getNewHandler(1, ResultBase.class));
                break;
            // 扫描封箱条
            case R.id.btn_scan:
                ScanCloseBoxSignActivity.actionStart(this,REQUEST_CODE_SCAN_CODE,
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
        public ItemAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_close_box_detail;
        }

        @Override
        protected void setView(final int position, View convertView, ViewGroup parent, boolean
                isNew) {
            final CloseBoxTag item = (CloseBoxTag) getItem(position);

            TextView tvIndex = getViewById(R.id.tv_index);
            final ImageView ivCodeBar = getViewById(R.id.iv_code_bar);
            TextView tvTagCode = getViewById(R.id.tv_tag_code);
            TextView tvMainAddress = getViewById(R.id.tv_main_address);
            TextView tvDetailAddress = getViewById(R.id.tv_detail_address);
            TextView tvBackOrderNum = getViewById(R.id.tv_back_order_num);
            TextView tvBoxCode = getViewById(R.id.tv_box_code);
            View div = getViewById(R.id.div);

            tvIndex.setText(String.format("%s/%s", position + 1, getCount()));

            tvTagCode.setText(item.getTagCode());

            if (TextUtils.isEmpty(item.getReceiver()) && TextUtils.isEmpty(item.getReceiverPhone
                    ())) {
                tvMainAddress.setText(String.format("%s %s %s", item.getProvince(), item.getCity(),
                        item.getCounty()));
                tvDetailAddress.setText(String.format("%s%s%s", item.getStreet(), item.getVillage(),
                        item.getAddress()));
            } else {
                tvMainAddress.setText(item.getReceiver());
                tvDetailAddress.setText(item.getReceiverPhone());
            }


            tvBackOrderNum.setText(String.format(getString(R.string
                            .close_box_back_detail_order_num),
                    String.valueOf(item.getBackOrderNum())));
            tvBoxCode.setText(String.format(getString(R.string.close_box_tag_detail_box_code),
                    String.valueOf(item.getBoxCode())));

            if (position < getCount() - 1) {
                div.setVisibility(View.VISIBLE);
            } else {
                div.setVisibility(View.GONE);
            }

            ivCodeBar.setTag(R.id.tag_position, String.valueOf(position));

            ThreadPool.PICTURE_THREAD_POOL.execute(new ExRunable(new Feedback() {
                @Override
                public void feedback(Object obj) {
                    if (ivCodeBar != null) {
                        ImageTag imageTag = (ImageTag) obj;
                        if (imageTag != null && imageTag.tag.equals(ivCodeBar.getTag(R.id
                                .tag_position))) {
                            ivCodeBar.setImageBitmap(imageTag.bm);
                        }
                    }
                }
            }) {
                @Override
                public Object execute() {
                    String curTag = String.valueOf(position);
                    return new ImageTag(BarcodeUtil.createQRCode(item.getTagCode()), curTag);
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
