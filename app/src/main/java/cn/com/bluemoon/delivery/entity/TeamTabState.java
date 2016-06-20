package cn.com.bluemoon.delivery.entity;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.team.GroupFragment;
import cn.com.bluemoon.delivery.team.MemberFragment;

public enum TeamTabState {
	GROUP(GroupFragment.class, R.drawable.card_tab_punch, R.string.team_group_title),
	MEMBER(MemberFragment.class, R.drawable.card_tab_record,R.string.team_member_title);

	private Class clazz;
	private int image;
	private int content;

	private TeamTabState(Class clazz, int image, int content) {
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
