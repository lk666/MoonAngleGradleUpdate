package cn.com.bluemoon.delivery;

import android.test.InstrumentationTestCase;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import junit.framework.Assert;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import cn.com.bluemoon.delivery.app.AppContext;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.coupon.ResultCouponAct;
import cn.com.bluemoon.delivery.utils.LogUtils;

/**
 * 2.10.2查询有发券权限的活动
 * Created by wangshanhai on 2016/4/28.
 */
public class GetOwnAuthCouponActTest extends InstrumentationTestCase {

    private String Tag;

    public GetOwnAuthCouponActTest() {
        super();
        AppContext.getInstance();
        Tag = "GetOwnAuthCouponActTest";
    }

    public void testGetOwnAuthCouponActSuccess() throws Throwable {
        final CountDownLatch signal = new CountDownLatch(1);
        final String token = ClientStateManager.getLoginToken(AppContext.getInstance());
        final StringBuilder strBuilder = new StringBuilder();

        runTestOnUiThread(new Runnable() { // THIS IS THE KEY TO SUCCESS
            @Override
            public void run() {
                AsyncHttpResponseHandler getOwnAuthCouponActHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          String responseString) {
                        LogUtils.i(Tag, responseString);
                        strBuilder.append(responseString);

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        LogUtils.i(Tag, "onFailure");
                        strBuilder.append("");
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        LogUtils.i(Tag, "onFinish");
                        signal.countDown();
                    }
                };

                DeliveryApi.getOwnAuthCouponAct(token, getOwnAuthCouponActHandler);

            }
        });

        try {
            signal.await(10, TimeUnit.SECONDS); // wait for callback
        } catch (InterruptedException e) {
            LogUtils.i(Tag, "timeOut");
        }
        try {
            ResultCouponAct tokenResult = JSON.parseObject(strBuilder.toString(),
                    ResultCouponAct.class);
            Assert.assertTrue(tokenResult.isSuccess);
            Assert.assertEquals(0, tokenResult.getResponseCode());
            Assert.assertNotNull(tokenResult.getActivitys());
            if(tokenResult.getActivitys().size()>0){
                for(int i=0;i<tokenResult.getActivitys().size();i++){
                    Assert.assertNotNull(tokenResult.getActivitys().get(i).getActivityCode());
                }
            }

        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

}
