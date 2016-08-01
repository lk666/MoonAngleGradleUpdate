package cn.com.bluemoon.delivery.module.storage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
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

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.storage.MallStoreRecieverAddress;
import cn.com.bluemoon.delivery.app.api.model.storage.ResultMallStoreRecieverAddress;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.utils.LibPublicUtil;
import cn.com.bluemoon.lib.view.CommonAlertDialog;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

public class WarehouseAddressActivity extends Activity {

    private String TAG = "WarehouseAddressActivity";

    private MallStoreRecieverAddressAdapter adapter;
    private WarehouseAddressActivity main;
    private CommonProgressDialog progressDialog;
    private PullToRefreshListView listView;
    private ResultMallStoreRecieverAddress item;
    private boolean isEdit;
    private int initAddressId;
    private int currentAddressId;
    private MallStoreRecieverAddress selectAddress = null;
    private TextView txtStoreId;


    private String storeId;
    private String storeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse_address);
        main = this;
        storeId = getIntent().getStringExtra("storeId");
        storeName = getIntent().getStringExtra("storeName");
        initAddressId = getIntent().getIntExtra("initAddressId", 0);
        currentAddressId = initAddressId;
        isEdit = false;
        if (StringUtil.isEmpty(storeId) || StringUtil.isEmpty(storeName)) {
            finish();
        }
        initCustomActionBar();
        listView = (PullToRefreshListView) findViewById(R.id.listview_main);
        txtStoreId = (TextView) findViewById(R.id.txt_store_id);

        txtStoreId.setText(String.format("%s-%s", storeId, storeName));
        listView.setMode(PullToRefreshBase.Mode.DISABLED);
        progressDialog = new CommonProgressDialog(main);
        adapter = new MallStoreRecieverAddressAdapter(main);
        getItem();

    }


    private void getItem() {

        if (null != selectAddress) {
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
            String token = ClientStateManager.getLoginToken(main);
            if (progressDialog != null) {
                progressDialog.show();
            }
            DeliveryApi.queryReceiveAddressByStoreCode(token, storeId, addressDetailHandler);
        }
    }

    private void setDefaultOrDeleteAddress(MallStoreRecieverAddress address, boolean isDelete) {
        String token = ClientStateManager.getLoginToken(main);
        if (progressDialog != null) {
            progressDialog.show();
        }


        if (isDelete) {
            selectAddress = address;
            selectAddress.setIsDefault(0);
            DeliveryApi.deleteReceiveAddress(token, address.getAddressId(), defaultOrDeleteHandler);

        } else {

            selectAddress = address;
            selectAddress.setIsDefault(1);
            DeliveryApi.modifyDefaultAddress(token, storeId, address.getAddressId(), defaultOrDeleteHandler);
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


    private void initCustomActionBar() {

        CommonActionBar bar = new CommonActionBar(main.getActionBar(),
                new IActionBarListener() {

                    @Override
                    public void onBtnRight(View v) {
                        // TODO Auto-generated method stub
                        if (null != item && null != item.getAddressList().get(0)) {
                            WarehouseAddressEditActivity.actionStart(main, storeId, storeName, false, item.getAddressList().get(0));
                        }
                    }

                    @Override
                    public void onBtnLeft(View v) {
                        // TODO Auto-generated method stub
                        back();
                    }

                    @Override
                    public void setTitle(TextView v) {
                        // TODO Auto-generated method stub
                        v.setText(R.string.text_store_address_title);
                    }
                });

        bar.getImgRightView().setImageResource(R.mipmap.add_warehouse);
        bar.getImgRightView().setVisibility(View.VISIBLE);

    }

    @SuppressLint("InflateParams")
    class MallStoreRecieverAddressAdapter extends BaseAdapter {

        private Context context;
        private List<MallStoreRecieverAddress> lists;


        public MallStoreRecieverAddressAdapter(Context context) {
            this.context = context;
        }

        public void setList(List<MallStoreRecieverAddress> list) {
            this.lists = list;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return lists.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return lists.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            // TODO Auto-generated method stub
            LayoutInflater inflate = LayoutInflater.from(context);
            if (lists.size() == 0) {
                View viewEmpty = inflate.inflate(R.layout.layout_no_data, null);
                AbsListView.LayoutParams params = new AbsListView.LayoutParams(
                        AbsListView.LayoutParams.MATCH_PARENT, listView.getHeight());
                viewEmpty.setLayoutParams(params);
                return viewEmpty;
            }

            convertView = inflate.inflate(R.layout.store_address_detail_item, null);

            TextView txtAddress = (TextView) convertView
                    .findViewById(R.id.txt_address);

            TextView txtReciver = (TextView) convertView
                    .findViewById(R.id.txt_receiver);

            final CheckBox cbDefault = (CheckBox) convertView
                    .findViewById(R.id.cb_select);

            LinearLayout layoutLine = (LinearLayout) convertView.findViewById(R.id.layout_line);

            TextView txtRecycle = (TextView) convertView.findViewById(R.id.txt_recycle);

            TextView txtEdit = (TextView) convertView.findViewById(R.id.txt_edit);
            txtEdit.setTag(lists.get(position));
            txtRecycle.setTag(lists.get(position));
            layoutLine.setTag(lists.get(position));
            cbDefault.setTag(lists.get(position));

            txtAddress.setText(String.format("%s%s%s%s%s%s",
                    lists.get(position).getProvinceName(),
                    lists.get(position).getCityName(),
                    lists.get(position).getCountyName(),
                    lists.get(position).getTownName(),
                    lists.get(position).getVillageName(),
                    lists.get(position).getAddress()));
            txtReciver.setText(String.format(
                    getString(R.string.text_store_receive_person),
                    lists.get(position).getReceiverName() + " " + lists.get(position).getReceiverPhone()));


            cbDefault.setChecked(lists.get(position).getIsDefault() > 0 ? true : false);


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
                currentAddressId = lists.get(position).getAddressId();
            }

            txtEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WarehouseAddressEditActivity.actionStart(main, storeId, storeName, true, (MallStoreRecieverAddress) v.getTag());
                }
            });


            txtRecycle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final MallStoreRecieverAddress address = (MallStoreRecieverAddress) v.getTag();
                    if (address.getIsDefault() != 0) {
                        LibPublicUtil.showToast(main, getString(R.string.error_delete_address));
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

            return convertView;
        }

    }


    AsyncHttpResponseHandler defaultOrDeleteHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            LogUtils.d(TAG, "defaultHandler result = " + responseString);
            if (progressDialog != null)
                progressDialog.dismiss();
            try {
                ResultBase baseResult = JSON.parseObject(responseString,
                        ResultBase.class);
                if (baseResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    isEdit = true;
                    getItem();
                } else {
                    PublicUtil.showErrorMsg(main, baseResult);
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
            isEdit = true;
            PublicUtil.showToastServerOvertime();
        }
    };


    AsyncHttpResponseHandler addressDetailHandler = new TextHttpResponseHandler(
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
                    setData(addressDetailResult);

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
            selectAddress=null;
            getItem();

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
        main.finish();
    }
}
