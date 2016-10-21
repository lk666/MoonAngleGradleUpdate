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
import cn.com.bluemoon.delivery.app.api.model.other.ResultAngelQr;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.utils.LogUtils;

/**
 * Created by wangshanhai on 2016/4/22.
 */
	/* 2.4.1 月亮天使获取二维码 */
	/* 返回： ResultAngelQr */
public class MoonAngelQrCodeServiceTest extends InstrumentationTestCase {

    private String Tag;

    public MoonAngelQrCodeServiceTest() {
        super();
        AppContext.getInstance();
        Tag = "MoonAngelQrCodeServiceTest";
    }

    public void testMoonAngelQrCodeServiceSuccess() throws Throwable {
        final CountDownLatch signal = new CountDownLatch(1);
        final String token = ClientStateManager.getLoginToken(AppContext.getInstance());
        final StringBuilder strBuilder = new StringBuilder();

        runTestOnUiThread(new Runnable() { // THIS IS THE KEY TO SUCCESS
            @Override
            public void run() {
                AsyncHttpResponseHandler angelCodeHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

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

                DeliveryApi.moonAngelQrCodeService(token, angelCodeHandler);

            }
        });

        try {
            signal.await(10, TimeUnit.SECONDS); // wait for callback
        } catch (InterruptedException e) {
            LogUtils.i(Tag, "timeOut");
        }
        try {
            ResultAngelQr tokenResult = JSON.parseObject(strBuilder.toString(),
                    ResultAngelQr.class);
            Assert.assertTrue(tokenResult.isSuccess);
            Assert.assertEquals(0, tokenResult.getResponseCode());
            Assert.assertNotNull(tokenResult.getQrcode());
            Assert.assertNotNull(tokenResult.getOrgCode());
            Assert.assertNotNull(tokenResult.getOrgName());
            Assert.assertNotNull(tokenResult.getQrcode());

        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

}
