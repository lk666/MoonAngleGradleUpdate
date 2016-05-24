package cn.com.bluemoon.delivery.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.R;

/**
 * Created by bm on 2016/4/6.
 */
public class TextSelectWindow extends PopupWindow {
    private Context mContext;
    private TextSelectCallback callback;
    private ListView listView;

    public TextSelectWindow(Context context,List<String> list,TextSelectCallback callback){
        this.mContext = context;
        this.callback = callback;
        init(list);
    }

    private void init(List<String> list){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_card_search,null);


        LinearLayout ll_popup = (LinearLayout) view
                .findViewById(R.id.layout_card_search);

        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setOutsideTouchable(true);
        setContentView(view);
        setBackgroundDrawable(mContext.getResources().getDrawable(
                R.drawable.bg_transparent));

        ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
                R.anim.push_top_in));

        listView = (ListView) view.findViewById(R.id.listview_select);
        if(list==null){
            list = new ArrayList<String>();
        }
        TextSelectAdapter adapter = new TextSelectAdapter(list);
        listView.setAdapter(adapter);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }

    public void showPopwindow(View popStart) {
        showAsDropDown(popStart);
    }

    class TextSelectAdapter extends BaseAdapter{
        List<String> list;

        public TextSelectAdapter(List<String> list){
            this.list = list;
        }
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_text, null);
            }
            TextView txtContent = (TextView) convertView.findViewById(R.id.txt_content);
            txtContent.setText(list.get(position));
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (callback != null) {
                        callback.onText(list.get(position));
                    }
                    dismiss();
                }
            });


            return convertView;
        }
    }


    public interface TextSelectCallback{
        void onText(String str);
    }
}


