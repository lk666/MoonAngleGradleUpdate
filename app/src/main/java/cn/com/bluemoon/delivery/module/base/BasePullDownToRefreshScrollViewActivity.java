package cn.com.bluemoon.delivery.module.base;

import android.view.View;
import android.view.ViewStub;

import com.loopj.android.http.AsyncHttpResponseHandler;

import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;

// TODO: lk 2016/7/14 缺少一种Mode.Both的刷新基类
/**
 * 普通下拉刷新页面，自动显示空数据页面和网络错误页面，不需在onCreate中调用 ButterKnife.bind(this)
 */
@Deprecated
public abstract class BasePullDownToRefreshScrollViewActivity extends BasePullToRefreshActivity {

    /**
     * 实际的内容页面View
     */
    private View contentView;

    @Override
    final protected int getLayoutId() {
        return R.layout.activity_pull_to_refresh_scroll_view;
    }

    @Override
    final protected void initPtr(PullToRefreshBase ptr) {
    }

    private void initContent() {
        if (contentView == null) {
            int layoutId = getContentViewLayoutId();
            View viewStub = findViewById(R.id.viewstub_content);
            if (viewStub != null) {
                final ViewStub stub = (ViewStub) viewStub;
                stub.setLayoutResource(layoutId);
                contentView = stub.inflate();
                ButterKnife.bind(this);
                initContentView(contentView);
            }
        }

        showRefreshView();
    }

    @Override
    final protected int getPtrId() {
        return R.id.ptrsv;
    }

    /**
     * 设置inflate后的具体内容的view/viewgroup
     *
     * @param contentView
     */
    protected abstract void initContentView(View contentView);

    /**
     * 获取具体内容的layout的id
     */
    protected abstract int getContentViewLayoutId();

    @Override
    final protected void setGetData(Object result) {
        // 判断数据是否为空
        if (isDataEmpty(result)) {
            showEmptyView();
        } else {
            initContent();
            setGetDataObj(result);
        }
    }

    /**
     * 设置请求成功的数据（数据不为空）
     *
     * @param result 继承ResultBase的classType数据，不为null，也非空数据
     * @return
     */
    protected abstract void setGetDataObj(Object result);

    /**
     * 判断数据是否为empty
     *
     * @param result 继承ResultBase的classType数据，不为null
     * @return
     */
    protected abstract boolean isDataEmpty(Object result);

    @Override
    final protected void setGetMore(Object result) {
//        // 判断数据是否为空
//        if (isDataEmpty(result)) {
////            showEmptyView();
//        } else {
//            setGetMoreObj(result);
//        }
    }

//     protected abstract void setGetMoreObj(Object result);

    @Override
    final protected void invokeGetMoreDeliveryApi(AsyncHttpResponseHandler handler) {
    }

    @Override
    final protected PullToRefreshBase.Mode getMode() {
        return PullToRefreshBase.Mode.PULL_FROM_START;
    }
}