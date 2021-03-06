package cn.com.bluemoon.delivery.module.evidencecash;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.EvidenceCashApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.evidencecash.ResultCashDetail;
import cn.com.bluemoon.delivery.common.photopicker.PhotoPreviewActivity;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;

/**
 * Created by ljl on 2016/11/21.
 */
public class TransferDetailActivity extends BaseActivity {

    @BindView(R.id.txt_transfer_type)
    TextView txtTransferType;
    @BindView(R.id.txt_transfer_money)
    TextView txtTransferMoney;
    @BindView(R.id.txt_transfer_status)
    TextView txtTransferStatus;
    @BindView(R.id.txt_reason)
    TextView txtReason;
    @BindView(R.id.layout_reason)
    LinearLayout layoutReason;
    @BindView(R.id.txt_time)
    TextView txtTime;
    @BindView(R.id.txt_transaction_code)
    TextView txtTransactionCode;
    @BindView(R.id.layout_transaction)
    LinearLayout layoutTransaction;
    @BindView(R.id.txt_voucher_money)
    TextView txtVoucherMoney;
    @BindView(R.id.txt_voucher_time)
    TextView txtVoucherTime;
    @BindView(R.id.img_voucher)
    ImageView imgVoucher;
    @BindView(R.id.layout_transfer_voucher)
    LinearLayout layoutTransferVoucher;

    @Override
    protected String getTitleString() {
        return getString(R.string.transfer_detail_title);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_transfer_detail;
    }

    @Override
    public void initView() {
        showWaitDialog();
        int manageId = getIntent().getIntExtra("manageId", 0);
        EvidenceCashApi.cashDetail(manageId, getToken(), getNewHandler(1, ResultCashDetail.class));
    }

    @Override
    public void initData() {

    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        hideWaitDialog();
        final ResultCashDetail.CashDetailBean bean = ((ResultCashDetail) result).getCashDetail();
        txtTransferMoney.setText(bean.getSymbol() + StringUtil.formatDoubleMoney(bean.getTradeMoney()));
        txtTransferType.setText(bean.getTradePayDisplay() + " - " + bean.getCashTypeDisplay());
        txtTransferStatus.setText(bean.getTradeStatusDisplay());
        if ("wait".equals(bean.getTradeStatusCode())) {
            txtTransferStatus.setTextColor(getResources().getColor(R.color.text_orange));
        } else if ("failure".equals(bean.getTradeStatusCode())) {
            txtTransferStatus.setTextColor(getResources().getColor(R.color.btn_red));
            if ("bank".equals(bean.getTradePayCode()) && StringUtils.isNotBlank(bean.getRefuseReason())) {
                layoutReason.setVisibility(View.VISIBLE);
                txtReason.setText(bean.getRefuseReason());
            }
        } else {
            txtTransferStatus.setTextColor(getResources().getColor(R.color.text_black_light));
            layoutTransaction.setVisibility(View.VISIBLE);
            txtTime.setText(DateUtil.getTime(bean.getPayTime(), "yyyy-MM-dd HH:mm"));
            if (StringUtils.isNotBlank(bean.getTradeTransaction())) {
                txtTransactionCode.setText(bean.getTradeTransaction());
            } else {
                txtTransactionCode.setText(R.string.transaction_code_is_null);
            }

        }
        if ("bank".equals(bean.getTradePayCode()) && !"manually".equals(bean.getTradeSource())) {
            layoutTransferVoucher.setVisibility(View.VISIBLE);
            txtVoucherMoney.setText(bean.getSymbol()+StringUtil.formatDoubleMoney(bean.getTradeMoney()));
            txtVoucherTime.setText(DateUtil.getTime(bean.getEvidenceTime(), "yyyy-MM-dd HH:mm"));
            Glide.with(this)
                    .load(bean.getEvidencePath())
                    .error(R.mipmap.place_holder)
                    .placeholder(R.mipmap.place_holder)
                    .centerCrop()
                    .into(imgVoucher);
            imgVoucher.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    List<String> imgs = new ArrayList<>();
                    imgs.add(bean.getEvidencePath());
                    PhotoPreviewActivity.actStart(TransferDetailActivity.this, imgs, 1);
                }
            });
        }
    }
}
