package cn.com.bluemoon.delivery.app.api.model.inventory;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by allenli on 2016/3/25.
 */
public class ResultCustormerService extends ResultBase {

      private String custormerServiceName;
    private String     custormerServicePhone;

    public String getCustormerServiceName() {
        return custormerServiceName;
    }

    public void setCustormerServiceName(String custormerServiceName) {
        this.custormerServiceName = custormerServiceName;
    }

    public String getCustormerServicePhone() {
        return custormerServicePhone;
    }

    public void setCustormerServicePhone(String custormerServicePhone) {
        this.custormerServicePhone = custormerServicePhone;
    }
}
