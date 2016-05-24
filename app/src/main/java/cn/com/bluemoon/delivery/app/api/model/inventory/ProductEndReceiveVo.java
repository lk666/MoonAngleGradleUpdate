package cn.com.bluemoon.delivery.app.api.model.inventory;

import java.io.Serializable;

/**
 *  end to Receive goods
 *
 * Created by wangshanhai on 2016/3/25.
 */
public class ProductEndReceiveVo implements Serializable {

    private String productNo;
    private String productName;
    private double reCase;
    private int reNum;
    private int carton;
    private int reDifferNum;
    private String reDifferReason;
    private String reDifferBackup;


    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public String getReDifferBackup() {
        return reDifferBackup;
    }

    public void setReDifferBackup(String reDifferBackup) {
        this.reDifferBackup = reDifferBackup;
    }

    public String getReDifferReason() {
        return reDifferReason;
    }

    public void setReDifferReason(String reDifferReason) {
        this.reDifferReason = reDifferReason;
    }

    public int getReDifferNum() {
        return reDifferNum;
    }

    public void setReDifferNum(int reDifferNum) {
        this.reDifferNum = reDifferNum;
    }

    public int getCarton() {
        return carton;
    }

    public void setCarton(int carton) {
        this.carton = carton;
    }

    public int getReNum() {
        return reNum;
    }

    public void setReNum(int reNum) {
        this.reNum = reNum;
    }

    public double getReCase() {
        return reCase;
    }

    public void setReCase(double reCase) {
        this.reCase = reCase;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
