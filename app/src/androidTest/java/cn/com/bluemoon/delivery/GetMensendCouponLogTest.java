package cn.com.bluemoon.delivery;

import android.test.InstrumentationTestCase;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import junit.framework.Assert;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import cn.com.bluemoon.delivery.app.AppContext;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultToken;
import cn.com.bluemoon.delivery.app.api.model.coupon.ResultMensendLog;
import cn.com.bluemoon.delivery.utils.LogUtils;

/**
 * Created by allenli on 2016/4/25.
 */
public class GetMensendCouponLogTest extends InstrumentationTestCase {

    private String Tag;

    public GetMensendCouponLogTest() {
        super();
        AppContext.getInstance();
        Tag ="GetMensendCouponLogTest";
    }


    public void testGetMensendCouponLogSuccess() throws  Throwable{
        final CountDownLatch signal = new CountDownLatch(1);
        final String account = "80101064";
        final String password = "000000";
        final StringBuilder strBuilder = new StringBuilder();




        runTestOnUiThread(new Runnable() { // THIS IS THE KEY TO SUCCESS
            @Override
            public void run() {
                AsyncHttpResponseHandler mensendCouponHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

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

                DeliveryApi.getMensendCouponLog(ClientStateManager.getLoginToken(AppContext.getInstance()), new Date().getTime(), mensendCouponHandler);

            }
        });

        try {
            signal.await(10, TimeUnit.SECONDS); // wait for callback
        } catch (InterruptedException e) {
            LogUtils.i(Tag, "timeOut");
        }

        try {
            ResultMensendLog mensenResult = JSON.parseObject(strBuilder.toString(),
                    ResultMensendLog.class);

            Assert.assertTrue(mensenResult.isSuccess);
            Assert.assertEquals(0, mensenResult.getResponseCode());
            Assert.assertNotNull(mensenResult.getMensendLogs());
            Assert.assertNotNull(mensenResult.getTotal());

        } catch (Exception e) {
            Assert.assertTrue(false);
        }


    }

}
