package cn.com.bluemoon.delivery.module.order;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.kymjs.kjframe.KJBitmap;

import java.util.List;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.Package;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.other.OrderInfo;
import cn.com.bluemoon.delivery.app.api.model.other.OrderState;
import cn.com.bluemoon.delivery.app.api.model.other.Product;
import cn.com.bluemoon.delivery.app.api.model.other.ResultOrderInfo;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.ui.ProductLinearLayout;
import cn.com.bluemoon.delivery.utils.DialogUtil;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.utils.LibViewUtil;
import cn.com.bluemoon.lib.view.CommonAlertDialog;

public class OrderDetailActivity extends BaseActivity {
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
	@Bind(R.id.txt_question_response)
	TextView txtQuestionResponse;
	@Bind(R.id.txt_address)
	TextView txtAddress;
	@Bind(R.id.txt_totalprice)
	TextView txtTotalPrice;
	@Bind(R.id.txt_totalnum)
	TextView txtTotalnum;
	@Bind(R.id.btn_send)
	Button btnSendSms;
	@Bind(R.id.layout_product)
	LinearLayout layoutProduct;
	@Bind(R.id.layout_question)
	LinearLayout layoutQuestion;

	private String orderId;
	private String orderSource;
	private OrderInfo item;
	private String dispatchStatus;
	private String dispatchId;

	public static void startAct(Context context, String orderId, String dispatchStatus) {
		Intent intent = new Intent();
		intent.setClass(context, OrderDetailActivity.class);
		intent.putExtra("orderId", orderId);
		intent.putExtra("dispatchStatus", dispatchStatus);
		context.startActivity(intent);
	}

	public static void startAct(Activity context, Fragment fragment, String orderId, String dispatchStatus) {
		Intent intent = new Intent();
		intent.setClass(context, OrderDetailActivity.class);
		intent.putExtra("orderId", orderId);
		intent.putExtra("dispatchStatus", dispatchStatus);
		fragment.startActivityForResult(intent, 1);
	}

	@Override
	protected void onBeforeSetContentLayout() {
		super.onBeforeSetContentLayout();
		dispatchStatus = getIntent().getStringExtra("dispatchStatus");
	}
	@Override
	protected String getTitleString() {
		return getString(R.string.tab_detail);
	}

