package cn.com.bluemoon.delivery.module.storage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.storage.MallStoreRecieverAddress;
import cn.com.bluemoon.delivery.app.api.model.storage.ResultMallStoreRecieverAddress;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.common.SelectAddressActivity;
import cn.com.bluemoon.delivery.entity.SubRegion;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.MaxLengthWatcher;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.lib.utils.LibPublicUtil;
import cn.com.bluemoon.lib.view.ClearEditText;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

/**
 * Created by allenli on 2016/3/30.
 */
public class WarehouseAddressEditActivity extends Activity {


    private String TAG = "WarehouseAddressEditActivity";
    private String storeId;
    private String storeName;
    private boolean isEdit;
    private WarehouseAddressEditActivity main;

    private MallStoreRecieverAddress address;

    private TextView textStoreId;

    private ClearEditText editReceiver;

    private ClearEditText editUserPhone;

    private ClearEditText editFloor;

    private CheckBox cbIsLift;

    private CheckBox cbIsDefault;

    private RelativeLayout regiogLayout;
    private TextView txtRegion;

    private RelativeLayout townLayout;
    private TextView txtTown;

    private RelativeLayout villageLayout;

    private TextView txtVillage;

    private Button btnSubmit;

    private ClearEditText editAddress;

    private CommonProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_detail);

        main = this;
        storeId = getIntent().getStringExtra("storeId");
        storeName = getIntent().getStringExtra("storeName");
        isEdit = getIntent().getBooleanExtra("isEdit", false);
        address = (MallStoreRecieverAddress) getIntent().getSerializableExtra("address");


        if (StringUtil.isEmpty(storeId) || StringUtil.isEmpty(storeName) || null == address) {
            finish();
        }
        initCustomActionBar();
        init();
        progressDialog = new CommonProgressDialog(main);
    }

    private void init() {

        textStoreId = (TextView) findViewById(R.id.text_store_id);
        editReceiver = (ClearEditText) findViewById(R.id.edit_receiver);
        editUserPhone = (ClearEditText) findViewById(R.id.edit_user_phone);
        editFloor = (ClearEditText) findViewById(R.id.edit_floor);
        cbIsLift = (CheckBox) findViewById(R.id.cb_is_lift);
        cbIsDefault = (CheckBox) findViewById(R.id.cb_default);
        regiogLayout = (RelativeLayout) findViewById(R.id.region_layout);
        txtRegion = (TextView) findViewById(R.id.txt_region);
        townLayout = (RelativeLayout) findViewById(R.id.town_layout);
        txtTown = (TextView) findViewById(R.id.txt_town);
        villageLayout = (RelativeLayout) findViewById(R.id.village_layout);
        txtVillage = (TextView) findViewById(R.id.txt_village);
        editAddress = (ClearEditText) findViewById(R.id.edit_address);
        btnSubmit = (Button) findViewById(R.id.btn_submit);


        editReceiver.addTextChangedListener(new MaxLengthWatcher(15, getString(R.string.error_input_receiver), editReceiver));
        editUserPhone.addTextChangedListener(new MaxLengthWatcher(11, getString(R.string.error_input_phone), editUserPhone));


        textStoreId.setText(String.format("%s-%s", storeId, storeName));
        txtRegion.setText(String.format("%s %s %s", address.getProvinceName(), address.getCityName(), address.getCountyName()));
        if (isEdit) {

            editReceiver.setText(address.getReceiverName());
            editReceiver.updateCleanable(0, false);


            editUserPhone.setText(address.getReceiverPhone());
            editUserPhone.updateCleanable(0, false);

            editFloor.setText(String.valueOf(address.getFloor()));
            editFloor.updateCleanable(0, false);



            cbIsLift.setChecked(address.getIsLift() != 0);
            cbIsDefault.setChecked(address.getIsDefault() != 0);

            cbIsDefault.setClickable(address.getIsDefault() == 0);

            txtTown.setText(address.getTownName());
            txtVillage.setText(address.getVillageName());
            editAddress.setText(address.getAddress());
            editAddress.updateCleanable(0, false);
        }else{
            address.setTownName("");
            address.setTown("");
            address.setVillageName("");
            address.setVillage("");

        }

        townLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!StringUtil.isEmpty(address.getCounty())) {
                    SelectAddressActivity.actionStart(main, address.getCounty(), "street");
                }
            }
        });

        villageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!StringUtil.isEmpty(address.getTown())) {
                    SelectAddressActivity.actionStart(main, address.getTown(), "village");
                }else{
                    PublicUtil.showToast(main,getString(R.string.error_town_select));
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

    private void dataSubmit() {

        if (StringUtil.isEmpty(editReceiver.getText().toString()) || StringUtil.isEmpty(editUserPhone.getText().toString())
                || StringUtil.isEmpty(editUserPhone.getText().toString()) || StringUtil.isEmpty(editFloor.getText().toString())
                || StringUtil.isEmpty(txtTown.getText().toString()) || StringUtil.isEmpty(txtVillage.getText().toString())
                || StringUtil.isEmpty(editAddress.getText().toString())) {
            LibPublicUtil.showToast(main, getString(R.string.error_input_address));
            return;
        } else if (editUserPhone.getText().toString().trim().length() != 11) {
            LibPublicUtil.showToast(main, getString(R.string.error_message_input_phone));
            return;
        }

        String token = ClientStateManager.getLoginToken(main);
        if (progressDialog != null) {
            progressDialog.show();
        }

        address.setReceiverName(editReceiver.getText().toString().trim());
        address.setReceiverPhone(editUserPhone.getText().toString().trim());
        address.setAddress(editAddress.getText().toString().trim());
        address.setFloor(Integer.valueOf(editFloor.getText().toString().trim()));
        address.setIsLift(cbIsLift.isChecked() ? 1 : 0);
        address.setIsDefault(cbIsDefault.isChecked() ? 1 : 0);

        if (!isEdit) {
            address.setAddressId(0);
        }
        DeliveryApi.manageReceiveAddress(token, address, addressHandler);

    }


    private void initCustomActionBar() {

        CommonActionBar bar = new CommonActionBar(main.getActionBar(),
                new IActionBarListener() {

                    @Override
                    public void onBtnRight(View v) {

                    }

                    @Override
                    public void onBtnLeft(View v) {
                        // TODO Auto-generated method stub
                        main.finish();
                    }

                    @Override
                    public void setTitle(TextView v) {
                        // TODO Auto-generated method stub
                        if (isEdit) {
                            v.setText(R.string.text_store_title_address_edit);
                        } else {
                            v.setText(R.string.text_store_title_address_new);
                        }

                    }
                });

    }


    AsyncHttpResponseHandler addressHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            LogUtils.d(TAG, "addressDetailHandler result = " + responseString);
            if (progressDialog != null)
                progressDialog.dismiss();
            try {
                ResultMallStoreRecieverAddress addressDetailResult = JSON.parseObject(responseString,
                        ResultMallStoreRecieverAddress.class);
                if (addressDetailResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    main.setResult(RESULT_OK);
                    main.finish();
                } else {
                    PublicUtil.showErrorMsg(main, addressDetailResult);
                }
            } catch (Exception e) {
                LogUtils.e(TAG, e.getMessage());
                PublicUtil.showToastServerBusy();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            LogUtils.e(TAG, throwable.getMessage());
            if (progressDialog != null)
                progressDialog.dismiss();
            PublicUtil.showToastServerOvertime();
        }
    };


    public static void actionStart(Activity context, String storeId, String storeName, boolean isEdit, MallStoreRecieverAddress address) {
        Intent intent = new Intent(context, WarehouseAddressEditActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("isEdit", isEdit);
        bundle.putString("storeId", storeId);
        bundle.putString("storeName", storeName);
        bundle.putSerializable("address", address);
        intent.putExtras(bundle);
        context.startActivityForResult(intent, 0);
    }

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
