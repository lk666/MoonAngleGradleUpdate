package cn.com.bluemoon.delivery.app.api.model.wash.manager;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by ljl on 2016/9/21.
 */
public class ResultRefuseSign extends ResultBase{

    private List<ClothesListBean> clothesList;

    public List<ClothesListBean> getClothesList() {
        return clothesList;
    }

    public void setClothesList(List<ClothesListBean> clothesList) {
        this.clothesList = clothesList;
    }

    public static class ClothesListBean {
        private String clothesCode;
        private boolean isRefuse;
        private String clothesName;

        public String getClothesCode() {
            return clothesCode;
        }

        public void setClothesCode(String clothesCode) {
            this.clothesCode = clothesCode;
        }

        public boolean isIsRefuse() {
            return isRefuse;
        }

        public void setIsRefuse(boolean isRefuse) {
            this.isRefuse = isRefuse;
        }

        public String getClothesName() {
            return clothesName;
        }

        public void setClothesName(String clothesName) {
            this.clothesName = clothesName;
        }
    }
}
