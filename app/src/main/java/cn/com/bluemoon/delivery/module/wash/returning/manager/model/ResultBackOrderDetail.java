package cn.com.bluemoon.delivery.module.wash.returning.manager.model;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by ljl on 2016/9/21.
 */
public class ResultBackOrderDetail extends ResultBase{


    private String buyerMessage;

    private List<ClothesListBean> clothesList;


    private String signImagePath;
    private long signTime;
    private int pageFlag;
    private String signName;

    private List<RefuseListBean> refuseList;

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

    public String getSignImagePath() {
        return signImagePath;
    }

    public void setSignImagePath(String signImagePath) {
        this.signImagePath = signImagePath;
    }

    public long getSignTime() {
        return signTime;
    }

    public void setSignTime(long signTime) {
        this.signTime = signTime;
    }

    public int getPageFlag() {
        return pageFlag;
    }

    public void setPageFlag(int pageFlag) {
        this.pageFlag = pageFlag;
    }

    public String getSignName() {
        return signName;
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }

    public List<RefuseListBean> getRefuseList() {
        return refuseList;
    }

    public void setRefuseList(List<RefuseListBean> refuseList) {
        this.refuseList = refuseList;
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

    public static class RefuseListBean {
        private String refuseIssueDesc;
        private String refuseClothesCode;
        private long refuseTagTime;
        private List<String> refuseImagePaths;

        public String getRefuseIssueDesc() {
            return refuseIssueDesc;
        }

        public void setRefuseIssueDesc(String refuseIssueDesc) {
            this.refuseIssueDesc = refuseIssueDesc;
        }

        public String getRefuseClothesCode() {
            return refuseClothesCode;
        }

        public void setRefuseClothesCode(String refuseClothesCode) {
            this.refuseClothesCode = refuseClothesCode;
        }

        public long getRefuseTagTime() {
            return refuseTagTime;
        }

        public void setRefuseTagTime(long refuseTagTime) {
            this.refuseTagTime = refuseTagTime;
        }

        public List<String> getRefuseImagePaths() {
            return refuseImagePaths;
        }

        public void setRefuseImagePaths(List<String> refuseImagePaths) {
            this.refuseImagePaths = refuseImagePaths;
        }
    }
}
