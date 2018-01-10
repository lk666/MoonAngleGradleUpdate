package cn.com.bluemoon.delivery;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.bluemoon.signature.lib.AbstractSignatureActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.ResultModelNum;
import cn.com.bluemoon.delivery.app.api.model.ResultUserRight;
import cn.com.bluemoon.delivery.app.api.model.card.ResultIsPunchCard;
import cn.com.bluemoon.delivery.app.api.model.contract.ResultCheckPersonReal;
import cn.com.bluemoon.delivery.app.api.model.message.Info;
import cn.com.bluemoon.delivery.app.api.model.message.ResultInfos;
import cn.com.bluemoon.delivery.app.api.model.message.ResultNewInfo;
import cn.com.bluemoon.delivery.app.api.model.other.ResultAngelQr;
import cn.com.bluemoon.delivery.common.menu.MenuAdapter;
import cn.com.bluemoon.delivery.common.menu.MenuCode;
import cn.com.bluemoon.delivery.common.menu.MenuManager;
import cn.com.bluemoon.delivery.common.menu.MenuSection;
import cn.com.bluemoon.delivery.module.base.BaseSlidingActivity;
import cn.com.bluemoon.delivery.module.contract.AuthUserInfoActivity;
import cn.com.bluemoon.delivery.module.contract.SignatureActivity;
import cn.com.bluemoon.delivery.module.notice.MessageListActivity;
import cn.com.bluemoon.delivery.module.notice.NoticeShowActivity;
import cn.com.bluemoon.delivery.module.track.TrackManager;
import cn.com.bluemoon.delivery.ui.AlwaysMarqueeTextView;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DialogUtil;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.delivery.utils.manager.ActivityManager;
import cn.com.bluemoon.lib.slidingmenu.SlidingMenu;
import cn.com.bluemoon.lib.view.CommonEmptyView;

