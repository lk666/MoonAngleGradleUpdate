package cn.com.bluemoon.delivery.module.evidencecash;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
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

    @Bind(R.id.txt_transfer_type)
    TextView txtTransferType;
    @Bind(R.id.txt_transfer_money)
    TextView txtTransferMoney;
    @Bind(R.id.txt_transfer_status)
    TextView txtTransferStatus;
    @Bind(R.id.txt_reason)
    TextView txtReason;
    @Bind(R.id.layout_reason)
    LinearLayout layoutReason;
    @Bind(R.id.txt_time)
    TextView txtTime;
    @Bind(R.id.txt_transaction_code)
    TextView txtTransactionCode;
    @Bind(R.id.layout_transaction)
    LinearLayout layoutTransaction;
    @Bind(R.id.txt_voucher_money)
    TextView txtVoucherMoney;
    @Bind(R.id.txt_voucher_time)
    TextView txtVoucherTime;
    @Bind(R.id.img_voucher)
    ImageView imgVoucher;
    @Bind(R.id.layout_transfer_voucher)
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
            txtTransactionCode.setText(bean.getTradeTransaction());
        }
        if ("bank".equals(bean.getTradePayCode())) {
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
