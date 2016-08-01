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

import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultToken;
import cn.com.bluemoon.delivery.utils.LogUtils;

/**
 * Created by allenli on 2016/4/21.
 */
public class SSOLoginTest extends InstrumentationTestCase {

    private String Tag;

    public SSOLoginTest() {
        super();
        AppContext.getInstance();
        Tag ="SSOLoginTest";
    }


    public void testSSOLoginSuccessAccount() throws  Throwable{
        final CountDownLatch signal = new CountDownLatch(1);
        final String account = "80101064";
        final String password = "000000";
        final StringBuilder strBuilder = new StringBuilder();




        runTestOnUiThread(new Runnable() { // THIS IS THE KEY TO SUCCESS
            @Override
            public void run() {
                AsyncHttpResponseHandler loginHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

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

                DeliveryApi.ssoLogin(account, password, loginHandler);

            }
        });

        try {
            signal.await(10, TimeUnit.SECONDS); // wait for callback
        } catch (InterruptedException e) {
            LogUtils.i(Tag, "timeOut");
        }

        try {
            ResultToken tokenResult = JSON.parseObject(strBuilder.toString(),
                    ResultToken.class);

            Assert.assertTrue(tokenResult.isSuccess);
            Assert.assertEquals(0,tokenResult.getResponseCode());
            Assert.assertNotNull(tokenResult.getToken());

        } catch (Exception e) {
            Assert.assertTrue(false);
        }


    }


    public void testSSOLoginErrorPassword() throws  Throwable{
        final CountDownLatch signal = new CountDownLatch(1);
        final String account = "80101064";
        final String password = "123456";
        final StringBuilder strBuilder = new StringBuilder();


        runTestOnUiThread(new Runnable() { // THIS IS THE KEY TO SUCCESS
            @Override
            public void run() {
                AsyncHttpResponseHandler loginHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          String responseString) {
                        LogUtils.i(Tag,responseString);
                        strBuilder.append(responseString);

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        LogUtils.i(Tag,"onFailure");
                        strBuilder.append("");
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        LogUtils.i(Tag, "onFinish");
                        signal.countDown();
                    }
                };

                DeliveryApi.ssoLogin(account, password, loginHandler);

            }
        });

        try {
            signal.await(10, TimeUnit.SECONDS); // wait for callback
        } catch (InterruptedException e) {
            LogUtils.i(Tag, "timeOut");
        }

        try {
            ResultToken tokenResult = JSON.parseObject(strBuilder.toString(),
                    ResultToken.class);

            Assert.assertTrue(tokenResult.isSuccess);
            Assert.assertEquals(2204,tokenResult.getResponseCode());
            Assert.assertNull(tokenResult.getToken());

        } catch (Exception e) {
            LogUtils.i(Tag, e.getMessage());
            Assert.assertTrue(false);
        }


    }
}
