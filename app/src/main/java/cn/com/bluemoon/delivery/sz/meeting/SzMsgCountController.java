package cn.com.bluemoon.delivery.sz.meeting;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;
import org.kymjs.kjframe.utils.StringUtils;

import java.util.ArrayList;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.sz.api.SzApi;
import cn.com.bluemoon.delivery.sz.api.response.MsgMainTypeResponse;
import cn.com.bluemoon.delivery.sz.bean.MainMsgCountBean;
import cn.com.bluemoon.delivery.sz.util.AssetUtil;
import cn.com.bluemoon.delivery.sz.util.AsyncHttpClientUtil;
import cn.com.bluemoon.delivery.sz.util.FileUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

/**
 * Created by dujiande on 2016/8/16.
 */
public class SzMsgCountController {
    private String TAG = SzMsgCountController.class.getSimpleName();

    private static SzMsgCountController ourInstance = new SzMsgCountController();

    public static SzMsgCountController getInstance() {
        return ourInstance;
    }

    private SzMsgCountController() {
    }

    private ArrayList<MainMsgCountBean> msgCountBeanArrayList;

    public ArrayList<MainMsgCountBean> getMsgCountBeanArrayList() {
        return msgCountBeanArrayList;
    }

    public void setMsgCountBeanArrayList(ArrayList<MainMsgCountBean> msgCountBeanArrayList) {
        this.msgCountBeanArrayList = msgCountBeanArrayList;
    }

    /**
     * 消息统计初始化
     */
    public void initMsgCount() {
        Context context = AppContext.getInstance().getApplicationContext();
        String responseString = AssetUtil.getContent(context,"msgMainType.txt");
        MsgMainTypeResponse response = JSON.parseObject(responseString,MsgMainTypeResponse.class);
        msgCountBeanArrayList = response.getMainTypeNews();
    }

    /**
     * 消息统计融合
     * @param datelist
     */
    public void mergeMsgCount(ArrayList<MainMsgCountBean> datelist){
        if(datelist != null && !datelist.isEmpty()){
            for(MainMsgCountBean item : datelist){
                for(MainMsgCountBean mainItem : msgCountBeanArrayList){
                    if(mainItem.getMsgType() == item.getMsgType()){
                        mainItem.setMsgCounts(item.getMsgCounts());
                        mainItem.setMsgId(item.getMsgId());
                        mainItem.setMsgInfo(item.getMsgInfo());
                        mainItem.setMsgTime(item.getMsgTime());
                    }
                }
            }
        }
    }

    /**
     * 计算所有的未读信息数量
     * @return
     */
    public int caculateUnReadMsg(){
        int total = 0;
        if(msgCountBeanArrayList != null && !msgCountBeanArrayList.isEmpty()){
            for(MainMsgCountBean mainItem : msgCountBeanArrayList){
                total += mainItem.getMsgCounts();
            }
        }
        return total;
    }

    public void getMsgMainTypeCount(final Context context, final boolean showloading, final boolean isRefresh,
                                    final RequestListener requestListener) {
        String token = ClientStateManager.getLoginToken(context);
        String staffNum = ClientStateManager.getUserName();
        final CommonProgressDialog progressDialog = new CommonProgressDialog(context);
        if(!StringUtils.isEmpty(token)){
            //先去SD卡读缓存
            String cache= FileUtil.getMainMsgCount(staffNum);
            if(!StringUtil.isEmptyString(cache)){
                requestListener.getCacheCallBack(cache);
            }
            //判读是否要更新
            if(FileUtil.isUpdateMsgMainTypeCount(staffNum) || isRefresh){
                SzApi.msgMainType(staffNum,new TextHttpResponseHandler(HTTP.UTF_8) {

                    public void onStart(){
                        super.onStart();
                        if(showloading){
                            progressDialog.show();
                        }

                    }

                    public void onFinish(){
                        super.onFinish();
                        if(showloading){
                            progressDialog.dismiss();
                            requestListener.stopLoad();
                        }

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        LogUtils.d(TAG,responseString);
                        requestListener.getHttpCallBack(responseString);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString,
                                          Throwable throwable) {
                        LogUtils.e(TAG, throwable.getMessage());
                        AsyncHttpClientUtil.onFailure(context,statusCode);
                    }
                });

            }
        }

    }




}
