package cn.com.bluemoon.delivery.ui;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.lib.qrcode.utils.BarcodeUtil;
import cn.com.bluemoon.lib.utils.ImageLoaderUtil;

/**
 * Created by bm on 2017/6/3.
 */

public class CourseQcodeView extends FrameLayout{

    private ImageView imgQcode;
    private TextView txtCourseName;
    private TextView txtTime;

    public CourseQcodeView(Context context) {
        super(context);
        init();
    }

    public CourseQcodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_qcode_dialog, this, true);
        imgQcode = (ImageView) findViewById(R.id.img_qcode);
        txtCourseName = (TextView) findViewById(R.id.txt_course_name);
        txtTime = (TextView) findViewById(R.id.txt_time);
    }

    public void setData(String qCodeMark, String name, long startTime,long endTime) {
        imgQcode.setImageBitmap(null);
        imgQcode.setImageBitmap(BarcodeUtil.creatBarcode(qCodeMark));
        txtCourseName.setText(name);
        txtTime.setText(new StringBuffer().append(DateUtil.getTimeToYMDHM(startTime)).append("-").append(DateUtil.getTimeToHours(endTime)).toString());
    }

}
