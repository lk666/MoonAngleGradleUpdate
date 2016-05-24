package cn.com.bluemoon.delivery.app.api.model.inventory;

import java.io.Serializable;

/**
 *  end to Receive goods
 *
 * Created by wangshanhai on 2016/3/25.
 */
public class ProductEndDeliverVo implements Serializable {
    private String productNo;
    private String productName;
    private double outCase;
    private int outNum;
    private int carton;


    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getOutCase() {
        return outCase;
    }

    public void setOutCase(double outCase) {
        this.outCase = outCase;
    }

    public int getOutNum() {
        return outNum;
    }

    public void setOutNum(int outNum) {
        this.outNum = outNum;
    }

    public int getCarton() {
        return carton;
    }

    public void setCarton(int carton) {
        this.carton = carton;
    }
}
