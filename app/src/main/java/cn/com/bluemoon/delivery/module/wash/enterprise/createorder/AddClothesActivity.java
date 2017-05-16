package cn.com.bluemoon.delivery.module.wash.enterprise.createorder;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

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
import cn.com.bluemoon.delivery.module.wash.enterprise.event.ClothesChangedEvent;
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
    @Bind(R.id.txt_collect_num)
    TextView txtCollectNum;
    @Bind(R.id.lv_clothes)
    NoScrollListView lvClothes;
    @Bind(R.id.div_list)
    View divList;
    @Bind(R.id.et_backup)
    EditText etBackup;

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
        Intent intent = new Intent(context, AddClothesActivity.class);
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
        etBackup.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etBackup.getLineCount() > 1) {
                    etBackup.setGravity(Gravity.START);
                } else {
                    etBackup.setGravity(Gravity.END);
                }
            }
        });

        divList.setVisibility(View.GONE);
        btnSend.setEnabled(false);

        list = new ArrayList<>();
        adapter = new ItemAdapter(this, this);
        adapter.setList(list);
        lvClothes.setAdapter(adapter);
        isClothesChanged = false;
        if (info != null) {
            isInited = true;
            curRemark = info.enterpriseOrderInfo.remark;
            setData(info);
        }
    }

    private void setData(ResultGetWashEnterpriseScan info) {
        if (info == null || info.enterpriseOrderInfo == null || info.employeeInfo == null) {
            return;
        }
        txtOrderCode.setText(info.enterpriseOrderInfo.outerCode);
        txtCollectBag.setText(info.enterpriseOrderInfo.collectBrcode);
        etBackup.setText(info.enterpriseOrderInfo.remark);

        if (info.enterpriseOrderInfo.clothesDetails != null) {
            list.clear();
            list.addAll(info.enterpriseOrderInfo.clothesDetails);
            refreshClothesData();
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
            isInited = false;
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

    /**
     * 手动搜索等入口进来的查询信息是否初始化完
     */
    private boolean isInited = false;

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        switch (requestCode) {
            // 查询数据
            case REQUEST_CODE_QUERY:
                setData((ResultEnterpriseDetail) result);
                break;

            // 点击删除衣物
            case REQUEST_CODE_DELETE:
                if (result.isSuccess) {
                    isClothesChanged = true;
                    list.remove(deletePos);
                    refreshClothesData();
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

        if (!isInited) {
            isInited = true;
            curRemark = info.enterpriseOrderInfo.remark;
            etBackup.setText(info.enterpriseOrderInfo.remark);
        } else {
            etBackup.setText(curRemark);
        }

        if (info.enterpriseOrderInfo.clothesDetails != null) {
            list.clear();
            list.addAll(info.enterpriseOrderInfo.clothesDetails);
            refreshClothesData();
        } else {
            divList.setVisibility(View.GONE);
            btnSend.setEnabled(false);
        }

        txtName.setText(info.employeeInfo.employeeName);
        txtNameCode.setText(info.employeeInfo.employeeCode);
        txtPhone.setText(info.employeeInfo.employeePhone);
        txtAddress.setText(info.employeeInfo.branchName);
    }

    @OnClick({R.id.btn_send, R.id.ll_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            // 添加衣物
            case R.id.ll_add:
                SelectClothesTypeActivity.actionStart(this, outerCode, 0x66);
                break;
            // 提交扣款
            case R.id.btn_send:
                curRemark = etBackup.getText().toString();
                EnterpriseConfirmOrderActivity.actionStart(this, outerCode, curRemark, 0x77);
                break;
        }
    }

    private String curRemark;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 提交扣款
        if (requestCode == 0x77) {
            if (resultCode == EnterpriseConfirmOrderActivity.RESULT_CANCEL_CONFIRM) {
                showWaitDialog();
                refreshData(outerCode);
            } else {
                finish();
            }

            return;
        }

        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            // 添加衣物成功
            case 0x66:
                ClothesInfo clothes = (ClothesInfo) data.getSerializableExtra
                        (SelectClothesTypeActivity.EXTRA_CLOTHES);
                if (clothes != null) {
                    isClothesChanged = true;
                    list.add(clothes);
                    refreshClothesData();
                }
                break;
        }
    }

    /**
     * 刷新衣物数据
     */
    private void refreshClothesData() {
        txtCollectNum.setText(getString(R.string.clothes_total_amount,
                list.size()));
        if (list.size() > 0) {
            divList.setVisibility(View.VISIBLE);
            btnSend.setEnabled(true);
        } else {
            divList.setVisibility(View.GONE);
            btnSend.setEnabled(false);
        }
        adapter.notifyDataSetChanged();
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

    /**
     * 衣物发生变更，通知主页刷新
     */
    private boolean isClothesChanged = false;

    @Override
    public void finish() {
        if (isClothesChanged) {
            EventBus.getDefault().post(new ClothesChangedEvent());
        }
        super.finish();
    }
}
