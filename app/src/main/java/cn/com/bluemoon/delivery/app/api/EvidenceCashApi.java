package cn.com.bluemoon.delivery.app.api;

import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Decoder.BASE64Encoder;
import cn.com.bluemoon.delivery.app.api.model.wash.manager.ImageInfo;
import cn.com.bluemoon.delivery.module.wash.returning.clothescheck.CheckBackOrder;
import cn.com.bluemoon.delivery.module.wash.returning.clothescheck.UploadImage;

/**
 * 我的保证金API
 * Created by ljl on 2016/11/16
 */
public class EvidenceCashApi extends DeliveryApi {
    /**
     *我的独家许可保证金
     * @param token token身份检验码 String
     */
    public static void cash(String token,AsyncHttpResponseHandler handler){
        if(null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("token",token);
        postRequest(params, "bluemoon-control/evidence/cash%s", handler);
    }

    /**
     *获取银行转账模板
     * @param token token身份检验码 String
     */
    public static void module(String token,AsyncHttpResponseHandler handler){
        if(null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("token",token);
        postRequest(params, "bluemoon-control/evidence/module%s", handler);
    }

    /**
     *获取保证金套餐
     * @param token token身份检验码 String
     */
    public static void combo(String token,AsyncHttpResponseHandler handler){
        if(null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("token",token);
        postRequest(params, "bluemoon-control/evidence/combo%s", handler);
    }

    /**
     *调用支付宝\微信接口\转账凭证
     * @param cashAmount 转账凭证金额或在线支付金额 int
     * @param evidencePath 回执单图片路径 String
     * @param token token身份检验码 String
     * @param type 操作方式 String
     */
    public static void saveCashInfo(long cashAmount,String evidencePath,String token,String type,AsyncHttpResponseHandler handler){
        if(null == token||null == type) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("cashAmount",cashAmount);
        params.put("evidencePath",evidencePath);
        params.put("token",token);
        params.put("type",type);
        postRequest(params, "bluemoon-control/evidence/saveCashInfo%s", handler);
    }

    /**
     *保证金明细列表
     * @param pageIndex 当前页码 int
     * @param token token身份检验码 String
     */
    public static void cashList(long pageIndex,String token,AsyncHttpResponseHandler handler){
        if(null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("pageIndex",pageIndex);
        params.put("pageSize", 10);
        params.put("token",token);
        postRequest(params, "bluemoon-control/evidence/cashList%s", handler);
    }

    /**
     *图片上传
     * @param file 图片数据
     * @param token token身份检验码 String
     */
    public static void uploadImg(byte[] file,String token,AsyncHttpResponseHandler handler){
        if(null == file||null == token) {
            return;
        }
        BASE64Encoder encoder = new BASE64Encoder();
        String fileString = encoder.encode(file);
        Map<String, Object> params = new HashMap<>();
        params.put("imgInfo",fileString);
        params.put("token",token);
        postRequest(params, "bluemoon-control/evidence/uploadImg%s", handler);
    }

    /**
     *明细详情
     * @param manageId 主键ID int
     * @param token token身份检验码 String
     */
    public static void cashDetail(int manageId,String token,AsyncHttpResponseHandler handler){
        if(null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("manageId",manageId);
        params.put("token",token);
        postRequest(params, "bluemoon-control/evidence/cashDetail%s", handler);
    }






}