	@Override
	protected void setActionBar(CommonActionBar titleBar) {
		super.setActionBar(titleBar);
		TextView txt = titleBar.getTvRightView();
		if (OrderState.ACCEPT.toString().equals(dispatchStatus)) {
			txt.setVisibility(View.VISIBLE);
			txt.setText(getString(R.string.pending_order_refuse_txt));
			txt.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (StringUtils.isNotBlank(orderSource)) {
						CommonAlertDialog.Builder dialog = new CommonAlertDialog.Builder(OrderDetailActivity.this);
						dialog.setMessage(getString(R.string.pending_order_refuse_alert));
						dialog.setPositiveButton(R.string.cancel_with_space, null);
						dialog.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								showWaitDialog();
								DeliveryApi.rejectOrder(getToken(), orderId, orderSource, getNewHandler(2, ResultBase.class));
							}
						});
						dialog.show();
					}
				}
			});
		} else if (OrderState.APPOINTMENT.toString().equals(dispatchStatus)
				|| OrderState.DELIVERY.toString().equals(dispatchStatus)
				|| OrderState.SIGN.toString().equals(dispatchStatus)) {
			txt.setVisibility(View.VISIBLE);
			txt.setText(getString(R.string.pending_order_cancle_txt));
			txt.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (StringUtils.isNotBlank(orderSource)) {
						Intent intent = new Intent(OrderDetailActivity.this, CancelOrderActivity.class);
						intent.putExtra("orderId", orderId);
						startActivityForResult(intent, 1);
					}
				}
			});
		}
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
		if (OrderState.APPOINTMENT.toString().equals(dispatchStatus)
				|| OrderState.DELIVERY.toString().equals(dispatchStatus)
				|| OrderState.SIGN.toString().equals(dispatchStatus)) {
			layoutQuestion.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void initData() {

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
			setResult(RESULT_OK);
			finish();
		}
	}

	@Override
	public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
		hideWaitDialog();
		if (requestCode == 1) {
			ResultOrderInfo orderInfoResult = (ResultOrderInfo) result;
			item = orderInfoResult.getOrderInfo();
			if (item == null) {
				return;
			}
			dispatchId = item.getDispatchId();
			orderSource = item.getSource();
			setLayout(item.getDispatchStatus());
			setTxtQuestionStyle();
			txtPayOrderTime.setText(item.getPayOrderTime());
			txtSubscribeTime.setText(item.getSubscribeTime());
			txtDeliveryTime.setText(item.getDeliveryTime());
			txtSignTime.setText(item.getSignTime());
			txtOrderid.setText(getString(R.string.order_detail_code,item.getOrderId()));
			txtSource.setText(getString(R.string.order_detail_source,item.getSource()));
			txtWarehouse.setText(OrdersUtils.getWarehouseStr(item, this));
			txtCustomerName.setText(item.getCustomerName());
			txtPhone.setText(item.getMobilePhone());
			txtAddress.setText(item.getAddress());
			txtTotalPrice.setText(getString(R.string.extract_order_total_pay, PublicUtil.getPriceFrom(item.getTotalPrice())));
			txtTotalnum.setText(getString(R.string.order_total_num, item.getDeliveryTotalNum()));
			if (item.getProductList() != null) {
				for (Product product : item.getProductList()) {
					ProductLinearLayout l = new ProductLinearLayout(this);
					l.setData(product, OrderDetailActivity.this);
					layoutProduct.addView(l);
				}
			} else {
				PublicUtil.showToast(R.string.return_change_get_detail_fail);
			}
		} else if (requestCode == 2) {
			toast(result.getResponseMsg());
			setResult(RESULT_OK);
			finish();
		} else if (requestCode == 3) {
			btnSendSms.setEnabled(false);
			btnSendSms.setBackgroundResource(R.drawable.btn_grep_shape4);
			toast(result.getResponseMsg());
		}
	}

	@Override
	public void onErrorResponse(int requestCode, ResultBase result) {
		if (result.getResponseCode() == 4401) {
			btnSendSms.setEnabled(false);
			btnSendSms.setBackgroundResource(R.drawable.btn_grep_shape4);
			new CommonAlertDialog.Builder(this)
					.setMessage(result.getResponseMsg())
					.setPositiveButton(R.string.confirm_with_space, null)
					.show();
		} else {
			super.onErrorResponse(requestCode, result);
		}
	}

	public void setTxtPhoneStyle() {
		txtPhone.setTextColor(getResources().getColor(R.color.text_blue));
		txtPhone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		txtPhone.getPaint().setAntiAlias(true);
		txtPhone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PublicUtil.showCallPhoneDialog(OrderDetailActivity.this, txtPhone.getText().toString());
			}
		});
	}
	public void setTxtQuestionStyle() {
		txtQuestionResponse.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		txtQuestionResponse.getPaint().setAntiAlias(true);
		txtQuestionResponse.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				QuestionCallBackActivity.actionStart(OrderDetailActivity.this, orderId, dispatchId, dispatchStatus);
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
			btnSendSms.setVisibility(View.VISIBLE);
			btnSendSms.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					showCallPhoneOrSendSMSDialog();
				}
			});
			setTxtPhoneStyle();
		} else if (OrderState.HISTORY.toString().equals(state)
				|| OrderState.PICKUP.toString().equals(state)) {
			layoutSubscribe.setVisibility(View.VISIBLE);
			layoutDelivery.setVisibility(View.VISIBLE);
			layoutSign.setVisibility(View.VISIBLE);
		}
	}

	public void showCallPhoneOrSendSMSDialog() {
		CommonAlertDialog.Builder dialog = new CommonAlertDialog.Builder(OrderDetailActivity.this);
		dialog.setMessage(getString(R.string.pending_order_receive_sign_sms_desc));
		dialog.setPositiveButton(R.string.btn_cancel_space, null);
		dialog.setNegativeButton(R.string.btn_send_space, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				showWaitDialog();
				DeliveryApi.resendReceiveCode(ClientStateManager.getLoginToken(), orderId, getNewHandler(3, ResultBase.class));
			}
		});
		dialog.show();
	}
}
