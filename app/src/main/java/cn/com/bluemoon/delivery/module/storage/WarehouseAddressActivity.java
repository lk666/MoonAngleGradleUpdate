package cn.com.bluemoon.delivery.module.storage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.storage.MallStoreRecieverAddress;
import cn.com.bluemoon.delivery.app.api.model.storage.ResultMallStoreRecieverAddress;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.utils.LibPublicUtil;
import cn.com.bluemoon.lib.view.CommonAlertDialog;
import cn.com.bluemoon.lib.view.CommonEmptyView;

public class WarehouseAddressActivity extends BaseActivity {

    @Bind(R.id.txt_store_id)
    TextView txtStoreId;
    @Bind(R.id.listview_main)
    PullToRefreshListView listView;

    private MallStoreRecieverAddressAdapter adapter;
    private ResultMallStoreRecieverAddress item;
    private boolean isEdit;
    private int initAddressId;
    private int currentAddressId;
    private MallStoreRecieverAddress selectAddress = null;


    private String storeId;
    private String storeName;

    private void setDefaultOrDeleteAddress(MallStoreRecieverAddress address, boolean isDelete) {
        String token = ClientStateManager.getLoginToken(WarehouseAddressActivity.this);
        showWaitDialog();
        if (isDelete) {
            selectAddress = address;
            selectAddress.setIsDefault(0);
            DeliveryApi.deleteReceiveAddress(1, token, address.getAddressId(), defaultOrDeleteHandler);

        } else {

            selectAddress = address;
            selectAddress.setIsDefault(1);
            DeliveryApi.modifyDefaultAddress(2, token, storeId, address.getAddressId(), defaultOrDeleteHandler);
        }
    }


    private void setData(ResultMallStoreRecieverAddress result) {
        if (result == null || result.getAddressList() == null || result.getAddressList().size() < 1) {
            adapter.setList(new ArrayList<MallStoreRecieverAddress>());
        } else {
            item = result;
            adapter.setList(item.getAddressList());
        }
        listView.setAdapter(adapter);

    }

    @Override
    protected String getTitleString() {
        return getString(R.string.text_store_address_title);
    }

    @Override
    protected void onActionBarBtnRightClick() {
        if (null != item && null != item.getAddressList().get(0)) {
            WarehouseAddressEditActivity.actionStart(this, storeId, storeName, false, item.getAddressList().get(0));
        }
    }

    @Override
    protected void setActionBar(CommonActionBar titleBar) {
        titleBar.getImgRightView().setImageResource(R.mipmap.add_warehouse);
        titleBar.getImgRightView().setVisibility(View.VISIBLE);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_warehouse_address;
    }

    @Override
    protected void onSuccessResponse(int requestCode, String jsonString) {

    }

    @Override
    public void initView() {
        storeId = getIntent().getStringExtra("storeId");
        storeName = getIntent().getStringExtra("storeName");
        initAddressId = getIntent().getIntExtra("initAddressId", 0);
        currentAddressId = initAddressId;
        isEdit = false;
        if (StringUtil.isEmpty(storeId) || StringUtil.isEmpty(storeName)) {
            finish();
        }

        txtStoreId.setText(String.format("%s-%s", storeId, storeName));
        listView.setMode(PullToRefreshBase.Mode.DISABLED);
        CommonEmptyView emptyView = new CommonEmptyView(this);
        emptyView.setContentHit(R.string.text_store_address_title);
        listView.setEmptyView(emptyView);
        adapter = new MallStoreRecieverAddressAdapter(WarehouseAddressActivity.this);
    }

    @Override
    public void initData() {
        if (selectAddress != null) {
            if (selectAddress.getIsDefault() > 0) {
                for (MallStoreRecieverAddress addressItem : item.getAddressList()
                        ) {
                    if (addressItem.getAddressId() == selectAddress.getAddressId()) {
                        addressItem.setIsDefault(1);
                    } else {
                        addressItem.setIsDefault(0);
                    }
                }
            } else {
                for (MallStoreRecieverAddress addressItem : item.getAddressList()
                        ) {

                    if (addressItem.getAddressId() == selectAddress.getAddressId()) {
                        selectAddress = addressItem;
                    }
                }

                item.getAddressList().remove(selectAddress);
                selectAddress = null;


            }
            adapter.notifyDataSetChanged();

        } else {
            String token = ClientStateManager.getLoginToken(WarehouseAddressActivity.this);
            showWaitDialog();
            DeliveryApi.queryReceiveAddressByStoreCode(token, storeId, addressDetailHandler);
        }
    }

