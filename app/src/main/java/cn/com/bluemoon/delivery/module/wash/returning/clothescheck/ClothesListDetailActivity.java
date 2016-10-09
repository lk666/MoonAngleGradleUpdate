package cn.com.bluemoon.delivery.module.wash.returning.clothescheck;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.ResultUploadExceptionImage;
import cn.com.bluemoon.delivery.app.api.model.wash.clothescheck.Clothes;
import cn.com.bluemoon.delivery.app.api.model.wash.clothescheck.ResultBackOrderDetail;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.wash.collect.AddPhotoAdapter;
import cn.com.bluemoon.delivery.module.wash.collect.ClothingPic;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.ui.NoScrollListView;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DialogUtil;
import cn.com.bluemoon.delivery.utils.FileUtil;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.utils.ImageLoaderUtil;
import cn.com.bluemoon.lib.view.ScrollGridView;
import cn.com.bluemoon.lib.view.TakePhotoPopView;
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
    private static final int MAX_UPLOAD_IMG = 5;
    @Bind(R.id.tv_back_order_code)
    TextView tvBackOrderCode;
    @Bind(R.id.tv_count)
    TextView tvCount;
    @Bind(R.id.lv)
    NoScrollListView lv;
    @Bind(R.id.et_abnormal)
    EditText etAbnormal;
    @Bind(R.id.ll_abnormal_txt)
    LinearLayout llAbnormalTxt;
    @Bind(R.id.sgv_photo)
    ScrollGridView sgvPhoto;
    @Bind(R.id.ll_abnormal_img)
    LinearLayout llAbnormalImg;
    @Bind(R.id.sc_main)
    ScrollView scMain;
    @Bind(R.id.btn_finish)
    Button btnFinish;

    @Bind(R.id.cb_abnormal)
    SwitchButton cbAbnormal;

    private String backOrderCode;

    /**
     * 数据
     */
    private ArrayList<Clothes> list = new ArrayList<>();

    private ItemAdapter adapter;
    private AddPhotoAdapter imgAdapter;

    private TakePhotoPopView takePhotoPop;
    /**
     * 异常图片列表
     */
    private List<ClothingPic> abnormalImgs;

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

        imgAdapter = new AddPhotoAdapter(this, this,
                getString(R.string.clothing_book_in_phote_most_5));
        sgvPhoto.setAdapter(imgAdapter);

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
     * 异常图片初始化
     */
    private void initClothesImgList() {
        abnormalImgs = new ArrayList<>();
        addAddImage();
        imgAdapter.setList(abnormalImgs);
        imgAdapter.notifyDataSetChanged();
    }

    /**
     * 上传图片
     */
    private void uploadImg(Bitmap bm) {
        showWaitDialog();
        ReturningApi.uploadExceptionImage(FileUtil.getBytes(bm), UUID.randomUUID() + ".png",
                getToken(), getNewHandler(REQUEST_CODE_UPLOAD_IMG,
                        ResultUploadExceptionImage.class));
    }

    /**
     * 检查完成按钮是否可见
     */
    private void checkFinish() {
        String txt = etAbnormal.getText().toString();
        // 有异常，且文字大于等于5
        if (cbAbnormal.isChecked() && txt.length() < 5) {
            btnFinish.setVisibility(View.GONE);
            return;
        }

        // 全部已扫描
        for (Clothes c : list) {
            if (!c.isScaned()) {
                btnFinish.setVisibility(View.GONE);
                return;
            }
        }
        btnFinish.setVisibility(View.VISIBLE);
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

            // 上传图片
            case REQUEST_CODE_UPLOAD_IMG:
                ResultUploadExceptionImage pic = (ResultUploadExceptionImage) result;
                ClothingPic cp = new ClothingPic();
                cp.setImgPath(pic.getImgPath());
                cp.setImgId("");
                abnormalImgs.add(abnormalImgs.size() - 1, cp);
                if (abnormalImgs.size() > MAX_UPLOAD_IMG) {
                    abnormalImgs.remove(abnormalImgs.size() - 1);
                }
                imgAdapter.notifyDataSetChanged();
                PublicUtil.showToast(getString(R.string.upload_success));
                break;
            // 清点完成
            case REQUEST_CODE_FINISH:
                finish();
        }
    }

    private void setData(ResultBackOrderDetail result) {
        if (result == null) {
            return;
        }

        btnFinish.setVisibility(View.GONE);

        initClothesImgList();

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

        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }

        switch (requestCode) {
            // 拍照
            case Constants.TAKE_PIC_RESULT:
                if (takePhotoPop != null) {
                    Bitmap bm = takePhotoPop.getTakeImageBitmap();
                    uploadImg(bm);
                }
                break;

            // 选择照片
            case Constants.CHOSE_PIC_RESULT:
                if (takePhotoPop != null) {
                    Bitmap bm = takePhotoPop.getPickImageBitmap(data);
                    uploadImg(bm);
                }
                break;
        }
    }

    private void addAddImage() {
        if (abnormalImgs.size() < MAX_UPLOAD_IMG) {
            ClothingPic addPic = new ClothingPic();
            addPic.setImgId(AddPhotoAdapter.ADD_IMG_ID);
            abnormalImgs.add(addPic);
        }
    }

    @OnClick(R.id.btn_finish)
    public void onClick() {
        //  点击清点完成
        ArrayList<UploadImage> imgs = new ArrayList<>();
        for (ClothingPic c : abnormalImgs) {
            if (!c.getImgId().equals(AddPhotoAdapter.ADD_IMG_ID)) {
                UploadImage u = new UploadImage(c.getImgPath());
                imgs.add(u);
            }
        }
        ReturningApi.scanBackClothesOrder(backOrderCode, imgs, etAbnormal.getText().toString(),
                getToken(), getNewHandler(REQUEST_CODE_FINISH, ResultBase.class));

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

            ImageLoaderUtil.displayImage(item.getImagePath(), iv);

            if (item.isScaned()) {
                ivScan.setVisibility(View.VISIBLE);
            } else {
                ivScan.setVisibility(View.GONE);
            }

            setClickEvent(isNew, position, iv, ivScan);
        }
    }

    @Override
    public void onItemClick(Object item, View view, int position) {
        // 列表衣物图片
        if (item instanceof Clothes) {
            DialogUtil.showPictureDialog(this, ((Clothes) item).getImagePath());
        }

        // 上传图片
        else if (item instanceof ClothingPic) {
            ClothingPic pic = (ClothingPic) item;
            // 添加相片按钮
            if (AddPhotoAdapter.ADD_IMG_ID.equals(pic.getImgId())) {
                if (takePhotoPop == null) {
                    takePhotoPop = new TakePhotoPopView(this, Constants
                            .TAKE_PIC_RESULT, Constants.CHOSE_PIC_RESULT);
                }
                takePhotoPop.getPic(view);
            }

            // 已上传图片
            else {
                switch (view.getId()) {
                    //  删除图片（本地删除）
                    case R.id.iv_delete:
                        showWaitDialog();
                        abnormalImgs.remove(position);
                        if (!AddPhotoAdapter.ADD_IMG_ID.equals(
                                abnormalImgs.get(abnormalImgs.size() - 1).getImgId())) {
                            addAddImage();
                        }
                        imgAdapter.notifyDataSetChanged();
                        break;
                    case R.id.iv_pic:
                        DialogUtil.showPictureDialog(this, pic.getImgPath());
                    default:
                        break;
                }
            }
        }
    }

}