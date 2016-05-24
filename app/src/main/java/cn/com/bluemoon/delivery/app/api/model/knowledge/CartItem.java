package cn.com.bluemoon.delivery.app.api.model.knowledge;

import java.util.List;

/**
 * Created by allenli on 2016/5/20.
 */
public class CartItem {
    private List<Paper> paperList;
    private String catSecondId;
    private String catSecondName;

    public List<Paper> getPaperList() {
        return paperList;
    }

    public void setPaperList(List<Paper> paperList) {
        this.paperList = paperList;
    }

    public String getCatSecondId() {
        return catSecondId;
    }

    public void setCatSecondId(String catSecondId) {
        this.catSecondId = catSecondId;
    }

    public String getCatSecondName() {
        return catSecondName;
    }

    public void setCatSecondName(String catSecondName) {
        this.catSecondName = catSecondName;
    }
}
