package cn.com.bluemoon.delivery.ui;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.common.photopicker.PhotoPickerActivity;
import cn.com.bluemoon.delivery.common.photopicker.PhotoPreviewActivity;
import cn.com.bluemoon.delivery.common.photopicker.SelectModel;
import cn.com.bluemoon.delivery.utils.Constants;

/**
 * Created by ljl on 2016/9/21.
 */
public class ImageGridView extends GridView {
    private Context mContext;
    private int maxSize = 5;
    private ArrayList<String> imagePaths = new ArrayList<>();

    public ImageGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        this.setFocusable(false);
    }

    public ImageGridView(Context context) {
        super(context);
        mContext = context;
        this.setFocusable(false);
    }

    public ImageGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        this.setFocusable(false);
    }

    public void setMaxSize(int size) {
        this.maxSize = size;
    }

    public void loadAdpater(List<String> paths, final boolean isAdd) {
        if (imagePaths != null && imagePaths.size() > 0) {
            imagePaths.clear();
        }
        if (paths != null && paths.contains(Constants.ICON_ADD)) {
            paths.remove(Constants.ICON_ADD);
        } else if (paths == null) {
            paths = new ArrayList<>();
        }
        if (isAdd) {
            paths.add(Constants.ICON_ADD);
        }
        imagePaths.addAll(paths);
        while (imagePaths.size() > maxSize) {
            imagePaths.remove(imagePaths.size() - 1);
        }
        ImageAdapter gridAdapter = new ImageAdapter(mContext, imagePaths);
        this.setAdapter(gridAdapter);
        this.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (imagePaths != null && imagePaths.size() > 0) {
                    String imgs = (String) parent.getItemAtPosition(position);
                    if (Constants.ICON_ADD.equals(imgs)) {
                        if (mContext instanceof Activity) {
                            PhotoPickerActivity.actStart((Activity) mContext, SelectModel.MULTI,
                                    maxSize - imagePaths.size() + 1, true, Constants
                                            .REQUEST_ADD_IMG);
                        }
                    } else {
                        if (isAdd) {
                            PhotoPreviewActivity.actStart((Activity) mContext, imagePaths, position,
                                    Constants.REQUEST_PREVIEW_IMG);
                        } else {
                            PhotoPreviewActivity.actStart(mContext, imagePaths, position);
                        }

                    }
                }

            }
        });

    }


    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    class ImageAdapter extends BaseAdapter {
        private Context mContext;
        private List<String> imagePaths;

        public ImageAdapter(Context context, List<String> imagePaths) {
            mContext = context;
            this.imagePaths = imagePaths;
        }

        @Override
        public int getCount() {
            return imagePaths.size();
        }

        @Override
        public String getItem(int position) {
            // TODO Auto-generated method stub
            return imagePaths.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            convertView = LayoutInflater.from(mContext).inflate(R.layout.image_gridview_item, null);

            final ImageView imgUpload = (ImageView) convertView.findViewById(R.id.img_upload);
            final RelativeLayout layoutAdd = (RelativeLayout) convertView.findViewById(R.id.rl_add);
            final ImageView ivAdd = (ImageView) convertView.findViewById(R.id.iv_add);
            final TextView txtMsg = (TextView) convertView.findViewById(R.id.tv_msg);
            final String path = imagePaths.get(position);
            if (path.equals(Constants.ICON_ADD)) {
                imgUpload.setVisibility(GONE);
                layoutAdd.setVisibility(VISIBLE);
                ivAdd.setImageResource(R.mipmap.ic_camera);
                txtMsg.setText(mContext.getString(R.string.clothing_book_in_phote_most_many,
                        maxSize));
            } else {
                imgUpload.setVisibility(VISIBLE);
                layoutAdd.setVisibility(GONE);
                Glide.with(mContext)
                        .load(path)
                        .placeholder(R.mipmap.place_holder)
                        .error(R.mipmap.place_holder)
                        .centerCrop()
                        .crossFade()
                        .into(imgUpload);
            }
            return convertView;
        }

    }
}
