package cn.com.bluemoon.delivery.module.wash.returning.incabinet;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import java.util.List;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.driver.DriverBox;
import cn.com.bluemoon.delivery.app.api.model.wash.driver.ResultCarriageDetail;
import cn.com.bluemoon.delivery.app.api.model.wash.incabinet.ResultCupboard;
import cn.com.bluemoon.delivery.module.base.BaseScanCodeActivity;


public class CabinetScanActivity extends BaseScanCodeActivity {

    public final static int MODE_CABINET = 0;
    public final static int MODE_DRIVER = 1;
    public final static int MODE_RECEIVER = 2;
    private int mode;
    private String clothesCode;
    private String cupboardCode;
    private ResultCarriageDetail carriageDetail;

    public static void actStart(Activity context,String title) {
        Intent intent = new Intent(context, CabinetScanActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("mode", MODE_CABINET);
        intent.putExtra("btnString", AppContext.getInstance().getString(R.string.with_order_collect_manual_input_code_btn));
        context.startActivityForResult(intent, 0);
    }

    public static void actStart(Fragment fragment,String title) {
        Intent intent = new Intent(fragment.getActivity(), CabinetScanActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("mode", MODE_RECEIVER);
        intent.putExtra("btnString", AppContext.getInstance().getString(R.string.with_order_collect_manual_input_code_btn));
        fragment.startActivityForResult(intent, 0);
    }

    public static void actStart(Activity context,String title,ResultCarriageDetail carriageDetail) {
        Intent intent = new Intent(context, CabinetScanActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("mode", MODE_DRIVER);
        intent.putExtra("item", carriageDetail);
        intent.putExtra("btnString", AppContext.getInstance().getString(R.string.with_order_collect_manual_input_code_btn));
        context.startActivityForResult(intent, 0);
    }

    @Override
    protected void onResult(String str, String type, Bitmap barcode) {
        switch (mode){
            case MODE_CABINET:
                if (TextUtils.isEmpty(cupboardCode)) {
                    clothesCode = str;
                    showWaitDialog();
                    ReturningApi.scanClothes(clothesCode, getToken(), getNewHandler(0, ResultCupboard.class));
                } else{
                    showWaitDialog();
                    ReturningApi.scanCupboard(clothesCode,cupboardCode,getToken(),getNewHandler(1,ResultBase.class));
                }
                break;
            case MODE_DRIVER:
                refreshList(str);
                break;
            case MODE_RECEIVER:
                break;
        }

    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        mode = getIntent().getIntExtra("mode",-1);
        if(mode == MODE_DRIVER){
            carriageDetail = (ResultCarriageDetail)getIntent().getSerializableExtra("item");
        }
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        if(requestCode == 0){
            ResultCupboard cupboard = (ResultCupboard)result;
            cupboardCode = cupboard.getCupboardCode();
            setTxtCode(cupboardCode);
        }else if(requestCode == 1){
            toast(result.getResponseMsg());
            clothesCode = null;
            cupboardCode = null;
            clearTxtCode();
        }
        //重新延时启动扫描，默认一秒延时
        startDelay();
    }

    /**
     * 更新扫描列表
     * @param code
     */
    private void refreshList(String code){
        List<DriverBox> list = carriageDetail.getBoxList();
        boolean isExist = false;
        boolean isFinish = true;
        for (DriverBox item:list){
            if(code.equals(item.getBoxCode())){
                if(item.isCheck){
                    toast(R.string.driver_scan_repeat);
                    startDelay();
                    return;
                }
                item.isCheck = true;
                isExist = true;
            }
            if(!item.isCheck){
                isFinish = false;
            }
        }
        if(!isExist){
            toast(R.string.driver_scan_not_exist);
            startDelay();
            return;
        }
        if(isFinish) {
            toast(R.string.driver_scan_finish);
            finish();
        }else{
            toast(R.string.driver_scan_success);
            startDelay();
        }

    }

    @Override
    public void finish() {
        if(mode==MODE_DRIVER){
            Intent intent = new Intent();
            intent.putExtra("item",carriageDetail);
            setResult(RESULT_OK, intent);
        }
        super.finish();
    }
}
