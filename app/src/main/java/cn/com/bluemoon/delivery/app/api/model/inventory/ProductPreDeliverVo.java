package cn.com.bluemoon.delivery.app.api.model.inventory;

import java.io.Serializable;

/**
 *  prepare to deliver goods
 *
 * Created by wangshanhai on 2016/3/25.
 */
public class ProductPreDeliverVo implements Serializable {
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



    private int differNum;
    private double diffCase;


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
