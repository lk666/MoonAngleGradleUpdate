package cn.com.bluemoon.delivery.module.wash.returning.manager;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.wash.returning.manager.model.ResultClothesDetail;
import cn.com.bluemoon.delivery.ui.ImageGridView;

/**
 * Created by ljl on 2016/9/21.
 */
public class ClothesDetailActivity extends BaseActivity {

    @Bind(R.id.txt_code)
    TextView txtCode;
    @Bind(R.id.txt_info)
    TextView txtInfo;
    @Bind(R.id.img_stain)
    ImageView imgStain;
    @Bind(R.id.img_flaw)
    ImageView imgFlaw;
    @Bind(R.id.txt_remark)
    TextView txtRemark;
    @Bind(R.id.txt_type)
    TextView txtType;
    @Bind(R.id.gridview_img)
    ImageGridView gridviewImg;
    @Bind(R.id.btn_clean)
    Button btnClean;
    private String clothesCode;
    private List<String> paths;

    @Override
    protected String getTitleString() {
        return "衣物详情";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_clothes_detail;
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
        txtCode.setText("衣物条码：" + clothesCode);
        txtRemark.setText("备注："+r.getRemark());
        txtType.setText(r.getTypeName());
        if (StringUtils.isNotBlank(r.getFlawDesc())) {
            txtInfo.setVisibility(View.VISIBLE);
            txtInfo.setText("瑕疵信息："+r.getFlawDesc());
        } else {
            txtInfo.setVisibility(View.GONE);
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
            gridviewImg.setList(paths);
        }
        btnClean.setVisibility(View.VISIBLE);
        btnClean.setText(r.getWashResult());
    }
}
