package cn.com.bluemoon.delivery.module.card;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.protocol.HTTP;
import org.kymjs.kjframe.KJBitmap;
import org.kymjs.kjframe.bitmap.BitmapCallBack;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.punchcard.ImageBean;
import cn.com.bluemoon.delivery.app.api.model.punchcard.ResultImage;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DialogUtil;
import cn.com.bluemoon.delivery.utils.FileUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.manager.ActivityManager;
import cn.com.bluemoon.lib.view.CommonAlertDialog;
import cn.com.bluemoon.lib.view.CommonProgressDialog;
import cn.com.bluemoon.lib.view.TakePhotoPopView;

/**
 * Created by liangjiangli on 2016/3/30.
 */
public class UploadImageActivity extends Activity{

    private String TAG = "UploadImageActivity";
    private ActivityManager manager;
    private TakePhotoPopView takePhotoPop;
    private GridView gridView;
    private ArrayList<ImageBean> images;
    private ImageAdapter adapter;
    private Button btnOk;
    private UploadImageActivity mContext;
    private CommonProgressDialog progressDialog;
    private List<ImageBean> imgList;
    private boolean hasImage;
    private boolean isDeleteImg;
    private List<ImageBean> needUploadList;
    private boolean uploadControl;
    private boolean deleteControl;
    private KJBitmap kjBitmap = new KJBitmap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        manager = ActivityManager.getInstance();
        manager.pushOneActivity(this);
        mContext = this;
        progressDialog = new CommonProgressDialog(mContext);
        progressDialog.setCancelable(false);
        setContentView(R.layout.activiy_punchcard_upload_img);
        gridView = (GridView) findViewById(R.id.gridview_img);
        hasImage = getIntent().getBooleanExtra("hasImage",true);
        DeliveryApi.getImgList(ClientStateManager.getLoginToken(mContext), getImgListHandler);

        btnOk = (Button)findViewById(R.id.btn_ok);


