package cn.com.bluemoon.delivery.app.api.model.evidencecash;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by ljl on 2016/11/17.
 */
public class ResultCashDetail extends ResultBase {

    private CashDetailBean cashDetail;

    public CashDetailBean getCashDetail() {
        return cashDetail;
    }

    public void setCashDetail(CashDetailBean cashDetail) {
        this.cashDetail = cashDetail;
    }

    public static class CashDetailBean {
        private long tradeMoney;
        private String refuseReason;
        private long payTime;
        private String tradeTransaction;
        private String evidencePath;
        private String symbol;
        private String tradeStatusCode;
        private long evidenceMoney;
        private long evidenceTime;
        private String cashTypeDisplay;
        private String tradePayCode;
        private String tradePayDisplay;
        private String tradeStatusDisplay;
        private String cashTypeCode;
        private String tradeSource;

        public String getTradeSource() {
            return tradeSource;
        }

        public void setTradeSource(String tradeSource) {
            this.tradeSource = tradeSource;
        }

        public long getTradeMoney() {
            return tradeMoney;
        }

        public void setTradeMoney(long tradeMoney) {
            this.tradeMoney = tradeMoney;
        }

        public String getRefuseReason() {
            return refuseReason;
        }

        public void setRefuseReason(String refuseReason) {
            this.refuseReason = refuseReason;
        }

        public long getPayTime() {
            return payTime;
        }

        public void setPayTime(long payTime) {
            this.payTime = payTime;
        }

        public String getTradeTransaction() {
            return tradeTransaction;
        }

        public void setTradeTransaction(String tradeTransaction) {
            this.tradeTransaction = tradeTransaction;
        }

        public String getEvidencePath() {
            return evidencePath;
        }

        public void setEvidencePath(String evidencePath) {
            this.evidencePath = evidencePath;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getTradeStatusCode() {
            return tradeStatusCode;
        }

        public void setTradeStatusCode(String tradeStatusCode) {
            this.tradeStatusCode = tradeStatusCode;
        }

        public long getEvidenceMoney() {
            return evidenceMoney;
        }

        public void setEvidenceMoney(long evidenceMoney) {
            this.evidenceMoney = evidenceMoney;
        }

        public long getEvidenceTime() {
            return evidenceTime;
        }

        public void setEvidenceTime(long evidenceTime) {
            this.evidenceTime = evidenceTime;
        }

        public String getCashTypeDisplay() {
            return cashTypeDisplay;
        }

        public void setCashTypeDisplay(String cashTypeDisplay) {
            this.cashTypeDisplay = cashTypeDisplay;
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

        public String getTradeStatusDisplay() {
            return tradeStatusDisplay;
        }

        public void setTradeStatusDisplay(String tradeStatusDisplay) {
            this.tradeStatusDisplay = tradeStatusDisplay;
        }

        public String getCashTypeCode() {
            return cashTypeCode;
        }

        public void setCashTypeCode(String cashTypeCode) {
            this.cashTypeCode = cashTypeCode;
        }
    }
}
