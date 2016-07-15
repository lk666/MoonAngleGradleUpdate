package cn.com.bluemoon.delivery.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cn.com.bluemoon.delivery.R;


import org.kymjs.kjframe.KJBitmap;

/**
 * Created by LIANGJIANGLI on 2016/7/14.
 */
public class ShowMultipleImageView extends Activity {
    private KJBitmap kjBitmap = new KJBitmap();
    private ViewPager viewPager;
    private int currentPage=0;
    private String[] images;

    PagerAdapter adapter=new PagerAdapter(){
        @Override
        public int getCount() {
            return images.length;
        }
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
        @Override
        public void destroyItem(ViewGroup container,int position,Object o){

        }

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
        viewPager = (ViewPager) findViewById(R.id.viewpager1);
        viewPager.setAdapter(adapter);
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
