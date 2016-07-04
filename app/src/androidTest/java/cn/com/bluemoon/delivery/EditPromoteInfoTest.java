package cn.com.bluemoon.delivery;

import android.test.InstrumentationTestCase;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.jobrecord.PeopleFlow;
import cn.com.bluemoon.delivery.app.api.model.jobrecord.ResultPromoteInfo;
import cn.com.bluemoon.delivery.utils.LogUtils;

/**
 * Created by LIANGJIANGLI on 2016/6/22.
 */
public class EditPromoteInfoTest extends InstrumentationTestCase {

    @Test
    public void test() {
        ResultPromoteInfo.PromoteInfo info = null;
        info.setAddress("oifoioidio");
        info.setBpCode("jfkkjkdjfk");
        info.setBpCode1("jfkkjkdjfk");
        info.setBpName("jfkjsjkjf");
        info.setBpName1("jfkjsjkjf");
        info.setCashPledge("98328938");
        info.setHolidayPrice("8ujdjj");
        PeopleFlow flow = new PeopleFlow();
        flow.setAddress("flowkjdjkjdjkfjk");
        flow.setCreateDate(9484848);
        flow.setEndTime(3434);
        flow.setPeopleStatus("ijsdkjsjk");
        List<PeopleFlow> list = new ArrayList<PeopleFlow>();
        list.add(flow);
        info.setPeopleFlow(list);
        List<String> imgList = new ArrayList<String>();
        imgList.add("http://8939");
        imgList.add("http://89e");
        imgList.add("http://893e");
        //info.setPicInfo(imgList);

        DeliveryApi.editPromoteInfo("jdjjd", info, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                LogUtils.d("test", "success="+bytes.toString());
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LogUtils.d("test", "faild="+bytes.toString());
            }
        });

    }
}
