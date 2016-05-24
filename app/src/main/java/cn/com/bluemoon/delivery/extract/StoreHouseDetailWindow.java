/** 
 * Description:
 * Copyright: Copyright (c) 2016
 * Company: BLUE MOON
 * @author: Eric Liang 
 * @version 1.0
 * @date: 2016/3/14
 */
package cn.com.bluemoon.delivery.extract;




import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultOrderInfoPickup;

public class StoreHouseDetailWindow extends PopupWindow {

	private Context mContext;
	private TextView txtShCode;
	private TextView txtShName;
	private TextView txtShChargeName;
	private TextView txtShAddress;
	private ResultOrderInfoPickup info;

	public StoreHouseDetailWindow(Context context,ResultOrderInfoPickup info) {
		mContext = context;
		this.info = info;
		Init();
	}

	private void Init() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.dialog_sh_detail,null);

		LinearLayout ll_popup = (LinearLayout) view
				.findViewById(R.id.layout_sh_detail);

		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		setFocusable(true);
		setOutsideTouchable(true);
		setContentView(view);
		setBackgroundDrawable(mContext.getResources().getDrawable(
				R.drawable.bg_transparent));

		ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
				R.anim.push_top_in));

		txtShCode = (TextView) view.findViewById(R.id.txt_sh_code);
		txtShName = (TextView) view.findViewById(R.id.txt_sh_name);
		txtShChargeName = (TextView) view.findViewById(R.id.txt_sh_charge_name);
		txtShAddress = (TextView) view.findViewById(R.id.txt_sh_address);
		txtShCode.setText(info.getStorehouseCode());
		txtShName.setText(info.getStorehouseName());
		txtShChargeName.setText(info.getStorechargeName() + "  " + info.getStorechargeMobileno());
		txtShAddress.setText(info.getStorehouseAddress());

		
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		
	}

	public void showPopwindow(View popStart) {
		showAsDropDown(popStart);
	}
}
