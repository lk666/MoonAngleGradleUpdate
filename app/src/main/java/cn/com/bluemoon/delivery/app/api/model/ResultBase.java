package cn.com.bluemoon.delivery.app.api.model;

public class ResultBase {
    private int responseCode = -1;
    public boolean isSuccess;
    private String responseMsg;

    /**
     * responseCode.
     *
     * @return the responseCode
     * @since JDK 1.6
     */
    public int getResponseCode() {
        return responseCode;
    }

    /**
     * responseCode.
     *
     * @param responseCode the responseCode to set
     * @since JDK 1.6
     */
    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    /**
     * responseMsg.
     *
     * @return the responseMsg
     * @since JDK 1.6
     */
    public String getResponseMsg() {
        return responseMsg;
    }

    /**
     * responseMsg.
     *
     * @param responseMsg the responseMsg to set
     * @since JDK 1.6
     */
    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }
}
