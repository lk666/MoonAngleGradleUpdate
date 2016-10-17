package cn.com.bluemoon.delivery.module.wash.returning.manager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.UUID;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.ResultUploadExceptionImage;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.BasePullToRefreshListViewFragment;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.app.api.model.wash.manager.ResultBackOrder;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.FileUtil;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.view.CommonAlertDialog;

/**
 * Created by ljl on 2016/9/19.
 */
public class SignFragment extends BasePullToRefreshListViewFragment {

    private long pageFlag = 0;
    private final String TYPE = "BACK_ORDER_WAIT_SIGN";
    private String fileName;
    private int index;
    private boolean isSingeName;
    private String backOrderCode;
    private String uploadFileName;

    @Override
    protected String getTitleString() {
        return getString(R.string.manger_tab_3);
    }


    @Override
    protected BaseListAdapter getNewAdapter() {
        return new SignAdapter(getActivity(), null);
    }

    @Override
    protected List getGetMoreList(ResultBase result) {
        ResultBackOrder r = (ResultBackOrder) result;
        pageFlag = r.getPageFlag();
        return r.getBackOrderList();
    }

    @Override
    protected List getGetDataList(ResultBase result) {
        ResultBackOrder r = (ResultBackOrder) result;
        pageFlag = r.getPageFlag();
        return r.getBackOrderList();
    }

    @Override
    protected PullToRefreshBase.Mode getMode() {
        return PullToRefreshBase.Mode.BOTH;
    }

    @Override
    protected void initPullToRefreshListView(PullToRefreshListView ptrlv) {
        ptrlv.getRefreshableView().setDivider(null);
        ptrlv.getRefreshableView().setDividerHeight(0);
        ptrlv.getRefreshableView().setCacheColorHint(Color.TRANSPARENT);
    }

    @Override
    protected void invokeGetDataDeliveryApi(int requestCode) {
        setAmount();
        pageFlag = 0;
        ReturningApi.queryBackOrderList(TYPE, 0, 0, 0, getToken(), getNewHandler(requestCode, ResultBackOrder.class));
    }

    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {
        ReturningApi.queryBackOrderList(TYPE, pageFlag, 0, 0, getToken(), getNewHandler(requestCode, ResultBackOrder.class));
    }

    @Override
    public void onItemClick(Object item, View view, int position) {

    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        super.onSuccessResponse(requestCode, jsonString, result);
        if (requestCode == 2) {
            deleteSignImage();
            ResultUploadExceptionImage r = (ResultUploadExceptionImage)result;
            ReturningApi.backOrderSign(backOrderCode, isSingeName, uploadFileName, r.getImgPath(), getToken(), getNewHandler(3, ResultBase.class));
        } else if (requestCode == 3) {
            hideWaitDialog();
            getList().remove(index);
            getAdapter().notifyDataSetChanged();
            toast(result.getResponseMsg());
            setAmount();
        }
    }

    class SignAdapter extends BaseListAdapter<ResultBackOrder.BackOrderListBean> {


        public SignAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.list_item_return_and_sign;
        }

