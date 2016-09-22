package cn.com.bluemoon.delivery.module.wash.returning.manager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.kymjs.kjframe.KJBitmap;

import java.util.List;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.wash.returning.manager.model.ResultBackOrderDetail;
import cn.com.bluemoon.delivery.ui.ImageGridView;
import cn.com.bluemoon.delivery.ui.UpDownTextView;
import cn.com.bluemoon.delivery.utils.DateUtil;
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
    @Bind(R.id.txt_open2)
    UpDownTextView txtOpen2;
    @Bind(R.id.layout_sign_refuse)
    LinearLayout layoutSignRefuse;
    @Bind(R.id.layout_sign_info)
    LinearLayout layoutSignInfo;
    @Bind(R.id.txt_type)
    TextView txtType;
    @Bind(R.id.txt_time)
    TextView txtTime;
    @Bind(R.id.img_sign)
    ImageView imgSign;
    @Bind(R.id.txt_open3)
    UpDownTextView txtOpen3;
    @Bind(R.id.listview_refuse)
    ListView listviewRefuse;
    @Bind(R.id.layout_refuse)
    LinearLayout layoutRefuse;
    private String backOrderCode;
    private boolean isHistory;
    private String name;
    private String phone;
    private String address;
    private boolean isUrgent;
    KJBitmap kj = new KJBitmap();
    private List<ResultBackOrderDetail.RefuseListBean> refuseList;


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
        isHistory = getIntent().getBooleanExtra("isHistory", false);
        UpDownTextView.ClickListener listener = new UpDownTextView.ClickListener() {
            @Override
            public void onClick(boolean clicked, int index) {
                switch (index) {
                    case 1:
                        if (clicked) {
                            listviewClothes.setVisibility(View.VISIBLE);
                        } else {
                            listviewClothes.setVisibility(View.GONE);
                        }
                        break;
                    case 2:
                        if (clicked) {
                            layoutSignInfo.setVisibility(View.VISIBLE);
                            layoutSignInfo.setFocusable(true);
                            layoutSignInfo.setFocusableInTouchMode(true);
                            layoutSignInfo.requestFocus();
                        } else {
                            layoutSignInfo.setVisibility(View.GONE);
                        }
                        break;
                    case 3:
                        if (clicked) {
                            listviewRefuse.setVisibility(View.VISIBLE);
                            listviewRefuse.setFocusable(true);
                            listviewRefuse.setFocusableInTouchMode(true);
                            listviewRefuse.requestFocus();
                            RefuseAdapter adapter = new RefuseAdapter(BackOrderDetailActivity.this, null);
                            adapter.setList(refuseList);
                            listviewRefuse.setAdapter(adapter);
                            LibViewUtil.setListViewHeight2(listviewRefuse);
                        } else {
                            listviewRefuse.setVisibility(View.GONE);
                        }
                        break;
                }

            }
        };
        txtOpen1.setListener(listener, 1);
        txtOpen2.setListener(listener, 2);
        txtOpen3.setListener(listener, 3);
        name = getIntent().getStringExtra("name");
        phone = getIntent().getStringExtra("phone");
        address = getIntent().getStringExtra("address");
        isUrgent = getIntent().getBooleanExtra("isUrgent", false);
        if (isHistory) {
            ReturningApi.returnClothesHistoryList(backOrderCode, getToken(), getNewHandler(1, ResultBackOrderDetail.class));
        } else {
            ReturningApi.queryBackOrderDetail(backOrderCode, getToken(), getNewHandler(1, ResultBackOrderDetail.class));
        }
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
        txtRemark.setText(r.getBuyerMessage());

        ClothesAdapter clothesAdapter = new ClothesAdapter(BackOrderDetailActivity.this, null);
        clothesAdapter.setList(r.getClothesList());
        listviewClothes.setAdapter(clothesAdapter);
        LibViewUtil.setListViewHeight2(listviewClothes);

        if (isHistory){
            layoutSignRefuse.setVisibility(View.VISIBLE);
            txtType.setText("签收方式：" + r.getSignName());
            txtTime.setText("签收时间：" + DateUtil.getTime(r.getSignTime(), "yyyy-MMdd HH:mm:ss"));
            kj.display(imgSign, r.getSignImagePath());

            List<ResultBackOrderDetail.RefuseListBean> list = r.getRefuseList();
            if (list != null && !list.isEmpty()) {
                layoutRefuse.setVisibility(View.VISIBLE);
                refuseList = list;
            }
        }
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

    class RefuseAdapter extends BaseListAdapter<ResultBackOrderDetail.RefuseListBean> {


        public RefuseAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.list_item_refuse_info;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {

            final ResultBackOrderDetail.RefuseListBean r = list.get(position);
            TextView txtCode = getViewById(R.id.txt_code);
            TextView txtTime = getViewById(R.id.txt_time);
            TextView txtReason = getViewById(R.id.txt_reason);
            ImageGridView gridviewImg = getViewById(R.id.gridview_img);
            txtCode.setText("衣物编码：" + r.getRefuseClothesCode());
            txtTime.setText("签收时间"+DateUtil.getTime(r.getRefuseTagTime(), "yyyy-MM-dd HH:mm:ss"));
            txtReason.setText(r.getRefuseIssueDesc());
            gridviewImg.setList(r.getRefuseImagePaths());

        }
    }
}
