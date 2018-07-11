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
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.kymjs.kjframe.KJBitmap;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.inventory.ResultTicketPhotoVo;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.lib.view.ScrollGridView;
import cn.com.bluemoon.lib.view.TakePhotoPopView;

public class OrderTicketUploadActivity extends BaseActivity {

    @BindView(R.id.iv_add_pic)
    ImageView ivAddPic;
    @BindView(R.id.gridview_ticket_pic)
    ScrollGridView gridviewTicketPic;
    @BindView(R.id.ll_ok)
    LinearLayout llOk;
    private String type;
    private Activity main;
    private OrderGridViewAdapter adapter;
    private List<String> list;
    private String relativeOrderCode;
    private TakePhotoPopView takePhotoPop;

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        main = this;
        list = new ArrayList<>();
        type = getIntent().getStringExtra("type");
        relativeOrderCode = getIntent().getStringExtra("storeCode");
        if(getIntent().hasExtra("piclist")){
            ArrayList<String> listPath = getIntent().getBundleExtra("piclist").getStringArrayList("piclist");
            list.addAll(listPath);
            if (!"endReceive".equals(type)) {
                if (list.size() < 5) {
                    list.add("addPic");
                }
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.order_ticket_pic_upload;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.text_deliver_ticket_tip);
    }

    @Override
    protected void onActionBarBtnLeftClick() {
        keyBack();
    }

    @Override
    public void initView() {
        gridviewTicketPic.setVisibility(View.VISIBLE);
        adapter = new OrderGridViewAdapter(this, list);
        gridviewTicketPic.setAdapter(adapter);
        if ("endReceive".equals(type)) {
            ivAddPic.setVisibility(View.GONE);
            llOk.setVisibility(View.GONE);
            if (!StringUtil.isEmpty(relativeOrderCode)) {
                showWaitDialog();
                DeliveryApi.getPicDetail(ClientStateManager.getLoginToken(), relativeOrderCode, getNewHandler(0, ResultTicketPhotoVo.class));
            }
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

    @Override
    public void initData() {

    }

    private void keyBack() {
        for (int i = 0; i < list.size(); i++) {
            if ("addPic".equals(list.get(i))) {
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            keyBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        ResultTicketPhotoVo photo = (ResultTicketPhotoVo) result;
        if (photo.getPicList() != null && photo.getPicList().size() > 0) {
            if (photo.getPicList().size() > 5) {
                list.addAll(photo.getPicList().subList(0, 5));
            } else {
                list.addAll(photo.getPicList());
            }
        }
        adapter.notifyDataSetChanged();
    }

    @OnClick({R.id.iv_add_pic, R.id.btn_pic_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_add_pic:
                showTakePhotoView();
                break;
            case R.id.btn_pic_ok:
                keyBack();
                break;
        }
    }

    private void refresh(){
        for (int i = 0; i < list.size(); i++) {
            if ("addPic".equals(list.get(i))) {
                list.remove(i);
            }
        }
        if (list.size() < 5) {
            list.add("addPic");
        }
        adapter.notifyDataSetChanged();
    }


    private void showTakePhotoView() {
        if (takePhotoPop == null) {
            takePhotoPop = new TakePhotoPopView(main,
                    Constants.TAKE_PIC_RESULT, Constants.CHOSE_PIC_RESULT);
        }
        takePhotoPop.getPic(ivAddPic);
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
                        toast(getString(R.string.get_photo_data_fail));
                        return;
                    }
                    Uri uri = takePhotoPop.getTakeImageUri();
                    if (null == uri) {
                        toast(getString(R.string.get_photo_data_fail));
                        return;
                    }
                    list.add(ImageUtil.getRealFilePath(main, uri));

                    break;
                case Constants.CHOSE_PIC_RESULT:
                    if (null == takePhotoPop) {
                        toast(getString(R.string.get_photo_data_fail));
                        return;
                    }
                    Uri uri_pick = takePhotoPop.getPickImageUri(data);
                    if (uri_pick == null) {
                        toast(getString(R.string.get_photo_data_fail));
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
        refresh();

    }


    public static void actionStart(Activity context, String type, String storeCode,
                                   ArrayList<String> piclist, int requestCode) {
        Intent intent = new Intent(context, OrderTicketUploadActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("storeCode", storeCode);
        Bundle b = new Bundle();
        b.putStringArrayList("piclist",piclist);
        intent.putExtra("piclist",b);
        context.startActivityForResult(intent, requestCode);
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
            return list.size();
        }


        @Override
        public Object getItem(int position) {
            return position;
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

//               if (convertView == null) {
            convertView = mInflater.inflate(R.layout.gridview_pic_item, null);
//            }
            ImageView ivItemImage = (ImageView) convertView.findViewById(R.id.iv_item_image);
            ImageButton ivItemDelete = (ImageButton) convertView.findViewById(R.id.iv_item_delete);

            if (kjb == null) kjb = new KJBitmap();
            final String item = list.get(position);

            if ("addPic".equals(item) && position < 5) {
                ivItemImage.setBackgroundDrawable(null);
                ivItemImage.setImageBitmap(null);
                ivItemImage.setBackgroundResource(R.mipmap.addpic);
                ivItemImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                ivItemDelete.setVisibility(View.GONE);
            } else {
                ivItemDelete.setVisibility(View.VISIBLE);

                kjb.display(ivItemImage, item, null);
            }

            if ("endReceive".equals(type) || item.contains("http://") || "addPic".equals(item)) {
                ViewUtil.setViewVisibility(ivItemDelete, View.GONE);
            } else {
                ViewUtil.setViewVisibility(ivItemDelete, View.VISIBLE);
            }

            ivItemImage.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ("addPic".equals(list.get(position))) {
                        showTakePhotoView();
                    } else {
                        PhotoActivity.actionStart(main, item);
                    }
                }
            });

            ivItemDelete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    list.remove(position);
                    refresh();
                }
            });
            return convertView;
        }

    }


}