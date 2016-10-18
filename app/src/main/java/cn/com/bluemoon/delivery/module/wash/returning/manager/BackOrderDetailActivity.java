package cn.com.bluemoon.delivery.module.wash.returning.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.common.photopicker.PhotoPreviewActivity;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.app.api.model.wash.manager.ResultBackOrder;
import cn.com.bluemoon.delivery.app.api.model.wash.manager.ResultBackOrderDetail;
import cn.com.bluemoon.delivery.ui.ImageGridView;
import cn.com.bluemoon.delivery.ui.UpDownTextView;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.PublicUtil;
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
    @Bind(R.id.layout_buyer_msg)
    LinearLayout layoutBuyerMsg;
    @Bind(R.id.layout_open1)
    LinearLayout layoutOpen1;
    @Bind(R.id.layout_open2)
    LinearLayout layoutOpen2;
    @Bind(R.id.layout_open3)
    LinearLayout layoutOpen3;
    private String backOrderCode;
    private boolean isHistory;
    private String name;
    private String phone;
    private String address;
    private boolean isUrgent;
    private List<ResultBackOrderDetail.RefuseListBean> refuseList;

    public static void actStart(Activity activity, ResultBackOrder.BackOrderListBean result, boolean isHistory) {
        Intent intent = new Intent(activity, BackOrderDetailActivity.class);
        intent.putExtra("backOrderCode", result.getBackOrderCode());
        intent.putExtra("name", result.getCustomerName());
        intent.putExtra("phone", result.getCustomerPhone());
        intent.putExtra("address", result.getCustomerAddress());
        intent.putExtra("isUrgent", result.isIsUrgent());
        intent.putExtra("isHistory", true);
        activity.startActivity(intent);
    }


    @Override
    protected String getTitleString() {
        return getString(R.string.manage_return_clothes_detial_title);
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
                            RefuseAdapter adapter = new RefuseAdapter(BackOrderDetailActivity.this, null);
                            adapter.setList(refuseList);
                            listviewRefuse.setAdapter(adapter);
                            LibViewUtil.setListViewHeight2(listviewRefuse);
                            listviewRefuse.setFocusable(true);
                            listviewRefuse.setFocusableInTouchMode(true);
                            listviewRefuse.requestFocus();

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
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view == layoutOpen1) {
                    txtOpen1.changeStatus();
                } else if (view == layoutOpen2) {
                    txtOpen2.changeStatus();
                }else if (view == layoutOpen3) {
                    txtOpen3.changeStatus();
                }
            }
        };
        layoutOpen1.setOnClickListener(clickListener);
        layoutOpen2.setOnClickListener(clickListener);
        layoutOpen3.setOnClickListener(clickListener);
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
        final ResultBackOrderDetail r = (ResultBackOrderDetail) result;
        txtNo.setText(getString(R.string.manage_clothes_code, backOrderCode));
        txtCustomerName.setText(name);
        txtMobilePhone.setText(phone);
        txtMobilePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PublicUtil.showCallPhoneDialog2(BackOrderDetailActivity.this, phone);
            }
        });
        txtAddress.setText(address);
        txtMobilePhone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        txtMobilePhone.getPaint().setAntiAlias(true);
        if (isUrgent) {
            txtUrgent.setVisibility(View.VISIBLE);
        } else {
            txtUrgent.setVisibility(View.GONE);
        }
        txtCount.setText(getString(R.string.total_amount, r.getClothesList().size()));
        if (StringUtils.isNotBlank(r.getBuyerMessage())) {
            txtRemark.setText(r.getBuyerMessage());
        } else {
            layoutBuyerMsg.setVisibility(View.GONE);
        }

        ClothesAdapter clothesAdapter = new ClothesAdapter(BackOrderDetailActivity.this, null);
        clothesAdapter.setList(r.getClothesList());
        listviewClothes.setAdapter(clothesAdapter);
        LibViewUtil.setListViewHeight2(listviewClothes);

        if (isHistory && StringUtils.isNotBlank(r.getSignName())){
            layoutSignRefuse.setVisibility(View.VISIBLE);
            txtType.setText(getString(R.string.manage_sign_type, r.getSignName()));
            txtTime.setText(getString(R.string.manage_sign_time, DateUtil.getTime(r.getSignTime(), "yyyy-MMdd HH:mm:ss")));
            Glide.with(this).load(r.getSignImagePath()).into(imgSign);
            imgSign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    List<String> imgList = new ArrayList<>();
                    imgList.add(r.getSignImagePath());
                    PhotoPreviewActivity.actStart(BackOrderDetailActivity.this, imgList, 1);
                }
            });

            List<ResultBackOrderDetail.RefuseListBean> list = r.getRefuseList();
            if (list != null && !list.isEmpty()) {
                layoutRefuse.setVisibility(View.VISIBLE);
                refuseList = list;
            }
        }
    }


    class ClothesAdapter extends BaseListAdapter<ResultBackOrderDetail.ClothesListBean> {
        private Context mContext;

        public ClothesAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
            mContext = context;
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
            Glide.with(mContext).load(r.getImgPath()).into(imgClothes);
            txtCode.setText(r.getClothesCode());
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
            txtCode.setText(getString(R.string.manage_clothes_no,r.getRefuseClothesCode()));
            txtTime.setText(getString(R.string.manage_refuse_time2, DateUtil.getTime(r.getRefuseTagTime(), "yyyy-MM-dd HH:mm:ss")));
            txtReason.setText(r.getRefuseIssueDesc());
            gridviewImg.loadAdpater(r.getRefuseImagePaths(), false);

        }
    }
}
