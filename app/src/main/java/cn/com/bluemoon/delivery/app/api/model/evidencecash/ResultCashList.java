package cn.com.bluemoon.delivery.app.api.model.evidencecash;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by ljl on 2016/11/17.
 */
public class ResultCashList extends ResultBase {


    private List<CashListBean> cashList;

    public List<CashListBean> getCashList() {
        return cashList;
    }

    public void setCashList(List<CashListBean> cashList) {
        this.cashList = cashList;
    }

    public static class CashListBean {
        private String tradePayDisPlay;
        private long tradeMoney;
        private String tradeStatusCode;
        private String tradeStatusDisPlay;
        private String symbol;
        private String cashTypeDisplay;
        private int tradePayCode;
        private int manageId;
        private String cashTypeCode;
        private int cashTime;

        public String getTradePayDisPlay() {
            return tradePayDisPlay;
        }

        public void setTradePayDisPlay(String tradePayDisPlay) {
            this.tradePayDisPlay = tradePayDisPlay;
        }

        public long getTradeMoney() {
            return tradeMoney;
        }

        public void setTradeMoney(long tradeMoney) {
            this.tradeMoney = tradeMoney;
        }

        public String getTradeStatusCode() {
            return tradeStatusCode;
        }

        public void setTradeStatusCode(String tradeStatusCode) {
            this.tradeStatusCode = tradeStatusCode;
        }

        public String getTradeStatusDisPlay() {
            return tradeStatusDisPlay;
        }

        public void setTradeStatusDisPlay(String tradeStatusDisPlay) {
            this.tradeStatusDisPlay = tradeStatusDisPlay;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getCashTypeDisplay() {
            return cashTypeDisplay;
        }

        public void setCashTypeDisplay(String cashTypeDisplay) {
            this.cashTypeDisplay = cashTypeDisplay;
        }

        public int getTradePayCode() {
            return tradePayCode;
        }

        public void setTradePayCode(int tradePayCode) {
            this.tradePayCode = tradePayCode;
        }

        public int getManageId() {
            return manageId;
        }

        public void setManageId(int manageId) {
            this.manageId = manageId;
        }

        public String getCashTypeCode() {
            return cashTypeCode;
        }

        public void setCashTypeCode(String cashTypeCode) {
            this.cashTypeCode = cashTypeCode;
        }

        public int getCashTime() {
            return cashTime;
        }

        public void setCashTime(int cashTime) {
            this.cashTime = cashTime;
        }
    }
}
