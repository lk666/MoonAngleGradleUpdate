package cn.com.bluemoon.delivery.module.wash.returning.incabinet;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.incabinet.ResultCupboard;
import cn.com.bluemoon.delivery.module.base.BaseScanCodeActivity;


public class CabinetScanActivity extends BaseScanCodeActivity {

    private String clothesCode;
    private String cupboardCode;


    public static void actStart(Activity context, Fragment fragment, String title, String btnString, String code, int requestCode) {
        actStart(context, fragment, title, btnString, code, CabinetScanActivity.class, requestCode);
    }

    @Override
    protected void onResult(String str, String type, Bitmap barcode) {
        if (TextUtils.isEmpty(cupboardCode)) {
            clothesCode = str;
            showWaitDialog();
            ReturningApi.scanClothes(clothesCode, getToken(), getNewHandler(0, ResultCupboard.class));
        } else{
            showWaitDialog();
            ReturningApi.scanCupboard(clothesCode,cupboardCode,getToken(),getNewHandler(1,ResultBase.class));
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
        //重新启动扫描
        resumeScan();
    }

    @Override
    public void onSuccessException(int requestCode, Throwable t) {
        super.onSuccessException(requestCode, t);
        resumeScan();
    }

    @Override
    public void onErrorResponse(int requestCode, ResultBase result) {
        super.onErrorResponse(requestCode, result);
        resumeScan();
    }

    @Override
    public void onFailureResponse(int requestCode, Throwable t) {
        super.onFailureResponse(requestCode, t);
        resumeScan();
    }
}
