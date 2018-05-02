package cn.com.bluemoon.delivery.app.api.model.group_booking;

import java.io.Serializable;

public class PaymentBean implements Serializable {
        public int icon;
        public int name;
        public int requestCode;
        public String type;
        public boolean isSelect;
        public String balance;
        public PaymentBean(int icon, int name, int requestCode, String type) {
            this.icon = icon;
            this.name = name;
            this.requestCode = requestCode;
            this.type = type;
        }
    public PaymentBean(String type) {
        this.type = type;
    }
    }