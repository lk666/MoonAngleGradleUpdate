package cn.com.bluemoon.delivery.module.track.api;

/**
 * Created by bm on 2016/12/12.
 * respCode	respDesc
 * 100	数据上报成功
 * 101	数据上报失败，请求报文记录数过长
 * 102	数据上报失败，请求报文数据格式不对
 */
public class ResultTrack {

    /**
     * 状态码
     */
    private String respCode;
    /**
     * 描述
     */
    private String respDesc;

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getRespDesc() {
        return respDesc;
    }

    public void setRespDesc(String respDesc) {
        this.respDesc = respDesc;
    }
}
