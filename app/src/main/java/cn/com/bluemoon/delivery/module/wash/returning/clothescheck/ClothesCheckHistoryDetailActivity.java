package cn.com.bluemoon.delivery.module.wash.returning.clothescheck;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.clothescheck.Clothes;
import cn.com.bluemoon.delivery.app.api.model.wash.clothescheck.ResultClothesCheckHistoryDetail;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.ui.NoScrollListView;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.DialogUtil;
import cn.com.bluemoon.lib.utils.ImageLoaderUtil;
import cn.com.bluemoon.lib.view.ScrollGridView;

/**
 * 衣物清点详情
 */
public class ClothesCheckHistoryDetailActivity extends BaseActivity implements
        OnListItemClickListener {
    private static final String EXTRA_BACK_ORDER_CODE = "EXTRA_BACK_ORDER_CODE";
    private static final String EXTRA_CHECK_LOG_ID = "EXTRA_CHECK_LOG_ID";
    private static final int REQUEST_CODE = 0x777;

    @Bind(R.id.tv_back_order_code)
    TextView tvBackOrderCode;
    @Bind(R.id.tv_count)
    TextView tvCount;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.lv)
    NoScrollListView lv;
    @Bind(R.id.tv_error)
    TextView tvError;
    @Bind(R.id.ll_error_txt)
    LinearLayout llErrorTxt;
    @Bind(R.id.sgv_photo)
    ScrollGridView sgvPhoto;
    @Bind(R.id.ll_error_img)
    LinearLayout llErrorImg;
    @Bind(R.id.sc_main)
    ScrollView scMain;

    private String backOrderCode;
    private String checkLogId;

    private ImgAdapter imgAdapter;
    private ClothesAdapter clothesAdapter;

    public static void actionStart(Activity activity, Fragment fragment, String checkLogId, String backOrderCode) {
        Intent intent = new Intent(activity, ClothesCheckHistoryDetailActivity.class);
        intent.putExtra(EXTRA_BACK_ORDER_CODE, backOrderCode);
        intent.putExtra(EXTRA_CHECK_LOG_ID, checkLogId);
        if (fragment != null) {
            fragment.startActivity(intent);
        } else {
            activity.startActivity(intent);
        }
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();

        if (getIntent().hasExtra(EXTRA_BACK_ORDER_CODE)) {
            backOrderCode = getIntent().getStringExtra(EXTRA_BACK_ORDER_CODE);
        }

        if (getIntent().hasExtra(EXTRA_CHECK_LOG_ID)) {
            checkLogId = getIntent().getStringExtra(EXTRA_CHECK_LOG_ID);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_clothes_check_history_detail;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.clothes_check_detail);
    }

    @Override
    public void initView() {
        imgAdapter = new ImgAdapter(this, this);
        clothesAdapter = new ClothesAdapter(this, this);
        lv.setAdapter(clothesAdapter);
        sgvPhoto.setAdapter(imgAdapter);
    }

    @Override
    public void initData() {
        showWaitDialog();
        ReturningApi.queryClothesCheckHistoryDetail(backOrderCode, checkLogId, getToken(),
                getNewHandler(REQUEST_CODE, ResultClothesCheckHistoryDetail.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        ResultClothesCheckHistoryDetail obj = (ResultClothesCheckHistoryDetail) result;
        setData(obj);
    }

    private void setData(ResultClothesCheckHistoryDetail result) {
        if (result == null) {
            return;
        }

        tvBackOrderCode.setText(String.format(getString(R.string.back_order_code), backOrderCode));
        tvCount.setText(String.format(getString(R.string.clothes_check_history_clothes_num),
                String.valueOf(result.getClothesList().size())));
        tvTime.setText(DateUtil.getTime(result.getOpTime(), "yyyy-MM-dd HH:mm"));

        // 异常描述
        if (TextUtils.isEmpty(result.getIssueDesc())) {
            llErrorTxt.setVisibility(View.GONE);
        } else {
            llErrorTxt.setVisibility(View.VISIBLE);
            tvError.setText(result.getIssueDesc());
        }

        // 异常图片
        if (result.getImagePathList() == null || result.getImagePathList().isEmpty()) {
            llErrorImg.setVisibility(View.GONE);
        } else {
            llErrorImg.setVisibility(View.VISIBLE);
            imgAdapter.setList(result.getImagePathList());
            imgAdapter.notifyDataSetChanged();
        }

        clothesAdapter.setList(result.getClothesList());
        clothesAdapter.notifyDataSetChanged();
        scMain.post(new Runnable() {
            @Override
            public void run() {
                scMain.scrollTo(0, 0);
            }
        });
    }

    class ImgAdapter extends BaseListAdapter<String> {
        public ImgAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_clothes_img;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            String item = (String) getItem(position);

            ImageView iv = getViewById(R.id.iv_pic);
            ImageLoaderUtil.displayImage(item, iv);

            setClickEvent(isNew, position, iv);
        }
    }

    class ClothesAdapter extends BaseListAdapter<Clothes> {
        public ClothesAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_clothes_check_detail;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            Clothes item = (Clothes) getItem(position);

            TextView tvCode = getViewById(R.id.tv_clothes_code);
            ImageView iv = getViewById(R.id.iv);

            tvCode.setText(String.format(getString(R.string.clothes_check_detail_code),
                    item.getClothesCode()));
            ImageLoaderUtil.displayImage(item.getImagePath(), iv);
            setClickEvent(isNew, position, iv);
        }
    }

    @Override
    public void onItemClick(Object item, View view, int position) {
        String imgPath;
        if (item instanceof Clothes) {
            imgPath = ((Clothes) item).getImagePath();
        } else {
            imgPath = item.toString();
        }
        DialogUtil.showPictureDialog(this, imgPath);
    }
}