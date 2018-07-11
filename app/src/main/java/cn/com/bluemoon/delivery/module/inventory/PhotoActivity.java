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
package cn.com.bluemoon.delivery.module.inventory;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import org.kymjs.kjframe.KJBitmap;
import org.kymjs.kjframe.bitmap.BitmapCallBack;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.utils.StringUtil;

public class PhotoActivity extends BaseActivity {

    @BindView(R.id.iv_image)
    ImageView ivImage;
    @BindView(R.id.pb_image)
    ProgressBar pbImage;
    private String imgUrl;
    KJBitmap kjb;
    private PhotoActivity main;

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏
        main = this;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.photo_tikect_image;
    }

    public static void actionStart(Context context, String imgUrl) {
        Intent intent = new Intent(context, PhotoActivity.class);
        intent.putExtra("imgUrl", imgUrl);
        context.startActivity(intent);
    }


    @Override
    public void initView() {
        if (kjb == null) kjb = new KJBitmap();
        imgUrl = getIntent().getStringExtra("imgUrl");
    }

    @Override
    public void initData() {
        if (StringUtil.isEmpty(imgUrl)) {
            toast(R.string.get_photo_fail);
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
                toast(R.string.get_big_photo_data_fail);
                main.finish();
            }

            @Override
            public void onSuccess(Bitmap bitmap) {
                super.onSuccess(bitmap);
            }
        });
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {

    }

    @OnClick(R.id.iv_image)
    public void onClick() {
        finish();
    }
}


