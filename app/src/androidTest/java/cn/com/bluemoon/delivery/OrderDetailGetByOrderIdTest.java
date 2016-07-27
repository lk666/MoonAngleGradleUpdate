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
import cn.com.bluemoon.delivery.app.api.model.ResultOrderInfo;
import cn.com.bluemoon.delivery.utils.LogUtils;

/**
 * Created by wangshanhai on 2016/4/22.
 */
/* 2.2.3 根据外部订单编号获取订单详情 */
	/* 返回： ResultOrderInfo */
public class OrderDetailGetByOrderIdTest extends InstrumentationTestCase {

    private String Tag;

    public OrderDetailGetByOrderIdTest() {
        super();
        AppContext.getInstance();
        Tag = "OrderDetailGetByOrderIdTest";
    }

    public void testGetOrderDetailByOrderIdSuccess() throws Throwable {
        final CountDownLatch signal = new CountDownLatch(1);
        final String token = ClientStateManager.getLoginToken(AppContext.getInstance());
        final String orderId = "C16040611175432959202";
        final StringBuilder strBuilder = new StringBuilder();

        runTestOnUiThread(new Runnable() { // THIS IS THE KEY TO SUCCESS
            @Override
            public void run() {
                AsyncHttpResponseHandler orderDetailHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

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

                DeliveryApi.getOrderDetailByOrderId(token, orderId, orderDetailHandler);

            }
        });

        try {
            signal.await(10, TimeUnit.SECONDS); // wait for callback
        } catch (InterruptedException e) {
            LogUtils.i(Tag, "timeOut");
        }
        try {
            ResultOrderInfo tokenResult = JSON.parseObject(strBuilder.toString(),
                    ResultOrderInfo.class);
            Assert.assertTrue(tokenResult.isSuccess);
            Assert.assertEquals(0, tokenResult.getResponseCode());
            Assert.assertNotNull(tokenResult.getOrderInfo());
            Assert.assertNotNull(tokenResult.getOrderInfo().getOrderId());
            Assert.assertNotNull(tokenResult.getOrderInfo().getProductList());
            Assert.assertNotNull(tokenResult.getOrderInfo().getCateAmount());
            Assert.assertNotNull(tokenResult.getOrderInfo().getAddress());
            Assert.assertNotNull(tokenResult.getOrderInfo().getCustomerName());
            Assert.assertNotNull(tokenResult.getOrderInfo().getDeliveryTime());
            Assert.assertNotNull(tokenResult.getOrderInfo().getDispatchId());
            Assert.assertNotNull(tokenResult.getOrderInfo().getDispatchStatus());
            Assert.assertNotNull(tokenResult.getOrderInfo().getExchangeState());
            Assert.assertNotNull(tokenResult.getOrderInfo().getMobilePhone());
            Assert.assertNotNull(tokenResult.getOrderInfo().getNickName());
            Assert.assertNotNull(tokenResult.getOrderInfo().getNickPhone());

        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

}
