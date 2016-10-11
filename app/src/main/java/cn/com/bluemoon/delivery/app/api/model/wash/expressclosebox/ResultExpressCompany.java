package cn.com.bluemoon.delivery.app.api.model.wash.expressclosebox;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by ljl on 2016/9/28.
 */
public class ResultExpressCompany extends ResultBase{


    private List<CompanyListBean> companyList;

    public List<CompanyListBean> getCompanyList() {
        return companyList;
    }

    public void setCompanyList(List<CompanyListBean> companyList) {
        this.companyList = companyList;
    }

    public static class CompanyListBean {
        private String companyName;
        private String companyCode;
        private boolean select;

        public boolean isSelect() {
            return select;
        }

        public void setSelect(boolean select) {
            this.select = select;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getCompanyCode() {
            return companyCode;
        }

        public void setCompanyCode(String companyCode) {
            this.companyCode = companyCode;
        }
    }
}
