package cn.com.bluemoon.delivery.module.wash.returning.manager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.GestureStroke;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.MainActivity;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.utils.LibImageUtil;

/**
 * Created by liangjiangli on 2016/9/5.
 */
public class ManualSignActivity extends BaseActivity implements OnGesturePerformedListener {
    @Bind(R.id.gesture)
    GestureOverlayView mDrawGestureView;

    private static ArrayList<GestureStroke> strokes;


    @Override
    protected int getLayoutId() {
        return R.layout.gesture;
    }

    @Override
    public void initView() {



        //txtFinish.setBackgroundColor(Color.RED);
        //btnReset.bringToFront();
        //mDrawGestureView.b
        //设置手势可多笔画绘制，默认情况为单笔画绘制
        mDrawGestureView.setGestureStrokeType(GestureOverlayView.GESTURE_STROKE_TYPE_MULTIPLE);
        //设置手势的颜色(蓝色)
        mDrawGestureView.setGestureColor(
                gestureColor(R.color.black)
        );
        //设置还没未能形成手势绘制是的颜色(红色)
        mDrawGestureView.setUncertainGestureColor(
                gestureColor(R.color.black)
        );
        mDrawGestureView.setDrawingCacheBackgroundColor(Color.WHITE);
        //设置手势的粗细
        mDrawGestureView.setGestureStrokeWidth(4);
            /*手势绘制完成后淡出屏幕的时间间隔，即绘制完手指离开屏幕后相隔多长时间手势从屏幕上消失；
         * 可以理解为手势绘制完成手指离开屏幕后到调用onGesturePerformed的时间间隔
         * 默认值为420毫秒，这里设置为2秒
        */
        mDrawGestureView.setFadeOffset(36000000);//清除前设置时间间隔缩小

        mDrawGestureView.setGestureStrokeAngleThreshold(180);

        //绑定监听器
        mDrawGestureView.addOnGesturePerformedListener(this);
    }

    @OnClick({R.id.btn_back, R.id.btn_reset, R.id.txt_finish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_reset:
                mDrawGestureView.setBackgroundColor(Color.WHITE);
                mDrawGestureView.setFadeOffset(100);//清除前设置时间间隔缩小
                mDrawGestureView.clear(true);
                mDrawGestureView.setFadeOffset(36000000);//清楚后恢复时间间隔
                break;
            case R.id.txt_finish:
                if (mDrawGestureView.getGesture() != null) {
                    int width = mDrawGestureView.getMeasuredWidth();
                    int height = mDrawGestureView.getMeasuredHeight();
                    final Bitmap bitmap =Bitmap.createBitmap(width, height,Bitmap.Config.ARGB_8888);
                    final Canvas canvas = new Canvas(bitmap);
                    canvas.drawColor(Color.WHITE);

                    final Paint paint = new Paint();
                    paint.setAntiAlias(true);
                    paint.setDither(true);
                    paint.setColor(Color.BLACK);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeJoin(Paint.Join.ROUND);
                    paint.setStrokeCap(Paint.Cap.ROUND);
                    paint.setStrokeWidth(4);

                    final Path path = toPath(null, mDrawGestureView.getGesture().getStrokes());
                    final RectF bounds = new RectF();
                    path.computeBounds(bounds, true);
                    canvas.translate(0, 0);
                    canvas.drawPath(path, paint);
                    String fileName = PublicUtil.getPhotoPath();
                    LibImageUtil.savaBitmap(bitmap, fileName);
                    Intent intent = new Intent();
                    intent.putExtra("fileName", fileName);
                    setResult(1, intent);
                    finish();
                } else {
                    PublicUtil.showToast("没签名点完成该提示什么文本？");
                }
                break;
        }
    }

    public Path toPath(Path path, ArrayList<GestureStroke> mStrokes) {
        if (path == null) path = new Path();
        if (mStrokes == null) return path;
        final ArrayList<GestureStroke> strokes = mStrokes;
        final int count = strokes.size();

        for (int i = 0; i < count; i++) {
            path.addPath(strokes.get(i).getPath());
        }

        return path;
    }


    private int gestureColor(int resId) {
        return getResources().getColor(resId);
    }


    @Override
    public void onGesturePerformed(GestureOverlayView overlay, final Gesture gesture) {
        /*View dialogView = getLayoutInflater().inflate(R.layout.show_gesture, null);
        //imageView用于显示绘制的手势
        ImageView imageView = (ImageView) dialogView.findViewById(R.id.show);
        //获取用户保存手势的名字
        EditText editText = (EditText) dialogView.findViewById(R.id.name);
        final String name = editText.getText().toString();
        // 调用Gesture的toBitmap方法形成对应手势的位图
        final Bitmap bitmap = gesture.toBitmap(500, 1000, 10, gestureColor(R.color.title_background));


        AlertDialog.Builder dialogBuider = new AlertDialog.Builder(ManualSignActivity.this);
        dialogBuider.setView(dialogView);
        //绑定对话框的确认按钮监听事件
        dialogBuider.setPositiveButton(
                "保存", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 添加手势
                        //sStore.addGesture(name, gesture);
                        // 保存添加的手势
                        //sStore.save();
                    }
                });
        //绑定对话框的取消按钮监听事件
        dialogBuider.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDrawGestureView.setFadeOffset(100);//清除前设置时间间隔缩小
                mDrawGestureView.clear(true);
                mDrawGestureView.setFadeOffset(360000);//清楚后恢复时间间隔

            }
        });
        //显示对话框
        dialogBuider.show();*/


    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        //移除绑定的监听器
        mDrawGestureView.removeOnGesturePerformedListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {

    }

}
