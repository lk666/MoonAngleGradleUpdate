package cn.com.bluemoon.delivery.module.wash.returning.closebox;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.closebox.ResultCloseBoxDetail;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.lib.qrcode.utils.BarcodeUtil;
import cn.com.bluemoon.lib.utils.threadhelper.ExRunable;
import cn.com.bluemoon.lib.utils.threadhelper.Feedback;
import cn.com.bluemoon.lib.utils.threadhelper.ThreadPool;

/**
 * 封箱详情
 */
public class CloseBoxDetailActivity extends BaseActivity {

    private final static String EXTRA_TAG_ID = "EXTRA_TAG_ID";
    private final static int REQUEST_CODE_QUERY = 0x777;
    @Bind(R.id.iv_code_bar)
    ImageView ivCodeBar;
    @Bind(R.id.tv_tag_code_title)
    TextView tvTagCodeTitle;
    @Bind(R.id.tv_tag_code)
    TextView tvTagCode;
    @Bind(R.id.tv_main_address)
    TextView tvMainAddress;
    @Bind(R.id.tv_detail_address)
    TextView tvDetailAddress;
    @Bind(R.id.tv_back_order_num)
    TextView tvBackOrderNum;
    @Bind(R.id.tv_box_code)
    TextView tvBoxCode;

    private String tagId;

    public static void actionStart(Context context, String tagCode) {
        Intent intent = new Intent(context, CloseBoxDetailActivity.class);
        intent.putExtra(EXTRA_TAG_ID, tagCode);
        context.startActivity(intent);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();

        if (getIntent() != null) {
            tagId = getIntent().getStringExtra(EXTRA_TAG_ID);
        }
        if (TextUtils.isEmpty(tagId)) {
            tagId = "";
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

        actionBar.getImgRightView().setImageResource(R.mipmap.ic_print_white);
        actionBar.getImgRightView().setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActionBarBtnRightClick() {
        List<String> tags = new ArrayList<>();
        tags.add(tagId);
        showWaitDialog();
        ReturningApi.printTags(tags, getToken(), getNewHandler(1, ResultBase.class));
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        showWaitDialog();
        ReturningApi.queryCloseBoxDetail(tagId, getToken(), getNewHandler
                (REQUEST_CODE_QUERY, ResultCloseBoxDetail.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        hideWaitDialog();
        if (requestCode == 1) {
            toast(result.getResponseMsg());
        } else {
            ResultCloseBoxDetail obj = (ResultCloseBoxDetail) result;
            setData(obj);
        }
    }

    private void setData(final ResultCloseBoxDetail item) {

        tvTagCode.setText(item.getTagCode());

        if (TextUtils.isEmpty(item.getReceiver()) && TextUtils.isEmpty(item.getReceiverPhone())) {
            tvMainAddress.setText(String.format("%s %s %s", item.getSourceProvince(), item
                    .getSourceCity(), item.getSourceCounty()));
            tvDetailAddress.setText(String.format("%s%s%s", item.getSourceStreet(), item
                    .getStreetVillage(), item.getSourceAddress()));
        } else {
            tvMainAddress.setText(item.getReceiver());
            tvDetailAddress.setText(item.getReceiverPhone());
        }

        tvBackOrderNum.setText(String.format(getString(R.string.close_box_back_detail_order_num),
                String.valueOf(item.getBackOrderNum())));
        tvBoxCode.setText(String.format(getString(R.string.close_box_tag_detail_box_code),
                String.valueOf(item.getBoxCode())));

        ThreadPool.PICTURE_THREAD_POOL.execute(new ExRunable(new Feedback() {
            @Override
            public void feedback(Object obj) {
                if (ivCodeBar != null && obj != null) {
                    ivCodeBar.setImageBitmap((Bitmap) obj);

                }
            }
        }) {
            @Override
            public Object execute() {
                return BarcodeUtil.createQRCode(item.getTagCode());
            }
        });
    }

}
