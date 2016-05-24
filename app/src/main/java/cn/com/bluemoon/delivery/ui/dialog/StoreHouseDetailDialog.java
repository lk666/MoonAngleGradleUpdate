package cn.com.bluemoon.delivery.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.entity.GetStorehousesJsonResult.Storehouse;

public class StoreHouseDetailDialog extends Dialog {
	
	public StoreHouseDetailDialog(Context context) {
		super(context);
	}

	public StoreHouseDetailDialog(Context context, int theme) {
		super(context, theme);
	}
	
	public static class Builder {
		private Context context;
		private boolean isCancelable = true;
		private View contentView;
		private Storehouse result;
		public Builder(Context context, Storehouse result) {
			this.context = context;
			this.result = result;
			
		}


		public Builder setView(View v) {
			this.contentView = v;
			return this;
		}

		public Builder setCancelable(boolean isCancelable) {
			this.isCancelable = isCancelable;
			return this;
		}
		
		
		public StoreHouseDetailDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final StoreHouseDetailDialog dialog = new StoreHouseDetailDialog(context,
					R.style.Dialog);
			if (isCancelable == false)
				dialog.setCancelable(isCancelable);
			View layout = inflater.inflate(R.layout.dialog_storehouse_detail, null);
			TextView tvStorehouseCode = (TextView)layout.findViewById(R.id.tv_storehouse_code);
			TextView tvStorehouseName = (TextView)layout.findViewById(R.id.tv_storehouse_name);
			TextView tvStorechargeName = (TextView)layout.findViewById(R.id.tv_storecharge_name);
			TextView tvStorehouseAddress = (TextView)layout.findViewById(R.id.tv_storehouse_address);
			tvStorehouseCode.setText(result.getStorehouseCode());
			tvStorehouseName.setText(result.getStorehouseName());
			tvStorechargeName.setText(String.format("%s  %s", result.getStorechargeName(),result.getStorechargeMobileno()));
			tvStorehouseAddress.setText(result.getAddress());
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			
			dialog.setContentView(layout);
			return dialog;
		}
		
		public StoreHouseDetailDialog show()
		{
			
			try {
				StoreHouseDetailDialog dialog = create();
				dialog.show();
				return dialog;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return new StoreHouseDetailDialog(context);
			
		}

	}
}
