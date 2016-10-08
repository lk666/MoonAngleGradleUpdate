package cn.com.bluemoon.delivery.module.wash.returning.pack;

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
import cn.com.bluemoon.delivery.app.api.model.wash.pack.ResultBackOrderDetail;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.lib.qrcode.utils.BarcodeUtil;
import cn.com.bluemoon.lib.utils.threadhelper.ExRunable;
import cn.com.bluemoon.lib.utils.threadhelper.Feedback;
import cn.com.bluemoon.lib.utils.threadhelper.ThreadPool;

/**
 * Created by allenli on 2016/10/8.
 */
public class PackDetailActivity extends BaseActivity {

    private final static String EXTRA_TAG_CODE = "EXTRA_TAG_CODE";
    private final static int REQUEST_CODE_QUERY = 0x777;
    @Bind(R.id.iv_code_bar)
    ImageView ivCodeBar;
    @Bind(R.id.tv_tag_code)
    TextView tvTagCode;

    @Bind(R.id.tv_order_username)
    TextView tvOrderUserName;
    @Bind(R.id.tv_order_userphone)
    TextView tvOrderUserPhone;

    @Bind(R.id.tv_main_address)
    TextView tvMainAddress;
    @Bind(R.id.tv_detail_address)
    TextView tvDetailAddress;

    @Bind(R.id.tv_back_order_num)
    TextView tvBackOrderNum;
    @Bind(R.id.tv_main_order_num)
    TextView tvMainkOrderNum;

    @Bind(R.id.tv_clothes_resource)
    TextView tvClothesReource;

    private String tagCode;

    public static void actionStart(Context context, String tagCode) {
        Intent intent = new Intent(context, PackDetailActivity.class);
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
        return R.layout.activity_pack_detail;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.title_back_detail);
    }

    @Override
    protected void setActionBar(CommonActionBar actionBar) {
        super.setActionBar(actionBar);

        actionBar.getImgRightView().setImageResource(R.mipmap.ic_print_white);
        actionBar.getImgRightView().setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActionBarBtnRightClick() {
        // TODO: lk 2016/9/20 点击打印
        toast("打印" + tagCode);
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        showWaitDialog();
        ReturningApi.queryBackOrderDetail2(tagCode, getToken(), getNewHandler
                (REQUEST_CODE_QUERY, ResultBackOrderDetail.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        ResultBackOrderDetail obj = (ResultBackOrderDetail) result;
        setData(obj);
    }

    private void setData(ResultBackOrderDetail item) {

        tvTagCode.setText(tagCode);
        tvOrderUserName.setText(String.format(getString(R.string.pack_back_box_user),item.getCustomerName()));
        tvOrderUserPhone.setText(String.format(getString(R.string.pack_back_order_phone),item.getCustomerPhone()));
        tvMainAddress.setText(String.format("%s %s %s", item.getProvince(), item.getCity(), item.getCounty()));
        tvDetailAddress.setText(String.format("%s%s%s", item.getStreet(), item.getVillage(), item.getAddress()));

        tvBackOrderNum.setText(String.format(getString(R.string.pack_back_order_clothes_num), item.getFoldNum()+ item.getHangNum()));

        tvMainkOrderNum.setText(String.format(getString(R.string.pack_back_order_clothes_num2), item.getFoldNum(), item.getHangNum()));
        tvClothesReource.setText(String.format(getString(R.string.pack_back_order_clothes_resource),
                String.valueOf(item.getClothesSource())));

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
                return BarcodeUtil.createQRCode(tagCode);
            }
        });
    }

}
