package cn.com.bluemoon.delivery.app.api.model.wash.enterprise;

import java.io.Serializable;
import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by liangjiangli on 2017/4/28.
 */

public class ResultEnterpriseRecord extends ResultBase {

    public List<EnterpriseListBean> enterpriseList;
    public static class EnterpriseListBean {

        public String enterpriseCode;
        public String enterpriseSName;
        public List<BranchListBean> branchList;


        public static class BranchListBean implements Serializable{

            public String branchCode;
            public String branchName;

        }
    }
}
