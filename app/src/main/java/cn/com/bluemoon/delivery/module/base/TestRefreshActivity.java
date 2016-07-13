package cn.com.bluemoon.delivery.module.base;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.clothing.ResultUserInfo;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshScrollView;

public class TestRefreshActivity extends BaseActionBarActivity {

    @Bind(R.id.tv_code)
    TextView tvCode;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_phone)
    TextView tvPhone;

    @Bind(R.id.ptrsv)
    PullToRefreshScrollView ptrsv;

    View viewstubError;
    View viewstubEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_refresh);
        ButterKnife.bind(this);

        setIntentData();
        initView();
        getData();
    }

    @Override
    protected int getActionBarTitleRes() {
        return R.string.title_clothing_book_in;
    }

    private void initView() {
        setViewVisibility(ptrsv, View.GONE);
        ptrsv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                getData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
            }
        });
    }

    private void setIntentData() {
    }

    public void getData() {
        setChildEnableRecursion(ptrsv, false);
        DeliveryApi.getEmp(ClientStateManager.getLoginToken(this), "80474765",
                createScrollViewRefreshResponseHandler(new IRefreshHttpResponseHandler() {

                    @Override
                    public void onResponseException(String responseString, Exception e) {
                        showNetErrorView();
                    }

                    @Override
                    public void onResponseFailure(int statusCode, Header[] headers, String
                            responseString, Throwable throwable) {
                        showNetErrorView();
                    }

                    @Override
                    public void onResponseSuccess(String responseString) {
                        ResultUserInfo result = JSON.parseObject(responseString,
                                ResultUserInfo.class);
                        // 判断数据是否为空
                        if (is) {
                            showEmptyView();
                        } else {
                            setData(result);
                        }
                        is = !is;
                    }
                }));
    }

    private void setData(ResultUserInfo result) {
        PublicUtil.showToast("asdsadsada");
        setViewVisibility(ptrsv, View.VISIBLE);
        tvCode.setText(result.getEmpCode());
        tvName.setText(result.getEmpName());
        tvPhone.setText(result.getPhone());
        setViewVisibility(viewstubEmpty, View.GONE);
        setViewVisibility(viewstubError, View.GONE);
    }

    void setViewVisibility(View view, int visibility) {
        if (view != null && view.getVisibility() != visibility) {
            view.setVisibility(visibility);
        }
    }

    // TODO: lk 2016/7/13 enable测试 

    /**
     * 显示空数据页
     */
    private void showEmptyView() {
        try {
            if (viewstubEmpty == null) {
                int layoutId = R.layout.view_empty;
                if (layoutId != 0) {
                    final View viewStub = findViewById(R.id.viewstub_empty);
                    if (viewStub != null) {
                        final ViewStub stub = (ViewStub) viewStub;
                        stub.setLayoutResource(layoutId);
                        viewstubEmpty = stub.inflate();
                        setEmptyViewEvent(viewstubEmpty);
                    }
                }
            }

            setViewVisibility(viewstubEmpty, View.VISIBLE);
            setViewVisibility(viewstubError, View.GONE);
            setViewVisibility(ptrsv, View.GONE);
        } catch (Exception e) {
            LogUtils.e(getDefaultTag(), e.getMessage());
        }
    }

    private void setEmptyViewEvent(View viewstubEmpty) {
        viewstubEmpty.findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });
    }

    /**
     * 显示错误页
     */
    private void showNetErrorView() {
        try {
            if (viewstubError == null) {
                int layoutId = R.layout.view_error;
                if (layoutId != 0) {
                    final View viewStub = findViewById(R.id.viewstub_error);
                    if (viewStub != null) {
                        final ViewStub stub = (ViewStub) viewStub;
                        stub.setLayoutResource(layoutId);
                        viewstubError = stub.inflate();
                        setErrorViewEvent(viewstubError);
                    }
                }
            }

            setViewVisibility(viewstubEmpty, View.GONE);
            setViewVisibility(viewstubError, View.VISIBLE);
            setViewVisibility(ptrsv, View.GONE);
        } catch (Exception e) {
            LogUtils.e(getDefaultTag(), e.getMessage());
        }
    }

    private void setErrorViewEvent(View viewstubError) {
        viewstubError.findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });
    }

    /**
     * 递归设置子控件的enable
     *
     * @param layout
     * @param isEnable
     */
    private static void setChildEnableRecursion(ViewGroup layout, boolean isEnable) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof ViewGroup) {
                setChildEnableRecursion((ViewGroup) child, isEnable);
            } else {
                if (isEnable) {
                    child.setEnabled((Boolean) child.getTag(R.id.tag_ori_enable));
                } else {
                    child.setTag(R.id.tag_ori_enable, child.isEnabled());
                    child.setEnabled(false);
                }
            }
        }
    }

    boolean is = true;

    /**
     * 创建一个通用的拓展AsyncHttpResponseHandler
     *
     * @param callback
     * @return
     */
    protected AsyncHttpResponseHandler createScrollViewRefreshResponseHandler(
            final IRefreshHttpResponseHandler callback) {
        return new TextHttpResponseHandler(
                HTTP.UTF_8) {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (ptrsv == null) {
                    return;
                }
                LogUtils.d(getDefaultTag(), "baseExtendHandler result = " + responseString);
                ptrsv.onRefreshComplete();
                setChildEnableRecursion(ptrsv, true);
                try {
                    ResultBase result = JSON.parseObject(responseString,
                            ResultBase.class);
                    if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                        if (callback != null) {
                            callback.onResponseSuccess(responseString);
                        }
                    } else {
                        PublicUtil.showErrorMsg(TestRefreshActivity.this, result);
                    }
                } catch (Exception e) {
                    LogUtils.e(getDefaultTag(), e.getMessage());
                    PublicUtil.showToastServerBusy();
                    if (callback != null) {
                        callback.onResponseException(responseString, e);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  String responseString, Throwable throwable) {
                if (ptrsv == null) {
                    return;
                }
                LogUtils.e(getDefaultTag(), throwable.getMessage());
                ptrsv.onRefreshComplete();
                setChildEnableRecursion(ptrsv, true);

                if (callback != null) {
                    callback.onResponseFailure(statusCode, headers, responseString, throwable);
                }
                PublicUtil.showToastServerOvertime();

            }
        };
    }

    /**
     * 封装AsyncHttpResponseHandler的回调(刷新用)
     */
    public interface IRefreshHttpResponseHandler extends IHttpResponseHandler {

        /**
         * 响应成功，onSuccess,但抛错的情况
         */
        void onResponseException(String responseString, Exception e);

        /**
         * onFailure的情况
         */
        void onResponseFailure(int statusCode, Header[] headers, String responseString, Throwable
                throwable);
    }
}
