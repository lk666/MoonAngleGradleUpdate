package cn.com.bluemoon.delivery.module.contract;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.bluemoon.signature.lib.AbstractSignatureActivity;

import cn.com.bluemoon.delivery.R;

/**
 * Created by liangjiangli on 2018/1/9.
 */

public class SignatureActivity extends AbstractSignatureActivity implements View.OnClickListener {

    /**
     *
     * @param context
     * @param dirPath 签名保存文件夹路径
     * @param requestCode
     */
    public static void startAct(Activity context, String dirPath, int requestCode) {
        Intent intent = new Intent(context, SignatureActivity.class);
        intent.putExtra(AbstractSignatureActivity.DIR_PATH, dirPath);
        context.startActivityForResult(intent,requestCode);
    }

    /**
     *
     * @param context
     * @param dirPath 签名保存文件夹路径
     * @param eraseColor 底图颜色，默认透明
     * @param requestCode
     */
    public static void startAct(Activity context, String dirPath, int eraseColor, int requestCode) {
        Intent intent = new Intent(context, SignatureActivity.class);
        intent.putExtra(AbstractSignatureActivity.ERASE_COLOR, eraseColor);
        intent.putExtra(AbstractSignatureActivity.DIR_PATH, dirPath);
        context.startActivityForResult(intent,requestCode);
    }

    @Override
    public int getTitleLayoutId() {
        return R.layout.signature_pad_title_bar;
    }

    @Override
    public void initView(View mainView) {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.tv_right).setOnClickListener(this);
        setBitmapSize(800);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                setResult(2);
                finish();
                break;
            case R.id.tv_right:
                onSaveClick();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        setResult(2);
        finish();
    }
}
