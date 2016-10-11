package cn.com.bluemoon.delivery.module.wash.returning.manager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.GestureStroke;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.MainActivity;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.utils.LibFileUtil;
import cn.com.bluemoon.lib.utils.LibImageUtil;

/**
 * Created by liangjiangli on 2016/9/5.
 */
public class ManualSignActivity extends BaseActivity implements OnGesturePerformedListener {
    @Bind(R.id.gesture)
    GestureOverlayView mDrawGestureView;

    private static ArrayList<GestureStroke> strokes;

    public static void actStart(Activity context, int requestCode) {
        Intent intent = new Intent(context, ManualSignActivity.class);
        context.startActivityForResult(intent, requestCode);
    }

    public static void actStart(Fragment fragment, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), ManualSignActivity.class);
        fragment.startActivityForResult(intent, requestCode);
    }


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
                    Bitmap bitmap = Bitmap.createBitmap(width, height,Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
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
                    toast(getString(R.string.manage_not_sign));
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
