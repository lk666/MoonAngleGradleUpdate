package cn.com.bluemoon.delivery.ui;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.apache.commons.lang3.StringUtils;
import org.kymjs.kjframe.KJBitmap;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.common.ShowMultipleImageActivity;

/**
 * Created by ljl on 2016/9/21.
 */
public class ImageGridView extends GridView{
    private KJBitmap kjBitmap = new KJBitmap();
    private Context mContext;
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

    public void setList(final List<String> list) {
        ImageAdapter adapter = new ImageAdapter(mContext, list);
        this.setAdapter(adapter);
        this.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (list != null && list.size() > 0) {
                    Intent intent = new Intent(mContext, ShowMultipleImageActivity.class);
                    intent.putStringArrayListExtra("bitmaps", (ArrayList<String>) list);
                    intent.putExtra("position", position);
                    mContext.startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                View.MeasureSpec.AT_MOST);
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

            convertView = LayoutInflater.from(mContext).inflate( R.layout.image_gridview_item, null);

            final ImageView imgWork = (ImageView) convertView.findViewById(R.id.img_promote);
            final String path = imagePaths.get(position);
            if (StringUtils.isNotBlank(path)) {
                kjBitmap.display(imgWork, path);
            }
            return convertView;
        }

    }
}
