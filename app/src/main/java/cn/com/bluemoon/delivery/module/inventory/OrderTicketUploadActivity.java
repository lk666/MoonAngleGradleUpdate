/**
 * Description:
 * Copyright: Copyright (c) 2016
 * Company: BLUE MOON
 *
 * @author: wangshanhai
 * @version 3.1.0
 * @date: 2016/3/27
 * @todo: select ticket picture to upload
 */
package cn.com.bluemoon.delivery.module.inventory;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;
import org.kymjs.kjframe.KJBitmap;
import org.kymjs.kjframe.bitmap.BitmapCallBack;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.inventory.ResultTicketPhotoVo;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.manager.ActivityManager;
import cn.com.bluemoon.lib.view.CommonProgressDialog;
import cn.com.bluemoon.lib.view.ScrollGridView;
import cn.com.bluemoon.lib.view.TakePhotoPopView;

public class OrderTicketUploadActivity extends Activity implements OnClickListener {
    private String TAG = "OrderTicketUploadActivity";

    private String type;
    private Activity main;

    private int MAX = 5;
    private OrderGridViewAdapter adapter;
    private CommonProgressDialog progressDialog;
    private ScrollGridView gridviewTicketPic;
    private ImageView ivAddPic;
    private Button btnPicOk;
    private LinearLayout llOk;
    private List<String> list;

    private String relativeOrderCode;


    private TakePhotoPopView takePhotoPop;
    private Bitmap bm;

