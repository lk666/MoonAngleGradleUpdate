package cn.com.bluemoon.delivery.app.api.model.scan;

import cn.com.bluemoon.cardocr.lib.bean.BankInfo;

/**
 * Created by bm on 2017/11/10.
 */

public class ResultBankInfo extends BankInfo {

    public boolean isSuccess;
    public String responesMsg;

    public ResultBankInfo(BankInfo bankInfo){
        if(bankInfo==null) return;
        setBankName(bankInfo.getBankName());
        setCardName(bankInfo.getCardName());
        setCardNumber(bankInfo.getCardNumber());
        setCardType(bankInfo.getCardType());
        isSuccess = true;
    }
}
