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
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.other.ResultOrderVo;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.entity.OrderType;
import cn.com.bluemoon.delivery.utils.LogUtils;

/**
 * Created by wangshanhai on 2016/4/22.
 */
public class OrdersGetByTypeTest extends InstrumentationTestCase {

    private String Tag;
    public OrdersGetByTypeTest() {
        super();
        AppContext.getInstance();
        Tag = "OrdersGetByTypeTest";
    }

    public void testGetOrdersByAcceptSuccess() throws Throwable {
        final CountDownLatch signal = new CountDownLatch(1);
        final String token = ClientStateManager.getLoginToken(AppContext.getInstance());
        final StringBuilder strBuilder = new StringBuilder();

        runTestOnUiThread(new Runnable() { // THIS IS THE KEY TO SUCCESS
            @Override
            public void run() {
                AsyncHttpResponseHandler getOrdersHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

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

                DeliveryApi.getOrdersByType(token,
                        OrderType.PENDINGORDERS, getOrdersHandler);

            }
        });

        try {
            signal.await(10, TimeUnit.SECONDS); // wait for callback
        } catch (InterruptedException e) {
            LogUtils.i(Tag, "timeOut");
        }
        try {
            ResultOrderVo tokenResult = JSON.parseObject(strBuilder.toString(),
                    ResultOrderVo.class);
            Assert.assertTrue(tokenResult.isSuccess);
            Assert.assertEquals(0, tokenResult.getResponseCode());
            Assert.assertNotNull(tokenResult.getItemList());
            if(tokenResult.getItemList().size()>0){
                for(int i=0;i<tokenResult.getItemList().size();i++){
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getOrderSource());
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getTotalPrice());
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getCateAmount());
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getAddress());
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getCustomerName());
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getDispatchId());
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getExchangeState());
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getMobilePhone());
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getOrderId());
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getStorechargeCode());
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getRegion());
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getPayOrderTime());

                }
            }


        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

    public void testGetOrdersByAppointmentSuccess() throws Throwable {
        final CountDownLatch signal = new CountDownLatch(1);
        final String token = ClientStateManager.getLoginToken(AppContext.getInstance());
        final StringBuilder strBuilder = new StringBuilder();

        runTestOnUiThread(new Runnable() { // THIS IS THE KEY TO SUCCESS
            @Override
            public void run() {
                AsyncHttpResponseHandler getOrdersHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

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

                DeliveryApi.getOrdersByType(token,
                        OrderType.PENDINGAPPOINTMENT, getOrdersHandler);

            }
        });

        try {
            signal.await(10, TimeUnit.SECONDS); // wait for callback
        } catch (InterruptedException e) {
            LogUtils.i(Tag, "timeOut");
        }
        try {
            ResultOrderVo tokenResult = JSON.parseObject(strBuilder.toString(),
                    ResultOrderVo.class);
            Assert.assertTrue(tokenResult.isSuccess);
            Assert.assertEquals(0, tokenResult.getResponseCode());
            Assert.assertNotNull(tokenResult.getItemList());
            if(tokenResult.getItemList().size()>0){
                for(int i=0;i<tokenResult.getItemList().size();i++){
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getOrderSource());
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getTotalPrice());
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getCateAmount());
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getAddress());
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getCustomerName());
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getDispatchId());
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getExchangeState());
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getMobilePhone());
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getOrderId());
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getStorechargeCode());
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getRegion());
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getPayOrderTime());

                }
            }

        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

    public void testGetOrdersByDeliverySuccess() throws Throwable {
        final CountDownLatch signal = new CountDownLatch(1);
        final String token = ClientStateManager.getLoginToken(AppContext.getInstance());
        final StringBuilder strBuilder = new StringBuilder();

        runTestOnUiThread(new Runnable() { // THIS IS THE KEY TO SUCCESS
            @Override
            public void run() {
                AsyncHttpResponseHandler getOrdersHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

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

                DeliveryApi.getOrdersByType(token,
                        OrderType.PENDINGDELIVERY, getOrdersHandler);

            }
        });

        try {
            signal.await(10, TimeUnit.SECONDS); // wait for callback
        } catch (InterruptedException e) {
            LogUtils.i(Tag, "timeOut");
        }
        try {
            ResultOrderVo tokenResult = JSON.parseObject(strBuilder.toString(),
                    ResultOrderVo.class);
            Assert.assertTrue(tokenResult.isSuccess);
            Assert.assertEquals(0, tokenResult.getResponseCode());
            Assert.assertNotNull(tokenResult.getItemList());
            if(tokenResult.getItemList().size()>0){
                for(int i=0;i<tokenResult.getItemList().size();i++){
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getOrderSource());
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getTotalPrice());
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getCateAmount());
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getAddress());
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getCustomerName());
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getDispatchId());
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getExchangeState());
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getMobilePhone());
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getOrderId());
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getStorechargeCode());
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getRegion());
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getPayOrderTime());

                }
            }

        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

    public void testGetOrdersBySignSuccess() throws Throwable {
        final CountDownLatch signal = new CountDownLatch(1);
        final String token = ClientStateManager.getLoginToken(AppContext.getInstance());
        final StringBuilder strBuilder = new StringBuilder();

        runTestOnUiThread(new Runnable() { // THIS IS THE KEY TO SUCCESS
            @Override
            public void run() {
                AsyncHttpResponseHandler getOrdersHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

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

                DeliveryApi.getOrdersByType(token,
                        OrderType.PENDINGRECEIPT, getOrdersHandler);

            }
        });

        try {
            signal.await(10, TimeUnit.SECONDS); // wait for callback
        } catch (InterruptedException e) {
            LogUtils.i(Tag, "timeOut");
        }
        try {
            ResultBase tokenResult = JSON.parseObject(strBuilder.toString(),
                    ResultBase.class);
            Assert.assertTrue(tokenResult.isSuccess);
            Assert.assertEquals(0, tokenResult.getResponseCode());

        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

}
