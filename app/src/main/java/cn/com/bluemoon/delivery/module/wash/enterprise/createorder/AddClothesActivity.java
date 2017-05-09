package cn.com.bluemoon.delivery.module.wash.enterprise.createorder;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.EnterpriseApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.enterprise.ClothesInfo;
import cn.com.bluemoon.delivery.app.api.model.wash.enterprise.ResultEnterpriseDetail;
import cn.com.bluemoon.delivery.app.api.model.wash.enterprise.ResultGetWashEnterpriseScan;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.wash.enterprise.event.ConfirmEvent;
import cn.com.bluemoon.delivery.ui.NoScrollListView;

/**
 * 添加衣物页面
 */
public class AddClothesActivity extends BaseActivity implements OnListItemClickListener {
    private static final String EXTRA_OUTER_CODE = "EXTRA_OUTER_CODE";
    private static final String EXTRA_INFO = "EXTRA_INFO";
    private static final int REQUEST_CODE_QUERY = 0x777;
    private static final int REQUEST_CODE_DELETE = 0x666;
    @Bind(R.id.txt_order_code)
    TextView txtOrderCode;
    @Bind(R.id.txt_name)
    TextView txtName;
    @Bind(R.id.txt_name_code)
    TextView txtNameCode;
    @Bind(R.id.txt_phone)
    TextView txtPhone;
    @Bind(R.id.txt_address)
    TextView txtAddress;
    @Bind(R.id.txt_collect_bag)
    TextView txtCollectBag;
    @Bind(R.id.txt_collect_remark)
    TextView txtCollectRemark;
    @Bind(R.id.txt_collect_num)
    TextView txtCollectNum;
    @Bind(R.id.ic_add)
    ImageView icAdd;
    @Bind(R.id.lv_clothes)
    NoScrollListView lvClothes;
    @Bind(R.id.div_list)
    View divList;

    @Bind(R.id.btn_send)
    Button btnSend;

    private String outerCode;
    private ResultGetWashEnterpriseScan info;

    /**
     * 首页列表、创建订单、手动搜索入口
     *
     * @param outerCode 洗衣订单编码
     */
    public static void actionStart(Context context, String outerCode) {
        Intent intent = new Intent(context, AddClothesActivity.class);
        intent.putExtra(EXTRA_OUTER_CODE, outerCode);
        context.startActivity(intent);
    }

