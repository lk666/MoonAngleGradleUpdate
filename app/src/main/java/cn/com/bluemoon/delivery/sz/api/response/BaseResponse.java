package cn.com.bluemoon.delivery.sz.api.response;

import java.io.Serializable;

/**
 * Created by dujiande on 2016/8/11.
 */
public class BaseResponse implements Serializable{

    private Boolean isSuccess;
    private int responseCode;
    private String responseMsg;

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMsg() {
        return responseMsg;
    }

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }
}
