package cn.com.bluemoon.delivery.common;

import android.app.Activity;
import android.content.Intent;

import com.alibaba.fastjson.JSON;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.com.bluemoon.delivery.module.contract.PactSignPDFActivity;
import cn.com.bluemoon.lib_iflytek.utils.JsonParser;

/**
 * Created by bm on 2018/1/12.
 */

public class PublicLinkManager {

    public static final String ID = "id";

    public static final Map<String, Class> PAGE_EVENT = new HashMap<String, Class>() {
        {
            //跳转电子合同页面
            put("contractPDF", PactSignPDFActivity.class);
        }
    };

    public static class EventBean {
        public String id;
    }

    /**
     * 跳转到具体页面
     *
     * @param data      跳转所需要的参数，json格式
     * @param pageEvent 页面编码
     */
    public static boolean gotoPage(Activity aty, String data, String pageEvent, int requestCode) {
        try {
            if (PublicLinkManager.PAGE_EVENT.containsKey(pageEvent)) {
                EventBean bean = JSON.parseObject(data, EventBean.class);
                if (bean != null) {
                    Intent intent = new Intent(aty, PublicLinkManager.PAGE_EVENT.get(pageEvent));
                    intent.putExtra(PublicLinkManager.ID, bean.id);
                    aty.startActivityForResult(intent, requestCode );
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
