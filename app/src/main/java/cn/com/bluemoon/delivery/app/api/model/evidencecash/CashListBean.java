package cn.com.bluemoon.delivery.app.api.model.evidencecash;

/**
 * Created by ljl on 2016/11/20.
 */
public class CashListBean {
    private int tradeMoney;
    private String symbol;
    private String cashTypeCode;
    private String tradeStatusDisplay;
    private String tradeStatusCode;
    private String cashTypeDisplay;
    private int manageId;
    private String tradePayCode;
    private String tradePayDisplay;
    private long cashTime;

    public int getTradeMoney() {
        return tradeMoney;
    }

    public void setTradeMoney(int tradeMoney) {
        this.tradeMoney = tradeMoney;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCashTypeCode() {
        return cashTypeCode;
    }

    public void setCashTypeCode(String cashTypeCode) {
        this.cashTypeCode = cashTypeCode;
    }

    public String getTradeStatusDisplay() {
        return tradeStatusDisplay;
    }

    public void setTradeStatusDisplay(String tradeStatusDisplay) {
        this.tradeStatusDisplay = tradeStatusDisplay;
    }

    public String getTradeStatusCode() {
        return tradeStatusCode;
    }

    public void setTradeStatusCode(String tradeStatusCode) {
        this.tradeStatusCode = tradeStatusCode;
    }

    public String getCashTypeDisplay() {
        return cashTypeDisplay;
    }

    public void setCashTypeDisplay(String cashTypeDisplay) {
        this.cashTypeDisplay = cashTypeDisplay;
    }

    public int getManageId() {
        return manageId;
    }

    public void setManageId(int manageId) {
        this.manageId = manageId;
    }

    public String getTradePayCode() {
        return tradePayCode;
    }

    public void setTradePayCode(String tradePayCode) {
        this.tradePayCode = tradePayCode;
    }

    public String getTradePayDisplay() {
        return tradePayDisplay;
    }

    public void setTradePayDisplay(String tradePayDisplay) {
        this.tradePayDisplay = tradePayDisplay;
    }

    public long getCashTime() {
        return cashTime;
    }

    public void setCashTime(long cashTime) {
        this.cashTime = cashTime;
    }
}
