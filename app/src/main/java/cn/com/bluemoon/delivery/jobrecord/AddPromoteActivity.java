package cn.com.bluemoon.delivery.jobrecord;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.commons.logging.Log;
import org.apache.http.Header;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.jobrecord.PeopleFlow;
import cn.com.bluemoon.delivery.app.api.model.jobrecord.ResultBpList;
import cn.com.bluemoon.delivery.app.api.model.jobrecord.ResultPromoteList;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.ui.ObservableScrollView;
import cn.com.bluemoon.delivery.utils.AnimationUtils;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.ViewHolder;
import cn.com.bluemoon.lib.tagview.FlowLayout;
import cn.com.bluemoon.lib.utils.LibViewUtil;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

/**
 * Created by LIANGJIANGLI on 2016/6/24.
 */
public class AddPromoteActivity extends Activity implements ObservableScrollView.ScrollListener{

    private String TAG = "AddPromoteActivity";
    private CommonProgressDialog progressDialog;
    private EditText etPromotePlace;
    private EditText etPromoteOtherFee;
    private TextView txtOutdoor;
    private TextView txtIndoor;
    private CompoundButton cbRent;
    private LinearLayout layoutRent;
    private ObservableScrollView root;
    int layoutRentHeight =  0;
    private Button btnOk2;
    private Button btnOk;
    private ImageView imgAddFlow;
    private LinearLayout layoutOtherFee;
    private LinearLayout layoutCommunitySelect;
    private TextView txtBpname;
    private ListView listview;
    private PeopleFlowAdapter peopleFlowAdapter;
    private List<PeopleFlow> flows = new ArrayList<PeopleFlow>();
    private int index;
    private ResultBpList.Item bp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_promote);
        etPromotePlace = (EditText) findViewById(R.id.et_promote_place);
        etPromoteOtherFee = (EditText) findViewById(R.id.et_promote_other_fee);
        txtOutdoor = (TextView) findViewById(R.id.txt_outdoor);
        txtIndoor = (TextView) findViewById(R.id.txt_indoor);
        cbRent = (CompoundButton) findViewById(R.id.cb_rent);
        layoutRent = (LinearLayout) findViewById(R.id.layout_rent);
        btnOk2 = (Button) findViewById(R.id.btn_ok2);
        btnOk = (Button) findViewById(R.id.btn_ok);
        listview = (ListView) findViewById(R.id.listview_people_flow);
        layoutOtherFee = (LinearLayout) findViewById(R.id.layout_other_fee);
        layoutCommunitySelect = (LinearLayout) findViewById(R.id.layout_community_select);
        txtBpname = (TextView) findViewById(R.id.txt_bpname);
        layoutRentHeight = AnimationUtils.getTargetHeight(layoutRent);

        imgAddFlow = (ImageView) findViewById(R.id.img_add_flow);
        root = (ObservableScrollView) findViewById(R.id.root);
        root.setScrollListener(this);
        progressDialog = new CommonProgressDialog(this);
        PublicUtil.setGravity(etPromotePlace);
        PublicUtil.setGravity(etPromoteOtherFee);

        peopleFlowAdapter = new PeopleFlowAdapter();
        listview.setAdapter(peopleFlowAdapter);
        LibViewUtil.setListViewHeight(listview);

        imgAddFlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddPromoteActivity.this, PeopleFlowActivity.class);
                intent.putExtra("type", 1);
                startActivityForResult(intent, 1);
            }
        });

        txtOutdoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtOutdoor.setBackgroundResource(R.drawable.btn_border_pink_shape4);
                txtOutdoor.setTextColor(getResources().getColor(R.color.text_red));
                txtIndoor.setBackgroundResource(R.drawable.btn_border_grep_shape4);
                txtIndoor.setTextColor(getResources().getColor(R.color.text_black_light));
            }
        });
        txtIndoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtIndoor.setBackgroundResource(R.drawable.btn_border_pink_shape4);
                txtIndoor.setTextColor(getResources().getColor(R.color.text_red));
                txtOutdoor.setBackgroundResource(R.drawable.btn_border_grep_shape4);
                txtOutdoor.setTextColor(getResources().getColor(R.color.text_black_light));
            }
        });



        cbRent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    layoutRent.setVisibility(View.VISIBLE);
                } else {
                    layoutRent.setVisibility(View.GONE);
                }
            }
        });

        layoutCommunitySelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddPromoteActivity.this, CommunitySelectActivity.class);
                startActivityForResult(intent, 2);
            }
        });

        initCustomActionBar();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (data != null && resultCode == 1) {
                PeopleFlow peopleFlow = (PeopleFlow)data.getSerializableExtra("peopleFlow");
                flows.add(peopleFlow);
            } else if (data != null && resultCode == 2) {
                PeopleFlow peopleFlow = (PeopleFlow)data.getSerializableExtra("peopleFlow");
                flows.set(index, peopleFlow);
            } else if (resultCode == 3) {
                flows.remove(index);
            }
            peopleFlowAdapter.notifyDataSetChanged();
            LibViewUtil.setListViewHeight(listview);
        } else if (requestCode == 2 && resultCode == 4 && data != null) {
            bp = (ResultBpList.Item)data.getSerializableExtra("community");
            txtBpname.setText(bp.getBpCode()+"—"+bp.getBpName());
        }
    }

    private void initCustomActionBar() {
        new CommonActionBar(getActionBar(), new IActionBarListener() {

            @Override
            public void setTitle(TextView v) {
                // TODO Auto-generated method stub
                v.setText(getResources().getString(R.string.promote_add_title));
            }

            @Override
            public void onBtnRight(View v) {

            }

            @Override
            public void onBtnLeft(View v) {
                finish();
            }
        });

    }

    @Override
    public void scrollOritention(int oritention) {
        /*int isVisible = btnOk2.getVisibility();
        if (oritention > 0 && isVisible == View.VISIBLE) {
            btnOk2.setVisibility(View.GONE);
            btnOk.setVisibility(View.VISIBLE);
        } else if (isVisible == View.GONE && oritention < 0){
            btnOk2.setVisibility(View.VISIBLE);
            btnOk.setVisibility(View.GONE);
        }*/
    }


    class PeopleFlowAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return flows.size();
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
            if (convertView == null) {
                convertView = LayoutInflater.from(AddPromoteActivity.this).inflate( R.layout.list_item_people_flow, null);
            }
            final PeopleFlow person = flows.get(position);
            final TextView txtDate = (TextView) convertView.findViewById(R.id.txt_date);
            final TextView txtTime = (TextView) convertView.findViewById(R.id.txt_time);

            String dateStr = String.format(getString(R.string.promote_date_txt),
                    DateUtil.getTime(person.getCreateDate(), "yyyy-MM-dd EE"), person.getAddress());
            String timeStr = String.format(getString(R.string.promote_time_txt),
                    DateUtil.getTime(person.getStartTime(), "HH:mm"), DateUtil.getTime(person.getEndTime(), "HH:mm"), person.getPeopleFlow());
            txtDate.setText(dateStr);
            txtTime.setText(timeStr);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AddPromoteActivity.this, PeopleFlowActivity.class);
                    intent.putExtra("type", 2);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("peopleFlow", person);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 1);
                }
            });

            return convertView;
        }

    }
}
