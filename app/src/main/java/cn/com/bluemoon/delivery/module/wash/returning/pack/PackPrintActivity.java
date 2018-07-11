package cn.com.bluemoon.delivery.module.wash.returning.pack;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
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
 * Created by allenli on 2016/10/10.
 */
public class PackPrintActivity extends BaseActivity {

    private final static String EXTRA_TAG_CODE = "EXTRA_TAG_CODE";
    private final static int REQUEST_CODE_QUERY = 0x777;
    @BindView(R.id.iv_code_bar)
    ImageView ivCodeBar;
    @BindView(R.id.tv_tag_code)
    TextView tvTagCode;

    @BindView(R.id.tv_order_username)
    TextView tvOrderUserName;
    @BindView(R.id.tv_order_userphone)
    TextView tvOrderUserPhone;

    @BindView(R.id.tv_main_address)
    TextView tvMainAddress;
    @BindView(R.id.tv_detail_address)
    TextView tvDetailAddress;

    @BindView(R.id.tv_back_order_num)
    TextView tvBackOrderNum;
    @BindView(R.id.tv_main_order_num)
    TextView tvMainkOrderNum;

    @BindView(R.id.btn_scan)
    Button btnScan;

    @BindView(R.id.tv_clothes_resource)
    TextView tvClothesReource;
    @BindView(R.id.btn_print_tag)
    Button btnPrintTag;

    private String tagCode;

    public static void actionStart(Context context, String tagCode) {
        Intent intent = new Intent(context, PackPrintActivity.class);
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
        return R.layout.activity_pack_print;
    }


    @Override
    public void initView() {
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanPackActivity.actStart(PackPrintActivity.this, tagCode);
            }
        });
        btnPrintTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWaitDialog();
                ReturningApi.printBackOrderDetail(tagCode, getToken(), getNewHandler(1, ResultBase.class));
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0){
            switch (resultCode){
                //包装成功
                case RESULT_OK:
                    String boxCode = data==null?null:data.getStringExtra("boxCode");
                    PackFinishActivity.actStart(this,tagCode,boxCode);
                    finish();
                    break;
                //包装失败
                case ScanPackActivity.RESULT_ERROR_PACK:
                    finish();
                    break;
            }
        }

    }

    @Override
    public void initData() {
        showWaitDialog();
        ReturningApi.queryBackOrderDetail2(tagCode, getToken(), getNewHandler
                (REQUEST_CODE_QUERY, ResultBackOrderDetail.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        hideWaitDialog();
        if (requestCode == REQUEST_CODE_QUERY) {
            ResultBackOrderDetail obj = (ResultBackOrderDetail) result;
            setData(obj);
        } else if (requestCode == 1){
            toast(result.getResponseMsg());
        }
    }

    @Override
    public void onErrorResponse(int requestCode, ResultBase result) {
        //跳转待打包
        if (requestCode == 1 && result.getResponseCode() == 210020) {
            toast(result.getResponseMsg());
            finish();
        } else {
            super.onErrorResponse(requestCode, result);
        }
    }

    private void setData(ResultBackOrderDetail item) {

        tvTagCode.setText(tagCode);
        tvOrderUserName.setText(getString(R.string.pack_back_box_user,item.getCustomerName()));
        tvOrderUserPhone.setText(getString(R.string.pack_back_order_phone,item.getCustomerPhone()));
        tvMainAddress.setText(String.format("%s %s %s", item.getProvince(), item.getCity(), item.getCounty()));
        tvDetailAddress.setText(String.format("%s%s%s", item.getStreet(), item.getVillage(), item.getAddress()));

        tvBackOrderNum.setText(getString(R.string.pack_back_order_clothes_num, item.getClothesNum()));

        tvMainkOrderNum.setText(getString(R.string.pack_back_order_clothes_num2, item.getFoldNum(), item.getHangNum()));
        tvClothesReource.setText(getString(R.string.pack_back_order_clothes_resource,
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
