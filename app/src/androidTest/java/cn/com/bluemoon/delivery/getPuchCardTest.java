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
import cn.com.bluemoon.delivery.app.api.model.punchcard.ResultShowPunchCardDetail;
import cn.com.bluemoon.delivery.utils.LogUtils;

/**
 * Created by wangshanhai on 2016/4/28.
 */
	/* 2.9.4 展示打卡信息 */
	/* 返回： ResultShowPunchCardDetail */
public class getPuchCardTest extends InstrumentationTestCase {

    private String Tag;

    public getPuchCardTest() {
        super();
        AppContext.getInstance();
        Tag = "getPuchCardTest";
    }

    public void testgetPuchCardSuccess() throws Throwable {
        final CountDownLatch signal = new CountDownLatch(1);
        final String token = ClientStateManager.getLoginToken(AppContext.getInstance());
        final StringBuilder strBuilder = new StringBuilder();

        runTestOnUiThread(new Runnable() { // THIS IS THE KEY TO SUCCESS
            @Override
            public void run() {
                AsyncHttpResponseHandler getPunchCardHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

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

                DeliveryApi.getPunchCard(token, getPunchCardHandler);

            }
        });

        try {
            signal.await(10, TimeUnit.SECONDS); // wait for callback
        } catch (InterruptedException e) {
            LogUtils.i(Tag, "timeOut");
        }
        try {
            ResultShowPunchCardDetail tokenResult = JSON.parseObject(strBuilder.toString(),
                    ResultShowPunchCardDetail.class);
            Assert.assertTrue(tokenResult.isSuccess);
            Assert.assertEquals(0, tokenResult.getResponseCode());
            Assert.assertNotNull(tokenResult.getWorkTaskList());
            if(tokenResult.getWorkTaskList().size()>0){
                for(int i=0;i<tokenResult.getWorkTaskList().size();i++){
                    Assert.assertNotNull(tokenResult.getWorkTaskList().get(i).getTaskCode());
                    Assert.assertNotNull(tokenResult.getWorkTaskList().get(i).getTaskName());
                    Assert.assertNotNull(tokenResult.getWorkTaskList().get(i).getWorkTaskType());
                }
            }

        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

}
