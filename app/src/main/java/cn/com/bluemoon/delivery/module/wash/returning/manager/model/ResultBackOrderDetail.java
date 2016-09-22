package cn.com.bluemoon.delivery.module.wash.returning.manager.model;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by ljl on 2016/9/21.
 */
public class ResultBackOrderDetail extends ResultBase{


    private String buyerMessage;

    private List<ClothesListBean> clothesList;

    public String getBuyerMessage() {
        return buyerMessage;
    }

    public void setBuyerMessage(String buyerMessage) {
        this.buyerMessage = buyerMessage;
    }

    public List<ClothesListBean> getClothesList() {
        return clothesList;
    }

    public void setClothesList(List<ClothesListBean> clothesList) {
        this.clothesList = clothesList;
    }

    public static class ClothesListBean {
        private String imgPath;
        private String clothesCode;
        private String clothesName;
        private String typeName;

        public String getImgPath() {
            return imgPath;
        }

        public void setImgPath(String imgPath) {
            this.imgPath = imgPath;
        }

        public String getClothesCode() {
            return clothesCode;
        }

        public void setClothesCode(String clothesCode) {
            this.clothesCode = clothesCode;
        }

        public String getClothesName() {
            return clothesName;
        }

        public void setClothesName(String clothesName) {
            this.clothesName = clothesName;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }
    }
}
