package cn.com.bluemoon.delivery.ui;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.kymjs.kjframe.KJBitmap;

import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.Package;
import cn.com.bluemoon.delivery.app.api.model.other.Product;
import cn.com.bluemoon.delivery.utils.DialogUtil;

/**
 * Created by ljl on 2016/12/8.
 */
public class ProductLinearLayout extends LinearLayout {

    ImageView imgProduct;
    TextView txtContent;
    TextView txtCount;
    KJBitmap kjb;
    LinearLayout layoutPackage;

    public ProductLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ProductLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProductLinearLayout(Context context) {
        super(context);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.list_item_product, this, true);
        imgProduct = (ImageView)findViewById(R.id.img_product);
        txtContent = (TextView)findViewById(R.id.txt_content);
        txtCount = (TextView)findViewById(R.id.txt_count);
        layoutPackage = (LinearLayout)findViewById(R.id.layout_package);
    }

    public void setData(final Product product,final Activity context) {
        List<Package> packages = product.getPackageDetails();
        if (packages != null && packages.size() > 0) {
            layoutPackage.setVisibility(View.VISIBLE);
            for (int i = 0; i <= packages.size()-1; i++) {
					Package p = packages.get(i);
					PackageLinearLayout l = new PackageLinearLayout(context);
					String name = (i+1) + "、"+ p.getProductName();
					String num = context.getString(R.string.send_num, Integer.valueOf(p.getProductNum())
							*Integer.valueOf(product.getBuyNum()));
					l.setData(name, num);
					layoutPackage.addView(l);
            }

        } else {
            layoutPackage.setVisibility(View.GONE);
            txtCount.setVisibility(View.VISIBLE);
            txtCount.setText(context.getString(R.string.send_num, product.getBuyNum()));
        }
        txtContent.setText(product.getShopProName());
        if (kjb == null) kjb = new KJBitmap();
        kjb.display(imgProduct, product.getImg());
        imgProduct.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    DialogUtil.showPictureDialog(context, product.getImg());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
