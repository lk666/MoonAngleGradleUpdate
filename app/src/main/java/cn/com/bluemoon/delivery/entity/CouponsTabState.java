package cn.com.bluemoon.delivery.entity;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.coupons.CouponsPushFragment;
import cn.com.bluemoon.delivery.coupons.CouponsRecordFragment;

public enum CouponsTabState {
	PUNCH(CouponsPushFragment.class, R.drawable.coupons_tab_push, R.string.coupons_tab_push),
	RECORD(CouponsRecordFragment.class, R.drawable.card_tab_record,R.string.coupons_tab_record);

	private Class clazz;
	private int image;
	private int content;

	private CouponsTabState(Class clazz, int image, int content) {
		this.clazz = clazz;
		this.image = image;
		this.content = content;
	}

	public Class getClazz() {
		return clazz;
	}

	public void setClazz(Class clazz) {
		this.clazz = clazz;
	}

	public void setImage(int image) {
		this.image = image;
	}

	public void setContent(int content) {
		this.content = content;
	}

	public int getImage() {
		return image;
	}

	public int getContent() {
		return content;
	}

}
