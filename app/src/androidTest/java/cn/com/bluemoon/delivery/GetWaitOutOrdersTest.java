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
import cn.com.bluemoon.delivery.app.api.model.inventory.ResultOrderVo;
import cn.com.bluemoon.delivery.utils.LogUtils;

/**
 * Created by wangshanhai on 2016/4/27.
 */
/*待发货-汇总接口 */
/* 返回： ResultOrderVo */
public class GetWaitOutOrdersTest extends InstrumentationTestCase {

    private String Tag;

    public GetWaitOutOrdersTest() {
        super();
        AppContext.getInstance();
        Tag = "GetWaitOutOrdersTest";
    }

    public void testGetWaitOutOrdersSuccess() throws Throwable {
        final CountDownLatch signal = new CountDownLatch(1);
        final String token = ClientStateManager.getLoginToken(AppContext.getInstance());
        final StringBuilder strBuilder = new StringBuilder();

        runTestOnUiThread(new Runnable() { // THIS IS THE KEY TO SUCCESS
            @Override
            public void run() {
                AsyncHttpResponseHandler inventoryOrderHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

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

                DeliveryApi.getWaitOutOrders(token, inventoryOrderHandler);

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
            Assert.assertNotNull(tokenResult.getOrderList());
            if(tokenResult.getOrderList().size()>0){
                for(int i=0;i<tokenResult.getOrderList().size();i++){
                    Assert.assertNotNull(tokenResult.getOrderList().get(i).getOrderCode());
                    Assert.assertNotNull(tokenResult.getOrderList().get(i).getOrderDate());
                    Assert.assertNotNull(tokenResult.getOrderList().get(i).getTotalAmountRmb());
                    Assert.assertNotNull(tokenResult.getOrderList().get(i).getTotalCase());
                    Assert.assertNotNull(tokenResult.getOrderList().get(i).getTotalNum());
                }
            }
        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

}
