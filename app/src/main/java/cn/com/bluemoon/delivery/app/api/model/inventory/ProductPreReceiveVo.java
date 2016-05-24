package cn.com.bluemoon.delivery.app.api.model.inventory;

import java.io.Serializable;

/**
 *  prepare to Receive goods
 *
 * Created by wangshanhai on 2016/3/25.
 */
public class ProductPreReceiveVo implements Serializable {
    private int detailLine;
    private String productNo;
    private String productName;
    private double outCase;
    private int outNum;
    private int priceBag;
    private int carton;

    
    public int getDetailLine() {
        return detailLine;
    }

    public void setDetailLine(int detailLine) {
        this.detailLine = detailLine;
    }

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

    public int getPriceBag() {
        return priceBag;
    }

    public void setPriceBag(int priceBag) {
        this.priceBag = priceBag;
    }

    public int getCarton() {
        return carton;
    }

    public void setCarton(int carton) {
        this.carton = carton;
    }



    private int reNum;
    private String reDifferReason;
    private String reDifferBackup;
    private String reDifferReasonName;

    private int differNum;
    private double diffCase;


    public int getReNum() {
        return reNum;
    }

    public void setReNum(int reNum) {
        this.reNum = reNum;
    }

    public String getReDifferReason() {
        return reDifferReason;
    }

    public void setReDifferReason(String reDifferReason) {
        this.reDifferReason = reDifferReason;
    }

    public String getReDifferBackup() {
        return reDifferBackup;
    }

    public void setReDifferBackup(String reDifferBackup) {
        this.reDifferBackup = reDifferBackup;
    }

    public String getReDifferReasonName() {
        return reDifferReasonName;
    }

    public void setReDifferReasonName(String reDifferReasonName) {
        this.reDifferReasonName = reDifferReasonName;
    }

    public int getDifferNum() {
        return differNum;
    }

    public void setDifferNum(int differNum) {
        this.differNum = differNum;
    }

    public double getDiffCase() {
        return diffCase;
    }

    public void setDiffCase(double diffCase) {
        this.diffCase = diffCase;
    }
}
