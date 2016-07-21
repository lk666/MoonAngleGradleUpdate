package cn.com.bluemoon.delivery.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.com.bluemoon.delivery.R;

public class AngelAlertDialog extends Dialog {
	
	public AngelAlertDialog(Context context) {
		super(context);
	}

	public AngelAlertDialog(Context context, int theme) {
		super(context, theme);
	}
	
	public static class Builder {
		private Context context;
		private String title;
		private String message;
		private String messageSmall;
		private float txtSize = -1;
		private float txtSmallSize = -1;
		private String positiveButtonText;
		private String negativeButtonText;
		private boolean isCancelable = true;
		private boolean isDismissable = true;
		private int titleGravity = -1;
		private int msgGravity = -1;
		private View contentView;
		private OnClickListener positiveButtonClickListener;
		private OnClickListener negativeButtonClickListener;

		public Builder(Context context) {
			this.context = context;
		}
		
		public Builder setTitleGravity(int titleGravity)
		{
			this.titleGravity = titleGravity;
			return this;
		}
		
		public Builder setMessageGravity(int msgGravity)
		{
			this.msgGravity = msgGravity;
			return this;
		}

		public Builder setMessage(String message) {
			this.message = message;
			return this;
		}
		
		public Builder setMessageSize(float size){
			this.txtSize = size;
			return this;
		}
		
		public Builder setMessageSmallSize(float size){
			this.txtSmallSize = size;
			return this;
		}
		
		public Builder setMessage(int message) {
			this.message = (String) context.getText(message);
			return this;
		}

		public Builder setMessageSmall(String messageSmall) {
			this.messageSmall = messageSmall;
			return this;
		}

		/**
		 * Set the Dialog message from resource
		 */
		public Builder setMessageSmall(int messageSmall) {
			this.messageSmall = (String) context.getText(messageSmall);
			return this;
		}

		/**
		 * Set the Dialog title from resource
		 * 
		 * @param title
		 * @return
		 */
		public Builder setTitle(int title) {
			this.title = (String) context.getText(title);
			return this;
		}

		/**
		 * Set the Dialog title from String
		 * 
		 * @param title
		 * @return
		 */

		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		public Builder setView(View v) {
			this.contentView = v;
			return this;
		}

		public Builder setCancelable(boolean isCancelable) {
			this.isCancelable = isCancelable;
			return this;
		}
		
		public Builder setDismissable(boolean isDismissable){
			this.isDismissable = isDismissable;
			return this;
		}

		/**
		 * Set the positive button resource and it's listener
		 * 
		 * @param positiveButtonText
		 * @return
		 */
		public Builder setPositiveButton(int positiveButtonText,
				OnClickListener listener) {
			this.positiveButtonText = (String) context
					.getText(positiveButtonText);
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setPositiveButton(String positiveButtonText,
				OnClickListener listener) {
			this.positiveButtonText = positiveButtonText;
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(int negativeButtonText,
				OnClickListener listener) {
			this.negativeButtonText = (String) context
					.getText(negativeButtonText);
			this.negativeButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(String negativeButtonText,
				OnClickListener listener) {
			this.negativeButtonText = negativeButtonText;
			this.negativeButtonClickListener = listener;
			return this;
		}
		
		public AngelAlertDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final AngelAlertDialog dialog = new AngelAlertDialog(context,
					R.style.Dialog);
			//�����Ƿ���Է���ȡ��
			if (isCancelable == false)
				dialog.setCancelable(isCancelable);
			View layout = inflater.inflate(R.layout.dialog_angel, null);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			//����title
			
			if (title != null) {
				TextView txtTitle = ((TextView) layout.findViewById(R.id.title));
				txtTitle.setText(title);
				if(titleGravity!=-1) txtTitle.setGravity(titleGravity);
			} else {
				((TextView) layout.findViewById(R.id.title))
						.setVisibility(View.GONE);
			}
			//�����ұ߰�ť
			if (positiveButtonText != null) {
				Button positiveBtn = ((Button) layout
						.findViewById(R.id.positiveButton));
				positiveBtn.setText(positiveButtonText);
				positiveBtn.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						if (positiveButtonClickListener != null) {
							positiveButtonClickListener.onClick(dialog,
									DialogInterface.BUTTON_POSITIVE);
							if(isDismissable)
							{
								dialog.dismiss();
							}
						}else{
							dialog.dismiss();
						}
						
					}
				});	
			} else {
				layout.findViewById(R.id.positiveButton).setVisibility(
						View.GONE);
			}
			//������߰�ť
			if (negativeButtonText != null) {
				Button negativeBtn = ((Button) layout
						.findViewById(R.id.negativeButton));
				negativeBtn.setText(negativeButtonText);
				negativeBtn.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						if (negativeButtonClickListener != null) {
							negativeButtonClickListener.onClick(dialog,
									DialogInterface.BUTTON_NEGATIVE);
							if(isDismissable)
							{
								dialog.dismiss();
							}
						}else{
							dialog.dismiss();
						}
					}
				});	
			} else {
				layout.findViewById(R.id.negativeButton).setVisibility(
						View.GONE);
			}

			//û�а�ť�����
			if (positiveButtonText == null && negativeButtonText == null) {
				layout.findViewById(R.id.line_bottom).setVisibility(View.GONE);
			}

			//������Ϣ
			if(message!=null||messageSmall!=null||contentView!=null)
			{
				LinearLayout lin_content = (LinearLayout) layout.findViewById(R.id.content);
				if(msgGravity!=-1) lin_content.setGravity(msgGravity);
				if (contentView != null) {
					lin_content.removeAllViews();
					lin_content.addView(contentView, new LayoutParams(
							LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
				}else if (message != null || messageSmall != null) {
					TextView txtMessage = ((TextView) layout
							.findViewById(R.id.message));
					TextView txtMessageSmall = ((TextView) layout
							.findViewById(R.id.message_small));
					if (message != null) {
						txtMessage.setText(message);
						if(txtSize!=-1){
							txtMessage.setTextSize(txtSize);
						}
					} else {
						txtMessage.setVisibility(View.GONE);
					}
					if (messageSmall != null) {
						txtMessageSmall.setText(messageSmall);
						if(txtSize!=-1){
							txtMessageSmall.setTextSize(txtSmallSize);
						}
					} else {
						txtMessageSmall.setVisibility(View.GONE);
					}
				}
			}
			dialog.setContentView(layout);
			return dialog;
		}
		
		public AngelAlertDialog show()
		{
			
			try {
				AngelAlertDialog dialog = create();
				dialog.show();
				return dialog;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return new AngelAlertDialog(context);
			
		}

	}
}
