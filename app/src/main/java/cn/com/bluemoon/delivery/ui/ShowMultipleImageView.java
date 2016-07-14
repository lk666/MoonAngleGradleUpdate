package cn.com.bluemoon.delivery.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.utils.LogUtils;

import android.app.ActionBar.LayoutParams;
import android.widget.ViewSwitcher;

import org.kymjs.kjframe.KJBitmap;
import org.kymjs.kjframe.bitmap.BitmapCallBack;

/**
 * Created by Administrator on 2016/7/14.
 */
public class ShowMultipleImageView extends Activity {
    private KJBitmap kjBitmap = new KJBitmap();
    private ViewPager viewPager;
    private int currentPage=0;
    private String[] images;
    /*private int images[] = new int[]{
            R.mipmap.card_banner,
            R.mipmap.welcome,
            R.mipmap.personal_default,
            R.mipmap.arrow_down,
            R.mipmap.icon_moonfriend,
            R.mipmap.qcode_left_nav
    };*/

    PagerAdapter adapter=new PagerAdapter(){
        @Override
        public int getCount() {
            return images.length;
        }
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0==arg1;
        }
        @Override
        public void destroyItem(ViewGroup container,int position,Object o){

        }

        //设置ViewPager指定位置要显示的view
        @Override
        public Object instantiateItem(ViewGroup container,int position){
            ImageView im=new ImageView(ShowMultipleImageView.this);
            kjBitmap.display(im, images[position]);
            container.addView(im);
            im.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            return im;

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_show_pic);
        images = (String[]) getIntent().getSerializableExtra("bitmaps");
        //LogUtils.d("imagess.szie="+imagess.length);
        viewPager = (ViewPager) findViewById(R.id.viewpager1);
        viewPager.setAdapter(adapter);
        //存放点点的容器
        final TextView txtView = (TextView) findViewById(R.id.txt_page);
        txtView.setText("1/"+images.length);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                currentPage=position;
                txtView.setText(currentPage+1 + "/"+ images.length);
            }

        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }


}
