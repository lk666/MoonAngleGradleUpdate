package cn.com.bluemoon.delivery.module.wash.returning.pack;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.kymjs.kjframe.utils.SystemTool;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.pack.ClothesItem;
import cn.com.bluemoon.delivery.app.api.model.wash.pack.ResultBackOrderClothes;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.KJFUtil;

/**
 * Created by allenli on 2016/10/9.
 */
public class BackOrderClothesActivity extends BaseActivity implements
        OnListItemClickListener {

    private static final String EXTRA_CUPBOARD_CODE = "EXTRA_CUPBOARD_CODE";
    private static final int REQUEST_CODE_QUERY_LIST = 0x777;
    private static final int REQUEST_CODE_SCANE_BACK_ORDER = 0x778;

    @Bind(R.id.tv_code_box)
    TextView tvCodeBox;
    @Bind(R.id.tv_back_num)
    TextView tvBackNum;
    @Bind(R.id.lv_back_order)
    ListView lvBackOrder;
    @Bind(R.id.btn_print)
    Button btnPrint;
    private String cupboardCode;

    /**
     * 数据
     */
    private ArrayList<ClothesItem> list = new ArrayList<>();

    private ClothesAdapter adapter;

    public static void actionStart(Context context, String cupboardCode) {
        Intent intent = new Intent(context, BackOrderClothesActivity.class);
        intent.putExtra(EXTRA_CUPBOARD_CODE, cupboardCode);
        context.startActivity(intent);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();

        if (getIntent().hasExtra(EXTRA_CUPBOARD_CODE)) {
            cupboardCode = getIntent().getStringExtra(EXTRA_CUPBOARD_CODE);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_clothes_pack_back_order_list;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.title_packing);
    }

    @Override
    protected void setActionBar(CommonActionBar titleBar) {
        titleBar.getImgLeftView().setVisibility(View.GONE);

        titleBar.getImgRightView().setImageResource(R.mipmap.ic_scan);
        titleBar.getImgRightView().setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActionBarBtnRightClick() {
        ScanBackClothesActivity.actionStart(this, REQUEST_CODE_SCANE_BACK_ORDER,
                cupboardCode, list);
    }

    @Override
    public void initView() {
        btnPrint.setVisibility(View.GONE);
        adapter = new ClothesAdapter(this, this);
        adapter.setList(list);
        lvBackOrder.setAdapter(adapter);
    }

    @Override
    public void initData() {
        showWaitDialog();
        ReturningApi.queryBackOrderClothesList(cupboardCode, getToken(), getNewHandler
                (REQUEST_CODE_QUERY_LIST, ResultBackOrderClothes.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        ResultBackOrderClothes obj = (ResultBackOrderClothes) result;
        setData(obj);
    }

    @Override
    public void onSuccessException(int requestCode, Throwable t) {
        super.onSuccessException(requestCode, t);
        finish();
    }

    @Override
    public void onFailureResponse(int requestCode, Throwable t) {
        super.onFailureResponse(requestCode, t);
        finish();
    }

    @Override
    public void onErrorResponse(int requestCode, ResultBase result) {
        super.onErrorResponse(requestCode, result);
        finish();
    }

    private void setData(ResultBackOrderClothes result) {
        if (result == null || null == result.getClothesList()) {
            return;
        }

        tvCodeBox.setText(String.format(getString(R.string.pack_back_order_clothes_code),
                cupboardCode));
        tvBackNum.setText(String.format(getString(R.string.pack_back_order_clothes_code), result.getClothesList().size()));
        btnPrint.setVisibility(View.GONE);
        list.clear();
        list.addAll(result.getClothesList());
        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.btn_print)
    public void onClick() {
        // 打印封箱条
        ArrayList<String> l = new ArrayList<>();
        for (ClothesItem order : list) {
            l.add(order.getClothesCode());
        }

       // CloseBoxListActivity.actionStart(this, cupboardCode, l);
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //  扫码返回
        if (requestCode == REQUEST_CODE_SCANE_BACK_ORDER) {
            ArrayList<ClothesItem> l = (ArrayList<ClothesItem>) data.getSerializableExtra
                    (ScanBackClothesActivity.EXTRA_LIST);

            list.clear();
            if (l != null) {
                list.addAll(l);
            }
            adapter.notifyDataSetChanged();

            int visibility = View.VISIBLE;
            for (ClothesItem item : list) {
                if (!item.isCheck) {
                    visibility = View.GONE;
                }
            }
            btnPrint.setVisibility(visibility);
        }
    }

    class ClothesAdapter extends BaseListAdapter<ClothesItem> {
        public ClothesAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_back_order_clothes;
        }

        @SuppressLint({"NewApi"})
        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            ClothesItem item = (ClothesItem) getItem(position);

            TextView tvBackCode = getViewById(R.id.tv_back_code);
            ImageView ivScan = getViewById(R.id.iv_scan);

            tvBackCode.setText(item.getClothesCode());
            if (item.isCheck) {
                Drawable drawable = context.getResources().getDrawable(R.mipmap.scaned);
                ivScan.setImageDrawable(drawable);
                Bitmap backDrawable = KJFUtil.getUtil().getKJB().getMemoryCache(item.getClothesImgPath());

                if (null != backDrawable) {
                    if (SystemTool.getSDKVersion() >= 16) {
                        ivScan.setBackground(new BitmapDrawable(ivScan.getResources(), backDrawable));
                    } else {
                        ivScan.setBackgroundDrawable(new BitmapDrawable(ivScan.getResources(), backDrawable));
                    }
                }

            } else {
                KJFUtil.getUtil().getKJB().display(ivScan, item.getClothesImgPath());
            }
        }
    }

    @Override
    public void onItemClick(Object item, View view, int position) {

    }

}