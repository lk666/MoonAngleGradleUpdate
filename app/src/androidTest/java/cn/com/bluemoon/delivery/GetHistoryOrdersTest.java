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
import cn.com.bluemoon.delivery.app.api.model.ResultOrder;
import cn.com.bluemoon.delivery.utils.LogUtils;

/**
 * Created by wangshanhai on 2016/4/22.
 */
	/* 2.2.10 获取历史订单列表 */
	/* 返回： ResultOrder */
public class GetHistoryOrdersTest extends InstrumentationTestCase {

    private String Tag;

    public GetHistoryOrdersTest() {
        super();
        AppContext.getInstance();
        Tag = "GetHistoryOrdersTest";
    }

    public void testGetHistoryOrdersSuccess() throws Throwable {
        final CountDownLatch signal = new CountDownLatch(1);
        final String token = ClientStateManager.getLoginToken(AppContext.getInstance());
        final String orderType = "dispatch";
        final long timestamp = 0;
        final long startTime = 0;
        final long endTime = 0;

        final StringBuilder strBuilder = new StringBuilder();

        runTestOnUiThread(new Runnable() { // THIS IS THE KEY TO SUCCESS
            @Override
            public void run() {
                AsyncHttpResponseHandler historyHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

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

                DeliveryApi.getHistoryOrders(token, orderType,
                        AppContext.PAGE_SIZE, timestamp, startTime, endTime, historyHandler);

            }
        });

        try {
            signal.await(10, TimeUnit.SECONDS); // wait for callback
        } catch (InterruptedException e) {
            LogUtils.i(Tag, "timeOut");
        }
        try {
            ResultOrder tokenResult = JSON.parseObject(strBuilder.toString(),
                    ResultOrder.class);
            Assert.assertTrue(tokenResult.isSuccess);
            Assert.assertEquals(0, tokenResult.getResponseCode());
            Assert.assertNotNull(tokenResult.getItemList());
            if(tokenResult.getItemList().size()>0){
                for(int i=0;i<tokenResult.getItemList().size();i++){
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getOrderId());
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getAddress());
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getOrderSource());
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getSignTime());
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getTotalPrice());
                }
            }

        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

}
