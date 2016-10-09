package cn.com.bluemoon.delivery.module.order;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.kymjs.kjframe.KJBitmap;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.OrderInfo;
import cn.com.bluemoon.delivery.app.api.model.OrderState;
import cn.com.bluemoon.delivery.app.api.model.Package;
import cn.com.bluemoon.delivery.app.api.model.Product;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.ResultOrderInfo;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.utils.DialogUtil;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.utils.LibViewUtil;

public class OrderDetailActivity extends BaseActivity {
	@Bind(R.id.listview_product)
	ListView listviewProduct;
	@Bind(R.id.txt_orderid)
	TextView txtOrderid;
	@Bind(R.id.txt_source)
	TextView txtSource;
	@Bind(R.id.txt_payOrdertime)
	TextView txtPayOrderTime;
	@Bind(R.id.txt_subscribetime)
	TextView txtSubscribeTime;
	@Bind(R.id.txt_deliverytime)
	TextView txtDeliveryTime;
	@Bind(R.id.txt_signtime)
	TextView txtSignTime;
	@Bind(R.id.txt_warehouse)
	TextView txtWarehouse;
	@Bind(R.id.layout_warehouse)
	LinearLayout layoytWarehouse;
	@Bind(R.id.layout_subscribe)
	LinearLayout layoutSubscribe;
	@Bind(R.id.layout_delivery)
	LinearLayout layoutDelivery;
	@Bind(R.id.layout_sign)
	LinearLayout layoutSign;
	@Bind(R.id.txt_customername)
	TextView txtCustomerName;
	@Bind(R.id.txt_phone)
	TextView txtPhone;
	@Bind(R.id.txt_address)
	TextView txtAddress;
	@Bind(R.id.txt_totalprice)
	TextView txtTotalPrice;

	private String orderId;
	private OrderInfo item;


	@Override
	protected String getTitleString() {
		return getString(R.string.tab_detail);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.order_details;
	}

	@Override
	public void initView() {
		orderId = getIntent().getStringExtra("orderId");
		showWaitDialog();
		DeliveryApi.getOrderDetailByOrderId(getToken(), orderId, getNewHandler(1, ResultOrderInfo.class));
	}

	@Override
	public void initData() {

	}

	@Override
	public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
		hideWaitDialog();
		ResultOrderInfo orderInfoResult = (ResultOrderInfo) result;
		item = orderInfoResult.getOrderInfo();
		if (item == null) {
			return;
		}
		setLayout(item.getDispatchStatus());
		txtPayOrderTime.setText(item.getPayOrderTime());
		txtSubscribeTime.setText(item.getSubscribeTime());
		txtDeliveryTime.setText(item.getDeliveryTime());
		txtSignTime.setText(item.getSignTime());
		txtOrderid.setText(item.getOrderId());
		txtSource.setText(item.getSource());
		txtWarehouse.setText(OrdersUtils.getWarehouseStr(item, this));
		txtCustomerName.setText(item.getCustomerName());
		txtPhone.setText(item.getMobilePhone());
		txtAddress.setText(String.format("%s%s", item.getRegion(), item.getAddress()));
		txtTotalPrice.setText(getString(R.string.extract_order_total_pay, PublicUtil.getPriceFrom(item.getTotalPrice())));
		if (item.getProductList() != null) {
			ProductAdapter adapter = new ProductAdapter(this, null);
			adapter.setList(item.getProductList());
			listviewProduct.setAdapter(adapter);
		} else {
			PublicUtil.showToast(R.string.return_change_get_detail_fail);
		}
		LibViewUtil.setListViewHeight2(listviewProduct);

	}

	public void setTxtPhoneStyle() {
		txtPhone.setTextColor(getResources().getColor(R.color.text_blue));
		txtPhone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		txtPhone.getPaint().setAntiAlias(true);
		txtPhone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PublicUtil.showCallPhoneDialog(OrderDetailActivity.this, txtPhone.getText().toString());
			}
		});
	}

	public void setLayout(String state) {
		if (OrderState.ACCEPT.toString().equals(state)) {
			layoytWarehouse.setVisibility(View.GONE);
		} else if (OrderState.APPOINTMENT.toString().equals(state)) {
			setTxtPhoneStyle();
		} else if (OrderState.DELIVERY.toString().equals(state)) {
			layoutSubscribe.setVisibility(View.VISIBLE);
		} else if (OrderState.SIGN.toString().equals(state)) {
			layoutSubscribe.setVisibility(View.VISIBLE);
			layoutDelivery.setVisibility(View.VISIBLE);
			setTxtPhoneStyle();
		} else if (OrderState.HISTORY.toString().equals(state)
				|| OrderState.PICKUP.toString().equals(state)) {
			layoutSubscribe.setVisibility(View.VISIBLE);
			layoutDelivery.setVisibility(View.VISIBLE);
			layoutSign.setVisibility(View.VISIBLE);
		}
	}

	class ProductAdapter extends BaseListAdapter<Product> {

		ImageView imgProduct;
		TextView txtContent;
		TextView txtMoney;
		TextView txtCount;
		KJBitmap kjb;
		ListView listviewPackageDetail;
		LinearLayout layoutPackage;

		public ProductAdapter(Context context, OnListItemClickListener listener) {
			super(context, listener);
		}

		@Override
		protected int getLayoutId() {
			return R.layout.list_item_product;
		}

		@Override
		protected void setView(final int position, View convertView, ViewGroup parent, boolean isNew) {
			imgProduct = getViewById(R.id.img_product);
			txtContent = getViewById(R.id.txt_content);
			txtMoney = getViewById(R.id.txt_money);
			txtCount = getViewById(R.id.txt_count);
			layoutPackage = getViewById(R.id.layout_package);
			listviewPackageDetail = getViewById(R.id.listview_package_detail);

			List<Package> packages = list.get(position).getPackageDetails();
			if (packages.size() > 0) {
				layoutPackage.setVisibility(View.VISIBLE);
				PackageAdapter adapter = new PackageAdapter(OrderDetailActivity.this, null);
				adapter.setList(packages);
				listviewPackageDetail.setAdapter(adapter);
//				LibViewUtil.setListViewHeight(listviewPackageDetail);
			} else {
				layoutPackage.setVisibility(View.GONE);
			}


			txtMoney.setText(String.format("%s%s", context.getString(R.string.order_money_sign)
					, PublicUtil.getPriceFrom(list.get(position).getPayPrice())));
			txtContent.setText(list.get(position).getShopProName());
			txtCount.setText(String.format("x%s", list.get(position).getBuyNum()));
			if (kjb == null) kjb = new KJBitmap();
			kjb.display(imgProduct, list.get(position).getImg());
			imgProduct.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						DialogUtil.showPictureDialog((Activity) context, list.get(position).getImg());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	class PackageAdapter extends BaseListAdapter<Package> {

		TextView txtContent;
		TextView txtNum;
		TextView txtIndex;

		public PackageAdapter(Context context, OnListItemClickListener listener) {
			super(context, listener);
		}

		@Override
		protected int getLayoutId() {
			return R.layout.list_item_order_package;
		}

		@Override
		protected void setView(final int position, View convertView, ViewGroup parent, boolean isNew) {
			txtContent = getViewById(R.id.txt_content);
			txtNum = getViewById(R.id.txt_num);
			txtIndex = getViewById(R.id.txt_index);
			Package p = list.get(position);
			if (p != null) {
				txtIndex.setText(String.valueOf(position+1)+"、");
				txtContent.setText(p.getProductName());
				txtNum.setText("x" + p.getProductNum());
			}

		}
	}
}
