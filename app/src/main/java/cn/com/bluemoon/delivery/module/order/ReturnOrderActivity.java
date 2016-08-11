package cn.com.bluemoon.delivery.module.order;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;
import org.kymjs.kjframe.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.Dict;
import cn.com.bluemoon.delivery.app.api.model.OrderVo;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.ResultDict;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DialogUtil;
import cn.com.bluemoon.delivery.utils.FileUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.manager.ActivityManager;
import cn.com.bluemoon.lib.tagview.Tag;
import cn.com.bluemoon.lib.tagview.TagListView;
import cn.com.bluemoon.lib.view.CommonProgressDialog;
import cn.com.bluemoon.lib.view.TakePhotoPopView;


public class ReturnOrderActivity extends Activity implements OnClickListener{
	private String TAG = "ReturnOrChangeOrderActivity";
	private ReturnOrderActivity main;
	private TakePhotoPopView takePhotoPop;
	private EditText edContent;
	private ImageView imgProduct;
	private ImageView imgDelete;
	private Button btnSubmit;
	private TagListView tagListView;
	private Bitmap bm;
	private CommonProgressDialog progressDialog;
	private OrderVo order;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_return_order);
		main =this;
		initCustomActionBar();
		ActivityManager.getInstance().pushOneActivity(this);
		progressDialog = new CommonProgressDialog(this);
		takePhotoPop = new TakePhotoPopView(this,
				Constants.TAKE_PIC_RESULT,Constants.CHOSE_PIC_RESULT);
		edContent = (EditText) findViewById(R.id.ed_content);
		btnSubmit = (Button) findViewById(R.id.btn_submit);
		btnSubmit.setOnClickListener(this);
		imgProduct = (ImageView) findViewById(R.id.img_product);
		imgProduct.setOnClickListener(this);
		imgDelete = (ImageView) findViewById(R.id.img_delete);
		imgDelete.setOnClickListener(this);

		if(getIntent()!=null&&getIntent().getExtras()!=null){
			order = (OrderVo)getIntent().getExtras().getSerializable("order");
		}
		if(order == null){
			PublicUtil.showToast(R.string.return_change_no_item);
			return;
		}
		if(progressDialog!=null){
			progressDialog.show();
		}
		DeliveryApi.getDictInfo(dictInfoHandler);

	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v==imgProduct){
			if(bm!=null){
				DialogUtil.showPictureDialog(main, bm);
			}else{
				takePhotoPop.getPic(v);
			}
		}else if(v==imgDelete){
			bm = null;
			refreshImage();
		}else if(v==btnSubmit){
			submit();
		}
	}
	
	private void initTagListView(List<Dict> list){

		tagListView = (TagListView) findViewById(R.id.tag_listview);
		tagListView.setMode(TagListView.Mode.single);
		if(list==null){
			list = new ArrayList<>();
		}
		List<Tag> mTags = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			Tag tag = new Tag();
			tag.setId(i);
			tag.setKey(list.get(i).getDictId());
			tag.setTitle(list.get(i).getDictName());
			mTags.add(tag);
		}
		tagListView.setTags(mTags);
	}
	
	private void submit(){
		if(order==null){
			PublicUtil.showToast(R.string.return_change_no_item);
			return;
		}
		if(bm==null){
			PublicUtil.showToast(R.string.return_change_no_photo);
			return;
		}
		if(tagListView.getTagsChecked().size()<=0){
			PublicUtil.showToast(R.string.return_change_no_reason);
			return;
		}
		String content = edContent.getText().toString();
		String token = ClientStateManager.getLoginToken(main);
		if(StringUtils.isEmpty(token)){
			PublicUtil.showMessageTokenExpire(main);
			return;
		}
		if(progressDialog!=null){
			progressDialog.show();
		}
		DeliveryApi.returnOrExchangeGoods(token, order.getOrderId(), 
				order.getDispatchId(), order.getOrderSource(),
				tagListView.getTagsChecked().get(0).getKey(), FileUtil.getBytes(bm),
				content, returnGoodsHandler);	
	}
	
	private void initCustomActionBar() {
		new CommonActionBar(getActionBar(), new IActionBarListener() {

			@Override
			public void onBtnRight(View v) {

			}

			@Override
			public void onBtnLeft(View v) {
				ReturnOrderActivity.this.finish();
			}

			@Override
			public void setTitle(TextView v) {
				v.setText(R.string.return_order);
			}

		});
	}
	
	private void refreshImage(){
		if(bm!=null){
			imgDelete.setVisibility(View.VISIBLE);
			imgProduct.setImageBitmap(bm);
		}else{
			imgDelete.setVisibility(View.GONE);
			imgProduct.setImageResource(R.mipmap.icon_add_picture);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_CANCELED)
			return;
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case Constants.TAKE_PIC_RESULT:
				if(takePhotoPop!=null){
					bm = takePhotoPop.getTakeImageBitmap();
					refreshImage();
				}
				break;
			case Constants.CHOSE_PIC_RESULT:
				if(takePhotoPop!=null){
					bm = takePhotoPop.getPickImageBitmap(data);
					refreshImage();
				}
				break;
			}
		}
	}
	
	AsyncHttpResponseHandler dictInfoHandler = new TextHttpResponseHandler(HTTP.UTF_8) {
		
		@Override
		public void onSuccess(int statusCode, Header[] headers,
				String responseString) {
			LogUtils.d(TAG, "getDictInfo result = " + responseString);
			if(progressDialog!=null) progressDialog.dismiss();
			try {
				ResultDict dictResult = JSON.parseObject(responseString,
						ResultDict.class);
				if (dictResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
					if(dictResult!=null&&dictResult.getItemList()!=null){
						initTagListView(dictResult.getItemList());
					}
				} else {
					PublicUtil.showErrorMsg(main, dictResult);
				}
			} catch (Exception e) {
				LogUtils.e(TAG, e.getMessage());
				PublicUtil.showToastServerBusy();
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers,
				String responseString, Throwable throwable) {
			LogUtils.e(TAG, throwable.getMessage());
			if(progressDialog!=null) progressDialog.dismiss();
			PublicUtil.showToastServerOvertime();
		}
	};
	
	AsyncHttpResponseHandler returnGoodsHandler = new TextHttpResponseHandler(HTTP.UTF_8) {
		
		@Override
		public void onSuccess(int statusCode, Header[] headers,
				String responseString) {
			LogUtils.d(TAG, "returnOrExchangeGoods result = " + responseString);
			if(progressDialog!=null) progressDialog.dismiss();
			try {
				ResultBase baseResult = JSON.parseObject(responseString,
						ResultBase.class);
				if (baseResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
//					PublicUtil.showToast(baseResult.getResponseMsg());
					setResult(RESULT_OK);
					finish();
				} else {
					PublicUtil.showErrorMsg(main, baseResult);
				}
			} catch (Exception e) {
				LogUtils.e(TAG, e.getMessage());
				PublicUtil.showToastServerBusy();
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers,
				String responseString, Throwable throwable) {
			LogUtils.e(TAG, throwable.getMessage());
			if(progressDialog!=null) progressDialog.dismiss();
			PublicUtil.showToastServerOvertime(main);
		}
	};
			
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart(TAG); 
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd(TAG); 
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(bm!=null&&!bm.isRecycled()){
			bm.recycle();
		}
		bm = null;
	}

}
