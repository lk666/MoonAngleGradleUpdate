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
import cn.com.bluemoon.delivery.app.api.model.ResultDict;
import cn.com.bluemoon.delivery.utils.LogUtils;

/**
 * Created by wangshanhai on 2016/4/22.
 */
	/* 2.6.1 获取业务字典接口 */
	/* 返回： ResultDict */
public class GetDictInfoTest extends InstrumentationTestCase {

    private String Tag;

    public GetDictInfoTest() {
        super();
        AppContext.getInstance();
        Tag = "GetDictInfoTest";
    }

    public void testUserLogoutSuccess() throws Throwable {
        final CountDownLatch signal = new CountDownLatch(1);
        final String token = ClientStateManager.getLoginToken(AppContext.getInstance());
        final StringBuilder strBuilder = new StringBuilder();

        runTestOnUiThread(new Runnable() { // THIS IS THE KEY TO SUCCESS
            @Override
            public void run() {
                AsyncHttpResponseHandler dictInfoHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

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

                DeliveryApi.getDictInfo(dictInfoHandler);

            }
        });

        try {
            signal.await(10, TimeUnit.SECONDS); // wait for callback
        } catch (InterruptedException e) {
            LogUtils.i(Tag, "timeOut");
        }
        try {
            ResultDict tokenResult = JSON.parseObject(strBuilder.toString(),
                    ResultDict.class);
            Assert.assertTrue(tokenResult.isSuccess);
            Assert.assertEquals(0, tokenResult.getResponseCode());
            Assert.assertNotNull(tokenResult.getItemList());
            if(tokenResult.getItemList().size()>0){
                for(int i=0;i<tokenResult.getItemList().size();i++){
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getDictId());
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getDictName());
                }
            }

        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

}
