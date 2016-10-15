package cn.com.bluemoon.delivery.app.api.model.wash.closebox;

import java.io.Serializable;

/**
 * {@link ResultCloseBoxHistoryList#tagList}数据项
 * Created by lk on 2016/9/14.
 */
public class TagItem implements Serializable {
    /**
     * 还衣单数量
     */
    private int backOrderNum;
    /**
     * 衣物数量
     */
    private int clothesNum;
    /**
     * 封箱标签
     */
    private String tagCode;
    /**
     * 封箱id
     */
    private String tagId;

    public int getClothesNum() {
        return clothesNum;
    }

    public void setClothesNum(int clothesNum) {
        this.clothesNum = clothesNum;
    }

    public String getTagCode() {
        return tagCode;
    }

    public void setTagCode(String tagCode) {
        this.tagCode = tagCode;
    }

    public int getBackOrderNum() {
        return backOrderNum;
    }

    public void setBackOrderNum(int backOrderNum) {
        this.backOrderNum = backOrderNum;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }
}
