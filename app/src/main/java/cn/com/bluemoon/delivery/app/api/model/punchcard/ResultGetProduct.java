package cn.com.bluemoon.delivery.app.api.model.punchcard;

import java.io.Serializable;
import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by liangjiangli on 2016/4/1.
 */
public class ResultGetProduct extends ResultBase implements  Serializable{

    private long timestamp;
    private List<Product> productList;

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public class Product implements Serializable{
        private String productCode;
        private String productName;

        public String getProductCode() {
            return productCode;
        }

        public void setProductCode(String productCode) {
            this.productCode = productCode;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }
    }
}
