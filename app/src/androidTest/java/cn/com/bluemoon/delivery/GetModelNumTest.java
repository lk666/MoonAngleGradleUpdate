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
import cn.com.bluemoon.delivery.app.api.model.ResultModelNum;
import cn.com.bluemoon.delivery.utils.LogUtils;

/**
 * 首页未读消息数量统计接口
 * Created by wangshanhai on 2016/4/28.
 */
public class GetModelNumTest extends InstrumentationTestCase {

    private String Tag;

    public GetModelNumTest() {
        super();
        AppContext.getInstance();
        Tag = "GetModelNumTest";
    }

    public void testGetModelNumSuccess() throws Throwable {
        final CountDownLatch signal = new CountDownLatch(1);
        final String token = ClientStateManager.getLoginToken(AppContext.getInstance());
        final StringBuilder strBuilder = new StringBuilder();

        runTestOnUiThread(new Runnable() { // THIS IS THE KEY TO SUCCESS
            @Override
            public void run() {
                AsyncHttpResponseHandler getAmountHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

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

                DeliveryApi.getModelNum(token, getAmountHandler);

            }
        });

        try {
            signal.await(10, TimeUnit.SECONDS); // wait for callback
        } catch (InterruptedException e) {
            LogUtils.i(Tag, "timeOut");
        }
        try {
            ResultModelNum tokenResult = JSON.parseObject(strBuilder.toString(),
                    ResultModelNum.class);
            Assert.assertTrue(tokenResult.isSuccess);
            Assert.assertEquals(0, tokenResult.getResponseCode());
            Assert.assertNotNull(tokenResult.getModelBeans());
            if(tokenResult.getModelBeans().size()>0){
                for (int i=0;i<tokenResult.getModelBeans().size();i++){
                    Assert.assertNotNull(tokenResult.getModelBeans().get(i).getMenuId());
                    Assert.assertNotNull(tokenResult.getModelBeans().get(i).getMenunName());
                    Assert.assertNotNull(tokenResult.getModelBeans().get(i).getNum());
                }
            }

        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

}
