package cn.com.bluemoon.delivery.module.wash.returning.closebox;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.closebox.CloseBoxTag;
import cn.com.bluemoon.delivery.app.api.model.wash.closebox.ResultCloseBoxList;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.lib.qrcode.utils.BarcodeUtil;
import cn.com.bluemoon.lib.utils.threadhelper.ExRunable;
import cn.com.bluemoon.lib.utils.threadhelper.Feedback;
import cn.com.bluemoon.lib.utils.threadhelper.ThreadPool;

/**
 * 封箱详情
 */
public class CloseBoxDetailActivity extends BaseActivity implements OnListItemClickListener {

    private final static String EXTRA_BOX_CODE = "EXTRA_BOX_CODE";
    private final static int REQUEST_CODE_QUERY = 0x777;
    @Bind(R.id.tv_count)
    TextView tvCount;
    @Bind(R.id.lv)
    ListView lv;

    private ItemAdapter adapter;
    private List<CloseBoxTag> list;

    private String boxCode;

    public static void actionStart(Context context, String tagCode) {
        Intent intent = new Intent(context, CloseBoxDetailActivity.class);
        intent.putExtra(EXTRA_BOX_CODE, tagCode);
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
        return R.layout.activity_close_box_detail;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.close_box_detail_title);
    }

    @Override
    protected void setActionBar(CommonActionBar actionBar) {
        super.setActionBar(actionBar);

        actionBar.getImgRightView().setImageResource(R.mipmap.ic_print);
        actionBar.getImgRightView().setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActionBarBtnRightClick() {
        // TODO: lk 2016/9/20 点击打印
        toast("打印" + boxCode);
    }

    @Override
    public void initView() {
        list = new ArrayList<>();
        adapter = new ItemAdapter(this, this);
        lv.setAdapter(adapter);
    }

    @Override
    public void initData() {
        showWaitDialog();
        ReturningApi.queryCloseBoxList(boxCode, getToken(), getNewHandler
                (REQUEST_CODE_QUERY, ResultCloseBoxList.class));
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
        tvCount.setText(String.format(getString(R.string.close_box_tag_count),
                result.getTagList().size()));
        list.clear();
        list.addAll(result.getTagList());
        adapter.notifyDataSetChanged();
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