public class MainActivity extends BaseSlidingActivity implements View.OnClickListener,
        BaseQuickAdapter.OnItemClickListener {

    @Bind(R.id.layout_title)
    FrameLayout layoutTitle;
    @Bind(R.id.txt_tips)
    AlwaysMarqueeTextView txtTips;
    @Bind(R.id.refresh_head)
    MaterialHeader refreshHead;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.layout_refresh)
    SmartRefreshLayout layoutRefresh;
    private SlidingMenu mMenu;
    private MenuFragment mMenuFragment;
    private ResultUserRight resultUserRight;
    private MenuAdapter menuAdapter;

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

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        //初始化侧滑栏
        initMenu();
        //兼容沉浸式
        ViewUtil.initTop(this, layoutTitle, false);
        //初始化下拉控件
        layoutRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getRightData();
            }
        });
        //刷新转圈颜色变化
        refreshHead.setColorSchemeColors(0xff1fb8ff, 0xffff6c47, 0xff87D657, 0xffE59062);
        //初始化主菜单
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        menuAdapter = new MenuAdapter();
        menuAdapter.bindToRecyclerView(recyclerView);
        menuAdapter.setOnItemClickListener(this);
        CommonEmptyView emptyView = new CommonEmptyView(this);
        emptyView.setRefreshable(false);
        menuAdapter.setEmptyView(emptyView);
        menuAdapter.replaceData(new ArrayList<MenuSection>());
        menuAdapter.openLoadAnimation();
    }

    /**
     * 初始化侧滑栏
     */
    private void initMenu() {
        mMenuFragment = new MenuFragment();
        setBehindContentView(R.layout.main_left_layout);// 设置左菜单
        getSupportFragmentManager().beginTransaction().replace(R.id.main_left_fragment,
                mMenuFragment)
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
    }

    @Override
    public void initData() {
        if (TextUtils.isEmpty(getToken())) {
            PublicUtil.showMessageTokenExpire(this);
            return;
        }
        //请求菜单数据和消息数据
        getRightData();
        //请求必读消息数据
        getNoticeData();
        //处理推送消息数据
        MenuManager.getInstance().jump(this, getIntent());
        //数据埋点
        TrackManager.checkData();
    }

    @Override
    public void onResume() {
        super.onResume();
        //请求角标数据
        if (resultUserRight != null) {
            getNumData();
        }
    }

    /**
     * 请求菜单数据和消息数据
     */
    private void getRightData() {
        DeliveryApi.getAppRights(getToken(), getNewHandler(1, ResultUserRight.class));
        getMessageData();
    }

    /**
     * 请求消息数据
     */
    private void getMessageData() {
        DeliveryApi.getNewMessage(getToken(), getNewHandler(2, ResultNewInfo.class));
    }

    /**
     * 请求角标数量
     */
    private void getNumData() {
        DeliveryApi.getModelNum(getToken(), getNewHandler(3, ResultModelNum.class));
    }

    /**
     * 判断是否打卡
     */
    public void requestPunchCard() {
        showWaitDialog(false);
        DeliveryApi.isPunchCard(getToken(), getNewHandler(4, ResultIsPunchCard.class));
    }

    /**
     * 获取必要未读消息列表
     */
    private void getNoticeData() {
        showWaitDialog(false);
        DeliveryApi.getMustReadInfoList(getToken(), getNewHandler(5, ResultInfos.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        switch (requestCode) {
            case 0:
                //获取二维码
                ResultAngelQr angelQrResult = (ResultAngelQr) result;
                if (TextUtils.isEmpty(angelQrResult.getQrcode())) {
                    PublicUtil.showToastErrorData(this);
                } else {
                    String title = getString(R.string.main_tab_qrcode);
                    String str = StringUtil.getStringParamsByFormat("\n",
                            angelQrResult.getOrgName(), angelQrResult.getInventoryName());
                    DialogUtil.showCodeDialog(this, title, angelQrResult.getQrcode(), str);
                }
                break;
            case 1:
                //获取主菜单和快捷菜单
                resultUserRight = (ResultUserRight) result;
                //设置主菜单数据
                menuAdapter.replaceData(MenuManager.getInstance().getMenuList(resultUserRight));
                //获取角标数量
                getNumData();
                break;
            case 2:
                //获取最新消息
                ResultNewInfo resultInfo = (ResultNewInfo) result;
                ViewUtil.setViewVisibility(txtTips, TextUtils.isEmpty(resultInfo.getMsgContent())
                        ? View.GONE : View.VISIBLE);
                txtTips.setText(resultInfo.getMsgContent());
                break;
            case 3:
                //设置角标数量
                MenuManager.getInstance().setAmount(this, menuAdapter, resultUserRight, (
                        (ResultModelNum) result).getModelBeans());
                break;
            case 4:
                //判断是否已打卡
                PublicUtil.showPunchCardView(this, ((ResultIsPunchCard) result).isPunchCard);
                break;
            case 5:
                //获取必读消息
                List<Info> list = ((ResultInfos) result).getInfoList();
                if (list != null && !list.isEmpty()) {
                    ArrayList<String> unReadList = new ArrayList<>();
                    for (Info info : list) {
                        unReadList.add(info.getInfoId());
                    }
                    NoticeShowActivity.startAction(this, unReadList);
                }
                break;
        }
    }

    @Override
    public void onFinishResponse(int requestCode) {
        super.onFinishResponse(requestCode);
        if (requestCode == 1) {
            layoutRefresh.finishRefresh();
        }
    }

    @OnClick({R.id.img_person, R.id.img_scan, R.id.txt_tips})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_person:
                mMenu.showMenu(!mMenu.isMenuShowing());
                if (MenuFragment.user == null && mMenuFragment != null) {
                    mMenuFragment.setUserInfo();
                }
                break;
            case R.id.img_scan:
                PublicUtil.openXScanView(this, null, null, 0);
                break;
            case R.id.txt_tips:
                ViewUtil.showActivity(this, MessageListActivity.class);
                break;
            default:
                break;
        }
    }

    /**
     * 点击二维码
     */
    public void openQrCode() {
        toggle();
        DeliveryApi.moonAngelQrCodeService(getToken(), getNewHandler(0, ResultAngelQr.class));
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (PublicUtil.isFastDoubleClick()) return;
        MenuSection item = menuAdapter.getData().get(position);
        if (!item.isHeader && !MenuCode.empty.toString().equals(item.t.getMenuCode())) {
            MenuManager.getInstance().onClickMenu(this, item.t);
        }
    }

    private long firstTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (System.currentTimeMillis() - firstTime > 2000) {
                firstTime = System.currentTimeMillis();
                ViewUtil.toast(R.string.app_quit_txt);
            } else {
                finish();
                ActivityManager.getInstance().finishAllActivity();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (resultUserRight != null) {
            resultUserRight = null;
        }
        initData();
    }
}
