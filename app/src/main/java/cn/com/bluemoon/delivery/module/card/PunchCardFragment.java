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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.utils.LibConstants;

public class PunchCardFragment extends Fragment implements OnClickListener {
	private String TAG = "PunchCardFragment";
	private CardTabActivity mContext;
	private ImageView imgBanner;
	private Button btnScan;
	private Button btnInput;

	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mContext = (CardTabActivity) activity;
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initCustomActionBar();
		View v = inflater.inflate(R.layout.fragment_tab_card_scan,
				container, false);
		imgBanner = (ImageView) v.findViewById(R.id.img_banner);
		btnScan = (Button) v.findViewById(R.id.btn_scan);
		btnInput = (Button) v.findViewById(R.id.btn_input);
		btnScan.setOnClickListener(this);
		btnInput.setOnClickListener(this);
		setImgBanner();

		return v;
	}

	private void setImgBanner(){
		int width = AppContext.getInstance().getDisplayWidth();
		Drawable drawable = getResources().getDrawable(R.mipmap.card_banner);
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
		if (resultCode == Activity.RESULT_CANCELED){
			return;
		}
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
				case Constants.REQUEST_SCAN:
					String resultStr = data.getStringExtra(LibConstants.SCAN_RESULT);
					Intent intent = new Intent(mContext,PunchCardOndutyActivity.class);
					intent.putExtra("code",resultStr);
					startActivityForResult(intent, 1);
					break;
				case 1:

					break;
			}
		}else if(resultCode == 2){
			mContext.finish();
		}
	}

	@Override
	public void onClick(View v) {
		if (v == btnScan) {
			PublicUtil.openScanCard(mContext, PunchCardFragment.this,
					getString(R.string.btn_san_punch_card_text),Constants.REQUEST_SCAN);
		} else if(v == btnInput) {
			Intent intent = new Intent(mContext,PunchCardOndutyActivity.class);
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
