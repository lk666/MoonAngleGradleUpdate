package cn.com.bluemoon.delivery.module.offline;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.OffLineApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.offline.ResultPlanscan;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.offline.adapter.TeacherCourseSchedulingAdapter;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.lib.qrcode.utils.BarcodeUtil;
import cn.com.bluemoon.lib.utils.LibImageUtil;

/**
 * 排课详情
 */
public class TeacherCourseSchedulingActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;


    private TextView txtCourseName;
    private TextView txtCourseSchedulingTime;
    private ImageView imgQrCode;
    private TextView txtSaveToAlbum;


    private String planCode;
    private TeacherCourseSchedulingAdapter adapter;

    private String qrCodeUrl;

    public static void startAction(Context context, String planCode) {
        Intent intent = new Intent(context, TeacherCourseSchedulingActivity.class);
        intent.putExtra("planCode", planCode);
        context.startActivity(intent);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        planCode = getIntent().getStringExtra("planCode");
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.offline_plan_detail);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_teacher_course_scheduling;
    }

    @Override
    public void initView() {
        adapter = new TeacherCourseSchedulingAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.bindToRecyclerView(recyclerView);
        adapter.addHeaderView(getHeadView());
        adapter.addFooterView(getFootView());
    }

    @Override
    public void initData() {
        OffLineApi.planscan(getToken(), planCode, getNewHandler(0, ResultPlanscan.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        ResultPlanscan resultPlanscan = (ResultPlanscan) result;
        initHeadData(resultPlanscan.data.planInfo);
        adapter.replaceData(resultPlanscan.data.courses);
    }

    public void initHeadData(final ResultPlanscan.PlanInfo info) {
        txtCourseName.setText(info.topic);
        txtCourseSchedulingTime.setText(
                new StringBuffer().append(DateUtil.getDotTime(info
                        .startTime)).append("-")
                        .append(DateUtil.getDotTime(info.endTime)).toString());
        imgQrCode.setImageBitmap(BarcodeUtil.createQRCode(info.qrCodeUrl));
        qrCodeUrl=info.qrCodeUrl;
        txtSaveToAlbum.setOnClickListener(this);
    }


    public View getHeadView() {
        View headView = LayoutInflater.from(this).inflate(R.layout
                .head_teacher_course_scheduling, null);

        txtCourseName = (TextView) headView.findViewById(R.id.txt_course_name);
        txtCourseSchedulingTime = (TextView) headView.findViewById(R.id
                .txt_course_scheduling_time);
        imgQrCode = (ImageView) headView.findViewById(R.id.img_code);
        txtSaveToAlbum = (TextView) headView.findViewById(R.id
                .txt_save_to_album);

        return headView;
    }

    public View getFootView() {
        View footView = new View(this);
        footView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams
                .MATCH_PARENT, ViewUtil.dp2px(30)));
        footView.setBackgroundColor(Color.WHITE);
        return footView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_save_to_album:
                //检查版本是否大于M
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                1);
                    }else {
                        saveToAlbum();
                    }
                }else{
                    saveToAlbum();
                }
                break;
        }
    }

    public void saveToAlbum(){
        //保存到相册
        String fileName = PublicUtil.getPhotoPath();
        if (LibImageUtil.savaBitmap(BarcodeUtil.createQRCode(qrCodeUrl), fileName)) {
            LibImageUtil.refreshImg(this, fileName);
            longToast(String.format(getString(R.string.QR_code_save), fileName));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if(requestCode==1){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveToAlbum();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
