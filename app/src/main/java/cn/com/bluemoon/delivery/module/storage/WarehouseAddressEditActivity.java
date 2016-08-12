package cn.com.bluemoon.delivery.module.storage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.storage.MallStoreRecieverAddress;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.common.SelectAddressActivity;
import cn.com.bluemoon.delivery.entity.SubRegion;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.delivery.utils.TextWatcherUtils;
import cn.com.bluemoon.lib.utils.LibPublicUtil;
import cn.com.bluemoon.lib.utils.LibViewUtil;
import cn.com.bluemoon.lib.view.ClearEditText;

/**
 * Created by allenli on 2016/3/30.
 */
public class WarehouseAddressEditActivity extends BaseActivity {


    @Bind(R.id.txt_store)
    TextView txtStore;
    @Bind(R.id.text_store_id)
    TextView textStoreId;
    @Bind(R.id.edit_receiver)
    ClearEditText editReceiver;
    @Bind(R.id.edit_user_phone)
    ClearEditText editUserPhone;
    @Bind(R.id.cb_is_lift)
    CheckBox cbIsLift;
    @Bind(R.id.edit_floor)
    ClearEditText editFloor;
    @Bind(R.id.text_title)
    TextView textTitle;
    @Bind(R.id.txt_region)
    TextView txtRegion;
    @Bind(R.id.region_layout)
    RelativeLayout regiogLayout;
    @Bind(R.id.text_town_title)
    TextView textTownTitle;
    @Bind(R.id.txt_town)
    TextView txtTown;
    @Bind(R.id.town_layout)
    RelativeLayout townLayout;
    @Bind(R.id.text_village_title)
    TextView txtVillageTitle;
    @Bind(R.id.txt_village)
    TextView txtVillage;
    @Bind(R.id.village_layout)
    RelativeLayout villageLayout;
    @Bind(R.id.edit_address)
    ClearEditText editAddress;
    @Bind(R.id.cb_default)
    CheckBox cbIsDefault;
    @Bind(R.id.btn_submit)
    Button btnSubmit;
    @Bind(R.id.v_disable_cb_default)
    View vDisableCbDefault;
    private String storeId;
    private String storeName;
    private boolean isEdit;
    private MallStoreRecieverAddress address;
    private WarehouseAddressEditActivity main;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_address_detail;
    }

    @Override
    protected void onBeforeSetContentLayout() {
        isEdit = getIntent().getBooleanExtra("isEdit", false);
    }

    @Override
    protected String getTitleString() {
        if (isEdit) {
            return getString(R.string.text_store_title_address_edit);
        } else {
            return getString(R.string.text_store_title_address_new);
        }
    }

    @Override
    public void initView() {
        main = this;
        storeId = getIntent().getStringExtra("storeId");
        storeName = getIntent().getStringExtra("storeName");
        address = (MallStoreRecieverAddress) getIntent().getSerializableExtra("address");

        if (StringUtil.isEmpty(storeId) || StringUtil.isEmpty(storeName) || null == address) {
            finish();
        }
        TextWatcherUtils.setMaxLengthWatcher(editReceiver, 15, getString(R.string
                .error_input_receiver));
        TextWatcherUtils.setMaxLengthWatcher(editUserPhone, 11, getString(R.string
                .error_input_phone));

        townLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!StringUtil.isEmpty(address.getCounty())) {
                    SelectAddressActivity.actionStart(main, address.getCounty(), "street");
                }
            }
        });

        villageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!StringUtil.isEmpty(address.getTown())) {
                    SelectAddressActivity.actionStart(main, address.getTown(), "village");
                } else {
                    PublicUtil.showToast(main, getString(R.string.error_town_select));
                }
            }
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataSubmit();
            }
        });
    }

    @Override
    public void initData() {
        textStoreId.setText(String.format("%s-%s", storeId, storeName));
        txtRegion.setText(String.format("%s %s %s", address.getProvinceName(), address
                .getCityName(), address.getCountyName()));
        if (isEdit) {
            editReceiver.setText(address.getReceiverName());
            editReceiver.updateCleanable(0, false);
            editUserPhone.setText(address.getReceiverPhone());
            editUserPhone.updateCleanable(0, false);
            editFloor.setText(String.valueOf(address.getFloor()));
            editFloor.updateCleanable(0, false);
            cbIsLift.setChecked(address.getIsLift() != 0);

            boolean isDefault = address.getIsDefault() != 0;
            if (isDefault) {
                cbIsDefault.setChecked(true);
                cbIsDefault.setClickable(false);
                LibViewUtil.setViewVisibility(vDisableCbDefault, View.VISIBLE);
            } else {
                cbIsDefault.setChecked(false);
                cbIsDefault.setClickable(true);
                LibViewUtil.setViewVisibility(vDisableCbDefault, View.GONE);
            }

            txtTown.setText(address.getTownName());
            txtVillage.setText(address.getVillageName());
            editAddress.setText(address.getAddress());
            editAddress.updateCleanable(0, false);
        } else {
            address.setTownName("");
            address.setTown("");
            address.setVillageName("");
            address.setVillage("");
        }
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        if (requestCode == 1) {
            main.setResult(RESULT_OK);
            main.finish();
        }
    }

    private void dataSubmit() {

        if (StringUtil.isEmpty(editReceiver.getText().toString()) || StringUtil.isEmpty
                (editUserPhone.getText().toString())
                || StringUtil.isEmpty(editUserPhone.getText().toString()) || StringUtil.isEmpty
                (editFloor.getText().toString())
                || StringUtil.isEmpty(txtTown.getText().toString()) || StringUtil.isEmpty
                (txtVillage.getText().toString())
                || StringUtil.isEmpty(editAddress.getText().toString())) {
            LibPublicUtil.showToast(main, getString(R.string.error_input_address));
            return;
        } else if (editUserPhone.getText().toString().trim().length() != 11) {
            LibPublicUtil.showToast(main, getString(R.string.error_message_input_phone));
            return;
        }

        String token = ClientStateManager.getLoginToken(main);
        showWaitDialog();

        address.setReceiverName(editReceiver.getText().toString().trim());
        address.setReceiverPhone(editUserPhone.getText().toString().trim());
        address.setAddress(editAddress.getText().toString().trim());
        address.setFloor(Integer.valueOf(editFloor.getText().toString().trim()));
        address.setIsLift(cbIsLift.isChecked() ? 1 : 0);
        address.setIsDefault(cbIsDefault.isChecked() ? 1 : 0);

        if (!isEdit) {
            address.setAddressId(0);
        }
        DeliveryApi.manageReceiveAddress(token, address, getNewHandler(1, ResultBase.class));

    }


    public static void actionStart(Activity context, String storeId, String storeName, boolean
            isEdit, MallStoreRecieverAddress address) {
        Intent intent = new Intent(context, WarehouseAddressEditActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("isEdit", isEdit);
        bundle.putString("storeId", storeId);
        bundle.putString("storeName", storeName);
        bundle.putSerializable("address", address);
        intent.putExtras(bundle);
        context.startActivityForResult(intent, 0);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            String type = data.getStringExtra("type");
            List<SubRegion> regions = (List<SubRegion>) data.getSerializableExtra("subRegionList");
            if (type.equals("street")) {
                if (!regions.get(0).getDcode().equals(address.getTown())) {
                    address.setTown(regions.get(0).getDcode());
                    address.setTownName(regions.get(0).getDname());
                    txtTown.setText(regions.get(0).getDname());
                    address.setVillage("");
                    address.setVillageName("");
                    txtVillage.setText("");
                }
            } else if (type.equals("village")) {
                address.setVillage(regions.get(0).getDcode());
                address.setVillageName(regions.get(0).getDname());
                txtVillage.setText(regions.get(0).getDname());
            }

        }
    }
}
