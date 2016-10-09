package cn.com.bluemoon.delivery.entity;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.module.card.alarm.AlarmSettingFragment;
import cn.com.bluemoon.delivery.module.card.PunchCardFragment;
import cn.com.bluemoon.delivery.module.card.RecordCardFragment;

public enum CardTabState {
	PUNCH(PunchCardFragment.class, R.drawable.card_tab_punch, R.string.tab_bottom_punch_card_text),
	RECORD(RecordCardFragment.class, R.drawable.card_tab_record,R.string.tab_bottom_punch_record_text),
	SETTING(AlarmSettingFragment.class, R.drawable.card_tab_record,R.string.tab_bottom_punch_alarm_setting);
	private Class clazz;
	private int image;
	private int content;

	private CardTabState(Class clazz, int image, int content) {
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
