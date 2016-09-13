package cn.com.bluemoon.delivery.module.card;


import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.utils.LibConstants;

public class PunchCardFragment extends Fragment implements OnClickListener {
    private String TAG = "PunchCardFragment";
    private CardTabActivity mContext;
    @Bind(R.id.img_banner)
    ImageView imgBanner;
    @Bind(R.id.txt_date)
    TextView txtDate;
    @Bind(R.id.txt_week)
    TextView txtWeek;

    @Bind(R.id.layout_scan)
    LinearLayout layoutScan;
    @Bind(R.id.layout_input)
    LinearLayout layoutInput;
    @Bind(R.id.layout_reset)
    LinearLayout layoutReset;

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = (CardTabActivity) activity;
    }

    ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initCustomActionBar();
        View v = inflater.inflate(R.layout.fragment_tab_card_scan,
                container, false);
        ButterKnife.bind(this, v);

        txtDate.setText(DateUtil.getTime(System.currentTimeMillis(), "MM.dd"));
        txtWeek.setText(DateUtil.getTime(System.currentTimeMillis(), "EEEE"));

        layoutScan.setOnClickListener(this);
        layoutReset.setOnClickListener(this);
        layoutInput.setOnClickListener(this);
        setImgBanner();
        return v;
    }

    private void setImgBanner() {
        int width = AppContext.getInstance().getDisplayWidth();
        Drawable drawable = getResources().getDrawable(R.mipmap.bg_punch_title);
        ViewGroup.LayoutParams params = imgBanner.getLayoutParams();
        params.width = width;
        params.height = width * drawable.getIntrinsicHeight() / drawable.getIntrinsicWidth();
        imgBanner.setLayoutParams(params);
        imgBanner.setImageDrawable(drawable);
    }

    private void initCustomActionBar() {
        new CommonActionBar(mContext.getActionBar(), new IActionBarListener() {

            @Override
            public void onBtnRight(View v) {

            }

            @Override
            public void onBtnLeft(View v) {
                mContext.finish();
            }

            @Override
            public void setTitle(TextView v) {
                v.setText(getText(R.string.main_tab_card));
            }

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.REQUEST_SCAN:
                    String resultStr = data.getStringExtra(LibConstants.SCAN_RESULT);
                    Intent intent = new Intent(mContext, PunchCardOndutyActivity.class);
                    intent.putExtra("code", resultStr);
                    startActivityForResult(intent, 1);
                    break;
                case 1:

                    break;
            }
        } else if (resultCode == 2) {
            mContext.finish();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == layoutScan) {
            PublicUtil.openScanView(mContext, PunchCardFragment.this,
                    getString(R.string.btn_san_punch_card_text), Constants.REQUEST_SCAN);
        } else if (v == layoutInput) {
            Intent intent = new Intent(mContext, PunchCardOndutyActivity.class);
            startActivityForResult(intent, 1);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
    }

}
