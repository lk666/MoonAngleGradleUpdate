package cn.com.bluemoon.delivery.app.api.model;  

import java.util.List;

public class ResultDict extends ResultBase {
   private  List<Dict> itemList;
	private List<Dict> dictList;

	public List<Dict> getDictList() {
		return dictList;
	}

	public void setDictList(List<Dict> dictList) {
		this.dictList = dictList;
	}

	public List<Dict> getItemList() {
	return itemList;
}

public void setItemList(List<Dict> itemList) {
	this.itemList = itemList;
}

   

}
  
