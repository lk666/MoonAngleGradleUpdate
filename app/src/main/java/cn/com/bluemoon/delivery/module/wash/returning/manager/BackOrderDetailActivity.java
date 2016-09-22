package cn.com.bluemoon.delivery.module.wash.returning.manager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.kymjs.kjframe.KJBitmap;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.wash.returning.manager.model.ResultBackOrderDetail;
import cn.com.bluemoon.delivery.ui.UpDownTextView;
import cn.com.bluemoon.lib.utils.LibViewUtil;

/**
 * Created by ljl on 2016/9/21.
 */
public class BackOrderDetailActivity extends BaseActivity {
    @Bind(R.id.txt_no)
    TextView txtNo;
    @Bind(R.id.txt_customerName)
    TextView txtCustomerName;
    @Bind(R.id.txt_mobilePhone)
    TextView txtMobilePhone;
    @Bind(R.id.txt_urgent)
    TextView txtUrgent;
    @Bind(R.id.txt_address)
    TextView txtAddress;
    @Bind(R.id.txt_count)
    TextView txtCount;
    @Bind(R.id.txt_open1)
    UpDownTextView txtOpen1;
    @Bind(R.id.listview_clothes)
    ListView listviewClothes;
    @Bind(R.id.txt_remark)
    TextView txtRemark;
    private String backOrderCode;
    private String name;
    private String phone;
    private String address;
    private boolean isUrgent;
    KJBitmap kj = new KJBitmap();


    @Override
    protected String getTitleString() {
        return "还衣单详情";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_back_order_detail;
    }

    @Override
    public void initView() {
        showWaitDialog();
        backOrderCode = getIntent().getStringExtra("backOrderCode");
        UpDownTextView.ClickListener listener = new UpDownTextView.ClickListener() {
            @Override
            public void onClick(boolean clicked, int index) {
                if (clicked) {
                    listviewClothes.setVisibility(View.VISIBLE);
                } else {
                    listviewClothes.setVisibility(View.GONE);
                }
            }
        };
        txtOpen1.setListener(listener, 1);
        name = getIntent().getStringExtra("name");
        phone = getIntent().getStringExtra("phone");
        address = getIntent().getStringExtra("address");
        isUrgent = getIntent().getBooleanExtra("isUrgent", false);
        ReturningApi.queryBackOrderDetail(backOrderCode, getToken(), getNewHandler(1, ResultBackOrderDetail.class));
    }

    @Override
    public void initData() {

    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        ResultBackOrderDetail r = (ResultBackOrderDetail) result;
        txtNo.setText("还衣单号：" + backOrderCode);
        txtCustomerName.setText(name);
        txtMobilePhone.setText(phone);
        txtAddress.setText(address);
        txtMobilePhone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        txtMobilePhone.getPaint().setAntiAlias(true);
        if (isUrgent) {
            txtUrgent.setVisibility(View.VISIBLE);
        } else {
            txtUrgent.setVisibility(View.GONE);
        }
        txtCount.setText(getString(R.string.total_amount, r.getClothesList().size()));
        ClothesAdapter adapter = new ClothesAdapter(this, null);
        adapter.setList(r.getClothesList());
        listviewClothes.setAdapter(adapter);
        LibViewUtil.setListViewHeight2(listviewClothes);
        txtRemark.setText(r.getBuyerMessage());
    }


    class ClothesAdapter extends BaseListAdapter<ResultBackOrderDetail.ClothesListBean> {


        public ClothesAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.list_item_clothes;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {

            final ResultBackOrderDetail.ClothesListBean r = list.get(position);
            TextView txtCode = getViewById(R.id.txt_code);
            TextView txtType = getViewById(R.id.txt_type);
            TextView txtName = getViewById(R.id.txt_name);
            ImageView imgClothes = getViewById(R.id.img_clothes);
            kj.display(imgClothes, r.getImgPath());
            txtCode.setText(r.getClothesName());
            txtType.setText(r.getTypeName());
            txtName.setText(r.getClothesName());

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BackOrderDetailActivity.this, ClothesDetailActivity.class);
                    intent.putExtra("clothesCode", r.getClothesCode());
                    startActivity(intent);
                }
            });
        }
    }
}