        takePhotoPop = new TakePhotoPopView(this,
                Constants.TAKE_PIC_RESULT,Constants.CHOSE_PIC_RESULT);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!uploadControl&&!PublicUtil.isFastDoubleClick(1000)) {
                    uploadControl = true;
                    if (images!=null&&images.size() > 1) {
                        needUploadList = new ArrayList<ImageBean>();
                        for (ImageBean img : images) {
                            if (StringUtils.isEmpty(img.getImgId())) {
                                needUploadList.add(img);
                            }
                        }
                        if (needUploadList.size() > 1) {
                            progressDialog.show();
                            DeliveryApi.uploadImg(ClientStateManager.getLoginToken(UploadImageActivity.this), FileUtil.getBytes(needUploadList.get(0).getBitmap()), uploadImageHandler);
                        } else {
                            uploadControl = false;
                            if (isDeleteImg) {
                                setResult(1);
                            }
                            finish();
                        }
                    } else {
                        uploadControl = false;
                        PublicUtil.showToast(getString(R.string.please_select_image));
                    }
                }


            }
        });

        initCustomActionBar();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showDialog();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showDialog() {
        if (isDeleteImg) {
            setResult(1);
        }
        boolean tag = false;
        if (images != null) {
            a : for (int i = 0; i < images.size(); i++) {
                ImageBean img = images.get(i);
                if (StringUtils.isEmpty(img.getImgId()) && i != images.size()-1) {
                    tag = true;
                    break a;
                }
            }
        }

        if (tag) {
            new CommonAlertDialog.Builder(mContext)
                    .setMessage(R.string.image_not_save)
                    .setNegativeButton(R.string.btn_ok,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    finish();
                                }
                            }).setPositiveButton(R.string.btn_cancel, null)
                    .show();
        } else {
            finish();
        }
    }

    private void showNetWorkErrorDialog() {
        new CommonAlertDialog.Builder(mContext)
                .setCancelable(false)
                .setMessage(R.string.image_upload_error)
                .setNegativeButton(R.string.dialog_confirm,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,int which) {
                                setResult(1);
                                finish();
                            }
                        }).show();
    }

    AsyncHttpResponseHandler getImgListHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d("test", "getImgListHandler result = " + responseString);
            try {
                ResultImage result = JSON.parseObject(responseString, ResultImage.class);
                if(null!=result && result.getResponseCode()==Constants.RESPONSE_RESULT_SUCCESS){
                    imgList = result.getImgList();
                    images = new ArrayList<ImageBean>();
                    if (imgList != null && imgList.size() > 0) {
                        for (ImageBean img :imgList) {
                            images.add(img);
                        }
                    }
                    ImageBean imageBean = new ImageBean();
                    images.add(imageBean);
                    adapter = new ImageAdapter();
                    gridView.setAdapter(adapter);
                } else {
                    PublicUtil.showErrorMsg(mContext, result);
                }
            } catch (Exception e) {
                PublicUtil.showToastServerBusy();
            }
        }
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString,
                              Throwable throwable) {
            LogUtils.d("statusCode="+statusCode);
            progressDialog.dismiss();
            PublicUtil.showToastServerOvertime();
        }
    };

    private void initCustomActionBar() {
        CommonActionBar bar = new CommonActionBar(this.getActionBar(), new IActionBarListener() {

            @Override
            public void onBtnRight(View v) {

            }

            @Override
            public void onBtnLeft(View v) {
                showDialog();
            }

            @Override
            public void setTitle(TextView v) {
                v.setText(getText(R.string.upload_imge_title));
            }

        });
    }

    AsyncHttpResponseHandler uploadImageHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

        @Override
        public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
            uploadControl = false;
            if(progressDialog!=null)
            progressDialog.dismiss();
            showNetWorkErrorDialog();
            //PublicUtil.showToastServerOvertime();
        }

        @Override
        public void onSuccess(int i, Header[] headers, String responseString) {
            LogUtils.d(TAG, "uploadImg result = " + responseString);
            try {
                ResultBase result = JSON.parseObject(responseString, ResultBase.class);
                if(result.getResponseCode()== Constants.RESPONSE_RESULT_SUCCESS){
                    needUploadList.remove(0);
                    if (needUploadList.size() > 1) {
                        DeliveryApi.uploadImg(ClientStateManager.getLoginToken(UploadImageActivity.this), FileUtil.getBytes(needUploadList.get(0).getBitmap()), uploadImageHandler);
                    } else {
                        uploadControl = false;
                        if(progressDialog!=null){
                            progressDialog.dismiss();
                        }
                        PublicUtil.showToast(result.getResponseMsg());
                        setResult(1);
                        finish();
                    }
                }else{
                    uploadControl = false;
                    if(progressDialog!=null){
                        progressDialog.dismiss();
                    }
                    PublicUtil.showErrorMsg(mContext, result);
                }
            } catch (Exception e) {
                uploadControl = false;
                if(progressDialog!=null){
                    progressDialog.dismiss();
                }
                PublicUtil.showToastServerBusy();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED)
            return;
        if (resultCode == Activity.RESULT_OK) {
            Bitmap bm = null;
            switch (requestCode) {
                case Constants.TAKE_PIC_RESULT:
                    if(takePhotoPop!=null){
                        bm = takePhotoPop.getTakeImageBitmap();
                    }
                    break;
                case Constants.CHOSE_PIC_RESULT:
                    if(takePhotoPop!=null){
                        bm = takePhotoPop.getPickImageBitmap(data);
                    }
                    break;
            }
            if (bm != null) {
                ImageBean imageBean = new ImageBean(bm);
                images.add(imageBean);
                setSortList(images);
                adapter.notifyDataSetChanged();
            }
        }
    }

    public void setSortList(ArrayList<ImageBean> images) {
        if (images != null) {
            ImageBean second =  images.get(images.size()-2);
            ImageBean last =  images.get(images.size()-1);
            images.set(images.size()-2, last);
            images.set(images.size()-1, second);
        }
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog = null;
    }

    class ImageAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.work_diary_menu_item, null);

            final ImageView imgWork = (ImageView) convertView.findViewById(R.id.img_work);
            final ImageView imgDelete = (ImageView) convertView.findViewById(R.id.img_delete);
            final ImageBean bean = images.get(position);
            if (StringUtils.isNotBlank(bean.getImgId()) && images.size() > 1) {
                kjBitmap.display(imgWork, bean.getImgPath(), new BitmapCallBack() {
                    @Override
                    public void onSuccess(Bitmap bitmap) {
                        ImageBean bean = images.get(position);
                        bean.setBitmap(bitmap);
                        images.set(position, bean);
                    }
                });
            } else {
                if (position == images.size() -1) {
                    Bitmap  bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_add_picture);
                    imgWork.setImageBitmap(bitmap);
                } else {
                    imgWork.setImageBitmap(bean.getBitmap());
                }
            }

           // imgWork.setImageBitmap(bm);
            if (position != images.size()-1) {
                imgDelete.setVisibility(View.VISIBLE);
            } else {
                imgDelete.setVisibility(View.GONE);
            }

            if (position == 5 && position == images.size()-1) {
                imgWork.setVisibility(View.GONE);
            } else {
                imgWork.setVisibility(View.VISIBLE);
            }

            imgWork.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(bean.getBitmap()!=null && position != images.size()-1){
                        DialogUtil.showPictureDialog(UploadImageActivity.this, images.get(position).getBitmap());
                    } else if(StringUtils.isNotBlank(bean.getImgId())){
                        PublicUtil.showToast(getString(R.string.down_image_not_complete));
                    } else {
                        takePhotoPop.getPic(v);
                    }
                }
            });

            imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (deleteControl) {
                        return;
                    }
                    deleteControl = true;
                    if (StringUtils.isNotBlank(bean.getImgId())) {
                        if (bean.getBitmap() != null) {
                            progressDialog.show();
                            DeliveryApi.removeImg(ClientStateManager.getLoginToken(mContext), Long.valueOf(bean.getImgId()), new TextHttpResponseHandler(HTTP.UTF_8) {
                                @Override
                                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                                    deleteControl = false;
                                    if(progressDialog!=null)
                                    progressDialog.dismiss();
                                    PublicUtil.showToastServerOvertime();
                                }

                                @Override
                                public void onSuccess(int i, Header[] headers, String s) {
                                    LogUtils.d(TAG, "deleteImg result = " + s);
                                    if(progressDialog!=null)
                                    progressDialog.dismiss();
                                    try {
                                        ResultBase result = JSON.parseObject(s, ResultBase.class);
                                        if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                                            isDeleteImg = true;
                                            images.remove(position);
                                            adapter.notifyDataSetChanged();
                                            PublicUtil.showToast(result.getResponseMsg());
                                        } else {
                                            PublicUtil.showErrorMsg(mContext, result);
                                        }
                                        deleteControl = false;
                                    } catch (Exception e) {
                                        deleteControl = false;
                                        PublicUtil.showToastServerBusy();
                                    }
                                }
                            });
                        } else {
                            deleteControl = false;
                            PublicUtil.showToast(getString(R.string.down_image_not_complete));
                        }

                    } else {
                        if (position != images.size()-1) {
                            images.remove(position);
                            adapter.notifyDataSetChanged();
                        }
                        deleteControl = false;
                    }

                }
            });

            return convertView;
        }

    }
}
