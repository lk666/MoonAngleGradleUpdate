package cn.com.bluemoon.delivery.module.wash.returning.manager;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.app.api.model.wash.manager.ResultRefuseSign;

/**
 * Created by ljl on 2016/9/21.
 */
public class CustomerRefuseActivity extends BaseActivity {
    @Bind(R.id.listview_refuse)
    ListView listviewRefuse;
    @Bind(R.id.btn_finish)
    Button btnFinish;
    private String backOrderCode;

    @Override
    protected String getTitleString() {
        return getString(R.string.manage_customer_refuse_title);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_custom_refuse;
    }

    @Override
    public void initView() {
        showWaitDialog();
        backOrderCode = getIntent().getStringExtra("backOrderCode");
        ReturningApi.refuseSignList(backOrderCode, getToken(), getNewHandler(1, ResultRefuseSign.class));
    }

    @Override
    public void initData() {

    }

    @OnClick(R.id.btn_finish)
    public void onClick(View view) {
        finish();
    }


    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        hideWaitDialog();
        ResultRefuseSign r = (ResultRefuseSign) result;
        RefuseAdapter adapter = new RefuseAdapter(this, null);
        adapter.setList(r.getClothesList());
        listviewRefuse.setAdapter(adapter);
    }


    class RefuseAdapter extends BaseListAdapter<ResultRefuseSign.ClothesListBean> {

        public RefuseAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.list_item_refuse;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            final ResultRefuseSign.ClothesListBean r = list.get(position);
            TextView txtCode = getViewById(R.id.txt_code);
            TextView txtName = getViewById(R.id.txt_name);
            Button btnRefuse = getViewById(R.id.btn_refuse);
            txtCode.setText(r.getClothesCode());
            txtName.setText(r.getClothesName());
            if (r.isIsRefuse()) {
                btnRefuse.setText(R.string.manage_show_refuse);
                btnRefuse.setTextColor(getResources().getColor(R.color.text_grep));
                btnRefuse.setBackgroundResource(R.drawable.btn_border_grep_shape4);
            } else {
                btnRefuse.setText(R.string.manage_refuse);
                btnRefuse.setTextColor(getResources().getColor(R.color.text_blue));
                btnRefuse.setBackgroundResource(R.drawable.btn_border_blue_shape4);
            }
            btnRefuse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CustomerRefuseActivity.this, RefuseDetailActivity.class);
                    intent.putExtra("clothesCode", r.getClothesCode());
                    startActivityForResult(intent, 1);
                }
            });

        }
    }
}
