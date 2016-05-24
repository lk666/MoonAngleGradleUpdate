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
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.storage.ResultStock;
import cn.com.bluemoon.delivery.utils.LogUtils;

/**
 * Created by wangshanhai on 2016/4/28.
 */
/*库存汇总信息查询接口 */
/* 返回： ResultStock */
public class QueryStockSummaryTest extends InstrumentationTestCase {

    private String Tag;

    public QueryStockSummaryTest() {
        super();
        AppContext.getInstance();
        Tag = "QueryStockSummaryTest";
    }

    public void testQueryStockSummarySuccess() throws Throwable {
        final CountDownLatch signal = new CountDownLatch(1);
        final String token = ClientStateManager.getLoginToken(AppContext.getInstance());
        final StringBuilder strBuilder = new StringBuilder();

        runTestOnUiThread(new Runnable() { // THIS IS THE KEY TO SUCCESS
            @Override
            public void run() {
                AsyncHttpResponseHandler loginoutHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

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

                DeliveryApi.logout(token, loginoutHandler);

            }
        });

        try {
            signal.await(10, TimeUnit.SECONDS); // wait for callback
        } catch (InterruptedException e) {
            LogUtils.i(Tag, "timeOut");
        }
        try {
            ResultStock tokenResult = JSON.parseObject(strBuilder.toString(),
                    ResultStock.class);
            Assert.assertTrue(tokenResult.isSuccess);
            Assert.assertEquals(0, tokenResult.getResponseCode());
            Assert.assertNotNull(tokenResult.getStockList());
            if(tokenResult.getStockList().size()>0){
                for(int i=0;i<tokenResult.getStockList().size();i++){
                    Assert.assertNotNull(tokenResult.getStockList().get(i).getStoreCode());
                    Assert.assertNotNull(tokenResult.getStockList().get(i).getStoreName());
                    Assert.assertNotNull(tokenResult.getStockList().get(i).getTotalCase());
                    Assert.assertNotNull(tokenResult.getStockList().get(i).getTotalCategory());
                    Assert.assertNotNull(tokenResult.getStockList().get(i).getTotalNum());
                    Assert.assertNotNull(tokenResult.getStockList().get(i).getTotalPrice());
                }
            }

        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

}
