package cn.com.bluemoon.delivery;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;
import org.kymjs.kjframe.KJBitmap;
import org.kymjs.kjframe.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.MenuBean;
import cn.com.bluemoon.delivery.app.api.model.MenuCode;
import cn.com.bluemoon.delivery.app.api.model.ModelNum;
import cn.com.bluemoon.delivery.app.api.model.ResultModelNum;
import cn.com.bluemoon.delivery.app.api.model.ResultScanService;
import cn.com.bluemoon.delivery.app.api.model.ResultUserRight;
import cn.com.bluemoon.delivery.app.api.model.UserRight;
import cn.com.bluemoon.delivery.app.api.model.card.ResultIsPunchCard;
import cn.com.bluemoon.delivery.app.api.model.message.ResultNewInfo;
import cn.com.bluemoon.delivery.app.api.model.other.ResultAngelQr;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.coupons.CouponsTabActivity;
import cn.com.bluemoon.delivery.module.evidencecash.EvidenceCashActivity;
import cn.com.bluemoon.delivery.module.extract.ExtractTabActivity;
import cn.com.bluemoon.delivery.module.inventory.InventoryTabActivity;
import cn.com.bluemoon.delivery.module.jobrecord.PromoteActivity;
import cn.com.bluemoon.delivery.module.notice.MessageListActivity;
import cn.com.bluemoon.delivery.module.notice.NoticeListActivity;
import cn.com.bluemoon.delivery.module.notice.PaperListActivity;
import cn.com.bluemoon.delivery.module.order.OrdersTabActivity;
import cn.com.bluemoon.delivery.module.storage.StorageTabActivity;
import cn.com.bluemoon.delivery.module.team.MyTeamActivity;
import cn.com.bluemoon.delivery.module.ticket.TicketChooseActivity;
import cn.com.bluemoon.delivery.module.wash.appointment.AppointmentTabActivity;
import cn.com.bluemoon.delivery.module.wash.collect.ClothingTabActivity;
import cn.com.bluemoon.delivery.module.wash.enterprise.EnterpriseWashTabActivity;
import cn.com.bluemoon.delivery.module.wash.returning.closebox.CloseBoxTabActivity;
import cn.com.bluemoon.delivery.module.wash.returning.clothescheck.ClothesCheckTabActivity;
import cn.com.bluemoon.delivery.module.wash.returning.cupboard.CupboardScanActivity;
import cn.com.bluemoon.delivery.module.wash.returning.driver.DriverTabActivity;
import cn.com.bluemoon.delivery.module.wash.returning.expressclosebox.ExpressCloseBoxTabActivity;
import cn.com.bluemoon.delivery.module.wash.returning.manager.ReturnManagerTabActivity;
import cn.com.bluemoon.delivery.module.wash.returning.pack.PackTabActivity;
import cn.com.bluemoon.delivery.module.wash.returning.transportreceive.TransportReceiveTabActivity;
import cn.com.bluemoon.delivery.ui.AlwaysMarqueeTextView;
import cn.com.bluemoon.delivery.ui.CustomGridView;
import cn.com.bluemoon.delivery.ui.common.TestActivity;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DialogUtil;
import cn.com.bluemoon.delivery.utils.KJFUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.delivery.utils.manager.ActivityManager;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.slidingmenu.SlidingMenu;
import cn.com.bluemoon.lib.slidingmenu.app.SlidingActivity;
import cn.com.bluemoon.lib.utils.LibConstants;
import cn.com.bluemoon.lib.utils.LibViewUtil;
import cn.com.bluemoon.lib.view.CommonAlertDialog;
import cn.com.bluemoon.lib.view.CommonEmptyView;
import cn.com.bluemoon.lib.view.CommonProgressDialog;
import cn.com.bluemoon.lib.view.RedpointTextView;

public class MainActivity extends SlidingActivity {

    private String TAG = "MainActivity";
    private AlwaysMarqueeTextView txtTips;
    private MainActivity main;
    private ActivityManager manager;
    private String token;
    private boolean isDestory;
    private CommonProgressDialog progressDialog;
    private List<UserRight> listRight;
    private int groupCount = 0;
    private SlidingMenu mMenu;
    private MenuFragment mMenuFragment;

