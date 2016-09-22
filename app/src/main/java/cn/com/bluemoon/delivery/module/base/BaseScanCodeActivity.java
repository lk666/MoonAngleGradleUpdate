package cn.com.bluemoon.delivery.module.base;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.common.InputCodeActivity;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.ViewUtil;

/**
 * 带二维码和条形图案的扫描类
 */
public abstract class BaseScanCodeActivity extends BaseScanActivity {

    @Bind(R.id.btn_input)
    Button btnInput;
    @Bind(R.id.txt_code)
    TextView txtCode;
    @Bind(R.id.layout_code)
    RelativeLayout layoutCode;

    /**
     * Activity扫描界面调起方法
     * @param context
     * @param title 界面标题，默认标题为“扫一扫”
     * @param btnString 手动输入按钮的问题，null则不显示
     * @param code 标题下面的编码，null则不显示
     * @param clazz
     * @param requestCode
     */
    public static void actStart(Activity context,String title, String btnString,String code, Class clazz, int requestCode) {
        Intent intent = new Intent(context, clazz);
        intent.putExtra("title", title);
        intent.putExtra("code", code);
        intent.putExtra("btnString", btnString);
        context.startActivityForResult(intent, requestCode);
    }

    /**
     * fragment扫描界面调起方法
     */
    public static void actStart(Fragment fragment, String title, String btnString,String code, Class clazz, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), clazz);
        intent.putExtra("title", title);
        intent.putExtra("code", code);
     intent.putExtra("btnString", btnString);
        fragment.startActivityForResult(intent, requestCode);
    }

    /*必须重写*/
    /**
     * 返回结果事件重写
     * 注：如果需要连续扫描：
     * 处理完数据需要继续扫描时再调用resumeScan或startDelay方法调起扫描；
     * @param str  返回的扫描内容
     * @param type 二维码类型
     * @param barcode 扫描区域的图像
     */
    @Override
    protected abstract void onResult(String str, String type, Bitmap barcode);

    /*可重写*/
    /**
     * 手动输入的点击事件处理,默认返回resultCode == Constants.RESULT_SCAN
     */
    protected void onBtnClick(View view){
        InputCodeActivity.actStart(this,0,InputCodeActivity.class);
    }

    /**
     * 手动输入返回输入内容
     * @param code
     */
    protected void onResultCode(String code){
        onResult(code,null,null);
    }


    /*公共方法*/

    /**
     * 设置手动输入的按钮
     * @param btnString null则不显示
     */
    final protected void setInputBtn(String btnString){
        if (!TextUtils.isEmpty(btnString)) {
            btnInput.setText(btnString);
            ViewUtil.setViewVisibility(btnInput, View.VISIBLE);
        }else{
            ViewUtil.setViewVisibility(layoutCode,View.GONE);
        }
    }

    /**
     * 设置顶部显示的内容
     * @param code null则不显示
     */
    final protected void setTxtCode(String code){
        if (!TextUtils.isEmpty(code)) {
            txtCode.setText(code);
            ViewUtil.setViewVisibility(layoutCode,View.VISIBLE);
        }else{
            ViewUtil.setViewVisibility(layoutCode,View.GONE);
        }
    }

    /**
     * 清除顶部显示的内容
     */
    final protected void clearTxtCode(){
        setTxtCode(null);
    }

    @Override
    final protected int getLayoutId() {
        return R.layout.activity_scan_code;
    }

    @Override
    final protected String getTitleString() {
        return title;
    }

    @Override
    final protected void initView() {
        super.initView();
        setInputBtn(getIntent().getStringExtra("btnString"));
        setTxtCode(getIntent().getStringExtra("code"));
    }

    @Override
    final protected int getSurfaceViewId() {
        return R.id.preview_view;
    }

    @Override
    final protected int getViewfinderViewId() {
        return R.id.viewfinder_view;
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {

    }

    @OnClick(R.id.btn_input)
    public void onClick() {
        onBtnClick(btnInput);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0&& resultCode == RESULT_OK&&data!=null){
            onResultCode(data.getStringExtra(Constants.RESULT_CODE));
        }
    }
}
