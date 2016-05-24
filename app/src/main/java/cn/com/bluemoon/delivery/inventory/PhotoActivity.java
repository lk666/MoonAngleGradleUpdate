/**
 * Description:
 * Copyright: Copyright (c) 2016
 * Company: BLUE MOON
 *
 * @author: wangshanhai
 * @version 3.1.0
 * @date: 2016/3/23
 * @todo: Big image view
 */
package cn.com.bluemoon.delivery.inventory;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.umeng.analytics.MobclickAgent;

import org.kymjs.kjframe.KJBitmap;
import org.kymjs.kjframe.bitmap.BitmapCallBack;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.manager.ActivityManager;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

public class PhotoActivity extends Activity implements OnClickListener {
    private String TAG = "PhotoActivity";

    private Activity main;

    private CommonProgressDialog progressDialog;

    private ImageView ivImage;
    private ProgressBar pbImage;

    private String imgUrl;
    private String type;

    KJBitmap kjb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.photo_tikect_image);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏
        ActivityManager.getInstance().pushOneActivity(this);
        init();
        initView();
    }

    private void init() {
        main = this;
        if (kjb == null) kjb = new KJBitmap();
        imgUrl = getIntent().getStringExtra("imgUrl");
        type = getIntent().getStringExtra("type");
        progressDialog = new CommonProgressDialog(main);
    }

    private void initView() {
        ivImage = (ImageView) findViewById(R.id.iv_image);
        ivImage.setOnClickListener(this);
        pbImage =(ProgressBar) findViewById(R.id.pb_image);


        if("".equals(imgUrl)|| imgUrl == null){
            PublicUtil.showToast(main, getString(R.string.get_photo_fail));
            return;
        }

        kjb.display(ivImage, imgUrl, new BitmapCallBack() {
            @Override
            public void onPreLoad() {
                super.onPreLoad();
                pbImage.setVisibility(View.VISIBLE);
            }

            @Override
            public void onDoHttp() {
                super.onDoHttp();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                pbImage.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Exception e) {
                super.onFailure(e);
                PublicUtil.showToast(main,getString(R.string.get_big_photo_data_fail));
                main.finish();
            }

            @Override
            public void onSuccess(Bitmap bitmap) {
                super.onSuccess(bitmap);
            }
        });

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_image:
                finish();
                break;
        }
    }


    public static void actionStart(Context context, String type, String imgUrl) {
        Intent intent = new Intent(context, PhotoActivity.class);
        intent.putExtra("imgUrl", imgUrl);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }


}


