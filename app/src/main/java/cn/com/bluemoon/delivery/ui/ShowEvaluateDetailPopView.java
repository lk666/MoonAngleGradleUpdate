package cn.com.bluemoon.delivery.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.com.bluemoon.delivery.app.api.model.offline.ResultSignStaffList;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.lib.qrcode.R;

public class ShowEvaluateDetailPopView {

    private RelativeLayout ll_popup;
    private PopupWindow pop;
    private Context context;
    private boolean isCancel;
    private DismissListener listener;
    private TextView nameView;
    private TextView codeView;
    private TextView signTimeView;
    private TextView gradeView;
    private TextView evaluateView;

    public ShowEvaluateDetailPopView(Context context,DismissListener listener) {
        super();
        this.context = context;
        this.listener=listener;
        popupInit();
    }

    public void showEva(View v, ResultSignStaffList.Data.Students student) {
        if(student.score<=0){
            return;
        }
        isCancel = true;
        if(nameView!=null)
        nameView.setText(student.studentName);
        if(codeView!=null)
        codeView.setText(student.studentCode);
        if(signTimeView!=null)
        signTimeView.setText(DateUtil.getTimeToYMDHM(student.assignTime));
        if(gradeView!=null)
            gradeView.setText(String.valueOf(student.score));
        if(evaluateView!=null)
            evaluateView.setText(context.getString(R.string.offline_type_evaluate)+student.comment);
        ll_popup.startAnimation(AnimationUtils.loadAnimation(context,
                R.anim.activity_translate_up_in));
        pop.showAtLocation(v, Gravity.BOTTOM, 0, 1);
    }


    @SuppressLint("NewApi")
    public void popupInit() {

        pop = new PopupWindow(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.window_show_evaluate_details, null);

        ll_popup = (RelativeLayout) view.findViewById(R.id.ll_popup);

        pop.setWidth(LayoutParams.MATCH_PARENT);
        pop.setHeight(LayoutParams.MATCH_PARENT);
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setContentView(view);
        pop.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bg_transparent));
        RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
        nameView= (TextView) view.findViewById(R.id.txt_name);
        codeView=(TextView) view.findViewById(R.id.txt_code);
        signTimeView=(TextView) view.findViewById(R.id.txt_sign_time);
        gradeView=(TextView) view.findViewById(R.id.txt_grade);
        evaluateView=(TextView) view.findViewById(R.id.txt_evaluate);
        ImageView closeView= (ImageView) view.findViewById(R.id.iv_close);

        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (listener != null && isCancel) {
                    listener.cancelReceiveValue();
                }
            }
        });
        parent.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        closeView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
    }


    public interface DismissListener {
        public void cancelReceiveValue();

    }

}
