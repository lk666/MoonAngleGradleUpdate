package cn.com.bluemoon.delivery.entity;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.module.extract.ScanFragment;
import cn.com.bluemoon.delivery.module.order.HistoryFragment;

public enum ExtractTabState {

	SCAN(ScanFragment.class, R.drawable.extract_tab_scan, R.string.tab_bottom_scan_code_text),

	HISTORY(HistoryFragment.class, R.drawable.extract_tab_history,R.string.tab_bottom_show_history_text);

	//STOREHOUSE(StoreHouseFragment.class, R.drawable.extract_tab_storehouse, R.string.tab_bottom_storehouse_text);
	
	private Class clazz;
	private int image;
	private int content;

	private ExtractTabState(Class clazz, int image, int content) {
		this.clazz = clazz;
		this.image = image;
		this.content = content;
	}

	public Class getClazz() {
		return clazz;
	}

	public int getImage() {
		return image;
	}

	public int getContent() {
		return content;
	}

}
