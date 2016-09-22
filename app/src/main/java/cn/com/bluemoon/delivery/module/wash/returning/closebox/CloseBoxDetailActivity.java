package cn.com.bluemoon.delivery.module.wash.returning.closebox;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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

    private final static String EXTRA_TAG_CODE = "EXTRA_TAG_CODE";
    private final static int REQUEST_CODE_QUERY_CLOSE_BOX_DETAIL = 0x777;

    private String tagCode;

    @Bind(R.id.iv_code_bar)
    ImageView ivCodeBar;
    @Bind(R.id.tv_tag_code)
    TextView tvTagCode;
    @Bind(R.id.tv_address)
    TextView tvAddress;
    @Bind(R.id.tv_address_detail)
    TextView tvAddressDetail;
    @Bind(R.id.tv_back_order_num)
    TextView tvBackOrderNum;
    @Bind(R.id.tv_box_code)
    TextView tvBoxCode;

    public static void actionStart(Context context, String tagCode) {
        Intent intent = new Intent(context, CloseBoxDetailActivity.class);
        intent.putExtra(EXTRA_TAG_CODE, tagCode);
        context.startActivity(intent);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();

        if (getIntent() != null) {
            tagCode = getIntent().getStringExtra(EXTRA_TAG_CODE);
        }
        if (TextUtils.isEmpty(tagCode)) {
            tagCode = "";
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
        // TODO: lk 2016/9/20 点击重新打印
        toast("重新打印" + tagCode);
    }

    @Override
    public void initView() {
    }

    @Override
    public void initData() {
        showWaitDialog();
        ReturningApi.queryCloseBoxDetail(tagCode, getToken(), getNewHandler
                (REQUEST_CODE_QUERY_CLOSE_BOX_DETAIL, ResultCloseBoxDetail.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        ResultCloseBoxDetail obj = (ResultCloseBoxDetail) result;
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

    private void setData(ResultCloseBoxDetail result) {
        tvTagCode.setText(String.format(getString(R.string.close_box_tag_detail_code), tagCode));

        if (result == null) {
            tvAddress.setText("");
            tvAddressDetail.setText("");
            tvBackOrderNum.setText(R.string.close_box_back_order_num);
            tvBoxCode.setText(R.string.close_box_tag_detail_box_code);
        } else {
            tvAddress.setText(String.format("%s %s %s", result.getSourceProvince(), result
                    .getSourceCity(), result.getSourceCounty()));
            tvAddressDetail.setText(result.getSourceAddress());
            tvBackOrderNum.setText(String.format(getString(R.string
                            .close_box_back_detail_order_num),

                    result.getBackOrderNum()));
            tvBoxCode.setText(String.format(getString(R.string.close_box_tag_detail_box_code),
                    result
                            .getBoxCode()));
        }

        ThreadPool.PICTURE_THREAD_POOL.execute(new ExRunable(new Feedback() {
            @Override
            public void feedback(Object obj) {
                if (ivCodeBar != null) {
                    ivCodeBar.setImageBitmap((Bitmap) obj);
                }

            }
        }) {
            @Override
            public Object execute() {
                return BarcodeUtil.createQRCode(tagCode);
            }
        });
    }
}
