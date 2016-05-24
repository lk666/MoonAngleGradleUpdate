package cn.com.bluemoon.delivery.ui.dialog;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import cn.com.bluemoon.delivery.R;

public class MessageDialog extends DialogFragment {

	private static final String KEY_DIALOG_ID = "DIALOG_ID";
	private View view;
	private String title;
	private String message;
	private static MessageDialog alertDialogFragment;
	private IDialogListener listener;
	public static MessageDialog newInstance(int id) {
		alertDialogFragment = new MessageDialog();
		Bundle arguments = new Bundle();
		arguments.putInt(KEY_DIALOG_ID, id);
		alertDialogFragment.setArguments(arguments);
		return alertDialogFragment;
	}
	
	public void setListener(IDialogListener listener) {
		this.listener = listener;
	}

	@Override
	public void setStyle(int style, int theme) {
		super.setStyle(style, theme);
	}
	
	public MessageDialog setTitle(String title){
		this.title = title;
		return alertDialogFragment;
	}
	
	public MessageDialog setMessage(String message){
		this.message = message;
		return alertDialogFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		final int id = getArguments().getInt(KEY_DIALOG_ID);
		setStyle(0, R.style.DialogStyle);
		view = inflater.inflate(R.layout.dialog_message, container, false);
		TextView title = (TextView) view.findViewById(R.id.title_tv);
		if (this.title!=null) {
			title.setText(this.title);
		}
		TextView message = (TextView) view.findViewById(R.id.message);
		message.setMovementMethod(new ScrollingMovementMethod());
		message.setText(this.message);
		Button okBtn = (Button) view.findViewById(R.id.dialog_ok);
		okBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				alertDialogFragment.dismiss();
				if (listener != null) {
					listener.doPositiveClick(id);
				}
			}
		});
		
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.TRANSPARENT));
		return view;
	}
	
	
}
