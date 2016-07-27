package cn.com.bluemoon.delivery.coupons;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.ui.BindView;

import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.coupon.Coupon;
import cn.com.bluemoon.delivery.app.api.model.coupon.MensendLog;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.utils.LibStringUtil;

/**
 * Created by bm on 2016/4/21.
 */
public class CouponsDetailActivity extends KJActivity{

    private String TAG="CouponsDetailActivity";
    @BindView(id= R.id.txt_push_time)
    private TextView txtPushTime;
    @BindView(id=R.id.txt_activity_name)
    private TextView txtActivityName;
    @BindView(id=R.id.txt_register_time)
    private TextView txtRegisterTime;
    @BindView(id=R.id.txt_customername)
    private TextView txtCustomerName;
    @BindView(id=R.id.txt_phone)
    private TextView txtPhone;
    @BindView(id=R.id.listView_detail)
    private ListView listViewDetail;
    private MensendLog item;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_coupons_detail);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        initCustomActionBar();
        if(getIntent()!=null){
            item = (MensendLog)getIntent().getSerializableExtra("item");
        }
        if(item==null){
            PublicUtil.showToastErrorData();
            return;
        }
        txtPushTime.setText(DateUtil.getTime(item.getSendTime(), "yyyy-MM-dd HH:mm"));
        txtActivityName.setText(item.getActivity().getActivitySName());
        txtRegisterTime.setText(String.format(getString(R.string.coupons_record_register_time),
                DateUtil.getTime(item.getUserBase().getRegistTime(), "yyyy-MM-dd")));
        txtCustomerName.setText(LibStringUtil.getStringByLengh(item.getUserBase().getNickName(), 9));
        txtPhone.setText(item.getUserBase().getMobile());
        CouponsDetailAdapter adapter = new CouponsDetailAdapter(aty,item.getActivity().getCoupons());
        listViewDetail.setAdapter(adapter);

    }

    private void initCustomActionBar() {

        new CommonActionBar(getActionBar(),new IActionBarListener() {

                    @Override
                    public void onBtnRight(View v) {
                        // TODO Auto-generated method stu
                    }

                    @Override
                    public void onBtnLeft(View v) {
                        // TODO Auto-generated method stub
                        finish();
                    }

                    @Override
                    public void setTitle(TextView v) {
                        // TODO Auto-generated method stub
                        v.setText(R.string.coupons_record_detail);
                    }
                });

    }


    class CouponsDetailAdapter extends BaseAdapter{

        private List<Coupon> list;
        private LayoutInflater layoutInflater;

        public CouponsDetailAdapter(Context context,List<Coupon> list){
            this.list = list;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView==null){
                convertView = layoutInflater.inflate(R.layout.item_coupons_detail,null);
            }

            TextView txtName = (TextView) convertView.findViewById(R.id.txt_content);
            txtName.setText(list.get(position).getCouponSName());

            return convertView;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
    }

}
