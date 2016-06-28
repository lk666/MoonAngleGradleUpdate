package cn.com.bluemoon.delivery.jobrecord;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.kymjs.kjframe.KJBitmap;
import org.kymjs.kjframe.bitmap.BitmapCallBack;

import java.util.List;

import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.jobrecord.PeopleFlow;
import cn.com.bluemoon.delivery.app.api.model.jobrecord.ResultPromoteInfo;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.utils.LibViewUtil;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

/**
 * Created by LIANGJIANGLI on 2016/6/28.
 */
public class PeopleFlowActivity extends Activity {
    private String TAG = "PeopleFlowActivity";
    private int type; /**1==add, 2==Eidt, 3==Info**/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_flow);
        type = getIntent().getIntExtra("type", 0);
        initCustomActionBar();
        if (type == 1) {
            Button btnSave = (Button)findViewById(R.id.btn_ok);
            TextView txtStar = (TextView)findViewById(R.id.txt_star);
            TextView txtStar2 = (TextView)findViewById(R.id.txt_star2);
            TextView txtStar3 = (TextView)findViewById(R.id.txt_star3);
            TextView txtStar4 = (TextView)findViewById(R.id.txt_star4);
            EditText etStatus = (EditText)findViewById(R.id.et_status);
            TextView txtEditDate = (TextView)findViewById(R.id.txt_edit_date);
            ImageView imgRight = (ImageView)findViewById(R.id.img_right);
            EditText etPlace = (EditText)findViewById(R.id.et_place);
            EditText etFlow = (EditText)findViewById(R.id.et_flow);
            LinearLayout layoutTime = (LinearLayout)findViewById(R.id.layout_time);
            TextView txtStartTime = (TextView)findViewById(R.id.txt_start_time);
            TextView txtEndTime = (TextView)findViewById(R.id.txt_end_time);
            btnSave.setVisibility(View.VISIBLE);
            txtStar.setVisibility(View.VISIBLE);
            txtStar2.setVisibility(View.VISIBLE);
            txtStar3.setVisibility(View.VISIBLE);
            txtStar4.setVisibility(View.VISIBLE);
            etStatus.setVisibility(View.VISIBLE);
            PublicUtil.setGravity(etStatus);
            txtEditDate.setVisibility(View.VISIBLE);
            imgRight.setVisibility(View.VISIBLE);
            etPlace.setVisibility(View.VISIBLE);
            etFlow.setVisibility(View.VISIBLE);
            layoutTime.setVisibility(View.VISIBLE);
            txtStartTime.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            txtStartTime.getPaint().setAntiAlias(true);
            txtEndTime.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            txtEndTime.getPaint().setAntiAlias(true);
        }
        if (type == 3) {
            PeopleFlow peopleFlow = (PeopleFlow)getIntent().getSerializableExtra("personFlow");
            TextView txtDate = (TextView)findViewById(R.id.txt_date);
            TextView txtTime = (TextView)findViewById(R.id.txt_time);
            TextView txtPlace = (TextView)findViewById(R.id.txt_place);
            TextView txtFlow = (TextView)findViewById(R.id.txt_flow);
            TextView txtStatus = (TextView)findViewById(R.id.txt_status);
            LinearLayout layoutStatus = (LinearLayout)findViewById(R.id.layout_status);
            txtDate.setVisibility(View.VISIBLE);
            txtTime.setVisibility(View.VISIBLE);
            txtPlace.setVisibility(View.VISIBLE);
            txtFlow.setVisibility(View.VISIBLE);
            layoutStatus.setVisibility(View.VISIBLE);
            txtDate.setText(DateUtil.getTime(peopleFlow.getCreateDate(),"yyyy-MM-dd")
                    + "  " + peopleFlow.getWeekday());
            txtTime.setText(DateUtil.getTime(peopleFlow.getStartTime(), "HH:mm")
                    + "-" + DateUtil.getTime(peopleFlow.getEndTime(), "HH:mm"));
            txtPlace.setText(peopleFlow.getAddress());
            txtFlow.setText(String.valueOf(peopleFlow.getPeopleFlow()));
            txtStatus.setText(String.valueOf(peopleFlow.getPeopleStatus()));
        }
    }


    private void initCustomActionBar() {
        new CommonActionBar(getActionBar(), new IActionBarListener() {

            @Override
            public void setTitle(TextView v) {
                if (type == 1) {
                    v.setText(getResources().getString(R.string.people_flow_add_title));
                } else if (type == 2 || type == 3) {
                    v.setText(getResources().getString(R.string.people_flow_title));
                }
            }

            @Override
            public void onBtnRight(View v) {

            }

            @Override
            public void onBtnLeft(View v) {
                finish();
            }
        });

        if (type == 2) {
            ImageView right = (ImageView)this.findViewById(R.id.img_right);
            right.setImageResource(R.mipmap.icon_del);
            right.setVisibility(View.VISIBLE);
        }

    }
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
    }

    public void onPause() {
        super.onPause();

        MobclickAgent.onPageEnd(TAG);
    }

}
