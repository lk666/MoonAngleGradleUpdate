package com.bluemoon.umengshare;

import android.content.Context;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.media.UMusic;

import java.io.File;

/**
 * Created by chinwe on 2016/11/11.
 */

public class ShareModel {

	private UMImage uMImage;//分享图片
	private String  uMTargetUrl;//分享链接
	private String  uMTitle;//分享标题
	private String  uMText;//分享文本
//	private UMusic  uMusic;//分享音乐
//	private UMVideo uMVideo;//分享小视频
//	private File uMFile;//分享文件

	/**
	 *
	 * @param context
	 * @param imgUrl 图片url
	 * @param uMTargetUrl 分享链接
	 * @param uMTitle 分享标题
	 * @param uMText 分享文本
	 */
	public ShareModel(Context context, String imgUrl, String uMTargetUrl, String uMTitle, String uMText) {
		this.uMImage=new UMImage(context,imgUrl);
		this.uMTargetUrl=uMTargetUrl;
		this.uMTitle=uMTitle;
		this.uMText=uMText;
	}



	public UMImage getuMImage() {
		return uMImage;
	}

	public void setuMImage(UMImage uMImage) {
		this.uMImage = uMImage;
	}

	public String getuMTargetUrl() {
		return uMTargetUrl;
	}

	public void setuMTargetUrl(String uMTargetUrl) {
		this.uMTargetUrl = uMTargetUrl;
	}

	public String getuMTitle() {
		return uMTitle;
	}

	public void setuMTitle(String uMTitle) {
		this.uMTitle = uMTitle;
	}

//	public UMusic getuMusic() {
//		return uMusic;
//	}
//
//	public void setuMusic(UMusic uMusic) {
//		this.uMusic = uMusic;
//	}
//
//	public UMVideo getuMVideo() {
//		return uMVideo;
//	}
//
//	public void setuMVideo(UMVideo uMVideo) {
//		this.uMVideo = uMVideo;
//	}
//
//	public File getuMFile() {
//		return uMFile;
//	}
//
//	public void setuMFile(File uMFile) {
//		this.uMFile = uMFile;
//	}

	public String getuMText() {
		return uMText;
	}

	public void setuMText(String uMText) {
		this.uMText = uMText;
	}
}
