package cn.com.bluemoon.delivery.common;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import bluemoon.com.lib_x5.bean.BaseParam;
import cn.com.bluemoon.delivery.module.contract.PactSignPDFActivity;
import cn.com.bluemoon.delivery.module.ptxs60.GroupBuyListActivity;
import cn.com.bluemoon.delivery.utils.LogUtils;

/**
 * Created by bm on 2018/1/12.
 */

public class PublicLinkManager {

    public static final String ID = "id";
    public static final String PDF_CODE = "pdfCode";

    public static final Map<String, Class> PAGE_EVENT = new HashMap<String, Class>() {
        {
            //跳转电子合同页面
            put("contractPDF", PactSignPDFActivity.class);
            //拼团销售
            put("groupSale", GroupBuyListActivity.class);
        }
    };

    public static class EventBean {
        public String id;
    }

    /**
     * 返回结果，必须继承BaseParam
     * 电子合同：
     * 0 表示不执行
     * 1 表示执行成功，跳转到已完成列表
     * 2 表示执行失败，电子合同已经被取消，需要刷新列表
     */
    public static class ResultBean extends BaseParam {

        public ResultData data;

        public ResultBean(boolean isSuccess, int pdfCode) {
            this.isSuccess = isSuccess;
            this.data = new ResultData(pdfCode);
        }

        public static class ResultData implements Serializable {
            public int pdfCode;

            public ResultData(int pdfCode) {
                this.pdfCode = pdfCode;
            }
        }


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
                Intent intent = new Intent(aty, PublicLinkManager.PAGE_EVENT.get(pageEvent));
                if (!TextUtils.isEmpty(data)) {
                    EventBean bean = JSON.parseObject(data, EventBean.class);
                    if (bean != null) {
                        intent.putExtra(PublicLinkManager.ID, bean.id);
                    }
                }
                aty.startActivityForResult(intent, requestCode);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