    /**
     * 扫一扫入口
     */
    public static void actionStart(Context context, ResultGetWashEnterpriseScan info) {
        Intent intent = new Intent(context, CreateOrderActivity.class);
        intent.putExtra(EXTRA_INFO, info);
        context.startActivity(intent);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        outerCode = getIntent().getStringExtra(EXTRA_OUTER_CODE);
        info = (ResultGetWashEnterpriseScan) getIntent().getSerializableExtra(EXTRA_INFO);
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.add_clothes);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_clothes;
    }

    @Override
    public void initView() {
        divList.setVisibility(View.GONE);
        btnSend.setEnabled(false);

        list = new ArrayList<>();
        adapter = new ItemAdapter(this, this);
        adapter.setList(list);
        lvClothes.setAdapter(adapter);

        if (info != null) {
            setData(info);
        }
    }

    private void setData(ResultGetWashEnterpriseScan info) {
        if (info == null || info.enterpriseOrderInfo == null || info.employeeInfo == null) {
            return;
        }
        txtOrderCode.setText(info.enterpriseOrderInfo.outerCode);
        txtCollectBag.setText(info.enterpriseOrderInfo.collectBrcode);
        if (TextUtils.isEmpty(info.enterpriseOrderInfo.remark)) {
            txtCollectRemark.setText(getString(R.string.promote_none));
        } else {
            txtCollectRemark.setText(info.enterpriseOrderInfo.remark);
        }

        if (info.enterpriseOrderInfo.clothesDetails != null) {
            txtCollectNum.setText(getString(R.string.clothes_total_amount,
                    info.enterpriseOrderInfo.actualCount));

            list.clear();
            list.addAll(info.enterpriseOrderInfo.clothesDetails);
            adapter.notifyDataSetChanged();

            if (list.size() > 0) {
                divList.setVisibility(View.VISIBLE);
                btnSend.setEnabled(true);
            } else {
                divList.setVisibility(View.GONE);
                btnSend.setEnabled(false);
            }
        } else {
            divList.setVisibility(View.GONE);
            btnSend.setEnabled(false);
        }

        txtName.setText(info.employeeInfo.employeeName);
        txtNameCode.setText(info.employeeInfo.employeeCode);
        txtPhone.setText(info.employeeInfo.employeePhone);
        txtAddress.setText(info.employeeInfo.branchName);

        outerCode = info.enterpriseOrderInfo.outerCode;
    }

    @Override
    public void initData() {
        // 手动搜索等入口进来的查询信息
        if (info == null && outerCode != null) {
            showWaitDialog();
            refreshData(outerCode);
        }
    }

    /**
     * 刷新数据用这个
     */
    private void refreshData(String outerCode) {
        EnterpriseApi.getWashEnterpriseDetail(outerCode, getToken(),
                getNewHandler(REQUEST_CODE_QUERY, ResultEnterpriseDetail.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        switch (requestCode) {
            // 查询数据
            case REQUEST_CODE_QUERY:
                setData((ResultEnterpriseDetail) result);
                break;
            //                    // 点击保存
            //                    case REQUEST_CODE_SAVE:
            //                        ResultSaveWashEnterpriseOrder order0 =
            // (ResultSaveWashEnterpriseOrder)
            //         result;
            //                        EventBus.getDefault().post(new CreateOrderEvent
            // (order0.outerCode));
            //                        finish();
            //                        break;
            // 点击删除衣物
            case REQUEST_CODE_DELETE:
                if (result.isSuccess) {
                    list.remove(deletePos);
                    adapter.notifyDataSetChanged();

                    if (list.size() > 0) {
                        divList.setVisibility(View.VISIBLE);
                        btnSend.setEnabled(true);
                    } else {
                        divList.setVisibility(View.GONE);
                        btnSend.setEnabled(false);
                    }
                }
                break;

        }
    }

    private List<ClothesInfo> list;
    private ItemAdapter adapter;

    /**
     * 刷新数据返回，设置数据
     */
    private void setData(ResultEnterpriseDetail info) {
        if (info == null || info.enterpriseOrderInfo == null || info.employeeInfo == null) {
            return;
        }
        txtOrderCode.setText(info.enterpriseOrderInfo.outerCode);
        txtCollectBag.setText(info.enterpriseOrderInfo.collectBrcode);
        if (TextUtils.isEmpty(info.enterpriseOrderInfo.remark)) {
            txtCollectRemark.setText(getString(R.string.promote_none));
        } else {
            txtCollectRemark.setText(info.enterpriseOrderInfo.remark);
        }

        if (info.enterpriseOrderInfo.clothesDetails != null) {
            txtCollectNum.setText(getString(R.string.clothes_total_amount,
                    info.enterpriseOrderInfo.actualCount));

            list.clear();
            list.addAll(info.enterpriseOrderInfo.clothesDetails);
            adapter.notifyDataSetChanged();

            if (list.size() > 0) {
                divList.setVisibility(View.VISIBLE);
                btnSend.setEnabled(true);
            } else {
                divList.setVisibility(View.GONE);
                btnSend.setEnabled(false);
            }
        } else {
            divList.setVisibility(View.GONE);
            btnSend.setEnabled(false);
        }

        txtName.setText(info.employeeInfo.employeeName);
        txtNameCode.setText(info.employeeInfo.employeeCode);
        txtPhone.setText(info.employeeInfo.employeePhone);
        txtAddress.setText(info.employeeInfo.branchName);
    }

    // TODO: lk 2017/5/8
    @OnClick({R.id.btn_send, R.id.ic_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            // 添加衣物
            case R.id.ic_add:
                toast("添加衣物");
                break;
            // 提交扣款
            case R.id.btn_send:
                EnterpriseConfirmOrderActivity.actionStart(this, outerCode, 0x77);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        showWaitDialog();
        refreshData(outerCode);
    }

    class ItemAdapter extends BaseListAdapter<ClothesInfo> {

        public ItemAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_enterprise_clothes_add;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            ImageView img = getViewById(R.id.img_cloth);
            TextView clothName = getViewById(R.id.txt_cloth_name);
            LinearLayout llDelete = getViewById(R.id.ll_delete);

            Glide.with(context)
                    .load(list.get(position).imgPath)
                    .placeholder(R.mipmap.loading_img_logo)
                    .error(R.mipmap.place_holder)
                    .into(img);

            clothName.setText(list.get(position).washName);
            setClickEvent(isNew, position, llDelete);
        }
    }

    private int deletePos;

    @Override
    public void onItemClick(Object item, View view, int position) {
        ClothesInfo cf = (ClothesInfo) item;
        if (cf != null) {
            showWaitDialog();
            deletePos = position;
            EnterpriseApi.deleteClothes(cf.clothesId, getToken(),
                    getNewHandler(REQUEST_CODE_DELETE, ResultBase.class));
        }
    }

    @Override
    protected boolean isUseEventBus() {
        return true;
    }

    // 提交扣款成功，结束页面
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ConfirmEvent event) {
        finish();
    }
}
