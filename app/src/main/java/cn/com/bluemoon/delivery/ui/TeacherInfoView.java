package cn.com.bluemoon.delivery.ui;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.lib.utils.ImageLoaderUtil;

/**
 * Created by bm on 2017/6/3.
 */

public class TeacherInfoView extends FrameLayout{

    private ImageView imgHead;
    private TextView txtName;

    public TeacherInfoView(Context context) {
        super(context);
        init();
    }

    public TeacherInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_teacher_info, this, true);
        imgHead = (ImageView) findViewById(R.id.img_head);
        txtName = (TextView) findViewById(R.id.txt_name);
    }

    public void setData(String pic, String name) {
        if(!TextUtils.isEmpty(pic)){
            ImageLoaderUtil.displayImage(pic, imgHead);
        }
        txtName.setText(name);
    }

}
