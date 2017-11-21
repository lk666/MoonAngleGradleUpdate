package cn.com.bluemoon.delivery.app.api.model;

public class UserRight {
    /**
     * color : 图标颜色
     * groupName : 分组名称
     * groupNum : 分组序号
     * iconImg : icon图标
     * menuCode : 模块标识
     * menuId : 模块id
     * menuName : 模块名称
     * url : 链接地址
     * amount:角标数量
     */

    private String color;
    private String groupName;
    private int groupNum;
    private String iconImg;
    private String menuCode;
    private String menuId;
    private String menuName;
    private String url;
    private int amount;
    public boolean isQuick;

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
    }

    public String getIconImg() {
        return iconImg;
    }

    public void setIconImg(String iconImg) {
        this.iconImg = iconImg;
    }

    public int getGroupNum() {
        return groupNum;
    }

    public void setGroupNum(int groupNum) {
        this.groupNum = groupNum;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
  
