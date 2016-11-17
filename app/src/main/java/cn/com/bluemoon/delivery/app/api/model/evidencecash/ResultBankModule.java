package cn.com.bluemoon.delivery.app.api.model.evidencecash;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by ljl on 2016/11/16.
 */
public class ResultBankModule extends ResultBase{

    private String moduleName;
    private String moduleAccount;
    private String moduleBank;
    private String moduleRemark;

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleAccount() {
        return moduleAccount;
    }

    public void setModuleAccount(String moduleAccount) {
        this.moduleAccount = moduleAccount;
    }

    public String getModuleBank() {
        return moduleBank;
    }

    public void setModuleBank(String moduleBank) {
        this.moduleBank = moduleBank;
    }

    public String getModuleRemark() {
        return moduleRemark;
    }

    public void setModuleRemark(String moduleRemark) {
        this.moduleRemark = moduleRemark;
    }
}