    private String flag = "NO";//YES表示记录了用户需要继续上传单据


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.order_ticket_pic_upload);
        ActivityManager.getInstance().pushOneActivity(this);
        init();
        initView();
    }

    private void init() {
        main = this;
        list = new ArrayList<String>();
        type = getIntent().getStringExtra("type");

        relativeOrderCode = getIntent().getStringExtra("relativeOrderCode");

        if (getIntent().hasExtra("piclist")) {
            ArrayList<String> listPath = getIntent().getBundleExtra("piclist").getStringArrayList("piclist");
            list.addAll(listPath);
            if (!"endReceive".equals(type)) {
                if (list.size() < 5) {
                    list.add("addPic");
                }
            }
        }
        progressDialog = new CommonProgressDialog(main);
        initCustomActionBar();

    }

    private void initView() {
        ivAddPic = (ImageView) findViewById(R.id.iv_add_pic);
        ivAddPic.setOnClickListener(this);
        btnPicOk = (Button) findViewById(R.id.btn_pic_ok);
        btnPicOk.setOnClickListener(this);
        llOk = (LinearLayout) findViewById(R.id.ll_ok);

        gridviewTicketPic = (ScrollGridView) findViewById(R.id.gridview_ticket_pic);
        gridviewTicketPic.setVisibility(View.VISIBLE);
        adapter = new OrderGridViewAdapter(main, list);
        gridviewTicketPic.setAdapter(adapter);
        initViewVisable();
    }


    private void initViewVisable() {
        if ("endReceive".equals(type)) {
            ivAddPic.setVisibility(View.GONE);
            llOk.setVisibility(View.GONE);
            getData();
        } else {
            if (list.size() > 0) {
                ivAddPic.setVisibility(View.GONE);
                gridviewTicketPic.setVisibility(View.VISIBLE);
            } else {
                ivAddPic.setVisibility(View.VISIBLE);
                gridviewTicketPic.setVisibility(View.GONE);
            }
        }

    }

    private boolean isDeliver() {
        if (InventoryTabActivity.DELIVERY_MANAGEMENT.equals(type)) {
            return true;
        } else {
            return false;
        }
    }

    private void initCustomActionBar() {
        CommonActionBar actionBar = new CommonActionBar(getActionBar(), new IActionBarListener() {

            @Override
            public void setTitle(TextView v) {
                // TODO Auto-generated method stub
           /*     if (isDeliver()) {
                    v.setText(getResources().getString(R.string.order_upload_deliver_pic_ticket));
                } else {
                    v.setText(getResources().getString(R.string.order_upload_receive_pic_ticket));
                }
*/
                v.setText(getResources().getString(R.string.text_deliver_ticket_tip));
            }

            @Override
            public void onBtnRight(View v) {
                // TODO Auto-generated method stub
                llOk.setVisibility(View.VISIBLE);
                flag = "YES";
                handler.obtainMessage(1002, list).sendToTarget();
            }

            @Override
            public void onBtnLeft(View v) {
                // TODO Auto-generated method stub
                // finish();
                for (int i = 0; i < list.size(); i++) {
                    if ("addPic".equals(list.get(i).toString())) {
                        list.remove(i);
                    }
                }
                Intent it = new Intent();
                Bundle b = new Bundle();
                b.putStringArrayList("datalist", (ArrayList<String>) list);
                it.putExtra("datalist", b);
                setResult(RESULT_OK, it);
                main.finish();
            }
        });

      /*  Drawable drawableFillter = getResources().getDrawable(R.mipmap.icon_filter);
        drawableFillter.setBounds(0, 0, drawableFillter.getMinimumWidth(), drawableFillter.getMinimumHeight());
        actionBar.getTvRightView().setCompoundDrawables(drawableFillter, null, null, null);
        if ("endReceive".equals(type)) {
            actionBar.getTvRightView().setVisibility(View.VISIBLE);
        }*/

    }


    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_add_pic:
                showTakePhotoView();
                break;
            case R.id.btn_pic_ok:
                for (int i = 0; i < list.size(); i++) {
                    if ("addPic".equals(list.get(i).toString())) {
                        list.remove(i);
                    }
                }
                Intent it = new Intent();
                Bundle b = new Bundle();
                b.putStringArrayList("datalist", (ArrayList<String>) list);
                it.putExtra("datalist", b);
                setResult(RESULT_OK, it);
                main.finish();
                break;
        }
    }


    private void getData() {
        if (relativeOrderCode == null || "".equals(relativeOrderCode)) {
            return;
        }
        String token = ClientStateManager.getLoginToken(main);
        if (progressDialog != null) {
            progressDialog.show();
        }
        DeliveryApi.getPicDetail(token, relativeOrderCode, getTicketPhotoHandler);
    }


    AsyncHttpResponseHandler getTicketPhotoHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            LogUtils.d(TAG, "deliverOrderDetailHandler result = " + responseString);


            if (progressDialog != null)
                progressDialog.dismiss();
            try {


                ResultTicketPhotoVo photo = JSON.parseObject(responseString,
                        ResultTicketPhotoVo.class);

                if (photo.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    // setData(deliverOrderDetailInfo);
                    if (photo.getPicList() != null || photo.getPicList().size() > 0) {
                        if (photo.getPicList().size() > 5) {
                            list.addAll(photo.getPicList().subList(0, 5));
                        } else {
                            list.addAll(photo.getPicList());
                        }
                    }
                    handler.obtainMessage(1001, list).sendToTarget();
                }

            } catch (Exception e) {
                LogUtils.e(TAG, e.getMessage());
                PublicUtil.showToastServerBusy();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            LogUtils.e(TAG, throwable.getMessage());
            if (progressDialog != null)
                progressDialog.dismiss();
            PublicUtil.showToastServerOvertime();
        }
    };


    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case 1002:
                    for (int i = 0; i < list.size(); i++) {
                        if ("addPic".equals(list.get(i).toString())) {
                            list.remove(i);
                        }
                    }
                    if (list.size() < 5) {
                        list.add("addPic");
                    }
                    adapter.notifyDataSetChanged();
                    break;
                case 1001:
                    for (int i = 0; i < list.size(); i++) {
                        if ("addPic".equals(list.get(i).toString())) {
                            list.remove(i);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    break;
                case 100:
                    ivAddPic.setVisibility(View.GONE);
                    llOk.setVisibility(View.VISIBLE);

                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }

    };


    private void showTakePhotoView() {
        if (takePhotoPop == null) {
            takePhotoPop = new TakePhotoPopView(main,
                    Constants.TAKE_PIC_RESULT, Constants.CHOSE_PIC_RESULT);
        }
        takePhotoPop.getPic(ivAddPic);
    }


    public void showToastGetFailPhoto() {
        PublicUtil.showToast(getString(R.string.get_photo_data_fail));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_CANCELED)
            return;
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.TAKE_PIC_RESULT:
                    if (null == takePhotoPop) {
                        showToastGetFailPhoto();
                        return;
                    }
                    Uri uri = takePhotoPop.getTakeImageUri();
                    if (null == uri) {
                        showToastGetFailPhoto();
                        return;
                    }
                    list.add(ImageUtil.getRealFilePath(main, uri));

                    break;
                case Constants.CHOSE_PIC_RESULT:
                    if (null == takePhotoPop) {
                        showToastGetFailPhoto();
                        return;
                    }
                    Uri uri_pick = takePhotoPop.getPickImageUri(data);
                    if (uri_pick == null) {
                        showToastGetFailPhoto();
                        return;
                    }
                    list.add(ImageUtil.getRealFilePath(main, uri_pick));
                    break;

            }

        }
        if (list.size() > 0) {
            ivAddPic.setVisibility(View.GONE);
            gridviewTicketPic.setVisibility(View.VISIBLE);
        } else {
            ivAddPic.setVisibility(View.VISIBLE);
            gridviewTicketPic.setVisibility(View.GONE);
        }
        handler.obtainMessage(1002, list).sendToTarget();

    }


    public static void actionStart(Context context, String type) {
        Intent intent = new Intent(context, OrderTicketUploadActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    class OrderGridViewAdapter extends BaseAdapter {

        LayoutInflater mInflater;
        public KJBitmap kjb;

        private List<String> list;
        Context context;

        public OrderGridViewAdapter(Context context, List<String> data) {
            this.context = context;
            this.mInflater = LayoutInflater.from(context);
            this.list = data;
        }

        public void setList(List<String> list) {
            this.list = list;
        }

        @Override
        public int getCount() {

            // TODO Auto-generated method stub、
            return list.size();
        }


        @Override
        public Object getItem(int position) {

            // TODO Auto-generated method stub
            return position;
        }


        @Override
        public long getItemId(int position) {

            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            //   if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.gridview_pic_item, null);
            holder.iv_item_image = (ImageView) convertView.findViewById(R.id.iv_item_image);
            holder.iv_item_delete = (ImageButton) convertView.findViewById(R.id.iv_item_delete);
            convertView.setTag(holder);
        /*    } else {
                holder = (ViewHolder) convertView.getTag();
            }*/

            if (kjb == null) kjb = new KJBitmap();

            if ("addPic".equals(list.get(position)) && position < 5) {
                holder.iv_item_image.setBackgroundDrawable(null);
                holder.iv_item_image.setImageBitmap(null);
                holder.iv_item_image.setBackgroundResource(R.mipmap.addpic);
                holder.iv_item_image.setScaleType(ImageView.ScaleType.FIT_XY);
                holder.iv_item_delete.setVisibility(View.GONE);
            } else {
                holder.iv_item_delete.setVisibility(View.VISIBLE);

                //  kjb.display(holder.iv_item_image, list.get(position).toString(), 200, 200, R.mipmap.loading_img_logo);

                kjb.display(holder.iv_item_image, list.get(position).toString(), new BitmapCallBack() {
                    @Override
                    public void onPreLoad() {
                        super.onPreLoad();
                        //pb_image.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onDoHttp() {
                        super.onDoHttp();
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        // pb_image.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        super.onFailure(e);

                    }

                    @Override
                    public void onSuccess(Bitmap bitmap) {
                        super.onSuccess(bitmap);
                    }
                });


            }

            if ("endReceive".equals(type) && "NO".equals(flag)) {
                holder.iv_item_delete.setVisibility(View.GONE);
            }

            if (list.get(position).toString().contains("http://")) {
                holder.iv_item_delete.setVisibility(View.GONE);
            } else {
                if (!"addPic".equals(list.get(position).toString())) {
                    holder.iv_item_delete.setVisibility(View.VISIBLE);
                }
            }

            holder.iv_item_image.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ("addPic".equals(list.get(position))) {
                        showTakePhotoView();
                    } else {
                        PhotoActivity.actionStart(main, "img", list.get(position).toString());
                    }
                }
            });

            holder.iv_item_delete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    list.remove(position);
                    handler.obtainMessage(1002, list).sendToTarget();
                }
            });
            return convertView;
        }

        final class ViewHolder {
            private ImageView iv_item_image;
            private ImageButton iv_item_delete;
        }

    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        for (int i = 0; i < list.size(); i++) {
            if ("addPic".equals(list.get(i).toString())) {
                list.remove(i);
            }
        }
        Intent it = new Intent();
        Bundle b = new Bundle();
        b.putStringArrayList("datalist", (ArrayList<String>) list);
        it.putExtra("datalist", b);
        setResult(RESULT_OK, it);
        main.finish();
    }
}


