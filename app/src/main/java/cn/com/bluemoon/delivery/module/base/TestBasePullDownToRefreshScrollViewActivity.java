package cn.com.bluemoon.delivery.module.base;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.clothing.ResultClothesDetail;
import cn.com.bluemoon.delivery.module.clothing.collect.ClothesPhotoAdapter;
import cn.com.bluemoon.delivery.module.clothing.collect.ClothingPic;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.DialogUtil;
import cn.com.bluemoon.lib.view.ScrollGridView;

public class TestBasePullDownToRefreshScrollViewActivity extends BasePullDownToRefreshScrollViewActivity
        implements OnListItemClickListener {
    /**
     * 已上传的图片列表
     */
    private List<ClothingPic> clothesImg;

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
    protected int getActionBarTitleRes() {
        return R.string.title_clothing_detail;
    }

    @Override
    protected void setIntentData() {
        clothesCode = getIntent().getStringExtra(EXTRA_CLOTHES_CODE);
        if (clothesCode == null) {
            clothesCode = "";
        }
    }

    @Override
    protected void initContentView(View contentView) {
        clothesAdapter = new ClothesPhotoAdapter(this, this);
        sgvPhoto.setAdapter(clothesAdapter);
    }

    @Override
    protected void invokeGetDataDeliveryApi(AsyncHttpResponseHandler handler) {
        DeliveryApi.getCollectInfoDetailsItem(clothesCode, ClientStateManager.getLoginToken(this)
                , handler);
    }


    boolean is = false;

    @Override
    protected boolean isDataEmpty(Object result) {
//        ResultClothesDetail resultClothesDetail = (ResultClothesDetail) result;
//        if (TextUtils.isEmpty(resultClothesDetail.getCollectCode())) {
//            return true;
//        }
        is = !is;
        return is;
    }

    @Override
    protected void setGetDataObj(Object result) {
        setClothesInfo((ResultClothesDetail) result);
    }

    /**
     * 设置衣物信息
     */
    private void setClothesInfo(ResultClothesDetail result) {
        tvCollectCode.setText(getString(R.string.clothing_detail_collect_code) + result
                .getCollectCode());

        // 加急的逻辑
        if (result.getIsUrgent() == 1) {
            tvUrgent.setVisibility(View.VISIBLE);
            tvCollectAppointBackTime.setVisibility(View.VISIBLE);
            tvCollectAppointBackTime.setText(getString(R.string
                    .clothing_detail_appoint_back_time) + DateUtil.getTime(result
                    .getAppointBackTime(), "yyyy-MM-dd " + "HH:mm"));
        } else {
            tvUrgent.setVisibility(View.GONE);
            tvCollectAppointBackTime.setVisibility(View.GONE);
        }

        String brcode = result.getCollectBrcode();
        if (TextUtils.isEmpty(brcode)) {
            tvCollectBrcode.setText(getString(R.string.clothing_detail_brcode) +
                    getString(R.string.text_empty));
        } else {
            tvCollectBrcode.setText(getString(R.string.clothing_detail_brcode) + result
                    .getCollectBrcode());
        }

        // 收件人信息
        tvOpName.setText(getString(R.string.clothing_detail_receiver_name) + result.getOpName());
        tvOpNumber.setText(result.getOpCode());
        tvOpPhone.setText(result.getOpPhone());
        tvOpPhone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tvOpPhone.getPaint().setAntiAlias(true);

        tvCollectTime.setText(getString(R.string.clothing_detail_time) + DateUtil.getTime(result
                .getOpTime(), "yyyy-MM-dd " + "HH:mm"));

        tvClotnesCode.setText(getString(R.string.clothing_detail_clothes_code) + clothesCode);
        tvTypeName.setText(result.getTypeName());
        tvClothesName.setText(result.getClothesName());

        if (result.getHasFlaw() == 1) {
            ivFlaw.setVisibility(View.VISIBLE);
            tvFlawDec.setText(getString(R.string.clothing_detail_flaw) + result.getFlawDesc());
        } else {
            ivFlaw.setVisibility(View.GONE);
            tvFlawDec.setText(getString(R.string.clothing_detail_flaw) +
                    getString(R.string.text_empty));
        }

        if (result.getHasStain() == 1) {
            ivStain.setVisibility(View.VISIBLE);
        } else {
            ivStain.setVisibility(View.GONE);
        }

        String backup = result.getRemark();
        if (TextUtils.isEmpty(backup)) {
            tvBackup.setText(getString(R.string.clothing_detail_backup) +
                    getString(R.string.text_empty));
        } else {
            tvBackup.setText(getString(R.string.clothing_detail_backup) + backup);
        }

        clothesImg = new ArrayList<>();
        clothesImg.addAll(result.getClothesImg());
        clothesAdapter.setList(clothesImg);
        clothesAdapter.notifyDataSetChanged();
    }

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.view_test_activity_pull_to_refresh_scrollview;
    }

    @Override
    protected int getErrorViewLayoutId() {
        return R.layout.view_test_activity_pull_to_refresh_scrollview_error;
    }

    @Override
    protected void initErrorViewEvent(View errorView) {
        errorView.findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
    }

    @Override
    protected int getEmptyViewLayoutId() {
        return R.layout.view_test_activity_pull_to_refresh_scrollview_empty;
    }

    @Override
    protected void initEmptyViewEvent(View emptyView) {
        emptyView.findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
    }

    @Override
    protected Class getResultClass() {
        return ResultClothesDetail.class;
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
                    DialogUtil.showPictureDialog(TestBasePullDownToRefreshScrollViewActivity.this,
                            pic.getImgPath());
                    break;
            }
        }
    }

    public static void actionStart(Context context, String clothesCode) {
        Intent intent = new Intent(context, TestBasePullDownToRefreshScrollViewActivity.class);
        intent.putExtra(EXTRA_CLOTHES_CODE, clothesCode);
        context.startActivity(intent);
    }
}