        @Override
        protected void setView(final int position, View convertView, ViewGroup parent, boolean isNew) {
            TextView txtNo = getViewById(R.id.txt_no);
            LinearLayout layoutDetail = getViewById(R.id.layout_detail);
            TextView txtCustomerName = getViewById(R.id.txt_customerName);
            TextView txtMobilePhone = getViewById(R.id.txt_mobilePhone);
            TextView txtAddress = getViewById(R.id.txt_address);
            Button btnAction = getViewById(R.id.btn_action);
            Button btnRefuseSign = getViewById(R.id.btn_refuse_sign);
            TextView txtTotalAmount = getViewById(R.id.txt_totalAmount);
            LinearLayout layoutUrgent = getViewById(R.id.layout_urgent);
            TextView txtUrgent = getViewById(R.id.txt_urgent);

            btnAction.setVisibility(View.VISIBLE);
            btnAction.setText(R.string.manage_sign2);
            btnRefuseSign.setVisibility(View.VISIBLE);
            final ResultBackOrder.BackOrderListBean result = list.get(position);
            if (result.isIsRefuse()) {
                btnRefuseSign.setText(R.string.manage_show_refuse_sign);
            } else {
                btnRefuseSign.setText(R.string.manage_customer_refuse);
            }
            txtCustomerName.setText(result.getCustomerName());
            txtMobilePhone.setText(result.getCustomerPhone());
            txtMobilePhone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            txtMobilePhone.getPaint().setAntiAlias(true);
            txtAddress.setText(result.getCustomerAddress());
            txtTotalAmount.setText(getString(R.string.manage_clothes_amount, result.getClothesNum()));

            if (result.isIsUrgent()) {
                layoutUrgent.setVisibility(View.VISIBLE);
                txtUrgent.setText(getString(R.string.manage_return_time, DateUtil.getTime(result.getAppointBackTime(), "yyyy-MM-dd  HH:mm")));
            } else {
                layoutUrgent.setVisibility(View.GONE);
            }
            txtNo.setText(getString(R.string.manage_clothes_num, result.getBackOrderCode()));
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.layout_detail:
                            BackOrderDetailActivity.actStart(getActivity(), result, false);
                            break;
                        case R.id.btn_action:
                            final View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_clothes_sign, null);
                            TextView txtSign = (TextView)view.findViewById(R.id.txt_sign);
                            txtSign.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ManualSignActivity.actStart(SignFragment.this, 1);
                                }
                            });
                            //show sign dialog
                            new CommonAlertDialog.Builder(getActivity()).setView(view)
                                    .setNegativeButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (StringUtils.isEmpty(fileName)) {
                                        longToast(context.getString(R.string.manage_not_sign));
                                    } else {
                                        RadioGroup rg = (RadioGroup)view.findViewById(R.id.rg_type);
                                        int checkId = rg.getCheckedRadioButtonId();
                                        isSingeName = true;
                                        if (checkId == R.id.radio2) {
                                            isSingeName = false;
                                        }
                                        dialog.dismiss();
                                        showWaitDialog();
                                        index = position;
                                        backOrderCode = result.getBackOrderCode();
                                        //upload sign image
                                        byte[] bytes = FileUtil.getBytes(BitmapFactory.decodeFile(fileName));
                                        uploadFileName = UUID.randomUUID() + ".png";
                                        ReturningApi.uploadImage(bytes, uploadFileName, getToken(), getNewHandler(2, ResultUploadExceptionImage.class));

                                    }
                                }
                            }).setDismissable(false).setPositiveButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteSignImage();
                                    dialog.dismiss();
                                }
                            }).setCancelable(false).show();

                            break;
                        case R.id.txt_mobilePhone:
                            PublicUtil.showCallPhoneDialog2(getActivity(), result.getCustomerPhone());
                            break;
                        case R.id.btn_refuse_sign:
                            index = position;
                            Intent intent = new Intent(getActivity(), CustomerRefuseActivity.class);
                            intent.putExtra("backOrderCode", result.getBackOrderCode());
                            SignFragment.this.startActivityForResult(intent, 2);
                            break;
                    }
                }
            };
            layoutDetail.setOnClickListener(listener);
            txtMobilePhone.setOnClickListener(listener);
            btnAction.setOnClickListener(listener);
            btnRefuseSign.setOnClickListener(listener);
        }
    }

    private void deleteSignImage() {
        if (StringUtils.isNotBlank(fileName)) {
            FileUtil.deleteFileOrDirectory(fileName);
            fileName = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data != null) {
            fileName = data.getStringExtra("fileName");
        } else if (requestCode == 2 && resultCode == 1) {
            ResultBackOrder.BackOrderListBean bean = (ResultBackOrder.BackOrderListBean)getList().get(index);
            if (!bean.isIsRefuse()) {
                bean.setIsRefuse(true);
                getAdapter().notifyDataSetChanged();
            }
        }
    }
}
