package cn.com.bluemoon.delivery.module.wash.returning.clothescheck;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.clothescheck.Clothes;
import cn.com.bluemoon.delivery.app.api.model.wash.clothescheck.ResultBackOrderDetail;
import cn.com.bluemoon.delivery.app.api.model.wash.manager.ImageInfo;
import cn.com.bluemoon.delivery.common.photopicker.PhotoPickerActivity;
import cn.com.bluemoon.delivery.common.photopicker.PhotoPreviewActivity;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.ui.ImageGridView;
import cn.com.bluemoon.delivery.ui.NoScrollListView;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.manager.UploadImageManager;
import cn.com.bluemoon.lib.view.switchbutton.SwitchButton;

/**
 * 衣物清单
 */
public class ClothesListDetailActivity extends BaseActivity implements
        OnListItemClickListener {

    private static final String EXTRA_BACK_ORDER_CODE = "EXTRA_BACK_ORDER_CODE";
    private static final int REQUEST_CODE_QUERY_LIST = 0x777;
    private static final int REQUEST_CODE_SCAN = 0x666;
    private static final int REQUEST_CODE_UPLOAD_IMG = 0x555;
    private static final int REQUEST_CODE_FINISH = 0x444;
    @BindView(R.id.tv_back_order_code)
    TextView tvBackOrderCode;
    @BindView(R.id.tv_count)
    TextView tvCount;
    @BindView(R.id.lv)
    NoScrollListView lv;
    @BindView(R.id.et_abnormal)
    EditText etAbnormal;
    @BindView(R.id.ll_abnormal_txt)
    LinearLayout llAbnormalTxt;
    @BindView(R.id.gridview_img)
    ImageGridView gridviewImg;
    @BindView(R.id.ll_abnormal_img)
    LinearLayout llAbnormalImg;
    @BindView(R.id.sc_main)
    ScrollView scMain;
    @BindView(R.id.btn_finish)
    Button btnFinish;

    @BindView(R.id.cb_abnormal)
    SwitchButton cbAbnormal;

    private String backOrderCode;

    private List<String> paths;
    private UploadImageManager uploadImageManager;

    /**
     * 数据
     */
    private ArrayList<Clothes> list = new ArrayList<>();

    private ItemAdapter adapter;

    public static void actionStart(Context context, String backOrderCode) {
        Intent intent = new Intent(context, ClothesListDetailActivity.class);
        intent.putExtra(EXTRA_BACK_ORDER_CODE, backOrderCode);
        context.startActivity(intent);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();

        if (getIntent().hasExtra(EXTRA_BACK_ORDER_CODE)) {
            backOrderCode = getIntent().getStringExtra(EXTRA_BACK_ORDER_CODE);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_clothes_list_detail;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.clothes_check_list_title);
    }

    @Override
    protected void setActionBar(CommonActionBar titleBar) {
        titleBar.getImgLeftView().setVisibility(View.GONE);

        titleBar.getImgRightView().setImageResource(R.mipmap.ic_scan);
        titleBar.getImgRightView().setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActionBarBtnRightClick() {
        ScanClothesCodeActivity.actionStart(this, null, REQUEST_CODE_SCAN, list);
    }

    @Override
    public void initView() {
        adapter = new ItemAdapter(this, this);
        adapter.setList(list);
        lv.setAdapter(adapter);
        paths = new ArrayList<>();
        gridviewImg.loadAdpater(paths, true);

        cbAbnormal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    llAbnormalTxt.setVisibility(View.VISIBLE);
                    llAbnormalImg.setVisibility(View.VISIBLE);
                } else {
                    llAbnormalTxt.setVisibility(View.GONE);
                    llAbnormalImg.setVisibility(View.GONE);
                }
                checkFinish();
            }
        });

        etAbnormal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etAbnormal.getLineCount() > 1) {
                    etAbnormal.setGravity(Gravity.START);
                } else {
                    etAbnormal.setGravity(Gravity.END);
                }
                checkFinish();
            }
        });
    }

    /**
     * 检查完成按钮是否可见
     */
    private void checkFinish() {
        // 有异常
        if (cbAbnormal.isChecked()) {
            btnFinish.setVisibility(View.VISIBLE);
            return;
        } else {
            // 全部已扫描
            for (Clothes c : list) {
                if (!c.isScaned()) {
                    btnFinish.setVisibility(View.GONE);
                    return;
                }
            }
            btnFinish.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void initData() {
        showWaitDialog();
        ReturningApi.queryBackOrderDetail3(backOrderCode, getToken(), getNewHandler
                (REQUEST_CODE_QUERY_LIST, ResultBackOrderDetail.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        switch (requestCode) {
            // 请求列表数据
            case REQUEST_CODE_QUERY_LIST:
                ResultBackOrderDetail obj = (ResultBackOrderDetail) result;
                setData(obj);
                break;
            // 清点完成
            case REQUEST_CODE_FINISH:
                hideWaitDialog();
                finish();
        }
    }

    private void setData(ResultBackOrderDetail result) {
        if (result == null) {
            return;
        }
        paths = new ArrayList<>();
        gridviewImg.loadAdpater(paths, true);

        btnFinish.setVisibility(View.GONE);

        cbAbnormal.setChecked(false);
        llAbnormalTxt.setVisibility(View.GONE);
        llAbnormalImg.setVisibility(View.GONE);

        tvBackOrderCode.setText(String.format(getString(R.string.back_order_code),
                backOrderCode));

        int num = 0;
        List<Clothes> steList = result.getClothesList();
        if (steList != null) {
            num = steList.size();
        }
        tvCount.setText(String.format(getString(R.string.clothes_check_history_clothes_num), num));

        list.clear();
        if (num > 0) {
            list.addAll(steList);
        }
        adapter.notifyDataSetChanged();
        scMain.post(new Runnable() {
            @Override
            public void run() {
                scMain.scrollTo(0, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //  扫码返回
        if (requestCode == REQUEST_CODE_SCAN) {
            ArrayList<Clothes> l = (ArrayList<Clothes>) data.getSerializableExtra(
                    ScanClothesCodeActivity.EXTRA_LIST);

            list.clear();
            if (l != null) {
                list.addAll(l);
            }
            adapter.notifyDataSetChanged();

            checkFinish();
            return;
        }

        // 图片控件
        if (requestCode == Constants.REQUEST_ADD_IMG && data != null) {
            List<String> list = data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT);
            paths.addAll(list);
            gridviewImg.loadAdpater(paths, true);
            uploadImageManager = null;
        } else if (requestCode == Constants.REQUEST_PREVIEW_IMG && data != null) {
            paths = data.getStringArrayListExtra(PhotoPreviewActivity.EXTRA_RESULT);
            gridviewImg.loadAdpater(paths, true);
            uploadImageManager = null;
        }
    }

    @OnClick(R.id.btn_finish)
    public void onClick() {
        btnFinish.setClickable(false);
        //标注异常
        if (cbAbnormal.isChecked()) {
            String txt = etAbnormal.getText().toString();
            //异常描述必须5字以上
            if (txt.length() < 5) {
                toast(R.string.manage_refuse_reason_tips);
                btnFinish.setClickable(true);
                return;
            }
            showWaitDialog();
            //有图片上传
            if (paths != null && paths.size() > 1) {
                if (uploadImageManager == null) {
                    uploadImageManager = new UploadImageManager(this, paths, new UploadImageManager.UploadResultListener() {
                        @Override
                        public void uploadResult(boolean success, List<ImageInfo> imgPaths) {
                            if (success) {
                                ReturningApi.scanBackClothesOrder(backOrderCode, imgPaths, etAbnormal.getText().toString(),
                                        getToken(), getNewHandler(REQUEST_CODE_FINISH, ResultBase.class));
                            } else {
                                hideWaitDialog();
                                btnFinish.setClickable(true);
                            }
                        }
                    });
                }
                uploadImageManager.upload();
            } else {
                //无图片上传
                ReturningApi.scanBackClothesOrder(backOrderCode, null, txt,
                        getToken(), getNewHandler(REQUEST_CODE_FINISH, ResultBase.class));
            }
        } else {
            //正常处理
            ReturningApi.scanBackClothesOrder(backOrderCode, null, null,
                    getToken(), getNewHandler(REQUEST_CODE_FINISH, ResultBase.class));
        }
    }



    class ItemAdapter extends BaseListAdapter<Clothes> {
        public ItemAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_clothes_check;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            Clothes item = (Clothes) getItem(position);

            TextView tvCode = getViewById(R.id.tv_code);
            ImageView ivScan = getViewById(R.id.iv_scan);
            ImageView iv = getViewById(R.id.iv);

            tvCode.setText(String.format(getString(R.string.clothes_check_detail_code),
                    item.getClothesCode()));

            Glide.with(context)
                    .load(item.getImagePath())
                    .error(R.mipmap.place_holder)
                    .placeholder(R.mipmap.place_holder)
                    .into(iv);

            if (item.isScaned()) {
                ivScan.setVisibility(View.VISIBLE);
            } else {
                ivScan.setVisibility(View.GONE);
            }

            //            setClickEvent(isNew, position, iv, ivScan);
        }
    }

    @Override
    public void onItemClick(Object item, View view, int position) {

    }

}