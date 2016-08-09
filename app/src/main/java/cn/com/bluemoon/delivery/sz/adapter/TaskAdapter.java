package cn.com.bluemoon.delivery.sz.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.kymjs.kjframe.KJBitmap;

import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.Product;
import cn.com.bluemoon.delivery.utils.DialogUtil;
import cn.com.bluemoon.delivery.utils.PublicUtil;

public class TaskAdapter extends BaseAdapter {

	private List<Product> list;
	private Context context;
	public KJBitmap kjb;

	public TaskAdapter(Context context , List<Product> list) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.list = list;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
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
		// TODO Auto-generated method stub
		ViewProductHolder holder;
		if(convertView==null) {
			holder = new ViewProductHolder();
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.order_product_item, null);
			
			holder.imgProduct = (ImageView) convertView.findViewById(R.id.img_product);
			holder.txtContent = (TextView) convertView.findViewById(R.id.txt_content);
			holder.txtMoney = (TextView) convertView.findViewById(R.id.txt_money);
			holder.txtCount = (TextView) convertView.findViewById(R.id.txt_count);
			holder.line =  convertView.findViewById(R.id.line);
			convertView.setTag(holder);
			
		}
		else {
			holder = (ViewProductHolder) convertView.getTag();
		}
		
		holder.txtMoney.setText(context.getString(R.string.order_money_sign)+PublicUtil.getPriceFrom(list.get(position).getPayPrice()));
		holder.txtContent.setText(list.get(position).getShopProName());
		holder.txtCount.setText("x"+list.get(position).getBuyNum());
		if(kjb== null) kjb = new KJBitmap();
		kjb.display(holder.imgProduct, list.get(position).getImg());
		
		if(position == list.size()-1){
			holder.line.setVisibility(View.GONE);
		}else{
			holder.line.setVisibility(View.VISIBLE);
		}
		
		holder.imgProduct.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					DialogUtil.showPictureDialog((Activity)context, list.get(position).getImg());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		return convertView;
	}
	
	class ViewProductHolder {
		ImageView imgProduct;
		TextView txtContent;
		TextView txtMoney;
		TextView txtCount;
		View line;
	}

}
