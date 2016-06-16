package cn.com.bluemoon.delivery.team;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.card.CardTabActivity;
import cn.com.bluemoon.delivery.ui.CommonActionBar;

public class GroupFragment extends Fragment {

    private MyTeamActivity mContext;


    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = (MyTeamActivity) activity;
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initCustomActionBar();
        View v = inflater.inflate(R.layout.fragment_group,
                container, false);

        return v;
    }

    private void initCustomActionBar() {
        CommonActionBar actionBar = new CommonActionBar(mContext.getActionBar(), new IActionBarListener() {

            @Override
            public void onBtnRight(View v) {

            }

            @Override
            public void onBtnLeft(View v) {
                mContext.finish();
            }

            @Override
            public void setTitle(TextView v) {
                v.setText(getText(R.string.ceo_team_group_title));
            }

        });
        actionBar.getImgRightView().setImageResource(R.mipmap.search_gray);
        actionBar.getImgRightView().setVisibility(View.VISIBLE);
    }
}
