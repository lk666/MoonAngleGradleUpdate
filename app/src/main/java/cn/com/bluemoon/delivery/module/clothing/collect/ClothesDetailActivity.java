package cn.com.bluemoon.delivery.module.clothing.collect;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.clothing.ResultClothesDetail;
import cn.com.bluemoon.delivery.module.base.BaseActionBarActivity;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.DialogUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.view.ScrollGridView;

/**
 * 衣物详情
 * Created by luokai on 2016/6/24.
 */
public class ClothesDetailActivity extends BaseActionBarActivity implements
        OnListItemClickListener {

    /**
     * 衣物编码
     */
    public static final String EXTRA_CLOTHES_CODE = "EXTRA_CLOTHES_CODE";
    @Bind(R.id.tv_collect_code)
    TextView tvCollectCode;
    @Bind(R.id.tv_urgent)
    TextView tvUrgent;
    @Bind(R.id.tv_collect_brcode)
    TextView tvCollectBrcode;
    @Bind(R.id.tv_op_name)
    TextView tvOpName;
    @Bind(R.id.tv_op_number)
    TextView tvOpNumber;
    @Bind(R.id.tv_op_phone)
    TextView tvOpPhone;
    @Bind(R.id.tv_collect_time)
    TextView tvCollectTime;
    @Bind(R.id.tv_collect_appoint_back_time)
    TextView tvCollectAppointBackTime;
    @Bind(R.id.tv_clotnes_code)
    TextView tvClotnesCode;
    @Bind(R.id.tv_clothes_name)
    TextView tvClothesName;
    @Bind(R.id.tv_type_name)
    TextView tvTypeName;
    @Bind(R.id.tv_flaw_dec)
    TextView tvFlawDec;
    @Bind(R.id.tv_backup)
    TextView tvBackup;
    @Bind(R.id.iv_stain)
    ImageView ivStain;
    @Bind(R.id.iv_flaw)
    ImageView ivFlaw;
    @Bind(R.id.sgv_photo)
    ScrollGridView sgvPhoto;

    /**
     * 衣物编码
     */
    private String clothesCode;

    private ClothesPhotoAdapter clothesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothes_detail);
        ButterKnife.bind(this);
        setIntentData();
        initView();
        getData();
    }

    @Override
    protected int getActionBarTitleRes() {
        return R.string.title_clothing_detail;
    }

    private void setIntentData() {
        clothesCode = getIntent().getStringExtra(EXTRA_CLOTHES_CODE);
        if (clothesCode == null) {
            clothesCode = "";
        }
    }

    private void initView() {
        clothesAdapter = new ClothesPhotoAdapter(this, this);
        sgvPhoto.setAdapter(clothesAdapter);
    }

    private void getData() {
        String token = ClientStateManager.getLoginToken(this);
        showProgressDialog();
        DeliveryApi.getCollectInfoDetailsItem(clothesCode, token, getCollectInfoDetailsItemHandler);
    }

    /**
     * 获取衣物详情返回
     */
    AsyncHttpResponseHandler getCollectInfoDetailsItemHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d(getDefaultTag(), "获取衣物详情 result = " + responseString);
            dismissProgressDialog();
            try {
                ResultClothesDetail result = JSON.parseObject(responseString,
                        ResultClothesDetail.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    setClothesInfo(result);
                } else {
                    PublicUtil.showErrorMsg(ClothesDetailActivity.this, result);
                }
            } catch (Exception e) {
                LogUtils.e(getDefaultTag(), e.getMessage());
                PublicUtil.showToastServerBusy();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            LogUtils.e(getDefaultTag(), throwable.getMessage());
            dismissProgressDialog();
            PublicUtil.showToastServerOvertime();
        }
    };

    /**
     * 设置衣物信息
     */
    private void setClothesInfo(ResultClothesDetail result) {
        tvCollectCode.setText(String.format(getString(R.string
                .with_order_collect_collect_number_text_num), result.getCollectCode()));

        // 加急的逻辑
        if (result.getIsUrgent() == 1) {
            tvUrgent.setVisibility(View.VISIBLE);
            tvCollectAppointBackTime.setVisibility(View.VISIBLE);
            tvCollectAppointBackTime.setText(String.format("%s%s",
                    getString(R.string.clothing_detail_appoint_back_time),
                    DateUtil.getTime(result.getAppointBackTime(), "yyyy-MM-dd HH:mm")));
        } else {
            tvUrgent.setVisibility(View.GONE);
            tvCollectAppointBackTime.setVisibility(View.GONE);
        }

        String brcode = result.getCollectBrcode();
        if (TextUtils.isEmpty(brcode)) {
            tvCollectBrcode.setText(String.format("%s%s",
                    getString(R.string.clothing_detail_brcode),
                    getString(R.string.text_empty)));
        } else {
            tvCollectBrcode.setText(String.format("%s%s",
                    getString(R.string.clothing_detail_brcode), result
                            .getCollectBrcode()));
        }

        // 收件人信息
        tvOpName.setText(String.format("%s%s", getString(R.string.clothing_detail_receiver_name),
                result.getOpName()));
        tvOpNumber.setText(result.getOpCode());
        tvOpPhone.setText(result.getOpPhone());
        tvOpPhone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tvOpPhone.getPaint().setAntiAlias(true);

        tvCollectTime.setText(String.format("%s%s", getString(R.string.clothing_detail_time),
                DateUtil.getTime(result
                        .getOpTime(), "yyyy-MM-dd " + "HH:mm")));

        tvClotnesCode.setText(String.format("%s%s", getString(R.string
                .clothing_detail_clothes_code), clothesCode));
        tvTypeName.setText(result.getTypeName());
        tvClothesName.setText(result.getClothesName());

        if (result.getHasFlaw() == 1) {
            ivFlaw.setVisibility(View.VISIBLE);
            tvFlawDec.setText(String.format("%s%s", getString(R.string.clothing_detail_flaw),
                    result.getFlawDesc()));
        } else {
            ivFlaw.setVisibility(View.GONE);
            tvFlawDec.setText(String.format("%s%s", getString(R.string.clothing_detail_flaw),
                    getString(R.string.text_empty)));
        }

        if (result.getHasStain() == 1) {
            ivStain.setVisibility(View.VISIBLE);
        } else {
            ivStain.setVisibility(View.GONE);
        }

        String backup = result.getRemark();
        if (TextUtils.isEmpty(backup)) {
            tvBackup.setText(String.format("%s%s", getString(R.string.clothing_detail_backup),
                    getString(R.string.text_empty)));
        } else {
            tvBackup.setText(String.format("%s%s", getString(R.string.clothing_detail_backup),
                    backup));
        }

        clothesAdapter.setList(result.getClothesImg());
        clothesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(Object item, View view, int position) {
        // 衣物照片
        if (item instanceof ClothingPic) {
            ClothingPic pic = (ClothingPic) item;
            switch (view.getId()) {
                // 浏览图片
                case R.id.iv_pic:
                    // TODO: lk 2016/6/25 实现毛玻璃效果 http://blog.csdn
                    // .net/lvshaorong/article/details/50392057
                    DialogUtil.showPictureDialog(ClothesDetailActivity.this, pic.getImgPath());
                    break;
            }
        }
    }


    public static void actionStart(Context context, String clothesCode) {
        Intent intent = new Intent(context, ClothesDetailActivity.class);
        intent.putExtra(EXTRA_CLOTHES_CODE, clothesCode);
        context.startActivity(intent);
    }
}