    private PullToRefreshListView scrollViewMain;
    private GridViewAdapter gridViewAdapter;
    private CommonEmptyView emptyView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        main = this;
        initMenu();
        token = ClientStateManager.getLoginToken(main);
        if (StringUtils.isEmpty(token)) {
            PublicUtil.showMessageTokenExpire(main);
            return;
        }

        manager = ActivityManager.getInstance();
        manager.pushOneActivity(main);
        progressDialog = new CommonProgressDialog(main);
        progressDialog.setCancelable(false);

        ImageView imgPerson = (ImageView) findViewById(R.id.img_person);
        ImageView imgScan = (ImageView) findViewById(R.id.img_scan);
        imgPerson.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mMenu.showMenu(!mMenu.isMenuShowing());
                if (MenuFragment.user == null && mMenuFragment != null) {
                    mMenuFragment.setUserInfo();
                }
            }
        });
        imgScan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                PublicUtil.openScanView(main, null, null, 0);
                // TODO: 2017/5/25 测试
                ViewUtil.showActivity(main, TestActivity.class);
            }
        });
        txtTips = (AlwaysMarqueeTextView) findViewById(R.id.txt_tips);
        txtTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(main, MessageListActivity.class);
                startActivity(it);
            }
        });
        scrollViewMain = (PullToRefreshListView) findViewById(R.id.scrollView_main);
        scrollViewMain.getLoadingLayoutProxy().setRefreshingLabel(getString(R.string.refreshing));
        emptyView = PublicUtil.setEmptyView(scrollViewMain, getString(R.string.main_menu_title),
                new CommonEmptyView.EmptyListener() {
                    @Override
                    public void onRefresh() {
                        if (progressDialog != null) progressDialog.show();
                        DeliveryApi.getAppRights(token, appRightsHandler);
                        DeliveryApi.getNewMessage(token, newMessageHandler);
                    }
                });
        scrollViewMain.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                txtTips.setVisibility(View.GONE);
                DeliveryApi.getAppRights(token, appRightsHandler);
                DeliveryApi.getNewMessage(token, newMessageHandler);
            }
        });


        if (progressDialog != null) progressDialog.show();
        DeliveryApi.getAppRights(token, appRightsHandler);
        DeliveryApi.getNewMessage(token, newMessageHandler);

        jump(getIntent());

    }

    public void openQcode() {
        mMenu.toggle();
        DeliveryApi.moonAngelQrCodeService(token, angelCodeHandler);
    }

    public void CloseMenu() {
        mMenu.toggle();
    }

    private void initMenu() {

        //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        //            Window window = this.getWindow();
        //            WindowManager.LayoutParams layoutParams = window.getAttributes();
        //            layoutParams.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        //            window.setAttributes(layoutParams);
        //        }

        mMenuFragment = new MenuFragment();

        setBehindContentView(R.layout.main_left_layout);// 设置左菜单
        getFragmentManager().beginTransaction().replace(R.id.main_left_fragment, mMenuFragment)
                .commit();
        mMenu = getSlidingMenu();
        mMenu.setMode(SlidingMenu.LEFT);
        // 设置触摸屏幕的模式
        mMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        mMenu.setShadowDrawable(R.drawable.shadow);
        mMenu.setShadowWidthRes(R.dimen.shadow_width);
        //获取屏幕宽高
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        // 设置滑动菜单视图的宽度
        mMenu.setBehindWidth(dm.widthPixels * 3 / 5);
        // 设置渐入渐出效果的值
        mMenu.setFadeDegree(0.35f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //获取状态栏高度
            int statusbarHeight = getStatusBarHeight();
            LinearLayout top_head = (LinearLayout) this.findViewById(R.id.top_head);
            top_head.setPadding(0, statusbarHeight, 0, 0);
        }

    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    AsyncHttpResponseHandler getAmountHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d("getOrderCount", "getOrderCountHandler result = " + responseString);
            try {
                ResultModelNum result = JSON.parseObject(responseString, ResultModelNum.class);
                if (null != result && result.getResponseCode() == Constants
                        .RESPONSE_RESULT_SUCCESS) {
                    if (result.getModelBeans().size() >= 0) {
                        setAmount(result.getModelBeans());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString,
                              Throwable throwable) {
            /*if (listRight!=null) {
                setMenu();
            }*/
        }
    };


    private void setAmount(List<ModelNum> modelNums) {

        if (modelNums != null && listRight != null) {
            int sum = 0;
            for (UserRight right : listRight) {
                boolean isExit = false;
                for (ModelNum num : modelNums) {
                    if (right.getMenuCode().equals(num.getMenuId())) {
                        right.setAmount(num.getNum());
                        sum += num.getNum();
                        isExit = true;
                        break;
                    }
                }
                if (!isExit) {
                    right.setAmount(0);
                }
            }
            setMenu();
            PublicUtil.setMainAmount(main, sum);
        }
    }

    private void setMenu() {
        List<MenuBean> list = new ArrayList<>();
        if (listRight != null) {
            // TODO: lk 2016/6/12 可先用hashmap分组，再补全空白，可减少for层级
            for (int i = 0; i < groupCount; i++) {
                List<UserRight> item = new ArrayList<>();
                for (UserRight right : listRight) {
                    if ((i + 1) == right.getGroupNum()) {
                        item.add(right);
                    }
                }
                if (item.size() > 0) {
                    int divNum = 0;
                    if (item.size() % 4 != 0) {
                        divNum = 4 - item.size() % 4;
                    }
                    if (divNum > 0) {
                        for (int j = 0; j < divNum; j++) {
                            UserRight userRight = new UserRight();
                            userRight.setMenuCode(MenuCode.empty.toString());
                            userRight.setMenuName("");
                            userRight.setIconImg("");
                            userRight.setIconResId(0);
                            userRight.setUrl("");
                            userRight.setMenuId("");
                            item.add(userRight);
                        }
                    }
                    MenuBean bean = new MenuBean();
                    bean.setGroup(i + 1);
                    bean.setItem(item);
                    list.add(bean);
                }
            }
        }

        if (gridViewAdapter == null) {
            gridViewAdapter = new GridViewAdapter(main, list);
            scrollViewMain.setAdapter(gridViewAdapter);
        } else {
            gridViewAdapter.setList(list);
            gridViewAdapter.notifyDataSetChanged();
        }


    }

    private void jump(Intent intent) {
        String view = PublicUtil.getPushView(intent);
        String url = PublicUtil.getPushUrl(intent);
        if ((!TextUtils.isEmpty(view) && !Constants.PUSH_H5.equals(view))
                || (Constants.PUSH_H5.equals(view) && !TextUtils.isEmpty(url))) {
            UserRight userRight = new UserRight();
            userRight.setMenuCode(view);
            userRight.setMenuName("");
            userRight.setIconImg("");
            userRight.setIconResId(0);
            userRight.setMenuId("");
            userRight.setUrl(url);
            clickGridView(userRight);
        }
    }


    private void gotoPunchCard() {
        if (PublicUtil.isTipsByDay(main)) {
            showLocationSettingDialog();
        } else {
            if (progressDialog != null) progressDialog.show();
            DeliveryApi.isPunchCard(ClientStateManager.getLoginToken(main), isPunchCardHandler);
        }
    }

    private void showLocationSettingDialog() {
        new CommonAlertDialog.Builder(main)
                .setMessage(R.string.card_get_location_tips)
                .setNegativeButton(R.string.btn_setting,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                startActivity(new Intent("android.settings" +
                                        ".LOCATION_SOURCE_SETTINGS"));
                            }
                        })
                .setPositiveButton(R.string.btn_later, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (progressDialog != null) progressDialog.show();
                        DeliveryApi.isPunchCard(ClientStateManager.getLoginToken(main),
                                isPunchCardHandler);
                    }
                })
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        isDestory = false;
        backCount = 0;
        if (listRight != null) {
            DeliveryApi.getModelNum(token, getAmountHandler);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    private int backCount = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (backCount == 0) {
                backCount = 1;
                Toast.makeText(this, R.string.app_quit_txt, Toast.LENGTH_SHORT).show();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        isDestory = false;
        if (listRight != null) listRight.clear();
        token = ClientStateManager.getLoginToken(main);
        if (StringUtils.isEmpty(token)) {
            PublicUtil.showMessageTokenExpire(main);
            return;
        }
        DeliveryApi.getAppRights(token, appRightsHandler);
        DeliveryApi.getNewMessage(token, newMessageHandler);
        jump(intent);

    }

    AsyncHttpResponseHandler appRightsHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            LogUtils.d(TAG, "getAppRights result = " + responseString);
            if (progressDialog != null) progressDialog.dismiss();
            scrollViewMain.onRefreshComplete();

            try {
                ResultUserRight userRightResult = JSON.parseObject(responseString,
                        ResultUserRight.class);
                if (userRightResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    listRight = userRightResult.getRightsList();
                    groupCount = userRightResult.getGroupCount();

                    // TODO: lk 2016/12/20
                    if (!BuildConfig.RELEASE) {
                        //mockData();
                    }
                    setMenu();
                    DeliveryApi.getModelNum(ClientStateManager.getLoginToken(main),
                            getAmountHandler);
                } else {
                    PublicUtil.showErrorMsg(main, userRightResult);
                    LibViewUtil.setViewVisibility(emptyView, View.VISIBLE);
                }
            } catch (Exception e) {
                LogUtils.e(TAG, e.getMessage());
                PublicUtil.showToastServerBusy();
                LibViewUtil.setViewVisibility(emptyView, View.VISIBLE);
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            LogUtils.e(TAG, throwable.getMessage());
            scrollViewMain.onRefreshComplete();
            PublicUtil.showToastServerOvertime();
            if (progressDialog != null) progressDialog.dismiss();
            LibViewUtil.setViewVisibility(emptyView, View.VISIBLE);
        }
    };

    private void mockData() {
        UserRight item1 = new UserRight();
        item1.setMenuCode(MenuCode.receive_enterprise_manager.toString());
        item1.setMenuName("企业收衣");
        item1.setIconImg(listRight.get(0).getIconImg());
        item1.setGroupNum(1);
        listRight.add(item1);
    }

    AsyncHttpResponseHandler isPunchCardHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d(TAG, "isPunchCardHandler result = " + responseString);
            if (isDestory) return;
            if (progressDialog != null)
                progressDialog.dismiss();
            try {
                ResultIsPunchCard isPunchCardResult = JSON.parseObject(responseString,
                        ResultIsPunchCard.class);
                if (isPunchCardResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    PublicUtil.showPunchCardView(main, isPunchCardResult.isPunchCard);
                    //                    PublicUtil.showPunchCardView(main,false);
                } else {
                    PublicUtil.showErrorMsg(main, isPunchCardResult);
                }
            } catch (Exception e) {
                LogUtils.e(TAG, e.getMessage());
                PublicUtil.showToastServerBusy();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString,
                              Throwable throwable) {
            LogUtils.e(TAG, throwable.getMessage());
            if (isDestory) return;
            if (progressDialog != null)
                progressDialog.dismiss();
            PublicUtil.showToastServerOvertime();
        }
    };

    AsyncHttpResponseHandler angelCodeHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d(TAG, "moonAngelQrCodeService result = " + responseString);
            if (isDestory) return;
            if (progressDialog != null)
                progressDialog.dismiss();
            try {
                ResultAngelQr angelQrResult = JSON.parseObject(responseString, ResultAngelQr.class);
                if (angelQrResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    if (StringUtils.isEmpty(angelQrResult.getQrcode())) {
                        PublicUtil.showToastErrorData(main);
                    } else {
                        String title = getString(R.string.main_tab_qrcode);
                        String str = StringUtil.getStringParamsByFormat("\n",
                                angelQrResult.getOrgName(), angelQrResult.getInventoryName());
                        DialogUtil.showCodeDialog(main, title, angelQrResult.getQrcode(), str);
                    }
                } else {
                    PublicUtil.showErrorMsg(main, angelQrResult);
                }
            } catch (Exception e) {
                LogUtils.e(TAG, e.getMessage());
                PublicUtil.showToastServerBusy();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString,
                              Throwable throwable) {
            LogUtils.e(TAG, throwable.getMessage());
            if (isDestory) return;
            if (progressDialog != null)
                progressDialog.dismiss();
            PublicUtil.showToastServerOvertime();
        }
    };


    AsyncHttpResponseHandler newMessageHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            LogUtils.d(TAG, "newMessageHandler result = " + responseString);
            try {
                ResultNewInfo resultInfos = JSON.parseObject(responseString,
                        ResultNewInfo.class);
                if (resultInfos.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    if (!TextUtils.isEmpty(resultInfos.getMsgContent())) {
                        txtTips.setVisibility(View.VISIBLE);
                        txtTips.setText(resultInfos.getMsgContent());
                    } else {
                        txtTips.setVisibility(View.GONE);
                    }

                } else {
                    PublicUtil.showErrorMsg(main, resultInfos);
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
            //  PublicUtil.showToastServerOvertime();
        }
    };


    @Override
    protected void onStop() {
        super.onStop();
        isDestory = true;
        //        ClientStateManager.setMenuOrder(main,listRight);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestory = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                    if (data == null) return;
                    if (progressDialog != null) progressDialog.show();
                    String result = data.getStringExtra(LibConstants.SCAN_RESULT);
                    DeliveryApi.scanService(result, ClientStateManager.getLoginToken(),
                            scanServiceHandler);
                    break;
            }
        }
    }

    AsyncHttpResponseHandler scanServiceHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            if (progressDialog != null) progressDialog.dismiss();
            LogUtils.d(TAG, "scanService result = " + responseString);
            try {
                ResultScanService result = JSON.parseObject(responseString, ResultScanService
                        .class);
                if (null != result && result.getResponseCode() == Constants
                        .RESPONSE_RESULT_SUCCESS) {
                    if (ResultScanService.TYPE_HTTP.equals(result.getType())) {
                        ResultScanService.Http http = JSON.parseObject(result.getResult(),
                                ResultScanService.Http.class);
                        String url = http.getUrl();
                        String token = ClientStateManager.getLoginToken();
                        if (!TextUtils.isEmpty(url)) {
                            if (!TextUtils.isEmpty(token)) {
                                if (url.contains("?")) {
                                    url = url + "&token=" + token;
                                } else {
                                    url = url + "?token=" + token;
                                }
                            }

                            PublicUtil.openWebView(main, url, null, false, false);
                        }
                    } else if (ResultScanService.TYPE_TEXT.equals(result.getType())) {
                        ResultScanService.Text text = JSON.parseObject(result.getResult(),
                                ResultScanService.Text.class);
                        PublicUtil.showToast(text.getText());
                    }
                }
            } catch (Exception e) {
                LogUtils.e(TAG, e.getMessage());
                e.printStackTrace();
            }
        }


        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString,
                              Throwable throwable) {
            LogUtils.e(TAG, throwable.getMessage());
            if (progressDialog != null) progressDialog.dismiss();
        }
    };


    class GridViewAdapter extends BaseAdapter {

        private Context context;
        private List<MenuBean> list;

        public GridViewAdapter(Context context, List<MenuBean> list) {
            this.context = context;
            this.list = list;
        }

        public void setList(List<MenuBean> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            HolderView holderView = null;
            if (convertView == null) {
                holderView = new HolderView();
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.main_gridview, null);
                holderView.gridView = (CustomGridView) convertView.findViewById(R.id.gridview_main);
                convertView.setTag(holderView);
            } else {
                holderView = (HolderView) convertView.getTag();
            }

            UserRightAdapter userRightAdapter = new UserRightAdapter(main, list.get(position)
                    .getItem());
            holderView.gridView.setAdapter(userRightAdapter);
            holderView.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    clickGridView(list.get(position).getItem().get(i));
                }
            });
            return convertView;
        }

        class HolderView {
            CustomGridView gridView;
        }
    }

    private void clickGridView(UserRight userRight) {

        if (PublicUtil.isFastDoubleClick(1000)) {
            return;
        }
        String menuCode = userRight.getMenuCode();
        LogUtils.d("view:" + menuCode);
        try {
            Intent intent;
            if (compare(MenuCode.dispatch, menuCode)) {
                OrdersTabActivity.actionStart(main);
            } else if (compare(MenuCode.site_sign, menuCode)) {
                ExtractTabActivity.actionStart(main);
            } else if (compare(MenuCode.check_in, menuCode)) {
                intent = new Intent(main, TicketChooseActivity.class);
                startActivity(intent);
            } else if (compare(MenuCode.mall_erp_delivery, menuCode)) {
                InventoryTabActivity.actionStart(main, InventoryTabActivity.DELIVERY_MANAGEMENT);
            } else if (compare(MenuCode.mall_erp_receipt, menuCode)) {
                InventoryTabActivity.actionStart(main, InventoryTabActivity.RECEIVE_MANAGEMENT);
            } else if (compare(MenuCode.mall_erp_stock, menuCode)) {
                StorageTabActivity.actionStart(main);
            } else if (compare(MenuCode.punch_card, menuCode)) {
                gotoPunchCard();
            } else if (compare(MenuCode.card_coupons, menuCode)) {
                intent = new Intent(main, CouponsTabActivity.class);
                startActivity(intent);
            } else if (compare(MenuCode.my_news, menuCode)) {
                intent = new Intent(main, MessageListActivity.class);
                startActivity(intent);
            } else if (compare(MenuCode.my_inform, menuCode)) {
                intent = new Intent(main, NoticeListActivity.class);
                startActivity(intent);
            } else if (compare(MenuCode.knowledge_base, menuCode)) {
                intent = new Intent(main, PaperListActivity.class);
                startActivity(intent);
            } else if (compare(MenuCode.customer_service, menuCode)) {
                DialogUtil.showServiceDialog(main);
            } else if (compare(MenuCode.receive_clothes_manager, menuCode)) {
                ClothingTabActivity.actionStart(main, ClothingTabActivity
                        .WITH_ORDER_COLLECT_MANAGE);
            } else if (compare(MenuCode.activity_collect_clothes, menuCode)) {
                ClothingTabActivity.actionStart(main, ClothingTabActivity
                        .WITHOUT_ORDER_COLLECT_MANAGE);
            } else if (compare(MenuCode.promote_file, menuCode)) {
                intent = new Intent(main, PromoteActivity.class);
                startActivity(intent);
            } else if (compare(MenuCode.my_team, menuCode)) {
                intent = new Intent(main, MyTeamActivity.class);
                startActivity(intent);
            } else if (compare(MenuCode.wash_cabinet_manager, menuCode)) {
                CupboardScanActivity.actStart(main);
            } else if (compare(MenuCode.wash_transport, menuCode)) {
                DriverTabActivity.actionStart(main);
            } else if (compare(MenuCode.wash_express_close_box, menuCode)) {
                ExpressCloseBoxTabActivity.actionStart(this);
            } else if (compare(MenuCode.wash_back_order_manager, menuCode)) {
                ReturnManagerTabActivity.actionStart(this);
            } else if (compare(MenuCode.wash_transport_sign, menuCode)) {
                TransportReceiveTabActivity.actionStart(this);
            } else if (compare(MenuCode.wash_carriage_close_box, menuCode)) {
                CloseBoxTabActivity.actionStart(main);
            } else if (compare(MenuCode.wash_back_order_package, menuCode)) {
                PackTabActivity.actionStart(main);
            } else if (compare(MenuCode.wash_clothes_check, menuCode)) {
                ClothesCheckTabActivity.actionStart(main);
            } else if (compare(MenuCode.my_deposit, menuCode)) {
                intent = new Intent(main, EvidenceCashActivity.class);
                startActivity(intent);
            }
            //预约收衣
            else if (compare(MenuCode.receive_appointment_manager, menuCode)) {
                AppointmentTabActivity.actionStart(main);
            }
            //企业洗衣
            else if (compare(MenuCode.receive_enterprise_manager, menuCode)) {
                EnterpriseWashTabActivity.actionStart(main);
            } else if (compare(MenuCode.card_coupons_web, menuCode)) {
                PublicUtil.openWebView(main, userRight.getUrl()
                                + (!userRight.getUrl().contains("?") ? "?" : "&")
                                + "token=" + ClientStateManager.getLoginToken(),
                        userRight.getMenuName(), false, true);
            } else if (!StringUtils.isEmpty(userRight.getUrl())) {
                String url = userRight.getUrl()
                        + (!userRight.getUrl().contains("?") ? "?" : "&")
                        + "token=" + ClientStateManager.getLoginToken();
                PublicUtil.openWebView(main, url, userRight.getMenuName(), false);
                LogUtils.d("openUrl:" + url);

            } else if (compare(MenuCode.empty, menuCode)) {
                //click empty
            } else {
                PublicUtil.showToast(getString(R.string.main_tab_no_data));
            }
        } catch (Exception ex) {
            PublicUtil.showToast(main, ex.getMessage());
        }
    }

    private boolean compare(MenuCode enumCode, String menuCode) {
        return enumCode.toString().equals(menuCode);
    }


    class UserRightAdapter extends BaseAdapter {

        private Context context;
        private List<UserRight> listUserRight;
        private KJBitmap kjb;

        public UserRightAdapter(Context context, List<UserRight> listUserRight) {
            this.context = context;
            this.listUserRight = listUserRight;
        }

        @Override
        public int getCount() {
            return listUserRight.size();
        }

        @Override
        public Object getItem(int position) {
            return 0;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            HolderView holderView;
            if (convertView == null) {
                holderView = new HolderView();
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.main_gridview_item, null);
                holderView.imgItem = (ImageView) convertView.findViewById(R.id.img_menu_item);
                holderView.txtItem = (TextView) convertView.findViewById(R.id.txt_menu_item);
                holderView.countTextView = (RedpointTextView) convertView.findViewById(R.id
                        .txt_dispatch_count);
                convertView.setTag(holderView);
            } else {
                holderView = (HolderView) convertView.getTag();
            }

            if (!MenuCode.empty.toString().equals(listUserRight.get(position).getMenuCode())) {
                if (listUserRight.get(position).getAmount() <= 0) {
                    holderView.countTextView.setText(String.valueOf(listUserRight.get(position)
                            .getAmount()));
                    holderView.countTextView.setVisibility(View.GONE);
                } else if (listUserRight.get(position).getAmount() < 100) {
                    holderView.countTextView.setText(String.valueOf(listUserRight.get(position)
                            .getAmount()));
                    holderView.countTextView.setVisibility(View.VISIBLE);
                } else {
                    holderView.countTextView.setText(getText(R.string.more_amount));
                    holderView.countTextView.setVisibility(View.VISIBLE);
                }

                if (!StringUtils.isEmpty(listUserRight.get(position).getIconImg())) {
                    KJFUtil.getUtil().getKJB().display(holderView.imgItem, listUserRight.get
                            (position).getIconImg());
                }

                holderView.txtItem.setText(listUserRight.get(position).getMenuName());
            } else {
                convertView.setBackgroundColor(getResources().getColor(R.color.white));
                holderView.txtItem.setVisibility(View.GONE);
                holderView.imgItem.setVisibility(View.GONE);
                holderView.countTextView.setVisibility(View.GONE);
            }

            return convertView;
        }

        class HolderView {
            ImageView imgItem;
            TextView txtItem;
            RedpointTextView countTextView;
        }
    }

    public static void actStart(Context context, String view, String url) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Constants.PUSH_VIEW, view);
        intent.putExtra(Constants.PUSH_URL, url);
        context.startActivity(intent);
    }

    public static void actStart(Context context) {
        actStart(context, null, null);
    }

    public static Intent getStartIntent(Context context, String view, String url) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Constants.PUSH_VIEW, view);
        intent.putExtra(Constants.PUSH_URL, url);
        return intent;
    }


}
