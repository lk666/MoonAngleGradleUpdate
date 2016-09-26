package cn.com.bluemoon.delivery.sz.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.utils.PublicUtil;


public class LongClickDialog extends Dialog {


    private Activity mActivity;
    public TextView deleteTv;


    public LongClickDialog(Context context) {
         super(context);
         this.mActivity = (Activity)context;
     }

    @Override
     protected void onCreate(Bundle savedInstanceState) {
         // TODO Auto-generated method stub
         super.onCreate(savedInstanceState);
         requestWindowFeature(Window.FEATURE_NO_TITLE);
         setCanceledOnTouchOutside(true);
         getWindow().setGravity(Gravity.CENTER);
         init();
     }

     public void init() {
         LayoutInflater inflater = LayoutInflater.from(mActivity);
         View view;
         view = inflater.inflate(R.layout.dialog_msg_longclick, null);
         setContentView(view);
         deleteTv = (TextView)  view.findViewById(R.id.delete_tv);
     }


    public void cancel(){
        super.cancel();
    }

}
