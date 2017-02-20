package cn.com.bluemoon.delivery.app.api.model.card;

/**
 * Created by allenli on 2017/2/20.
 */
public class AddressInfo {
    private String business;
    private String formattedAddress;

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }
}
