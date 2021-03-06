package cn.com.bluemoon.delivery.module.wash.returning.manager;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.app.api.model.wash.manager.ResultClothesDetail;
import cn.com.bluemoon.delivery.ui.ImageGridView;

/**
 * Created by ljl on 2016/9/21.
 */
public class ClothesDetailActivity extends BaseActivity {

    @BindView(R.id.txt_code)
    TextView txtCode;
    @BindView(R.id.txt_stain_info)
    TextView txtStainInfo;
    @BindView(R.id.txt_flaw_info)
    TextView txtFlawInfo;
    @BindView(R.id.img_stain)
    ImageView imgStain;
    @BindView(R.id.img_flaw)
    ImageView imgFlaw;
    @BindView(R.id.txt_remark)
    TextView txtRemark;
    @BindView(R.id.txt_type)
    TextView txtType;
    @BindView(R.id.gridview_img)
    ImageGridView gridviewImg;
    @BindView(R.id.btn_clean)
    Button btnClean;
    private String clothesCode;
    private List<String> paths;

    @Override
    protected String getTitleString() {
        return getString(R.string.manage_clothes_detail_title);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_manager_clothes_detail;
    }

    @Override
    public void initView() {
        showWaitDialog();
        clothesCode = getIntent().getStringExtra("clothesCode");
        ReturningApi.clothesDetail(clothesCode, getToken(), getNewHandler(1, ResultClothesDetail.class));
    }

    @Override
    public void initData() {

    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        ResultClothesDetail r = (ResultClothesDetail) result;
        txtCode.setText(getString(R.string.manage_clothes_code2, clothesCode));
        if (StringUtils.isNotBlank(r.getRemark())) {
            txtRemark.setText(getString(R.string.manage_remark, r.getRemark()));
        } else {
            txtRemark.setText(getString(R.string.manage_remark, getString(R.string.manage_remark_is_null)));
        }

        txtType.setText(r.getTypeName());
        if (StringUtils.isNotBlank(r.getFlawDesc())) {
            txtFlawInfo.setVisibility(View.VISIBLE);
            txtFlawInfo.setText(getString(R.string.manage_flaw_des, r.getFlawDesc()));
        } else {
            txtFlawInfo.setVisibility(View.GONE);
        }
        if (StringUtils.isNotBlank(r.getStainMakeup())) {
            txtStainInfo.setVisibility(View.VISIBLE);
            txtStainInfo.setText(getString(R.string.manage_stain_des, r.getStainMakeup()));
        } else {
            txtStainInfo.setVisibility(View.GONE);
        }
        if (r.getHasFlaw() == 1) {
            imgFlaw.setVisibility(View.VISIBLE);
        }
        if (r.getHasStain() == 1) {
            imgStain.setVisibility(View.VISIBLE);
        }
        List<ResultClothesDetail.ClothesImgBean> imgs = r.getClothesImg();
        if (imgs != null && !imgs.isEmpty()) {
            paths = new ArrayList<>();
            for (ResultClothesDetail.ClothesImgBean img : imgs) {
                paths.add(img.getImgPath());
            }
            gridviewImg.loadAdpater(paths, false);
        }
        btnClean.setVisibility(View.VISIBLE);
        btnClean.setText(r.getWashResult());
    }
}