    @SuppressLint("InflateParams")
    class MallStoreRecieverAddressAdapter extends BaseListAdapter<MallStoreRecieverAddress> {

        private List<MallStoreRecieverAddress> lists;


        public MallStoreRecieverAddressAdapter(Context context) {
            super(context, null);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.store_address_detail_item;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            final MallStoreRecieverAddress address = lists.get(position);
            TextView txtAddress = getViewById(R.id.txt_address);
            TextView txtReciver = getViewById(R.id.txt_receiver);
            final CheckBox cbDefault = getViewById(R.id.cb_select);
            LinearLayout layoutLine = getViewById(R.id.layout_line);
            TextView txtRecycle = getViewById(R.id.txt_recycle);
            TextView txtEdit = getViewById(R.id.txt_edit);
            txtEdit.setTag(address);
            txtRecycle.setTag(address);
            layoutLine.setTag(address);
            cbDefault.setTag(address);

            txtAddress.setText(String.format("%s%s%s%s%s%s",
                    address.getProvinceName(),
                    address.getCityName(),
                    address.getCountyName(),
                    address.getTownName(),
                    address.getVillageName(),
                    address.getAddress()));
            txtReciver.setText(String.format(
                    getString(R.string.text_store_receive_person),
                    address.getReceiverName() + " " + address.getReceiverPhone()));


            cbDefault.setChecked(address.getIsDefault() > 0 ? true : false);


            cbDefault.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MallStoreRecieverAddress address = (MallStoreRecieverAddress) v.getTag();
                    if (address.getIsDefault() != 0) {
                        cbDefault.setChecked(true);
                    } else {
                        setDefaultOrDeleteAddress(address, false);
                    }
                }
            });

            layoutLine.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    MallStoreRecieverAddress address = (MallStoreRecieverAddress) v.getTag();
                    if (address.getIsDefault() == 0) {
                        setDefaultOrDeleteAddress(address, false);
                    }
                }
            });


            if (cbDefault.isChecked()) {
                currentAddressId = address.getAddressId();
            }

            txtEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WarehouseAddressEditActivity.actionStart(WarehouseAddressActivity.this, storeId, storeName, true, (MallStoreRecieverAddress) v.getTag());
                }
            });


            txtRecycle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final MallStoreRecieverAddress address = (MallStoreRecieverAddress) v.getTag();
                    if (address.getIsDefault() != 0) {
                        LibPublicUtil.showToast(WarehouseAddressActivity.this, getString(R.string.error_delete_address));
                    } else {

                        new CommonAlertDialog.Builder(context)
                                .setMessage(getString(R.string.title_delete_address_confirm))
                                .setPositiveButton(R.string.dialog_cancel, null
                                ).setNegativeButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                setDefaultOrDeleteAddress(address, true);
                            }
                        }).show();
                    }
                }
            });
        }
    }


    AsyncHttpResponseHandler defaultOrDeleteHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            hideWaitDialog();
            try {
                ResultBase baseResult = JSON.parseObject(responseString,
                        ResultBase.class);
                if (baseResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    isEdit = true;
                    initData();
                } else {
                    PublicUtil.showErrorMsg(WarehouseAddressActivity.this, baseResult);
                }
            } catch (Exception e) {
                PublicUtil.showToastServerBusy();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            hideWaitDialog();
            isEdit = true;
            PublicUtil.showToastServerOvertime();
        }
    };


    AsyncHttpResponseHandler addressDetailHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            hideWaitDialog();
            try {
                ResultMallStoreRecieverAddress addressDetailResult = JSON.parseObject(responseString,
                        ResultMallStoreRecieverAddress.class);
                if (addressDetailResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    setData(addressDetailResult);

                } else {
                    PublicUtil.showErrorMsg(WarehouseAddressActivity.this, addressDetailResult);
                }
            } catch (Exception e) {
                PublicUtil.showToastServerBusy();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            hideWaitDialog();
            PublicUtil.showToastServerOvertime();
        }
    };

    public static void actionStart(Fragment fragment, String storeId, String storeName, int initAddressId) {
        Intent intent = new Intent(fragment.getActivity(), WarehouseAddressActivity.class);
        intent.putExtra("storeId", storeId);
        intent.putExtra("storeName", storeName);
        intent.putExtra("initAddressId", initAddressId);
        fragment.startActivityForResult(intent, 0);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            isEdit = true;
            selectAddress = null;
            initData();
        }

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void back() {
        if (initAddressId == currentAddressId && !isEdit) {
            setResult(RESULT_CANCELED);
        } else {
            setResult(RESULT_OK);
        }
        finish();
    }
}
