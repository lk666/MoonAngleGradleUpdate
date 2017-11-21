package cn.com.bluemoon.delivery;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemDragListener;
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
import cn.com.bluemoon.delivery.app.api.model.UserRight;
import cn.com.bluemoon.delivery.app.api.model.card.ResultIsPunchCard;
import cn.com.bluemoon.delivery.app.api.model.message.Info;
import cn.com.bluemoon.delivery.app.api.model.message.ResultInfos;
import cn.com.bluemoon.delivery.app.api.model.message.ResultNewInfo;
import cn.com.bluemoon.delivery.app.api.model.other.ResultAngelQr;
import cn.com.bluemoon.delivery.common.menu.MenuAdapter;
import cn.com.bluemoon.delivery.common.menu.MenuBgAdapter;
import cn.com.bluemoon.delivery.common.menu.MenuEditAdapter;
import cn.com.bluemoon.delivery.common.menu.MenuManager;
import cn.com.bluemoon.delivery.common.menu.MenuQuickAdapter;
import cn.com.bluemoon.delivery.common.menu.MenuSection;
import cn.com.bluemoon.delivery.module.base.BaseSlidingActivity;
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
        BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemChildClickListener,
        OnItemDragListener, View.OnTouchListener {


    @Bind(R.id.img_edit_arrow)
    ImageView imgEditArrow;
    @Bind(R.id.img_person)
    ImageView imgPerson;
    @Bind(R.id.img_scan)
    ImageView imgScan;
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
    @Bind(R.id.recycler_quick)
    RecyclerView recyclerQuick;
    @Bind(R.id.recycler_bg)
    RecyclerView recyclerBg;
    @Bind(R.id.layout_quick)
    FrameLayout layoutQuick;
    @Bind(R.id.txt_edit)
    TextView txtEdit;
    @Bind(R.id.txt_edit_hint)
    TextView txtEditHint;
    @Bind(R.id.txt_finish)
    TextView txtFinish;
    @Bind(R.id.recycler_edit)
    RecyclerView recyclerEdit;
    @Bind(R.id.layout_edit)
    FrameLayout layoutEdit;
    @Bind(R.id.layout_edit_title)
    FrameLayout layoutEditTitle;
    @Bind(R.id.layout_bottom)
    FrameLayout layoutBottom;
    @Bind(R.id.top_head)
    LinearLayout topHead;
    private SlidingMenu mMenu;
    private MenuFragment mMenuFragment;
    private ResultUserRight resultUserRight;
    private MenuAdapter menuAdapter;
    private MenuEditAdapter editAdapter;
    private MenuQuickAdapter quickAdapter;
    private boolean isEdit;
    private ItemTouchHelper itemTouchHelper;

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
        ViewUtil.initTop(this, topHead, false);
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
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        menuAdapter = new MenuAdapter();
        menuAdapter.bindToRecyclerView(recyclerView);
        menuAdapter.setOnItemClickListener(this);
        menuAdapter.setOnItemChildClickListener(this);
        CommonEmptyView emptyView = new CommonEmptyView(this);
        emptyView.setRefreshable(false);
        menuAdapter.setEmptyView(emptyView);
        menuAdapter.replaceData(new ArrayList<MenuSection>());
        menuAdapter.openLoadAnimation();
        //初始化编辑菜单的背景
        recyclerBg.setLayoutManager(new GridLayoutManager(this, 4));
        MenuBgAdapter bgAdapter = new MenuBgAdapter(MenuManager.getBgList());
        bgAdapter.bindToRecyclerView(recyclerBg);
        //初始化编辑菜单
        recyclerEdit.setLayoutManager(new GridLayoutManager(this, 4));
        editAdapter = new MenuEditAdapter();
        editAdapter.bindToRecyclerView(recyclerEdit);
        editAdapter.setOnItemClickListener(this);
        editAdapter.setOnItemChildClickListener(this);
        //设置拖拽事件
        ItemDragAndSwipeCallback itemDragCallback = new ItemDragAndSwipeCallback(editAdapter);
        itemTouchHelper = new ItemTouchHelper(itemDragCallback);
        itemTouchHelper.attachToRecyclerView(recyclerEdit);
        editAdapter.setOnItemDragListener(this);
        editAdapter.setEmptyView(R.layout.layout_empty_edit);
        //初始化快捷菜单,横向排列
        recyclerQuick.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager
                .HORIZONTAL, false));
        quickAdapter = new MenuQuickAdapter();
        quickAdapter.bindToRecyclerView(recyclerQuick);
        quickAdapter.setEmptyView(R.layout.layout_empty_edit);
        //编辑菜单下滑关闭设置
        layoutEditTitle.setOnTouchListener(this);
        txtEditHint.setOnTouchListener(this);
    }

    /**
     * 初始化侧滑栏
     */
    private void initMenu() {
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

    /**
     * 保存快捷菜单
     */
    public void saveEditMenu() {
        showWaitDialog(false);
        DeliveryApi.addUserDefinedMenus(getToken(), MenuManager.getInstance().getEditMenuList
                (editAdapter.getData()), getNewHandler(6, ResultBase.class));
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
                if(resultUserRight.rightsList==null){
                    resultUserRight.rightsList = new ArrayList<>();
                }
                if(resultUserRight.quickList==null){
                    resultUserRight.quickList = new ArrayList<>();
                }
                ViewUtil.setViewVisibility(layoutBottom, View.VISIBLE);
                //首次显示处理，单编辑菜单和快捷菜单都没有显示时（即第一次加载），根据快捷菜单的个数显示不同界面
                if (layoutEdit.getVisibility() == View.GONE && layoutQuick.getVisibility() ==
                        View.GONE) {
                    ViewUtil.setViewVisibility(resultUserRight.quickList.size() > 0 ? layoutEdit :
                            layoutQuick, View.VISIBLE);
                }
                //设置主菜单数据
                menuAdapter.replaceData(MenuManager.getInstance().getMenuList(resultUserRight));
                //设置简洁图标快捷菜单
                setQuickMenu();
                //设置可编辑的快捷菜单
                resetEditMenu();
                //获取角标数量
                getNumData();
                break;
            case 2:
                //获取最新消息
                ResultNewInfo resultInfo = (ResultNewInfo) result;
                if (!TextUtils.isEmpty(resultInfo.getMsgContent())) {
                    txtTips.setVisibility(View.VISIBLE);
                    txtTips.setText(resultInfo.getMsgContent());
                } else {
                    txtTips.setVisibility(View.GONE);
                }
                break;
            case 3:
                //设置角标数量
                MenuManager.getInstance().setAmount(this, menuAdapter, editAdapter,
                        resultUserRight, ((ResultModelNum) result).getModelBeans());
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
            case 6:
                //编辑完成
                resultUserRight.quickList.clear();
                resultUserRight.quickList.addAll(editAdapter.getData());
                //更新快捷菜单
                setQuickMenu();
                //恢复为不可编辑状态
                setEdit(false);
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

    @OnClick({R.id.img_person, R.id.img_scan, R.id.txt_tips, R.id.txt_edit, R.id.txt_finish, R.id
            .txt_edit_hint, R.id.show_view, R.id.layout_edit_title})
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
            case R.id.txt_edit:
                isEdit = !isEdit;
                setEdit(isEdit);
                //点击取消
                if (!isEdit) {
                    resetEditMenu();
                    //如果用户取消，则恢复原来的勾选状态
                    MenuManager.getInstance().refreshAllSelect(resultUserRight);
                }
                break;
            case R.id.txt_finish:
                //点击完成
                saveEditMenu();
                break;
            case R.id.show_view:
                ViewUtil.setViewVisibility(layoutEdit, View.VISIBLE);
                ViewUtil.setViewVisibility(layoutQuick, View.GONE);
                break;
            case R.id.txt_edit_hint:
                hideEditMenu();
                break;
            default:
                break;
        }
    }

    /**
     * 改变编辑状态的处理
     */
    private void setEdit(boolean isEdit) {
        this.isEdit = isEdit;
        layoutRefresh.setEnableRefresh(!isEdit);
        editAdapter.isUseEmpty(!isEdit);
        txtEdit.setSelected(isEdit);
        txtEdit.setText(isEdit ? R.string.btn_cancel : R.string.btn_edit);
        menuAdapter.setEdit(isEdit);
        editAdapter.setEdit(isEdit);
        ViewUtil.setViewVisibility(txtFinish, isEdit ? View.VISIBLE : View.GONE);
        ViewUtil.setViewVisibility(imgEditArrow, isEdit ? View.GONE : View.VISIBLE);
        ViewUtil.setViewVisibility(layoutTitle, isEdit ? View.GONE : View.VISIBLE);
        ViewUtil.setViewVisibility(txtEditHint, isEdit ? View.GONE : View.VISIBLE);
        ViewUtil.setViewVisibility(recyclerBg, isEdit ? View.VISIBLE : View.GONE);
        // 开启拖拽
        if (isEdit) {
            editAdapter.enableDragItem(itemTouchHelper);
        } else {
            editAdapter.disableDragItem();
        }
    }

    /**
     * 设置编辑的快捷菜单
     */
    private void resetEditMenu() {
        editAdapter.replaceData(resultUserRight.quickList);
    }

    /**
     * 设置快捷图标菜单
     */
    private void setQuickMenu() {
        quickAdapter.replaceData(MenuManager.getInstance().getIconList(this, resultUserRight
                .quickList));
    }

    /**
     * 收下编辑菜单
     */
    private void hideEditMenu() {
        if (!isEdit) {
            ViewUtil.setViewVisibility(layoutEdit, View.GONE);
            ViewUtil.setViewVisibility(layoutQuick, View.VISIBLE);
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
        if (isEdit) return;
        if (PublicUtil.isFastDoubleClick()) return;
        if (adapter instanceof MenuAdapter) {
            MenuSection item = menuAdapter.getData().get(position);
            if (!item.isHeader) {
                MenuManager.getInstance().onClickMenu(this, item.t);
            }
        } else if (adapter instanceof MenuEditAdapter) {
            UserRight item = editAdapter.getData().get(position);
            MenuManager.getInstance().onClickMenu(this, item);
        }
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (!isEdit) return;
        if (PublicUtil.isFastDoubleClick()) return;
        if (adapter instanceof MenuAdapter) {
            MenuSection item = menuAdapter.getData().get(position);
            if (!item.isHeader) {
                if (editAdapter.getData().size() < 8) {
                    view.setEnabled(false);
                    item.t.isQuick = true;
                    editAdapter.addData(item.t);
                } else {
                    toast(R.string.edit_limit_8);
                }

            }
        } else if (adapter instanceof MenuEditAdapter) {
            //因为是同一个索引，改变了会同步到主菜单
            editAdapter.getData().get(position).isQuick = false;
            menuAdapter.notifyDataSetChanged();
            editAdapter.remove(position);
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

    @Override
    public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos) {
        viewHolder.itemView.setBackgroundColor(0xf8f8f8f8);
    }

    @Override
    public void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView
            .ViewHolder target, int to) {
    }

    @Override
    public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos) {
        viewHolder.itemView.setBackgroundResource(R.drawable.btn_white);
    }


    //手指按下的点为(x1, y1)手指离开屏幕的点为(x2, y2)
    float y1 = 0;
    float y2 = 0;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (isEdit) return false;
        //继承了Activity的onTouchEvent方法，直接监听点击事件
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //当手指按下的时候
            y1 = event.getY();
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            //当手指离开的时候
            y2 = event.getY();
            if (y2 - y1 > 50) {
                hideEditMenu();
            }
        }
        return false;
    }
}
