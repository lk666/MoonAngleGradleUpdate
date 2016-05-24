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
import cn.com.bluemoon.delivery.app.api.model.ResultVenueInfo;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;

/**
 * Created by wangshanhai on 2016/4/22.
 */
	/* 2.8.1 获取场馆/场次列表 */
	/* 返回： ResultVenueInfo */
public class GetVenueListTest extends InstrumentationTestCase {

    private String Tag;

    public GetVenueListTest() {
        super();
        AppContext.getInstance();
        Tag = "GetVenueListTest";
    }

    public void testGetVenueListSuccess() throws Throwable {
        final CountDownLatch signal = new CountDownLatch(1);
        final String token = ClientStateManager.getLoginToken(AppContext.getInstance());
        final StringBuilder strBuilder = new StringBuilder();

        runTestOnUiThread(new Runnable() { // THIS IS THE KEY TO SUCCESS
            @Override
            public void run() {
                AsyncHttpResponseHandler venueListHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

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

                DeliveryApi.getVenueList(token, Constants.TYPE_VENUE, "",
                        venueListHandler);

            }
        });

        try {
            signal.await(10, TimeUnit.SECONDS); // wait for callback
        } catch (InterruptedException e) {
            LogUtils.i(Tag, "timeOut");
        }
        try {
            ResultVenueInfo tokenResult = JSON.parseObject(strBuilder.toString(),
                    ResultVenueInfo.class);
            Assert.assertTrue(tokenResult.isSuccess);
            Assert.assertEquals(0, tokenResult.getResponseCode());
            Assert.assertNotNull(tokenResult.getItemList());
            if(tokenResult.getItemList().size()>0){
                for (int i=0;i<tokenResult.getItemList().size();i++){
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getVenueCode());
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getVenueSname());
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getTimesCode());
                    Assert.assertNotNull(tokenResult.getItemList().get(i).getTimesName());
                }
            }

        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

}
