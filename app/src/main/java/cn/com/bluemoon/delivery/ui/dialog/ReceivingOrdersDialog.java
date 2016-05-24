package cn.com.bluemoon.delivery.ui.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import cn.com.bluemoon.delivery.R;

public class ReceivingOrdersDialog extends DialogFragment {

	private static final String KEY_DIALOG_ID = "DIALOG_ID";
	private View view;

	private String amount;

	public static ReceivingOrdersDialog newInstance(int id) {
		ReceivingOrdersDialog alertDialogFragment = new ReceivingOrdersDialog();
		Bundle arguments = new Bundle();
		arguments.putInt(KEY_DIALOG_ID, id);
		alertDialogFragment.setArguments(arguments);
		return alertDialogFragment;
	}

	public void setAmount(int textId) {
		this.amount = getString(textId);
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	@Override
	public void setStyle(int style, int theme) {
		super.setStyle(style, theme);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		final int id = getArguments().getInt(KEY_DIALOG_ID);
		setStyle(0, R.style.DialogStyle);
		view = inflater.inflate(R.layout.dialog_receiving_orders, container,
				false);
		TextView amountText = (TextView) view.findViewById(R.id.amount_text);
		Button okBtn = (Button) view.findViewById(R.id.dialog_ok);
		Button cancleBtn = (Button) view.findViewById(R.id.dialog_cancle);
		amountText.setText(amount);
		okBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((DialogResponses) getTargetFragment()).doPositiveClick(id);
			}
		});
		cancleBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((DialogResponses) getTargetFragment()).doNegativeClick(id);
			}
		});
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.TRANSPARENT));
		return view;
	}

}
