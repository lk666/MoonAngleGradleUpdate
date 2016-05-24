package cn.com.bluemoon.delivery.entity;

public class GridViewItem {

	private int key;
	private int imageId;
	private String name;
	
	public GridViewItem(int key,int imageId,String name)
	{
		this.key = key;
		this.imageId = imageId;
		this.name = name;
	}
	
	public int getKey() {
		return key;
	}
	public void setKey(int key) {
		this.key = key;
	}
	public int getImageId() {
		return imageId;
	}
	public void setImageId(int imageId) {
		this.imageId = imageId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
